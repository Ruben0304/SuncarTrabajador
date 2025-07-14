package com.suncar.suncartrabajador.ui.features.Materiales

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import com.suncar.suncartrabajador.ui.components.CustomCard
import com.suncar.suncartrabajador.ui.components.EmptyStateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialesComposable(
        modifier: Modifier = Modifier,
        materialesViewModel: MaterialesViewModel = viewModel(),
        isMantenimiento: Boolean = false
) {
        val state by materialesViewModel.uiState.collectAsState()
        var typeExpanded by remember { mutableStateOf(false) }
        var productExpanded by remember { mutableStateOf(false) }
        var selectedProduct by remember { mutableStateOf<MaterialProduct?>(null) }
        var quantity by remember { mutableStateOf(1) }

        CustomCard(
                modifier = modifier.padding(16.dp),
                title = "Materiales",
                subtitle =
                        if (isMantenimiento) "Añade materiales si los utilizaste (opcional)"
                        else "Añade o elimina materiales de tu proyecto",
                icon = Icons.Filled.Inventory,
                isLoading = state.isLoading
        ) {
                // Mostrar indicador de opcional para mantenimiento
                if (isMantenimiento) {
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                                .copy(alpha = 0.5f)
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
                                                text =
                                                        "Los materiales son opcionales en mantenimiento",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                }

                // Dropdown de tipo de material
                ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                        OutlinedTextField(
                                value = state.selectedType?.categoria
                                                ?: "Seleccionar tipo de material",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo de Material") },
                                trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = typeExpanded
                                        )
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                        ) {
                                state.materialTypes.forEach { category ->
                                        DropdownMenuItem(
                                                text = { Text(category.categoria) },
                                                onClick = {
                                                        materialesViewModel.selectType(category)
                                                        selectedProduct = null
                                                        typeExpanded = false
                                                }
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Indicador de carga de productos
                if (state.selectedType != null && state.isLoadingProducts) {
                        Box(
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(40.dp),
                                                strokeWidth = 4.dp,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                                text = "Cargando productos...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color =
                                                        MaterialTheme.colorScheme.onSurface.copy(
                                                                alpha = 0.7f
                                                        )
                                        )
                                }
                        }
                }

                // LazyRow de productos mejorado
                if (state.selectedType != null &&
                                !state.isLoadingProducts &&
                                state.availableProducts.isNotEmpty()
                ) {
                        Text(
                                text = "Selecciona los productos necesarios:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val selectedMaterials =
                                state.materials.filter { it.type == state.selectedType!!.categoria }
                        LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                                items(state.availableProducts) { product ->
                                        // Buscar si este producto ya está seleccionado
                                        val selectedMaterial =
                                                selectedMaterials.find {
                                                        it.productCode == product.codigo
                                                }
                                        var isChecked by remember {
                                                mutableStateOf(selectedMaterial != null)
                                        }
                                        var quantity by remember {
                                                mutableStateOf(
                                                        selectedMaterial?.quantity?.toIntOrNull()
                                                                ?: 0
                                                )
                                        }

                                        // Card del producto con diseño mejorado
                                        Card(
                                                modifier =
                                                        Modifier.width(280.dp)
                                                                .padding(vertical = 4.dp)
                                                                .border(
                                                                        width = 1.dp,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .outline,
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                ),
                                                elevation =
                                                        CardDefaults.cardElevation(
                                                                defaultElevation =
                                                                        if (isChecked) 6.dp
                                                                        else 2.dp
                                                        ),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surface
                                                        ),
                                                shape = RoundedCornerShape(12.dp)
                                        ) {
                                                Column(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(16.dp)
                                                ) {
                                                        // Header con checkbox y nombre
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                verticalAlignment = Alignment.Top,
                                                                horizontalArrangement =
                                                                        Arrangement.SpaceBetween
                                                        ) {
                                                                Checkbox(
                                                                        checked = isChecked,
                                                                        onCheckedChange = { checked
                                                                                ->
                                                                                isChecked = checked
                                                                                if (checked) {
                                                                                        // Agregar
                                                                                        // material
                                                                                        val newMaterial =
                                                                                                MaterialItem(
                                                                                                        type =
                                                                                                                state.selectedType!!
                                                                                                                        .categoria,
                                                                                                        name =
                                                                                                                product.descripcion,
                                                                                                        quantity =
                                                                                                                quantity.toString(),
                                                                                                        unit =
                                                                                                                product.um,
                                                                                                        productCode =
                                                                                                                product.codigo
                                                                                                )
                                                                                        materialesViewModel
                                                                                                .updateMaterials(
                                                                                                        state.materials +
                                                                                                                newMaterial
                                                                                                )
                                                                                } else {
                                                                                        // Quitar
                                                                                        // material
                                                                                        materialesViewModel
                                                                                                .updateMaterials(
                                                                                                        state.materials
                                                                                                                .filterNot {
                                                                                                                        it.productCode ==
                                                                                                                                product.codigo &&
                                                                                                                                it.type ==
                                                                                                                                        state.selectedType!!
                                                                                                                                                .categoria
                                                                                                                }
                                                                                                )
                                                                                }
                                                                        }
                                                                )

                                                                // Código del producto
                                                                Surface(
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        6.dp
                                                                                ),
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .secondaryContainer,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        start = 8.dp
                                                                                )
                                                                ) {
                                                                        Text(
                                                                                text =
                                                                                        product.codigo,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelSmall,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSecondaryContainer,
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                horizontal =
                                                                                                        8.dp,
                                                                                                vertical =
                                                                                                        4.dp
                                                                                        )
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        // Nombre del producto
                                                        Text(
                                                                text = product.descripcion,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium.copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium
                                                                        ),
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurface,
                                                                maxLines = 2,
                                                                overflow = TextOverflow.Ellipsis,
                                                                modifier = Modifier.fillMaxWidth()
                                                        )

                                                        Spacer(modifier = Modifier.height(12.dp))

                                                        // Sección de cantidad
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically,
                                                                horizontalArrangement =
                                                                        Arrangement.SpaceBetween
                                                        ) {
                                                                Column(
                                                                        modifier =
                                                                                Modifier.weight(1f)
                                                                ) {
                                                                        Text(
                                                                                text = "Cantidad",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelSmall,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                4.dp
                                                                                        )
                                                                        )
                                                                        OutlinedTextField(
                                                                                value =
                                                                                        if (quantity ==
                                                                                                        0
                                                                                        )
                                                                                                ""
                                                                                        else
                                                                                                quantity.toString(),
                                                                                onValueChange = {
                                                                                        val newValue =
                                                                                                it.toIntOrNull()
                                                                                                        ?: 0
                                                                                        quantity =
                                                                                                newValue
                                                                                        if (isChecked
                                                                                        ) {
                                                                                                // Actualizar cantidad en el estado
                                                                                                val updatedMaterials =
                                                                                                        state.materials
                                                                                                                .map {
                                                                                                                        mat
                                                                                                                        ->
                                                                                                                        if (mat.productCode ==
                                                                                                                                        product.codigo &&
                                                                                                                                        mat.type ==
                                                                                                                                                state.selectedType!!
                                                                                                                                                        .categoria
                                                                                                                        ) {
                                                                                                                                mat.copy(
                                                                                                                                        quantity =
                                                                                                                                                quantity.toString()
                                                                                                                                )
                                                                                                                        } else
                                                                                                                                mat
                                                                                                                }
                                                                                                materialesViewModel
                                                                                                        .updateMaterials(
                                                                                                                updatedMaterials
                                                                                                        )
                                                                                        }
                                                                                },
                                                                                enabled = isChecked,
                                                                                modifier =
                                                                                        Modifier.width(
                                                                                                80.dp
                                                                                        ),
                                                                                keyboardOptions =
                                                                                        androidx.compose
                                                                                                .foundation
                                                                                                .text
                                                                                                .KeyboardOptions(
                                                                                                        keyboardType =
                                                                                                                androidx.compose
                                                                                                                        .ui
                                                                                                                        .text
                                                                                                                        .input
                                                                                                                        .KeyboardType
                                                                                                                        .Number
                                                                                                ),
                                                                                singleLine = true,
                                                                                textStyle =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall
                                                                        )
                                                                }

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(
                                                                                        12.dp
                                                                                )
                                                                )

                                                                // Unidad de medida
                                                                Column(
                                                                        horizontalAlignment =
                                                                                Alignment.End
                                                                ) {
                                                                        Text(
                                                                                text = "Unidad",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelSmall,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                4.dp
                                                                                        )
                                                                        )
                                                                        Surface(
                                                                                shape =
                                                                                        RoundedCornerShape(
                                                                                                8.dp
                                                                                        ),
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .tertiaryContainer,
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                top =
                                                                                                        4.dp
                                                                                        )
                                                                        ) {
                                                                                Text(
                                                                                        text =
                                                                                                product.um,
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .labelMedium
                                                                                                        .copy(
                                                                                                                fontWeight =
                                                                                                                        FontWeight
                                                                                                                                .SemiBold
                                                                                                        ),
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onTertiaryContainer,
                                                                                        modifier =
                                                                                                Modifier.padding(
                                                                                                        horizontal =
                                                                                                                12.dp,
                                                                                                        vertical =
                                                                                                                6.dp
                                                                                                )
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de materiales agregados
                if (state.materials.isEmpty()) {
                        EmptyStateCard(
                                title = "No hay materiales agregados",
                                description =
                                        "Usa el formulario para añadir materiales a tu proyecto.",
                                icon = Icons.Filled.Inventory
                        )
                } else {
                        Text("Materiales agregados:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                                items(state.materials) { material ->
                                        MaterialItemRow(
                                                material = material,
                                                onRemoveClick = {
                                                        materialesViewModel.removeMaterial(material)
                                                }
                                        )
                                }
                        }
                }
        }
}

@Composable
fun MaterialItemRow(material: MaterialItem, onRemoveClick: () -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Column {
                                Text(text = material.name)
                                Text(
                                        text = "Tipo: ${material.type}",
                                        style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                        text = "Cantidad: ${material.quantity} ${material.unit}",
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                        IconButton(onClick = onRemoveClick) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar material")
                        }
                }
        }
}
