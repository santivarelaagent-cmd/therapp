package com.example.therapp.ui.navigation.routes

import kotlinx.serialization.Serializable


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/4/2025
 * @version 1.0
 */
@Serializable
sealed class HomeRoutes {
    @Serializable
    object RoutinesScreen : HomeRoutes()

    @Serializable
    object TherapiesScreen : HomeRoutes()

    @Serializable
    object CameraScreen : HomeRoutes()

    @Serializable
    data class RoutineDetailScreen(val routineId: Int) : HomeRoutes()
}