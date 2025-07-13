package com.suncar.suncartrabajador.ui.features.Cliente

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.suncar.suncartrabajador.domain.models.LocationData
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import android.app.Activity
import androidx.annotation.RequiresPermission
import com.suncar.suncartrabajador.app.service_implementations.ClienteService
import com.suncar.suncartrabajador.data.schemas.ClienteVerificacionResponse
import com.suncar.suncartrabajador.domain.models.Cliente

class ClienteViewModel(
    private val service: ClienteService = ClienteService()
) : ViewModel() {
    private val _state = MutableStateFlow(ClienteState())
    private val clienteValidator: ClienteValidator = ClienteValidator()
    val state: StateFlow<ClienteState> = _state

    private var debounceJob: Job? = null
    private var gpsMonitoringJob: Job? = null
    private var locationCallback: LocationCallback? = null

    fun onNumeroChanged(numero: String) {
        val currentState = _state.value.copy(numero = numero)
        val isValid = clienteValidator.isValid(currentState)
        _state.update { it.copy(numero = numero, isValid = isValid, error = null, cliente = null) }
        debounceJob?.cancel()
        if (isValid) {
            debounceJob = viewModelScope.launch {
                delay(1000)
                validarNumeroEnService(numero)
            }
        }
    }

    private fun validarNumeroEnService(numero: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, cliente = null) }
            
            val result = service.verificarCliente(numero)
            
            if (result.isSuccess) {
                val response = result.getOrNull()
                if (response?.existe == true) {
                    // Cliente existe, crear objeto Cliente con los datos recibidos
                    val cliente = Cliente(
                        numero = numero,
                        nombre = response.nombre ?: "",
                        direccion = response.direccion
                    )
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            cliente = cliente, 
                            error = null
                        ) 
                    }
                } else {
                    // Cliente no existe
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            cliente = null, 
                            error = response?.mensaje ?: "Cliente no encontrado"
                        ) 
                    }
                }
            } else {
                // Error en la verificación
                val errorMessage = result.exceptionOrNull()?.message ?: "Error al verificar cliente"
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        cliente = null, 
                        error = errorMessage
                    ) 
                }
            }
        }
    }

    fun setEsClienteNuevo(nuevo: Boolean) {
        val isValid = clienteValidator.isValid(_state.value.copy(esClienteNuevo = nuevo))
        _state.update { it.copy(esClienteNuevo = nuevo, isValid = isValid, error = null) }
    }

    fun onNombreClienteNuevoChanged(nombre: String) {
        val isValid = clienteValidator.isValid(_state.value.copy(nombreClienteNuevo = nombre))
        _state.update { it.copy(nombreClienteNuevo = nombre, isValid = isValid, error = null) }
    }

    fun onNumeroClienteNuevoChanged(numero: String) {
        val isValid = clienteValidator.isValid(_state.value.copy(numeroClienteNuevo = numero))
        _state.update { it.copy(numeroClienteNuevo = numero, isValid = isValid, error = null) }
    }

    fun updateAddress(address: String) {
        _state.update { currentState ->
            currentState.copy(
                locationData = currentState.locationData.copy(address = address)
            )
        }
    }

    fun updateLocationData(locationData: LocationData) {
        _state.update { currentState ->
            currentState.copy(locationData = locationData)
        }
    }

    fun setGpsPermission(granted: Boolean) {
        _state.update { it.copy(hasGpsPermission = granted) }
    }

    fun setGpsEnabled(enabled: Boolean) {
        _state.update { it.copy(gpsEnabled = enabled) }
        if (enabled) stopGpsMonitoring()
    }

    fun setLocationAccuracy(accuracy: Float?) {
        _state.update { it.copy(locationAccuracy = accuracy) }
    }

    fun setStatusMessage(message: String) {
        _state.update { it.copy(statusMessage = message) }
    }

    fun startGpsMonitoring(context: Context) {
        if (_state.value.isGpsMonitoring) return
        _state.update { it.copy(isGpsMonitoring = true) }
        setStatusMessage("Monitoreando GPS... Actívalo para continuar.")
        gpsMonitoringJob = viewModelScope.launch {
            while (true) {
                delay(3000)
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (gpsEnabled) {
                    setGpsEnabled(true)
                    setStatusMessage("¡GPS activado! Obteniendo ubicación...")
                    break
                } else {
                    setStatusMessage("GPS aún desactivado. Verificando en 3 segundos...")
                }
            }
        }
    }

    fun stopGpsMonitoring() {
        gpsMonitoringJob?.cancel()
        gpsMonitoringJob = null
        _state.update { it.copy(isGpsMonitoring = false) }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun onPermissionResult(granted: Boolean, context: Context) {
        setGpsPermission(granted)
        if (granted) {
            setStatusMessage("Permiso concedido. Obteniendo ubicación...")
            checkAndRequestLocation(context, null, null)
        } else {
            setStatusMessage("Permiso de ubicación denegado.")
        }
    }

    fun checkGpsStatus(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        setGpsEnabled(gpsEnabled)
        if (!gpsEnabled) {
            startGpsMonitoring(context)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun checkAndRequestLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient?,
        activity: Activity?
    ) {
        val state = _state.value
        if (state.hasGpsPermission && state.gpsEnabled && (state.locationData.latitud.isBlank() || state.locationData.longitud.isBlank())) {
            setStatusMessage("Cargando ubicación por GPS...")
            setLocationAccuracy(null)
            updateLocationData(state.locationData.copy(latitud = "", longitud = ""))
            val client = fusedLocationClient ?: return
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setMinUpdateIntervalMillis(1000L)
                .setWaitForAccurateLocation(true)
                .build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    setLocationAccuracy(location.accuracy)
                    if (location.accuracy <= 10f) {
                        setStatusMessage("Ubicación obtenida: ${location.latitude},${location.longitude}")
                        updateLocationData(
                            state.locationData.copy(
                                latitud = "${location.latitude}",
                                longitud = "${location.longitude}"
                            )
                        )
                        client.removeLocationUpdates(this)
                    } else {
                        setStatusMessage("Mejorando precisión...")
                    }
                }
            }
            client.requestLocationUpdates(locationRequest, locationCallback!!, activity?.mainLooper)
        }
    }

    fun removeLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
    }

    fun onAddressChange(address: String) {
        updateAddress(address)
    }

    override fun onCleared() {
        super.onCleared()
        stopGpsMonitoring()
    }
} 