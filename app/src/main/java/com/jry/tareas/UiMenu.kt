package com.jry.tareas

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay
import androidx.core.net.toUri

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MenuUI(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
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
                .padding(28.dp, 1.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie_menu.json"))
            val progress by animateLottieCompositionAsState(composition)
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Menú",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
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
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }

            BotonGithubAnimado(
                onClick = {
                    val url = "https://github.com/InkSh4dow"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun BotonGithubAnimado(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(750)
        expanded = true
    }

    val shape = if (expanded) RoundedCornerShape(50) else CircleShape

    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .height(48.dp)
            .shadow(4.dp, shape)
            .animateContentSize(animationSpec = tween(durationMillis = 500))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .defaultMinSize(minWidth = 48.dp)
                .padding(horizontal = if (expanded) 20.dp else 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "GitHub",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            AnimatedVisibility(
                visible = expanded,
                enter = androidx.compose.animation.fadeIn(animationSpec = tween(400)),
                exit = androidx.compose.animation.fadeOut(animationSpec = tween(200))
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GitHub",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
