package com.example.waterreminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compose.AppTheme
import com.mikhaellopez.circularprogressbar.CircularProgressBar

@Composable
fun DashBoard(
    viewModel: WaterViewModel,
    goToRecord: () -> Unit,
    goToHistory: () -> Unit,
    onGoProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    DashBoardContent(
        name = uiState.name,
        progress = viewModel.getPercentage(),
        drinkingCount = uiState.drinkingCount,
        drinkingGoals = uiState.drinkingGoals,
        advise = viewModel.displayDrinkingAdvise(),
        goToRecord = goToRecord,
        goToHistory = goToHistory,
        onGoProfile = onGoProfile,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashBoardContent(
    name: String,
    progress: Float,
    drinkingCount: Int,
    drinkingGoals: Int,
    advise: String,
    goToRecord: () -> Unit,
    goToHistory: () -> Unit,
    onGoProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("💧 Hi, $name!")
                },
                actions = {
                    var menuExpanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Profile") },
                            onClick = {
                                menuExpanded = false
                                onGoProfile()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Log out") },
                            onClick = {
                                menuExpanded = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProgressCard(progress, drinkingCount, drinkingGoals)

            Spacer(Modifier.height(24.dp))

            Text(
                text = advise,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1f))

            ActionButtons(goToRecord, goToHistory)
        }
    }
}

@Composable
private fun ProgressCard(progress: Float, drinkingCount: Int, drinkingGoals: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Today's Progress",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                WaterProgressChart(progress = progress)
            }

            Text(
                text = "$drinkingCount / $drinkingGoals ml",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ActionButtons(goToRecord: () -> Unit, goToHistory: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = goToRecord,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.WaterDrop, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Record", style = MaterialTheme.typography.titleMedium)
        }
        Button(
            onClick = goToHistory,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("History", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun WaterProgressChart(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(200.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                CircularProgressBar(it).apply {
                    progressMax = 100f
                    this.progress = progress * 100f
                    progressBarWidth = 20f
                    backgroundProgressBarWidth = 20f
                    roundBorder = true
                    progressBarColor = progressColor.toArgb()
                    backgroundProgressBarColor = backgroundColor.toArgb()
                }
            },
            update = { view ->
                view.setProgressWithAnimation(progress * 100f, 1000)
            }
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashBoard() {
    AppTheme {
        DashBoardContent(
            name = "Ray",
            progress = 0.62f,
            drinkingCount = 1550,
            drinkingGoals = 2500,
            advise = "You're doing great, keep it up! 👍",
            goToRecord = {},
            goToHistory = {},
            onGoProfile = {},
            onLogout = {}
        )
    }
}
