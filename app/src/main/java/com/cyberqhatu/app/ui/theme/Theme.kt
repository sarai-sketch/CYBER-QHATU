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
    primaryContainer = QhatuPurpleContainer,
    onPrimaryContainer = Color.White,
    secondary = QhatuCoral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF382B60),
    onSecondaryContainer = Color(0xFFEBE6FF),
    tertiary = QhatuLime,
    onTertiary = QhatuLimeDark,
    background = Color(0xFF121020),
    surface = Color(0xFF1A172E),
    surfaceVariant = Color(0xFF282347),
    onBackground = Color(0xFFF4F3FA),
    onSurface = Color(0xFFF4F3FA),
    onSurfaceVariant = Color(0xFFB0ACD0),
    outline = Color(0xFF3E3860),
    outlineVariant = Color(0xFF2D274A),
    error = Color(0xFFFF6B5B),
    onError = Color.White
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
    surfaceVariant = Color(0xFFEBE6FF),
    onBackground = QhatuTextPrimary,
    onSurface = QhatuTextPrimary,
    onSurfaceVariant = QhatuTextSecondary,
    outline = QhatuBorder,
    outlineVariant = Color(0xFFE2DFF5),
    error = QhatuCoral,
    onError = Color.White
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
