package com.suncar.suncartrabajador.data.schemas

import com.google.gson.annotations.SerializedName
import com.suncar.suncartrabajador.domain.models.TeamMember
import java.time.LocalDate

/**
 * Request schema para el reporte de inversión Contiene todos los datos capturados en el formulario
 * de inversión
 */
data class InversionRequest(
        @SerializedName("tipo_reporte") val tipoReporte: String = "inversion",
        @SerializedName("brigada") val brigada: BrigadaRequest,
        @SerializedName("materiales") val materiales: List<MaterialRequest>,
        @SerializedName("cliente") val cliente: ClienteCreateRequest,
        @SerializedName("fecha_hora") val fechaHora: FechaHoraRequest,
        @SerializedName("adjuntos") val adjuntos: AdjuntosRequest,
        @SerializedName("firma_cliente") val firmaCliente: FirmaClienteRequest? = null,
        @SerializedName("fecha_creacion") val fechaCreacion: String = LocalDate.now().toString(),
        @SerializedName("local_id") val localId: Long = System.currentTimeMillis(),
)

/** Datos de la brigada para el request */
data class BrigadaRequest(
        @SerializedName("lider") val lider: TeamMember,
        @SerializedName("integrantes") val integrantes: List<TeamMember>
)

/** Datos de materiales para el request */
data class MaterialRequest(
        @SerializedName("tipo") val tipo: String,
        @SerializedName("nombre") val nombre: String,
        @SerializedName("cantidad") val cantidad: String,
        @SerializedName("unidad_medida") val unidadMedida: String,
        @SerializedName("codigo_producto") val codigoProducto: String
)

/** Datos de ubicación para el request */
data class ClienteRequest(
        @SerializedName("numero") val numero: String,
)

/** Datos de fecha y hora para el request */
data class FechaHoraRequest(
        @SerializedName("fecha") val fecha: String,
        @SerializedName("hora_inicio") val horaInicio: String,
        @SerializedName("hora_fin") val horaFin: String,
)

/** Datos de adjuntos para el request */
data class AdjuntosRequest(
        @SerializedName("fotos_inicio") val fotosInicio: List<String>, // base64 de las imágenes
        @SerializedName("fotos_fin") val fotosFin: List<String> //  base64 de las imágenes
)

/** Datos de la firma del cliente para el request */
data class FirmaClienteRequest(@SerializedName("firma_base64") val firmaBase64: String? = null)
