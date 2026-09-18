package com.example.mkstore.ui.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.remote.dto.ProductDto
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: ProductDto? = null,
    val error: String? = null,
    val isAddedToCart: Boolean = false
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: StoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        val productId = savedStateHandle.get<String>("productId")?.toIntOrNull() ?: 0
        if (productId > 0) {
            loadProduct(productId)
        }
    }

    private fun loadProduct(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val product = repository.getProductById(id)
                _uiState.value = _uiState.value.copy(isLoading = false, product = product)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun addToCart() {
        val product = uiState.value.product ?: return
        viewModelScope.launch {
            val cartEntity = CartEntity(
                id = product.id,
                title = product.title,
                price = product.price,
                image = product.image,
                quantity = 1
            )
            repository.addToCart(cartEntity)
            _uiState.value = _uiState.value.copy(isAddedToCart = true)
        }
    }
}
