package com.lukyss.android_kotlin_project.ui.news_admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is news Fragment"
    }
    val text: LiveData<String> = _text
}