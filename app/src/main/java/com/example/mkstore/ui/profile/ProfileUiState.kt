package com.example.mkstore.ui.profile

import com.example.mkstore.data.model.UserProfile

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
