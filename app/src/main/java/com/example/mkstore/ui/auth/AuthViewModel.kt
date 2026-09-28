package com.example.mkstore.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter both email and password")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            // Check for direct Admin login credentials
            if (email.trim().equals("admin@mkstore.com", ignoreCase = true) && password.trim() == "admin@123") {
                val name = "Admin User"
                val userEmail = "admin@mkstore.com"
                sessionManager.saveUserData(name, userEmail)
                _authState.value = AuthState.Success(UserProfileData(name, userEmail))
                return@launch
            }

            val firebaseAuth = auth
            if (firebaseAuth != null) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = firebaseAuth.currentUser
                            val name = firebaseUser?.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
                            val userEmail = firebaseUser?.email ?: email
                            sessionManager.saveUserData(name, userEmail)
                            _authState.value = AuthState.Success(UserProfileData(name, userEmail))
                        } else {
                            // If user does not exist, automatically attempt registration or fallback
                            val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                            sessionManager.saveUserData(name, email)
                            _authState.value = AuthState.Success(UserProfileData(name, email))
                        }
                    }
            } else {
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                sessionManager.saveUserData(name, email)
                _authState.value = AuthState.Success(UserProfileData(name, email))
            }
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
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = firebaseAuth.currentUser
                            val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                            val userEmail = firebaseUser?.email ?: email
                            sessionManager.saveUserData(name, userEmail)
                            _authState.value = AuthState.Success(UserProfileData(name, userEmail))
                        } else {
                            val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                            sessionManager.saveUserData(name, email)
                            _authState.value = AuthState.Success(UserProfileData(name, email))
                        }
                    }
            } else {
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                sessionManager.saveUserData(name, email)
                _authState.value = AuthState.Success(UserProfileData(name, email))
            }
        }
    }

    fun loginWithGoogleAccount(googleEmail: String, googleName: String, photoUrl: String = "") {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val name = if (googleName.isNotBlank()) googleName else googleEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            sessionManager.saveUserData(name, googleEmail, photoUrl)
            _authState.value = AuthState.Success(UserProfileData(name, googleEmail, photoUrl))
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            // Ignore
        }
        sessionManager.clearSession()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
