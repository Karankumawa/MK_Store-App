package com.example.mkstore.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.remote.dto.ProductDto
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDto> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadProducts()
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = repository.getCategories()
                _uiState.value = _uiState.value.copy(categories = categories)
            } catch (e: Exception) {
                // handle error or ignore for mock
            }
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
                _uiState.value = _uiState.value.copy(isLoading = false, products = products)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }
}
