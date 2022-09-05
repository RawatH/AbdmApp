package org.commcare.dalvik.abha.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.commcare.dalvik.data.repository.AbdmRepositoryImpl
import org.commcare.dalvik.domain.model.HqResponseModel
import org.commcare.dalvik.domain.model.MobileOtpRequest
import org.commcare.dalvik.domain.usecases.RequestAadhaarOtpUsecase
import org.commcare.dalvik.domain.usecases.RequestMobileOtpUseCase
import javax.inject.Inject

@HiltViewModel
class GenerateAbhaViewModel @Inject constructor(
//    val adbmRepositoryImpl: AbdmRepositoryImpl,
    val reqAadhaarOtpUsecase: RequestAadhaarOtpUsecase,
    val reqMobileOtpUseCase: RequestMobileOtpUseCase
) : BaseViewModel() {

    val AADHAR_NUMBER_LENGTH = 16
    val MOBILE_NUMBER_LENGTH = 10

    val uiState = MutableStateFlow<GenerateAbhaUiState>(GenerateAbhaUiState.InvalidData)

    val beneficiaryMobileNumber: MutableLiveData<String> = MutableLiveData()
    val beneficiaryAadhaarNumber: MutableLiveData<String> = MutableLiveData()

    init {
        validateData()
        viewModelScope.launch {
            beneficiaryAadhaarNumber.asFlow().collect {
                validateData()
            }
        }
    }


    private fun validateData() {
        var isDataValid = true
        viewModelScope.launch {
            beneficiaryMobileNumber.value?.apply {
                if (this.isNotEmpty() && this.length == MOBILE_NUMBER_LENGTH) {
                    val firstChar = this.first().toString().toInt()
                    if(firstChar in IntRange(6,9)){

                    }else {
                        isDataValid = false
                    }
                }
            }
            beneficiaryAadhaarNumber.value?.apply {
                if (this.isNullOrEmpty() || this.length != AADHAR_NUMBER_LENGTH) {
                    isDataValid = false
                }
            }

            uiState.emit(if (isDataValid) GenerateAbhaUiState.DataValidated else GenerateAbhaUiState.InvalidData)
        }
    }


    fun requestOtp() {
        viewModelScope.launch {
            val mobileOtpResponse = async {
                val mobileOtpRequest = MobileOtpRequest(beneficiaryMobileNumber.value!!)
               val mobileOtpFlow =  reqMobileOtpUseCase.execute(mobileOtpRequest)
                mobileOtpFlow.collect{
                    when (it){
                        is HqResponseModel.Success<String>->{
                            uiState.emit(GenerateAbhaUiState.Success(it.data))
                        }

                        is HqResponseModel.Error<String>->{
                            uiState.emit(GenerateAbhaUiState.Error(it.error))
                        }


                        is HqResponseModel.Loading->{
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