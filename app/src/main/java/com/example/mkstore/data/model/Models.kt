package com.example.mkstore.data.model

data class UserProfileModel(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "user", // "user" or "admin"
    val isBlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val stockQuantity: Int = 0,
    val category: String = "",
    val imageUrl: String = ""
)

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val itemsList: String = "",
    val totalAmount: Double = 0.0,
    val shippingAddress: String = "",
    val status: String = "Pending", // "Pending" | "Shipped" | "Delivered"
    val updatedAt: Long = System.currentTimeMillis()
)
