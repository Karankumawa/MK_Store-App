package com.example.mkstore.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val orderPlaced by viewModel.orderPlaced.collectAsState()
    var address by remember { mutableStateOf("123 Green Street, Apartment 4B, New York, NY 10001") }
    var cardNumber by remember { mutableStateOf("4242 •••• •••• 4242") }
    var paymentOption by remember { mutableStateOf("Credit/Debit Card") }

    val orderId = remember { "#MKS-" + (10000..99999).random() }

    if (orderPlaced) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(56.dp)
                )
            },
            title = {
                Text(
                    text = "Order Placed Successfully! 🎉",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Order ID: $orderId",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Estimated Delivery: By Thursday, 24 Sep",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Shipping To:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                            Text(text = address, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Payment Method: $paymentOption", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onOrderSuccess,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue Shopping")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Shipping Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Delivery Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Payment Option", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = paymentOption == "Credit/Debit Card",
                        onClick = { paymentOption = "Credit/Debit Card" },
                        label = { Text("Card") }
                    )
                    FilterChip(
                        selected = paymentOption == "UPI / Google Pay",
                        onClick = { paymentOption = "UPI / Google Pay" },
                        label = { Text("UPI / GPay") }
                    )
                    FilterChip(
                        selected = paymentOption == "Cash on Delivery",
                        onClick = { paymentOption = "Cash on Delivery" },
                        label = { Text("COD") }
                    )
                }

                if (paymentOption == "Credit/Debit Card") {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Card Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = { viewModel.placeOrder() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Place Order", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
