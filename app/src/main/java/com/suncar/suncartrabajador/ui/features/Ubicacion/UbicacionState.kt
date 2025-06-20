package com.suncar.suncartrabajador.ui.features.Ubicacion

import com.suncar.suncartrabajador.domain.models.LocationData

data class UbicacionState(
    val locationData: LocationData = LocationData(),
    val isLoading: Boolean = false,
    val hasGpsPermission: Boolean = false,
    val gpsEnabled: Boolean = false
) 