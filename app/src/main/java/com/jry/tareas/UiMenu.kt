package com.jry.tareas

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MenuUI(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
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
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Modo oscuro")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = modoOscuro,
                    onCheckedChange = onModoOscuroChange
                )
            }

            Button(
                onClick = {
                    val url = "https://github.com/InkSh4dow"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "GitHub Icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("GitHub")
            }
        }
    }
}
