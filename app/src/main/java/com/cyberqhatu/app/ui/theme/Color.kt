package com.cyberqhatu.app.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de Colores de la Marca Cyber Qhatu
val QhatuPrimary = Color(0xFF27C5F5)        // Color 1: #27C5F5
val QhatuPrimaryContainer = Color(0xFF0F98C2) // Variante oscura
val QhatuPrimaryLight = Color(0xFFD4F3FC)     // Variante clara

val QhatuSecondary = Color(0xFFF5275E)      // Color 2: #F5275E
val QhatuSecondaryPressed = Color(0xFFC71746)

val QhatuTertiary = Color(0xFFF5BE27)       // Color 3: #F5BE27
val QhatuTertiaryDark = Color(0xFFC79515)

val QhatuBackground = Color(0xFFF2F8FA)
val QhatuSurface = Color(0xFFFFFFFF)
val QhatuTextPrimary = Color(0xFF0B2129)
val QhatuTextSecondary = Color(0xFF55717C)
val QhatuBorder = Color(0xFFD4E6ED)

// Compatibilidad (Aliases para no romper los archivos existentes temporalmente)
val QhatuPurple = QhatuPrimary
val QhatuPurpleContainer = QhatuPrimaryContainer
val QhatuPurpleLight = QhatuPrimaryLight
val QhatuCoral = QhatuSecondary
val QhatuCoralPressed = QhatuSecondaryPressed
val QhatuLime = QhatuTertiary
val QhatuLimeDark = QhatuTertiaryDark

// Compatibilidad con esquema de temas predeterminado
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Paleta oficial
val CqIndigo = QhatuPrimary      // Confianza — encabezados, botones primarios
val CqIndigoDark = QhatuPrimaryContainer
val CqLima = QhatuTertiary        // Verificado — insignias, estados activos
val CqCoral = QhatuSecondary       // Accion — reportes, alertas
val CqLavanda = QhatuPrimaryLight     // Fondo de pantallas y tarjetas
val CqTextSub = QhatuTextSecondary
