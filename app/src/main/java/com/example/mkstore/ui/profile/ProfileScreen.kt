package com.example.mkstore.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mkstore.ui.auth.AuthState
import com.example.mkstore.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val user = (authState as? AuthState.Success)?.user

    var isEditing by remember { mutableStateOf(false) }
    var name by remember(user) { mutableStateOf(user?.name ?: "Guest User") }
    var email by remember(user) { mutableStateOf(user?.email ?: "guest@mkstore.com") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                if (isEditing) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            authViewModel.sessionManager.saveUserData(name, email)
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile Changes")
                    }
                } else {
                    Text(
                        text = if (user != null && user.name.isNotBlank()) user.name else name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (user != null && user.email.isNotBlank()) user.email else email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "MK Store Member • Verified",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "My Orders", fontWeight = FontWeight.SemiBold)
                        HorizontalDivider()
                        Text(text = "Shipping Addresses", fontWeight = FontWeight.SemiBold)
                        HorizontalDivider()
                        Text(text = "Payment Methods", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Button(
                onClick = {
                    authViewModel.logout()
                    onLogoutClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}
