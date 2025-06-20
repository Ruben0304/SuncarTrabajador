//package com.suncar.suncartrabajador.ui.old.pages
//
//import AttachmentSection
//import android.Manifest
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.animateContentSize
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.suncar.suncartrabajador.ui.components.*
//import com.suncar.suncartrabajador.ui.old.components.BrigadeLeaderSelector
//import com.suncar.suncartrabajador.ui.old.components.DateRangeSelector
//import com.suncar.suncartrabajador.ui.old.components.MaterialCard
//import com.suncar.suncartrabajador.ui.old.components.SectionCard
//import com.suncar.suncartrabajador.ui.old.components.TeamMemberCard
//import com.suncar.suncartrabajador.ui.viewmodels.H1114UiState
//import com.suncar.suncartrabajador.ui.old.viewmodels.H1114ViewModel
//
//@Composable
//fun H1114FormContent(
//    uiState: H1114UiState,
//    viewModel: H1114ViewModel,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickMultipleVisualMedia(),
//        onResult = { uris -> uris.forEach { uri -> viewModel.addAttachment(uri) } }
//    )
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture(),
//        onResult = { success ->
//            if (success) cameraImageUri?.let { uri -> viewModel.addAttachment(uri) }
//        }
//    )
//
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (isGranted) {
//                val newUri = ComposeFileProvider.getImageUri(context)
//                cameraImageUri = newUri
//                cameraLauncher.launch(newUri)
//            }
//        }
//    )
//
//    if (uiState.showImageSourceDialog) {
//        AlertDialog(
//            onDismissRequest = { viewModel.onImageSourceDialogDismiss() },
//            title = { Text("Seleccionar fuente") },
//            confirmButton = {
//                TextButton(onClick = {
//                    viewModel.onImageSourceDialogDismiss()
//                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                }) { Text("Galería") }
//            },
//            dismissButton = {
//                TextButton(onClick = {
//                    viewModel.onImageSourceDialogDismiss()
//                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                }) { Text("Cámara") }
//            }
//        )
//    }
//
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(20.dp)
//    ) {
//        item {
//            ElevatedCard(
//                modifier = Modifier.fillMaxWidth(),
//                shape = MaterialTheme.shapes.medium,
//                elevation = CardDefaults.elevatedCardElevation(6.dp),
//                colors = CardDefaults.elevatedCardColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            ) {
//                Column(modifier = Modifier.padding(24.dp)) {
//                    Text("Formulario H1114", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text("Reporte de Brigada de Trabajo", style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//        }
//
//        item {
//            SectionCard(title = "1. Identificación de la Brigada") {
//                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                    BrigadeLeaderSelector(
//                        selectedLeader = uiState.brigadeLeader,
//                        onLeaderSelected = { viewModel.updateBrigadeLeader(it) },
//                        availableMembers = uiState.availableTeamMembers.filterNot { available ->
//                            uiState.teamMembers.any { selected -> selected.id == available.id }
//                        }
//                    )
//                }
//            }
//        }
//
//        item {
//            SectionCard(title = "2. Miembros del Equipo") {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.animateContentSize()
//                ) {
//                    uiState.teamMembers.forEachIndexed { index, member ->
//                        TeamMemberCard(
//                            member = member,
//                            index = index + 1,
//                            onUpdate = { updated -> viewModel.updateTeamMember(member, updated) },
//                            onRemove = { viewModel.removeTeamMember(member) },
//                            canRemove = uiState.teamMembers.size > 1,
//                            availableMembers = uiState.availableTeamMembers.filterNot { available ->
//                                uiState.teamMembers.any { selected -> selected.id == available.id }
//                            }
//                        )
//                    }
//                    ElevatedButton(
//                        onClick = { viewModel.addTeamMember() },
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = MaterialTheme.shapes.medium
//                    ) {
//                        Icon(Icons.Default.Add, contentDescription = null)
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Añadir Miembro")
//                    }
//                }
//            }
//        }
//
//        item {
//            SectionCard(title = "3. Materiales Utilizados") {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    modifier = Modifier.animateContentSize()
//                ) {
//                    uiState.materials.forEachIndexed { index, item ->
//                        MaterialCard(
//                            item = item,
//                            index = index + 1,
//                            materialTypes = uiState.materialTypes,
//                            materialBrands = uiState.materialBrands,
//                            materialProducts = uiState.materialProducts,
//                            onUpdate = { updated -> viewModel.updateMaterial(item, updated) },
//                            onRemove = { viewModel.removeMaterial(item) },
//                            canRemove = uiState.materials.size > 1
//                        )
//                    }
//                    ElevatedButton(
//                        onClick = { viewModel.addMaterial() },
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = MaterialTheme.shapes.medium
//                    ) {
//                        Icon(Icons.Default.Add, contentDescription = null)
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Añadir Material")
//                    }
//                }
//            }
//        }
//
//        item {
//            SectionCard(title = "4. Ubicación del Trabajo") {
//                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
////                    MapComponent(
////                        onLocationSelected = { coords, address ->
////                            viewModel.onLocationSelected(coords, address)
////                        }
////                    )
//                    OutlinedTextField(
//                        value = uiState.location.address,
//                        onValueChange = {
//                            viewModel.updateLocation(uiState.location.copy(address = it))
//                        },
//                        label = { Text("Dirección") },
//                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = MaterialTheme.shapes.medium
//                    )
////                    OutlinedTextField(
////                        value = uiState.location.coordinates,
////                        onValueChange = {
////                            viewModel.updateLocation(uiState.location.copy(coordinates = it))
////                        },
////                        label = { Text("Coordenadas (lat, long)") },
////                        leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null) },
////                        modifier = Modifier.fillMaxWidth(),
////                        shape = MaterialTheme.shapes.medium
////                    )
////                    ElevatedCard(
////                        colors = CardDefaults.elevatedCardColors(
////                            containerColor = MaterialTheme.colorScheme.secondaryContainer
////                        ),
////                        modifier = Modifier.fillMaxWidth(),
////                        elevation = CardDefaults.elevatedCardElevation(4.dp)
////                    ) {
////                        Row(
////                            modifier = Modifier.padding(16.dp),
////                            verticalAlignment = Alignment.CenterVertically
////                        ) {
////                            Icon(Icons.Default.Navigation, contentDescription = null)
////                            Spacer(modifier = Modifier.width(8.dp))
////                            Text("Distancia a sede central: ${uiState.location.distance} km")
////                        }
////                    }
//                }
//            }
//        }
//
//        item {
//            SectionCard(title = "5. Archivos Adjuntos del Proyecto") {
//                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//                    AttachmentSection(
//                        title = "Fotos de Inicio de Proyecto",
//                        attachments = uiState.startAttachments,
//                        onAddAttachmentClick = { viewModel.onAddStartAttachmentClicked() },
//                        onRemoveAttachment = { uri -> viewModel.removeStartAttachment(uri) }
//                    )
//                    AttachmentSection(
//                        title = "Fotos de Fin de Proyecto",
//                        attachments = uiState.endAttachments,
//                        onAddAttachmentClick = { viewModel.onAddEndAttachmentClicked() },
//                        onRemoveAttachment = { uri -> viewModel.removeEndAttachment(uri) }
//                    )
//                }
//            }
//        }
//
//        item {
//            SectionCard(title = "6. Periodo de Trabajo") {
//                DateRangeSelector(
//                    startDate = uiState.startDate,
//                    endDate = uiState.endDate,
//                    onStartDateSelected = { viewModel.updateStartDate(it) },
//                    onEndDateSelected = { viewModel.updateEndDate(it) }
//                )
//            }
//        }
//
//        item {
//            ElevatedButton(
//                onClick = { viewModel.submitForm() },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                enabled = uiState.isFormValid && !uiState.isSubmitting,
//                shape = MaterialTheme.shapes.medium,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (uiState.isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
//                )
//            ) {
//                if (uiState.isSubmitting) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(20.dp),
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        strokeWidth = 2.dp
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Enviando...")
//                } else {
//                    Icon(Icons.Default.Send, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Enviar Formulario", style = MaterialTheme.typography.titleMedium)
//                }
//            }
//            if (!uiState.isFormValid) {
//                Text(
//                    "Por favor, complete todos los campos requeridos",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//        }
//    }
//}
