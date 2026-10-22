package com.cyberqhatu.app.feature.tienda.domain

import java.util.Date

class FavoritoTienda(
    val idFavorito: String,
    val idUsuario: String,
    val idTienda: String,
    val fechaAgregado: Date = Date()
)
