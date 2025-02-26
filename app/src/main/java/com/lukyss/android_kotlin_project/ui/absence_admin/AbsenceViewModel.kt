package com.lukyss.android_kotlin_project.ui.absence_admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AbsenceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Absence Admin Fragment"
    }
    val text: LiveData<String> = _text
}