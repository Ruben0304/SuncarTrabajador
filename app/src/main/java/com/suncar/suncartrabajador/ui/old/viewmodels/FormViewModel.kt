//package com.suncar.suncartrabajador.ui.old.viewmodels
//
//
//
//import android.net.Uri
//
//import androidx.lifecycle.ViewModel
//
//import androidx.lifecycle.viewModelScope
//import com.suncar.suncartrabajador.domain.models.LocationData
//import com.suncar.suncartrabajador.domain.models.MaterialItem
//import com.suncar.suncartrabajador.domain.models.TeamMember
//
//import kotlinx.coroutines.delay
//
//import kotlinx.coroutines.flow.MutableStateFlow
//
//import kotlinx.coroutines.flow.StateFlow
//
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//
//import kotlinx.coroutines.launch
//import java.time.LocalDate
//
//import kotlin.random.Random
//
//
//enum class AttachmentType {
//    START_PROJECT,
//    END_PROJECT
//}
//
//class H1114ViewModel : ViewModel() {
//
//
//
//    private val _uiState = MutableStateFlow(H1114UiState())
//
//    val uiState: StateFlow<H1114UiState> = _uiState.asStateFlow()
//
//
//
//    init {
//
//        loadInitialData()
//
//    }
//
//
//
//
//
//
//
//    fun updateBrigadeLeader(brigadeLeader: TeamMember) {
//
//        _uiState.value = _uiState.value.copy(brigadeLeader = brigadeLeader,)
//
//        validateForm()
//
//    }
//
//
//
//    fun updateTeamMembers(teamMembers: List<TeamMember>) {
//
//        _uiState.value = _uiState.value.copy(teamMembers = teamMembers,)
//
//        validateForm()
//
//    }
//
//
//
//    fun addTeamMember() {
//
//        val currentMembers = _uiState.value.teamMembers
//
//        _uiState.value = _uiState.value.copy(teamMembers = currentMembers + TeamMember(),)
//
//        validateForm()
//
//    }
//
//
//
//    fun removeTeamMember(member: TeamMember) {
//
//        val currentMembers = _uiState.value.teamMembers
//
//        if (currentMembers.size > 1) {
//
//            _uiState.value = _uiState.value.copy(
//
//                teamMembers = currentMembers.filterNot { it == member },
//
//                )
//
//            validateForm()
//
//        }
//
//    }
//
//
//
//    fun updateTeamMember(oldMember: TeamMember, newMember: TeamMember) {
//
//        val currentMembers = _uiState.value.teamMembers
//
//        _uiState.value = _uiState.value.copy(
//
//            teamMembers = currentMembers.map { if (it == oldMember) newMember else it },
//
//            )
//
//        validateForm()
//
//    }
//
//
//
//    fun updateMaterials(materials: List<MaterialItem>) {
//
//        _uiState.value = _uiState.value.copy(materials = materials,)
//
//        validateForm()
//
//    }
//
//
//
//    fun addMaterial() {
//
//        val currentMaterials = _uiState.value.materials
//
//        _uiState.value = _uiState.value.copy(materials = currentMaterials + MaterialItem(),)
//
//        validateForm()
//
//    }
//
//
//
//    fun removeMaterial(material: MaterialItem) {
//
//        val currentMaterials = _uiState.value.materials
//
//        if (currentMaterials.size > 1) {
//
//            _uiState.value = _uiState.value.copy(
//
//                materials = currentMaterials.filterNot { it == material },
//
//                )
//
//            validateForm()
//
//        }
//
//    }
//
//
//
//    fun updateMaterial(oldMaterial: MaterialItem, newMaterial: MaterialItem) {
//
//        val currentMaterials = _uiState.value.materials
//
//        _uiState.value = _uiState.value.copy(
//
//            materials = currentMaterials.map { if (it == oldMaterial) newMaterial else it },
//
//            )
//
//        validateForm()
//
//    }
//
//
//
//    fun updateLocation(location: LocationData) {
//
//        _uiState.value = _uiState.value.copy(location = location,)
//
//        validateForm()
//
//    }
//
//
//
//    fun onLocationSelected(coordinates: String, address: String) {
//
//        val currentLocation = _uiState.value.location
//
//        val distance = calculateDistance(coordinates)
//
//        _uiState.value = _uiState.value.copy(
//
//            location = currentLocation.copy(
//
//                coordinates = coordinates,
//
//                address = address,
//
//                distance = distance
//
//            ),
//
//            )
//
//        validateForm()
//
//    }
//
//
//    // Dentro de tu ViewModel
//    fun onAddStartAttachmentClicked() {
//        _uiState.update { it.copy(showImageSourceDialog = true, pendingAttachmentType = AttachmentType.START_PROJECT) }
//    }
//
//    fun onAddEndAttachmentClicked() {
//        _uiState.update { it.copy(showImageSourceDialog = true, pendingAttachmentType = AttachmentType.END_PROJECT) }
//    }
//
//    // Función para añadir el adjunto basada en el tipo pendiente
//    fun addAttachment(uri: Uri) {
//        _uiState.value.pendingAttachmentType?.let { type ->
//            when (type) {
//                AttachmentType.START_PROJECT -> addStartAttachment(uri)
//                AttachmentType.END_PROJECT -> addEndAttachment(uri)
//            }
//            // Limpiar el tipo pendiente después de añadir la imagen
//            _uiState.update { it.copy(pendingAttachmentType = null) }
//        }
//    }
//
//    fun onImageSourceDialogDismiss() {
//        // El ViewModel decide que el diálogo se debe ocultar
//        _uiState.update { it.copy(showImageSourceDialog = false) }
//    }
//
//    // --- TUS FUNCIONES EXISTENTES (SON CORRECTAS) ---
//// Dentro de tu ViewModel
//    fun addStartAttachment(uri: Uri) {
//        _uiState.update { it.copy(startAttachments = it.startAttachments + uri) }
//    }
//
//    fun removeStartAttachment(uri: Uri) {
//        _uiState.update { it.copy(startAttachments = it.startAttachments - uri) }
//    }
//
//    fun addEndAttachment(uri: Uri) {
//        _uiState.update { it.copy(endAttachments = it.endAttachments + uri) }
//    }
//
//    fun removeEndAttachment(uri: Uri) {
//        _uiState.update { it.copy(endAttachments = it.endAttachments - uri) }
//    }
//
//    // Y las funciones para manejar el click en añadir foto
//
//
//    fun updateStartDate(date: LocalDate) {
//        _uiState.update { currentState ->
//            currentState.copy(startDate = date)
//        }
//    }
//
//    fun updateEndDate(date: LocalDate) {
//        _uiState.update { currentState ->
//            currentState.copy(endDate = date)
//        }
//    }
//
//
//    fun submitForm() {
//
//        if (_uiState.value.isFormValid && !_uiState.value.isSubmitting) {
//
//            viewModelScope.launch {
//
//                _uiState.value = _uiState.value.copy(isSubmitting = true,)
//
//
//
//                try {
//
//// Simular envío del formulario
//
//                    delay(2000)
//
//
//
//// Aquí iría la lógica real de envío
//
//// Por ejemplo: repository.submitForm(_uiState.value)
//
//
//
//// Reset form or navigate away on success
//
//
//
//                } catch (e: Exception) {
//
//// Manejar error
//
//                } finally {
//
//                    _uiState.value = _uiState.value.copy()
//
//                }
//
//            }
//
//        }
//
//    }
//
//
//
//    private fun validateForm() {
//
//        val state = _uiState.value
//
//        val isValid = state.brigadeLeader.name.isNotBlank() &&
//
//                !state.startDate.isAfter(state.endDate) &&
//
//                state.brigadeLeader.id.isNotBlank() &&
//
//                state.teamMembers.all { it.name.isNotBlank() && it.id.isNotBlank() } &&
//
//                state.materials.all {
//
//                    it.type.isNotBlank() &&
//
//                            it.brand.isNotBlank() &&
//
//                            it.name.isNotBlank() &&
//
//                            it.quantity.isNotBlank()
//
//                } &&
//
//                state.location.coordinates.isNotBlank()
//
//
//
//        _uiState.value = _uiState.value.copy(isFormValid = isValid,)
//
//    }
//
//
//
//    private fun calculateDistance(coords: String): String {
//
//        return "%.1f".format(Random.nextDouble(10.0, 50.0))
//
//    }
//
//}