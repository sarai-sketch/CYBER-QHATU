package com.cyberqhatu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = QhatuPrimaryLight,
    onPrimary = QhatuPrimary,
    primaryContainer = QhatuPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = QhatuSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E6C85),
    onSecondaryContainer = QhatuPrimaryLight,
    tertiary = QhatuTertiary,
    onTertiary = QhatuTertiaryDark,
    background = Color(0xFF0F1A1F),
    surface = Color(0xFF152229),
    surfaceVariant = Color(0xFF23353D),
    onBackground = Color(0xFFE8F1F5),
    onSurface = Color(0xFFE8F1F5),
    onSurfaceVariant = Color(0xFFA1B3BC),
    outline = Color(0xFF38525E),
    outlineVariant = Color(0xFF263C47),
    error = QhatuSecondary,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = QhatuPrimary,
    onPrimary = Color.White,
    primaryContainer = QhatuPrimary,
    onPrimaryContainer = Color.White,
    secondary = QhatuSecondary,
    onSecondary = Color.White,
    secondaryContainer = QhatuPrimaryLight,
    onSecondaryContainer = QhatuPrimary,
    tertiary = QhatuTertiary,
    onTertiary = QhatuTertiaryDark,
    background = QhatuBackground,
    surface = QhatuSurface,
    surfaceVariant = QhatuPrimaryLight,
    onBackground = QhatuTextPrimary,
    onSurface = QhatuTextPrimary,
    onSurfaceVariant = QhatuTextSecondary,
    outline = QhatuBorder,
    outlineVariant = Color(0xFFD4E6ED),
    error = QhatuSecondary,
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
