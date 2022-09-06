package org.commcare.dalvik.abha.ui.main.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.application.AbdmApplication
import org.commcare.dalvik.abha.databinding.AbdmActivityBinding
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaUiState
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.data.network.HeaderInterceptor

@AndroidEntryPoint
class AbdmActivity : BaseActivity<AbdmActivityBinding>(AbdmActivityBinding::inflate) {

    private  val TAG = "AbdmActivity"
    private lateinit var navHostFragment: NavHostFragment
    lateinit var  loader: ProgressBar;
     val viewmodel:GenerateAbhaViewModel by viewModels()

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
    }

    fun observeLoader(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewmodel.uiState.collect {
                Log.d(TAG,"ABDM FLOW -> ${it}")
                when (it) {
                    is GenerateAbhaUiState.Loading -> {
                        Log.d(TAG,"LOADER VISIBILITY ${it.isLoading}")
                        binding.loader.visibility = if(it.isLoading) View.VISIBLE else View.GONE
                    }

                }
            }
        }
    }



    fun inflateNavGraph() {
        val bundle =
//            bundleOf("abha_id" to "7560833229")
            bundleOf("mobile_num" to "7560833229" , "abdm_api_key"  to "1122333")

        HeaderInterceptor.API_KEY = bundle.getString("abdm_api_key","11")

        intent.putExtras(bundle)
        val inflater = navController.navInflater

        val navGraph: Int =
            if (intent.hasExtra("abha_id")) R.navigation.abha_verification_navigation else
                R.navigation.abha_creation_navigation
        val graph = inflater.inflate(navGraph)
        graph.addInDefaultArgs(intent.extras)
        navController.setGraph(graph,bundle)
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


}