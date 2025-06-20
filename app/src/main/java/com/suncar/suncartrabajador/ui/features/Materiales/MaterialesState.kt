package com.suncar.suncartrabajador.ui.features.Materiales

import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialType
import com.suncar.suncartrabajador.domain.models.MaterialProduct

data class MaterialesState(
    val materials: List<MaterialItem> = listOf(MaterialItem()),
    val materialTypes: List<MaterialType> = emptyList(),
    val availableProducts: List<MaterialProduct> = emptyList(),
    val selectedType: MaterialType? = null,
    val isLoading: Boolean = false
)