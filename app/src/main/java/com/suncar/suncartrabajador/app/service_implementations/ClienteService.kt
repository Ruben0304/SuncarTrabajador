package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.schemas.ClienteUpdateRequest
import com.suncar.suncartrabajador.data.schemas.ClienteUpdateResponse
import com.suncar.suncartrabajador.data.schemas.ClienteVerificacionResponse
import com.suncar.suncartrabajador.data.services.ClienteApiService
import com.suncar.suncartrabajador.domain.models.Cliente
import java.io.IOException

class ClienteService {
    private val clienteApiService: ClienteApiService = RetrofitClient.createService()

    /** Actualiza un cliente existente en el servidor */
    suspend fun actualizarCliente(numero: String, clienteRequest: ClienteUpdateRequest): Result<ClienteUpdateResponse> {
        return try {
            val response = clienteApiService.actualizarCliente(numero, clienteRequest)
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    /** Verifica si existe un cliente por número */
    suspend fun verificarCliente(numero: String): Result<ClienteVerificacionResponse> {
        return try {
            val response = clienteApiService.verificarCliente(numero)
            if (response.success && response.data != null) {
                val clienteVerificacion =
                        ClienteVerificacionResponse(
                                existe = true,
                                nombre = response.data.nombre,
                                direccion = response.data.direccion,
                                mensaje = response.message
                        )
                Result.success(clienteVerificacion)
            } else {
                val clienteVerificacion =
                        ClienteVerificacionResponse(
                                existe = false,
                                nombre = null,
                                direccion = null,
                                mensaje = response.message
                        )
                Result.success(clienteVerificacion)
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    /** Obtiene la lista de clientes del servidor con búsqueda opcional por nombre */
    suspend fun obtenerClientes(nombre: String? = null): Result<List<Cliente>> {
        return try {
            val query = nombre?.takeIf { it.isNotBlank() }
            val clientes = clienteApiService.listarClientes(query)
            val mapped = clientes.mapNotNull { response ->
                val numero = response.numero?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val nombreCliente = response.nombre?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                Cliente(
                    numero = numero,
                    nombre = nombreCliente,
                    direccion = response.direccion
                )
            }
            Result.success(mapped)
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}
