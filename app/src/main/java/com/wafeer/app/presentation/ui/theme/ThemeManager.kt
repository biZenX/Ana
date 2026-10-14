package com.wafeer.app.presentation.ui.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.wafeer.app.domain.model.UserSettings
import com.wafeer.app.presentation.appColorScheme
import com.wafeer.app.presentation.appTheme
import com.wafeer.app.presentation.appTypography
import com.wafeer.app.presentation.isRoundedFontEnabled
import com.wafeer.app.presentation.isAmoledEnabled
import com.wafeer.app.presentation.dynamicColorEnabled
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor() {

    fun applyUserSettings(context: Context, settings: UserSettings) {
        context.appTheme = settings.themeMode
        context.appTypography = settings.typographyMode
        context.isRoundedFontEnabled = settings.isRoundedFontEnabled
        context.isAmoledEnabled = settings.isAmoledEnabled
        context.appColorScheme = settings.colorScheme
        context.dynamicColorEnabled = settings.dynamicColorEnabled

        applyLocale(settings.language)
    }

    fun applyLocale(language: String) {
        val currentLocales = runCatching { AppCompatDelegate.getApplicationLocales() }.getOrNull()
        val isEmpty = currentLocales == null || currentLocales.isEmpty
        val currentTags = currentLocales?.toLanguageTags().orEmpty()

        val isSystem = language == "system" || language.isBlank()

        if (isSystem) {
            if (!isEmpty) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }
            return
        }

        val targetLocale = Locale.forLanguageTag(language)
        val targetTag = targetLocale.toLanguageTag()

        val alreadyMatches = if (isEmpty) {
            false
        } else {
            currentTags.equals(targetTag, ignoreCase = true) ||
                currentTags.equals(language, ignoreCase = true)
        }

        if (!alreadyMatches) {
            runCatching {
                Locale.setDefault(targetLocale)
            }
            val appLocale = LocaleListCompat.forLanguageTags(language)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}
