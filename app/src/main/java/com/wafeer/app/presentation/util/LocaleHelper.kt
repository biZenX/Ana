package com.wafeer.app.presentation.util

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object LocaleHelper {

    fun currentLanguageCode(): String {
        val appLang = runCatching {
            val appLocales = AppCompatDelegate.getApplicationLocales()
            if (appLocales != null && !appLocales.isEmpty) {
                appLocales.get(0)?.language
            } else {
                null
            }
        }.getOrNull()

        val raw = if (!appLang.isNullOrBlank()) {
            appLang
        } else {
            runCatching { Locale.getDefault()?.language }.getOrNull().orEmpty()
        }
        return raw.lowercase(Locale.ROOT)
            .substringBefore('-')
            .substringBefore('_')
            .ifBlank { "en" }
    }

    fun isArabic(): Boolean = currentLanguageCode() == "ar"

    fun isEnglish(): Boolean = currentLanguageCode() == "en"
}
