package com.cyberqhatu.app.confianza
import java.util.Date

data class Resena(
    val idResena: String,
    val idTienda: String,
    val idUsuario: String,
    val idCodigoResena: String? = null,
    val rating: Int,
    val comentario: String,
    val source: FuenteResena,
    val isVerified: Boolean = false,
    val fechaCreacion: Date = Date()
) {
    fun publicarResena(): Boolean {
        // Lógica de dominio/validación para la publicación
        return true
    }
}