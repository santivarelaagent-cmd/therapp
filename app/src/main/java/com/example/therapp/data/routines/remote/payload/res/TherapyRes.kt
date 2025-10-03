package com.example.therapp.data.routines.remote.payload.res

data class TherapyRes(
    val id: Int,
    val name: String,
    val description: String,
    val isModel: Boolean,
    val isActive: Boolean,
    val routines: List<String>
)
