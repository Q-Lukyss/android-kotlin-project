package com.lukyss.android_kotlin_project.database.http.models

data class Formation(
    val uid: String,
    val annee: String,
    val nom: String,
    val cours: List<String>,
    val eleves: List<String>
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Formation {
            return Formation(
                uid = uid,
                annee = data?.get("annee") as? String ?: "",
                nom = data?.get("nom") as? String ?: "",
                cours = (data?.get("cours") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                eleves = (data?.get("eleves") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
}
