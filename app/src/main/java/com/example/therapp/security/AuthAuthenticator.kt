package com.example.therapp.security

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator
@Inject
constructor(
    private val asyncStorage: AsyncStorage
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        val accessToken = runBlocking {
            asyncStorage.getItem("access_token")
        } ?: return null

        return runBlocking {
            accessToken.let {
                Log.d("token: ", accessToken)
                response.request.newBuilder().header("Authorization", "Token $it").build()
            }
        }
    }
}