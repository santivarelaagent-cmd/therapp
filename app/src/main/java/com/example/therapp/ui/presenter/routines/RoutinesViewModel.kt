package com.example.therapp.ui.presenter.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therapp.common.api.ApiResponse
import com.example.therapp.data.routines.remote.payload.res.ScheduledTrainingRes
import com.example.therapp.domain.use_cases.routines.RoutinesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ApiResponse.Loading
            )


}