package com.example.storyapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
) {

    private var currentPagingSource: StoryPagingSource? = null


    fun getStory(): Flow<PagingData<ListStoryItem>> {
        return Pager(config = PagingConfig(
            pageSize = 5,
        ), pagingSourceFactory = {
            StoryPagingSource(apiService).also {
                currentPagingSource = it
            }
        }).flow
    }

        fun invalidatePagingSource() {
            currentPagingSource?.invalidate()
        }

    fun fetchDetailStory(id: String): Flow<Result<ListStoryItem>> = flow {
        emit(Result.Loading)
        try {
            val message = apiService.getDetailStoriesDetail(id).story
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message ?: e.message.toString()
            emit(Result.Error(errorMessage))
            Log.e(TAG, "fetchDetailStory : $errorMessage")
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "fetchDetailStory : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.localizedMessage}"))
            Log.e(TAG, "fetchDetailStory: Unexpected error", e)
        }
    }

    fun addStory(imageFile: File, description: String) = flow {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", imageFile.name, requestImageFile
        )
        try {
            val message = apiService.addStory(multipartBody, requestBody)
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
            Log.e(TAG, "addStory : $errorMessage")
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "addStory : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.localizedMessage}"))
            Log.e(TAG, "addStory: Unexpected error", e)
        }
    }

    fun getStoriesWithLocation(): Flow<Result<List<ListStoryItem>>> = flow {
        emit(Result.Loading)
        try {
            val message = apiService.getStoriesWithLocation().listStory
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
            Log.e(TAG, "fetchStory: $errorMessage")
        } catch (e: IOException) {
            emit(Result.Error("No Internet Connection"))
            Log.e(TAG, "FetchStory : ${e.localizedMessage}")
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.localizedMessage}"))
            Log.e(TAG, "fetchStory: Unexpected error", e)
        }
    }


    companion object {
        const val TAG = "StoryRepository"
    }

}