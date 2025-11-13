package com.example.waterreminder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.example.waterreminder.ui.Gender
import com.example.waterreminder.ui.ViewModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoard(
    viewModel: ViewModel = viewModel(),
    onNext: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("💧 Hi! ${uiState.name}")
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
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Today's water intake",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(32.dp))

                WaterProgressChart(progress = viewModel.getPercentage(), modifier = Modifier.align(Alignment.CenterHorizontally))


            }
        }
    }
}


@Composable
fun WaterProgressChart(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer

    AndroidView(
        modifier = modifier.size(200.dp),
        factory = { context ->
            CircularProgressBar(context).apply {
                progressMax = 100f
                setProgressWithAnimation(progress * 100f)

                progressBarWidth = 16f
                backgroundProgressBarWidth = 16f
                roundBorder = true

                progressBarColor = progressColor.toArgb()
                backgroundProgressBarColor = backgroundColor.toArgb()
            }
        },
        update = { view ->
            view.setProgressWithAnimation(progress * 100f)
        }
    )
}



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewDashBoard() {
    val viewModel = ViewModel().apply {
        onNameChange("Donald")
        onGenderSelected(Gender.Male)
        onDrinkingGoalsChange(2800)
    }

    AppTheme {
        DashBoard(
            viewModel = viewModel,
            onNext = {}
        )
    }
}
