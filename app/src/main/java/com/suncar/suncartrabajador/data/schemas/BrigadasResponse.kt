package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.Brigada

/**
 * Respuesta del endpoint de brigadas
 * Estructura: { "data": Brigada[] }
 */
data class BrigadasResponse(
    @SerializedName("data")
    val data: List<Brigada>
)

