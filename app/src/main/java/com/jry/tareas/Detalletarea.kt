package com.jry.tareas

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(navController: NavController, taskId: Int, taskDao: TaskDao) {
    var isEditing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val task by taskDao.getTaskById(taskId).collectAsState(initial = null)

    var editTitle by remember { mutableStateOf(TextFieldValue()) }
    var editDescription by remember { mutableStateOf(TextFieldValue()) }
    var showError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val taskBackgroundColor = remember(task?.colorHex) {
        task?.colorHex?.let {
            try {
                Color(android.graphics.Color.parseColor(it))
            } catch (e: IllegalArgumentException) {
                Log.e("TaskDetailScreen", "Error parsing color: $it", e)
                null
            }
        }
    }

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

    LaunchedEffect(task, isEditing) {
        if (!isEditing) {
            task?.let {
                editTitle = TextFieldValue(it.title)
                editDescription = TextFieldValue(it.description)
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
                    containerColor = if (isEditing) MaterialTheme.colorScheme.surface else taskBackgroundColor ?: MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = if (isEditing) MaterialTheme.colorScheme.background else taskBackgroundColor ?: MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val taskValue = task) {
                null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> {
                    if (isEditing) {
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
                    ActionButton(
                        icon = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                        text = if (isEditing) "Guardar" else "Editar",
                        onClick = {
                            if (isEditing) {
                                if (editTitle.text.isBlank()) {
                                    showError = true
                                } else {
                                    scope.launch {
                                        task?.let {
                                            val updatedTask = it.copy(
                                                title = editTitle.text,
                                                description = editDescription.text
                                            )
                                            taskDao.updateTask(updatedTask)
                                        }
                                        isEditing = false
                                    }
                                }
                            } else {
                                isEditing = true
                            }
                        },
                        iconTint = bottomBarContentColor
                    )

                    ActionButton(
                        icon = Icons.Default.Circle,
                        text = "Color",
                        onClick = { /* Accion de color */ },
                        iconTint = taskBackgroundColor ?: bottomBarContentColor
                    )

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
