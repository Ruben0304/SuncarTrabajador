package com.suncar.suncartrabajador.ui.reportes.Mantenimiento

import com.suncar.suncartrabajador.data.schemas.MantenimientoData
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaState
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState
import com.suncar.suncartrabajador.ui.features.Cliente.ClienteState
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeState
import com.suncar.suncartrabajador.ui.features.Descripcion.DescripcionState

// State principal de la pantalla de Mantenimiento

data class MantenimientoState(
    val adjuntosState: AdjuntosState = AdjuntosState(),
    val brigadaState: BrigadaState = BrigadaState(),
    val materialesState: MaterialesState = MaterialesState(),
    val clienteState: ClienteState = ClienteState(),
    val dateTimeState: DateTimeState = DateTimeState(),
    val descripcionState: DescripcionState = DescripcionState(),
    val isFormValid: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSaving: Boolean = false, // Estado separado para el botón Save
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val responseData: MantenimientoData? = null,
    val showDetailsDialog: Boolean = false
) 