package com.example.mkstore.ui.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mkstore.data.local.SessionManager
import com.example.mkstore.data.model.UserProfile
import com.example.mkstore.data.repository.UserRepository
import com.example.mkstore.ui.auth.AuthUiState
import com.example.mkstore.ui.profile.ProfileUiState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainAppViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    val profileUiState: StateFlow<ProfileUiState> = _authUiState.flatMapLatest { state ->
        if (state is AuthUiState.Success) {
            userRepository.observeProfile(state.uid).map { profile ->
                if (profile != null) {
                    ProfileUiState.Success(profile)
                } else {
                    ProfileUiState.Error("Profile not found in Firestore")
                }
            }.catch { e ->
                emit(ProfileUiState.Error(e.localizedMessage ?: "Error loading profile"))
            }
        } else {
            flowOf(ProfileUiState.Loading)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState.Loading
    )

    init {
        checkAutoLogin()
    }

    fun checkAutoLogin() {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            _authUiState.value = AuthUiState.Success(currentUser.uid)
        } else if (sessionManager.isLoggedIn()) {
            _authUiState.value = AuthUiState.Success("local_session_uid")
        }
    }

    fun register(email: String, pass: String, displayName: String) {
        if (email.isBlank() || pass.isBlank() || displayName.isBlank()) {
            _authUiState.value = AuthUiState.Error("All fields are required")
            return
        }
        _authUiState.value = AuthUiState.Loading
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: ""
                    val profile = UserProfile(
                        uid = uid,
                        displayName = displayName,
                        email = email,
                        bio = "Welcome to MK Store!"
                    )
                    viewModelScope.launch {
                        try {
                            userRepository.saveOrUpdateProfile(profile)
                        } catch (e: Exception) {
                            // ignore if firestore offline
                        }
                        sessionManager.saveUserData(displayName, email)
                        _authUiState.value = AuthUiState.Success(uid)
                    }
                }
                .addOnFailureListener { e ->
                    _authUiState.value = AuthUiState.Error(e.localizedMessage ?: "Registration failed")
                }
        } else {
            sessionManager.saveUserData(displayName, email)
            _authUiState.value = AuthUiState.Success("local_session_uid")
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authUiState.value = AuthUiState.Error("Please enter both email and password")
            return
        }
        _authUiState.value = AuthUiState.Loading
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: ""
                    sessionManager.saveUserData(email.substringBefore("@"), email)
                    _authUiState.value = AuthUiState.Success(uid)
                }
                .addOnFailureListener { e ->
                    _authUiState.value = AuthUiState.Error(e.localizedMessage ?: "Login failed")
                }
        } else {
            sessionManager.saveUserData(email.substringBefore("@"), email)
            _authUiState.value = AuthUiState.Success("local_session_uid")
        }
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String?) -> Unit) {
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { e -> onComplete(false, e.localizedMessage) }
        } else {
            onComplete(true, "Password reset email sent (simulation)")
        }
    }

    fun signInWithGoogle(context: Context, serverClientId: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val rawNonce = UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setNonce(hashedNonce)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context = context, request = request)

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                    val firebaseAuth = auth
                    if (firebaseAuth != null) {
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnSuccessListener { authResult ->
                                val user = authResult.user
                                val uid = user?.uid ?: ""
                                val profile = UserProfile(
                                    uid = uid,
                                    displayName = user?.displayName ?: "Google User",
                                    email = user?.email ?: "",
                                    bio = "Signed in with Google"
                                )
                                viewModelScope.launch {
                                    userRepository.saveOrUpdateProfile(profile)
                                    sessionManager.saveUserData(profile.displayName, profile.email)
                                    _authUiState.value = AuthUiState.Success(uid)
                                }
                            }
                            .addOnFailureListener { e ->
                                _authUiState.value = AuthUiState.Error(e.localizedMessage ?: "Google Authentication Failed")
                            }
                    } else {
                        val email = googleIdTokenCredential.id
                        sessionManager.saveUserData(googleIdTokenCredential.displayName ?: "Google User", email)
                        _authUiState.value = AuthUiState.Success("local_session_uid")
                    }
                }
            } catch (e: GetCredentialException) {
                _authUiState.value = AuthUiState.Error("Google Sign-In canceled or failed")
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.localizedMessage ?: "Google Sign-In Failed")
            }
        }
    }

    fun updateBio(uid: String, newBio: String) {
        viewModelScope.launch {
            try {
                userRepository.updateBio(uid, newBio)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            // ignore
        }
        sessionManager.clearSession()
        _authUiState.value = AuthUiState.Idle
    }

    fun resetAuthError() {
        if (_authUiState.value is AuthUiState.Error) {
            _authUiState.value = AuthUiState.Idle
        }
    }
}
