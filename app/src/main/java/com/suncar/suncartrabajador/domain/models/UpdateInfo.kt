package com.suncar.suncartrabajador.domain.models

import com.google.gson.annotations.SerializedName

data class UpdateInfo(
        @SerializedName("is_up_to_date") val isUpToDate: Boolean,
        @SerializedName("latest_version") val latestVersion: String?,
        @SerializedName("download_url") val downloadUrl: String,
        @SerializedName("file_size") val fileSize: Long,
        @SerializedName("changelog") val changelog: String,
        @SerializedName("force_update") val forceUpdate: Boolean
)

data class UpdateRequest(
        @SerializedName("current_version") val currentVersion: String?,
        @SerializedName("platform") val platform: String = "android"
)
