package com.cyberqhatu.app.confianza

enum class AportadoPor { REPORTANTE, REPORTADO }

class ReporteEvidencia(
    val idEvidencia: String,
    val idReporte: String,
    var url: String,
    var aportadoPor: AportadoPor
)
