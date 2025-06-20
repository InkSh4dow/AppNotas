package com.jry.tareas

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@Composable
fun MenuUI(
    onClose: () -> Unit,
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
                    .size(2.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            BotonGithubAnimado(
                onClick = {
                    val url = "https://www.ejemplo.com"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun darkButtonColors(
    isEqualsButton: Boolean = false,
    isOperation: Boolean = false
): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = when {
        isEqualsButton -> Color(0xFFFFA500)
        isOperation -> Color(0xFF03A9F4)
        else -> Color(0xFF424242)
    },
    contentColor = Color.White
)

@Composable
fun lightButtonColors(
    isEqualsButton: Boolean = false,
    isOperation: Boolean = false
): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = when {
        isEqualsButton -> Color(0xFFFFA500)
        isOperation -> Color(0xFF03A9F4)
        else -> Color.White
    },
    contentColor = Color.Black
)

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

    Surface(
        onClick = onClick,
        shape = if (expanded) RoundedCornerShape(50) else CircleShape,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp,
        modifier = modifier
            .height(48.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 500))
            .clip(if (expanded) RoundedCornerShape(50) else CircleShape)
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
