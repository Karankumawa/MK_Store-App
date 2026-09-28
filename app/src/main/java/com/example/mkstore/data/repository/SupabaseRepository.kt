package com.example.mkstore.data.repository

import com.example.mkstore.data.model.*
import com.example.mkstore.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRepository @Inject constructor() {

    private val client = SupabaseClientProvider.client

    // 1. User Profiles Table
    suspend fun saveOrUpdateUserProfile(profile: SupabaseUserProfile) = withContext(Dispatchers.IO) {
        client.from("user_profiles").upsert(profile)
    }

    suspend fun getUserProfile(uid: String): SupabaseUserProfile? = withContext(Dispatchers.IO) {
        try {
            client.from("user_profiles")
                .select {
                    filter { eq("uid", uid) }
                }
                .decodeSingleOrNull<SupabaseUserProfile>()
        } catch (e: Exception) {
            null
        }
    }

    // 2. Cart Items Table
    suspend fun getCartItems(userId: String): List<SupabaseCartItem> = withContext(Dispatchers.IO) {
        try {
            client.from("cart_items")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SupabaseCartItem>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToCart(item: SupabaseCartItem) = withContext(Dispatchers.IO) {
        client.from("cart_items").insert(item)
    }

    suspend fun removeFromCart(cartItemId: Long) = withContext(Dispatchers.IO) {
        client.from("cart_items").delete {
            filter { eq("id", cartItemId) }
        }
    }

    suspend fun clearCart(userId: String) = withContext(Dispatchers.IO) {
        client.from("cart_items").delete {
            filter { eq("user_id", userId) }
        }
    }

    // 3. Orders Table
    suspend fun createOrder(order: SupabaseOrder) = withContext(Dispatchers.IO) {
        client.from("orders").insert(order)
    }

    suspend fun getUserOrders(userId: String): List<SupabaseOrder> = withContext(Dispatchers.IO) {
        try {
            client.from("orders")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SupabaseOrder>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 4. Shipping Addresses Table
    suspend fun addShippingAddress(address: SupabaseShippingAddress) = withContext(Dispatchers.IO) {
        client.from("shipping_addresses").insert(address)
    }

    suspend fun getShippingAddresses(userId: String): List<SupabaseShippingAddress> = withContext(Dispatchers.IO) {
        try {
            client.from("shipping_addresses")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SupabaseShippingAddress>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 5. Payment Methods Table
    suspend fun addPaymentMethod(paymentMethod: SupabasePaymentMethod) = withContext(Dispatchers.IO) {
        client.from("payment_methods").insert(paymentMethod)
    }

    suspend fun getPaymentMethods(userId: String): List<SupabasePaymentMethod> = withContext(Dispatchers.IO) {
        try {
            client.from("payment_methods")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SupabasePaymentMethod>()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
