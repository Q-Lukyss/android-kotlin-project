package com.lukyss.android_kotlin_project.database.http.models

import java.util.Date
import com.google.firebase.Timestamp

data class Seances(
    val uid: String,
    val date: Date,
    val dateFin: Date,
    val idCours: String,
    val lieu: String,
    val presences: List<String>
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Seances {
            val dateObj = when (val d = data?.get("date")) {
                is Timestamp -> d.toDate()
                is Date -> d
                else -> Date()
            }
            val dateFinObj = when (val d = data?.get("date_fin")) {
                is Timestamp -> d.toDate()
                is Date -> d
                else -> Date()
            }
            return Seances(
                uid = uid,
                date = dateObj,
                dateFin = dateFinObj,
                idCours = data?.get("id_cours") as? String ?: "",
                lieu = data?.get("lieu") as? String ?: "",
                presences = (data?.get("presences") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
}
