package com.example.waterreminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@Composable
fun LoginPage(
    viewModel: WaterViewModel,
    onGoHome: () -> Unit,
    onGoWelcome: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    AppTheme {
        Scaffold {
            LoginPageContent(
                padding = it,
                nameInput = nameInput,
                onNameChange = { nameInput = it },
                errorMessage = errorMessage,
                isLoading = isLoading,
                onLoginClick = {
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
                            errorMessage = "User not found. Please register."
                        },
                        onError = { e ->
                            isLoading = false
                            errorMessage = "Login failed: ${e.message}"
                        }
                    )
                },
                onGoWelcome = onGoWelcome
            )
        }
    }
}

@Composable
private fun LoginPageContent(
    padding: PaddingValues,
    nameInput: String,
    onNameChange: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onGoWelcome: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        Text(
            text = "💧 WaterReminder",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1f))

        OutlinedTextField(
            value = nameInput,
            onValueChange = onNameChange,
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = errorMessage != null,
            supportingText = {
                if (errorMessage != null) {
                    Text(errorMessage)
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            enabled = nameInput.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account?")
            TextButton(onClick = onGoWelcome, enabled = !isLoading) {
                Text("Register")
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Page")
@Composable
fun PreviewLoginPage() {
    AppTheme {
        Scaffold {
            LoginPageContent(
                padding = it,
                nameInput = "Donald",
                onNameChange = {},
                errorMessage = null,
                isLoading = false,
                onLoginClick = {},
                onGoWelcome = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Login Page - Loading")
@Composable
fun PreviewLoginPageLoading() {
    AppTheme {
        Scaffold {
            LoginPageContent(
                padding = it,
                nameInput = "Donald",
                onNameChange = {},
                errorMessage = null,
                isLoading = true,
                onLoginClick = {},
                onGoWelcome = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Login Page - Error")
@Composable
fun PreviewLoginPageError() {
    AppTheme {
        Scaffold {
            LoginPageContent(
                padding = it,
                nameInput = "Donald",
                onNameChange = {},
                errorMessage = "This user does not exist.",
                isLoading = false,
                onLoginClick = {},
                onGoWelcome = {}
            )
        }
    }
}
