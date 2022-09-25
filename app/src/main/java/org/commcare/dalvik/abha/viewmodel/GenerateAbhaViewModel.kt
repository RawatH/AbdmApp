package org.commcare.dalvik.abha.viewmodel

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.commcare.dalvik.abha.model.AbhaNumberRequestModel
import org.commcare.dalvik.abha.model.AbhaVerificationRequestModel
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.DialogType
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.PropMutableLiveData
import org.commcare.dalvik.domain.model.VerifyOtpRequestModel
import org.commcare.dalvik.data.util.PrefKeys
import org.commcare.dalvik.domain.model.AbdmErrorModel
import org.commcare.dalvik.domain.model.AbhaDetailModel
import org.commcare.dalvik.domain.model.HqResponseModel
import org.commcare.dalvik.domain.model.LanguageManager
import org.commcare.dalvik.domain.usecases.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GenerateAbhaViewModel @Inject constructor(
    private val generateAuthOtpUsecase: GenerateAuthOtpUsecase,
    private val authenticationMethodsUsecase: GetAuthenticationMethodsUsecase,
    private val reqAadhaarOtpUsecase: RequestAadhaarOtpUsecase,
    private val reqMobileOtpUseCase: RequestMobileOtpUseCase,
    val saveDataUsecase: SaveDataUsecase,
    private val translationUseCase: GetTranslationUseCase,
    private val verifyAadhaarOtpUseCase: VerifyAadhaarOtpUseCase,
    private val verifyMobileOtpUseCase: VerifyMobileOtpUseCase,
    private val confirmAadhaarOtpUsecase: ConfirmAadhaarOtpUsecase,
    private val confirmMobileOtpUsecase: ConfirmMobileOtpUsecase
    ) : BaseViewModel() {

    var selectedAuthMethod: String? = null
    var otpFailureCount = MutableLiveData(0)
    var abhaVerificationRequestModel:MutableLiveData<AbhaVerificationRequestModel> = MutableLiveData()
    var abhaRequestModel: PropMutableLiveData<AbhaNumberRequestModel> = PropMutableLiveData()
    val abhaDetailModel: MutableLiveData<AbhaDetailModel> = MutableLiveData()

    val uiState = MutableStateFlow<GenerateAbhaUiState>(GenerateAbhaUiState.InvalidState)

    fun init(mobileNumber: String) {
        abhaRequestModel.setValue(AbhaNumberRequestModel(mobileNumber))
        //TODO : Remove this for testing only
        abhaRequestModel.value?.aadhaar = "232755042430"
    }

    fun resetUiState() {
        viewModelScope.launch {
            uiState.emit(GenerateAbhaUiState.Loading(false))
//            uiState.emit(GenerateAbhaUiState.InvalidState)
        }
    }

    fun checkIfBlocked() = saveDataUsecase.executeFetch(PrefKeys.OTP_BLOCKED_TS.getKey())

    fun validateData() {
        viewModelScope.launch {
            var isMobileNumberValid = false
            abhaRequestModel.value?.mobileNumber?.apply {
                if (this.isNotEmpty() && this.length == AppConstants.MOBILE_NUMBER_LENGTH) {
                    val firstChar = this.first().toString().toInt()
                    if (firstChar in IntRange(6, 9)) {
                        isMobileNumberValid = true
                    }
                }
            }

            var isAadhaarValid = false
            abhaRequestModel.value?.aadhaar?.apply {
                if (this.isNotEmpty() && this.length == AppConstants.AADHAR_NUMBER_LENGTH) {
                    isAadhaarValid = true
                }
            }

            uiState.emit(if (isMobileNumberValid && isAadhaarValid) GenerateAbhaUiState.ValidState else GenerateAbhaUiState.InvalidState)
        }
    }

    /**
     * Req AadhaarOtp
     */
    fun requestAadhaarOtp() {
        viewModelScope.launch {
            val aadhaarOtpResponse = async {
                val aadhaarOtpFlow = reqAadhaarOtpUsecase.execute(abhaRequestModel.value!!.aadhaar)
                aadhaarOtpFlow.collect {
                    when (it) {
                        is HqResponseModel.Loading -> {
                            uiState.emit(GenerateAbhaUiState.Loading(true))
                        }
                        is HqResponseModel.Success -> {
                            uiState.emit(
                                GenerateAbhaUiState.Success(
                                    it.value,
                                    RequestType.AADHAAR_OTP
                                )
                            )
                        }
                        is HqResponseModel.Error -> {
                            uiState.emit(
                                GenerateAbhaUiState.Error(
                                    it.value,
                                    RequestType.AADHAAR_OTP
                                )
                            )
                        }
                        is HqResponseModel.AbdmError -> {
                            uiState.emit(
                                GenerateAbhaUiState.AbdmError(
                                    it.value,
                                    RequestType.AADHAAR_OTP
                                )
                            )
                        }
                    }
                }
            }

            aadhaarOtpResponse.await()
        }
    }

    /**
     * Re Mobile Otp
     */
    fun requestMobileOtp() {
        viewModelScope.launch {
            reqMobileOtpUseCase.execute(
                abhaRequestModel.value!!.mobileNumber,
                abhaRequestModel.value!!.txnId
            ).collect {
                when (it) {
                    is HqResponseModel.Success -> {
                        uiState.emit(
                            GenerateAbhaUiState.Success(
                                it.value,
                                RequestType.MOBILE_OTP_RESEND
                            )
                        )
                    }

                    is HqResponseModel.Error -> {
                        uiState.emit(
                            GenerateAbhaUiState.Error(
                                it.value,
                                RequestType.MOBILE_OTP_RESEND
                            )
                        )
                    }

                    is HqResponseModel.Loading -> {
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }
                }
            }
        }
    }

    /**
     * Get data from data store
     */
    fun getData(key: Preferences.Key<String>) {
        viewModelScope.launch {
            saveDataUsecase.executeFetch(PrefKeys.OTP_BLOCKED_TS.getKey()).collect {
                Timber.d("OTP TS : ${it}")
            }
        }
    }

    /**
     * Save data in data store
     */
    private fun saveData(key: Preferences.Key<String>, value: String) {
        saveDataUsecase.executeSave(value, key)
    }

    /**
     * Increase count by 1
     */
    private fun incOtpFailureCount() {
        otpFailureCount.value = otpFailureCount.value!!.inc()
    }

    /**
     * Resend Mobile OTP request
     */
    fun resendMobileOtpRequest() {
//        viewModelScope.launch(Dispatchers.Main) {
//            uiState.emit(GenerateAbhaUiState.MobileOtpRequested)
//            uiState.emit(GenerateAbhaUiState.Loading(true))
//
//            reqMobileOtpUseCase.execute(abhaRequestModel.value?.mobileNumber ?: "12312321")
//                .collect {
//
//                    when (it) {
//                        is HqResponseModel.Loading -> {
////                        uiState.emit(GenerateAbhaUiState.Loading(true))
//                        }
//                        is HqResponseModel.Success -> {
//                            uiState.emit(
//                                GenerateAbhaUiState.Success(
//                                    it.value,
//                                    RequestType.MOBILE_OTP_RESEND
//                                )
//                            )
//                        }
//                        is HqResponseModel.Error -> {
//                            incOtpFailureCount()
//                            uiState.emit(
//                                GenerateAbhaUiState.Error(
//                                    it.value,
//                                    RequestType.MOBILE_OTP_RESEND
//                                )
//                            )
//                        }
//                    }
//                }
//
////            saveData(PrefKeys.OTP_BLOCKED_TS.getKey(), System.currentTimeMillis().toString())
//
//        }
    }

    /**
     * Verify Mobile OTP
     */
    fun verifyMobileOtp(verifyMobileOtpRequestModel: VerifyOtpRequestModel) {
        viewModelScope.launch(Dispatchers.Main) {
            verifyMobileOtpUseCase.execute(verifyMobileOtpRequestModel).collect {
                when (it) {
                    HqResponseModel.Loading -> {
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }

                    is HqResponseModel.Success -> {
                        uiState.emit(
                            GenerateAbhaUiState.Success(
                                it.value,
                                RequestType.MOBILE_OTP_VERIFY
                            )
                        )

                    }
                    is HqResponseModel.Error -> {
                        uiState.emit(GenerateAbhaUiState.Loading(false))

                    }
                    is HqResponseModel.AbdmError -> {
                        GenerateAbhaUiState.AbdmError(
                            it.value,
                            RequestType.MOBILE_OTP_VERIFY
                        )
                    }

                }
            }
        }
    }

    /**
     * Verify Aadhaar OTP
     */
    fun verifyAadhaarOtp(verifyOtpRequestModel: VerifyOtpRequestModel) {
        viewModelScope.launch(Dispatchers.Main) {
            verifyAadhaarOtpUseCase.execute(verifyOtpRequestModel).collect {
                when (it) {
                    HqResponseModel.Loading -> {
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }

                    is HqResponseModel.Success -> {
                        uiState.emit(
                            GenerateAbhaUiState.Success(
                                it.value,
                                RequestType.AADHAAR_OTP_VERIFY
                            )
                        )

                    }
                    is HqResponseModel.Error -> {
                        uiState.emit(GenerateAbhaUiState.Loading(false))

                    }
                    is HqResponseModel.AbdmError -> {
                        GenerateAbhaUiState.AbdmError(
                            it.value,
                            RequestType.AADHAAR_OTP_VERIFY
                        )
                    }

                }
            }
        }

    }

    /**
     * Resend Aadhaar OTP request
     */
    fun resendAadhaarOtpRequest() {

        viewModelScope.launch(Dispatchers.Main) {
            uiState.emit(GenerateAbhaUiState.AadhaarOtpRequested)

            uiState.emit(GenerateAbhaUiState.Loading(true))
            delay(2000)
            val errJson = JsonObject()
            errJson.addProperty("msg", "OTP failed")
            uiState.emit(GenerateAbhaUiState.Error(errJson, RequestType.AADHAAR_OTP))


            reqAadhaarOtpUsecase.execute(abhaRequestModel.value!!.aadhaar).collect {

                when (it) {
                    is HqResponseModel.Loading -> {
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }
                    is HqResponseModel.Success -> {
                        uiState.emit(
                            GenerateAbhaUiState.Success(
                                it.value,
                                RequestType.AADHAAR_OTP
                            )
                        )
                    }
                    is HqResponseModel.Error -> {
                        incOtpFailureCount()
                        uiState.emit(
                            GenerateAbhaUiState.Error(
                                JsonObject(),
                                RequestType.AADHAAR_OTP
                            )
                        )
                    }
                }
            }

//            saveData(PrefKeys.OTP_BLOCKED_TS.getKey(), System.currentTimeMillis().toString())

        }
    }

    /**
     * Reset block state
     */
    fun clearBlockState() {
        saveDataUsecase.removeKey(PrefKeys.OTP_BLOCKED_TS.getKey())
    }

    /**
     * Fetch Translations
     */
    fun getTranslation(langCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            translationUseCase.execute(langCode)?.let {
                LanguageManager.translationModel = it
                uiState.emit(GenerateAbhaUiState.TranslationReceived)
            } ?: Timber.d("Unable to fetch translations ")
        }
    }

    /**
     * Generate Auth OTP
     */
    fun getAuthOtp(healthId: String, authMethod: String) {
        viewModelScope.launch {
            generateAuthOtpUsecase.execute(healthId, authMethod).collect {
                when (it) {
                    HqResponseModel.Loading -> {
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }

                    is HqResponseModel.Success -> {
                        uiState.emit(
                            GenerateAbhaUiState.Success(
                                it.value,
                                RequestType.GENERATE_AUTH_OTP
                            )
                        )
                    }
                    is HqResponseModel.Error -> {

                    }
                    is HqResponseModel.AbdmError -> {
                        uiState.emit(
                            GenerateAbhaUiState.AbdmError(
                                it.value,
                                RequestType.GENERATE_AUTH_OTP
                            )
                        )

                    }
                }
            }
        }
    }

    /**
     * Fetch Authentication methods
     */
    fun getAuthenticationMethods(healthId: String) {
        viewModelScope.launch {
            authenticationMethodsUsecase.execute(healthId).collect {
                when (it) {
                    HqResponseModel.Loading -> {
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }

                    is HqResponseModel.Success -> {
                        if (it.value.has("auth_methods")) {
                            uiState.emit(
                                GenerateAbhaUiState.Success(
                                    it.value,
                                    RequestType.AUTH_METHODS
                                )
                            )
                        } else {
                            uiState.emit(
                                GenerateAbhaUiState.Error(
                                    JsonObject(),
                                    RequestType.AUTH_METHODS
                                )
                            )
                        }

                    }
                    is HqResponseModel.Error -> {

                    }
                    is HqResponseModel.AbdmError -> {

                    }
                }
            }
        }
    }

    /**
     * Confirm Auth AADHAAR OTP
     */

    fun confirmAadhaarAuthOtp(verifyOOtpRequestModel: VerifyOtpRequestModel){
        viewModelScope.launch {
           confirmAadhaarOtpUsecase.execute(verifyOOtpRequestModel).collect{
               when(it){
                   HqResponseModel.Loading ->{
                       uiState.emit(GenerateAbhaUiState.Loading(true))
                   }
                   is HqResponseModel.Success ->{
                       uiState.emit(
                           GenerateAbhaUiState.Success(
                               it.value,
                               RequestType.CONFIRM_AUTH_AADHAAR_OTP
                           )
                       )

                   }
                   is HqResponseModel.AbdmError ->{

                   }

                   is HqResponseModel.Error ->{

                   }
               }

           }
        }

    }

    /**
     * Confirm Auth MOBILE OTP
     */

    fun confirmMobileAuthOtp(verifyOOtpRequestModel: VerifyOtpRequestModel){
        viewModelScope.launch {
            confirmMobileOtpUsecase.execute(verifyOOtpRequestModel).collect{
                when(it){
                    HqResponseModel.Loading ->{
                        uiState.emit(GenerateAbhaUiState.Loading(true))
                    }
                    is HqResponseModel.Success ->{
                        uiState.emit(
                            GenerateAbhaUiState.Success(
                                it.value,
                                RequestType.CONFIRM_AUTH_MOBILE_OTP
                            )
                        )
                    }
                    is HqResponseModel.AbdmError ->{

                    }

                    is HqResponseModel.Error ->{

                    }
                }
            }
        }

    }
}

