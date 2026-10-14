package com.wafeer.app.presentation.ui.updater

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wafeer.app.data.updater.UpdateDownloadState
import com.wafeer.app.domain.model.updater.AppUpdateInfo
import com.wafeer.app.presentation.util.LocaleHelper

@Composable
fun UpdatePromptDialog(
    updateInfo: AppUpdateInfo,
    downloadState: UpdateDownloadState,
    onStartDownload: () -> Unit,
    onOpenDownloads: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    val langCode = LocaleHelper.currentLanguageCode()
    val strings = updateDialogStrings(langCode)
    Dialog(
        onDismissRequest = {
            if (downloadState !is UpdateDownloadState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = downloadState !is UpdateDownloadState.Downloading,
            dismissOnClickOutside = downloadState !is UpdateDownloadState.Downloading,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Top Header Row with Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SystemUpdate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp),
                        )
                    }

                    if (downloadState !is UpdateDownloadState.Downloading) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = strings.close,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title and Version badge
                Text(
                    text = strings.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Text(
                            text = "${strings.versionPrefix} v${updateInfo.versionName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    if (updateInfo.releaseDate.isNotBlank()) {
                        Text(
                            text = updateInfo.releaseDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Main Features (1-2)
                if (updateInfo.mainFeatures.isNotEmpty()) {
                    SectionHeader(
                        title = strings.whatsNew,
                        icon = Icons.Rounded.AutoAwesome,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    for (feature in updateInfo.mainFeatures) {
                        HighlightCard(
                            text = com.wafeer.app.data.repository.ChangelogRepository.localizeTextForLanguage(feature, langCode),
                            isFeature = true,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Section 2: Improvements & Fixes (1-2)
                if (updateInfo.improvements.isNotEmpty()) {
                    SectionHeader(
                        title = strings.improvementsAndFixes,
                        icon = Icons.Rounded.CheckCircle,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    for (improvement in updateInfo.improvements) {
                        HighlightCard(
                            text = com.wafeer.app.data.repository.ChangelogRepository.localizeTextForLanguage(improvement, langCode),
                            isFeature = false,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Download Progress / Status
                when (downloadState) {
                    is UpdateDownloadState.Downloading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = strings.downloading((downloadState.progress * 100).toInt()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { downloadState.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                            )
                        }
                    }

                    is UpdateDownloadState.Error -> {
                        Text(
                            text = downloadState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }

                    is UpdateDownloadState.ReadyToInstall -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = strings.downloadComplete,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = strings.conflictHint,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onOpenDownloads,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Folder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.openDownloads,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }

                    UpdateDownloadState.Idle -> Unit
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = downloadState !is UpdateDownloadState.Downloading,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(strings.later)
                    }

                    Button(
                        onClick = onStartDownload,
                        modifier = Modifier.weight(1.4f),
                        enabled = downloadState !is UpdateDownloadState.Downloading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (downloadState is UpdateDownloadState.ReadyToInstall) {
                                strings.installNow
                            } else {
                                strings.updateNow
                            },
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

private data class UpdateDialogStrings(
    val close: String,
    val title: String,
    val versionPrefix: String,
    val whatsNew: String,
    val improvementsAndFixes: String,
    val downloading: (Int) -> String,
    val downloadComplete: String,
    val conflictHint: String,
    val openDownloads: String,
    val later: String,
    val updateNow: String,
    val installNow: String,
)

private fun updateDialogStrings(langCode: String): UpdateDialogStrings = when (langCode) {
    "ar" -> UpdateDialogStrings(
        close = "إغلاق",
        title = "يتوفر تحديث جديد لوفير",
        versionPrefix = "الإصدار",
        whatsNew = "المميزات الجديدة",
        improvementsAndFixes = "التحسينات والإصلاحات",
        downloading = { "جاري تنزيل التحديث $it%..." },
        downloadComplete = "اكتمل التنزيل! تم حفظ ملف التحديث في مجلد التنزيلات أيضاً.",
        conflictHint = "لو ظهر تعارض في التثبيت لأول مرة بسبب مفتاح التوقيع السابق، خذ نسخة احتياطية من الإعدادات ثم ثبّت النسخة مباشرة من مجلد التنزيلات.",
        openDownloads = "فتح مجلد التنزيلات (Downloads)",
        later = "لاحقاً",
        updateNow = "تحديث الآن",
        installNow = "تثبيت الآن",
    )
    "fr" -> UpdateDialogStrings(
        close = "Fermer",
        title = "Nouvelle mise à jour Wafeer disponible",
        versionPrefix = "Version",
        whatsNew = "Nouveautés",
        improvementsAndFixes = "Améliorations et correctifs",
        downloading = { "Téléchargement de la mise à jour $it%..." },
        downloadComplete = "Téléchargement terminé ! L'APK a également été enregistré dans Téléchargements.",
        conflictHint = "En cas de conflit de signature lors de l'installation, sauvegardez vos données dans les Paramètres puis installez depuis votre dossier Téléchargements.",
        openDownloads = "Ouvrir le dossier Téléchargements",
        later = "Plus tard",
        updateNow = "Mettre à jour",
        installNow = "Installer maintenant",
    )
    "es" -> UpdateDialogStrings(
        close = "Cerrar",
        title = "Nueva actualización de Wafeer disponible",
        versionPrefix = "Versión",
        whatsNew = "Novedades",
        improvementsAndFixes = "Mejoras y correcciones",
        downloading = { "Descargando actualización $it%..." },
        downloadComplete = "¡Descarga completa! El APK también se guardó en tu carpeta Descargas.",
        conflictHint = "Si aparece un conflicto de firma al instalar, haz una copia de seguridad en Ajustes e instala directamente desde tu carpeta Descargas.",
        openDownloads = "Abrir carpeta Descargas",
        later = "Más tarde",
        updateNow = "Actualizar ahora",
        installNow = "Instalar ahora",
    )
    "de" -> UpdateDialogStrings(
        close = "Schließen",
        title = "Neues Wafeer-Update verfügbar",
        versionPrefix = "Version",
        whatsNew = "Neuigkeiten",
        improvementsAndFixes = "Verbesserungen & Fehlerbehebungen",
        downloading = { "Update wird heruntergeladen $it%..." },
        downloadComplete = "Download abgeschlossen! Die APK wurde auch im Ordner Downloads gespeichert.",
        conflictHint = "Falls ein Signaturkonflikt auftritt, sichern Sie Ihre Daten in den Einstellungen und installieren Sie direkt aus dem Ordner Downloads.",
        openDownloads = "Ordner Downloads öffnen",
        later = "Später",
        updateNow = "Jetzt aktualisieren",
        installNow = "Jetzt installieren",
    )
    "it" -> UpdateDialogStrings(
        close = "Chiudi",
        title = "Nuovo aggiornamento Wafeer disponibile",
        versionPrefix = "Versione",
        whatsNew = "Novità",
        improvementsAndFixes = "Miglioramenti e correzioni",
        downloading = { "Download aggiornamento $it%..." },
        downloadComplete = "Download completato! L'APK è stato salvato anche nella cartella Download.",
        conflictHint = "In caso di conflitto di firma durante l'installazione, esegui un backup nelle Impostazioni e installa direttamente dalla cartella Download.",
        openDownloads = "Apri cartella Download",
        later = "Più tardi",
        updateNow = "Aggiorna ora",
        installNow = "Installa ora",
    )
    "pt" -> UpdateDialogStrings(
        close = "Fechar",
        title = "Nova atualização do Wafeer disponível",
        versionPrefix = "Versão",
        whatsNew = "Novidades",
        improvementsAndFixes = "Melhorias e correções",
        downloading = { "Baixando atualização $it%..." },
        downloadComplete = "Download concluído! O APK também foi salvo na pasta Downloads.",
        conflictHint = "Se aparecer um conflito de assinatura, faça backup dos seus dados nas Configurações e instale diretamente da pasta Downloads.",
        openDownloads = "Abrir pasta Downloads",
        later = "Mais tarde",
        updateNow = "Atualizar agora",
        installNow = "Instalar agora",
    )
    "ru" -> UpdateDialogStrings(
        close = "Закрыть",
        title = "Доступно обновление Wafeer",
        versionPrefix = "Версия",
        whatsNew = "Что нового",
        improvementsAndFixes = "Улучшения и исправления",
        downloading = { "Загрузка обновления $it%..." },
        downloadComplete = "Загрузка завершена! APK-файл также сохранен в папку «Загрузки».",
        conflictHint = "При конфликте подписи создайте резервную копию в Настройках и установите обновление напрямую из папки «Загрузки».",
        openDownloads = "Открыть папку «Загрузки»",
        later = "Позже",
        updateNow = "Обновить сейчас",
        installNow = "Установить сейчас",
    )
    "zh" -> UpdateDialogStrings(
        close = "关闭",
        title = "发现 Wafeer 新版本",
        versionPrefix = "版本",
        whatsNew = "新增功能",
        improvementsAndFixes = "优化与修复",
        downloading = { "正在下载更新 $it%..." },
        downloadComplete = "下载完成！更新安装包已同时保存至“下载”文件夹。",
        conflictHint = "若首次安装出现签名冲突，请先在“设置”中备份数据，然后直接从“下载”文件夹安装。",
        openDownloads = "打开“下载”文件夹",
        later = "稍后",
        updateNow = "立即更新",
        installNow = "立即安装",
    )
    "ja" -> UpdateDialogStrings(
        close = "閉じる",
        title = "Wafeer の新しいアップデート",
        versionPrefix = "バージョン",
        whatsNew = "新機能",
        improvementsAndFixes = "改善と修正",
        downloading = { "アップデートをダウンロード中 $it%..." },
        downloadComplete = "ダウンロード完了！APKはダウンロードフォルダにも保存されました。",
        conflictHint = "署名の競合が表示された場合は、設定でデータをバックアップしてからダウンロードフォルダから直接インストールしてください。",
        openDownloads = "ダウンロードフォルダを開く",
        later = "後で",
        updateNow = "今すぐ更新",
        installNow = "今すぐインストール",
    )
    "ko" -> UpdateDialogStrings(
        close = "닫기",
        title = "새로운 Wafeer 업데이트 가능",
        versionPrefix = "버전",
        whatsNew = "새로운 기능",
        improvementsAndFixes = "개선 및 수정 사항",
        downloading = { "업데이트 다운로드 중 $it%..." },
        downloadComplete = "다운로드 완료! 업데이트 APK가 다운로드 폴더에도 저장되었습니다.",
        conflictHint = "설치 중 서명 충돌이 발생하면 설정에서 데이터를 백업한 후 다운로드 폴더에서 직접 설치하세요.",
        openDownloads = "다운로드 폴더 열기",
        later = "나중에",
        updateNow = "지금 업데이트",
        installNow = "지금 설치",
    )
    "hi" -> UpdateDialogStrings(
        close = "बंद करें",
        title = "नया Wafeer अपडेट उपलब्ध है",
        versionPrefix = "संस्करण",
        whatsNew = "नया क्या है",
        improvementsAndFixes = "सुधार और बग फिक्स",
        downloading = { "अपडेट डाउनलोड हो रहा है $it%..." },
        downloadComplete = "डाउनलोड पूरा हुआ! अपडेट APK आपके Downloads फ़ोल्डर में भी सहेजा गया है।",
        conflictHint = "यदि इंस्टॉलेशन में हस्ताक्षर विरोध दिखाई दे, तो पहले सेटिंग्स में डेटा का बैकअप लें और सीधे Downloads फ़ोल्डर से इंस्टॉल करें।",
        openDownloads = "Downloads फ़ोल्डर खोलें",
        later = "बाद में",
        updateNow = "अभी अपडेट करें",
        installNow = "अभी इंस्टॉल करें",
    )
    "el" -> UpdateDialogStrings(
        close = "Κλείσιμο",
        title = "Νέα ενημέρωση Wafeer διαθέσιμη",
        versionPrefix = "Έκδοση",
        whatsNew = "Τι νέο υπάρχει",
        improvementsAndFixes = "Βελτιώσεις & Διορθώσεις",
        downloading = { "Λήψη ενημέρωσης $it%..." },
        downloadComplete = "Η λήψη ολοκληρώθηκε! Το APK αποθηκεύτηκε επίσης στον φάκελο Λήψεις.",
        conflictHint = "Αν εμφανιστεί διένεξη υπογραφής, κρατήστε αντίγραφο ασφαλείας στις Ρυθμίσεις και εγκαταστήστε απευθείας από τον φάκελο Λήψεις.",
        openDownloads = "Άνοιγμα φακέλου Λήψεις",
        later = "Αργότερα",
        updateNow = "Ενημέρωση τώρα",
        installNow = "Εγκατάσταση τώρα",
    )
    "bg" -> UpdateDialogStrings(
        close = "Затвори",
        title = "Налична е нова актуализация на Wafeer",
        versionPrefix = "Версия",
        whatsNew = "Какво е новото",
        improvementsAndFixes = "Подобрения и поправки",
        downloading = { "Изтегляне на актуализацията $it%..." },
        downloadComplete = "Изтеглянето завърши! APK файлът е запазен и в папка Изтегляния.",
        conflictHint = "Ако се появи конфликт при инсталиране, направете резервно копие в Настройки и инсталирайте директно от папка Изтегляния.",
        openDownloads = "Отвори папка Изтегляния",
        later = "По-късно",
        updateNow = "Актуализирай сега",
        installNow = "Инсталирай сега",
    )
    else -> UpdateDialogStrings(
        close = "Close",
        title = "New Wafeer Update Available",
        versionPrefix = "Version",
        whatsNew = "What's New",
        improvementsAndFixes = "Improvements & Fixes",
        downloading = { "Downloading update $it%..." },
        downloadComplete = "Download complete! The update APK was also saved to your Downloads folder.",
        conflictHint = "If an installation conflict appears due to an older signing key, back up your data in Settings first, then install directly from your Downloads folder.",
        openDownloads = "Open Downloads Folder",
        later = "Later",
        updateNow = "Update Now",
        installNow = "Install Now",
    )
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun HighlightCard(text: String, isFeature: Boolean) {
    val annotatedText = androidx.compose.runtime.remember(text) {
        com.wafeer.app.presentation.util.MarkdownParser.parseToAnnotatedString(
            rawText = text,
            boldWeight = FontWeight.Bold,
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFeature) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (isFeature) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        shape = CircleShape,
                    ),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp,
            )
        }
    }
}
