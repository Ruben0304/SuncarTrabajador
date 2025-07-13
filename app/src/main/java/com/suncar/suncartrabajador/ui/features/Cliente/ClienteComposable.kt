package com.suncar.suncartrabajador.ui.features.Cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import com.google.android.gms.location.LocationServices
import com.suncar.suncartrabajador.ui.components.CustomCard
import com.suncar.suncartrabajador.ui.components.EmptyStateCard
import com.suncar.suncartrabajador.ui.shared.LocationLottieAnimation

@Composable
fun ClienteComposable(
    modifier: Modifier = Modifier,
    clienteViewModel: ClienteViewModel = viewModel()
) {
    val state by clienteViewModel.state.collectAsState()
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permiso de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            clienteViewModel.onPermissionResult(granted, context)
        }
    )

    // Chequear y pedir permiso al entrar
    LaunchedEffect(Unit) {
        val hasPermission =
            context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        clienteViewModel.setGpsPermission(hasPermission)
        if (!hasPermission) {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            clienteViewModel.checkGpsStatus(context)
        }
    }
    LaunchedEffect(state.hasGpsPermission) {
        if (state.hasGpsPermission) {
            clienteViewModel.checkGpsStatus(context)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            clienteViewModel.stopGpsMonitoring()
            clienteViewModel.removeLocationUpdates(fusedLocationClient)
        }
    }
    LaunchedEffect(state.hasGpsPermission, state.gpsEnabled, state.esClienteNuevo) {
        if (state.hasGpsPermission && state.gpsEnabled && (state.locationData.latitud.isBlank() || state.locationData.longitud.isBlank()) && state.esClienteNuevo) {
            clienteViewModel.checkAndRequestLocation(context, fusedLocationClient, activity)
        }
    }
    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Cliente",
        subtitle = "Selecciona o ingresa los datos del cliente",
        icon = Icons.Filled.Person,
        isLoading = state.isLoading
    ) {
        // Radio para cliente nuevo/existente
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = !state.esClienteNuevo,
                onClick = { clienteViewModel.setEsClienteNuevo(false) }
            )
            Text("Cliente existente", modifier = Modifier.selectable(selected = !state.esClienteNuevo, onClick = { clienteViewModel.setEsClienteNuevo(false) }))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = state.esClienteNuevo,
                onClick = { clienteViewModel.setEsClienteNuevo(true) }
            )
            Text("Cliente nuevo", modifier = Modifier.selectable(selected = state.esClienteNuevo, onClick = { clienteViewModel.setEsClienteNuevo(true) }))
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!state.esClienteNuevo) {
            // Lógica de cliente existente (igual que antes)
            OutlinedTextField(
                value = state.numero,
                onValueChange = { clienteViewModel.onNumeroChanged(it) },
                label = { Text("Número de Cliente") },
                placeholder = { Text("Ej: 1001") },
                isError = state.error != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verificando cliente...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (state.cliente != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Cliente Encontrado",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ClienteInfoRow("Número:", state.cliente!!.numero)
                        ClienteInfoRow("Nombre:", state.cliente!!.nombre)
                        state.cliente!!.direccion?.let { direccion ->
                            ClienteInfoRow("Dirección:", direccion)
                        }
                    }
                }
            }
        } else {
            // Lógica de cliente nuevo + ubicación (fusionada de UbicacionComposable)
            OutlinedTextField(
                value = state.numeroClienteNuevo,
                onValueChange = { clienteViewModel.onNumeroClienteNuevoChanged(it) },
                label = { Text("Número de Cliente") },
                placeholder = { Text("Ej: 1006") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.nombreClienteNuevo,
                onValueChange = { clienteViewModel.onNombreClienteNuevoChanged(it) },
                label = { Text("Nombre del Cliente") },
                placeholder = { Text("Ej: Juan Pérez") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // --- UI de ubicación ---
            val showLottie = state.statusMessage == "Cargando ubicación por GPS..." || state.statusMessage == "Mejorando precisión..."
            OutlinedTextField(
                value = state.locationData.address,
                onValueChange = { clienteViewModel.onAddressChange(it) },
                label = { Text("Dirección del proyecto") },
                placeholder = { Text("Escribe la dirección o espera la ubicación...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (showLottie) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LocationLottieAnimation()
                        Spacer(modifier = Modifier.height(16.dp))
                        if (state.statusMessage.isNotBlank()) {
                            Text(
                                text = state.statusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                if (state.statusMessage.isNotBlank()) {
                    Text(
                        text = state.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                val coords = if (state.locationData.latitud.isNotBlank() && state.locationData.longitud.isNotBlank())
                    "${state.locationData.latitud},${state.locationData.longitud}" else ""
                if (coords.isNotBlank()) {
                    LocationInfoCard(
                        address = state.locationData.address,
                        coordinates = coords
                    )
                } else {
                    EmptyStateCard(
                        title = "No hay ubicación configurada",
                        description = "Escribe una dirección.",
                        icon = Icons.Filled.Place
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (!state.hasGpsPermission) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Permisos de ubicación no concedidos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else if (!state.gpsEnabled) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GPS desactivado, por favor activa la ubicación",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            if (state.isGpsMonitoring) {
                                // Mensaje opcional de monitoreo
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Por favor activa el GPS",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationInfoCard(
    address: String,
    coordinates: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información de Ubicación",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (address.isNotBlank()) {
                LocationInfoRow("Dirección:", address)
            }
            if (coordinates.isNotBlank()) {
                LocationInfoRow("Coordenadas:", coordinates)
            }
        }
    }
}
@Composable
private fun LocationInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun ClienteInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
} 