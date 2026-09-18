package com.example.mkstore.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mkstore.data.local.CartEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCheckoutClick: (Boolean) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (state.cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "$${String.format("%.2f", state.totalPrice)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onCheckoutClick(viewModel.sessionManager.isLoggedIn()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Proceed to Checkout")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Your cart is empty", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.updateQuantity(item, item.quantity + 1) },
                        onDecrement = { viewModel.updateQuantity(item, item.quantity - 1) },
                        onRemove = { viewModel.removeItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDecrement, modifier = Modifier.size(32.dp), contentPadding = PaddingValues(0.dp)) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = onIncrement, modifier = Modifier.size(32.dp), contentPadding = PaddingValues(0.dp)) {
                        Text("+")
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
