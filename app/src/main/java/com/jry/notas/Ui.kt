package com.jry.notas

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

//Funcion para ordenar las notas

enum class SortOrder {
    // Orden por defecto (generalmente por fecha de creación)
    DEFAULT,
    // Orden alfabético ascendente (A-Z)
    ASCENDING,
    // Orden alfabético descendente (Z-A)
    DESCENDING
}

/**
Composable que representa la pantalla principal de la aplicación
Muestra una lista de notas en una cuadrícula. Permite al usuario
añadir nuevas notas, buscarlas, ordenarlas y acceder al menú de la aplicación
*/

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(navController: NavController, taskDao: TaskDao) {
    val contexto = LocalContext.current
    val tareas by taskDao.getAllTasks().collectAsState(initial = emptyList())

    val datos = remember { Datos(contexto) }
    val modoOscuro by datos.darkMode.collectAsState(initial = false)

    val scope = rememberCoroutineScope()
    val estadoModal = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    var orden by remember { mutableStateOf(SortOrder.DEFAULT) }
    val tareasOrdenadas by remember(tareas, orden) {
        derivedStateOf {
            when (orden) {
                SortOrder.ASCENDING -> tareas.sortedBy { it.title }
                SortOrder.DESCENDING -> tareas.sortedByDescending { it.title }
                SortOrder.DEFAULT -> tareas
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { estadoModal.show() } }) {
                        Icon(Icons.Default.Menu, "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, "Buscar")
                    }
                    var filtroMenuExpandido by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { filtroMenuExpandido = true }) {
                            Icon(Icons.Default.FilterList, "Filtros")
                        }
                        MaterialTheme(
                            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenu(
                                expanded = filtroMenuExpandido,
                                onDismissRequest = { filtroMenuExpandido = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Por creación", style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        orden = SortOrder.DEFAULT
                                        filtroMenuExpandido = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Alfabéticamente (A-Z)", style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        orden = SortOrder.ASCENDING
                                        filtroMenuExpandido = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Alfabéticamente (Z-A)", style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        orden = SortOrder.DESCENDING
                                        filtroMenuExpandido = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("addTask") },
                icon = { Icon(Icons.Default.Add, "Nueva nota") },
                text = { Text("Nueva nota", style = MaterialTheme.typography.bodyLarge) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            )
        }
    ) { paddingValues ->
        if (tareas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LottieAnimacion(assetName = "vacio.lottie", size = 250.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Aún no hay Notas",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "¡Añade una para empezar!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tareasOrdenadas, key = { it.id }) { tarea ->
                    val cardColor = remember(tarea.colorHex) {
                        if (tarea.colorHex != null) {
                            try {
                                Color(tarea.colorHex.toColorInt())
                            } catch (e: Exception) {
                                null // Devolver null para usar el color por defecto
                            }
                        } else {
                            null
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("taskDetail/${tarea.id}") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor ?: MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = tarea.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (tarea.description.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = tarea.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 5,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (estadoModal.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { estadoModal.hide() } },
                sheetState = estadoModal
            ) {
                MenuUI(
                    modoOscuro = modoOscuro,
                    onModoOscuroChange = { nuevoValor ->
                        scope.launch {
                            datos.guardarModoOscuro(nuevoValor)
                        }
                    },
                    onNavigateToAbout = {
                        scope.launch { estadoModal.hide() }
                        navController.navigate("about")
                    }
                )
            }
        }
    }
}
