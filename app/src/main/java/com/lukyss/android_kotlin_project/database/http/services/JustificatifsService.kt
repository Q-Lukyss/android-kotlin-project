package com.lukyss.android_kotlin_project.database.http.services

import retrofit2.http.GET

interface JustificatifsService {
    @GET("Justificatifs")
    suspend fun getAllArticles() : JustificatifsResponse
}