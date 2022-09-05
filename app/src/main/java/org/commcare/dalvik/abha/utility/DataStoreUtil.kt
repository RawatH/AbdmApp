package org.commcare.dalvik.abha.utility

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import javax.inject.Singleton


@Singleton
class DataStoreUtil(val context: Context) {

    companion object {
        private val USER_PREFERENCES_NAME = "abdm_pref"

        private val Context.dataStore by preferencesDataStore(
            name = USER_PREFERENCES_NAME
        )

        val OTP_BLOCKED_TS = stringPreferencesKey("OTP_BLOCKED_TS")
        val MOBILE_OTP_REQ_TS = stringPreferencesKey("MOBILE_OTP_REQ_TS")
        val AADHAAR_OTP_REQ_TS = stringPreferencesKey("AADHAAR_OTP_REQ_TS")
    }

    suspend fun saveToDataStore(prefKey: Preferences.Key<String>, prefValue: String) {
        context.dataStore.edit {
            it[prefKey] = prefValue
        }
    }

    fun getFromDataStore(prefKey: Preferences.Key<String>) =
        context.dataStore.data.map {
            it[prefKey]
        }


    suspend fun clearDataStore() {
        context.dataStore.edit {
            it.clear()
        }
    }

}