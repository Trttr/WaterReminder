package com.example.waterreminder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.example.waterreminder.ui.Gender
import com.example.waterreminder.ui.ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordSuccessfulPage(
    viewModel: ViewModel,
    onNext: () -> Unit
) {
    val addRecordSuccessfulPageUiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("🎉 Successfully Added!")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp), // Increased padding
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Nice Job, ${addRecordSuccessfulPageUiState.name}!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            // Display the latest record in a Card
            val latestRecord = viewModel.returnLatestRecord()
            if (latestRecord.first > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your latest record:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Amount: ${latestRecord.first} ml",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Type: ${latestRecord.second}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ProgressBar(progress = viewModel.getPercentage())
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${addRecordSuccessfulPageUiState.drinkingCount}ml / ${addRecordSuccessfulPageUiState.drinkingGoals}ml",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = viewModel.returnRecordMessage(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finish", style = MaterialTheme.typography.titleMedium)
            }
        }
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewAddRecordSuccessfulPage() {
    AppTheme {
        val previewViewModel: ViewModel = viewModel()
        previewViewModel.onNameChange("Donald")
        previewViewModel.onGenderSelected(Gender.Male)
        previewViewModel.drinkingRecordChanged(500)
        previewViewModel.waterTypeChanged("☕ Warm")
        previewViewModel.addRecord()
        previewViewModel.drinkingRecordChanged(1000)
        previewViewModel.waterTypeChanged("🧊 Ice")
        previewViewModel.addRecord()

        AddRecordSuccessfulPage(
            viewModel = previewViewModel,
            onNext = {}
        )
    }
}
