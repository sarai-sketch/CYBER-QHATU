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
    secondary = QhatuLime,
    onSecondary = QhatuLimeDark,
    secondaryContainer = QhatuLimeDark,
    onSecondaryContainer = QhatuLimeLight,
    tertiary = QhatuCoral,
    onTertiary = Color.White,
    background = Color(0xFF100B26),
    surface = Color(0xFF181236),
    surfaceVariant = Color(0xFF261D4C),
    onBackground = Color(0xFFEBE6FF),
    onSurface = Color(0xFFEBE6FF),
    onSurfaceVariant = Color(0xFFBDB8DC),
    outline = Color(0xFF3C3168),
    outlineVariant = Color(0xFF2B2050),
    error = QhatuCoral,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = QhatuPurple,
    onPrimary = Color.White,
    primaryContainer = QhatuPurple,
    onPrimaryContainer = Color.White,
    secondary = QhatuLime,
    onSecondary = QhatuLimeDark,
    secondaryContainer = QhatuLimeLight,
    onSecondaryContainer = QhatuLimeDark,
    tertiary = QhatuCoral,
    onTertiary = Color.White,
    background = QhatuBackground,
    surface = QhatuSurface,
    surfaceVariant = QhatuPurpleLight,
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
