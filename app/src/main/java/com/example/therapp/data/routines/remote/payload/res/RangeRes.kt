package com.example.therapp.data.routines.remote.payload.res

import com.google.gson.annotations.SerializedName

data class RangeRes(
    @SerializedName("point_tracked_id")
    val pointTrackedId: Int?,
    @SerializedName("point_tracked")
    val pointTracked: PointTrackedRes?,
    @SerializedName("difficulty_id")
    val difficultyId: Int,
    @SerializedName("max_angle")
    val maxAngle: Double,
    @SerializedName("min_angle")
    val minAngle: Double
)



