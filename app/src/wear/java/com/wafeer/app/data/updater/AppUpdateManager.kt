package com.wafeer.app.data.updater

import android.content.Context
import com.wafeer.app.data.util.CacheUtils
import com.wafeer.app.domain.model.updater.AppUpdateInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    companion object {
        fun cleanOldUpdateApks(context: Context) = CacheUtils.cleanOldUpdateApks(context)
        fun getAppCacheSizeBytes(context: Context): Long = CacheUtils.getAppCacheSizeBytes(context)
        fun clearAppCache(context: Context): Long = CacheUtils.clearAppCache(context)
        fun formatCacheSize(bytes: Long): String = CacheUtils.formatCacheSize(bytes)

        fun parseVersionCode(tag: String): Int = 0

        fun isVersionNewer(remote: String, current: String): Boolean = false
    }

    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState: StateFlow<UpdateDownloadState> = _downloadState.asStateFlow()

    suspend fun checkForUpdates(): Result<AppUpdateInfo?> = Result.success(null)

    suspend fun downloadUpdate(info: AppUpdateInfo) {}

    fun copyApkToPublicDownloads(apkFile: File, versionName: String): Boolean = false

    fun openDownloadsFolder() {}

    fun installApk(apkFile: File) {}

    fun resetState() {
        _downloadState.value = UpdateDownloadState.Idle
    }

    fun openInBrowser(url: String) {}
}
