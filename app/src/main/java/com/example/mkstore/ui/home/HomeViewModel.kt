package com.example.mkstore.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.model.BannerItem
import com.example.mkstore.data.remote.dto.ProductDto
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDto> = emptyList(),
    val filteredProducts: List<ProductDto> = emptyList(),
    val banners: List<BannerItem> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val favoriteIds: Set<Int> = emptySet(),
    val cartCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val cartItems: Flow<List<CartEntity>> = repository.cartItems

    init {
        loadBanners()
        loadCategories()
        loadProducts()
        observeCartCount()
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            repository.cartItems.collectLatest { list ->
                val totalQty = list.sumOf { it.quantity }
                _uiState.value = _uiState.value.copy(cartCount = totalQty)
            }
        }
    }

    fun updateCartQuantity(item: CartEntity, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                repository.addToCart(item.copy(quantity = newQuantity))
            } else {
                repository.removeFromCart(item.id)
            }
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun loadBanners() {
        viewModelScope.launch {
            try {
                val banners = repository.getBanners()
                _uiState.value = _uiState.value.copy(banners = banners)
            } catch (e: Exception) { }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = repository.getCategories()
                _uiState.value = _uiState.value.copy(categories = categories)
            } catch (e: Exception) { }
        }
    }

    fun loadProducts(category: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedCategory = category)
            try {
                val products = if (category == null) {
                    repository.getProducts()
                } else {
                    repository.getProductsByCategory(category)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products,
                    filteredProducts = filterProductsList(products, _uiState.value.searchQuery)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredProducts = filterProductsList(_uiState.value.products, query)
        )
    }

    fun toggleFavorite(productId: Int) {
        val currentFavs = _uiState.value.favoriteIds.toMutableSet()
        if (currentFavs.contains(productId)) {
            currentFavs.remove(productId)
        } else {
            currentFavs.add(productId)
        }
        _uiState.value = _uiState.value.copy(favoriteIds = currentFavs)
    }

    private fun filterProductsList(products: List<ProductDto>, query: String): List<ProductDto> {
        if (query.isBlank()) return products
        return products.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
    }
}
