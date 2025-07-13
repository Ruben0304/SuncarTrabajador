package com.suncar.suncartrabajador.ui.features.DatosIniciales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.app.service_implementations.UpdateApiServices
import com.suncar.suncartrabajador.app.service_implementations.UpdateService
import com.suncar.suncartrabajador.ui.features.UpdateCheck.UpdateCheckViewModel
import com.suncar.suncartrabajador.ui.features.UpdateCheck.UpdateCheckViewModelFactory
import com.suncar.suncartrabajador.ui.features.UpdateCheck.UpdateDialog
import com.suncar.suncartrabajador.ui.shared.AdvancedLottieAnimation
import com.suncar.suncartrabajador.utils.AppUtils
import kotlinx.coroutines.delay

@Composable
fun DatosInicialesComposable(
        modifier: Modifier = Modifier,
        onDataLoadComplete: () -> Unit,
        datosInicialesViewModel: DatosInicialesViewModel = viewModel(),
        updateCheckViewModel: UpdateCheckViewModel =
                viewModel(
                        factory =
                                UpdateCheckViewModelFactory(
                                        UpdateService(
                                                updateApiService = UpdateApiServices().api,
                                                context = LocalContext.current
                                        )
                                )
                )
) {
        val state by datosInicialesViewModel.uiState.collectAsState()
        val context = LocalContext.current
        var updateCheckCompleted by remember { mutableStateOf(false) }

        // Verificar actualizaciones primero
        LaunchedEffect(Unit) {
                if (!updateCheckCompleted) {
                        val currentVersion = AppUtils.getAppVersion(context)
                        updateCheckViewModel.checkForUpdates(currentVersion) { updateInfo ->
                                updateCheckCompleted = true
                                // Si la app está actualizada o el usuario decidió continuar, cargar
                                // datos iniciales
                                if (updateInfo.isUpToDate) {
                                        datosInicialesViewModel.startInitialDataLoad(
                                                context,
                                                onDataLoadComplete
                                        )
                                }
                        }
                }
        }

        // Iniciar carga de datos cuando la verificación de actualización se complete
        LaunchedEffect(updateCheckCompleted) {
                if (updateCheckCompleted) {
                        datosInicialesViewModel.startInitialDataLoad(context, onDataLoadComplete)
                }
        }

        // Verificar conexión a internet periódicamente si está bloqueado
        LaunchedEffect(state.isBlockedByNoInternet) {
                if (state.isBlockedByNoInternet) {
                        while (state.isBlockedByNoInternet) {
                                delay(2000) // Verificar cada 2 segundos
                                datosInicialesViewModel.checkInternetConnectionAndUpdate(context)
                        }
                }
        }

        Box(
                modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
        ) {
                // Mostrar pantalla de bloqueo por falta de internet
                if (state.isBlockedByNoInternet) {
                        NoInternetScreen(
                                onRetry = {
                                        datosInicialesViewModel.retryLoad(
                                                context,
                                                onDataLoadComplete
                                        )
                                },
                                onRestart = { datosInicialesViewModel.restartApp(context) }
                        )
                } else {
                        // Pantalla normal de carga
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                        ) {
                                // Animación Lottie
                                AdvancedLottieAnimation()

                                Spacer(modifier = Modifier.height(32.dp))

                                // Texto de carga
                                Text(
                                        text = state.currentLoadingStep,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                //            // Barra de progreso
                                //            LinearProgressIndicator(
                                //                progress = state.loadingProgress,
                                //                modifier = Modifier
                                //                    .fillMaxWidth()
                                //                    .height(8.dp),
                                //                color = MaterialTheme.colorScheme.primary,
                                //                trackColor =
                                // MaterialTheme.colorScheme.primary.copy(alpha
                                // = 0.2f)
                                //            )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Mensaje de error si existe
                                state.errorMessage?.let { errorMessage ->
                                        ErrorCard(
                                                message = errorMessage,
                                                onRetry = {
                                                        datosInicialesViewModel.retryLoad(
                                                                context,
                                                                onDataLoadComplete
                                                        )
                                                }
                                        )
                                }

                                // Información de trabajadores cargados
                                if (state.trabajadores.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                                text =
                                                        "${state.trabajadores.size} trabajadores cargados",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color =
                                                        MaterialTheme.colorScheme.onBackground.copy(
                                                                alpha = 0.7f
                                                        )
                                        )
                                }
                        }
                }
        }

        // Mostrar diálogo de actualización si es necesario
        UpdateDialog(
                viewModel = updateCheckViewModel,
                updateService =
                        UpdateService(
                                updateApiService = UpdateApiServices().api,
                                context = context
                        ),
                onDismiss = {
                        updateCheckCompleted = true
                        datosInicialesViewModel.startInitialDataLoad(context, onDataLoadComplete)
                },
                onUpdateComplete = {
                        updateCheckCompleted = true
                        datosInicialesViewModel.startInitialDataLoad(context, onDataLoadComplete)
                }
        )
}

@Composable
private fun InternetConnectionWarning() {
        Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                shape = RoundedCornerShape(12.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = "Sin conexión",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = "Sin conexión a internet",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                )
                                Text(
                                        text = "Algunas funcionalidades estarán limitadas",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                        }
                }
        }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                shape = RoundedCornerShape(12.dp)
        ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                        text = "Error de carga",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                                onClick = onRetry,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                        ),
                                modifier = Modifier.align(Alignment.End)
                        ) { Text("Reintentar") }
                }
        }
}

@Composable
private fun NoInternetScreen(onRetry: () -> Unit, onRestart: () -> Unit) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
        ) {
                // Icono de WiFi desconectado
                Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Sin conexión a internet",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Título
                Text(
                        text = "Sin conexión a internet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                        text =
                                "La aplicación requiere conexión a internet para funcionar correctamente. Por favor, verifica tu conexión y vuelve a intentar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Botón de reintentar
                Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                )
                ) {
                        Text(
                                text = "Reintentar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de reiniciar aplicación
                OutlinedButton(
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors =
                                ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                )
                ) {
                        Text(
                                text = "Reiniciar aplicación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                        )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Información adicional
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                        shape = RoundedCornerShape(12.dp)
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                        text = "¿Qué puedes hacer?",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                        text =
                                                "• Verifica que tu dispositivo tenga conexión a internet\n• Intenta conectarte a una red WiFi diferente\n• Si usas datos móviles, verifica que estén activos\n• Reinicia la aplicación si el problema persiste",
                                        style = MaterialTheme.typography.bodySmall,
                                        color =
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.8f
                                                )
                                )
                        }
                }
        }
}
