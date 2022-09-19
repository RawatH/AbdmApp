package org.commcare.dalvik.abha.utility

import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.commcare.dalvik.domain.model.LanguageManager
import org.commcare.dalvik.domain.model.TranslationKey
import org.json.JSONException
import java.lang.NullPointerException

class BindingUtils {
    companion object{
        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun setTranslatedText(view: Button, key: TranslationKey) {
            try {
                val translatedText = LanguageManager.getTranslatedValue(key)
                view.text = translatedText
            }catch (e:JSONException){
                view.text = key.name
            }
        }

        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun setTranslatedText(view: TextInputEditText, key: TranslationKey) {
            try {
                val hintText = LanguageManager.getTranslatedValue(key)
                view.hint = hintText
            }catch (e:JSONException){
                view.hint = key.name
            }
        }

        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun setTranslatedText(view: TextInputLayout, key: TranslationKey) {
            try {
                val hintText = LanguageManager.getTranslatedValue(key)
                view.hint = hintText
            }catch (e:JSONException){
                view.hint = key.name
            }
        }

        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun setTranslatedText(view: TextView, key: TranslationKey) {
            try {
                val hintText = LanguageManager.getTranslatedValue(key)
                view.text = hintText
            }catch (e:JSONException){
                view.text = key.name
            }
        }

        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun setTranslatedText(view: CheckBox, key: TranslationKey) {
            try {
                val text = LanguageManager.getTranslatedValue(key)
                view.text = text
            }catch (e:JSONException){
                view.text = key.name
            }
        }

        @JvmStatic
        @BindingAdapter("translatedTextKey")
        fun setTranslatedText(view: AutoCompleteTextView, key: TranslationKey) {
            try {
                val hintText = LanguageManager.getTranslatedValue(key)
                view.hint = hintText
            }catch (e:JSONException){
                view.hint = key.name
            }
        }



    }
}