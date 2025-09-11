package com.suncar.suncartrabajador.ui.features.FirmaCliente

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FirmaClienteViewModel : ViewModel() {
    private val _state = MutableStateFlow(FirmaClienteState())
    val state: StateFlow<FirmaClienteState> = _state

    fun onSignatureChanged(hasSignature: Boolean) {
        _state.update { it.copy(hasSignature = hasSignature, error = null) }
    }

    fun onClearSignature() {
        _state.update { it.copy(hasSignature = false, error = null) }
    }

    fun onError(error: String) {
        _state.update { it.copy(error = error) }
    }



    fun setSignature(uri: Uri) {
        _state.update { it.copy(signatureUri = uri, error = null) }
    }

    fun clearSignature() {
        _state.update { it.copy(signatureUri = null, error = null) }
    }
}
