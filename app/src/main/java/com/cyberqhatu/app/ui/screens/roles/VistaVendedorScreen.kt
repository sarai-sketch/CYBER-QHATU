package com.cyberqhatu.app.ui.screens.roles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberqhatu.app.ui.PantallaMiPerfil
import com.cyberqhatu.app.ui.theme.QhatuPurple
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen
import com.cyberqhatu.app.verificacion.ui.EscanerIdentidadScreen
import com.cyberqhatu.app.verificacion.ui.InstruccionesVerificacionScreen

/**
 * Vista del Vendedor / Administrador de Tienda:
 * - Requiere verificación biométrica previa para operar.
 * - Gestión de inventario y ventas.
 * - Publicación de nuevos productos.
 * - Edición de perfil de tienda y gestión de colaboradores administradores.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaVendedorScreen(
    onCerrarSesion: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var modoVerificacion by remember { mutableIntStateOf(0) } // 0: Normal, 1: Instrucciones, 2: Escáner
    var isVerificado by remember { mutableStateOf(false) }

    if (modoVerificacion == 1) {
        InstruccionesVerificacionScreen(
            onComenzarClick = { modoVerificacion = 2 },
            onVolverClick = { modoVerificacion = 0 }
        )
        return
    } else if (modoVerificacion == 2) {
        EscanerIdentidadScreen(
            onVerificacionExitosa = { _ ->
                isVerificado = true
                modoVerificacion = 0
            },
            onVolverClick = { modoVerificacion = 0 }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel de Administrador de Tienda",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = QhatuPurple
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventario") },
                    label = { Text("Inventario") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AddBox, contentDescription = "Publicar") },
                    label = { Text("Publicar") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Mi Tienda") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> InventarioVentasScreen(
                    modifier = Modifier.fillMaxSize(),
                    onBackClick = {}
                )
                1 -> PublicarProductoScreen(
                    modifier = Modifier.fillMaxSize(),
                    onBackClick = { selectedTab = 0 }
                )
                2 -> PantallaMiPerfil(
                    nombreUsuario = "Administrador Principal",
                    correoUsuario = "admin.tienda@cyberqhatu.bo",
                    telefonoUsuario = "+591 71111111",
                    estadoVerificacion = isVerificado,
                    tipoRol = "Administrador de Tienda",
                    onEditarPerfilClick = {},
                    onCerrarSesionClick = onCerrarSesion,
                    onVerificarIdentidadClick = { modoVerificacion = 1 }
                )
            }
        }
    }
}
