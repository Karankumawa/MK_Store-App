package com.example.mkstore.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.SupabaseOrder
import com.example.mkstore.data.repository.StoreRepository
import com.example.mkstore.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val supabaseRepository: SupabaseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _orderPlaced = MutableStateFlow(false)
    val orderPlaced: StateFlow<Boolean> = _orderPlaced.asStateFlow()

    fun placeOrder() {
        viewModelScope.launch {
            try {
                val cartList = repository.cartItems.first()
                val total = cartList.sumOf { it.price * it.quantity }
                val summary = cartList.joinToString(", ") { "${it.title} x${it.quantity}" }
                val userEmail = sessionManager.getUserEmail().ifBlank { "guest_user" }
                val randomNum = (10000..99999).random()

                val newOrder = SupabaseOrder(
                    orderId = "#MKS-$randomNum",
                    userId = userEmail,
                    itemsSummary = summary.ifBlank { "Store Order" },
                    totalAmount = total,
                    shippingAddress = "123 Green Street, NY",
                    paymentMethod = "Cash on Delivery",
                    status = "Pending"
                )
                supabaseRepository.createOrder(newOrder)
            } catch (e: Exception) { }

            repository.clearCart()
            _orderPlaced.value = true
        }
    }
}
