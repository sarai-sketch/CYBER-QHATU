package com.cyberqhatu.app.confianza

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

enum class EstadoApelacion { NINGUNA, PENDIENTE, ACEPTADA, RECHAZADA }

data class ReporteTienda(
    val id: String,
    val idTienda: String,
    val nombreTienda: String,
    val motivo: String,
    val descripcion: String,
    val estado: EstadoReporte = EstadoReporte.PENDIENTE,
    val estadoApelacion: EstadoApelacion = EstadoApelacion.NINGUNA,
    val textoApelacion: String = "",
    val vistoPorTienda: Boolean = false // false = la tienda tiene un aviso sin leer
)

/**
 * Estado en memoria de reportes, strikes y apelaciones (mock, sin base de datos).
 * Lo comparten la vista del moderador, el panel de la tienda y la pantalla de reportar.
 */
object ModeracionStore {
    const val LIMITE_STRIKES = 3

    val reportes = mutableStateListOf<ReporteTienda>()
    private val strikes = mutableStateMapOf<String, Int>() // idTienda -> strikes

    fun strikesDe(idTienda: String) = strikes[idTienda] ?: 0
    fun estaSuspendida(idTienda: String) = strikesDe(idTienda) >= LIMITE_STRIKES
    fun reportesDe(idTienda: String) = reportes.filter { it.idTienda == idTienda }
    fun avisosDe(idTienda: String) = reportes.count { it.idTienda == idTienda && !it.vistoPorTienda }

    fun enviarReporte(idTienda: String, nombreTienda: String, motivo: String, descripcion: String) {
        reportes.add(
            ReporteTienda(
                id = "R-%03d".format(reportes.size + 1),
                idTienda = idTienda,
                nombreTienda = nombreTienda,
                motivo = motivo,
                descripcion = descripcion
            )
        )
    }

    /** Moderador: aceptar = pone strike a la tienda, rechazar = sin sanción. */
    fun resolver(idReporte: String, aceptar: Boolean) {
        val i = reportes.indexOfFirst { it.id == idReporte }
        if (i < 0 || reportes[i].estado != EstadoReporte.PENDIENTE) return
        val r = reportes[i]
        reportes[i] = r.copy(
            estado = if (aceptar) EstadoReporte.ACEPTADO else EstadoReporte.RECHAZADO,
            vistoPorTienda = false
        )
        if (aceptar) strikes[r.idTienda] = strikesDe(r.idTienda) + 1
    }

    /** Tienda: solo puede apelar un reporte aceptado, y una sola vez. */
    fun apelar(idReporte: String, texto: String) {
        val i = reportes.indexOfFirst { it.id == idReporte }
        if (i < 0) return
        val r = reportes[i]
        if (r.estado != EstadoReporte.ACEPTADO || r.estadoApelacion != EstadoApelacion.NINGUNA) return
        reportes[i] = r.copy(estadoApelacion = EstadoApelacion.PENDIENTE, textoApelacion = texto)
    }

    /** Moderador: aceptar la apelación retira el strike. */
    fun resolverApelacion(idReporte: String, aceptar: Boolean) {
        val i = reportes.indexOfFirst { it.id == idReporte }
        if (i < 0 || reportes[i].estadoApelacion != EstadoApelacion.PENDIENTE) return
        val r = reportes[i]
        reportes[i] = r.copy(
            estadoApelacion = if (aceptar) EstadoApelacion.ACEPTADA else EstadoApelacion.RECHAZADA,
            vistoPorTienda = false
        )
        if (aceptar) strikes[r.idTienda] = maxOf(0, strikesDe(r.idTienda) - 1)
    }

    fun marcarVistos(idTienda: String) {
        reportes.forEachIndexed { i, r ->
            if (r.idTienda == idTienda && !r.vistoPorTienda) {
                reportes[i] = r.copy(vistoPorTienda = true)
            }
        }
    }
}