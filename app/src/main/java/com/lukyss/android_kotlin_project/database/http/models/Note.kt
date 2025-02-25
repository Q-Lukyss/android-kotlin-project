package com.lukyss.android_kotlin_project.database.http.models

data class Note(
    val uid: String,
    val idCours: String,
    val idUser: String,
    val libelle: String,
    val note: Double
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Note {
            val noteValue = when (val n = data?.get("note")) {
                is Number -> n.toDouble()
                else -> 0.0
            }
            return Note(
                uid = uid,
                idCours = data?.get("id_cours") as? String ?: "",
                idUser = data?.get("id_user") as? String ?: "",
                libelle = data?.get("libelle") as? String ?: "",
                note = noteValue
            )
        }
    }
}
