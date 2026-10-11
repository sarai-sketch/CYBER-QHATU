package com.cyberqhatu.app.ui.screens.tienda

import com.cyberqhatu.app.tienda.Categoria
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.EstadoTienda
import com.cyberqhatu.app.tienda.FavoritoProducto
import com.cyberqhatu.app.tienda.FavoritoTienda
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.tienda.TipoTienda
import com.cyberqhatu.app.usuarios.Vendedor
import java.util.Date

/**
 * Datos simulados utilizando las clases reales de dominio del proyecto
 * (tienda, usuarios, confianza, etc.) para la presentación del Módulo 2.
 */
object DomainMockData {
    val categorias = listOf(
        Categoria("c1", "Ropa Usada / Americana"),
        Categoria("c2", "Electrónica y Celulares"),
        Categoria("c3", "Artesanías y Textiles"),
        Categoria("c4", "Abarrotes y Alimentos"),
        Categoria("c5", "Ferretería y Herramientas")
    )

    val vendedores = listOf(
        Vendedor(
            idUsuario = "u1",
            nombre = "Don Carlos Mamani",
            correo = "carlos@gmail.com",
            telefonoWhatsapp = "71234567",
            ci = "1234567 LP",
            estadoVerificacion = true
        ),
        Vendedor(
            idUsuario = "u2",
            nombre = "Doña Rosa Quispe",
            correo = "rosa@gmail.com",
            telefonoWhatsapp = "76543210",
            ci = "7654321 LP",
            estadoVerificacion = false
        )
    )

    val tiendas = listOf(
        Tienda(
            idTienda = "t1",
            idPropietario = "u1",
            nombre = "Puesto Don Carlos - Ropa Americana",
            descripcion = "Venta de ropa americana clasificada de primera.",
            logo = null,
            tipo = TipoTienda.FISICA,
            direccionGoogleMaps = "Sector Altiplano, Calle 4, Puesto #12 - Feria 16 de Julio",
            horarioApertura = "06:00",
            horarioCierre = "18:00",
            linkWhatsapp = "https://wa.me/59171234567",
            estado = EstadoTienda.ACTIVA
        ),
        Tienda(
            idTienda = "t2",
            idPropietario = "u2",
            nombre = "TecnoFeria 16 - Celulares y Accesorios",
            descripcion = "Venta de celulares nuevos y semi nuevos con garantía.",
            logo = null,
            tipo = TipoTienda.VIRTUAL,
            direccionGoogleMaps = "Sector Melchor Pérez, Puesto #45 - Feria 16 de Julio",
            horarioApertura = "08:00",
            horarioCierre = "19:00",
            linkWhatsapp = "https://wa.me/59176543210",
            estado = EstadoTienda.ACTIVA
        )
    )

    val productos = listOf(
        Producto(
            idProducto = "p1",
            idTienda = "t1",
            idCategoria = "c1",
            marca = "Columbia",
            modelo = "Titanium",
            nombre = "Chamarra Térmica de Cuero Importada",
            descripcion = "Chamarra impermeable para el frío alteño, excelente estado, talla L.",
            precio = 150.0,
            precioCompra = 90.0,
            stock = 5,
            stockMinimo = 1,
            estado = EstadoProducto.ACTIVO,
            etiquetaPrecioTransparente = true
        ),
        Producto(
            idProducto = "p2",
            idTienda = "t2",
            idCategoria = "c2",
            marca = "Samsung",
            modelo = "Galaxy A52",
            nombre = "Smartphone Samsung Galaxy A52 128GB",
            descripcion = "Celular liberado para Entel/Viva/Tigo, incluye cargador rápido.",
            precio = 850.0,
            precioCompra = 700.0,
            stock = 3,
            stockMinimo = 1,
            estado = EstadoProducto.ACTIVO,
            etiquetaPrecioTransparente = true
        ),
        Producto(
            idProducto = "p3",
            idTienda = "t1",
            idCategoria = "c3",
            marca = "Artesanal",
            nombre = "Aguayo Tradicional Alpaca Legítima",
            descripcion = "Tejido a mano por artesanas de El Alto, colores vivos y resistentes.",
            precio = 95.0,
            precioCompra = 60.0,
            stock = 10,
            stockMinimo = 2,
            estado = EstadoProducto.ACTIVO,
            etiquetaPrecioTransparente = true
        )
    )

    val reputaciones = mapOf(
        "t1" to 4.9,
        "t2" to 4.7
    )
    val transacciones = mapOf(
        "t1" to 156,
        "t2" to 89
    )

    val favoritosProductos = mutableListOf<FavoritoProducto>()
    val favoritosTiendas = mutableListOf<FavoritoTienda>()
}
