package com.example.storyapp.data.repository

import com.example.storyapp.data.preferences.UserPreferences

class LanguageRepository private constructor(
    private val userPreferences: UserPreferences
) {
    companion object {

        @Volatile
        private var instance: LanguageRepository? = null
        fun getInstance(
            userPreferences: UserPreferences
        ): LanguageRepository = instance ?: synchronized(this) {
            instance ?: LanguageRepository(userPreferences)
        }.also { instance = it }
    }

    suspend fun getSavedLanguage(): String? {
        return userPreferences.getLanguage()
    }

    suspend fun saveLanguage(languageCode: String) {
        userPreferences.saveLanguage(languageCode)
    }

}