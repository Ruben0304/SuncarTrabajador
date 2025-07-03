package com.suncar.suncartrabajador.ui.reportes.Inversion

import com.suncar.suncartrabajador.data.schemas.InversionData
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaState
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState
import com.suncar.suncartrabajador.ui.features.Cliente.ClienteState
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeState

// State principal de la pantalla de Inversión

data class InversionState(
    val adjuntosState: AdjuntosState = AdjuntosState(),
    val brigadaState: BrigadaState = BrigadaState(),
    val materialesState: MaterialesState = MaterialesState(),
    val clienteState: ClienteState = ClienteState(),
    val dateTimeState: DateTimeState = DateTimeState(),
    val isFormValid: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSaving: Boolean = false, // Estado separado para el botón Save
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val responseData: InversionData? = null,
    val showDetailsDialog: Boolean = false
)
