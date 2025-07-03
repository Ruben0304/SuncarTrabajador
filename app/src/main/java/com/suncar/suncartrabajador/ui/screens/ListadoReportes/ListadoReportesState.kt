package com.suncar.suncartrabajador.ui.screens.ListadoReportes

import com.suncar.suncartrabajador.domain.models.ReportList

data class ListadoReportesState(
    val reports: List<ReportList> = emptyList(),
    val isLoading: Boolean = false,
    val sendingReports: Set<String> = emptySet(), // Para rastrear reportes que se están enviando
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false
) 