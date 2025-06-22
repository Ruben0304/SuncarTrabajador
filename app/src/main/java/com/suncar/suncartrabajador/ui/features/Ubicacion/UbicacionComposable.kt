package com.suncar.suncartrabajador.ui.features.Ubicacion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.components.CustomCard
import com.suncar.suncartrabajador.ui.components.EmptyStateCard
import com.suncar.suncartrabajador.ui.shared.LocationLottieAnimation
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionComposable(
    modifier: Modifier = Modifier,
    ubicacionViewModel: UbicacionViewModel = viewModel()
) {
    val state by ubicacionViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permiso de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            ubicacionViewModel.setGpsPermission(granted)
            if (granted) {
                ubicacionViewModel.setStatusMessage("Permiso concedido. Obteniendo ubicación...")
            } else {
                ubicacionViewModel.setStatusMessage("Permiso de ubicación denegado.")
            }
        }
    )

    // Chequear y pedir permiso al entrar
    LaunchedEffect(Unit) {
        val hasPermission =
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ubicacionViewModel.setGpsPermission(hasPermission)
        if (!hasPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Chequear si el GPS está activo
    LaunchedEffect(state.hasGpsPermission) {
        if (state.hasGpsPermission) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ubicacionViewModel.setGpsEnabled(gpsEnabled)
            if (!gpsEnabled) {
                ubicacionViewModel.setStatusMessage("Activa el GPS para obtener la ubicación.")
            }
        }
    }

    // Obtener ubicación precisa automáticamente
    LaunchedEffect(state.hasGpsPermission, state.gpsEnabled) {
        if (state.hasGpsPermission && state.gpsEnabled) {
            ubicacionViewModel.setStatusMessage("Cargando ubicación por GPS...")
            ubicacionViewModel.setLocationAccuracy(null)
            ubicacionViewModel.updateLocationData(state.locationData.copy(coordinates = "", distance = "0.0"))
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setMinUpdateIntervalMillis(1000L)
                .setWaitForAccurateLocation(true)
                .build()
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    ubicacionViewModel.setLocationAccuracy(location.accuracy)
                    if (location.accuracy <= 30f) { // Precisión aceptable
                        ubicacionViewModel.setStatusMessage("Ubicación obtenida: ${location.latitude},${location.longitude}")
                        ubicacionViewModel.updateLocationData(
                            state.locationData.copy(
                                coordinates = "${location.latitude},${location.longitude}",
                                // Puedes agregar lógica para calcular la distancia si lo necesitas
                            )
                        )
                        fusedLocationClient.removeLocationUpdates(this)
                    } else {
                        ubicacionViewModel.setStatusMessage("Mejorando precisión...")
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, activity?.mainLooper)
        }
    }

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Gestión de Ubicación",
        subtitle = "Configura la ubicación del proyecto",
        icon = Icons.Filled.LocationOn
    ) {
        // Determinar si se debe mostrar la animación Lottie
        val showLottie = state.statusMessage == "Cargando ubicación por GPS..." || state.statusMessage == "Mejorando precisión..."

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
            // Input de dirección
            OutlinedTextField(
                value = state.locationData.address,
                onValueChange = { ubicacionViewModel.updateAddress(it) },
                label = { Text("Dirección del proyecto") },
                placeholder = { Text("Escribe la dirección o espera la ubicación...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de estado SIEMPRE visible
            if (state.statusMessage.isNotBlank()) {
                Text(
                    text = state.statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Mostrar información de ubicación
            if (state.locationData.coordinates.isNotBlank()) {
                LocationInfoCard(
                    address = state.locationData.address,
                    coordinates = state.locationData.coordinates,
                    distance = state.locationData.distance
                )
            } else {
                EmptyStateCard(
                    title = "No hay ubicación configurada",
                    description = "Escribe una dirección o espera la ubicación por GPS.",
                    icon = Icons.Filled.Place
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado de GPS y permisos
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
                            text = "GPS desactivado. Por favor actívalo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun LocationInfoCard(
    address: String,
    coordinates: String,
    distance: String
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
            
            if (distance.isNotBlank() && distance != "0.0") {
                LocationInfoRow("Distancia:", "$distance km")
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