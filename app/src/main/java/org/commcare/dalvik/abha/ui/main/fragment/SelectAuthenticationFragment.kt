package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.SelectAuthMethodBinding
import org.commcare.dalvik.abha.ui.main.activity.AbdmActivity
import org.commcare.dalvik.abha.ui.main.activity.VerificationMode
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.abha.viewmodel.RequestType
import org.commcare.dalvik.domain.model.LanguageManager

class SelectAuthenticationFragment :
    BaseFragment<SelectAuthMethodBinding>(SelectAuthMethodBinding::inflate),
    AdapterView.OnItemClickListener {

    private val viewModel: GenerateAbhaViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
        observeUiState()
        fetchAuthMehtods()
    }

    private fun fetchAuthMehtods() {
        if(hasNetworkConnectivity()) {
            arguments?.getString("abha_id")?.let {
                viewModel.getAuthenticationMethods(it)
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {
                        is GenerateAbhaUiState.Success -> {
                            when (it.requestType) {
                                RequestType.AUTH_METHODS -> {
                                    val filter = listOf("AADHAAR_OTP", "MOBILE_OTP")
                                    val authList = mutableListOf<String>()
                                    it.data.getAsJsonArray("auth_methods")
                                        .forEach {
                                            if (filter.contains(it.asString)) {
                                                authList.add(LanguageManager.getTranslatedValue(it.asString))
                                            }
                                        }
                                    val adapter =
                                        ArrayAdapter(
                                            requireContext(),
                                            R.layout.dropdown_item,
                                            authList
                                        )
                                    (binding.authSelection as? MaterialAutoCompleteTextView)?.apply {
                                        setAdapter(adapter)
                                        setOnItemClickListener(this@SelectAuthenticationFragment)
                                        if(authList.size == 1){
                                            setText(adapter.getItem(0).toString(),false)
                                        }
                                    }


                                }
                            }

                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        is GenerateAbhaUiState.Error -> {
                            (activity as AbdmActivity).showBlockerDialog(it.data.get("message").asString)
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                        is GenerateAbhaUiState.AbdmError -> {
                            (activity as AbdmActivity).showBlockerDialog(it.data.getActualMessage())
                            viewModel.uiState.emit(GenerateAbhaUiState.Loading(false))
                        }

                    }
                }
            }

        }
    }


    override fun onClick(view: View?) {
        super.onClick(view)
        navigateToNextScreen()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position).toString().let {
            viewModel.selectedAuthMethod = it
            binding.startVerfication.isEnabled = true
        }
    }

    private fun navigateToNextScreen() {
        viewModel.selectedAuthMethod?.let {
            if (it == "AADHAAR_OTP") {
                val bundle = bundleOf(
                    "authMethod" to viewModel.selectedAuthMethod ,
                    "verificationMode" to VerificationMode.CONFIRM_AADHAAR_OTP,
                    "abhaId" to arguments?.getString("abha_id")
                )
                findNavController().navigate(
                    R.id.action_selectAuthenticationFragment_to_verifyAadhaarOtpFragment,
                    bundle
                )
            } else {
                val bundle = bundleOf(
                    "authMethod" to viewModel.selectedAuthMethod ,
                    "verificationMode" to VerificationMode.CONFIRM_MOBILE_OTP,
                    "abhaId" to arguments?.getString("abha_id")
                )
                findNavController().navigate(
                    R.id.action_selectAuthenticationFragment_to_verifyMobileOtpFragment,
                    bundle
                )
            }
        }

    }
}