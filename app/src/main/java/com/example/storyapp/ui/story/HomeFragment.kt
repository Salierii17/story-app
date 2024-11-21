package com.example.storyapp.ui.story

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.StoryAdapter
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.auth.AuthViewModel
import com.example.storyapp.utils.Result

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireActivity())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        storyViewModel.fetchStory()

    }


    private fun setupRecyclerView() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvAllStories.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            binding.rvAllStories.layoutManager = LinearLayoutManager(requireContext())
        }
        storyAdapter = StoryAdapter { storyItem ->
            navigateToStoryDetail(storyItem)
        }
        binding.rvAllStories.adapter = storyAdapter
    }

    private fun setupObservers() {
        storyViewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    showPlaceholder(false)
                }

                is Result.Success -> {
                    showLoading(false)
                    showPlaceholder(false)
                    val storyData = result.data
                    Log.d("HomeFragment", "Fetched stories: $storyData")
                    storyAdapter.submitList(storyData)
                    showToast(getString(R.string.list_fetch_success))
                }

                is Result.Error -> {
                    showLoading(false)
                    showPlaceholder(true)
                    showToast(result.error)
                }
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