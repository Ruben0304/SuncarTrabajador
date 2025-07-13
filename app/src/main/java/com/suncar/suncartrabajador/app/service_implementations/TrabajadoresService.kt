package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.services.TrabajadoresApiService
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.singleton.Trabajadores
import java.io.IOException

class TrabajadoresService {
    private val trabajadoresApiService: TrabajadoresApiService = RetrofitClient.createService()

    /** Obtiene la lista de trabajadores desde el servidor */
    suspend fun getTrabajadores(): Result<List<TeamMember>> {
        return try {
            val response = trabajadoresApiService.getTrabajadores()
            if (response.success) {
                // Actualizar el singleton con los trabajadores obtenidos
                Trabajadores.setTrabajadores(response.data)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    /** Agrega un nuevo trabajador al servidor */
    suspend fun addTrabajador(trabajador: TeamMember): Result<TeamMember> {
        return try {
            val response = trabajadoresApiService.addTrabajador(trabajador)
            if (response.success) {
                // Agregar el nuevo trabajador al singleton
                Trabajadores.addTrabajador(response.data)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}
