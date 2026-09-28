package com.example.mkstore.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.model.BannerItem
import com.example.mkstore.data.model.OrderModel
import com.example.mkstore.data.model.ProductModel
import com.example.mkstore.data.model.UserProfileModel
import com.example.mkstore.data.repository.AdminRepository
import com.example.mkstore.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val isLoading: Boolean = false,
    val products: List<ProductModel> = emptyList(),
    val orders: List<OrderModel> = emptyList(),
    val users: List<UserProfileModel> = emptyList(),
    val banners: List<BannerItem> = emptyList(),
    val message: String? = null,
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observeAdminData()
        loadBanners()
    }

    fun loadBanners() {
        viewModelScope.launch {
            try {
                val banners = storeRepository.getBanners()
                _uiState.value = _uiState.value.copy(banners = banners)
            } catch (e: Exception) { }
        }
    }

    fun addBanner(title: String, subtitle: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                storeRepository.addBanner(BannerItem(title = title, subtitle = subtitle))
                loadBanners()
                _uiState.value = _uiState.value.copy(message = "Banner offer added successfully!")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun deleteBanner(id: Long) {
        viewModelScope.launch {
            try {
                storeRepository.deleteBanner(id)
                loadBanners()
                _uiState.value = _uiState.value.copy(message = "Banner offer removed")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    private fun observeAdminData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                adminRepository.observeProducts(),
                adminRepository.observeOrders(),
                adminRepository.observeUsers()
            ) { products, orders, users ->
                _uiState.value.copy(
                    isLoading = false,
                    products = products,
                    orders = orders,
                    users = users
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun addProduct(name: String, price: Double, description: String, stockQuantity: Int, category: String, imageUrl: String = "") {
        viewModelScope.launch {
            try {
                val newProduct = ProductModel(
                    name = name,
                    price = price,
                    description = description,
                    stockQuantity = stockQuantity,
                    category = category,
                    imageUrl = imageUrl.ifBlank { "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg" }
                )
                adminRepository.addProduct(newProduct)
                _uiState.value = _uiState.value.copy(message = "Product added successfully!")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun updateProduct(product: ProductModel) {
        viewModelScope.launch {
            try {
                adminRepository.updateProduct(product)
                _uiState.value = _uiState.value.copy(message = "Product updated successfully!")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                adminRepository.deleteProduct(productId)
                _uiState.value = _uiState.value.copy(message = "Product deleted")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                adminRepository.updateOrderStatus(orderId, newStatus)
                _uiState.value = _uiState.value.copy(message = "Order status updated to $newStatus")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun toggleUserRole(uid: String, currentRole: String) {
        viewModelScope.launch {
            try {
                val newRole = if (currentRole == "admin") "user" else "admin"
                adminRepository.updateUserRole(uid, newRole)
                _uiState.value = _uiState.value.copy(message = "User role changed to $newRole")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun toggleUserBlock(uid: String, isCurrentlyBlocked: Boolean) {
        viewModelScope.launch {
            try {
                adminRepository.toggleUserBlockStatus(uid, !isCurrentlyBlocked)
                _uiState.value = _uiState.value.copy(message = if (isCurrentlyBlocked) "User unblocked" else "User blocked")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}
