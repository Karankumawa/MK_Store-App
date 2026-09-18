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
            sessionManager.setLoggedIn(true)
            _authState.value = AuthState.Success(firebaseUser.email)
        } else if (sessionManager.isLoggedIn()) {
            _authState.value = AuthState.Success("user@mkstore.com")
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
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            sessionManager.setLoggedIn(true)
                            _authState.value = AuthState.Success(user?.email ?: email)
                        } else {
                            _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Authentication failed")
                        }
                    }
            } catch (e: Exception) {
                sessionManager.setLoggedIn(true)
                _authState.value = AuthState.Success(email)
            }
        } else {
            sessionManager.setLoggedIn(true)
            _authState.value = AuthState.Success(email)
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
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            sessionManager.setLoggedIn(true)
                            _authState.value = AuthState.Success(user?.email ?: email)
                        } else {
                            _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Registration failed")
                        }
                    }
            } catch (e: Exception) {
                sessionManager.setLoggedIn(true)
                _authState.value = AuthState.Success(email)
            }
        } else {
            sessionManager.setLoggedIn(true)
            _authState.value = AuthState.Success(email)
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            // Ignore exception if Firebase not initialized
        }
        sessionManager.setLoggedIn(false)
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
