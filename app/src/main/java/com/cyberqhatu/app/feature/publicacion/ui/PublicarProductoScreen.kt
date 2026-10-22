package com.cyberqhatu.app.feature.publicacion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Composable
fun PublicarProductoScreen(
    viewModel: PublicarProductoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val formState by viewModel.uiState.collectAsStateWithLifecycle()

    PublicarProductoContent(
        formState = formState,
        onTituloChanged = viewModel::onTituloChanged,
        onCategoriaChanged = viewModel::onCategoriaChanged,
        onPrecioChanged = viewModel::onPrecioChanged,
        onStockChanged = viewModel::onStockChanged,
        onDescripcionChanged = viewModel::onDescripcionChanged,
        onCondicionChanged = viewModel::onCondicionChanged,
        onAgregarFoto = viewModel::agregarFoto,
        onEliminarFoto = viewModel::eliminarFoto,
        onPublicarProducto = viewModel::publicarProducto,
        onResetForm = viewModel::resetForm,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PublicarProductoContent(
    formState: PublicarProductoFormState,
    onTituloChanged: (String) -> Unit,
    onCategoriaChanged: (String) -> Unit,
    onPrecioChanged: (String) -> Unit,
    onStockChanged: (String) -> Unit,
    onDescripcionChanged: (String) -> Unit,
    onCondicionChanged: (CondicionProducto) -> Unit,
    onAgregarFoto: () -> Unit,
    onEliminarFoto: (Int) -> Unit,
    onPublicarProducto: () -> Unit,
    onResetForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val categoriasDisponibles = listOf(
        "Ropa y Calzado",
        "Electrónica y Tecnología",
        "Hogar y Artesanías",
        "Deportes y Entretenimiento",
        "Accesorios y Belleza",
        "Otros"
    )

    if (formState.esExitoso) {
        AlertDialog(
            onDismissRequest = onResetForm,
            title = {
                Text(
                    text = " ¡Producto Publicado!",
                    fontWeight = FontWeight.Bold,
                    color = QhatuPurple
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Tu producto ya está disponible en la tienda de Cyber Qhatu:")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Título: ${formState.titulo}", fontWeight = FontWeight.SemiBold)
                    Text("• Categoría: ${formState.categoria.ifBlank { "Sin categoría" }}")
                    Text("• Precio: Bs ${formState.precio}")
                    Text("• Stock: ${formState.stock} unidades")
                    Text("• Condición: ${formState.condicion.tituloMostrar}")
                }
            },
            confirmButton = {
                Button(
                    onClick = onResetForm,
                    colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral)
                ) {
                    Text("Aceptar y Crear Otro", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(QhatuPurple)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Panel de vendedor",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Publicar Producto (D1)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onPublicarProducto,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QhatuCoral
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Publicar producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            formState.mensajeError?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Fotos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Fotos del Producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QhatuPurple
                )
                Text(
                    text = "Sube imágenes nítidas de tu producto para generar confianza en los compradores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = QhatuPurpleLight
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .size(100.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = QhatuPurple,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onAgregarFoto() }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Sube una foto",
                                    tint = QhatuPurple,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Sube una foto",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = QhatuPurple,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    itemsIndexed(formState.imagenesUris) { index, foto ->
                        Box(
                            modifier = Modifier.size(100.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Foto subida",
                                        tint = QhatuPurple,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = foto,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = QhatuPurple,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(QhatuCoral)
                                    .clickable { onEliminarFoto(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✕",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Condición
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Condición del Producto *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QhatuPurple
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CondicionProducto.entries.forEach { condicionEnum ->
                        val selected = formState.condicion == condicionEnum
                        FilterChip(
                            selected = selected,
                            onClick = { onCondicionChanged(condicionEnum) },
                            label = { Text(condicionEnum.tituloMostrar, fontWeight = FontWeight.Medium) },
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
            }

            Text(
                text = "Información General",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = QhatuPurple
            )

            OutlinedTextField(
                value = formState.titulo,
                onValueChange = onTituloChanged,
                label = { Text("Nombre del producto *") },
                placeholder = { Text("Ej. Chompa de alpaca tradicional") },
                isError = formState.tituloError != null,
                supportingText = {
                    formState.tituloError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QhatuPurple,
                    focusedLabelColor = QhatuPurple
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (formState.categoria.isEmpty()) "Seleccionar categoría" else formState.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QhatuPurple,
                        focusedLabelColor = QhatuPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    categoriasDisponibles.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                onCategoriaChanged(cat)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = formState.precio,
                    onValueChange = onPrecioChanged,
                    label = { Text("Precio (Bs) *") },
                    placeholder = { Text("180") },
                    isError = formState.precioError != null,
                    supportingText = {
                        formState.precioError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QhatuPurple,
                        focusedLabelColor = QhatuPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = formState.stock,
                    onValueChange = onStockChanged,
                    label = { Text("Stock *") },
                    placeholder = { Text("1") },
                    isError = formState.stockError != null,
                    supportingText = {
                        formState.stockError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QhatuPurple,
                        focusedLabelColor = QhatuPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = formState.descripcion,
                onValueChange = onDescripcionChanged,
                label = { Text("Descripción del producto") },
                placeholder = { Text("Añade detalles sobre la confección, material, tallas y envíos...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QhatuPurple,
                    focusedLabelColor = QhatuPurple
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PublicarProductoPreview() {
    CyberQhatuTheme {
        PublicarProductoContent(
            formState = PublicarProductoFormState(),
            onTituloChanged = {},
            onCategoriaChanged = {},
            onPrecioChanged = {},
            onStockChanged = {},
            onDescripcionChanged = {},
            onCondicionChanged = {},
            onAgregarFoto = {},
            onEliminarFoto = {},
            onPublicarProducto = {},
            onResetForm = {}
        )
    }
}
