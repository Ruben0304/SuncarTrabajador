package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.schemas.ClienteCreateRequest
import com.suncar.suncartrabajador.data.schemas.ClienteCreateResponse
import com.suncar.suncartrabajador.data.schemas.ClienteVerificacionResponse
import com.suncar.suncartrabajador.data.services.ClienteApiService
import com.suncar.suncartrabajador.domain.models.Cliente
import java.io.IOException

class ClienteService {
    private val clienteApiService: ClienteApiService = RetrofitClient.createService()

    /** Crea un nuevo cliente en el servidor */
    suspend fun crearCliente(clienteRequest: ClienteCreateRequest): Result<ClienteCreateResponse> {
        return try {
            val cliente = clienteApiService.crearCliente(clienteRequest)
            Result.success(cliente)
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
}
