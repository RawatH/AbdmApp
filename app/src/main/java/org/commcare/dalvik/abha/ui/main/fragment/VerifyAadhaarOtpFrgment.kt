package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.VerifyAadhaarOtpBinding

class VerifyAadhaarOtpFragment:BaseFragment<VerifyAadhaarOtpBinding>(VerifyAadhaarOtpBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
    }

    override fun onFragmentReady() {

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        val mode =  arguments?.getString("mode")
        if (mode == null) {
            findNavController().navigate(R.id.action_verifyAadhaarOtpFragment_to_abhaDetailFragment)
        }else{
            findNavController().navigate(R.id.action_verifyAadhaarOtpFragment_to_abhaVerificationResultFragment)
        }
    }
}