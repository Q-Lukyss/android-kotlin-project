package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.lukyss.android_kotlin_project.database.http.models.Cours
import kotlinx.coroutines.tasks.await

class CoursService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Cours")

    // Récupère tous les cours
    suspend fun getCourses(): List<Cours> {
        val querySnapshot = collectionRef.get().await()
        return querySnapshot.documents.map { doc ->
            Cours.fromFirestore(doc.data, doc.id)
        }
    }

    // Récupère les cours dont l'ID est dans la liste passée en paramètre
    suspend fun getCoursesByIds(ids: List<String>): List<Cours> {
        if (ids.isEmpty()) return emptyList()
        val q = collectionRef.whereIn(FieldPath.documentId(), ids)
        val snapshot = q.get().await()
        return snapshot.documents.map { doc ->
            Cours.fromFirestore(doc.data, doc.id)
        }
    }

    // Récupère les cours pour un intervenant (filtre sur id_enseignant)
    suspend fun getCoursesForIntervenant(teacherId: String): List<Cours> {
        val q = collectionRef.whereEqualTo("id_enseignant", teacherId)
        val snapshot = q.get().await()
        return snapshot.documents.map { doc ->
            Cours.fromFirestore(doc.data, doc.id)
        }
    }

    // Ajoute un nouveau cours
    suspend fun addCours(cours: Cours) {
        val data = hashMapOf(
            "id_enseignant" to cours.idEnseignant,
            "nom" to cours.nom,
            "seances" to cours.seances,
            "supports" to cours.supports
        )
        collectionRef.add(data).await()
    }

    // Met à jour un cours
    suspend fun updateCours(cours: Cours) {
        val docRef = collectionRef.document(cours.uid)
        val data = hashMapOf(
            "id_enseignant" to cours.idEnseignant,
            "nom" to cours.nom,
            "seances" to cours.seances,
            "supports" to cours.supports
        )
        docRef.update(data as Map<String, Any>).await()
    }

    // Supprime un cours
    suspend fun deleteCours(coursId: String) {
        collectionRef.document(coursId).delete().await()
    }
}
