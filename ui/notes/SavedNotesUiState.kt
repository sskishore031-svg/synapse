package com.siamts.synapse.ui.notes

data class SavedNoteItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val statusText: String,
    val buttonAction: String, // Open, Download
)

data class DownloadedFileItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val offlineBadge: String,
    val buttonAction: String, // Open
)

/**
 * UI State for the Saved & Downloaded Notes Screen ("Your Study Library").
 */
data class SavedNotesUiState(
    val headerTag: String = "YOUR STUDY LIBRARY",
    val title: String = "Saved & Downloaded Notes",
    val searchQuery: String = "",
    val savedNotes: List<SavedNoteItem> = listOf(
        SavedNoteItem("1", "Gradient descent", "Machine learning · AI-generated", "Saved · Downloaded", "Open"),
        SavedNoteItem("2", "Linear regression", "Machine learning · AI-generated", "Saved · Yesterday", "Download"),
    ),
    val downloadedFiles: List<DownloadedFileItem> = listOf(
        DownloadedFileItem("1", "Gradient descent", "PDF · 3 pages · 248 KB", "Available offline", "Open"),
        DownloadedFileItem("2", "Matrix operations", "PDF · 4 pages · 312 KB", "Available offline", "Open"),
    ),
    val disclaimer: String = "Downloaded notes stay available without internet.",
    val selectedTab: Int = 3,
)
