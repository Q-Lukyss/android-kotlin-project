// SeanceService.kt
package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lukyss.android_kotlin_project.database.http.models.Seances
import kotlinx.coroutines.tasks.await
import java.util.Date

class SeancesService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Seances")

    // Récupère les séances à venir pour une liste de cours
    suspend fun getUpcomingSeancesForCourses(courseIds: List<String>): List<Seances> {
        if (courseIds.isEmpty()) return emptyList()
        val today = Date()
        val q = collectionRef
            .whereIn("id_cours", courseIds)
            .whereGreaterThanOrEqualTo("date", today)
            .orderBy("date", Query.Direction.ASCENDING)
        val querySnapshot = q.get().await()
        return querySnapshot.documents.map { doc ->
            Seances.fromFirestore(doc.data, doc.id)
        }
    }

    // Récupère les séances pour un cours donné
    suspend fun getSeancesForCourse(courseId: String): List<Seances> {
        val q = collectionRef
            .whereEqualTo("id_cours", courseId)
            .orderBy("date", Query.Direction.ASCENDING)
        val snapshot = q.get().await()
        return snapshot.documents.map { doc ->
            Seances.fromFirestore(doc.data, doc.id)
        }
    }

    // Ajoute une séance
    suspend fun addSeance(seance: Seances) {
        val data = hashMapOf(
            "date" to seance.date,
            "date_fin" to seance.dateFin,
            "id_cours" to seance.idCours,
            "lieu" to seance.lieu,
            "presences" to seance.presences
        )
        collectionRef.add(data).await()
    }

    // Met à jour une séance
    suspend fun updateSeance(seance: Seances) {
        val docRef = collectionRef.document(seance.uid)
        val data = hashMapOf(
            "date" to seance.date,
            "date_fin" to seance.dateFin,
            "id_cours" to seance.idCours,
            "lieu" to seance.lieu,
            "presences" to seance.presences
        )
        docRef.update(data as Map<String, Any>).await()
    }

    // Supprime une séance
    suspend fun deleteSeance(seanceId: String) {
        collectionRef.document(seanceId).delete().await()
    }
}
