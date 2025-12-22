package com.example.waterreminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPage(
    viewModel: WaterViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
){

    val recordPageUiState by viewModel.uiState.collectAsState()
    var selectedWaterType by remember { mutableStateOf<String?>(null) }
    val waterTypes = listOf("🧊 Ice", "☕ Warm", "🔥 Hot")

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("➕  Add a Water Record")
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
            Column {
                Text(
                    text = "How much water did you drink?",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = recordPageUiState.drinkingRecords.toString(),
                    onValueChange = { it ->
                        val value = it.toIntOrNull() ?: 0
                        viewModel.drinkingRecordChanged(value)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text("ml") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "What type of water did you drink?",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    waterTypes.forEach { waterType ->
                        WaterTypeCard(
                            text = waterType,
                            isSelected = selectedWaterType == waterType,
                            onClick = { selectedWaterType = waterType; viewModel.waterTypeChanged(waterType) }
                        )
                    }
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomCenter)) {

                Button(
                    onClick = { viewModel.addRecord(); onNext() },
                    enabled = recordPageUiState.isRecordPageRecordEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Record")
                }

                Button(
                    onClick = { onBack() },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WaterTypeCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (isSelected) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    } else {
        CardDefaults.cardColors()
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = colors
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewRecordPage() {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("➕  Add a Water Record") }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "How much water did you drink?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = "500",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = { Text("ml") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "What type of water did you drink?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("🧊 Ice", "☕ Warm", "🔥 Hot").forEach { waterType ->
                            WaterTypeCard(
                                text = waterType,
                                isSelected = waterType == "☕ Warm",
                                onClick = {}
                            )
                        }
                    }
                }

                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Button(
                        onClick = {},
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Record")
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}
