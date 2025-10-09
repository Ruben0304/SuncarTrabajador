package com.suncar.suncartrabajador.ui.screens.ListadoReportes

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.suncar.suncartrabajador.data.schemas.InversionRequest
import com.suncar.suncartrabajador.data.schemas.InversionResponse
import com.suncar.suncartrabajador.app.service_implementations.InversionService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suncar.suncartrabajador.app.service_implementations.AveriaService
import com.suncar.suncartrabajador.app.service_implementations.ClienteService
import com.suncar.suncartrabajador.app.service_implementations.MantenimientoService
import com.suncar.suncartrabajador.data.schemas.AveriaRequest
import com.suncar.suncartrabajador.data.schemas.AveriaResponse
import com.suncar.suncartrabajador.data.schemas.ClienteCreateRequest
import com.suncar.suncartrabajador.data.schemas.MantenimientoRequest
import com.suncar.suncartrabajador.data.schemas.MantenimientoResponse
import java.io.File

class ListadoReportesViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ListadoReportesState())
    val uiState: StateFlow<ListadoReportesState> = _uiState.asStateFlow()
    private val repository = ReportListRepository()
    private val clienteService: ClienteService = ClienteService()
    @SuppressLint("StaticFieldLeak")
    private val context: Context = getApplication<Application>().applicationContext

    init {
        loadReportList()
    }

    private fun loadReportList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val localReports = getLocalReports()
                _uiState.update {
                    it.copy(
                        reports = localReports,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun getLocalReports(): List<ReportList> {
        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val reports = mutableListOf<ReportList>()

            // Leer inversiones
            val inversionesFile = File(context.filesDir, "inversiones_pendientes.json")
            if (inversionesFile.exists()) {
                try {
                    val json = inversionesFile.readText()
                    val inversionType = object : TypeToken<List<InversionRequest>>() {}.type
                    val inversiones: List<InversionRequest> = gson.fromJson(json, inversionType) ?: emptyList()
                    reports.addAll(inversiones.map {
                        ReportList(
                            img = it.adjuntos.fotosFin.firstOrNull() ?: "",
                            fecha = it.fechaHora.fecha,
                            cliente = it.cliente.direccion ?: it.cliente.numero,
                            tipo = it.tipoReporte,
                            id = it.localId.toInt()
                        )
                    })
                } catch (e: Exception) {
                    // Si hay error al leer inversiones, continuar
                }
            }

            // Leer averías
            val averiasFile = File(context.filesDir, "averias_pendientes.json")
            if (averiasFile.exists()) {
                try {
                    val json = averiasFile.readText()
                    val averiaType = object : TypeToken<List<AveriaRequest>>() {}.type
                    val averias: List<AveriaRequest> = gson.fromJson(json, averiaType) ?: emptyList()
                    reports.addAll(averias.map {
                        ReportList(
                            img = it.adjuntos.fotosFin.firstOrNull() ?: "",
                            fecha = it.fechaHora.fecha,
                            cliente = it.cliente.direccion ?: it.cliente.numero,
                            tipo = it.tipoReporte,
                            id = it.localId.toInt()
                        )
                    })
                } catch (e: Exception) {
                    // Si hay error al leer averías, continuar
                }
            }

            // Leer mantenimientos
            val mantenimientosFile = File(context.filesDir, "mantenimientos_pendientes.json")
            if (mantenimientosFile.exists()) {
                try {
                    val json = mantenimientosFile.readText()
                    val mantenimientoType = object : TypeToken<List<MantenimientoRequest>>() {}.type
                    val mantenimientos: List<MantenimientoRequest> = gson.fromJson(json, mantenimientoType) ?: emptyList()
                    reports.addAll(mantenimientos.map {
                        ReportList(
                            img = it.adjuntos.fotosFin.firstOrNull() ?: "",
                            fecha = it.fechaHora.fecha,
                            cliente = it.cliente.direccion ?: it.cliente.numero,
                            tipo = it.tipoReporte,
                            id = it.localId.toInt()
                        )
                    })
                } catch (e: Exception) {
                    // Si hay error al leer mantenimientos, continuar
                }
            }

            reports
        }
    }

    fun refreshReportList() {
        loadReportList()
    }

    fun sendReport(report: ReportList) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sendingReports = it.sendingReports + "${report.id}",
                    successMessage = null,
                    errorMessage = null,
                    showSuccessDialog = false,
                    showErrorDialog = false
                )
            }
            try {
                // Buscar el request correspondiente según el tipo
                val request = when(report.tipo){
                    "inversion" -> getInversionRequestForReport(report)
                    "averia" -> getAveriaRequestForReport(report)
                    "mantenimiento" -> getMantenimientoRequestForReport(report)
                    else -> null
                }
                
                if (request == null) {
                    _uiState.update {
                        it.copy(
                            sendingReports = it.sendingReports - "${report.id}",
                            errorMessage = "No se encontró el reporte original para enviar.",
                            showErrorDialog = true
                        )
                    }
                    return@launch
                }

                // El cliente ya existe en la base de datos (no necesitamos crearlo)
                // La ubicación ya se actualizó vía PATCH si fue necesario

                // Enviar según el tipo de reporte
                when (report.tipo) {
                    "inversion" -> {
                        val inversionService = InversionService()
                        val result = inversionService.enviarInversionLocalMultipart(request as InversionRequest)
                        handleInversionResponse(result, request, report)
                    }
                    "averia" -> {
                        val averiaService = AveriaService()
                        val result = averiaService.enviarAveriaLocalMultipart(request as AveriaRequest)
                        handleAveriaResponse(result, request, report)
                    }
                    "mantenimiento" -> {
                        val mantenimientoService = MantenimientoService()
                        val result = mantenimientoService.enviarMantenimientoLocalMultipart(request as MantenimientoRequest)
                        handleMantenimientoResponse(result, request, report)
                    }
                    else -> throw IllegalArgumentException("Tipo de reporte no válido")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        sendingReports = it.sendingReports - "${report.id}",
                        errorMessage = "Error inesperado: ${e.message}",
                        showErrorDialog = true
                    )
                }
            }
        }
    }

    private suspend fun handleInversionResponse(result: Result<InversionResponse>, request: Any, report: ReportList) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.success == true) {
                // Éxito: eliminar del archivo local
                removeRequestFromLocal(request, report.tipo)
                _uiState.update { currentState ->
                    currentState.copy(
                        reports = currentState.reports.filter {
                            it.id != report.id
                        },
                        sendingReports = currentState.sendingReports - "${report.id}",
                        successMessage = response.message,
                        showSuccessDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        sendingReports = it.sendingReports - "${report.id}",
                        errorMessage = response?.message ?: "Error desconocido del servidor",
                        showErrorDialog = true
                    )
                }
            }
        } else {
            val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            _uiState.update {
                it.copy(
                    sendingReports = it.sendingReports - "${report.id}",
                    errorMessage = errorMessage,
                    showErrorDialog = true
                )
            }
        }
    }

    private suspend fun handleAveriaResponse(result: Result<AveriaResponse>, request: Any, report: ReportList) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.success == true) {
                // Éxito: eliminar del archivo local
                removeRequestFromLocal(request, report.tipo)
                _uiState.update { currentState ->
                    currentState.copy(
                        reports = currentState.reports.filter {
                            it.id != report.id
                        },
                        sendingReports = currentState.sendingReports - "${report.id}",
                        successMessage = response.message,
                        showSuccessDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        sendingReports = it.sendingReports - "${report.id}",
                        errorMessage = response?.message ?: "Error desconocido del servidor",
                        showErrorDialog = true
                    )
                }
            }
        } else {
            val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            _uiState.update {
                it.copy(
                    sendingReports = it.sendingReports - "${report.id}",
                    errorMessage = errorMessage,
                    showErrorDialog = true
                )
            }
        }
    }

    private suspend fun handleMantenimientoResponse(result: Result<MantenimientoResponse>, request: Any, report: ReportList) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.success == true) {
                // Éxito: eliminar del archivo local
                removeRequestFromLocal(request, report.tipo)
                _uiState.update { currentState ->
                    currentState.copy(
                        reports = currentState.reports.filter {
                            it.id != report.id
                        },
                        sendingReports = currentState.sendingReports - "${report.id}",
                        successMessage = response.message,
                        showSuccessDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        sendingReports = it.sendingReports - "${report.id}",
                        errorMessage = response?.message ?: "Error desconocido del servidor",
                        showErrorDialog = true
                    )
                }
            }
        } else {
            val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            _uiState.update {
                it.copy(
                    sendingReports = it.sendingReports - "${report.id}",
                    errorMessage = errorMessage,
                    showErrorDialog = true
                )
            }
        }
    }

    private suspend fun getInversionRequestForReport(report: ReportList): InversionRequest? {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "inversiones_pendientes.json")
            if (!file.exists()) return@withContext null
            val json = file.readText()
            val gson = Gson()
            val type = object : TypeToken<List<InversionRequest>>() {}.type
            val inversiones: List<InversionRequest> = try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            inversiones.find {
                it.localId.toInt() == report.id
            }
        }
    }
    private suspend fun getAveriaRequestForReport(report: ReportList): AveriaRequest? {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "averias_pendientes.json")
            if (!file.exists()) return@withContext null
            val json = file.readText()
            val gson = Gson()
            val type = object : TypeToken<List<AveriaRequest>>() {}.type
            val averias: List<AveriaRequest> = try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            averias.find {
                it.localId.toInt() == report.id
            }
        }
    }
    private suspend fun getMantenimientoRequestForReport(report: ReportList): MantenimientoRequest? {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "mantenimientos_pendientes.json")
            if (!file.exists()) return@withContext null
            val json = file.readText()
            val gson = Gson()
            val type = object : TypeToken<List<MantenimientoRequest>>() {}.type
            val mantenimientos: List<MantenimientoRequest> = try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            mantenimientos.find {
                it.localId.toInt() == report.id
            }
        }
    }

    private suspend fun removeRequestFromLocal(request: Any, tipo: String) {
        withContext(Dispatchers.IO) {
            val fileName = when (tipo) {
                "inversion" -> "inversiones_pendientes.json"
                "averia" -> "averias_pendientes.json"
                "mantenimiento" -> "mantenimientos_pendientes.json"
                else -> throw IllegalArgumentException("Tipo de reporte no válido")
            }
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withContext
            val json = file.readText()
            val gson = Gson()
            val type = when (tipo) {
                "inversion" -> object : TypeToken<MutableList<InversionRequest>>() {}.type
                "averia" -> object : TypeToken<MutableList<AveriaRequest>>() {}.type
                "mantenimiento" -> object : TypeToken<MutableList<MantenimientoRequest>>() {}.type
                else -> throw IllegalArgumentException("Tipo de reporte no válido")
            }
            val requests: MutableList<Any> = try {
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
            val removed = requests.removeIf {
                when (tipo) {
                    "inversion" -> {
                        it is InversionRequest && it.localId == (request as InversionRequest).localId
                    }
                    "averia" -> {
                        it is AveriaRequest && it.localId == (request as AveriaRequest).localId
                    }
                    "mantenimiento" -> {
                        it is MantenimientoRequest && it.localId == (request as MantenimientoRequest).localId
                    }
                    else -> false
                }
            }
            if (removed) {
                file.writeText(gson.toJson(requests))
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false, successMessage = null) }
    }

    fun dismissErrorDialog() {
        _uiState.update { it.copy(showErrorDialog = false, errorMessage = null) }
    }
} 