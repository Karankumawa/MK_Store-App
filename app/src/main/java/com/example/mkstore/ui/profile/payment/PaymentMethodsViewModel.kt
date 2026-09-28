package com.example.mkstore.ui.profile.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.SupabasePaymentMethod
import com.example.mkstore.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodsViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _paymentMethods = MutableStateFlow<List<SupabasePaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<SupabasePaymentMethod>> = _paymentMethods.asStateFlow()

    init {
        loadPaymentMethods()
    }

    fun loadPaymentMethods() {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().ifBlank { "guest_user" }
            val list = supabaseRepository.getPaymentMethods(userEmail)
            _paymentMethods.value = list
        }
    }

    fun addPaymentMethod(provider: String, details: String) {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().ifBlank { "guest_user" }
            val type = if (details.contains("@")) "UPI" else "Card"
            val newMethod = SupabasePaymentMethod(
                userId = userEmail,
                paymentType = type,
                provider = provider,
                details = details
            )
            supabaseRepository.addPaymentMethod(newMethod)
            loadPaymentMethods()
        }
    }
}
