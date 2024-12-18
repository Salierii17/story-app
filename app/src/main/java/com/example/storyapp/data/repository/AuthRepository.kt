package com.example.storyapp.data.repository

import android.util.Log
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.datastore.TokenManager
import com.example.storyapp.data.datastore.UserSessionManager
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userSessionManager: UserSessionManager,
    private val tokenManager: TokenManager
) {

    fun register(
        name: String, email: String, password: String
    ): Flow<Result<String>> = flow {
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

    fun login(email: String, password: String): Flow<Result<LoggedInUser>> = flow {
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

//    suspend fun saveUser(loggedInUser: LoggedInUser) = userSessionManager.saveUser(loggedInUser)

    val isUserLoggedIn: Flow<Boolean> = flow {
        emit(!tokenManager.getToken().isNullOrEmpty())
    }

    suspend fun saveToken(token: String) = tokenManager.saveToken(token)

    suspend fun logout() = tokenManager.clearToken()


    companion object {
        const val TAG = "AuthRepository"
    }


}