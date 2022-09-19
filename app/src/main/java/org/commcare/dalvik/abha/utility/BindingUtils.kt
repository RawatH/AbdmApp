package org.commcare.dalvik.abha.utility

import android.widget.Button
import androidx.databinding.BindingAdapter
import org.commcare.dalvik.domain.model.LanguageManager
import org.commcare.dalvik.domain.model.TranslationKey
import org.json.JSONException

class BindingUtils {
    companion object{
        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun loadImage(view: Button, key: TranslationKey) {
            try {
                val translatedText = LanguageManager.getTranslatedValue(key)
                view.text = translatedText
            }catch (e:JSONException){
                view.text = key.name
            }
        }
    }
}