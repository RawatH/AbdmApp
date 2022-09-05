package org.commcare.dalvik.abha.ui.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.abha.application.AbdmApplication
import org.commcare.dalvik.abha.databinding.AbdmActivityBinding
import org.commcare.dalvik.abha.viewmodel.GenerateAbhaViewModel
import org.commcare.dalvik.data.network.HeaderInterceptor

@AndroidEntryPoint
class AbdmActivity : BaseActivity<AbdmActivityBinding>(AbdmActivityBinding::inflate) {


    private lateinit var navHostFragment: NavHostFragment

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

    }



    fun inflateNavGraph() {
        val bundle = bundleOf("mobile_num" to "9560833229" , "abdm_api_key"  to "1122333")
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