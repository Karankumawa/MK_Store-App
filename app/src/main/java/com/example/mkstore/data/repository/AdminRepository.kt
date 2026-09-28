package com.example.mkstore.data.repository

import com.example.mkstore.data.model.OrderModel
import com.example.mkstore.data.model.ProductModel
import com.example.mkstore.data.model.UserProfileModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor() {

    private val db: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }

    suspend fun getUserRole(uid: String): String {
        val firestore = db ?: return "user"
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("role") ?: "user"
        } catch (e: Exception) {
            "user"
        }
    }

    // Products Management
    suspend fun addProduct(product: ProductModel) {
        val firestore = db ?: return
        val docRef = if (product.id.isBlank()) firestore.collection("products").document() else firestore.collection("products").document(product.id)
        val finalProduct = product.copy(id = docRef.id)
        docRef.set(finalProduct, SetOptions.merge()).await()
    }

    suspend fun updateProduct(product: ProductModel) {
        val firestore = db ?: return
        if (product.id.isBlank()) return
        firestore.collection("products").document(product.id)
            .set(product, SetOptions.merge()).await()
    }

    suspend fun deleteProduct(productId: String) {
        val firestore = db ?: return
        firestore.collection("products").document(productId).delete().await()
    }

    fun observeProducts(): Flow<List<ProductModel>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("products").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(ProductModel::class.java)
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    // Orders Management
    fun observeOrders(): Flow<List<OrderModel>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("orders").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(OrderModel::class.java)
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val firestore = db ?: return
        firestore.collection("orders").document(orderId)
            .update("status", newStatus, "updatedAt", System.currentTimeMillis()).await()
    }

    // Users Management
    fun observeUsers(): Flow<List<UserProfileModel>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(UserProfileModel::class.java)
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateUserRole(uid: String, newRole: String) {
        val firestore = db ?: return
        firestore.collection("users").document(uid).update("role", newRole).await()
    }

    suspend fun toggleUserBlockStatus(uid: String, isBlocked: Boolean) {
        val firestore = db ?: return
        firestore.collection("users").document(uid).update("isBlocked", isBlocked).await()
    }
}
