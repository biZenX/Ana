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
import androidx.compose.material.icons.rounded.VisibilityOff
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
            showTutorial = showNumpadTutorial,
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
                when (index) {
                    0 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_numpad_title),
                        description = stringResource(R.string.tutorial_numpad_description),
                        icon = Icons.Rounded.Dialpad,
                    )
                    2 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_settings_title),
                        description = stringResource(R.string.tutorial_settings_description),
                        icon = Icons.Rounded.Tune,
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
                    )
                    3 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_comment_title),
                        description = stringResource(R.string.tutorial_comment_description),
                        icon = Icons.AutoMirrored.Rounded.Label,
                    )
                    4 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_recurrent_title),
                        description = stringResource(R.string.tutorial_recurrent_description),
                        icon = Icons.Rounded.EventRepeat,
                    )
                    5 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_analytics_title),
                        description = stringResource(R.string.tutorial_analytics_description),
                        icon = Icons.Rounded.BarChart,
                    )
                    6 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_privacy_title),
                        description = stringResource(R.string.tutorial_privacy_description),
                        icon = Icons.Rounded.VisibilityOff,
                    )
                    7 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_calc_title),
                        description = stringResource(R.string.tutorial_calc_description),
                        icon = Icons.Rounded.Calculate,
                    )
                    8 -> TutorialTooltip(
                        title = stringResource(R.string.tutorial_credit_toggle_title),
                        description = stringResource(R.string.tutorial_credit_toggle_description),
                        icon = Icons.Rounded.CreditCard,
                    )
                    else -> Text(text = "")
                }
            },
        ) {
            BudgetFormulaHost {
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
            }
        }
    }
}
