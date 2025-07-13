package com.suncar.suncartrabajador.ui.features.UpdateCheck

import com.suncar.suncartrabajador.domain.models.UpdateInfo

data class UpdateCheckState(
        val isLoading: Boolean = false,
        val updateInfo: UpdateInfo? = null,
        val showUpdateDialog: Boolean = false,
        val isDownloading: Boolean = false,
        val downloadProgress: Float = 0f,
        val error: String? = null
)
