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
        val appLocale = if (settings.language == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(settings.language)
        }
        if (AppCompatDelegate.getApplicationLocales() != appLocale) {
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}

