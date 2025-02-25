package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FirebaseFirestore
import com.lukyss.android_kotlin_project.database.http.models.Documents
import kotlinx.coroutines.tasks.await

class DocumentsService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Documents")

    // Récupère les documents pour un utilisateur donné
    suspend fun getDocumentsForUser(userId: String): List<Documents> {
        val q = collectionRef.whereEqualTo("id_user", userId)
        val querySnapshot = q.get().await()
        return querySnapshot.documents.map { doc ->
            Documents.fromFirestore(doc.data, doc.id)
        }
    }
}
