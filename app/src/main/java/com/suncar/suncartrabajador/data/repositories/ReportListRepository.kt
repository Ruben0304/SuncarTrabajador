package com.suncar.suncartrabajador.data.repositories

import com.suncar.suncartrabajador.domain.models.ReportList
import kotlinx.coroutines.delay

class ReportListRepository {

    private val allReports = listOf(
        ReportList(
            img = "https://picsum.photos/200/200?random=1",
            fecha = "15/12/2024",
            cliente = "Constructora ABC S.A."
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=2",
            fecha = "14/12/2024",
            cliente = "Inmobiliaria XYZ Ltda."
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=3",
            fecha = "13/12/2024",
            cliente = "Desarrollos Urbanos S.A."
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=4",
            fecha = "12/12/2024",
            cliente = "Constructora del Norte"
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=5",
            fecha = "11/12/2024",
            cliente = "Proyectos Residenciales"
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=6",
            fecha = "10/12/2024",
            cliente = "Edificaciones Modernas"
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=7",
            fecha = "09/12/2024",
            cliente = "Constructora Sur Ltda."
        ),
        ReportList(
            img = "https://picsum.photos/200/200?random=8",
            fecha = "08/12/2024",
            cliente = "Desarrollos Comerciales"
        )
    )

    suspend fun getReportList(): List<ReportList> {
        delay(1000) // Simulate network delay
        return allReports
    }
} 