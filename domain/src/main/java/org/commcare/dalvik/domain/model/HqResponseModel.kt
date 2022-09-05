package org.commcare.dalvik.domain.model

sealed class HqResponseModel<out T>() {
    class Success<T>(val data: T) : HqResponseModel<T>()
    class Error<T>(val error: T) : HqResponseModel<T>()
    object Loading : HqResponseModel<Nothing>()
}
