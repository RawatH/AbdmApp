package org.commcare.dalvik.abha.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.commcare.dalvik.abha.BuildConfig
import timber.log.Timber


@HiltAndroidApp
class AbdmApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}