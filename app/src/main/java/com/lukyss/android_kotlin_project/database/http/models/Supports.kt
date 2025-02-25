package com.lukyss.android_kotlin_project.database.http.models

data class Supports(
    val uid: String,
    val idCours: String,
    val nom: String
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Supports {
            return Supports(
                uid = uid,
                idCours = data?.get("id_cours") as? String ?: "",
                nom = data?.get("nom") as? String ?: ""
            )
        }
    }
}


