package com.jry.tareas

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight", "UseKtx")
@Composable
fun TaskDetailScreen(navController: NavController, taskId: Int) {
    val context = LocalContext.current
    val database = remember { TaskDatabase.getDatabase(context) }
    val task by database.taskDao().getTaskById(taskId).collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    val taskBackgroundColor = remember(task) {
        task?.colorHex?.let {
            try {
                Color(it.toColorInt())
            } catch (e: IllegalArgumentException) {
                Log.e("TaskDetailScreen", "Error al parsear el color hexadecimal: $it", e)
                null
            }
        }
    }
    var isInEditMode by remember { mutableStateOf(false) }

    var editableTitle by remember { mutableStateOf("") }
    var editableDescription by remember { mutableStateOf("") }

    LaunchedEffect(task, isInEditMode) {
        if (!isInEditMode) {
            task?.let {
                editableTitle = it.title
                editableDescription = it.description
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
                    containerColor = taskBackgroundColor ?: MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = taskBackgroundColor ?: MaterialTheme.colorScheme.background
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
                    if (isInEditMode) {
                        OutlinedTextField(
                            value = editableTitle,
                            onValueChange = { editableTitle = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editableDescription,
                            onValueChange = { editableDescription = it },
                            label = { Text("Descripción") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp)
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

            if (showColorPicker) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Seleccionar color de fondo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val colors = listOf(
                            null,
                            Color(0xFF4CAF50),
                            Color(0xFF2196F3),
                            Color(0xFFFF9800),
                            Color(0xFF9C27B0),
                            Color(0xFFF44336),
                            Color(0xFF607D8B)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(colors) { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            color ?: MaterialTheme.colorScheme.outline
                                        )
                                        .clickable {
                                            scope.launch {
                                                task?.let { currentTask ->
                                                    val newHex = color?.let { c ->
                                                        String.format("#%08X", c.toArgb())
                                                    }
                                                    val updatedTask = currentTask.copy(colorHex = newHex)
                                                    database.taskDao().updateTask(updatedTask)
                                                }
                                            }
                                            showColorPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (color == null) {
                                        Text(
                                            text = "×",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val bottomBarBackgroundColor = if (taskBackgroundColor != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            val bottomBarContentColor = if (taskBackgroundColor != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary

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
                        icon = if (isInEditMode) Icons.Default.Done else Icons.Default.Edit,
                        text = if (isInEditMode) "Guardar" else "Editar",
                        onClick = {
                            if (isInEditMode) {
                                scope.launch {
                                    task?.let {
                                        val updatedTask = it.copy(
                                            title = editableTitle,
                                            description = editableDescription
                                        )
                                        database.taskDao().updateTask(updatedTask)
                                    }
                                    isInEditMode = false
                                }
                            } else {
                                isInEditMode = true
                            }
                        },
                        iconTint = bottomBarContentColor
                    )

                    ActionButton(
                        icon = Icons.Default.Circle,
                        text = "Color",
                        onClick = { showColorPicker = !showColorPicker },
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de que quieres eliminar esta tarea? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            task?.let { taskToDelete ->
                                database.taskDao().deleteTask(taskToDelete)
                                showDeleteDialog = false
                                navController.popBackStack()
                            }
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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
