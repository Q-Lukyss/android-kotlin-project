// NewsService.kt
package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lukyss.android_kotlin_project.database.http.models.News
import kotlinx.coroutines.tasks.await

class NewsService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("News")

    // Récupère les news triées par date décroissante (les plus récentes en premier)
    suspend fun getNews(): List<News> {
        val q = collectionRef
            .whereEqualTo("is_active", true)
            .orderBy("date", Query.Direction.DESCENDING)
        val querySnapshot = q.get().await()
        return querySnapshot.documents.map { doc ->
            News.fromFirestore(doc.data, doc.id)
        }
    }

    // Ajoute une nouvelle news
    suspend fun addNews(news: News) {
        val data = hashMapOf(
            "titre" to news.titre,
            "contenu" to news.contenu,
            "date" to news.date,
            "is_active" to news.isActive
        )
        collectionRef.add(data).await()
    }

    // Met à jour une news existante
    suspend fun updateNews(news: News) {
        val docRef = collectionRef.document(news.uid)
        val data = hashMapOf(
            "titre" to news.titre,
            "contenu" to news.contenu,
            "date" to news.date,
            "is_active" to news.isActive
        )
        docRef.update(data as Map<String, Any>).await()
    }

    // Supprime une news
    suspend fun deleteNews(newsId: String) {
        collectionRef.document(newsId).delete().await()
    }
}
