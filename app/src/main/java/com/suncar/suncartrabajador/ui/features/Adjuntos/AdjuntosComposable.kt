package com.suncar.suncartrabajador.ui.features.Adjuntos

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.ui.components.CustomCard
import com.suncar.suncartrabajador.ui.components.EmptyStateCard
import com.suncar.suncartrabajador.ui.features.Adjuntos.ComposeFileProvider

@Composable
fun AdjuntosComposable(
    modifier: Modifier = Modifier,
    adjuntosViewModel: AdjuntosViewModel = viewModel(),
    isMantenimiento: Boolean = false
) {
    val state by adjuntosViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var pendingAttachmentType by remember { mutableStateOf<AttachmentType?>(null) }

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> 
            uris.forEach { uri -> 
                when (pendingAttachmentType) {
                    AttachmentType.START_PROJECT -> adjuntosViewModel.addStartAttachment(uri)
                    AttachmentType.END_PROJECT -> adjuntosViewModel.addEndAttachment(uri)
                    null -> {}
                }
            }
            pendingAttachmentType = null
        }
    )

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraImageUri?.let { uri ->
                    when (pendingAttachmentType) {
                        AttachmentType.START_PROJECT -> adjuntosViewModel.addStartAttachment(uri)
                        AttachmentType.END_PROJECT -> adjuntosViewModel.addEndAttachment(uri)
                        null -> {}
                    }
                }
            }
            pendingAttachmentType = null
        }
    )

    // Launcher para permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newUri = ComposeFileProvider.getImageUri(context)
                cameraImageUri = newUri
                cameraLauncher.launch(newUri)
            }
        }
    )

    // Función para manejar la selección de fuente de imagen
    fun handleImageSourceSelection(type: AttachmentType) {
        pendingAttachmentType = type
        showImageSourceDialog = true
    }

    // Función para manejar la selección de galería
    fun handleGallerySelection() {
        showImageSourceDialog = false
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Función para manejar la selección de cámara
    fun handleCameraSelection() {
        showImageSourceDialog = false
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Fotos",
        subtitle = if (isMantenimiento) "Añade fotos si las tienes disponibles (opcional)" else "Añade fotos de inicio y fin del proyecto",
        icon = Icons.Filled.AttachFile,
        isLoading = state.isLoading
    ) {
        // Mostrar indicador de opcional para mantenimiento
        if (isMantenimiento) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Opcional",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Las fotos son opcionales en mantenimiento",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Sección de fotos de inicio
        AttachmentSection(
            title = "Fotos de Inicio del Proyecto",
            attachments = state.startAttachments,
            onAddAttachmentClick = { handleImageSourceSelection(AttachmentType.START_PROJECT) },
            onRemoveAttachment = { uri -> adjuntosViewModel.removeStartAttachment(uri) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de fotos de fin
        AttachmentSection(
            title = "Fotos de Fin del Proyecto",
            attachments = state.endAttachments,
            onAddAttachmentClick = { handleImageSourceSelection(AttachmentType.END_PROJECT) },
            onRemoveAttachment = { uri -> adjuntosViewModel.removeEndAttachment(uri) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Estado vacío si no hay adjuntos
        if (state.startAttachments.isEmpty() && state.endAttachments.isEmpty()) {
            EmptyStateCard(
                title = "No hay adjuntos",
                description = if (isMantenimiento) "Las fotos son opcionales. Puedes añadirlas si las tienes disponibles." else "Usa los botones para añadir fotos del proyecto.",
                icon = Icons.Filled.PhotoLibrary
            )
        }
    }

    // Diálogo para seleccionar fuente de imagen
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { 
                showImageSourceDialog = false 
                pendingAttachmentType = null
            },
            title = { Text("Seleccionar fuente") },
            text = { Text("¿De dónde quieres seleccionar la imagen?") },
            confirmButton = {
                TextButton(onClick = { handleGallerySelection() }) {
                    Text("Galería")
                }
            },
            dismissButton = {
                TextButton(onClick = { handleCameraSelection() }) {
                    Text("Cámara")
                }
            }
        )
    }
}

// Enum para identificar el tipo de adjunto
enum class AttachmentType {
    START_PROJECT,
    END_PROJECT
} 