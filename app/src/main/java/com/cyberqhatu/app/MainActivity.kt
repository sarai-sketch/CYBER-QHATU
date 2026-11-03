package com.cyberqhatu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import com.cyberqhatu.app.confianza.FuenteResena
import com.cyberqhatu.app.confianza.ModeracionStore
import com.cyberqhatu.app.confianza.Resena
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.ui.PantallaMiPerfil
import com.cyberqhatu.app.ui.screens.comprador.EscribirResenaScreen
import com.cyberqhatu.app.ui.screens.comprador.FavoritosScreen
import com.cyberqhatu.app.ui.screens.comprador.LoginCompradorScreen
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.ui.screens.moderador.VistaModeradorScreen
import com.cyberqhatu.app.ui.screens.tienda.DetalleProductoScreen
import com.cyberqhatu.app.ui.screens.tienda.DomainMockData
import com.cyberqhatu.app.ui.screens.tienda.MarketplaceHomeScreen
import com.cyberqhatu.app.ui.screens.tienda.StoreProfileScreen
import com.cyberqhatu.app.ui.screens.vendedor.CrearTiendaScreen
import com.cyberqhatu.app.core.session.SesionActual
import com.cyberqhatu.app.feature.tienda.data.repository.FavoritosSync
import com.cyberqhatu.app.ui.screens.vendedor.GestionColaboradoresScreen
import com.cyberqhatu.app.ui.screens.vendedor.GestionTiendaHubScreen
import com.cyberqhatu.app.ui.screens.vendedor.PasosEmpezarVenderScreen
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen
import com.cyberqhatu.app.verificacion.data.repository.VerificacionRepositoryImpl
import com.cyberqhatu.app.verificacion.ui.EscanerIdentidadScreen
import com.cyberqhatu.app.verificacion.ui.InstruccionesVerificacionScreen
import com.cyberqhatu.app.verificacion.ui.OpcionesVendedorPostVerificacionScreen
import com.cyberqhatu.app.auth.data.repository.AuthRepositoryImpl
import com.cyberqhatu.app.feature.perfil.data.repository.SqliteCloudUsuarioRepository
import com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudProductoRepository
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.ui.screens.perfil.PerfilViewModel
import com.cyberqhatu.app.ui.screens.perfil.UpdateState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    data class EscribirResena(val targetName: String) : RootScreen()
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

    // Instanciamos el repositorio y obtenemos el estado inicial de la sesión
    val authRepository = remember { AuthRepositoryImpl() }
    val tiendaRepository = remember { com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudTiendaRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    val isUserLoggedIn = authRepository.isUserLoggedInAndVerified()
    val initialEmail = authRepository.getCurrentUserEmail() ?: "juan.perez@cyberqhatu.bo"
    val initialName = authRepository.getCurrentUserName() ?: "Usuario"
    val emailLowerForInit = initialEmail.lowercase()

    var isLoggedIn by remember { mutableStateOf(isUserLoggedIn) }
    var currentUserEmail by remember { mutableStateOf(initialEmail) }
    var currentUserName by remember { mutableStateOf(initialName) }
    var currentUserPhone by remember { mutableStateOf("+591 70000000") }
    
    val estadoLocalFlow = remember(repository, currentUserEmail) {
        repository.obtenerEstadoVerificacionLocal(currentUserEmail)
    }
    val estadoLocal by estadoLocalFlow.collectAsState(initial = null)
    val estaVerificado = estadoLocal?.esExitoso == true
    
    var isTiendaCreada by remember { mutableStateOf(false) }
    var miTiendaId by remember { mutableStateOf<String?>(null) }
    var esPropietarioTienda by remember { mutableStateOf(true) }
    var refrescarTienda by remember { mutableIntStateOf(0) }
    var esModerador by remember { mutableStateOf(false) }

    // Sesion actual + favoritos desde la nube
    LaunchedEffect(currentUserEmail, isLoggedIn) {
        if (isLoggedIn) {
            SesionActual.idUsuario = currentUserEmail
            FavoritosSync.cargarDesdeNube(currentUserEmail)
        } else {
            SesionActual.idUsuario = null
            FavoritosSync.limpiar()
        }
    }

    // Al iniciar sesión o entrar a la app, buscar usuario y tienda remota
    LaunchedEffect(currentUserEmail, isLoggedIn, refrescarTienda) {
        if (isLoggedIn) {
            val emailLower = currentUserEmail.lowercase()
            if (emailLower == "moderador@cyberqhatu.bo") {
                esModerador = true
            } else {
                withContext(Dispatchers.IO) {
                    try {
                        val usuarioRemoto = SqliteCloudUsuarioRepository()
                            .obtenerUsuarioPorId(currentUserEmail)
                            .firstOrNull()
                        if (usuarioRemoto != null) {
                            currentUserPhone = usuarioRemoto.telefonoWhatsapp
                            if (currentUserName.isBlank() || currentUserName == "Usuario") {
                                currentUserName = usuarioRemoto.nombre
                            }
                        }
                    } catch (_: Exception) {}

                    try {
                        val tiendaRemota = tiendaRepository.obtenerTiendaDeUsuario(currentUserEmail)
                        if (tiendaRemota != null) {
                            isTiendaCreada = true
                            miTiendaId = tiendaRemota.idTienda
                            esPropietarioTienda = tiendaRemota.idPropietario.equals(currentUserEmail, ignoreCase = true)
                        } else {
                            isTiendaCreada = false
                            miTiendaId = null
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }

    var currentScreen by remember { mutableStateOf<RootScreen>(RootScreen.Home) }
    var lastStoreId by remember { mutableStateOf("t1") }
    var modoVerificacionStep by remember { mutableIntStateOf(0) } // 0: Normal, 1: Pasos, 2: Instrucciones, 3: Escáner, 4: Crear Tienda

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    val perfilViewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.provideFactory(authRepository))
    val updateState by perfilViewModel.updateState.collectAsState()

    LaunchedEffect(updateState) {
        if (updateState is UpdateState.Success) {
            currentUserName = (updateState as UpdateState.Success).newName
            showEditNameDialog = false
            perfilViewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showEditNameDialog = false
                    perfilViewModel.resetState()
                },
                title = { Text("Editar Perfil") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Nombre completo") },
                            singleLine = true
                        )
                        if (updateState is UpdateState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                        } else if (updateState is UpdateState.Error) {
                            Text(
                                text = (updateState as UpdateState.Error).message, 
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { perfilViewModel.actualizarNombre(tempName) },
                        enabled = tempName.isNotBlank() && updateState !is UpdateState.Loading
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showEditNameDialog = false
                        perfilViewModel.resetState()
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        when {
            esModerador -> {
                VistaModeradorScreen(
                    onCerrarSesion = {
                        authRepository.logout()
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
                    idUsuario = currentUserEmail,
                    onVerificacionExitosa = { _ ->
                        modoVerificacionStep = 35 // Ir a Opciones Post-Verificación
                    },
                    onVolverClick = { modoVerificacionStep = 1 }
                )
            }
            modoVerificacionStep == 35 -> {
                OpcionesVendedorPostVerificacionScreen(
                    currentUserEmail = currentUserEmail,
                    onCrearTiendaClick = { modoVerificacionStep = 4 },
                    onTiendaVinculada = {
                        isTiendaCreada = true
                        refrescarTienda++
                        modoVerificacionStep = 0
                        currentScreen = RootScreen.TiendaHub
                    },
                    onBackClick = {
                        modoVerificacionStep = 0
                        currentScreen = RootScreen.Perfil
                    }
                )
            }
            modoVerificacionStep == 4 -> {
                CrearTiendaScreen(
                    idPropietario = authRepository.getCurrentUserEmail() ?: "u1",
                    onTiendaCreada = { nuevaTienda ->
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
                            onLoginSuccess = { email, phone ->
                                isLoggedIn = true
                                currentUserEmail = email.trim()
                                if (phone.isNotBlank()) currentUserPhone = phone
                                currentUserName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Usuario"
                                val emailLower = currentUserEmail.lowercase()
                                
                                if (emailLower == "moderador@cyberqhatu.bo") {
                                    esModerador = true
                                }
                                
                                currentScreen = RootScreen.Home
                            },
                            onBackClick = {
                                currentScreen = RootScreen.Home
                            }
                        )
                    }
                    is RootScreen.Perfil -> {
                        val nombreMostrado = currentUserName.ifBlank { "Usuario" }
                        val rolMostrado = if (isTiendaCreada) "Vendedor / Propietario de Tienda" else "Comprador"

                        PantallaMiPerfil(
                            nombreUsuario = nombreMostrado,
                            correoUsuario = currentUserEmail,
                            telefonoUsuario = currentUserPhone,
                            estadoVerificacion = estaVerificado,
                            tipoRol = rolMostrado,
                            onEditarPerfilClick = {
                                tempName = currentUserName
                                showEditNameDialog = true
                            },
                            onCerrarSesionClick = {
                                authRepository.logout()
                                isLoggedIn = false
                                isTiendaCreada = false
                                miTiendaId = null
                                esModerador = false
                                currentScreen = RootScreen.Login
                            },
                            onVerificarIdentidadClick = {
                                if (isTiendaCreada) {
                                    currentScreen = RootScreen.TiendaHub
                                } else if (estaVerificado) {
                                    modoVerificacionStep = 35
                                } else {
                                    modoVerificacionStep = 1
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
                        
                        var nombreDeLaTienda by remember { mutableStateOf("Mi tienda") }
                        LaunchedEffect(idTiendaHub) {
                            val tiendas = tiendaRepository.obtenerTiendaDeUsuario(currentUserEmail)
                            if (tiendas != null && tiendas.idTienda == idTiendaHub) {
                                nombreDeLaTienda = tiendas.nombre
                            }
                        }
                        
                        GestionTiendaHubScreen(
                            idTienda = idTiendaHub,
                            nombreTienda = nombreDeLaTienda,
                            onVerPerfilTienda = { currentScreen = RootScreen.StoreProfile(idTiendaHub) },
                            onAgregarProducto = { currentScreen = RootScreen.PublicarProducto },
                            onVerInventario = { currentScreen = RootScreen.Inventario },
                            onGestionarColaboradores = { currentScreen = RootScreen.Colaboradores },
                            puedeGestionarColaboradores = esPropietarioTienda,
                            onBackClick = { currentScreen = RootScreen.Home }
                        )
                    }
                    is RootScreen.PublicarProducto -> {
                        val idTiendaHub = miTiendaId ?: lastStoreId
                        PublicarProductoScreen(
                            idTienda = idTiendaHub,
                            modifier = Modifier.fillMaxSize(),
                            onBackClick = { currentScreen = RootScreen.TiendaHub }
                        )
                    }
                    is RootScreen.Inventario -> {
                        InventarioVentasScreen(
                            idTienda = miTiendaId ?: lastStoreId,
                            modifier = Modifier.fillMaxSize(),
                            onBackClick = { currentScreen = RootScreen.TiendaHub }
                        )
                    }
                    is RootScreen.Colaboradores -> {
                        GestionColaboradoresScreen(
                            storeId = miTiendaId ?: lastStoreId,
                            onBackClick = { currentScreen = RootScreen.TiendaHub },
                            onInvitacionAceptada = { refrescarTienda++ }
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
                            },
                            onEscribirResenaClick = { storeName ->
                                currentScreen = RootScreen.EscribirResena(storeName)
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
                    is RootScreen.EscribirResena -> {
                        EscribirResenaScreen(
                            nombreItem = screen.targetName,
                            onPublicarResena = { rating, comentario ->
                                DomainMockData.resenasList.add(
                                    0,
                                    Resena(
                                        idResena = System.currentTimeMillis().toString(),
                                        idTienda = lastStoreId,
                                        idUsuario = currentUserEmail,
                                        rating = rating,
                                        comentario = comentario,
                                        source = FuenteResena.DIRECT_SEARCH,
                                        isVerified = true
                                    )
                                )
                                currentScreen = RootScreen.StoreProfile(lastStoreId)
                            },
                            onBackClick = { currentScreen = RootScreen.StoreProfile(lastStoreId) }
                        )
                    }
                    is RootScreen.ProductDetail -> {
                        var productoDetail by remember { mutableStateOf<Producto?>(
                            DomainMockData.productos.firstOrNull { it.idProducto == screen.productId }
                        ) }
                        val prodRepo = remember { SqliteCloudProductoRepository() }
                        
                        LaunchedEffect(screen.productId) {
                            try {
                                val prodList = prodRepo.obtenerProductosRemotos().firstOrNull()
                                productoDetail = prodList?.firstOrNull { it.idProducto == screen.productId }
                                    ?: DomainMockData.productos.firstOrNull { it.idProducto == screen.productId }
                                    ?: Producto(
                                        idProducto = screen.productId,
                                        idTienda = "t1",
                                        idCategoria = "c1",
                                        nombre = "Producto Verificado",
                                        descripcion = "Artículo de calidad en feria.",
                                        precio = 100.0,
                                        stock = 5
                                    )
                            } catch (_: Exception) {
                                productoDetail = Producto(
                                    idProducto = screen.productId,
                                    idTienda = "t1",
                                    idCategoria = "c1",
                                    nombre = "Producto Verificado",
                                    descripcion = "Artículo de calidad en feria.",
                                    precio = 100.0,
                                    stock = 5
                                )
                            }
                        }

                        if (productoDetail != null) {
                            DetalleProductoScreen(
                                producto = productoDetail!!,
                                isLoggedIn = isLoggedIn,
                                onRequireLogin = { currentScreen = RootScreen.Login },
                                onEscribirResenaClick = { productName ->
                                    currentScreen = RootScreen.EscribirResena(productName)
                                },
                                onBackClick = {
                                    currentScreen = RootScreen.Home
                                }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
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
    onRequireLogin: () -> Unit = {},
    onEscribirResenaClick: (String) -> Unit = {}
) {
    StoreProfileScreen(
        storeId = storeId,
        isLoggedIn = isLoggedIn,
        onBackClick = onBackClick,
        onNavigateToProductDetail = onNavigateToProductDetail,
        onNavigateToReportar = onNavigateToReportar,
        onRequireLogin = onRequireLogin,
        onEscribirResenaClick = onEscribirResenaClick
    )
}
