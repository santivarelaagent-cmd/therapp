package com.example.therapp.data.auth.remote.payload.res

import com.google.gson.annotations.SerializedName

data class PermissionRes(
    val id : Int,
    val name : String,
    val codename : String,
    @SerializedName("content_type")
    val contentType : Int,
)
