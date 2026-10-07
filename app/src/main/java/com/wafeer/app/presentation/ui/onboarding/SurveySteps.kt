package com.wafeer.app.presentation.ui.onboarding

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Egg
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafeer.app.R
import com.wafeer.app.presentation.LocalWindowInsets
import com.wafeer.app.presentation.ui.theme.bodyMediumCondensed
import com.wafeer.app.presentation.ui.theme.component.DescriptionButton
import com.wafeer.app.presentation.ui.theme.titleMediumCondensed

data class VarietyCategoryItem(
    val id: String,
    val titleRes: Int,
    val defaultRoles: Set<SurveyRole>,
    val icon: ImageVector,
)

val EgyptianHouseholdVarieties = listOf(
    VarietyCategoryItem(
        id = "dairy",
        titleRes = R.string.survey_category_dairy,
        defaultRoles = setOf(SurveyRole.HOMEMAKER, SurveyRole.HEAD_OF_HOUSEHOLD, SurveyRole.INDIVIDUAL),
        icon = Icons.Rounded.Egg,
    ),
    VarietyCategoryItem(
        id = "meat",
        titleRes = R.string.survey_category_meat,
        defaultRoles = setOf(SurveyRole.HEAD_OF_HOUSEHOLD, SurveyRole.HOMEMAKER),
        icon = Icons.Rounded.Restaurant,
    ),
    VarietyCategoryItem(
        id = "legumes",
        titleRes = R.string.survey_category_legumes,
        defaultRoles = setOf(SurveyRole.HEAD_OF_HOUSEHOLD, SurveyRole.HOMEMAKER),
        icon = Icons.Rounded.Inventory2,
    ),
    VarietyCategoryItem(
        id = "vegetables",
        titleRes = R.string.survey_category_vegetables,
        defaultRoles = setOf(SurveyRole.HOMEMAKER, SurveyRole.HEAD_OF_HOUSEHOLD),
        icon = Icons.Rounded.Grass,
    ),
    VarietyCategoryItem(
        id = "fruits",
        titleRes = R.string.survey_category_fruits,
        defaultRoles = setOf(SurveyRole.HEAD_OF_HOUSEHOLD, SurveyRole.HOMEMAKER),
        icon = Icons.Rounded.Spa,
    ),
    VarietyCategoryItem(
        id = "spices",
        titleRes = R.string.survey_category_spices,
        defaultRoles = setOf(SurveyRole.HOMEMAKER),
        icon = Icons.Rounded.Coffee,
    ),
    VarietyCategoryItem(
        id = "cleaning",
        titleRes = R.string.survey_category_cleaning,
        defaultRoles = setOf(SurveyRole.HOMEMAKER),
        icon = Icons.Rounded.CleaningServices,
    ),
    VarietyCategoryItem(
        id = "transport",
        titleRes = R.string.survey_category_transport,
        defaultRoles = setOf(SurveyRole.HEAD_OF_HOUSEHOLD, SurveyRole.INDIVIDUAL),
        icon = Icons.Rounded.DirectionsCar,
    ),
    VarietyCategoryItem(
        id = "bills",
        titleRes = R.string.survey_category_bills,
        defaultRoles = setOf(SurveyRole.HEAD_OF_HOUSEHOLD, SurveyRole.INDIVIDUAL),
        icon = Icons.Rounded.ReceiptLong,
    ),
    VarietyCategoryItem(
        id = "personal",
        titleRes = R.string.survey_category_personal,
        defaultRoles = setOf(SurveyRole.INDIVIDUAL, SurveyRole.HEAD_OF_HOUSEHOLD),
        icon = Icons.Rounded.AccountBalanceWallet,
    ),
)

