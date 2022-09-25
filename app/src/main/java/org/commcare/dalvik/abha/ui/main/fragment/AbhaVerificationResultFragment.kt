package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import org.commcare.dalvik.abha.databinding.AbhaVerificationResultBinding
import org.commcare.dalvik.abha.ui.main.activity.AbdmActivity
import org.commcare.dalvik.domain.model.AbhaVerificationResultModel

class AbhaVerificationResultFragment :
    BaseFragment<AbhaVerificationResultBinding>(AbhaVerificationResultBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
        arguments?.getSerializable("resultModel")?.let {
            it as AbhaVerificationResultModel
            binding.model = it
        }

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        (activity as AbdmActivity).onAbhaNumberVerification()
    }
}