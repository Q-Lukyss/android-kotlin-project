package com.lukyss.android_kotlin_project.database.http.models

data class UserResponse(
    val success: Boolean,
    val user: UserModel? = null,
    val message: String? = null
)
