package com.wafeer.app.data.repository

import com.wafeer.app.domain.model.changelog.VersionRelease
import com.wafeer.app.generated.GeneratedChangelog
import com.wafeer.app.presentation.util.LocaleHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangelogRepository @Inject constructor() {

    fun getAllReleases(): List<VersionRelease> {
        val langCode = LocaleHelper.currentLanguageCode()
        return GeneratedChangelog.getReleases(langCode)
    }

    fun getLatestRelease(): VersionRelease? = getAllReleases().firstOrNull()

    fun getRelease(versionCode: Int): VersionRelease? =
        getAllReleases().firstOrNull { it.versionCode == versionCode }

    companion object {
        private val arabicRegex = Regex("[\\u0600-\\u06FF]")

        fun containsArabic(text: String): Boolean = arabicRegex.containsMatchIn(text)

        private fun cleanNormalizedKey(text: String): String =
            text.trim().replace("**", "").trimEnd('.').trim()

        fun localizeTextForLanguage(rawText: String, langCode: String = LocaleHelper.currentLanguageCode()): String {
            val targetLang = langCode.lowercase().substringBefore('-').substringBefore('_').ifBlank { "en" }
            val trimmed = rawText.trim()
            val cleanKey = cleanNormalizedKey(trimmed)
            if (cleanKey.isEmpty()) return trimmed

            val enReleases = GeneratedChangelog.getReleases("en")
            val arReleases = GeneratedChangelog.getReleases("ar")
            val targetReleases = GeneratedChangelog.getReleases(targetLang)

            for (rIdx in enReleases.indices) {
                val enItems = enReleases[rIdx].items
                val arItems = arReleases.getOrNull(rIdx)?.items
                val targetItems = targetReleases.getOrNull(rIdx)?.items ?: continue

                for (iIdx in enItems.indices) {
                    val enTitle = cleanNormalizedKey(enItems[iIdx].title)
                    val arTitle = arItems?.getOrNull(iIdx)?.let { cleanNormalizedKey(it.title) }
                    val targetTitle = targetItems.getOrNull(iIdx)?.title ?: continue

                    if (cleanKey.equals(enTitle, ignoreCase = true) ||
                        cleanKey.startsWith(enTitle, ignoreCase = true) ||
                        enTitle.startsWith(cleanKey, ignoreCase = true) ||
                        (arTitle != null && (cleanKey == arTitle || cleanKey.startsWith(arTitle) || arTitle.startsWith(cleanKey)))
                    ) {
                        return targetTitle
                    }
                }
            }

            return trimmed
        }

        fun localizeText(rawText: String, isArabic: Boolean = LocaleHelper.isArabic()): String {
            val langCode = if (isArabic) "ar" else LocaleHelper.currentLanguageCode().let { if (it == "ar") "en" else it }
            return localizeTextForLanguage(rawText, langCode)
        }

        fun localizeReleaseForLanguage(
            release: VersionRelease,
            langCode: String = LocaleHelper.currentLanguageCode(),
        ): VersionRelease {
            val targetLang = langCode.lowercase().substringBefore('-').substringBefore('_').ifBlank { "en" }
            val matched = GeneratedChangelog.getReleases(targetLang)
                .firstOrNull { it.versionCode == release.versionCode }
            if (matched != null && matched.items.size == release.items.size) {
                return matched
            }
            return release.copy(
                items = release.items.map { item ->
                    item.copy(title = localizeTextForLanguage(item.title, targetLang))
                }
            )
        }

        fun localizeRelease(release: VersionRelease, isArabic: Boolean = LocaleHelper.isArabic()): VersionRelease {
            val langCode = if (isArabic) "ar" else LocaleHelper.currentLanguageCode().let { if (it == "ar") "en" else it }
            return localizeReleaseForLanguage(release, langCode)
        }
    }
}