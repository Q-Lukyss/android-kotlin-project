// FormationViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.FormationService
import com.lukyss.android_kotlin_project.database.http.models.Formation
import kotlinx.coroutines.launch

class FormationViewModel(
    private val formationService: FormationService = FormationService()
) : ViewModel() {

    private val _formations = MutableLiveData<List<Formation>>(emptyList())
    val formations: LiveData<List<Formation>> get() = _formations

    private val _loadingFormations = MutableLiveData<Boolean>(false)
    val loadingFormations: LiveData<Boolean> get() = _loadingFormations

    private val _errorFormations = MutableLiveData<String?>()
    val errorFormations: LiveData<String?> get() = _errorFormations

    fun fetchFormations() {
        _loadingFormations.value = true
        viewModelScope.launch {
            try {
                _formations.value = formationService.getFormations()
                _errorFormations.value = null
            } catch (e: Exception) {
                _errorFormations.value = e.message
            } finally {
                _loadingFormations.value = false
            }
        }
    }

    fun addFormation(newFormation: Formation) {
        viewModelScope.launch {
            try {
                formationService.addFormation(newFormation)
                fetchFormations()
            } catch (e: Exception) {
                _errorFormations.value = e.message
            }
        }
    }

    fun updateFormation(updatedFormation: Formation) {
        viewModelScope.launch {
            try {
                formationService.updateFormation(updatedFormation)
                fetchFormations()
            } catch (e: Exception) {
                _errorFormations.value = e.message
            }
        }
    }

    fun deleteFormation(formationId: String) {
        viewModelScope.launch {
            try {
                formationService.deleteFormation(formationId)
                fetchFormations()
            } catch (e: Exception) {
                _errorFormations.value = e.message
            }
        }
    }
}
