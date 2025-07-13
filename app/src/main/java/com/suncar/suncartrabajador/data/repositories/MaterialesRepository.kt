package com.suncar.suncartrabajador.data.repositories

import android.util.Log
import com.suncar.suncartrabajador.app.service_implementations.MaterialesApiServices
import com.suncar.suncartrabajador.domain.models.InitialMaterialesData
import com.suncar.suncartrabajador.domain.models.MaterialCategory
import com.suncar.suncartrabajador.domain.models.MaterialProduct

class MaterialesRepository {
    // Configuración de Retrofit
    private val apiService = MaterialesApiServices().api

    // Cache para evitar múltiples llamadas
    private var cachedMaterialTypes: List<MaterialCategory>? = null

    suspend fun getInitialMaterialesData(): InitialMaterialesData {
        return try {
            val categorias = getMaterialTypes()
            InitialMaterialesData(
                    materialTypes = categorias,
                    materialProducts = emptyList() // Los productos se cargan bajo demanda
            )
        } catch (e: Exception) {
            Log.e("MaterialesRepository", "Error getting initial data: ${e.message}", e)
            // En caso de error, retornar datos de fallback
            InitialMaterialesData(materialTypes = emptyList(), materialProducts = emptyList())
        }
    }

    suspend fun getMaterialTypes(): List<MaterialCategory> {
        // Usar cache si está disponible
        cachedMaterialTypes?.let {
            return it
        }

        return try {
            Log.d("MaterialesRepository", "Fetching material types from API")
            val response = apiService.getCategorias()
            Log.d(
                    "MaterialesRepository",
                    "Received response: success=${response.success}, message=${response.message}"
            )

            if (response.success) {
                val categorias = response.data
                Log.d("MaterialesRepository", "Received ${categorias.size} material types")
                cachedMaterialTypes = categorias
                categorias
            } else {
                Log.e("MaterialesRepository", "API returned error: ${response.message}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MaterialesRepository", "Error fetching material types: ${e.message}", e)
            // En caso de error, retornar datos de fallback
            emptyList()
        }
    }

    suspend fun getMaterialProductsByType(categoryId: String): List<MaterialProduct> {
        return try {
            Log.d("MaterialesRepository", "Fetching products for category ID: $categoryId")

            val response = apiService.getMaterialesByCategoria(categoryId)
            Log.d(
                    "MaterialesRepository",
                    "Received response: success=${response.success}, message=${response.message}"
            )

            if (response.success) {
                val productos = response.data
                Log.d(
                        "MaterialesRepository",
                        "Received ${productos.size} products for category ID: $categoryId"
                )
                productos
            } else {
                Log.e("MaterialesRepository", "API returned error: ${response.message}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(
                    "MaterialesRepository",
                    "Error fetching products for category ID $categoryId: ${e.message}",
                    e
            )
            // En caso de error, retornar datos de fallback
            emptyList()
        }
    }
}
