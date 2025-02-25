// FormationService.kt
package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lukyss.android_kotlin_project.database.http.models.Formation
import kotlinx.coroutines.tasks.await

class FormationService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Formation")

    // Récupère la formation pour laquelle le champ 'eleves' contient userId
    suspend fun getFormationForUser(userId: String): Formation? {
        val q = collectionRef.whereArrayContains("eleves", userId)
        val querySnapshot = q.get().await()
        return if (querySnapshot.documents.isNotEmpty()) {
            val doc = querySnapshot.documents.first()
            Formation.fromFirestore(doc.data, doc.id)
        } else {
            null
        }
    }

    // Récupère toutes les formations
    suspend fun getFormations(): List<Formation> {
        val querySnapshot = collectionRef.get().await()
        return querySnapshot.documents.map { doc ->
            Formation.fromFirestore(doc.data, doc.id)
        }
    }

    // Ajoute une formation
    suspend fun addFormation(formation: Formation) {
        val data = hashMapOf(
            "annee" to formation.annee,
            "nom" to formation.nom,
            "cours" to formation.cours,
            "eleves" to formation.eleves
        )
        collectionRef.add(data).await()
    }

    // Met à jour une formation
    suspend fun updateFormation(formation: Formation) {
        val docRef = collectionRef.document(formation.uid)
        val data = hashMapOf(
            "annee" to formation.annee,
            "nom" to formation.nom,
            "cours" to formation.cours,
            "eleves" to formation.eleves
        )
        docRef.update(data as Map<String, Any>).await()
    }

    // Supprime une formation
    suspend fun deleteFormation(formationId: String) {
        collectionRef.document(formationId).delete().await()
    }
}
