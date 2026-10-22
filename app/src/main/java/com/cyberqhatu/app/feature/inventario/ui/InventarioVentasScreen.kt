package com.cyberqhatu.app.feature.inventario.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyberqhatu.app.core.ui.theme.*
import java.util.Locale

@Composable
fun InventarioVentasScreen(
    viewModel: InventarioVentasViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    InventarioVentasContent(
        state = state,
        onTextoBusquedaChanged = viewModel::onTextoBusquedaChanged,
        onFiltroSeleccionadoChanged = viewModel::onFiltroSeleccionadoChanged,
        onGuardarCambiosProducto = viewModel::guardarCambiosProducto,
        onConfirmarVentaRapida = viewModel::confirmarVentaRapida,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioVentasContent(
    state: ResumenVentasState,
    onTextoBusquedaChanged: (String) -> Unit,
    onFiltroSeleccionadoChanged: (EstadoInventario) -> Unit,
    onGuardarCambiosProducto: (String, Int, Double, EstadoInventario) -> Unit,
    onConfirmarVentaRapida: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var productoAEditar by remember { mutableStateOf<ProductoInventario?>(null) }
    var productoAVender by remember { mutableStateOf<ProductoInventario?>(null) }

    val productosFiltrados = state.listaProductos.filter { prod ->
        val coincideBusqueda = prod.titulo.contains(state.textoBusqueda, ignoreCase = true) ||
                prod.categoria.contains(state.textoBusqueda, ignoreCase = true)
        val coincideFiltro = state.filtroSeleccionado == EstadoInventario.TODOS ||
                prod.estado == state.filtroSeleccionado
        coincideBusqueda && coincideFiltro
    }

    // Editar producto
    productoAEditar?.let { prod ->
        var nuevoStockText by remember { mutableStateOf(prod.stock.toString()) }
        var nuevoPrecioText by remember { mutableStateOf(prod.precio.toString()) }
        var nuevoEstado by remember { mutableStateOf(prod.estado) }

        AlertDialog(
            onDismissRequest = { productoAEditar = null },
            title = { Text("Editar Producto", fontWeight = FontWeight.Bold, color = QhatuPurple) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(prod.titulo, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = nuevoStockText,
                        onValueChange = { nuevoStockText = it },
                        label = { Text("Stock Disponible") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QhatuPurple),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoPrecioText,
                        onValueChange = { nuevoPrecioText = it },
                        label = { Text("Precio (Bs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QhatuPurple),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Estado:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(EstadoInventario.ACTIVO, EstadoInventario.PAUSADO, EstadoInventario.AGOTADO).forEach { est ->
                            FilterChip(
                                selected = nuevoEstado == est,
                                onClick = { nuevoEstado = est },
                                label = { Text(est.tituloMostrar) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = QhatuPurple,
                                    selectedLabelColor = Color.White
                                )
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
                        onGuardarCambiosProducto(prod.id, stockInt, precioDouble, nuevoEstado)
                        productoAEditar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QhatuPurple)
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

    // Registrar venta
    productoAVender?.let { prod ->
        var cantidadText by remember { mutableStateOf("1") }
        var metodoPago by remember { mutableStateOf("QR / Transferencia") }

        AlertDialog(
            onDismissRequest = { productoAVender = null },
            title = { Text("Registrar Venta Rápida", fontWeight = FontWeight.Bold, color = QhatuPurple) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Producto: ${prod.titulo}", fontWeight = FontWeight.SemiBold)
                    Text("Stock disponible: ${prod.stock} unidades")

                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = { cantidadText = it },
                        label = { Text("Cantidad a vender") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QhatuPurple),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Método de Pago:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("QR / Transferencia", "Efectivo", "Tarjeta").forEach { metodo ->
                            FilterChip(
                                selected = metodoPago == metodo,
                                onClick = { metodoPago = metodo },
                                label = { Text(metodo) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = QhatuPurple,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cantInt = cantidadText.toIntOrNull() ?: 1
                        onConfirmarVentaRapida(prod.id, cantInt)
                        productoAVender = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral)
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
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(QhatuPurple)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Panel de vendedor",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Moda Andina",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(QhatuLime)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✓ Verificada",
                                style = MaterialTheme.typography.labelMedium,
                                color = QhatuLimeDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.18f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Ingresos",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Bs ${String.format(Locale.US, "%.0f", state.totalIngresos)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.18f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Ventas",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${state.totalVentasMes}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Inventario de Productos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QhatuPurple
                )

                OutlinedTextField(
                    value = state.textoBusqueda,
                    onValueChange = onTextoBusquedaChanged,
                    placeholder = { Text("Buscar producto...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QhatuPurple,
                        focusedLabelColor = QhatuPurple
                    ),
                    shape = RoundedCornerShape(14.dp),
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
                            onClick = { onFiltroSeleccionadoChanged(filtro) },
                            label = { Text(filtro.tituloMostrar, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = QhatuPurple,
                                selectedLabelColor = Color.White,
                                containerColor = QhatuPurpleLight,
                                labelColor = QhatuPurple
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (productosFiltrados.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = QhatuPurpleLight
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay productos en este filtro.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = QhatuPurple,
                                fontWeight = FontWeight.Medium
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
            }
        }
    }
}

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
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(QhatuPurpleLight)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = ".", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = producto.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = QhatuPurple,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(QhatuPurpleLight)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Stock ${producto.stock}",
                                style = MaterialTheme.typography.labelSmall,
                                color = QhatuPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bs ${String.format(Locale.US, "%.0f", producto.precio)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${producto.ventasRealizadas} ventas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEditar,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("️ Editar", style = MaterialTheme.typography.labelMedium, color = QhatuPurple)
                }

                Button(
                    onClick = onVender,
                    enabled = producto.stock > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QhatuCoral
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Venta Rápida", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InventarioVentasPreview() {
    CyberQhatuTheme {
        InventarioVentasContent(
            state = ResumenVentasState(),
            onTextoBusquedaChanged = {},
            onFiltroSeleccionadoChanged = {},
            onGuardarCambiosProducto = { _, _, _, _ -> },
            onConfirmarVentaRapida = { _, _ -> }
        )
    }
}
