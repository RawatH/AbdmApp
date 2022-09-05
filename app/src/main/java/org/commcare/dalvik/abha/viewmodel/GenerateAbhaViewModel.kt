package org.commcare.dalvik.abha.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.model.AbhaNumberRequestModel
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.PropMutableLiveData
import org.commcare.dalvik.domain.model.HqResponseModel
import org.commcare.dalvik.domain.usecases.RequestAadhaarOtpUsecase
import org.commcare.dalvik.domain.usecases.RequestMobileOtpUseCase
import javax.inject.Inject

@HiltViewModel
class GenerateAbhaViewModel @Inject constructor(
    val reqAadhaarOtpUsecase: RequestAadhaarOtpUsecase,
    val reqMobileOtpUseCase: RequestMobileOtpUseCase
) : BaseViewModel() {

    var abhaRequestModel: PropMutableLiveData<AbhaNumberRequestModel> = PropMutableLiveData()

    val uiState = MutableStateFlow<GenerateAbhaUiState>(GenerateAbhaUiState.InvalidData)

    fun init(mobileNumber: String) {
        abhaRequestModel.setValue(AbhaNumberRequestModel(mobileNumber))
    }


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
            abhaRequestModel.value?.aadhaarNumber?.apply {
                if (this.isNotEmpty() && this.length == AppConstants.AADHAR_NUMBER_LENGTH) {
                    isAadhaarValid = true
                }
            }

            uiState.emit(if (isMobileNumberValid && isAadhaarValid) GenerateAbhaUiState.DataValidated else GenerateAbhaUiState.InvalidData)
        }
    }


    fun requestOtp() {
        viewModelScope.launch {
            val mobileOtpResponse = async {
                val mobileOtpFlow = reqMobileOtpUseCase.execute(abhaRequestModel.value!!.mobileNumber)
                mobileOtpFlow.collect {
                    when (it) {
                        is HqResponseModel.Success<String> -> {
                            uiState.emit(GenerateAbhaUiState.Success(it.data))
                        }

                        is HqResponseModel.Error<String> -> {
                            uiState.emit(GenerateAbhaUiState.Error(it.error))
                        }


                        is HqResponseModel.Loading -> {
                            uiState.emit(GenerateAbhaUiState.Loading)
                        }
                    }
                }
            }

//            val aadhaarOtpResponse = async {
//                reqAadhaarOtpUsecase.execute( beneficiaryAadhaarNumber.value!!, adbmRepositoryImpl)
//            }

            mobileOtpResponse.await()
//            aadhaarOtpResponse.await()


        }
    }
}

sealed class GenerateAbhaUiState {
    object Loading : GenerateAbhaUiState()
    object DataValidated : GenerateAbhaUiState()
    object InvalidData : GenerateAbhaUiState()
    data class Success(val msg: String) : GenerateAbhaUiState()
    data class Error(val errorMsg: String) : GenerateAbhaUiState()
}