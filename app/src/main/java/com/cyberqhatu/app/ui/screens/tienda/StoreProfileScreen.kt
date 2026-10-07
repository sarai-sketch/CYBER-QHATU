package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme

/**
 * Pantalla 4: Perfil de Tienda (B3)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreProfileScreen(
    storeId: String = "t1",
    onBackClick: () -> Unit = {}
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
                    title = { Text(text = "Perfil de Puesto / Tienda", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    navigationIcon = {
                        TextButton(onClick = onBackClick) {
                            Text(text = "← Volver", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                // Cabecera de la Tienda
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏪", fontSize = 36.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = tienda.nombre,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212529)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📍", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tienda.direccionGoogleMaps ?: "Feria 16 de Julio, El Alto",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6C757D),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⭐", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${reputacion ?: 5.0} (${ventas ?: 10} transacciones exitosas - Nivel ALTO)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF495057)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Horario: ${tienda.horarioApertura ?: "06:00"} - ${tienda.horarioCierre ?: "18:00"}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Tipo: ${tienda.tipo}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1976D2),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { /* Abrir enlace de WhatsApp */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "💬 Contactar por WhatsApp", color = Color.White)
                            }
                        }
                    }
                }

                // Sección de Productos del Puesto
                item {
                    Text(
                        text = "Catálogo del Puesto (${productosDeTienda.size} productos)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )
                }

                items(productosDeTienda) { producto ->
                    DomainStoreProductCard(producto = producto)
                }
            }
        }
    }
}

@Composable
fun DomainStoreProductCard(producto: Producto) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = producto.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212529),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Bs. ${producto.precio}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (producto.marca != null || producto.modelo != null) {
                Text(
                    text = "Marca: ${producto.marca ?: "-"} | Modelo: ${producto.modelo ?: "-"}",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )
            }
            Text(
                text = producto.descripcion,
                fontSize = 13.sp,
                color = Color(0xFF6C757D)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Text(
                        text = categoria?.nombre ?: "Categoría",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = "Stock disponible: ${producto.stock} unidades",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
