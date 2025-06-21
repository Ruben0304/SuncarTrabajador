package com.suncar.suncartrabajador.ui.layout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.suncar.suncartrabajador.ui.screens.Brigada.BrigadaComposable
import com.suncar.suncartrabajador.ui.screens.ListadoReportes.ListadoReportesComposable
import com.suncar.suncartrabajador.ui.screens.Nuevo.NuevoComposable
import com.suncar.suncartrabajador.ui.reportes.Inversion.InversionScreen


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    var currentDestination by remember { mutableStateOf(AppDestinations.REPORTS) }
    var currentScreen by remember { mutableStateOf<Screen?>(null) }

    Scaffold(
        topBar = {
            TopAppBarCustom(
                onBackPressed = {
                    if (currentScreen != null) {
                        currentScreen = null
                    }
                },
                showBackButton = currentScreen != null
            )
        },
        bottomBar = {
            // Solo mostrar bottom bar si no estamos en una pantalla específica
            if (currentScreen == null) {
                BottomAppBar(
                    actions = {
                        AppDestinations.entries.forEach { destination ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label) },
                                selected = currentDestination == destination,
                                onClick = { currentDestination = destination }
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Contenido principal basado en la navegación
            when {
                currentScreen != null -> {
                    when (currentScreen) {
                        Screen.INVERSION -> {
                            InversionScreen(
                                onBackPressed = { currentScreen = null },
                                onSubmit = {
                                    // TODO: Implementar lógica de envío
                                    currentScreen = null
                                }
                            )
                        }
                        // Agregar más pantallas aquí según sea necesario
                        Screen.OPERACION -> TODO()
                        Screen.AVERIA -> TODO()
                        null -> TODO()
                    }
                }
                currentDestination == AppDestinations.REPORTS -> {
                    ListadoReportesComposable(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                currentDestination == AppDestinations.NUEVO -> {
                    NuevoComposable(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToInversion = { currentScreen = Screen.INVERSION }
                    )
                }
                currentDestination == AppDestinations.BRIGADA -> {
                    BrigadaComposable(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    REPORTS("Reportes", Icons.Default.Assessment),
    NUEVO("Nuevo", Icons.Default.Add),
    BRIGADA("Brigada", Icons.Default.Group),
}

enum class Screen {
    INVERSION,
    OPERACION,
    AVERIA
}


