package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.example.storyapp.MainActivity
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.utils.Result
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            login(email, password)
        }
        startAnimations()
    }

    private fun startAnimations() {
        binding.edLoginEmailLayout.translationX = -1000f
        binding.edLoginPasswordLayout.translationX = 1000f
        binding.btnLogin.translationY = 1000f

        val emailEditTextAnimator =
            ObjectAnimator.ofFloat(binding.edLoginEmailLayout, "translationX", 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }

        val passwordEditTextAnimator =
            ObjectAnimator.ofFloat(binding.edLoginPasswordLayout, "translationX", 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }

        val buttonAnimator = ObjectAnimator.ofFloat(binding.btnLogin, "translationY", 0f).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(emailEditTextAnimator, passwordEditTextAnimator, buttonAnimator)
            start()
        }
    }

    private fun login(email: String, password: String) {
        authViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val user = result.data
                        showToast("Login Successfully")

                        lifecycleScope.launch {
                            authViewModel.saveUser(user)
                        }

                        // Navigate to MainActivity
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

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