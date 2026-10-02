package com.serranoie.app.minus.domain.model.updater

data class AppUpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val mainFeatures: List<String>,
    val improvements: List<String>,
    val downloadUrl: String,
    val fileSize: Long = 0L,
)
