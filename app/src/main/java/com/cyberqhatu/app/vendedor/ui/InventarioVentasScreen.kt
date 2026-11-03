package com.cyberqhatu.app.vendedor.ui
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CancellationException

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.ui.theme.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioVentasScreen(
    nombreTienda: String = "Mi tienda",
    idTienda: String = "t1",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sqliteCloudRepository = remember { SqliteCloudProductoRepository() }

    var productosRemotos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var state by remember { mutableStateOf(ResumenVentasState()) }
    var cargandoInventario by remember { mutableStateOf(true) }
    var errorInventario by remember { mutableStateOf<String?>(null) }
    var recargar by remember { mutableStateOf(0) }

    // Inventario = SOLO los productos de esta tienda, desde SQLite Cloud
    LaunchedEffect(idTienda, recargar) {
        cargandoInventario = true
        errorInventario = null
        try {
            productosRemotos = sqliteCloudRepository.obtenerProductosDeTienda(idTienda)
            try {
                val (ingresos, ventas) = sqliteCloudRepository.estadisticasMes(idTienda)
                state = state.copy(totalIngresos = ingresos, totalVentasMes = ventas)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Inventario", "No se pudieron cargar las estadisticas: ${e.message}", e)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("Inventario", "Error cargando inventario: ${e.message}", e)
            errorInventario = e.message ?: "No se pudo cargar el inventario"
        } finally {
            cargandoInventario = false
        }
    }

    val productosUnificados = productosRemotos

    val productosInventario = productosUnificados.map { p ->
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
                        val estadoProd = if (nuevoEstado == EstadoInventario.ACTIVO) EstadoProducto.ACTIVO else EstadoProducto.OCULTO

                        coroutineScope.launch {
                            try {
                                sqliteCloudRepository.actualizarProducto(prod.id, stockInt, precioDouble, estadoProd)
                                recargar++
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.e("Inventario", "Error guardando cambios: ${e.message}", e)
                                Toast.makeText(context, e.message ?: "No se pudieron guardar los cambios", Toast.LENGTH_LONG).show()
                            }
                        }
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

    // Registrar Venta Rápida
    productoAVender?.let { prod ->
        var cantidadText by remember { mutableStateOf("1") }
        var metodoPago by remember { mutableStateOf("EFECTIVO") }

        AlertDialog(
            onDismissRequest = { productoAVender = null },
            title = { Text("Registrar Venta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(prod.titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Precio unitario: Bs ${prod.precio}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Stock actual: ${prod.stock}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = { cantidadText = it },
                        label = { Text("Cantidad vendida") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Método de pago:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("EFECTIVO", "QR / TRANSFERENCIA").forEach { metodo ->
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
                                try {
                                    sqliteCloudRepository.registrarVenta(
                                        idTienda = idTienda,
                                        idProducto = prod.id,
                                        nombreProducto = prod.titulo,
                                        cantidad = cantInt,
                                        precioUnitario = prod.precio,
                                        canalVenta = if (metodoPago.startsWith("QR")) "QR" else "EFECTIVO"
                                    )
                                    recargar++
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    Log.e("Inventario", "Error registrando venta: ${e.message}", e)
                                    Toast.makeText(context, e.message ?: "No se pudo registrar la venta", Toast.LENGTH_LONG).show()
                                }
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
                title = { Text(text = "Inventario: $nombreTienda", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
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
                                text = nombreTienda,
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
                                text = "Verificada",
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

                if (cargandoInventario) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (errorInventario != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("No se pudo cargar el inventario", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(errorInventario.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = { recargar++ }) { Text("Reintentar") }
                    }
                } else if (productosFiltrados.isEmpty()) {
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
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
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
                            color = QhatuCoral
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onEditar,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Editar", style = MaterialTheme.typography.labelMedium)
                            }
                            Button(
                                onClick = onVender,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Vender", style = MaterialTheme.typography.labelMedium, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
