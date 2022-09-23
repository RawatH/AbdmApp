package org.commcare.dalvik.domain.model

import com.google.gson.JsonObject


data class TranslationModel(
    val meta: Meta,
    val data: JsonObject
) {

    fun getTranslatedString(key: String): String {
        return if (data.has(key)) {
            data[key]?.let {
               return it.asString
            } ?:key
        } else {
           return LanguageManager.getDefaultTranslation(key)
        }
    }

}

class Meta(val code: String)





