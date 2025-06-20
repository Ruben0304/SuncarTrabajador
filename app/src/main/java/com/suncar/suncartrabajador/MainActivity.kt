package com.suncar.suncartrabajador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.suncar.suncartrabajador.ui.theme.SuncarTrabajadorTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            SuncarTrabajadorTheme {
                SuncarTrabajadorApp()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuncarTrabajadorApp() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )
    val scope = rememberCoroutineScope()

    val isSheetVisible = bottomSheetScaffoldState.bottomSheetState.currentValue != SheetValue.Hidden
    val fabIconRotation by animateFloatAsState(targetValue = if (isSheetVisible) 45f else 0f)
    val fabColor by animateColorAsState(
        targetValue = if (isSheetVisible) Color.Red else MaterialTheme.colorScheme.primary
    )

    // Control manual del indicador
    val showIndicator = remember { mutableStateOf(true) }

    // Si el formulario se cierra, volver a mostrar el indicador
    LaunchedEffect(isSheetVisible) {
        if (!isSheetVisible) {
            showIndicator.value = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
//                H1114FormScreen()
            },
            // Estas propiedades desactivan el swipe
            sheetSwipeEnabled = false, // Desactiva el swipe (disponible en versiones más recientes)
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // TopBar
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon),
                                    contentDescription = "Icono de la app",
                                    modifier = Modifier
                                        .size(52.dp)
                                        .padding(end = 12.dp)
                                )
                                Text(
                                    text = "SunCar Trabajadores",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Contenido principal
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Instrucciones para el Formulario H1114",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Text(
                                    text = "Para completar correctamente el formulario, sigue estos pasos:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Text(
                                    text = "• Seleccionar jefe de brigada del equipo de trabajo\n\n" +
                                            "• Seleccionar miembros que conformarán la brigada\n\n" +
                                            "• Seleccionar materiales de forma filtrada:\n" +
                                            "  - Primero selecciona el tipo de material\n" +
                                            "  - Luego selecciona la marca disponible\n" +
                                            "  - Finalmente selecciona el nombre específico\n" +
                                            "  (Las opciones se expandirán automáticamente según tu selección anterior)\n\n" +
                                            "• Escribir la dirección completa del proyecto\n\n" +
                                            "• Tomar y adjuntar fotos del inicio del proyecto\n\n" +
                                            "• Tomar y adjuntar fotos del fin del proyecto\n\n" +
                                            "• Seleccionar la fecha de inicio del proyecto\n\n" +
                                            "• Seleccionar la fecha de finalización del proyecto",
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        )

        // Indicador sin fondo a la izquierda del FAB
        if (showIndicator.value && !isSheetVisible) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 96.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Abrir formulario H1114",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Flecha hacia el botón",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // FAB siempre visible
        FloatingActionButton(
            onClick = {
                scope.launch {
                    if (isSheetVisible) {
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    } else {
                        showIndicator.value = false // Oculta el texto instantáneamente
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = fabColor
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isSheetVisible) "Cerrar formulario" else "Mostrar formulario",
                modifier = Modifier.rotate(fabIconRotation)
            )
        }
    }
}

//
//@PreviewScreenSizes
//@Composable
//fun SuncarTrabajadorApp() {
//    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
//
//    NavigationSuiteScaffold(
//        navigationSuiteItems = {
//            AppDestinations.entries.forEach {
//                item(
//                    icon = {
//                        Icon(
//                            it.icon,
//                            contentDescription = it.label
//                        )
//                    },
//                    label = { Text(it.label) },
//                    selected = it == currentDestination,
//                    onClick = { currentDestination = it }
//                )
//            }
//        }
//    ) {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            H1114FormScreen(
//                modifier = Modifier.padding(innerPadding)
//            )
//        }
//    }
//}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SuncarTrabajadorTheme {
        SuncarTrabajadorApp()
    }
}