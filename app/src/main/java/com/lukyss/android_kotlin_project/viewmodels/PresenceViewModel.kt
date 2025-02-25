// PresenceViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.PresencesService
import com.lukyss.android_kotlin_project.database.http.models.Presences
import androidx.lifecycle.map
import kotlinx.coroutines.launch

class PresenceViewModel(
    private val presenceService: PresencesService = PresencesService()
) : ViewModel() {

    private val _presences = MutableLiveData<List<Presences>>(emptyList())
    val presences: LiveData<List<Presences>> get() = _presences

    private val _loadingPresence = MutableLiveData<Boolean>(false)
    val loadingPresence: LiveData<Boolean> get() = _loadingPresence

    private val _errorPresence = MutableLiveData<String?>()
    val errorPresence: LiveData<String?> get() = _errorPresence

    fun fetchPresences() {
        _loadingPresence.value = true
        viewModelScope.launch {
            try {
                _presences.value = presenceService.getAllPresences()
                _errorPresence.value = null
            } catch (e: Exception) {
                _errorPresence.value = e.message
            } finally {
                _loadingPresence.value = false
            }
        }
    }

    fun fetchPresencesForUser(userId: String) {
        _loadingPresence.value = true
        viewModelScope.launch {
            try {
                _presences.value = presenceService.getPresencesForUser(userId)
                _errorPresence.value = null
            } catch (e: Exception) {
                _errorPresence.value = e.message
            } finally {
                _loadingPresence.value = false
            }
        }
    }

    fun updatePresence(presence: Presences) {
        viewModelScope.launch {
            try {
                presenceService.updatePresence(presence)
                fetchPresences()
            } catch (e: Exception) {
                _errorPresence.value = e.message
            }
        }
    }

    val nbPresences: LiveData<Int> = _presences.map { presences ->
        presences.count { it.isPresent }
    }

    val nbAbsences: LiveData<Int> = _presences.map { presences ->
        presences.count { !it.isPresent }
    }

    val absencesToJustify: LiveData<List<Presences>> = _presences.map { presences ->
        presences.filter { !it.isPresent && !it.justificatifs.isNullOrEmpty() }
    }

}
