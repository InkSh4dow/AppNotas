package com.jry.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    var showMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Barra arriba (EN DESARROLLO)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6200EE))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Mi Aplicación",
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { /* TODO: Acción de búsqueda */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White
                    )
                }
            }

            // Lista de tareas (BASE SIN DESARROLLO)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(8f) // 80%
                    .background(Color.LightGray),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(50) { index ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.White)
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ítem $index")
                    }
                }
            }

            // Barra abajo (BASE SIN DESARROLLO)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 10%
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text("Barra Inferior", color = Color.White)
            }
        }

        if (showMenu) {
            ModalBottomSheet(
                onDismissRequest = { showMenu = false },
                sheetState = sheetState
            ) {
                UiMenu(
                    onClose = { showMenu = false }
                )
            }
        }
    }
}