package com.suncar.suncartrabajador.app.service_implementations

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.schemas.AveriaRequest
import com.suncar.suncartrabajador.data.schemas.AveriaResponse
import com.suncar.suncartrabajador.data.schemas.ClienteRequest
import com.suncar.suncartrabajador.data.services.AveriaApiService
import com.suncar.suncartrabajador.utils.ImageUtils
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AveriaService {
    private val averiaApiService: AveriaApiService = RetrofitClient.createService()

    private fun parseValidationError(errorBody: String?): String {
        if (errorBody == null) return "Error desconocido"
        return try {
            val json = org.json.JSONObject(errorBody)
            val errors = json.optJSONArray("errors")
            if (errors != null) {
                (0 until errors.length()).joinToString("\n") { i ->
                    val err = errors.getJSONObject(i)
                    val field = err.optString("field")
                    val error = err.optString("error")
                    val value = err.optString("value")
                    "$field: $error (Valor recibido: $value)"
                }
            } else {
                json.optString("message", "Error de validación")
            }
        } catch (e: Exception) {
            "Error de validación: $errorBody"
        }
    }

    /** Guarda localmente la avería en un archivo JSON (lista de averías no enviadas) */
    fun guardarAveriaLocal(context: Context, averiaRequest: AveriaRequest) {
        val gson = Gson()
        val averias = leerAveriasLocales(context)
        averias.add(averiaRequest)
        val json = gson.toJson(averias)
        val file = File(context.filesDir, ARCHIVO_AVERIAS_PENDIENTES)
        file.writeText(json)
    }

    /** Lee la lista de averías locales (no enviadas) desde el archivo JSON */
    private fun leerAveriasLocales(context: Context): MutableList<AveriaRequest> {
        val file = File(context.filesDir, ARCHIVO_AVERIAS_PENDIENTES)
        if (!file.exists()) return mutableListOf()
        val json = file.readText()
        return try {
            val gson = Gson()
            val type = object : TypeToken<MutableList<AveriaRequest>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    /** Envía el formulario de avería al servidor usando multipart/form-data */
    suspend fun enviarAveriaMultipart(
            tipoReporte: RequestBody,
            brigada: RequestBody,
            materiales: RequestBody,
            cliente: RequestBody,
            fechaHora: RequestBody,
            descripcion: RequestBody,
            fotosInicio: List<MultipartBody.Part>,
            fotosFin: List<MultipartBody.Part>,
            firmaCliente: MultipartBody.Part?
    ): AveriaResponse {
        try {
            return averiaApiService.enviarAveria(
                    tipoReporte,
                    brigada,
                    materiales,
                    cliente,
                    fechaHora,
                    descripcion,
                    fotosInicio,
                    fotosFin,
                    firmaCliente
            )
        } catch (e: HttpException) {
            val errorMessage =
                    when (e.code()) {
                        400 ->
                                "Datos inválidos: Verifique que todos los campos estén correctamente completados"
                        401 -> "No autorizado: Verifique sus credenciales"
                        403 -> "Acceso denegado: No tiene permisos para realizar esta acción"
                        404 -> "Servicio no encontrado: El endpoint no está disponible"
                        422 -> {
                            val errorBody = e.response()?.errorBody()?.string()
                            val detail = parseValidationError(errorBody)
                            "Datos de validación incorrectos:\n$detail"
                        }
                        500 -> "Error interno del servidor: Intente nuevamente más tarde"
                        502 ->
                                "Servidor no disponible: El servicio está temporalmente fuera de línea"
                        503 -> "Servicio temporalmente no disponible: Intente nuevamente más tarde"
                        else -> "Error del servidor (${e.code()}): ${e.message()}"
                    }
            throw Exception(errorMessage)
        } catch (e: IOException) {
            throw Exception("Error de conexión: Verifique su conexión a internet")
        } catch (e: Exception) {
            throw Exception("Error inesperado: ${e.message}")
        }
    }

    /**
     * Envía un AveriaRequest local (con imágenes en base64) al backend usando multipart/form-data
     */
    suspend fun enviarAveriaLocalMultipart(request: AveriaRequest): Result<AveriaResponse> {
        return try {
            val clienteNumero = ClienteRequest(request.cliente.numero)
            val tipoReporteBody =
                    request.tipoReporte.toRequestBody("text/plain".toMediaTypeOrNull())
            val gson = Gson()
            val brigadaBody =
                    gson.toJson(request.brigada)
                            .toRequestBody("application/json".toMediaTypeOrNull())
            val materialesBody =
                    gson.toJson(request.materiales)
                            .toRequestBody("application/json".toMediaTypeOrNull())
            val clienteBody =
                    gson.toJson(clienteNumero).toRequestBody("application/json".toMediaTypeOrNull())
            val fechaHoraBody =
                    gson.toJson(request.fechaHora)
                            .toRequestBody("application/json".toMediaTypeOrNull())
            val descripcionBody =
                    request.descripcion.toRequestBody("text/plain".toMediaTypeOrNull())

            // Usar versiones asíncronas para evitar bloquear la UI
            val fotosInicioParts = mutableListOf<MultipartBody.Part>()
            for (base64 in request.adjuntos.fotosInicio) {
                val part = ImageUtils.base64ToMultipartAsync(base64, "fotos_inicio")
                if (part != null) {
                    fotosInicioParts.add(part)
                }
            }

            val fotosFinParts = mutableListOf<MultipartBody.Part>()
            for (base64 in request.adjuntos.fotosFin) {
                val part = ImageUtils.base64ToMultipartAsync(base64, "fotos_fin")
                if (part != null) {
                    fotosFinParts.add(part)
                }
            }

            // Preparar firma cliente como multipart
            var firmaClientePart: MultipartBody.Part? = null
            request.firmaCliente?.firmaBase64?.let { base64Firma ->
                firmaClientePart = ImageUtils.base64ToMultipartAsync(base64Firma, "firma_cliente")
            }

            val response =
                    enviarAveriaMultipart(
                            tipoReporteBody,
                            brigadaBody,
                            materialesBody,
                            clienteBody,
                            fechaHoraBody,
                            descripcionBody,
                            fotosInicioParts,
                            fotosFinParts,
                            firmaClientePart
                    )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val ARCHIVO_AVERIAS_PENDIENTES = "averias_pendientes.json"
    }
}
