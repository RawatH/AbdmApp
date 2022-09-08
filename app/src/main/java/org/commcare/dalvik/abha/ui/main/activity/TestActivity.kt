package org.commcare.dalvik.abha.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.data.network.HeaderInterceptor
import timber.log.Timber

class TestActivity : AppCompatActivity() {

    val REQ_CODE_A = 100
    val REQ_CODE_B = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        findViewById<Button>(R.id.intentA).setOnClickListener {
            startIntentB()
        }

        findViewById<Button>(R.id.intentB).setOnClickListener {
            startIntentA()
        }

        HeaderInterceptor.API_KEY = "Token 01bed27f81885164999b2adc0e28b8ba8cb58eda"

    }

    fun startIntentA() {
        val intent = Intent(this, AbdmActivity::class.java).apply {
            putExtras(
                bundleOf(
                    "abha_id" to "9560833229"
                )
            )
        }
        startActivityForResult(intent, REQ_CODE_A)

    }

    fun startIntentB() {

        val intent = Intent(this, AbdmActivity::class.java).apply {
            putExtras(
                bundleOf(
                    "mobile_num" to "7560833229",
                    "abdm_api_key" to "1122333"
                )
            )
        }
        startActivityForResult(intent, REQ_CODE_B)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("OnActivityResult--------${resultCode}")

        when (requestCode) {
            REQ_CODE_A -> {

            }

            REQ_CODE_B -> {

            }
        }

    }

}