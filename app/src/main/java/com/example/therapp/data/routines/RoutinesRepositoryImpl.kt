package com.example.therapp.data.routines

import com.example.therapp.common.api.ApiResponse
import com.example.therapp.common.api.apiRequestFlow
import com.example.therapp.data.routines.remote.RoutinesApi
import com.example.therapp.data.routines.remote.payload.res.ScheduledTrainingRes
import com.example.therapp.domain.repository.RoutinesRepository
import kotlinx.coroutines.flow.Flow
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
    private val api: RoutinesApi
) : RoutinesRepository {
    override fun getScheduledTraining(): Flow<ApiResponse<List<ScheduledTrainingRes>>> =
        apiRequestFlow {
            api.getScheduledTraining()
        }
}