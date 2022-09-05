package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.StartAbhaVerificationBinding

class StartAbhaVerificationFragment():BaseFragment<StartAbhaVerificationBinding>(StartAbhaVerificationBinding::inflate) {
    override fun onFragmentReady() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        findNavController().navigate(R.id.action_startAbhaVerificationFragment_to_selectAuthenticationFragment)
    }
}