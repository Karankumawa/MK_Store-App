package com.example.mkstore.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                Text(
                    text = "MK Store Member",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EST. 2025",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                    viewModel.logout()
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
