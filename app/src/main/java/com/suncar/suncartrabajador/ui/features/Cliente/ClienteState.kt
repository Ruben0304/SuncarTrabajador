package com.suncar.suncartrabajador.ui.features.Cliente

import com.suncar.suncartrabajador.domain.models.Cliente
import com.google.android.gms.maps.model.LatLng

data class ClienteState(
    val numero: String = "",
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val cliente: Cliente? = null,
    val nombreBusqueda: String = "",
    val clientesSugeridos: List<Cliente> = emptyList(),
    val mostrarSugerencias: Boolean = false,
    val isBuscandoClientes: Boolean = false,
    val mensajeBusqueda: String? = null,
    val esErrorBusqueda: Boolean = false,
    val error: String? = null,
    val mostrarMapaParaUbicacion: Boolean = false,
    val ubicacionSeleccionada: LatLng? = null,
    val isActualizandoUbicacion: Boolean = false,
    val mensajeActualizacion: String? = null
) 
