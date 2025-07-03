package com.suncar.suncartrabajador.ui.features.Materiales

import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import com.suncar.suncartrabajador.domain.models.MaterialCategory

data class MaterialesState(
    val materials: List<MaterialItem> = emptyList(),
    val materialTypes: List<MaterialCategory> = emptyList(),
    val availableProducts: List<MaterialProduct> = emptyList(),
    val selectedType: MaterialCategory? = null,
    val isLoading: Boolean = false,
    val isLoadingProducts: Boolean = false
)