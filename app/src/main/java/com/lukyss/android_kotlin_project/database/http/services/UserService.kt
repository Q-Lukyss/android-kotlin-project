package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.lukyss.android_kotlin_project.database.http.models.User

class UserService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Users")

    // Récupère un user par son uid
    suspend fun getUser(uid: String): User? {
        return try {
            val docSnapshot = collectionRef.document(uid).get().await()
            if (docSnapshot.exists()) {
                User.fromFirestore(docSnapshot.data, docSnapshot.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserService", "Erreur lors de la récupération du user", e)
            throw e
        }
    }

    // Crée ou met à jour un user dans Firestore
    suspend fun createOrUpdateUser(user: User) {
        try {
            val data = hashMapOf(
                "nom" to user.nom,
                "prenom" to user.prenom,
                "email" to user.email,
                "statut" to user.statut
            )
            collectionRef.document(user.uid).set(data).await()
        } catch (e: Exception) {
            Log.e("UserService", "Erreur lors de la création/mise à jour du user", e)
            throw e
        }
    }

    // Récupère la liste des utilisateurs
    suspend fun getUsers(): List<User> {
        return try {
            val querySnapshot = collectionRef.get().await()
            querySnapshot.documents.mapNotNull { doc ->
                doc.data?.let { User.fromFirestore(it, doc.id) }
            }
        } catch (e: Exception) {
            Log.e("UserService", "Erreur lors de la récupération des users", e)
            throw e
        }
    }

    // Ajoute un nouveau user (l'ID sera généré par Firestore)
    suspend fun addUser(user: User) {
        try {
            val data = hashMapOf(
                "nom" to user.nom,
                "prenom" to user.prenom,
                "email" to user.email,
                "statut" to user.statut
            )
            collectionRef.add(data).await()
        } catch (e: Exception) {
            Log.e("UserService", "Erreur lors de l'ajout du user", e)
            throw e
        }
    }

    // Met à jour un user existant (par exemple, uniquement le nom et le prénom)
    suspend fun updateUser(user: User) {
        try {
            val data = hashMapOf(
                "nom" to user.nom,
                "prenom" to user.prenom
            )
            collectionRef.document(user.uid).update(data as Map<String, Any>).await()
        } catch (e: Exception) {
            Log.e("UserService", "Erreur lors de la mise à jour du user", e)
            throw e
        }
    }

    // Supprime un user
    suspend fun deleteUser(uid: String) {
        try {
            collectionRef.document(uid).delete().await()
        } catch (e: Exception) {
            Log.e("UserService", "Erreur lors de la suppression du user", e)
            throw e
        }
    }
}
