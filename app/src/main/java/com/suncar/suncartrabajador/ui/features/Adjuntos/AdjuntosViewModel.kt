package com.suncar.suncartrabajador.ui.features.Adjuntos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdjuntosViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdjuntosState())
    val uiState: StateFlow<AdjuntosState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simular carga inicial
            kotlinx.coroutines.delay(500)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun addStartAttachment(uri: Uri) {
        _uiState.update { currentState ->
            val newAttachments = currentState.startAttachments + uri
            currentState.copy(startAttachments = newAttachments)
        }
    }

    fun removeStartAttachment(uri: Uri) {
        _uiState.update { currentState ->
            val newAttachments = currentState.startAttachments.filterNot { it == uri }
            currentState.copy(startAttachments = newAttachments)
        }
    }

    fun addEndAttachment(uri: Uri) {
        _uiState.update { currentState ->
            val newAttachments = currentState.endAttachments + uri
            currentState.copy(endAttachments = newAttachments)
        }
    }

    fun removeEndAttachment(uri: Uri) {
        _uiState.update { currentState ->
            val newAttachments = currentState.endAttachments.filterNot { it == uri }
            currentState.copy(endAttachments = newAttachments)
        }
    }

    fun clearAllAttachments() {
        _uiState.update {
            it.copy(
                startAttachments = emptyList(),
                endAttachments = emptyList()
            )
        }
    }
} 