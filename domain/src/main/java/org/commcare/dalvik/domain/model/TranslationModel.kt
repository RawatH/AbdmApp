package org.commcare.dalvik.domain.model

import com.google.gson.JsonObject


data class TranslationModel(
    val meta: Meta,
    val data: JsonObject
) {

    fun getTranslatedString(key: String) = data[key].asString

}

class Meta(val code: String)


