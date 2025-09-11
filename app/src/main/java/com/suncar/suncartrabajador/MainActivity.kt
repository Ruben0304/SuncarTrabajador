package com.suncar.suncartrabajador

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.suncar.suncartrabajador.singleton.Auth
import com.suncar.suncartrabajador.ui.features.DatosIniciales.DatosInicialesComposable
import com.suncar.suncartrabajador.ui.reportes.Averia.AveriaScreen
import com.suncar.suncartrabajador.ui.reportes.Inversion.InversionScreen
import com.suncar.suncartrabajador.ui.reportes.Mantenimiento.MantenimientoScreen
import com.suncar.suncartrabajador.ui.screens.Cuenta.CuentaConfigScreen
import com.suncar.suncartrabajador.ui.screens.ListadoReportes.ListadoReportesComposable
import com.suncar.suncartrabajador.ui.screens.Login.LoginComposable
import com.suncar.suncartrabajador.ui.screens.Nuevo.NuevoComposable
import com.suncar.suncartrabajador.ui.theme.SuncarTrabajadorTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuncarTrabajadorTheme {
                val context = LocalContext.current
                val window = (context as Activity).window

                LaunchedEffect(Unit) {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                        controller.hide(WindowInsetsCompat.Type.navigationBars())
                        controller.systemBarsBehavior =
                                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
                SuncarTrabajadorApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Preview
fun SuncarTrabajadorApp() {
    val navController = rememberNavController()
    val destinations =
            listOf(
                    Triple(
                            "main_app/reports",
                            "Reportes",
                            androidx.compose.material.icons.Icons.Default.Assessment
                    ),
                    Triple(
                            "main_app/nuevo",
                            "Nuevo",
                            androidx.compose.material.icons.Icons.Default.Add
                    ),
                    Triple(
                            "main_app/cuenta",
                            "Cuenta",
                            androidx.compose.material.icons.Icons.Default.AccountCircle
                    )
            )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarRoutes =
            destinations.map { it.first } +
                    listOf(
                            "main_app/nuevo/averia",
                            "main_app/nuevo/mantenimiento",
                            "main_app/nuevo/inversion"
                    )

    // Configuración de animaciones
    val animationDuration = 300 // Duración en milisegundos

    // Animación de slide horizontal (para navegación principal)
    val slideHorizontalTransition =
            slideInHorizontally(animationSpec = tween(animationDuration), initialOffsetX = { it }) +
                    fadeIn(animationSpec = tween(animationDuration)) with
                    slideOutHorizontally(
                            animationSpec = tween(animationDuration),
                            targetOffsetX = { -it }
                    ) + fadeOut(animationSpec = tween(animationDuration))

    // Animación de slide vertical (para reportes)
    val slideVerticalTransition =
            slideInVertically(animationSpec = tween(animationDuration), initialOffsetY = { it }) +
                    fadeIn(animationSpec = tween(animationDuration)) with
                    slideOutVertically(
                            animationSpec = tween(animationDuration),
                            targetOffsetY = { -it }
                    ) + fadeOut(animationSpec = tween(animationDuration))

    // Animación de fade (para login y carga)
    val fadeTransition =
            fadeIn(animationSpec = tween(animationDuration)) with
                    fadeOut(animationSpec = tween(animationDuration))

    Scaffold(
            topBar = {
                if (currentRoute in topBarRoutes) {
                    com.suncar.suncartrabajador.ui.layout.TopAppBarCustom(
                            onBackPressed = { navController.popBackStack() },
                            showBackButton = navController.previousBackStackEntry != null
                    )
                }
            },
            bottomBar = {
                if (currentRoute in destinations.map { it.first }) {
                    androidx.compose.material3.BottomAppBar(
                            containerColor =
                                    androidx.compose.material3.MaterialTheme.colorScheme.background,
                            actions = {
                                destinations.forEach { (route, label, icon) ->
                                    NavigationBarItem(
                                            colors =
                                                    androidx.compose.material3
                                                            .NavigationBarItemDefaults.colors(
                                                            androidx.compose.material3.MaterialTheme
                                                                    .colorScheme
                                                                    .onPrimary,
                                                            androidx.compose.material3.MaterialTheme
                                                                    .colorScheme
                                                                    .onSurface,
                                                            androidx.compose.material3.MaterialTheme
                                                                    .colorScheme
                                                                    .primary
                                                    ),
                                            icon = {
                                                androidx.compose.material3.Icon(
                                                        icon,
                                                        contentDescription = label
                                                )
                                            },
                                            label = { androidx.compose.material3.Text(label) },
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
        androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = "loading_data") {
                // Pantallas de carga y login con fade
                composable(
                        "loading_data",
                        enterTransition = { fadeIn(animationSpec = tween(animationDuration)) },
                        exitTransition = { fadeOut(animationSpec = tween(animationDuration)) }
                ) {
                    com.suncar.suncartrabajador.ui.features.DatosIniciales.DatosInicialesComposable(
                            modifier = Modifier.fillMaxSize(),
                            onDataLoadComplete = {
                                if (com.suncar.suncartrabajador.singleton.Auth.isUserAuthenticated()
                                ) {
                                    navController.navigate("main_app/nuevo") {
                                        popUpTo("loading_data") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("login") {
                                        popUpTo("loading_data") { inclusive = true }
                                    }
                                }
                            }
                    )
                }
                composable(
                        "login",
                        enterTransition = {
                            slideInHorizontally(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetX = { it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetX = { -it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    com.suncar.suncartrabajador.ui.screens.Login.LoginComposable(
                            modifier = Modifier.fillMaxSize(),
                            onLoginSuccess = {
                                navController.navigate("loading_data") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                    )
                }

                // Navegación principal con slide horizontal
                composable(
                        "main_app/reports",
                        enterTransition = {
                            slideInHorizontally(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetX = { -it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetX = { it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    val context = LocalContext.current
                    val listadoReportesViewModel:
                            com.suncar.suncartrabajador.ui.screens.ListadoReportes.ListadoReportesViewModel =
                            viewModel(
                                    factory =
                                            ViewModelProvider.AndroidViewModelFactory(
                                                    context.applicationContext as Application
                                            )
                            )
                    com.suncar.suncartrabajador.ui.screens.ListadoReportes
                            .ListadoReportesComposable(
                                    modifier = Modifier.fillMaxSize(),
                                    listadoReportesViewModel = listadoReportesViewModel
                            )
                }
                composable(
                        "main_app/nuevo",
                        enterTransition = {
                            slideInHorizontally(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetX = { it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetX = { -it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    com.suncar.suncartrabajador.ui.screens.Nuevo.NuevoComposable(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController
                    )
                }
                composable(
                        "main_app/cuenta",
                        enterTransition = {
                            slideInHorizontally(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetX = { it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetX = { -it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    com.suncar.suncartrabajador.ui.screens.Cuenta.CuentaConfigScreen(
                            modifier = Modifier.fillMaxSize(),
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("main_app/reports") { inclusive = true }
                                }
                            }
                    )
                }

                // Reportes con slide vertical
                composable(
                        "main_app/nuevo/averia",
                        enterTransition = {
                            slideInVertically(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetY = { it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutVertically(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetY = { it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    com.suncar.suncartrabajador.ui.reportes.Averia.AveriaScreen(
                            onBackPressed = { navController.popBackStack() },
                            onSubmit = { navController.popBackStack("main_app/reports", false) }
                    )
                }
                composable(
                        "main_app/nuevo/mantenimiento",
                        enterTransition = {
                            slideInVertically(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetY = { it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutVertically(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetY = { it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    com.suncar.suncartrabajador.ui.reportes.Mantenimiento.MantenimientoScreen(
                            onBackPressed = { navController.popBackStack() },
                            onSubmit = { navController.popBackStack("main_app/reports", false) }
                    )
                }
                composable(
                        "main_app/nuevo/inversion",
                        enterTransition = {
                            slideInVertically(
                                    animationSpec = tween(animationDuration),
                                    initialOffsetY = { it }
                            ) + fadeIn(animationSpec = tween(animationDuration))
                        },
                        exitTransition = {
                            slideOutVertically(
                                    animationSpec = tween(animationDuration),
                                    targetOffsetY = { it }
                            ) + fadeOut(animationSpec = tween(animationDuration))
                        }
                ) {
                    com.suncar.suncartrabajador.ui.reportes.Inversion.InversionScreen(
                            onBackPressed = { navController.popBackStack() },
                            onSubmit = { navController.popBackStack("main_app/reports", false) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SuncarTrabajadorTheme { SuncarTrabajadorApp() }
}
