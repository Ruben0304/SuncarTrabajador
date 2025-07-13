package com.suncar.suncartrabajador.ui.screens.Cuenta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.singleton.Auth
import com.suncar.suncartrabajador.ui.shared.HeaderSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaConfigScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    brigadaViewModel: CuentaScreenViewModel = viewModel()
) {
    val state by brigadaViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var memberExpanded by remember { mutableStateOf(false) }
    var showManualForm by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var expandedBrigada by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf(TextFieldValue("")) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header de perfil
        item {
            HeaderSection("Mi Cuenta","Gestiona tu información personal")
        }

        // Información de la cuenta
        item {
            AccountInfoCard(
                userName = Auth.getCurrentUser()?.name ?: "Usuario",
                userPassword = Auth.getCurrentUser()?.password ?: "••••••••",
                showPassword = showPassword,
                onTogglePassword = { showPassword = !showPassword }
            )
        }

        // Opciones de cuenta
        item {
            AccountOptionsCard(
                onLogout = { brigadaViewModel.showLogoutDialog() },
                onChangePassword = { brigadaViewModel.showChangePasswordDialog() }
            )
        }

        // Gestión de brigada (colapsable)
//        item {
//            BrigadaManagementCard(
//                isExpanded = expandedBrigada,
//                onToggleExpanded = { expandedBrigada = !expandedBrigada },
//                teamMembersCount = state.teamMembers.size
//            )
//        }

        // Contenido de brigada (solo si está expandido)
        if (expandedBrigada) {
            if (state.isLoading) {
                item {
                    LoadingCard()
                }
            } else {
                item {
                    TeamMembersSection(
                        members = state.teamMembers,
                        onRemoveMember = { member ->
                            brigadaViewModel.removeTeamMember(member)
                        }
                    )
                }

                item {
                    AddMemberCard(
                        availableMembers = state.availableTeamMembers,
                        memberExpanded = memberExpanded,
                        onMemberExpandedChange = { memberExpanded = it },
                        onAddMember = { member ->
                            brigadaViewModel.addTeamMember(member)
                            memberExpanded = false
                        },
                        showManualForm = showManualForm,
                        onToggleManualForm = { showManualForm = !showManualForm },
                        onAddManualMember = { name, ci ->
                            brigadaViewModel.addTeamMember(TeamMember(name, ci))
                            showManualForm = false
                        }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de logout
    if (state.showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = { brigadaViewModel.logout(context, onLogout) },
            onDismiss = { brigadaViewModel.hideLogoutDialog() }
        )
    }

    // Diálogo de cambio de contraseña
    if (state.showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { brigadaViewModel.hideChangePasswordDialog() },
            title = { Text("Cambiar Contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newPasswordError = null
                        },
                        label = { Text("Nueva contraseña") },
                        isError = newPasswordError != null,
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    if (newPasswordError != null) {
                        Text(newPasswordError!!, color = MaterialTheme.colorScheme.error)
                    }
                    if (state.isChangingPassword) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    if (state.changePasswordError != null) {
                        Text(state.changePasswordError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPassword.text.length < 6) {
                            newPasswordError = "La contraseña debe tener al menos 6 caracteres"
                        } else {
                            brigadaViewModel.changePassword(newPassword.text)
                        }
                    },
                    enabled = !state.isChangingPassword
                ) { Text("Cambiar") }
            },
            dismissButton = {
                TextButton(
                    onClick = { brigadaViewModel.hideChangePasswordDialog() },
                    enabled = !state.isChangingPassword
                ) { Text("Cancelar") }
            }
        )
    }

    // Mensaje de éxito
    if (state.changePasswordSuccess == true) {
        LaunchedEffect(state.changePasswordSuccess) {
            newPassword = TextFieldValue("")
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { brigadaViewModel.hideChangePasswordDialog() }) { Text("OK") }
            }
        ) { Text("Contraseña cambiada exitosamente") }
    }
}


