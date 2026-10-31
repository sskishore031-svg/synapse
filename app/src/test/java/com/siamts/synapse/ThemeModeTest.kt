package com.siamts.synapse

import com.siamts.synapse.data.local.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeModeTest {

    @Test
    fun testThemeModeFromValue() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromValue("SYSTEM"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromValue("system"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromValue("LIGHT"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromValue("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromValue("DARK"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromValue("dark"))
    }

    @Test
    fun testThemeModeFallbackForNullOrInvalid() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromValue(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromValue(""))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromValue("UNKNOWN_MODE"))
    }

    @Test
    fun testThemeModeTitlesAndDescriptions() {
        assertEquals("System Default", ThemeMode.SYSTEM.title)
        assertEquals("Light Mode", ThemeMode.LIGHT.title)
        assertEquals("Dark Mode", ThemeMode.DARK.title)
    }
}
