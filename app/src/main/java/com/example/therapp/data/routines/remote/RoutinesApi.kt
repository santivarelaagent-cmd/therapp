package com.example.therapp.data.routines.remote

import com.example.therapp.data.routines.remote.payload.res.ScheduledTrainingRes
import retrofit2.Response
import retrofit2.http.GET

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/19/2025
 * @version 1.0
 */
interface RoutinesApi {
    @GET("/scheduled_training")
    suspend fun getScheduledTraining(): Response<List<ScheduledTrainingRes>?>
}