package org.commcare.dalvik.abha.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.AbhaDetailBinding
import org.commcare.dalvik.abha.databinding.KeyValueBinding
import org.commcare.dalvik.abha.ui.main.activity.AbdmActivity
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel

class AbhaCreationResultFragment : BaseFragment<AbhaDetailBinding>(AbhaDetailBinding::inflate) {
    private val viewModel: GenerateAbhaViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
        binding.model = viewModel.abhaDetailModel.value
        renderAadhaarData()
    }


    private fun renderAadhaarData() {
        viewModel.abhaDetailModel.value?.getAadhaarDataList()?.forEachIndexed { index, kvModel ->
            val kvBinding = KeyValueBinding.inflate(LayoutInflater.from(requireContext()))
            if (index % 2 == 0) {
                kvBinding.tableRow.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            } else {
                kvBinding.tableRow.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey_lighter
                    )
                )
            }
            kvBinding.model = kvModel
            binding.aadhaarDataTableLayout.addView(kvBinding.root)
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)

        val intent = Intent()
        intent.putExtra("abha_id",binding.model?.healthIdNumber)

        if(binding.shareWithCC.isChecked) {
            intent.putExtra("response_status" ,binding.model?.data?.toString())
        }
        (activity as AbdmActivity).onAbhaNumberReceived(intent)
    }


}