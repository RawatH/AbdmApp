package org.commcare.dalvik.abha.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import org.commcare.dalvik.abha.databinding.AbhaVerificationResultBinding
import org.commcare.dalvik.abha.ui.main.activity.AbdmActivity
import org.commcare.dalvik.abha.ui.main.activity.AbdmResponseCode
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
        val intent = Intent().apply {
            putExtra("health_id",binding.model?.healthId)
            putExtra("code", AbdmResponseCode.SUCCESS.value)
            putExtra("verified", binding.model?.status)
            putExtra("message", "")
        }

        (activity as AbdmActivity).onAbhaNumberVerification(intent)
    }
}