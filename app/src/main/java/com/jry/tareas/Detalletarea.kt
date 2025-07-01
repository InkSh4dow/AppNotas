package com.jry.tareas

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private const val ANIMATION_DURATION_MS = 100

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TaskDetailScreen(navController: NavController, taskId: Int) {
    val context = LocalContext.current
    val database = remember { TaskDatabase.getDatabase(context) }
    val task by database.taskDao().getTaskById(taskId).collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    var isExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var taskBackgroundColor by remember { mutableStateOf(Color.Transparent) }
    var isInEditMode by remember { mutableStateOf(false) }

    var editableTitle by remember { mutableStateOf("") }
    var editableDescription by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        delay(10)
        isExpanded = true
    }

    LaunchedEffect(task) {
        task?.let {
            editableTitle = it.title
            editableDescription = it.description
        }
    }


    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            delay(ANIMATION_DURATION_MS.toLong())
            navController.popBackStack()
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val transition = updateTransition(targetState = isExpanded, label = "expansionTransition")

    val boxColor by transition.animateColor(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) {
            if (taskBackgroundColor != Color.Transparent) taskBackgroundColor
            else MaterialTheme.colorScheme.background
        } else MaterialTheme.colorScheme.secondary
    }
    val contentColor by transition.animateColor(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSecondary
    }
    val boxWidth by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) screenWidth else 56.dp
    }
    val boxHeight by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) screenHeight else 56.dp
    }
    val cornerRadius by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) 0.dp else 28.dp
    }
    val boxPadding by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) 0.dp else 16.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = boxPadding, top = boxPadding)
                .size(width = boxWidth, height = boxHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(boxColor)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(delayMillis = 300)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { isExpanded = false }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = contentColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    when (val taskValue = task) {
                        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = contentColor)
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
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = taskValue.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = contentColor
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                if (taskValue.description.isNotBlank()) {
                                    Text(
                                        text = taskValue.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = contentColor
                                    )
                                }

                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

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
                                    Color.Transparent,
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
                                                    if (color == Color.Transparent)
                                                        MaterialTheme.colorScheme.outline
                                                    else color
                                                )
                                                .clickable {
                                                    taskBackgroundColor = color
                                                    showColorPicker = false
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (color == Color.Transparent) {
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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .shadow(8.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = {
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
                            }) {
                                Icon(
                                    imageVector = if (isInEditMode) Icons.Default.Done else Icons.Default.Edit,
                                    contentDescription = if (isInEditMode) "Guardar" else "Editar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Borrar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(onClick = {
                                showColorPicker = !showColorPicker
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Cambiar fondo",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = MaterialTheme.colorScheme.onSecondary
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
