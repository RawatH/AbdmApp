package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import kotlinx.coroutines.withContext
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.EnterAadhaarBinding
import org.commcare.dalvik.abha.utility.DialogType
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.checkMobileFirstNumber
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.domain.model.LanguageManager
import org.commcare.dalvik.domain.model.OtpResponseModel
import timber.log.Timber

@AndroidEntryPoint
class EnterAadhaarNumberFragment : BaseFragment<EnterAadhaarBinding>(EnterAadhaarBinding::inflate) {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        attachUiStateObserver()
        populateIntentData()
    }

    private fun populateIntentData() {
        arguments?.getString("mobile_num")?.apply {
            viewModel.init(this)
            observeRequestModel()
        }

    }

    /**
     * Observer ABHA request data
     */
    private fun observeRequestModel(){
        viewModel.abhaRequestModel.observe(viewLifecycleOwner){
            if (!binding.mobileNumEt.checkMobileFirstNumber()) {
                binding.mobileNumInputLayout.helperText =
                    resources.getText(R.string.mobile_start_number)
            }
           viewModel.validateData()
        }
    }



    private fun attachUiStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {
                        is GenerateAbhaUiState.Loading -> {
                            binding.generateOtp.isEnabled = !it.isLoading
                            binding.aadharNumberEt.isEnabled = it.isLoading
                        }
                        is GenerateAbhaUiState.InvalidState -> {
                            binding.generateOtp.isEnabled = false
                        }
                        is GenerateAbhaUiState.ValidState -> {
                            binding.generateOtp.isEnabled = true
                        }
                        is GenerateAbhaUiState.Success -> {
//                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
//                            val otpModel = Gson().fromJson(it.data,OtpResponseModel::class.java)
//                            val bundle = bundleOf("txnId" to otpModel.txnId)
//                            navigateToAadhaarOtpVerificationScreen(bundle)
//                            binding.generateOtp.isEnabled = true
                        }

                        is GenerateAbhaUiState.TranslationReceived ->{
                            Handler(Looper.getMainLooper()).post {
                                binding.invalidateAll()
                            }

                        }
                        is GenerateAbhaUiState.Error -> {
                            Timber.d("XXXXXXXX" + it.data)
                            binding.generateOtp.isEnabled = true
                            binding.aadharNumberEt.isEnabled = true
                            DialogUtility.showDialog(requireContext(), it.data.toString() , type = DialogType.Blocking )
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }
                        is  GenerateAbhaUiState.AbdmError->{
                            binding.generateOtp.isEnabled = true
                            binding.aadharNumberEt.isEnabled = true
                            DialogUtility.showDialog(requireContext(), it.data.getActualMessage() , type = DialogType.Blocking )
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }
                    }
                }
            }
        }

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        navigateToAadhaarOtpVerificationScreen()
    }

    private fun navigateToAadhaarOtpVerificationScreen() {
        findNavController().navigate(R.id.action_enterAbhaCreationDetailsFragment_to_verifyAadhaarOtpFragment)
    }


}