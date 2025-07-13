package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint de reporte de mantenimiento
 * Basado en la estructura del backend de FastAPI
 */
data class MantenimientoResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: MantenimientoData
)

/**
 * Datos del reporte de mantenimiento en la respuesta
 */
data class MantenimientoData(
    @SerializedName("tipo_reporte")
    val tipoReporte: String,
    
    @SerializedName("brigada")
    val brigada: BrigadaResponse,
    
    @SerializedName("materiales")
    val materiales: List<MaterialResponse>,
    
    @SerializedName("ubicacion")
    val ubicacion: UbicacionResponse,
    
    @SerializedName("cliente")
    val cliente: ClienteResponse?,
    
    @SerializedName("fecha_hora")
    val fechaHora: FechaHoraResponse,
    
    @SerializedName("descripcion")
    val descripcion: String,
    
    @SerializedName("adjuntos")
    val adjuntos: AdjuntosResponse
) 