package com.siamts.synapse

import android.os.Bundle
import com.siamts.synapse.data.local.BaseCurriculumData
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    data object ProgressAnalytics : AppScreen()
    data object Flashcards : AppScreen()
    data object AiTutor : AppScreen()
    data object Home : AppScreen()
    data class DynamicChapterLesson(val chapterId: Int, val subjectId: String? = null) : AppScreen()
    data object QuizHomeSelection : AppScreen()
    data class SubjectQuizSelection(val subjectId: Int, val subjectName: String) : AppScreen()
    data class DynamicQuizAttempt(val chapterId: Int) : AppScreen()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            SYNAPSETheme(darkTheme = isDark) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    val profileViewModel: ProfileViewModel = viewModel()
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
                    val screenBackStack = remember { mutableStateListOf<AppScreen>() }

                    fun navigateTo(newScreen: AppScreen) {
                        if (newScreen != currentScreen) {
                            screenBackStack.add(currentScreen)
                            currentScreen = newScreen
                        }
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
                                is AppScreen.ProgressAnalytics -> AppScreen.Profile
                                is AppScreen.AiTutor -> AppScreen.Dashboard
                                is AppScreen.QuizHomeSelection -> AppScreen.Dashboard
                                is AppScreen.SubjectQuizSelection -> AppScreen.QuizHomeSelection
                                is AppScreen.DynamicQuizAttempt -> AppScreen.QuizHomeSelection
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
                                navigateTo(AppScreen.QuizResult(subId, chapId, score, total))
                            }
                        } else if (route.startsWith("roadmap_screen/")) {
                            val subId = route.removePrefix("roadmap_screen/")
                            navigateTo(AppScreen.Roadmap(subId))
                        } else if (route.startsWith("lesson_screen/")) {
                            val parts = route.removePrefix("lesson_screen/").split("/")
                            if (parts.size == 1) {
                                parts[0].toIntOrNull()?.let { navigateTo(AppScreen.DynamicChapterLesson(it)) }
                            } else if (parts.size >= 2) {
                                val subId = parts[0]
                                val chapId = parts[1].toIntOrNull() ?: 4
                                navigateTo(AppScreen.DynamicChapterLesson(chapterId = chapId, subjectId = subId))
                            }
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
                        animationSpec = tween(durationMillis = 500),
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
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
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onLearnTabClick = {
                                        currentScreen = AppScreen.SubjectSelection
                                    },
                                    onBrainMapClick = {
                                        currentScreen = AppScreen.BrainMap
                                    },
                                    onFlashcardsClick = {
                                        currentScreen = AppScreen.Flashcards
                                    },
                                    onQuizTabClick = {
                                        currentScreen = AppScreen.QuizHomeSelection
                                    },
                                    onNotesTabClick = {
                                        currentScreen = AppScreen.NotesHome
                                    },
                                    onProfileTabClick = {
                                        currentScreen = AppScreen.Profile
                                    },
                                    onNotificationClick = {
                                        currentScreen = AppScreen.NotificationsSettings
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
                                        currentScreen = AppScreen.Dashboard
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
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
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
                                        navigateTo(AppScreen.DynamicQuizAttempt(chapterId))
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
                                            val firstId = roadmapViewModel.uiState.value.milestones.firstOrNull()?.chapterId ?: 4
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
                                        navigateTo(AppScreen.DynamicQuizAttempt(targetId))
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.QuadraticLessonPart2
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.QuadraticLesson
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.QuadraticLesson
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
                                        currentScreen = AppScreen.MathematicsChapterQuiz
                                    },
                                    onLearnTabClick = {
                                        currentScreen = AppScreen.QuadraticLesson
                                    },
                                    onNotesTabClick = {
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.QuadraticLessonPart2
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
                                        currentScreen = AppScreen.NoteDownloadedSuccess
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.QuadraticLessonPart2
                                    },
                                    onLearnTabClick = {
                                        currentScreen = AppScreen.QuadraticLesson
                                    },
                                    onQuizTabClick = {
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.QuadraticLesson
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
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onLearnTabClick = {
                                        currentScreen = AppScreen.QuadraticLesson
                                    },
                                    onNotesTabClick = {
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.QuadraticLesson
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.AlgorithmsLessonPart2
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.AlgorithmsLesson
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.AlgorithmsLesson
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.DataVisualizationLessonPart2
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.DataVisualizationLesson
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.DataVisualizationLesson
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.WavesLessonPart2
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.WavesLesson
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.WavesLesson
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.QuizIntro
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.ChemicalBondingLessonPart2
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.MathematicsNotes
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.ChemicalBondingLesson
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.ChemicalBondingLesson
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
                                        currentScreen = AppScreen.LessonPart2
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.DynamicQuizAttempt(lessonViewModel.uiState.value.chapterId)
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.NotesList(
                                            type = "subject",
                                            subjectId = lessonViewModel.uiState.value.subjectId,
                                            title = "${lessonViewModel.uiState.value.subjectTag} Notes"
                                        )
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.DynamicQuizAttempt(lessonPart2ViewModel.uiState.value.chapterId)
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.Lesson
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.NotesList(
                                            type = "subject",
                                            subjectId = lessonPart2ViewModel.uiState.value.subjectId,
                                            title = "${lessonPart2ViewModel.uiState.value.subjectTag} Notes"
                                        )
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Lesson
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
                                            currentScreen = AppScreen.QuizIntro
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.MixedSavedNotes
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
                                        navigateTo(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
                                    },
                                    onQuizComplete = { result ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        val score = result?.score ?: 0
                                        val total = result?.totalQuestions ?: qList.size.coerceAtLeast(1)
                                        val subId = quizViewModel.currentSubjectId
                                        val chapId = quizViewModel.currentChapterId
                                        navigateTo(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
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
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onSubmitQuizClick = {
                                        currentScreen = AppScreen.QuizResults
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
                                        currentScreen = AppScreen.QuizReviewAnswers
                                    },
                                    onRetakeQuiz = {
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onBackToRoadmap = {
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.QuizReviewAnswers -> {
                                QuizReviewAnswersPreviewScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.QuizResults
                                    },
                                    onRetryQuizClick = {
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onDashboardClick = {
                                        currentScreen = AppScreen.Dashboard
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
                                        currentScreen = AppScreen.NoteSavedSuccess
                                    },
                                    onDownloadNotes = {
                                        currentScreen = AppScreen.NoteDownloadedSuccess
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
                                        } else if (tabIndex == 1) {
                                            currentScreen = AppScreen.SubjectSelection
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.QuizIntro
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.MixedSavedNotes
                                        } else if (tabIndex == 4) {
                                            currentScreen = AppScreen.Profile
                                        }
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Dashboard
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
                                        currentScreen = AppScreen.MixedSavedNotes
                                    },
                                    onBackToLearningClick = {
                                        currentScreen = AppScreen.Dashboard
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
                                        currentScreen = AppScreen.MixedSavedNotes
                                    },
                                    onBackToLearningClick = {
                                        currentScreen = AppScreen.Dashboard
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
                                        currentScreen = AppScreen.NotesGenerator
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
                                        } else if (tabIndex == 1) {
                                            currentScreen = AppScreen.SubjectSelection
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.QuizIntro
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.MixedSavedNotes
                                        } else if (tabIndex == 4) {
                                            currentScreen = AppScreen.Profile
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
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onGenerateNewNotesClick = {
                                        currentScreen = AppScreen.NotesGenerator
                                    },
                                    onSettingsClick = {
                                        currentScreen = AppScreen.NotificationsSettings
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
                                        navigateBack()
                                    },
                                    onLogoutClick = {
                                        profileViewModel.logout {
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
                                    onProgressAnalyticsClick = {
                                        navigateTo(AppScreen.ProgressAnalytics)
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            navigateTo(AppScreen.Dashboard)
                                        } else if (tabIndex == 1) {
                                            navigateTo(AppScreen.SubjectSelection)
                                        } else if (tabIndex == 2) {
                                            navigateTo(AppScreen.QuizHomeSelection)
                                        } else if (tabIndex == 3) {
                                            navigateTo(AppScreen.NotesHome)
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
                                        currentScreen = AppScreen.Profile
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
                                                    currentScreen = AppScreen.Profile
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
                                        currentScreen = AppScreen.Profile
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
                                        currentScreen = AppScreen.Profile
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
                                        currentScreen = AppScreen.Profile
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
                                        currentScreen = AppScreen.Profile
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.HelpSupport -> {
                                HelpSupportScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.Profile
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.AboutSynapse -> {
                                AboutSynapseScreen(
                                    onBackClick = {
                                        navigateBack()
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
                                        navigateBack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.TermsOfService -> {
                                TermsOfServiceScreen(
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.PrivacyPolicy -> {
                                PrivacyPolicyScreen(
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.OpenSourceLicenses -> {
                                OpenSourceLicensesScreen(
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.ProgressAnalytics -> {
                                ProgressAnalyticsScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.Profile
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
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.AiTutor -> {
                                val aiTutorViewModel: AiTutorViewModel = viewModel()
                                AiTutorScreen(
                                    viewModel = aiTutorViewModel,
                                    onBackClick = {
                                        currentScreen = AppScreen.Dashboard
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
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onFlashcardsClick = {
                                        currentScreen = AppScreen.Flashcards
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.QuizHomeSelection
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.NotesHome
                                    },
                                    onProfileClick = {
                                        currentScreen = AppScreen.Profile
                                    },
                                    onBrainMapClick = {
                                        currentScreen = AppScreen.BrainMap
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
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onSelectSubject = { subId, subName ->
                                        currentScreen = AppScreen.SubjectQuizSelection(subId, subName)
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
                                        currentScreen = AppScreen.QuizHomeSelection
                                    },
                                    onStartQuiz = { chapterId ->
                                        currentScreen = AppScreen.DynamicQuizAttempt(chapterId)
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            is AppScreen.DynamicQuizAttempt -> {
                                val quizViewModel: QuizViewModel = viewModel()
                                LaunchedEffect(screen.chapterId) {
                                    quizViewModel.loadChapterQuiz(screen.chapterId)
                                }
                                QuizScreen(
                                    viewModel = quizViewModel,
                                    onBackClick = {
                                        popBackStack()
                                    },
                                    onNavigateToResult = { subId, chapId, score, total, _ ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        navigateTo(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
                                    },
                                    onQuizComplete = { result ->
                                        val qList = quizViewModel.uiState.value.questions
                                        val uAnswers = quizViewModel.uiState.value.answersMap
                                        val score = result?.score ?: 0
                                        val total = result?.totalQuestions ?: qList.size.coerceAtLeast(1)
                                        val subId = quizViewModel.currentSubjectId
                                        val chapId = quizViewModel.currentChapterId
                                        navigateTo(AppScreen.QuizResult(subId, chapId, score, total, qList, uAnswers))
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
                                        navigateTo(AppScreen.DynamicQuizAttempt(chapInt))
                                    },
                                    onBackToRoadmap = {
                                        navigateTo(AppScreen.Roadmap(screen.subjectId))
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
                                        navigateTo(AppScreen.QuizHomeSelection)
                                    },
                                    onBackClick = {
                                        navigateTo(AppScreen.QuizHomeSelection)
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
                                        navigateTo(AppScreen.DynamicQuizAttempt(screen.resultData.chapterId))
                                    },
                                    onBackToChaptersClick = {
                                        navigateTo(AppScreen.QuizHomeSelection)
                                    },
                                    onBackClick = {
                                        navigateBack()
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
