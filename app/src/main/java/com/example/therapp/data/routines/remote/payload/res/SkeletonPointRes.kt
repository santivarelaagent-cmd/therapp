package com.example.therapp.data.routines.remote.payload.res

import com.google.gson.annotations.SerializedName

data class SkeletonPointRes(
    val id: Int,
    val codename: String,
    val verbose: String,
    @SerializedName("left_point")
    val leftPoint: Int,
    @SerializedName("right_point")
    val rightPoint: Int
)
