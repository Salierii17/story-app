package com.example.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.ui.auth.AuthActivity
import com.example.storyapp.ui.auth.AuthViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireActivity())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            logout()
        }
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