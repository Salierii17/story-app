package com.example.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository, private val loginDataSource: LoginDataSource
) : ViewModel() {


    fun register(name: String, email: String, password: String) =
        authRepository.register(name, email, password)

    fun login(email: String, password: String) = authRepository.login(email, password)

    fun getLoggedInUser(): Flow<LoggedInUser?> {
        return authRepository.getLoggedInUser()
    }

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