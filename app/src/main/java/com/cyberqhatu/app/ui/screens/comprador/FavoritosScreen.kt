package com.cyberqhatu.app.ui.screens.comprador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudFavoritoRepository
import android.util.Log
import com.cyberqhatu.app.core.session.SesionActual
import com.cyberqhatu.app.feature.tienda.data.repository.FavProductoRow
import com.cyberqhatu.app.feature.tienda.data.repository.FavTiendaRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Pantalla dedicada a los Favoritos del Comprador (Productos y Tiendas marcadas con corazón).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    isLogged: Boolean = false,
    onNavigateToStoreProfile: (storeId: String) -> Unit = {},
    onNavigateToProductDetail: (productId: String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val favoritoRepository = remember { SqliteCloudFavoritoRepository() }

    var tiendas by remember { mutableStateOf<List<FavTiendaRow>>(emptyList()) }
    var prods by remember { mutableStateOf<List<FavProductoRow>>(emptyList()) }
    var cargando by remember { mutableStateOf(false) }
    var errorCarga by remember { mutableStateOf<String?>(null) }
    var recargar by remember { mutableStateOf(0) }

    LaunchedEffect(isLogged, recargar) {
        val idUsuario = SesionActual.idUsuario
        if (isLogged && idUsuario != null) {
            cargando = true
            errorCarga = null
            try {
                val (p, t) = withContext(Dispatchers.IO) {
                    favoritoRepository.obtenerProductos(idUsuario) to favoritoRepository.obtenerTiendas(idUsuario)
                }
                prods = p
                tiendas = t
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("FavoritosScreen", "Error cargando favoritos: ${e.message}", e)
                errorCarga = e.message ?: "No se pudieron cargar los favoritos"
            } finally {
                cargando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (!isLogged) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Text("Inicia sesión para ver tus favoritos guardados", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Button(onClick = onLoginClick) {
                            Text("Iniciar Sesión")
                        }
                    }
                }
            } else {
                if (cargando) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (errorCarga != null) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("No se pudieron cargar tus favoritos", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text(errorCarga.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(onClick = { recargar++ }) { Text("Reintentar") }
                        }
                    }
                } else if (prods.isEmpty() && tiendas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                            Text("No tienes favoritos guardados", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            Text("Toca el icono de corazón en productos o tiendas para agregarlos aquí.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (tiendas.isNotEmpty()) {
                            item {
                                Text("Tiendas Favoritas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            items(tiendas) { tienda ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToStoreProfile(tienda.idTienda) }
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tienda.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text(tienda.descripcion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }

                        if (prods.isNotEmpty()) {
                            item {
                                Text("Productos Favoritos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            items(prods) { producto ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToProductDetail(producto.idProducto) }
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Spacer(Modifier.height(2.dp))
                                            Text("Bs. ${producto.precio}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
