package com.example.therapp.ui.presenter.routines

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.therapp.common.api.ApiResponse
import com.example.therapp.data.routines.remote.payload.res.ScheduledTrainingRes
import com.example.therapp.ui.components.RoutineItem

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/4/2025
 * @version 1.0
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutinesScreen(
    viewModel: RoutinesViewModel = hiltViewModel()
) {
    val state by viewModel.scheduledTraining.collectAsState()
    Column {
        Text("Routines")
        when (state) {
            is ApiResponse.Loading -> {
                Text("Cargando entrenamientos...")
            }

            is ApiResponse.Success -> {
                val trainings = (state as ApiResponse.Success<List<ScheduledTrainingRes>>).data
                LazyColumn {
                    items(trainings) { training: ScheduledTrainingRes ->
                        RoutineItem(
                            routine = training.routine,
                            startTime = training.startTime
                        )
                    }
                }
            }

            is ApiResponse.Error -> {
                val error = (state as ApiResponse.Error).errorMessage
                Text("Error: $error")
            }

            is ApiResponse.Failure -> {

                Text("Error de conexión")
            }
        }
    }
}
