package com.example.mkstore.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
    }

    fun loginWithEmail() {
        viewModelScope.launch {
            if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "Please enter both email and password")
                return@launch
            }
            if (_uiState.value.password.length < 6) {
                _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(1200L) // Simulate secure authentication
            sessionManager.setLoggedIn(true)
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(1500L) // Simulate Google Sign-In SDK dialog & token exchange
            sessionManager.setLoggedIn(true)
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
        }
    }
}
