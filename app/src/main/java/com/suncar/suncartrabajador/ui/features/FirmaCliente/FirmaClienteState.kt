package com.suncar.suncartrabajador.ui.features.FirmaCliente

import android.net.Uri

data class FirmaClienteState(
        val signatureUri: Uri? = null,
        val error: String? = null,
        val hasSignature: Boolean = false
)
