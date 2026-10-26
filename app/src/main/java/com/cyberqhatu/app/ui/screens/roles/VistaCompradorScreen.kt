package com.cyberqhatu.app.ui.screens.roles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.screens.tienda.MarketplaceHomeScreen
import com.cyberqhatu.app.ui.theme.QhatuPurple

/**
 * Vista del Comprador:
 * - Exploración de tiendas y productos sin requerir login obligatorio.
 * - Gestión de favoritos (si el usuario está logueado).
 * - Visualización de catálogo y perfil.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaCompradorScreen(
    onNavigateToStoreProfile: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isLogged by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == 0) "Cyber-Qhatu Marketplace" else if (selectedTab == 1) "Mis Favoritos" else "Mi Cuenta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = QhatuPurple
                    )
                },
                actions = {
                    if (!isLogged) {
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Iniciar Sesión", color = QhatuPurple, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(onClick = { isLogged = false }) {
                            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        if (isLogged) {
                            selectedTab = 1
                        } else {
                            onNavigateToLogin()
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> MarketplaceHomeScreen(
                    onNavigateToStoreProfile = onNavigateToStoreProfile,
                    onNavigateToProductDetail = onNavigateToProductDetail,
                    onNavigateToPublicar = {},
                    onNavigateToInventario = {},
                    onNavigateToPerfil = { selectedTab = 2 }
                )
                1 -> {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Tus Tiendas y Productos Favoritos", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = QhatuPurple)
                            Spacer(Modifier.height(12.dp))
                            if (!isLogged) {
                                Text("Inicia sesión para guardar tus favoritos.")
                            } else {
                                Text("No tienes favoritos guardados todavía.")
                            }
                        }
                    }
                }
                2 -> {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Perfil de Comprador", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = QhatuPurple)
                            Spacer(Modifier.height(12.dp))
                            Text(if (isLogged) "Estado: Conectado (Comprador)" else "Estado: Invitado / Sin sesión")
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { isLogged = !isLogged }) {
                                Text(if (isLogged) "Cambiar a modo Invitado" else "Simular Login de Comprador")
                            }
                        }
                    }
                }
            }
        }
    }
}
