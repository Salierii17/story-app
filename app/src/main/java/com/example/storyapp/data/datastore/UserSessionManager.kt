package com.example.storyapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.model.LoggedInUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    suspend fun saveUser(loggedInUser: LoggedInUser) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = loggedInUser.userId
            preferences[USER_NAME_KEY] = loggedInUser.name
            preferences[USER_TOKEN_KEY] = loggedInUser.token
        }
    }

    val user = context.dataStore.data.map { preferences ->
        val userId = preferences[USER_ID_KEY]
        val name = preferences[USER_NAME_KEY]
        val token = preferences[USER_TOKEN_KEY]
        if (userId != null && name != null && token != null) {
            LoggedInUser(userId, name, token)
        } else {
            null
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val userPreferences = dataStore.data.firstOrNull()
        return userPreferences?.get(USER_TOKEN_KEY) != null
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    }

}