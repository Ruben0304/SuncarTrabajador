package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.MaterialProduct

/**
 * Respuesta del endpoint de productos por categoría Basado en la estructura del backend de FastAPI
 */
data class ProductoListResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: List<MaterialProduct>
)
