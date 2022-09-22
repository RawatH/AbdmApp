package org.commcare.dalvik.abha.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.*
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

import timber.log.Timber

@AndroidEntryPoint
class AbdmActivity : BaseActivity<AbdmActivityBinding>(AbdmActivityBinding::inflate) {

    private val TAG = "AbdmActivity"
    private lateinit var navHostFragment: NavHostFragment
    val viewmodel: GenerateAbhaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding?.apply {
            setSupportActionBar(this.toolbarContainer.toolbar)
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
        viewmodel.getTranslation("hin")
    }

    private fun checkForBlockScenario() {
        lifecycleScope.launch(Dispatchers.Main){
            viewmodel.checkIfBlocked().collect{ ts ->
                ts?.let {
                    val blockTimeSpent = System.currentTimeMillis() - ts.toLong()
                    if (blockTimeSpent < AppConstants.OTP_BLOCK_TS) {
                        val timeLeft = AppConstants.OTP_BLOCK_TS - blockTimeSpent
                        val minutesLeft = (timeLeft/1000)/60
                        val secondsLeft = (timeLeft/1000)%60
                        val timeLeftStr = minutesLeft.toString()+"min : ${secondsLeft}sec"
                        DialogUtility.showDialog(this@AbdmActivity,
                            resources.getString(R.string.app_blocked,timeLeftStr) ,
                            {dispatchResult()},DialogType.Blocking)
                    }else{
                        viewmodel.clearBlockState()
                    }
                }

            }
        }
    }

    /**
     * OTP failure check
     */
    fun observeOtpFailure() {
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


    fun observeLoader() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.uiState.collect {
                    Timber.d("ABDM FLOW -> ${it}")
                    when (it) {
                        is GenerateAbhaUiState.Loading -> {
                            Log.d(TAG, "LOADER VISIBILITY ${it.isLoading}")
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


    fun inflateNavGraph() {
        val bundle = intent.extras ?: bundleOf()


        HeaderInterceptor.API_KEY = bundle.getString("abdm_api_key", "11")

        intent.putExtras(bundle)
        val inflater = navController.navInflater

        val navGraph: Int =
            if (intent.hasExtra("abha_id")) R.navigation.abha_verification_navigation else
                R.navigation.abha_creation_navigation
        val graph = inflater.inflate(navGraph)
        graph.addInDefaultArgs(intent.extras)
        navController.setGraph(graph, bundle)
    }

    fun onAbhaNumberReceived() {
        finish()
    }

    fun onAbhaNumberVerification() {
        finish()
    }

    override fun getNavHostId(): Int {
        return R.id.nav_host_fragment
    }

    fun dispatchResult(intent: Intent? = null) {
        setResult(111, intent)
        finish()
    }

}