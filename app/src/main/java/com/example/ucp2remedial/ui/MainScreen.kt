package com.example.ucp2remedial.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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

        MenuCard("Buku", "Kelola data buku & relasi pengarang", Color(0xFF3F51B5)) {
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
    var selectedParentId by remember { mutableStateOf<Long?>(null) }
    var expandParent by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedIdToDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kategori Hierarkis") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Kategori Baru") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedButton(onClick = { expandParent = true }, modifier = Modifier.fillMaxWidth()) {
                    val parentName = categories.find { it.id == selectedParentId }?.name ?: "Pilih Induk (Opsional)"
                    Text(text = parentName)
                }
                DropdownMenu(expanded = expandParent, onDismissRequest = { expandParent = false }) {
                    DropdownMenuItem(text = { Text("Tidak Ada Induk") }, onClick = {
                        selectedParentId = null
                        expandParent = false
                    })
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedParentId = cat.id
                                expandParent = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                viewModel.addCategory(name, selectedParentId)
                name = ""
                selectedParentId = null
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan Kategori")
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(categories) { cat ->
                    val parentName = categories.find { it.id == cat.parentId }?.name ?: "-"
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(cat.name, fontWeight = FontWeight.Bold)
                                Text("Induk: $parentName", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = {
                                selectedIdToDelete = cat.id
                                showDialog = true
                            }) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Kategori") },
            text = { Text("Opsi penghapusan buku terkait:") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteCategory(selectedIdToDelete!!, true)
                    showDialog = false
                }) { Text("Hapus Buku Juga") }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.deleteCategory(selectedIdToDelete!!, false)
                    showDialog = false
                }) { Text("Ubah jadi Tanpa Kategori") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(viewModel: LibraryViewModel, navController: NavController) {
    val books by viewModel.books.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val authors by viewModel.authors.collectAsState()

    var title by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("TERSEDIA") }
    var selectedCatId by remember { mutableStateOf<Long?>(null) }
    var selectedAuthorIds by remember { mutableStateOf<List<Long>>(emptyList()) }

    var expandCat by remember { mutableStateOf(false) }
    var expandAuth by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buku & Pengarang") }, navigationIcon = {
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

            Box {
                OutlinedButton(onClick = { expandAuth = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Pilih Pengarang (${selectedAuthorIds.size} dipilih)")
                }
                DropdownMenu(expanded = expandAuth, onDismissRequest = { expandAuth = false }) {
                    authors.forEach { author ->
                        val isSelected = selectedAuthorIds.contains(author.id)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(author.name)
                                }
                            },
                            onClick = {
                                selectedAuthorIds = if (isSelected) {
                                    selectedAuthorIds - author.id
                                } else {
                                    selectedAuthorIds + author.id
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.addBook(title, status, selectedCatId, selectedAuthorIds)
                    title = ""
                    selectedAuthorIds = emptyList()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan Buku") }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(books) { book ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(book.title, fontWeight = FontWeight.Bold)
                            val catName = categories.find { it.id == book.categoryId }?.name ?: "Tanpa Kategori"
                            Text("Kategori: $catName", fontSize = 12.sp)
                            Text("Status: ${book.status}", fontSize = 12.sp, color = if(book.status == "DIPINJAM") Color.Red else Color.Green)
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