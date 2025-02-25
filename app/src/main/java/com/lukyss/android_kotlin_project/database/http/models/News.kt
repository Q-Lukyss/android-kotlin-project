package com.lukyss.android_kotlin_project.database.http.models

import java.util.Date
import com.google.firebase.Timestamp

data class News(
    val uid: String,
    val titre: String,
    val contenu: String,
    val date: Date,
    val isActive: Boolean
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): News {
            val dateObj = when (val d = data?.get("date")) {
                is Timestamp -> d.toDate()
                is Date -> d
                else -> Date()
            }
            return News(
                uid = uid,
                titre = data?.get("titre") as? String ?: "",
                contenu = data?.get("contenu") as? String ?: "",
                date = dateObj,
                isActive = data?.get("is_active") as? Boolean ?: false
            )
        }
    }
}
