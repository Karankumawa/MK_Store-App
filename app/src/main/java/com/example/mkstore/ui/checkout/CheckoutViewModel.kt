package com.example.mkstore.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.*
import com.example.mkstore.data.repository.StoreRepository
import com.example.mkstore.data.repository.SupabaseRepository
import com.example.mkstore.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val savedFullName: String = "",
    val savedPhone: String = "",
    val savedStreet: String = "",
    val savedCity: String = "",
    val savedPostalCode: String = "",
    val savedPaymentOption: String = "UPI / Google Pay",
    val savedPaymentDetail: String = "",
    val orderPlaced: Boolean = false
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val supabaseRepository: SupabaseRepository,
    private val userRepository: UserRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadSavedUserCheckoutData()
    }

    private fun loadSavedUserCheckoutData() {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().trim().lowercase()
            if (!sessionManager.isLoggedIn() || userEmail.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    savedFullName = "",
                    savedPhone = "",
                    savedStreet = "",
                    savedCity = "",
                    savedPostalCode = "",
                    savedPaymentOption = "UPI / Google Pay",
                    savedPaymentDetail = ""
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Load saved user profile
                val profile = userRepository.getUserProfileByEmail(userEmail)
                val userName = profile?.displayName ?: sessionManager.getUserName()

                // 2. Load saved shipping addresses
                val addresses = supabaseRepository.getShippingAddresses(userEmail)
                val defaultAddr = addresses.find { it.isDefault } ?: addresses.firstOrNull()

                // 3. Load saved payment methods
                val payments = supabaseRepository.getPaymentMethods(userEmail)
                val defaultPay = payments.find { it.isDefault } ?: payments.firstOrNull()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    savedFullName = userName.ifBlank { "User Name" },
                    savedPhone = defaultAddr?.phoneNumber ?: "",
                    savedStreet = defaultAddr?.streetAddress ?: "",
                    savedCity = defaultAddr?.city ?: "",
                    savedPostalCode = defaultAddr?.postalCode ?: "",
                    savedPaymentOption = defaultPay?.provider ?: defaultPay?.paymentType ?: "UPI / Google Pay",
                    savedPaymentDetail = defaultPay?.details ?: ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun placeOrder(
        fullName: String,
        phone: String,
        streetAddress: String,
        city: String,
        postalCode: String,
        paymentOption: String,
        paymentDetail: String
    ) {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail().trim().lowercase()
            if (!sessionManager.isLoggedIn() || userEmail.isBlank()) return@launch

            try {
                // 1. Sync updated Shipping Address to Supabase
                val newAddress = SupabaseShippingAddress(
                    userId = userEmail,
                    fullName = fullName,
                    phoneNumber = phone,
                    streetAddress = streetAddress,
                    city = city,
                    postalCode = postalCode,
                    isDefault = true
                )
                supabaseRepository.addShippingAddress(newAddress)

                // 2. Sync updated Payment Method to Supabase
                val newPayment = SupabasePaymentMethod(
                    userId = userEmail,
                    paymentType = if (paymentDetail.contains("@")) "UPI" else "Card",
                    provider = paymentOption,
                    details = paymentDetail,
                    isDefault = true
                )
                supabaseRepository.addPaymentMethod(newPayment)

                // 3. Sync User Profile changes (Full Name) to Supabase
                val updatedProfile = UserProfile(
                    uid = userEmail,
                    email = userEmail,
                    displayName = fullName
                )
                userRepository.saveOrUpdateProfile(updatedProfile)

                // 4. Create Order in Supabase
                val cartList = repository.cartItems.first()
                val total = cartList.sumOf { it.price * it.quantity }
                val summary = cartList.joinToString(", ") { "${it.title} x${it.quantity}" }
                val randomNum = (10000..99999).random()

                val newOrder = SupabaseOrder(
                    orderId = "#MKS-$randomNum",
                    userId = userEmail,
                    itemsSummary = summary.ifBlank { "Store Order" },
                    totalAmount = total,
                    shippingAddress = "$streetAddress, $city - $postalCode",
                    paymentMethod = paymentOption,
                    status = "Pending"
                )
                supabaseRepository.createOrder(newOrder)
            } catch (e: Exception) { }

            // Clear Cart & set Order Placed
            repository.clearCart()
            _uiState.value = _uiState.value.copy(orderPlaced = true)
        }
    }
}
