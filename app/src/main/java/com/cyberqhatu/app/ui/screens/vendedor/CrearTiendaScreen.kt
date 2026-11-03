package com.cyberqhatu.app.ui.screens.vendedor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.feature.tienda.data.repository.SqliteCloudTiendaRepository
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.tienda.TipoTienda
import com.cyberqhatu.app.ui.screens.tienda.DomainMockData
import kotlinx.coroutines.launch
import java.util.UUID

private val HORA_REGEX = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

/**
 * Paso 2 para vender: formulario para crear y registrar la tienda.
 * Incluye selección del tipo de tienda (Física o Virtual).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTiendaScreen(
    idPropietario: String = "u1",
    onTiendaCreada: (Tienda) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val tiendaRepository = remember { SqliteCloudTiendaRepository() }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoTienda.FISICA) }
    var direccion by remember { mutableStateOf("") }
    var apertura by remember { mutableStateOf("08:00") }
    var cierre by remember { mutableStateOf("18:00") }
    var whatsapp by remember { mutableStateOf("") }

    val nombreOk = nombre.trim().length >= 3
    val horasValidas = HORA_REGEX.matches(apertura) && HORA_REGEX.matches(cierre)
    val horarioOk = horasValidas && apertura < cierre
    val whatsappOk = whatsapp.length == 8 && (whatsapp.first() == '6' || whatsapp.first() == '7')
    
    val formularioValido = nombreOk && whatsappOk && (apertura.isEmpty() || cierre.isEmpty() || horarioOk)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Tienda", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Paso 2: Registra tu tienda", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                Text(
                    "Completa los datos de tu puesto o tienda para comenzar a vender en Cyber-Qhatu.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la tienda / Puesto *") },
                    isError = nombre.isNotEmpty() && !nombreOk,
                    supportingText = { if (nombre.isNotEmpty() && !nombreOk) Text("Mínimo 3 caracteres") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Tipo de Comercio / Puesto *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = tipoSeleccionado == TipoTienda.FISICA,
                        onClick = { tipoSeleccionado = TipoTienda.FISICA },
                        label = { Text("Puesto / Tienda Física") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = tipoSeleccionado == TipoTienda.VIRTUAL,
                        onClick = { tipoSeleccionado = TipoTienda.VIRTUAL },
                        label = { Text("Tienda Virtual / Online") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción de productos (Opcional)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Ubicación / Sector (Ej. Sector Altiplano Puesto #12)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = apertura,
                        onValueChange = { apertura = it.take(5) },
                        label = { Text("Apertura (HH:mm)") },
                        singleLine = true,
                        isError = !HORA_REGEX.matches(apertura),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = cierre,
                        onValueChange = { cierre = it.take(5) },
                        label = { Text("Cierre (HH:mm)") },
                        singleLine = true,
                        isError = !HORA_REGEX.matches(cierre),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                if (horasValidas && apertura >= cierre) {
                    Text(
                        "La hora de cierre debe ser después de la apertura.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it.filter(Char::isDigit).take(8) },
                    label = { Text("WhatsApp de contacto *") },
                    prefix = { Text("+591 ") },
                    isError = whatsapp.isNotEmpty() && !whatsappOk,
                    supportingText = { Text("8 dígitos, empieza con 6 o 7") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(8.dp))

                var guardando by remember { mutableStateOf(false) }
                var errorNube by remember { mutableStateOf<String?>(null) }

                if (errorNube != null) {
                    Text(
                        text = errorNube!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        guardando = true
                        val nuevaTienda = Tienda(
                            idTienda = "t-${UUID.randomUUID().toString().take(8)}",
                            idPropietario = idPropietario,
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            logo = null,
                            tipo = tipoSeleccionado,
                            direccionGoogleMaps = direccion.trim(),
                            horarioApertura = apertura,
                            horarioCierre = cierre,
                            linkWhatsapp = "https://wa.me/591$whatsapp"
                        )
                        
                        DomainMockData.tiendas.add(0, nuevaTienda)

                        coroutineScope.launch {
                            try {
                                val resultado = tiendaRepository.guardarTiendaRemota(nuevaTienda)
                                resultado.fold(
                                    onSuccess = {
                                        onTiendaCreada(nuevaTienda)
                                    },
                                    onFailure = { error ->
                                        errorNube = "Error SQLite Cloud: ${error.message}"
                                        guardando = false
                                    }
                                )
                            } catch (e: Exception) {
                                errorNube = "Excepción: ${e.message}"
                                guardando = false
                            }
                        }
                    },
                    enabled = formularioValido && !guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (guardando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar y Abrir Tienda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
