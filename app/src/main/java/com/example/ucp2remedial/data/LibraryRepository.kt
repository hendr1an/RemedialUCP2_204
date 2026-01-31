package com.example.ucp2remedial.data

import kotlinx.coroutines.flow.Flow

class LibraryRepository(private val dao: LibraryDao) {

    fun getAllCategories(): Flow<List<Category>> = dao.getAllCategories()
    fun getAllBooks(): Flow<List<Book>> = dao.getAllBooks()
    fun getAllAuthors(): Flow<List<Author>> = dao.getAllAuthors()

    suspend fun addCategory(name: String) {
        val category = Category(name = name)
        dao.insertCategory(category)
    }

    suspend fun addBook(title: String, status: String, categoryId: Long?) {
        val book = Book(title = title, status = status, categoryId = categoryId)
        dao.insertBook(book)
    }

    suspend fun addAuthor(name: String) {
        val author = Author(name = name)
        dao.insertAuthor(author)
    }

    suspend fun deleteCategory(categoryId: Long, deleteBooks: Boolean) {
        val books = dao.getBooksByCategory(categoryId)
        val isAnyBookBorrowed = books.any { it.status == "DIPINJAM" }

        if (isAnyBookBorrowed) {
            throw Exception("Gagal: Ada buku yang sedang dipinjam dalam kategori ini.")
        }

        if (deleteBooks) {
            dao.softDeleteBooksByCategory(categoryId)
            logAudit("DELETE_BOOKS", "Books in category $categoryId soft deleted")
        } else {
            dao.removeCategoryFromBooks(categoryId)
            logAudit("UPDATE_BOOKS", "Books in category $categoryId set to No Category")
        }

        dao.softDeleteCategory(categoryId)
        logAudit("DELETE_CATEGORY", "Category $categoryId soft deleted")
    }

    private suspend fun logAudit(action: String, details: String) {
        val log = AuditLog(timestamp = System.currentTimeMillis(), action = action, details = details)
        dao.insertAudit(log)
    }
}