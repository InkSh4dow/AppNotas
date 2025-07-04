package com.jry.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var isTitleError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = TaskDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    fun validateAndSave() {
        if (title.isBlank()) {
            isTitleError = true
        } else {
            scope.launch {
                db.taskDao().insertTask(Task(title = title, description = description))
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Tarea", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton({ navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { validateAndSave() },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Guardar")
                        Spacer(Modifier.width(4.dp))
                        Text("Guardar")
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    if (isTitleError) isTitleError = false
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                isError = isTitleError,
                supportingText = {
                    if (isTitleError) {
                        Text("El título no puede estar vacío", color = MaterialTheme.colorScheme.error)
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
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                placeholder = { Text("Escribe aquí tu tarea...") },
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
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}