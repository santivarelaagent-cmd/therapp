package com.example.therapp.data.auth.remote.payload.res

import com.google.gson.annotations.SerializedName


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 8/31/2025
 * @version 1.0
 */
data class SignInRes(
    val user: UserRes,
    @SerializedName("access_token")
    val accessToken: String
)