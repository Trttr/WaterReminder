package com.example.waterreminder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@Composable
fun RecordPage(
    viewModel: WaterViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    RecordScreen(
        drinkingRecords = uiState.drinkingRecords,
        onDrinkingRecordsChange = { viewModel.drinkingRecordChanged(it) },
        waterType = uiState.waterType,
        onWaterTypeChange = { viewModel.waterTypeChanged(it) },
        isRecordEnabled = uiState.isRecordPageRecordEnabled,
        onRecordClick = {
            viewModel.addRecord()
            onNext()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    drinkingRecords: Int,
    onDrinkingRecordsChange: (Int) -> Unit,
    waterType: String,
    onWaterTypeChange: (String) -> Unit,
    isRecordEnabled: Boolean,
    onRecordClick: () -> Unit,
    onBack: () -> Unit
) {
    val waterTypes = listOf("🧊 Ice", "☕ Warm", "🔥 Hot")

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Add a Water Record") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "How much water did you drink?",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = if (drinkingRecords == 0) "" else drinkingRecords.toString(),
                onValueChange = {
                    onDrinkingRecordsChange(it.toIntOrNull() ?: 0)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = { Text("ml") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Amount") }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "What type of water did you drink?",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                waterTypes.forEach { type ->
                    WaterTypeCard(
                        text = type,
                        isSelected = waterType == type,
                        onClick = { onWaterTypeChange(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onRecordClick,
                enabled = isRecordEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Record")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WaterTypeCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordPage() {
    AppTheme {
        RecordScreen(
            drinkingRecords = 500,
            onDrinkingRecordsChange = {},
            waterType = "☕ Warm",
            onWaterTypeChange = {},
            isRecordEnabled = true,
            onRecordClick = {},
            onBack = {}
        )
    }
}
