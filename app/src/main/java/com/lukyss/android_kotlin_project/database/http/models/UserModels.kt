package com.lukyss.android_kotlin_project.database.http.models

data class UserModel(
    val email: String = "",
    val nom: String = "",
    val prenom: String = "",
    val formation: Array<Number> = arrayOf(),
    val document: Array<Number> = arrayOf(),
    val statut: Int = 0 // 0 : étudiant, 5 : intervenant, 10 : administratif
)
