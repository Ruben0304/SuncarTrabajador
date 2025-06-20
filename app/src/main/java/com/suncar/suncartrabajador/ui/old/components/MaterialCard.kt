//package com.suncar.suncartrabajador.ui.old.components
//
//import android.util.Log
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.slideInVertically
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.ExpandLess
//import androidx.compose.material.icons.filled.ExpandMore
//import androidx.compose.material.icons.filled.Inventory
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import com.suncar.suncartrabajador.ui.viewmodels.MaterialBrand
//import com.suncar.suncartrabajador.ui.viewmodels.MaterialItem
//import com.suncar.suncartrabajador.ui.viewmodels.MaterialProduct
//import com.suncar.suncartrabajador.ui.viewmodels.MaterialType
//
//@Composable
//fun MaterialCard(
//    item: MaterialItem,
//    index: Int,
//    materialTypes: List<MaterialType>,
//    materialBrands: List<MaterialBrand>,
//    materialProducts: List<MaterialProduct>,
//    onUpdate: (MaterialItem) -> Unit,
//    onRemove: () -> Unit,
//    canRemove: Boolean
//) {
//    var typeExpanded by remember { mutableStateOf(false) }
//    var brandExpanded by remember { mutableStateOf(false) }
//    var nameExpanded by remember { mutableStateOf(false) }
//
//    val availableBrands = materialBrands.filter { brand ->
//        materialTypes.find { it.name == item.type }?.id == brand.typeId
//    }
//
//    val availableProducts = materialProducts.filter { product ->
//        availableBrands.find { it.name == item.brand }?.id == product.brandId
//    }
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
//                    "Material $index",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.SemiBold
//                )
//                if (canRemove) {
//                    IconButton(onClick = onRemove) {
//                        Icon(
//                            Icons.Default.Delete,
//                            contentDescription = "Eliminar material",
//                            tint = MaterialTheme.colorScheme.error
//                        )
//                    }
//                }
//            }
//
//// Tipo de material
//            Box {
//                OutlinedTextField(
//                    value = item.type,
//                    onValueChange = {},
//                    label = { Text("Tipo de material") },
//                    readOnly = true,
//                    trailingIcon = {
//                        Icon(
//                            if (typeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                            contentDescription = null
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                // Capa clickable
//                Box(
//                    modifier = Modifier
//                        .matchParentSize()
//                        .clickable { typeExpanded = true }
//                )
//
//                DropdownMenu(
//                    expanded = typeExpanded,
//                    onDismissRequest = { typeExpanded = false }
//                ) {
//                    materialTypes.forEach { type ->
//                        DropdownMenuItem(
//                            text = { Text(type.name) },
//                            onClick = {
//                                onUpdate(
//                                    item.copy(
//                                        type = type.name,
//                                        brand = "",
//                                        name = ""
//                                    )
//                                )
//                                typeExpanded = false
//                            }
//                        )
//                    }
//                }
//            }
//
//// Marca - Visible si hay tipo seleccionado
//            AnimatedVisibility(
//                visible = item.type.isNotBlank(),
//                enter = fadeIn() + slideInVertically()
//            ) {
//                Box {
//                    OutlinedTextField(
//                        value = item.brand,
//                        onValueChange = {},
//                        label = { Text("Marca") },
//                        readOnly = true,
//                        enabled = availableBrands.isNotEmpty(),
//                        trailingIcon = {
//                            Icon(
//                                if (brandExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                                contentDescription = null
//                            )
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    if (availableBrands.isNotEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .matchParentSize()
//                                .clickable { brandExpanded = true }
//                        )
//                    }
//
//                    DropdownMenu(
//                        expanded = brandExpanded,
//                        onDismissRequest = { brandExpanded = false }
//                    ) {
//                        availableBrands.forEach { brand ->
//                            DropdownMenuItem(
//                                text = { Text(brand.name) },
//                                onClick = {
//                                    onUpdate(
//                                        item.copy(
//                                            brand = brand.name,
//                                            name = ""
//                                        )
//                                    )
//                                    brandExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//
//// Producto - Visible si hay marca seleccionada
//            AnimatedVisibility(
//                visible = item.brand.isNotBlank(),
//                enter = fadeIn() + slideInVertically()
//            ) {
//                Box {
//                    OutlinedTextField(
//                        value = item.name,
//                        onValueChange = {},
//                        label = { Text("Producto") },
//                        readOnly = true,
//                        enabled = availableProducts.isNotEmpty(),
//                        trailingIcon = {
//                            Icon(
//                                if (nameExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                                contentDescription = null
//                            )
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    if (availableProducts.isNotEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .matchParentSize()
//                                .clickable { nameExpanded = true }
//                        )
//                    }
//
//                    DropdownMenu(
//                        expanded = nameExpanded,
//                        onDismissRequest = { nameExpanded = false }
//                    ) {
//                        availableProducts.forEach { product ->
//                            DropdownMenuItem(
//                                text = { Text(product.name) },
//                                onClick = {
//                                    onUpdate(item.copy(name = product.name))
//                                    nameExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//
//
//            // Cantidad - Siempre visible
//            OutlinedTextField(
//                value = item.quantity,
//                onValueChange = { onUpdate(item.copy(quantity = it)) },
//                label = { Text("Cantidad") },
//                leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null) },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//    }
//}