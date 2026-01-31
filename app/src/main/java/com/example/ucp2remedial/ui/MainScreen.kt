package com.example.ucp2remedial.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(viewModel: LibraryViewModel) {
    val categories by viewModel.categories.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Long?>(null) }
    var newCategoryName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Library Management", style = MaterialTheme.typography.headlineMedium)

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("New Category") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                viewModel.addCategory(newCategoryName)
                newCategoryName = ""
            }) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedCategory = category.id
                            showDialog = true
                        }
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    if (showDialog && selectedCategory != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Category") },
            text = { Text("Choose action for books in this category:") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteCategory(selectedCategory!!, deleteBooks = true)
                    showDialog = false
                }) {
                    Text("Delete Books Also")
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.deleteCategory(selectedCategory!!, deleteBooks = false)
                    showDialog = false
                }) {
                    Text("Keep Books (No Category)")
                }
            }
        )
    }
}