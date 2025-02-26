package com.lukyss.android_kotlin_project

class LoginUseCase {
    fun getNavigationDestination(statut: Int): Int? {
        return when (statut) {
            0 -> R.id.navigation_home_etudiant
            10 -> R.id.navigation_home_administratif
            5 -> null // Pour intervenant, par exemple, on affiche juste un Toast
            else -> null
        }
    }
}