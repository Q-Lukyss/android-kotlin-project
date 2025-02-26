package com.lukyss.android_kotlin_project.database.http.models

data class Presences(
    val uid: String,
    val idSeance: String,
    val idUser: String,
    val isPresent: Boolean,
    val justificatifs: List<String>? = null
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Presences {
            val rawJustifs = data?.get("justificatifs")
            val justifList = when (rawJustifs) {
                is List<*> -> rawJustifs.filterIsInstance<String>()
                else -> emptyList()
            }

            return Presences(
                uid = uid,
                idSeance = data?.get("id_seance") as? String ?: "",
                idUser = data?.get("id_user") as? String ?: "",
                isPresent = data?.get("is_present") as? Boolean ?: false,
                justificatifs = justifList
            )
        }
    }
}
