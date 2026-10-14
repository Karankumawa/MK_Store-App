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
        try {
            client.from("user_profiles").upsert(profile)
        } catch (e: Exception) {
            try {
                client.from("user_profiles").insert(profile)
            } catch (e2: Exception) {
                android.util.Log.e("SupabaseRLS", "Failed to save profile under RLS: ${e2.localizedMessage}")
            }
        }
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

    suspend fun getUserProfileByEmail(email: String): SupabaseUserProfile? = withContext(Dispatchers.IO) {
        try {
            client.from("user_profiles")
                .select {
                    filter { eq("email", email) }
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
        try {
            client.from("orders").insert(order)
            android.util.Log.d("OrderPlacement", "Successfully created order ${order.orderId} in Supabase")
        } catch (e: Exception) {
            android.util.Log.e("OrderPlacement", "Error inserting order in Supabase", e)
        }
    }

    suspend fun getUserOrders(userId: String = "guest_user"): List<SupabaseOrder> = withContext(Dispatchers.IO) {
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

    // 4. Shipping Addresses Table (Deduplicated)
    suspend fun addShippingAddress(address: SupabaseShippingAddress) = withContext(Dispatchers.IO) {
        try {
            val existing = getShippingAddresses(address.userId)
            val match = existing.find {
                it.streetAddress.equals(address.streetAddress, ignoreCase = true) &&
                it.city.equals(address.city, ignoreCase = true)
            }
            if (match == null) {
                client.from("shipping_addresses").insert(address)
            }
        } catch (e: Exception) {
            try { client.from("shipping_addresses").insert(address) } catch (e2: Exception) { }
        }
    }

    suspend fun getShippingAddresses(userId: String = "guest_user"): List<SupabaseShippingAddress> = withContext(Dispatchers.IO) {
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

    // 5. Payment Methods Table (Deduplicated)
    suspend fun addPaymentMethod(paymentMethod: SupabasePaymentMethod) = withContext(Dispatchers.IO) {
        try {
            val existing = getPaymentMethods(paymentMethod.userId)
            val match = existing.find {
                it.details.equals(paymentMethod.details, ignoreCase = true) ||
                (it.provider != null && it.provider.equals(paymentMethod.provider, ignoreCase = true))
            }
            if (match == null) {
                client.from("payment_methods").insert(paymentMethod)
            }
        } catch (e: Exception) {
            try { client.from("payment_methods").insert(paymentMethod) } catch (e2: Exception) { }
        }
    }

    suspend fun getPaymentMethods(userId: String = "guest_user"): List<SupabasePaymentMethod> = withContext(Dispatchers.IO) {
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

    // 6. Sliding Banners / Offers Table
    suspend fun getBanners(): List<BannerItem> = withContext(Dispatchers.IO) {
        try {
            client.from("banners")
                .select()
                .decodeList<BannerItem>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addBanner(banner: BannerItem) = withContext(Dispatchers.IO) {
        client.from("banners").insert(banner)
    }

    suspend fun deleteBanner(id: Long) = withContext(Dispatchers.IO) {
        client.from("banners").delete {
            filter { eq("id", id) }
        }
    }
}
