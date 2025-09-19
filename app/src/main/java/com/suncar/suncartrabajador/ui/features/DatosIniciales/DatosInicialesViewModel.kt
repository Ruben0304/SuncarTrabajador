package com.suncar.suncartrabajador.ui.features.DatosIniciales

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suncar.suncartrabajador.app.service_implementations.AuthService
import com.suncar.suncartrabajador.app.service_implementations.TrabajadoresService
import com.suncar.suncartrabajador.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class DatosInicialesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DatosInicialesState())
    val uiState: StateFlow<DatosInicialesState> = _uiState.asStateFlow()

    private val validator = DatosInicialesValidator()
    private val trabajadoresService = TrabajadoresService()

    /** Inicia el proceso de carga de datos iniciales */
    fun startInitialDataLoad(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingProgress = 0f) }

            try {
                // Paso 1: Verificar conectividad de internet
                _uiState.update {
                    it.copy(
                            currentLoadingStep = "Verificando conexión a internet...",
                            loadingProgress = 0.2f
                    )
                }

                val hasInternet = checkInternetConnection(context)
                _uiState.update { it.copy(hasInternetConnection = hasInternet) }

                if (!hasInternet) {
                    _uiState.update {
                        it.copy(
                                isLoading = false,
                                isBlockedByNoInternet = true,
                                errorMessage =
                                        "No hay conexión a internet. La aplicación requiere conexión a internet para funcionar correctamente.",
                                currentLoadingStep = "Conexión a internet requerida"
                        )
                    }
                    return@launch // Detener la ejecución aquí
                }

                val sessionManager = SessionManager(context)
                if (sessionManager.hasSession()) {
                    _uiState.update {
                        it.copy(currentLoadingStep = "Iniciando session", loadingProgress = 0.5f)
                    }
                    val user = sessionManager.getSession()
                    val service = AuthService()
                    service.login(user!!.ci, user.password)
                }
                // Paso 2: Cargar trabajadores desde el servidor
                _uiState.update {
                    it.copy(
                            currentLoadingStep = "Cargando datos de trabajadores...",
                            loadingProgress = 0.5f
                    )
                }

                val trabajadoresResult = trabajadoresService.getTrabajadores()

                if (trabajadoresResult.isSuccess) {
                    val trabajadores = trabajadoresResult.getOrNull() ?: emptyList()
                    _uiState.update {
                        it.copy(
                                trabajadores = trabajadores,
                                loadingProgress = 0.8f,
                                currentLoadingStep = "Finalizando carga..."
                        )
                    }
                    
                    // Solo completar si la carga fue exitosa
                    _uiState.update {
                        it.copy(
                                isLoading = false,
                                loadingProgress = 1.0f,
                                currentLoadingStep = "Carga completada"
                        )
                    }
                    onComplete()
                } else {
                    val errorMessage =
                            trabajadoresResult.exceptionOrNull()?.message
                                    ?: "Error desconocido al cargar trabajadores"
                    _uiState.update {
                        it.copy(
                                isLoading = false,
                                errorMessage = errorMessage,
                                currentLoadingStep = "Error al cargar trabajadores"
                        )
                    }
                    // No llamar onComplete() en caso de error
                    return@launch
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar datos: ${e.message}",
                            currentLoadingStep = "Error en la carga"
                    )
                }
            }
        }
    }

    /** Verifica si hay conexión a internet con timeout de 20 segundos */
    private suspend fun checkInternetConnection(context: Context): Boolean {
        return try {
            withTimeout(20000) { // 20 segundos de timeout
                withContext(Dispatchers.IO) {
                    val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                                    ConnectivityManager
                    val network = connectivityManager.activeNetwork ?: return@withContext false
                    val activeNetwork =
                            connectivityManager.getNetworkCapabilities(network)
                                    ?: return@withContext false

                    when {
                        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            // Si hay timeout o cualquier error, asumimos que no hay conexión
            false
        }
    }

    /** Verifica si el estado actual permite continuar */
    fun isReadyToContinue(): Boolean {
        return validator.isReadyToContinue(_uiState.value)
    }

    /** Verifica si hay datos mínimos cargados */
    fun hasMinimumData(): Boolean {
        return validator.hasMinimumData(_uiState.value)
    }

    /** Limpia el mensaje de error */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /** Reintenta la carga de datos */
    fun retryLoad(context: Context, onComplete: () -> Unit) {
        _uiState.update {
            it.copy(
                    errorMessage = null,
                    isLoading = true,
                    loadingProgress = 0f,
                    isBlockedByNoInternet = false
            )
        }
        startInitialDataLoad(context, onComplete)
    }

    /** Verifica la conexión a internet y actualiza el estado */
    fun checkInternetConnectionAndUpdate(context: Context) {
        viewModelScope.launch {
            val hasInternet = checkInternetConnection(context)
            _uiState.update {
                it.copy(hasInternetConnection = hasInternet, isBlockedByNoInternet = !hasInternet)
            }
        }
    }

    /** Reinicia la aplicación */
    fun restartApp(context: Context) {
        // Limpiar el estado
        _uiState.update {
            it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isBlockedByNoInternet = false,
                    loadingProgress = 0f,
                    currentLoadingStep = "Reiniciando aplicación..."
            )
        }
        // Reiniciar el proceso de carga
        startInitialDataLoad(context) {}
    }
}
