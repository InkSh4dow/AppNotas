package com.jry.tareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val settingsDataStore = remember { Datos(context) }
    val darkMode by settingsDataStore.darkMode.collectAsStateWithLifecycle(initialValue = false)


    val colorScheme = if (darkMode) {
        darkColorScheme(
            primary = Color(0xFF228B22),
            onPrimary = Color.White,
            secondary = Color(0xFF228B22),
            onSecondary = Color.White,
            surface = Color(0xFF181A20),
            onSurface = Color.White,
            background = Color(0xFF121212)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF66BB6A),
            onPrimary = Color.Black,
            secondary = Color(0xFF66BB6A),
            onSecondary = Color.White,
            surface = Color.White,
            onSurface = Color.Black,
            background = Color(0xFFF2F2F2)
        )
    }

    MaterialTheme(colorScheme = colorScheme, typography = TipografiaApp) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                Home(navController = navController)
            }
            composable("addTask") {
                AddTaskScreen(navController = navController)
            }
            composable(
                "taskDetail/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId")
                taskId?.let {
                    TaskDetailScreen(navController = navController, taskId = it)
                }
            }
        }
    }
}