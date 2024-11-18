package com.example.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.StoryAdapter
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.auth.AuthActivity
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

        binding.actionLogout.setOnClickListener {
            logout()
        }
    }


    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { storyItem ->
            navigateToStoryDetail(storyItem.id)
        }

        binding.rvAllStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun setupObservers() {
        storyViewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.loading.visibility = View.GONE
                    val storyData = result.data
                    Log.d("HomeFragment", "Fetched stories: $storyData")
                    storyAdapter.submitList(storyData)
                    showToast("Success: Fetch Successfully")
                }

                is Result.Error -> {
                    binding.loading.visibility = View.GONE
                    showToast("Error: ${result.error}")
                }
            }
        }
    }

    private fun navigateToStoryDetail(id: String) {
        val bundle = Bundle().apply {
            putString("story_id", id)
        }
        findNavController().navigate(R.id.action_navigation_home_to_navigation_detail, bundle)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        authViewModel.logout()

        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}