package com.lukyss.android_kotlin_project.ui.calendrier_admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendrierViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Calendrier Admin Fragment"
    }
    val text: LiveData<String> = _text
}