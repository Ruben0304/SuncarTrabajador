package com.suncar.suncartrabajador.data.repositories

import com.suncar.suncartrabajador.domain.models.MaterialType
import com.suncar.suncartrabajador.domain.models.MaterialBrand
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import kotlinx.coroutines.delay

class MaterialesRepository {

    private val materialTypes = listOf(
        MaterialType("1", "Herramientas"),
        MaterialType("2", "Equipos de Seguridad"),
        MaterialType("3", "Materiales de Construcción"),
        MaterialType("4", "Equipos Electrónicos")
    )

    private val materialBrands = listOf(
        MaterialBrand("1", "DeWalt", "1"),
        MaterialBrand("2", "Makita", "1"),
        MaterialBrand("3", "Bosch", "1"),
        MaterialBrand("4", "3M", "2"),
        MaterialBrand("5", "Honeywell", "2"),
        MaterialBrand("6", "MSA", "2"),
        MaterialBrand("7", "Cemex", "3"),
        MaterialBrand("8", "Holcim", "3"),
        MaterialBrand("9", "Argos", "3"),
        MaterialBrand("10", "Samsung", "4"),
        MaterialBrand("11", "LG", "4"),
        MaterialBrand("12", "Sony", "4")
    )

    private val materialProducts = listOf(
        MaterialProduct("1", "Taladro Eléctrico", "1"),
        MaterialProduct("2", "Sierra Circular", "1"),
        MaterialProduct("3", "Martillo Demoledor", "1"),
        MaterialProduct("4", "Atornillador", "2"),
        MaterialProduct("5", "Lijadora Orbital", "2"),
        MaterialProduct("6", "Esmeriladora", "2"),
        MaterialProduct("7", "Casco de Seguridad", "4"),
        MaterialProduct("8", "Gafas de Protección", "4"),
        MaterialProduct("9", "Guantes de Trabajo", "4"),
        MaterialProduct("10", "Chaleco Reflectivo", "5"),
        MaterialProduct("11", "Botas de Seguridad", "5"),
        MaterialProduct("12", "Arnés de Seguridad", "6"),
        MaterialProduct("13", "Cemento Portland", "7"),
        MaterialProduct("14", "Arena", "7"),
        MaterialProduct("15", "Grava", "7"),
        MaterialProduct("16", "Concreto Premezclado", "8"),
        MaterialProduct("17", "Ladrillos", "8"),
        MaterialProduct("18", "Bloques", "8"),
        MaterialProduct("19", "Tablet de Campo", "10"),
        MaterialProduct("20", "Smartphone Resistente", "10"),
        MaterialProduct("21", "Cámara Digital", "11"),
        MaterialProduct("22", "GPS", "11"),
        MaterialProduct("23", "Radio Comunicación", "12")
    )

    suspend fun getInitialMaterialesData(): InitialMaterialesData {
        delay(1000) // Simulate network delay
        return InitialMaterialesData(
            materialTypes = materialTypes,
            materialBrands = materialBrands,
            materialProducts = materialProducts
        )
    }

    fun getMaterialTypes(): List<MaterialType> {
        return materialTypes
    }

    fun getMaterialBrandsByType(typeId: String): List<MaterialBrand> {
        return materialBrands.filter { it.typeId == typeId }
    }

    fun getMaterialProductsByBrand(brandId: String): List<MaterialProduct> {
        return materialProducts.filter { it.brandId == brandId }
    }
}

data class InitialMaterialesData(
    val materialTypes: List<MaterialType>,
    val materialBrands: List<MaterialBrand>,
    val materialProducts: List<MaterialProduct>
) 