package com.suncar.suncartrabajador.ui.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppNavigationViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow(AppScreen.LOADING_DATA)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun logout() {
        _currentScreen.value = AppScreen.LOGIN
    }
}

enum class AppScreen {
    LOADING_DATA,
    LOGIN,
    MAIN_APP
} 