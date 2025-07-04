package com.jry.tareas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var q by rememberSaveable { mutableStateOf("") }
    val ctx = LocalContext.current
    val tasks by remember { TaskDatabase.getDatabase(ctx).taskDao().getAllTasks() }
        .collectAsStateWithLifecycle(emptyList())
    val filtered = if (q.isBlank()) emptyList() else tasks.filter {
        it.title.contains(q, true) || it.description.contains(q, true)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TopAppBar(
                    title = {
                        TextField(
                            value = q,
                            onValueChange = { q = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar tareas...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton({ navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                        }
                    },
                    modifier = Modifier
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (q.isNotBlank() && filtered.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LottieAnimacion(assetName = "vacio.lottie", size = 200.dp)
                    Text("No se encontraron resultados.")
                }
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { task ->
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            navController.navigate("taskDetail/${task.id}")
                        },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                task.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
