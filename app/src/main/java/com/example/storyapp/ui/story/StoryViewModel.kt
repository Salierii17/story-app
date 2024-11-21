package com.example.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.R
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.Result
import kotlinx.coroutines.launch
import java.io.File

class StoryViewModel(
    private val repository: StoryRepository, private val dataSource: LoginDataSource
) : ViewModel() {

    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> get() = _stories

    private val _addStory = MutableLiveData<Result<StoryResponse>>()
    val addStory: LiveData<Result<StoryResponse>> get() = _addStory

    private val _storyDetail = MutableLiveData<Result<ListStoryItem>>()
    val storyDetail: LiveData<Result<ListStoryItem>> get() = _storyDetail

    fun fetchStory() {
        viewModelScope.launch {
            try {
                dataSource.user.collect { loggedInUser ->
                    loggedInUser?.let { user ->
                        repository.fetchStory(user.token).observeForever { result ->
                            _stories.postValue(result)
                        }
                    } ?: run { _stories.postValue(Result.Error(R.string.invalid_user.toString())) }
                }
            } catch (e: Exception) {
                _stories.postValue(Result.Error("Error fetching stories: ${e.localizedMessage}"))
            }
        }
    }

    fun addStory(file: File, description: String) {
        viewModelScope.launch {
            try {
                dataSource.user.collect { loggedInUser ->
                    loggedInUser?.let { user ->
                        repository.addStory(user.token, file, description)
                            .observeForever { result ->
                                _addStory.postValue(result)
                            }
                    } ?: run { _stories.postValue(Result.Error(R.string.invalid_user.toString())) }
                }
            } catch (e: Exception) {
                _stories.postValue(Result.Error("Error added story: ${e.localizedMessage}"))
            }
        }
    }

    fun fetchDetailStory(id: String) {
        viewModelScope.launch {
            dataSource.user.collect { loggedInUser ->
                loggedInUser?.let { user ->
                    repository.fetchDetailStory(user.token, id).observeForever { result ->
                        _storyDetail.postValue(result)
                    }
                } ?: run { _stories.postValue(Result.Error(R.string.invalid_user.toString())) }
            }
        }
    }
}