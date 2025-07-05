package com.jry.tareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF228B22), onPrimary = Color.White, secondary = Color(0xFF228B22),
    onSecondary = Color.White, surface = Color(0xFF181A20), onSurface = Color.White, background = Color(0xFF121212)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF66BB6A), onPrimary = Color.Black, secondary = Color(0xFF66BB6A),
    onSecondary = Color.White, surface = Color.White, onSurface = Color.Black, background = Color(0xFFF2F2F2)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppNavigation() }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val datos = remember { Datos(context) }
    val darkMode by datos.darkMode.collectAsStateWithLifecycle(false)
    val colorScheme = if (darkMode) DarkColorScheme else LightColorScheme
    val database = remember { TaskDatabase.getDatabase(context) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TipografiaApp
    ) {
        val navController = rememberNavController()
        AppNavGraph(navController = navController, database = database)
    }
}

@Composable
fun AppNavGraph(navController: NavHostController, database: TaskDatabase) {
    NavHost(
        navController,
        startDestination = "home"
    ) {
        composable("home") { Home(navController, database.taskDao()) }
        composable("addTask") {
            AddTaskScreen(navController = navController, taskDao = database.taskDao())
        }
        composable("about") {
            AboutScreen(navController = navController)
        }
        composable(
            "taskDetail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            if (taskId != null) {
                TaskDetailScreen(navController = navController, taskId = taskId, taskDao = database.taskDao())
            }
        }
        composable("search") {
            SearchScreen(navController = navController, taskDao = database.taskDao())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, navController: NavHostController) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla para '$title'", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
