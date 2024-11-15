package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.utils.Result

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

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Register : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

}