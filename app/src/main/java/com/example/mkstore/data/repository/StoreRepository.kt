package com.example.mkstore.data.repository

import com.example.mkstore.data.local.CartDao
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.remote.ApiService
import com.example.mkstore.data.remote.dto.ProductDto
import com.example.mkstore.data.remote.dto.RatingDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val apiService: ApiService,
    private val cartDao: CartDao,
    private val productRepository: ProductRepository
) {
    // Remote & Supabase Combined
    suspend fun getProducts(): List<ProductDto> {
        val resultList = mutableListOf<ProductDto>()

        // 1. Fetch from Supabase PostgreSQL Database
        try {
            val supabaseProducts = productRepository.getProducts()
            for (p in supabaseProducts) {
                resultList.add(
                    ProductDto(
                        id = p.id?.toInt() ?: (1000..9999).random(),
                        title = p.name,
                        price = p.price,
                        description = "Supabase Live Item - Premium Quality",
                        category = "electronics",
                        image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                        rating = RatingDto(4.8, 120)
                    )
                )
            }
        } catch (e: Exception) {
            // Ignore Supabase error if offline
        }

        // 2. Fetch from ApiService (FakeStoreAPI)
        try {
            val apiProducts = apiService.getProducts()
            resultList.addAll(apiProducts)
        } catch (e: Exception) {
            // Ignore API error
        }

        // 3. Fallback if both returned 0 products
        if (resultList.isEmpty()) {
            resultList.addAll(getDefaultFallbackProducts())
        }

        return resultList
    }

    suspend fun getCategories(): List<String> {
        return try {
            apiService.getCategories()
        } catch (e: Exception) {
            listOf("electronics", "jewelery", "men's clothing", "women's clothing")
        }
    }

    suspend fun getProductsByCategory(category: String): List<ProductDto> {
        val all = getProducts()
        val filtered = all.filter { it.category.equals(category, ignoreCase = true) }
        return if (filtered.isNotEmpty()) filtered else all
    }

    suspend fun getProductById(id: Int): ProductDto {
        return try {
            apiService.getProductById(id)
        } catch (e: Exception) {
            getProducts().find { it.id == id } ?: getDefaultFallbackProducts().first()
        }
    }

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

    private fun getDefaultFallbackProducts(): List<ProductDto> {
        return listOf(
            ProductDto(
                id = 1,
                title = "Fjallraven - Foldsack No. 1 Backpack",
                price = 109.95,
                description = "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
                category = "men's clothing",
                image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                rating = RatingDto(3.9, 120)
            ),
            ProductDto(
                id = 2,
                title = "Mens Casual Premium Slim Fit T-Shirts",
                price = 22.3,
                description = "Slim-fit style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing.",
                category = "men's clothing",
                image = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                rating = RatingDto(4.1, 259)
            ),
            ProductDto(
                id = 3,
                title = "Mens Cotton Jacket",
                price = 55.99,
                description = "great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors.",
                category = "men's clothing",
                image = "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
                rating = RatingDto(4.7, 500)
            ),
            ProductDto(
                id = 4,
                title = "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
                price = 695.0,
                description = "From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean's pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.",
                category = "jewelery",
                image = "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
                rating = RatingDto(4.6, 400)
            )
        )
    }
}
