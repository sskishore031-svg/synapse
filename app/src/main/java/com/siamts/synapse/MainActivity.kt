package com.siamts.synapse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.siamts.synapse.data.local.BaseCurriculumData
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.siamts.synapse.ui.components.BottomNavBar
import com.siamts.synapse.ui.navigation.NavGraph
import com.siamts.synapse.notification.SynapseNotificationHelper
import com.siamts.synapse.ui.algorithms.AlgorithmsLessonPart2Screen
import com.siamts.synapse.ui.algorithms.AlgorithmsLessonPart2ViewModel
import com.siamts.synapse.ui.algorithms.AlgorithmsLessonScreen
import com.siamts.synapse.ui.algorithms.AlgorithmsLessonViewModel
import com.siamts.synapse.ui.auth.LoginScreen
import com.siamts.synapse.ui.auth.LoginViewModel
import com.siamts.synapse.ui.brainmap.BrainMapScreen
import com.siamts.synapse.ui.brainmap.BrainMapViewModel
import com.siamts.synapse.ui.chemistry.ChemicalBondingLessonPart2Screen
import com.siamts.synapse.ui.chemistry.ChemicalBondingLessonPart2ViewModel
import com.siamts.synapse.ui.chemistry.ChemicalBondingLessonScreen
import com.siamts.synapse.ui.chemistry.ChemicalBondingLessonViewModel
import com.siamts.synapse.ui.dashboard.DashboardScreen
import com.siamts.synapse.ui.dashboard.DashboardViewModel
import com.siamts.synapse.ui.datavis.DataVisualizationLessonPart2Screen
import com.siamts.synapse.ui.datavis.DataVisualizationLessonPart2ViewModel
import com.siamts.synapse.ui.datavis.DataVisualizationLessonScreen
import com.siamts.synapse.ui.datavis.DataVisualizationLessonViewModel
import com.siamts.synapse.ui.flashcards.FlashcardsScreen
import com.siamts.synapse.ui.flashcards.FlashcardsViewModel
import com.siamts.synapse.ui.home.HomeScreen
import com.siamts.synapse.ui.home.HomeViewModel
import com.siamts.synapse.ui.lesson.LessonPart2Screen
import com.siamts.synapse.ui.lesson.LessonPart2ViewModel
import com.siamts.synapse.ui.lesson.LessonScreen
import com.siamts.synapse.ui.lesson.LessonViewModel
import com.siamts.synapse.ui.notes.MixedSavedDownloadedNotesPreviewScreen
import com.siamts.synapse.ui.notes.NoteDownloadedSuccessScreen
import com.siamts.synapse.ui.notes.NoteSavedSuccessScreen
import com.siamts.synapse.ui.notes.NoteSuccessViewModel
import com.siamts.synapse.ui.notes.NotesGeneratorScreen
import com.siamts.synapse.ui.notes.NotesGeneratorViewModel
import com.siamts.synapse.ui.notes.SavedNotesScreen
import com.siamts.synapse.ui.notes.SavedNotesViewModel
import com.siamts.synapse.ui.profile.AboutRoutes
import com.siamts.synapse.ui.profile.AboutSynapseScreen
import com.siamts.synapse.ui.profile.AchievementsScreen
import com.siamts.synapse.ui.profile.HelpSupportScreen
import com.siamts.synapse.ui.profile.LanguageSelectionScreen
import com.siamts.synapse.ui.profile.NotificationsSettingsScreen
import com.siamts.synapse.ui.profile.OpenSourceLicensesScreen
import com.siamts.synapse.ui.profile.PersonalInfoScreen
import com.siamts.synapse.ui.profile.PrivacyPolicyScreen
import com.siamts.synapse.ui.profile.ProfileScreen
import com.siamts.synapse.ui.profile.ProfileViewModel
import com.siamts.synapse.ui.profile.ProgressAnalyticsScreen
import com.siamts.synapse.ui.profile.SettingsScreen
import com.siamts.synapse.ui.profile.StudyGoalsScreen
import com.siamts.synapse.ui.profile.TermsOfServiceScreen
import com.siamts.synapse.ui.profile.WhatsNewScreen
import com.siamts.synapse.ui.quadratic.MathChapterQuizViewModel
import com.siamts.synapse.ui.quadratic.MathNotesViewModel
import com.siamts.synapse.ui.quadratic.MathPracticeViewModel
import com.siamts.synapse.ui.quadratic.MathematicsChapterQuizScreen
import com.siamts.synapse.ui.quadratic.MathematicsNotesScreen
import com.siamts.synapse.ui.quadratic.MathematicsPracticeScreen
import com.siamts.synapse.ui.quadratic.QuadraticLessonPart2Screen
import com.siamts.synapse.ui.quadratic.QuadraticLessonPart2ViewModel
import com.siamts.synapse.ui.quadratic.QuadraticLessonScreen
import com.siamts.synapse.ui.quadratic.QuadraticLessonViewModel
import com.siamts.synapse.ui.quiz.MixedQuizPreviewScreen
import com.siamts.synapse.ui.quiz.QuizIntroPreviewScreen
import com.siamts.synapse.ui.quiz.QuizScreen
import com.siamts.synapse.ui.quiz.QuizViewModel
import com.siamts.synapse.ui.results.QuizResultsScreen
import com.siamts.synapse.ui.results.QuizResultScreen
import com.siamts.synapse.ui.results.QuizResultsViewModel
import com.siamts.synapse.ui.results.QuizReviewAnswersPreviewScreen
import com.siamts.synapse.ui.roadmap.RoadmapScreen
import com.siamts.synapse.ui.roadmap.RoadmapViewModel
import com.siamts.synapse.ui.setup.ProfileSetupScreen
import com.siamts.synapse.ui.setup.ProfileSetupViewModel
import com.siamts.synapse.ui.splash.SplashScreen
import com.siamts.synapse.ui.splash.SplashViewModel
import com.siamts.synapse.ui.subjects.SubjectSelectionScreen
import com.siamts.synapse.ui.subjects.SubjectSelectionViewModel
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.ThemeViewModel
import com.siamts.synapse.data.local.ThemeMode
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import com.siamts.synapse.ui.tutor.AiTutorScreen
import com.siamts.synapse.ui.tutor.AiTutorViewModel
import com.siamts.synapse.ui.waves.WavesLessonPart2Screen
import com.siamts.synapse.ui.waves.WavesLessonPart2ViewModel
import com.siamts.synapse.ui.waves.WavesLessonScreen
import com.siamts.synapse.ui.waves.WavesLessonViewModel
import com.siamts.synapse.ui.welcome.WelcomeContent
import com.siamts.synapse.ui.welcome.WelcomeScreen
import com.siamts.synapse.ui.welcome.WelcomeUiState
import com.siamts.synapse.ui.welcome.WelcomeViewModel

