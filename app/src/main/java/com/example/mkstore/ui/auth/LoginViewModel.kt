package com.example.mkstore.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun loginWithEmail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(1000L) // Simulate network request
            if (_uiState.value.email.isNotBlank() && _uiState.value.password.isNotBlank()) {
                _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Please enter valid email and password")
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            delay(1000L) // Simulate Google Auth
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
        }
    }
}
