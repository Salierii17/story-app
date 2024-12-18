package com.example.storyapp.ui.story

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository,
) : ViewModel() {

    private val _addStory = MutableStateFlow<Result<StoryResponse>>(Result.Initial)
    val addStory = _addStory.asStateFlow()

    private val _storyDetail = MutableStateFlow<Result<ListStoryItem>>(Result.Initial)
    val storyDetail = _storyDetail.asStateFlow()

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val refreshTrigger = MutableStateFlow(Unit) // Used to trigger refresh

    @OptIn(ExperimentalCoroutinesApi::class)
    val stories: Flow<PagingData<ListStoryItem>> = refreshTrigger
        .flatMapLatest { repository.getStory() }
        .map { pagingData ->
            pagingData.map { it }
        }


    fun addStory(file: File, description: String) {
        viewModelScope.launch {
            repository.addStory(file, description).collectLatest { result ->
                Log.d("StoryViewModel", "StateFlow emitting result: $result")
                _addStory.value = result
                refreshTrigger.value = Unit
            }
        }
    }

    fun fetchDetailStory(id: String) {
        viewModelScope.launch {
            repository.getDetailStory(id).collectLatest { result ->
                _storyDetail.value = result
            }
        }
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun refreshPagingData() {
        refreshTrigger.value = Unit // Emit a new value to trigger refresh
    }
}