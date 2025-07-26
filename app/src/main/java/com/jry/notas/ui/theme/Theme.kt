package com.jry.notas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val ModoOscuro = darkColorScheme(
    background = OscuroPrincipal,
    secondary = OscuroSecundario,
    onBackground = OscuroTextoPrincipal,
    onSecondary = OscuroTextoSecundario,
    primary = OscuroInteraccion,
    onPrimary = OscuroIconos
)

private val ModoClaro = lightColorScheme(
    background = ClaroPrincipal,
    secondary = ClaroSecundario,
    onBackground = ClaroTextoPrincipal,
    onSecondary = ClaroTextoSecundario,
    primary = ClaroInteraccion,
    onPrimary = ClaroIconos
)

@Composable
fun TareasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && true -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> ModoOscuro
        else -> ModoClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Fuente,
        content = content
    )
}