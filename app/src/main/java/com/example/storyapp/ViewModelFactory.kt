package com.example.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.auth.AuthViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val loginDataSource: LoginDataSource
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, loginDataSource) as T
        }
        else {
            throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideRepository(context),
                Injection.provideLoginDataSource(context)
            )
        }.also { instance = it }
    }
}