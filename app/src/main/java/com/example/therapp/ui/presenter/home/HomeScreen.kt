package com.example.therapp.ui.presenter.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.therapp.ui.components.AppToolBar
import com.example.therapp.ui.components.HomeBottomBar
import com.example.therapp.ui.navigation.graphs.Graph
import com.example.therapp.ui.navigation.graphs.homeGraph

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/3/2025
 * @version 1.0
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val homeNavController = rememberNavController()

    val navBackStackEntry = homeNavController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                Text("Drawer header")
                Text("Item 1")
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppToolBar(
                    toolbarTitle = "TherApp",
                    signOutButtonClicked = {
                        viewModel.signOut()
                    },
                    navButtonClicked = { /*TODO*/ }
                )
            },
            bottomBar = {
                HomeBottomBar(
                    navigationItems = BottomNavigation.entries,
                    currentDestination = currentDestination,
                    navigateTo = {
                        homeNavController.navigate(it) {
                            popUpTo(homeNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                )
            },
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF3B4048)),
//                    .border(1.dp, Color.Red),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            ) {
                NavHost(
                    navController = homeNavController,
                    startDestination = Graph.Home,
                    modifier = Modifier.padding(16.dp)
                ) {
                    homeGraph(navController = homeNavController)
                }
            }
        }
    }

}