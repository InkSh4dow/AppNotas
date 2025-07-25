package com.jry.notas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
Composable que define la interfaz de usuario del menú
Muestra opciones como cambiar al modo oscuro y navegar a la pantalla "Sobre la app".
*/

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MenuUI(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val menuHeight = screenHeight * 0.75f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(menuHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Menú",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = if (modoOscuro) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Icono Modo Oscuro",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Modo oscuro", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = modoOscuro,
                    onCheckedChange = onModoOscuroChange
                )
            }

            // Botón "Sobre la app" con diseño del botón GitHub
            Button(
                onClick = onNavigateToAbout,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Sobre la app",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Sobre la app", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
