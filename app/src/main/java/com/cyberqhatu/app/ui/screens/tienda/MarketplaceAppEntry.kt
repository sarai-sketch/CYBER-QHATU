package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.cyberqhatu.app.ui.PantallaMiPerfil
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen

sealed class AppScreen {
    object Home : AppScreen()
    data class StoreProfile(val storeId: String) : AppScreen()
    data class ProductDetail(val productId: String) : AppScreen()
    object Publicar : AppScreen()
    object Inventario : AppScreen()
    data class Reportar(val storeName: String) : AppScreen()
    object Perfil : AppScreen()
}

/**
 * Navegador jerárquico principal de Cyber-Qhatu.
 * - Sin BottomBar en ninguna pantalla.
 * - Navegación fluida por TopAppBar y acciones contextuales.
 */
@Composable
fun MarketplaceAppEntry(
    estaVerificado: Boolean = false,
    onVerificarIdentidadClick: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var lastStoreId by remember { mutableStateOf("t1") }

    when (val screen = currentScreen) {
        is AppScreen.Home -> {
            MarketplaceHomeScreen(
                onNavigateToStoreProfile = { storeId ->
                    lastStoreId = storeId
                    currentScreen = AppScreen.StoreProfile(storeId)
                },
                onNavigateToProductDetail = { productId ->
                    currentScreen = AppScreen.ProductDetail(productId)
                },
                onNavigateToPublicar = {
                    currentScreen = AppScreen.Publicar
                },
                onNavigateToInventario = {
                    currentScreen = AppScreen.Inventario
                },
                onNavigateToPerfil = {
                    currentScreen = AppScreen.Perfil
                }
            )
        }
        is AppScreen.StoreProfile -> {
            lastStoreId = screen.storeId
            StoreProfileScreen(
                storeId = screen.storeId,
                onBackClick = {
                    currentScreen = AppScreen.Home
                },
                onNavigateToProductDetail = { productId ->
                    currentScreen = AppScreen.ProductDetail(productId)
                },
                onNavigateToReportar = { storeName ->
                    currentScreen = AppScreen.Reportar(storeName)
                }
            )
        }
        is AppScreen.ProductDetail -> {
            val producto = DomainMockData.productos.firstOrNull { it.idProducto == screen.productId }
                ?: DomainMockData.productos.first()
            DetalleProductoScreen(
                producto = producto,
                onBackClick = {
                    currentScreen = AppScreen.Home
                }
            )
        }
        is AppScreen.Publicar -> {
            PublicarProductoScreen(
                modifier = Modifier.fillMaxSize(),
                onBackClick = {
                    currentScreen = AppScreen.Home
                }
            )
        }
        is AppScreen.Inventario -> {
            InventarioVentasScreen(
                modifier = Modifier.fillMaxSize(),
                onBackClick = {
                    currentScreen = AppScreen.Home
                }
            )
        }
        is AppScreen.Reportar -> {
            ReportarTiendaScreen(
                nombreTienda = screen.storeName,
                onBackClick = {
                    currentScreen = AppScreen.StoreProfile(lastStoreId)
                }
            )
        }
        is AppScreen.Perfil -> {
            PantallaMiPerfil(
                nombreUsuario = "Juan Pérez",
                correoUsuario = "juan.perez@cyberqhatu.bo",
                telefonoUsuario = "+591 70000000",
                estadoVerificacion = estaVerificado,
                tipoRol = "Vendedor",
                onEditarPerfilClick = {},
                onCerrarSesionClick = {
                    currentScreen = AppScreen.Home
                },
                onBackClick = {
                    currentScreen = AppScreen.Home
                },
                onVerificarIdentidadClick = onVerificarIdentidadClick
            )
        }
    }
}
