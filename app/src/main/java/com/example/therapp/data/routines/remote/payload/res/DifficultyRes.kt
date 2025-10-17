package com.example.therapp.data.routines.remote.payload.res

import com.google.gson.annotations.SerializedName

data class DifficultyRes(
    val id: Int,
    @SerializedName("exercise_id")
    val exerciseId: Int,
    val name: String,
    val description: String,
    val ranges: List<String>
)
