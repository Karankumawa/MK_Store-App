package com.example.mkstore.ui.auth

data class UserProfileData(
    val name: String = "",
    val email: String = "",
    val photoUrl: String = ""
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfileData) : AuthState()
    data class Error(val message: String) : AuthState()
}
