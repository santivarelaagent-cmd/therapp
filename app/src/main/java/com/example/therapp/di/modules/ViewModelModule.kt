package com.example.therapp.di.modules

import com.example.therapp.domain.use_cases.auth.AuthUseCases
import com.example.therapp.service.VideoStorageRepository
import com.example.therapp.ui.presenter.pose_camera.MainViewModel
import com.example.therapp.ui.presenter.sign_in.SignInViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideSignInViewModel(
        authUseCases: AuthUseCases
    ) = SignInViewModel(
        authUseCases = authUseCases
    )

    @Provides
    @Singleton
    fun provideMainViewModel(
        videoRepository: VideoStorageRepository
    ) = MainViewModel(
        videoRepository
    )
}