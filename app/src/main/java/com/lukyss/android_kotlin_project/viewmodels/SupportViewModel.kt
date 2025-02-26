package com.lukyss.android_kotlin_project.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import com.lukyss.android_kotlin_project.database.http.models.Supports
import com.lukyss.android_kotlin_project.database.http.services.SupportsService
import com.lukyss.android_kotlin_project.database.http.models.Cours
import kotlinx.coroutines.launch

class SupportViewModel(private val supportService: SupportsService = SupportsService()) : ViewModel() {
    private val _supports = MutableLiveData<List<Supports>>()
    val supports: LiveData<List<Supports>> get() = _supports

    private val _loadingSupports = MutableLiveData<Boolean>()
    val loadingSupports: LiveData<Boolean> get() = _loadingSupports

    private val _errorSupports = MutableLiveData<String?>()
    val errorSupports: LiveData<String?> get() = _errorSupports

    fun fetchSupportsForCourse(courseId: String) {
        _loadingSupports.value = true
        viewModelScope.launch {
            try {
                val fetchedSupports = supportService.getSupportsForCourse(courseId)
                _supports.value = fetchedSupports
                _errorSupports.value = null
            } catch (e: Exception) {
                _errorSupports.value = e.message
            } finally {
                _loadingSupports.value = false
            }
        }
    }
}
