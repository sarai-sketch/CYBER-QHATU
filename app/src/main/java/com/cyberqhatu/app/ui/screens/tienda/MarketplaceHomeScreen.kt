package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme

/**
 * Pantalla 3: Inicio del Marketplace (B1)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceHomeScreen(
    onNavigateToStoreProfile: (storeId: String) -> Unit = {}
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
                                fontSize = 18.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📍", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Feria 16 de Julio, El Alto",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF8F9FA)
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF4F6F8)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Buscador
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("🔍 Buscar productos o puestos en la feria...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                // Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🇧🇴 Catálogo Oficial de la Feria 16 de Julio",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Precios transparentes y contacto directo vía WhatsApp con los puestos de la feria.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Categorías
                item {
                    Text(
                        text = "Categorías de Productos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212529)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip(
                                name = "Todos",
                                isSelected = selectedCategoryName == "Todos",
                                onClick = { selectedCategoryName = "Todos" }
                            )
                        }
                        items(DomainMockData.categorias) { cat ->
                            CategoryChip(
                                name = cat.nombre,
                                isSelected = selectedCategoryName == cat.nombre,
                                onClick = { selectedCategoryName = cat.nombre }
                            )
                        }
                    }
                }

                // Tiendas Destacadas
                item {
                    Text(
                        text = "Puestos y Tiendas en la Feria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212529)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(DomainMockData.tiendas) { tienda ->
                            DomainStoreCard(
                                tienda = tienda,
                                onClick = { onNavigateToStoreProfile(tienda.idTienda) }
                            )
                        }
                    }
                }

                // Catálogo de Productos
                item {
                    Text(
                        text = "Productos en Venta (${filteredProducts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF212529)
                    )
                }

                items(filteredProducts) { producto ->
                    val tienda = DomainMockData.tiendas.firstOrNull { it.idTienda == producto.idTienda }
                    DomainProductCard(
                        producto = producto,
                        tienda = tienda,
                        onStoreClick = {
                            if (tienda != null) {
                                onNavigateToStoreProfile(tienda.idTienda)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF495057)

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun DomainStoreCard(
    tienda: Tienda,
    onClick: () -> Unit
) {
    val reputacion = DomainMockData.reputaciones[tienda.idTienda]
    val ventas = DomainMockData.transacciones[tienda.idTienda]

    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🏪", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tienda.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(
                text = tienda.direccionGoogleMaps ?: "Feria 16 de Julio",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⭐ ${reputacion ?: 5.0} (${ventas ?: 10} ventas)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF57C00)
            )
        }
    }
}

@Composable
fun DomainProductCard(
    producto: Producto,
    tienda: Tienda?,
    onStoreClick: () -> Unit
) {
    val categoria = DomainMockData.categorias.firstOrNull { it.idCategoria == producto.idCategoria }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoria?.nombre ?: "General",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Bs. ${producto.precio}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = producto.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212529)
            )
            if (producto.marca != null || producto.modelo != null) {
                Text(
                    text = "Marca: ${producto.marca ?: "-"} | Modelo: ${producto.modelo ?: "-"}",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = producto.descripcion,
                fontSize = 13.sp,
                color = Color(0xFF6C757D),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onStoreClick)
                ) {
                    Text(text = "🏬", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tienda?.nombre ?: "Puesto de la Feria",
                        fontSize = 12.sp,
                        color = Color(0xFF495057),
                        fontWeight = FontWeight.Medium
                    )
                }
                if (producto.etiquetaPrecioTransparente) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Precio Transparente",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