@Composable
fun SurveyBudgetTypeStep(
    initialType: SurveyBudgetType = SurveyBudgetType.HOUSEHOLD,
    onContinue: (SurveyBudgetType) -> Unit,
) {
    var selectedType by remember { mutableStateOf(initialType) }
    val statusBarHeight = LocalWindowInsets.current.calculateTopPadding()
    val navigationBarHeight = LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarHeight + 12.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = navigationBarHeight),
    ) {
        SurveyStepHeader(
            currentStep = 1,
            totalSteps = 3,
            title = stringResource(R.string.survey_budget_type_title),
            subtitle = stringResource(R.string.survey_subtitle),
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SurveyOptionCard(
                title = stringResource(R.string.survey_type_home),
                description = stringResource(R.string.survey_type_home_desc),
                icon = Icons.Rounded.Home,
                isSelected = selectedType == SurveyBudgetType.HOUSEHOLD,
                onClick = { selectedType = SurveyBudgetType.HOUSEHOLD },
            )

            SurveyOptionCard(
                title = stringResource(R.string.survey_type_salary),
                description = stringResource(R.string.survey_type_salary_desc),
                icon = Icons.Rounded.Payments,
                isSelected = selectedType == SurveyBudgetType.SALARY,
                onClick = { selectedType = SurveyBudgetType.SALARY },
            )

            SurveyOptionCard(
                title = stringResource(R.string.survey_type_allowance),
                description = stringResource(R.string.survey_type_allowance_desc),
                icon = Icons.Rounded.AccountBalanceWallet,
                isSelected = selectedType == SurveyBudgetType.ALLOWANCE,
                onClick = { selectedType = SurveyBudgetType.ALLOWANCE },
            )
        }

        DescriptionButton(
            title = { Text(stringResource(R.string.survey_btn_continue)) },
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            onClick = { onContinue(selectedType) },
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun SurveyRoleStep(
    initialRole: SurveyRole = SurveyRole.HEAD_OF_HOUSEHOLD,
    onContinue: (SurveyRole) -> Unit,
) {
    var selectedRole by remember { mutableStateOf(initialRole) }
    val statusBarHeight = LocalWindowInsets.current.calculateTopPadding()
    val navigationBarHeight = LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarHeight + 12.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = navigationBarHeight),
    ) {
        SurveyStepHeader(
            currentStep = 2,
            totalSteps = 3,
            title = stringResource(R.string.survey_role_title),
            subtitle = stringResource(R.string.survey_subtitle),
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SurveyOptionCard(
                title = stringResource(R.string.survey_role_head),
                description = stringResource(R.string.survey_role_head_desc),
                icon = Icons.Rounded.FamilyRestroom,
                isSelected = selectedRole == SurveyRole.HEAD_OF_HOUSEHOLD,
                onClick = { selectedRole = SurveyRole.HEAD_OF_HOUSEHOLD },
            )

            SurveyOptionCard(
                title = stringResource(R.string.survey_role_homemaker),
                description = stringResource(R.string.survey_role_homemaker_desc),
                icon = Icons.Rounded.Kitchen,
                isSelected = selectedRole == SurveyRole.HOMEMAKER,
                onClick = { selectedRole = SurveyRole.HOMEMAKER },
            )

            SurveyOptionCard(
                title = stringResource(R.string.survey_role_individual),
                description = stringResource(R.string.survey_role_individual_desc),
                icon = Icons.Rounded.School,
                isSelected = selectedRole == SurveyRole.INDIVIDUAL,
                onClick = { selectedRole = SurveyRole.INDIVIDUAL },
            )
        }

        DescriptionButton(
            title = { Text(stringResource(R.string.survey_btn_continue)) },
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            onClick = { onContinue(selectedRole) },
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun SurveyVarietiesStep(
    selectedRole: SurveyRole,
    onFinish: (Set<String>) -> Unit,
) {
    val initialSelection = remember(selectedRole) {
        EgyptianHouseholdVarieties
            .filter { selectedRole in it.defaultRoles }
            .map { it.id }
            .toSet()
    }
    var selectedIds by remember(selectedRole) { mutableStateOf(initialSelection) }
    val statusBarHeight = LocalWindowInsets.current.calculateTopPadding()
    val navigationBarHeight = LocalWindowInsets.current.calculateBottomPadding().coerceAtLeast(16.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarHeight + 12.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = navigationBarHeight),
    ) {
        SurveyStepHeader(
            currentStep = 3,
            totalSteps = 3,
            title = stringResource(R.string.survey_varieties_title),
            subtitle = stringResource(R.string.survey_varieties_subtitle),
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(EgyptianHouseholdVarieties, key = { it.id }) { item ->
                val isChecked = item.id in selectedIds
                val itemTitle = stringResource(item.titleRes)

                VarietyItemRow(
                    title = itemTitle,
                    icon = item.icon,
                    isChecked = isChecked,
                    onToggle = {
                        selectedIds = if (isChecked) {
                            selectedIds - item.id
                        } else {
                            selectedIds + item.id
                        }
                    },
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        DescriptionButton(
            title = { Text(stringResource(R.string.survey_btn_finish)) },
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            onClick = {
                onFinish(selectedIds)
            },
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SurveyStepHeader(
    currentStep: Int,
    totalSteps: Int,
    title: String,
    subtitle: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.survey_step_counter, currentStep, totalSteps),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMediumCondensed,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SurveyOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(250),
        label = "BorderColor",
    )
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMediumCondensed.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun VarietyItemRow(
    title: String,
    icon: ImageVector,
    isChecked: Boolean,
    onToggle: () -> Unit,
) {
    val backgroundColor = if (isChecked) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val borderColor = if (isChecked) MaterialTheme.colorScheme.secondary else Color.Transparent

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onToggle() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(
                        if (isChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceContainerHighest,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
