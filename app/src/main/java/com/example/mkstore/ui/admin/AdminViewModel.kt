package com.example.mkstore.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.model.BannerItem
import com.example.mkstore.data.model.OrderModel
import com.example.mkstore.data.model.Product
import com.example.mkstore.data.model.ProductModel
import com.example.mkstore.data.model.UserProfileModel
import com.example.mkstore.data.repository.AdminRepository
import com.example.mkstore.data.repository.ProductRepository
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
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAdminData()
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val products = adminRepository.getProducts()
                val orders = adminRepository.getOrders()
                val users = adminRepository.getUsers()
                val banners = storeRepository.getBanners()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products,
                    orders = orders,
                    users = users,
                    banners = banners
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun addProduct(name: String, price: Double, description: String, stockQuantity: Int, category: String, imageUrl: String = "") {
        viewModelScope.launch {
            try {
                val newProduct = Product(
                    name = name,
                    price = price,
                    description = description.ifBlank { null },
                    category = category.ifBlank { null },
                    imageUrl = imageUrl.ifBlank { null }
                )
                productRepository.insertProduct(newProduct)
                loadAdminData()
                _uiState.value = _uiState.value.copy(message = "Product added to Supabase!")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                val idLong = productId.toLongOrNull() ?: 0L
                if (idLong > 0) {
                    adminRepository.deleteProduct(idLong)
                    loadAdminData()
                }
                _uiState.value = _uiState.value.copy(message = "Product deleted")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun addBanner(title: String, subtitle: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                storeRepository.addBanner(BannerItem(title = title, subtitle = subtitle))
                loadAdminData()
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
                loadAdminData()
                _uiState.value = _uiState.value.copy(message = "Banner offer removed")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                adminRepository.updateOrderStatus(orderId, newStatus)
                loadAdminData()
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
                loadAdminData()
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
                loadAdminData()
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
