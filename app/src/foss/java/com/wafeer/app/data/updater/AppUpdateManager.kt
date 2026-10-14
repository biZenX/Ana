package com.wafeer.app.data.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.wafeer.app.BuildConfig
import com.wafeer.app.data.util.CacheUtils
import com.wafeer.app.domain.model.updater.AppUpdateInfo
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

@Singleton
class AppUpdateManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    companion object {
        private const val TAG = "AppUpdateManager"
        private const val GITHUB_REPO = "biZenX/Wafeer"
        private const val API_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

        fun cleanOldUpdateApks(context: Context) = CacheUtils.cleanOldUpdateApks(context)
        fun getAppCacheSizeBytes(context: Context): Long = CacheUtils.getAppCacheSizeBytes(context)
        fun clearAppCache(context: Context): Long = CacheUtils.clearAppCache(context)
        fun formatCacheSize(bytes: Long): String = CacheUtils.formatCacheSize(bytes)

        fun parseVersionCode(tag: String): Int {
            val parts = tag.removePrefix("v").substringBefore("-")
                .split('.')
                .mapNotNull { it.takeWhile { c -> c.isDigit() }.toIntOrNull() }
            if (parts.size >= 3) {
                return parts[0] * 10000 + parts[1] * 100 + parts[2]
            }
            return 0
        }

