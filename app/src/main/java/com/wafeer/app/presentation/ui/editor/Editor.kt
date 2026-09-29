@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.wafeer.app.presentation.ui.editor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.CreditScore
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafeer.app.R
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.BudgetSettings
import com.wafeer.app.domain.model.BudgetSplitMode
import com.wafeer.app.domain.model.BudgetState
import com.wafeer.app.domain.model.RecurrentFrequency
import com.wafeer.app.domain.model.SupportedCurrency
import com.wafeer.app.domain.model.SymbolPosition
import com.wafeer.app.presentation.ui.budget.BudgetUiState
import com.wafeer.app.presentation.ui.editor.calculation.evaluateCalculation
import com.wafeer.app.presentation.ui.editor.category.CategoryToolbar
import com.wafeer.app.presentation.ui.editor.category.NewCategoryTag
import com.wafeer.app.presentation.ui.editor.category.EditableCategoryTag
import com.wafeer.app.presentation.ui.editor.category.FocusController
import com.wafeer.app.presentation.ui.editor.dialogs.CreditCutoffDayDialog
import com.wafeer.app.presentation.ui.editor.dialogs.RecurrentExpenseDialog
import com.wafeer.app.presentation.ui.editor.note.EditableNoteTag
import com.wafeer.app.presentation.ui.editor.sheets.BudgetPeriodSheet
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.colorButton
import com.wafeer.app.presentation.ui.theme.component.AutoResizeBasicTextField
import com.wafeer.app.presentation.ui.theme.component.budget.formula.LocalBudgetFormulaHost
import com.wafeer.app.presentation.ui.theme.component.budget.pill.BudgetPill
import com.wafeer.app.presentation.ui.theme.component.budget.pill.BUDGET_PILL_FORMULA_KEY
import com.wafeer.app.presentation.ui.theme.component.numpad.EditStage
import com.wafeer.app.presentation.ui.theme.displayLargeCondensed
import com.wafeer.app.presentation.ui.theme.titleSmallCondensed
import com.wafeer.app.presentation.ui.tutorial.TutorialBoxState
import com.wafeer.app.presentation.ui.tutorial.markForTutorial
import com.wafeer.app.presentation.util.LocalCensorMode
import com.wafeer.app.presentation.util.Utils.strongHapticFeedback
import com.wafeer.app.presentation.util.Utils.weakHapticFeedback
import com.wafeer.app.presentation.util.haptic.HapticUtil.performUIHaptic
import com.wafeer.app.presentation.util.font.format.symbolOnlyCurrencyFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logcat.logcat
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

private val EditorFontSize = 96.sp
private const val EditorSymbolScale = 0.45f
private val EditorMinFontSize = 24.sp

