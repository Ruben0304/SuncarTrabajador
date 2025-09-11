package com.suncar.suncartrabajador.ui.reportes.Mantenimiento

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
import com.suncar.suncartrabajador.ui.features.Descripcion.DescripcionComposable
import com.suncar.suncartrabajador.ui.features.Descripcion.DescripcionViewModel
import com.suncar.suncartrabajador.ui.features.FirmaCliente.FirmaClienteComposable
import com.suncar.suncartrabajador.ui.features.FirmaCliente.FirmaClienteViewModel
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesComposable
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesViewModel
import com.suncar.suncartrabajador.ui.shared.HeaderSection
import com.suncar.suncartrabajador.ui.shared.SaveButton
import com.suncar.suncartrabajador.ui.shared.SendButton

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MantenimientoScreen(
        onBackPressed: () -> Unit = {},
        onSubmit: () -> Unit = {},
        mantenimientoViewModel: MantenimientoViewModel = viewModel()
) {
    var isSubmitPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val state by mantenimientoViewModel.uiState.collectAsState()

    // ViewModels de cada componente
    val brigadaViewModel: BrigadaViewModel = viewModel()
    val materialesViewModel: MaterialesViewModel = viewModel()
    val clienteViewModel: ClienteViewModel = viewModel()
    val dateTimeViewModel: DateTimeViewModel = viewModel()
    val adjuntosViewModel: AdjuntosViewModel = viewModel()
    val descripcionViewModel: DescripcionViewModel = viewModel()
    val firmaClienteViewModel: FirmaClienteViewModel = viewModel()

    // Observar cambios en los estados de cada componente
    val brigadaState by brigadaViewModel.uiState.collectAsState()
    val materialesState by materialesViewModel.uiState.collectAsState()
    val clienteState by clienteViewModel.state.collectAsState()
    val dateTimeState by dateTimeViewModel.uiState.collectAsState()
    val adjuntosState by adjuntosViewModel.uiState.collectAsState()
    val descripcionState by descripcionViewModel.state.collectAsState()
    val firmaClienteState by firmaClienteViewModel.state.collectAsState()

    // Actualizar el estado del ViewModel principal cuando cambien los componentes
    LaunchedEffect(brigadaState) { mantenimientoViewModel.updateBrigada(brigadaState) }

    LaunchedEffect(materialesState) { mantenimientoViewModel.updateMateriales(materialesState) }

    LaunchedEffect(clienteState) { mantenimientoViewModel.updateCliente(clienteState) }

    LaunchedEffect(dateTimeState) { mantenimientoViewModel.updateDateTime(dateTimeState) }

    LaunchedEffect(adjuntosState) { mantenimientoViewModel.updateAdjuntos(adjuntosState) }

    LaunchedEffect(descripcionState) { mantenimientoViewModel.updateDescripcion(descripcionState) }

    LaunchedEffect(firmaClienteState) {
        mantenimientoViewModel.updateFirmaCliente(firmaClienteState)
    }

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
                        "Reporte de Mantenimiento",
                        "Complete los campos requeridos. Materiales y fotos son opcionales.",
                        modifier = Modifier.padding(bottom = 8.dp, top = 16.dp, start = 16.dp)
                )
            }

            item { BrigadaComposable(brigadaViewModel = brigadaViewModel) }
            item {
                MaterialesComposable(
                        materialesViewModel = materialesViewModel,
                        isMantenimiento = true
                )
            }
            item { ClienteComposable(clienteViewModel = clienteViewModel) }
            item { DateTimeComposable(dateTimeViewModel = dateTimeViewModel) }
            item { DescripcionComposable(descripcionViewModel = descripcionViewModel) }
            item {
                AdjuntosComposable(adjuntosViewModel = adjuntosViewModel, isMantenimiento = true)
            }
            item { FirmaClienteComposable(firmaClienteViewModel = firmaClienteViewModel) }
            item {
                SendButton(
                        modifier = Modifier.scale(scale),
                        onClick = {
                            isSubmitPressed = true
                            mantenimientoViewModel.submitForm(context)
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
                            mantenimientoViewModel.guardarMantenimientoLocal(context)
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
                onDismissRequest = { mantenimientoViewModel.dismissSuccessDialog() },
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
                                            ?: "El reporte de mantenimiento ha sido enviado correctamente.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                                onClick = { mantenimientoViewModel.showDetailsDialog() },
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF4CAF50)
                                        )
                        ) { Text("Ver Detalles") }
                        Button(
                                onClick = {
                                    mantenimientoViewModel.dismissSuccessDialog()
                                    onSubmit()
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
                onDismissRequest = { mantenimientoViewModel.dismissErrorDialog() },
                icon = {
                    Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFF44336)
                    )
                },
                title = {
                    Text(
                            text = "Error",
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
                            onClick = { mantenimientoViewModel.dismissErrorDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) { Text("Aceptar") }
                }
        )
    }

    // Diálogo de detalles
    if (state.showDetailsDialog) {
        AlertDialog(
                onDismissRequest = { mantenimientoViewModel.dismissDetailsDialog() },
                title = {
                    Text(
                            text = "Detalles del Reporte",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        state.responseData?.let { data ->
                            Text("Tipo: ${data.tipoReporte}")
                            Text("Cliente: ${data.cliente?.numero ?: "N/A"}")
                            Text("Fecha: ${data.fechaHora.fecha}")
                            Text("Hora Inicio: ${data.fechaHora.horaInicio}")
                            Text("Hora Fin: ${data.fechaHora.horaFin}")
                            Text("Descripción: ${data.descripcion}")
                            Text("Líder: ${data.brigada.lider.nombre}")
                            Text("Integrantes: ${data.brigada.integrantes.size}")
                            Text("Materiales: ${data.materiales.size}")
                            Text("Fotos Inicio: ${data.adjuntos.fotosInicio.size}")
                            Text("Fotos Fin: ${data.adjuntos.fotosFin.size}")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { mantenimientoViewModel.dismissDetailsDialog() }) {
                        Text("Cerrar")
                    }
                }
        )
    }
}
