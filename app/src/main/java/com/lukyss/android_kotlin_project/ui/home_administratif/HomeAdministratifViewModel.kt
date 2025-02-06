package com.lukyss.android_kotlin_project.ui.home_administratif

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeAdministratifViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home adminstratif Fragment"
    }
    val text: LiveData<String> = _text
}