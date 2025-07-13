package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.MaterialCategory

/**
 * Respuesta del endpoint de categorías de productos Basado en la estructura del backend de FastAPI
 */
data class CategoriaListResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: List<MaterialCategory>
)
