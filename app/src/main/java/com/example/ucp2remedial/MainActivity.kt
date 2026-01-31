package com.example.ucp2remedial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ucp2remedial.data.AppDatabase
import com.example.ucp2remedial.data.LibraryRepository
import com.example.ucp2remedial.ui.LibraryApp
import com.example.ucp2remedial.ui.LibraryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = AppDatabase.getDatabase(this)
        val repository = LibraryRepository(database.libraryDao())


        val viewModel = LibraryViewModel(repository)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                LibraryApp(viewModel = viewModel)
            }
        }
    }
}