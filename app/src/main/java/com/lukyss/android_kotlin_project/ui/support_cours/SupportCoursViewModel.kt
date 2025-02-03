package com.lukyss.android_kotlin_project.ui.support_cours

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SupportCoursViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is support_cours Fragment"
    }
    val text: LiveData<String> = _text
}