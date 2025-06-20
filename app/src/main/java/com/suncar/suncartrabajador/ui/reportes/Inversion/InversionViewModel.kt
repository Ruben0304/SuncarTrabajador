package com.suncar.suncartrabajador.ui.reportes.Inversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosValidator
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaState
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaValidator
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesValidator
import com.suncar.suncartrabajador.ui.features.Ubicacion.UbicacionState
import com.suncar.suncartrabajador.ui.features.Ubicacion.UbicacionValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InversionViewModel(
    private val adjuntosValidator: AdjuntosValidator = AdjuntosValidator(),
    private val brigadaValidator: BrigadaValidator = BrigadaValidator(),
    private val materialesValidator: MaterialesValidator = MaterialesValidator(),
    private val ubicacionValidator: UbicacionValidator = UbicacionValidator()
) : ViewModel() {
    private val _uiState = MutableStateFlow(InversionState())
    val uiState: StateFlow<InversionState> = _uiState.asStateFlow()

    fun updateAdjuntos(state: AdjuntosState) {
        _uiState.update { it.copy(adjuntosState = state) }
        validateForm()
    }

    fun updateBrigada(state: BrigadaState) {
        _uiState.update { it.copy(brigadaState = state) }
        validateForm()
    }

    fun updateMateriales(state: MaterialesState) {
        _uiState.update { it.copy(materialesState = state) }
        validateForm()
    }

    fun updateUbicacion(state: UbicacionState) {
        _uiState.update { it.copy(ubicacionState = state) }
        validateForm()
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = adjuntosValidator.isValid(state.adjuntosState)
                && brigadaValidator.isValid(state.brigadaState)
                && materialesValidator.isValid(state.materialesState)
                && ubicacionValidator.isValid(state.ubicacionState)
        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun submitForm() {
        if (_uiState.value.isFormValid && !_uiState.value.isSubmitting) {
            viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true) }
                try {
                    // Simular envío
                    // delay(2000)
                    // Aquí iría la lógica real de envío
                } catch (e: Exception) {
                    // Manejar error
                } finally {
                    _uiState.update { it.copy(isSubmitting = false) }
                }
            }
        }
    }
}

