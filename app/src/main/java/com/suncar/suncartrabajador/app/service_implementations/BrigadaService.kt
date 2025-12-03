package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.services.BrigadaApiService
import com.suncar.suncartrabajador.domain.models.Brigada
import java.io.IOException

class BrigadaService {
    private val brigadaApiService: BrigadaApiService = RetrofitClient.createService()

    /**
     * Obtiene todas las brigadas desde el servidor
     */
    suspend fun getBrigadas(): Result<List<Brigada>> {
        return try {
            val response = brigadaApiService.getBrigadas()
            Result.success(response.data)
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    /**
     * Obtiene la brigada del usuario actual por su CI
     * Busca en todas las brigadas la que tiene como líder al usuario con el CI proporcionado
     * Puede buscar por lider_ci o por lider.id
     */
    suspend fun getBrigadaByUserCi(userCi: String): Result<Brigada?> {
        return try {
            val brigadasResult = getBrigadas()
            if (brigadasResult.isSuccess) {
                val brigadas = brigadasResult.getOrNull() ?: emptyList()
                // Buscar la brigada donde el líder tiene el CI del usuario
                // Puede coincidir con lider_ci o con lider.id
                val brigada = brigadas.find { 
                    it.liderCi == userCi || it.lider.id == userCi 
                }
                Result.success(brigada)
            } else {
                Result.failure(brigadasResult.exceptionOrNull() ?: Exception("Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

