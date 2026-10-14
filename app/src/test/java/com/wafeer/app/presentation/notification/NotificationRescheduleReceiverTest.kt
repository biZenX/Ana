package com.wafeer.app.presentation.notification

import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRescheduleReceiverTest {

    @Test
    fun allowedSystemActions_containsExpectedSystemEvents() {
        val allowed = NotificationRescheduleReceiver.ALLOWED_SYSTEM_ACTIONS

        assertTrue(allowed.contains(Intent.ACTION_BOOT_COMPLETED))
        assertTrue(allowed.contains(Intent.ACTION_TIME_CHANGED))
        assertTrue(allowed.contains(Intent.ACTION_TIMEZONE_CHANGED))
        assertTrue(allowed.contains(Intent.ACTION_MY_PACKAGE_REPLACED))
    }

    @Test
    fun allowedSystemActions_rejectsUnauthorizedOrArbitraryActions() {
        val allowed = NotificationRescheduleReceiver.ALLOWED_SYSTEM_ACTIONS

        assertFalse(allowed.contains("com.wafeer.app.FAKE_ACTION"))
        assertFalse(allowed.contains("android.intent.action.VIEW"))
        assertFalse(allowed.contains(""))
        assertFalse(allowed.contains("random.external.app.BROADCAST"))
    }
}
