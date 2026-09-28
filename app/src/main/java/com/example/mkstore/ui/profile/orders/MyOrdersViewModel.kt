package com.example.mkstore.ui.profile.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.SupabaseOrder
import com.example.mkstore.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _orders = MutableStateFlow<List<SupabaseOrder>>(emptyList())
    val orders: StateFlow<List<SupabaseOrder>> = _orders.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().ifBlank { "guest_user" }
            val list = supabaseRepository.getUserOrders(userEmail)
            _orders.value = list
        }
    }
}
