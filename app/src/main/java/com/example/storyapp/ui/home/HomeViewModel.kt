package com.example.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {

    fun fetchStory() {
        viewModelScope.launch {
            repository.fetchStory()
        }
    }


    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}