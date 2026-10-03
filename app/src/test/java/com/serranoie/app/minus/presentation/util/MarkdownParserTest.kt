package com.serranoie.app.minus.presentation.util

import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownParserTest {

    @Test
    fun `parseToAnnotatedString strips leading markdown headers and bullets`() {
        val raw = "### **عنوان التحديث**: تفاصيل التحديث الجديد"
        val parsed = MarkdownParser.parseToAnnotatedString(raw)

        assertEquals("عنوان التحديث: تفاصيل التحديث الجديد", parsed.text)
        assertEquals(1, parsed.spanStyles.size)
        assertEquals(FontWeight.Bold, parsed.spanStyles[0].item.fontWeight)
        assertEquals(0, parsed.spanStyles[0].start)
        assertEquals("عنوان التحديث".length, parsed.spanStyles[0].end)
    }

    @Test
    fun `parseToAnnotatedString handles bullet points correctly`() {
        val raw = "- **ميزة جديدة**: إضافة خيار التبديل"
        val parsed = MarkdownParser.parseToAnnotatedString(raw)

        assertEquals("ميزة جديدة: إضافة خيار التبديل", parsed.text)
        assertEquals(1, parsed.spanStyles.size)
        assertEquals(FontWeight.Bold, parsed.spanStyles[0].item.fontWeight)
    }

    @Test
    fun `stripMarkdown removes markdown tokens cleanly for notifications`() {
        val raw = "- **إصلاح لوجيك الصرف**: معالجة شروط النصائح"
        val stripped = MarkdownParser.stripMarkdown(raw)

        assertEquals("إصلاح لوجيك الصرف: معالجة شروط النصائح", stripped)
    }
}
