package com.serranoie.app.minus.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object MarkdownParser {

    /**
     * Cleans raw markdown artifacts (headers like ###, bullet markers, etc.)
     * and converts bold/italic/code markdown tokens into a styled Compose [AnnotatedString].
     */
    fun parseToAnnotatedString(
        rawText: String,
        boldWeight: FontWeight = FontWeight.Bold,
        highlightColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    ): AnnotatedString {
        if (rawText.isBlank()) return AnnotatedString("")

        // Step 1: Strip leading markdown headers (###, ##, #) and bullet dashes
        var cleaned = rawText.trim()
            .replace(Regex("^#{1,6}\\s+"), "") // strip ### headers requiring space
            .replace(Regex("^[\\-+•*]\\s+"), "") // strip bullet markers requiring space
            .replace(Regex("^\\d+\\.\\s+"), "") // strip ordered list numbers (1. )
            .trim()

        // Step 2: Parse bold (**text**), italic (*text*), and inline code (`text`)
        return buildAnnotatedString {
            var i = 0
            val len = cleaned.length

            while (i < len) {
                // Check bold **text**
                if (i + 1 < len && cleaned[i] == '*' && cleaned[i + 1] == '*') {
                    val endIndex = cleaned.indexOf("**", i + 2)
                    if (endIndex != -1) {
                        val boldContent = cleaned.substring(i + 2, endIndex)
                        withStyle(
                            SpanStyle(
                                fontWeight = boldWeight,
                                color = if (highlightColor != androidx.compose.ui.graphics.Color.Unspecified) highlightColor else androidx.compose.ui.graphics.Color.Unspecified,
                            )
                        ) {
                            append(boldContent)
                        }
                        i = endIndex + 2
                        continue
                    }
                }

                // Check inline code `text`
                if (cleaned[i] == '`') {
                    val endIndex = cleaned.indexOf('`', i + 1)
                    if (endIndex != -1) {
                        val codeContent = cleaned.substring(i + 1, endIndex)
                        withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)) {
                            append(codeContent)
                        }
                        i = endIndex + 1
                        continue
                    }
                }

                // Check italic *text*
                if (cleaned[i] == '*' && (i + 1 == len || cleaned[i + 1] != '*')) {
                    val endIndex = cleaned.indexOf('*', i + 1)
                    if (endIndex != -1 && (endIndex + 1 == len || cleaned[endIndex + 1] != '*')) {
                        val italicContent = cleaned.substring(i + 1, endIndex)
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(italicContent)
                        }
                        i = endIndex + 1
                        continue
                    }
                }

                // Regular character
                append(cleaned[i])
                i++
            }
        }
    }

    /**
     * Cleans plain text from markdown syntax without formatting (for notifications, logs, etc.)
     */
    fun stripMarkdown(rawText: String): String {
        return rawText.trim()
            .replace(Regex("^#{1,6}\\s+"), "")
            .replace(Regex("^[\\-+•*]\\s+"), "")
            .replace(Regex("^\\d+\\.\\s+"), "")
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("`(.*?)`"), "$1")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            .trim()
    }
}
