package com.cyberqhatu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = QhatuPurpleLight,
    onPrimary = QhatuPurple,
    primaryContainer = QhatuPurple,
    onPrimaryContainer = Color.White,
    secondary = QhatuCoral,
    onSecondary = Color.White,
    tertiary = QhatuLime,
    background = Color(0xFF131124),
    surface = Color(0xFF1E1B36),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = QhatuPurple,
    onPrimary = Color.White,
    primaryContainer = QhatuPurple,
    onPrimaryContainer = Color.White,
    secondary = QhatuCoral,
    onSecondary = Color.White,
    secondaryContainer = QhatuPurpleLight,
    onSecondaryContainer = QhatuPurple,
    tertiary = QhatuLime,
    onTertiary = QhatuLimeDark,
    background = QhatuBackground,
    surface = QhatuSurface,
    onBackground = QhatuTextPrimary,
    onSurface = QhatuTextPrimary,
    onSurfaceVariant = QhatuTextSecondary,
    outline = QhatuBorder
)

@Composable
fun CyberQhatuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
