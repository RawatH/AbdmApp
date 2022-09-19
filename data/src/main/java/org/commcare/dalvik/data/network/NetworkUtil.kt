package org.commcare.dalvik.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.commcare.dalvik.domain.model.HqResponseModel
import retrofit2.Response

class NetworkUtil {
    companion object {
        const val BASE_URL = "https://staging.commcarehq.org/abdm/api/"
        const val TRANSLATION_BASE_URL = "https://raw.githubusercontent.com/"
        fun getTranslationEndpoint(code:String)=
            "https://raw.githubusercontent.com/dimagi/abdm-app/main/resources/languages/${code}/language.json"
    }
}

fun <T> safeApiCall(call: suspend () -> Response<T>): Flow<HqResponseModel<String>> = flow {
    this.emit(HqResponseModel.Loading)
    try {
        val response = call.invoke()
        response.let {
            if (response.isSuccessful) {
                emit(HqResponseModel.Success(it.body().toString()))
            } else {
                emit(HqResponseModel.Error("Error code ${it.code()}"))
            }
        }

    } catch (t: Throwable) {
        emit(HqResponseModel.Error(t.message.toString()))
    }
}
