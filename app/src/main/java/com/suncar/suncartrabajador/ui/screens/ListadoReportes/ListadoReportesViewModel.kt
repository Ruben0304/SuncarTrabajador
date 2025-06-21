package com.suncar.suncartrabajador.ui.screens.ListadoReportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.ReportListRepository
import com.suncar.suncartrabajador.domain.models.ReportList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ListadoReportesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ListadoReportesState())
    val uiState: StateFlow<ListadoReportesState> = _uiState.asStateFlow()
    private val repository = ReportListRepository()

    init {
        loadReportList()
    }

    private fun loadReportList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reports = repository.getReportList()
                _uiState.update {
                    it.copy(
                        reports = reports,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshReportList() {
        loadReportList()
    }

    fun sendReport(report: ReportList) {
        viewModelScope.launch {
            // Agregar el reporte a la lista de envío
            _uiState.update { 
                it.copy(
                    sendingReports = it.sendingReports + "${report.fecha}_${report.cliente}"
                )
            }
            
            // Simular envío con delay
            delay(2000) // 2 segundos de delay
            
            // Remover el reporte de la lista y de los que se están enviando
            _uiState.update { currentState ->
                currentState.copy(
                    reports = currentState.reports.filter { 
                        "${it.fecha}_${it.cliente}" != "${report.fecha}_${report.cliente}" 
                    },
                    sendingReports = currentState.sendingReports - "${report.fecha}_${report.cliente}"
                )
            }
        }
    }
} 