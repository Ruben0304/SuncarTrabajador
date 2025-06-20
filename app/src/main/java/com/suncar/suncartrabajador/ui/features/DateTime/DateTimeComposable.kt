package com.suncar.suncartrabajador.ui.features.DateTime

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.components.CustomCard
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeComposable(
    modifier: Modifier = Modifier,
    dateTimeViewModel: DateTimeViewModel = viewModel()
) {
    val state by dateTimeViewModel.uiState.collectAsState()
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Fecha y Hora",
        subtitle = "Configura el horario de trabajo",
        icon = Icons.Filled.AccessTime,
        isLoading = state.isLoading
    ) {
        // Fecha actual (no editable)
        state.currentDate?.let { currentDate ->
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            val formattedDate = currentDate.format(dateFormatter).replaceFirstChar { it.uppercase() }

            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha Actual") },
                leadingIcon = {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Fecha")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hora de inicio
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Seleccionar hora",
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora de Inicio") },
                leadingIcon = {
                    Icon(Icons.Filled.AccessTime, contentDescription = "Hora inicio")
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { showStartTimePicker = true }) {
                Text("Cambiar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hora de fin
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Seleccionar hora",
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora de Fin") },
                leadingIcon = {
                    Icon(Icons.Filled.AccessTime, contentDescription = "Hora fin")
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { showEndTimePicker = true }) {
                Text("Cambiar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { dateTimeViewModel.refreshCurrentTime() }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Actualizar hora actual")
            }
        }

        // Mostrar duración
        if (state.startTime != null && state.endTime != null) {
            Spacer(modifier = Modifier.height(16.dp))

            val duration = java.time.Duration.between(state.startTime, state.endTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = "Duración",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Duración: ${hours}h ${minutes}min",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Time picker para hora de inicio
    if (showStartTimePicker) {
        val currentTime = state.startTime ?: java.time.LocalTime.now()
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute
        )

        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            title = { Text("Seleccionar Hora de Inicio") },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateTimeViewModel.updateStartTime(timePickerState.hour, timePickerState.minute)
                        showStartTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }

    // Time picker para hora de fin
    if (showEndTimePicker) {
        val currentTime = state.endTime ?: java.time.LocalTime.now()
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute
        )

        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            title = { Text("Seleccionar Hora de Fin") },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateTimeViewModel.updateEndTime(timePickerState.hour, timePickerState.minute)
                        showEndTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}