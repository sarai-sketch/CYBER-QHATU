package com.cyberqhatu.app.ui.screens.tienda

sealed class AppScreen {
    object Home : AppScreen()
    data class StoreProfile(val storeId: String) : AppScreen()
    data class ProductDetail(val productId: String) : AppScreen()
    object Publicar : AppScreen()
    object Inventario : AppScreen()
    data class Reportar(val storeName: String) : AppScreen()
    object Perfil : AppScreen()
}
