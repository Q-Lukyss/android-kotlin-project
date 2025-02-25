package com.lukyss.android_kotlin_project.database.http.models

data class Cours(
    val uid: String,
    val idEnseignant: String,
    val nom: String,
    val seances: List<String>,
    val supports: List<String>
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Cours {
            return Cours(
                uid = uid,
                idEnseignant = data?.get("id_enseignant") as? String ?: "",
                nom = data?.get("nom") as? String ?: "",
                seances = (data?.get("seances") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                supports = (data?.get("supports") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
}
