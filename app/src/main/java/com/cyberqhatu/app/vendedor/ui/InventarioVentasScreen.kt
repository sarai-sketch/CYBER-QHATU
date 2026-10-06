package com.cyberqhatu.app.vendedor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * Pantalla 8: Inventario y Ventas (D2) - Módulo 4 Vendedor.
 * Contiene Dashboard, Buscador, Filtros, Lista y Modales para Ajuste de Stock y Venta Rápida.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioVentasScreen(
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(ResumenVentasState()) }

    var productoAEditar by remember { mutableStateOf<ProductoInventario?>(null) }
    var productoAVender by remember { mutableStateOf<ProductoInventario?>(null) }

    val productosFiltrados = state.listaProductos.filter { prod ->
        val coincideBusqueda = prod.titulo.contains(state.textoBusqueda, ignoreCase = true) ||
                prod.categoria.contains(state.textoBusqueda, ignoreCase = true)
        val coincideFiltro = state.filtroSeleccionado == EstadoInventario.TODOS ||
                prod.estado == state.filtroSeleccionado
        coincideBusqueda && coincideFiltro
    }

    // --- DIÁLOGO 1: EDITAR STOCK Y PRECIO ---
    productoAEditar?.let { prod ->
        var nuevoStockText by remember { mutableStateOf(prod.stock.toString()) }
        var nuevoPrecioText by remember { mutableStateOf(prod.precio.toString()) }
        var nuevoEstado by remember { mutableStateOf(prod.estado) }

        AlertDialog(
            onDismissRequest = { productoAEditar = null },
            title = { Text("✏️ Editar Producto", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(prod.titulo, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = nuevoStockText,
                        onValueChange = { nuevoStockText = it },
                        label = { Text("Stock Disponible") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoPrecioText,
                        onValueChange = { nuevoPrecioText = it },
                        label = { Text("Precio (S/)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Estado de Publicación:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(EstadoInventario.ACTIVO, EstadoInventario.PAUSADO, EstadoInventario.AGOTADO).forEach { est ->
                            FilterChip(
                                selected = nuevoEstado == est,
                                onClick = { nuevoEstado = est },
                                label = { Text(est.tituloMostrar) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stockInt = nuevoStockText.toIntOrNull() ?: prod.stock
                        val precioDouble = nuevoPrecioText.toDoubleOrNull() ?: prod.precio
                        val estadoFinal = if (stockInt <= 0) EstadoInventario.AGOTADO else nuevoEstado

                        val listaActualizada = state.listaProductos.map { p ->
                            if (p.id == prod.id) {
                                p.copy(stock = stockInt, precio = precioDouble, estado = estadoFinal)
                            } else p
                        }

                        state = state.copy(
                            listaProductos = listaActualizada,
                            productosActivos = listaActualizada.count { it.estado == EstadoInventario.ACTIVO },
                            productosAgotados = listaActualizada.count { it.estado == EstadoInventario.AGOTADO }
                        )
                        productoAEditar = null
                    }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { productoAEditar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIÁLOGO 2: REGISTRAR VENTA FÍSICA RÁPIDA ---
    productoAVender?.let { prod ->
        var cantidadText by remember { mutableStateOf("1") }
        var metodoPago by remember { mutableStateOf("Yape / Plin") }

        AlertDialog(
            onDismissRequest = { productoAVender = null },
            title = { Text("🛒 Registrar Venta Rápida", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Producto: ${prod.titulo}", fontWeight = FontWeight.SemiBold)
                    Text("Stock actual: ${prod.stock} unidades")

                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = { cantidadText = it },
                        label = { Text("Cantidad a vender") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Método de Pago:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Yape / Plin", "Efectivo", "Tarjeta").forEach { metodo ->
                            FilterChip(
                                selected = metodoPago == metodo,
                                onClick = { metodoPago = metodo },
                                label = { Text(metodo) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cantInt = cantidadText.toIntOrNull() ?: 1
                        if (cantInt > 0 && cantInt <= prod.stock) {
                            val nuevoStock = prod.stock - cantInt
                            val ingresoVenta = prod.precio * cantInt

                            val listaActualizada = state.listaProductos.map { p ->
                                if (p.id == prod.id) {
                                    p.copy(
                                        stock = nuevoStock,
                                        ventasRealizadas = p.ventasRealizadas + cantInt,
                                        estado = if (nuevoStock == 0) EstadoInventario.AGOTADO else p.estado
                                    )
                                } else p
                            }

                            state = state.copy(
                                listaProductos = listaActualizada,
                                totalIngresos = state.totalIngresos + ingresoVenta,
                                totalVentasMes = state.totalVentasMes + 1,
                                productosActivos = listaActualizada.count { it.estado == EstadoInventario.ACTIVO },
                                productosAgotados = listaActualizada.count { it.estado == EstadoInventario.AGOTADO }
                            )
                        }
                        productoAVender = null
                    }
                ) {
                    Text("Confirmar Venta")
                }
            },
            dismissButton = {
                TextButton(onClick = { productoAVender = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventario y Ventas (D2)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Módulo 4: Panel de Gestión del Vendedor",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // --- SECCIÓN 1: DASHBOARD ---
            Text(
                text = "Resumen de Rendimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Ingresos Totales del Mes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "S/ ${String.format(Locale.US, "%.2f", state.totalIngresos)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📈 +18% respecto al mes anterior",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🛍️ Ventas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.totalVentasMes}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "pedidos este mes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📦 Stock Activo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.productosActivos}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${state.productosAgotados} agotados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- SECCIÓN 2: BUSCADOR Y FILTROS ---
            Text(
                text = "Mi Inventario de Productos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = state.textoBusqueda,
                onValueChange = { state = state.copy(textoBusqueda = it) },
                placeholder = { Text("🔎 Buscar por nombre o categoría...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(EstadoInventario.entries) { filtro ->
                    FilterChip(
                        selected = state.filtroSeleccionado == filtro,
                        onClick = { state = state.copy(filtroSeleccionado = filtro) },
                        label = { Text(filtro.tituloMostrar) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            // --- SECCIÓN 3: LISTA DE PRODUCTOS ---
            if (productosFiltrados.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron productos que coincidan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    productosFiltrados.forEach { prod ->
                        TarjetaProductoInventario(
                            producto = prod,
                            onEditar = { productoAEditar = prod },
                            onVender = { productoAVender = prod }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Tarjeta individual con botones de acción para editar e inventariar venta.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TarjetaProductoInventario(
    producto: ProductoInventario,
    onEditar: () -> Unit,
    onVender: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📦", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = producto.categoria,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        val (bgColor, textColor) = when (producto.estado) {
                            EstadoInventario.ACTIVO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                            EstadoInventario.AGOTADO -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                            EstadoInventario.PAUSADO -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
                            EstadoInventario.TODOS -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = producto.estado.tituloMostrar,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = producto.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "S/ ${String.format(Locale.US, "%.2f", producto.precio)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = "Stock: ${producto.stock} | Ventas: ${producto.ventasRealizadas}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Acción Rápida (Editar / Registrar Venta)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("✏️ Editar Stock", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onVender,
                    enabled = producto.stock > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🛒 Venta Rápida", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
