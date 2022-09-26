package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.VerifyAadhaarOtpBinding
import org.commcare.dalvik.abha.model.AbhaNumberRequestModel
import org.commcare.dalvik.abha.ui.main.activity.VerificationMode
import org.commcare.dalvik.abha.ui.main.custom.OtpTimerState
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.DialogType
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.data.util.PrefKeys
import org.commcare.dalvik.domain.model.AbhaVerificationResultModel
import org.commcare.dalvik.domain.model.OtpResponseModel
import org.commcare.dalvik.domain.model.VerifyOtpRequestModel
import timber.log.Timber

class VerifyAadhaarOtpFragment :
    BaseFragment<VerifyAadhaarOtpBinding>(VerifyAadhaarOtpBinding::inflate) {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        observeOtpTimer()
        viewModel.resetUiState()
        observeUiState()

        lifecycleScope.launch(Dispatchers.Main) {
            binding.aadhaarOtpEt.observeText().collect {
                binding.verifyOtp.isEnabled = it == AppConstants.AADHAAR_OTP_LENGTH
            }
        }

        /**
         * Request for OTP
         */
        arguments?.getSerializable("verificationMode")?.let {
            it as VerificationMode
            when (it){
                VerificationMode.VERIFY_AADHAAR_OTP ->{
                    requestAadhaarOtp()
                }
                VerificationMode.CONFIRM_AADHAAR_OTP ->{
                    requestAadhaarAuthOtp()
                }
            }
        }

    }

    private fun requestAadhaarOtp(){
        viewModel.requestAadhaarOtp()
    }

    private fun requestAadhaarAuthOtp(){
        arguments?.getString("abhaId")?.let { healthId ->
            viewModel.selectedAuthMethod?.let {
                viewModel.getAuthOtp(healthId, it)
            }
        }
    }

    /**
     * Observer UI STATE
     */
    private fun observeUiState() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    Timber.d("EMIT Received -> ${it}")
                    when (it) {

                        GenerateAbhaUiState.InvalidState -> {

                        }


                        GenerateAbhaUiState.AuthOtpRequested,
                        GenerateAbhaUiState.AadhaarOtpRequested -> {
                            Timber.d("--------- OTP REQUESTED -----------")
                            binding.resentOtp.isEnabled = false
                            binding.verifyOtp.isEnabled = false
                            binding.aadhaarOtpEt.setText("")
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(true))
                        }

                        GenerateAbhaUiState.VerifyAadhaarOtpRequested->{
                            Timber.d("--------- VERIFY AADHAAR OTP REQUESTED-----------")
                            binding.resentOtp.isEnabled = false
                            binding.verifyOtp.isEnabled = false
                            binding.aadhaarOtpEt.isEnabled = false
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(true))
                        }

                        /**
                         * SUCCESS
                         */
                        is GenerateAbhaUiState.Success -> {
                            when (it.requestType) {
                                RequestType.GENERATE_AUTH_OTP -> {
                                    val otResponseModel =
                                        Gson().fromJson(it.data, OtpResponseModel::class.java)
                                    viewModel.abhaRequestModel.setValue(AbhaNumberRequestModel(""))
                                    viewModel.abhaRequestModel.value?.txnId = otResponseModel.txnId
                                    binding.timeProgress.startTimer()
                                }

                                RequestType.CONFIRM_AUTH_AADHAAR_OTP -> {
                                    val abhaVerificationResultModel = Gson().fromJson(
                                        it.data,
                                        AbhaVerificationResultModel::class.java
                                    )
                                    arguments?.getString("abhaId")?.let {
                                        abhaVerificationResultModel.healthId =it
                                    }

                                    val bundle = bundleOf("resultModel" to abhaVerificationResultModel)
                                    navigateToNextScreen(RequestType.CONFIRM_AUTH_MOBILE_OTP,bundle)
                                }

                                RequestType.AADHAAR_OTP -> {
                                    binding.timeProgress.startTimer()
                                    val otResponseModel =
                                        Gson().fromJson(it.data, OtpResponseModel::class.java)
                                    viewModel.abhaRequestModel.value?.txnId = otResponseModel.txnId

                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    navigateToNextScreen(RequestType.AADHAAR_OTP_VERIFY)
                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        /**
                         * ERROR
                         */

                        is GenerateAbhaUiState.Error -> {
                            when (it.requestType) {
                                RequestType.AADHAAR_OTP -> {
                                    binding.resentOtp.isEnabled = true
                                    binding.verifyOtp.isEnabled =
                                        binding.aadhaarOtpEt.text?.isNotEmpty() ?: false
                                    binding.timeProgress.startTimer()
                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    binding.verifyOtp.text = ""
                                }

                                RequestType.GENERATE_AUTH_OTP -> {
                                    Timber.d("Error AADHAAR AUTH OTP")
                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        /**
                         * ABDM ERROR
                         */
                        is GenerateAbhaUiState.AbdmError -> {
                            when (it.requestType) {
                                RequestType.AADHAAR_OTP -> {
                                    binding.verifyOtp.isEnabled =
                                        binding.aadhaarOtpEt.text?.isNotEmpty() ?: false
                                    binding.timeProgress.startTimer()
                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    binding.aadhaarOtpEt.setText("")
                                    binding.aadhaarOtpEt.isEnabled = true
                                    binding.verifyOtp.isEnabled = true
                                    if(binding.timeProgress.timeState.value != OtpTimerState.TimerStarted){
                                        binding.resentOtp.isEnabled = true
                                    }
                                }
                            }
                            DialogUtility.showDialog(
                                requireContext(),
                                it.data.getActualMessage(),
                                type = DialogType.Blocking
                            )
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }
                    }
                }
            }
        }
    }

    private fun observeOtpTimer() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.timeProgress.timeState.collect {
                when (it) {
                    OtpTimerState.None ->{
                       // Nothing for now
                    }
                    OtpTimerState.TimerStarted -> {
                        binding.resentOtp.isEnabled = false
                    }
                    OtpTimerState.TimerOver -> {
                        binding.resentOtp.isEnabled = true
                        viewModel.getData(PrefKeys.OTP_BLOCKED_TS.getKey())
                    }
                }
            }
        }
    }

    private fun getAadhaarOtpVeriyModel() = VerifyOtpRequestModel(
        viewModel.abhaRequestModel.value?.txnId!!,
        binding.aadhaarOtpEt.text.toString()
    )

    override fun onClick(view: View?) {
        super.onClick(view)
        val verificationMode = arguments?.getSerializable("verificationMode")
        when (view?.id) {
            R.id.resentOtp -> {
                when (verificationMode) {
                    VerificationMode.CONFIRM_AADHAAR_OTP -> {
                        requestAadhaarAuthOtp()
                    }
                    VerificationMode.VERIFY_AADHAAR_OTP -> {
                        viewModel.requestAadhaarOtp()
                    }
                }

            }

            R.id.verifyOtp -> {
                when (verificationMode) {
                    VerificationMode.CONFIRM_AADHAAR_OTP -> {
                        viewModel.confirmAadhaarAuthOtp(getAadhaarOtpVeriyModel())
                    }
                    else -> {
                        viewModel.verifyAadhaarOtp(getAadhaarOtpVeriyModel())
                    }
                }
            }

        }
    }

    private fun navigateToNextScreen(srcRequestType:RequestType ,bundle: Bundle = bundleOf()) {
        when(srcRequestType){
            RequestType.CONFIRM_AUTH_MOBILE_OTP ->{
                findNavController().navigate(
                    R.id.action_verifyAadhaarOtpFragment_to_abhaVerificationResultFragment ,
                    bundle
                )
            }
            RequestType.AADHAAR_OTP_VERIFY ->{
                val bundle = bundleOf("verificationMode" to VerificationMode.VERIFY_MOBILE_OTP)
                findNavController().navigate(
                    R.id.action_verifyAadhaarOtpFragment_to_verifyMobileOtpFragment,
                    bundle
                )
            }

        }

    }

}