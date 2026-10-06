package com.cyberqhatu.app.tienda

import java.util.Date

class FavoritoTienda(
    val idFavorito: String,
    val idUsuario: String,
    val idTienda: String,
    val fechaAgregado: Date = Date()
)
