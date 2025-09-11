package com.suncar.suncartrabajador.ui.reportes.Inversion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosComposable
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosViewModel
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaComposable
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaViewModel
import com.suncar.suncartrabajador.ui.features.Cliente.ClienteComposable
import com.suncar.suncartrabajador.ui.features.Cliente.ClienteViewModel
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeComposable
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeViewModel
import com.suncar.suncartrabajador.ui.features.FirmaCliente.FirmaClienteComposable
import com.suncar.suncartrabajador.ui.features.FirmaCliente.FirmaClienteViewModel
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesComposable
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesViewModel
import com.suncar.suncartrabajador.ui.shared.HeaderSection
import com.suncar.suncartrabajador.ui.shared.SaveButton
import com.suncar.suncartrabajador.ui.shared.SendButton

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun InversionScreen(
        onBackPressed: () -> Unit = {},
        onSubmit: () -> Unit = {},
        inversionViewModel: InversionViewModel = viewModel()
) {
    var isSubmitPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val state by inversionViewModel.uiState.collectAsState()

    // ViewModels de cada componente
    val brigadaViewModel: BrigadaViewModel = viewModel()
    val materialesViewModel: MaterialesViewModel = viewModel()
    val clienteViewModel: ClienteViewModel = viewModel()
    val dateTimeViewModel: DateTimeViewModel = viewModel()
    val adjuntosViewModel: AdjuntosViewModel = viewModel()
    val firmaClienteViewModel: FirmaClienteViewModel = viewModel()

    // Observar cambios en los estados de cada componente
    val brigadaState by brigadaViewModel.uiState.collectAsState()
    val materialesState by materialesViewModel.uiState.collectAsState()
    val clienteState by clienteViewModel.state.collectAsState()
    val dateTimeState by dateTimeViewModel.uiState.collectAsState()
    val adjuntosState by adjuntosViewModel.uiState.collectAsState()
    val firmaClienteState by firmaClienteViewModel.state.collectAsState()

    // Actualizar el estado del ViewModel principal cuando cambien los componentes
    LaunchedEffect(brigadaState) { inversionViewModel.updateBrigada(brigadaState) }

    LaunchedEffect(materialesState) { inversionViewModel.updateMateriales(materialesState) }

    LaunchedEffect(clienteState) { inversionViewModel.updateCliente(clienteState) }

    LaunchedEffect(dateTimeState) { inversionViewModel.updateDateTime(dateTimeState) }

    LaunchedEffect(adjuntosState) { inversionViewModel.updateAdjuntos(adjuntosState) }

    LaunchedEffect(firmaClienteState) { inversionViewModel.updateFirmaCliente(firmaClienteState) }

    val scale by
            animateFloatAsState(
                    targetValue = if (isSubmitPressed) 0.95f else 1f,
                    animationSpec = tween(durationMillis = 100),
                    label = "submit_scale"
            )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                HeaderSection(
                        "Reporte de Inversión",
                        "Complete todos los campos requeridos para el reporte de inversión",
                        modifier = Modifier.padding(bottom = 8.dp, top = 16.dp, start = 16.dp)
                )
            }

            item { BrigadaComposable(brigadaViewModel = brigadaViewModel) }
            item { MaterialesComposable(materialesViewModel = materialesViewModel) }
            item { ClienteComposable(clienteViewModel = clienteViewModel) }
            item { DateTimeComposable(dateTimeViewModel = dateTimeViewModel) }
            item { AdjuntosComposable(adjuntosViewModel = adjuntosViewModel) }
            item { FirmaClienteComposable(firmaClienteViewModel = firmaClienteViewModel) }
            item {
                SendButton(
                        modifier = Modifier.scale(scale),
                        onClick = {
                            isSubmitPressed = true
                            inversionViewModel.submitForm(context)
                        },
                        enabled = state.isFormValid && !state.isSubmitting,
                        isLoading = state.isSubmitting
                )
            }
            item {
                SaveButton(
                        modifier = Modifier.scale(scale),
                        onClick = {
                            isSubmitPressed = true
                            inversionViewModel.guardarInversionLocal(context)
                        },
                        enabled = state.isFormValid && !state.isSaving,
                        isLoading = state.isSaving
                )
            }
        }

        // Reset pressed state
        LaunchedEffect(isSubmitPressed) {
            if (isSubmitPressed) {
                kotlinx.coroutines.delay(100)
                isSubmitPressed = false
            }
        }
    }

    // Diálogo de éxito
    if (state.showSuccessDialog) {
        AlertDialog(
                onDismissRequest = { inversionViewModel.dismissSuccessDialog() },
                icon = {
                    Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50)
                    )
                },
                title = {
                    Text(
                            text = "¡Enviado Exitosamente!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                            text = state.successMessage
                                            ?: "El reporte de inversión ha sido enviado correctamente.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                                onClick = { inversionViewModel.showDetailsDialog() },
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF4CAF50)
                                        )
                        ) { Text("Ver más") }
                        Button(
                                onClick = {
                                    inversionViewModel.dismissSuccessDialog()
                                    onBackPressed()
                                },
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50)
                                        )
                        ) { Text("Aceptar") }
                    }
                }
        )
    }

    // Diálogo de error
    if (state.showErrorDialog) {
        AlertDialog(
                onDismissRequest = { inversionViewModel.dismissErrorDialog() },
                icon = {
                    Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFF44336)
                    )
                },
                title = {
                    Text(
                            text = "Error al Enviar",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                            text = state.errorMessage ?: "Ha ocurrido un error inesperado.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                            onClick = { inversionViewModel.dismissErrorDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) { Text("Entendido") }
                }
        )
    }

    // Diálogo de detalles de la respuesta
    if (state.showDetailsDialog && state.responseData != null) {
        AlertDialog(
                onDismissRequest = { inversionViewModel.dismissDetailsDialog() },
                title = {
                    Text(
                            text = "Detalles del Reporte",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        item {
                            Text(
                                    text = "Tipo de Reporte: ${state.responseData!!.tipoReporte}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                    text = "Brigada:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                            Text(
                                    "Líder: ${state.responseData!!.brigada.lider.nombre} (CI: ${state.responseData!!.brigada.lider.ci})"
                            )
                            Text("Integrantes: ${state.responseData!!.brigada.integrantes.size}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                    text = "Materiales:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                            state.responseData!!.materiales.forEach { material ->
                                Text(
                                        "• ${material.nombre} - ${material.cantidad} ${material.unidadMedida}"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                    text = "Cliente:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                            Text("Nombre: ${state.responseData!!.cliente?.nombre}")
                            Text("Código: ${state.responseData!!.cliente?.numero}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                    text = "Fecha y Hora:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                            Text("Fecha: ${state.responseData!!.fechaHora.fecha}")
                            Text("Hora Inicio: ${state.responseData!!.fechaHora.horaInicio}")
                            Text("Hora Fin: ${state.responseData!!.fechaHora.horaFin}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                    text = "Adjuntos:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                            Text("Fotos Inicio: ${state.responseData!!.adjuntos.fotosInicio.size}")
                            Text("Fotos Fin: ${state.responseData!!.adjuntos.fotosFin.size}")
                        }
                    }
                },
                confirmButton = {
                    Button(
                            onClick = {
                                inversionViewModel.dismissDetailsDialog()
                                inversionViewModel.dismissSuccessDialog()
                                onBackPressed()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("Cerrar") }
                }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun InversionScreenPreview() {
    MaterialTheme { InversionScreen() }
}
