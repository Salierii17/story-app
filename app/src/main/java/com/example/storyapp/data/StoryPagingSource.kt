package com.example.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.model.ListStoryItem
import javax.inject.Inject

class StoryPagingSource @Inject constructor(
    private val apiService: ApiService,
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(page, params.loadSize)
            val listStories = responseData.listStory

            val sortedStories = listStories.sortedByDescending { it.createdAt }

            LoadResult.Page(
                data = sortedStories,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (listStories.isEmpty()) null else page + 1
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
        const val INITIAL_PAGE_INDEX = 1
    }
}