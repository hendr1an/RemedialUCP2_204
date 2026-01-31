package com.example.ucp2remedial.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LibraryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Insert
    suspend fun insertBook(book: Book)

    @Insert
    suspend fun insertAudit(log: AuditLog)

    @Query("SELECT * FROM categories WHERE isDeleted = 0")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM books WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun getBooksByCategory(categoryId: Long): List<Book>

    @Query("UPDATE categories SET isDeleted = 1 WHERE id = :categoryId")
    suspend fun softDeleteCategory(categoryId: Long)

    @Query("UPDATE books SET isDeleted = 1 WHERE categoryId = :categoryId")
    suspend fun softDeleteBooksByCategory(categoryId: Long)

    @Query("UPDATE books SET categoryId = NULL WHERE categoryId = :categoryId")
    suspend fun removeCategoryFromBooks(categoryId: Long)
}