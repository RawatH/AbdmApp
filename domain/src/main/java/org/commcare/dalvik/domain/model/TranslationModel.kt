package org.commcare.dalvik.domain.model

import com.google.gson.JsonObject


data class TranslationModel(
    val meta: Meta,
    val data: JsonObject
) {

    fun getTranslatedString(key: String):String {
        return data[key]?.let {
            it.asString
        } ?: key
    }

}

class Meta(val code: String)


