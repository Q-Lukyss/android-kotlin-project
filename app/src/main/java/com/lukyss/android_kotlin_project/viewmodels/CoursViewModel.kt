// CoursViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.models.Cours
import com.lukyss.android_kotlin_project.database.http.models.Formation
import com.lukyss.android_kotlin_project.database.http.services.CoursService
import com.lukyss.android_kotlin_project.database.http.services.FormationService
import kotlinx.coroutines.launch

class CoursViewModel(
    private val coursService: CoursService = CoursService(),
    private val formationService: FormationService = FormationService()
) : ViewModel() {

    private val _formation = MutableLiveData<Formation?>()
    val formation: LiveData<Formation?> get() = _formation

    private val _courses = MutableLiveData<List<Cours>>(emptyList())
    val courses: LiveData<List<Cours>> get() = _courses

    private val _loadingFormation = MutableLiveData<Boolean>(false)
    val loadingFormation: LiveData<Boolean> get() = _loadingFormation

    private val _errorFormation = MutableLiveData<String?>()
    val errorFormation: LiveData<String?> get() = _errorFormation

    private val _loadingCourses = MutableLiveData<Boolean>(false)
    val loadingCourses: LiveData<Boolean> get() = _loadingCourses

    private val _errorCourses = MutableLiveData<String?>()
    val errorCourses: LiveData<String?> get() = _errorCourses

    // Charge les cours pour un intervenant (filtrage par id_enseignant)
    fun fetchCoursesForIntervenant(teacherId: String) {
        _loadingCourses.value = true
        viewModelScope.launch {
            try {
                _courses.value = coursService.getCoursesForIntervenant(teacherId)
                _errorCourses.value = null
            } catch (e: Exception) {
                _errorCourses.value = e.message
            } finally {
                _loadingCourses.value = false
            }
        }
    }

    // Charge les cours associés à la formation (formation.cours étant une liste d'UID)
    fun fetchCoursesForFormation() {
        val form = _formation.value
        if (form != null) {
            _loadingCourses.value = true
            viewModelScope.launch {
                try {
                    _courses.value = coursService.getCoursesByIds(form.cours)
                    _errorCourses.value = null
                } catch (e: Exception) {
                    _errorCourses.value = e.message
                } finally {
                    _loadingCourses.value = false
                }
            }
        } else {
            _errorCourses.value = "Aucune formation trouvée."
        }
    }

    // Charge la formation pour un utilisateur puis ses cours associés
    fun fetchFormationAndCoursesForUser(userId: String) {
        _loadingFormation.value = true
        viewModelScope.launch {
            try {
                _formation.value = formationService.getFormationForUser(userId)
                _errorFormation.value = null
            } catch (e: Exception) {
                _errorFormation.value = e.message
            } finally {
                _loadingFormation.value = false
            }
            if (_formation.value != null) {
                fetchCoursesForFormation()
            }
        }
    }

    // Méthodes d'administration pour les cours
    fun addCours(newCours: Cours) {
        viewModelScope.launch {
            try {
                coursService.addCours(newCours)
                fetchCourses()
            } catch (e: Exception) {
                _errorCourses.value = e.message
            }
        }
    }

    fun updateCours(updatedCours: Cours) {
        viewModelScope.launch {
            try {
                coursService.updateCours(updatedCours)
                fetchCourses()
            } catch (e: Exception) {
                _errorCourses.value = e.message
            }
        }
    }

    fun deleteCours(coursId: String) {
        viewModelScope.launch {
            try {
                coursService.deleteCours(coursId)
                fetchCourses()
            } catch (e: Exception) {
                _errorCourses.value = e.message
            }
        }
    }

    // Charge tous les cours (utile pour l'administration)
    fun fetchCourses() {
        _loadingCourses.value = true
        viewModelScope.launch {
            try {
                _courses.value = coursService.getCourses()
                _errorCourses.value = null
            } catch (e: Exception) {
                _errorCourses.value = e.message
            } finally {
                _loadingCourses.value = false
            }
        }
    }

    // Méthodes auxiliaires pour récupérer les notes et supports (exemple simplifié)
    suspend fun fetchNotes(courseId: String): List<Cours> {
        return coursService.getCoursesByIds(listOf(courseId))
    }

    suspend fun fetchSupports(courseId: String): List<Cours> {
        return coursService.getCoursesByIds(listOf(courseId))
    }
}
