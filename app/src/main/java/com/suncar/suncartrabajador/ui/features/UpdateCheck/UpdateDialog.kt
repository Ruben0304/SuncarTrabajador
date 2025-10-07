package com.suncar.suncartrabajador.ui.features.UpdateCheck

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suncar.suncartrabajador.app.service_implementations.UpdateService

@Composable
fun UpdateDialog(
        viewModel: UpdateCheckViewModel,
        updateService: UpdateService,
        onDismiss: () -> Unit,
        onUpdateComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val updateInfo = state.updateInfo

    if (state.showUpdateDialog && updateInfo != null) {
        AlertDialog(
                onDismissRequest = {
                    if (!updateInfo.forceUpdate) {
                        viewModel.dismissUpdateDialog()
                        onDismiss()
                    }
                },
                title = {
                    Text(
                            text = "Nueva actualización disponible",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                    ) {
                        Text(
                                text = "Versión ${updateInfo.latestVersion}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text =
                                        "Tamaño: ${updateService.formatFileSize(updateInfo.fileSize)}",
                                style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = "Novedades:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                                text = updateInfo.changelog,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify
                        )

                        if (state.isDownloading) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Text(
                                        text = "Descargando actualización...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = "⚠️ No cierre la aplicación durante la descarga",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                        progress = state.downloadProgress,
                                        modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                        text = "${(state.downloadProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        state.error?.let { errorMessage ->
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                            onClick = {
                                viewModel.downloadUpdate { file ->
                                    if (file != null) {
                                        viewModel.dismissUpdateDialog()
                                        onUpdateComplete()
                                    }
                                }
                            },
                            enabled = !state.isDownloading
                    ) {
                        if (state.isDownloading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Actualizar")
                    }
                },
                dismissButton = {
                    if (!updateInfo.forceUpdate) {
                        OutlinedButton(
                                onClick = {
                                    viewModel.dismissUpdateDialog()
                                    onDismiss()
                                },
                                enabled = !state.isDownloading
                        ) { Text("Más tarde") }
                    }
                }
        )
    }
}
