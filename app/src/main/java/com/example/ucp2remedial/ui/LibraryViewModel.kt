package com.example.ucp2remedial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp2remedial.data.Author
import com.example.ucp2remedial.data.Book
import com.example.ucp2remedial.data.Category
import com.example.ucp2remedial.data.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val books: StateFlow<List<Book>> = repository.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authors: StateFlow<List<Author>> = repository.getAllAuthors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tambahkan validasi cyclic sederhana
    fun addCategory(name: String, parentId: Long?) {
        viewModelScope.launch {
            repository.addCategory(name, parentId) // Pastikan repo juga terima parentId
        }
    }

    // Update fungsi ini menerima List Author
    fun addBook(title: String, status: String, categoryId: Long?, authorIds: List<Long>) {
        viewModelScope.launch {
            repository.addBook(title, status, categoryId, authorIds)
        }
    }
    fun addAuthor(name: String) {
        viewModelScope.launch { repository.addAuthor(name) }
    }

    fun deleteCategory(categoryId: Long, deleteBooks: Boolean) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(categoryId, deleteBooks)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}