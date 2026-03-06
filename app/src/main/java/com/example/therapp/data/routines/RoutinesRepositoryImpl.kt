package com.example.therapp.data.routines

import com.example.therapp.common.api.ApiResponse
import com.example.therapp.data.routines.local.RoutineDao
import com.example.therapp.data.routines.remote.RoutinesApi
import com.example.therapp.data.routines.remote.payload.mapper.toDomain
import com.example.therapp.data.routines.remote.payload.mapper.toEntity
import com.example.therapp.domain.model.Routine
import com.example.therapp.domain.model.ScheduledTraining
import com.example.therapp.domain.repository.RoutinesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/19/2025
 * @version 1.0
 */
@Singleton
class RoutinesRepositoryImpl @Inject constructor(
    private val api: RoutinesApi,
    private val dao: RoutineDao
) : RoutinesRepository {

    override fun getScheduledTraining(): Flow<ApiResponse<List<ScheduledTraining>>> {
        return dao.getScheduledTrainingsWithRoutines().map { entities ->
            if (entities.isEmpty()) {
                ApiResponse.Loading
            } else {
                ApiResponse.Success(entities.map { it.toDomain() })
            }
        }
    }

    override suspend fun refreshScheduledTraining(): ApiResponse<Unit> {
        return try {
            val response = api.getScheduledTraining()
            if (response.isSuccessful) {
                val remoteData = response.body() ?: emptyList()
                
                val trainings = remoteData.map { it.toEntity() }
                val therapies = remoteData.map { it.routine.therapy.toEntity() }.distinctBy { it.id }
                val routines = remoteData.map { it.routine.toEntity() }.distinctBy { it.id }
                
                val exercises = remoteData.flatMap { res -> res.routine.exercises.map { e -> e.toEntity() } }.distinctBy { it.id }
                val difficulties = remoteData.flatMap { res -> res.routine.exercises.flatMap { e -> e.difficulties.map { d -> d.toEntity() } } }.distinctBy { it.id }
                val ranges = remoteData.flatMap { res -> res.routine.exercises.flatMap { e -> e.difficulties.flatMap { d -> d.ranges.map { r -> r.toEntity(d.id) } } } }

                dao.updateAllData(
                    trainings,
                    therapies,
                    routines,
                    exercises,
                    difficulties,
                    ranges
                )
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.Error(response.message())
            }
        } catch (e: Exception) {
            ApiResponse.Failure(
                e.message ?: "Unknown error",
                code = 400
            )
        }
    }

    override fun getRoutineById(id: Int): Flow<Routine?> {
        return dao.getRoutineWithExercisesById(id).map { it?.toDomain() }
    }
}