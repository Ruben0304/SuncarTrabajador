package com.suncar.suncartrabajador.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.suncar.suncartrabajador.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUtils {

    /**
     * Genera un nombre único para foto siguiendo el patrón: reporte_{timestamp}_{inicio|fin}_{numero}.jpg
     * @param tipo "inicio" o "fin" dependiendo del tipo de foto
     * @param numero Número secuencial de la foto (1, 2, 3, etc.)
     * @return Nombre único para la foto
     */
    fun generateUniqueImageName(tipo: String, numero: Int): String {
        val timestamp = System.currentTimeMillis()
        return "reporte_${timestamp}_${tipo}_${numero}.jpg"
    }
    
    /**
     * Convierte un Uri de imagen a base64, comprimiendo la imagen para que pese cerca de 100KB
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 100)
     * @return String base64 de la imagen comprimida o null si hay error
     */
    fun uriToBase64Compressed(context: Context, uri: Uri, maxSizeKB: Int = 100): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (bitmap == null) return null

            val outputStream = ByteArrayOutputStream()
            var quality = 90
            var bytes: ByteArray
            do {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                bytes = outputStream.toByteArray()
                quality -= 5
            } while (bytes.size > maxSizeKB * 1024 && quality > 10)

            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convierte un Uri de imagen a base64 usando compresión de forma asíncrona
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 100)
     * @return String base64 de la imagen comprimida o null si hay error
     */
    suspend fun uriToBase64CompressedAsync(context: Context, uri: Uri, maxSizeKB: Int = 100): String? {
        return withContext(Dispatchers.IO) {
            uriToBase64Compressed(context, uri, maxSizeKB)
        }
    }

    /**
     * Convierte una imagen Uri a base64 usando compresión
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @return String base64 de la imagen comprimida o null si hay error
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return uriToBase64Compressed(context, uri)
    }

    /**
     * Convierte una imagen Uri a base64 usando compresión de forma asíncrona
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @return String base64 de la imagen comprimida o null si hay error
     */
    suspend fun uriToBase64Async(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            uriToBase64Compressed(context, uri)
        }
    }

    /**
     * Convierte una lista de Uris a base64 usando compresión
     * @param context Contexto de la aplicación
     * @param uris Lista de Uris de imágenes
     * @return Lista de strings base64 comprimidos
     */
    fun urisToBase64(context: Context, uris: List<Uri>): List<String> {
        return uris.mapNotNull { uri ->
            uriToBase64Compressed(context, uri)
        }
    }

    /**
     * Convierte una lista de Uris a base64 usando compresión de forma asíncrona
     * @param context Contexto de la aplicación
     * @param uris Lista de Uris de imágenes
     * @return Lista de strings base64 comprimidos
     */
    suspend fun urisToBase64Async(context: Context, uris: List<Uri>): List<String> {
        return withContext(Dispatchers.IO) {
            uris.mapNotNull { uri ->
                uriToBase64Compressed(context, uri)
            }
        }
    }

    /**
     * Decodifica un string base64 a Bitmap de forma segura
     */
    fun decodeBase64ToBitmap(base64: String): android.graphics.Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convierte un Uri de imagen a MultipartBody.Part comprimido para envío multipart
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @param partName Nombre del campo en el form-data
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    fun uriToCompressedMultipart(
        context: Context,
        uri: Uri,
        partName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return uriToCompressedMultipartWithName(context, uri, partName, "image.jpg", maxSizeKB)
    }

    /**
     * Convierte un Uri de imagen a MultipartBody.Part comprimido para envío multipart con nombre personalizado
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @param partName Nombre del campo en el form-data
     * @param fileName Nombre del archivo personalizado
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    fun uriToCompressedMultipartWithName(
        context: Context,
        uri: Uri,
        partName: String,
        fileName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (bitmap == null) return null

            val outputStream = ByteArrayOutputStream()
            var quality = 90
            var bytes: ByteArray
            do {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                bytes = outputStream.toByteArray()
                quality -= 5
            } while (bytes.size > maxSizeKB * 1024 && quality > 10)

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convierte un Uri de imagen a MultipartBody.Part comprimido para envío multipart de forma asíncrona
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @param partName Nombre del campo en el form-data
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    suspend fun uriToCompressedMultipartAsync(
        context: Context,
        uri: Uri,
        partName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            uriToCompressedMultipart(context, uri, partName, maxSizeKB)
        }
    }

    /**
     * Convierte un Uri de imagen a MultipartBody.Part comprimido para envío multipart con nombre personalizado de forma asíncrona
     * @param context Contexto de la aplicación
     * @param uri Uri de la imagen
     * @param partName Nombre del campo en el form-data
     * @param fileName Nombre del archivo personalizado
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    suspend fun uriToCompressedMultipartWithNameAsync(
        context: Context,
        uri: Uri,
        partName: String,
        fileName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            uriToCompressedMultipartWithName(context, uri, partName, fileName, maxSizeKB)
        }
    }

    /**
     * Convierte un string base64 a MultipartBody.Part comprimido para envío multipart
     * @param base64 String base64 de la imagen
     * @param partName Nombre del campo en el form-data
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    fun base64ToMultipart(
        base64: String,
        partName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return base64ToMultipartWithName(base64, partName, "image.jpg", maxSizeKB)
    }

    /**
     * Convierte un string base64 a MultipartBody.Part comprimido para envío multipart con nombre personalizado
     * @param base64 String base64 de la imagen
     * @param partName Nombre del campo en el form-data
     * @param fileName Nombre del archivo personalizado
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    fun base64ToMultipartWithName(
        base64: String,
        partName: String,
        fileName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return try {
            val decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            val outputStream = ByteArrayOutputStream()
            var quality = 90
            var bytes: ByteArray
            do {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                bytes = outputStream.toByteArray()
                quality -= 5
            } while (bytes.size > maxSizeKB * 1024 && quality > 10)
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convierte un string base64 a MultipartBody.Part comprimido para envío multipart de forma asíncrona
     * @param base64 String base64 de la imagen
     * @param partName Nombre del campo en el form-data
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    suspend fun base64ToMultipartAsync(
        base64: String,
        partName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            base64ToMultipart(base64, partName, maxSizeKB)
        }
    }

    /**
     * Convierte un string base64 a MultipartBody.Part comprimido para envío multipart con nombre personalizado de forma asíncrona
     * @param base64 String base64 de la imagen
     * @param partName Nombre del campo en el form-data
     * @param fileName Nombre del archivo personalizado
     * @param maxSizeKB Tamaño máximo deseado en KB (por defecto 130)
     * @return MultipartBody.Part listo para enviar o null si hay error
     */
    suspend fun base64ToMultipartWithNameAsync(
        base64: String,
        partName: String,
        fileName: String,
        maxSizeKB: Int = 130
    ): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            base64ToMultipartWithName(base64, partName, fileName, maxSizeKB)
        }
    }
}
