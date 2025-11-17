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
import com.example.waterreminder.ui.ViewModel


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
    val viewModel: ViewModel = viewModel()


    NavHost(
        navController = navController,
        startDestination = NavRoutes.Welcome
    ) {
        composable(NavRoutes.Welcome) {
            WelcomePage(
                viewModel = viewModel,
                onNext = {
                    navController.navigate(NavRoutes.Dashboard)
                }
            )
        }

        composable(NavRoutes.Dashboard) {
            DashBoard(
                viewModel = viewModel,
                goToRecord = {
                    navController.navigate(NavRoutes.Addrecord)
                },
                goToHistory = {
                    navController.navigate(NavRoutes.History)
                }
            )
        }

        composable(NavRoutes.Addrecord){
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



    }
}