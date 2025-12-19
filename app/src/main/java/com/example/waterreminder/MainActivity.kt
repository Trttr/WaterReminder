package com.example.waterreminder

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.waterreminder.ui.WaterViewModel
import com.example.waterreminder.ui.WaterViewModelFactory


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
    val app = LocalContext.current.applicationContext as Application
    val viewModel: WaterViewModel = viewModel(factory = WaterViewModelFactory(app))


    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login
    ) {
        composable(NavRoutes.Welcome) {
            WelcomePage(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(NavRoutes.Dashboard) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Dashboard) {
            DashBoard(
                viewModel = viewModel,
                goToRecord = {
                    navController.navigate(NavRoutes.AddRecord)
                },
                goToHistory = {
                    navController.navigate(NavRoutes.History)
                }
            )
        }

        composable(NavRoutes.AddRecord){
            RecordPage(
                viewModel = viewModel,
                onBack = {
                    navController.navigate(NavRoutes.Dashboard)
                },
                onNext = {
                    navController.navigate(NavRoutes.AddRecordSuccessful)
                }
            )

        }

        composable(NavRoutes.AddRecordSuccessful){
            AddRecordSuccessfulPage(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(NavRoutes.Dashboard)
                }
            )
        }

        composable(NavRoutes.History){
            HistoryPage(
                viewModel = viewModel,
                onBack = {
                    navController.navigate(NavRoutes.Dashboard)
                }
            )
        }

        composable(NavRoutes.Login) {
            LoginPage(
                viewModel = viewModel,
                onGoHome = {
                    navController.navigate(NavRoutes.Dashboard) {
                        popUpTo(NavRoutes.Login) { inclusive = true } // 回退不回登录
                    }
                },
                onGoWelcome = {
                    navController.navigate(NavRoutes.Welcome)
                }
            )
        }



    }
}