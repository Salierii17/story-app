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
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

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