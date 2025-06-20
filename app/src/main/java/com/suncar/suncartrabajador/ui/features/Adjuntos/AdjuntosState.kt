package com.suncar.suncartrabajador.ui.features.Adjuntos

import android.net.Uri

data class AdjuntosState(
    val startAttachments: List<Uri> = emptyList(),
    val endAttachments: List<Uri> = emptyList(),
    val isLoading: Boolean = false
) 