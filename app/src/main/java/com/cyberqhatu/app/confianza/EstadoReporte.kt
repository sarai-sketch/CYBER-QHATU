package com.cyberqhatu.app.confianza

enum class EstadoReporte {
    PENDIENTE,
    ACEPTADO,   // el moderador aceptó el reporte: se pone un strike a la tienda
    RECHAZADO   // el moderador rechazó el reporte: sin sanción
}