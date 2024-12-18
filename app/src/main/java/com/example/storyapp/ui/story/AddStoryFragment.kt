package com.example.storyapp.ui.story

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentAddStoryBinding
import com.example.storyapp.utils.Result
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private val storyViewModel: StoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)

        storyViewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                showImage(it)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding.buttonChoosePicture.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { submitStory() }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            storyViewModel.addStory.collectLatest { result ->
                when (result) {
                    is Result.Initial -> Unit
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        showToast(result.data.message)
                        findNavController().navigate(R.id.action_navigation_add_story_to_navigation_home)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            storyViewModel.setImageUri(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage(uri: Uri) {
        binding.ivAddPhoto.setImageURI(uri)
    }

    private fun submitStory() {
        storyViewModel.imageUri.value?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            storyViewModel.addStory(imageFile, description)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}