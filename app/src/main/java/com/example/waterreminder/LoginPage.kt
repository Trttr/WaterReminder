package com.example.waterreminder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme

@Composable
fun LoginPage(
    viewModel: WaterViewModel,
    onGoHome: () -> Unit,
    onGoWelcome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        viewModel.loginByName(
                            nameInput = nameInput,
                            onExistingUser = {
                                isLoading = false
                                onGoHome()
                            },
                            onNewUser = {
                                isLoading = false
                                errorMessage = "User not found"
                            },
                            onError = { e ->
                                isLoading = false
                                errorMessage = "Login failed: ${e.message}"
                            }
                        )
                    },
                    enabled = nameInput.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }

                if (isLoading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Register")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Page")
@Composable
fun PreviewLoginPage() {
    AppTheme {
        // Preview 里不使用 ViewModel（避免数据库/网络/协程）
        var nameInput by remember { mutableStateOf("Donald") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(true) }

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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { isLoading = true },
                    enabled = nameInput.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }

                if (isLoading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
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
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Register")
                }
            }
        }
    }
}