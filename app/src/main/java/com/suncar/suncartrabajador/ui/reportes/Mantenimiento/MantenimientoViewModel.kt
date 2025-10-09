package com.suncar.suncartrabajador.ui.reportes.Mantenimiento

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.suncar.suncartrabajador.app.service_implementations.ClienteService
import com.suncar.suncartrabajador.app.service_implementations.MantenimientoService
import com.suncar.suncartrabajador.data.schemas.*
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.ui.features.Adjuntos.AdjuntosState
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaState
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaValidator
import com.suncar.suncartrabajador.ui.features.Cliente.ClienteState
import com.suncar.suncartrabajador.ui.features.Cliente.ClienteValidator
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeState
import com.suncar.suncartrabajador.ui.features.DateTime.DateTimeValidator
import com.suncar.suncartrabajador.ui.features.Descripcion.DescripcionState
import com.suncar.suncartrabajador.ui.features.Descripcion.DescripcionValidator
import com.suncar.suncartrabajador.ui.features.FirmaCliente.FirmaClienteState
import com.suncar.suncartrabajador.ui.features.FirmaCliente.FirmaClienteValidator
import com.suncar.suncartrabajador.ui.features.Materiales.MaterialesState
import com.suncar.suncartrabajador.utils.ImageUtils
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MantenimientoViewModel(
        private val adjuntosValidator: MantenimientoAdjuntosValidator =
                MantenimientoAdjuntosValidator(),
        private val brigadaValidator: BrigadaValidator = BrigadaValidator(),
        private val materialesValidator: MantenimientoMaterialesValidator =
                MantenimientoMaterialesValidator(),
        private val clienteValidator: ClienteValidator = ClienteValidator(),
        private val dateTimeValidator: DateTimeValidator = DateTimeValidator(),
        private val descripcionValidator: DescripcionValidator = DescripcionValidator(),
        private val firmaClienteValidator: FirmaClienteValidator = FirmaClienteValidator(),
        private val mantenimientoService: MantenimientoService = MantenimientoService(),
        private val clienteService: ClienteService = ClienteService()
) : ViewModel() {
        private val _uiState = MutableStateFlow(MantenimientoState())
        val uiState: StateFlow<MantenimientoState> = _uiState.asStateFlow()

        fun updateAdjuntos(state: AdjuntosState) {
                _uiState.update { it.copy(adjuntosState = state) }
                validateForm()
        }

        fun updateBrigada(state: BrigadaState) {
                _uiState.update { it.copy(brigadaState = state) }
                validateForm()
        }

        fun updateMateriales(state: MaterialesState) {
                _uiState.update { it.copy(materialesState = state) }
                validateForm()
        }

        fun updateCliente(state: ClienteState) {
                _uiState.update { it.copy(clienteState = state) }
                validateForm()
        }

        fun updateDateTime(state: DateTimeState) {
                _uiState.update { it.copy(dateTimeState = state) }
                validateForm()
        }

        fun updateDescripcion(state: DescripcionState) {
                _uiState.update { it.copy(descripcionState = state) }
                validateForm()
        }

        fun updateFirmaCliente(state: FirmaClienteState) {
                _uiState.update { it.copy(firmaClienteState = state) }
                validateForm()
        }

        private fun validateForm() {
                val state = _uiState.value
                val isValid =
                        adjuntosValidator.isValid(state.adjuntosState) &&
                                brigadaValidator.isValid(state.brigadaState) &&
                                materialesValidator.isValid(state.materialesState) &&
                                clienteValidator.isValid(state.clienteState) &&
                                dateTimeValidator.isValid(state.dateTimeState) &&
                                descripcionValidator.validarDescripcion(
                                        state.descripcionState.descripcion
                                ) &&
                                firmaClienteValidator.isValid(state.firmaClienteState)
                _uiState.update { it.copy(isFormValid = isValid) }
        }

        fun submitForm(context: Context) {
                if (_uiState.value.isFormValid && !_uiState.value.isSubmitting) {
                        viewModelScope.launch {
                                _uiState.update {
                                        it.copy(
                                                isSubmitting = true,
                                                successMessage = null,
                                                errorMessage = null,
                                                showSuccessDialog = false,
                                                showErrorDialog = false,
                                                responseData = null,
                                                showDetailsDialog = false
                                        )
                                }
                                try {
                                        // Enviar el reporte directamente con el número de cliente
                                        val result = enviarMantenimientoMultipart(context)
                                        if (result.isSuccess) {
                                                val response = result.getOrNull()
                                                if (response?.success == true) {
                                                        _uiState.update {
                                                                it.copy(
                                                                        successMessage =
                                                                                response.message,
                                                                        responseData =
                                                                                response.data,
                                                                        showSuccessDialog = true,
                                                                        isSubmitting = false
                                                                )
                                                        }
                                                } else {
                                                        _uiState.update {
                                                                it.copy(
                                                                        errorMessage =
                                                                                response?.message
                                                                                        ?: "Error desconocido del servidor",
                                                                        showErrorDialog = true,
                                                                        isSubmitting = false
                                                                )
                                                        }
                                                }
                                        } else {
                                                val errorMessage =
                                                        result.exceptionOrNull()?.message
                                                                ?: "Error desconocido"
                                                _uiState.update {
                                                        it.copy(
                                                                errorMessage = errorMessage,
                                                                showErrorDialog = true,
                                                                isSubmitting = false
                                                        )
                                                }
                                        }
                                } catch (e: Exception) {
                                        _uiState.update {
                                                it.copy(
                                                        errorMessage =
                                                                "Error inesperado: ${e.message}",
                                                        showErrorDialog = true,
                                                        isSubmitting = false
                                                )
                                        }
                                }
                        }
                }
        }

        private suspend fun enviarMantenimientoMultipart(
                context: Context
        ): Result<MantenimientoResponse> {
                val state = _uiState.value
                val gson = Gson()
                // Preparar campos de texto
                val tipoReporteBody =
                        "mantenimiento".toRequestBody("text/plain".toMediaTypeOrNull())
                val brigadaRequest =
                        BrigadaRequest(
                                lider = state.brigadaState.leader ?: TeamMember(),
                                integrantes = state.brigadaState.teamMembers
                        )
                val materialesRequest =
                        state.materialesState.materials.map { material ->
                                MaterialRequest(
                                        tipo = material.type,
                                        nombre = material.name,
                                        cantidad = material.quantity,
                                        unidadMedida = material.unit,
                                        codigoProducto = material.productCode
                                )
                        }

                val clienteRequest =
                        ClienteRequest(
                                numero = state.clienteState.numero
                        )

                val fechaHoraRequest =
                        FechaHoraRequest(
                                fecha =
                                        state.dateTimeState.currentDate?.format(
                                                DateTimeFormatter.ISO_LOCAL_DATE
                                        )
                                                ?: "",
                                horaInicio =
                                        state.dateTimeState.startTime?.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                        )
                                                ?: "",
                                horaFin =
                                        state.dateTimeState.endTime?.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                        )
                                                ?: ""
                        )
                val brigadaBody =
                        gson.toJson(brigadaRequest)
                                .toRequestBody("application/json".toMediaTypeOrNull())
                val materialesBody =
                        gson.toJson(materialesRequest)
                                .toRequestBody("application/json".toMediaTypeOrNull())
                val clienteBody =
                        gson.toJson(clienteRequest)
                                .toRequestBody("application/json".toMediaTypeOrNull())
                val fechaHoraBody =
                        gson.toJson(fechaHoraRequest)
                                .toRequestBody("application/json".toMediaTypeOrNull())
                val descripcionBody =
                        state.descripcionState.descripcion.toRequestBody(
                                "text/plain".toMediaTypeOrNull()
                        )

                // Preparar imágenes comprimidas de forma asíncrona
                val fotosInicioParts = mutableListOf<MultipartBody.Part>()
                for ((index, uri) in state.adjuntosState.startAttachments.withIndex()) {
                        val fileName = ImageUtils.generateUniqueImageName("inicio", index + 1)
                        val part =
                                ImageUtils.uriToCompressedMultipartWithNameAsync(
                                        context,
                                        uri,
                                        "fotos_inicio",
                                        fileName
                                )
                        if (part != null) {
                                fotosInicioParts.add(part)
                        }
                }

                val fotosFinParts = mutableListOf<MultipartBody.Part>()
                for ((index, uri) in state.adjuntosState.endAttachments.withIndex()) {
                        val fileName = ImageUtils.generateUniqueImageName("fin", index + 1)
                        val part =
                                ImageUtils.uriToCompressedMultipartWithNameAsync(context, uri, "fotos_fin", fileName)
                        if (part != null) {
                                fotosFinParts.add(part)
                        }
                }

                // Preparar firma cliente como multipart
                var firmaClientePart: MultipartBody.Part? = null
                state.firmaClienteState.signatureUri?.let { uri ->
                        firmaClientePart =
                                ImageUtils.uriToCompressedMultipartAsync(
                                        context,
                                        uri,
                                        "firma_cliente"
                                )
                }

                // Llamar al endpoint multipart
                return try {
                        val response =
                                mantenimientoService.enviarMantenimientoMultipart(
                                        tipoReporteBody,
                                        brigadaBody,
                                        materialesBody,
                                        clienteBody,
                                        fechaHoraBody,
                                        descripcionBody,
                                        fotosInicioParts,
                                        fotosFinParts,
                                        firmaClientePart
                                )
                        Result.success(response)
                } catch (e: Exception) {
                        Result.failure(e)
                }
        }

        /** Cierra el diálogo de éxito */
        fun dismissSuccessDialog() {
                _uiState.update { it.copy(showSuccessDialog = false, successMessage = null) }
        }

        /** Cierra el diálogo de error */
        fun dismissErrorDialog() {
                _uiState.update { it.copy(showErrorDialog = false, errorMessage = null) }
        }

        /** Muestra el diálogo con los detalles de la respuesta */
        fun showDetailsDialog() {
                _uiState.update { it.copy(showDetailsDialog = true) }
        }

        /** Cierra el diálogo de detalles */
        fun dismissDetailsDialog() {
                _uiState.update { it.copy(showDetailsDialog = false) }
        }

        fun guardarMantenimientoLocal(context: Context) {
                if (_uiState.value.isFormValid && !_uiState.value.isSaving) {
                        viewModelScope.launch {
                                _uiState.update {
                                        it.copy(
                                                isSaving = true,
                                                successMessage = null,
                                                errorMessage = null,
                                                showSuccessDialog = false,
                                                showErrorDialog = false,
                                                responseData = null,
                                                showDetailsDialog = false
                                        )
                                }
                                try {
                                        val request = buildMantenimientoRequest(context)
                                        withContext(Dispatchers.IO) {
                                                mantenimientoService.guardarMantenimientoLocal(
                                                        context,
                                                        request
                                                )
                                        }
                                        _uiState.update {
                                                it.copy(
                                                        successMessage =
                                                                "Guardado local exitoso. Se enviará cuando haya internet.",
                                                        showSuccessDialog = true,
                                                        isSaving = false
                                                )
                                        }
                                } catch (e: Exception) {
                                        _uiState.update {
                                                it.copy(
                                                        errorMessage =
                                                                "Error al guardar localmente: ${e.message}",
                                                        showErrorDialog = true,
                                                        isSaving = false
                                                )
                                        }
                                }
                        }
                }
        }

        private suspend fun buildMantenimientoRequest(context: Context): MantenimientoRequest {
                val state = _uiState.value

                val brigadaRequest =
                        BrigadaRequest(
                                lider = state.brigadaState.leader ?: TeamMember(),
                                integrantes = state.brigadaState.teamMembers
                        )

                val materialesRequest =
                        state.materialesState.materials.map { material ->
                                MaterialRequest(
                                        tipo = material.type,
                                        nombre = material.name,
                                        cantidad = material.quantity,
                                        unidadMedida = material.unit,
                                        codigoProducto = material.productCode
                                )
                        }

                // Usar solo el número de cliente (la ubicación ya se actualizó vía PATCH si fue necesario)
                val clienteRequest =
                        ClienteCreateRequest(
                                numero = state.clienteState.numero,
                                nombre = null,
                                direccion = null,
                                latitud = null,
                                longitud = null
                        )

                val fechaHoraRequest =
                        FechaHoraRequest(
                                fecha =
                                        state.dateTimeState.currentDate?.format(
                                                DateTimeFormatter.ISO_LOCAL_DATE
                                        )
                                                ?: "",
                                horaInicio =
                                        state.dateTimeState.startTime?.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                        )
                                                ?: "",
                                horaFin =
                                        state.dateTimeState.endTime?.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                        )
                                                ?: ""
                        )

                // Usar funciones asíncronas para evitar bloquear la UI
                val fotosInicioBase64 =
                        ImageUtils.urisToBase64Async(context, state.adjuntosState.startAttachments)
                val fotosFinBase64 =
                        ImageUtils.urisToBase64Async(context, state.adjuntosState.endAttachments)
                val adjuntosRequest =
                        AdjuntosRequest(fotosInicio = fotosInicioBase64, fotosFin = fotosFinBase64)
                // Crear FirmaClienteRequest igual que en Inversion
                val firmaBase64 =
                        state.firmaClienteState.signatureUri?.let {
                                ImageUtils.uriToBase64CompressedAsync(context, it)
                        }
                val firmaClienteRequest = FirmaClienteRequest(firmaBase64 = firmaBase64)
                return MantenimientoRequest(
                        tipoReporte = "mantenimiento",
                        brigada = brigadaRequest,
                        materiales = materialesRequest,
                        cliente = clienteRequest,
                        fechaHora = fechaHoraRequest,
                        descripcion = state.descripcionState.descripcion,
                        adjuntos = adjuntosRequest,
                        firmaCliente = firmaClienteRequest
                )
        }
}
