package com.example.therapp.ui.presenter.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therapp.common.api.ApiResponse
import com.example.therapp.data.routines.remote.payload.res.ScheduledTrainingRes
import com.example.therapp.domain.use_cases.routines.RoutinesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/19/2025
 * @version 1.0
 */
@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val routinesUseCases: RoutinesUseCases
) : ViewModel() {
    val scheduledTraining: StateFlow<ApiResponse<List<ScheduledTrainingRes>>> =
        routinesUseCases.getScheduledTraining()
            // 2. Añade el operador catch para manejar excepciones del flujo
            .catch { exception ->
                // Comprueba el tipo de excepción para dar un mensaje más específico
                val errorMessage = when (exception) {
                    is SocketTimeoutException -> "Se agotó el tiempo de espera. Revisa tu conexión a internet."
                    is UnknownHostException -> "No se pudo conectar al servidor. Revisa tu conexión a internet."
                    else -> "Ocurrió un error inesperado: ${exception.message}"
                }
                // Emite el estado de error con el mensaje
                emit(ApiResponse.Error(errorMessage))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ApiResponse.Loading
            )
}