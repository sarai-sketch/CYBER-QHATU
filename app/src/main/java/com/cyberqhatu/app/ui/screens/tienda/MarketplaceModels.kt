package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.runtime.mutableStateListOf
import com.cyberqhatu.app.confianza.Resena
import com.cyberqhatu.app.tienda.Categoria
import com.cyberqhatu.app.tienda.FavoritoProducto
import com.cyberqhatu.app.tienda.FavoritoTienda
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.usuarios.Vendedor

/**
 * Colecciones de memoria sin datos de prueba iniciales para la plataforma Cyber-Qhatu.
 */
object DomainMockData {
    val categorias = listOf(
        Categoria("c1", "Ropa Usada / Americana"),
        Categoria("c2", "Electrónica y Celulares"),
        Categoria("c3", "Artesanías y Textiles"),
        Categoria("c4", "Abarrotes y Alimentos"),
        Categoria("c5", "Ferretería y Herramientas")
    )

    val vendedores = mutableStateListOf<Vendedor>()

    val tiendas = mutableStateListOf<Tienda>()

    val productos = mutableStateListOf<Producto>()

    val reputaciones = mutableMapOf<String, Double>()
    val transacciones = mutableMapOf<String, Int>()

    val favoritosProductos = mutableListOf<FavoritoProducto>()
    val favoritosTiendas = mutableListOf<FavoritoTienda>()

    val resenasList = mutableStateListOf<Resena>()
}
