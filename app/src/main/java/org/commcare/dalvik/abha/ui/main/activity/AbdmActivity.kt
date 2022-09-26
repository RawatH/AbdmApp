package org.commcare.dalvik.abha.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.databinding.AbdmActivityBinding
import org.commcare.dalvik.abha.utility.AppConstants
import org.commcare.dalvik.abha.utility.DialogType
import org.commcare.dalvik.abha.utility.DialogUtility
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.data.network.HeaderInterceptor
import org.commcare.dalvik.data.util.PrefKeys
import org.commcare.dalvik.domain.model.LanguageManager
import org.commcare.dalvik.domain.model.TranslationKey
import timber.log.Timber
import java.io.Serializable

@AndroidEntryPoint
class AbdmActivity : BaseActivity<AbdmActivityBinding>(AbdmActivityBinding::inflate) {

    private lateinit var navHostFragment: NavHostFragment
    val viewmodel: GenerateAbhaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verifyIntentData()
        mBinding?.apply {
            setSupportActionBar(this.toolbarContainer.toolbar)
            intent.extras?.containsKey("abha_id")?.let { hasAbhaId ->
                if(hasAbhaId){
                    supportActionBar?.title = LanguageManager.getTranslatedValue(TranslationKey.ABHA_VERIFICATION)
                }else{
                    supportActionBar?.title = LanguageManager.getTranslatedValue(TranslationKey.ABHA_CREATION)
                }
            }
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        inflateNavGraph()
        setupActionBarWithNavController(navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        observeLoader()
        observeOtpFailure()
        checkForBlockScenario()

        intent.extras?.getString("lang_code")?.let {
            viewmodel.getTranslation(it)
        }

    }

    private fun verifyIntentData(){
        intent.extras?.containsKey("abdm_api_token")?.let {  tokenPresent ->
            if(!tokenPresent){
                dispatchResult(getErrorIntent("API token missing"))
            }
        }

        if( intent.extras?.containsKey("mobile_number") == false && intent.extras?.containsKey("abha_id") ==false){
            dispatchResult(getErrorIntent("Missing Mobile number / ABHA ID"))
        }

    }

    private fun checkForBlockScenario() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewmodel.checkIfBlocked().collect { ts ->
                ts?.let {
                    val blockTimeSpent = System.currentTimeMillis() - ts.toLong()
                    if (blockTimeSpent < AppConstants.OTP_BLOCK_TS) {
                        val timeLeft = AppConstants.OTP_BLOCK_TS - blockTimeSpent
                        val minutesLeft = (timeLeft / 1000) / 60
                        val secondsLeft = (timeLeft / 1000) % 60
                        val timeLeftStr = minutesLeft.toString() + "min : ${secondsLeft}sec"
                        DialogUtility.showDialog(
                            this@AbdmActivity,
                            resources.getString(R.string.app_blocked, timeLeftStr),
                            { dispatchResult(Intent()) }, DialogType.Blocking
                        )
                    } else {
                        viewmodel.clearBlockState()
                    }
                }

            }
        }
    }

    /**
     * OTP failure check
     */
    private fun observeOtpFailure() {
        lifecycleScope.launch {
            viewmodel.otpFailureCount.asFlow().collect { otpFailCount ->
                if (otpFailCount == 4) {
                    viewmodel.saveDataUsecase.executeSave(
                        System.currentTimeMillis().toString(),
                        PrefKeys.OTP_BLOCKED_TS.getKey()
                    )
                    viewmodel.otpFailureCount.value = 0
                    DialogUtility.showDialog(this@AbdmActivity, "Too many OTP attempts.")
                }
            }
        }
    }


    private fun observeLoader() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.uiState.collect {
                    Timber.d("EMISSION -> ${it}")
                    when (it) {
                        is GenerateAbhaUiState.Loading -> {
                            Timber.d("LOADER VISIBILITY ${it.isLoading}")
                            binding.loader.visibility =
                                if (it.isLoading) View.VISIBLE else View.GONE
                        }

                        is GenerateAbhaUiState.Blocked -> {
                            DialogUtility.showDialog(this@AbdmActivity, "Too many OTP attempts.")
                        }

                    }
                }
            }
        }
    }


    private fun inflateNavGraph() {
        val bundle = intent.extras ?: bundleOf()

        bundle.getString("abdm_api_token")?.let {
            HeaderInterceptor.API_KEY = it
        }



        intent.putExtras(bundle)
        val inflater = navController.navInflater

        val navGraph: Int =
            if (intent.hasExtra("abha_id")) R.navigation.abha_verification_navigation else
                R.navigation.abha_creation_navigation
        val graph = inflater.inflate(navGraph)
        graph.addInDefaultArgs(intent.extras)
        navController.setGraph(graph, bundle)
    }

    fun onAbhaNumberReceived(intent: Intent) {
        dispatchResult(intent)
    }

    fun onAbhaNumberVerification(intent: Intent) {
        dispatchResult(intent)
    }

    override fun getNavHostId(): Int {
        return R.id.nav_host_fragment
    }

    private fun dispatchResult(intent: Intent) {
        setResult(111, intent)
        finish()
    }

    private fun getErrorIntent(msg:String) = Intent().apply {
        putExtra("verified","failure")
        putExtra("response_status",msg)
    }

}

/**
 * Mode of Verification
 */
enum class VerificationMode : Serializable {
    VERIFY_MOBILE_OTP,
    VERIFY_AADHAAR_OTP,
    CONFIRM_MOBILE_OTP,
    CONFIRM_AADHAAR_OTP
}