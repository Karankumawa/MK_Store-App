package com.example.mkstore.data.repository

import android.util.Log
import com.example.mkstore.data.local.CartDao
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.model.BannerItem
import com.example.mkstore.data.model.SupabaseCartItem
import com.example.mkstore.data.remote.ApiService
import com.example.mkstore.data.remote.dto.ProductDto
import com.example.mkstore.data.remote.dto.RatingDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val apiService: ApiService,
    private val cartDao: CartDao,
    private val productRepository: ProductRepository,
    private val supabaseRepository: SupabaseRepository
) {
    // Fetch live products from Supabase PostgreSQL Database
    suspend fun getProducts(): List<ProductDto> {
        val resultList = mutableListOf<ProductDto>()

        try {
            val supabaseProducts = productRepository.getProducts()
            Log.d("SupabaseDebug", "Fetched ${supabaseProducts.size} products from Supabase")
            for (p in supabaseProducts) {
                val img = p.displayImageUrl ?: "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=400"
                resultList.add(
                    ProductDto(
                        id = p.id?.toInt() ?: (1000..9999).random(),
                        title = p.displayName,
                        price = p.price,
                        description = p.displayDescription,
                        category = p.displayCategory,
                        image = img,
                        rating = RatingDto(4.8, 120)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("SupabaseDebug", "Error fetching products from Supabase", e)
        }

        return resultList
    }

    suspend fun getCategories(): List<String> {
        return try {
            val products = getProducts()
            val categories = products.map { it.category }.distinct().filter { it.isNotBlank() }
            if (categories.isNotEmpty()) categories else listOf("electronics", "jewelery", "men's clothing")
        } catch (e: Exception) {
            listOf("electronics", "jewelery", "men's clothing")
        }
    }

    suspend fun getProductsByCategory(category: String): List<ProductDto> {
        val all = getProducts()
        val filtered = all.filter { it.category.equals(category, ignoreCase = true) }
        return if (filtered.isNotEmpty()) filtered else all
    }

    suspend fun getProductById(id: Int): ProductDto {
        val loaded = getProducts().find { it.id == id }
        if (loaded != null) return loaded
        return getProducts().firstOrNull() ?: ProductDto(
            id = 1,
            title = "Item",
            price = 0.0,
            description = "Supabase Item",
            category = "electronics",
            image = "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=400",
            rating = RatingDto(4.5, 10)
        )
    }

    // Local Cart & Supabase Sync
    val cartItems: Flow<List<CartEntity>> = cartDao.getCartItems()

    // Favorites
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    fun toggleFavorite(productId: Int) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _favoriteIds.value = current
    }

    suspend fun getFavoriteProducts(): List<ProductDto> {
        val ids = _favoriteIds.value
        if (ids.isEmpty()) return emptyList()
        val all = getProducts()
        return all.filter { ids.contains(it.id) }
    }

    suspend fun addToCart(item: CartEntity, userId: String = "guest_user") {
        cartDao.insertOrUpdateCartItem(item)
        try {
            val supabaseCart = SupabaseCartItem(
                userId = userId,
                productId = item.id.toLong(),
                title = item.title,
                price = item.price,
                image = item.image,
                quantity = item.quantity
            )
            supabaseRepository.addToCart(supabaseCart)
        } catch (e: Exception) { }
    }

    suspend fun removeFromCart(productId: Int) {
        cartDao.removeFromCart(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Live Banners from Supabase
    suspend fun getBanners(): List<BannerItem> {
        return supabaseRepository.getBanners()
    }

    suspend fun addBanner(banner: BannerItem) {
        supabaseRepository.addBanner(banner)
    }

    suspend fun deleteBanner(id: Long) {
        supabaseRepository.deleteBanner(id)
    }
}
