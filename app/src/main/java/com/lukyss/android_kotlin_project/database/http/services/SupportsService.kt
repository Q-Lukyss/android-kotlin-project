// SupportService.kt
package com.lukyss.android_kotlin_project.database.http.services

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lukyss.android_kotlin_project.database.http.models.Supports
import kotlinx.coroutines.tasks.await

class SupportsService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Supports")

    // Récupère les supports pour un cours donné
    suspend fun getSupportsForCourse(courseId: String): List<Supports> {
        return try {
            val querySnapshot = collectionRef.whereEqualTo("id_cours", courseId).get().await()
            querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { Supports.fromFirestore(it, doc.id) }
            }
        } catch (e: Exception) {
            Log.e("SupportService", "Erreur lors de la récupération des supports", e)
            throw e
        }
    }

    // Ajoute un nouveau support
    suspend fun addSupport(support: Supports) {
        val data = hashMapOf(
            "id_cours" to support.idCours,
            "nom" to support.nom
        )
        collectionRef.add(data).await()
    }

    // Met à jour un support existant
    suspend fun updateSupport(support: Supports) {
        val docRef = collectionRef.document(support.uid)
        val data = hashMapOf("nom" to support.nom)
        docRef.update(data as Map<String, Any>).await()
    }

    // Supprime un support
    suspend fun deleteSupport(supportId: String) {
        collectionRef.document(supportId).delete().await()
    }
}
