package com.example.therapp.data.routines.remote.payload.res

data class RoutineRes(
    val id: Int,
    val therapyId: Int,
    val therapy: TherapyRes,
    val name: String,
    val description: String,
    val exercises: List<ExercisesRes>,
    val isModeled: Boolean,
    val isActive: Boolean
)
