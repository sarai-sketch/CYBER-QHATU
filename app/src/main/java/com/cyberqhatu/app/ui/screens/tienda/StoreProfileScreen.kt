package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.tienda.FavoritoTienda
import com.cyberqhatu.app.ui.theme.*

/**
 * Pantalla de Perfil de Tienda con diseño 100% unificado al Detalle de Producto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreProfileScreen(
    storeId: String = "t1",
    isLoggedIn: Boolean = false,
    onBackClick: () -> Unit = {},
    onNavigateToProductDetail: (productId: String) -> Unit = {},
    onNavigateToReportar: (storeName: String) -> Unit = {},
    onRequireLogin: () -> Unit = {}
) {
    CyberQhatuTheme {
        val tienda = DomainMockData.tiendas.firstOrNull { it.idTienda == storeId }
            ?: DomainMockData.tiendas.first()
        val reputacion = DomainMockData.reputaciones[tienda.idTienda]
        val ventas = DomainMockData.transacciones[tienda.idTienda]
        val productosDeTienda = DomainMockData.productos.filter { it.idTienda == tienda.idTienda }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Perfil de Puesto", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = QhatuPurple
                            )
                        }
                    },
                    actions = {
                        var esFavorito by remember {
                            mutableStateOf(DomainMockData.favoritosTiendas.any { it.idTienda == tienda.idTienda })
                        }
                        IconButton(
                            onClick = {
                                if (!isLoggedIn) {
                                    onRequireLogin()
                                } else {
                                    esFavorito = !esFavorito
                                    if (esFavorito) {
                                        if (DomainMockData.favoritosTiendas.none { it.idTienda == tienda.idTienda }) {
                                            DomainMockData.favoritosTiendas.add(
                                                FavoritoTienda(
                                                    idFavorito = System.currentTimeMillis().toString(),
                                                    idUsuario = "USR-101",
                                                    idTienda = tienda.idTienda
                                                )
                                            )
                                        }
                                    } else {
                                        DomainMockData.favoritosTiendas.removeAll { it.idTienda == tienda.idTienda }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito Tienda",
                                tint = if (esFavorito) Color.Red else QhatuPurple
                            )
                        }
                        IconButton(onClick = { onNavigateToReportar(tienda.nombre) }) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Reportar Tienda",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = QhatuPurple,
                        navigationIconContentColor = QhatuPurple,
                        actionIconContentColor = QhatuPurple
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                // Banner Superior Encabezado (Idéntico al Detalle de Producto)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .background(QhatuPurple)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            text = "Comercio Local Verificado",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tienda.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta de información principal del Puesto
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(QhatuPurple.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = QhatuPurple,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = tienda.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = QhatuPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tienda.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(text = "$reputacion", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Text("Reputación", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "$ventas+", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                                    Text("Ventas exitosas", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Botón de Contacto WhatsApp
                    Button(
                        onClick = { /* Abrir enlace WhatsApp */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "WhatsApp",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Contactar por WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Productos de este Puesto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = QhatuPurple
                    )

                    if (productosDeTienda.isEmpty()) {
                        Text("No hay productos publicados en este puesto.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        productosDeTienda.forEach { producto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToProductDetail(producto.idProducto) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(QhatuPurple.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingBag,
                                            contentDescription = null,
                                            tint = QhatuPurple,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Bs. ${producto.precio}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = QhatuCoral)
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
