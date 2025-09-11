package com.suncar.suncartrabajador.ui.layout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(navController: NavController, onLogout: () -> Unit = {}) {
    val destinations =
            listOf(
                    Triple("main_app/reports", "Reportes", Icons.Default.Assessment),
                    Triple("main_app/nuevo", "Nuevo", Icons.Default.Add),
                    Triple("main_app/cuenta", "Cuenta", Icons.Default.AccountCircle)
            )
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Scaffold(
            topBar = {
                TopAppBarCustom(
                        onBackPressed = { navController.popBackStack() },
                        showBackButton = navController.previousBackStackEntry != null
                )
            },
            bottomBar = {
                if (currentRoute in destinations.map { it.first }) {
                    BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.background,
                            actions = {
                                destinations.forEach { (route, label, icon) ->
                                    NavigationBarItem(
                                            colors =
                                                    NavigationBarItemDefaults.colors(
                                                            MaterialTheme.colorScheme.onPrimary,
                                                            MaterialTheme.colorScheme.onSurface,
                                                            MaterialTheme.colorScheme.primary
                                                    ),
                                            icon = { Icon(icon, contentDescription = label) },
                                            label = { Text(label) },
                                            selected = currentRoute == route,
                                            onClick = {
                                                if (currentRoute != route) {
                                                    navController.navigate(route) {
                                                        popUpTo("main_app/reports") {
                                                            inclusive = false
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                    )
                                }
                            }
                    )
                }
            }
    ) { innerPadding ->
        // El contenido real se maneja ahora en el NavHost de MainActivity
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {}
    }
}
