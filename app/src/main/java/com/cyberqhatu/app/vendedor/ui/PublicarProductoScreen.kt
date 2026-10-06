package com.cyberqhatu.app.vendedor.ui

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Pantalla 7: Publicar Producto (D1) - Módulo 4 Vendedor.
 * Contiene formulario, selector de condición y componente ligero de carga simulada de fotos.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PublicarProductoScreen(
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(PublicarProductoFormState()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val categoriasDisponibles = listOf(
        "Electrónica y Tecnología",
        "Ropa y Calzado",
        "Hogar y Jardín",
        "Deportes y Entretenimiento",
        "Accesorios y Belleza",
        "Otros"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Publicar Producto (D1)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Módulo 4: Panel de Gestión del Vendedor",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // --- SECCIÓN 1: FOTOS DEL PRODUCTO ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Fotos del Producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Agrega hasta 5 fotos claras para destacar tu publicación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Botón ligero para agregar imagen simulada
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .size(90.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (formState.imagenesUris.size < 5) {
                                        val nuevaImagen = "Foto #${formState.imagenesUris.size + 1}"
                                        formState = formState.copy(
                                            imagenesUris = formState.imagenesUris + nuevaImagen
                                        )
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "➕",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Agregar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Lista de fotos agregadas
                    itemsIndexed(formState.imagenesUris) { index, foto ->
                        Box(
                            modifier = Modifier.size(90.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "📷",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = foto,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            // Botón eliminar imagen
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                                    .clickable {
                                        val listaActualizada = formState.imagenesUris.toMutableList()
                                        listaActualizada.removeAt(index)
                                        formState = formState.copy(imagenesUris = listaActualizada)
                                    },
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

            // --- SECCIÓN 2: CONDICIÓN DEL PRODUCTO ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Condición del Producto *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
                            onClick = {
                                formState = formState.copy(condicion = condicionEnum)
                            },
                            label = { Text(condicionEnum.tituloMostrar) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // --- SECCIÓN 3: INFORMACIÓN BÁSICA DEL PRODUCTO ---
            Text(
                text = "Información General",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 1. Título del Producto
            OutlinedTextField(
                value = formState.titulo,
                onValueChange = { nuevoTexto ->
                    val error = if (nuevoTexto.isBlank()) "El título no puede estar vacío" else null
                    formState = formState.copy(titulo = nuevoTexto, tituloError = error)
                },
                label = { Text("Título del producto *") },
                placeholder = { Text("Ej. Laptop Gamer 16GB RAM") },
                isError = formState.tituloError != null,
                supportingText = {
                    formState.tituloError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Categoría
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
                                formState = formState.copy(categoria = cat)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // 3. Fila de Precio y Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Precio (S/)
                OutlinedTextField(
                    value = formState.precio,
                    onValueChange = { nuevoPrecio ->
                        val error = when {
                            nuevoPrecio.isBlank() -> "Requerido"
                            nuevoPrecio.toDoubleOrNull() == null -> "Inválido"
                            else -> null
                        }
                        formState = formState.copy(precio = nuevoPrecio, precioError = error)
                    },
                    label = { Text("Precio (S/) *") },
                    placeholder = { Text("0.00") },
                    isError = formState.precioError != null,
                    supportingText = {
                        formState.precioError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                // Stock Disponible
                OutlinedTextField(
                    value = formState.stock,
                    onValueChange = { nuevoStock ->
                        val error = when {
                            nuevoStock.isBlank() -> "Requerido"
                            nuevoStock.toIntOrNull() == null || nuevoStock.toInt() < 1 -> ">= 1"
                            else -> null
                        }
                        formState = formState.copy(stock = nuevoStock, stockError = error)
                    },
                    label = { Text("Stock *") },
                    placeholder = { Text("1") },
                    isError = formState.stockError != null,
                    supportingText = {
                        formState.stockError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Descripción del Producto
            OutlinedTextField(
                value = formState.descripcion,
                onValueChange = { formState = formState.copy(descripcion = it) },
                label = { Text("Descripción del producto") },
                placeholder = { Text("Describe las características principales, estado y detalles de envío...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
