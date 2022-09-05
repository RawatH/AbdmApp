package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
class EnterAadhaarNumberFragment :BaseFragment<EnterAadhaarBinding>(EnterAadhaarBinding::inflate) {
    private  val TAG = "EnterAadhaarNumberFragm"

    private val viewModel: GenerateAbhaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = viewModel
        binding.clickHandler = this
        attachUiObserver()
        populateIntentData()
    }

    fun populateIntentData(){
        arguments?.getString("mobile_num")?.apply {
            viewModel.beneficiaryMobileNumber.value = this

        }

        arguments?.getString("abdm_api_key")?.apply {

        }
    }

    override fun onFragmentReady() {

    }

    fun attachUiObserver(){
        viewModel.beneficiaryMobileNumber.observe(viewLifecycleOwner){
            if(!binding.mobileNumEt.checkMobileFirstNumber()){
                binding.mobileNumInputLayout.helperText = resources.getText(R.string.mobile_start_number)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.uiState.collect{
                when(it){
                    is GenerateAbhaUiState.Loading ->{
                        binding.progressBar.visibility = View.VISIBLE
                        binding.generateOtp.isEnabled = false
                        binding.aadharNumberEt.isEnabled = false
                    }
                    is GenerateAbhaUiState.InvalidData ->{
                        binding.generateOtp.isEnabled = false
                        binding.progressBar.visibility = View.GONE
                    }
                    is GenerateAbhaUiState.DataValidated ->{
                      binding.generateOtp.isEnabled = true
                    }
                    is GenerateAbhaUiState.Success ->{
                        navigateToVerificationScreen()
                        binding.generateOtp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }

                    is GenerateAbhaUiState.Error ->{
                        Log.d(TAG,"XXXXXXXX"+it.errorMsg)
                        Toast.makeText(requireContext(),it.errorMsg,Toast.LENGTH_LONG).show()
                        binding.generateOtp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        binding.aadharNumberEt.isEnabled = true
                        DialogUtility.showDialog(requireContext(),it.errorMsg)
                    }
                }
            }
        }

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        viewModel.requestOtp()
    }

    fun navigateToVerificationScreen(){
        findNavController().navigate(R.id.action_enterAbhaCreationDetailsFragment_to_verifyOtpFragment)
    }



}