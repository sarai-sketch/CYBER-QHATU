package com.cyberqhatu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.ui.screens.tienda.DetalleProductoScreen
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberQhatuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaDemoSwitcher(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaDemoSwitcher(modifier: Modifier = Modifier) {
    // 0 = Detalle de producto, 1 = Reportar tienda
    var pantallaActual by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pantallaActual) {
            Tab(
                selected = pantallaActual == 0,
                onClick = { pantallaActual = 0 },
                text = { Text("Detalle de producto") }
            )
            Tab(
                selected = pantallaActual == 1,
                onClick = { pantallaActual = 1 },
                text = { Text("Reportar tienda") }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (pantallaActual) {
                0 -> DetalleProductoScreen()
                1 -> ReportarTiendaScreen()
            }
        }
    }
}