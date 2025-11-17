package com.example.waterreminder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import com.example.waterreminder.ui.Gender
import com.example.waterreminder.ui.ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    viewModel: ViewModel,
    onBack: () -> Unit
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
                    Text("📜 History")
                }

            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (uiState.recordList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No records yet!", style = MaterialTheme.typography.headlineSmall)
                    Text("Go add some water intake records.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.recordList.reversed()) { record ->
                        HistoryRecordItem(record = record)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun HistoryRecordItem(record: Pair<Int, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${record.first} ml",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = record.second,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "History Page With Records")
@Composable
fun PreviewHistoryPage() {
    AppTheme {
        val previewViewModel = viewModel<ViewModel>()
        // Simulate some records for the preview
        previewViewModel.onNameChange("Ray")
        previewViewModel.onGenderSelected(Gender.Male)
        previewViewModel.drinkingRecordChanged(500)
        previewViewModel.waterTypeChanged("☕ Warm")
        previewViewModel.addRecord()
        previewViewModel.drinkingRecordChanged(350)
        previewViewModel.waterTypeChanged("🧊 Ice")
        previewViewModel.addRecord()
        previewViewModel.drinkingRecordChanged(1000)
        previewViewModel.waterTypeChanged("🔥 Hot")
        previewViewModel.addRecord()

        HistoryPage(
            viewModel = previewViewModel,
            onBack = {}
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "History Page Empty")
@Composable
fun PreviewHistoryPageEmpty() {
    AppTheme {
        HistoryPage(
            viewModel = viewModel(),
            onBack = {}
        )
    }
}
