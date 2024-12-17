package com.example.storyapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.settingsDataStore

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


