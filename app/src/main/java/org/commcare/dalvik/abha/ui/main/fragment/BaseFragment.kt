package org.commcare.dalvik.abha.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.callbackFlow

abstract class BaseFragment<B : ViewBinding>(val bindingInflater: (layoutInflater: LayoutInflater) -> B) :
    Fragment(), View.OnClickListener {

    var mViewDatabinding: B? = null

    val binding: B
        get() = mViewDatabinding as B


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewDatabinding = bindingInflater.invoke(inflater)

        if (mViewDatabinding == null) {
            throw IllegalAccessException()
        }
        return binding.root
    }

    abstract fun onFragmentReady()

    override fun onClick(view: View?) {

    }

}