package com.siamts.synapse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siamts.synapse.ui.auth.LoginScreen
import com.siamts.synapse.ui.auth.LoginViewModel
import com.siamts.synapse.ui.brainmap.BrainMapScreen
import com.siamts.synapse.ui.brainmap.BrainMapViewModel
import com.siamts.synapse.ui.dashboard.DashboardScreen
import com.siamts.synapse.ui.dashboard.DashboardViewModel
import com.siamts.synapse.ui.flashcards.FlashcardsScreen
import com.siamts.synapse.ui.flashcards.FlashcardsViewModel
import com.siamts.synapse.ui.home.HomeScreen
import com.siamts.synapse.ui.home.HomeViewModel
import com.siamts.synapse.ui.lesson.LessonPart2Screen
import com.siamts.synapse.ui.lesson.LessonPart2ViewModel
import com.siamts.synapse.ui.lesson.LessonScreen
import com.siamts.synapse.ui.lesson.LessonViewModel
import com.siamts.synapse.ui.notes.NoteDownloadedSuccessScreen
import com.siamts.synapse.ui.notes.NoteSavedSuccessScreen
import com.siamts.synapse.ui.notes.NoteSuccessViewModel
import com.siamts.synapse.ui.notes.NotesGeneratorScreen
import com.siamts.synapse.ui.notes.NotesGeneratorViewModel
import com.siamts.synapse.ui.notes.SavedNotesScreen
import com.siamts.synapse.ui.notes.SavedNotesViewModel
import com.siamts.synapse.ui.quadratic.QuadraticLessonScreen
import com.siamts.synapse.ui.quadratic.QuadraticLessonViewModel
import com.siamts.synapse.ui.quiz.QuizScreen
import com.siamts.synapse.ui.quiz.QuizViewModel
import com.siamts.synapse.ui.results.QuizResultsScreen
import com.siamts.synapse.ui.results.QuizResultsViewModel
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
import com.siamts.synapse.ui.welcome.WelcomeContent
import com.siamts.synapse.ui.welcome.WelcomeScreen
import com.siamts.synapse.ui.welcome.WelcomeUiState
import com.siamts.synapse.ui.welcome.WelcomeViewModel

sealed class AppScreen {
    data object Splash : AppScreen()
    data object Welcome : AppScreen()
    data object Login : AppScreen()
    data object ProfileSetup : AppScreen()
    data object Dashboard : AppScreen()
    data object BrainMap : AppScreen()
    data object SubjectSelection : AppScreen()
    data object Roadmap : AppScreen()
    data object QuadraticLesson : AppScreen()
    data object Lesson : AppScreen()
    data object LessonPart2 : AppScreen()
    data object Quiz : AppScreen()
    data object QuizResults : AppScreen()
    data object NotesGenerator : AppScreen()
    data object NoteSavedSuccess : AppScreen()
    data object NoteDownloadedSuccess : AppScreen()
    data object SavedNotes : AppScreen()
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
                    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

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
                                    onSplashFinished = {
                                        currentScreen = AppScreen.Welcome
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
                                        currentScreen = AppScreen.ProfileSetup
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
                                        currentScreen = AppScreen.Dashboard
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
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.NotesGenerator
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.AiTutor
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
                                    onSubjectClick = {
                                        currentScreen = AppScreen.Roadmap
                                    },
                                    onTabSelect = { tabIndex ->
                                        if (tabIndex == 0) {
                                            currentScreen = AppScreen.Dashboard
                                        } else if (tabIndex == 3) {
                                            currentScreen = AppScreen.NotesGenerator
                                        } else if (tabIndex == 2) {
                                            currentScreen = AppScreen.AiTutor
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
                                        currentScreen = AppScreen.QuadraticLesson
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
                                        currentScreen = AppScreen.NotesGenerator
                                    },
                                    onQuizClick = {
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNextClick = {
                                        currentScreen = AppScreen.Lesson
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Roadmap
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
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.NotesGenerator
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.QuadraticLesson
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
                                        currentScreen = AppScreen.Quiz
                                    },
                                    onPreviousClick = {
                                        currentScreen = AppScreen.Lesson
                                    },
                                    onAskAiClick = {
                                        currentScreen = AppScreen.AiTutor
                                    },
                                    onNotesClick = {
                                        currentScreen = AppScreen.NotesGenerator
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.Lesson
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                )
                            }

                            AppScreen.Quiz -> {
                                val quizViewModel: QuizViewModel = viewModel()
                                QuizScreen(
                                    viewModel = quizViewModel,
                                    onQuizComplete = {
                                        currentScreen = AppScreen.QuizResults
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.LessonPart2
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
                                    onBackClick = {
                                        currentScreen = AppScreen.Quiz
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
                                        currentScreen = AppScreen.SavedNotes
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
                                        currentScreen = AppScreen.SavedNotes
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
                                        }
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
