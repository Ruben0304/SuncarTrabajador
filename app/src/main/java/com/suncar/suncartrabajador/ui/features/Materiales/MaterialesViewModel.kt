package com.suncar.suncartrabajador.ui.features.Materiales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.data.repositories.MaterialesRepository
import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import com.suncar.suncartrabajador.domain.models.MaterialCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaterialesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MaterialesState())
    val uiState: StateFlow<MaterialesState> = _uiState.asStateFlow()
    private val repository = MaterialesRepository()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val initialData = repository.getInitialMaterialesData()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    materialTypes = initialData.materialTypes
                )
            }
        }
    }

    fun updateMaterials(materials: List<MaterialItem>) {
        _uiState.value = _uiState.value.copy(materials = materials)
    }

    fun addMaterial() {
        val currentMaterials = _uiState.value.materials
        _uiState.value = _uiState.value.copy(materials = currentMaterials + MaterialItem())
    }

    fun removeMaterial(material: MaterialItem) {
        val currentMaterials = _uiState.value.materials
        if (currentMaterials.size > 1) {
            _uiState.value = _uiState.value.copy(
                materials = currentMaterials.filterNot { it == material }
            )
        }
    }

    fun updateMaterial(oldMaterial: MaterialItem, newMaterial: MaterialItem) {
        val currentMaterials = _uiState.value.materials
        _uiState.value = _uiState.value.copy(
            materials = currentMaterials.map { if (it == oldMaterial) newMaterial else it }
        )
    }

    fun selectType(category: MaterialCategory) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    selectedType = category,
                    availableProducts = emptyList(), // Limpiar productos anteriores
                    isLoadingProducts = true
                ) 
            }
            
            // Cargar productos para el tipo seleccionado usando el ID
            try {
                val products = repository.getMaterialProductsByType(category.id)
                _uiState.update { 
                    it.copy(
                        availableProducts = products,
                        isLoadingProducts = false
                    ) 
                }
            } catch (e: Exception) {
                // En caso de error, mantener la lista vacía
                _uiState.update { 
                    it.copy(
                        availableProducts = emptyList(),
                        isLoadingProducts = false
                    ) 
                }
            }
        }
    }
} 