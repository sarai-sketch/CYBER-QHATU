package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.runtime.*

/**
 * Navegador interno para el flujo de Marketplace (B1 -> B3)
 */
@Composable
fun MarketplaceAppEntry() {
    var currentScreen by remember { mutableStateOf<MarketplaceScreen>(MarketplaceScreen.Home) }

    when (val screen = currentScreen) {
        is MarketplaceScreen.Home -> {
            MarketplaceHomeScreen(
                onNavigateToStoreProfile = { storeId ->
                    currentScreen = MarketplaceScreen.StoreProfile(storeId)
                }
            )
        }
        is MarketplaceScreen.StoreProfile -> {
            StoreProfileScreen(
                storeId = screen.storeId,
                onBackClick = {
                    currentScreen = MarketplaceScreen.Home
                }
            )
        }
    }
}

sealed class MarketplaceScreen {
    object Home : MarketplaceScreen()
    data class StoreProfile(val storeId: String) : MarketplaceScreen()
}
