package com.example.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.data.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerResponse = MutableLiveData<Result<RegisterResponse>>()
    val registerResponse: LiveData<Result<RegisterResponse>> get() = _registerResponse

    fun registerUser(name: String, email: String, password: String) =
        authRepository.registerUser(name, email, password)

}