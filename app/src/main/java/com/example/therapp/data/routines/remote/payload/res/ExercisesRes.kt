package com.example.therapp.data.routines.remote.payload.res

import com.google.gson.annotations.SerializedName

data class ExercisesRes(
    val id: Int,
    val name: String,
    val description: String,
    val order: Int,
    val routine: String,
    @SerializedName("routine_id")
    val routineId: Int,
    val status: String,
    val video: String,
    @SerializedName("is_model")
    val isModel: Boolean,
    @SerializedName("is_active")
    val isActive: Boolean,
    val difficulties: List<DifficultyRes>
)
