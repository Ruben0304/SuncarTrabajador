package com.suncar.suncartrabajador.ui.features.Cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.suncar.suncartrabajador.app.service_implementations.ClienteService
import com.suncar.suncartrabajador.data.schemas.ClienteUpdateRequest
import com.suncar.suncartrabajador.domain.models.Cliente
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClienteViewModel(
    private val service: ClienteService = ClienteService()
) : ViewModel() {
    private val _state = MutableStateFlow(ClienteState())
    private val clienteValidator: ClienteValidator = ClienteValidator()
    val state: StateFlow<ClienteState> = _state

    private var debounceJob: Job? = null
    private var buscarClientesJob: Job? = null

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

    fun onNombreBusquedaChanged(nombre: String) {
        buscarClientesJob?.cancel()
        _state.update {
            it.copy(
                nombreBusqueda = nombre,
                mensajeBusqueda = null,
                esErrorBusqueda = false,
                clientesSugeridos = emptyList(),
                mostrarSugerencias = false
            )
        }

        if (nombre.trim().length < 2) {
            _state.update { it.copy(isBuscandoClientes = false) }
            return
        }

        buscarClientesJob = viewModelScope.launch {
            delay(300)
            val query = nombre.trim()
            _state.update { it.copy(isBuscandoClientes = true) }
            val result = service.obtenerClientes(query)
            if (result.isSuccess) {
                val clientes = result.getOrDefault(emptyList())
                _state.update {
                    it.copy(
                        clientesSugeridos = clientes,
                        mostrarSugerencias = clientes.isNotEmpty(),
                        isBuscandoClientes = false,
                        mensajeBusqueda = if (clientes.isEmpty()) "No se encontraron clientes" else null,
                        esErrorBusqueda = false
                    )
                }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error al buscar clientes"
                _state.update {
                    it.copy(
                        isBuscandoClientes = false,
                        mensajeBusqueda = errorMessage,
                        esErrorBusqueda = true,
                        clientesSugeridos = emptyList(),
                        mostrarSugerencias = false
                    )
                }
            }
        }
    }

    fun onSugerenciasExpandedChange(expanded: Boolean) {
        _state.update { current ->
            if (!expanded) {
                current.copy(mostrarSugerencias = false)
            } else if (current.clientesSugeridos.isNotEmpty()) {
                current.copy(mostrarSugerencias = true)
            } else {
                current
            }
        }
    }

    fun onClienteSugerenciaSeleccionado(cliente: Cliente) {
        buscarClientesJob?.cancel()
        debounceJob?.cancel()
        _state.update {
            it.copy(
                nombreBusqueda = cliente.nombre,
                numero = cliente.numero,
                clientesSugeridos = emptyList(),
                mostrarSugerencias = false,
                mensajeBusqueda = null,
                esErrorBusqueda = false
            )
        }
        onNumeroChanged(cliente.numero)
    }

    fun onEstablecerUbicacionClick() {
        _state.update { it.copy(mostrarMapaParaUbicacion = true) }
    }

    fun onCerrarMapa() {
        _state.update { it.copy(mostrarMapaParaUbicacion = false) }
    }

    fun onUbicacionSeleccionada(latLng: LatLng) {
        _state.update {
            it.copy(
                ubicacionSeleccionada = latLng,
                mostrarMapaParaUbicacion = false
            )
        }
        actualizarUbicacionCliente(latLng)
    }

    private fun actualizarUbicacionCliente(latLng: LatLng) {
        val numeroCliente = _state.value.numero
        if (numeroCliente.isBlank()) {
            _state.update { it.copy(mensajeActualizacion = "Error: No hay cliente seleccionado") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isActualizandoUbicacion = true, mensajeActualizacion = null) }

            val updateRequest = ClienteUpdateRequest(
                latitud = latLng.latitude,
                longitud = latLng.longitude
            )

            val result = service.actualizarCliente(numeroCliente, updateRequest)

            if (result.isSuccess) {
                val response = result.getOrNull()
                _state.update {
                    it.copy(
                        isActualizandoUbicacion = false,
                        mensajeActualizacion = response?.message ?: "Ubicación actualizada correctamente"
                    )
                }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar ubicación"
                _state.update {
                    it.copy(
                        isActualizandoUbicacion = false,
                        mensajeActualizacion = errorMessage
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        debounceJob?.cancel()
        buscarClientesJob?.cancel()
    }
}
