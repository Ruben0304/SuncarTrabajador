package com.suncar.suncartrabajador.app.service_implementations

import com.suncar.suncartrabajador.data.http.RetrofitClient
import com.suncar.suncartrabajador.data.services.InversionApiService
import com.suncar.suncartrabajador.data.schemas.InversionRequest
import com.suncar.suncartrabajador.data.schemas.InversionResponse
import retrofit2.HttpException
import java.io.IOException
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suncar.suncartrabajador.data.schemas.ClienteRequest
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.suncar.suncartrabajador.utils.ImageUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class InversionService {
    private val inversionApiService: InversionApiService = RetrofitClient.createService()

    /**
     * Envía el formulario de inversión al servidor
     */
//    suspend fun enviarInversion(inversionRequest: InversionRequest): Result<InversionResponse> {
//        return try {
//            val respuesta = inversionApiService.enviarInversion(inversionRequest)
//            Result.success(respuesta)
//        } catch (e: HttpException) {
//            // Manejar errores HTTP específicos
//            val errorMessage = when (e.code()) {
//                400 -> "Datos inválidos: Verifique que todos los campos estén correctamente completados"
//                401 -> "No autorizado: Verifique sus credenciales"
//                403 -> "Acceso denegado: No tiene permisos para realizar esta acción"
//                404 -> "Servicio no encontrado: El endpoint no está disponible"
//                422 -> {
//                    // Leer el cuerpo de la respuesta de error
//                    val errorBody = e.response()?.errorBody()?.string()
//                    // Intenta parsear el JSON para mostrar los detalles
//                    val detail = parseValidationError(errorBody)
//                    "Datos de validación incorrectos:\n$detail"
//                }
//                500 -> "Error interno del servidor: Intente nuevamente más tarde"
//                502 -> "Servidor no disponible: El servicio está temporalmente fuera de línea"
//                503 -> "Servicio temporalmente no disponible: Intente nuevamente más tarde"
//                else -> "Error del servidor (${e.code()}): ${e.message()}"
//            }
//            Result.failure(Exception(errorMessage))
//        } catch (e: IOException) {
//            Result.failure(Exception("Error de conexión: Verifique su conexión a internet"))
//        } catch (e: Exception) {
//            Result.failure(Exception("Error inesperado: ${e.message}"))
//        }
//    }

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

    /**
     * Guarda localmente la inversión en un archivo JSON (lista de inversiones no enviadas)
     */
    fun guardarInversionLocal(context: Context, inversionRequest: InversionRequest) {
        val gson = Gson()
        val inversiones = leerInversionesLocales(context)
        inversiones.add(inversionRequest)
        val json = gson.toJson(inversiones)
        val file = File(context.filesDir, ARCHIVO_INVERSIONES_PENDIENTES)
        file.writeText(json)
    }

    /**
     * Lee la lista de inversiones locales (no enviadas) desde el archivo JSON
     */
    private fun leerInversionesLocales(context: Context): MutableList<InversionRequest> {
        val file = File(context.filesDir, ARCHIVO_INVERSIONES_PENDIENTES)
        if (!file.exists()) return mutableListOf()
        val json = file.readText()
        return try {
            val gson = Gson()
            val type = object : TypeToken<MutableList<InversionRequest>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    /**
     * Envía el formulario de inversión al servidor usando multipart/form-data
     */
    suspend fun enviarInversionMultipart(
        tipoReporte: RequestBody,
        brigada: RequestBody,
        materiales: RequestBody,
        cliente: RequestBody,
        fechaHora: RequestBody,
        fotosInicio: List<MultipartBody.Part>,
        fotosFin: List<MultipartBody.Part>
    ): InversionResponse {
        try {
            return inversionApiService.enviarInversion(
                tipoReporte,
                brigada,
                materiales,
                cliente,
                fechaHora,
                fotosInicio,
                fotosFin
            )
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos: Verifique que todos los campos estén correctamente completados"
                401 -> "No autorizado: Verifique sus credenciales"
                403 -> "Acceso denegado: No tiene permisos para realizar esta acción"
                404 -> "Servicio no encontrado: El endpoint no está disponible"
                422 -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    val detail = parseValidationError(errorBody)
                    "Datos de validación incorrectos:\n$detail"
                }
                500 -> "Error interno del servidor: Intente nuevamente más tarde"
                502 -> "Servidor no disponible: El servicio está temporalmente fuera de línea"
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
     * Envía un InversionRequest local (con imágenes en base64) al backend usando multipart/form-data
     */
    suspend fun enviarInversionLocalMultipart(request: InversionRequest): Result<InversionResponse> {
        return try {
            val clienteNumero = ClienteRequest(request.cliente.numero)
            val tipoReporteBody = request.tipoReporte.toRequestBody("text/plain".toMediaTypeOrNull())
            val gson = Gson()
            val brigadaBody = gson.toJson(request.brigada).toRequestBody("application/json".toMediaTypeOrNull())
            val materialesBody = gson.toJson(request.materiales).toRequestBody("application/json".toMediaTypeOrNull())
            val clienteBody = gson.toJson(clienteNumero).toRequestBody("application/json".toMediaTypeOrNull())
            val fechaHoraBody = gson.toJson(request.fechaHora).toRequestBody("application/json".toMediaTypeOrNull())
            
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
            
            val response = enviarInversionMultipart(
                tipoReporteBody,
                brigadaBody,
                materialesBody,
                clienteBody,
                fechaHoraBody,
                fotosInicioParts,
                fotosFinParts
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val ARCHIVO_INVERSIONES_PENDIENTES = "inversiones_pendientes.json"
    }
}