import com.siamts.synapse.ui.auth.ForgotPasswordScreen
import com.siamts.synapse.ui.auth.ForgotPasswordViewModel
import com.siamts.synapse.ui.auth.SignUpScreen
import com.siamts.synapse.ui.auth.SignUpViewModel
import com.siamts.synapse.data.model.QuizResultDataDto
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.ui.results.QuizReviewScreen
import com.siamts.synapse.ui.lesson.ChapterLessonScreen
import com.siamts.synapse.ui.lesson.ChapterLessonViewModel
import com.siamts.synapse.ui.quiz.QuizHomeSelectionScreen
import com.siamts.synapse.ui.quiz.QuizHomeViewModel
import com.siamts.synapse.ui.quiz.SubjectQuizSelectionScreen
import com.siamts.synapse.ui.quiz.SubjectQuizViewModel
import com.siamts.synapse.ui.notes.NotesHomeScreen
import com.siamts.synapse.ui.notes.NotesHomeViewModel
import com.siamts.synapse.ui.notes.NotesListScreen
import com.siamts.synapse.ui.notes.NotesListViewModel
import com.siamts.synapse.ui.subjects.SubjectDetailScreen
import com.siamts.synapse.ui.subjects.SubjectDetailViewModel

sealed class AppScreen {
    data object Splash : AppScreen()
    data object Welcome : AppScreen()
    data object Login : AppScreen()
    data object SignUp : AppScreen()
    data object ForgotPassword : AppScreen()
    data object ProfileSetup : AppScreen()
    data object Dashboard : AppScreen()
    data object BrainMap : AppScreen()
    data object SubjectSelection : AppScreen()
    open class Roadmap(val subjectId: String = "MATH") : AppScreen() {
        companion object : Roadmap("MATH")

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Roadmap) return false
            return subjectId == other.subjectId
        }

        override fun hashCode(): Int = subjectId.hashCode()
    }
    data object QuadraticLesson : AppScreen()
    data object QuadraticLessonPart2 : AppScreen()
    data object MathematicsPractice : AppScreen()
    data object MathematicsNotes : AppScreen()
    data object MathematicsChapterQuiz : AppScreen()
    data object AlgorithmsLesson : AppScreen()
    data object AlgorithmsLessonPart2 : AppScreen()
    data object DataVisualizationLesson : AppScreen()
    data object DataVisualizationLessonPart2 : AppScreen()
    data object WavesLesson : AppScreen()
    data object WavesLessonPart2 : AppScreen()
    data object ChemicalBondingLesson : AppScreen()
    data object ChemicalBondingLessonPart2 : AppScreen()
    data object Lesson : AppScreen()
    data object LessonPart2 : AppScreen()
    data object QuizIntro : AppScreen()
    data object Quiz : AppScreen()
    data object MixedQuiz : AppScreen()
    data object QuizResults : AppScreen()
    data object QuizReviewAnswers : AppScreen()
    data object NotesGenerator : AppScreen()
    data object NoteSavedSuccess : AppScreen()
    data object NoteDownloadedSuccess : AppScreen()
    data object SavedNotes : AppScreen()
    data object MixedSavedNotes : AppScreen()
    data object Profile : AppScreen()
    data object PersonalInfo : AppScreen()
    data object Achievements : AppScreen()
    data object StudyGoals : AppScreen()
    data object LanguageSelection : AppScreen()
    data object NotificationsSettings : AppScreen()
    data object HelpSupport : AppScreen()
    data object AboutSynapse : AppScreen()
    data object WhatsNew : AppScreen()
    data object TermsOfService : AppScreen()
    data object PrivacyPolicy : AppScreen()
    data object OpenSourceLicenses : AppScreen()
    open class ProgressAnalytics(val initialTab: Int = 0) : AppScreen() {
        companion object : ProgressAnalytics(0)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ProgressAnalytics) return false
            return initialTab == other.initialTab
        }

        override fun hashCode(): Int = initialTab
    }
    data object Flashcards : AppScreen()
    open class AiTutor(val initialPrompt: String? = null) : AppScreen() {
        companion object : AiTutor(null)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AiTutor) return false
            return initialPrompt == other.initialPrompt
        }

        override fun hashCode(): Int = initialPrompt?.hashCode() ?: 0
    }
    data object Home : AppScreen()
    data class DynamicChapterLesson(val chapterId: Int, val subjectId: String? = null) : AppScreen()
    data object QuizHomeSelection : AppScreen()
    data class SubjectQuizSelection(val subjectId: Int, val subjectName: String) : AppScreen()
    data class DynamicQuizAttempt(val chapterId: Int, val subjectId: String = "") : AppScreen()
    data class DynamicQuizResults(
        val resultData: QuizResultDataDto,
        val questions: List<QuizQuestionDto> = emptyList(),
        val userAnswers: Map<Int, String> = emptyMap()
    ) : AppScreen()
    data class DynamicQuizReview(
        val resultData: QuizResultDataDto,
        val questions: List<QuizQuestionDto> = emptyList(),
        val userAnswers: Map<Int, String> = emptyMap()
    ) : AppScreen()
    data class QuizResult(
        val subjectId: String,
        val chapterId: String,
        val score: Int,
        val total: Int,
        val questions: List<QuizQuestionDto> = emptyList(),
        val userAnswers: Map<Int, String> = emptyMap()
    ) : AppScreen()
    open class NotesHome(val initialFilter: String = "ALL") : AppScreen() {
        companion object : NotesHome("ALL")

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NotesHome) return false
            return initialFilter == other.initialFilter
        }

        override fun hashCode(): Int = initialFilter.hashCode()
    }
    data class NotesList(val type: String, val subjectId: Int? = null, val title: String = "Notes") : AppScreen()
    data class SubjectDetail(val subjectId: String) : AppScreen()
    data object Settings : AppScreen()
}

