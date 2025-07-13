package com.suncar.suncartrabajador.ui.features.Descripcion

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DescripcionViewModel : ViewModel() {
    private val _state = MutableStateFlow(DescripcionState())
    private val descripcionValidator = DescripcionValidator()
    val state: StateFlow<DescripcionState> = _state

    fun onDescripcionChanged(descripcion: String) {
        val isValid = descripcionValidator.validarDescripcion(descripcion)
        _state.update { it.copy(descripcion = descripcion, isValid = isValid, error = if (!isValid && descripcion.isNotBlank()) "Debe tener al menos 10 caracteres" else null) }
    }
} 