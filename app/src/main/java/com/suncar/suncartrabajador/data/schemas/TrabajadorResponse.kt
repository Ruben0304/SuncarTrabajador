package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.TeamMember

/** Respuesta del endpoint para agregar un trabajador */
data class TrabajadorResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: TeamMember
)
