package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.VerifyMobileOtpBinding
import org.commcare.dalvik.abha.ui.main.custom.ProgressState
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.data.util.PrefKeys

@AndroidEntryPoint
class VerifyMobileOtpFragment :
    BaseFragment<VerifyMobileOtpBinding>(VerifyMobileOtpBinding::inflate) {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()


    private val TAG = "VerifyMobileOtpFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        observeOtpTimer()
        observeUiState()

        lifecycleScope.launch(Dispatchers.Main) {
            binding.otpEt.observeText().collect {
                binding.verifyOtp.isEnabled = it > 4
            }
        }
    }

    fun observeUiState() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect {
                when (it) {

                    GenerateAbhaUiState.InvalidState -> {

                    }
                    GenerateAbhaUiState.MobileOtpRequested -> {
                        binding.resentOtp.isEnabled = false
                    }
                    is GenerateAbhaUiState.Success -> {
                        when (it.requestType) {
                            RequestType.MOBILE_OTP_RESEND -> {

                            }
                            RequestType.MOBILE_OTP_VERIFIED -> {
                                navigateToNextScreen()
                            }
                        }
                        viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                    }
                    is GenerateAbhaUiState.Error -> {
                        when (it.requestType) {
                            RequestType.MOBILE_OTP_RESEND -> {
                                binding.timeProgress.startTimer()
                            }
                            RequestType.MOBILE_OTP_VERIFIED -> {
                                navigateToNextScreen()
                            }
                        }
                        viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                    }
                }
            }
        }
    }

    fun observeOtpTimer() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.timeProgress.timestate.collect {
                when (it) {
                    ProgressState.TimeoutStarted -> {
                        binding.resentOtp.isEnabled = false
                    }
                    ProgressState.TimeoutOver -> {
                        binding.resentOtp.isEnabled = true
                        viewModel.getData(PrefKeys.OTP_BLOCKED_TS.getKey())
                    }
                }
            }
        }
    }

    override fun onFragmentReady() {

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.resentOtp -> {
                binding.resentOtp.isEnabled = false
                viewModel.resendMobileOtpRequest()
            }

            R.id.verifyOtp -> {
                navigateToNextScreen()
            }

        }
    }

    private fun navigateToNextScreen() {
        val mode = arguments?.getString("mode")
        if (mode == null) {
            findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_verifyAadhaarOtpFragment)
        } else {
            findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_abhaVerificationResultFragment)
        }
    }
}

sealed class VerifyMode {
    object MobileOTP : VerifyMode()
    object AadhaarOTP : VerifyMode()
}

sealed class VerifyOtpUiState {
    object Loading : VerifyOtpUiState()
    object Success : VerifyOtpUiState()
    object Error : VerifyOtpUiState()
    object DataValidated : VerifyOtpUiState()
    object DataInvalid : VerifyOtpUiState()
    object VerifyOtpBlocked : VerifyOtpUiState()
}