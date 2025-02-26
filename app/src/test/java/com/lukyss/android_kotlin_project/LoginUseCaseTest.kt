package com.lukyss.android_kotlin_project

import org.junit.Assert.assertEquals
import org.junit.Test

class LoginUseCaseTest {

    private val useCase = LoginUseCase()

    @Test
    fun testEtudiant() {
        val destination = useCase.getNavigationDestination(0)
        assertEquals(R.id.navigation_home_etudiant, destination)
    }

    @Test
    fun testAdministratif() {
        val destination = useCase.getNavigationDestination(10)
        assertEquals(R.id.navigation_home_administratif, destination)
    }

    @Test
    fun testIntervenant() {
        val destination = useCase.getNavigationDestination(5)
        assertEquals(null, destination)
    }

    @Test
    fun testIdentifiantsIncorrects() {
        val destination = useCase.getNavigationDestination(999)
        assertEquals(null, destination)
    }
}