package com.cyberqhatu.app.feature.tienda.domain

class Categoria(
    val idCategoria: String,
    var nombre: String,
    var idCategoriaPadre: String? = null
) {
    fun obtenerSubcategorias(): List<Categoria> {
        return emptyList()
    }
}
