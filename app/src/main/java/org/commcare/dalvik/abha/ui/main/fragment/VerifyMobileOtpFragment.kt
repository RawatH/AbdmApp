package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.MutableStateFlow
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.VerifyMobileOtpBinding

class VerifyMobileOtpFragment :
    BaseFragment<VerifyMobileOtpBinding>(VerifyMobileOtpBinding::inflate) {

    val uiState = MutableStateFlow<VerifyOtpUiState>(VerifyOtpUiState.DataInvalid)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clickHandler = this

    }

    override fun onFragmentReady() {

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.resentOtp -> {
                binding.verifyOtp.isEnabled = false
            }

            R.id.verifyOtp -> {
                val mode = arguments?.getString("mode")
                if (mode == null) {
                    findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_verifyAadhaarOtpFragment)
                } else {
                    findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_abhaVerificationResultFragment)
                }
            }

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