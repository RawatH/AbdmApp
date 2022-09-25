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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.VerifyMobileOtpBinding
import org.commcare.dalvik.abha.ui.main.activity.VerificationMode
import org.commcare.dalvik.abha.ui.main.custom.ProgressState
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.domain.model.VerifyOtpRequestModel
import org.commcare.dalvik.data.util.PrefKeys
import org.commcare.dalvik.domain.model.AbhaDetailModel
import org.commcare.dalvik.domain.model.AbhaVerificationResultModel

@AndroidEntryPoint
class VerifyMobileOtpFragment :
    BaseFragment<VerifyMobileOtpBinding>(VerifyMobileOtpBinding::inflate) {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        observeOtpTimer()
        observeUiState()

        lifecycleScope.launch(Dispatchers.Main) {
            binding.mobileOtpEt.observeText().collect {
                binding.verifyOtp.isEnabled = it == AppConstants.MOBILE_OTP_LENGTH
            }
        }

        viewModel.requestMobileOtp()

    }

    private fun observeUiState() {
        lifecycleScope.launch {
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
                                RequestType.CONFIRM_AUTH_MOBILE_OTP -> {
                                    val abhaVerificationResultModel = Gson().fromJson(
                                        it.data,
                                        AbhaVerificationResultModel::class.java
                                    )
                                    val bundle = bundleOf("resultModel" to abhaVerificationResultModel)
                                    navigateToNextScreen(RequestType.CONFIRM_AUTH_MOBILE_OTP,bundle)
                                }
                                RequestType.MOBILE_OTP_RESEND -> {

                                }
                                RequestType.MOBILE_OTP_VERIFY -> {
                                    val abhaDetailModel =
                                        Gson().fromJson(it.data, AbhaDetailModel::class.java)
                                    abhaDetailModel.data = it.data
                                    viewModel.abhaDetailModel.value = abhaDetailModel
                                    navigateToNextScreen(RequestType.MOBILE_OTP_VERIFY)
                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        is GenerateAbhaUiState.Error -> {
                            when (it.requestType) {
                                RequestType.MOBILE_OTP_RESEND -> {
                                    binding.verifyOtp.isEnabled =
                                        binding.mobileOtpEt.text?.isNotEmpty() ?: false
                                    binding.timeProgress.startTimer()
                                }
                                RequestType.MOBILE_OTP_VERIFY -> {
                                    navigateToNextScreen(RequestType.MOBILE_OTP_VERIFY)
                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }
                    }
                }
            }
        }
    }

    private fun observeOtpTimer() {
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

    private fun getMobileOtpRequestModel() = VerifyOtpRequestModel(
        viewModel.abhaRequestModel.value!!.txnId,
        binding.mobileOtpEt.text.toString()
    )


    override fun onClick(view: View?) {
        super.onClick(view)
        val verificationMode = arguments?.getSerializable("verificationMode")
        when (view?.id) {
            R.id.resentOtp -> {
                when (verificationMode) {
                    VerificationMode.CONFIRM_MOBILE_OTP -> {
                        viewModel.confirmMobileAuthOtp(getMobileOtpRequestModel())
                    }
                    else -> {
                        binding.verifyOtp.isEnabled = false
                        binding.resentOtp.isEnabled = false
                        viewModel.resendMobileOtpRequest()
                    }
                }
            }

            R.id.verifyOtp -> {
                when (verificationMode) {
                    VerificationMode.CONFIRM_MOBILE_OTP -> {
                        viewModel.confirmMobileAuthOtp(getMobileOtpRequestModel())
                    }
                    else -> {
                        viewModel.verifyMobileOtp(getMobileOtpRequestModel())
                    }
                }
            }

        }
    }

    private fun navigateToNextScreen(srcRequestType: RequestType , bundle: Bundle = bundleOf()) {

        when (srcRequestType) {
            RequestType.CONFIRM_AUTH_MOBILE_OTP -> {
                findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_abhaVerificationResultFragment ,bundle)
            }

            RequestType.MOBILE_OTP_VERIFY -> {
                findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_abhaDetailFragment)
            }

        }
    }
}
