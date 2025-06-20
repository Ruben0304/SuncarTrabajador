package com.suncar.suncartrabajador.domain.models


// Data classes for form state

data class BrigadeLeader(

    val name: String = "",

    val id: String = ""

)



data class TeamMember(

    val name: String = "",

    val id: String = ""

)



data class MaterialItem(

    val type: String = "",

    val name: String = "",

    val quantity: String = ""

)



data class LocationData(

    val address: String = "",

    val coordinates: String = "",

    val distance: String = ""

)



// Data classes for materials hierarchy

data class MaterialType(val id: String, val name: String)

data class MaterialBrand(val id: String, val name: String, val typeId: String)

data class MaterialProduct(val id: String, val name: String, val brandId: String)