package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
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
import org.commcare.dalvik.abha.ui.main.custom.ProgressState
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.domain.model.VerifyOtpRequestModel
import org.commcare.dalvik.data.util.PrefKeys
import org.commcare.dalvik.domain.model.AbhaDetailModel

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

    fun observeUiState() {
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
                                RequestType.MOBILE_OTP_RESEND -> {

                                }
                                RequestType.MOBILE_OTP_VERIFY -> {
                                    val abhaDetailModel =
                                        Gson().fromJson(it.data, AbhaDetailModel::class.java)
                                    abhaDetailModel.data = it.data
                                    viewModel.abhaDetailModel.value = abhaDetailModel
                                    navigateToNextScreen()
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


    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.resentOtp -> {
                binding.verifyOtp.isEnabled = false
                binding.resentOtp.isEnabled = false
                viewModel.resendMobileOtpRequest()
            }

            R.id.verifyOtp -> {
                val verifyMobileOtpRequestModel = VerifyOtpRequestModel(
                    viewModel.abhaRequestModel.value!!.txnId,
                    binding.mobileOtpEt.text.toString()
                )
                viewModel.verifyMobileOtp(verifyMobileOtpRequestModel)
            }

        }
    }

    private fun navigateToNextScreen() {
        arguments?.getString("mode")?.let {
            findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_abhaVerificationResultFragment)
        } ?: findNavController().navigate(R.id.action_verifyMobileOtpFragment_to_abhaDetailFragment)

    }
}
