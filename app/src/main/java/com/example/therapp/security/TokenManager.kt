package com.example.therapp.security

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.therapp.common.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_KEY = longPreferencesKey("auth-user")
        private val USER_ROLES = stringSetPreferencesKey("auth-user-roles")
    }

    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveRefreshToken(token: String) {
        Log.d("saveRefreshToken ->", token)
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    suspend fun deleteRefreshToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }


    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    suspend fun saveAccessToken(token: String) {
        Log.d("saveAccessToken ->", token)
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun deleteAccessToken() {
        Log.d("TokenManager ->", "deleteAccessToken")
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }


    suspend fun saveUserId(userId: Long) {
//        val roleSet = mutableSetOf<String>()
//        signInResponse.roles.forEach {
//            roleSet.add(it)
//        }

        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = userId
//            preferences[USER_ROLES] = roleSet
        }
    }

    fun getUserId(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_KEY]
        }
    }


}