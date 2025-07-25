package com.jry.notas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 Este archivo nos muestra la pantalla para crear una nueva tarea.
 Permite al usuario introducir un título y una descripción para una nueva tarea
 y guardarla en la base de datos
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, taskDao: TaskDao) {
    // Estados para almacenar el título y la descripción de la tarea.
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // Estado para mostrar un error si el título está vacío al intentar guardar
    var showError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nueva Tarea",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    Button(
                        onClick = {
                            // Esto valida que el título no esté vacío
                            if (title.isBlank()) {
                                showError = true
                            } else {
                                scope.launch {
                                    val task = Task(
                                        title = title,
                                        description = description
                                    )
                                    taskDao.insert(task)
                                    navController.popBackStack()
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                             containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Guardar", style = MaterialTheme.typography.bodyLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            // Botón flotante para cancelar y volver al inicio
            FloatingActionButton(
                onClick = { navController.popBackStack() },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de texto para el título
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    showError = false // Ocultar el error al empezar a escribir
                },
                label = { Text("Título", style = MaterialTheme.typography.bodyLarge) },
                placeholder = { Text("Escribe el título de tu tarea...", style = MaterialTheme.typography.bodyLarge) },
                leadingIcon = {
                    Icon(Icons.Default.Create, contentDescription = "Título")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = showError,
                supportingText = if (showError) {
                    { Text("El título es obligatorio", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Campo de texto para la descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción", style = MaterialTheme.typography.bodyLarge) },
                placeholder = { Text("Añade una descripción...", style = MaterialTheme.typography.bodyLarge) },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = "Descripción")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}