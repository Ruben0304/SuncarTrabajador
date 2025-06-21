package com.suncar.suncartrabajador.ui.screens.Login

data class LoginState(
    val ci: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false
) 