package com.example.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository.Companion.TAG
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class StoryPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val bearerToken = "Bearer $token"
            val position = params.key ?: INTIAL_PAGE_INDEX
            val responseData = apiService.getStories(bearerToken, position, params.loadSize)
            val listStories = responseData.listStory

            LoadResult.Page(
                data = listStories,
                prevKey = if (position == INTIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (position == INTIAL_PAGE_INDEX) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val INTIAL_PAGE_INDEX = 1
    }
}