package com.cyberqhatu.app.confianza

enum class NivelConfianza { BAJO, MEDIO, ALTO }

class Reputacion(
    val idReputacion: String,
    val idUsuario: String,
    var puntaje: Double = 0.0,
    var totalTransacciones: Int = 0,
    var nivelConfianza: NivelConfianza = NivelConfianza.BAJO
) {
    fun actualizar() { TODO() }
}
