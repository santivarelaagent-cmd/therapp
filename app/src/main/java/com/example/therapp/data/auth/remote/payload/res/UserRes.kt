package com.example.therapp.data.auth.remote.payload.res

import com.google.gson.annotations.SerializedName

data class UserRes(
    val url: String,
    val id: Int,
    val username: String,
    val email: String,
    val groups: List<GroupRes>,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
)
