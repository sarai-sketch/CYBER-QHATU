package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.tienda.EstadoProducto
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
    estado = EstadoProducto.ACTIVO
)

@Composable
fun DetalleProductoScreen(producto: Producto = productoEjemplo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Banner Superior Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(QhatuPurple)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Tienda Oficial",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Detalle del Producto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // Foto del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(QhatuPurpleLight),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Foto del producto",
                        tint = QhatuPurple,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Foto del producto",
                        color = QhatuPurple,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Insignias de estado
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EstadoBadge(text = "✓ Precio verificado", bg = QhatuLime, fg = QhatuLimeDark)
                EstadoBadge(text = producto.estado.name, bg = QhatuPurple, fg = Color.White)
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = producto.nombre,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = QhatuPurple
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bs ${producto.precio.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = QhatuCoral
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = producto.descripcion,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // Tarjeta de la tienda
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(QhatuPurpleLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = QhatuPurple)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text("Moda Andina", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = QhatuPurple)
                        Spacer(Modifier.height(2.dp))
                        Text("★★★★★  4.8 (126 reseñas)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text("Ver tienda ›", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = QhatuPurple)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botones de acción
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { /* . */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QhatuPurple)
                ) {
                    Text("Enviar consulta", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { /* . */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QhatuCoral)
                ) {
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

@Composable
private fun EstadoBadge(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ResenaItem(autor: String, comentario: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("★★★★★", fontSize = 12.sp, color = Color(0xFFFFB300))
                Spacer(Modifier.width(8.dp))
                Text(autor, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = QhatuPurple)
            }
            Spacer(Modifier.height(4.dp))
            Text(comentario, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DetalleProductoScreenPreview() {
    DetalleProductoScreen()
}
