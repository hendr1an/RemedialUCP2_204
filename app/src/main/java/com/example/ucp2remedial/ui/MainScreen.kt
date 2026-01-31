package com.example.ucp2remedial.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun LibraryApp(viewModel: LibraryViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("category") { CategoryScreen(viewModel, navController) }
        composable("book") { BookScreen(viewModel, navController) }
        composable("author") { AuthorScreen(viewModel, navController) }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "Sistem Manajemen Data Buku",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
        )

        MenuCard("Buku", "Kelola data buku", Color(0xFF3F51B5)) {
            navController.navigate("book")
        }
        MenuCard("Kategori", "Kelola kategori hierarkis", Color(0xFF3F51B5)) {
            navController.navigate("category")
        }
        MenuCard("Pengarang", "Kelola data pengarang", Color(0xFF3F51B5)) {
            navController.navigate("author")
        }
    }
}

@Composable
fun MenuCard(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(100.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(viewModel: LibraryViewModel, navController: NavController) {
    val categories by viewModel.categories.collectAsState()
    var name by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kategori") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kategori") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.addCategory(name)
                    name = ""
                }) { Icon(Icons.Default.Add, contentDescription = null) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(categories) { cat ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat.name)
                            IconButton(onClick = {
                                selectedId = cat.id
                                showDialog = true
                            }) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedId != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Kategori") },
            text = { Text("Apa yang harus dilakukan dengan buku di kategori ini?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteCategory(selectedId!!, true)
                    showDialog = false
                }) { Text("Hapus Buku Juga") }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.deleteCategory(selectedId!!, false)
                    showDialog = false
                }) { Text("Biarkan (Tanpa Kategori)") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(viewModel: LibraryViewModel, navController: NavController) {
    val books by viewModel.books.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var title by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("TERSEDIA") }
    var selectedCatId by remember { mutableStateOf<Long?>(null) }
    var expandCat by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buku") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(16.dp)) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Judul Buku") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedButton(onClick = { expandCat = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = categories.find { it.id == selectedCatId }?.name ?: "Pilih Kategori")
                }
                DropdownMenu(expanded = expandCat, onDismissRequest = { expandCat = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedCatId = cat.id
                                expandCat = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.addBook(title, status, selectedCatId)
                    title = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tambah Buku") }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(books) { book ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(book.title, fontWeight = FontWeight.Bold)
                            Text("Status: ${book.status}", fontSize = 12.sp)
                            val catName = categories.find { it.id == book.categoryId }?.name ?: "Tanpa Kategori"
                            Text("Kategori: $catName", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorScreen(viewModel: LibraryViewModel, navController: NavController) {
    val authors by viewModel.authors.collectAsState()
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pengarang") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nama Pengarang") }, modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.addAuthor(name)
                    name = ""
                }) { Icon(Icons.Default.Add, contentDescription = null) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(authors) { author ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Text(text = author.name, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}