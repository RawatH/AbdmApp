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
import org.commcare.dalvik.abha.ui.main.custom.ProgressState
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.DialogType
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.observeText
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.data.model.request.VerifyOtpRequestModel
import org.commcare.dalvik.data.util.PrefKeys
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
        binding.verifyOtp.isEnabled = true

        viewModel.requestAadhaarOtp()
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
                                RequestType.AADHAAR_OTP -> {
                                    val otResponseModel =
                                        Gson().fromJson(it.data, OtpResponseModel::class.java)
                                    viewModel.abhaRequestModel.value?.txnId = otResponseModel.txnId

                                }
                                RequestType.AADHAAR_OTP_VERIFY -> {
                                    navigateToNextScreen()
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
                viewModel.abhaRequestModel.value?.txnId?.let {
                    val verifyOtpRequestModel = VerifyOtpRequestModel(it, binding.aadhaarOtpEt.text.toString())
                    viewModel.verifyAadhaarOtp(verifyOtpRequestModel)
                }
            }

        }
    }

    private fun navigateToNextScreen(){
        findNavController().navigate(
            R.id.action_verifyAadhaarOtpFragment_to_verifyMobileOtpFragment)
    }

}