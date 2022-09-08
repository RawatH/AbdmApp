package org.commcare.dalvik.abha.di

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.commcare.dalvik.abha.R
import org.commcare.dalvik.data.network.HeaderInterceptor
import org.commcare.dalvik.data.network.NetworkUtil
import org.commcare.dalvik.data.services.HqServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideHttpLogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
//            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor , headerInterceptor: HeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor{chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token 01bed27f81885164999b2adc0e28b8ba8cb58eda")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(NetworkUtil.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideHqServices(retrofit: Retrofit): HqServices {
        return retrofit.create(HqServices::class.java)
    }


}