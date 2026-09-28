package com.example.mkstore

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MkStoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            // Firebase initialized
        }
    }
}
