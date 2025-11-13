package com.example.waterreminder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.example.waterreminder.ui.DashBoardViewModel
import com.example.waterreminder.ui.Gender
import com.example.waterreminder.ui.WelcomePageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoard(
    welcomePageViewModel: WelcomePageViewModel = viewModel(),
    dashBoardViewModel: DashBoardViewModel = viewModel(),
    onNext: () -> Unit
) {
    val welcomeUiState by welcomePageViewModel.uiState.collectAsState()
    val dashboardUiState by dashBoardViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("\uD83E\uDD64 Hi! ${welcomeUiState.name}")
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {

        }

    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewDashBoard() {
    val welcomePageViewModel = WelcomePageViewModel().apply {
        onNameChange("Donald")
        onGenderSelected(Gender.Male)
        onDrinkingGoalsChange(2800)
    }
    val dashBoardViewModel = DashBoardViewModel()

    AppTheme {
        DashBoard(
            welcomePageViewModel = welcomePageViewModel,
            dashBoardViewModel = dashBoardViewModel,
            onNext = {}
        )
    }
}
