package org.commcare.dalvik.data.repository

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.commcare.dalvik.data.network.NetworkUtil
import org.commcare.dalvik.data.services.TranslationService
import org.commcare.dalvik.domain.model.LanguageManager
import org.commcare.dalvik.domain.model.TranslationModel
import org.commcare.dalvik.domain.repositories.TranslationRepository
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(private val translationService: TranslationService) :
    TranslationRepository {
    override suspend fun getTranslationData(langCode: String): TranslationModel? {
        val job = CoroutineScope(Dispatchers.IO).async {
            translationService.getTranslationData(NetworkUtil.getTranslationEndpoint(langCode)).body()
        }

        return job.await() ?: Gson().fromJson(
            LanguageManager.DEFAULT_TRANSLATIONS,
            TranslationModel::class.java
        )
    }
}