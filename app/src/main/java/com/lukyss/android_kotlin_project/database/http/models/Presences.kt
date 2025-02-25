package com.lukyss.android_kotlin_project.database.http.models

data class Presences(
    val uid: String,
    val idSeance: String,
    val idUser: String,
    val isPresent: Boolean,
    val justificatifs: String? = null
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Presences {
            return Presences(
                uid = uid,
                idSeance = data?.get("id_seance") as? String ?: "",
                idUser = data?.get("id_user") as? String ?: "",
                isPresent = data?.get("is_present") as? Boolean ?: false,
                justificatifs = data?.get("justificatifs") as? String
            )
        }
    }
}
