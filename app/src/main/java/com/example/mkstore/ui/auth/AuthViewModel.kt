package com.example.mkstore.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel() {

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val firebaseUser = auth?.currentUser
        if (firebaseUser != null) {
            val name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "User"
            val email = firebaseUser.email ?: ""
            val photoUrl = firebaseUser.photoUrl?.toString() ?: ""
            sessionManager.saveUserData(name, email, photoUrl)
            _authState.value = AuthState.Success(UserProfileData(name, email, photoUrl))
        } else if (sessionManager.isLoggedIn()) {
            val name = sessionManager.getUserName()
            val email = sessionManager.getUserEmail()
            val photoUrl = sessionManager.getUserPhotoUrl()
            _authState.value = AuthState.Success(UserProfileData(name, email, photoUrl))
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            delay(1000L)
            val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, email)
            _authState.value = AuthState.Success(UserProfileData(name, email))
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            delay(1000L)
            val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, email)
            _authState.value = AuthState.Success(UserProfileData(name, email))
        }
    }

    fun loginWithGoogleAccount(googleEmail: String, googleName: String, photoUrl: String = "") {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            delay(1200L)
            val name = if (googleName.isNotBlank()) googleName else googleEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, googleEmail, photoUrl)
            _authState.value = AuthState.Success(UserProfileData(name, googleEmail, photoUrl))
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            // Ignore exception if Firebase not initialized
        }
        sessionManager.clearSession()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
