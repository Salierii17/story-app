package com.example.storyapp.data.repository

import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.RegisterResponse

class AuthRepository private constructor(
    private val apiService: ApiService
) {
    companion object {

        const val TAG = "AuthRepository"

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }

    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

}