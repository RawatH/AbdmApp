package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.AbhaDetailBinding
import org.commcare.dalvik.abha.ui.main.activity.AbdmActivity
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel

class AbhaCreationResultFragment : BaseFragment<AbhaDetailBinding>(AbhaDetailBinding::inflate) {
    private val viewModel: GenerateAbhaViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
        binding.model = viewModel.abhaDetailModel.value
    }

    override fun onFragmentReady() {

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        Toast.makeText(requireContext(), "DONE", Toast.LENGTH_SHORT).show()
        (activity as AbdmActivity).onAbhaNumberReceived()
    }


}