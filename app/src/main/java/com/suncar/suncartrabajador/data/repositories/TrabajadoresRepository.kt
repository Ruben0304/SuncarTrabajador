package com.suncar.suncartrabajador.data.repositories

import android.util.Log
import com.suncar.suncartrabajador.app.service_implementations.TrabajadoresApiServices
import com.suncar.suncartrabajador.domain.models.TeamMember

class TrabajadoresRepository {
    // Configuración de Retrofit
    private val apiService = TrabajadoresApiServices().api

    // Cache para evitar múltiples llamadas
    private var cachedTrabajadores: List<TeamMember>? = null

    suspend fun getTrabajadores(): List<TeamMember> {
        // Usar cache si está disponible
        cachedTrabajadores?.let {
            return it
        }

        return try {
            Log.d("TrabajadoresRepository", "Fetching trabajadores from API")
            val response = apiService.getTrabajadores()
            Log.d(
                    "TrabajadoresRepository",
                    "Received response: success=${response.success}, message=${response.message}"
            )

            if (response.success) {
                val trabajadores = response.data
                Log.d("TrabajadoresRepository", "Received ${trabajadores.size} trabajadores")
                cachedTrabajadores = trabajadores
                trabajadores
            } else {
                Log.e("TrabajadoresRepository", "API returned error: ${response.message}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("TrabajadoresRepository", "Error fetching trabajadores: ${e.message}", e)
            // En caso de error, retornar datos de fallback
            emptyList()
        }
    }

    /**
     * Limpia el cache de trabajadores
     */
    fun clearCache() {
        cachedTrabajadores = null
    }

    /**
     * Refresca los datos desde la API
     */
    suspend fun refresh(): List<TeamMember> {
        clearCache()
        return getTrabajadores()
    }
}