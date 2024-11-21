package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.utils.Result
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

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
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)?.message
            } catch (ex: Exception) {
                "Unknown error occurred"
            } ?: "Unknown error occurred"
            emit(Result.Error(errorMessage))
            Log.e(TAG, "fetchStory: $errorMessage")
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.localizedMessage}"))
            Log.e(TAG, "fetchStory: Network error", e)
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.localizedMessage}"))
            Log.e(TAG, "fetchStory: Unexpected error", e)
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
            Log.e(TAG, "fetchDetailStory : $errorMessage")
        }
    }

    fun addStory(token: String, imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", imageFile.name, requestImageFile
        )
        try {
            val message = apiService.addStory("Bearer $token", multipartBody, requestBody)
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: e.message.toString()
            emit(Result.Error(errorMessage))
            Log.e(TAG, "addStory : $errorMessage")
        }
    }


}