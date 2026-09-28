package com.example.mkstore.ui.profile.addresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.SupabaseShippingAddress
import com.example.mkstore.data.repository.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingAddressesViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<SupabaseShippingAddress>>(emptyList())
    val addresses: StateFlow<List<SupabaseShippingAddress>> = _addresses.asStateFlow()

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().trim()
            if (!sessionManager.isLoggedIn() || userEmail.isBlank()) {
                _addresses.value = emptyList()
                return@launch
            }
            val list = supabaseRepository.getShippingAddresses(userEmail)
            _addresses.value = list
        }
    }

    fun addAddress(fullName: String, phone: String, street: String, city: String, postalCode: String) {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().trim()
            if (!sessionManager.isLoggedIn() || userEmail.isBlank()) return@launch
            val newAddress = SupabaseShippingAddress(
                userId = userEmail,
                fullName = fullName,
                phoneNumber = phone,
                streetAddress = street,
                city = city,
                postalCode = postalCode
            )
            supabaseRepository.addShippingAddress(newAddress)
            loadAddresses()
        }
    }
}