private val CalcInputMinFontSize = 20.sp
private val CalcInputMaxFontSize = 57.sp
private val CalcResultMinFontSize = 16.sp
private val CalcResultMaxFontSize = 36.sp

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Editor(
    uiState: BudgetUiState,
    animState: AnimState,
    onInputChange: (String) -> Unit = {},
    onFocus: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAnalytics: () -> Unit = {},
    onOpenWallet: () -> Unit = {},
    onUnresolvedSurplusBannerClick: () -> Unit = {},
    onPendingLeftoverClick: () -> Unit = {},
    leftoverChoiceOpen: Boolean = false,
    openWalletOnStart: Boolean = false,
    showBudgetPeriodSheet: Boolean = false,
    forceBudgetPeriodSheetSetup: Boolean = false,
    selectedViewPeriod: BudgetPeriod? = null,
    onShowBudgetPeriodSheet: () -> Unit = {},
    onHideBudgetPeriodSheet: () -> Unit = {},
    onPeriodSelected: (BudgetPeriod) -> Unit = {},
    onCommentClick: () -> Unit,
    onBudgetPillClickForTutorial: () -> Unit = {},
    onAnalyticsClickForTutorial: () -> Unit = {},
    onChangePeriod: (BudgetPeriod) -> Unit = {},
    onFinishBudgetEarly: () -> Unit = {},
    onSaveBudget: (BudgetSettings) -> Unit = {},
    onCommentUpdate: (String) -> Unit = {},
    onNoteUpdate: (String) -> Unit = {},
    onDeleteTag: (String) -> Unit = {},
    onCreateCategory: suspend (String) -> Boolean = { true },
    onCategoryEditingChanged: (Boolean) -> Unit = {},
    onRecurrentToggle: (Boolean) -> Unit = {},
    onCreditToggle: (Boolean) -> Unit = {},
    onWalletIncomeToggle: (Boolean) -> Unit = {},
    onDismissTip: (Int) -> Unit = {},
    showCreditQuickToggleFeature: Boolean = false,
    extraNoteEnabled: Boolean = false,
    newCategoryTagEnabled: Boolean = false,
    directCategoryPopupEnabled: Boolean = false,
    categoryGridModeEnabled: Boolean = false,
    isCategoryGridVisible: Boolean = false,
    isCalculation: Boolean = false,
    onShowCategoryGrid: () -> Unit = {},
    onHideCategoryGrid: () -> Unit = {},
    onDisableCalculationMode: () -> Unit = {},
    onDismissRecurrentDialog: () -> Unit = {},
    onDismissCreditCutoffDialog: () -> Unit = {},
    onRecurrentExpenseConfirm: (RecurrentFrequency, LocalDate, Int?, String) -> Unit = { _, _, _, _ -> },
    onCreditCutoffConfirm: (Int) -> Unit = {},
    onSaveAllowanceDays: (Boolean, Set<Int>) -> Unit = { _, _ -> },
    onApply: () -> Unit = {},
    showAnalyticsButton: Boolean = true,
    showSettingsButton: Boolean = true,
    budgetPillHintAnchorModifier: Modifier = Modifier,
    analyticsHintAnchorModifier: Modifier = Modifier,
    tutorialBoxState: TutorialBoxState? = null,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val budgetPillBounce = remember { Animatable(1f) }
    val activeTransactionCount = uiState.transactions.count { !it.isDeleted }
    val lastTransactionCount = remember { mutableStateOf(activeTransactionCount) }
    LaunchedEffect(activeTransactionCount) {
        val added = activeTransactionCount == lastTransactionCount.value + 1
        lastTransactionCount.value = activeTransactionCount
        if (added) {
            budgetPillBounce.animateTo(
                1.1f,
                tween(durationMillis = 90, easing = FastOutSlowInEasing)
            )
            budgetPillBounce.animateTo(
                1f,
                tween(durationMillis = 140, easing = FastOutSlowInEasing)
            )
            view.strongHapticFeedback()
        }
    }

    val isSquareScreen =
        configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat() > 0.8f
    val topBarHeight = if (isSquareScreen) 54.dp else 66.dp

    val editorFocusController = remember { FocusController() }

    if (uiState.showRecurrentDialog) {
        RecurrentExpenseDialog(
            budgetSettings = uiState.budgetSettings,
            onDismiss = onDismissRecurrentDialog,
            onConfirm = onRecurrentExpenseConfirm,
            fallbackComments = mapOf(
                RecurrentFrequency.WEEKLY to stringResource(R.string.recurrent_ticket_weekly_unnamed),
                RecurrentFrequency.BIWEEKLY to stringResource(R.string.recurrent_ticket_biweekly_unnamed),
                RecurrentFrequency.MONTHLY to stringResource(R.string.recurrent_ticket_monthly_unnamed),
            ),
        )
    }

    if (uiState.showCreditCutoffDialog) {
        CreditCutoffDayDialog(
            initialDay = uiState.budgetSettings?.creditCardCutoffDay ?: 15,
            onDismiss = onDismissCreditCutoffDialog,
            onConfirm = onCreditCutoffConfirm
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorButton)
            .statusBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onFocus() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val periodSurplus = uiState.hasUnresolvedRolloverSurplus
            val pendingLeftover = uiState.budgetState?.pendingLeftover?.takeIf { it.signum() > 0 }
            BudgetPill(
                budgetState = uiState.budgetState,
                budgetSettings = uiState.budgetSettings,
                viewPeriod = selectedViewPeriod ?: BudgetPeriod.DAILY,
                currencyCode = uiState.budgetSettings?.currencyCode ?: "USD",
                centerRemainingAmount = animState == AnimState.EDITING,
                splitMode = uiState.budgetSettings?.splitMode ?: BudgetSplitMode.STATIC,
                calculationPreview = uiState.calculationPreview,
                draftAmount = uiState.numpadDraftAmount,
                hasUnresolvedSurplus = periodSurplus || pendingLeftover != null,
                unresolvedSurplusAmount = if (periodSurplus) uiState.unresolvedSurplusAmount else pendingLeftover,
                onOpenBudgetSheet = {
                    view.weakHapticFeedback()
                    onShowBudgetPeriodSheet()
                },
                onUnresolvedSurplusClick = {
                    view.weakHapticFeedback()
                    if (periodSurplus) onUnresolvedSurplusBannerClick() else onPendingLeftoverClick()
                },
                pinSurplusFace = leftoverChoiceOpen,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleX = budgetPillBounce.value
                        scaleY = budgetPillBounce.value
                    }
                    .animateContentSize(animationSpec = tween(200))
                    .padding(end = 8.dp)
                    .then(budgetPillHintAnchorModifier)
            )

            AnimatedContent(
                targetState = animState == AnimState.EDITING,
                modifier = Modifier.fillMaxHeight(),
                transitionSpec = {
                    slideInHorizontally(animationSpec = tween(200)) { it } + fadeIn(tween(200)) togetherWith slideOutHorizontally(
                        animationSpec = tween(200)
                    ) { -it } + fadeOut(
                        tween(
                            200
                        )
                    )
                },
                label = "topBarTrailingSwitch"
            ) { isEditing ->
                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val walletDescription = stringResource(R.string.wallet_mode_tooltip)
                        val recurrentDescription = "Recurrent payment"
                        val creditDescription = "Credit card payment"

                        if (showCreditQuickToggleFeature) {
                            FlowRow(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    ButtonGroupDefaults.ConnectedSpaceBetween
                                ),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    tooltip = {
                                        PlainTooltip(
                                            modifier = Modifier.semantics {
                                                liveRegion = LiveRegionMode.Assertive
                                                paneTitle = creditDescription
                                            }
                                        ) { Text(creditDescription) }
                                    },
                                    state = rememberTooltipState(),
                                ) {
                                    ToggleButton(
                                        checked = uiState.isCreditEnabled,
                                        onCheckedChange = {
                                            performUIHaptic(view)
                                            onCreditToggle(it)
                                        },
                                        shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .semantics { role = Role.RadioButton }
                                            .let { m ->
                                                if (tutorialBoxState != null) m.markForTutorial(
                                                    tutorialBoxState,
                                                    index = 8
                                                ) else m
                                            },
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                                alpha = 0.65f
                                            ),
                                            checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.tertiary,
                                            checkedContentColor = MaterialTheme.colorScheme.onTertiary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CreditCard,
                                            contentDescription = creditDescription
                                        )
                                    }
                                }

                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    tooltip = {
                                        PlainTooltip(
                                            modifier = Modifier.semantics {
                                                liveRegion = LiveRegionMode.Assertive
                                                paneTitle = walletDescription
                                            }
                                        ) { Text(walletDescription) }
                                    },
                                    state = rememberTooltipState(),
                                ) {
                                    ToggleButton(
                                        checked = uiState.isWalletIncomeEnabled,
                                        onCheckedChange = {
                                            performUIHaptic(view)
                                            onWalletIncomeToggle(it)
                                        },
                                        shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .semantics { role = Role.RadioButton },
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                                alpha = 0.65f
                                            ),
                                            checkedContainerColor = Color(0xFF10B981),
                                            contentColor = Color(0xFF10B981),
                                            checkedContentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AccountBalanceWallet,
                                            contentDescription = walletDescription
                                        )
                                    }
                                }

                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    tooltip = {
                                        PlainTooltip(
                                            modifier = Modifier.semantics {
                                                liveRegion = LiveRegionMode.Assertive
                                                paneTitle = recurrentDescription
                                            }
                                        ) { Text(recurrentDescription) }
                                    },
                                    state = rememberTooltipState(),
                                ) {
                                    ToggleButton(
                                        checked = uiState.isRecurrentEnabled,
                                        onCheckedChange = { checked ->
                                            performUIHaptic(view)
                                            onRecurrentToggle(checked)
                                        },
                                        shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .semantics { role = Role.RadioButton }
                                            .let { m ->
                                                if (tutorialBoxState != null) m.markForTutorial(
                                                    tutorialBoxState,
                                                    index = 4
                                                ) else m
                                            },
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                                alpha = 0.65f
                                            ),
                                            checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.tertiary,
                                            checkedContentColor = MaterialTheme.colorScheme.onTertiary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.EventRepeat,
                                            contentDescription = recurrentDescription
                                        )
                                    }
                                }
                            }
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    ButtonGroupDefaults.ConnectedSpaceBetween
                                ),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    tooltip = {
                                        PlainTooltip(
                                            modifier = Modifier.semantics {
                                                liveRegion = LiveRegionMode.Assertive
                                                paneTitle = walletDescription
                                            }
                                        ) { Text(walletDescription) }
                                    },
                                    state = rememberTooltipState(),
                                ) {
                                    ToggleButton(
                                        checked = uiState.isWalletIncomeEnabled,
                                        onCheckedChange = {
                                            performUIHaptic(view)
                                            onWalletIncomeToggle(it)
                                        },
                                        shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .semantics { role = Role.RadioButton },
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                                alpha = 0.65f
                                            ),
                                            checkedContainerColor = Color(0xFF10B981),
                                            contentColor = Color(0xFF10B981),
                                            checkedContentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AccountBalanceWallet,
                                            contentDescription = walletDescription
                                        )
                                    }
                                }

                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Below
                                    ),
                                    tooltip = {
                                        PlainTooltip(
                                            modifier = Modifier.semantics {
                                                liveRegion = LiveRegionMode.Assertive
                                                paneTitle = recurrentDescription
                                            }
                                        ) { Text(recurrentDescription) }
                                    },
                                    state = rememberTooltipState(),
                                ) {
                                    ToggleButton(
                                        checked = uiState.isRecurrentEnabled,
                                        onCheckedChange = { checked ->
                                            performUIHaptic(view)
                                            onRecurrentToggle(checked)
                                        },
                                        shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .semantics { role = Role.RadioButton }
                                            .let { m ->
                                                if (tutorialBoxState != null) m.markForTutorial(
                                                    tutorialBoxState,
                                                    index = 4
                                                ) else m
                                            },
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                                alpha = 0.65f
                                            ),
                                            checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.tertiary,
                                            checkedContentColor = MaterialTheme.colorScheme.onTertiary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.EventRepeat,
                                            contentDescription = recurrentDescription
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (showAnalyticsButton) {
                            IconButton(
                                onClick = {
                                    onAnalyticsClickForTutorial()
                                    view.weakHapticFeedback()
                                    onOpenAnalytics()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .then(analyticsHintAnchorModifier)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.BarChart,
                                    contentDescription = stringResource(R.string.analytics_title),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }

                        if (showSettingsButton) {
                            val isCensored = LocalCensorMode.current
                            IconButton(
                                onClick = {
                                    onOpenSettings()
                                    view.weakHapticFeedback()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .let { m ->
                                        if (tutorialBoxState != null) m.markForTutorial(
                                            tutorialBoxState,
                                            index = 2
                                        ) else m
                                    }
                            ) {
                                BadgedBox(
                                    badge = { if (isCensored) Badge() },
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = stringResource(R.string.settings_title),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(28.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedContent(
            targetState = if (animState == AnimState.EDITING) AnimState.EDITING else AnimState.IDLE,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            transitionSpec = {
                if (targetState == AnimState.EDITING) {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                } else {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                }
            },
            label = "editorContent"
        ) { state ->
            if (state == AnimState.EDITING) {
                logcat("IMPL:TUTORIAL") { "Editor entered EDITING state → composing EditingContent (category tag + recurrent will register)" }
                EditingContent(
                    input = uiState.numpadInput,
                    currencyCode = uiState.budgetSettings?.currencyCode ?: "USD",
                    tags = uiState.tags,
                    currentComment = uiState.currentComment,
                    currentNote = uiState.currentNote,
                    isWalletIncomeEnabled = uiState.isWalletIncomeEnabled,
                    extraNoteEnabled = extraNoteEnabled,
                    onCommentUpdate = onCommentUpdate,
                    onNoteUpdate = onNoteUpdate,
                    onDeleteTag = onDeleteTag,
                    onCategoryEditingChanged = onCategoryEditingChanged,
                    editorFocusController = editorFocusController,
                    directCategoryPopupEnabled = directCategoryPopupEnabled,
                    categoryGridModeEnabled = categoryGridModeEnabled,
                    isCategoryGridVisible = isCategoryGridVisible,
                    isCalculation = isCalculation,
                    onShowCategoryGrid = onShowCategoryGrid,
                    onHideCategoryGrid = onHideCategoryGrid,
                    onDisableCalculationMode = onDisableCalculationMode,
                    onApply = onApply,
                    tutorialBoxState = tutorialBoxState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                IdleContent(
                    uiState = uiState,
                    onCreateCategory = onCreateCategory,
                    newCategoryTagEnabled = newCategoryTagEnabled,
                    onDismissTip = onDismissTip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

    if (showBudgetPeriodSheet) {
        ModalBottomSheet(
            onDismissRequest = onHideBudgetPeriodSheet,
            sheetState = sheetState,
        ) {
            logcat {
                "Opening BudgetPeriodSheet: forceBudgetPeriodSheetSetup=$forceBudgetPeriodSheetSetup, hasBudgetSettings=${uiState.budgetSettings != null}, currentPeriodId=${uiState.currentPeriodId}, startInEditMode=$forceBudgetPeriodSheetSetup"
            }
            val formulaHost = LocalBudgetFormulaHost.current
            BudgetPeriodSheet(
                budgetSettings = uiState.budgetSettings,
                budgetState = uiState.budgetState,
                selectedPeriod = selectedViewPeriod,
                pendingExpensesCount = uiState.pendingExpensesForNextPeriod.size,
                currencyCode = uiState.budgetSettings?.currencyCode ?: "USD",
                startInEditMode = forceBudgetPeriodSheetSetup,
                allowanceDaysEnabled = uiState.allowanceDaysEnabled,
                activeSpendingDays = uiState.activeSpendingDays,
                onSaveAllowanceDays = onSaveAllowanceDays,
                onPeriodSelected = { newPeriod ->
                    logcat { "BudgetPeriodSheet onPeriodSelected -> newPeriod=$newPeriod" }
                    onPeriodSelected(newPeriod)
                },
                onSaveBudget = { newSettings ->
                    logcat { "BudgetPeriodSheet onSaveBudget -> $newSettings" }
                    onSaveBudget(newSettings)
                    scope.launch { sheetState.hide() }
                    onHideBudgetPeriodSheet()
                },
                onEditBudget = {
                    onShowBudgetPeriodSheet()
                    scope.launch { sheetState.hide() }
                },
                onFinishEarly = {
                    onFinishBudgetEarly()
                    onOpenAnalytics()
                    scope.launch { sheetState.hide() }
                    onHideBudgetPeriodSheet()
                },
                onShowFormula = formulaHost?.let { host ->
                    { request ->
                        scope.launch {
                            sheetState.hide()
                            onHideBudgetPeriodSheet()
                            host.show(BUDGET_PILL_FORMULA_KEY, request)
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun EditingContent(
    input: String,
    currencyCode: String,
    tags: List<String>,
    currentComment: String,
    currentNote: String = "",
    isWalletIncomeEnabled: Boolean = false,
    extraNoteEnabled: Boolean = false,
    onCommentUpdate: (String) -> Unit,
    onNoteUpdate: (String) -> Unit = {},
    onDeleteTag: (String) -> Unit,
    onCategoryEditingChanged: (Boolean) -> Unit = {},
    editorFocusController: FocusController,
    directCategoryPopupEnabled: Boolean = false,
    categoryGridModeEnabled: Boolean = false,
    isCategoryGridVisible: Boolean = false,
    isCalculation: Boolean = false,
    onShowCategoryGrid: () -> Unit = {},
    onHideCategoryGrid: () -> Unit = {},
    onDisableCalculationMode: () -> Unit = {},
    onApply: () -> Unit = {},
    tutorialBoxState: TutorialBoxState? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = symbolOnlyCurrencyFormat(currencyCode)

    val effectiveTags = remember(isWalletIncomeEnabled, tags) {
        if (isWalletIncomeEnabled) {
            val incomeTags = listOf("هدية", "رد دين", "مكافأة", "شغل إضافي")
            (incomeTags + tags).distinct()
        } else {
            tags
        }
    }

    val hasExpressionOperators = remember(input) { input.any { it in "+-×÷" } }

    val isSimpleSignEntry = remember(input) {
        (input.startsWith("+") || input.startsWith("-")) &&
                input.substring(1).none { it in "+-×÷" }
    }

    val calculationResult = remember(input, hasExpressionOperators) {
        if (!hasExpressionOperators || input.isEmpty()) return@remember null

        val last = input.lastOrNull()
        if (last != null && (last in "+-×÷" || last == '.')) {
            null
        } else {
            evaluateCalculation(input)
        }
    }

    val symbolStyle = MaterialTheme.typography.titleSmallCondensed.toSpanStyle()
    val annotatedDisplayContent =
        remember(input, currencyCode, hasExpressionOperators, symbolStyle) {
            val supportedCurrency = SupportedCurrency.findByCode(currencyCode)
            val currencySymbol = supportedCurrency?.symbol ?: "$"
            val isSymbolAtEnd = supportedCurrency?.symbolPosition == SymbolPosition.END

            if (hasExpressionOperators) {
                val leadingSign =
                    if (input.startsWith("+") || input.startsWith("-")) input[0].toString() else ""
                val remaining = if (leadingSign.isNotEmpty()) input.substring(1) else input
                val hasInnerOperators = remaining.any { it in "+-×÷" }

                val useLeadingSignOrder = leadingSign.isNotEmpty() && !hasInnerOperators

                val formattedRemaining = if (useLeadingSignOrder) {
                    val value = remaining.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val formatted = currencyFormat.format(value)
                    formatted.replace(currencySymbol, "").trim()
                } else {
                    remaining
                }

                AnnotatedString.Builder().apply {
                    if (useLeadingSignOrder) {
                        append(leadingSign)
                        if (isSymbolAtEnd) {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                            append(formattedRemaining)
                            pop()
                            pushStyle(
                                symbolStyle.copy(
                                    fontSize = EditorFontSize * EditorSymbolScale,
                                    fontWeight = FontWeight.Bold,
                                    baselineShift = BaselineShift(0f)
                                )
                            )
                            append(currencySymbol)
                            pop()
                        } else {
                            pushStyle(
                                symbolStyle.copy(
                                    fontSize = EditorFontSize * EditorSymbolScale,
                                    fontWeight = FontWeight.Bold,
                                    baselineShift = BaselineShift(0f)
                                )
                            )
                            append(currencySymbol)
                            pop()
                            pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                            append(formattedRemaining)
                            pop()
                        }
                    } else {
                        if (isSymbolAtEnd) {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                            append(input)
                            pop()
                            pushStyle(
                                symbolStyle.copy(
                                    fontSize = EditorFontSize * EditorSymbolScale,
                                    fontWeight = FontWeight.Bold,
                                    baselineShift = BaselineShift(0f)
                                )
                            )
                            append(currencySymbol)
                            pop()
                        } else {
                            pushStyle(
                                symbolStyle.copy(
                                    fontSize = EditorFontSize * EditorSymbolScale,
                                    fontWeight = FontWeight.Bold,
                                    baselineShift = BaselineShift(0f)
                                )
                            )
                            append(currencySymbol)
                            pop()
                            pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                            append(input)
                            pop()
                        }
                    }
                }.toAnnotatedString()
            } else {
                val value = input.toBigDecimalOrNull() ?: BigDecimal.ZERO
                val formatted = currencyFormat.format(value)
                val amount = formatted.replace(currencySymbol, "").trim()
                AnnotatedString.Builder().apply {
                    if (formatted.startsWith(currencySymbol)) {
                        pushStyle(
                            symbolStyle.copy(
                                fontSize = EditorFontSize * EditorSymbolScale,
                                fontWeight = FontWeight.Bold,
                                baselineShift = BaselineShift(0f)
                            )
                        )
                        append(currencySymbol)
                        pop()
                        pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                        append(amount)
                        pop()
                    } else {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                        append(amount)
                        pop()
                        pushStyle(
                            symbolStyle.copy(
                                fontSize = EditorFontSize * EditorSymbolScale,
                                fontWeight = FontWeight.Bold,
                                baselineShift = BaselineShift(0f)
                            )
                        )
                        append(currencySymbol)
                        pop()
                    }
                }.toAnnotatedString()
            }
        }

    val annotatedCalculationResult =
        remember(calculationResult, currencyCode, symbolStyle, isSimpleSignEntry) {
            if (calculationResult == null || isSimpleSignEntry) return@remember null
            val supportedCurrency = SupportedCurrency.findByCode(currencyCode)
            val currencySymbol = supportedCurrency?.symbol ?: "$"
            val isSymbolAtEnd = supportedCurrency?.symbolPosition == SymbolPosition.END

            val isNegative = calculationResult.startsWith("-")
            val sign = if (isNegative) "-" else ""
            val absValue = if (isNegative) calculationResult.substring(1) else calculationResult

            AnnotatedString.Builder().apply {
                append("= ")
                append(sign)
                if (isSymbolAtEnd) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                    append(absValue)
                    pop()
                    pushStyle(
                        symbolStyle.copy(
                            fontSize = CalcResultMaxFontSize * EditorSymbolScale,
                            fontWeight = FontWeight.Bold,
                            baselineShift = BaselineShift(0f)
                        )
                    )
                    append(currencySymbol)
                    pop()
                } else {
                    pushStyle(
                        symbolStyle.copy(
                            fontSize = CalcResultMaxFontSize * EditorSymbolScale,
                            fontWeight = FontWeight.Bold,
                            baselineShift = BaselineShift(0f)
                        )
                    )
                    append(currencySymbol)
                    pop()
                    pushStyle(SpanStyle(fontWeight = FontWeight.Light))
                    append(absValue)
                    pop()
                }
            }.toAnnotatedString()
        }

    val baseTextStyle = MaterialTheme.typography.displayLargeCondensed.copy(
        fontWeight = FontWeight.W500,
        fontSize = EditorFontSize,
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val availableWidth = maxWidth - 32.dp
        val toolbarWidth = maxWidth - 48.dp
        val containerSizePx = remember(availableWidth, maxHeight, density) {
            with(density) {
                IntSize(
                    width = availableWidth.toPx().toInt(),
                    height = maxHeight.toPx().toInt()
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                AnimatedContent(
                    targetState = if (hasExpressionOperators && calculationResult != null && !isSimpleSignEntry) "result" else "input",
                    transitionSpec = {
                        (
                                fadeIn(animationSpec = tween(200)) + slideInHorizontally(
                                    animationSpec = tween(200)
                                ) { it / 4 }
                                ) togetherWith (
                                fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                                    animationSpec = tween(200)
                                ) { -it / 4 }
                                )
                    },
                    label = "EditorNumberTransition",
                    modifier = Modifier.fillMaxWidth()
                ) { state ->
                    if (state == "result" && hasExpressionOperators && calculationResult != null && !isSimpleSignEntry) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AutoResizeBasicTextField(
                                value = input,
                                annotatedValue = annotatedDisplayContent,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.wrapContentWidth(Alignment.End),
                                textStyle = baseTextStyle.copy(
                                    color = if (isWalletIncomeEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.End
                                ),
                                singleLine = true,
                                minFontSize = CalcInputMinFontSize,
                                maxFontSize = CalcInputMaxFontSize,
                                containerSize = containerSizePx
                            )
                            AutoResizeBasicTextField(
                                value = calculationResult ?: "",
                                annotatedValue = annotatedCalculationResult,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .wrapContentWidth(Alignment.End)
                                    .padding(top = 4.dp),
                                textStyle = baseTextStyle.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End
                                ),
                                singleLine = true,
                                minFontSize = CalcResultMinFontSize,
                                maxFontSize = CalcResultMaxFontSize,
                                containerSize = containerSizePx
                            )
                        }
                    } else {
                        AutoResizeBasicTextField(
                            value = input,
                            annotatedValue = annotatedDisplayContent,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.wrapContentWidth(Alignment.End),
                            textStyle = baseTextStyle.copy(
                                color = if (isWalletIncomeEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.End
                            ),
                            singleLine = true,
                            minFontSize = EditorMinFontSize,
                            maxFontSize = EditorFontSize,
                            containerSize = containerSizePx,
                            decorationBox = { innerTextField ->
                                Box { innerTextField() }
                            }
                        )
                    }
                }
            }

            if (categoryGridModeEnabled) {
                var isInlineNoteEditing by remember { mutableStateOf(false) }
                var isInlineCategoryEditing by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 26.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (extraNoteEnabled && !isInlineCategoryEditing) {
                        EditableNoteTag(
                            currentNote = currentNote,
                            onNoteUpdate = onNoteUpdate,
                            editorFocusController = editorFocusController,
                            extendWidth = toolbarWidth,
                            onlyIcon = false,
                            onEdit = { editing ->
                                isInlineNoteEditing = editing
                                onCategoryEditingChanged(editing || isInlineCategoryEditing)
                            },
                        )
                    }
                    if (!isInlineNoteEditing) {
                        if (extraNoteEnabled && !isInlineCategoryEditing) {
                            Spacer(Modifier.width(8.dp))
                        }
                        EditableCategoryTag(
                            currentComment = currentComment,
                            tags = effectiveTags,
                            onCommentUpdate = onCommentUpdate,
                            editorFocusController = editorFocusController,
                            modifier = Modifier.let { m ->
                                if (tutorialBoxState != null) m.markForTutorial(
                                    tutorialBoxState,
                                    index = 3
                                ) else m
                            },
                            extendWidth = toolbarWidth,
                            onlyIcon = false,
                            onEdit = { editing ->
                                isInlineCategoryEditing = editing
                                onCategoryEditingChanged(editing || isInlineNoteEditing)
                            },
                            onDeleteTag = onDeleteTag,
                            directCategoryPopupEnabled = directCategoryPopupEnabled,
                            categoryGridModeEnabled = categoryGridModeEnabled,
                            isCategoryGridVisible = isCategoryGridVisible,
                            isCalculation = isCalculation,
                            onShowCategoryGrid = onShowCategoryGrid,
                            onHideCategoryGrid = onHideCategoryGrid,
                            onDisableCalculationMode = onDisableCalculationMode,
                        )
                    }
                }
            } else {
                CategoryToolbar(
                    tags = effectiveTags,
                    currentComment = currentComment,
                    stage = EditStage.EDIT_SPENT,
                    onCommentUpdate = onCommentUpdate,
                    currentNote = currentNote,
                    onNoteUpdate = onNoteUpdate,
                    extraNoteEnabled = extraNoteEnabled,
                    onDeleteTag = onDeleteTag,
                    onEditingChanged = onCategoryEditingChanged,
                    editorFocusController = editorFocusController,
                    directCategoryPopupEnabled = directCategoryPopupEnabled,
                    categoryGridModeEnabled = categoryGridModeEnabled,
                    isCategoryGridVisible = isCategoryGridVisible,
                    isCalculation = isCalculation,
                    onShowCategoryGrid = onShowCategoryGrid,
                    onHideCategoryGrid = onHideCategoryGrid,
                    onDisableCalculationMode = onDisableCalculationMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 26.dp)
                        .let { m ->
                            if (tutorialBoxState != null) m.markForTutorial(
                                tutorialBoxState,
                                index = 3
                            ) else m
                        },
                )
            }
        }
    }
}

private data class ContextualFinancialTip(
    val id: Int,
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
    val icon: ImageVector,
    val isWarning: Boolean,
)

private var sessionTipDismissed: Boolean = false
private val sessionDismissedTipIds = mutableSetOf<Int>()

@Composable
private fun IdleContent(
    uiState: BudgetUiState,
    onCreateCategory: suspend (String) -> Boolean = { true },
    newCategoryTagEnabled: Boolean = false,
    onDismissTip: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cursorVisible = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(530.milliseconds)
            cursorVisible.value = !cursorVisible.value
        }
    }

    val budgetState = uiState.budgetState
    val dailyLimit = budgetState?.dailyBudget ?: BigDecimal.ZERO
    val spentToday = budgetState?.totalSpentToday ?: BigDecimal.ZERO
    val remainingToday = budgetState?.remainingToday ?: BigDecimal.ZERO
    val alertThreshold = uiState.budgetAlertThresholdPercent

    val percentSpent = remember(dailyLimit, spentToday) {
        if (dailyLimit > BigDecimal.ZERO) {
            (spentToday.toFloat() / dailyLimit.toFloat() * 100f).toInt()
        } else 0
    }

    val isAlertExceeded = dailyLimit > BigDecimal.ZERO && (spentToday > dailyLimit || remainingToday < BigDecimal.ZERO)
    val isAlertWarning = !isAlertExceeded && dailyLimit > BigDecimal.ZERO && percentSpent >= alertThreshold && percentSpent > 0

    var tipDismissedState by rememberSaveable { mutableStateOf(sessionTipDismissed) }
    var dismissedIdsState by rememberSaveable { mutableStateOf(sessionDismissedTipIds.toSet()) }
    val effectiveDismissed = remember(dismissedIdsState, uiState.dismissedFinancialTipIds, sessionDismissedTipIds) {
        dismissedIdsState + uiState.dismissedFinancialTipIds + sessionDismissedTipIds
    }

    val activeTip: ContextualFinancialTip? = remember(
        uiState.financialTipsEnabled,
        uiState.numpadInput,
        budgetState,
        uiState.creditOwed,
        uiState.transactions,
        uiState.hasUnresolvedRolloverSurplus,
        effectiveDismissed,
        tipDismissedState,
    ) {
        if (!uiState.financialTipsEnabled || tipDismissedState || sessionTipDismissed ||
            (uiState.numpadInput.isNotEmpty() && uiState.numpadInput != "0")
        ) return@remember null

        val totalBudget = uiState.budgetSettings?.totalBudget ?: BigDecimal.ZERO
        val totalSpent = budgetState?.totalSpentInPeriod ?: BigDecimal.ZERO
        val daysRemaining = budgetState?.daysRemaining ?: 0
        val periodTotalDays = budgetState?.periodTotalDays ?: 30
        val creditOwed = uiState.creditOwed ?: BigDecimal.ZERO
        val transactions = uiState.transactions
        val totalRemaining = totalBudget.subtract(totalSpent)
        val budgetSpentRatio = if (totalBudget > BigDecimal.ZERO) totalSpent.divide(totalBudget, 4, java.math.RoundingMode.HALF_UP).toFloat() else 0f
        val periodProgressRatio = if (periodTotalDays > 0) (periodTotalDays - daysRemaining).toFloat() / periodTotalDays else 0f

        // Trigger 1 (Violating Tip 1: "لا تنفق أكثر مما تكسب"):
        // Fired ONLY when the entire period budget is actually exhausted/exceeded:
        val isActualDeficit = totalBudget > BigDecimal.ZERO && (totalSpent > totalBudget || budgetState?.isOverBudget == true)
        if (isActualDeficit && 1 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 1,
                titleRes = R.string.financial_tip_1_title,
                bodyRes = R.string.financial_tip_1_body,
                icon = Icons.Rounded.Warning,
                isWarning = true,
            )
        }

        // Trigger 3 (Violating Tip 3: "تجنّب الديون الاستهلاكية"):
        // Fired when the user is accumulating consumer credit card debt (> 20% of budget or multiple credit charges)
        val creditTransactionsCount = transactions.count { it.isCredit }
        if (creditOwed > BigDecimal.ZERO && (creditTransactionsCount >= 2 || (totalBudget > BigDecimal.ZERO && creditOwed >= totalBudget.multiply(BigDecimal("0.20"))))) {
            if (3 !in effectiveDismissed) {
                return@remember ContextualFinancialTip(
                    id = 3,
                    titleRes = R.string.financial_tip_3_title,
                    bodyRes = R.string.financial_tip_3_body,
                    icon = Icons.Rounded.CreditScore,
                    isWarning = true,
                )
            }
        }

        // Trigger 6 (Violating Tip 6: "ارفع دخلك قبل أن ترفع مستوى معيشتك"):
        // Fired when there is a disproportionately large single purchase (> 50% of total budget and >= 250)
        val largestExpense = transactions.filter { it.amount > BigDecimal.ZERO && !it.isRecurrent }.maxOfOrNull { it.amount } ?: BigDecimal.ZERO
        if (totalBudget > BigDecimal.ZERO && largestExpense >= totalBudget.multiply(BigDecimal("0.50")) && largestExpense >= BigDecimal("250")) {
            if (6 !in effectiveDismissed) {
                return@remember ContextualFinancialTip(
                    id = 6,
                    titleRes = R.string.financial_tip_6_title,
                    bodyRes = R.string.financial_tip_6_body,
                    icon = Icons.Rounded.TrendingUp,
                    isWarning = true,
                )
            }
        }

        // Trigger 2 (Violating Tip 2: "احتفظ باحتياطي للطوارئ"):
        // Fired when more than half the period remains, but over 70% of the total budget has been consumed
        if (daysRemaining > 5 && periodProgressRatio < 0.5f && budgetSpentRatio >= 0.70f) {
            if (2 !in effectiveDismissed) {
                return@remember ContextualFinancialTip(
                    id = 2,
                    titleRes = R.string.financial_tip_2_title,
                    bodyRes = R.string.financial_tip_2_body,
                    icon = Icons.Rounded.Shield,
                    isWarning = true,
                )
            }
        }

        // Trigger 5 (Opportunity for Tip 5: "استثمر جزءاً من دخلك"):
        // Fired when user has surplus rollover savings from past period
        if (uiState.hasUnresolvedRolloverSurplus && 5 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 5,
                titleRes = R.string.financial_tip_5_title,
                bodyRes = R.string.financial_tip_5_body,
                icon = Icons.Rounded.Savings,
                isWarning = false,
            )
        }

        // Trigger 4 (Opportunity for Tip 4: "لا تعتمد على مصدر دخل واحد إلى الأبد"):
        // Fired ONLY at the very end of period (1..2 days left) when remaining budget is exhausted and no secondary income recorded.
        if (daysRemaining in 1..2 && totalRemaining <= BigDecimal.ZERO &&
            transactions.none { it.amount < BigDecimal.ZERO } &&
            4 !in effectiveDismissed
        ) {
            return@remember ContextualFinancialTip(
                id = 4,
                titleRes = R.string.financial_tip_4_title,
                bodyRes = R.string.financial_tip_4_body,
                icon = Icons.Rounded.AccountBalance,
                isWarning = false,
            )
        }

        // Trigger 7 (Violating Tip 7: "لا تطارد الثراء السريع"):
        // Fired when user logs an expense with high-risk speculation keywords
        val speculationKeywords = listOf("تداول", "عملات", "كريبتو", "بورصة", "crypto", "trading", "forex", "قمار", "رهان")
        val hasSpeculationExpense = transactions.any { tx ->
            val commentLower = tx.comment.lowercase()
            speculationKeywords.any { kw -> commentLower.contains(kw) }
        }
        if (hasSpeculationExpense && 7 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 7,
                titleRes = R.string.financial_tip_7_title,
                bodyRes = R.string.financial_tip_7_body,
                icon = Icons.Rounded.HourglassTop,
                isWarning = true,
            )
        }

        // Trigger 8 (Violating Tip 8: "فخ العروض والتخفيضات" - The Discount Trap):
        // Fired when user logs an expense with keywords like عرض, تخفيض, خصم, sale, offer, discount, أوفر
        val discountKeywords = listOf("عرض", "تخفيض", "خصم", "sale", "offer", "discount", "أوفر", "تخفيضات", "عروض")
        val hasDiscountExpense = transactions.any { tx ->
            val commentLower = tx.comment.lowercase()
            discountKeywords.any { kw -> commentLower.contains(kw) }
        }
        if (hasDiscountExpense && 8 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 8,
                titleRes = R.string.financial_tip_8_title,
                bodyRes = R.string.financial_tip_8_body,
                icon = Icons.Rounded.LocalOffer,
                isWarning = true,
            )
        }

        // Trigger 10 (Violating Tip 10: "تدقيق الاشتراكات الدورية" - Subscription Audit):
        // Fired when user has 4 or more recurring expenses registered
        val recurrentCount = transactions.count { it.isRecurrent }
        if (recurrentCount >= 4 && 10 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 10,
                titleRes = R.string.financial_tip_10_title,
                bodyRes = R.string.financial_tip_10_body,
                icon = Icons.Rounded.EventRepeat,
                isWarning = true,
            )
        }

        // Trigger 9 (Violating Tip 9: "قاعدة الـ 24 ساعة" - The 24-Hour Impulse Rule):
        // Fired when user logs an impulse purchase keyword
        val impulseKeywords = listOf("شراء مندفع", "فوري", "مفاجئ", "اندفاع", "عجبني", "impulse")
        val hasImpulseExpense = transactions.any { tx ->
            val commentLower = tx.comment.lowercase()
            impulseKeywords.any { kw -> commentLower.contains(kw) }
        }
        if (hasImpulseExpense && 9 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 9,
                titleRes = R.string.financial_tip_9_title,
                bodyRes = R.string.financial_tip_9_body,
                icon = Icons.Rounded.HourglassTop,
                isWarning = true,
            )
        }

        // Trigger 11 (Violating Tip 11: "الحذر من تضخم نمط الحياة" - Lifestyle Inflation):
        // Fired when user received extra income (wallet deposit) but total spend in period consumed more than 80% of it
        val extraIncomeSum = transactions.filter { it.amount < BigDecimal.ZERO }.sumOf { it.amount.abs() }
        if (extraIncomeSum > BigDecimal.ZERO && totalSpent >= extraIncomeSum.multiply(BigDecimal("0.80")) && 11 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 11,
                titleRes = R.string.financial_tip_11_title,
                bodyRes = R.string.financial_tip_11_body,
                icon = Icons.Rounded.TrendingUp,
                isWarning = true,
            )
        }

        // Trigger 12 (Tip 12: "تكلفة الفرصة البديلة" - Opportunity Cost):
        // Fired ONLY when total period remaining budget is genuinely tight (< 15% of total budget) and spent ratio >= 85%
        if (totalBudget > BigDecimal.ZERO && totalRemaining > BigDecimal.ZERO &&
            totalRemaining < totalBudget.multiply(BigDecimal("0.15")) &&
            budgetSpentRatio >= 0.85f &&
            spentToday > BigDecimal.ZERO &&
            12 !in effectiveDismissed
        ) {
            return@remember ContextualFinancialTip(
                id = 12,
                titleRes = R.string.financial_tip_12_title,
                bodyRes = R.string.financial_tip_12_body,
                icon = Icons.Rounded.Savings,
                isWarning = false,
            )
        }

        // Trigger 14 (Tip 14: "التوقف عن محاولة مواكبة الآخرين" - Comparison Trap):
        // Fired when user logs a luxury purchase keyword
        val luxuryKeywords = listOf("ماركة", "براند", "فاخر", "luxury", "brand", "كشخة")
        val hasLuxuryExpense = transactions.any { tx ->
            val commentLower = tx.comment.lowercase()
            luxuryKeywords.any { kw -> commentLower.contains(kw) }
        }
        if (hasLuxuryExpense && 14 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 14,
                titleRes = R.string.financial_tip_14_title,
                bodyRes = R.string.financial_tip_14_body,
                icon = Icons.Rounded.AccountBalanceWallet,
                isWarning = false,
            )
        }

        // Trigger 15 (Tip 15: "حساب التكلفة الكاملة قبل اتخاذ قرار كبير" - Total Cost of Ownership):
        // Fired when a single purchase is a major financial decision (>= 50% of total budget and >= 500)
        val largestSingleExpense = transactions.filter { it.amount > BigDecimal.ZERO && !it.isRecurrent }.maxOfOrNull { it.amount } ?: BigDecimal.ZERO
        if (totalBudget > BigDecimal.ZERO && largestSingleExpense >= totalBudget.multiply(BigDecimal("0.50")) && largestSingleExpense >= BigDecimal("500") && 15 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 15,
                titleRes = R.string.financial_tip_15_title,
                bodyRes = R.string.financial_tip_15_body,
                icon = Icons.Rounded.BarChart,
                isWarning = true,
            )
        }

        // Trigger 16 (Tip 16: "وضع حد واضح لما يمكنك تحمّله" - Clear Spending Ceiling):
        // Fired when remaining budget is critically low (spent >= 85%) and days remain (> 5)
        if (totalBudget > BigDecimal.ZERO && daysRemaining > 5 && budgetSpentRatio >= 0.85f && 16 !in effectiveDismissed) {
            return@remember ContextualFinancialTip(
                id = 16,
                titleRes = R.string.financial_tip_16_title,
                bodyRes = R.string.financial_tip_16_body,
                icon = Icons.Rounded.Shield,
                isWarning = true,
            )
        }

        null
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val tagWidth = maxWidth - 48.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isAlertExceeded || isAlertWarning) {
                    val alertColor = if (isAlertExceeded) Color(0xFFE57373) else Color(0xFFFFB74D)
                    val alertBgColor = alertColor.copy(alpha = 0.12f)
                    val alertText = if (isAlertExceeded) {
                        stringResource(R.string.budget_alert_exceeded)
                    } else {
                        stringResource(R.string.budget_alert_warning, percentSpent)
                    }
                    val alertIcon = if (isAlertExceeded) Icons.Rounded.Warning else Icons.Rounded.NotificationsActive

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = alertBgColor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = alertIcon,
                                contentDescription = null,
                                tint = alertColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = alertText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (activeTip != null) {
                val tipColor = if (activeTip.isWarning) Color(0xFFE57373) else MaterialTheme.colorScheme.primary
                val tipBgColor = if (activeTip.isWarning) {
                    tipColor.copy(alpha = 0.10f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = tipBgColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = activeTip.icon,
                                    contentDescription = null,
                                    tint = tipColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.financial_tip_title),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tipColor,
                                )
                            }
                            IconButton(
                                onClick = {
                                    onDismissTip(activeTip.id)
                                    dismissedIdsState = dismissedIdsState + activeTip.id
                                    sessionDismissedTipIds.add(activeTip.id)
                                    tipDismissedState = true
                                    sessionTipDismissed = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(activeTip.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(activeTip.bodyRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    val dashWidth = 4.dp
                    val dashHeight = 48.dp
                    Box(
                        modifier = Modifier
                            .size(width = dashWidth, height = dashHeight)
                            .graphicsLayer {
                                alpha = if (cursorVisible.value) 1f else 0f
                            }
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                shape = CircleShape
                            )
                    )
                }

                if (newCategoryTagEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 26.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NewCategoryTag(
                            onCreateCategory = onCreateCategory,
                            extendWidth = tagWidth,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(26.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun EditorPreview_Idle() {
    WafeerTheme {
        Editor(
            uiState = BudgetUiState(
                budgetSettings = BudgetSettings(
                    totalBudget = BigDecimal("500.00"),
                    period = BudgetPeriod.DAILY,
                    startDate = LocalDate.now(),
                    currencyCode = "USD"
                ),
                budgetState = BudgetState(
                    remainingToday = BigDecimal("110.00"),
                    totalSpentToday = BigDecimal("12.50"),
                    dailyBudget = BigDecimal("122.50"),
                    daysRemaining = 15,
                    progress = 0.1f,
                    isOverBudget = false,
                    totalBudget = BigDecimal("500.00"),
                    totalSpentInPeriod = BigDecimal("12.50")
                ),
                transactions = emptyList(),
                numpadInput = "",
                isNumpadValid = false
            ),
            animState = AnimState.IDLE,
            onFocus = {},
            onOpenHistory = {},
            onOpenSettings = {},
            onCommentClick = {},
            onCommentUpdate = {},
            onDeleteTag = {},
            onRecurrentToggle = {},
            onCreditToggle = {},
            onDismissRecurrentDialog = {},
            onDismissCreditCutoffDialog = {},
            onRecurrentExpenseConfirm = { _, _, _, _ -> },
            onCreditCutoffConfirm = {}
        )
    }
}

@PreviewLightDark
@Composable
fun EditorPreview_Editing() {
    WafeerTheme {
        Editor(
            uiState = BudgetUiState(
                budgetSettings = BudgetSettings(
                    totalBudget = BigDecimal("500.00"),
                    period = BudgetPeriod.DAILY,
                    startDate = LocalDate.now(),
                    currencyCode = "USD"
                ),
                budgetState = BudgetState(
                    remainingToday = BigDecimal("110.00"),
                    totalSpentToday = BigDecimal("12.50"),
                    dailyBudget = BigDecimal("122.50"),
                    daysRemaining = 15,
                    progress = 0.1f,
                    isOverBudget = false,
                    totalBudget = BigDecimal("500.00"),
                    totalSpentInPeriod = BigDecimal("12.50")
                ),
                transactions = emptyList(),
                numpadInput = "250",
                isNumpadValid = true,
                numpadDraftAmount = BigDecimal("250")
            ),
            animState = AnimState.EDITING,
            onFocus = {},
            onOpenHistory = {},
            onOpenSettings = {},
            onCommentClick = {},
            onCommentUpdate = {},
            onDeleteTag = {},
            onRecurrentToggle = {},
            onCreditToggle = {},
            onDismissRecurrentDialog = {},
            onDismissCreditCutoffDialog = {},
            onRecurrentExpenseConfirm = { _, _, _, _ -> },
            onCreditCutoffConfirm = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun EditorPreview_Editing_WithCredit() {
    WafeerTheme {
        Editor(
            uiState = BudgetUiState(
                budgetSettings = BudgetSettings(
                    totalBudget = BigDecimal("500.00"),
                    period = BudgetPeriod.DAILY,
                    startDate = LocalDate.now(),
                    currencyCode = "QAR"
                ),
                budgetState = BudgetState(
                    remainingToday = BigDecimal("110.00"),
                    totalSpentToday = BigDecimal("12.50"),
                    dailyBudget = BigDecimal("122.50"),
                    daysRemaining = 15,
                    progress = 0.1f,
                    isOverBudget = false,
                    totalBudget = BigDecimal("500.00"),
                    totalSpentInPeriod = BigDecimal("12.50")
                ),
                transactions = emptyList(),
                numpadInput = "250",
                isNumpadValid = true,
                numpadDraftAmount = BigDecimal("250"),
                isCreditEnabled = true,
                isRecurrentEnabled = true
            ),
            animState = AnimState.EDITING,
            showCreditQuickToggleFeature = true,
            onFocus = {},
            onOpenHistory = {},
            onOpenSettings = {},
            onCommentClick = {},
            onCommentUpdate = {},
            onDeleteTag = {},
            onRecurrentToggle = {},
            onCreditToggle = {},
            onDismissRecurrentDialog = {},
            onDismissCreditCutoffDialog = {},
            onRecurrentExpenseConfirm = { _, _, _, _ -> },
            onCreditCutoffConfirm = {}
        )
    }
}
