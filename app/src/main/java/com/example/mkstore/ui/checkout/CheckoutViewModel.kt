package com.example.mkstore.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _orderPlaced = MutableStateFlow(false)
    val orderPlaced: StateFlow<Boolean> = _orderPlaced.asStateFlow()

    fun placeOrder() {
        viewModelScope.launch {
            repository.clearCart()
            _orderPlaced.value = true
        }
    }
}
