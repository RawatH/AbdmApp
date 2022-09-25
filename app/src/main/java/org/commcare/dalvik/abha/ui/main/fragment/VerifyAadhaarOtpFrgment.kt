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
import org.commcare.dalvik.abha.ui.main.activity.VerificationMode
import org.commcare.dalvik.abha.ui.main.custom.ProgressState
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.DialogType
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.domain.model.VerifyOtpRequestModel
import org.commcare.dalvik.data.util.PrefKeys
import org.commcare.dalvik.domain.model.AbhaVerificationResultModel
import org.commcare.dalvik.domain.model.OtpResponseModel

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

        arguments?.containsKey("genAadhaarOtp")?.let {
            viewModel.requestAadhaarOtp()
        }

    }

    private fun observeUiState() {
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

                        is GenerateAbhaUiState.Error -> {
                            when (it.requestType) {
                                RequestType.AADHAAR_OTP -> {
                                    binding.verifyOtp.isEnabled =
                                        binding.aadhaarOtpEt.text?.isNotEmpty() ?: false
                                    binding.timeProgress.startTimer()
                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    binding.verifyOtp.text = ""

                                }
                            }
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        is GenerateAbhaUiState.AbdmError -> {
                            when (it.requestType) {
                                RequestType.AADHAAR_OTP -> {
                                    binding.verifyOtp.isEnabled =
                                        binding.aadhaarOtpEt.text?.isNotEmpty() ?: false
                                    binding.timeProgress.startTimer()
                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    binding.verifyOtp.text = ""

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

    private fun getAaadhaarOtpVeriyModel() = VerifyOtpRequestModel(
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
                        viewModel.confirmAadhaarAuthOtp(getAaadhaarOtpVeriyModel())
                    }
                    else -> {
                        binding.aadhaarOtpEt.setText("")
                        binding.resentOtp.isEnabled = false
                        viewModel.resendAadhaarOtpRequest()
                    }
                }

            }

            R.id.verifyOtp -> {
                when (verificationMode) {
                    VerificationMode.CONFIRM_AADHAAR_OTP -> {
                        viewModel.confirmAadhaarAuthOtp(getAaadhaarOtpVeriyModel())
                    }
                    else -> {
                        viewModel.verifyAadhaarOtp(getAaadhaarOtpVeriyModel())
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
                findNavController().navigate(
                    R.id.action_verifyAadhaarOtpFragment_to_verifyMobileOtpFragment
                )
            }

        }

    }

}