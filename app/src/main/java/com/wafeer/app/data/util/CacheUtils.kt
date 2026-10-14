package com.wafeer.app.data.util

import android.content.Context
import java.io.File

object CacheUtils {
    fun cleanOldUpdateApks(context: Context) {
        runCatching {
            val updatesDir = File(context.cacheDir, "updates")
            if (updatesDir.exists()) {
                updatesDir.listFiles()?.forEach { it.delete() }
            }
            context.cacheDir.listFiles { file -> file.extension.equals("apk", ignoreCase = true) }?.forEach {
                it.delete()
            }
        }
    }

    fun getAppCacheSizeBytes(context: Context?): Long {
        if (context == null) return 0L
        return runCatching {
            getFolderSize(context.cacheDir) + (context.externalCacheDir?.let { getFolderSize(it) } ?: 0L)
        }.getOrDefault(0L)
    }

    fun clearAppCache(context: Context?): Long {
        if (context == null) return 0L
        return runCatching {
            val before = getAppCacheSizeBytes(context)
            deleteFolderContents(context.cacheDir)
            context.externalCacheDir?.let { deleteFolderContents(it) }
            val after = getAppCacheSizeBytes(context)
            (before - after).coerceAtLeast(0L)
        }.getOrDefault(0L)
    }

    fun formatCacheSize(bytes: Long): String {
        val isAr = runCatching { com.wafeer.app.presentation.util.LocaleHelper.isArabic() }.getOrDefault(false)
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format(
                java.util.Locale.US,
                if (isAr) "%.1f جيجابايت" else "%.1f GB",
                bytes / (1024f * 1024f * 1024f),
            )
            bytes >= 1024 * 1024 -> String.format(
                java.util.Locale.US,
                if (isAr) "%.1f ميجابايت" else "%.1f MB",
                bytes / (1024f * 1024f),
            )
            bytes >= 1024 -> String.format(
                java.util.Locale.US,
                if (isAr) "%.1f كيلوبايت" else "%.1f KB",
                bytes / 1024f,
            )
            else -> if (isAr) "$bytes بايت" else "$bytes B"
        }
    }

    private fun getFolderSize(file: File?): Long {
        return runCatching {
            if (file == null || !file.exists()) return 0L
            if (file.isFile) return file.length()
            var size = 0L
            file.listFiles()?.forEach { size += getFolderSize(it) }
            size
        }.getOrDefault(0L)
    }

    private fun deleteFolderContents(file: File?) {
        if (file == null || !file.exists()) return
        file.listFiles()?.forEach { child ->
            if (child.isDirectory) deleteFolderContents(child)
            child.delete()
        }
    }
}
