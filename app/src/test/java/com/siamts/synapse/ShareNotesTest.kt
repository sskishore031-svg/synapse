package com.siamts.synapse

import com.siamts.synapse.util.ShareUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareNotesTest {

    @Test
    fun testFormatShareTextWithSubjectAndCategory() {
        val title = "Quadratic Equations & Factoring"
        val content = "ax² + bx + c = 0.\nRoots: x = (-b ± √(b² - 4ac)) / (2a)"
        val subject = "Mathematics"
        val category = "High-Yield Summary"

        val formatted = ShareUtils.formatShareText(
            title = title,
            content = content,
            subject = subject,
            category = category
        )

        assertTrue("Must contain note title", formatted.contains("📝 Quadratic Equations & Factoring"))
        assertTrue("Must contain subject", formatted.contains("Mathematics"))
        assertTrue("Must contain category", formatted.contains("High-Yield Summary"))
        assertTrue("Must contain formula content", formatted.contains("ax² + bx + c = 0"))
        assertTrue("Must contain SYNAPSE signature", formatted.contains("Shared via SYNAPSE AI Tutor"))
    }

    @Test
    fun testFormatShareTextWithoutCategory() {
        val title = "Binary Search"
        val content = "Time complexity is O(log n)."
        val subject = "Computer Science"

        val formatted = ShareUtils.formatShareText(
            title = title,
            content = content,
            subject = subject
        )

        assertTrue(formatted.startsWith("📝 Binary Search • Computer Science"))
        assertTrue(formatted.contains("Time complexity is O(log n)."))
        assertTrue(formatted.endsWith("Shared via SYNAPSE AI Tutor 🎓"))
    }

    @Test
    fun testFormatShareTextMinimal() {
        val title = "Chemical Bonding"
        val content = "Ionic vs Covalent bonds."

        val formatted = ShareUtils.formatShareText(
            title = title,
            content = content
        )

        assertTrue(formatted.contains("📝 Chemical Bonding"))
        assertTrue(formatted.contains("Ionic vs Covalent bonds."))
    }
}
