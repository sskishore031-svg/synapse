package com.siamts.synapse.data.local

import com.google.gson.Gson
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.ExtendedCurriculumCatalog
import java.io.File
import java.io.InputStreamReader

/**
 * On-device data provider for the complete Tamil Nadu State Board (Samacheer Kalvi)
 * Class 10, Class 11, and Class 12 Core Curriculum (17 Subjects, 248 Chapters, 2480 MCQs).
 * Seamlessly loads from assets in Android runtime and falls back to project file in JVM unit tests.
 */
object SamacheerCurriculumData {
    private val gson = Gson()

    val subjects: List<CatalogSubject> by lazy {
        loadSubjects()
    }

    private fun loadSubjects(): List<CatalogSubject> {
        // 1. Try Android application context assets
        try {
            val context = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
            if (context != null) {
                context.assets.open("extended_curriculum_catalog.json").use { stream ->
                    InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                        val catalog = gson.fromJson(reader, ExtendedCurriculumCatalog::class.java)
                        if (catalog?.subjects?.isNotEmpty() == true) {
                            return catalog.subjects
                        }
                    }
                }
            }
        } catch (_: Throwable) {}

        // 2. Fallback for JVM unit tests (runs without Android Context)
        val possiblePaths = listOf(
            File("app/src/main/assets/extended_curriculum_catalog.json"),
            File("src/main/assets/extended_curriculum_catalog.json"),
            File("../app/src/main/assets/extended_curriculum_catalog.json"),
            File("c:/Users/SCAD LAB/AndroidStudioProjects/SYNAPSE/app/src/main/assets/extended_curriculum_catalog.json")
        )
        for (f in possiblePaths) {
            if (f.exists()) {
                try {
                    f.reader(Charsets.UTF_8).use { reader ->
                        val catalog = gson.fromJson(reader, ExtendedCurriculumCatalog::class.java)
                        if (catalog?.subjects?.isNotEmpty() == true) {
                            return catalog.subjects
                        }
                    }
                } catch (_: Throwable) {}
            }
        }
        return emptyList()
    }

    fun getSubjectById(subjectId: String): CatalogSubject? {
        val trimmed = subjectId.trim()
        val numId = trimmed.toIntOrNull()
        return subjects.firstOrNull { sub ->
            (numId != null && sub.subjectId == numId) ||
            sub.id.equals(trimmed, ignoreCase = true) ||
            sub.name.equals(trimmed, ignoreCase = true) ||
            sub.slug.equals(trimmed, ignoreCase = true)
        }
    }
}

