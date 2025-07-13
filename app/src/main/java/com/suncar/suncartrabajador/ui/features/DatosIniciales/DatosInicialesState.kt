package com.suncar.suncartrabajador.ui.features.DatosIniciales

import com.suncar.suncartrabajador.domain.models.TeamMember

data class DatosInicialesState(
        val isLoading: Boolean = true,
        val hasInternetConnection: Boolean = true,
        val isBlockedByNoInternet: Boolean = false,
        val trabajadores: List<TeamMember> = emptyList(),
        val errorMessage: String? = null,
        val loadingProgress: Float = 0f,
        val currentLoadingStep: String = "Iniciando aplicación..."
)
