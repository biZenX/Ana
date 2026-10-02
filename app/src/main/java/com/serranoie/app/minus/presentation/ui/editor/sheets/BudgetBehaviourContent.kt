@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.serranoie.app.minus.presentation.ui.editor.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Switch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.serranoie.app.minus.R
import com.serranoie.app.minus.domain.model.BudgetSplitMode
import com.serranoie.app.minus.domain.model.RemainingBudgetStrategy
import com.serranoie.app.minus.presentation.ui.theme.MinusTheme
import com.serranoie.app.minus.presentation.ui.theme.bodyMediumCondensed
import com.serranoie.app.minus.presentation.ui.theme.bodySmallCondensed
import com.serranoie.app.minus.presentation.ui.theme.colorButton
import com.serranoie.app.minus.presentation.ui.theme.component.CustomPaddedListItem
import com.serranoie.app.minus.presentation.ui.theme.component.MorphCornerShape
import com.serranoie.app.minus.presentation.ui.theme.component.PaddedListGroup
import com.serranoie.app.minus.presentation.ui.theme.component.PaddedListItemPosition
import com.serranoie.app.minus.presentation.ui.theme.component.toShape
import com.serranoie.app.minus.presentation.util.Utils.confirmFeedback

@Composable
internal fun BudgetBehaviourContent(
    strategy: RemainingBudgetStrategy,
    splitMode: BudgetSplitMode,
    onStrategySelected: (RemainingBudgetStrategy) -> Unit,
    onSplitModeSelected: (BudgetSplitMode) -> Unit,
    applyLabel: String,
    onBack: () -> Unit,
    onApply: () -> Unit,
    allowanceDaysEnabled: Boolean = false,
    activeSpendingDays: Set<Int> = setOf(7, 1, 2, 3, 4),
    onAllowanceDaysToggled: (Boolean) -> Unit = {},
    onActiveSpendingDaysChanged: (Set<Int>) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val overscroll = rememberOverscrollEffect()
    val scrollGuard = remember(scrollState, overscroll) { SheetScrollGuard(scrollState, overscroll) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .nestedScroll(scrollGuard)
                .verticalScroll(scrollState, overscrollEffect = scrollGuard),
        ) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)) {
                Text(
                    text = stringResource(R.string.budget_behaviour_title),
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.budget_behaviour_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            PaddedListGroup(
                title = stringResource(R.string.budget_allowance_days_title),
                paddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            ) {
                AllowanceDaysCard(
                    enabled = allowanceDaysEnabled,
                    selectedDays = activeSpendingDays,
                    onToggle = onAllowanceDaysToggled,
                    onDaysChanged = onActiveSpendingDaysChanged,
                )
            }

            PaddedListGroup(
                title = stringResource(R.string.budget_behaviour_surplus_title),
                paddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            ) {
                SurplusStrategyCard(
                    strategy = strategy,
                    onStrategySelected = onStrategySelected,
                )
            }

            BehaviourOptionGroup(
                title = stringResource(R.string.split_mode_label),
                options = listOf(
                    BudgetSplitMode.DYNAMIC,
                    BudgetSplitMode.CARRY_OVER,
                    BudgetSplitMode.ASK_ME,
                    BudgetSplitMode.STATIC
                ),
                selected = splitMode,
                onSelect = onSplitModeSelected,
                optionTag = ::budgetSplitModeOptionTag,
                optionTitle = { option ->
                    stringResource(
                        when (option) {
                            BudgetSplitMode.STATIC -> R.string.split_mode_static
                            BudgetSplitMode.DYNAMIC -> R.string.split_mode_dynamic
                            BudgetSplitMode.CARRY_OVER -> R.string.split_mode_carry_over
                            BudgetSplitMode.ASK_ME -> R.string.split_mode_ask_me
                        }
                    )
                },
                optionDescription = { option ->
                    stringResource(
                        when (option) {
                            BudgetSplitMode.STATIC -> R.string.split_mode_static_desc
                            BudgetSplitMode.DYNAMIC -> R.string.split_mode_dynamic_desc
                            BudgetSplitMode.CARRY_OVER -> R.string.split_mode_carry_over_desc
                            BudgetSplitMode.ASK_ME -> R.string.split_mode_ask_me_desc
                        }
                    )
                },
                paddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            val backLabel = stringResource(R.string.back)
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above,
                ),
                state = rememberTooltipState(),
                tooltip = {
                    PlainTooltip(
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Assertive
                            paneTitle = backLabel
                        },
                    ) {
                        Text(backLabel)
                    }
                },
            ) {
                FilledTonalButton(
                    onClick = onBack,
                    shapes = ButtonDefaults.shapes(
                        shape = ButtonGroupDefaults.connectedLeadingButtonShape,
                        pressedShape = ButtonGroupDefaults.connectedLeadingButtonPressShape,
                    ),
                    contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                    modifier = Modifier.heightIn(min = ButtonDefaults.MediumContainerHeight),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = backLabel,
                        modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MediumContainerHeight)),
                    )
                }
            }
            Button(
                onClick = onApply,
                shapes = ButtonDefaults.shapes(
                    shape = ButtonGroupDefaults.connectedTrailingButtonShape,
                    pressedShape = ButtonGroupDefaults.connectedTrailingButtonPressShape,
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = ButtonDefaults.MediumContainerHeight)
                    .testTag(BUDGET_PERIOD_APPLY_BUTTON_TAG),
            ) {
                Text(applyLabel, style = MaterialTheme.typography.labelMediumEmphasized)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SurplusStrategyCard(
    strategy: RemainingBudgetStrategy,
    onStrategySelected: (RemainingBudgetStrategy) -> Unit,
) {
    val view = LocalView.current
    val previewFadeSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val strategies = listOf(
        RemainingBudgetStrategy.ASK_ALWAYS,
        RemainingBudgetStrategy.SPLIT_EQUALLY,
        RemainingBudgetStrategy.ADD_TO_FIRST_DAY,
    )
    Surface(
        shape = MaterialTheme.shapes.large,
        color = colorButton,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                strategies.forEachIndexed { index, option ->
                    ToggleButton(
                        checked = option == strategy,
                        onCheckedChange = {
                            view.confirmFeedback()
                            onStrategySelected(option)
                        },
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            strategies.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .semantics {
                                role = Role.RadioButton
                                selected = option == strategy
                            }
                            .testTag(budgetStrategyOptionTag(option)),
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ) {
                        Text(
                            text = stringResource(
                                when (option) {
                                    RemainingBudgetStrategy.ASK_ALWAYS -> R.string.budget_behaviour_surplus_ask
                                    RemainingBudgetStrategy.SPLIT_EQUALLY -> R.string.budget_behaviour_surplus_spread
                                    RemainingBudgetStrategy.ADD_TO_FIRST_DAY -> R.string.budget_behaviour_surplus_first_day
                                }
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                AnimatedContent(
                    targetState = strategy,
                    modifier = Modifier.weight(1f),
                    transitionSpec = { fadeIn(previewFadeSpec) togetherWith fadeOut(previewFadeSpec) },
                    label = "surplusPreview",
                ) { shown ->
                    Text(
                        text = stringResource(
                            when (shown) {
                                RemainingBudgetStrategy.ASK_ALWAYS -> R.string.budget_behaviour_surplus_preview_ask
                                RemainingBudgetStrategy.SPLIT_EQUALLY -> R.string.budget_behaviour_surplus_preview_spread
                                RemainingBudgetStrategy.ADD_TO_FIRST_DAY -> R.string.budget_behaviour_surplus_preview_first_day
                            }
                        ),
                        style = MaterialTheme.typography.bodyMediumCondensed,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> BehaviourOptionGroup(
    title: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    optionTag: (T) -> String,
    optionTitle: @Composable (T) -> String,
    optionDescription: @Composable (T) -> String,
    paddingValues: PaddingValues = PaddingValues(16.dp),
) {
    val view = LocalView.current
    PaddedListGroup(
        title = title,
        modifier = Modifier.selectableGroup(),
        paddingValues = paddingValues,
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selected
            val position = when {
                options.size == 1 -> PaddedListItemPosition.Single
                index == 0 -> PaddedListItemPosition.First
                index == options.lastIndex -> PaddedListItemPosition.Last
                else -> PaddedListItemPosition.Middle
            }
            val selection by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                label = "optionShape",
            )
            CustomPaddedListItem(
                onClick = {
                    view.confirmFeedback()
                    onSelect(option)
                },
                position = position,
                customShape = MorphCornerShape(position.toShape(), MaterialTheme.shapes.extraLarge, selection),
                modifier = Modifier
                    .testTag(optionTag(option))
                    .semantics(mergeDescendants = true) {
                        this.selected = isSelected
                        role = Role.RadioButton
                    },
                background = if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                ) {
                    Text(
                        text = optionTitle(option),
                        style = MaterialTheme.typography.bodyLargeEmphasized
                    )
                    Text(
                        text = optionDescription(option),
                        style = MaterialTheme.typography.bodySmallCondensed,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}

private class SheetScrollGuard(
    private val scrollState: ScrollState,
    private val overscroll: OverscrollEffect?,
) : NestedScrollConnection, OverscrollEffect {
    private var blockedScroll = Offset.Zero
    private var blockedFling = Velocity.Zero
    private val canScroll: Boolean
        get() = scrollState.canScrollForward || scrollState.canScrollBackward

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        blockedScroll = if (canScroll) available else Offset.Zero
        return blockedScroll
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        blockedFling = if (canScroll) available else Velocity.Zero
        return blockedFling
    }

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        val effect = overscroll ?: return performScroll(delta)
        return effect.applyToScroll(delta, source) { available ->
            blockedScroll = Offset.Zero
            performScroll(available) - blockedScroll
        }
    }

    override suspend fun applyToFling(velocity: Velocity, performFling: suspend (Velocity) -> Velocity) {
        if (overscroll == null) {
            performFling(velocity)
            return
        }
        overscroll.applyToFling(velocity) { available ->
            blockedFling = Velocity.Zero
            performFling(available) - blockedFling
        }
    }

    override val isInProgress: Boolean
        get() = overscroll?.isInProgress == true

    override val node: DelegatableNode = overscroll?.node ?: object : Modifier.Node() {}
}

@Composable
private fun AllowanceDaysCard(
    enabled: Boolean,
    selectedDays: Set<Int>,
    onToggle: (Boolean) -> Unit,
    onDaysChanged: (Set<Int>) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.budget_allowance_days_switch_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.budget_allowance_days_switch_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                )
            }

            AnimatedVisibility(visible = enabled) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val isSunThu = selectedDays == setOf(7, 1, 2, 3, 4)
                        val isMonFri = selectedDays == setOf(1, 2, 3, 4, 5)

                        FilterChip(
                            selected = isSunThu,
                            onClick = { onDaysChanged(setOf(7, 1, 2, 3, 4)) },
                            label = {
                                Text(
                                    stringResource(R.string.budget_allowance_preset_sun_thu),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                        )
                        FilterChip(
                            selected = isMonFri,
                            onClick = { onDaysChanged(setOf(1, 2, 3, 4, 5)) },
                            label = {
                                Text(
                                    stringResource(R.string.budget_allowance_preset_mon_fri),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    val daysList = listOf(
                        7 to R.string.day_sunday_short,
                        1 to R.string.day_monday_short,
                        2 to R.string.day_tuesday_short,
                        3 to R.string.day_wednesday_short,
                        4 to R.string.day_thursday_short,
                        5 to R.string.day_friday_short,
                        6 to R.string.day_saturday_short,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        daysList.forEach { (dayInt, labelRes) ->
                            val isDaySelected = dayInt in selectedDays
                            Surface(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        val newSet = if (isDaySelected) {
                                            if (selectedDays.size > 1) selectedDays - dayInt else selectedDays
                                        } else {
                                            selectedDays + dayInt
                                        }
                                        onDaysChanged(newSet)
                                    },
                                shape = CircleShape,
                                color = if (isDaySelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = stringResource(labelRes),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDaySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.budget_allowance_days_summary, selectedDays.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BudgetBehaviourContentPreview() {
    MinusTheme {
        Surface {
            BudgetBehaviourContent(
                strategy = RemainingBudgetStrategy.SPLIT_EQUALLY,
                splitMode = BudgetSplitMode.DYNAMIC,
                onStrategySelected = {},
                onSplitModeSelected = {},
                applyLabel = "Apply",
                onBack = {},
                onApply = {},
            )
        }
    }
}

