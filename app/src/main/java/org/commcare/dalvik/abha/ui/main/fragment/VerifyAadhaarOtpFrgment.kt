package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.VerifyAadhaarOtpBinding
import org.commcare.dalvik.abha.ui.main.custom.ProgressState
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.data.util.PrefKeys

class VerifyAadhaarOtpFragment:BaseFragment<VerifyAadhaarOtpBinding>(VerifyAadhaarOtpBinding::inflate) {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        observeOtpTimer()
        observeUiState()

        lifecycleScope.launch(Dispatchers.Main) {
            binding.aadhaarOtpEt.observeText().collect {
                binding.verifyOtp.isEnabled = it > AppConstants.AADHAAR_OTP_LENGTH
            }
        }
        binding.verifyOtp.isEnabled = true
    }

    fun observeUiState() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {

                        GenerateAbhaUiState.InvalidState -> {

                        }

                        GenerateAbhaUiState.MobileOtpRequested -> {
                            binding.resentOtp.isEnabled = false
                        }

                        is GenerateAbhaUiState.Success -> {
                            when (it.requestType) {
                                RequestType.AADHAAR_OTP_RESEND -> {

                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    navigateToNextScreen()
                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        is GenerateAbhaUiState.Error -> {
                            when (it.requestType) {
                                RequestType.AADHAAR_OTP_RESEND -> {
                                    binding.verifyOtp.isEnabled =
                                        binding.aadhaarOtpEt.text?.isNotEmpty() ?: false
                                    binding.timeProgress.startTimer()
                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    navigateToNextScreen()
                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }
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


        super.onClick(view)
        when (view?.id) {
            R.id.resentOtp -> {
                binding.resentOtp.isEnabled = false
                binding.verifyOtp.isEnabled = false
                viewModel.resendAadhaarOtpRequest()
            }

            R.id.verifyOtp -> {
                navigateToNextScreen()
            }

        }
    }

    private fun navigateToNextScreen(){
        findNavController().navigate(R.id.action_verifyAadhaarOtpFragment_to_verifyMobileOtpFragment)
    }
}