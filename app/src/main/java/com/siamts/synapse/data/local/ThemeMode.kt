package com.siamts.synapse.data.local

/**
 * Supported theme modes for SYNAPSE AI.
 */
enum class ThemeMode(
    val value: String,
    val title: String,
    val description: String
) {
    SYSTEM(
        value = "SYSTEM",
        title = "System Default",
        description = "Follows your device's light or dark appearance"
    ),
    LIGHT(
        value = "LIGHT",
        title = "Light Mode",
        description = "Crisp, bright, and vibrant interface"
    ),
    DARK(
        value = "DARK",
        title = "Dark Mode",
        description = "Futuristic cyberpunk dark neon theme"
    );

    companion object {
        fun fromValue(value: String?): ThemeMode {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: SYSTEM
        }
    }
}
