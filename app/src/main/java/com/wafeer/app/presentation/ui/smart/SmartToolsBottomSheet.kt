package com.wafeer.app.presentation.ui.smart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.ShoppingBag
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wafeer.app.domain.model.smart.AffordabilityVerdictLevel
import com.wafeer.app.domain.model.smart.MicroGoal
import com.wafeer.app.domain.model.smart.WishlistItem
import com.wafeer.app.domain.model.smart.WishlistStatus
import com.wafeer.app.presentation.ui.theme.ThmanyahSerifDisplayFamily
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
                        text = "أدوات التوفير الذكية",
                        fontFamily = ThmanyahSerifDisplayFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "أدوات عملية لقرارات مالية أهدى وأذكى",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "إغلاق",
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
                    title = "أقدر أشتريها؟",
                    icon = Icons.Rounded.Calculate,
                    onClick = { onTabSelected(SmartToolTab.CALCULATOR) },
                )
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.MICRO_GOALS,
                    title = "برطمان الأهداف",
                    icon = Icons.Rounded.Savings,
                    onClick = { onTabSelected(SmartToolTab.MICRO_GOALS) },
                )
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.WISHLIST_48H,
                    title = "فلتر الـ 48 ساعة",
                    icon = Icons.Rounded.Timer,
                    onClick = { onTabSelected(SmartToolTab.WISHLIST_48H) },
                )
                TabItem(
                    selected = uiState.selectedTab == SmartToolTab.FUN_FUND,
                    title = "فلوس الروقان",
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
            shape = RoundedCornerShape(12.dp),
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
// 1. CALCULATOR TAB ("أقدر أشتريها ولا لأ؟")
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
            text = "قبل ما تدفع، اعرف تأثيرها فوراً على باقي أيامك:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.calculationInputPrice,
            onValueChange = onPriceInputChanged,
            label = { Text("سعر الحاجة ($currencyCode)") },
            placeholder = { Text("مثلاً: 350") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = itemTitle,
            onValueChange = { itemTitle = it },
            label = { Text("اسم الحاجة (اختياري)") },
            placeholder = { Text("مثلاً: شوز، قهوة مع أصحابي، كورس") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val result = uiState.calculationResult
        if (result != null) {
            val (badgeBg, badgeText, badgeColor, badgeIcon) = when (result.verdict) {
                AffordabilityVerdictLevel.SAFE -> Quadruple(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    "تقدر تشتريها وأنت مطمن",
                    MaterialTheme.colorScheme.primary,
                    Icons.Rounded.CheckCircle,
                )
                AffordabilityVerdictLevel.CAUTION -> Quadruple(
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    "هتحتاج تشد الحزام",
                    MaterialTheme.colorScheme.tertiary,
                    Icons.Rounded.Warning,
                )
                AffordabilityVerdictLevel.DANGEROUS -> Quadruple(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    "هتكسر ميزانيتك!",
                    MaterialTheme.colorScheme.error,
                    Icons.Rounded.Warning,
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                            label = "تكلفة بالأيام",
                            value = "${String.format(java.util.Locale.US, "%.1f", result.daysOfAllowanceCost)} يوم",
                        )
                        StatPill(
                            label = "مصروفك اليومي بعدها",
                            value = "${result.newDailyAllowanceAfter.toInt()} $currencyCode",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Recommendations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        val name = itemTitle.ifBlank { "حاجة بـ ${result.price} $currencyCode" }
                        onAddToWishlist(name, result.price)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("أجّلها 48 ساعة")
                }

                FilledTonalButton(
                    onClick = {
                        val name = itemTitle.ifBlank { "شراء ${result.price} $currencyCode" }
                        onAddToJar(name, result.price)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("وفّر لها في البرطمان")
                }
            }
        }
    }
}

// ==========================================
// 2. MICRO-GOAL JAR TAB ("برطمان الأهداف")
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
                text = "أهدافك الصغيرة المحققة بالتدريج",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("هدف جديد")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (goals.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.Savings,
                title = "لا توجد أهداف نشطة حالياً",
                description = "أنشئ برطمان توفير لشراء سماعة، حذاء، كورس، أو طلعة سفر، وحوّش فيها خطوة بخطوة!",
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                            text = if (goal.isCompleted) "تم اكتمال الهدف بنجاح!" else "محوّش ${goal.savedAmount.toInt()} من ${goal.targetAmount.toInt()} $currencyCode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "حذف",
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
                    text = "لو حوّشت ${dailyPace.toInt()} $currencyCode يومياً، هتوصل لهدفك في $daysNeeded يوم.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDepositClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إيداع في البرطمان")
                }
            }
        }
    }
}

