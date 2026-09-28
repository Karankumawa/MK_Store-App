package com.example.mkstore

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MkStoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("project-13664921779")
                    .setApplicationId("1:229066895617:android:3a89e9f2b1892842010214")
                    .setGcmSenderId("229066895617")
                    .setApiKey("AIzaSyB-mkstore-project-key-229066895617")
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            // Firebase initialized
        }
    }
}
