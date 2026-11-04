package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel managing Subject Selection, Live Search, Dynamic Interests Mapping,
 * and Extended Catalog dynamic addition.
 */
class SubjectSelectionViewModel(
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    },
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val baseSubjects = listOf(
        Subject(
            id = "MATH",
            title = "Mathematics",
            iconSymbol = "∑",
            progressPercentage = 72,
            progressColor = Color(0xFF3B82F6),
            category = "Formal Sciences",
            chapters = listOf(
                SubjectChapterSnippet(4, "Quadratic Equations", "Roots, factoring, discriminants, and parabolic graphs."),
                SubjectChapterSnippet(1, "Calculus (Differentiation & Integration)", "Differentiation, derivatives, limits, rates of change, power rule, and integration."),
                SubjectChapterSnippet(2, "Linear Algebra", "Matrices, determinants, eigenvalues, and vector spaces."),
                SubjectChapterSnippet(3, "Probability & Statistics", "Bayes' theorem, normal distributions, and hypothesis tests.")
            )
        ),
        Subject(
            id = "CS",
            title = "Computer Science",
            iconSymbol = "</>",
            progressPercentage = 84,
            progressColor = Color(0xFF10B981),
            category = "Technology",
            chapters = listOf(
                SubjectChapterSnippet(8, "Algorithms & Programming", "Python programming, Big-O notation, binary search, sorting, and recursion."),
                SubjectChapterSnippet(5, "Data Structures", "Linked lists, stacks, queues, trees, and hash tables."),
                SubjectChapterSnippet(6, "Operating Systems", "Process scheduling, virtual memory, threads, and concurrency."),
                SubjectChapterSnippet(7, "Computer Networks & SQL", "Networks, TCP/IP protocols, relational algebra, and SQL transactions.")
            )
        ),
        Subject(
            id = "DS",
            title = "Data Science",
            iconSymbol = "📈",
            progressPercentage = 58,
            progressColor = Color(0xFF8B5CF6),
            category = "Data & Analytics",
            chapters = listOf(
                SubjectChapterSnippet(13, "Data Visualization", "Heatmaps, scatter plots, Matplotlib, Seaborn, and dashboards."),
                SubjectChapterSnippet(9, "Exploratory Data Analysis", "Summary statistics, missing value imputation, and outliers."),
                SubjectChapterSnippet(10, "Machine Learning Foundations", "Linear regression, logistic classifiers, and loss functions."),
                SubjectChapterSnippet(11, "Model Evaluation & Metrics", "ROC-AUC curves, precision, recall, and cross-validation.")
            )
        ),
        Subject(
            id = "AI",
            title = "Artificial Intelligence",
            iconSymbol = "🧠",
            progressPercentage = 80,
            progressColor = Color(0xFF2563EB),
            category = "Computer Science",
            chapters = listOf(
                SubjectChapterSnippet(18, "Neural Networks & Deep Learning", "Forward propagation, backpropagation, activations, and gradient descent."),
                SubjectChapterSnippet(14, "Transformers & Attention", "Self-attention mechanisms, encoder-decoder, BERT, and GPT."),
                SubjectChapterSnippet(15, "Retrieval-Augmented Generation (RAG)", "Vector embeddings, cosine similarity, semantic search, and context grounding."),
                SubjectChapterSnippet(16, "Reinforcement Learning", "Q-learning, policy gradients, Markov decision processes, and reward shaping.")
            )
        ),
        Subject(
            id = "PHY",
            title = "Physics",
            iconSymbol = "⚛",
            progressPercentage = 42,
            progressColor = Color(0xFFD97706),
            category = "Physical Sciences",
            chapters = listOf(
                SubjectChapterSnippet(23, "Waves & Sound", "Frequency, wavelength, Doppler effect, and wave superposition."),
                SubjectChapterSnippet(19, "Newton's Laws of Motion", "Inertia, F = ma, action-reaction, friction, and momentum."),
                SubjectChapterSnippet(20, "Thermodynamics", "Zeroth, First, and Second laws, entropy, and heat engines."),
                SubjectChapterSnippet(21, "Electromagnetism", "Coulomb's law, electric fields, magnetic flux, and Faraday's law.")
            )
        ),
        Subject(
            id = "CHEM",
            title = "Chemistry",
            iconSymbol = "🧪",
            progressPercentage = 35,
            progressColor = Color(0xFFEF4444),
            category = "Physical Sciences",
            chapters = listOf(
                SubjectChapterSnippet(28, "Chemical Bonding", "Ionic, covalent, metallic bonds, electronegativity, and polarity."),
                SubjectChapterSnippet(24, "Acids and Bases", "pH scale, neutralization, buffers, and Arrhenius/Brønsted definitions."),
                SubjectChapterSnippet(25, "Periodic Table Trends", "Atomic radii, ionization energy, and electron affinities."),
                SubjectChapterSnippet(26, "Stoichiometry & Reactions", "Mole conversions, balancing equations, and limiting reactants.")
            )
        )
    )

    private val _uiState = MutableStateFlow(
        SubjectSelectionUiState(
            subjects = baseSubjects,
            filteredSubjects = baseSubjects
        )
    )
    val uiState: StateFlow<SubjectSelectionUiState> = _uiState.asStateFlow()

    init {
        observeUserSession()
        if (catalogRepo != null) {
            observeCatalog()
        }
    }

    private fun applyInterests(subjects: List<Subject>, interests: List<String>): List<Subject> {
        if (interests.isEmpty()) return subjects
        val mapped = subjects.map { sub ->
            val match = interests.any { userInt ->
                sub.title.contains(userInt, ignoreCase = true) ||
                userInt.contains(sub.title, ignoreCase = true) ||
                sub.category.contains(userInt, ignoreCase = true)
            }
            sub.copy(isInterested = match)
        }
        return mapped.sortedByDescending { it.isInterested }
    }

    private fun observeUserSession() {
        val manager = sessionManager ?: SessionManager.getInstanceOrNull() ?: return
        viewModelScope.launch {
            manager.sessionStateFlow.collect { sessionState ->
                if (sessionState is AuthSessionState.Authenticated) {
                    val interests = (sessionState.user.selectedInterests ?: sessionState.user.interests).orEmpty()
                    _uiState.update { current ->
                        val updatedSubjects = applyInterests(current.subjects, interests)
                        val (filtered, results) = performSearch(updatedSubjects, current.searchQuery)
                        current.copy(
                            userInterests = interests,
                            subjects = updatedSubjects,
                            filteredSubjects = filtered,
                            searchResults = results
                        )
                    }
                }
            }
        }
    }

    private fun observeCatalog() {
        if (catalogRepo == null) return
        viewModelScope.launch {
            catalogRepo.catalogSubjects.collect { catalogList ->
                val addedSubjects = catalogList.filter { it.isAdded }.map { catSub ->
                    Subject(
                        id = catSub.subjectId.toString(),
                        title = catSub.name,
                        iconSymbol = catSub.iconSymbol,
                        progressPercentage = 0,
                        progressColor = try {
                            Color(android.graphics.Color.parseColor(catSub.colorHex))
                        } catch (_: Throwable) {
                            Color(0xFF2563EB)
                        },
                        category = catSub.category,
                        chapters = catSub.chapters.map { ch ->
                            SubjectChapterSnippet(
                                chapterId = ch.chapterId,
                                title = ch.title,
                                summary = ch.summary
                            )
                        }
                    )
                }

                val allCombined = applyInterests(baseSubjects + addedSubjects, _uiState.value.userInterests)
                val currentQuery = _uiState.value.searchQuery

                _uiState.update { current ->
                    val (filtered, results) = performSearch(allCombined, currentQuery)
                    current.copy(
                        subjects = allCombined,
                        filteredSubjects = filtered,
                        searchResults = results,
                        availableCatalog = catalogList
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val allSubs = _uiState.value.subjects
        val (filtered, results) = performSearch(allSubs, query)
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredSubjects = filtered,
                searchResults = results
            )
        }
    }

    private fun performSearch(
        subjects: List<Subject>,
        query: String
    ): Pair<List<Subject>, List<SearchResultItem>> {
        val trimmed = query.trim().lowercase(Locale.ROOT)
        if (trimmed.isEmpty()) {
            return Pair(subjects, emptyList())
        }

        val matchingSubjects = subjects.filter { sub ->
            sub.title.lowercase(Locale.ROOT).contains(trimmed) ||
            sub.category.lowercase(Locale.ROOT).contains(trimmed) ||
            sub.chapters.any { it.title.lowercase(Locale.ROOT).contains(trimmed) }
        }

        val chapterResults = mutableListOf<SearchResultItem>()
        for (sub in subjects) {
            for (ch in sub.chapters) {
                if (ch.title.lowercase(Locale.ROOT).contains(trimmed) ||
                    ch.summary.lowercase(Locale.ROOT).contains(trimmed) ||
                    sub.title.lowercase(Locale.ROOT).contains(trimmed)
                ) {
                    chapterResults.add(
                        SearchResultItem(
                            subjectId = sub.id,
                            subjectName = sub.title,
                            iconSymbol = sub.iconSymbol,
                            color = sub.progressColor,
                            chapterId = ch.chapterId,
                            chapterTitle = ch.title,
                            snippet = ch.summary
                        )
                    )
                }
            }
        }

        return Pair(matchingSubjects, chapterResults)
    }

    fun openCatalog() {
        _uiState.update { it.copy(isCatalogSheetOpen = true) }
    }

    fun closeCatalog() {
        _uiState.update { it.copy(isCatalogSheetOpen = false) }
    }

    fun toggleSubjectInCatalog(subjectId: String) {
        val repo = catalogRepo ?: return
        if (repo.isSubjectAdded(subjectId)) {
            repo.removeSubject(subjectId)
        } else {
            repo.addSubject(subjectId)
        }
    }

    fun addSubjectFromCatalog(subjectId: String) {
        catalogRepo?.addSubject(subjectId)
    }

    fun onSubjectClicked(subjectId: String) {
        // Handled via callback
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
