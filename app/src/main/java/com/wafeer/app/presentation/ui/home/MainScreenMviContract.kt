package com.wafeer.app.presentation.ui.home

import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.FirstLaunchTutorialStage
import com.wafeer.app.domain.model.LeftoverChoice
import com.wafeer.app.domain.model.Transaction
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetEditorIntent
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetNumpadIntent
import com.wafeer.app.presentation.ui.budget.mvi.intent.BudgetTransactionIntent

sealed interface MainScreenUiIntent {
    data class QueueDeleteWithUndo(
        val transaction: Transaction,
        val message: String,
    ) : MainScreenUiIntent

    data object CancelPendingDelete : MainScreenUiIntent
    data object DismissSnackbar : MainScreenUiIntent

    data class AdvanceTutorial(val expected: FirstLaunchTutorialStage) : MainScreenUiIntent
    data class SetShownStage(val stage: FirstLaunchTutorialStage?) : MainScreenUiIntent

    data class SetDragProgress(val progress: Float) : MainScreenUiIntent

    data class ShowBudgetPeriodSheet(val forceSetup: Boolean = false) : MainScreenUiIntent

    data object HideBudgetPeriodSheet : MainScreenUiIntent

    data class SetSelectedPeriod(val period: BudgetPeriod) : MainScreenUiIntent

    data object MarkWalletSheetOpened : MainScreenUiIntent

    data class SetTutorialBoxCompleted(val completed: Boolean) : MainScreenUiIntent

    data object ExitDemoModeAndStartRealBudget : MainScreenUiIntent

    data class ProcessBudgetTransactionIntent(val intent: BudgetTransactionIntent) : MainScreenUiIntent
    data class ProcessBudgetEditorIntent(val intent: BudgetEditorIntent) : MainScreenUiIntent
    data class ProcessBudgetNumpadIntent(val intent: BudgetNumpadIntent) : MainScreenUiIntent
}

sealed interface MainScreenUiEffect {
    data class ShowUndoSnackbar(
        val message: String,
        val actionLabel: String,
    ) : MainScreenUiEffect

    data class RequestUndo(val transaction: Transaction) : MainScreenUiEffect

    data class UpdateDragProgress(val progress: Float) : MainScreenUiEffect


    data object OpenAnalytics : MainScreenUiEffect
}

data class MainScreenUiState(
    val pendingDeleteTransaction: Transaction? = null,
    val isSnackbarVisible: Boolean = false,
    val snackbarMessage: String = "",
    val snackbarActionLabel: String = "",
    val snackbarHasUndo: Boolean = false,

    val shownStage: FirstLaunchTutorialStage? = null,

    val showBudgetPeriodSheet: Boolean = false,
    val forceBudgetPeriodSheetSetup: Boolean = false,
    val selectedViewPeriod: BudgetPeriod? = null,
    val walletSheetOpened: Boolean = false,

    val onboardingCompleted: Boolean = false,
    val tutorialStage: FirstLaunchTutorialStage = FirstLaunchTutorialStage.COMPLETED,
    val tutorialBoxCompleted: Boolean = false,
    val showCreditQuickToggleFeature: Boolean = false,
    val directCategoryPopupEnabled: Boolean = false,
    val categoryGridModeEnabled: Boolean = false,
    val extraNoteEnabled: Boolean = false,
    val newCategoryTagEnabled: Boolean = false,
    val demoModeActive: Boolean = false,
    val demoMissionCompleted: Boolean = false,
)

data class MainScreenActions(
    val onProcessIntent: (MainScreenUiIntent) -> Unit,
    val onAdvanceTutorial: (FirstLaunchTutorialStage) -> Unit,
    val onNavigateToAnalytics: () -> Unit = {},
    val onNavigateToSettings: () -> Unit = {},
    val onNavigateToWallet: () -> Unit = {},
    val onPeriodSelected: (BudgetPeriod) -> Unit = {},
    val onShowSnackbar: (String) -> Unit = {},
    val onUnresolvedSurplusBannerClick: () -> Unit = {},
    val onLeftoverChoice: (LeftoverChoice) -> Unit = {},
    val onCreateCategory: suspend (String) -> Boolean = { false },
)

data class MainScreenFeatureFlags(
    val showCreditQuickToggleFeature: Boolean,
    val directCategoryPopupEnabled: Boolean,
    val categoryGridModeEnabled: Boolean,
    val extraNoteEnabled: Boolean,
    val newCategoryTagEnabled: Boolean,
)

data class MainScreenBudgetPeriodState(
    val showBudgetPeriodSheet: Boolean,
    val forceBudgetPeriodSheetSetup: Boolean,
    val selectedViewPeriod: BudgetPeriod?,
    val onPeriodSelected: (BudgetPeriod) -> Unit,
    val showLeftoverChoice: Boolean = false,
    val onLeftoverChoiceVisible: (Boolean) -> Unit = {},
)
