package com.suncar.suncartrabajador.domain.models

data class MaterialProduct(val codigo: String, val descripcion: String, val um: String)

data class MaterialCategory(val id: String, val categoria: String)

data class MaterialItem(
    val type: String = "",
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val productCode: String = ""
)

data class InitialMaterialesData(
    val materialTypes: List<MaterialCategory>,
    val materialProducts: List<MaterialProduct>
)