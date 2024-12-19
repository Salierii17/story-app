package com.example.storyapp.data.repository

import com.example.storyapp.data.datastore.UserPreferences
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    private val userPreferences: UserPreferences
) {

    suspend fun getSavedLanguage(): String? {
        return userPreferences.getLanguage()
    }

    suspend fun saveLanguage(languageCode: String) {
        userPreferences.saveLanguage(languageCode)
    }
}