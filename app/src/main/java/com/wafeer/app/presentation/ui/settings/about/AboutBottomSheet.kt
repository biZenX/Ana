package com.wafeer.app.presentation.ui.settings.about

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafeer.app.BuildConfig
import com.wafeer.app.R
import com.wafeer.app.presentation.ui.theme.component.CustomPaddedListItem
import com.wafeer.app.presentation.ui.theme.component.PaddedListItemPosition
import com.wafeer.app.presentation.ui.theme.component.SettingsLeadingIcon
import com.wafeer.app.presentation.util.Utils.weakHapticFeedback
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutBottomSheet(
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uriHandler = LocalUriHandler.current
    val view = LocalView.current
    val scrollState = rememberScrollState()

    var isAvatarPressed by remember { mutableStateOf(false) }
    val avatarScale by animateFloatAsState(
        targetValue = if (isAvatarPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "avatarScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_lava")
    val lavaPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lava_phase"
    )

    val sageGreenPrimary = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.90f)
    val sageGreenAura = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Developer Avatar with organic circulating lava blob
            Box(
                modifier = Modifier
                    .scale(avatarScale)
                    .size(136.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        view.weakHapticFeedback()
                        isAvatarPressed = true
                    },
                contentAlignment = Alignment.Center
            ) {
                // Smooth organic liquid lava blob circulating softly behind the avatar
                Canvas(
                    modifier = Modifier.size(136.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r0 = size.width * 0.38f
                    val amp1 = size.width * 0.046f
                    val amp2 = size.width * 0.026f

                    val lavaPath = Path()
                    val auraPath = Path()
                    val steps = 180

                    for (i in 0..steps) {
                        val theta = (i.toFloat() / steps) * (2f * PI.toFloat())
                        // Smooth organic fluid harmonics (lava-lamp motion, zero sharp points)
                        val r = r0 + amp1 * sin(3f * theta - lavaPhase) + amp2 * cos(2f * theta + lavaPhase)
                        val x = center.x + r * cos(theta)
                        val y = center.y + r * sin(theta)

                        if (i == 0) lavaPath.moveTo(x, y) else lavaPath.lineTo(x, y)

                        val ra = r + 7.dp.toPx() + 3.dp.toPx() * sin(2f * theta - lavaPhase)
                        val ax = center.x + ra * cos(theta)
                        val ay = center.y + ra * sin(theta)
                        if (i == 0) auraPath.moveTo(ax, ay) else auraPath.lineTo(ax, ay)
                    }
                    lavaPath.close()
                    auraPath.close()

                    drawPath(path = auraPath, color = sageGreenAura)
                    drawPath(path = lavaPath, color = sageGreenPrimary)
                }

                // Developer Avatar character on top
                Image(
                    painter = painterResource(id = R.drawable.img_developer_avatar),
                    contentDescription = stringResource(R.string.about_developer),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(118.dp)
                )
            }

            LaunchedEffect(isAvatarPressed) {
                if (isAvatarPressed) {
                    kotlinx.coroutines.delay(180L)
                    isAvatarPressed = false
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Developer Name + Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Arkim",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Developer Role / Handle
            Text(
                text = "${stringResource(R.string.about_developer_title)} • ${stringResource(R.string.about_developer_handle)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline
            Text(
                text = stringResource(R.string.about_developer_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Version Chip
            AssistChip(
                onClick = {
                    view.weakHapticFeedback()
                    uriHandler.openUri("https://github.com/biZenX/Wafeer/releases")
                },
                label = {
                    Text(
                        text = "v${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                border = null
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Items Group without showing direct URLs
            CustomPaddedListItem(
                onClick = {
                    view.weakHapticFeedback()
                    uriHandler.openUri("https://github.com/biZenX")
                },
                position = PaddedListItemPosition.First
            ) {
                SettingsLeadingIcon(icon = Icons.Rounded.Person)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.about_github_profile_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CustomPaddedListItem(
                onClick = {
                    view.weakHapticFeedback()
                    uriHandler.openUri("https://github.com/biZenX/Wafeer")
                },
                position = PaddedListItemPosition.Middle
            ) {
                SettingsLeadingIcon(icon = Icons.Rounded.Code)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.about_repository_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CustomPaddedListItem(
                onClick = {
                    view.weakHapticFeedback()
                    uriHandler.openUri("https://bizenx.github.io/Wafeer/")
                },
                position = PaddedListItemPosition.Middle
            ) {
                SettingsLeadingIcon(icon = Icons.Rounded.Language)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.about_website_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CustomPaddedListItem(
                onClick = {
                    view.weakHapticFeedback()
                    uriHandler.openUri("https://github.com/biZenX/Wafeer/issues")
                },
                position = PaddedListItemPosition.Last
            ) {
                SettingsLeadingIcon(icon = Icons.Rounded.BugReport)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.about_issues_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Open Source Credits Card (Thanks text only)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.about_credits_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.about_credits_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
