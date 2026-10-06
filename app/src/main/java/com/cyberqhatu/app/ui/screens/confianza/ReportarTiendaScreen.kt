package com.cyberqhatu.app.ui.screens.confianza

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.theme.*

private val motivos = listOf(
    "No entrego el producto",
    "Producto distinto al anunciado",
    "Trato irrespetuoso"
)

@Composable
fun ReportarTiendaScreen(nombreTienda: String = "Moda Andina") {
    var motivoSeleccionado by remember { mutableStateOf(motivos.first()) }
    var descripcion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CqLavanda)
    ) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB03838))
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text("Reportar tienda", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Tarjeta de la tienda
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4E0F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(nombreTienda, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Stock 2 \u00B7 Calle Comercio 123", fontSize = 11.sp, color = CqTextSub)
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Motivo del reporte", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))

            motivos.forEach { motivo ->
                val seleccionado = motivo == motivoSeleccionado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (seleccionado) Color(0xFFECE8FB) else Color.White)
                        .border(
                            width = if (seleccionado) 2.dp else 1.dp,
                            color = if (seleccionado) CqIndigo else Color(0xFFE4E0F5),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = seleccionado,
                        onClick = { motivoSeleccionado = motivo },
                        colors = RadioButtonDefaults.colors(selectedColor = CqIndigo)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(motivo, fontSize = 12.sp)
                }

            }

            Spacer(Modifier.height(10.dp))
            Text("Describe lo sucedido", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text("Ej: El vendedor no entrego el pedido...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CqIndigo)
            )

            Spacer(Modifier.height(18.dp))
            Text("Evidencia", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(
                                width = if (index == 0) 2.dp else 1.dp,
                                color = if (index == 0) CqIndigo else Color(0xFFE4E0F5),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (index == 0) "+" else "", fontSize = 20.sp, color = CqIndigo)
                    }
                }
            }

            Spacer(Modifier.height(26.dp))

            Button(
                onClick = { /* .*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = CqCoral)
            ) { Text("Enviar reporte") }

            Spacer(Modifier.height(14.dp))

            Text(
                "La tienda puede refutar con su propia evidencia. Un moderador decide.",
                fontSize = 11.sp,
                color = CqTextSub
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun ReportarTiendaScreenPreview() {
    ReportarTiendaScreen()
}