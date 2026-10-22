package com.cyberqhatu.app.feature.confianza.domain

class Reporte(
    val idReporte: String,
    val idReportante: String,
    val idTiendaReportada: String,
    val motivo: String,
    val descripcion: String,
    var estado: EstadoReporte = EstadoReporte.PENDIENTE
) {
    fun enviar(): Boolean {
        return true
    }

    fun revisar() {
        this.estado = EstadoReporte.REVISADO
    }

    fun resolver() {
        this.estado = EstadoReporte.RESUELTO
    }
}
