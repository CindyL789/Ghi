package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = TerracottaPrimaryContainer,
    onPrimaryContainer = TerracottaOnPrimaryContainer,
    secondary = SageSecondary,
    onSecondary = Color.White,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = SageOnSecondaryContainer,
    tertiary = HoneyTertiary,
    onTertiary = HoneyOnTertiaryContainer,
    tertiaryContainer = HoneyTertiaryContainer,
    onTertiaryContainer = HoneyOnTertiaryContainer,
    background = WarmBackground,
    onBackground = WarmOnSurface,
    surface = WarmSurface,
    onSurface = WarmOnSurface,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = WarmOnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5C2B1E),
    onPrimaryContainer = Color(0xFFF7ECE1),
    secondary = SageSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF284437),
    onSecondaryContainer = Color(0xFFE8F3EE),
    tertiary = HoneyTertiary,
    onTertiary = Color.Black,
    background = Color(0xFF1C1A19),
    onBackground = Color(0xFFEFE8E1),
    surface = Color(0xFF262322),
    onSurface = Color(0xFFEFE8E1),
    surfaceVariant = Color(0xFF383230),
    onSurfaceVariant = Color(0xFFD4C8BF)
)

@Composable
fun LoopCrochetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep default consistent brand colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LoopCrochetTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

