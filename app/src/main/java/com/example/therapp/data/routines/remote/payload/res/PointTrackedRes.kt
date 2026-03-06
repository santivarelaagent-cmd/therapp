package com.example.therapp.data.routines.remote.payload.res

import com.google.gson.annotations.SerializedName

data class PointTrackedRes(
    @SerializedName("skeleton_point")
    val skeletonPoint: SkeletonPointRes?,
    @SerializedName("skeleton_point_id")
    val skeletonPointId: Int,
    @SerializedName("exercise_id")
    val exerciseId: Int,
    @SerializedName("max_angle")
    val maxAngle: Double,
    @SerializedName("min_angle")
    val minAngle: Double
)