/**
 * UI State
 */
sealed class GenerateAbhaUiState {
    data class Loading(val isLoading: Boolean) : GenerateAbhaUiState()
    object TranslationReceived : GenerateAbhaUiState()
    object ValidState : GenerateAbhaUiState()
    object InvalidState : GenerateAbhaUiState()
    object MobileAadhaarOtpGenerated : GenerateAbhaUiState()
    object MobileOtpRequested : GenerateAbhaUiState()
    object AadhaarOtpRequested : GenerateAbhaUiState()
    object MobileOtpVerified : GenerateAbhaUiState()
    object AadhaarOtpVerified : GenerateAbhaUiState()
    object Blocked : GenerateAbhaUiState()
    data class Success(val data: JsonObject, val requestType: RequestType) :
        GenerateAbhaUiState()

    data class Error(val data: JsonObject, val requestType: RequestType) : GenerateAbhaUiState()
    data class AbdmError(val data: AbdmErrorModel, val requestType: RequestType) :
        GenerateAbhaUiState()
}

/**
 * Request type sent
 */
enum class RequestType {
    MOBILE_OTP_RESEND,
    MOBILE_OTP_VERIFY,
    AADHAAR_OTP,
    AADHAAR_OTP_VERIFY,
    AUTH_METHODS,
    GENERATE_AUTH_OTP,
    CONFIRM_AUTH_AADHAAR_OTP,
    CONFIRM_AUTH_MOBILE_OTP
}