@Composable
fun AccountInfoCard(
    userName: String,
    userPassword: String,
    showPassword: Boolean,
    onTogglePassword: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Información de la Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de usuario
            AccountInfoItem(
                label = "Usuario",
                value = userName,
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Contraseña",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (showPassword) userPassword else "••••••••",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onTogglePassword,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AccountInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AccountOptionsCard(
    onLogout: () -> Unit,
    onChangePassword: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Opciones de Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Opción de cambiar contraseña
            AccountOption(
                icon = Icons.Default.Key,
                title = "Cambiar Contraseña",
                subtitle = "Actualiza tu contraseña de acceso",
                onClick = onChangePassword
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opción de cerrar sesión
            AccountOption(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Cerrar Sesión",
                subtitle = "Salir de la aplicación",
                onClick = onLogout,
                isDestructive = true
            )
        }
    }
}

@Composable
fun AccountOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDestructive)
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDestructive)
                        MaterialTheme.colorScheme.onError.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BrigadaManagementCard(
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    teamMembersCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Surface(
            onClick = onToggleExpanded,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gestión de Brigada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$teamMembersCount miembros registrados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando miembros de la brigada...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddMemberCard(
    availableMembers: List<TeamMember>,
    memberExpanded: Boolean,
    onMemberExpandedChange: (Boolean) -> Unit,
    onAddMember: (TeamMember) -> Unit,
    showManualForm: Boolean,
    onToggleManualForm: () -> Unit,
    onAddManualMember: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Añadir Miembro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para seleccionar de lista
            OutlinedButton(
                onClick = { onMemberExpandedChange(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Seleccionar de empleados")
                    }
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = memberExpanded,
                onDismissRequest = { onMemberExpandedChange(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (availableMembers.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay empleados disponibles") },
                        onClick = { onMemberExpandedChange(false) },
                        enabled = false
                    )
                } else {
                    availableMembers.forEach { member ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(member.name, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "CI: ${member.id}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = { onAddMember(member) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón para formulario manual
            TextButton(
                onClick = onToggleManualForm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar manualmente")
            }

            // Formulario manual
            if (showManualForm) {
                Spacer(modifier = Modifier.height(16.dp))
                ManualAddForm(
                    onAddMember = onAddManualMember,
                    onCancel = onToggleManualForm
                )
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Cerrar Sesión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres cerrar sesión? Deberás iniciar sesión nuevamente para acceder a tu cuenta.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Mantener las funciones existentes
@Composable
fun ManualAddForm(
    onAddMember: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var newMemberName by remember { mutableStateOf("") }
    var newMemberCI by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var ciError by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = newMemberName,
                onValueChange = {
                    newMemberName = it
                    nameError = false
                },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("El nombre es obligatorio") }
                } else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newMemberCI,
                onValueChange = {
                    newMemberCI = it
                    ciError = false
                },
                label = { Text("Cédula de Identidad") },
                modifier = Modifier.fillMaxWidth(),
                isError = ciError,
                supportingText = if (ciError) {
                    { Text("La cédula es obligatoria") }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val validName = newMemberName.isNotBlank()
                        val validCI = newMemberCI.isNotBlank()
                        nameError = !validName
                        ciError = !validCI

                        if (validName && validCI) {
                            onAddMember(newMemberName, newMemberCI)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = newMemberName.isNotBlank() && newMemberCI.isNotBlank()
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}

@Composable
fun TeamMembersSection(
    members: List<TeamMember>,
    onRemoveMember: (TeamMember) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Miembros de la Brigada",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${members.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (members.isEmpty()) {
                EmptyStateCard(
                    title = "No hay miembros en la brigada",
                    description = "Añade miembros usando las opciones de arriba",
                    icon = Icons.Outlined.Group
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    members.forEach { member ->
                        BrigadeMemberItem(
                            member = member,
                            onRemoveClick = { onRemoveMember(member) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrigadeMemberItem(
    member: TeamMember,
    onRemoveClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "CI: ${member.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onRemoveClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar miembro",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}