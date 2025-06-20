package com.suncar.suncartrabajador.ui.reportes.Inversion

import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaState
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState
import com.suncar.suncartrabajador.ui.features.Ubicacion.UbicacionState

// State principal de la pantalla de Inversión

data class InversionState(
    val adjuntosState: AdjuntosState = AdjuntosState(),
    val brigadaState: BrigadaState = BrigadaState(),
    val materialesState: MaterialesState = MaterialesState(),
    val ubicacionState: UbicacionState = UbicacionState(),
    val isFormValid: Boolean = false,
    val isSubmitting: Boolean = false
)
