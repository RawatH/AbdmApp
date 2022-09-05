package org.commcare.dalvik.data.network

import okhttp3.Interceptor
import okhttp3.Response


class  HeaderInterceptor:Interceptor {
   companion object{
        var API_KEY= ""
    }

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("content-type", "application/json")
                .addHeader("abdm_api_key", API_KEY)
                .build()
        )
    }


}