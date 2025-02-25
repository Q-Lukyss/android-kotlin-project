// DocumentViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.DocumentsService
import com.lukyss.android_kotlin_project.database.http.models.Documents
import kotlinx.coroutines.launch

class DocumentViewModel(
    private val documentService: DocumentsService = DocumentsService()
) : ViewModel() {

    private val _documents = MutableLiveData<List<Documents>>(emptyList())
    val documents: LiveData<List<Documents>> get() = _documents

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchDocumentsForUser(userId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _documents.value = documentService.getDocumentsForUser(userId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
