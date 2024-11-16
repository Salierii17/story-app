package com.example.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) =
        authRepository.register(name, email, password)

    fun login(email: String, password: String) = authRepository.login(email, password)

    fun saveUser(loggedInUser: LoggedInUser) {
        viewModelScope.launch {
            authRepository.saveUser(loggedInUser)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}