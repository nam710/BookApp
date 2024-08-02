package com.example.bookapp.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStoreInstance(val context:Context) {



    companion object {
        val LOGGED_IN = booleanPreferencesKey("loggedIn")
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("login")
    }

    suspend fun storeLoginState(loggedIn: Boolean) {
        context.dataStore.edit {
            it[LOGGED_IN] = loggedIn
        }
    }

    fun getStoredLoginState() = context.dataStore.data.map {
        it[LOGGED_IN] ?: false
    }
}

