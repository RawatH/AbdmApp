package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.SelectAuthMethodBinding
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel

class SelectAuthenticationFragment :
    BaseFragment<SelectAuthMethodBinding>(SelectAuthMethodBinding::inflate), AdapterView.OnItemClickListener {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
        observeUiState()
        fetchAuthMehtods()
    }

    private fun fetchAuthMehtods() {
        arguments?.getString("abha_id")?.apply {
            viewModel.getAuthenticationMethods(this)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect{
                    when(it){
                        is GenerateAbhaUiState.Success -> {
                            val filter = listOf("AADHAAR_OTP","MOBILE_OTP")
                            val authList = mutableListOf<String>()
                            it.data.getAsJsonArray("auth_methods")
                                .forEach {
                                    if(filter.contains(it.asString)) {
                                       authList.add(it.asString)
                                    }
                            }
                            val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, authList)
                            (binding.authSelection as? MaterialAutoCompleteTextView)?.setAdapter(adapter)
                            binding.startVerfication.isEnabled = true
                        }

                    }
                }
            }

        }
    }


    override fun onClick(view: View?) {
        super.onClick(view)
        arguments?.getString("abha_id")?.let { healthId ->
            viewModel.selectedAuthMethod?.let {
                viewModel.getAuthOtp(healthId,it)
            }
        }

//        val bundle = bundleOf("mode" to "verification")
//        findNavController().navigate(
//            R.id.action_selectAuthenticationFragment_to_verifyMobileOtpFragment,
//            bundle
//        )

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position).toString().let {
            viewModel.selectedAuthMethod = it
        }
    }
}