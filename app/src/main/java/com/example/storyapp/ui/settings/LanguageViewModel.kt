package com.example.storyapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repository: LanguageRepository
) : ViewModel() {

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> get() = _language

    fun loadLanguage() {
        viewModelScope.launch {
            val savedLanguage = repository.getSavedLanguage()
            _language.value = savedLanguage ?: "en"
        }
    }

    fun saveLanguage(languageCode: String) {
        viewModelScope.launch {
            repository.saveLanguage(languageCode)
        }
    }
}

