package com.example.waterreminder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compose.AppTheme
import com.mikhaellopez.circularprogressbar.CircularProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoard(
    viewModel: WaterViewModel,
    goToRecord: () -> Unit,
    goToHistory: () -> Unit,
    onGoProfile: () -> Unit
) {
    val dashBoardUiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("💧 Hi! ${dashBoardUiState.name}")
                },
                actions = {
                    IconButton(onClick = onGoProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Edit Profile",
                            tint = Color(0xFF2E7D32) // green
                        )
                    }
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

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${dashBoardUiState.drinkingCount} / ${dashBoardUiState.drinkingGoals} ml",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(28.dp))

                Text(
                    text = viewModel.displayDrinkingAdvise(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = goToRecord,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Record Water Intake")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = goToHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("History")
                }
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(200.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewDashBoard() {
    AppTheme {
        val name = "Donald"
        val progress = 0.38f
        val advise = "You need to drink more water!"

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("💧 Hi! $name") },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Edit Profile",
                                tint = Color(0xFF2E7D32)
                            )
                        }
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

                    WaterProgressChart(
                        progress = progress,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "280 / 2500 ml",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = advise,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Record Water Intake") }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("History") }
                }
            }
        }
    }
}
