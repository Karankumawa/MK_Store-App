package com.example.mkstore.data.repository

import com.example.mkstore.data.local.CartDao
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.remote.ApiService
import com.example.mkstore.data.remote.dto.ProductDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val apiService: ApiService,
    private val cartDao: CartDao
) {
    // Remote
    suspend fun getProducts(): List<ProductDto> = apiService.getProducts()
    suspend fun getCategories(): List<String> = apiService.getCategories()
    suspend fun getProductsByCategory(category: String): List<ProductDto> = apiService.getProductsByCategory(category)
    suspend fun getProductById(id: Int): ProductDto = apiService.getProductById(id)

    // Local Cart
    val cartItems: Flow<List<CartEntity>> = cartDao.getCartItems()

    suspend fun addToCart(item: CartEntity) {
        cartDao.insertOrUpdateCartItem(item)
    }

    suspend fun removeFromCart(productId: Int) {
        cartDao.removeFromCart(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
