// NoteViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.NoteService
import com.lukyss.android_kotlin_project.database.http.models.Note
import kotlinx.coroutines.launch
import androidx.lifecycle.map

class NoteViewModel(
    private val noteService: NoteService = NoteService()
) : ViewModel() {

    private val _globalNotes = MutableLiveData<List<Note>>(emptyList())
    val globalNotes: LiveData<List<Note>> get() = _globalNotes

    private val _loadingGlobalNotes = MutableLiveData<Boolean>(false)
    val loadingGlobalNotes: LiveData<Boolean> get() = _loadingGlobalNotes

    private val _errorGlobalNotes = MutableLiveData<String?>()
    val errorGlobalNotes: LiveData<String?> get() = _errorGlobalNotes

    // notesMap : clé = "$studentId_$courseId", valeur = liste de Note
    private val _notesMap = MutableLiveData<MutableMap<String, List<Note>>>(mutableMapOf())
    val notesMap: MutableLiveData<MutableMap<String, List<Note>>> get() = _notesMap

    private val _loadingStudentNotes = MutableLiveData<Boolean>(false)
    val loadingStudentNotes: LiveData<Boolean> get() = _loadingStudentNotes

    private val _errorStudentNotes = MutableLiveData<String?>()
    val errorStudentNotes: LiveData<String?> get() = _errorStudentNotes

    fun fetchNotesForUser(userId: String) {
        _loadingGlobalNotes.value = true
        viewModelScope.launch {
            try {
                _globalNotes.value = noteService.getNotesForUser(userId)
                _errorGlobalNotes.value = null
            } catch (e: Exception) {
                _errorGlobalNotes.value = e.message
            } finally {
                _loadingGlobalNotes.value = false
            }
        }
    }

    fun fetchNotesForStudentAndCourse(studentId: String, courseId: String) {
        _loadingStudentNotes.value = true
        viewModelScope.launch {
            try {
                val notes = noteService.getNotesForStudentAndCourse(studentId, courseId)
                _notesMap.value?.set("${studentId}_$courseId", notes)
                _errorStudentNotes.value = null
            } catch (e: Exception) {
                _errorStudentNotes.value = e.message
            } finally {
                _loadingStudentNotes.value = false
            }
        }
    }

    fun addNoteForStudent(note: Note) {
        viewModelScope.launch {
            try {
                noteService.addNote(note)
                fetchNotesForStudentAndCourse(note.idUser, note.idCours)
            } catch (e: Exception) {
                _errorStudentNotes.value = e.message
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                noteService.updateNote(note)
                fetchNotesForStudentAndCourse(note.idUser, note.idCours)
            } catch (e: Exception) {
                _errorStudentNotes.value = e.message
            }
        }
    }

    fun deleteNote(noteId: String, studentId: String, courseId: String) {
        viewModelScope.launch {
            try {
                noteService.deleteNote(noteId)
                fetchNotesForStudentAndCourse(studentId, courseId)
            } catch (e: Exception) {
                _errorStudentNotes.value = e.message
            }
        }
    }

    val averageGrade: LiveData<Double> = _globalNotes.map { notes ->
        if (notes.isEmpty()) 0.0 else notes.sumOf { it.note } / notes.size
    }
}
