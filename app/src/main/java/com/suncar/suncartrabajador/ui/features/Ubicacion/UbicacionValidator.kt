package com.suncar.suncartrabajador.ui.features.Ubicacion

class UbicacionValidator {
    fun isValid(state: UbicacionState): Boolean {
        return state.locationData.coordinates.isNotBlank() &&
                state.locationData.address.isNotBlank()
    }
} 