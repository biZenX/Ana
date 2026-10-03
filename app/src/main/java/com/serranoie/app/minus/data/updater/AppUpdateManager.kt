package com.serranoie.app.minus.data.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.serranoie.app.minus.BuildConfig
import com.serranoie.app.minus.domain.model.updater.AppUpdateInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import logcat.logcat
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

sealed interface UpdateDownloadState {
    data object Idle : UpdateDownloadState
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : UpdateDownloadState
    data class ReadyToInstall(val apkFile: File) : UpdateDownloadState
    data class Error(val message: String) : UpdateDownloadState
}

@Singleton
class AppUpdateManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    companion object {
        private const val TAG = "AppUpdateManager"
        private const val GITHUB_REPO = "biZenX/Ana"
        private const val API_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
    }

    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState: StateFlow<UpdateDownloadState> = _downloadState.asStateFlow()

    /**
     * Checks if a new release is available on GitHub.
     * Returns AppUpdateInfo if a newer version is found, or null otherwise.
     */
    suspend fun checkForUpdates(): Result<AppUpdateInfo?> = withContext(Dispatchers.IO) {
        runCatching {
            logcat(TAG) { "Checking for updates from $API_URL" }
            val url = URL(API_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "Wafeer-Android-App")
            }

            if (conn.responseCode == 404) {
                logcat(TAG) { "GitHub API response: 404 - No release found on $GITHUB_REPO" }
                throw IllegalStateException("لم يتم العثور على إصدار منشور على GitHub (تأكد من نشر Release عام في المستودع)")
            } else if (conn.responseCode == 403) {
                logcat(TAG) { "GitHub API response: 403 - Rate limit reached" }
                throw IllegalStateException("تم تجاوز حد استعلامات GitHub مؤقتاً، يرجى المحاولة لاحقاً")
            } else if (conn.responseCode != 200) {
                logcat(TAG) { "GitHub API response: ${conn.responseCode}" }
                throw IllegalStateException("استجابة غير متوقعة من خادم التحديث (${conn.responseCode})")
            }

            val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonText)

            val tagName = json.optString("tag_name", "").removePrefix("v").trim()
            val publishedAt = json.optString("published_at", "").take(10)
            val body = json.optString("body", "")

            // Parse version numbers (e.g. "1.3.0" -> major:1, minor:3, patch:0)
            val remoteVersionCode = parseVersionCode(tagName)
            val currentVersionCode = BuildConfig.VERSION_CODE

            logcat(TAG) { "Remote tag: $tagName (code $remoteVersionCode), Current: ${BuildConfig.VERSION_NAME} (code $currentVersionCode)" }

            if (remoteVersionCode <= currentVersionCode && !isVersionNewer(tagName, BuildConfig.VERSION_NAME)) {
                logcat(TAG) { "App is up to date" }
                return@runCatching null
            }

            // Find APK asset
            val assets = json.optJSONArray("assets")
            var downloadUrl = ""
            var fileSize = 0L
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        downloadUrl = asset.optString("browser_download_url", "")
                        fileSize = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            if (downloadUrl.isBlank()) {
                downloadUrl = json.optString("html_url", "")
            }

            val (mainFeatures, improvements) = parseReleaseHighlights(body)

            AppUpdateInfo(
                versionName = tagName.ifBlank { "1.3.0" },
                versionCode = remoteVersionCode,
                releaseDate = publishedAt,
                mainFeatures = mainFeatures,
                improvements = improvements,
                downloadUrl = downloadUrl,
                fileSize = fileSize,
            )
        }
    }

    /**
     * Downloads the APK file to cacheDir/updates/Wafeer-v{version}.apk
     */
    suspend fun downloadUpdate(info: AppUpdateInfo) = withContext(Dispatchers.IO) {
        if (info.downloadUrl.isBlank() || !info.downloadUrl.endsWith(".apk", ignoreCase = true)) {
            // If downloadUrl is a web page (no direct apk asset), open in browser
            openInBrowser(info.downloadUrl)
            return@withContext
        }

        try {
            _downloadState.value = UpdateDownloadState.Downloading(0f, 0L, info.fileSize)
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val apkFile = File(updatesDir, "Wafeer-v${info.versionName}.apk")

            val url = URL(info.downloadUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 30000
                instanceFollowRedirects = true
            }

            val totalBytes = if (info.fileSize > 0) info.fileSize else conn.contentLengthLong.coerceAtLeast(1L)
            var downloadedBytes = 0L

            conn.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    var lastEmittedProgress = 0
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloadedBytes += read
                        val progress = (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                        val percent = (progress * 100).toInt()
                        if (percent != lastEmittedProgress) {
                            lastEmittedProgress = percent
                            _downloadState.value = UpdateDownloadState.Downloading(progress, downloadedBytes, totalBytes)
                        }
                    }
                }
            }

            _downloadState.value = UpdateDownloadState.ReadyToInstall(apkFile)
            logcat(TAG) { "APK downloaded successfully: ${apkFile.absolutePath} (${apkFile.length()} bytes)" }
            installApk(apkFile)
        } catch (e: Exception) {
            logcat(TAG) { "Download failed: ${e.message}" }
            _downloadState.value = UpdateDownloadState.Error(e.message ?: "فشل تنزيل ملف التحديث")
        }
    }

    /**
     * Triggers the Android package installer to install the downloaded APK.
     */
    fun installApk(apkFile: File) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val manageIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(manageIntent)
                    return
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile,
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        }.onFailure {
            logcat(TAG) { "Error starting installer: ${it.message}" }
            _downloadState.value = UpdateDownloadState.Error("تعذر فتح ملف التثبيت: ${it.localizedMessage}")
        }
    }

    fun openInBrowser(url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun resetState() {
        _downloadState.value = UpdateDownloadState.Idle
    }

    private fun parseVersionCode(tag: String): Int {
        val parts = tag.split('.').mapNotNull { it.takeWhile { c -> c.isDigit() }.toIntOrNull() }
        if (parts.size >= 3) {
            return parts[0] * 10000 + parts[1] * 100 + parts[2]
        }
        return 0
    }

    private fun isVersionNewer(remote: String, current: String): Boolean {
        val remoteParts = remote.split('.').mapNotNull { it.takeWhile { c -> c.isDigit() }.toIntOrNull() }
        val currentParts = current.split('.').mapNotNull { it.takeWhile { c -> c.isDigit() }.toIntOrNull() }
        for (i in 0 until maxOf(remoteParts.size, currentParts.size)) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    private fun parseReleaseHighlights(body: String): Pair<List<String>, List<String>> {
        val features = mutableListOf<String>()
        val improvements = mutableListOf<String>()

        val rawLines = body.lines().map { it.trim() }.filter { it.isNotBlank() }

        // Filter out headers, markdown dividers, and intro titles
        val candidateLines = rawLines.filterNot { line ->
            line.startsWith("#") ||
            line.startsWith("---") ||
            line.startsWith("***") ||
            line.startsWith("===") ||
            line.contains("ما الجديد في الإصدار", ignoreCase = true) ||
            line.contains("سجل التغييرات", ignoreCase = true) ||
            line.equals("changelog", ignoreCase = true)
        }.map { cleanBulletText(it) }.filter { it.isNotBlank() }

        for (line in candidateLines) {
            val lower = line.lowercase()
            val isFeatureKeyword = lower.startsWith("feat") ||
                lower.startsWith("new") ||
                line.contains("ميزة") ||
                line.contains("إضافة") ||
                line.contains("جديد") ||
                line.contains("إعدادات") ||
                line.contains("خاصية")

            val isImprovementKeyword = lower.startsWith("improve") ||
                lower.startsWith("fix") ||
                lower.startsWith("refactor") ||
                line.contains("تحسين") ||
                line.contains("إصلاح") ||
                line.contains("لوجيك") ||
                line.contains("حفظ") ||
                line.contains("معالجة") ||
                line.contains("تحديث")

            when {
                isFeatureKeyword && features.size < 2 -> features.add(line)
                isImprovementKeyword && improvements.size < 2 -> improvements.add(line)
                else -> {
                    if (features.size < 2) {
                        features.add(line)
                    } else if (improvements.size < 2) {
                        improvements.add(line)
                    }
                }
            }
        }

        if (features.isEmpty()) {
            features.add("**إدارة مالية متجددة**: تحسينات ذكية في متابعة المصروف اليومي والميزانية")
        }
        if (improvements.isEmpty()) {
            improvements.add("**استقرار وسرعة**: تعزيز استقرار التطبيق ودقة معالجة البيانات محلياً")
        }

        return Pair(features.take(2), improvements.take(2))
    }

    private fun cleanBulletText(text: String): String {
        return text
            .replace(Regex("^#{1,6}\\s*"), "") // strip any remaining markdown headers
            .replace(Regex("^[\\-*•+]\\s*"), "") // strip bullet markers
            .replace(Regex("^\\d+\\.\\s*"), "") // strip numbering
            .replace(Regex("^(feat|fix|improve|refactor)(\\(.*?\\))?:?\\s*", RegexOption.IGNORE_CASE), "")
            // Remove excessive emojis at line start (e.g. 🧠, 💾, ⚙️, 🚀, ⭐) to maintain a sleek fintech aesthetic
            .replace(Regex("^[\\p{So}\\p{Sk}\\p{Cs}\\p{Cn}]+\\s*"), "")
            .trim()
    }
}
