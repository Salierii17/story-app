package com.example.storyapp.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.databinding.FragmentDetailBinding
import com.example.storyapp.ui.home.HomeViewModel
import com.example.storyapp.utils.Result

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireActivity())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storyId = arguments?.getString("story_id") ?: ""

        homeViewModel.fetchDetailStory(storyId)

        setupObservers()

    }

    private fun setupObservers() {
        homeViewModel.storyDetail.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.loading.visibility = View.GONE
                    val storyData = result.data
                    Log.d("HomeFragment", "Fetched stories: $storyData")

                    setStoryDetailData(storyData)
                }

                is Result.Error -> {
                    binding.loading.visibility = View.GONE
                    showToast("Error: ${result.error}")
                }
            }
        }
    }

    private fun setStoryDetailData(storyData: ListStoryItem) {

        Glide.with(this).load(storyData.photoUrl).placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_broken_image)
            .into(binding.ivDetailPhoto)

        binding.tvDetailName.text = storyData.name
        binding.tvDetailDescription.text = storyData.description

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}