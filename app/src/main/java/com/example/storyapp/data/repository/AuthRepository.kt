package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.utils.Result
import com.google.gson.Gson
import retrofit2.HttpException

class AuthRepository private constructor(
    private val apiService: ApiService, private val loginDataSource: LoginDataSource
) {
    companion object {

        const val TAG = "AuthRepository"

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService, loginDataSource: LoginDataSource
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, loginDataSource)
        }.also { instance = it }
    }

    fun register(
        name: String, email: String, password: String
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            //get success message
            val message = apiService.register(name, email, password).message
            emit(Result.Success(message))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: e.message.toString()
            emit(Result.Error(errorMessage))
            Log.e(TAG, "Register : $errorMessage")
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoggedInUser>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password).loginResult
            val user = LoggedInUser(
                userId = response?.userId.toString(),
                name = response?.name.toString(),
                token = response?.token.toString()
            )
            loginDataSource.saveUser(user)
            emit(Result.Success(user))
        } catch (e: Exception) {
            Log.e(TAG, "Login : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }


    fun getLoggedInUser() =  loginDataSource.user

    suspend fun logout() {
        loginDataSource.logout()
    }


}