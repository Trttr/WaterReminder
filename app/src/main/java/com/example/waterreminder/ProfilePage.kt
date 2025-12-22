package com.example.waterreminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    viewModel: WaterViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Local editable state (we do NOT allow changing name/key)
    var gender by remember(uiState.gender) { mutableStateOf(uiState.gender) }
    var goalsText by remember(uiState.drinkingGoals) { mutableStateOf(uiState.drinkingGoals.toString()) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val goals = goalsText.trim().toIntOrNull() ?: 0
    val canSave = (gender != null) && goals > 0 && !isSaving

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("👤 Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { if (!isSaving) onBack() }) {
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
                .padding(24.dp)
        ) {
            // Read-only name
            Text(
                text = "Name",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.name,
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Gender",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderCard(
                    label = "Male",
                    selected = gender == Gender.Male,
                    onClick = { if (!isSaving) gender = Gender.Male },
                    modifier = Modifier.weight(1f)
                )
                GenderCard(
                    label = "Female",
                    selected = gender == Gender.Female,
                    onClick = { if (!isSaving) gender = Gender.Female },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Daily drinking goal",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = goalsText,
                onValueChange = { if (!isSaving) goalsText = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving,
                trailingIcon = { Text("ml") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    error = null
                    isSaving = true
                    val g = gender
                    if (g == null) {
                        isSaving = false
                        error = "Please select a gender"
                        return@Button
                    }
                    if (goals <= 0) {
                        isSaving = false
                        error = "Please enter a valid goal"
                        return@Button
                    }

                    viewModel.updateProfileGenderGoals(
                        gender = g,
                        goals = goals,
                        onDone = {
                            isSaving = false
                            onBack()
                        },
                        onError = { t ->
                            isSaving = false
                            error = t.message ?: "Update failed"
                        }
                    )
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { if (!isSaving) onBack() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun GenderCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewProfilePage() {
    com.example.compose.AppTheme {
        // Preview-only: simple static UI without ViewModel
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("👤  Edit Profile") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
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
                    .padding(24.dp)
            ) {
                Text("Name", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = "Donald",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(24.dp))

                Text("Gender", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GenderCard(label = "Male", selected = true, onClick = {}, modifier = Modifier.weight(1f))
                    GenderCard(label = "Female", selected = false, onClick = {}, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                Text("Daily drinking goal", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = "2500",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text("ml") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(28.dp))

                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Save") }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
            }
        }
    }
}