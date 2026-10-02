@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.serranoie.app.minus.presentation.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import com.serranoie.app.minus.R
import kotlin.math.ceil

@Composable
internal fun AddExpenseButton(
    contentDescription: String,
    size: Dp,
    modifier: GlanceModifier = GlanceModifier,
) {
    val iconSize = (size.value / 2).dp
    Box(
        modifier = modifier
            .size(size)
            .clickable(actionRunCallback<OpenAppAction>()),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(R.drawable.shape_soft_star_1),
            contentDescription = null,
            modifier = GlanceModifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
        )
        Image(
            provider = ImageProvider(R.drawable.ic_plus),
            contentDescription = contentDescription,
            modifier = GlanceModifier.size(iconSize),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary)
        )
    }
}

@Preview(widthDp = 120, heightDp = 60)
@Composable
private fun AddExpenseButtonPreview() {
    GlanceTheme {
        Row(modifier = GlanceModifier.size(120.dp, 60.dp), verticalAlignment = Alignment.CenterVertically) {
            AddExpenseButton(contentDescription = "Add new expense", size = 40.dp)
            AddExpenseButton(contentDescription = "Add new expense", size = 32.dp)
        }
    }
}

fun renderThmanyahTextBitmap(
    context: Context,
    text: String,
    textColor: Int,
    textSizeSp: Float,
    isBold: Boolean = false,
    useDisplayFont: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
): Bitmap {
    if (text.isEmpty()) {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
    val displayMetrics = context.resources.displayMetrics
    val textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSp, displayMetrics)

    val hasLetters = text.any { it.isLetter() }
    val fontRes = if (hasLetters) {
        when {
            useDisplayFont && isBold -> R.font.thmanyah_serif_display_bold
            useDisplayFont -> R.font.thmanyah_serif_display_medium
            isBold -> R.font.thmanyah_sans_bold
            else -> R.font.thmanyah_sans_medium
        }
    } else {
        when {
            useDisplayFont && isBold -> R.font.ibm_plex_sans_arabic_bold
            useDisplayFont -> R.font.ibm_plex_sans_arabic_semibold
            isBold -> R.font.ibm_plex_sans_arabic_bold
            else -> R.font.ibm_plex_sans_arabic_medium
        }
    }
    val typeface = ResourcesCompat.getFont(context, fontRes) ?: Typeface.DEFAULT

    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        this.textSize = textSizePx
        this.color = textColor
        this.isAntiAlias = true
        this.isSubpixelText = true
    }

    val layoutAlignment = when (textAlign) {
        TextAlign.Center -> Layout.Alignment.ALIGN_CENTER
        TextAlign.End -> Layout.Alignment.ALIGN_OPPOSITE
        else -> Layout.Alignment.ALIGN_NORMAL
    }

    val textWidth = ceil(textPaint.measureText(text)).toInt().coerceAtLeast(1)
    val layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, textWidth)
        .setAlignment(layoutAlignment)
        .setIncludePad(false)
        .build()

    val horizontalPadding = (textSizePx * 0.12f).toInt().coerceAtLeast(2)
    val verticalPadding = (textSizePx * 0.15f).toInt().coerceAtLeast(2)

    val bitmapWidth = layout.width + (horizontalPadding * 2)
    val bitmapHeight = layout.height + (verticalPadding * 2)

    val bitmap = Bitmap.createBitmap(
        bitmapWidth.coerceAtLeast(1),
        bitmapHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    canvas.save()
    canvas.translate(horizontalPadding.toFloat(), verticalPadding.toFloat())
    layout.draw(canvas)
    canvas.restore()
    return bitmap
}

@Composable
fun WidgetThmanyahText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    color: ColorProvider = GlanceTheme.colors.onSurface,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    useDisplayFont: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1,
) {
    val context = LocalContext.current
    val colorInt = color.getColor(context).toArgb()
    val isBold = fontWeight == FontWeight.Bold
    val bitmap = remember(text, colorInt, fontSize.value, isBold, useDisplayFont, textAlign) {
        renderThmanyahTextBitmap(
            context = context,
            text = text,
            textColor = colorInt,
            textSizeSp = fontSize.value,
            isBold = isBold,
            useDisplayFont = useDisplayFont,
            textAlign = textAlign,
        )
    }

    val density = context.resources.displayMetrics.density
    val widthDp = (bitmap.width / density).dp
    val heightDp = (bitmap.height / density).dp

    Image(
        provider = ImageProvider(bitmap),
        contentDescription = text,
        modifier = modifier.size(widthDp, heightDp)
    )
}

@Composable
fun WidgetThmanyahText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    color: Color,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    useDisplayFont: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1,
) = WidgetThmanyahText(
    text = text,
    modifier = modifier,
    color = ColorProvider(color),
    fontSize = fontSize,
    fontWeight = fontWeight,
    useDisplayFont = useDisplayFont,
    textAlign = textAlign,
    maxLines = maxLines,
)
