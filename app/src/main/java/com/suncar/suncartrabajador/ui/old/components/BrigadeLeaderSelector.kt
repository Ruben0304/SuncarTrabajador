//package com.suncar.suncartrabajador.ui.old.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
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
//fun BrigadeLeaderSelector(
//    selectedLeader: TeamMember?,
//    onLeaderSelected: (TeamMember) -> Unit,
//    availableMembers: List<TeamMember>
//) {
//    var dropdownExpanded by remember { mutableStateOf(false) }
//    var selectedName by remember { mutableStateOf(selectedLeader?.name ?: "") }
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
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Jefe de Brigada",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//
//            ExposedDropdownMenuBox(
//                expanded = dropdownExpanded,
//                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
//            ) {
//                OutlinedTextField(
//                    value = selectedName,
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Selecciona un jefe de brigada") },
//                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
//                    trailingIcon = {
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
//                    },
//                    modifier = Modifier
//                        .menuAnchor()
//                        .fillMaxWidth()
//                )
//
//                ExposedDropdownMenu(
//                    expanded = dropdownExpanded,
//                    onDismissRequest = { dropdownExpanded = false }
//                ) {
//                    availableMembers.forEach { member ->
//                        DropdownMenuItem(
//                            text = { Text(member.name) },
//                            onClick = {
//                                selectedName = member.name
//                                onLeaderSelected(member)
//                                dropdownExpanded = false
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
