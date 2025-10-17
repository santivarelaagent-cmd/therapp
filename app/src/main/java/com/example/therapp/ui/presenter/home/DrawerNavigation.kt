package com.example.therapp.ui.presenter.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.therapp.ui.navigation.routes.DrawerRoutes

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 8/5/2025
 * @version 1.0
 */
enum class DrawerNavigation(
    val title: String,
    val description: String,
    val route: DrawerRoutes,
    val icon: ImageVector,
    val selectedIcon: ImageVector?,
    val unselectedIcon: ImageVector?,
) {
    PROFILE(
        title = "Mi Perfil",
        description = "Home",
        route = DrawerRoutes.ProfileScreen,
        icon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
    ),
    SETTINGS(
        title = "Ajustes",
        description = "Home",
        route = DrawerRoutes.SettingsScreen,
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
}