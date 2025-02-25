// SupportViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.SupportsService
import com.lukyss.android_kotlin_project.database.http.models.Supports
import kotlinx.coroutines.launch

class SupportViewModel(
    private val supportService: SupportsService = SupportsService()
) : ViewModel() {

    private val _supports = MutableLiveData<List<Supports>>(emptyList())
    val supports: LiveData<List<Supports>> get() = _supports

    private val _loadingSupports = MutableLiveData<Boolean>(false)
    val loadingSupports: LiveData<Boolean> get() = _loadingSupports

    private val _errorSupports = MutableLiveData<String?>()
    val errorSupports: LiveData<String?> get() = _errorSupports

    fun fetchSupportsForCourse(courseId: String) {
        _loadingSupports.value = true
        viewModelScope.launch {
            try {
                _supports.value = supportService.getSupportsForCourse(courseId)
                _errorSupports.value = null
            } catch (e: Exception) {
                _errorSupports.value = e.message
            } finally {
                _loadingSupports.value = false
            }
        }
    }

    fun addSupport(support: Supports) {
        viewModelScope.launch {
            try {
                supportService.addSupport(support)
                fetchSupportsForCourse(support.idCours)
            } catch (e: Exception) {
                _errorSupports.value = e.message
            }
        }
    }

    fun updateSupport(support: Supports) {
        viewModelScope.launch {
            try {
                supportService.updateSupport(support)
                fetchSupportsForCourse(support.idCours)
            } catch (e: Exception) {
                _errorSupports.value = e.message
            }
        }
    }

    fun deleteSupport(supportId: String, courseId: String) {
        viewModelScope.launch {
            try {
                supportService.deleteSupport(supportId)
                fetchSupportsForCourse(courseId)
            } catch (e: Exception) {
                _errorSupports.value = e.message
            }
        }
    }
}
