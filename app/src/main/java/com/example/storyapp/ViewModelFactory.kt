package com.example.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.LoginDataSource
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.auth.AuthViewModel
import com.example.storyapp.ui.story.StoryViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository,
    private val loginDataSource: LoginDataSource
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return createViewModel(modelClass)
            ?: throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
    }

    private fun <T : ViewModel> createViewModel(modelClass: Class<T>): T? {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(storyRepository,loginDataSource) as T
            }

            else -> null
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideAuthRepository(context),
                Injection.provideStoryRepository(context),
                Injection.provideLoginDataSource(context)
            )
        }.also { instance = it }
    }
}