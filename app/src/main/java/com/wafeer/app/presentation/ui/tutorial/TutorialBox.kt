package com.wafeer.app.presentation.ui.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.R
import com.wafeer.app.presentation.ui.theme.bodyMediumCondensed
import com.wafeer.app.presentation.ui.theme.titleMediumCondensed
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntSize
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlinx.coroutines.delay
import logcat.logcat
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TutorialBox(
    showTutorial: Boolean,
    onTutorialCompleted: () -> Unit,
    state: TutorialBoxState,
    tutorialTarget: @Composable (index: Int) -> Unit,
    onTutorialReopened: () -> Unit = {},
    onCutoutClick: ((Int) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val isCompleted by remember { derivedStateOf { state.isCompleted } }
    val currentIndex by remember { derivedStateOf { state.currentIndexState.value } }
    val activeBounds by remember { derivedStateOf { state.currentBounds } }
    val isVirtual by remember {
        derivedStateOf { state.currentIndexState.value in VirtualIndices }
    }
    val shouldShow by remember(showTutorial, canvasSize) {
        derivedStateOf {
            showTutorial && !isCompleted && (isVirtual || activeBounds != null) &&
                    canvasSize.isSpecified
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                val w = coords.size.width.toFloat()
                val h = coords.size.height.toFloat()
                if (w > 0f && h > 0f) canvasSize = Size(w, h)
            },
    ) {
        content()

        AnimatedVisibility(
            visible = shouldShow,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(400))
        ) {
            val order = state.registrationOrder
            val currentStepIndex = order.indexOf(currentIndex).coerceAtLeast(0)
            val totalSteps = order.size.coerceAtLeast(1)

            TutorialOverlay(
                bounds = activeBounds ?: Rect.Zero,
                canvasSize = canvasSize,
                index = currentIndex,
                currentStep = currentStepIndex,
                totalSteps = totalSteps,
                isVirtual = isVirtual,
                tutorialTarget = tutorialTarget,
                onNext = { state.advance() },
                onSkipAll = { state.skipAll() },
                onCutoutClick = onCutoutClick,
            )
        }
    }

    LaunchedEffect(currentIndex, state.targetBounds.size, showTutorial) {
        if (showTutorial &&
            currentIndex != -1 &&
            !isCompleted &&
            !isVirtual &&
            state.currentBounds == null
        ) {
            delay(400.milliseconds)
            if (state.currentBounds == null) {
                logcat(TUTORIAL_LOG_TAG) {
                    "TutorialBox: current target $currentIndex " +
                            "still has no bounds after settle delay, auto-advancing"
                }
                state.advance()
            }
        }
    }

    LaunchedEffect(state.pendingRewindCandidates.size) {
        if (state.pendingRewindCandidates.isNotEmpty()) {
            val currentIndexBeforeDelay = state.currentIndexState.value
            val isCompletedBeforeDelay = state.isCompleted
            delay(50.milliseconds)

            if (state.currentIndexState.value != currentIndexBeforeDelay ||
                state.isCompleted != isCompletedBeforeDelay
            ) {
                state.pendingRewindCandidates.clear()
                return@LaunchedEffect
            }
            if (state.pendingRewindCandidates.isEmpty()) return@LaunchedEffect
            val order = state.registrationOrder
            val lowest = state.pendingRewindCandidates
                .minByOrNull { order.indexOf(it) }

            state.pendingRewindCandidates.clear()
            if (lowest != null) {
                val targetPos = order.indexOf(lowest)
                if (state.isCompleted) {
                    state.isCompleted = false
                    state.currentIndexState.value = lowest
                    onTutorialReopened()
                    logcat(TUTORIAL_LOG_TAG) {
                        "rewind-apply: index=$lowest AFTER completion " +
                                "(targetPos=$targetPos) — fired onTutorialReopened"
                    }
                } else {
                    val currentPos = order
                        .indexOf(state.currentIndexState.value)
                        .coerceAtLeast(0)
                    if (currentPos != targetPos) {
                        val outgoing = state.currentIndexState.value
                        if (outgoing in order && outgoing !in GatedIndices) {
                            state.visitedIndices.add(outgoing)
                        }
                        state.currentIndexState.value = lowest
                        logcat(TUTORIAL_LOG_TAG) {
                            "rewind-apply: index=$lowest " +
                                    "(currentPos=$currentPos, targetPos=$targetPos) " +
                                    "marked outgoing index=$outgoing as visited"
                        }
                    } else {
                        logcat(TUTORIAL_LOG_TAG) {
                            "rewind-apply: skipped index=$lowest " +
                                    "(currentPos=$currentPos == targetPos=$targetPos)"
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(isCompleted) {
        if (isCompleted) onTutorialCompleted()
    }
}

@Composable
private fun TutorialOverlay(
    bounds: Rect,
    canvasSize: Size,
    index: Int,
    currentStep: Int,
    totalSteps: Int,
    isVirtual: Boolean,
    tutorialTarget: @Composable (Int) -> Unit,
    onNext: () -> Unit,
    onSkipAll: () -> Unit,
    onCutoutClick: ((Int) -> Unit)? = null,
) {
    val density = LocalDensity.current
    val isPrivacyStep = index == 6
    val effectiveBounds = if (isPrivacyStep) {
        val sensorWidthPx = with(density) { 150.dp.toPx() }
        val sensorHeightPx = with(density) { 52.dp.toPx() }
        val sensorLeft = (canvasSize.width - sensorWidthPx) / 2f
        Rect(
            left = sensorLeft,
            top = 0f,
            right = sensorLeft + sensorWidthPx,
            bottom = sensorHeightPx,
        )
    } else bounds

    if (!isPrivacyStep && !isVirtual && bounds.isEmpty) return

    val scrimColor = Color.Black.copy(alpha = 0.6f)
    val highlightStrokeColor = MaterialTheme.colorScheme.inversePrimary
    val pulseStrokeColor = MaterialTheme.colorScheme.inversePrimary
    val interactionSource = remember { MutableInteractionSource() }
    val paddingPx = with(density) { 4.dp.toPx() }
    val cornerRadiusPx = with(density) { 12.dp.toPx() }
    val tooltipGapPx = with(density) { 20.dp.toPx() }
    val tooltipMaxWidthPx = with(density) { 320.dp.toPx() }
    val tooltipMaxWidth = with(density) { tooltipMaxWidthPx.toDp() }
    val tooltipMinHeightEstimate = with(density) { 180.dp.toPx() }

    val infiniteTransition = rememberInfiniteTransition(label = "TutorialPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    val (tooltipX, tooltipY) = if (isVirtual && !isPrivacyStep) {
        val centredX = (canvasSize.width - tooltipMaxWidthPx) / 2f
        val centredY = (canvasSize.height - tooltipMinHeightEstimate) / 2f
        centredX.toInt() to centredY.toInt()
    } else {
        computeTooltipPosition(
            targetBounds = effectiveBounds,
            canvasSize = canvasSize,
            gapPx = tooltipGapPx,
            tooltipWidthPx = tooltipMaxWidthPx,
            tooltipHeightPx = tooltipMinHeightEstimate,
        )
    }

    val animatedBounds by animateRectAsState(
        targetValue = effectiveBounds,
        animationSpec = tween(400, easing = LinearOutSlowInEasing),
        label = "TargetBounds"
    )

    val animatedCutout = Rect(
        left = animatedBounds.left - paddingPx,
        top = animatedBounds.top - paddingPx,
        right = animatedBounds.right + paddingPx,
        bottom = animatedBounds.bottom + paddingPx,
    )

    val animatedOffset by animateIntOffsetAsState(
        targetValue = IntOffset(tooltipX, tooltipY),
        animationSpec = tween(400, easing = LinearOutSlowInEasing),
        label = "TooltipOffset"
    )

    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.92f) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(index) {
        contentAlpha.snapTo(0f)
        contentScale.snapTo(0.92f)
        progress.snapTo(0f)
        contentAlpha.animateTo(1f, tween(300))
        contentScale.animateTo(1f, tween(400, easing = LinearOutSlowInEasing))
    }

    val hasHole = !animatedCutout.isEmpty && (!isVirtual || isPrivacyStep)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(animatedCutout, isVirtual, isPrivacyStep, onCutoutClick, onNext) {
                detectTapGestures { offset ->
                    if (hasHole && animatedCutout.contains(offset)) {
                        if (onCutoutClick != null) {
                            onCutoutClick(index)
                        } else {
                            onNext()
                        }
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!hasHole) {
                drawRect(color = scrimColor)
            } else {
                val holeRadius = if (isPrivacyStep) 24.dp.toPx() else cornerRadiusPx
                val outer = Path().apply {
                    addRect(Rect(offset = Offset.Zero, size = this@Canvas.size))
                }
                val hole = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = animatedCutout,
                            cornerRadius = CornerRadius(holeRadius, holeRadius),
                        ),
                    )
                }
                val scrimPath = Path().apply {
                    op(outer, hole, PathOperation.Difference)
                }
                drawPath(path = scrimPath, color = scrimColor)

                // Pulse Ring
                val pulseWidth = animatedCutout.width * pulseScale
                val pulseHeight = animatedCutout.height * pulseScale
                val pulseTopLeft = Offset(
                    animatedCutout.left - (pulseWidth - animatedCutout.width) / 2f,
                    animatedCutout.top - (pulseHeight - animatedCutout.height) / 2f
                )
                drawRoundRect(
                    color = pulseStrokeColor.copy(alpha = pulseAlpha),
                    topLeft = pulseTopLeft,
                    size = Size(pulseWidth, pulseHeight),
                    cornerRadius = CornerRadius(
                        holeRadius * pulseScale,
                        holeRadius * pulseScale
                    ),
                    style = Stroke(width = 8f),
                )

                drawRoundRect(
                    color = highlightStrokeColor,
                    topLeft = Offset(animatedCutout.left, animatedCutout.top),
                    size = Size(animatedCutout.width, animatedCutout.height),
                    cornerRadius = CornerRadius(holeRadius, holeRadius),
                    style = Stroke(width = 5f),
                )

                // Dynamic Curved Directional Arrow
                if (hasHole && tooltipSize.width > 0 && tooltipSize.height > 0) {
                    val tooltipRect = Rect(
                        left = animatedOffset.x.toFloat(),
                        top = animatedOffset.y.toFloat(),
                        right = (animatedOffset.x + tooltipSize.width).toFloat(),
                        bottom = (animatedOffset.y + tooltipSize.height).toFloat(),
                    )
                    val isBelow = tooltipRect.top >= animatedCutout.bottom - 6f
                    val isAbove = tooltipRect.bottom <= animatedCutout.top + 6f
                    val isLeft = tooltipRect.right <= animatedCutout.left + 6f

                    val (startPt, endPt, controlPt) = when {
                        isBelow -> {
                            val startX = animatedCutout.center.x.coerceIn(tooltipRect.left + 24f, tooltipRect.right - 24f)
                            val startY = tooltipRect.top
                            val endX = animatedCutout.center.x.coerceIn(animatedCutout.left + 12f, animatedCutout.right - 12f)
                            val endY = animatedCutout.bottom + 8.dp.toPx()
                            val curvature = 24.dp.toPx() * (if (startX >= endX) 1f else -1f)
                            Triple(
                                Offset(startX, startY),
                                Offset(endX, endY),
                                Offset((startX + endX) / 2f + curvature, (startY + endY) / 2f)
                            )
                        }
                        isAbove -> {
                            val startX = animatedCutout.center.x.coerceIn(tooltipRect.left + 24f, tooltipRect.right - 24f)
                            val startY = tooltipRect.bottom
                            val endX = animatedCutout.center.x.coerceIn(animatedCutout.left + 12f, animatedCutout.right - 12f)
                            val endY = animatedCutout.top - 8.dp.toPx()
                            val curvature = 24.dp.toPx() * (if (startX >= endX) 1f else -1f)
                            Triple(
                                Offset(startX, startY),
                                Offset(endX, endY),
                                Offset((startX + endX) / 2f + curvature, (startY + endY) / 2f)
                            )
                        }
                        isLeft -> {
                            val startX = tooltipRect.right
                            val startY = animatedCutout.center.y.coerceIn(tooltipRect.top + 16f, tooltipRect.bottom - 16f)
                            val endX = animatedCutout.left - 8.dp.toPx()
                            val endY = animatedCutout.center.y.coerceIn(animatedCutout.top + 12f, animatedCutout.bottom - 12f)
                            Triple(
                                Offset(startX, startY),
                                Offset(endX, endY),
                                Offset((startX + endX) / 2f, (startY + endY) / 2f + 20.dp.toPx())
                            )
                        }
                        else -> {
                            val startX = tooltipRect.left
                            val startY = animatedCutout.center.y.coerceIn(tooltipRect.top + 16f, tooltipRect.bottom - 16f)
                            val endX = animatedCutout.right + 8.dp.toPx()
                            val endY = animatedCutout.center.y.coerceIn(animatedCutout.top + 12f, animatedCutout.bottom - 12f)
                            Triple(
                                Offset(startX, startY),
                                Offset(endX, endY),
                                Offset((startX + endX) / 2f, (startY + endY) / 2f + 20.dp.toPx())
                            )
                        }
                    }

                    val dist = hypot(endPt.x - startPt.x, endPt.y - startPt.y)
                    if (dist >= 20.dp.toPx()) {
                        val arrowPath = Path().apply {
                            moveTo(startPt.x, startPt.y)
                            quadraticTo(controlPt.x, controlPt.y, endPt.x, endPt.y)
                        }

                        // Shadow outline
                        drawPath(
                            path = arrowPath,
                            color = Color.Black.copy(alpha = 0.4f),
                            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Highlight arrow curve
                        drawPath(
                            path = arrowPath,
                            color = highlightStrokeColor,
                            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Origin dot
                        drawCircle(
                            color = highlightStrokeColor,
                            radius = 3.5.dp.toPx(),
                            center = startPt
                        )

                        // Arrowhead
                        val tangentX = endPt.x - controlPt.x
                        val tangentY = endPt.y - controlPt.y
                        val angle = atan2(tangentY.toDouble(), tangentX.toDouble())
                        val arrowHeadLen = 12.dp.toPx()
                        val spread = Math.PI / 5.5

                        val tip = endPt
                        val wing1 = Offset(
                            (endPt.x - cos(angle - spread) * arrowHeadLen).toFloat(),
                            (endPt.y - sin(angle - spread) * arrowHeadLen).toFloat()
                        )
                        val wing2 = Offset(
                            (endPt.x - cos(angle + spread) * arrowHeadLen).toFloat(),
                            (endPt.y - sin(angle + spread) * arrowHeadLen).toFloat()
                        )

                        val headPath = Path().apply {
                            moveTo(tip.x, tip.y)
                            lineTo(wing1.x, wing1.y)
                            lineTo(wing2.x, wing2.y)
                            close()
                        }
                        drawPath(path = headPath, color = Color.Black.copy(alpha = 0.4f), style = Stroke(width = 2.dp.toPx()))
                        drawPath(path = headPath, color = highlightStrokeColor, style = Fill)
                    }
                }
            }
        }

        if (hasHole) {
            PulsingTapPointer(targetBounds = animatedCutout)
        }

        val currentLayoutDir = LocalLayoutDirection.current
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .offset { animatedOffset }
                    .onGloballyPositioned { coords ->
                        tooltipSize = coords.size
                    }
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        scaleX = contentScale.value
                        scaleY = contentScale.value
                    }
                    .widthIn(max = tooltipMaxWidth)
                    .padding(horizontal = 16.dp),
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDir) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Balanced reading timer line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress.value)
                                    .height(3.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            tutorialTarget(index)
                            Spacer(Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Animated dots indicator with smooth transition between dots (بدون كلام)
                                if (totalSteps > 1) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (i in 0 until totalSteps) {
                                            val isActive = i == currentStep
                                            val dotWidth by animateDpAsState(
                                                targetValue = if (isActive) 18.dp else 6.dp,
                                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                                label = "DotWidth_$i"
                                            )
                                            val dotColor by animateColorAsState(
                                                targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                                animationSpec = tween(300),
                                                label = "DotColor_$i"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(6.dp)
                                                    .width(dotWidth)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(dotColor)
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(Modifier.width(1.dp))
                                }

                                // Next and Skip Tour action buttons
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isLast = currentStep >= totalSteps - 1
                                    if (!isLast) {
                                        TextButton(
                                            onClick = onSkipAll,
                                            modifier = Modifier.height(36.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        ) {
                                            Text(
                                                text = stringResource(R.string.tutorial_end_tour),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                                maxLines = 1,
                                                softWrap = false,
                                            )
                                        }
                                    }

                                    FilledTonalButton(
                                        onClick = onNext,
                                        modifier = Modifier
                                            .height(36.dp)
                                            .defaultMinSize(minWidth = 68.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    ) {
                                        Text(
                                            text = if (isLast || isVirtual) {
                                                stringResource(R.string.tutorial_understood)
                                            } else {
                                                stringResource(R.string.next)
                                            },
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            softWrap = false,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun computeTooltipPosition(
    targetBounds: Rect,
    canvasSize: Size,
    gapPx: Float,
    tooltipWidthPx: Float,
    tooltipHeightPx: Float,
): Pair<Int, Int> {
    val spaceAbove = targetBounds.top
    val spaceBelow = canvasSize.height - targetBounds.bottom
    val spaceLeft = targetBounds.left
    val spaceRight = canvasSize.width - targetBounds.right

    val placement = when {
        spaceBelow >= tooltipHeightPx + gapPx -> TooltipPlacement.Below
        spaceAbove >= tooltipHeightPx + gapPx -> TooltipPlacement.Above
        spaceRight >= tooltipWidthPx + gapPx -> TooltipPlacement.Right
        spaceLeft >= tooltipWidthPx + gapPx -> TooltipPlacement.Left
        else -> TooltipPlacement.Below
    }

    val tooltipWidth = tooltipWidthPx.coerceAtMost(canvasSize.width - 32f)
    val centerX = (targetBounds.left + targetBounds.right) / 2f
    val centerY = (targetBounds.top + targetBounds.bottom) / 2f

    val (rawX, rawY) = when (placement) {
        TooltipPlacement.Below -> {
            val anchoredX =
                (centerX - tooltipWidth / 2f).coerceIn(16f, canvasSize.width - tooltipWidth - 16f)
            anchoredX to (targetBounds.bottom + gapPx)
        }

        TooltipPlacement.Above -> {
            val anchoredX =
                (centerX - tooltipWidth / 2f).coerceIn(16f, canvasSize.width - tooltipWidth - 16f)
            anchoredX to (targetBounds.top - gapPx - tooltipHeightPx)
        }

        TooltipPlacement.Right -> {
            (targetBounds.right + gapPx) to (centerY - tooltipHeightPx / 2f)
        }

        TooltipPlacement.Left -> {
            (targetBounds.left - gapPx - tooltipWidth) to (centerY - tooltipHeightPx / 2f)
        }
    }

    val safeX = rawX.coerceIn(0f, (canvasSize.width - tooltipWidth).coerceAtLeast(0f))
    val safeY = rawY.coerceIn(0f, (canvasSize.height - tooltipHeightPx).coerceAtLeast(0f))
    return safeX.toInt() to safeY.toInt()
}

private val Size.isSpecified: Boolean
    get() = width > 0f && height > 0f

private enum class TooltipPlacement { Above, Below, Left, Right }

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun TutorialBoxPreview() {
    WafeerTheme {
        val state = rememberTutorialBoxState()
        LaunchedEffect(Unit) {
            state.advance()
        }
        TutorialBox(
            showTutorial = true,
            onTutorialCompleted = {},
            state = state,
            tutorialTarget = { index ->
                TutorialTooltip(
                    title = "Tutorial step $index",
                    description = "This is a description for the tutorial step $index."
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun TutorialTooltip(
    title: String?,
    description: String,
    icon: ImageVector? = null,
    badgeText: String? = null,
    actionContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        if (icon != null || title != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (icon != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.size(38.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }

                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMediumCondensed.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        if (description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMediumCondensed,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (actionContent != null) {
            Spacer(Modifier.height(10.dp))
            actionContent()
        }
    }
}

@Composable
private fun PulsingTapPointer(
    targetBounds: Rect,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TapPointerTransition")

    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RippleScale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RippleAlpha"
    )

    val centerX = targetBounds.center.x
    val centerY = targetBounds.center.y
    val primaryColor = MaterialTheme.colorScheme.primary

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Canvas(modifier = modifier.fillMaxSize()) {
            // Soft focal wave pulse on the interactive target
            drawCircle(
                color = primaryColor.copy(alpha = rippleAlpha),
                radius = 18.dp.toPx() * rippleScale,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx()),
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.25f),
                radius = 6.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }
    }
}
