package com.example.therapp.ui.presenter.routines

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therapp.common.api.ApiResponse
import com.example.therapp.domain.model.ScheduledTraining
import com.example.therapp.domain.use_cases.routines.RoutinesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    var isRefreshing by mutableStateOf(false)
        private set

    val scheduledTraining: StateFlow<ApiResponse<List<ScheduledTraining>>> =
        routinesUseCases.getScheduledTraining()
            .catch { exception ->
                val errorMessage = when (exception) {
                    is SocketTimeoutException -> "Se agotó el tiempo de espera. Revisa tu conexión a internet."
                    is UnknownHostException -> "No se pudo conectar al servidor. Revisa tu conexión a internet."
                    else -> "Ocurrió un error inesperado: ${exception.message}"
                }
                Log.e("RoutinesViewModel", "scheduledTraining catch error: $errorMessage", exception)
                emit(ApiResponse.Error(errorMessage))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ApiResponse.Loading
            )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            routinesUseCases.refreshScheduledTraining()
            isRefreshing = false
        }
    }
}