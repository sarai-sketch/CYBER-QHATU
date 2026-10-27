package com.cyberqhatu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.cyberqhatu.app.confianza.ModeracionStore
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.ui.PantallaMiPerfil
import com.cyberqhatu.app.ui.screens.comprador.FavoritosScreen
import com.cyberqhatu.app.ui.screens.comprador.LoginCompradorScreen
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.ui.screens.moderador.VistaModeradorScreen
import com.cyberqhatu.app.ui.screens.tienda.DetalleProductoScreen
import com.cyberqhatu.app.ui.screens.tienda.DomainMockData
import com.cyberqhatu.app.ui.screens.tienda.MarketplaceHomeScreen
import com.cyberqhatu.app.ui.screens.tienda.StoreProfileScreen
import com.cyberqhatu.app.ui.screens.vendedor.CrearTiendaScreen
import com.cyberqhatu.app.ui.screens.vendedor.GestionColaboradoresScreen
import com.cyberqhatu.app.ui.screens.vendedor.GestionTiendaHubScreen
import com.cyberqhatu.app.ui.screens.vendedor.PasosEmpezarVenderScreen
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.ui.EscanerIdentidadScreen
import com.cyberqhatu.app.verificacion.ui.InstruccionesVerificacionScreen

sealed class RootScreen {
    object Home : RootScreen()
    object Login : RootScreen()
    object Perfil : RootScreen()
    object Favoritos : RootScreen()
    object TiendaHub : RootScreen()
    object PublicarProducto : RootScreen()
    object Inventario : RootScreen()
    object Colaboradores : RootScreen()
    data class StoreProfile(val storeId: String) : RootScreen()
    data class ProductDetail(val productId: String) : RootScreen()
    data class Reportar(val storeId: String, val storeName: String) : RootScreen()
}

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
        )
    }

    val estadoLocalFlow = remember(repository) {
        repository.obtenerEstadoVerificacionLocal("USR-101")
    }
    val estadoLocal by estadoLocalFlow.collectAsState(initial = null)
    val estaVerificado = estadoLocal?.esExitoso == true

    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUserEmail by remember { mutableStateOf("juan.perez@cyberqhatu.bo") }
    var isTiendaCreada by remember { mutableStateOf(false) }
    var miTiendaId by remember { mutableStateOf<String?>(null) }
    var esModerador by remember { mutableStateOf(false) }

    var currentScreen by remember { mutableStateOf<RootScreen>(RootScreen.Home) }
    var lastStoreId by remember { mutableStateOf("t1") }
    var modoVerificacionStep by remember { mutableIntStateOf(0) } // 0: Normal, 1: Pasos, 2: Instrucciones, 3: Escáner, 4: Crear Tienda

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            esModerador -> {
                VistaModeradorScreen(
                    onCerrarSesion = {
                        esModerador = false
                        isLoggedIn = false
                        isTiendaCreada = false
                        miTiendaId = null
                        currentScreen = RootScreen.Home
                    }
                )
            }
            modoVerificacionStep == 1 -> {
                PasosEmpezarVenderScreen(
                    onComenzarVerificacion = { modoVerificacionStep = 2 },
                    onBackClick = { modoVerificacionStep = 0 }
                )
            }
            modoVerificacionStep == 2 -> {
                InstruccionesVerificacionScreen(
                    onComenzarClick = { modoVerificacionStep = 3 },
                    onVolverClick = { modoVerificacionStep = 1 }
                )
            }
            modoVerificacionStep == 3 -> {
                EscanerIdentidadScreen(
                    onVerificacionExitosa = { _ ->
                        modoVerificacionStep = 4 // Ir a crear tienda (Paso 2)
                    },
                    onVolverClick = { modoVerificacionStep = 1 }
                )
            }
            modoVerificacionStep == 4 -> {
                CrearTiendaScreen(
                    onTiendaCreada = { nuevaTienda ->
                        DomainMockData.tiendas.add(0, nuevaTienda)
                        miTiendaId = nuevaTienda.idTienda
                        isTiendaCreada = true
                        modoVerificacionStep = 0
                        currentScreen = RootScreen.TiendaHub
                    },
                    onBackClick = { modoVerificacionStep = 0 }
                )
            }
            else -> {
                when (val screen = currentScreen) {
                    is RootScreen.Home -> {
                        MarketplaceHomeScreen(
                            isLoggedIn = isLoggedIn,
                            isTiendaCreada = isTiendaCreada,
                            avisosTienda = miTiendaId?.let { ModeracionStore.avisosDe(it) } ?: 0,
                            onNavigateToStoreProfile = { storeId ->
                                lastStoreId = storeId
                                currentScreen = RootScreen.StoreProfile(storeId)
                            },
                            onNavigateToProductDetail = { productId ->
                                currentScreen = RootScreen.ProductDetail(productId)
                            },
                            onNavigateToTiendaHub = {
                                currentScreen = RootScreen.TiendaHub
                            },
                            onNavigateToFavoritos = {
                                if (isLoggedIn) {
                                    currentScreen = RootScreen.Favoritos
                                } else {
                                    currentScreen = RootScreen.Login
                                }
                            },
                            onNavigateToPerfil = {
                                if (isLoggedIn) {
                                    currentScreen = RootScreen.Perfil
                                } else {
                                    currentScreen = RootScreen.Login
                                }
                            }
                        )
                    }
                    is RootScreen.Login -> {
                        LoginCompradorScreen(
                            onLoginSuccess = { email ->
                                isLoggedIn = true
                                currentUserEmail = email.trim()
                                val emailLower = currentUserEmail.lowercase()

                                when {
                                    emailLower == "moderador@cyberqhatu.bo" -> {
                                        esModerador = true
                                    }
                                    emailLower == "carlos@gmail.com" -> {
                                        isTiendaCreada = true
                                        miTiendaId = "t1"
                                    }
                                    emailLower == "rosa@gmail.com" -> {
                                        isTiendaCreada = true
                                        miTiendaId = "t2"
                                    }
                                    else -> {
                                        // Correo de usuario o comprador normal
                                    }
                                }

                                currentScreen = RootScreen.Home
                            },
                            onBackClick = {
                                currentScreen = RootScreen.Home
                            }
                        )
                    }
                    is RootScreen.Perfil -> {
                        val nombreMostrado = when (currentUserEmail.lowercase()) {
                            "carlos@gmail.com" -> "Don Carlos Mamani"
                            "rosa@gmail.com" -> "Doña Rosa Quispe"
                            else -> "Juan Pérez"
                        }
                        val rolMostrado = if (isTiendaCreada) "Vendedor / Propietario de Tienda" else "Comprador"

                        PantallaMiPerfil(
                            nombreUsuario = nombreMostrado,
                            correoUsuario = currentUserEmail,
                            telefonoUsuario = "+591 70000000",
                            estadoVerificacion = estaVerificado,
                            tipoRol = rolMostrado,
                            onEditarPerfilClick = {},
                            onCerrarSesionClick = {
                                isLoggedIn = false
                                isTiendaCreada = false
                                miTiendaId = null
                                esModerador = false
                                currentScreen = RootScreen.Home
                            },
                            onVerificarIdentidadClick = {
                                if (isTiendaCreada) {
                                    currentScreen = RootScreen.TiendaHub
                                } else {
                                    modoVerificacionStep = 1 // Flujo completo: Pasos -> Verificación biométrica -> Crear Tienda
                                }
                            },
                            onBackClick = { currentScreen = RootScreen.Home }
                        )
                    }
                    is RootScreen.Favoritos -> {
                        FavoritosScreen(
                            isLogged = isLoggedIn,
                            onLoginClick = { currentScreen = RootScreen.Login },
                            onBackClick = { currentScreen = RootScreen.Home }
                        )
                    }
                    is RootScreen.TiendaHub -> {
                        val idTiendaHub = miTiendaId ?: lastStoreId
                        GestionTiendaHubScreen(
                            idTienda = idTiendaHub,
                            nombreTienda = DomainMockData.tiendas
                                .firstOrNull { it.idTienda == idTiendaHub }?.nombre ?: "Mi tienda",
                            onVerPerfilTienda = { currentScreen = RootScreen.StoreProfile(idTiendaHub) },
                            onAgregarProducto = { currentScreen = RootScreen.PublicarProducto },
                            onVerInventario = { currentScreen = RootScreen.Inventario },
                            onGestionarColaboradores = { currentScreen = RootScreen.Colaboradores },
                            onBackClick = { currentScreen = RootScreen.Home }
                        )
                    }
                    is RootScreen.PublicarProducto -> {
                        PublicarProductoScreen(
                            modifier = Modifier.fillMaxSize(),
                            onBackClick = { currentScreen = RootScreen.TiendaHub }
                        )
                    }
                    is RootScreen.Inventario -> {
                        InventarioVentasScreen(
                            modifier = Modifier.fillMaxSize(),
                            onBackClick = { currentScreen = RootScreen.TiendaHub }
                        )
                    }
                    is RootScreen.Colaboradores -> {
                        GestionColaboradoresScreen(
                            onBackClick = { currentScreen = RootScreen.TiendaHub }
                        )
                    }
                    is RootScreen.StoreProfile -> {
                        StoreProfileStoreProfile(
                            storeId = screen.storeId,
                            isLoggedIn = isLoggedIn,
                            onBackClick = {
                                if (screen.storeId == miTiendaId) {
                                    currentScreen = RootScreen.TiendaHub
                                } else {
                                    currentScreen = RootScreen.Home
                                }
                            },
                            onNavigateToProductDetail = { productId ->
                                currentScreen = RootScreen.ProductDetail(productId)
                            },
                            onNavigateToReportar = { storeName ->
                                if (isLoggedIn) {
                                    currentScreen = RootScreen.Reportar(screen.storeId, storeName)
                                } else {
                                    currentScreen = RootScreen.Login
                                }
                            },
                            onRequireLogin = {
                                currentScreen = RootScreen.Login
                            }
                        )
                    }
                    is RootScreen.Reportar -> {
                        ReportarTiendaScreen(
                            nombreTienda = screen.storeName,
                            onEnviar = { motivo: String, descripcion: String ->
                                ModeracionStore.enviarReporte(screen.storeId, screen.storeName, motivo, descripcion)
                                currentScreen = RootScreen.StoreProfile(screen.storeId)
                            },
                            onBackClick = {
                                currentScreen = RootScreen.StoreProfile(screen.storeId)
                            }
                        )
                    }
                    is RootScreen.ProductDetail -> {
                        val producto = DomainMockData.productos.firstOrNull { it.idProducto == screen.productId }
                            ?: DomainMockData.productos.first()
                        DetalleProductoScreen(
                            producto = producto,
                            isLoggedIn = isLoggedIn,
                            onRequireLogin = { currentScreen = RootScreen.Login },
                            onBackClick = {
                                currentScreen = RootScreen.Home
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreProfileStoreProfile(
    storeId: String,
    isLoggedIn: Boolean = false,
    onBackClick: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToReportar: (String) -> Unit,
    onRequireLogin: () -> Unit = {}
) {
    StoreProfileScreen(
        storeId = storeId,
        isLoggedIn = isLoggedIn,
        onBackClick = onBackClick,
        onNavigateToProductDetail = onNavigateToProductDetail,
        onNavigateToReportar = onNavigateToReportar,
        onRequireLogin = onRequireLogin
    )
}
