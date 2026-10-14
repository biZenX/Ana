package com.wafeer.app.presentation.ui.smart

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wafeer.app.R
import com.wafeer.app.domain.model.smart.AffordabilityVerdictLevel
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartToolsBottomSheet(
    uiState: SmartToolsUiState,
    currentDailyAllowance: BigDecimal,
    remainingBudget: BigDecimal,
    remainingDays: Int,
    currencyCode: String,
    onTabSelected: (SmartToolTab) -> Unit,
    onPriceInputChanged: (String) -> Unit,
    onAddGoal: (String, BigDecimal) -> Unit,
    onDepositToGoal: (MicroGoal, BigDecimal) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onAddWishlistItem: (String, BigDecimal, Int) -> Unit,
    onMarkWishlistSaved: (WishlistItem) -> Unit,
    onMarkWishlistPurchased: (WishlistItem) -> Unit,
    onDeleteWishlistItem: (Long) -> Unit,
    onSetFunFundRatio: (Float) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.smart_tools_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.smart_tools_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.smart_tools_close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                edgePadding = 16.dp,
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.CALCULATOR,
                    title = stringResource(R.string.smart_tab_affordability),
                    icon = Icons.Rounded.Calculate,
                    onClick = { onTabSelected(SmartToolTab.CALCULATOR) },
                )
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.MICRO_GOALS,
                    title = stringResource(R.string.smart_tab_micro_goals),
                    icon = Icons.Rounded.Savings,
                    onClick = { onTabSelected(SmartToolTab.MICRO_GOALS) },
                )
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.WISHLIST_48H,
                    title = stringResource(R.string.smart_tab_wishlist_48h),
                    icon = Icons.Rounded.Timer,
                    onClick = { onTabSelected(SmartToolTab.WISHLIST_48H) },
                )
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.FUN_FUND,
                    title = stringResource(R.string.smart_tab_fun_fund),
                    icon = Icons.Rounded.LocalCafe,
                    onClick = { onTabSelected(SmartToolTab.FUN_FUND) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (uiState.selectedTab) {
                SmartToolTab.CALCULATOR -> {
                    AffordabilityCalculatorTab(
                        uiState = uiState,
                        currentDailyAllowance = currentDailyAllowance,
                        remainingBudget = remainingBudget,
                        remainingDays = remainingDays,
                        currencyCode = currencyCode,
                        onPriceInputChanged = onPriceInputChanged,
                        onAddToWishlist = { title, price ->
                            onAddWishlistItem(title, price, 48)
                            onTabSelected(SmartToolTab.WISHLIST_48H)
                        },
                        onAddToJar = { title, target ->
                            onAddGoal(title, target)
                            onTabSelected(SmartToolTab.MICRO_GOALS)
                        },
                    )
                }
                SmartToolTab.MICRO_GOALS -> {
                    MicroGoalsTab(
                        goals = uiState.goals,
                        currencyCode = currencyCode,
                        dailyAllowance = currentDailyAllowance,
                        onAddGoal = onAddGoal,
                        onDepositToGoal = onDepositToGoal,
                        onDeleteGoal = onDeleteGoal,
                    )
                }
                SmartToolTab.WISHLIST_48H -> {
                    Wishlist48HTab(
                        wishlist = uiState.wishlist,
                        totalSavings = uiState.totalWishlistSavings,
                        currencyCode = currencyCode,
                        onAddItem = { title, price, hours -> onAddWishlistItem(title, price, hours) },
                        onMarkSaved = onMarkWishlistSaved,
                        onMarkPurchased = onMarkWishlistPurchased,
                        onDeleteItem = onDeleteWishlistItem,
                    )
                }
                SmartToolTab.FUN_FUND -> {
                    FunFundTab(
                        ratioPercent = uiState.funFundRatio,
                        remainingBudget = remainingBudget,
                        currencyCode = currencyCode,
                        onSetRatio = onSetFunFundRatio,
                    )
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    selected: Boolean,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "tabBg",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tabText",
    )

    Tab(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 4.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bgColor,
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor,
                )
            }
        }
    }
}

