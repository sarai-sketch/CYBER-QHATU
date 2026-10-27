package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
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
import com.cyberqhatu.app.tienda.EstadoProducto
import com.cyberqhatu.app.tienda.FavoritoProducto
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.ui.theme.*

private val productoEjemplo = Producto(
    idProducto = "P-001",
    idTienda = "T-001",
    idCategoria = "CAT-001",
    nombre = "Chompa de Alpaca Fina",
    descripcion = "Chompa tejida a mano, 100% fibra de alpaca tradicional, suave al tacto y de alta durabilidad.",
    precio = 180.0,
    stock = 3,
    estado = EstadoProducto.ACTIVO,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    producto: Producto = productoEjemplo,
    isLoggedIn: Boolean = false,
    onBackClick: () -> Unit = {},
    onRequireLogin: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalle del Producto", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = QhatuPurple) },
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
                        mutableStateOf(DomainMockData.favoritosProductos.any { it.idProducto == producto.idProducto })
                    }
                    IconButton(
                        onClick = {
                            if (!isLoggedIn) {
                                onRequireLogin()
                            } else {
                                esFavorito = !esFavorito
                                if (esFavorito) {
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
                            imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito Producto",
                            tint = if (esFavorito) Color.Red else QhatuPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = QhatuPurple,
                    navigationIconContentColor = QhatuPurple,
                    actionIconContentColor = QhatuPurple,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
        ) {
            // Banner Superior Encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(QhatuPurple)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Column {
                    Text(
                        text = "Producto Verificado en Feria",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Tarjeta de información principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Bs. ${producto.precio}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = QhatuCoral,
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = QhatuLimeDark.copy(alpha = 0.15f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = QhatuLimeDark,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Código Seguro",
                                        color = QhatuLimeDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = producto.descripcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Tarjeta de Información del Puesto / Vendedor
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(QhatuPurple.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = QhatuPurple,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Puesto Don Carlos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = QhatuPurple,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.8 (126 reseñas)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones de acción
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { /* . */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = QhatuPurple),
                    ) {
                        Text("Enviar consulta", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { /* . */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "WhatsApp",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Abrir WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Reseñas (18) · 4.8", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QhatuPurple)
                Spacer(Modifier.height(12.dp))
                ResenaItem(autor = "Ana R.", comentario = "Tal cual la foto, muy buena atención y entrega rápida.")
                Spacer(Modifier.height(10.dp))
                ResenaItem(autor = "Carlos M.", comentario = "Producto 100% verificado con código seguro.")
            }
        }
    }
}

@Composable
private fun ResenaItem(autor: String, comentario: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = autor, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = QhatuPurple)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = comentario, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
