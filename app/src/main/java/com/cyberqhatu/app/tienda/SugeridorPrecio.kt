package com.cyberqhatu.app.tienda

class SugeridorPrecio {
    fun calcularSugerencia(producto: Producto): Double {
        return producto.precio
    }

    fun validarPrecio(producto: Producto, precio: Double): Boolean {
        return precio > 0
    }
}
