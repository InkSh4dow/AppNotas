package com.jry.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    var showMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val settings = remember { Datos(context) }
    val darkMode by settings.darkMode.collectAsStateWithLifecycle(false)
    val db = remember { TaskDatabase.getDatabase(context) }
    val tasks by db.taskDao().getAllTasks().collectAsStateWithLifecycle(emptyList())

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            // Top bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ showMenu = true }) {
                        Icon(Icons.Default.Menu, "Menú", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(
                        "Tasks",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Box(Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.secondary)) {
                        IconButton({ navController.navigate("search") }) {
                            Icon(Icons.Default.Search, "Buscar", tint = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
            // Grid de tareas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().weight(1f).background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    Box(
                        Modifier
                            .padding(8.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable { navController.navigate("taskDetail/${task.id}") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(task.title, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            // Bottom bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({}) {
                        Icon(Icons.Default.FilterAlt, "Filtros", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.secondary)) {
                        IconButton({ navController.navigate("addTask") }) {
                            Icon(Icons.Default.Add, "Agregar", tint = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
        }
        // Menú modal
        if (showMenu) ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            sheetState = sheetState
        ) {
            MenuUI(darkMode) { newMode -> scope.launch { settings.saveDarkMode(newMode) } }
        }
    }
}
