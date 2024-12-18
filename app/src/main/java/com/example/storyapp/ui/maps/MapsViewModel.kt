package com.example.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _storiesMaps = MutableStateFlow<Result<List<ListStoryItem>>>(Result.Initial)
    val storiesMaps = _storiesMaps.asStateFlow()

//    val items = storyRepository.getItemStream().cachedIn(viewModelScope)

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            storyRepository.getStoriesWithLocation().collectLatest { result ->
                _storiesMaps.value = result
            }
        }
    }
}