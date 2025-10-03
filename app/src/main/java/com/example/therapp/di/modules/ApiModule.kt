package com.example.therapp.di.modules

import com.example.therapp.common.HOST_URL
import com.example.therapp.data.auth.remote.AuthApi
import com.example.therapp.data.routines.remote.RoutinesApi
import com.example.therapp.security.AuthAuthenticator
import com.example.therapp.security.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder().baseUrl(HOST_URL).addConverterFactory(GsonConverterFactory.create())


    @Provides
    @Singleton
    fun provideAuthAPI(
        retrofit: Retrofit.Builder
    ): AuthApi =
        retrofit.build().create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideRoutinesApi(
        retrofit: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): RoutinesApi =
        retrofit.client(okHttpClient).build().create(RoutinesApi::class.java)


}