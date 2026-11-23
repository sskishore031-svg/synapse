package com.siamts.synapse.ui.navigation

import com.siamts.synapse.AppScreen

object NavTabs {
    const val HOME = 0
    const val LEARN = 1
    const val QUIZ = 2
    const val NOTES = 3
    const val PROFILE = 4
}

object NavGraph {
    /**
     * Resolves the bottom navigation bar active tab index (0..4) for any screen.
     * Sub-routes like ProgressAnalytics, Achievements, and Settings properly map
     * to PROFILE (4) so the Profile tab remains actively highlighted.
     * Returns -1 for non-tab screens (Splash, Welcome, Login, SignUp, etc.).
     */
    fun getTabForScreen(screen: AppScreen): Int {
        return when (screen) {
            is AppScreen.Dashboard,
            is AppScreen.Home -> NavTabs.HOME

            is AppScreen.SubjectSelection,
            is AppScreen.Roadmap,
            is AppScreen.DynamicChapterLesson,
            is AppScreen.Lesson,
            is AppScreen.LessonPart2,
            is AppScreen.QuadraticLesson,
            is AppScreen.QuadraticLessonPart2,
            is AppScreen.AlgorithmsLesson,
            is AppScreen.AlgorithmsLessonPart2,
            is AppScreen.DataVisualizationLesson,
            is AppScreen.DataVisualizationLessonPart2,
            is AppScreen.WavesLesson,
            is AppScreen.WavesLessonPart2,
            is AppScreen.ChemicalBondingLesson,
            is AppScreen.ChemicalBondingLessonPart2,
            is AppScreen.SubjectDetail,
            is AppScreen.BrainMap,
            is AppScreen.Flashcards -> NavTabs.LEARN

            is AppScreen.QuizHomeSelection,
            is AppScreen.SubjectQuizSelection,
            is AppScreen.DynamicQuizAttempt,
            is AppScreen.DynamicQuizResults,
            is AppScreen.DynamicQuizReview,
            is AppScreen.Quiz,
            is AppScreen.MixedQuiz,
            is AppScreen.QuizResults,
            is AppScreen.QuizReviewAnswers,
            is AppScreen.MathematicsPractice,
            is AppScreen.MathematicsChapterQuiz,
            is AppScreen.QuizIntro,
            is AppScreen.QuizResult -> NavTabs.QUIZ

            is AppScreen.NotesHome,
            is AppScreen.NotesList,
            is AppScreen.SavedNotes,
            is AppScreen.MixedSavedNotes,
            is AppScreen.NotesGenerator,
            is AppScreen.NoteSavedSuccess,
            is AppScreen.NoteDownloadedSuccess,
            is AppScreen.MathematicsNotes -> NavTabs.NOTES

            is AppScreen.Profile,
            is AppScreen.ProgressAnalytics,
            is AppScreen.Settings,
            is AppScreen.PersonalInfo,
            is AppScreen.Achievements,
            is AppScreen.StudyGoals,
            is AppScreen.LanguageSelection,
            is AppScreen.NotificationsSettings,
            is AppScreen.HelpSupport,
            is AppScreen.AboutSynapse,
            is AppScreen.WhatsNew,
            is AppScreen.TermsOfService,
            is AppScreen.PrivacyPolicy,
            is AppScreen.OpenSourceLicenses -> NavTabs.PROFILE

            else -> -1
        }
    }

    /**
     * Determines whether the unified Bottom Navigation Bar should be visible.
     */
    fun shouldShowBottomBar(screen: AppScreen): Boolean {
        return getTabForScreen(screen) != -1
    }

    /**
     * Maps a bottom navigation tab index to its root screen destination.
     */
    fun getScreenForTab(tabIndex: Int): AppScreen {
        return when (tabIndex) {
            NavTabs.HOME -> AppScreen.Dashboard
            NavTabs.LEARN -> AppScreen.SubjectSelection
            NavTabs.QUIZ -> AppScreen.QuizHomeSelection
            NavTabs.NOTES -> AppScreen.NotesHome
            NavTabs.PROFILE -> AppScreen.Profile
            else -> AppScreen.Dashboard
        }
    }
}
