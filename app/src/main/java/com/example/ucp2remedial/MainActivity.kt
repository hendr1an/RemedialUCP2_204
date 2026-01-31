package com.example.ucp2remedial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.ucp2remedial.data.AppDatabase
import com.example.ucp2remedial.data.LibraryRepository
import com.example.ucp2remedial.ui.LibraryViewModel
import com.example.ucp2remedial.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = LibraryRepository(database.libraryDao())
        val viewModel = LibraryViewModel(repository)

        setContent {
            Surface {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}