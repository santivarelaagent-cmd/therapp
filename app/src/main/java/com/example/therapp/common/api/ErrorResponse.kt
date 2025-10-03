package com.example.therapp.common.api

data class ErrorResponse(
    val path: String,
    val error: String,
    val message: String,
    val status: Int
)
