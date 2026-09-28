package com.example.mkstore.data.repository

import com.example.mkstore.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }

    suspend fun saveOrUpdateProfile(profile: UserProfile) {
        val db = firestore ?: return
        db.collection("users")
            .document(profile.uid)
            .set(profile, SetOptions.merge())
            .await()
    }

    fun observeProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listenerRegistration = db.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    trySend(profile)
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun updateBio(uid: String, newBio: String) {
        val db = firestore ?: return
        db.collection("users")
            .document(uid)
            .update("bio", newBio)
            .await()
    }
}