// ==========================================
// 3. 48-HOUR WISHLIST TAB ("فلتر الـ 48 ساعة")
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
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
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
                        text = "فلوس وفّرتها بفضل التأني:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                text = "قائمة الشراء تحت التهدئة",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة رغبة")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (wishlist.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Rounded.Timer,
                title = "لا توجد رغبات شراء في الانتظار",
                description = "كل ما تجيلك رغبة تشتري حاجة مش ضرورية، حطها هنا 48 ساعة. لو لقيت نفسك لسه محتاجها اشتريها، ولو صرفت نظر هتفرح بالفلوس اللي وفّرتها!",
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (item.status) {
                WishlistStatus.SAVED_AND_DISMISSED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                WishlistStatus.PURCHASED -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                WishlistStatus.COOLING -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                        contentDescription = "حذف",
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
                                "انتهت فترة التهدئة! هل لسه محتاجها فعلاً؟"
                            } else {
                                "باقي ${item.remainingHours} ساعة و${item.remainingMinutes} دقيقة تفكير"
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
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {
                            Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("صرفت نظر ووفّرت!")
                        }

                        OutlinedButton(
                            onClick = onPurchased,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                        ) {
                            Text("اشتريتها")
                        }
                    }
                }
                WishlistStatus.SAVED_AND_DISMISSED -> {
                    Text(
                        text = "عاش! صرفت نظر ووفّرت ${item.price.toPlainString()} $currencyCode في جيبك.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                WishlistStatus.PURCHASED -> {
                    Text(
                        text = "تم الشراء بعد التفكير والتروي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. GUILT-FREE FUN FUND TAB ("فلوس الروقان")
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
            text = "فلسفة الروقان المحمي:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "عشان متتخنقش من التوفير، خصص نسبة ثابتة لمزاجك وخروجتك والقهوة، واصرفها وإنت رايق بدون أي تأنيب ضمير!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Big Buffer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
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
                    text = "مخصص لروقانك هذا الشهر:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )

                Text(
                    text = "${funAmount.toInt()} $currencyCode",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "اصرفها وإنت مرتاح، مش هتأثر على التزاماتك الأساسية.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "اختر نسبة فلوس الروقان من ميزانيتك:",
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
                    shape = RoundedCornerShape(12.dp),
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
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
                    text = "إنشاء برطمان هدف جديد",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم الهدف") },
                    placeholder = { Text("مثلاً: سماعة إيربودز، كورس فلاتر") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("المبلغ المطلوب ($currencyCode)") },
                    placeholder = { Text("مثلاً: 1200") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            val amount = target.toBigDecimalOrNull()
                            if (title.isNotBlank() && amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(title, amount)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("إنشاء البرطمان")
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
                    text = "إيداع في برطمان: ${goal.title}",
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
                            shape = RoundedCornerShape(12.dp),
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
                    label = { Text("أو مبلغ مخصص ($currencyCode)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            val amount = customAmount.toBigDecimalOrNull()
                            if (amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(amount)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("إيداع الآن")
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
                    text = "إضافة رغبة لقائمة الـ 48 ساعة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم الحاجة") },
                    placeholder = { Text("مثلاً: ساعة كاسيو، حذاء، جاكت") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("السعر التقريبي ($currencyCode)") },
                    placeholder = { Text("مثلاً: 600") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            val amount = price.toBigDecimalOrNull()
                            if (title.isNotBlank() && amount != null && amount > BigDecimal.ZERO) {
                                onConfirm(title, amount, 48)
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("بدء التهدئة (48h)")
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
