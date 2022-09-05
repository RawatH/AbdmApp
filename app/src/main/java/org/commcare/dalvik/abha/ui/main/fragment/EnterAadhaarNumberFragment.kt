package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.EnterAadhaarBinding
import org.commcare.dalvik.abha.utility.DataStoreUtil
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.utility.checkMobileFirstNumber
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel

@AndroidEntryPoint
class EnterAadhaarNumberFragment : BaseFragment<EnterAadhaarBinding>(EnterAadhaarBinding::inflate) {
    private val TAG = "EnterAadhaarNumberFragm"

    private val viewModel: GenerateAbhaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        attachUiStateObserver()
        populateIntentData()
    }

    fun populateIntentData() {
        lifecycleScope.launch {
            DataStoreUtil(requireContext()).saveToDataStore(
                DataStoreUtil.OTP_BLOCKED_TS,
                System.currentTimeMillis().toString()
            )
        }
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
            viewModel.uiState.collect {
                when (it) {
                    is GenerateAbhaUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.generateOtp.isEnabled = false
                        binding.aadharNumberEt.isEnabled = false
                    }
                    is GenerateAbhaUiState.InvalidData -> {
                        binding.generateOtp.isEnabled = false
                        binding.progressBar.visibility = View.GONE
                    }
                    is GenerateAbhaUiState.DataValidated -> {
                        binding.generateOtp.isEnabled = true
                    }
                    is GenerateAbhaUiState.Success -> {
                        navigateToVerificationScreen()
                        binding.generateOtp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }

                    is GenerateAbhaUiState.Error -> {
                        lifecycleScope.launch {
                            DataStoreUtil(requireContext()).getFromDataStore(DataStoreUtil.OTP_BLOCKED_TS)
                                .catch { e -> e.printStackTrace() }
                                .collect {
                                    withContext(Dispatchers.Main) {
                                        Log.d(TAG, "OTP BLOCKED TS : " + it)
                                    }
                                }

                        }
                        Log.d(TAG, "XXXXXXXX" + it.errorMsg)
                        binding.generateOtp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        binding.aadharNumberEt.isEnabled = true
                        DialogUtility.showDialog(requireContext(), it.errorMsg + "")
                    }
                }
            }
        }

    }

    override fun onClick(view: View?) {
        super.onClick(view)
//        viewModel.requestOtp()
        navigateToVerificationScreen()
    }

    fun navigateToVerificationScreen() {

        findNavController().navigate(R.id.action_enterAbhaCreationDetailsFragment_to_verifyOtpFragment)
    }


}