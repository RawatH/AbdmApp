package org.commcare.dalvik.abha.utility

import android.graphics.Typeface
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import org.commcare.dalvik.data.network.HeaderInterceptor


fun TextInputEditText.checkMobileFirstNumber():Boolean{
    if(text?.isEmpty() == true){
        return true
    }else{
        text?.first().toString().toInt().apply {
            return this in IntRange(6, 9)
        }
    }
}

fun Toolbar.changeToolbarFont(){
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (view is TextView && view.text == title) {
            view.typeface = Typeface.createFromAsset(view.context.assets, "fonts/nunitosans_bold")
            break
        }
    }
}