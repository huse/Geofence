package com.hus.geofence.authentication

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.clear
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey

class AuthenticationPreference(context: Context) {
    private val appContext = context.applicationContext
    private val dataStorePref: DataStore<Preferences>
    companion object {
        private val KEY_TO_CHECK_IF_LOG_IN = preferencesKey<Boolean>("key_is_user_logged_in")
        private val KEY_FOR_USERNAME = preferencesKey<String>("key_username")
        private val KEY_FOR_EMAIL = preferencesKey<String>("key_email")
        private const val TAG = "AuthenticationPreference"
    }

    init {
        dataStorePref = appContext.createDataStore(
            name = "data_store_created"
        )
    }
    private val checkUserLogIn: Flow<Boolean?>
        get() = dataStorePref.data.map { preferences ->
            preferences[KEY_TO_CHECK_IF_LOG_IN]
        }

    suspend fun savingUsrAndPass(username: String, email: String){
        dataStorePref.edit { preferences ->
            preferences[KEY_FOR_EMAIL] = email
            preferences[KEY_FOR_USERNAME] = username
            preferences[KEY_TO_CHECK_IF_LOG_IN] = true
        }
        Log.d(TAG, "savingUsrAndPass    "+ checkUserLogIn())
    }

    suspend fun checkUserLogIn(): Boolean = checkUserLogIn.first() ?: false

    suspend fun clearData(){
        dataStorePref.edit { preferences ->
            preferences.clear()
        }
    }



}