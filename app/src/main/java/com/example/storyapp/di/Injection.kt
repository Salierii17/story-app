package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.preferences.LoginDataSource
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.data.repository.LanguageRepository
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.preferences.UserPreferences

object Injection {

    private fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }

    fun provideLoginDataSource(context: Context): LoginDataSource {
        return LoginDataSource(context)
    }

    private fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences(context)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = provideApiService()
        val loginDataSource = provideLoginDataSource(context)
        return AuthRepository.getInstance(apiService, loginDataSource)
    }

    fun provideStoryRepository(): StoryRepository {
        val apiService = provideApiService()
        return StoryRepository.getInstance(apiService)
    }

    fun provideLanguageRepository(context: Context): LanguageRepository {
        val userPreferences = provideUserPreferences(context)
        return LanguageRepository.getInstance(userPreferences)
    }


}