package com.example.mkstore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.model.Product
import com.example.mkstore.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(val products: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val products = repository.getProducts()
                _uiState.value = ProductUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.localizedMessage ?: "Error connecting to Supabase")
            }
        }
    }

    fun addProduct(name: String, price: Double, description: String? = null, category: String? = null, imageUrl: String? = null) {
        if (name.isBlank() || price <= 0) return
        viewModelScope.launch {
            try {
                val newProduct = Product(
                    name = name,
                    price = price,
                    description = description?.ifBlank { null },
                    category = category?.ifBlank { null },
                    imageUrl = imageUrl?.ifBlank { null }
                )
                repository.insertProduct(newProduct)
                loadProducts()
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.localizedMessage ?: "Error adding product")
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(id)
                loadProducts()
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.localizedMessage ?: "Error deleting product")
            }
        }
    }
}
