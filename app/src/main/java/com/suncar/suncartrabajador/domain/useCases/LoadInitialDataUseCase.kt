package com.suncar.suncartrabajador.domain.useCases

import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.domain.models.LocationData
import com.suncar.suncartrabajador.domain.models.MaterialBrand
import com.suncar.suncartrabajador.domain.models.MaterialItem
import com.suncar.suncartrabajador.domain.models.MaterialProduct
import com.suncar.suncartrabajador.domain.models.MaterialType
import com.suncar.suncartrabajador.domain.models.TeamMember
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun loadInitialDataUseCase() {



        val availableTeamMembers = listOf(
            TeamMember("Juan Pérez", "12345678"),
            TeamMember("María García", "87654321"),
            TeamMember("Carlos López", "11223344"),
            TeamMember("Ana Torres", "99887766"),
            TeamMember("Luis Martínez", "44556677")
        )


        val materialTypes = listOf(

            MaterialType("cement", "Cemento"),

            MaterialType("steel", "Acero"),

            MaterialType("wood", "Madera"),

            MaterialType("paint", "Pintura"),

            MaterialType("screws", "Tornillos"),

            MaterialType("tools", "Herramientas")

        )



        val materialBrands = listOf(

            MaterialBrand("holcim", "Holcim", "cement"),

            MaterialBrand("cemex", "Cemex", "cement"),

            MaterialBrand("lafarge", "Lafarge", "cement"),

            MaterialBrand("arcelor", "ArcelorMittal", "steel"),

            MaterialBrand("nucor", "Nucor", "steel"),

            MaterialBrand("gerdau", "Gerdau", "steel"),

            MaterialBrand("weyerhaeuser", "Weyerhaeuser", "wood"),

            MaterialBrand("georgia", "Georgia-Pacific", "wood"),

            MaterialBrand("sherwin", "Sherwin-Williams", "paint"),

            MaterialBrand("ppg", "PPG", "paint"),

            MaterialBrand("hilti", "Hilti", "screws"),

            MaterialBrand("fischer", "Fischer", "screws"),

            MaterialBrand("dewalt", "DeWalt", "tools"),

            MaterialBrand("bosch", "Bosch", "tools")

        )



        val materialProducts = listOf(

            MaterialProduct("cement_type1", "Cemento Portland Tipo I", "holcim"),

            MaterialProduct("cement_type2", "Cemento Portland Tipo II", "holcim"),

            MaterialProduct("cement_rapid", "Cemento de Fraguado Rápido", "cemex"),

            MaterialProduct("cement_white", "Cemento Blanco", "lafarge"),

            MaterialProduct("rebar_12", "Varilla 12mm", "arcelor"),

            MaterialProduct("rebar_16", "Varilla 16mm", "arcelor"),

            MaterialProduct("sheet_steel", "Lámina Galvanizada", "nucor"),

            MaterialProduct("pine_board", "Tabla de Pino 2x4", "weyerhaeuser"),

            MaterialProduct("plywood", "Contrachapado 15mm", "georgia"),

            MaterialProduct("latex_paint", "Pintura Látex Interior", "sherwin"),

            MaterialProduct("enamel_paint", "Esmalte Exterior", "ppg"),

            MaterialProduct("screw_wood", "Tornillo para Madera 3\"", "hilti"),

            MaterialProduct("screw_metal", "Tornillo Autoperforante", "fischer"),

            MaterialProduct("drill", "Taladro Inalámbrico 18V", "dewalt"),

            MaterialProduct("saw", "Sierra Circular", "bosch")

        )



//        _uiState.value = _uiState.value.copy(
//
//            isLoading = false,
//
//            brigadeLeader = TeamMember(),
//
//            // Comienza con lista vacía
//            teamMembers = emptyList(),
//
//            availableTeamMembers = availableTeamMembers,
//
//            materials = listOf(MaterialItem()),
//
//            location = LocationData("Calle Falsa 123, Madrid", "40.4168,-3.7038", "15.2"),
//
//            materialTypes = materialTypes,
//
//            materialBrands = materialBrands,
//
//            materialProducts = materialProducts
//
//        )
//
//
//
//        validateForm()

//    }

}