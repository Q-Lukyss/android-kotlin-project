package com.lukyss.android_kotlin_project.database.http.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lukyss.android_kotlin_project.database.http.models.Note
import kotlinx.coroutines.tasks.await

class NoteService {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Notes")

    // Récupère les notes pour un cours donné
    suspend fun getNotesForCourse(courseId: String): List<Note> {
        val q = collectionRef.whereEqualTo("id_cours", courseId)
        val querySnapshot = q.get().await()
        return querySnapshot.documents.map { doc ->
            Note.fromFirestore(doc.data, doc.id)
        }
    }

    // Récupère les notes pour un utilisateur donné
    suspend fun getNotesForUser(userId: String): List<Note> {
        val q = collectionRef.whereEqualTo("id_user", userId)
        val querySnapshot = q.get().await()
        return querySnapshot.documents.map { doc ->
            Note.fromFirestore(doc.data, doc.id)
        }
    }

    // Récupère les notes pour un élève et un cours donné
    suspend fun getNotesForStudentAndCourse(studentId: String, courseId: String): List<Note> {
        val q = collectionRef
            .whereEqualTo("id_user", studentId)
            .whereEqualTo("id_cours", courseId)
        val snapshot = q.get().await()
        return snapshot.documents.map { doc ->
            Note.fromFirestore(doc.data, doc.id)
        }
    }

    // Ajoute une note
    suspend fun addNote(note: Note) {
        val data = hashMapOf(
            "id_user" to note.idUser,
            "id_cours" to note.idCours,
            "libelle" to note.libelle,
            "note" to note.note
        )
        collectionRef.add(data).await()
    }

    // Met à jour une note
    suspend fun updateNote(note: Note) {
        val docRef = collectionRef.document(note.uid)
        val data = hashMapOf(
            "libelle" to note.libelle,
            "note" to note.note
        )
        docRef.update(data as Map<String, Any>).await()
    }

    // Supprime une note
    suspend fun deleteNote(noteId: String) {
        collectionRef.document(noteId).delete().await()
    }
}
