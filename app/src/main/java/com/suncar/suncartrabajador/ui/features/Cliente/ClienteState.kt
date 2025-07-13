package com.suncar.suncartrabajador.ui.features.Cliente

import com.suncar.suncartrabajador.domain.models.Cliente
import com.suncar.suncartrabajador.domain.models.LocationData

data class ClienteState(
    val numero: String = "",
    val nombreClienteNuevo: String = "",
    val numeroClienteNuevo: String = "",
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val cliente: Cliente? = null,
    val error: String? = null,
    val esClienteNuevo: Boolean = false,
    val locationData: LocationData = LocationData(),
    val hasGpsPermission: Boolean = false,
    val gpsEnabled: Boolean = false,
    val locationAccuracy: Float? = null,
    val statusMessage: String = "",
    val isGpsMonitoring: Boolean = false
) 