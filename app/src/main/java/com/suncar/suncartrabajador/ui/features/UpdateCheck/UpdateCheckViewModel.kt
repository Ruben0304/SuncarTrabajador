package com.suncar.suncartrabajador.ui.features.UpdateCheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.app.service_implementations.UpdateService
import com.suncar.suncartrabajador.domain.models.UpdateInfo
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateCheckViewModel(private val updateService: UpdateService) : ViewModel() {

    private val _state = MutableStateFlow(UpdateCheckState())
    val state: StateFlow<UpdateCheckState> = _state.asStateFlow()

    fun checkForUpdates(currentVersion: String?, onUpdateCheckComplete: (UpdateInfo) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val updateInfo = updateService.checkForUpdates(currentVersion)
                _state.update { it.copy(isLoading = false, updateInfo = updateInfo) }

                if (!updateInfo.isUpToDate) {
                    _state.update { it.copy(showUpdateDialog = true) }
                } else {
                    onUpdateCheckComplete(updateInfo)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                            isLoading = false,
                            error = "Error al verificar actualizaciones: ${e.message}"
                    )
                }
                // En caso de error, continuar con la carga normal
                onUpdateCheckComplete(
                        UpdateInfo(
                                isUpToDate = true,
                                latestVersion = currentVersion,
                                downloadUrl = "",
                                fileSize = 0,
                                changelog = "",
                                forceUpdate = false
                        )
                )
            }
        }
    }

    fun downloadUpdate(onDownloadComplete: (File?) -> Unit) {
        val updateInfo = state.value.updateInfo ?: return

        viewModelScope.launch {
            _state.update { it.copy(isDownloading = true, downloadProgress = 0f) }

            try {
                val fileName = "SuncarTrabajador-${updateInfo.latestVersion}.apk"
                val downloadedFile = updateService.downloadUpdate(updateInfo.downloadUrl, fileName)

                _state.update { it.copy(isDownloading = false, downloadProgress = 1f) }

                if (downloadedFile != null) {
                    updateService.installUpdate(downloadedFile)
                }

                onDownloadComplete(downloadedFile)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                            isDownloading = false,
                            error = "Error al descargar la actualización: ${e.message}"
                    )
                }
                onDownloadComplete(null)
            }
        }
    }

    fun dismissUpdateDialog() {
        _state.update { it.copy(showUpdateDialog = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
