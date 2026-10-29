package com.siamts.synapse

import android.os.Bundle
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
import com.siamts.synapse.ui.profile.AboutSynapseScreen
import com.siamts.synapse.ui.profile.AchievementsScreen
import com.siamts.synapse.ui.profile.HelpSupportScreen
import com.siamts.synapse.ui.profile.LanguageSelectionScreen
import com.siamts.synapse.ui.profile.NotificationsSettingsScreen
import com.siamts.synapse.ui.profile.PersonalInfoScreen
import com.siamts.synapse.ui.profile.ProfileScreen
import com.siamts.synapse.ui.profile.ProfileViewModel
import com.siamts.synapse.ui.profile.ProgressAnalyticsScreen
import com.siamts.synapse.ui.profile.StudyGoalsScreen
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
    data object Roadmap : AppScreen()
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
    data object ProgressAnalytics : AppScreen()
    data object Flashcards : AppScreen()
    data object AiTutor : AppScreen()
    data object Home : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SYNAPSETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val profileViewModel: ProfileViewModel = viewModel()
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

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
                                        currentScreen = if (isAuthenticated) AppScreen.Dashboard else AppScreen.Welcome
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
                                        currentScreen = AppScreen.Login
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
                                        currentScreen = AppScreen.Login
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
                                        currentScreen = AppScreen.MixedQuiz
                                    },
                                    onNotesTabClick = {
                                        currentScreen = AppScreen.MixedSavedNotes
                                    },
                                    onProfileTabClick = {
                                        currentScreen = AppScreen.Profile
                                    },
                                    onNotificationClick = {
                                        currentScreen = AppScreen.NotificationsSettings
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
                                            currentScreen = AppScreen.MixedQuiz
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

                            AppScreen.SubjectSelection -> {
                                val subjectSelectionViewModel: SubjectSelectionViewModel = viewModel()
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                SubjectSelectionScreen(
                                    viewModel = subjectSelectionViewModel,
                                    onSubjectClick = { subjectId ->
                                        if (subjectId == "4" || subjectId == "Artificial Intelligence") {
                                            roadmapViewModel.loadArtificialIntelligenceRoadmap()
                                        } else if (subjectId == "5" || subjectId == "Physics") {
                                            roadmapViewModel.loadPhysicsRoadmap()
                                        } else if (subjectId == "6" || subjectId == "Chemistry") {
                                            roadmapViewModel.loadChemistryRoadmap()
                                        } else if (subjectId == "3" || subjectId == "Data Science") {
                                            roadmapViewModel.loadDataScienceRoadmap()
                                        } else if (subjectId == "2" || subjectId == "Computer Science") {
                                            roadmapViewModel.loadComputerScienceRoadmap()
                                        } else if (subjectId == "1" || subjectId == "Mathematics") {
                                            roadmapViewModel.loadMathematicsRoadmap()
                                        }
                                        currentScreen = AppScreen.Roadmap
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.MixedQuiz
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

                            AppScreen.Roadmap -> {
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                RoadmapScreen(
                                    viewModel = roadmapViewModel,
                                    onContinueClick = {
                                        val currentActiveLesson = roadmapViewModel.uiState.value.activeLessonTitle
                                        when (currentActiveLesson) {
                                            "Quadratic Equations" -> currentScreen = AppScreen.QuadraticLesson
                                            "Algorithms" -> currentScreen = AppScreen.AlgorithmsLesson
                                            "Data Visualization" -> currentScreen = AppScreen.DataVisualizationLesson
                                            "Waves" -> currentScreen = AppScreen.WavesLesson
                                            "Chemical Bonding" -> currentScreen = AppScreen.ChemicalBondingLesson
                                            else -> currentScreen = AppScreen.Lesson
                                        }
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.SubjectSelection
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
                                        currentScreen = AppScreen.MathematicsPractice
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
                                        currentScreen = AppScreen.MathematicsPractice
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
                                        currentScreen = AppScreen.MathematicsPractice
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
                                        currentScreen = AppScreen.MathematicsPractice
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
                                        currentScreen = AppScreen.MathematicsPractice
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
                                        currentScreen = AppScreen.MathematicsPractice
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
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.MathematicsNotes
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
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.Lesson
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.MathematicsNotes
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
                                        currentScreen = AppScreen.MathematicsPractice
                                    },
                                    onStartQuizClick = {
                                        currentScreen = AppScreen.Quiz
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
                                    onQuizComplete = {
                                        currentScreen = AppScreen.QuizResults
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.QuizIntro
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
                                val quizResultsViewModel: QuizResultsViewModel = viewModel()
                                QuizResultsScreen(
                                    viewModel = quizResultsViewModel,
                                    onHomeClick = {
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onReviewAnswersClick = {
                                        currentScreen = AppScreen.QuizReviewAnswers
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.MixedQuiz
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
                                            currentScreen = AppScreen.MixedQuiz
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
                                            currentScreen = AppScreen.MixedQuiz
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
                                val roadmapViewModel: RoadmapViewModel = viewModel()
                                MixedSavedDownloadedNotesPreviewScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.Dashboard
                                    },
                                    onReadNoteClick = { subjectName ->
                                        when (subjectName) {
                                            "Mathematics" -> {
                                                roadmapViewModel.loadMathematicsRoadmap()
                                                currentScreen = AppScreen.MathematicsNotes
                                            }
                                            "Computer Science" -> {
                                                roadmapViewModel.loadComputerScienceRoadmap()
                                                currentScreen = AppScreen.AlgorithmsLesson
                                            }
                                            "Data Science" -> {
                                                roadmapViewModel.loadDataScienceRoadmap()
                                                currentScreen = AppScreen.DataVisualizationLesson
                                            }
                                            "Physics" -> {
                                                roadmapViewModel.loadPhysicsRoadmap()
                                                currentScreen = AppScreen.WavesLesson
                                            }
                                            "Chemistry" -> {
                                                roadmapViewModel.loadChemistryRoadmap()
                                                currentScreen = AppScreen.ChemicalBondingLesson
                                            }
                                            else -> {
                                                roadmapViewModel.loadArtificialIntelligenceRoadmap()
                                                currentScreen = AppScreen.Lesson
                                            }
                                        }
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
                                        currentScreen = AppScreen.MixedSavedNotes
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
                                        currentScreen = AppScreen.PersonalInfo
                                    },
                                    onAchievementsClick = {
                                        currentScreen = AppScreen.Achievements
                                    },
                                    onStudyGoalsClick = {
                                        currentScreen = AppScreen.StudyGoals
                                    },
                                    onLanguageClick = {
                                        currentScreen = AppScreen.LanguageSelection
                                    },
                                    onNotificationsClick = {
                                        currentScreen = AppScreen.NotificationsSettings
                                    },
                                    onHelpSupportClick = {
                                        currentScreen = AppScreen.HelpSupport
                                    },
                                    onAboutSynapseClick = {
                                        currentScreen = AppScreen.AboutSynapse
                                    },
                                    onProgressAnalyticsClick = {
                                        currentScreen = AppScreen.ProgressAnalytics
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
                                        } else if (tabIndex == 1) {
                                            currentScreen = AppScreen.SubjectSelection
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.MixedQuiz
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.MixedSavedNotes
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
                                    onSaveChangesClick = { newName, newEmail, newPhone, newBio, newPhotoUri ->
                                        profileViewModel.updateProfile(
                                            name = newName,
                                            email = newEmail,
                                            phone = newPhone,
                                            bio = newBio,
                                            photoUri = newPhotoUri,
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
                                StudyGoalsScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.Profile
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.LanguageSelection -> {
                                LanguageSelectionScreen(
                                    onBackClick = {
                                        currentScreen = AppScreen.Profile
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.NotificationsSettings -> {
                                NotificationsSettingsScreen(
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
                                        currentScreen = AppScreen.Profile
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
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
