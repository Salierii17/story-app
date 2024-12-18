package com.example.storyapp.ui

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.example.storyapp.DataDummy
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.ui.story.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = flowOf(data)
        `when`(storyRepository.getStory()).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)

        storyViewModel.stories.test {
            val actualQuote = awaitItem()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualQuote)

            // Check the story is not null
            Assert.assertNotNull(differ.snapshot())
            // Check the story size match dummy data
            Assert.assertEquals(dummyStory.size, differ.snapshot().size)
            // Check the first story data match  dummy data
            Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedFlow = flowOf(data)
        `when`(storyRepository.getStory()).thenReturn(expectedFlow)

        val storyViewModel = StoryViewModel(storyRepository)

        storyViewModel.stories.test {
            val actualQuote = awaitItem()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualQuote)

            Assert.assertEquals(0, differ.snapshot().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    class StoryPagingSource : PagingSource<Int, Flow<List<ListStoryItem>>>() {
        override fun getRefreshKey(state: PagingState<Int, Flow<List<ListStoryItem>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flow<List<ListStoryItem>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }

        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}