package com.wafeer.app.presentation.ui.home

import android.os.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafeer.app.R
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.presentation.ui.budget.BudgetViewModel
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetNumpadIntent
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetTransactionIntent
import com.wafeer.app.presentation.ui.changelog.ChangelogGate
import com.wafeer.app.presentation.ui.theme.component.budget.formula.BudgetFormulaHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.wafeer.app.presentation.LocalWindowInsets
import com.wafeer.app.presentation.ui.demo.SimulationDemoBanner
import com.wafeer.app.presentation.ui.tutorial.SensorDiagnosticDialog
import com.wafeer.app.presentation.ui.tutorial.TutorialBox
import com.wafeer.app.presentation.ui.tutorial.TutorialTooltip
import com.wafeer.app.presentation.ui.tutorial.rememberTutorialBoxState
import logcat.logcat

private const val TAG = "MainScreen"

@Composable
fun MainScreen(
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    openWalletOnStart: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val mainScreenState by mainScreenViewModel.uiState.collectAsStateWithLifecycle()
    val budgetUiState by budgetViewModel.uiState.collectAsStateWithLifecycle()
    val censorDiagnosticState by mainScreenViewModel.censorDiagnosticState.collectAsStateWithLifecycle()

    val tutorialStage = mainScreenState.tutorialStage
    val tutorialBoxCompleted = mainScreenState.tutorialBoxCompleted

    val effectiveSelectedPeriod =
        mainScreenState.selectedViewPeriod ?: budgetUiState.budgetSettings?.period
        ?: BudgetPeriod.DAILY

    logcat(TAG) { "MainScreen composed (openWalletOnStart=$openWalletOnStart, effectivePeriod=$effectiveSelectedPeriod)" }

    LaunchedEffect(Unit) {
        logcat(TAG) { "Triggering onRequestNotificationPermission from MainScreen LaunchedEffect" }
        onRequestNotificationPermission()
    }

    LaunchedEffect(openWalletOnStart, mainScreenState.walletSheetOpened) {
        if (openWalletOnStart && !mainScreenState.walletSheetOpened) {
            mainScreenViewModel.processIntent(
                MainScreenUiIntent.ShowBudgetPeriodSheet(forceSetup = true),
                tutorialStage,
            )
            mainScreenViewModel.processIntent(
                MainScreenUiIntent.MarkWalletSheetOpened,
                tutorialStage,
            )
        }
    }

    LaunchedEffect(Unit) {
        mainScreenViewModel.effects.collect { effect ->
            when (effect) {
                is MainScreenUiEffect.RequestUndo -> {
                    budgetViewModel.processIntent(
                        BudgetTransactionIntent.RestoreTransactionTapped(effect.transaction),
                    )
                }

                is MainScreenUiEffect.UpdateDragProgress -> {
                    budgetViewModel.processIntent(
                        BudgetNumpadIntent.SetDragProgress(effect.progress),
                    )
                }

                is MainScreenUiEffect.OpenAnalytics -> {
                    onNavigateToAnalytics()
                }

                is MainScreenUiEffect.ShowUndoSnackbar -> {}
            }
        }
    }

    val hasNoBudget = budgetUiState.budgetSettings == null || budgetUiState.budgetSettings?.endDate == null
    val tutorialWalkOrder = remember(hasNoBudget) {
        if (hasNoBudget) {
            listOf(1, 0, 2, 3, 4, 8, 5, 6, 7)
        } else {
            listOf(0, 1, 2, 3, 4, 8, 5, 6, 7)
        }
    }
    var isFreePracticeActive by remember { mutableStateOf(false) }
    var showSensorDiagnostic by remember { mutableStateOf(false) }
    val showNumpadTutorial = !tutorialBoxCompleted && !mainScreenState.showBudgetPeriodSheet
    val tutorialBoxState = rememberTutorialBoxState(order = tutorialWalkOrder)

    LaunchedEffect(tutorialBoxCompleted) {
        if (!tutorialBoxCompleted && tutorialBoxState.isCompleted) {
            tutorialBoxState.resetForReplay()
        }
    }

    ChangelogGate(
        currentVersionCode = run {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                0,
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION") info.versionCode
            }
        },
    ) {
        TutorialBox(
            showTutorial = showNumpadTutorial && !isFreePracticeActive && !mainScreenState.demoModeActive,
            onFreePractice = { isFreePracticeActive = true },
            onTutorialCompleted = {
                logcat(TAG) { "TutorialBox completed → persisting tutorialBoxCompleted=true" }
                mainScreenViewModel.processIntent(
                    MainScreenUiIntent.SetTutorialBoxCompleted(true),
                    tutorialStage,
                )
            },
            onTutorialReopened = {
                logcat(TAG) { "TutorialBox reopened (gated target became measurable) → persisting tutorialBoxCompleted=false" }
                mainScreenViewModel.processIntent(
                    MainScreenUiIntent.SetTutorialBoxCompleted(false),
                    tutorialStage,
                )
            },
            onCutoutClick = { index ->
                if (index == 1) {
                    mainScreenViewModel.processIntent(
                        MainScreenUiIntent.ShowBudgetPeriodSheet(forceSetup = hasNoBudget),
                        tutorialStage,
                    )
                }
                tutorialBoxState.advance()
            },
            state = tutorialBoxState,
            tutorialTarget = { index ->
                val practiceBadge = stringResource(R.string.tutorial_practice_mode)
                when (index) {
                    0 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_numpad_title),
                        description = stringResource(R.string.tutorial_numpad_description),
                        icon = Icons.Rounded.Dialpad,
                        badgeText = practiceBadge,
                    )
                    2 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_settings_title),
                        description = stringResource(R.string.tutorial_settings_description),
                        icon = Icons.Rounded.Tune,
                        badgeText = practiceBadge,
                    )
                    1 -> TutorialTooltip(
                        title = if (hasNoBudget) {
                            stringResource(R.string.tutorial_budget_pill_setup_title)
                        } else {
                            stringResource(R.string.tutorial_budget_pill_title)
                        },
                        description = if (hasNoBudget) {
                            stringResource(R.string.tutorial_budget_pill_setup_description)
                        } else {
                            stringResource(R.string.tutorial_budget_pill_description)
                        },
                        icon = Icons.Rounded.AccountBalanceWallet,
                        badgeText = practiceBadge,
                    )
                    3 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_comment_title),
                        description = stringResource(R.string.tutorial_comment_description),
                        icon = Icons.AutoMirrored.Rounded.Label,
                        badgeText = practiceBadge,
                    )
                    4 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_recurrent_title),
                        description = stringResource(R.string.tutorial_recurrent_description),
                        icon = Icons.Rounded.EventRepeat,
                        badgeText = practiceBadge,
                    )
                    5 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_analytics_title),
                        description = stringResource(R.string.tutorial_analytics_description),
                        icon = Icons.Rounded.BarChart,
                        badgeText = practiceBadge,
                    )
                    6 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_privacy_title),
                        description = stringResource(R.string.tutorial_privacy_description),
                        icon = Icons.Rounded.VisibilityOff,
                        badgeText = stringResource(R.string.tutorial_interactive_test_mode),
                        actionContent = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { mainScreenViewModel.toggleCensor() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.tutorial_test_censor_action),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                OutlinedButton(
                                    onClick = { showSensorDiagnostic = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Sensors,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.tutorial_diagnose_sensor_action),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    )
                    7 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_calc_title),
                        description = stringResource(R.string.tutorial_calc_description),
                        icon = Icons.Rounded.Calculate,
                        badgeText = practiceBadge,
                    )
                    8 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_credit_toggle_title),
                        description = stringResource(R.string.tutorial_credit_toggle_description),
                        icon = Icons.Rounded.CreditCard,
                        badgeText = practiceBadge,
                    )
                    else -> Text(text = "")
                }
            },
        ) {
            BudgetFormulaHost {
                Box(Modifier.fillMaxSize()) {
                    MainScreenContent(
                        mainScreenState = mainScreenState,
                        budgetUiState = budgetUiState,
                        actions =
                            MainScreenActions(
                                onProcessIntent = { intent ->
                                    when (intent) {
                                        is MainScreenUiIntent.ProcessBudgetTransactionIntent -> {
                                            budgetViewModel.processIntent(intent.intent)
                                        }

                                        is MainScreenUiIntent.ProcessBudgetEditorIntent -> {
                                            budgetViewModel.processIntent(intent.intent)
                                        }

                                        is MainScreenUiIntent.ProcessBudgetNumpadIntent -> {
                                            budgetViewModel.processIntent(intent.intent)
                                        }

                                        else -> {
                                            mainScreenViewModel.processIntent(intent, tutorialStage)
                                        }
                                    }
                                },
                                onAdvanceTutorial = { expected ->
                                    mainScreenViewModel.processIntent(
                                        MainScreenUiIntent.AdvanceTutorial(expected),
                                        tutorialStage
                                    )
                                },
                                onNavigateToAnalytics = onNavigateToAnalytics,
                                onNavigateToSettings = onNavigateToSettings,
                                onUnresolvedSurplusBannerClick = budgetViewModel::onUnresolvedSurplusBannerClicked,
                                onLeftoverChoice = budgetViewModel::onLeftoverChoice,
                                onCreateCategory = budgetViewModel::createCategory,
                                onPeriodSelected = { period ->
                                    mainScreenViewModel.processIntent(
                                        MainScreenUiIntent.SetSelectedPeriod(period), tutorialStage
                                    )
                                },
                            ),
                        openWalletOnStart = openWalletOnStart,
                        tutorialBoxState = tutorialBoxState,
                    )

                    if (mainScreenState.demoModeActive) {
                        SimulationDemoBanner(
                            demoMissionCompleted = mainScreenState.demoMissionCompleted,
                            onStartRealBudget = {
                                mainScreenViewModel.processIntent(
                                    MainScreenUiIntent.ExitDemoModeAndStartRealBudget,
                                    tutorialStage,
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(
                                    top = LocalWindowInsets.current.calculateTopPadding() + 12.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                ),
                        )
                    } else if (!tutorialBoxCompleted && isFreePracticeActive) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(
                                    top = LocalWindowInsets.current.calculateTopPadding() + 12.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                ),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 6.dp,
                            shadowElevation = 8.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tune,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.tutorial_sandbox_banner_title),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.tutorial_sandbox_banner_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = { isFreePracticeActive = false },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    ) {
                                        Text(
                                            text = stringResource(R.string.tutorial_sandbox_resume_guide),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            isFreePracticeActive = false
                                            mainScreenViewModel.processIntent(
                                                MainScreenUiIntent.SetTutorialBoxCompleted(true),
                                                tutorialStage,
                                            )
                                        },
                                        modifier = Modifier.weight(1.5f),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    ) {
                                        Text(
                                            text = stringResource(R.string.tutorial_sandbox_finish),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showSensorDiagnostic) {
            SensorDiagnosticDialog(
                diagnosticState = censorDiagnosticState,
                onToggleCensor = { mainScreenViewModel.toggleCensor() },
                onDismiss = { showSensorDiagnostic = false },
            )
        }
    }
}
