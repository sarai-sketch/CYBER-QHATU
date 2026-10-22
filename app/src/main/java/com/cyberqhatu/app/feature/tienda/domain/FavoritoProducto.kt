package com.cyberqhatu.app.feature.tienda.domain

import java.util.Date

class FavoritoProducto(
    val idFavorito: String,
    val idUsuario: String,
    val idProducto: String,
    val fechaAgregado: Date = Date()
)
