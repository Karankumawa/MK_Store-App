package com.example.mkstore.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mkstore.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var showGoogleDialog by remember { mutableStateOf(false) }
    var googleAccountEmail by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    if (showGoogleDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleDialog = false },
            title = { Text("Sign in with Google", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your Google Account email to continue:")
                    OutlinedTextField(
                        value = googleAccountEmail,
                        onValueChange = { googleAccountEmail = it },
                        label = { Text("Google Account Email") },
                        placeholder = { Text("user@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (googleAccountEmail.isNotBlank()) {
                            showGoogleDialog = false
                            viewModel.loginWithGoogleAccount(
                                googleEmail = googleAccountEmail,
                                googleName = googleAccountEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                            )
                        }
                    },
                    enabled = googleAccountEmail.isNotBlank()
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSignUpMode) "Create Account 📝" else "Welcome to MK Store 🛍️", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hero Gradient Header Card
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "MK Store Logo",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSignUpMode) "Join MK Store Today!" else "Sign In to Your Account",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isSignUpMode) "Create an account to track orders & sync cart across devices." else "Enter your email & password to manage your orders & wishlist.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (authState is AuthState.Error) viewModel.resetState()
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("user@example.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (authState is AuthState.Error) viewModel.resetState()
                        },
                        label = { Text("Password (Min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = "Toggle password visibility")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (authState is AuthState.Error) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isSignUpMode) {
                                viewModel.signUp(email, password)
                            } else {
                                viewModel.login(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = authState !is AuthState.Loading,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text(if (isSignUpMode) "Create Account ➔" else "Sign In ➔", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Admin Fill Helper Pill
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        email = "admin@mkstore.com"
                        password = "admin@123"
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Admin", tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text(text = "Quick Admin Demo Credentials", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Tap to auto-fill: admin@mkstore.com / admin@123", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showGoogleDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = authState !is AuthState.Loading,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continue with Google", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                isSignUpMode = !isSignUpMode
                viewModel.resetState()
            }) {
                Text(if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Sign Up", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = onLoginSuccess) {
                Text("Skip for now / Continue as Guest", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}
