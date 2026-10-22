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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.ui.PantallaMiPerfil
import com.cyberqhatu.app.ui.screens.auth.PantallaBienvenida
import com.cyberqhatu.app.ui.screens.auth.PantallaConfirmacionOTP
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.ui.screens.tienda.DetalleProductoScreen
import com.cyberqhatu.app.ui.screens.tienda.MarketplaceAppEntry
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.ui.viewmodel.AuthState
import com.cyberqhatu.app.ui.viewmodel.AuthViewModel
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.ui.EscanerIdentidadScreen
import com.cyberqhatu.app.verificacion.ui.InstruccionesVerificacionScreen
import com.google.firebase.auth.FirebaseAuth

const val USE_AUTH_EMULATOR = false // TODO: false antes del merge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDebuggable = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable && USE_AUTH_EMULATOR) {
            try {
                FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            } catch (_: Exception) {}
        }

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
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: "USR-GUEST"

    val repository = remember(context) {
        val db = QhatuDatabase.getInstance(context)
        VerificacionRepositoryImpl(
            verificacionCacheDao = db.verificacionCacheDao(),
            verificacionDao = db.verificacionDao(),
            apiService = com.cyberqhatu.app.data.api.RetrofitClient.verificacionApiService
        )
    }

    val estadoLocalFlow = remember(repository, currentUid) {
        repository.obtenerEstadoVerificacionLocal(currentUid)
    }
    val estadoLocal by estadoLocalFlow.collectAsState(initial = null)
    val estaVerificado = estadoLocal?.esExitoso == true

    var selectedTab by remember { mutableIntStateOf(0) }
    var modoVerificacionStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        authViewModel.checkCurrentSession()
    }

    // Flujo de navegación según estado de autenticación
    when (val state = authState) {
        is AuthState.EmailSent, is AuthState.EmailNotVerified -> {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "tu correo"
            PantallaConfirmacionOTP(
                email = userEmail,
                authState = state,
                onYaVerifiqueClicked = {
                    authViewModel.reloadAndCheckVerified()
                },
                onReenviarClicked = {
                    authViewModel.resendVerificationEmail()
                },
                onVolverClicked = {
                    authViewModel.logout()
                }
            )
            return
        }
        is AuthState.Idle, is AuthState.Error -> {
            if (FirebaseAuth.getInstance().currentUser == null || FirebaseAuth.getInstance().currentUser?.isEmailVerified == false) {
                PantallaBienvenida(
                    viewModel = authViewModel,
                    onLoginExitoso = {
                        selectedTab = 0
                    }
                )
                return
            }
        }
        else -> {}
    }

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
                val currentUser = FirebaseAuth.getInstance().currentUser
                when (selectedTab) {
                    0 -> MarketplaceAppEntry()
                    1 -> MarketplaceAppEntry()
                    2 -> DetalleProductoScreen()
                    3 -> ReportarTiendaScreen()
                    4 -> PublicarProductoScreen(modifier = Modifier.fillMaxSize())
                    5 -> InventarioVentasScreen(modifier = Modifier.fillMaxSize())
                    6 -> PantallaMiPerfil(
                        nombreUsuario = currentUser?.displayName ?: "Usuario CyberQhatu",
                        correoUsuario = currentUser?.email ?: "correo@cyberqhatu.bo",
                        telefonoUsuario = "+591 70000000",
                        estadoVerificacion = estaVerificado,
                        tipoRol = "Vendedor",
                        onEditarPerfilClick = {},
                        onCerrarSesionClick = {
                            authViewModel.logout()
                        },
                        onVerificarIdentidadClick = { modoVerificacionStep = 1 }
                    )
                }
            }
        }
    }
}
