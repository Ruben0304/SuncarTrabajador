//package com.suncar.suncartrabajador.ui.old.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.suncar.suncartrabajador.ui.viewmodels.TeamMember
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TeamMemberCard(
//    member: TeamMember,
//    index: Int,
//    onUpdate: (TeamMember) -> Unit,
//    onRemove: () -> Unit,
//    canRemove: Boolean,
//    availableMembers: List<TeamMember>
//) {
//    var dropdownExpanded by remember { mutableStateOf(false) }
//    var selectedName by remember { mutableStateOf(member.name) }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Miembro $index",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.SemiBold
//                )
//                if (canRemove) {
//                    IconButton(onClick = onRemove) {
//                        Icon(
//                            imageVector = Icons.Default.Delete,
//                            contentDescription = "Eliminar miembro",
//                            tint = MaterialTheme.colorScheme.error
//                        )
//                    }
//                }
//            }
//
//            // Dropdown de selección usando ExposedDropdownMenuBox
//            ExposedDropdownMenuBox(
//                expanded = dropdownExpanded,
//                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
//            ) {
//                OutlinedTextField(
//                    value = selectedName,
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Selecciona un miembro") },
//                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
//                    trailingIcon = {
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
//                    },
//                    modifier = Modifier
//                        .menuAnchor() // Posiciona correctamente el menú
//                        .fillMaxWidth()
//                )
//
//                ExposedDropdownMenu(
//                    expanded = dropdownExpanded,
//                    onDismissRequest = { dropdownExpanded = false }
//                ) {
//                    availableMembers.forEach { available ->
//                        DropdownMenuItem(
//                            text = { Text(available.name) },
//                            onClick = {
//                                selectedName = available.name
//                                onUpdate(available)
//                                dropdownExpanded = false
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