        /**
         * Compares two semantic version strings (e.g. "1.3.24" vs "1.3.23").
         * Strips leading 'v' and trailing pre-release annotations like "-beta".
         * Returns true if remote is strictly newer than current.
         */
        fun isVersionNewer(remote: String, current: String): Boolean {
            fun parseParts(v: String): List<Int> {
                val clean = v.trim().removePrefix("v").substringBefore("-")
                return clean.split('.').map { part ->
                    part.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
                }
            }
            val remoteParts = parseParts(remote)
            val currentParts = parseParts(current)
            val maxLen = maxOf(remoteParts.size, currentParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (r > c) return true
                if (r < c) return false
            }
            return false
        }
    }

    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState: StateFlow<UpdateDownloadState> = _downloadState.asStateFlow()

    /**
     * Checks if a new release is available on GitHub.
     * Returns AppUpdateInfo if a newer version is found, or null otherwise.
     */
    suspend fun checkForUpdates(): Result<AppUpdateInfo?> = withContext(Dispatchers.IO) {
        runCatching {
            val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
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
                throw IllegalStateException(
                    if (isAr) {
                        "لم يتم العثور على إصدار منشور على GitHub (تأكد من نشر Release عام في المستودع)"
                    } else {
                        "No published release found on GitHub"
                    },
                )
            } else if (conn.responseCode == 403) {
                logcat(TAG) { "GitHub API response: 403 - Rate limit reached" }
                throw IllegalStateException(
                    if (isAr) {
                        "تم تجاوز حد استعلامات GitHub مؤقتاً، يرجى المحاولة لاحقاً"
                    } else {
                        "GitHub API rate limit reached temporarily, please try again later"
                    },
                )
            } else if (conn.responseCode != 200) {
                logcat(TAG) { "GitHub API response: ${conn.responseCode}" }
                throw IllegalStateException(
                    if (isAr) {
                        "استجابة غير متوقعة من خادم التحديث (${conn.responseCode})"
                    } else {
                        "Unexpected response from update server (${conn.responseCode})"
                    },
                )
            }

            val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonText)

            val tagName = json.optString("tag_name", "").removePrefix("v").trim()
            val publishedAt = json.optString("published_at", "").take(10)
            val body = json.optString("body", "")

            val currentVersionName = BuildConfig.VERSION_NAME.removePrefix("v").trim()
            val remoteVersionCode = parseVersionCode(tagName)

            logcat(TAG) { "Remote tag: $tagName (code $remoteVersionCode), Current: $currentVersionName (code ${BuildConfig.VERSION_CODE})" }

            if (!isVersionNewer(tagName, currentVersionName)) {
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

            val (mainFeatures, improvements) = parseReleaseHighlights(body, remoteVersionCode)

            AppUpdateInfo(
                versionName = tagName.ifBlank { currentVersionName },
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
            copyApkToPublicDownloads(apkFile, info.versionName)
            installApk(apkFile)
        } catch (e: Exception) {
            logcat(TAG) { "Download failed: ${e.message}" }
            val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
            _downloadState.value = UpdateDownloadState.Error(
                e.message ?: if (isAr) "فشل تنزيل ملف التحديث" else "Failed to download update file",
            )
        }
    }

    /**
     * Copies the downloaded APK to the public Downloads folder so the user can easily find it.
     */
    fun copyApkToPublicDownloads(apkFile: File, versionName: String): Boolean {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "Wafeer-v$versionName.apk")
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        apkFile.inputStream().use { input -> input.copyTo(out) }
                    }
                    logcat(TAG) { "Saved update APK to public MediaStore.Downloads: $uri" }
                    true
                } else {
                    false
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                if (downloadsDir.exists() || downloadsDir.mkdirs()) {
                    val targetFile = File(downloadsDir, "Wafeer-v$versionName.apk")
                    apkFile.copyTo(targetFile, overwrite = true)
                    logcat(TAG) { "Saved update APK to public downloads dir: ${targetFile.absolutePath}" }
                    true
                } else {
                    false
                }
            }
        }.getOrElse {
            logcat(TAG) { "Failed to copy APK to public Downloads: ${it.message}" }
            false
        }
    }

    fun openDownloadsFolder() {
        runCatching {
            val intent = Intent(android.app.DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }.onFailure {
            runCatching {
                @Suppress("DEPRECATION")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS).path), "*/*")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
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
            val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
            _downloadState.value = UpdateDownloadState.Error(
                if (isAr) {
                    "تعذر فتح ملف التثبيت: ${it.localizedMessage}"
                } else {
                    "Could not open installer: ${it.localizedMessage}"
                },
            )
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

    private fun parseReleaseHighlights(body: String, remoteVersionCode: Int = 0): Pair<List<String>, List<String>> {
        val langCode = com.wafeer.app.presentation.util.LocaleHelper.currentLanguageCode()
        val isAr = langCode == "ar"

        // If the remote release is already in our localized GeneratedChangelog, use its native localized items directly
        if (remoteVersionCode > 0) {
            val localRelease = com.wafeer.app.generated.GeneratedChangelog.getReleases(langCode)
                .firstOrNull { it.versionCode == remoteVersionCode }
            if (localRelease != null && localRelease.items.isNotEmpty()) {
                val localFeatures = localRelease.items
                    .filter { it.type == com.wafeer.app.domain.model.changelog.ReleaseType.FEATURE }
                    .map { it.title }
                val localImprovements = localRelease.items
                    .filter { it.type != com.wafeer.app.domain.model.changelog.ReleaseType.FEATURE }
                    .map { it.title }
                if (localFeatures.isNotEmpty() || localImprovements.isNotEmpty()) {
                    return Pair(localFeatures.take(2), localImprovements.take(2))
                }
            }
        }

        val features = mutableListOf<String>()
        val improvements = mutableListOf<String>()

        val rawLines = body.lines().map { it.trim() }.filter { it.isNotBlank() }

        // Parse lines along with their section context if markdown headers are present
        val taggedLines = mutableListOf<Pair<String, String?>>()
        var currentSection: String? = null

        for (rawLine in rawLines) {
            if (rawLine.startsWith("#")) {
                val headerLower = rawLine.lowercase()
                currentSection = when {
                    headerLower.contains("مميزات") || headerLower.contains("ميزات") || headerLower.contains("feature") -> "FEATURE"
                    headerLower.contains("تحسين") || headerLower.contains("إصلاح") || headerLower.contains("اصلاح") ||
                        headerLower.contains("improve") || headerLower.contains("fix") -> "IMPROVEMENT"
                    else -> currentSection
                }
                continue
            }
            if (rawLine.startsWith("---") ||
                rawLine.startsWith("***") ||
                rawLine.startsWith("===") ||
                rawLine.contains("ما الجديد في الإصدار", ignoreCase = true) ||
                rawLine.contains("الجديد في هذا الإصدار", ignoreCase = true) ||
                rawLine.contains("سجل التغييرات", ignoreCase = true) ||
                rawLine.contains("what's new", ignoreCase = true) ||
                rawLine.equals("changelog", ignoreCase = true)
            ) {
                continue
            }
            val cleaned = cleanBulletText(rawLine)
            if (cleaned.isNotBlank()) {
                taggedLines.add(cleaned to currentSection)
            }
        }

        // If the release body contains both Arabic and English bullets, prefer the lines matching Arabic vs Latin script first
        val matchingLocaleLines = taggedLines.filter { (line, _) ->
            com.wafeer.app.data.repository.ChangelogRepository.containsArabic(line) == isAr
        }
        val candidateLines = (matchingLocaleLines.ifEmpty { taggedLines })
            .map { (line, section) ->
                com.wafeer.app.data.repository.ChangelogRepository.localizeTextForLanguage(line, langCode) to section
            }
            .distinctBy { it.first }

        for ((line, section) in candidateLines) {
            when (section) {
                "FEATURE" -> if (features.size < 2) features.add(line)
                "IMPROVEMENT" -> if (improvements.size < 2) improvements.add(line)
                else -> {
                    if (features.size < 2) {
                        features.add(line)
                    } else if (improvements.size < 2) {
                        improvements.add(line)
                    }
                }
            }
        }

        if (features.isEmpty() && improvements.isEmpty()) {
            val latestLocal = com.wafeer.app.generated.GeneratedChangelog.getReleases(langCode).firstOrNull()
            if (latestLocal != null) {
                features.addAll(
                    latestLocal.items
                        .filter { it.type == com.wafeer.app.domain.model.changelog.ReleaseType.FEATURE }
                        .map { it.title }
                        .take(2),
                )
                improvements.addAll(
                    latestLocal.items
                        .filter { it.type != com.wafeer.app.domain.model.changelog.ReleaseType.FEATURE }
                        .map { it.title }
                        .take(2),
                )
            }
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
