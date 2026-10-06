package com.cyberqhatu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cyberqhatu.app.ui.screens.confianza.ReportarTiendaScreen
import com.cyberqhatu.app.ui.screens.tienda.DetalleProductoScreen
import com.cyberqhatu.app.ui.theme.CyberQhatuTheme
import com.cyberqhatu.app.vendedor.ui.InventarioVentasScreen
import com.cyberqhatu.app.vendedor.ui.PublicarProductoScreen
import androidx.compose.material3.Icon
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberQhatuTheme {
                MainNavigationApp()
            }
        }
    }
}

@Composable
fun MainNavigationApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text(".", fontWeight = FontWeight.Bold) },
                    label = { Text("Detalle") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text(".", fontWeight = FontWeight.Bold) },
                    label = { Text("Reportar") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text(".", fontWeight = FontWeight.Bold) },
                    label = { Text("Publicar") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Text(".", fontWeight = FontWeight.Bold) },
                    label = { Text("Inventario") }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DetalleProductoScreen()
                1 -> ReportarTiendaScreen()
                2 -> PublicarProductoScreen(modifier = Modifier.fillMaxSize())
                3 -> InventarioVentasScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
