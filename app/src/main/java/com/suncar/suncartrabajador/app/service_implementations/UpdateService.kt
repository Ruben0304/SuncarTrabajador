package com.suncar.suncartrabajador.app.service_implementations

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.suncar.suncartrabajador.data.services.UpdateApiService
import com.suncar.suncartrabajador.domain.models.UpdateInfo
import com.suncar.suncartrabajador.domain.models.UpdateRequest
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class UpdateService(private val updateApiService: UpdateApiService, private val context: Context) {

    suspend fun checkForUpdates(currentVersion: String?): UpdateInfo {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateRequest(currentVersion = currentVersion, platform = "android")
                updateApiService.checkForUpdates(request)
            } catch (e: Exception) {
                // Si hay error en la verificación, asumimos que la app está actualizada
                UpdateInfo(
                        isUpToDate = false,
                        latestVersion = currentVersion,
                        downloadUrl = "",
                        fileSize = 0,
                        changelog = "Error" + e.message,
                        forceUpdate = false
                )
            }
        }
    }

    suspend fun downloadUpdate(
        downloadUrl: String,
        fileName: String,
        onProgressUpdate: (Float) -> Unit
    ): File? {
        return withContext(Dispatchers.IO) {
            try {
                val client =
                        OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS)
                                .build()
                val request = Request.Builder().url(downloadUrl).build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext null
                }

                val downloadsDir =
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                        )
                val file = File(downloadsDir, fileName)

                response.body?.let { body ->
                    val totalBytes = body.contentLength()
                    val inputStream = body.byteStream()
                    val outputStream = FileOutputStream(file)

                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    inputStream.use { input ->
                        outputStream.use { output ->
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead

                                if (totalBytes > 0) {
                                    val progress = (totalBytesRead.toFloat() / totalBytes.toFloat())
                                    onProgressUpdate(progress)
                                }
                            }
                        }
                    }
                }

                file
            } catch (e: IOException) {
                null
            }
        }
    }

    fun installUpdate(apkFile: File): Boolean {
        return try {
            if (!apkFile.exists() || !apkFile.canRead()) {
                return false
            }

            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun formatFileSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        return "%.1f %s".format(size, units[unitIndex])
    }
}
