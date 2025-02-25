package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.SeancesService
import com.lukyss.android_kotlin_project.database.http.models.Seances
import kotlinx.coroutines.launch

class SeanceViewModel(
    private val seanceService: SeancesService = SeancesService()
) : ViewModel() {

    private val _seances = MutableLiveData<List<Seances>>(emptyList())
    val seances: LiveData<List<Seances>> get() = _seances

    private val _upcomingSeances = MutableLiveData<List<Seances>>(emptyList())
    val upcomingSeances: LiveData<List<Seances>> get() = _upcomingSeances

    private val _loadingSeances = MutableLiveData<Boolean>(false)
    val loadingSeances: LiveData<Boolean> get() = _loadingSeances

    private val _errorSeances = MutableLiveData<String?>()
    val errorSeances: LiveData<String?> get() = _errorSeances

    fun fetchSeancesForCourse(courseId: String) {
        _loadingSeances.value = true
        viewModelScope.launch {
            try {
                _seances.value = seanceService.getSeancesForCourse(courseId)
                _errorSeances.value = null
            } catch (e: Exception) {
                _errorSeances.value = e.message
            } finally {
                _loadingSeances.value = false
            }
        }
    }

    fun fetchUpcomingSeancesForCourses(courseIds: List<String>) {
        _loadingSeances.value = true
        viewModelScope.launch {
            try {
                _upcomingSeances.value = seanceService.getUpcomingSeancesForCourses(courseIds)
                _errorSeances.value = null
            } catch (e: Exception) {
                _errorSeances.value = e.message
            } finally {
                _loadingSeances.value = false
            }
        }
    }

    fun addSeance(seance: Seances) {
        viewModelScope.launch {
            try {
                seanceService.addSeance(seance)
                fetchSeancesForCourse(seance.idCours)
            } catch (e: Exception) {
                _errorSeances.value = e.message
            }
        }
    }

    fun updateSeance(seance: Seances) {
        viewModelScope.launch {
            try {
                seanceService.updateSeance(seance)
                fetchSeancesForCourse(seance.idCours)
            } catch (e: Exception) {
                _errorSeances.value = e.message
            }
        }
    }

    fun deleteSeance(seanceId: String, courseId: String) {
        viewModelScope.launch {
            try {
                seanceService.deleteSeance(seanceId)
                fetchSeancesForCourse(courseId)
            } catch (e: Exception) {
                _errorSeances.value = e.message
            }
        }
    }
}
