package com.example.waterreminder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.waterreminder.ui.WaterViewModel

@Composable
fun LoginPage(
    viewModel: WaterViewModel,
    onGoHome: () -> Unit,
    onGoWelcome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AppTheme {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "💧 WaterReminder",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        viewModel.loginByName(
                            nameInput = nameInput,
                            onExistingUser = { onGoHome() },
                            onNewUser = { errorMessage = "User not found" },
                            onError = { e ->
                                errorMessage = "Login failed: ${e.message}"
                            }
                        )
                    },
                    enabled = nameInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                if (errorMessage != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { onGoWelcome() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    }
}