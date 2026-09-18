package com.example.mkstore.ui.profile

import androidx.lifecycle.ViewModel
import com.example.mkstore.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "John Doe",
    val email: String = "john.doe@example.com",
    val phone: String = "+1 234 567 890",
    val isEditing: Boolean = false,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun toggleEditing() {
        val editing = !_uiState.value.isEditing
        _uiState.value = _uiState.value.copy(
            isEditing = editing,
            successMessage = if (!editing) "Profile updated successfully!" else null
        )
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun logout() {
        sessionManager.setLoggedIn(false)
    }
}
