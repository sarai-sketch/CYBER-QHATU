package com.cyberqhatu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.ui.screens.auth.PantallaBienvenida
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.ui.PantallaMiPerfil
import com.cyberqhatu.app.ui.screens.tienda.DetalleProductoScreen
import com.cyberqhatu.app.ui.screens.tienda.MarketplaceAppEntry
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.ui.EscanerIdentidadScreen
import com.cyberqhatu.app.verificacion.ui.InstruccionesVerificacionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberQhatuTheme {
                MainNavigationApp()
            }
        }
    }
}

@Composable
fun MainNavigationApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        val db = QhatuDatabase.getInstance(context)
        VerificacionRepositoryImpl(
            verificacionCacheDao = db.verificacionCacheDao(),
            verificacionDao = db.verificacionDao(),
            apiService = com.cyberqhatu.app.data.api.RetrofitClient.verificacionApiService
        )
    }

    val tokenManager = remember(context) { com.cyberqhatu.app.data.local.TokenManager(context) }
    val tokenFlow = remember(tokenManager) { tokenManager.getToken() }
    val token by tokenFlow.collectAsState(initial = null)
    
    val estadoLocalFlow = remember(repository, token) {
        repository.obtenerEstadoVerificacionLocal(token ?: "USR-101")
    }
    val estadoLocal by estadoLocalFlow.collectAsState(initial = null)
    val estaVerificado = estadoLocal?.esExitoso == true

    var selectedTab by remember { mutableIntStateOf(1) } // Empieza en PantallaBienvenida
    var modoVerificacionStep by remember { mutableIntStateOf(0) }

    if (modoVerificacionStep == 1) {
        InstruccionesVerificacionScreen(
            onComenzarClick = { modoVerificacionStep = 2 },
            onVolverClick = { modoVerificacionStep = 0 }
        )
    } else if (modoVerificacionStep == 2) {
        EscanerIdentidadScreen(
            onVerificacionExitosa = { _: String ->
                modoVerificacionStep = 0
            },
            onVolverClick = { modoVerificacionStep = 0 }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Marketplace"
                            )
                        },
                        label = { Text("Market") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio"
                            )
                        },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Detalle"
                            )
                        },
                        label = { Text("Detalle") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Reportar"
                            )
                        },
                        label = { Text("Reportar") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Publicar"
                            )
                        },
                        label = { Text("Publicar") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 5,
                        onClick = { selectedTab = 5 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Inventario"
                            )
                        },
                        label = { Text("Inventario") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 6,
                        onClick = { selectedTab = 6 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil"
                            )
                        },
                        label = { Text("Perfil") }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> MarketplaceAppEntry()
                    1 -> PantallaBienvenida(onContinuarClicked = { selectedTab = 0 })
                    2 -> DetalleProductoScreen()
                    3 -> ReportarTiendaScreen()
                    4 -> PublicarProductoScreen(modifier = Modifier.fillMaxSize())
                    5 -> InventarioVentasScreen(modifier = Modifier.fillMaxSize())
                    6 -> PantallaMiPerfil(
                        nombreUsuario = "Juan Pérez",
                        correoUsuario = "juan.perez@cyberqhatu.bo",
                        telefonoUsuario = "+591 70000000",
                        estadoVerificacion = estaVerificado,
                        tipoRol = "Vendedor",
                        onEditarPerfilClick = {},
                        onCerrarSesionClick = { selectedTab = 1 },
                        onVerificarIdentidadClick = { modoVerificacionStep = 1 }
                    )
                }
            }
        }
    }
}
