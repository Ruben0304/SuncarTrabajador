package com.suncar.suncartrabajador.ui.features.Brigada

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.domain.models.TeamMember
import com.suncar.suncartrabajador.ui.components.CustomCard
import com.suncar.suncartrabajador.ui.components.EmptyStateCard
import com.suncar.suncartrabajador.ui.features.Brigada.BrigadaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrigadaComposable(
    modifier: Modifier = Modifier,
    brigadaViewModel: BrigadaViewModel = viewModel()
) {
    val state by brigadaViewModel.uiState.collectAsState()
    var leaderExpanded by remember { mutableStateOf(false) }
    var memberExpanded by remember { mutableStateOf(false) }

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Brigada",
        subtitle = "Añade o elimina miembros de tu equipo",
        icon = Icons.Filled.Groups,
        isLoading = state.isLoading
    ) {
        // Dropdown to select leader
        ExposedDropdownMenuBox(
            expanded = leaderExpanded,
            onExpandedChange = { leaderExpanded = !leaderExpanded }
        ) {
            OutlinedTextField(
                value = state.leader?.name ?: "Seleccionar líder",
                onValueChange = {},
                readOnly = true,
                label = { Text("Líder de Brigada") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaderExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = leaderExpanded,
                onDismissRequest = { leaderExpanded = false }
            ) {
                state.availableLeaders.forEach { member ->
                    DropdownMenuItem(
                        text = { Text(member.name) },
                        onClick = {
                            brigadaViewModel.selectLeader(member)
                            leaderExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // List of current team members
        if (state.teamMembers.isEmpty()) {
            EmptyStateCard(
                title = "No hay miembros en la brigada",
                description = "Usa el selector para añadir miembros.",
                icon = Icons.Filled.PeopleOutline
            )
        } else {
            Text("Miembros de la Brigada:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(state.teamMembers, key = { it.id }) { member ->
                    BrigadeMemberItem(
                        member = member,
                        onRemoveClick = { brigadaViewModel.removeTeamMember(member) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown to add team members
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = { memberExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir miembro")
            }
            DropdownMenu(
                expanded = memberExpanded,
                onDismissRequest = { memberExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.availableTeamMembers.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay miembros para añadir") },
                        onClick = { memberExpanded = false },
                        enabled = false
                    )
                } else {
                    state.availableTeamMembers.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member.name) },
                            onClick = {
                                brigadaViewModel.addTeamMember(member)
                                memberExpanded = false
                            }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = member.name)
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar miembro")
            }
        }
    }
}