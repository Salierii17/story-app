package com.example.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.Result
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StoryRepository, private val dataSource: LoginDataSource
) : ViewModel() {


    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> get() = _stories

    fun fetchStory() {
        viewModelScope.launch {
            dataSource.user.collect { loggedInUser ->
                loggedInUser?.let { user ->
                    repository.fetchStory(user.token).observeForever { result ->
                        _stories.postValue(result)
                    }
                } ?: run { _stories.postValue(Result.Error("User not logged in")) }
            }
        }
    }

//    fun fetchStory() = repository.fetchStory()


    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}