package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.datastore.UserSessionManager
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.utils.Result
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userSessionManager: UserSessionManager
) {

    fun register(
        name: String, email: String, password: String
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            val message = apiService.register(name, email, password).message
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
            Log.e(TAG, "Register : $errorMessage")
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Register : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.e(TAG, "Register : ${e.localizedMessage}")
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
            userSessionManager.saveUser(user)
            emit(Result.Success(user))
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "Login : ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Login : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveUser(loggedInUser: LoggedInUser) = userSessionManager.saveUser(loggedInUser)

    suspend fun logout() = userSessionManager.logout()


    companion object {
        const val TAG = "AuthRepository"
    }


}