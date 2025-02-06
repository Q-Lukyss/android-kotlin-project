package com.lukyss.android_kotlin_project.ui.home_etudiant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeEtudiantViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home étudiant Fragment"
    }
    val text: LiveData<String> = _text
}