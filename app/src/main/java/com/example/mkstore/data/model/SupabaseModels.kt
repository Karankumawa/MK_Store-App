package com.example.mkstore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseUserProfile(
    val uid: String,
    val email: String,
    @SerialName("display_name")
    val displayName: String? = null,
    val bio: String? = null,
    val role: String = "user",
    @SerialName("is_blocked")
    val isBlocked: Boolean = false
)

@Serializable
data class SupabaseCartItem(
    val id: Long? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("product_id")
    val productId: Long,
    val title: String,
    val price: Double,
    val image: String? = null,
    val quantity: Int = 1
)

@Serializable
data class SupabaseOrder(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("items_summary")
    val itemsSummary: String,
    @SerialName("total_amount")
    val totalAmount: Double,
    @SerialName("shipping_address")
    val shippingAddress: String,
    @SerialName("payment_method")
    val paymentMethod: String = "Cash on Delivery",
    val status: String = "Pending"
)

@Serializable
data class SupabaseShippingAddress(
    val id: Long? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("street_address")
    val streetAddress: String,
    val city: String,
    val state: String? = null,
    @SerialName("postal_code")
    val postalCode: String,
    val country: String = "India",
    @SerialName("is_default")
    val isDefault: Boolean = false
)

@Serializable
data class SupabasePaymentMethod(
    val id: Long? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("payment_type")
    val paymentType: String,
    val provider: String? = null,
    val details: String? = null,
    @SerialName("is_default")
    val isDefault: Boolean = false
)

@Serializable
data class BannerItem(
    val id: Long? = null,
    val title: String = "",
    val subtitle: String = "",
    @SerialName("image_url")
    val imageUrl: String? = null
)
