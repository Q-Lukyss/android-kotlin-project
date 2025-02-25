// NewsViewModel.kt
package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.services.NewsService
import com.lukyss.android_kotlin_project.database.http.models.News
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsService: NewsService = NewsService()
) : ViewModel() {

    private val _news = MutableLiveData<List<News>>(emptyList())
    val news: LiveData<List<News>> get() = _news

    private val _loadingNews = MutableLiveData<Boolean>(false)
    val loadingNews: LiveData<Boolean> get() = _loadingNews

    private val _errorNews = MutableLiveData<String?>()
    val errorNews: LiveData<String?> get() = _errorNews

    fun fetchNews() {
        _loadingNews.value = true
        viewModelScope.launch {
            try {
                _news.value = newsService.getNews()
                _errorNews.value = null
            } catch (e: Exception) {
                _errorNews.value = e.message
            } finally {
                _loadingNews.value = false
            }
        }
    }

    fun addNews(newsItem: News) {
        viewModelScope.launch {
            try {
                newsService.addNews(newsItem)
                fetchNews()
            } catch (e: Exception) {
                _errorNews.value = e.message
            }
        }
    }

    fun updateNews(newsItem: News) {
        viewModelScope.launch {
            try {
                newsService.updateNews(newsItem)
                fetchNews()
            } catch (e: Exception) {
                _errorNews.value = e.message
            }
        }
    }

    fun deleteNews(newsId: String) {
        viewModelScope.launch {
            try {
                newsService.deleteNews(newsId)
                fetchNews()
            } catch (e: Exception) {
                _errorNews.value = e.message
            }
        }
    }
}
