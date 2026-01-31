package com.example.ucp2remedial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp2remedial.data.Category
import com.example.ucp2remedial.data.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(name, null)
            loadCategories()
        }
    }

    fun deleteCategory(categoryId: Long, deleteBooks: Boolean) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(categoryId, deleteBooks)
                _errorMessage.value = null
                loadCategories()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}