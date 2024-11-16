package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.repository.AuthRepository

object Injection {
    fun provideRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val loginDataSource = LoginDataSource(context)
        return AuthRepository.getInstance(apiService, loginDataSource)
    }

    fun provideLoginDataSource(context: Context): LoginDataSource {
        return LoginDataSource(context)
    }

}