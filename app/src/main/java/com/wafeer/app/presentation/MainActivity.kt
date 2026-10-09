package com.wafeer.app.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wafeer.app.R
import com.wafeer.app.data.repository.SettingsRepository
import com.wafeer.app.data.wearable.WearableService
import com.wafeer.app.domain.model.AppColorScheme
import com.wafeer.app.domain.model.ContrastMode
import com.wafeer.app.domain.model.RemainingBudgetStrategy
import com.wafeer.app.domain.model.ThemeMode
import com.wafeer.app.domain.model.TypographyMode
import com.wafeer.app.domain.time.MidnightTransitionManager
import com.wafeer.app.navigation.AppNavGraph
import com.wafeer.app.navigation.Screen
import com.wafeer.app.presentation.notification.NotificationHelper
import com.wafeer.app.presentation.notification.NotificationScheduler
import com.wafeer.app.presentation.permission.PermissionHandler
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.ThemeManager
import com.wafeer.app.presentation.ui.theme.component.RolloverDialog
import com.wafeer.app.presentation.util.CensorManager
import com.wafeer.app.presentation.util.LocalCensorMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

var Context.appTheme by mutableStateOf(ThemeMode.SYSTEM)
var Context.appTypography by mutableStateOf(TypographyMode.EXPRESSIVE)
var Context.isRoundedFontEnabled by mutableStateOf(true)
var Context.isAmoledEnabled by mutableStateOf(false)
var Context.appColorScheme by mutableStateOf(AppColorScheme.BRAND)
var Context.appContrast by mutableStateOf(ContrastMode.NORMAL)
var Context.dynamicColorEnabled by mutableStateOf(false)

val LocalWindowSize = compositionLocalOf { WindowWidthSizeClass.Compact }
val LocalWindowInsets = compositionLocalOf { PaddingValues(0.dp) }

