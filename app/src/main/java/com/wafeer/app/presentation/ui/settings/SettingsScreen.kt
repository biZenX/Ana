package com.wafeer.app.presentation.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafeer.app.presentation.ui.settings.csv.CsvTransferEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun SettingsScreen(
    onNavigateToBugReport: () -> Unit,
    onNavigateToChangelog: () -> Unit = {},
    onNavigateToAppearance: () -> Unit = {},
    onNavigateToFeatureLab: () -> Unit = {},
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pendingUpdate by viewModel.pendingUpdate.collectAsStateWithLifecycle()
    val downloadState by viewModel.updateDownloadState.collectAsStateWithLifecycle()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsStateWithLifecycle()
    val updateCheckMessage by viewModel.updateCheckMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        viewModel.onImportResult(uri)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        viewModel.refreshNotificationPermission()
        if (isGranted) {
            viewModel.onNotificationPermissionGranted()
            val sent = viewModel.onSendTestNotification()
            val msg = if (sent) {
                context.getString(com.wafeer.app.R.string.notification_test_sent_toast)
            } else {
                context.getString(com.wafeer.app.R.string.notification_permission_needed_toast)
            }
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(
                context,
                context.getString(com.wafeer.app.R.string.notification_permission_needed_toast),
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        val manager = EntryPointAccessors
            .fromApplication(context.applicationContext, CsvTransferEntryPoint::class.java)
            .csvTransferManager()
        viewModel.setCsvTransferManager(manager)
        viewModel.setImportLauncher(importLauncher)
    }

    val cacheSize by viewModel.cacheSize.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshNotificationPermission()
                viewModel.updateCacheSize()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsUiEffect.NavigateToBugReport -> {
                    viewModel.consumeEffect()
                    onNavigateToBugReport()
                }

                is SettingsUiEffect.NavigateBack -> {
                    viewModel.consumeEffect()
                    onNavigateBack()
                }

                null -> { /* no-op */
                }
            }
        }
    }

    LaunchedEffect(updateCheckMessage) {
        updateCheckMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearUpdateCheckMessage()
        }
    }

    pendingUpdate?.let { info ->
        com.wafeer.app.presentation.ui.updater.UpdatePromptDialog(
            updateInfo = info,
            downloadState = downloadState,
            onStartDownload = viewModel::startDownloadingUpdate,
            onDismiss = viewModel::dismissUpdateDialog,
        )
    }

    Settings(
        isCensored = uiState.isCensored,
        recurrentPaymentsViewMode = uiState.recurrentPaymentsViewMode,
        notificationHour = uiState.notificationHour,
        notificationMinute = uiState.notificationMinute,
        recurrentNotificationHour = uiState.recurrentNotificationHour,
        recurrentNotificationMinute = uiState.recurrentNotificationMinute,
        exactAlarmEnabled = uiState.exactAlarmEnabled,
        notificationPermissionGranted = uiState.notificationPermissionGranted,
        onCensorModeToggle = viewModel::onCensorModeToggle,
        onNavigateToFeatureLab = onNavigateToFeatureLab,
        onRecurrentPaymentsViewModeChange = viewModel::onRecurrentPaymentsViewModeChange,
        onNotificationTimeChange = viewModel::onNotificationTimeChange,
        onRecurrentNotificationTimeChange = viewModel::onRecurrentNotificationTimeChange,
        onOpenExactAlarmSettings = viewModel::onOpenExactAlarmSettings,
        onOpenNotificationSettings = {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU && !uiState.notificationPermissionGranted) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.onOpenNotificationSettings()
                viewModel.refreshNotificationPermission()
            }
        },
        onSendTestNotification = {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU && !uiState.notificationPermissionGranted) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                val sent = viewModel.onSendTestNotification()
                if (sent) {
                    val msg = context.getString(com.wafeer.app.R.string.notification_test_sent_toast)
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.onOpenNotificationSettings()
                    val msg = "يرجى السماح بالإشعارات من إعدادات النظام للتطبيق"
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        },
        periodMappingMode = uiState.periodMappingMode,
        onPeriodMappingModeChange = viewModel::onPeriodMappingModeChange,
        savingsPreferences = uiState.savingsPreferences,
        onSavingsPreferencesChange = viewModel::onSavingsPreferencesChange,
        onExportCsv = viewModel::onExportCsv,
        onImportCsv = viewModel::onImportCsv,
        cacheSize = cacheSize,
        onClearCache = viewModel::onClearCache,
        onResetTutorial = viewModel::onResetTutorial,
        financialTipsEnabled = uiState.financialTipsEnabled,
        onFinancialTipsToggle = viewModel::setFinancialTipsEnabled,
        onResetDismissedTips = viewModel::resetDismissedFinancialTips,
        onBugReportClick = viewModel::onBugReportClick,
        onNavigateToChangelog = onNavigateToChangelog,
        onNavigateToAppearance = onNavigateToAppearance,
        isCheckingUpdate = isCheckingUpdate,
        onCheckForUpdates = viewModel::checkForUpdates,
        onTestUpdateDialog = viewModel::showDemoUpdateDialog,
        onBack = viewModel::onBack,
    )
}
