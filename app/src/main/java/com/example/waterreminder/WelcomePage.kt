package com.example.waterreminder

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomePage(
    viewModel: WaterViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val welcomeUiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("\uD83E\uDD64  WaterReminder")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to login"
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
                    text = "Did you drink water today?",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Name",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = welcomeUiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GenderOption(
                        label = "Male",
                        selected = welcomeUiState.gender == Gender.Male,
                        onClick = { viewModel.onGenderSelected(Gender.Male) },
                        modifier = Modifier.weight(1f)
                    )
                    GenderOption(
                        label = "Female",
                        selected = welcomeUiState.gender == Gender.Female,
                        onClick = { viewModel.onGenderSelected(Gender.Female) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Your Daily Goals",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = welcomeUiState.drinkingGoals.toString(),
                    onValueChange = { it ->
                        val value = it.toIntOrNull() ?: 0
                        viewModel.onDrinkingGoalsChange(value)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text("ml") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(5.dp))
                if (welcomeUiState.isWelcomePageNextEnabled){
                    Text(
                        text = viewModel.checkDrinkingStatus(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Button(
                onClick = {
                    val st = welcomeUiState
                    val g = st.gender
                    if (g != null) {
                        viewModel.createUserAfterWelcome(
                            name = st.name,
                            gender = g,
                            drinkingGoals = st.drinkingGoals,
                            onDone = { onNext() }
                        )
                    }
                },
                enabled = welcomeUiState.isWelcomePageNextEnabled,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text("Next")
            }

        }
    }
}

@Composable
private fun GenderOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
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
fun PreviewWelcomePage() {
    AppTheme {
        var name by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Donald") }
        var gender by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Gender?>(Gender.Male) }
        var goalsText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("2800") }

        val goals = goalsText.toIntOrNull() ?: 0
        val isNextEnabled = name.isNotBlank() && gender != null && goals > 0

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("🥤  WaterReminder") }
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
                        text = "Did you drink water today?",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(32.dp))

                    Text(text = "Name", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(32.dp))

                    Text(text = "Gender", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GenderOption(
                            label = "Male",
                            selected = gender == Gender.Male,
                            onClick = { gender = Gender.Male },
                            modifier = Modifier.weight(1f)
                        )
                        GenderOption(
                            label = "Female",
                            selected = gender == Gender.Female,
                            onClick = { gender = Gender.Female },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(text = "Your Daily Goals", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = goalsText,
                        onValueChange = { goalsText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = { Text("ml") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(Modifier.height(5.dp))
                    if (isNextEnabled) {
                        Text(
                            text = "Let’s set your goal and start tracking!",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                Button(
                    onClick = {},
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    }
}
