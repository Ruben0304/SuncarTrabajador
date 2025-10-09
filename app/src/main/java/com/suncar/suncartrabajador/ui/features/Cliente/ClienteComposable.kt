package com.suncar.suncartrabajador.ui.features.Cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.components.CustomCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteComposable(
    modifier: Modifier = Modifier,
    clienteViewModel: ClienteViewModel = viewModel()
) {
    val state by clienteViewModel.state.collectAsState()

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Cliente",
        subtitle = "Busca y selecciona un cliente",
        icon = Icons.Filled.Person,
        isLoading = state.isLoading
    ) {
        // Campo de búsqueda por nombre
        ExposedDropdownMenuBox(
            expanded = state.mostrarSugerencias,
            onExpandedChange = { expanded ->
                clienteViewModel.onSugerenciasExpandedChange(expanded)
            }
        ) {
            OutlinedTextField(
                value = state.nombreBusqueda,
                onValueChange = { clienteViewModel.onNombreBusquedaChanged(it) },
                label = { Text("Buscar cliente por nombre") },
                placeholder = { Text("Ej: Ernesto") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (state.isBuscandoClientes) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.mostrarSugerencias)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = state.mostrarSugerencias,
                onDismissRequest = { clienteViewModel.onSugerenciasExpandedChange(false) }
            ) {
                state.clientesSugeridos.forEach { cliente ->
                    DropdownMenuItem(
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = cliente.nombre,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Número: ${cliente.numero}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                cliente.direccion?.takeIf { it.isNotBlank() }?.let { direccion ->
                                    Text(
                                        text = direccion,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        onClick = { clienteViewModel.onClienteSugerenciaSeleccionado(cliente) }
                    )
                }
            }
        }

        if (state.mensajeBusqueda != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.mensajeBusqueda ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = if (state.esErrorBusqueda) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de número de cliente
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

        // Información del cliente encontrado
        if (state.cliente != null) {
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

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para establecer ubicación
            Button(
                onClick = { clienteViewModel.onEstablecerUbicacionClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isActualizandoUbicacion
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isActualizandoUbicacion) "Actualizando..." else "Establecer Ubicación"
                )
            }

            // Mensaje de actualización de ubicación
            if (state.mensajeActualizacion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.mensajeActualizacion ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Mostrar ubicación seleccionada si existe
            if (state.ubicacionSeleccionada != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Ubicación Seleccionada",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lat: ${state.ubicacionSeleccionada!!.latitude}, Lng: ${state.ubicacionSeleccionada!!.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }

    // Mostrar Google Maps cuando sea necesario
    if (state.mostrarMapaParaUbicacion) {
        GoogleMapsLocationPicker(
            onLocationSelected = { latLng ->
                clienteViewModel.onUbicacionSeleccionada(latLng)
            },
            onDismiss = {
                clienteViewModel.onCerrarMapa()
            }
        )
    }
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
