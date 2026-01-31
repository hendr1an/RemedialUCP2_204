package com.example.ucp2remedial.data

class LibraryRepository(private val dao: LibraryDao) {

    suspend fun getAllCategories() = dao.getAllCategories()

    suspend fun addCategory(name: String, parentId: Long?) {
        val category = Category(name = name, parentId = parentId)
        dao.insertCategory(category)
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
        val log = AuditLog(
            timestamp = System.currentTimeMillis(),
            action = action,
            details = details
        )
        dao.insertAudit(log)
    }
}