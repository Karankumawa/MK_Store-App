package com.example.mkstore.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mkstore.data.model.OrderModel
import com.example.mkstore.data.model.ProductModel
import com.example.mkstore.data.model.UserProfileModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onSignOutClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    // Dialog state for new product
    var newName by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var newStock by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("electronics") }

    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add New Product") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Price ($)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newStock, onValueChange = { newStock = it }, label = { Text("Stock Quantity") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newCategory, onValueChange = { newCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newDesc, onValueChange = { newDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    val price = newPrice.toDoubleOrNull() ?: 0.0
                    val stock = newStock.toIntOrNull() ?: 0
                    if (newName.isNotBlank() && price > 0) {
                        viewModel.addProduct(newName, price, newDesc, stock, newCategory)
                        newName = ""
                        newPrice = ""
                        newDesc = ""
                        newStock = ""
                        showAddProductDialog = false
                    }
                }) {
                    Text("Add Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MK Store Admin Panel", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSignOutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Products") },
                    label = { Text("Products") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Orders") },
                    label = { Text("Orders") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Users") },
                    label = { Text("Users") }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { showAddProductDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when (selectedTab) {
                    0 -> AdminProductsTab(products = state.products, onDelete = { viewModel.deleteProduct(it) })
                    1 -> AdminOrdersTab(orders = state.orders, onStatusChange = { id, status -> viewModel.updateOrderStatus(id, status) })
                    2 -> AdminUsersTab(users = state.users, onToggleRole = { uid, role -> viewModel.toggleUserRole(uid, role) }, onToggleBlock = { uid, isBlocked -> viewModel.toggleUserBlock(uid, isBlocked) })
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(
    products: List<ProductModel>,
    onDelete: (String) -> Unit
) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products in store. Tap '+' to add a product!")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Price: $${product.price} | Stock: ${product.stockQuantity}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Category: ${product.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onDelete(product.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<OrderModel>,
    onStatusChange: (String, String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No customer orders found.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Order: ${order.orderId}", fontWeight = FontWeight.Bold)
                            Text(
                                text = order.status,
                                fontWeight = FontWeight.Bold,
                                color = when (order.status) {
                                    "Delivered" -> Color(0xFF4CAF50)
                                    "Shipped" -> Color(0xFF2196F3)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Address: ${order.shippingAddress}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = order.status == "Pending", onClick = { onStatusChange(order.orderId, "Pending") }, label = { Text("Pending") })
                            FilterChip(selected = order.status == "Shipped", onClick = { onStatusChange(order.orderId, "Shipped") }, label = { Text("Shipped") })
                            FilterChip(selected = order.status == "Delivered", onClick = { onStatusChange(order.orderId, "Delivered") }, label = { Text("Delivered") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersTab(
    users: List<UserProfileModel>,
    onToggleRole: (String, String) -> Unit,
    onToggleBlock: (String, Boolean) -> Unit
) {
    if (users.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No registered users found.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(users) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = user.displayName.ifBlank { "User" }, fontWeight = FontWeight.Bold)
                            Surface(color = if (user.role == "admin") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
                                Text(text = user.role.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { onToggleRole(user.uid, user.role) }) {
                                Text(if (user.role == "admin") "Make User" else "Make Admin")
                            }
                            Button(
                                onClick = { onToggleBlock(user.uid, user.isBlocked) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (user.isBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            ) {
                                Text(if (user.isBlocked) "Unblock" else "Block")
                            }
                        }
                    }
                }
            }
        }
    }
}
