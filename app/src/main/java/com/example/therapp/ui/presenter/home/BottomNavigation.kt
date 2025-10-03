package com.example.therapp.ui.presenter.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.therapp.ui.navigation.routes.HomeRoutes

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 8/5/2025
 * @version 1.0
 */
enum class BottomNavigation(
    val title: String,
    val description: String,
    val route: HomeRoutes,
    val icon: ImageVector,
    val selectedIcon: ImageVector?,
    val unselectedIcon: ImageVector?,
) {
    HOME(
        title = "Rutinas",
        description = "Home",
        route = HomeRoutes.RoutinesScreen,
        icon = Icons.Default.CalendarMonth,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
    ),
    THERAPIES(
        title = "Terapias",
        description = "Therapies",
        route = HomeRoutes.TherapiesScreen,
        icon = Icons.Default.LocalHospital,
        selectedIcon = Icons.Filled.LocalHospital,
        unselectedIcon = Icons.Outlined.LocalHospital,
    ),
    CAMERA(
        title = "Camara",
        description = "Camera",
        route = HomeRoutes.CameraScreen,
        icon = Icons.Default.Camera,
        selectedIcon = Icons.Filled.Camera,
        unselectedIcon = Icons.Outlined.Camera,
    )
}