class MainActivity : ComponentActivity() {
    private val pendingNotificationPayload = mutableStateOf<String?>(null)

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra("NOTIFICATION_PAYLOAD")?.let { payload ->
            pendingNotificationPayload.value = payload
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        intent?.getStringExtra("NOTIFICATION_PAYLOAD")?.let { payload ->
            pendingNotificationPayload.value = payload
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    SynapseNotificationHelper.scheduleStudyReminders(context)
                    com.siamts.synapse.notification.SmartNotificationScheduler.scheduleAll(context)
                    try {
                        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("synapse_daily_alerts")
                    } catch (_: Exception) { }
                    val prefsRepo = com.siamts.synapse.data.repository.UserPreferencesRepository.getInstance(context)
                    coroutineScope.launch {
                        if (prefsRepo.isStudyReminderEnabled()) {
                            val h = prefsRepo.getStudyReminderHour()
                            val m = prefsRepo.getStudyReminderMinute()
                            com.siamts.synapse.notification.StudyReminderScheduler.scheduleDailyStudyReminder(context, h, m)
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                SynapseNotificationHelper.createNotificationChannel(context)
                val prefsRepo = com.siamts.synapse.data.repository.UserPreferencesRepository.getInstance(context)
                val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else true

                if (isPermissionGranted) {
                    SynapseNotificationHelper.scheduleStudyReminders(context)
                    com.siamts.synapse.notification.SmartNotificationScheduler.scheduleAll(context)
                    try {
                        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("synapse_daily_alerts")
                    } catch (_: Exception) { }
                    if (prefsRepo.isStudyReminderEnabled()) {
                        val h = prefsRepo.getStudyReminderHour()
                        val m = prefsRepo.getStudyReminderMinute()
                        com.siamts.synapse.notification.StudyReminderScheduler.scheduleDailyStudyReminder(context, h, m)
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            SYNAPSETheme(darkTheme = isDark) {
                val view = androidx.compose.ui.platform.LocalView.current
                if (!view.isInEditMode) {
                    androidx.compose.runtime.SideEffect {
                        val window = (view.context as android.app.Activity).window
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            window.isNavigationBarContrastEnforced = false
                            window.isStatusBarContrastEnforced = false
                        }
                        window.navigationBarColor = android.graphics.Color.TRANSPARENT
                        window.statusBarColor = android.graphics.Color.TRANSPARENT
                        val insets = androidx.core.view.WindowCompat.getInsetsController(window, view)
                        insets.isAppearanceLightNavigationBars = !isDark
                        insets.isAppearanceLightStatusBars = !isDark
                    }
                }
                val profileViewModel: ProfileViewModel = viewModel()
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
                val screenBackStack = remember { mutableStateListOf<AppScreen>() }

                fun navigateTo(newScreen: AppScreen) {
                    if (newScreen != currentScreen) {
                        screenBackStack.add(currentScreen)
                        currentScreen = newScreen
                    }
                }

                fun switchTab(tabScreen: AppScreen) {
                    if (tabScreen == currentScreen) return
                    screenBackStack.clear()
                    if (tabScreen != AppScreen.Dashboard) {
                        screenBackStack.add(AppScreen.Dashboard)
                    }
                    currentScreen = tabScreen
                }

                fun handleNotificationRoute(route: String) {
                    when (route) {
                        "quiz_hub" -> switchTab(AppScreen.QuizHomeSelection)
                        "learn" -> switchTab(AppScreen.SubjectSelection)
                        "mistake_vault" -> navigateTo(AppScreen.DynamicQuizAttempt(chapterId = -999, subjectId = "MISTAKE_VAULT"))
                        "home" -> switchTab(AppScreen.Dashboard)
                        "milestone" -> navigateTo(AppScreen.Achievements)
                        else -> {
                            if (route.startsWith("subject/")) {
                                navigateTo(AppScreen.SubjectDetail(route.removePrefix("subject/")))
                            }
                        }
                    }
                }

                LaunchedEffect(pendingNotificationPayload.value) {
                    pendingNotificationPayload.value?.let { payload ->
                        handleNotificationRoute(payload)
                        pendingNotificationPayload.value = null
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        val currentTab = NavGraph.getTabForScreen(currentScreen)
                        if (currentTab != -1) {
                            BottomNavBar(
                                selectedTab = currentTab,
                                onTabSelect = { tabIndex ->
                                    switchTab(NavGraph.getScreenForTab(tabIndex))
                                }
                            )
                        }
                    }
                ) { innerPadding ->

                    fun navigateToQuizResult(resultScreen: AppScreen.QuizResult) {
                        while (screenBackStack.isNotEmpty() && screenBackStack.last() is AppScreen.DynamicQuizAttempt) {
                            screenBackStack.removeAt(screenBackStack.size - 1)
                        }
                        currentScreen = resultScreen
                    }

                    fun navigateBack() {
                        if (screenBackStack.isNotEmpty()) {
                            currentScreen = screenBackStack.removeAt(screenBackStack.size - 1)
                        } else {
                            currentScreen = when (currentScreen) {
                                is AppScreen.NotesList -> AppScreen.NotesHome
                                is AppScreen.NotesHome -> AppScreen.Dashboard
                                is AppScreen.MixedSavedNotes -> AppScreen.NotesHome
                                is AppScreen.SavedNotes -> AppScreen.NotesHome
                                is AppScreen.NotesGenerator -> AppScreen.NotesHome
                                is AppScreen.Profile -> AppScreen.Dashboard
                                is AppScreen.Settings -> AppScreen.Profile
                                is AppScreen.PersonalInfo -> AppScreen.Profile
                                is AppScreen.Achievements -> AppScreen.Profile
                                is AppScreen.StudyGoals -> AppScreen.Profile
                                is AppScreen.LanguageSelection -> AppScreen.Profile
                                is AppScreen.NotificationsSettings -> AppScreen.Profile
                                is AppScreen.HelpSupport -> AppScreen.Profile
                                is AppScreen.AboutSynapse -> AppScreen.Profile
                                is AppScreen.WhatsNew -> AppScreen.AboutSynapse
                                is AppScreen.TermsOfService -> AppScreen.AboutSynapse
                                is AppScreen.PrivacyPolicy -> AppScreen.AboutSynapse
                                is AppScreen.OpenSourceLicenses -> AppScreen.AboutSynapse
                                is AppScreen.ProgressAnalytics -> AppScreen.Dashboard
                                is AppScreen.AiTutor -> AppScreen.Dashboard
                                is AppScreen.QuizHomeSelection -> AppScreen.Dashboard
                                is AppScreen.SubjectQuizSelection -> AppScreen.QuizHomeSelection
                                is AppScreen.DynamicQuizAttempt -> {
                                    val sub = (currentScreen as AppScreen.DynamicQuizAttempt).subjectId
                                    val sId = sub.toIntOrNull()
                                    if (sId != null && sId > 0) {
                                        AppScreen.SubjectQuizSelection(sId, "Subject $sId")
                                    } else {
                                        AppScreen.QuizHomeSelection
                                    }
                                }
                                is AppScreen.DynamicQuizResults -> AppScreen.QuizHomeSelection
                                is AppScreen.DynamicQuizReview -> AppScreen.QuizHomeSelection
                                is AppScreen.QuizResult -> {
                                    val backSub = (currentScreen as AppScreen.QuizResult).subjectId
                                    AppScreen.Roadmap(backSub)
                                }
                                is AppScreen.Quiz -> AppScreen.Dashboard
                                is AppScreen.MixedQuiz -> AppScreen.Dashboard
                                is AppScreen.QuizResults -> AppScreen.Dashboard
                                is AppScreen.QuizReviewAnswers -> AppScreen.Dashboard
                                is AppScreen.Roadmap -> AppScreen.SubjectSelection
                                is AppScreen.SubjectSelection -> AppScreen.Dashboard
                                is AppScreen.BrainMap -> AppScreen.Dashboard
                                is AppScreen.Flashcards -> AppScreen.Dashboard
                                is AppScreen.QuadraticLessonPart2 -> AppScreen.QuadraticLesson
                                is AppScreen.QuadraticLesson -> AppScreen.Roadmap
                                is AppScreen.MathematicsPractice -> AppScreen.QuadraticLessonPart2
                                is AppScreen.MathematicsNotes -> AppScreen.QuadraticLesson
                                is AppScreen.MathematicsChapterQuiz -> AppScreen.QuadraticLesson
                                is AppScreen.AlgorithmsLessonPart2 -> AppScreen.AlgorithmsLesson
                                is AppScreen.AlgorithmsLesson -> AppScreen.Roadmap
                                is AppScreen.DataVisualizationLessonPart2 -> AppScreen.DataVisualizationLesson
                                is AppScreen.DataVisualizationLesson -> AppScreen.Roadmap
                                is AppScreen.WavesLessonPart2 -> AppScreen.WavesLesson
                                is AppScreen.WavesLesson -> AppScreen.Roadmap
                                is AppScreen.ChemicalBondingLessonPart2 -> AppScreen.ChemicalBondingLesson
                                is AppScreen.ChemicalBondingLesson -> AppScreen.Roadmap
                                is AppScreen.LessonPart2 -> AppScreen.Lesson
                                is AppScreen.Lesson -> AppScreen.Roadmap
                                is AppScreen.DynamicChapterLesson -> AppScreen.Roadmap
                                is AppScreen.SubjectDetail -> AppScreen.SubjectSelection
                                is AppScreen.Home -> AppScreen.Dashboard
                                else -> AppScreen.Dashboard
                            }
                        }
                    }

                    fun popBackStack() {
                        navigateBack()
                    }

                    fun navigateToRoute(route: String) {
                        if (route.startsWith("quiz_result_screen/")) {
                            val parts = route.removePrefix("quiz_result_screen/").split("/")
                            if (parts.size >= 4) {
                                val subId = parts[0]
                                val chapId = parts[1]
                                val score = parts[2].toIntOrNull() ?: 0
                                val total = parts[3].toIntOrNull() ?: 5
                                navigateToQuizResult(AppScreen.QuizResult(subId, chapId, score, total))
                            }
                        } else if (route.startsWith("roadmap_screen/")) {
                            val subId = route.removePrefix("roadmap_screen/")
                            navigateTo(AppScreen.Roadmap(subId))
                        } else if (route.startsWith("lesson_screen/") || route.startsWith("lesson_content/")) {
                            val clean = if (route.startsWith("lesson_screen/")) route.removePrefix("lesson_screen/") else route.removePrefix("lesson_content/")
                            val parts = clean.split("/")
                            if (parts.size == 1) {
                                val cid = parts[0].toIntOrNull() ?: 1
                                val resolvedSubId = BaseCurriculumData.getChapterById(cid)?.let {
                                    BaseCurriculumData.subjects.firstOrNull { s -> s.chapters.any { ch -> ch.chapterId == cid } }?.id
                                } ?: com.siamts.synapse.data.repository.SubjectCatalogRepository.getInstanceOrNull()?.getAllSubjects()?.firstOrNull { s -> s.chapters.any { ch -> ch.chapterId == cid } }?.id
                                navigateTo(AppScreen.DynamicChapterLesson(chapterId = cid, subjectId = resolvedSubId))
                            } else if (parts.size >= 2) {
                                val subId = parts[0]
                                val chapId = parts[1].toIntOrNull()
                                    ?: BaseCurriculumData.getSubjectById(subId)?.chapters?.firstOrNull()?.chapterId
                                    ?: 1
                                navigateTo(AppScreen.DynamicChapterLesson(chapterId = chapId, subjectId = subId))
                            }
                        } else if (route == "quiz_hub_screen" || route == "quiz_home" || route == "quiz") {
                            navigateTo(AppScreen.QuizHomeSelection)
                        } else if (route.startsWith("quiz_screen/") || route.startsWith("quiz/")) {
                            val clean = if (route.startsWith("quiz_screen/")) route.removePrefix("quiz_screen/") else route.removePrefix("quiz/")
                            val parts = clean.split("/")
                            if (parts.size >= 2) {
                                val subId = parts[0]
                                val chapId = parts[1].toIntOrNull() ?: 1
                                navigateTo(AppScreen.DynamicQuizAttempt(chapterId = chapId, subjectId = subId))
                            } else {
                                val chapId = parts.firstOrNull()?.toIntOrNull() ?: 1
                                navigateTo(AppScreen.DynamicQuizAttempt(chapterId = chapId, subjectId = ""))
                            }
                        } else if (route == "add_subject_screen") {
                            navigateTo(AppScreen.SubjectSelection)
                        }
                    }

                    BackHandler(enabled = currentScreen != AppScreen.Dashboard && currentScreen != AppScreen.Login && currentScreen != AppScreen.Welcome && currentScreen != AppScreen.Splash) {
                        navigateBack()
                    }

                    BackHandler(enabled = currentScreen == AppScreen.Login || currentScreen == AppScreen.Dashboard || currentScreen == AppScreen.Welcome) {
                        finish()
                    }

                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(durationMillis = 120),
                        label = "ScreenTransition",
                    ) { screen ->
                        when (screen) {
                            AppScreen.Splash -> {
                                val splashViewModel: SplashViewModel = viewModel()
                                SplashScreen(
                                    viewModel = splashViewModel,
                                    onSplashFinished = { isAuthenticated ->
                                        screenBackStack.clear()
                                        currentScreen = if (isAuthenticated) AppScreen.Dashboard else AppScreen.Login
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            AppScreen.Welcome -> {
                                val welcomeViewModel: WelcomeViewModel = viewModel()
                                WelcomeScreen(
                                    viewModel = welcomeViewModel,
                                    onStartClick = {
                                        screenBackStack.clear()
                                        val sessionManager = com.siamts.synapse.data.local.SessionManager.getInstanceOrNull()
                                        val isAuthenticated = sessionManager?.isLoggedInCached() == true
                                        currentScreen = if (isAuthenticated) AppScreen.Dashboard else AppScreen.Login
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Login -> {
                                val loginViewModel: LoginViewModel = viewModel()
                                LoginScreen(
                                    viewModel = loginViewModel,
                                    onLoginSuccess = {
                                        screenBackStack.clear()
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onSignUpClick = {
                                        currentScreen = AppScreen.SignUp
                                    },
                                    onForgotPasswordClick = {
                                        currentScreen = AppScreen.ForgotPassword
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.SignUp -> {
                                val signUpViewModel: SignUpViewModel = viewModel()
                                SignUpScreen(
                                    viewModel = signUpViewModel,
                                    onSignUpSuccess = {
                                        screenBackStack.clear()
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onSignInClick = {
                                        currentScreen = AppScreen.Login
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.ForgotPassword -> {
                                val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
                                ForgotPasswordScreen(
                                    viewModel = forgotPasswordViewModel,
                                    onBackToLoginClick = {
                                        currentScreen = AppScreen.Login
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.ProfileSetup -> {
                                val profileSetupViewModel: ProfileSetupViewModel = viewModel()
                                ProfileSetupScreen(
                                    viewModel = profileSetupViewModel,
                                    onContinueClick = {
                                        profileSetupViewModel.completeSetup(
                                            onSuccess = {
                                                currentScreen = AppScreen.Dashboard
                                            }
                                        )
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Login
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Dashboard -> {
                                val dashboardViewModel: DashboardViewModel = viewModel()
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onAiChatQuerySubmit = { prompt ->
                                        navigateTo(AppScreen.AiTutor(prompt))
                                    },
                                    onLearnTabClick = {
                                        switchTab(AppScreen.SubjectSelection)
                                    },
                                    onBrainMapClick = {
                                        navigateTo(AppScreen.BrainMap)
                                    },
                                    onFlashcardsClick = {
                                        navigateTo(AppScreen.Flashcards)
                                    },
                                    onQuizTabClick = {
                                        switchTab(AppScreen.QuizHomeSelection)
                                    },
                                    onNotesTabClick = {
                                        switchTab(AppScreen.NotesHome)
                                    },
                                    onProfileTabClick = {
                                        switchTab(AppScreen.Profile)
                                    },
                                    onNotificationClick = {
                                        navigateTo(AppScreen.NotificationsSettings)
                                    },
                                    onNotificationActionRoute = { route ->
                                        handleNotificationRoute(route)
                                    },
                                    onProgressAnalyticsClick = {
                                        navigateTo(AppScreen.ProgressAnalytics())
                                    },
                                    onToggleTheme = {
                                        themeViewModel.toggleTheme()
                                    },
                                    onContinueLessonClick = { subjectSlug, _ ->
                                        when (subjectSlug.lowercase()) {
                                            "chemistry" -> currentScreen = AppScreen.ChemicalBondingLesson
                                            "physics" -> currentScreen = AppScreen.WavesLesson
                                            "data-science" -> currentScreen = AppScreen.DataVisualizationLesson
                                            "computer-science", "algorithms" -> currentScreen = AppScreen.AlgorithmsLesson
                                            "mathematics" -> currentScreen = AppScreen.QuadraticLesson
                                            else -> currentScreen = AppScreen.Lesson
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.BrainMap -> {
                                val brainMapViewModel: BrainMapViewModel = viewModel()
                                BrainMapScreen(
                                    viewModel = brainMapViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onTabSelect = { tabIndex ->
                                        when (tabIndex) {
                                            0 -> switchTab(AppScreen.Dashboard)
                                            1 -> switchTab(AppScreen.SubjectSelection)
                                            2 -> switchTab(AppScreen.QuizHomeSelection)
                                            3 -> switchTab(AppScreen.NotesHome)
                                            4 -> switchTab(AppScreen.Profile)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.SubjectSelection -> {
                                val subjectSelectionViewModel: SubjectSelectionViewModel = viewModel()
                                SubjectSelectionScreen(
                                    viewModel = subjectSelectionViewModel,
                                    onSubjectClick = { subjectId ->
                                        navigateTo(AppScreen.Roadmap(subjectId))
                                    },
                                    onChapterClick = { chapterId ->
                                        navigateTo(AppScreen.DynamicChapterLesson(chapterId))
                                    },
                                    onTabSelect = { tabIndex ->
                                        when (tabIndex) {
                                            0 -> switchTab(AppScreen.Dashboard)
                                            1 -> switchTab(AppScreen.SubjectSelection)
                                            2 -> switchTab(AppScreen.QuizHomeSelection)
                                            3 -> switchTab(AppScreen.NotesHome)
                                            4 -> switchTab(AppScreen.Profile)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.SubjectDetail -> {
                                val subjectDetailViewModel: SubjectDetailViewModel = viewModel()
                                LaunchedEffect(screen.subjectId) {
                                    subjectDetailViewModel.loadSubject(screen.subjectId)
                                }
                                SubjectDetailScreen(
                                    subjectId = screen.subjectId,
                                    viewModel = subjectDetailViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onChapterClick = { chapterId ->
                                        navigateTo(AppScreen.DynamicChapterLesson(chapterId = chapterId, subjectId = screen.subjectId))
                                    },
                                    onQuizClick = { chapterId ->
                                        navigateTo(AppScreen.DynamicQuizAttempt(chapterId = chapterId, subjectId = screen.subjectId))
                                    },
                                    onNotesClick = { subId, subName ->
                                        navigateTo(AppScreen.NotesList(type = "ALL", subjectId = subId, title = "$subName Notes"))
                                    },
                                    onRoadmapClick = { _ ->
                                        navigateTo(AppScreen.Roadmap(screen.subjectId))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.Roadmap -> {
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                LaunchedEffect(screen.subjectId) {
                                    roadmapViewModel.loadSubjectRoadmap(screen.subjectId)
                                }
                                RoadmapScreen(
                                    viewModel = roadmapViewModel,
                                    onChapterClick = { chapterId ->
                                        navigateTo(AppScreen.DynamicChapterLesson(chapterId = chapterId, subjectId = screen.subjectId))
                                    },
                                    onContinueClick = {
                                        val activeId = roadmapViewModel.uiState.value.activeChapterId
                                        if (activeId > 0) {
                                            navigateTo(AppScreen.DynamicChapterLesson(chapterId = activeId, subjectId = screen.subjectId))
                                        } else {
                                            val firstId = roadmapViewModel.uiState.value.milestones.firstOrNull()?.chapterId
                                                ?: BaseCurriculumData.getSubjectById(screen.subjectId)?.chapters?.firstOrNull()?.chapterId
                                                ?: 1
                                            navigateTo(AppScreen.DynamicChapterLesson(chapterId = firstId, subjectId = screen.subjectId))
                                        }
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.DynamicChapterLesson -> {
                                val chapterLessonViewModel: ChapterLessonViewModel = viewModel()
                                LaunchedEffect(screen.chapterId, screen.subjectId) {
                                    chapterLessonViewModel.loadChapterLesson(screen.chapterId, screen.subjectId)
                                }
                                ChapterLessonScreen(
                                    chapterId = screen.chapterId,
                                    subjectId = screen.subjectId,
                                    viewModel = chapterLessonViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onNavigateToChapter = { targetId ->
                                        navigateTo(AppScreen.DynamicChapterLesson(chapterId = targetId, subjectId = screen.subjectId))
                                    },
                                    onTakeQuiz = { targetId ->
                                        navigateTo(AppScreen.DynamicQuizAttempt(chapterId = targetId, subjectId = screen.subjectId ?: ""))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuadraticLesson -> {
                                val quadraticLessonViewModel: QuadraticLessonViewModel = viewModel()
                                QuadraticLessonScreen(
                                    viewModel = quadraticLessonViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(1))
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.QuadraticLessonPart2)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuadraticLessonPart2 -> {
                                val quadraticLessonPart2ViewModel: QuadraticLessonPart2ViewModel = viewModel()
                                QuadraticLessonPart2Screen(
                                    viewModel = quadraticLessonPart2ViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.MathematicsPractice)
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.MathematicsPractice -> {
                                val mathPracticeViewModel: MathPracticeViewModel = viewModel()
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                val currentSubject by roadmapViewModel.uiState.collectAsStateWithLifecycle()

                                LaunchedEffect(currentSubject.subjectName) {
                                    mathPracticeViewModel.loadSubjectPractice(currentSubject.subjectName)
                                }

                                MathematicsPracticeScreen(
                                    viewModel = mathPracticeViewModel,
                                    onViewSolutionsClick = {
                                        navigateTo(AppScreen.MathematicsChapterQuiz)
                                    },
                                    onLearnTabClick = {
                                        switchTab(AppScreen.SubjectSelection)
                                    },
                                    onNotesTabClick = {
                                        switchTab(AppScreen.NotesHome)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.MathematicsNotes -> {
                                val mathNotesViewModel: MathNotesViewModel = viewModel()
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                val currentSubject by roadmapViewModel.uiState.collectAsStateWithLifecycle()

                                LaunchedEffect(currentSubject.subjectName) {
                                    mathNotesViewModel.loadSubjectNotes(currentSubject.subjectName)
                                }

                                MathematicsNotesScreen(
                                    viewModel = mathNotesViewModel,
                                    onDownloadNotesClick = {
                                        navigateTo(AppScreen.NoteDownloadedSuccess)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.MathematicsPractice)
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onLearnTabClick = {
                                        switchTab(AppScreen.SubjectSelection)
                                    },
                                    onQuizTabClick = {
                                        switchTab(AppScreen.QuizHomeSelection)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.MathematicsChapterQuiz -> {
                                val mathChapterQuizViewModel: MathChapterQuizViewModel = viewModel()
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                val currentSubject by roadmapViewModel.uiState.collectAsStateWithLifecycle()

                                LaunchedEffect(currentSubject.subjectName) {
                                    mathChapterQuizViewModel.loadSubjectQuiz(currentSubject.subjectName)
                                }

                                MathematicsChapterQuizScreen(
                                    viewModel = mathChapterQuizViewModel,
                                    onStartQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(1))
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onLearnTabClick = {
                                        switchTab(AppScreen.SubjectSelection)
                                    },
                                    onNotesTabClick = {
                                        switchTab(AppScreen.NotesHome)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.AlgorithmsLesson -> {
                                val algorithmsLessonViewModel: AlgorithmsLessonViewModel = viewModel()
                                AlgorithmsLessonScreen(
                                    viewModel = algorithmsLessonViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(2))
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.AlgorithmsLessonPart2)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.AlgorithmsLessonPart2 -> {
                                val algorithmsLessonPart2ViewModel: AlgorithmsLessonPart2ViewModel = viewModel()
                                AlgorithmsLessonPart2Screen(
                                    viewModel = algorithmsLessonPart2ViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.MathematicsPractice)
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.DataVisualizationLesson -> {
                                val dataVisViewModel: DataVisualizationLessonViewModel = viewModel()
                                DataVisualizationLessonScreen(
                                    viewModel = dataVisViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(3))
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.DataVisualizationLessonPart2)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.DataVisualizationLessonPart2 -> {
                                val dataVisPart2ViewModel: DataVisualizationLessonPart2ViewModel = viewModel()
                                DataVisualizationLessonPart2Screen(
                                    viewModel = dataVisPart2ViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.MathematicsPractice)
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.WavesLesson -> {
                                val wavesViewModel: WavesLessonViewModel = viewModel()
                                WavesLessonScreen(
                                    viewModel = wavesViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(5))
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.WavesLessonPart2)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.WavesLessonPart2 -> {
                                val wavesPart2ViewModel: WavesLessonPart2ViewModel = viewModel()
                                WavesLessonPart2Screen(
                                    viewModel = wavesPart2ViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.MathematicsPractice)
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.ChemicalBondingLesson -> {
                                val chemistryViewModel: ChemicalBondingLessonViewModel = viewModel()
                                ChemicalBondingLessonScreen(
                                    viewModel = chemistryViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(6))
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.ChemicalBondingLessonPart2)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.ChemicalBondingLessonPart2 -> {
                                val chemistryPart2ViewModel: ChemicalBondingLessonPart2ViewModel = viewModel()
                                ChemicalBondingLessonPart2Screen(
                                    viewModel = chemistryPart2ViewModel,
                                    onNotesClick = {
                                        navigateTo(AppScreen.MathematicsNotes)
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNextClick = {
                                        navigateTo(AppScreen.MathematicsPractice)
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Lesson -> {
                                val lessonViewModel: LessonViewModel = viewModel()
                                LessonScreen(
                                    viewModel = lessonViewModel,
                                    onNextClick = {
                                        navigateTo(AppScreen.LessonPart2)
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onQuizClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(lessonViewModel.uiState.value.chapterId))
                                    },
                                    onNotesClick = {
                                        navigateTo(
                                            AppScreen.NotesList(
                                                type = "subject",
                                                subjectId = lessonViewModel.uiState.value.subjectId,
                                                title = "${lessonViewModel.uiState.value.subjectTag} Notes"
                                            )
                                        )
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.LessonPart2 -> {
                                val lessonPart2ViewModel: LessonPart2ViewModel = viewModel()
                                LessonPart2Screen(
                                    viewModel = lessonPart2ViewModel,
                                    onNextClick = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(lessonPart2ViewModel.uiState.value.chapterId))
                                    },
                                    onPreviousClick = {
                                        popBackStack()
                                    },
                                    onAskAiClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onNotesClick = {
                                        navigateTo(
                                            AppScreen.NotesList(
                                                type = "subject",
                                                subjectId = lessonPart2ViewModel.uiState.value.subjectId,
                                                title = "${lessonPart2ViewModel.uiState.value.subjectTag} Notes"
                                            )
                                        )
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuizIntro -> {
                                QuizIntroPreviewScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onStartQuizClick = {
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
                                        } else if (tabIndex == 1) {
                                            currentScreen = AppScreen.SubjectSelection
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.QuizHomeSelection
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.NotesHome
                                        } else if (tabIndex == 4) {
                                            currentScreen = AppScreen.Profile
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Quiz -> {
                                val quizViewModel: QuizViewModel = viewModel()
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                val currentSubject by roadmapViewModel.uiState.collectAsStateWithLifecycle()

                                LaunchedEffect(currentSubject.subjectName) {
                                    quizViewModel.loadSubjectQuiz(currentSubject.subjectName)
                                }

                                QuizScreen(
                                    viewModel = quizViewModel,
                                    onNavigateToResult = { subId, chapId, score, total, _ ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        navigateToQuizResult(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
                                    },
                                    onQuizComplete = { result ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        val score = result?.score ?: 0
                                        val total = result?.totalQuestions ?: qList.size.coerceAtLeast(1)
                                        val subId = quizViewModel.currentSubjectId
                                        val chapId = quizViewModel.currentChapterId
                                        navigateToQuizResult(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.MixedQuiz -> {
                                MixedQuizPreviewScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onSubmitQuizClick = {
                                        navigateTo(AppScreen.QuizResults)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuizResults -> {
                                QuizResultScreen(
                                    subjectId = "MATH",
                                    chapterId = "4",
                                    score = 4,
                                    total = 5,
                                    onReviewAnswers = {
                                        navigateTo(AppScreen.QuizReviewAnswers)
                                    },
                                    onRetakeQuiz = {
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onBackToRoadmap = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuizReviewAnswers -> {
                                QuizReviewAnswersPreviewScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onRetryQuizClick = {
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onDashboardClick = {
                                        switchTab(AppScreen.Dashboard)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.NotesGenerator -> {
                                val notesGeneratorViewModel: NotesGeneratorViewModel = viewModel()
                                NotesGeneratorScreen(
                                    viewModel = notesGeneratorViewModel,
                                    onOpenSavedNotes = {
                                        navigateTo(AppScreen.NoteSavedSuccess)
                                    },
                                    onDownloadNotes = {
                                        navigateTo(AppScreen.NoteDownloadedSuccess)
                                    },
                                    onTabSelect = { tabIndex ->
                                        when (tabIndex) {
                                            0 -> switchTab(AppScreen.Dashboard)
                                            1 -> switchTab(AppScreen.SubjectSelection)
                                            2 -> switchTab(AppScreen.QuizHomeSelection)
                                            3 -> switchTab(AppScreen.NotesHome)
                                            4 -> switchTab(AppScreen.Profile)
                                        }
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.NoteSavedSuccess -> {
                                val noteSuccessViewModel: NoteSuccessViewModel = viewModel()
                                NoteSavedSuccessScreen(
                                    viewModel = noteSuccessViewModel,
                                    onViewSavedNotesClick = {
                                        navigateTo(AppScreen.MixedSavedNotes)
                                    },
                                    onBackToLearningClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.NoteDownloadedSuccess -> {
                                val noteSuccessViewModel: NoteSuccessViewModel = viewModel()
                                NoteDownloadedSuccessScreen(
                                    viewModel = noteSuccessViewModel,
                                    onOpenDownloadedNotesClick = {
                                        navigateTo(AppScreen.MixedSavedNotes)
                                    },
                                    onBackToLearningClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.SavedNotes -> {
                                val savedNotesViewModel: SavedNotesViewModel = viewModel()
                                SavedNotesScreen(
                                    viewModel = savedNotesViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onTabSelect = { tabIndex ->
                                        when (tabIndex) {
                                            0 -> switchTab(AppScreen.Dashboard)
                                            1 -> switchTab(AppScreen.SubjectSelection)
                                            2 -> switchTab(AppScreen.QuizHomeSelection)
                                            3 -> switchTab(AppScreen.NotesHome)
                                            4 -> switchTab(AppScreen.Profile)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.MixedSavedNotes -> {
                                MixedSavedDownloadedNotesPreviewScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onGenerateNewNotesClick = {
                                        navigateTo(AppScreen.NotesGenerator)
                                    },
                                    onSettingsClick = {
                                        navigateTo(AppScreen.NotificationsSettings)
                                    },
                                    onReadNoteClick = { subjectName ->
                                        navigateTo(AppScreen.SubjectDetail(subjectName))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Profile -> {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onOpenSavedNotesClick = {
                                        navigateTo(AppScreen.NotesHome("SAVED"))
                                    },
                                    onOpenDownloadedNotesClick = {
                                        navigateTo(AppScreen.NotesHome("DOWNLOADED"))
                                    },
                                    onSettingsClick = {
                                        navigateTo(AppScreen.Settings)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onLogoutClick = {
                                        profileViewModel.logout {
                                            screenBackStack.clear()
                                            currentScreen = AppScreen.Login
                                        }
                                    },
                                    onRemoveProfilePictureClick = {
                                        profileViewModel.removeProfilePicture()
                                    },
                                    onMyProfileClick = {
                                        navigateTo(AppScreen.PersonalInfo)
                                    },
                                    onAchievementsClick = {
                                        navigateTo(AppScreen.Achievements)
                                    },
                                    onStudyGoalsClick = {
                                        navigateTo(AppScreen.StudyGoals)
                                    },
                                    onLanguageClick = {
                                        navigateTo(AppScreen.LanguageSelection)
                                    },
                                    onNotificationsClick = {
                                        navigateTo(AppScreen.NotificationsSettings)
                                    },
                                    onHelpSupportClick = {
                                        navigateTo(AppScreen.HelpSupport)
                                    },
                                    onAboutSynapseClick = {
                                        navigateTo(AppScreen.AboutSynapse)
                                    },
                                    onTabSelect = { tabIndex ->
                                        when (tabIndex) {
                                            0 -> switchTab(AppScreen.Dashboard)
                                            1 -> switchTab(AppScreen.SubjectSelection)
                                            2 -> switchTab(AppScreen.QuizHomeSelection)
                                            3 -> switchTab(AppScreen.NotesHome)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.PersonalInfo -> {
                                val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

                                PersonalInfoScreen(
                                    initialName = profileUiState.userName,
                                    initialEmail = profileUiState.userEmail,
                                    initialPhone = profileUiState.userPhone,
                                    initialBio = profileUiState.userBio,
                                    initialPhotoUri = profileUiState.profilePhotoUri,
                                    isSaving = profileUiState.isUpdating,
                                    errorMessage = profileUiState.errorMessage,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onSaveChangesClick = { newName, newEmail, newPhone, newBio, newPhotoUri, removePhoto ->
                                        profileViewModel.updateProfile(
                                            name = newName,
                                            email = newEmail,
                                            phone = newPhone,
                                            bio = newBio,
                                            photoUri = newPhotoUri,
                                            removePhoto = removePhoto,
                                            onComplete = { success ->
                                                if (success) {
                                                    popBackStack()
                                                }
                                            }
                                        )
                                    },
                                    onRemoveProfilePictureClick = {
                                        profileViewModel.removeProfilePicture()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Achievements -> {
                                AchievementsScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.StudyGoals -> {
                                val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
                                StudyGoalsScreen(
                                    initialGoal = profileUiState.studyGoalText,
                                    onGoalSelected = { newGoal ->
                                        profileViewModel.updateStudyGoal(newGoal)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.LanguageSelection -> {
                                val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
                                LanguageSelectionScreen(
                                    initialLanguage = profileUiState.languageText,
                                    onLanguageSelected = { newLang ->
                                        profileViewModel.updatePreferredLanguage(newLang)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.NotificationsSettings -> {
                                val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
                                NotificationsSettingsScreen(
                                    initialEnabled = profileUiState.notificationsEnabled,
                                    onToggleNotifications = { enabled ->
                                        profileViewModel.onToggleNotifications(enabled)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.HelpSupport -> {
                                HelpSupportScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.AboutSynapse -> {
                                AboutSynapseScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onWhatsNewClick = {
                                        navigateTo(AppScreen.WhatsNew)
                                    },
                                    onTermsOfServiceClick = {
                                        navigateTo(AppScreen.TermsOfService)
                                    },
                                    onPrivacyPolicyClick = {
                                        navigateTo(AppScreen.PrivacyPolicy)
                                    },
                                    onOpenSourceLicensesClick = {
                                        navigateTo(AppScreen.OpenSourceLicenses)
                                    },
                                    onNavigateToRoute = { route ->
                                        when (route) {
                                            AboutRoutes.WHATS_NEW -> navigateTo(AppScreen.WhatsNew)
                                            AboutRoutes.TERMS_OF_SERVICE -> navigateTo(AppScreen.TermsOfService)
                                            AboutRoutes.PRIVACY_POLICY -> navigateTo(AppScreen.PrivacyPolicy)
                                            AboutRoutes.OPEN_SOURCE_LICENSES -> navigateTo(AppScreen.OpenSourceLicenses)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.WhatsNew -> {
                                WhatsNewScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.TermsOfService -> {
                                TermsOfServiceScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.PrivacyPolicy -> {
                                PrivacyPolicyScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.OpenSourceLicenses -> {
                                OpenSourceLicensesScreen(
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.ProgressAnalytics -> {
                                ProgressAnalyticsScreen(
                                    initialTab = (currentScreen as? AppScreen.ProgressAnalytics)?.initialTab ?: 0,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onTrackClick = { trackSlug ->
                                        navigateTo(AppScreen.SubjectDetail(trackSlug))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Flashcards -> {
                                val flashcardsViewModel: FlashcardsViewModel = viewModel()
                                FlashcardsScreen(
                                    viewModel = flashcardsViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.AiTutor -> {
                                val tutorScreen = currentScreen as? AppScreen.AiTutor
                                val aiTutorViewModel: AiTutorViewModel = viewModel()
                                LaunchedEffect(tutorScreen?.initialPrompt) {
                                    val prompt = tutorScreen?.initialPrompt
                                    if (!prompt.isNullOrBlank()) {
                                        aiTutorViewModel.onSendMessage(prompt)
                                    }
                                }
                                AiTutorScreen(
                                    viewModel = aiTutorViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onNavigateToHome = {
                                        switchTab(AppScreen.Home)
                                    },
                                    onNavigateToLearn = {
                                        switchTab(AppScreen.SubjectSelection)
                                    },
                                    onNavigateToQuiz = { subjectId, subjectName ->
                                        val sId = subjectId?.toIntOrNull()
                                        if (sId != null && !subjectName.isNullOrBlank()) {
                                            navigateTo(AppScreen.SubjectQuizSelection(sId, subjectName))
                                        } else {
                                            navigateTo(AppScreen.QuizHomeSelection)
                                        }
                                    },
                                    onNavigateToSubject = { subjectId ->
                                        navigateTo(AppScreen.Roadmap(subjectId))
                                    },
                                    onNavigateToNotes = {
                                        navigateTo(AppScreen.NotesHome())
                                    },
                                    onNavigateToProfile = {
                                        navigateTo(AppScreen.Profile)
                                    },
                                    onNavigateToProgress = {
                                        navigateTo(AppScreen.ProgressAnalytics())
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Home -> {
                                val homeViewModel: HomeViewModel = viewModel()
                                HomeScreen(
                                    viewModel = homeViewModel,
                                    onSubjectClick = { slug ->
                                        navigateTo(AppScreen.SubjectDetail(slug))
                                    },
                                    onContinueLessonClick = { subjectSlug, _ ->
                                        navigateTo(AppScreen.SubjectDetail(subjectSlug))
                                    },
                                    onAiChatClick = {
                                        navigateTo(AppScreen.AiTutor)
                                    },
                                    onAiChatQuerySubmit = { prompt ->
                                        navigateTo(AppScreen.AiTutor(prompt))
                                    },
                                    onFlashcardsClick = {
                                        navigateTo(AppScreen.Flashcards)
                                    },
                                    onQuizClick = {
                                        switchTab(AppScreen.QuizHomeSelection)
                                    },
                                    onNotesClick = {
                                        switchTab(AppScreen.NotesHome)
                                    },
                                    onProfileClick = {
                                        switchTab(AppScreen.Profile)
                                    },
                                    onBrainMapClick = {
                                        navigateTo(AppScreen.BrainMap)
                                    },
                                    onProgressAnalyticsClick = {
                                        navigateTo(AppScreen.ProgressAnalytics())
                                    },
                                    onStreakCalendarClick = {
                                        navigateTo(AppScreen.ProgressAnalytics(3))
                                    },
                                    onToggleTheme = {
                                        themeViewModel.toggleTheme()
                                    },
                                    onNotificationActionRoute = { route ->
                                        handleNotificationRoute(route)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuizHomeSelection -> {
                                val quizHomeViewModel: QuizHomeViewModel = viewModel()
                                QuizHomeSelectionScreen(
                                    viewModel = quizHomeViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onSelectSubject = { subId, subName ->
                                        navigateTo(AppScreen.SubjectQuizSelection(subId, subName))
                                    },
                                    onOpenMistakeVault = {
                                        navigateTo(AppScreen.DynamicQuizAttempt(chapterId = -999, subjectId = "MISTAKE_VAULT"))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.SubjectQuizSelection -> {
                                val subjectQuizViewModel: SubjectQuizViewModel = viewModel()
                                SubjectQuizSelectionScreen(
                                    subjectId = screen.subjectId,
                                    subjectName = screen.subjectName,
                                    viewModel = subjectQuizViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onStartQuiz = { chapterId, subId ->
                                        navigateTo(AppScreen.DynamicQuizAttempt(chapterId = chapterId, subjectId = subId))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.DynamicQuizAttempt -> {
                                val quizViewModel: QuizViewModel = viewModel()
                                LaunchedEffect(screen.chapterId, screen.subjectId) {
                                    quizViewModel.loadChapterQuiz(chapterId = screen.chapterId, subjectId = screen.subjectId)
                                }
                                QuizScreen(
                                    viewModel = quizViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onRetryClick = {
                                        quizViewModel.loadChapterQuiz(chapterId = screen.chapterId, subjectId = screen.subjectId)
                                    },
                                    onNavigateToResult = { subId, chapId, score, total, _ ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        navigateToQuizResult(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
                                    },
                                    onQuizComplete = { result ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        val score = result?.score ?: 0
                                        val total = result?.totalQuestions ?: qList.size.coerceAtLeast(1)
                                        val subId = quizViewModel.currentSubjectId
                                        val chapId = quizViewModel.currentChapterId
                                        navigateToQuizResult(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.QuizResult -> {
                                val sub = BaseCurriculumData.getSubjectById(screen.subjectId)
                                val chap = BaseCurriculumData.getChapterById(screen.chapterId.toIntOrNull() ?: 4)
                                QuizResultScreen(
                                    subjectId = screen.subjectId,
                                    chapterId = screen.chapterId,
                                    score = screen.score,
                                    total = screen.total,
                                    subjectTitle = sub?.name,
                                    chapterTitle = chap?.title,
                                    onReviewAnswers = {
                                        val qList: List<QuizQuestionDto> = if (screen.questions.isNotEmpty()) screen.questions else (chap?.mcqs ?: emptyList())
                                        val resData = QuizResultDataDto(
                                            studentName = "SYNAPSE Scholar",
                                            subjectName = sub?.name ?: "Mathematics",
                                            quizTitle = "${chap?.title ?: "Chapter"} Quiz",
                                            chapterId = screen.chapterId.toIntOrNull() ?: 4,
                                            score = screen.score,
                                            totalMarks = screen.total * 10,
                                            percentage = if (screen.total > 0) (screen.score * 100) / screen.total else 0,
                                            correctAnswers = screen.score,
                                            incorrectAnswers = (screen.total - screen.score).coerceAtLeast(0),
                                            totalQuestions = screen.total,
                                            attemptDate = "Today",
                                            passed = if (screen.total > 0) ((screen.score * 100) / screen.total) >= 60 else false
                                        )
                                        navigateTo(AppScreen.DynamicQuizReview(resData, qList, screen.userAnswers))
                                    },
                                    onRetakeQuiz = {
                                        val chapInt = screen.chapterId.toIntOrNull() ?: 4
                                        currentScreen = AppScreen.DynamicQuizAttempt(chapterId = chapInt, subjectId = screen.subjectId)
                                    },
                                    onBackToRoadmap = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.DynamicQuizResults -> {
                                val quizResultsViewModel: QuizResultsViewModel = viewModel()
                                LaunchedEffect(screen.resultData) {
                                    quizResultsViewModel.setQuizResult(screen.resultData)
                                }
                                QuizResultsScreen(
                                    viewModel = quizResultsViewModel,
                                    onHomeClick = {
                                        switchTab(AppScreen.Dashboard)
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onReviewAnswersClick = {
                                        navigateTo(AppScreen.DynamicQuizReview(screen.resultData, screen.questions, screen.userAnswers))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.DynamicQuizReview -> {
                                QuizReviewScreen(
                                    resultData = screen.resultData,
                                    questions = screen.questions,
                                    userAnswers = screen.userAnswers,
                                    onRetakeQuizClick = {
                                        currentScreen = AppScreen.DynamicQuizAttempt(screen.resultData.chapterId)
                                    },
                                    onBackToChaptersClick = {
                                        popBackStack()
                                    },
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.NotesHome -> {
                                val notesHomeViewModel: NotesHomeViewModel = viewModel()
                                NotesHomeScreen(
                                    viewModel = notesHomeViewModel,
                                    initialFilter = screen.initialFilter,
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    onOpenNotesList = { type, subjectId, title ->
                                        navigateTo(AppScreen.NotesList(type, subjectId, title))
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.NotesList -> {
                                val notesListViewModel: NotesListViewModel = viewModel()
                                NotesListScreen(
                                    type = screen.type,
                                    subjectId = screen.subjectId,
                                    title = screen.title,
                                    viewModel = notesListViewModel,
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Settings -> {
                                SettingsScreen(
                                    onBackClick = { navigateBack() },
                                    onLogoutClick = {
                                        profileViewModel.logout {
                                            screenBackStack.clear()
                                            currentScreen = AppScreen.Login
                                        }
                                    },
                                    onAboutClick = {
                                        navigateTo(AppScreen.AboutSynapse)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    themeViewModel = themeViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    SYNAPSETheme {
        WelcomeContent(
            uiState = WelcomeUiState(),
            onStartClick = {},
            onPageClick = {},
        )
    }
}
