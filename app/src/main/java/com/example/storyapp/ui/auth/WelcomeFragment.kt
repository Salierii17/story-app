package com.example.storyapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupButton.setOnClickListener { navigateToRegistration() }
        binding.loginButton.setOnClickListener { navigateToLogin() }
    }

    private fun navigateToRegistration() {
        val extras = FragmentNavigatorExtras(
            binding.titleTextView to "title"
        )
        findNavController().navigate(
            R.id.action_navigation_welcome_to_navigation_registration, null, null, extras
        )
    }

    private fun navigateToLogin() {
        val extras = FragmentNavigatorExtras(
            binding.titleTextView to "title"
        )
        findNavController().navigate(
            R.id.action_navigation_welcome_to_navigation_login, null, null, extras
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}