package com.suncar.suncartrabajador.ui.features.Ubicacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.UbicacionRepository
import com.suncar.suncartrabajador.domain.models.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UbicacionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UbicacionState())
    val uiState: StateFlow<UbicacionState> = _uiState.asStateFlow()
    private val repository = UbicacionRepository()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val initialData = repository.getInitialUbicacionData()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    locationData = initialData
                )
            }
        }
    }

    fun updateAddress(address: String) {
        _uiState.update { currentState ->
            currentState.copy(
                locationData = currentState.locationData.copy(address = address)
            )
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentLocation = repository.getCurrentLocation()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    locationData = currentLocation
                )
            }
        }
    }

    fun updateLocationData(locationData: LocationData) {
        _uiState.update { currentState ->
            currentState.copy(locationData = locationData)
        }
    }

    fun setGpsPermission(granted: Boolean) {
        _uiState.update { it.copy(hasGpsPermission = granted) }
    }

    fun setGpsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(gpsEnabled = enabled) }
    }
} 