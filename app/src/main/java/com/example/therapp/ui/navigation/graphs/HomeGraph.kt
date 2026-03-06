package com.example.therapp.ui.navigation.graphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.therapp.ui.navigation.routes.DrawerRoutes
import com.example.therapp.ui.navigation.routes.HomeRoutes
import com.example.therapp.ui.presenter.pose_camera.CameraScreen
import com.example.therapp.ui.presenter.routines.RoutinesScreen
import com.example.therapp.ui.presenter.routines.detail.RoutineDetailScreen
import com.example.therapp.ui.presenter.therapies.TherapiesScreen

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/4/2025
 * @version 1.0
 */
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<Graph.Home>(startDestination = HomeRoutes.RoutinesScreen) {
        composable<HomeRoutes.RoutinesScreen> {
            RoutinesScreen(
                onRoutineClick = { routineId ->
                    navController.navigate(HomeRoutes.RoutineDetailScreen(routineId))
                }
            )
        }
        
        composable<HomeRoutes.RoutineDetailScreen> {
            RoutineDetailScreen()
        }

        composable<HomeRoutes.TherapiesScreen> {
            TherapiesScreen()
        }
        composable<HomeRoutes.CameraScreen> {
            CameraScreen(navController)
        }
        composable<DrawerRoutes.ProfileScreen> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Profile Screen")
            }
        }

        composable<DrawerRoutes.SettingsScreen> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Settings Screen")
            }
        }
    }
}