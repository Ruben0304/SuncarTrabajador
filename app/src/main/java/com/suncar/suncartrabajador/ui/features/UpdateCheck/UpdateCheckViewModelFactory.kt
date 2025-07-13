package com.suncar.suncartrabajador.ui.features.UpdateCheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suncar.suncartrabajador.app.service_implementations.UpdateService

class UpdateCheckViewModelFactory(private val updateService: UpdateService) :
        ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateCheckViewModel::class.java)) {
            return UpdateCheckViewModel(updateService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
