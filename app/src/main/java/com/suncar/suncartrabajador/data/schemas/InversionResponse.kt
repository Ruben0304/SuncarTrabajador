package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint de reporte de inversión
 * Basado en la estructura del backend de FastAPI
 */
data class InversionResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: InversionData
)

/**
 * Datos del reporte de inversión en la respuesta
 */
data class InversionData(
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
    
    @SerializedName("adjuntos")
    val adjuntos: AdjuntosResponse
)

/**
 * Datos de la brigada en la respuesta
 */
data class BrigadaResponse(
    @SerializedName("lider")
    val lider: TeamMemberResponse,
    
    @SerializedName("integrantes")
    val integrantes: List<TeamMemberResponse>
)

/**
 * Datos de un miembro del equipo en la respuesta
 */
data class TeamMemberResponse(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("CI")
    val ci: String
)

/**
 * Datos de materiales en la respuesta
 */
data class MaterialResponse(
    @SerializedName("tipo")
    val tipo: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("cantidad")
    val cantidad: String,
    
    @SerializedName("unidad_medida")
    val unidadMedida: String,
    
    @SerializedName("codigo_producto")
    val codigoProducto: String
)

/**
 * Datos de ubicación en la respuesta
 */
data class UbicacionResponse(
    @SerializedName("direccion")
    val direccion: String,
    
    @SerializedName("latitud")
    val latitud: String,
    
    @SerializedName("longitud")
    val longitud: String
)

/**
 * Datos de fecha y hora en la respuesta
 */
data class FechaHoraResponse(
    @SerializedName("fecha")
    val fecha: String,
    
    @SerializedName("hora_inicio")
    val horaInicio: String,
    
    @SerializedName("hora_fin")
    val horaFin: String
)

/**
 * Datos de adjuntos en la respuesta
 */
data class AdjuntosResponse(
    @SerializedName("fotos_inicio")
    val fotosInicio: List<String>,
    
    @SerializedName("fotos_fin")
    val fotosFin: List<String>
)

/**
 * Datos de cliente en la respuesta
 */
data class ClienteResponse(
    @SerializedName("numero")
    val numero: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("direccion")
    val direccion: String?
) 