// ==========================================
// 1. CALCULATOR TAB
// ==========================================
@Composable
private fun AffordabilityCalculatorTab(
    uiState: SmartToolsUiState,
    currentDailyAllowance: BigDecimal,
    remainingBudget: BigDecimal,
    remainingDays: Int,
    currencyCode: String,
    onPriceInputChanged: (String) -> Unit,
    onAddToWishlist: (String, BigDecimal) -> Unit,
    onAddToJar: (String, BigDecimal) -> Unit,
) {
    var itemTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.smart_calc_header),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.calculationInputPrice,
            onValueChange = onPriceInputChanged,
            label = { Text(stringResource(R.string.smart_calc_price_label, currencyCode)) },
            placeholder = { Text(stringResource(R.string.smart_calc_price_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = itemTitle,
            onValueChange = { itemTitle = it },
            label = { Text(stringResource(R.string.smart_calc_name_label)) },
            placeholder = { Text(stringResource(R.string.smart_calc_name_placeholder)) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val result = uiState.calculationResult
        if (result != null) {
            val (badgeBg, badgeText, badgeColor, badgeIcon) = when (result.verdict) {
                AffordabilityVerdictLevel.SAFE -> Quadruple(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    stringResource(R.string.smart_calc_verdict_safe),
                    MaterialTheme.colorScheme.primary,
                    Icons.Rounded.CheckCircle,
                )
                AffordabilityVerdictLevel.CAUTION -> Quadruple(
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    stringResource(R.string.smart_calc_verdict_caution),
                    MaterialTheme.colorScheme.tertiary,
                    Icons.Rounded.Warning,
                )
                AffordabilityVerdictLevel.DANGEROUS -> Quadruple(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    stringResource(R.string.smart_calc_verdict_dangerous),
                    MaterialTheme.colorScheme.error,
                    Icons.Rounded.Warning,
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = badgeBg),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = badgeColor,
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = result.recommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        StatPill(
                            label = stringResource(R.string.smart_calc_stat_days_cost),
                            value = String.format(java.util.Locale.getDefault(), "%.1f", result.daysOfAllowanceCost),
                        )
                        StatPill(
                            label = stringResource(R.string.smart_calc_stat_daily_after),
                            value = "${result.newDailyAllowanceAfter.toInt()} $currencyCode",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Recommendations
            val defaultWishlistName = stringResource(R.string.smart_calc_default_wishlist_item, result.price.toPlainString(), currencyCode)
            val defaultJarName = stringResource(R.string.smart_calc_default_jar_item, result.price.toPlainString(), currencyCode)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        val name = itemTitle.ifBlank { defaultWishlistName }
                        onAddToWishlist(name, result.price)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.smart_calc_btn_wishlist))
                }

                FilledTonalButton(
                    onClick = {
                        val name = itemTitle.ifBlank { defaultJarName }
                        onAddToJar(name, result.price)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.smart_calc_btn_jar))
                }
            }
        }
    }
}

