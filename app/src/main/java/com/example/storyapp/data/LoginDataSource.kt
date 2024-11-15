package com.example.storyapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.model.LoggedInUser
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "user_prefs")

class LoginDataSource(private val context: Context) {

    private val dataStore = context.dataStore

    // Save user data (e.g., token)
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

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    }


//    fun login(email: String, password: String): Result<LoggedInUser> {
//        try {
//            // TODO: handle loggedInUser authentication
//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
//        } catch (e: Throwable) {
//            return Result.Error(IOException("Error logging in", e))
//        }
//    }

}