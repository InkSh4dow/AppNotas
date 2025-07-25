package com.jry.notas

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

/**
 Este es un Composable que muestra una animación Lottie desde un archivo de assets
 La animación se reproduce una vez, espera 3 segundos y luego se reinicia
 */

@Composable
fun LottieAnimacion(
    modifier: Modifier = Modifier,
    assetName: String,
    size: Dp
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))
    var isPlaying by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        // Reinicia la animación desde el principio cada vez que isPlaying se vuelve true
        restartOnPlay = true
    )
    LaunchedEffect(progress) {
        if (progress == 1f) {
            isPlaying = false
            delay(3000)
            isPlaying = true
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(size)
    )
}
