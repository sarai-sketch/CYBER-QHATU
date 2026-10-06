package com.cyberqhatu.app.confianza

import java.time.LocalDateTime

data class CodigoResena(
    val idCodigo: String,
    val idTienda: String,
    val codigo: String,
    var estado: EstadoCodigoResena = EstadoCodigoResena.UNUSED,
    val fechaCreacion: LocalDateTime = LocalDateTime.now()
)