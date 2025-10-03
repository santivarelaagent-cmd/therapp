package com.example.therapp.data.auth

import android.util.Log
import com.example.therapp.common.api.apiRequestFlow
import com.example.therapp.data.auth.remote.AuthApi
import com.example.therapp.data.auth.remote.payload.req.SignInReq
import com.example.therapp.domain.repository.AuthRepository
import com.example.therapp.security.AsyncStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 8/15/2025
 * @version 1.0
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val storage: AsyncStorage,
    private val api: AuthApi
) : AuthRepository {
    override fun signIn(req: SignInReq) = apiRequestFlow {
        val response = api.signIn(req)
        Log.d("AuthRepositoryImpl", "signIn: $response")
        if (response.isSuccessful) {
            response.body()?.let { signInRes ->
                signInRes.accessToken?.let { saveToken(it) }
            }
        }
        response
    }

    override suspend fun signOut() {
        storage.removeItem("access_token")
    }

    override fun isSignedIn(): Flow<Boolean> =
        storage.observeItem("access_token").map { !it.isNullOrEmpty() }


    suspend fun saveToken(token: String) {

        Log.d("AuthRepositoryImpl", "saveToken: $token")
        storage.setItem("access_token", token)
    }

    suspend fun clearToken() {
        storage.removeItem("access_token")
    }

}