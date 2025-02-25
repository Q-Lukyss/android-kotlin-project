package com.lukyss.android_kotlin_project.database.http.models

data class Documents(
    val uid: String,
    val idUser: String,
    val nom: String
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Documents {
            return Documents(
                uid = uid,
                idUser = data?.get("id_user") as? String ?: "",
                nom = data?.get("nom") as? String ?: ""
            )
        }
    }
}
