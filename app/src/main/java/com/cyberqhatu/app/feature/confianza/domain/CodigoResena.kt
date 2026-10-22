package com.cyberqhatu.app.feature.confianza.domain

import java.util.Date

class CodigoResena(
    val idCodigo: String,
    val idTienda: String,
    val codigo: String,
    var estado: EstadoCodigoResena = EstadoCodigoResena.UNUSED,
    val fechaCreacion: Date = Date()
)
