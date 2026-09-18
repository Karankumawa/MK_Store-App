package com.example.mkstore.ui.profile

import androidx.lifecycle.ViewModel
import com.example.mkstore.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel() {
    fun logout() {
        sessionManager.setLoggedIn(false)
    }
}
