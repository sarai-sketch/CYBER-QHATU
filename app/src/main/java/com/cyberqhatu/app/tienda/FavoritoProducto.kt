package com.cyberqhatu.app.tienda

import java.util.Date

class FavoritoProducto(
    val idFavorito: String,
    val idUsuario: String,
    val idProducto: String,
    val fechaAgregado: Date = Date()
)
