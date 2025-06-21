package com.suncar.suncartrabajador.ui.screens.Login

object LoginValidator {
    
    /**
     * Valida el formulario completo de login
     * @param ci Cédula de identidad
     * @param password Contraseña
     * @return ValidationResult con el resultado de la validación
     */
    fun validateLoginForm(ci: String, password: String): ValidationResult {
        return when {
            ci.isBlank() -> ValidationResult.Error("El CI es requerido")
            password.isBlank() -> ValidationResult.Error("La contraseña es requerida")
            !isValidCi(ci) -> ValidationResult.Error("El CI debe tener al menos 8 dígitos")
            !isValidPassword(password) -> ValidationResult.Error("La contraseña debe tener al menos 6 caracteres")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Valida solo el campo CI
     * @param ci Cédula de identidad
     * @return ValidationResult con el resultado de la validación
     */
    fun validateCi(ci: String): ValidationResult {
        return when {
            ci.isBlank() -> ValidationResult.Error("El CI es requerido")
            !isValidCi(ci) -> ValidationResult.Error("El CI debe tener al menos 8 dígitos")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Valida solo el campo contraseña
     * @param password Contraseña
     * @return ValidationResult con el resultado de la validación
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("La contraseña es requerida")
            !isValidPassword(password) -> ValidationResult.Error("La contraseña debe tener al menos 6 caracteres")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Verifica si el CI es válido
     * @param ci Cédula de identidad
     * @return true si es válido, false en caso contrario
     */
    private fun isValidCi(ci: String): Boolean {
        return ci.length >= 8 && ci.all { it.isDigit() }
    }
    
    /**
     * Verifica si la contraseña es válida
     * @param password Contraseña
     * @return true si es válida, false en caso contrario
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
} 