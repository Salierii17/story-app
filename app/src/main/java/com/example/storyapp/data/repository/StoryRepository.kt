package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.utils.Result
import com.google.gson.Gson
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    companion object {

        const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService)
        }.also { instance = it }
    }

    fun fetchStory(token: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val message = apiService.getStories("Bearer $token").listStory
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: e.message.toString()
            emit(Result.Error(errorMessage))
            Log.e(AuthRepository.TAG, "fetchStory : $errorMessage")
        }
    }

    fun fetchDetailStory(token: String, id: String): LiveData<Result<ListStoryItem>> = liveData {
        emit(Result.Loading)
        try {
            val message = apiService.getDetailStoriesDetail("Bearer $token", id).story
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: e.message.toString()
            emit(Result.Error(errorMessage))
            Log.e(AuthRepository.TAG, "fetchStory : $errorMessage")
        }
    }

}