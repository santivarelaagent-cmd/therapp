package com.example.therapp.common.api

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

fun <T> apiRequestFlow(
    timeOut: Long = 60000L,
    call: suspend () -> Response<T>
): Flow<ApiResponse<T & Any>> = flow {
    emit(ApiResponse.Loading)
    withTimeoutOrNull(timeOut) {
        val response = call()
        try {
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    emit(ApiResponse.Success(data))
                }
            } else {
                response.errorBody()?.let { error ->
                    error.close()
                    val parsedError: ErrorResponse =
                        Gson().fromJson(error.charStream(), ErrorResponse::class.java)

                    emit(ApiResponse.Failure(parsedError.message, parsedError.status))
                }
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: e.toString(), 400))
        }
    } ?: emit(ApiResponse.Failure("Timeout! Please try again.", 408))
}.flowOn(Dispatchers.IO)