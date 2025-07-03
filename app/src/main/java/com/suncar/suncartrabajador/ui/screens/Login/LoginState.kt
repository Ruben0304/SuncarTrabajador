package com.suncar.suncartrabajador.ui.screens.Login

data class LoginState(
    val ci: String = "12345678",
    val password: String = "12345678*",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val loginSuccess: Boolean = false
) 