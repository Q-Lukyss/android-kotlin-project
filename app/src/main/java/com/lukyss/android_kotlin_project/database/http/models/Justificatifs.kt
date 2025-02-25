package com.lukyss.android_kotlin_project.database.http.models

data class Justificatifs(
    val uid: String,
    val isAdminValid: Boolean,
    val motif: String,
    val piece: String
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>?, uid: String): Justificatifs {
            return Justificatifs(
                uid = uid,
                isAdminValid = data?.get("is_admin_valid") as? Boolean ?: false,
                motif = data?.get("motif") as? String ?: "",
                piece = data?.get("piece") as? String ?: ""
            )
        }
    }
}
