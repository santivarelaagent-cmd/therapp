package com.example.therapp.security

import android.util.Log
import com.example.therapp.security.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val accessToken = runBlocking {
            tokenManager.getAccessToken().first()
        }

        Log.d("AuthInterceptor", "accessToken : $accessToken")

        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "Token $accessToken")

        Log.d("AuthInterceptor", "request : $request")

        return chain.proceed(request.build())
    }
}