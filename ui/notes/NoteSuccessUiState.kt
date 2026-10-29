package com.siamts.synapse.ui.notes

/**
 * UI State for the Note Saved Success and Note Downloaded Success Screens.
 */
data class NoteSuccessUiState(
    val title: String = "Keep learning.",
    val savedSubtitle: String = "A good idea, kept for later.",
    val downloadedSubtitle: String = "Take your learning with you.",
    val savedHeadline: String = "Saved Successfully!",
    val savedMessage: String = "Gradient descent notes have been saved to your library.",
    val downloadedHeadline: String = "Downloaded Successfully!",
    val downloadedMessage: String = "Gradient descent notes are ready to read without an internet connection.",
    val noteTitle: String = "Gradient descent",
    val savedNoteCategory: String = "Machine learning · AI-generated",
    val savedNoteTags: String = "Gradient · Loss · Learning rate",
    val downloadedFileInfo: String = "PDF · 3 pages · 248 KB",
    val downloadedFileStatus: String = "Downloaded notes · Ready to open"
)
