package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.cyberqhatu.app.tienda.Categoria
import com.cyberqhatu.app.tienda.FavoritoProducto
import com.cyberqhatu.app.tienda.FavoritoTienda
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.ui.theme.*

/**
 * Pantalla de Inicio del Marketplace con diseño 100% unificado y armonizado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceHomeScreen(
    isLoggedIn: Boolean = false,
    isTiendaCreada: Boolean = false,
    avisosTienda: Int = 0,
    onNavigateToStoreProfile: (storeId: String) -> Unit = {},
    onNavigateToProductDetail: (productId: String) -> Unit = {},
    onNavigateToPublicar: () -> Unit = {},
    onNavigateToInventario: () -> Unit = {},
    onNavigateToTiendaHub: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    CyberQhatuTheme {
        var searchQuery by remember { mutableStateOf("") }
        var selectedCategoryName by remember { mutableStateOf("Todos") }

        val filteredProducts = DomainMockData.productos.filter { product ->
            val matchesSearch = product.nombre.contains(searchQuery, ignoreCase = true) ||
                    product.descripcion.contains(searchQuery, ignoreCase = true)
            val category = DomainMockData.categorias.firstOrNull { it.idCategoria == product.idCategoria }
            val matchesCategory = selectedCategoryName == "Todos" || category?.nombre == selectedCategoryName
            matchesSearch && matchesCategory
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Cyber-Qhatu Marketplace",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Ubicación",
                                    modifier = Modifier.size(14.sp.value.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Comercio Local Verificado",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        if (isTiendaCreada) {
                            IconButton(onClick = onNavigateToTiendaHub) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = "Mi Tienda",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (isLoggedIn) {
                            IconButton(onClick = onNavigateToFavoritos) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favoritos",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = onNavigateToPerfil) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Mi Perfil",
                                tint = MaterialTheme.colorScheme.primary
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Buscador
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar productos o puestos locales...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )
                }

                // Categorías
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Categorías",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val allCategories = listOf(Categoria("c0", "Todos", "")) + DomainMockData.categorias
                            items(allCategories) { categoria ->
                                val isSelected = selectedCategoryName == categoria.nombre
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategoryName = categoria.nombre },
                                    label = { Text(categoria.nombre) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Banner Promocional de Confianza
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verificado",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Garantía Cyber-Qhatu",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Comercios locales verificados con identidad biométrica.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Sección Tiendas Destacadas
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Puestos y Tiendas Verificadas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(DomainMockData.tiendas) { tienda ->
                                Card(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clickable { onNavigateToStoreProfile(tienda.idTienda) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Storefront,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = tienda.nombre,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f)
                                            )
                                            var esFavTienda by remember {
                                                mutableStateOf(DomainMockData.favoritosTiendas.any { it.idTienda == tienda.idTienda })
                                            }
                                            IconButton(
                                                onClick = {
                                                    if (!isLoggedIn) {
                                                        onNavigateToPerfil()
                                                    } else {
                                                        esFavTienda = !esFavTienda
                                                        if (esFavTienda) {
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
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (esFavTienda && isLoggedIn) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Favorito",
                                                    tint = if (esFavTienda && isLoggedIn) Color.Red else Color.Gray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Calificación",
                                                tint = Color(0xFFFFC107),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${DomainMockData.reputaciones[tienda.idTienda] ?: 4.8} (${DomainMockData.transacciones[tienda.idTienda] ?: 50})",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Sección Productos
                item {
                    Text(
                        text = "Productos Disponibles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(filteredProducts) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToProductDetail(producto.idProducto) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = producto.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = producto.descripcion,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Bs. ${producto.precio}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            var esFavProd by remember {
                                mutableStateOf(DomainMockData.favoritosProductos.any { it.idProducto == producto.idProducto })
                            }
                            IconButton(
                                onClick = {
                                    if (!isLoggedIn) {
                                        onNavigateToPerfil()
                                    } else {
                                        esFavProd = !esFavProd
                                        if (esFavProd) {
                                            if (DomainMockData.favoritosProductos.none { it.idProducto == producto.idProducto }) {
                                                DomainMockData.favoritosProductos.add(
                                                    FavoritoProducto(
                                                        idFavorito = System.currentTimeMillis().toString(),
                                                        idUsuario = "USR-101",
                                                        idProducto = producto.idProducto
                                                    )
                                                )
                                            }
                                        } else {
                                            DomainMockData.favoritosProductos.removeAll { it.idProducto == producto.idProducto }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (esFavProd && isLoggedIn) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorito Producto",
                                    tint = if (esFavProd && isLoggedIn) Color.Red else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
