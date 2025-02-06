package com.lukyss.android_kotlin_project.database.http

import com.lukyss.android_kotlin_project.database.http.models.PresencesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PresencesApiService {
    @GET("Presences")
    suspend fun getAllArticles() : PresencesResponse
}