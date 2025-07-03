package com.suncar.suncartrabajador.ui.screens.ListadoReportes

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.suncar.suncartrabajador.R
import com.suncar.suncartrabajador.domain.models.ReportList
import com.suncar.suncartrabajador.ui.shared.HeaderSection
import com.suncar.suncartrabajador.utils.ImageUtils

@Composable
fun ListadoReportesComposable(
    modifier: Modifier = Modifier,
    listadoReportesViewModel: ListadoReportesViewModel = viewModel()
) {
    val state by listadoReportesViewModel.uiState.collectAsState()

    // Recarga automática al entrar en la pantalla
    LaunchedEffect(Unit) {
        listadoReportesViewModel.refreshReportList()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        HeaderSection("Reportes no enviados","Reportes guardados que aún no has enviado")
        // Título

        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estado de carga
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando reportes...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else if (state.reports.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay reportes disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Listado de reportes
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.reports, key = { it.id }) { report ->
                    ReportItem(report = report, listadoReportesViewModel = listadoReportesViewModel)
                }
            }
        }
    }

    // Diálogo de éxito
    if (state.showSuccessDialog && state.successMessage != null) {
        AlertDialog(
            onDismissRequest = { listadoReportesViewModel.dismissSuccessDialog() },
            title = { Text("Éxito") },
            text = { Text(state.successMessage ?: "") },
            confirmButton = {
                Button(onClick = { listadoReportesViewModel.dismissSuccessDialog() }) {
                    Text("OK")
                }
            }
        )
    }
    // Diálogo de error
    if (state.showErrorDialog && state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { listadoReportesViewModel.dismissErrorDialog() },
            title = { Text("Error") },
            text = { Text(state.errorMessage ?: "") },
            confirmButton = {
                Button(onClick = { listadoReportesViewModel.dismissErrorDialog() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ReportItem(
    report: ReportList,
    modifier: Modifier = Modifier,
    listadoReportesViewModel: ListadoReportesViewModel = viewModel()
) {
    val state by listadoReportesViewModel.uiState.collectAsState()
    val isSending = state.sendingReports.contains("${report.id}")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen a la izquierda
            Base64Image(
                base64 = report.img,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp)
            )
            
            // Contenido de texto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Fecha como texto principal
                Text(
                    text = report.fecha,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Cliente como texto secundario
                Text(
                    text = report.cliente,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Botón "Enviar" al final
            Button(
                onClick = { 
                    if (!isSending) {
                        listadoReportesViewModel.sendReport(report)
                    }
                },
                modifier = Modifier.padding(start = 8.dp),
                enabled = !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando...")
                } else {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun Base64Image(base64: String, modifier: Modifier = Modifier) {
    val bitmap = remember(base64) { ImageUtils.decodeBase64ToBitmap(base64) }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Imagen base64",
            modifier = modifier,
            contentScale = ContentScale.Crop,

        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.icon_inside),
            contentDescription = if (base64.isNotBlank()) "Imagen no válida" else "Sin imagen",
            modifier = modifier
        )
    }
}