package com.example.mkstore.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.remote.dto.ProductDto
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favoriteProducts: List<ProductDto> = emptyList(),
    val cartCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteProducts()
        observeCartCount()
    }

    fun loadFavoriteProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val allProducts = repository.getProducts()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    favoriteProducts = allProducts.take(3)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            repository.cartItems.collectLatest { list ->
                val totalQty = list.sumOf { it.quantity }
                _uiState.value = _uiState.value.copy(cartCount = totalQty)
            }
        }
    }

    fun addToCart(product: ProductDto) {
        viewModelScope.launch {
            val cartEntity = CartEntity(
                id = product.id,
                title = product.title,
                price = product.price,
                image = product.image,
                quantity = 1
            )
            repository.addToCart(cartEntity)
        }
    }

    fun removeFavorite(productId: Int) {
        viewModelScope.launch {
            val currentList = _uiState.value.favoriteProducts.filter { it.id != productId }
            _uiState.value = _uiState.value.copy(favoriteProducts = currentList)
        }
    }
}
