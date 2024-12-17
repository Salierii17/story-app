package com.example.storyapp.utils

sealed class Result<out R> private constructor() {
    data object Initial : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}