const val DEFAULT_NOTIFICATION_HOUR = 9
const val DEFAULT_NOTIFICATION_MINUTE = 0
const val DEFAULT_RECURRENT_NOTIFICATION_HOUR = 8
const val DEFAULT_RECURRENT_NOTIFICATION_MINUTE = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val isDone: MutableState<Boolean> = mutableStateOf(false)
    private val isReady: MutableState<Boolean> = mutableStateOf(false)
    private val dataStoreLoaded: MutableState<Boolean> = mutableStateOf(false)
    private val onboardingComplete: MutableState<Boolean> = mutableStateOf(false)
    private val earlyFinishPending: MutableState<Boolean> = mutableStateOf(false)

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var permissionHandler: PermissionHandler

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var censorManager: CensorManager

    @Inject
    lateinit var wearableService: WearableService

    @Inject
    lateinit var midnightTransitionManager: MidnightTransitionManager

    @Inject
    lateinit var appUpdateManager: com.wafeer.app.data.updater.AppUpdateManager

    private val autoUpdateInfo: MutableState<com.wafeer.app.domain.model.updater.AppUpdateInfo?> = mutableStateOf(null)

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        permissionHandler.onNotificationPermissionResult(isGranted, notificationScheduler)
    }

    private fun checkAndRequestNotificationPermission() {
        permissionHandler.requestNotificationPermissionIfNeeded(
            activity = this,
            launcher = requestNotificationPermissionLauncher,
        )
    }

    override fun onResume() {
        super.onResume()
        censorManager.start()
    }

    override fun onPause() {
        super.onPause()
        censorManager.stop()
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            val keepOn = !dataStoreLoaded.value || !isDone.value
            keepOn
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            try {
                runCatching {
                    val nodeIds = wearableService.getReachableSenderNodeIds()
                    logcat {
                        "wear capability wafeer_wear_sender reachableNodes=${nodeIds.size} ids=${nodeIds.joinToString()}"
                    }
                }.onFailure {
                    logcat { it.asLog() }
                }

                val userSettings = settingsRepository.getSettings()
                onboardingComplete.value = userSettings.onboardingCompleted
                earlyFinishPending.value = userSettings.earlyFinishActive
                themeManager.applyUserSettings(applicationContext, userSettings)

                dataStoreLoaded.value = true
                isDone.value = true
            } catch (_: Exception) {
                logcat("MainActivity") { "Initial settings load failed" }
                dataStoreLoaded.value = true
                isDone.value = true
            }

            notificationScheduler.initializeNotifications()

            launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    com.wafeer.app.data.updater.AppUpdateCheckWorker.schedule(applicationContext)
                    appUpdateManager.checkForUpdates().onSuccess { info ->
                        if (info != null) {
                            autoUpdateInfo.value = info

                            val prefs = applicationContext.getSharedPreferences("wafeer_update_check_prefs", android.content.Context.MODE_PRIVATE)
                            val lastNotified = prefs.getInt("last_notified_update_version_code", 0)
                            if (info.versionCode > lastNotified) {
                                notificationHelper.showUpdateNotification(info)
                                prefs.edit().putInt("last_notified_update_version_code", info.versionCode).apply()
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore offline
                }
            }
        }

        settingsRepository.observeSettings().onEach { settings ->
            onboardingComplete.value = settings.onboardingCompleted
            earlyFinishPending.value = settings.earlyFinishActive
            themeManager.applyUserSettings(applicationContext, settings)
        }.launchIn(lifecycleScope)

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    lifecycleScope.launch {
                        midnightTransitionManager.handleAppStart()
                    }
                }
            },
        )

        setContent {
            val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current

            LaunchedEffect(Unit) {
                isReady.value = true
            }

            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass

            val windowInsets = WindowInsets.systemBars.asPaddingValues()

            if (isReady.value && dataStoreLoaded.value) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val dynamicColor = context.dynamicColorEnabled
                val isCensored by censorManager.isCensored.collectAsStateWithLifecycle()

                val startDestination = when {
                    earlyFinishPending.value -> Screen.Analytics.route
                    !onboardingComplete.value -> Screen.Onboarding.route
                    else -> Screen.Main.route
                }

                WafeerTheme(dynamicColor = dynamicColor) {
                    CompositionLocalProvider(
                        LocalWindowSize provides widthSizeClass,
                        LocalWindowInsets provides windowInsets,
                        LocalCensorMode provides isCensored,
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            val navController = rememberNavController()

                            AppNavGraph(
                                activityResultRegistryOwner = activityResultRegistryOwner,
                                startDestination = startDestination,
                                navController = navController,
                                onOnboardingComplete = {
                                    lifecycleScope.launch {
                                        settingsRepository.setOnboardingCompleted(true)
                                        midnightTransitionManager.onBudgetSetupHandled()
                                    }
                                },
                                onRequestNotificationPermission = {
                                    checkAndRequestNotificationPermission()
                                },
                            )

                            MidnightRolloverDialogHost(navController)
                            HandleBudgetSetupNavigation(navController)

                            val currentUpdateInfo by autoUpdateInfo
                            val downloadState by appUpdateManager.downloadState.collectAsStateWithLifecycle()

                            currentUpdateInfo?.let { info ->
                                com.wafeer.app.presentation.ui.updater.UpdatePromptDialog(
                                    updateInfo = info,
                                    downloadState = downloadState,
                                    onStartDownload = {
                                        lifecycleScope.launch {
                                            appUpdateManager.downloadUpdate(info)
                                        }
                                    },
                                    onOpenDownloads = {
                                        appUpdateManager.openDownloadsFolder()
                                    },
                                    onDismiss = {
                                        autoUpdateInfo.value = null
                                        appUpdateManager.resetState()
                                    },
                                )
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    isDone.value = true
                }
            }
        }
    }

    @Composable
    private fun MidnightRolloverDialogHost(navController: NavHostController) {
        val shouldShowMidnightDialog by midnightTransitionManager.shouldShowTransitionDialog.collectAsStateWithLifecycle()
        val midnightTransitionData by midnightTransitionManager.midnightTransitionData.collectAsStateWithLifecycle()

        if (!shouldShowMidnightDialog || midnightTransitionData == null) return
        val data = midnightTransitionData!!

        if (data.shouldNavigateToAnalyticsOnly) {
            LaunchedEffect(
                data.periodEndDate,
                data.remainingAmount,
                data.totalBudget,
                data.totalSpent,
            ) {
                midnightTransitionManager.onTransitionDialogConfirmed()
                navController.navigate(Screen.Analytics.route) {
                    popUpTo(Screen.Main.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
            return
        }

        val periodLabel = if (data.isPersistedReopen) {
            stringResource(R.string.rollover_dialog_pending_label)
        } else {
            "${data.periodStartDate.dayOfMonth} ${
                data.periodStartDate.month.name.lowercase().take(3)
            } - ${data.periodEndDate.dayOfMonth} ${
                data.periodEndDate.month.name.lowercase().take(3)
            }"
        }

        fun resolveAndMaybeNavigate(strategy: RemainingBudgetStrategy?) {
            lifecycleScope.launch {
                midnightTransitionManager.resolveUnresolvedSurplus(strategy)
                if (!data.isPersistedReopen) {
                    navController.navigate(Screen.Analytics.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        }

        RolloverDialog(
            remainingAmount = data.remainingAmount,
            currencyCode = data.currencyCode,
            periodLabel = periodLabel,
            spentAmount = if (data.isPersistedReopen) null else data.totalSpent,
            onSplitEqually = {
                resolveAndMaybeNavigate(RemainingBudgetStrategy.SPLIT_EQUALLY)
            },
            onCarryToNextDay = {
                resolveAndMaybeNavigate(RemainingBudgetStrategy.ADD_TO_FIRST_DAY)
            },
            onViewAnalytics = { resolveAndMaybeNavigate(null) },
            onDismiss = {
                midnightTransitionManager.onTransitionDialogDismissed()
            },
        )
    }

    @Composable
    private fun HandleBudgetSetupNavigation(navController: NavHostController) {
        val needsBudgetSetup by midnightTransitionManager.needsBudgetSetup.collectAsStateWithLifecycle()
        LaunchedEffect(needsBudgetSetup, onboardingComplete.value) {
            if (needsBudgetSetup && onboardingComplete.value) {
                midnightTransitionManager.onBudgetSetupHandled()

                val hasBudget = settingsRepository.observeBudgetEndDate().first() != null
                if (hasBudget) {
                    navController.navigate(
                        Screen.Main.createRoute(
                            openWallet = true,
                            forceWalletSetup = false,
                        ),
                    ) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else if (needsBudgetSetup && !onboardingComplete.value) {
                logcat {
                    "needsBudgetSetup detected but onboarding NOT complete -> suppressing wallet setup navigation until onboarding finishes"
                }
            }
        }
    }
}
