package com.wafeer.app.data.updater

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdateManagerTest {

    @Test
    fun isVersionNewer_detectsStrictlyNewerVersions() {
        // Newer patch
        assertTrue(AppUpdateManager.isVersionNewer("1.3.25", "1.3.24"))
        assertTrue(AppUpdateManager.isVersionNewer("v1.3.25", "1.3.24"))
        assertTrue(AppUpdateManager.isVersionNewer("1.3.25-beta", "1.3.24"))

        // Newer minor
        assertTrue(AppUpdateManager.isVersionNewer("1.4.0", "1.3.24"))
        assertTrue(AppUpdateManager.isVersionNewer("v1.4.0", "v1.3.24"))

        // Newer major
        assertTrue(AppUpdateManager.isVersionNewer("2.0.0", "1.3.24"))
        assertTrue(AppUpdateManager.isVersionNewer("2.0.0-rc1", "1.9.9"))

        // Multi-segment sub-patch
        assertTrue(AppUpdateManager.isVersionNewer("1.3.24.2", "1.3.24.1"))
    }

    @Test
    fun isVersionNewer_rejectsOlderOrEqualVersions() {
        // Same version
        assertFalse(AppUpdateManager.isVersionNewer("1.3.24", "1.3.24"))
        assertFalse(AppUpdateManager.isVersionNewer("v1.3.24", "1.3.24"))
        assertFalse(AppUpdateManager.isVersionNewer("1.3.24", "v1.3.24"))

        // Older patch
        assertFalse(AppUpdateManager.isVersionNewer("1.3.23", "1.3.24"))
        assertFalse(AppUpdateManager.isVersionNewer("1.3.20", "1.3.24"))

        // Older minor
        assertFalse(AppUpdateManager.isVersionNewer("1.2.99", "1.3.24"))

        // Older major
        assertFalse(AppUpdateManager.isVersionNewer("0.9.9", "1.0.0"))
    }

    @Test
    fun parseVersionCode_computesCorrectCode() {
        assertEquals(10324, AppUpdateManager.parseVersionCode("1.3.24"))
        assertEquals(10324, AppUpdateManager.parseVersionCode("v1.3.24"))
        assertEquals(10324, AppUpdateManager.parseVersionCode("v1.3.24-release"))
        assertEquals(10000, AppUpdateManager.parseVersionCode("1.0.0"))
        assertEquals(20512, AppUpdateManager.parseVersionCode("2.5.12"))
    }
}
