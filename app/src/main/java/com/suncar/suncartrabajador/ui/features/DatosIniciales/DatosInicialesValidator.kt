package com.suncar.suncartrabajador.ui.features.DatosIniciales

class DatosInicialesValidator {
    fun isReadyToContinue(state: DatosInicialesState): Boolean {
        return !state.isLoading && state.trabajadores.isNotEmpty() && !state.isBlockedByNoInternet
    }

    fun hasMinimumData(state: DatosInicialesState): Boolean {
        return state.trabajadores.isNotEmpty() && !state.isBlockedByNoInternet
    }

    fun isBlockedByNoInternet(state: DatosInicialesState): Boolean {
        return state.isBlockedByNoInternet
    }
}
