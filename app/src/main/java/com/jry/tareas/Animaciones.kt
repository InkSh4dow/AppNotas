package com.jry.tareas

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
