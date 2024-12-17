package com.example.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.LoggedInUser
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<Result<String>>(Result.Initial)
    var registerState = _registerState.asStateFlow()

    val _loginState = MutableStateFlow<Result<LoggedInUser>>(Result.Initial)
    var loginState = _loginState.asStateFlow()


    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(name, email, password).collectLatest { result ->
                _registerState.value = result
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collectLatest { result ->
                _loginState.value = result
            }
        }
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