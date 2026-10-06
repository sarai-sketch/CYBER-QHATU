package com.cyberqhatu.app.ui.screens.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    idVendedor = "V-010",
    idTienda = "T-001",
    nombre = "Chompa de Alpaca Fina",
    descripcion = "Chompa tejida a mano, 100% fibra de alpaca, talla M.",
    precio = 180.0,
    stock = 3,
    estado = EstadoProducto.ACTIVO
)

@Composable
fun DetalleProductoScreen(producto: Producto = productoEjemplo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CqLavanda)
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFFECE8FB)),
            contentAlignment = Alignment.Center
        ) {
            Text("Foto del producto", color = CqTextSub, fontSize = 13.sp)
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // Insignias de estado
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EstadoBadge(text = "Precio verificado", bg = CqLima, fg = Color(0xFF231E3C))
                EstadoBadge(text = producto.estado.name, bg = CqIndigo, fg = Color.White)
              }

            Spacer(Modifier.height(14.dp))

            Text(
                text = producto.nombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF231E3C)
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bs ${producto.precio.toInt()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CqCoral
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(producto.descripcion, fontSize = 13.sp, color = CqTextSub)

            Spacer(Modifier.height(18.dp))

            // Tarjeta de la tienda
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4E0F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFECE8FB)),
                        contentAlignment = Alignment.Center
                    ) { Text("MA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CqIndigo) }

                    Spacer(Modifier.width(10.dp))

                    Column(Modifier.weight(1f)) {
                        Text("Moda Andina", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("\u2605\u2605\u2605\u2605\u2605  4.8 (126 resenas)", fontSize = 11.sp, color = CqTextSub)
                    }

                    Text("Ver tienda \u203A", fontSize = 11.sp, color = CqIndigo)
                }
            }

            Spacer(Modifier.height(18.dp))

            // enviar - abrir
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { /* . */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CqIndigo)
                ) { Text("Enviar consulta") }

                Button(
                    onClick = { /* . */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = CqCoral)
                ) { Text("Abrir WhatsApp") }
            }

            Spacer(Modifier.height(24.dp))

            Text("Resenas (18) \u00B7 4.8", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            ResenaItem(autor = "Ana R.", comentario = "Tal cual la foto, buen trato.")
            Spacer(Modifier.height(8.dp))
            ResenaItem(autor = "Carlos M.", comentario = "Producto verificado con codigo.")
        }
    }
}

@Composable
private fun EstadoBadge(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ResenaItem(autor: String, comentario: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\u2605\u2605\u2605\u2605\u2605", fontSize = 11.sp, color = Color(0xFFF0AA28))
            Spacer(Modifier.width(8.dp))
            Text(autor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Text(comentario, fontSize = 11.sp, color = CqTextSub)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DetalleProductoScreenPreview() {
    DetalleProductoScreen()
}