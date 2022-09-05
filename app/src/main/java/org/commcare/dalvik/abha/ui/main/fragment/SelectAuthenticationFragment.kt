package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.SelectAuthMethodBinding

class SelectAuthenticationFragment :
    BaseFragment<SelectAuthMethodBinding>(SelectAuthMethodBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = arrayOf("Mobile", "Aadhaar ")
        (binding.authSelection as? MaterialAutoCompleteTextView)?.setSimpleItems(items)

        binding.clickHandler = this
    }

    override fun onFragmentReady() {

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        val bundle = bundleOf("mode" to "verification")
        findNavController().navigate(
            R.id.action_selectAuthenticationFragment_to_verifyMobileOtpFragment,
            bundle
        )

//        findNavController().navigate(R.id.action_selectAuthenticationFragment_to_verifyAadhaarOtpFragment ,bundle)

    }
}