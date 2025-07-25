package com.jry.notas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

//Este archivo nos Muestra una pantalla de búsqueda para filtrar tareas por título o descripción

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, taskDao: TaskDao) {
    // Estado para almacenar el texto de búsqueda
    // incluso si la app se reinicia o se rota
    var q by rememberSaveable { mutableStateOf("") }

    // Recolecta la lista de todas las tareas desde la base de datos
    val tasks by taskDao.getAllTasks().collectAsStateWithLifecycle(emptyList())

    // Filtra las tareas basándose en la consulta 'q'.
    // `remember` optimiza el rendimiento al evitar recalcular la lista en cada recomposición,
    // a menos que 'q' o la lista de 'tasks' cambien.
    val filteredTasks = remember(q, tasks) {
        if (q.isBlank()) {
            emptyList()
        } else {
            tasks.filter {
                it.title.contains(q, ignoreCase = true) || it.description.contains(q, ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de búsqueda superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton({ navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                }
                TextField(
                    value = q,
                    onValueChange = { q = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    placeholder = { Text("Buscar nota...", style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    trailingIcon = {
                        // Muestra un botón para limpiar la búsqueda si hay texto
                        if (q.isNotEmpty()) {
                            IconButton(onClick = { q = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Contenido principal y nos muestra los resultados o un mensaje
            Column(modifier = Modifier.weight(1f)) {
                if (q.isNotBlank() && filteredTasks.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin resultados para '$q'", style = MaterialTheme.typography.bodyLarge)
                    }
                } else if (q.isNotBlank()) {
                    // Muestra la lista de resultados
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("taskDetail/${task.id}")
                                    },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(Modifier.padding(24.dp)) {
                                    Text(
                                        task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        task.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
