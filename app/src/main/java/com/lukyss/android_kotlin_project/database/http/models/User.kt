package com.lukyss.android_kotlin_project.database.http.models

data class User(
    val uid: String,
    val nom: String,
    val prenom: String,
    val email: String,
    val statut: String
) {
    companion object {
        fun fromFirestore(data: Map<String, Any>?, uuid: String): User {
            return User(
                uid = uuid,
                nom = data?.get("nom") as? String ?: "",
                prenom = data?.get("prenom") as? String ?: "",
                email = data?.get("email") as? String ?: "",
                statut = data?.get("statut") as? String ?: ""
            )
        }
    }
}
