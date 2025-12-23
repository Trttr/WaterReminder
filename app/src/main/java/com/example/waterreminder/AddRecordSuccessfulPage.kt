package com.example.waterreminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@Composable
fun AddRecordSuccessfulPage(
    viewModel: WaterViewModel,
    onNext: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val latestRecord = viewModel.returnLatestRecord()

    AddRecordSuccessfulPageContent(
        name = uiState.name,
        latestRecordAmount = latestRecord.first,
        latestRecordType = latestRecord.second,
        dailyCount = uiState.drinkingCount,
        dailyGoal = uiState.drinkingGoals,
        progress = viewModel.getPercentage(),
        message = viewModel.returnRecordMessage(),
        onNext = onNext
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecordSuccessfulPageContent(
    name: String,
    latestRecordAmount: Int,
    latestRecordType: String,
    dailyCount: Int,
    dailyGoal: Int,
    progress: Float,
    message: String,
    onNext: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("🎉 Successfully Added!") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            CongratulationCard(name, latestRecordAmount, latestRecordType)

            Spacer(Modifier.height(32.dp))

            ProgressSection(dailyCount, dailyGoal, progress)

            Spacer(Modifier.height(32.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Finish", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun CongratulationCard(name: String, amount: Int, type: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nice Job, $name!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            if (amount > 0) {
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "Your latest record:",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = "Amount")
                    Spacer(Modifier.width(8.dp))
                    Text("$amount ml", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(16.dp))
                    WaterTypeIcon(type)
                    Spacer(Modifier.width(8.dp))
                    Text(type, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun ProgressSection(count: Int, goal: Int, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Today's Progress",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ProgressBar(progress = progress)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${count}ml / ${goal}ml",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun ProgressBar(progress: Float) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round,
    )
}

@Composable
private fun WaterTypeIcon(waterType: String) {
    val icon = when (waterType) {
        "Ice" -> "🧊"
        "Warm" -> "☕"
        "Hot" -> "🔥"
        else -> "💧"
    }
    Text(text = icon, style = MaterialTheme.typography.headlineSmall)
}

@Preview(showBackground = true)
@Composable
fun PreviewAddRecordSuccessfulPage() {
    AppTheme {
        AddRecordSuccessfulPageContent(
            name = "Ray",
            latestRecordAmount = 500,
            latestRecordType = "Warm",
            dailyCount = 1500,
            dailyGoal = 3700,
            progress = 0.4f,
            message = "Great start! Every drop counts. 💧",
            onNext = {}
        )
    }
}
