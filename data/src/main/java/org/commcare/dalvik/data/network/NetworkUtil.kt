package org.commcare.dalvik.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.commcare.dalvik.domain.model.HqResponseModel
import retrofit2.Response

class NetworkUtil {
    companion object {
        const val BASE_URL = "https://virtserver.swaggerhub.com/AKASHJ1708/Test/1.0.0/"
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
