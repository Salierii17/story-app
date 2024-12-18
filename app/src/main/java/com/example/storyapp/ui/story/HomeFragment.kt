package com.example.storyapp.ui.story

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.LoadingStateAdapter
import com.example.storyapp.ui.StoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val storyViewModel: StoryViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        storyAdapter.retry()
    }

    private fun setupRecyclerView() {
        val layoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(requireContext(), 2)
            } else {
                LinearLayoutManager(requireContext())
            }

        binding.rvAllStories.layoutManager = layoutManager
        storyAdapter = StoryAdapter { storyItem ->
            navigateToStoryDetail(storyItem)
        }
        storyAdapter.addLoadStateListener { loadState ->
            val isLoading = loadState.source.refresh is LoadState.Loading
            val isEmpty =
                loadState.source.refresh is LoadState.NotLoading && storyAdapter.itemCount == 0

            showLoading(isLoading)
            showPlaceholder(isEmpty)
            showToast(getString(R.string.list_fetch_success))
        }

        binding.rvAllStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { storyAdapter.retry() }

        )
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            storyViewModel.stories.collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
            }
        }
    }


    private fun navigateToStoryDetail(storyItem: ListStoryItem) {
        val bundle = Bundle().apply {
            putString("story_id", storyItem.id)
        }
        findNavController().navigate(R.id.action_navigation_home_to_navigation_detail, bundle)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showPlaceholder(isEmpty: Boolean) {
        binding.placeholder.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}