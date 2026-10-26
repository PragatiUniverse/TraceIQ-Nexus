package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TraceIQColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = NavyBackground,
    primaryContainer = NavySurfaceElevated,
    onPrimaryContainer = CyanAccent,
    secondary = VioletAccent,
    onSecondary = Color.White,
    secondaryContainer = NavySurface,
    onSecondaryContainer = Color.White,
    tertiary = AmberWarning,
    onTertiary = NavyBackground,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = RoseConflict,
    onError = Color.White
)

@Composable
fun TraceIQNexusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TraceIQColorScheme,
        typography = Typography,
        content = content
    )
}
