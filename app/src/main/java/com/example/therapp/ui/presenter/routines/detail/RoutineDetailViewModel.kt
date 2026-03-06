package com.example.therapp.ui.presenter.routines.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.therapp.domain.model.Routine
import com.example.therapp.domain.repository.RoutinesRepository
import com.example.therapp.ui.navigation.routes.HomeRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val repository: RoutinesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<HomeRoutes.RoutineDetailScreen>()
    val routineId = route.routineId

    val routine: StateFlow<Routine?> = repository.getRoutineById(routineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
