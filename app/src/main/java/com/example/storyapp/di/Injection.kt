package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.repository.AuthRepository

object Injection {
    fun provideRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
//        val database = EventDatabase.getInstance(context)
//        val dao = database.eventDao()
//        return EventRepository.getInstance(apiService, dao)
        return AuthRepository.getInstance(apiService)
    }

}