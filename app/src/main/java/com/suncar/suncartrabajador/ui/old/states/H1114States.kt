package com.suncar.suncartrabajador.ui.old.states

import android.net.Uri
import com.suncar.suncartrabajador.domain.models.LocationData
import com.suncar.suncartrabajador.domain.models.MaterialBrand
import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import com.suncar.suncartrabajador.domain.models.MaterialType
import com.suncar.suncartrabajador.domain.models.TeamMember
//import com.suncar.suncartrabajador.ui.old.viewmodels.AttachmentType
import java.time.LocalDate
import java.time.LocalDateTime

// UI State
data class H1114UiState(
    val isLoading: Boolean = true,
    val brigadeLeader: TeamMember = TeamMember(),
    val teamMembers: List<TeamMember> = emptyList(),
    val materials: List<MaterialItem> = emptyList(),
    val location: LocationData = LocationData(),
    val startAttachments: List<Uri> = emptyList(), // Lista para fotos de inicio
    val endAttachments: List<Uri> = emptyList(),   // Lista para fotos de fin
    val submissionTime: LocalDateTime = LocalDateTime.now(),
    val materialTypes: List<MaterialType> = emptyList(),
    val materialBrands: List<MaterialBrand> = emptyList(),
    val materialProducts: List<MaterialProduct> = emptyList(),
    val isFormValid: Boolean = false,
    val isSubmitting: Boolean = false,
    val availableTeamMembers: List<TeamMember> = emptyList(),
    val showImageSourceDialog: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
//    val pendingAttachmentType: AttachmentType? = null, // Nuevo estado para saber qué tipo de adjunto se espera
)