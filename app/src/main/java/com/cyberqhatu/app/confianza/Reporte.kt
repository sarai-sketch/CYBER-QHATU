package com.cyberqhatu.app.confianza

data class Reporte(
    val idReporte: String,
    val idReportante: String,
    val idTiendaReportada: String,
    val motivo: String,
    val descripcion: String,
    var estado: EstadoReporte = EstadoReporte.PENDIENTE
) {
    fun enviar(): Boolean {
        // Lógica de dominio para enviar el reporte
        return true
    }

    fun revisar() {
        this.estado = EstadoReporte.REVISADO
    }

    fun resolver() {
        this.estado = EstadoReporte.RESUELTO
    }
}