package com.example.waterreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.waterreminder.ui.DashBoardViewModel
import com.example.waterreminder.ui.WelcomePageViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNav()
            }
        }
    }
}

@Composable
private fun AppNav() {
    val navController = rememberNavController()
    val welcomePageViewModel: WelcomePageViewModel = viewModel()
    val dashBoardViewModel: DashBoardViewModel = viewModel()


    NavHost(
        navController = navController,
        startDestination = NavRoutes.Welcome
    ) {
        composable(NavRoutes.Welcome) {
            WelcomePage(
                welcomePageViewModel = welcomePageViewModel,
                onNext = {
                    navController.navigate(NavRoutes.Dashboard)
                }
            )
        }

        composable(NavRoutes.Dashboard) {
            DashBoard(
                welcomePageViewModel = welcomePageViewModel,
                dashBoardViewModel = dashBoardViewModel,
                onNext = {
                    navController.navigate(NavRoutes.Dashboard)
                }
            )
        }
    }
}