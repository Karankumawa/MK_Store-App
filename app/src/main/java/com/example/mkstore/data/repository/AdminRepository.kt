package com.example.mkstore.data.repository

import com.example.mkstore.data.model.OrderModel
import com.example.mkstore.data.model.ProductModel
import com.example.mkstore.data.model.SupabaseOrder
import com.example.mkstore.data.model.SupabaseUserProfile
import com.example.mkstore.data.model.UserProfileModel
import com.example.mkstore.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val productRepository: ProductRepository
) {
    // 1. Supabase Live Products
    suspend fun getProducts(): List<ProductModel> = withContext(Dispatchers.IO) {
        try {
            val list = productRepository.getProducts()
            list.map {
                ProductModel(
                    id = (it.id ?: 0L).toString(),
                    name = it.displayName,
                    price = it.price,
                    description = it.displayDescription,
                    category = it.displayCategory,
                    imageUrl = it.displayImageUrl ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        try {
            productRepository.deleteProduct(id)
        } catch (e: Exception) { }
    }

    // 2. Supabase Live Users
    suspend fun getUsers(): List<UserProfileModel> = withContext(Dispatchers.IO) {
        try {
            val client = SupabaseClientProvider.client
            val list = client.from("user_profiles").select().decodeList<SupabaseUserProfile>()
            list.map {
                UserProfileModel(
                    uid = it.uid,
                    email = it.email,
                    displayName = it.displayName ?: it.email.substringBefore("@"),
                    role = it.role,
                    isBlocked = it.isBlocked
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateUserRole(uid: String, newRole: String) = withContext(Dispatchers.IO) {
        try {
            val client = SupabaseClientProvider.client
            client.from("user_profiles").update({
                set("role", newRole)
            }) {
                filter { eq("uid", uid) }
            }
        } catch (e: Exception) { }
    }

    suspend fun toggleUserBlockStatus(uid: String, isBlocked: Boolean) = withContext(Dispatchers.IO) {
        try {
            val client = SupabaseClientProvider.client
            client.from("user_profiles").update({
                set("is_blocked", isBlocked)
            }) {
                filter { eq("uid", uid) }
            }
        } catch (e: Exception) { }
    }

    // 3. Supabase Live Orders
    suspend fun getOrders(): List<OrderModel> = withContext(Dispatchers.IO) {
        try {
            val client = SupabaseClientProvider.client
            val list = client.from("orders").select().decodeList<SupabaseOrder>()
            list.map {
                OrderModel(
                    orderId = it.orderId,
                    userId = it.userId,
                    itemsList = it.itemsSummary,
                    totalAmount = it.totalAmount,
                    shippingAddress = it.shippingAddress,
                    status = it.status
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) = withContext(Dispatchers.IO) {
        try {
            val client = SupabaseClientProvider.client
            client.from("orders").update({
                set("status", newStatus)
            }) {
                filter { eq("order_id", orderId) }
            }
        } catch (e: Exception) { }
    }
}
