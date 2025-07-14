package com.jry.tareas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

/**
 Este archivo muestra los detalles de una nota específica.
 Esta pantalla permite ver, editar y eliminar una tarea. El usuario puede alternar
 entre un modo de visualización y un modo de edición.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(navController: NavController, taskId: Int, taskDao: TaskDao) {
    // Estado para controlar si la UI está en modo de edición.
    var isEditing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val task by taskDao.getTaskById(taskId).collectAsState(initial = null)

    // Estados para los campos de texto en el modo de edición.
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    
    var showError by remember { mutableStateOf(false) }
    // Estado para controlar la visibilidad del diálogo de eliminación
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Fondo de la nota
    val taskBackgroundColor = remember(task?.colorHex) {
        task?.colorHex?.let {
            try {
                Color(it.toColorInt())
            } catch (e: IllegalArgumentException) {
                Log.e("TaskDetailScreen", "Error al parsear el color: $it", e)
                null // Devuelve null si el color es inválido.
            }
        }
    }

    // Muestra el diálogo de confirmación para eliminar la nota
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta nota? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            task?.let {
                                taskDao.deleteTask(it)
                                navController.popBackStack()
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Efecto que se ejecuta cuando la tarea cambia o cuando se sale del modo de edición
    // Sincroniza los campos de edición con los datos actuales de la tarea
    LaunchedEffect(task, isEditing) {
        if (!isEditing) {
            task?.let {
                editTitle = it.title
                editDescription = it.description
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    task?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Icono de nota",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = it.title,
                                maxLines = 1,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // Cambia el color de la barra superior según el modo y el color de la nota
                    containerColor = if (isEditing) MaterialTheme.colorScheme.surface else taskBackgroundColor ?: MaterialTheme.colorScheme.surface
                )
            )
        },
        // Cambia el color de fondo de la pantalla
        containerColor = if (isEditing) MaterialTheme.colorScheme.background else taskBackgroundColor ?: MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Muestra un indicador de carga mientras se obtiene la nota
            when (val taskValue = task) {
                null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> {
                    // Muestra los campos de edición o el texto de visualización
                    if (isEditing) {
                        // Campo de texto para editar el título
                        TextField(
                            value = editTitle,
                            onValueChange = {
                                editTitle = it
                                if (showError) showError = false
                            },
                            label = { Text("Título", style = MaterialTheme.typography.bodyLarge) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            isError = showError,
                            supportingText = {
                                if (showError) {
                                    Text("El título no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Campo de texto para editar la descripción
                        TextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Descripción", style = MaterialTheme.typography.bodyLarge) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        // Muestra la descripción de la tarea en modo de solo lectura
                        if (taskValue.description.isNotBlank()) {
                            Text(
                                text = taskValue.description,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 24.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            val bottomBarBackgroundColor = MaterialTheme.colorScheme.surface
            val bottomBarContentColor = MaterialTheme.colorScheme.onSurface

            // Barra de acciones inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(bottomBarBackgroundColor)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Botón para Editar/Guardar
                    ActionButton(
                        icon = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                        text = if (isEditing) "Guardar" else "Editar",
                        onClick = {
                            if (isEditing) {
                                if (editTitle.isBlank()) {
                                    showError = true
                                } else {
                                    scope.launch {
                                        task?.let {
                                            val updatedTask = it.copy(
                                                title = editTitle,
                                                description = editDescription
                                            )
                                            taskDao.updateTask(updatedTask)
                                        }
                                        isEditing = false
                                    }
                                }
                            } else {
                                // Entra en modo de edición
                                isEditing = true
                            }
                        },
                        iconTint = bottomBarContentColor
                    )

                    // Botón para cambiar el color 
                    ActionButton(
                        icon = Icons.Default.Circle,
                        text = "Color",
                        onClick = { /**/ },
                        iconTint = taskBackgroundColor ?: bottomBarContentColor
                    )

                    // Botón para borrar la tarea
                    ActionButton(
                        icon = Icons.Default.Delete,
                        text = "Borrar",
                        onClick = { showDeleteDialog = true },
                        iconTint = bottomBarContentColor
                    )
                }
            }
        }
    }
}

/**
 Un Composable privado para los botones de acción en la barra inferior.
  */
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    iconTint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = iconTint
        )
    }
}
