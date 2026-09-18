package com.example.mkstore.ui.auth

import androidx.lifecycle.ViewModel
import com.example.mkstore.data.local.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            sessionManager.setLoggedIn(true)
            _authState.value = AuthState.Success(currentUser.email)
        } else {
            sessionManager.setLoggedIn(false)
            _authState.value = AuthState.Idle
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        sessionManager.setLoggedIn(true)
                        _authState.value = AuthState.Success(user?.email)
                    } else {
                        _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Authentication failed")
                    }
                }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.localizedMessage ?: "Firebase error")
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
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        sessionManager.setLoggedIn(true)
                        _authState.value = AuthState.Success(user?.email)
                    } else {
                        _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Registration failed")
                    }
                }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.localizedMessage ?: "Firebase error")
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Ignore if firebase not initialized
        }
        sessionManager.setLoggedIn(false)
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
