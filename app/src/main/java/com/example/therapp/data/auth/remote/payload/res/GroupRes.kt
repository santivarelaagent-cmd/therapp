package com.example.therapp.data.auth.remote.payload.res

data class GroupRes(
    val name : String,
    val permissions : List<PermissionRes>,
)
