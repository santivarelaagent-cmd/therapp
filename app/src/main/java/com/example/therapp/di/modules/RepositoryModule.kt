package com.example.therapp.di.modules

import com.example.therapp.data.auth.AuthRepositoryImpl
import com.example.therapp.data.auth.remote.AuthApi
import com.example.therapp.data.routines.RoutinesRepositoryImpl
import com.example.therapp.data.routines.remote.RoutinesApi
import com.example.therapp.domain.repository.AuthRepository
import com.example.therapp.domain.repository.RoutinesRepository
import com.example.therapp.security.AsyncStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        asyncStorage: AsyncStorage,
        authApi: AuthApi
    ): AuthRepository = AuthRepositoryImpl(asyncStorage, authApi)

    @Provides
    @Singleton
    fun provideRoutinesRepository(
        routinesApi: RoutinesApi
    ): RoutinesRepository = RoutinesRepositoryImpl(routinesApi)
}