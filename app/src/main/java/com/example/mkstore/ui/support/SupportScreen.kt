package com.example.mkstore.ui.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Help & Support 💬", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = "Support", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "We are here 24/7 for you!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "Have questions about your order, delivery, or returns? Contact our support experts anytime.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text(text = "Contact Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Toll-Free Helpline", fontWeight = FontWeight.Bold)
                            Text(text = "+91 1800-MK-STORE (Available 24/7)", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Support Email", fontWeight = FontWeight.Bold)
                            Text(text = "support@mkstore.com", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Text(text = "Frequently Asked Questions (FAQs)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Q: How do I track my active orders?", fontWeight = FontWeight.Bold)
                    Text(text = "A: Go to Profile -> My Orders and tap on any order to view live shipment tracking timeline.", style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider()
                    Text(text = "Q: What are the available payment options?", fontWeight = FontWeight.Bold)
                    Text(text = "A: We support UPI (Google Pay, PhonePe, Paytm), Credit/Debit Cards, NetBanking, and Cash on Delivery.", style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider()
                    Text(text = "Q: What is the return policy?", fontWeight = FontWeight.Bold)
                    Text(text = "A: All products are covered under our hassle-free 7-day replacement and return guarantee.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
