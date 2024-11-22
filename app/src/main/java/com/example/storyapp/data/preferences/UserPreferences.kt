package com.example.storyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.userPreferencesStore by preferencesDataStore(name = "settings")

class UserPreferences(context: Context) {

    private val dataStore: DataStore<Preferences> = context.userPreferencesStore

    suspend fun saveLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    suspend fun getLanguage(): String? {
        val preferences = dataStore.data.first()
        return preferences[LANGUAGE_KEY]
    }

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

}


