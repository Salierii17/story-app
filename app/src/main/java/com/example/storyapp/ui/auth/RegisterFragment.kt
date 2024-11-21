package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.utils.Result

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            addUser(name, email, password)
        }

        startAnimations()

    }

    private fun startAnimations() {
        binding.edRegisterNameLayout.translationX = -1000f
        binding.edRegisterEmailLayout.translationX = 1000f
        binding.edRegisterPasswordLayout.translationX = 1000f
        binding.signupButton.translationY = 1000f

        val nameEditTextAnimator =
            ObjectAnimator.ofFloat(binding.edRegisterNameLayout, "translationX", 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }

        val emailEditTextAnimator =
            ObjectAnimator.ofFloat(binding.edRegisterEmailLayout, "translationX", 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }

        val passwordEditTextAnimator =
            ObjectAnimator.ofFloat(binding.edRegisterPasswordLayout, "translationX", 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }

        val buttonAnimator =
            ObjectAnimator.ofFloat(binding.signupButton, "translationY", 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }

        AnimatorSet().apply {
            playTogether(
                nameEditTextAnimator,
                emailEditTextAnimator,
                passwordEditTextAnimator,
                buttonAnimator
            )
            start()
        }
    }

    private fun addUser(name: String, email: String, password: String) {
        authViewModel.register(name, email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val message = result.data
                        showToast("Success: $message")

                        findNavController().navigate(R.id.action_navigation_registration_to_navigation_welcome)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showToast("Error: ${result.error}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}