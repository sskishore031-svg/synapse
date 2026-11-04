package com.siamts.synapse.util

import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Utility for sharing study notes, summaries, and formulas to external
 * applications (WhatsApp, Telegram, Gmail, Messages, Drive, Bluetooth, etc.)
 * using Android's native ACTION_SEND Chooser Intent.
 */
object ShareUtils {

    /**
     * Formats note content into a clean, human-readable message for sharing.
     */
    fun formatShareText(
        title: String,
        content: String,
        subject: String = "",
        category: String = ""
    ): String {
        return buildString {
            append("📝 ")
            append(title.trim())
            if (subject.isNotBlank()) {
                append(" • ")
                append(subject.trim())
            }
            if (category.isNotBlank() && !category.equals(subject, ignoreCase = true) && !category.equals("ALL", ignoreCase = true)) {
                append(" (")
                append(category.trim())
                append(")")
            }
            append("\n\n")
            append(content.trim())
            append("\n\n— Shared via SYNAPSE AI Tutor 🎓")
        }
    }

    /**
     * Builds the Intent for sharing note text.
     */
    fun createShareIntent(
        title: String,
        content: String,
        subject: String = "",
        category: String = ""
    ): Intent {
        val shareBody = formatShareText(title, content, subject, category)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        return Intent.createChooser(sendIntent, "Share note with a friend via...").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Launches the system share chooser dialog to send note to a friend.
     */
    fun shareNote(
        context: Context,
        title: String,
        content: String,
        subject: String = "",
        category: String = ""
    ) {
        try {
            val chooserIntent = createShareIntent(title, content, subject, category)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Unable to open sharing apps: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
