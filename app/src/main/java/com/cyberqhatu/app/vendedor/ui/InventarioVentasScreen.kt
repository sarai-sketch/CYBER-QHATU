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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.data.local.database.QhatuDatabase
import com.cyberqhatu.app.feature.tienda.data.repository.ProductoRepository
import com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudProductoRepository
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.ui.theme.QhatuCoral
import com.cyberqhatu.app.ui.theme.QhatuLime
import com.cyberqhatu.app.ui.theme.QhatuLimeDark
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.ui.theme.QhatuPurpleLight
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioVentasScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember(context) {
        ProductoRepository(QhatuDatabase.getInstance(context).productoDao())
    }
    val sqliteCloudRepository = remember { SqliteCloudProductoRepository() }

    val productosRoomFlow = remember(repository) {
        repository.obtenerTodosLosProductos()
    }
    val productosRoom by productosRoomFlow.collectAsState(initial = emptyList())

    var state by remember { mutableStateOf(ResumenVentasState()) }

    val productosInventario = if (productosRoom.isNotEmpty()) {
        productosRoom.map { p ->
            ProductoInventario(
                id = p.idProducto,
                titulo = p.nombre,
                categoria = p.idCategoria,
                precio = p.precio,
                stock = p.stock,
                ventasRealizadas = 0,
                estado = if (p.stock > 0) EstadoInventario.ACTIVO else EstadoInventario.AGOTADO
            )
        }
    } else {
        state.listaProductos
    }

    var productoAEditar by remember { mutableStateOf<ProductoInventario?>(null) }
    var productoAVender by remember { mutableStateOf<ProductoInventario?>(null) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )

    val productosFiltrados = productosInventario.filter { prod ->
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
            title = { Text("Editar Producto", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(prod.titulo, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

                    OutlinedTextField(
                        value = nuevoStockText,
                        onValueChange = { nuevoStockText = it },
                        label = { Text("Stock Disponible") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoPrecioText,
                        onValueChange = { nuevoPrecioText = it },
                        label = { Text("Precio (Bs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Estado:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(EstadoInventario.ACTIVO, EstadoInventario.PAUSADO, EstadoInventario.AGOTADO).forEach { est ->
                            FilterChip(
                                selected = nuevoEstado == est,
                                onClick = { nuevoEstado = est },
                                label = { Text(est.tituloMostrar) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
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
                        val estadoFinal = if (stockInt <= 0) EstadoProducto.OCULTO else EstadoProducto.ACTIVO

                        coroutineScope.launch {
                            repository.actualizarProducto(
                                idProducto = prod.id,
                                nuevoStock = stockInt,
                                nuevoPrecio = precioDouble,
                                nuevoEstado = estadoFinal
                            )
                            sqliteCloudRepository.actualizarStockYPrecioRemoto(
                                idProducto = prod.id,
                                nuevoStock = stockInt,
                                nuevoPrecio = precioDouble,
                                nuevoEstado = estadoFinal
                            )
                        }
                        productoAEditar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
            title = { Text("Registrar Venta Rápida", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Producto: ${prod.titulo}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Stock disponible: ${prod.stock} unidades", color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = { cantidadText = it },
                        label = { Text("Cantidad a vender") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Método de Pago:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("QR / Transferencia", "Efectivo", "Tarjeta").forEach { metodo ->
                            FilterChip(
                                selected = metodoPago == metodo,
                                onClick = { metodoPago = metodo },
                                label = { Text(metodo) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
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
                        if (cantInt > 0 && cantInt <= prod.stock) {
                            coroutineScope.launch {
                                repository.registrarVentaRapida(
                                    idProducto = prod.id,
                                    cantidad = cantInt,
                                    stockActual = prod.stock,
                                    precioUnitario = prod.precio
                                )
                                val nuevoStock = (prod.stock - cantInt).coerceAtLeast(0)
                                val nuevoEstado = if (nuevoStock == 0) EstadoProducto.OCULTO else EstadoProducto.ACTIVO
                                sqliteCloudRepository.actualizarStockYPrecioRemoto(
                                    idProducto = prod.id,
                                    nuevoStock = nuevoStock,
                                    nuevoPrecio = prod.precio,
                                    nuevoEstado = nuevoEstado
                                )
                                state = state.copy(
                                    totalIngresos = state.totalIngresos + (prod.precio * cantInt),
                                    totalVentasMes = state.totalVentasMes + 1
                                )
                            }
                        }
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
        topBar = {
            TopAppBar(
                title = { Text(text = "Inventario y Ventas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = state.textoBusqueda,
                    onValueChange = { state = state.copy(textoBusqueda = it) },
                    placeholder = { Text("Buscar producto...") },
                    colors = textFieldColors,
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
                            onClick = { state = state.copy(filtroSeleccionado = filtro) },
                            label = { Text(filtro.tituloMostrar, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (productosFiltrados.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            containerColor = MaterialTheme.colorScheme.surface
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
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
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Stock ${producto.stock}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    Text("️ Editar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
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
