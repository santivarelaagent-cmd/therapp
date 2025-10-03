package com.example.therapp.data.routines.remote.payload.res

import com.google.gson.annotations.SerializedName

data class ScheduledTrainingRes(
    val id: Int,
    val routine: RoutineRes,
    @SerializedName("start_time")
    val startTime: String,
    val status: String,
)
