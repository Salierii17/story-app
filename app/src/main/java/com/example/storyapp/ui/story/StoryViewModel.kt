package com.example.storyapp.ui.story

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.preferences.LoginDataSource
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

class StoryViewModel(
    private val repository: StoryRepository,
    private val dataSource: LoginDataSource,
) : ViewModel() {

    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> get() = _stories

    private val _addStory = MutableLiveData<Result<StoryResponse>>()
    val addStory: LiveData<Result<StoryResponse>> get() = _addStory

    private val _storyDetail = MutableLiveData<Result<ListStoryItem>>()
    val storyDetail: LiveData<Result<ListStoryItem>> get() = _storyDetail

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri


    fun fetchStory() {
        viewModelScope.launch {
            val user = dataSource.user.firstOrNull()
            if (user == null) {
                _stories.postValue(Result.Error("User not logged in"))
            } else {
                repository.fetchStory(user.token).observeForever { response ->
                    _stories.postValue(response)
                }
            }
        }
    }

    fun addStory(file: File, description: String) {
        viewModelScope.launch {
            try {
                val user = dataSource.user.firstOrNull()
                if (user == null) {
                    _addStory.postValue(Result.Error("User not logged in"))
                } else {
                    repository.addStory(user.token, file, description).observeForever { result ->
                        _addStory.postValue(result)
                    }
                }
            } catch (e: Exception) {
                _stories.postValue(Result.Error("Error added story: ${e.localizedMessage}"))
            }
        }
    }

    fun fetchDetailStory(id: String) {
        viewModelScope.launch {
            try {
                val user = dataSource.user.firstOrNull()
                if (user == null) {
                    _storyDetail.postValue(Result.Error("User not logged in"))
                } else {
                    repository.fetchDetailStory(user.token, id).observeForever { result ->
                        _storyDetail.postValue(result)
                    }
                }
            } catch (e: Exception) {
                _storyDetail.postValue(
                    Result.Error(
                        "Error fetch detail story: ${e.message.toString()}"
                    )
                )
            }
        }
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

}