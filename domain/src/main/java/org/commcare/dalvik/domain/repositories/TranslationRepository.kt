package org.commcare.dalvik.domain.repositories

import org.commcare.dalvik.domain.model.LanguageCode
import org.commcare.dalvik.domain.model.TranslationModel

interface TranslationRepository {
    suspend fun getTranslationData(langCode:LanguageCode) :TranslationModel?
}