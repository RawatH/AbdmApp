package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.EnterAadhaarBinding
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.checkMobileFirstNumber
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel

@AndroidEntryPoint
class EnterAadhaarNumberFragment : BaseFragment<EnterAadhaarBinding>(EnterAadhaarBinding::inflate) {
    private val TAG = "EnterAadhaarNumberFragm"

    private val viewModel: GenerateAbhaViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        attachUiStateObserver()
        populateIntentData()
    }

    fun populateIntentData() {
        arguments?.getString("mobile_num")?.apply {
            viewModel.init(this)
            observeRequestModel()
        }

    }

    /**
     * Observer ABHA request data
     */
    fun observeRequestModel(){
        viewModel.abhaRequestModel.observe(viewLifecycleOwner){
            if (!binding.mobileNumEt.checkMobileFirstNumber()) {
                binding.mobileNumInputLayout.helperText =
                    resources.getText(R.string.mobile_start_number)
            }
           viewModel.validateData()
        }
    }

    override fun onFragmentReady() {

    }

    fun attachUiStateObserver() {
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
                            navigateToVerificationScreen()
                            binding.generateOtp.isEnabled = true
                        }

                        is GenerateAbhaUiState.Error -> {
                            Log.d(TAG, "XXXXXXXX" + it.errorMsg)
                            binding.generateOtp.isEnabled = true
                            binding.aadharNumberEt.isEnabled = true
                            DialogUtility.showDialog(requireContext(), it.errorMsg + "")
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }
                    }
                }
            }
        }

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        viewModel.requestOtp()
//        navigateToVerificationScreen()
//        DialogUtility.showDialog(requireContext(),"TEST",DialogType.Blocking)
    }

    fun navigateToVerificationScreen() {

        findNavController().navigate(R.id.action_enterAbhaCreationDetailsFragment_to_verifyAadhaarOtpFragment)
    }


}