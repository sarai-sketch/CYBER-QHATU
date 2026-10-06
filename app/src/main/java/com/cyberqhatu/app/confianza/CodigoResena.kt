package com.cyberqhatu.app.confianza

import java.util.Date

class CodigoResena(
    val idCodigo: String,
    val idTienda: String,
    val codigo: String,
    var estado: EstadoCodigoResena = EstadoCodigoResena.UNUSED,
    val fechaCreacion: Date = Date()
)
