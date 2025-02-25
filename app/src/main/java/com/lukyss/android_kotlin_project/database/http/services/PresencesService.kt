// PresenceService.kt
package com.lukyss.android_kotlin_project.database.http.services

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lukyss.android_kotlin_project.database.http.models.Presences
import kotlinx.coroutines.tasks.await

class PresencesService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Presences")

    // Récupère toutes les présences
    suspend fun getAllPresences(): List<Presences> {
        return try {
            val q = collectionRef
            val querySnapshot = q.get().await()
            querySnapshot.documents.map { doc ->
                Presences.fromFirestore(doc.data, doc.id)
            }
        } catch (e: Exception) {
            Log.e("PresenceService", "Erreur lors de la récupération des présences", e)
            throw e
        }
    }

    // Met à jour une présence (ex. pour ajouter un justificatif)
    suspend fun updatePresence(presence: Presences) {
        try {
            val docRef = collectionRef.document(presence.uid)
            val data = hashMapOf("justificatifs" to presence.justificatifs)
            docRef.update(data as Map<String, Any>).await()
        } catch (e: Exception) {
            Log.e("PresenceService", "Erreur lors de la mise à jour de la présence", e)
            throw e
        }
    }

    // Récupère les présences pour un utilisateur donné
    suspend fun getPresencesForUser(userId: String): List<Presences> {
        try {
            val q = collectionRef.whereEqualTo("id_user", userId)
            val querySnapshot = q.get().await()
            return querySnapshot.documents.map { doc ->
                Presences.fromFirestore(doc.data, doc.id)
            }
        } catch (e: Exception) {
            Log.e("PresenceService", "Erreur lors de la récupération des présences", e)
            throw e
        }
    }
}
