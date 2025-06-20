package com.suncar.suncartrabajador.ui.features.Materiales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialType
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import com.suncar.suncartrabajador.ui.components.CustomCard
import com.suncar.suncartrabajador.ui.components.EmptyStateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialesComposable(
    modifier: Modifier = Modifier,
    materialesViewModel: MaterialesViewModel = viewModel()
) {
    val state by materialesViewModel.uiState.collectAsState()
    var typeExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<MaterialProduct?>(null) }
    var quantity by remember { mutableStateOf(1) }

    CustomCard(
        modifier = modifier.padding(16.dp),
        title = "Gestión de Materiales",
        subtitle = "Añade o elimina materiales de tu proyecto",
        icon = Icons.Filled.Inventory,
        isLoading = state.isLoading
    ) {
        // Dropdown de tipo de material
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = state.selectedType?.name ?: "Seleccionar tipo de material",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Material") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                state.materialTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            materialesViewModel.selectType(type)
                            selectedProduct = null
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown de producto (solo si hay tipo seleccionado)
        ExposedDropdownMenuBox(
            expanded = productExpanded,
            onExpandedChange = {
                if (state.selectedType != null) productExpanded = !productExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedProduct?.name ?: "Seleccionar producto",
                onValueChange = {},
                readOnly = true,
                label = { Text("Producto") },
                enabled = state.selectedType != null,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = productExpanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = productExpanded,
                onDismissRequest = { productExpanded = false }
            ) {
                state.availableProducts.forEach { product ->
                    DropdownMenuItem(
                        text = { Text(product.name) },
                        onClick = {
                            selectedProduct = product
                            productExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de cantidad con controles numéricos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Text(
//                text = "Cantidad:",
//                modifier = Modifier.width(80.dp),
//                style = MaterialTheme.typography.bodyMedium
//            )
            
            IconButton(
                onClick = { if (quantity > 1) quantity-- },
                enabled = selectedProduct != null && quantity > 1
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
            }
            
            OutlinedTextField(
                value = quantity.toString(),
                onValueChange = { 
                    val newValue = it.toIntOrNull() ?: 1
                    quantity = if (newValue > 0) newValue else 1
                },
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f),
                enabled = selectedProduct != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
            
            IconButton(
                onClick = { quantity++ },
                enabled = selectedProduct != null
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para agregar material
        Button(
            onClick = {
                if (state.selectedType != null && selectedProduct != null) {
                    val newMaterial = MaterialItem(
                        type = state.selectedType!!.name,
                        name = selectedProduct!!.name,
                        quantity = quantity.toString()
                    )
                    materialesViewModel.updateMaterials(state.materials + newMaterial)
                    // Limpiar selección
                    selectedProduct = null
                    quantity = 1
                }
            },
            enabled = state.selectedType != null && selectedProduct != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar material")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar material")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de materiales agregados
        if (state.materials.isEmpty()) {
            EmptyStateCard(
                title = "No hay materiales agregados",
                description = "Usa el formulario para añadir materiales a tu proyecto.",
                icon = Icons.Filled.Inventory
            )
        } else {
            Text("Materiales agregados:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(state.materials) { material ->
                    MaterialItemRow(
                        material = material,
                        onRemoveClick = { materialesViewModel.removeMaterial(material) }
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialItemRow(
    material: MaterialItem,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = material.name)
                Text(text = "Tipo: ${material.type}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Cantidad: ${material.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar material")
            }
        }
    }
}
