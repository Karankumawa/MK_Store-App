package com.example.mkstore.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.CartEntity
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartEntity> = emptyList(),
    val totalPrice: Double = 0.0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: StoreRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = repository.cartItems
        .map { items ->
            val total = items.sumOf { it.price * it.quantity }
            CartUiState(cartItems = items, totalPrice = total)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState()
        )

    fun updateQuantity(item: CartEntity, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                repository.addToCart(item.copy(quantity = newQuantity))
            } else {
                repository.removeFromCart(item.id)
            }
        }
    }

    fun removeItem(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }
}
