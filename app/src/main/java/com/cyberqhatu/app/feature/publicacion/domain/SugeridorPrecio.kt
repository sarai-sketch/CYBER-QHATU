package com.cyberqhatu.app.feature.publicacion.domain

import com.cyberqhatu.app.feature.tienda.domain.Producto

class SugeridorPrecio {
    fun calcularSugerencia(producto: Producto): Double {
        return producto.precio
    }

    fun validarPrecio(producto: Producto, precio: Double): Boolean {
        return precio > 0
    }
}
