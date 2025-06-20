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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionComposable(
    modifier: Modifier = Modifier,
    ubicacionViewModel: UbicacionViewModel = viewModel()
) {
    val state by ubicacionViewModel.uiState.collectAsState()

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Gestión de Ubicación",
        subtitle = "Configura la ubicación del proyecto",
        icon = Icons.Filled.LocationOn,
        isLoading = state.isLoading
    ) {
        // Input de dirección
        OutlinedTextField(
            value = state.locationData.address,
            onValueChange = { ubicacionViewModel.updateAddress(it) },
            label = { Text("Dirección del proyecto") },
            placeholder = { Text("Ingresa la dirección del proyecto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para obtener ubicación actual
        Button(
            onClick = { ubicacionViewModel.getCurrentLocation() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Icon(
                imageVector = Icons.Filled.MyLocation,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Obtener ubicación actual")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar información de ubicación
        if (state.locationData.address.isNotBlank() || state.locationData.coordinates.isNotBlank()) {
            LocationInfoCard(
                address = state.locationData.address,
                coordinates = state.locationData.coordinates,
                distance = state.locationData.distance
            )
        } else {
            EmptyStateCard(
                title = "No hay ubicación configurada",
                description = "Ingresa una dirección o usa el botón para obtener tu ubicación actual.",
                icon = Icons.Filled.Place
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estado de GPS
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