// ==========================================
// 2. MICRO-GOAL JAR TAB
// ==========================================
@Composable
private fun MicroGoalsTab(
    goals: List<MicroGoal>,
    currencyCode: String,
    dailyAllowance: BigDecimal,
    onAddGoal: (String, BigDecimal) -> Unit,
    onDepositToGoal: (MicroGoal, BigDecimal) -> Unit,
    onDeleteGoal: (Long) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedGoalForDeposit by remember { mutableStateOf<MicroGoal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.smart_goals_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.smart_goals_btn_new))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (goals.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.Savings,
                title = stringResource(R.string.smart_goals_empty_title),
                description = stringResource(R.string.smart_goals_empty_desc),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(goals, key = { it.id }) { goal ->
                    MicroGoalCard(
                        goal = goal,
                        currencyCode = currencyCode,
                        dailyAllowance = dailyAllowance,
                        onDepositClick = { selectedGoalForDeposit = goal },
                        onDeleteClick = { onDeleteGoal(goal.id) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            currencyCode = currencyCode,
            onConfirm = { title, target ->
                onAddGoal(title, target)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }

    selectedGoalForDeposit?.let { goal ->
        DepositDialog(
            goal = goal,
            currencyCode = currencyCode,
            onConfirm = { amount ->
                onDepositToGoal(goal, amount)
                selectedGoalForDeposit = null
            },
            onDismiss = { selectedGoalForDeposit = null },
        )
    }
}

@Composable
private fun MicroGoalCard(
    goal: MicroGoal,
    currencyCode: String,
    dailyAllowance: BigDecimal,
    onDepositClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (goal.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (goal.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Savings,
                            contentDescription = null,
                            tint = if (goal.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    Column {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (goal.isCompleted) {
                                stringResource(R.string.smart_goal_completed_badge)
                            } else {
                                stringResource(
                                    R.string.smart_goal_progress_text,
                                    goal.savedAmount.toInt().toString(),
                                    goal.targetAmount.toInt().toString(),
                                    currencyCode,
                                )
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.smart_goal_delete_desc),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { goal.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pacer / Advice
            if (!goal.isCompleted) {
                val dailyPace = if (dailyAllowance > BigDecimal.ZERO) dailyAllowance * BigDecimal("0.2") else BigDecimal("25")
                val daysNeeded = goal.daysToTarget(dailyPace)
                Text(
                    text = stringResource(R.string.smart_goal_pace_tip, dailyPace.toInt().toString(), currencyCode, daysNeeded),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDepositClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.smart_goal_btn_deposit))
                }
            }
        }
    }
}

// ==========================================
// 3. 48-HOUR WISHLIST TAB
// ==========================================
@Composable
private fun Wishlist48HTab(
    wishlist: List<WishlistItem>,
    totalSavings: BigDecimal,
    currencyCode: String,
    onAddItem: (String, BigDecimal, Int) -> Unit,
    onMarkSaved: (WishlistItem) -> Unit,
    onMarkPurchased: (WishlistItem) -> Unit,
    onDeleteItem: (Long) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        // Savings Trophy Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.smart_wishlist_savings_header),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${totalSavings.toPlainString()} $currencyCode",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.smart_wishlist_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.smart_wishlist_btn_add))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (wishlist.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.Timer,
                title = stringResource(R.string.smart_wishlist_empty_title),
                description = stringResource(R.string.smart_wishlist_empty_desc),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(wishlist, key = { it.id }) { item ->
                    WishlistCard(
                        item = item,
                        currencyCode = currencyCode,
                        onSaved = { onMarkSaved(item) },
                        onPurchased = { onMarkPurchased(item) },
                        onDelete = { onDeleteItem(item.id) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddWishlistDialog(
            currencyCode = currencyCode,
            onConfirm = { title, price, hours ->
                onAddItem(title, price, hours)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun WishlistCard(
    item: WishlistItem,
    currencyCode: String,
    onSaved: () -> Unit,
    onPurchased: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (item.status) {
                WishlistStatus.SAVED_AND_DISMISSED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                WishlistStatus.PURCHASED -> MaterialTheme.colorScheme.surfaceContainerLow
                WishlistStatus.COOLING -> MaterialTheme.colorScheme.surfaceContainer
            },
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${item.price.toPlainString()} $currencyCode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.smart_goal_delete_desc),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (item.status) {
                WishlistStatus.COOLING -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HourglassTop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = if (item.isCoolingFinished) {
                                stringResource(R.string.smart_wishlist_ready_prompt)
                            } else {
                                stringResource(R.string.smart_wishlist_remaining_prompt, item.remainingHours, item.remainingMinutes)
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = onSaved,
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {
                            Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.smart_wishlist_btn_saved))
                        }

                        OutlinedButton(
                            onClick = onPurchased,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {
                            Text(stringResource(R.string.smart_wishlist_btn_bought))
                        }
                    }
                }
                WishlistStatus.SAVED_AND_DISMISSED -> {
                    Text(
                        text = stringResource(R.string.smart_wishlist_status_saved, item.price.toPlainString(), currencyCode),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                WishlistStatus.PURCHASED -> {
                    Text(
                        text = stringResource(R.string.smart_wishlist_status_purchased),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. GUILT-FREE FUN FUND TAB
// ==========================================
@Composable
private fun FunFundTab(
    ratioPercent: Float,
    remainingBudget: BigDecimal,
    currencyCode: String,
    onSetRatio: (Float) -> Unit,
) {
    val funAmount = (remainingBudget * BigDecimal((ratioPercent / 100f).toDouble())).coerceAtLeast(BigDecimal.ZERO)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.smart_fun_fund_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.smart_fun_fund_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Big Buffer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalCafe,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.smart_fun_fund_allocated_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "${funAmount.toInt()} $currencyCode",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.smart_fun_fund_allocated_tip),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.smart_fun_fund_slider_label),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val options = listOf(10f, 15f, 20f, 25f)
            for (opt in options) {
                val isSelected = opt == ratioPercent
                OutlinedButton(
                    onClick = { onSetRatio(opt) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = if (isSelected) {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    },
                    border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    Text(
                        text = "${opt.toInt()}%",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

// ==========================================
// SHARED DIALOGS & COMPONENTS
// ==========================================
@Composable
private fun StatPill(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun EmptyStateCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AddGoalDialog(
    currencyCode: String,
    onConfirm: (String, BigDecimal) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_create_goal_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.smart_dialog_goal_name)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_goal_name_hint)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text(stringResource(R.string.smart_dialog_goal_target, currencyCode)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_goal_target_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val amount = target.toBigDecimalOrNull()
                            if (title.isNotBlank() && amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(title, amount)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_create))
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositDialog(
    goal: MicroGoal,
    currencyCode: String,
    onConfirm: (BigDecimal) -> Unit,
    onDismiss: () -> Unit,
) {
    var customAmount by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_deposit_title, goal.title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val presets = listOf(20, 50, 100)
                    for (preset in presets) {
                        FilledTonalButton(
                            onClick = { onConfirm(BigDecimal(preset)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {
                            Text("+$preset")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text(stringResource(R.string.smart_dialog_custom_amount, currencyCode)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val amount = customAmount.toBigDecimalOrNull()
                            if (amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(amount)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_deposit_now))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWishlistDialog(
    currencyCode: String,
    onConfirm: (String, BigDecimal, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.smart_dialog_add_wishlist_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.smart_dialog_item_name)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_item_name_hint)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.smart_dialog_approx_price, currencyCode)) },
                    placeholder = { Text(stringResource(R.string.smart_dialog_approx_price_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                        Text(stringResource(R.string.smart_dialog_cancel))
                    }

                    Button(
                        onClick = {
                            val amount = price.toBigDecimalOrNull()
                            if (title.isNotBlank() && amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(title, amount, 48)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(stringResource(R.string.smart_dialog_start_cooldown))
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
