package com.example.mkstore.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.UserProfile
import com.example.mkstore.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val sessionManager: SessionManager,
    private val userRepository: UserRepository
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
            saveUserToSupabase(email, name)
            _authState.value = AuthState.Success(UserProfileData(name, email, photoUrl))
        } else if (sessionManager.isLoggedIn() && sessionManager.getUserEmail().isNotBlank()) {
            val name = sessionManager.getUserName()
            val email = sessionManager.getUserEmail()
            val photoUrl = sessionManager.getUserPhotoUrl()
            _authState.value = AuthState.Success(UserProfileData(name, email, photoUrl))
        } else {
            sessionManager.clearSession()
            _authState.value = AuthState.Idle
        }
    }

    private fun saveUserToSupabase(email: String, displayName: String) {
        viewModelScope.launch {
            try {
                val profile = UserProfile(
                    uid = email,
                    email = email,
                    displayName = displayName
                )
                val role = if (email.equals("admin@mkstore.com", ignoreCase = true) || email.contains("admin", ignoreCase = true)) "admin" else "user"
                userRepository.saveOrUpdateProfile(profile, role = role)
            } catch (e: Exception) { }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter both email and password")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val trimmedEmail = email.trim()

            // Check for direct Admin login credentials
            if (trimmedEmail.equals("admin@mkstore.com", ignoreCase = true) && password.trim() == "admin@123") {
                val adminName = "Admin User"
                sessionManager.saveUserData(adminName, trimmedEmail)
                saveUserToSupabase(trimmedEmail, adminName)
                _authState.value = AuthState.Success(UserProfileData(adminName, trimmedEmail))
                return@launch
            }

            // Verify if user is registered in Supabase database
            val existingUser = userRepository.getUserProfileByEmail(trimmedEmail)
            if (existingUser == null) {
                _authState.value = AuthState.Error("Account not found in database. Please click 'Sign Up' to register.")
                return@launch
            }

            if (existingUser.isBlocked) {
                _authState.value = AuthState.Error("Your account has been blocked by Admin.")
                return@launch
            }

            val name = existingUser.displayName?.ifBlank { null }
                ?: trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, trimmedEmail)
            _authState.value = AuthState.Success(UserProfileData(name, trimmedEmail))
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter both email and password")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val trimmedEmail = email.trim()
            val existingUser = userRepository.getUserProfileByEmail(trimmedEmail)
            if (existingUser != null) {
                _authState.value = AuthState.Error("Account with this email already exists in database. Please Sign In.")
                return@launch
            }

            val name = trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, trimmedEmail)
            saveUserToSupabase(trimmedEmail, name)
            _authState.value = AuthState.Success(UserProfileData(name, trimmedEmail))
        }
    }

    fun loginWithGoogleAccount(googleEmail: String, googleName: String, photoUrl: String = "") {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val name = if (googleName.isNotBlank()) googleName else googleEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, googleEmail, photoUrl)
            saveUserToSupabase(googleEmail, name)
            _authState.value = AuthState.Success(UserProfileData(name, googleEmail, photoUrl))
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) { }
        sessionManager.clearSession()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
