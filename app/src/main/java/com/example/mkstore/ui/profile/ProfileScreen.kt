package com.example.mkstore.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mkstore.ui.auth.AuthState
import com.example.mkstore.ui.auth.AuthViewModel
import com.example.mkstore.ui.navigation.AppBottomNavBar
import com.example.mkstore.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val isLoggedIn = authState is AuthState.Success
    val user = (authState as? AuthState.Success)?.user
    val isAdmin = user?.email?.contains("admin", ignoreCase = true) == true

    var isEditing by remember { mutableStateOf(false) }
    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account Hub 👤", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentRoute = Screen.Profile.route,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 1. Hero User Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isLoggedIn && user != null && user.name.isNotBlank()) user.name.take(1).uppercase() else "G",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoggedIn && user != null) {
                            if (isEditing) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Display Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        authViewModel.sessionManager.saveUserData(name, email)
                                        authViewModel.checkCurrentUser()
                                        isEditing = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Save Changes")
                                }
                            } else {
                                Text(
                                    text = user.name.ifBlank { "MK Store Member" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = if (isAdmin) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = if (isAdmin) "👑 ADMIN MANAGER" else "🌟 VERIFIED SHOPPER",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Guest Shopper",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Sign in to access your orders, addresses, and saved payment methods.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // 2. Profile Quick Action Grid Cards
                Text(text = "Quick Account Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileMenuTile(
                        icon = Icons.Default.ShoppingBag,
                        title = "My Orders",
                        subtitle = "Track purchases",
                        onClick = onOrdersClick,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileMenuTile(
                        icon = Icons.Default.HomeWork,
                        title = "Addresses",
                        subtitle = "Delivery locations",
                        onClick = onAddressesClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileMenuTile(
                        icon = Icons.Default.CreditCard,
                        title = "Payment",
                        subtitle = "Cards & UPI IDs",
                        onClick = onPaymentClick,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAdmin) {
                        ProfileMenuTile(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "Admin Panel",
                            subtitle = "Store controls",
                            onClick = { onNavigate(Screen.AdminDashboard.route) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        ProfileMenuTile(
                            icon = Icons.Default.Person,
                            title = "Wishlist",
                            subtitle = "Saved items",
                            onClick = { onNavigate(Screen.Favorites.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Bottom Action Button
            if (isLoggedIn) {
                Button(
                    onClick = {
                        authViewModel.logout()
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out Account", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log In / Create Account ➔", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}
