package com.siamts.synapse.data.local

import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Embedded on-device curriculum bank containing all base subjects (Mathematics, Computer Science,
 * Data Science, Artificial Intelligence, Physics, Chemistry) with 5 MCQs per chapter, complete
 * lesson parts, and pre-seeded study notes.
 */
object BaseCurriculumData {

    val subjects: List<CatalogSubject> by lazy {
        listOf(
            createMathematicsSubject(),
            createComputerScienceSubject(),
            createDataScienceSubject(),
            createArtificialIntelligenceSubject(),
            createPhysicsSubject(),
            createChemistrySubject()
        )
    }

    val seededNotes: List<NoteItemDto> by lazy {
        val notes = mutableListOf<NoteItemDto>()
        var noteCounter = 2000

        for (subject in subjects) {
            for (chapter in subject.chapters) {
                noteCounter++
                notes.add(
                    NoteItemDto(
                        id = noteCounter,
                        userId = 1,
                        subjectId = subject.subjectId,
                        chapterId = chapter.chapterId,
                        title = "${chapter.title} Notes",
                        content = buildString {
                            appendLine("## ${chapter.title}")
                            appendLine("**Subject:** ${subject.name} | **Category:** ${subject.category}")
                            appendLine()
                            appendLine("### Key Concepts")
                            appendLine(chapter.summary)
                            appendLine()
                            if (chapter.coreFormulas.isNotEmpty()) {
                                appendLine("### Core Formulas & Rules")
                                chapter.coreFormulas.forEach { f ->
                                    appendLine("- `$f`")
                                }
                                appendLine()
                            }
                            if (chapter.syllabus.isNotEmpty()) {
                                appendLine("### Syllabus & Learning Objectives")
                                chapter.syllabus.forEach { s ->
                                    appendLine("- $s")
                                }
                                appendLine()
                            }
                            appendLine("### Exam Takeaways")
                            appendLine("1. Master core definitions and apply them to standard practice problems.")
                            appendLine("2. Double-check calculation signs and variable domains before finalizing answers.")
                            appendLine("3. Review real-world engineering and computational applications.")
                        },
                        category = "SUBJECT",
                        isSaved = true,
                        isDownloaded = true,
                        subjectName = subject.name,
                        subjectSlug = subject.slug,
                        subjectIcon = subject.iconSymbol,
                        subjectColor = subject.colorHex,
                        chapterTitle = chapter.title,
                        createdAt = "2026-11-04 10:00:00",
                        updatedAt = "2026-11-04 10:00:00"
                    )
                )
            }
        }
        notes
    }

    // =========================================================================
    // 1. MATHEMATICS (id: "MATH", numericId: 1)
    // =========================================================================
    private fun createMathematicsSubject(): CatalogSubject {
        return CatalogSubject(
            id = "MATH",
            subjectId = 1,
            name = "Mathematics",
            slug = "mathematics",
            iconSymbol = "∑",
            colorHex = "#3B82F6",
            category = "Formal Sciences",
            description = "Fundamental study of quantity, structure, space, algebra, calculus, and mathematical change.",
            isAdded = true,
            chapters = listOf(
                // Chapter 4: Quadratic Equations
                CatalogChapter(
                    chapterId = 4,
                    chapterOrder = 1,
                    title = "Quadratic Equations",
                    estimatedMinutes = 20,
                    summary = "Roots, factoring, discriminants, and parabolic graphs.",
                    syllabus = listOf(
                        "Standard form ax² + bx + c = 0",
                        "Factoring & Zero Product Property",
                        "Quadratic Formula & Discriminant Analysis",
                        "Parabola Vertex & Axis of Symmetry"
                    ),
                    coreFormulas = listOf(
                        "x = (-b ± √(b² - 4ac)) / (2a)",
                        "Discriminant: D = b² - 4ac",
                        "Vertex x-coordinate: h = -b / (2a)",
                        "Sum of roots = -b/a, Product of roots = c/a"
                    ),
                    part1Title = "Core Fundamentals & Factoring",
                    part1Content = "A quadratic equation is a second-degree polynomial equation in a single variable x, expressed in standard form as ax² + bx + c = 0 where a ≠ 0. Factoring transforms a polynomial into the product of simpler linear binomials.",
                    part2Title = "The Quadratic Formula & Discriminant",
                    part2Content = "When factoring is impractical, the universal quadratic formula yields exact solutions. The discriminant D = b² - 4ac determines root characteristics: two distinct real roots if D > 0, one repeated root if D = 0, and complex conjugate roots if D < 0.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1041,
                            questionNumber = 1,
                            question = "What is the discriminant of the quadratic equation 2x² - 4x + 2 = 0?",
                            options = mapOf("A" to "0", "B" to "16", "C" to "-8", "D" to "4"),
                            correctOption = "A",
                            explanation = "D = b² - 4ac = (-4)² - 4(2)(2) = 16 - 16 = 0. When D = 0, the equation has exactly one real repeated root."
                        ),
                        QuizQuestionDto(
                            id = 1042,
                            questionNumber = 2,
                            question = "What are the roots of the equation x² - 5x + 6 = 0?",
                            options = mapOf("A" to "x = 2, 3", "B" to "x = -2, -3", "C" to "x = 1, 6", "D" to "x = -1, 5"),
                            correctOption = "A",
                            explanation = "Factoring gives (x - 2)(x - 3) = 0. Setting each factor to zero yields x = 2 and x = 3."
                        ),
                        QuizQuestionDto(
                            id = 1043,
                            questionNumber = 3,
                            question = "If the discriminant D < 0, what can be concluded about the roots of ax² + bx + c = 0?",
                            options = mapOf("A" to "Two distinct real roots", "B" to "One repeated real root", "C" to "Two complex conjugate roots", "D" to "No solutions exist in any number system"),
                            correctOption = "C",
                            explanation = "When D < 0, the square root of a negative quantity introduces the imaginary unit i, yielding two complex conjugate roots."
                        ),
                        QuizQuestionDto(
                            id = 1044,
                            questionNumber = 4,
                            question = "What is the x-coordinate of the vertex of the parabola y = ax² + bx + c?",
                            options = mapOf("A" to "x = -b / (2a)", "B" to "x = b / (2a)", "C" to "x = -b / a", "D" to "x = c / a"),
                            correctOption = "A",
                            explanation = "The axis of symmetry passes through the vertex at x = -b / (2a)."
                        ),
                        QuizQuestionDto(
                            id = 1045,
                            questionNumber = 5,
                            question = "For a quadratic equation with roots r₁ and r₂, what is the product r₁ * r₂ equal to?",
                            options = mapOf("A" to "c / a", "B" to "-b / a", "C" to "b / a", "D" to "-c / a"),
                            correctOption = "A",
                            explanation = "By Vieta's formulas, the sum of roots is -b/a and the product of roots is c/a."
                        )
                    )
                ),
                // Chapter 1: Calculus (Differentiation & Integration)
                CatalogChapter(
                    chapterId = 1,
                    chapterOrder = 2,
                    title = "Calculus (Differentiation & Integration)",
                    estimatedMinutes = 25,
                    summary = "Differentiation rules, rates of change, power rule, and definite integrals.",
                    syllabus = listOf(
                        "Limits and Definition of Derivative",
                        "Power, Product, Quotient, and Chain Rules",
                        "Definite and Indefinite Integrals",
                        "Fundamental Theorem of Calculus"
                    ),
                    coreFormulas = listOf(
                        "d/dx [xⁿ] = n * xⁿ⁻¹",
                        "d/dx [f(g(x))] = f'(g(x)) * g'(x)",
                        "∫ xⁿ dx = (xⁿ⁺¹)/(n+1) + C (n ≠ -1)",
                        "∫ [a to b] f'(x) dx = f(b) - f(a)"
                    ),
                    part1Title = "Differentiation Rules & Tangents",
                    part1Content = "Differentiation quantifies the instantaneous rate of change of a continuous function. The derivative f'(x) geometrically represents the tangent slope at any given point x.",
                    part2Title = "Integration & Fundamental Theorem",
                    part2Content = "Integration calculates accumulation and areas under curves. The Fundamental Theorem of Calculus connects differentiation and integration as reciprocal inverse processes.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1011,
                            questionNumber = 1,
                            question = "What is the derivative of f(x) = 4x³ + 2x?",
                            options = mapOf("A" to "12x² + 2", "B" to "12x³ + 2", "C" to "7x² + 2", "D" to "12x²"),
                            correctOption = "A",
                            explanation = "Using the power rule: d/dx(4x³) = 12x² and d/dx(2x) = 2. Summing them yields 12x² + 2."
                        ),
                        QuizQuestionDto(
                            id = 1012,
                            questionNumber = 2,
                            question = "What rule is used to differentiate a composition of functions f(g(x))?",
                            options = mapOf("A" to "Product Rule", "B" to "Quotient Rule", "C" to "Chain Rule", "D" to "L'Hôpital's Rule"),
                            correctOption = "C",
                            explanation = "The Chain Rule states that d/dx[f(g(x))] = f'(g(x)) * g'(x)."
                        ),
                        QuizQuestionDto(
                            id = 1013,
                            questionNumber = 3,
                            question = "What is the indefinite integral ∫ 3x² dx?",
                            options = mapOf("A" to "x³ + C", "B" to "6x + C", "C" to "3x³ + C", "D" to "x² + C"),
                            correctOption = "A",
                            explanation = "∫ 3x² dx = 3 * (x³ / 3) + C = x³ + C."
                        ),
                        QuizQuestionDto(
                            id = 1014,
                            questionNumber = 4,
                            question = "At a critical point where f'(x) = 0, what does f''(x) > 0 indicate?",
                            options = mapOf("A" to "Local minimum", "B" to "Local maximum", "C" to "Inflection point", "D" to "Vertical asymptote"),
                            correctOption = "A",
                            explanation = "By the Second Derivative Test, if f'(x) = 0 and f''(x) > 0, the curve is concave up, indicating a local minimum."
                        ),
                        QuizQuestionDto(
                            id = 1015,
                            questionNumber = 5,
                            question = "What is the definite integral ∫ [0 to 2] 2x dx?",
                            options = mapOf("A" to "4", "B" to "2", "C" to "8", "D" to "0"),
                            correctOption = "A",
                            explanation = "The antiderivative is x². Evaluating from 0 to 2 yields 2² - 0² = 4."
                        )
                    )
                ),
                // Chapter 2: Linear Algebra
                CatalogChapter(
                    chapterId = 2,
                    chapterOrder = 3,
                    title = "Linear Algebra",
                    estimatedMinutes = 22,
                    summary = "Matrices, determinants, eigenvalues, and vector spaces.",
                    syllabus = listOf(
                        "Matrix Operations & Properties",
                        "Determinants & Inverses",
                        "Eigenvalues and Eigenvectors",
                        "Dot Products & Orthogonality"
                    ),
                    coreFormulas = listOf(
                        "det(A - λI) = 0 (Characteristic equation)",
                        "A * v = λ * v",
                        "u · v = ||u|| ||v|| cos(θ)",
                        "A⁻¹ = (1/det(A)) * adj(A)"
                    ),
                    part1Title = "Matrix Algebra & Determinants",
                    part1Content = "Linear algebra provides the mathematical framework for multidimensional vector transformations. A matrix represents a linear mapping between coordinate systems.",
                    part2Title = "Eigendecomposition & Vector Spaces",
                    part2Content = "Eigenvectors represent directions along which a linear transformation acts merely by scaling with a factor equal to the eigenvalue λ. This is the foundation of PCA and machine learning dimensionality reduction.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1021,
                            questionNumber = 1,
                            question = "What is the determinant of a 2x2 matrix [[a, b], [c, d]]?",
                            options = mapOf("A" to "ad - bc", "B" to "ab - cd", "C" to "ad + bc", "D" to "ac - bd"),
                            correctOption = "A",
                            explanation = "The determinant of a 2x2 matrix [[a, b], [c, d]] is computed as ad - bc."
                        ),
                        QuizQuestionDto(
                            id = 1022,
                            questionNumber = 2,
                            question = "What condition must a square matrix satisfy to be invertible?",
                            options = mapOf("A" to "det(A) ≠ 0", "B" to "det(A) = 0", "C" to "All entries must be positive", "D" to "Trace must be zero"),
                            correctOption = "A",
                            explanation = "A matrix is non-singular and has an inverse if and only if its determinant is non-zero."
                        ),
                        QuizQuestionDto(
                            id = 1023,
                            questionNumber = 3,
                            question = "In the equation A * v = λ * v, what does λ represent?",
                            options = mapOf("A" to "Eigenvalue", "B" to "Eigenvector", "C" to "Determinant", "D" to "Matrix Rank"),
                            correctOption = "A",
                            explanation = "λ is the scalar eigenvalue corresponding to the eigenvector v."
                        ),
                        QuizQuestionDto(
                            id = 1024,
                            questionNumber = 4,
                            question = "When two non-zero vectors u and v have a dot product u · v = 0, what is their geometric relationship?",
                            options = mapOf("A" to "They are orthogonal (perpendicular)", "B" to "They are parallel", "C" to "They are collinear", "D" to "They have equal magnitude"),
                            correctOption = "A",
                            explanation = "u · v = ||u|| ||v|| cos(θ) = 0 implies cos(θ) = 0, meaning θ = 90° (perpendicular/orthogonal)."
                        ),
                        QuizQuestionDto(
                            id = 1025,
                            questionNumber = 5,
                            question = "What is the product of an invertible matrix A and its inverse A⁻¹?",
                            options = mapOf("A" to "Identity matrix I", "B" to "Zero matrix", "C" to "Matrix A²", "D" to "Scalar 1"),
                            correctOption = "A",
                            explanation = "By definition, multiplying any invertible matrix by its inverse yields the identity matrix I."
                        )
                    )
                ),
                // Chapter 3: Probability & Statistics
                CatalogChapter(
                    chapterId = 3,
                    chapterOrder = 4,
                    title = "Probability & Statistics",
                    estimatedMinutes = 20,
                    summary = "Bayes' theorem, normal distributions, and hypothesis tests.",
                    syllabus = listOf(
                        "Conditional Probability & Bayes' Rule",
                        "Discrete and Continuous Random Variables",
                        "Gaussian (Normal) Distribution",
                        "Variance, Standard Deviation, and Confidence Intervals"
                    ),
                    coreFormulas = listOf(
                        "P(A|B) = [P(B|A) * P(A)] / P(B)",
                        "Var(X) = E[X²] - (E[X])²",
                        "Standard Deviation σ = √Var(X)",
                        "Z-Score = (X - μ) / σ"
                    ),
                    part1Title = "Probability Laws & Bayes' Theorem",
                    part1Content = "Probability models the likelihood of random events. Bayes' Theorem formalizes how prior beliefs should be rationally updated when presented with new evidence.",
                    part2Title = "Distributions & Hypothesis Testing",
                    part2Content = "The Central Limit Theorem establishes that sums of independent random variables approach a normal distribution regardless of their original distribution shape.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1031,
                            questionNumber = 1,
                            question = "Which theorem allows updating the probability of a hypothesis given observed evidence?",
                            options = mapOf("A" to "Bayes' Theorem", "B" to "Central Limit Theorem", "C" to "Law of Large Numbers", "D" to "Markov's Inequality"),
                            correctOption = "A",
                            explanation = "Bayes' Theorem provides the mathematical basis for updating prior beliefs P(A) with evidence B to compute posterior P(A|B)."
                        ),
                        QuizQuestionDto(
                            id = 1032,
                            questionNumber = 2,
                            question = "In a standard normal distribution, what are the mean (μ) and standard deviation (σ)?",
                            options = mapOf("A" to "μ = 0, σ = 1", "B" to "μ = 1, σ = 0", "C" to "μ = 0, σ = 0", "D" to "μ = 1, σ = 1"),
                            correctOption = "A",
                            explanation = "The standard normal distribution is standardized to have a mean of 0 and a variance (and standard deviation) of 1."
                        ),
                        QuizQuestionDto(
                            id = 1033,
                            questionNumber = 3,
                            question = "Approximately what percentage of values lie within ±1 standard deviation of the mean in a normal distribution?",
                            options = mapOf("A" to "68%", "B" to "95%", "C" to "99.7%", "D" to "50%"),
                            correctOption = "A",
                            explanation = "According to the 68-95-99.7 empirical rule, approximately 68% of data falls within 1 standard deviation."
                        ),
                        QuizQuestionDto(
                            id = 1034,
                            questionNumber = 4,
                            question = "If two events A and B are independent, what is P(A ∩ B)?",
                            options = mapOf("A" to "P(A) * P(B)", "B" to "P(A) + P(B)", "C" to "P(A) / P(B)", "D" to "P(A|B)"),
                            correctOption = "A",
                            explanation = "For independent events, the probability of both occurring together is the product of their individual probabilities."
                        ),
                        QuizQuestionDto(
                            id = 1035,
                            questionNumber = 5,
                            question = "What does the Z-score measure?",
                            options = mapOf("A" to "Number of standard deviations a data point is from the mean", "B" to "The sample variance", "C" to "The median of the distribution", "D" to "The probability of a Type I error"),
                            correctOption = "A",
                            explanation = "Z = (X - μ) / σ indicates how many standard deviations a value X lies above or below the mean μ."
                        )
                    )
                )
            )
        )
    }

    // =========================================================================
    // 2. COMPUTER SCIENCE (id: "CS", numericId: 2)
    // =========================================================================
    private fun createComputerScienceSubject(): CatalogSubject {
        return CatalogSubject(
            id = "CS",
            subjectId = 2,
            name = "Computer Science",
            slug = "computer-science",
            iconSymbol = "</>",
            colorHex = "#10B981",
            category = "Technology",
            description = "Study of computational systems, algorithmic efficiency, data structures, and operating system mechanics.",
            isAdded = true,
            chapters = listOf(
                // Chapter 8: Algorithms & Programming
                CatalogChapter(
                    chapterId = 8,
                    chapterOrder = 1,
                    title = "Algorithms & Programming",
                    estimatedMinutes = 20,
                    summary = "Big-O notation, binary search, sorting algorithms, and recursion.",
                    syllabus = listOf(
                        "Asymptotic Time & Space Complexity",
                        "Binary Search & Divide-and-Conquer",
                        "Quicksort & Mergesort Analysis",
                        "Recursion vs Iteration"
                    ),
                    coreFormulas = listOf(
                        "Binary Search: O(log n) time",
                        "Merge Sort: O(n log n) worst-case time",
                        "Master Theorem: T(n) = a*T(n/b) + f(n)",
                        "Linear Scan: O(n) time"
                    ),
                    part1Title = "Big-O Asymptotics & Searching",
                    part1Content = "Algorithm analysis evaluates efficiency independently of hardware. Binary search exploits sorted arrays to eliminate half the search space with each comparison.",
                    part2Title = "Sorting & Divide-and-Conquer",
                    part2Content = "Divide-and-conquer algorithms break problems into independent subproblems, solve each recursively, and combine results. Merge sort guarantees O(n log n) stability.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1081,
                            questionNumber = 1,
                            question = "What is the worst-case time complexity of Binary Search on a sorted array of size n?",
                            options = mapOf("A" to "O(log n)", "B" to "O(n)", "C" to "O(n log n)", "D" to "O(1)"),
                            correctOption = "A",
                            explanation = "Binary search divides the search space in half at each iteration, giving logarithmic O(log n) time complexity."
                        ),
                        QuizQuestionDto(
                            id = 1082,
                            questionNumber = 2,
                            question = "Which sorting algorithm guarantees O(n log n) worst-case time complexity?",
                            options = mapOf("A" to "Merge Sort", "B" to "Quick Sort", "C" to "Bubble Sort", "D" to "Insertion Sort"),
                            correctOption = "A",
                            explanation = "Merge Sort always divides arrays evenly, guaranteeing O(n log n) worst-case time, whereas Quick Sort degrades to O(n²) with poor pivot choices."
                        ),
                        QuizQuestionDto(
                            id = 1083,
                            questionNumber = 3,
                            question = "What is a mandatory requirement for a recursive function to terminate?",
                            options = mapOf("A" to "Base case", "B" to "A while loop", "C" to "A global variable", "D" to "Dynamic allocation"),
                            correctOption = "A",
                            explanation = "A base case specifies the terminating condition that prevents unbounded call stack recursion."
                        ),
                        QuizQuestionDto(
                            id = 1084,
                            questionNumber = 4,
                            question = "What is the average time complexity of Hash Table key lookup?",
                            options = mapOf("A" to "O(1)", "B" to "O(log n)", "C" to "O(n)", "D" to "O(n²)"),
                            correctOption = "A",
                            explanation = "Hash tables compute array indices via hashing in constant average time O(1)."
                        ),
                        QuizQuestionDto(
                            id = 1085,
                            questionNumber = 5,
                            question = "Which data structure follows the First-In, First-Out (FIFO) principle?",
                            options = mapOf("A" to "Queue", "B" to "Stack", "C" to "Binary Heap", "D" to "Trie"),
                            correctOption = "A",
                            explanation = "A Queue inserts at the rear and removes from the front, upholding FIFO semantics."
                        )
                    )
                ),
                // Chapter 5: Data Structures
                CatalogChapter(
                    chapterId = 5,
                    chapterOrder = 2,
                    title = "Data Structures",
                    estimatedMinutes = 25,
                    summary = "Linked lists, stacks, queues, binary search trees, and hash maps.",
                    syllabus = listOf(
                        "Arrays vs Singly/Doubly Linked Lists",
                        "Stack (LIFO) & Queue (FIFO) Implementations",
                        "Binary Search Trees (BST) & AVL Balance",
                        "Hash Maps and Collision Resolution (Chaining vs Open Addressing)"
                    ),
                    coreFormulas = listOf(
                        "Array Random Access: O(1)",
                        "Linked List Insertion: O(1) given pointer",
                        "BST Average Search: O(log n)",
                        "Hash Collision Load Factor: α = n / k"
                    ),
                    part1Title = "Linear Data Structures",
                    part1Content = "Linear structures organize elements sequentially. Linked lists provide dynamic resizing without memory reallocation, while arrays offer instant cache-friendly indexing.",
                    part2Title = "Hierarchical & Associative Structures",
                    part2Content = "Trees establish parent-child hierarchical relations. A Binary Search Tree ensures that left descendants are strictly less than parent nodes and right descendants strictly greater.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1051,
                            questionNumber = 1,
                            question = "What is the time complexity to insert a node at the head of a Singly Linked List?",
                            options = mapOf("A" to "O(1)", "B" to "O(n)", "C" to "O(log n)", "D" to "O(n²)"),
                            correctOption = "A",
                            explanation = "Updating the head pointer and linking to the previous head takes constant time O(1)."
                        ),
                        QuizQuestionDto(
                            id = 1052,
                            questionNumber = 2,
                            question = "Which traversal of a Binary Search Tree produces elements in sorted ascending order?",
                            options = mapOf("A" to "In-order traversal", "B" to "Pre-order traversal", "C" to "Post-order traversal", "D" to "Level-order traversal"),
                            correctOption = "A",
                            explanation = "In-order traversal visits left subtree, root, then right subtree, producing strictly sorted order in a BST."
                        ),
                        QuizQuestionDto(
                            id = 1053,
                            questionNumber = 3,
                            question = "What condition causes a stack overflow error?",
                            options = mapOf("A" to "Pushing onto a full call stack or exceeding memory limits", "B" to "Popping from an empty stack", "C" to "Allocating memory on the heap", "D" to "Using a circular queue"),
                            correctOption = "A",
                            explanation = "Stack overflow happens when the execution stack exceeds its allocated memory ceiling, frequently due to infinite recursion."
                        ),
                        QuizQuestionDto(
                            id = 1054,
                            questionNumber = 4,
                            question = "In open addressing for hash tables, how are collisions resolved?",
                            options = mapOf("A" to "Probing subsequent array slots (linear/quadratic)", "B" to "Appending to a linked list", "C" to "Discarding old entries", "D" to "Increasing key bit length"),
                            correctOption = "A",
                            explanation = "Open addressing stores all elements directly in the hash table array by probing alternative positions when collisions occur."
                        ),
                        QuizQuestionDto(
                            id = 1055,
                            questionNumber = 5,
                            question = "What is the maximum number of children a node can have in a Binary Tree?",
                            options = mapOf("A" to "2", "B" to "1", "C" to "3", "D" to "Unlimited"),
                            correctOption = "A",
                            explanation = "By definition, each node in a binary tree has at most two children, termed the left and right child."
                        )
                    )
                ),
                // Chapter 6: Operating Systems
                CatalogChapter(
                    chapterId = 6,
                    chapterOrder = 3,
                    title = "Operating Systems",
                    estimatedMinutes = 20,
                    summary = "Process scheduling, virtual memory, threads, and concurrency.",
                    syllabus = listOf(
                        "Processes vs Threads & Context Switching",
                        "CPU Scheduling (Round Robin, FCFS, Priority)",
                        "Paging, Virtual Memory & Page Replacement",
                        "Deadlock: Coffman Conditions & Prevention"
                    ),
                    coreFormulas = listOf(
                        "Page Fault Penalty = Disk Access Time",
                        "Effective Memory Access Time = (1-p)*T_mem + p*T_disk",
                        "Turnaround Time = Completion Time - Arrival Time",
                        "Waiting Time = Turnaround Time - Burst Time"
                    ),
                    part1Title = "Process Management & Scheduling",
                    part1Content = "An operating system manages shared hardware resources. Processes represent isolated execution units with distinct memory spaces, whereas threads share process memory.",
                    part2Title = "Virtual Memory & Synchronization",
                    part2Content = "Virtual memory decouples logical process addresses from physical RAM via paging. Synchronization primitives like mutexes and semaphores prevent race conditions.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1061,
                            questionNumber = 1,
                            question = "Which of the following is NOT one of the four Coffman conditions required for a deadlock?",
                            options = mapOf("A" to "Preemption allowed", "B" to "Mutual exclusion", "C" to "Hold and wait", "D" to "Circular wait"),
                            correctOption = "A",
                            explanation = "Deadlock requires No Preemption. If preemption is allowed, deadlocks cannot occur."
                        ),
                        QuizQuestionDto(
                            id = 1062,
                            questionNumber = 2,
                            question = "What hardware component translates virtual memory addresses into physical RAM addresses?",
                            options = mapOf("A" to "Memory Management Unit (MMU)", "B" to "ALU", "C" to "DMA Controller", "D" to "Instruction Register"),
                            correctOption = "A",
                            explanation = "The MMU translates virtual page addresses into physical frame addresses using page tables."
                        ),
                        QuizQuestionDto(
                            id = 1063,
                            questionNumber = 3,
                            question = "What is the primary difference between a process and a thread?",
                            options = mapOf("A" to "Threads within the same process share memory space", "B" to "Processes share memory space automatically", "C" to "Threads cannot execute concurrently", "D" to "Processes require no context switching"),
                            correctOption = "A",
                            explanation = "Threads share code, data, and heap of their parent process, while processes have separate virtual address spaces."
                        ),
                        QuizQuestionDto(
                            id = 1064,
                            questionNumber = 4,
                            question = "What scheduling algorithm assigns a fixed time quantum to each ready process in cyclic order?",
                            options = mapOf("A" to "Round Robin", "B" to "First-Come First-Served", "C" to "Shortest Job First", "D" to "Multilevel Feedback"),
                            correctOption = "A",
                            explanation = "Round Robin gives each process an equal slice of CPU time (quantum) in rotation."
                        ),
                        QuizQuestionDto(
                            id = 1065,
                            questionNumber = 5,
                            question = "What event occurs when a requested page is not currently resident in physical RAM?",
                            options = mapOf("A" to "Page fault", "B" to "Segmentation fault", "C" to "Kernel panic", "D" to "Cache eviction"),
                            correctOption = "A",
                            explanation = "A page fault is an interrupt raised by the hardware MMU when accessing an unmapped virtual page, prompting OS disk retrieval."
                        )
                    )
                ),
                // Chapter 7: Computer Networks & SQL
                CatalogChapter(
                    chapterId = 7,
                    chapterOrder = 4,
                    title = "Computer Networks & SQL",
                    estimatedMinutes = 22,
                    summary = "TCP/IP protocols, OSI reference model, relational schema, and SQL transactions.",
                    syllabus = listOf(
                        "OSI 7-Layer vs TCP/IP 4-Layer Models",
                        "TCP 3-Way Handshake vs UDP Datagrams",
                        "Relational Schema, Keys & Normalization",
                        "ACID Transaction Properties & SQL Joins"
                    ),
                    coreFormulas = listOf(
                        "TCP Handshake: SYN -> SYN-ACK -> ACK",
                        "Bandwidth-Delay Product = Bandwidth * RTT",
                        "SQL: SELECT ... FROM ... WHERE ... GROUP BY ...",
                        "ACID = Atomicity, Consistency, Isolation, Durability"
                    ),
                    part1Title = "Network Architecture & Protocols",
                    part1Content = "Computer networks enable distributed communication across layers. TCP provides reliable, ordered stream delivery via acknowledgments, while UDP prioritizes low latency.",
                    part2Title = "Database Schemas & Transactions",
                    part2Content = "Relational databases structure information into tables with primary and foreign keys. ACID properties ensure reliability during concurrent operations.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1071,
                            questionNumber = 1,
                            question = "Which layer of the OSI model is responsible for end-to-end packet routing across networks?",
                            options = mapOf("A" to "Network Layer (Layer 3)", "B" to "Transport Layer (Layer 4)", "C" to "Data Link Layer (Layer 2)", "D" to "Session Layer (Layer 5)"),
                            correctOption = "A",
                            explanation = "The Network layer handles logical addressing (IP) and routing packets across intermediate networks."
                        ),
                        QuizQuestionDto(
                            id = 1072,
                            questionNumber = 2,
                            question = "What are the three packets exchanged during a TCP connection establishment?",
                            options = mapOf("A" to "SYN, SYN-ACK, ACK", "B" to "ACK, SYN, FIN", "C" to "REQ, RES, ACK", "D" to "HELO, SYN, ACK"),
                            correctOption = "A",
                            explanation = "TCP initiates connections via a 3-way handshake: Client sends SYN, Server replies SYN-ACK, Client confirms with ACK."
                        ),
                        QuizQuestionDto(
                            id = 1073,
                            questionNumber = 3,
                            question = "What does the 'I' represent in the ACID properties of database transactions?",
                            options = mapOf("A" to "Isolation", "B" to "Integrity", "C" to "Indexing", "D" to "Immutability"),
                            correctOption = "A",
                            explanation = "Isolation guarantees that concurrent transactions execute without interfering with one another."
                        ),
                        QuizQuestionDto(
                            id = 1074,
                            questionNumber = 4,
                            question = "Which SQL clause filters groups produced by a GROUP BY query?",
                            options = mapOf("A" to "HAVING", "B" to "WHERE", "C" to "ORDER BY", "D" to "FILTER"),
                            correctOption = "A",
                            explanation = "WHERE filters individual rows before grouping, while HAVING filters aggregated groups after GROUP BY."
                        ),
                        QuizQuestionDto(
                            id = 1075,
                            questionNumber = 5,
                            question = "What type of join returns all rows from the left table and matched rows from the right table?",
                            options = mapOf("A" to "LEFT OUTER JOIN", "B" to "INNER JOIN", "C" to "RIGHT OUTER JOIN", "D" to "FULL OUTER JOIN"),
                            correctOption = "A",
                            explanation = "A LEFT JOIN preserves every row from the left table, filling right table columns with NULL when no match exists."
                        )
                    )
                )
            )
        )
    }

    // =========================================================================
    // 3. DATA SCIENCE (id: "DS", numericId: 3)
    // =========================================================================
    private fun createDataScienceSubject(): CatalogSubject {
        return CatalogSubject(
            id = "DS",
            subjectId = 3,
            name = "Data Science",
            slug = "data-science",
            iconSymbol = "📈",
            colorHex = "#8B5CF6",
            category = "Data & Analytics",
            description = "Multidisciplinary domain analyzing structured and unstructured data using statistics, algorithms, and visualization.",
            isAdded = true,
            chapters = listOf(
                // Chapter 13: Data Visualization
                CatalogChapter(
                    chapterId = 13,
                    chapterOrder = 1,
                    title = "Data Visualization",
                    estimatedMinutes = 20,
                    summary = "Heatmaps, scatter plots, Matplotlib, Seaborn, and dashboards.",
                    syllabus = listOf(
                        "Chart Selection: Distribution, Comparison & Correlation",
                        "Scatter Plots & Correlation Analysis",
                        "Box Plots: Quartiles, IQR, and Outliers",
                        "Heatmaps and Correlation Matrices"
                    ),
                    coreFormulas = listOf(
                        "Interquartile Range: IQR = Q3 - Q1",
                        "Outlier Lower Bound: Q1 - 1.5 * IQR",
                        "Outlier Upper Bound: Q3 + 1.5 * IQR",
                        "Pearson Correlation: r = Cov(X,Y) / (σx * σy)"
                    ),
                    part1Title = "Visual Encodings & Chart Types",
                    part1Content = "Data visualization translates numerical information into spatial representations. Choosing the correct visual encoding (scatter plots for bivariate relationships, histograms for continuous frequencies) is critical.",
                    part2Title = "Exploratory Plots & Outlier Identification",
                    part2Content = "Box plots reveal skewness, median positioning, and potential outliers based on Tukey's 1.5 * IQR fence. Heatmaps display multivariate feature correlations succinctly.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1131,
                            questionNumber = 1,
                            question = "Which plot type is most effective for visualizing the bivariate relationship and correlation between two continuous variables?",
                            options = mapOf("A" to "Scatter Plot", "B" to "Pie Chart", "C" to "Stacked Bar Chart", "D" to "Radar Chart"),
                            correctOption = "A",
                            explanation = "Scatter plots map two continuous features along perpendicular axes, revealing linear or non-linear trends, clustering, and outliers."
                        ),
                        QuizQuestionDto(
                            id = 1132,
                            questionNumber = 2,
                            question = "In a standard box plot, how is the Interquartile Range (IQR) defined?",
                            options = mapOf("A" to "Q3 - Q1", "B" to "Q2 - Q1", "C" to "Max - Min", "D" to "Mean ± Standard Deviation"),
                            correctOption = "A",
                            explanation = "IQR measures the spread of the middle 50% of the dataset, calculated as the difference between the 75th percentile (Q3) and 25th percentile (Q1)."
                        ),
                        QuizQuestionDto(
                            id = 1133,
                            questionNumber = 3,
                            question = "According to Tukey's rule, a data point is classified as an outlier if it falls below which threshold?",
                            options = mapOf("A" to "Q1 - 1.5 * IQR", "B" to "Median - 2 * IQR", "C" to "Mean - 3 * IQR", "D" to "Q1 - 3 * IQR"),
                            correctOption = "A",
                            explanation = "The standard lower outlier boundary is Q1 - 1.5 * IQR. Values below this threshold are marked as outliers."
                        ),
                        QuizQuestionDto(
                            id = 1134,
                            questionNumber = 4,
                            question = "What visual element in a heatmap represents the strength of correlation between variable pairs?",
                            options = mapOf("A" to "Color intensity / hue gradient", "B" to "Bar height", "C" to "Marker size", "D" to "Dashed line pattern"),
                            correctOption = "A",
                            explanation = "Heatmaps encode numerical matrix values into an intuitive color spectrum (e.g. diverging blue to red)."
                        ),
                        QuizQuestionDto(
                            id = 1135,
                            questionNumber = 5,
                            question = "Why should pie charts generally be avoided when comparing more than 5 categories?",
                            options = mapOf("A" to "Human perception struggles to accurately judge angle and slice area differences", "B" to "They cannot display percentages", "C" to "They require 3D rendering", "D" to "They only work with negative numbers"),
                            correctOption = "A",
                            explanation = "Humans estimate linear length (bar charts) much more accurately than 2D angles and slice areas in pie charts."
                        )
                    )
                ),
                // Chapter 9: Exploratory Data Analysis
                CatalogChapter(
                    chapterId = 9,
                    chapterOrder = 2,
                    title = "Exploratory Data Analysis",
                    estimatedMinutes = 22,
                    summary = "Summary statistics, missing value imputation, and distribution analysis.",
                    syllabus = listOf(
                        "Measures of Central Tendency & Dispersion",
                        "Handling Missing Values (Mean, Median, MICE)",
                        "Skewness, Kurtosis & Transformations",
                        "Feature Encoding (One-Hot vs Label Encoding)"
                    ),
                    coreFormulas = listOf(
                        "Mean: μ = (Σx) / n",
                        "Sample Variance: s² = Σ(x - x̄)² / (n - 1)",
                        "Skewness: E[(X - μ)³] / σ³",
                        "Log Transformation: y = ln(x + 1)"
                    ),
                    part1Title = "Data Distributions & Central Tendency",
                    part1Content = "Exploratory Data Analysis (EDA) investigates datasets before formal modeling. The median is robust to extreme outliers, whereas the mean is pulled towards heavy distribution tails.",
                    part2Title = "Data Cleaning & Preprocessing",
                    part2Content = "Missing data must be diagnosed as MCAR, MAR, or MNAR before selecting an imputation strategy. Log transformations help stabilize variance in right-skewed data.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1091,
                            questionNumber = 1,
                            question = "Which measure of central tendency is least sensitive to extreme outliers?",
                            options = mapOf("A" to "Median", "B" to "Mean", "C" to "Midrange", "D" to "Standard deviation"),
                            correctOption = "A",
                            explanation = "The median is the 50th percentile rank value and remains stable even when extreme outlier values are present."
                        ),
                        QuizQuestionDto(
                            id = 1092,
                            questionNumber = 2,
                            question = "When a dataset is positively (right) skewed, what is the typical relationship between mean and median?",
                            options = mapOf("A" to "Mean > Median", "B" to "Mean < Median", "C" to "Mean = Median", "D" to "No predictable relationship"),
                            correctOption = "A",
                            explanation = "In right-skewed distributions, large values pull the arithmetic mean upward, making it greater than the median."
                        ),
                        QuizQuestionDto(
                            id = 1093,
                            questionNumber = 3,
                            question = "What encoding technique converts categorical variables into binary indicator columns?",
                            options = mapOf("A" to "One-Hot Encoding", "B" to "Standardization", "C" to "Min-Max Scaling", "D" to "Ordinal Binning"),
                            correctOption = "A",
                            explanation = "One-Hot Encoding creates a new 0/1 binary column for each distinct category."
                        ),
                        QuizQuestionDto(
                            id = 1094,
                            questionNumber = 4,
                            question = "What is the purpose of applying a logarithmic transformation to skewed data?",
                            options = mapOf("A" to "Compress high values and make the distribution more symmetrical (Gaussian-like)", "B" to "Eliminate missing data automatically", "C" to "Convert continuous data into categorical", "D" to "Double the sample size"),
                            correctOption = "A",
                            explanation = "Log transformations compress long right tails, stabilizing variance and normalizing residuals for linear algorithms."
                        ),
                        QuizQuestionDto(
                            id = 1095,
                            questionNumber = 5,
                            question = "What does a correlation coefficient of r = 0 between two variables imply?",
                            options = mapOf("A" to "No linear relationship exists between them", "B" to "A perfect non-linear relationship exists", "C" to "One variable causes the other", "D" to "The variables have equal variance"),
                            correctOption = "A",
                            explanation = "Pearson's r = 0 indicates absence of linear correlation; non-linear dependencies may still exist."
                        )
                    )
                ),
                // Chapter 10: Machine Learning Foundations
                CatalogChapter(
                    chapterId = 10,
                    chapterOrder = 3,
                    title = "Machine Learning Foundations",
                    estimatedMinutes = 25,
                    summary = "Supervised vs unsupervised learning, loss functions, and gradient descent.",
                    syllabus = listOf(
                        "Supervised vs Unsupervised vs Reinforcement Paradigms",
                        "Linear Regression & Ordinary Least Squares",
                        "Logistic Regression & Sigmoid Decision Boundary",
                        "Bias-Variance Tradeoff & Overfitting Prevention"
                    ),
                    coreFormulas = listOf(
                        "Linear Model: ŷ = wᵀx + b",
                        "Mean Squared Error: MSE = (1/n) Σ(y - ŷ)²",
                        "Sigmoid: σ(z) = 1 / (1 + e⁻ᶻ)",
                        "L2 Regularization (Ridge): Loss + λ ||w||²"
                    ),
                    part1Title = "Supervised vs Unsupervised Learning",
                    part1Content = "Supervised learning models predict known target labels from input features, while unsupervised learning discovers latent patterns, groupings, and clusters without labels.",
                    part2Title = "Optimization & Generalization",
                    part2Content = "Models must generalize to unseen test instances. Overfitting occurs when high model capacity memorizes training noise at the expense of validation accuracy.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1101,
                            questionNumber = 1,
                            question = "Which of the following is an example of an unsupervised learning task?",
                            options = mapOf("A" to "K-Means Customer Segmentation", "B" to "Spam email classification", "C" to "House price prediction", "D" to "Medical diagnosis classification"),
                            correctOption = "A",
                            explanation = "K-Means clustering groups unlabeled data points by feature similarity without target supervision."
                        ),
                        QuizQuestionDto(
                            id = 1102,
                            questionNumber = 2,
                            question = "What activation function maps real numbers to the probability range (0, 1) in Logistic Regression?",
                            options = mapOf("A" to "Sigmoid (Logistic)", "B" to "ReLU", "C" to "Linear", "D" to "Step function"),
                            correctOption = "A",
                            explanation = "The Sigmoid function σ(z) = 1 / (1 + e⁻ᶻ) squashes any real value into a valid probability between 0 and 1."
                        ),
                        QuizQuestionDto(
                            id = 1103,
                            questionNumber = 3,
                            question = "What problem arises when a model has very low training error but very high validation/test error?",
                            options = mapOf("A" to "Overfitting (High Variance)", "B" to "Underfitting (High Bias)", "C" to "Data Leakage", "D" to "Feature Satiation"),
                            correctOption = "A",
                            explanation = "Overfitting occurs when a model fits training noise too closely, failing to generalize to unseen test data."
                        ),
                        QuizQuestionDto(
                            id = 1104,
                            questionNumber = 4,
                            question = "How does L2 regularization (Ridge) combat model overfitting?",
                            options = mapOf("A" to "Penalizes the sum of squared weight magnitudes ||w||²", "B" to "Sets random features to exactly zero", "C" to "Increases the learning rate exponentially", "D" to "Removes training samples"),
                            correctOption = "A",
                            explanation = "Ridge regression adds a penalty proportional to squared weights, shrinking parameters towards zero and smoothing complexity."
                        ),
                        QuizQuestionDto(
                            id = 1105,
                            questionNumber = 5,
                            question = "What loss function is standard for evaluating continuous regression models?",
                            options = mapOf("A" to "Mean Squared Error (MSE)", "B" to "Binary Cross-Entropy", "C" to "Hinge Loss", "D" to "Kullback-Leibler Divergence"),
                            correctOption = "A",
                            explanation = "MSE measures the average squared difference between continuous predictions and true targets."
                        )
                    )
                ),
                // Chapter 11: Model Evaluation & Metrics
                CatalogChapter(
                    chapterId = 11,
                    chapterOrder = 4,
                    title = "Model Evaluation & Metrics",
                    estimatedMinutes = 20,
                    summary = "Precision, recall, ROC-AUC, confusion matrix, and cross-validation.",
                    syllabus = listOf(
                        "Confusion Matrix: TP, FP, TN, FN",
                        "Precision vs Recall Tradeoff",
                        "F1-Score: Harmonic Mean Formulation",
                        "ROC Curve & AUC Score Interpretation"
                    ),
                    coreFormulas = listOf(
                        "Precision = TP / (TP + FP)",
                        "Recall (Sensitivity) = TP / (TP + FN)",
                        "F1 = 2 * (Precision * Recall) / (Precision + Recall)",
                        "Accuracy = (TP + TN) / (TP + TN + FP + FN)"
                    ),
                    part1Title = "Classification Performance Metrics",
                    part1Content = "Accuracy can be misleading in imbalanced datasets. Precision measures positive predictive value, while recall measures detection coverage.",
                    part2Title = "ROC-AUC & Validation Protocols",
                    part2Content = "The ROC curve plots True Positive Rate against False Positive Rate across all decision thresholds. An AUC of 1.0 represents a flawless classifier, while 0.5 denotes random guessing.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1111,
                            questionNumber = 1,
                            question = "In a medical test where missing a disease is dangerous, which metric should be prioritized?",
                            options = mapOf("A" to "Recall (Sensitivity)", "B" to "Precision", "C" to "Specificity", "D" to "Accuracy"),
                            correctOption = "A",
                            explanation = "High recall minimizes False Negatives (cases where sick patients are incorrectly identified as healthy)."
                        ),
                        QuizQuestionDto(
                            id = 1112,
                            questionNumber = 2,
                            question = "What is the F1-Score?",
                            options = mapOf("A" to "The harmonic mean of precision and recall", "B" to "The arithmetic mean of accuracy and precision", "C" to "The geometric mean of true positive rate and true negative rate", "D" to "The area under the PR curve"),
                            correctOption = "A",
                            explanation = "F1 = 2 * (Precision * Recall) / (Precision + Recall), balancing precision and recall."
                        ),
                        QuizQuestionDto(
                            id = 1113,
                            questionNumber = 3,
                            question = "What does an Area Under the ROC Curve (AUC) score of 0.5 signify?",
                            options = mapOf("A" to "Performance equivalent to random guessing", "B" to "A perfect classifier", "C" to "All predictions are false positives", "D" to "High precision with low recall"),
                            correctOption = "A",
                            explanation = "An AUC of 0.5 represents the diagonal line of no discrimination (random coin flip guessing)."
                        ),
                        QuizQuestionDto(
                            id = 1114,
                            questionNumber = 4,
                            question = "What is the purpose of K-Fold Cross Validation?",
                            options = mapOf("A" to "Evaluate model stability across multiple non-overlapping validation splits", "B" to "Speed up gradient descent training time", "C" to "Increase the number of features", "D" to "Convert regression into classification"),
                            correctOption = "A",
                            explanation = "K-Fold splits the dataset into K partitions, training on K-1 folds and validating on 1 fold iteratively to reduce split bias."
                        ),
                        QuizQuestionDto(
                            id = 1115,
                            questionNumber = 5,
                            question = "What does a False Positive represent in binary classification?",
                            options = mapOf("A" to "A negative instance incorrectly classified as positive", "B" to "A positive instance incorrectly classified as negative", "C" to "A correctly identified positive instance", "D" to "A missing target value"),
                            correctOption = "A",
                            explanation = "A False Positive (Type I error) occurs when the model predicts the positive class for an actual negative instance."
                        )
                    )
                )
            )
        )
    }

    // =========================================================================
    // 4. ARTIFICIAL INTELLIGENCE (id: "AI", numericId: 4)
    // =========================================================================
    private fun createArtificialIntelligenceSubject(): CatalogSubject {
        return CatalogSubject(
            id = "AI",
            subjectId = 4,
            name = "Artificial Intelligence",
            slug = "artificial-intelligence",
            iconSymbol = "🧠",
            colorHex = "#2563EB",
            category = "Computer Science",
            description = "Engineering intelligent agents that perceive environments, learn representations, reason, and make autonomous decisions.",
            isAdded = true,
            chapters = listOf(
                // Chapter 18: Neural Networks & Deep Learning
                CatalogChapter(
                    chapterId = 18,
                    chapterOrder = 1,
                    title = "Neural Networks & Deep Learning",
                    estimatedMinutes = 25,
                    summary = "Forward propagation, backpropagation, activations, and gradient descent.",
                    syllabus = listOf(
                        "Perceptrons & Multilayer Architectures",
                        "Activation Functions: ReLU, GeLU, Sigmoid, Softmax",
                        "Backpropagation & The Chain Rule",
                        "Optimizers: SGD, Momentum, Adam"
                    ),
                    coreFormulas = listOf(
                        "Layer Output: a = σ(W * x + b)",
                        "Weight Update: w ← w - α * (∂L / ∂w)",
                        "ReLU(x) = max(0, x)",
                        "Softmax(zᵢ) = e^(zᵢ) / Σ e^(zⱼ)"
                    ),
                    part1Title = "Neural Architectures & Activations",
                    part1Content = "Neural networks stack layers of parameterized matrix transformations interleaved with non-linear activation functions. Non-linearities like ReLU enable networks to approximate arbitrary continuous functions.",
                    part2Title = "Backpropagation & Optimization",
                    part2Content = "Backpropagation recursively computes partial derivatives of the scalar loss with respect to all network weights via the chain rule. Gradient descent steps weights opposite the gradient direction.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1181,
                            questionNumber = 1,
                            question = "Why are non-linear activation functions essential in multilayer neural networks?",
                            options = mapOf("A" to "Without non-linearities, any deep network collapses into a single linear transformation", "B" to "To guarantee convex loss landscapes", "C" to "To eliminate backpropagation", "D" to "To prevent gradient updates"),
                            correctOption = "A",
                            explanation = "A composition of purely linear functions is mathematically equivalent to a single linear matrix multiplication. Non-linearities provide universal approximation capability."
                        ),
                        QuizQuestionDto(
                            id = 1182,
                            questionNumber = 2,
                            question = "What mathematical calculus principle underpins backpropagation?",
                            options = mapOf("A" to "The Chain Rule", "B" to "L'Hôpital's Rule", "C" to "Taylor Series Expansion", "D" to "Green's Theorem"),
                            correctOption = "A",
                            explanation = "Backpropagation applies the multivariate Chain Rule to propagate gradient signals backward through layer compositions."
                        ),
                        QuizQuestionDto(
                            id = 1183,
                            questionNumber = 3,
                            question = "What is the defining formula of the Rectified Linear Unit (ReLU) activation?",
                            options = mapOf("A" to "f(x) = max(0, x)", "B" to "f(x) = 1 / (1 + e⁻ˣ)", "C" to "f(x) = tanh(x)", "D" to "f(x) = x²"),
                            correctOption = "A",
                            explanation = "ReLU outputs 0 for negative inputs and x for positive inputs, avoiding saturation in the positive regime."
                        ),
                        QuizQuestionDto(
                            id = 1184,
                            questionNumber = 4,
                            question = "What symptom indicates that the learning rate is set excessively high during gradient descent?",
                            options = mapOf("A" to "Loss fluctuates wildly or diverges towards infinity", "B" to "Training gets stuck in a local minimum immediately", "C" to "Weights freeze at zero", "D" to "Memory consumption doubles"),
                            correctOption = "A",
                            explanation = "An excessively high learning rate causes weight updates to overshoot loss minima, causing oscillation or catastrophic divergence."
                        ),
                        QuizQuestionDto(
                            id = 1185,
                            questionNumber = 5,
                            question = "Which optimizer adapts individual learning rates per parameter using first and second gradient moments?",
                            options = mapOf("A" to "Adam", "B" to "Vanilla SGD", "C" to "Momentum without decay", "D" to "Coordinate Descent"),
                            correctOption = "A",
                            explanation = "Adam (Adaptive Moment Estimation) computes exponentially decaying averages of past gradients (momentum) and squared gradients (scaling)."
                        )
                    )
                ),
                // Chapter 14: Transformers & Attention
                CatalogChapter(
                    chapterId = 14,
                    chapterOrder = 2,
                    title = "Transformers & Attention",
                    estimatedMinutes = 25,
                    summary = "Self-attention mechanisms, encoder-decoder, BERT, and GPT architectures.",
                    syllabus = listOf(
                        "Limitations of RNNs/LSTMs in Sequential Modeling",
                        "Scaled Dot-Product Attention: Q, K, V",
                        "Multi-Head Attention & Positional Encodings",
                        "Autoregressive Decoders (GPT) vs Encoders (BERT)"
                    ),
                    coreFormulas = listOf(
                        "Attention(Q, K, V) = softmax((Q * Kᵀ) / √dₖ) * V",
                        "MultiHead(Q,K,V) = Concat(head₁,...,headₕ) * Wᴼ",
                        "Positional Encoding: PE(pos, 2i) = sin(pos / 10000^(2i/d))",
                        "Self-Attention Complexity: O(n² * d)"
                    ),
                    part1Title = "Scaled Dot-Product Attention",
                    part1Content = "The Transformer replaces recurrent loops with parallel self-attention. Queries (Q) and Keys (K) compute dynamic pairwise token relevance, scaled by √dₖ to stabilize gradients before softmax weighting of Values (V).",
                    part2Title = "Architecture Variations & Encodings",
                    part2Content = "Because attention is permutation-invariant, positional encodings inject sequence order. Encoder models (BERT) contextualize bidirectionally, whereas decoder models (GPT) employ causal masking for generative language tasks.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1141,
                            questionNumber = 1,
                            question = "What is the formula for Scaled Dot-Product Attention in Transformers?",
                            options = mapOf("A" to "softmax((Q * Kᵀ) / √dₖ) * V", "B" to "sigmoid(Q * K) * V", "C" to "tanh(Q * Kᵀ) / dₖ", "D" to "(Q * Vᵀ) / K"),
                            correctOption = "A",
                            explanation = "Scaled dot-product attention scales query-key products by the square root of key dimension dₖ before softmax."
                        ),
                        QuizQuestionDto(
                            id = 1142,
                            questionNumber = 2,
                            question = "Why do Transformers require positional encodings?",
                            options = mapOf("A" to "Self-attention is permutation-invariant and does not inherently encode sequence order", "B" to "To reduce token vocabulary size", "C" to "To prevent vanishing gradients in deep layers", "D" to "To compress model weights"),
                            correctOption = "A",
                            explanation = "Without positional encodings, a Transformer treats a sentence as an unordered bag of tokens."
                        ),
                        QuizQuestionDto(
                            id = 1143,
                            questionNumber = 3,
                            question = "What architectural masking ensures autoregressive decoders (like GPT) cannot attend to future tokens?",
                            options = mapOf("A" to "Causal (Look-ahead) Masking", "B" to "Dropout Masking", "C" to "Token Pruning", "D" to "Sparse Attention Mask"),
                            correctOption = "A",
                            explanation = "Causal masking sets upper-triangular attention logits to -∞ so tokens only attend to preceding positions."
                        ),
                        QuizQuestionDto(
                            id = 1144,
                            questionNumber = 4,
                            question = "What is the computational complexity of standard full self-attention with respect to sequence length n?",
                            options = mapOf("A" to "O(n²)", "B" to "O(n)", "C" to "O(log n)", "D" to "O(n³)"),
                            correctOption = "A",
                            explanation = "Comparing every token to every other token in an n-token sequence requires computing an n x n attention matrix, resulting in quadratic O(n²) complexity."
                        ),
                        QuizQuestionDto(
                            id = 1145,
                            questionNumber = 5,
                            question = "How does Multi-Head Attention improve over single-head attention?",
                            options = mapOf("A" to "Allows the model to attend to information from different representation subspaces simultaneously", "B" to "Halves total parameter count", "C" to "Converts transformer models into recurrent cells", "D" to "Eliminates need for layer normalization"),
                            correctOption = "A",
                            explanation = "Multiple parallel attention heads allow the model to jointly attend to semantic, syntactic, and long-range dependencies."
                        )
                    )
                ),
                // Chapter 15: Retrieval-Augmented Generation (RAG)
                CatalogChapter(
                    chapterId = 15,
                    chapterOrder = 3,
                    title = "Retrieval-Augmented Generation (RAG)",
                    estimatedMinutes = 22,
                    summary = "Vector embeddings, cosine similarity, semantic search, and context grounding.",
                    syllabus = listOf(
                        "Overcoming Hallucinations with External Memory",
                        "Dense Embeddings & Vector Indexing (HNSW, FAISS)",
                        "Similarity Metrics: Cosine vs Dot Product vs Euclidean",
                        "Chunking Strategies, Re-ranking & Context Prompting"
                    ),
                    coreFormulas = listOf(
                        "Cosine Similarity: cos(θ) = (A · B) / (||A|| * ||B||)",
                        "Normalized Embeddings: cos(θ) = A · B (when ||A||=||B||=1)",
                        "Recall@K = (Relevant Chunks in Top K) / Total Relevant",
                        "Prompt = [Context Chunks] + [User Query]"
                    ),
                    part1Title = "Semantic Search & Vector Embeddings",
                    part1Content = "RAG augments LLMs by retrieving relevant knowledge chunks from external databases. Text is transformed into dense vector embeddings where semantic proximity corresponds to geometric closeness.",
                    part2Title = "Grounding & Generation Pipelines",
                    part2Content = "Retrieved chunks are injected into the prompt as factual context. This mitigates hallucination, enables citations, and avoids the costly retraining of model parameters.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1151,
                            questionNumber = 1,
                            question = "What is the primary motivation for using Retrieval-Augmented Generation (RAG) in AI tutors?",
                            options = mapOf("A" to "Ground responses in verified curriculum sources to prevent hallucination", "B" to "Train the base model weights on-the-fly", "C" to "Replace tokenizers with character embeddings", "D" to "Compress the APK file size"),
                            correctOption = "A",
                            explanation = "RAG provides the language model with authoritative retrieved facts, ensuring grounded, accurate, hallucination-free answers."
                        ),
                        QuizQuestionDto(
                            id = 1152,
                            questionNumber = 2,
                            question = "What metric evaluates the semantic similarity between two normalized text embedding vectors A and B?",
                            options = mapOf("A" to "Cosine Similarity (or Dot Product)", "B" to "Manhattan Distance", "C" to "Hamming Distance", "D" to "Chi-Square Metric"),
                            correctOption = "A",
                            explanation = "Cosine similarity measures the angle between directional vectors, invariant to scale and optimal for text semantics."
                        ),
                        QuizQuestionDto(
                            id = 1153,
                            questionNumber = 3,
                            question = "Why is semantic chunking preferred over arbitrary character slicing?",
                            options = mapOf("A" to "Preserves coherent context, complete thoughts, and topic boundaries", "B" to "Reduces vector dimensions to zero", "C" to "Eliminates need for embeddings", "D" to "Enforces single-sentence retrieval"),
                            correctOption = "A",
                            explanation = "Semantic chunking ensures complete conceptual ideas are retrieved intact rather than sliced mid-sentence."
                        ),
                        QuizQuestionDto(
                            id = 1154,
                            questionNumber = 4,
                            question = "What is the role of a re-ranker in an advanced two-stage RAG pipeline?",
                            options = mapOf("A" to "Cross-encode top candidate chunks with the query to precisely re-score relevance", "B" to "Delete cached embeddings", "C" to "Translate queries into SQL", "D" to "Generate synthetic quiz questions"),
                            correctOption = "A",
                            explanation = "A re-ranker evaluates deep query-document interactions on top-K candidates from fast vector search."
                        ),
                        QuizQuestionDto(
                            id = 1155,
                            questionNumber = 5,
                            question = "What does Top-K retrieval specify in a RAG pipeline?",
                            options = mapOf("A" to "The number of most relevant context chunks to pass into the prompt", "B" to "The temperature of the model", "C" to "The batch size during pretraining", "D" to "The maximum token vocabulary"),
                            correctOption = "A",
                            explanation = "Top-K defines how many highest-scoring chunks are retrieved and injected into the generative model prompt."
                        )
                    )
                ),
                // Chapter 16: Reinforcement Learning
                CatalogChapter(
                    chapterId = 16,
                    chapterOrder = 4,
                    title = "Reinforcement Learning",
                    estimatedMinutes = 20,
                    summary = "Q-learning, policy gradients, Markov decision processes, and reward shaping.",
                    syllabus = listOf(
                        "Markov Decision Processes: S, A, P, R, γ",
                        "Exploration vs Exploitation & ε-Greedy Strategy",
                        "Bellman Equation & Value Iteration",
                        "Q-Learning & Deep Q-Networks (DQN)"
                    ),
                    coreFormulas = listOf(
                        "Bellman Optimality: Q*(s, a) = R(s, a) + γ max Q*(s', a')",
                        "Q-Learning Update: Q(s, a) ← Q(s,a) + α [r + γ max Q(s',a') - Q(s,a)]",
                        "Discounted Return: Gₜ = Σ γᵏ Rₜ₊ₖ₊₁",
                        "Policy Gradient: ∇J(θ) = E[∇ log π_θ(a|s) * Gₜ]"
                    ),
                    part1Title = "Markov Decision Processes & Rewards",
                    part1Content = "Reinforcement Learning formalizes trial-and-error decision making. An agent interacts with an environment, choosing actions to maximize accumulated discounted rewards.",
                    part2Title = "Temporal Difference & Q-Learning",
                    part2Content = "Q-Learning is a model-free temporal difference algorithm that learns optimal state-action values directly from experience transitions without requiring environment transition dynamics.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1161,
                            questionNumber = 1,
                            question = "In an MDP, what parameter γ ∈ [0, 1) controls the importance of future versus immediate rewards?",
                            options = mapOf("A" to "Discount Factor (γ)", "B" to "Learning Rate (α)", "C" to "Exploration rate (ε)", "D" to "Temperature"),
                            correctOption = "A",
                            explanation = "The discount factor γ balances immediate gratification (γ close to 0) with long-term accumulated return (γ close to 1)."
                        ),
                        QuizQuestionDto(
                            id = 1162,
                            questionNumber = 2,
                            question = "What strategy balances trying novel actions with exploiting the best known action in RL?",
                            options = mapOf("A" to "Epsilon-Greedy (ε-greedy)", "B" to "Random restart", "C" to "Strictly greedy choice", "D" to "Deadlock avoidance"),
                            correctOption = "A",
                            explanation = "ε-greedy chooses a random exploratory action with probability ε and the current best greedy action with probability 1-ε."
                        ),
                        QuizQuestionDto(
                            id = 1163,
                            questionNumber = 3,
                            question = "What fundamental recurrence relation decomposes a state value into immediate reward plus discounted future value?",
                            options = mapOf("A" to "Bellman Equation", "B" to "Euler-Lagrange Equation", "C" to "Schrödinger Equation", "D" to "Navier-Stokes Equation"),
                            correctOption = "A",
                            explanation = "The Bellman Equation recursively relates the value of a state to the immediate reward plus expected discounted continuation values."
                        ),
                        QuizQuestionDto(
                            id = 1164,
                            questionNumber = 4,
                            question = "Why is Q-learning classified as an 'off-policy' learning algorithm?",
                            options = mapOf("A" to "It learns the optimal policy value Q* while following an exploratory policy like ε-greedy", "B" to "It does not require an agent", "C" to "It only trains on supervised labels", "D" to "It runs without rewards"),
                            correctOption = "A",
                            explanation = "Q-learning updates its target using the greedy max operator max Q(s', a') regardless of what action the behavioral policy chose."
                        ),
                        QuizQuestionDto(
                            id = 1165,
                            questionNumber = 5,
                            question = "In Policy Gradient methods (e.g. REINFORCE), what is directly parameterized and optimized?",
                            options = mapOf("A" to "The policy distribution π_θ(a|s)", "B" to "The state transition probability matrix", "C" to "The environment physics simulator", "D" to "The discount factor γ"),
                            correctOption = "A",
                            explanation = "Policy gradient algorithms directly parameterize policy probabilities with neural network weights θ and ascend expected return gradients."
                        )
                    )
                )
            )
        )
    }

    // =========================================================================
    // 5. PHYSICS (id: "PHY", numericId: 5)
    // =========================================================================
    private fun createPhysicsSubject(): CatalogSubject {
        return CatalogSubject(
            id = "PHY",
            subjectId = 5,
            name = "Physics",
            slug = "physics",
            iconSymbol = "⚛",
            colorHex = "#D97706",
            category = "Physical Sciences",
            description = "Fundamental study of nature, kinematics, forces, energy conservation, wave optics, and electromagnetism.",
            isAdded = true,
            chapters = listOf(
                // Chapter 23: Waves & Sound
                CatalogChapter(
                    chapterId = 23,
                    chapterOrder = 1,
                    title = "Waves & Sound",
                    estimatedMinutes = 20,
                    summary = "Frequency, wavelength, Doppler effect, and wave superposition.",
                    syllabus = listOf(
                        "Mechanical vs Electromagnetic Waves",
                        "Wave Equation: v = f * λ",
                        "The Doppler Effect & Frequency Shifts",
                        "Constructive and Destructive Superposition"
                    ),
                    coreFormulas = listOf(
                        "Wave Speed: v = f * λ",
                        "Period: T = 1 / f",
                        "Doppler Shift: f' = f * (v ± v₀) / (v ∓ vₛ)",
                        "Wave Intensity: I = P / (4πr²)"
                    ),
                    part1Title = "Wave Mechanics & Characteristics",
                    part1Content = "A wave is an oscillation that propagates energy through space without bulk transport of matter. Transverse waves oscillate perpendicular to travel, while longitudinal waves oscillate parallel.",
                    part2Title = "Superposition & Doppler Effect",
                    part2Content = "The principle of superposition states that overlapping wave displacements add algebraically. The Doppler effect alters perceived frequency when sound sources and observers move relative to each other.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1231,
                            questionNumber = 1,
                            question = "What is the speed of a sound wave with a frequency of 440 Hz and a wavelength of 0.77 m?",
                            options = mapOf("A" to "338.8 m/s", "B" to "571.4 m/s", "C" to "120.0 m/s", "D" to "300,000 km/s"),
                            correctOption = "A",
                            explanation = "v = f * λ = 440 Hz * 0.77 m = 338.8 m/s."
                        ),
                        QuizQuestionDto(
                            id = 1232,
                            questionNumber = 2,
                            question = "What type of wave is a sound wave traveling through air?",
                            options = mapOf("A" to "Longitudinal mechanical wave", "B" to "Transverse electromagnetic wave", "C" to "Surface torsional wave", "D" to "Circular standing wave"),
                            correctOption = "A",
                            explanation = "Sound in fluids/air travels as compressions and rarefactions parallel to propagation, making it a longitudinal mechanical wave."
                        ),
                        QuizQuestionDto(
                            id = 1233,
                            questionNumber = 3,
                            question = "When a sound source moves towards a stationary observer, how does the observed frequency compare to the emitted frequency?",
                            options = mapOf("A" to "Observed frequency is higher", "B" to "Observed frequency is lower", "C" to "Observed frequency is identical", "D" to "Observed frequency drops to zero"),
                            correctOption = "A",
                            explanation = "Due to the Doppler effect, wave fronts compress in front of the approaching source, causing a higher observed pitch."
                        ),
                        QuizQuestionDto(
                            id = 1234,
                            questionNumber = 4,
                            question = "What phenomenon occurs when two waves meet in phase (crest meets crest)?",
                            options = mapOf("A" to "Constructive interference", "B" to "Destructive interference", "C" to "Refraction", "D" to "Polarization"),
                            correctOption = "A",
                            explanation = "In-phase waves superimpose constructively, yielding a combined wave with amplitude equal to the sum of individual amplitudes."
                        ),
                        QuizQuestionDto(
                            id = 1235,
                            questionNumber = 5,
                            question = "When a wave transitions from a less dense to a more dense medium, which property remains invariant?",
                            options = mapOf("A" to "Frequency", "B" to "Wavelength", "C" to "Wave speed", "D" to "Amplitude"),
                            correctOption = "A",
                            explanation = "Wave frequency is determined strictly by the oscillating source and remains invariant across medium boundaries."
                        )
                    )
                ),
                // Chapter 19: Newton's Laws of Motion
                CatalogChapter(
                    chapterId = 19,
                    chapterOrder = 2,
                    title = "Newton's Laws of Motion",
                    estimatedMinutes = 22,
                    summary = "Inertia, F = ma, action-reaction pairs, friction, and momentum.",
                    syllabus = listOf(
                        "First Law: Inertia & Equilibrium",
                        "Second Law: F_net = m * a",
                        "Third Law: Action-Reaction Pairs",
                        "Linear Momentum & Impulse Conservation"
                    ),
                    coreFormulas = listOf(
                        "F_net = m * a",
                        "Weight: W = m * g",
                        "Static Friction: f_s ≤ μ_s * N",
                        "Linear Momentum: p = m * v"
                    ),
                    part1Title = "Inertia & Dynamic Forces",
                    part1Content = "Classical mechanics models the motion of macroscopic bodies. Newton's first law establishes inertial reference frames where unforced objects maintain constant velocity.",
                    part2Title = "Action-Reaction & Momentum",
                    part2Content = "Forces always occur in matched action-reaction pairs acting on different objects. Isolated systems conserve total linear momentum across all collisions.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1191,
                            questionNumber = 1,
                            question = "What net force is required to accelerate a 5 kg mass at 4 m/s²?",
                            options = mapOf("A" to "20 N", "B" to "0.8 N", "C" to "1.25 N", "D" to "9 N"),
                            correctOption = "A",
                            explanation = "F = m * a = 5 kg * 4 m/s² = 20 N."
                        ),
                        QuizQuestionDto(
                            id = 1192,
                            questionNumber = 2,
                            question = "Why do Newton's third law action-reaction pairs never cancel each other out?",
                            options = mapOf("A" to "They act on two different bodies", "B" to "They act in the same direction", "C" to "One force is always larger than the other", "D" to "They act at different moments in time"),
                            correctOption = "A",
                            explanation = "Action-reaction forces act on opposite interacting objects (e.g. Earth on Moon and Moon on Earth), so they cannot cancel on a single free-body diagram."
                        ),
                        QuizQuestionDto(
                            id = 1193,
                            questionNumber = 3,
                            question = "Which physical property measures a body's resistance to changes in its state of motion?",
                            options = mapOf("A" to "Inertia (Inertial Mass)", "B" to "Weight", "C" to "Velocity", "D" to "Surface area"),
                            correctOption = "A",
                            explanation = "Mass is the quantitative measure of inertia: larger mass resists acceleration more under equivalent net force."
                        ),
                        QuizQuestionDto(
                            id = 1194,
                            questionNumber = 4,
                            question = "In an isolated system with no external net forces, what quantity is strictly conserved during collisions?",
                            options = mapOf("A" to "Total linear momentum", "B" to "Total kinetic energy (in inelastic collisions)", "C" to "Velocity of each object", "D" to "Potential energy only"),
                            correctOption = "A",
                            explanation = "Total linear momentum Σp is universally conserved in all isolated collisions (elastic and inelastic alike)."
                        ),
                        QuizQuestionDto(
                            id = 1195,
                            questionNumber = 5,
                            question = "What is the relationship between impulse J and change in momentum Δp?",
                            options = mapOf("A" to "J = Δp (Impulse-Momentum Theorem)", "B" to "J = Δp / Δt", "C" to "J = (Δp)²", "D" to "J = m * Δp"),
                            correctOption = "A",
                            explanation = "Impulse J = F_avg * Δt equals the change in linear momentum Δp."
                        )
                    )
                ),
                // Chapter 20: Thermodynamics
                CatalogChapter(
                    chapterId = 20,
                    chapterOrder = 3,
                    title = "Thermodynamics",
                    estimatedMinutes = 20,
                    summary = "Zeroth, First, and Second laws, entropy, and heat engines.",
                    syllabus = listOf(
                        "Temperature, Thermal Equilibrium & Zeroth Law",
                        "First Law: ΔU = Q - W (Energy Conservation)",
                        "Second Law: Entropy & Carnot Limit",
                        "Ideal Gas Law: PV = nRT"
                    ),
                    coreFormulas = listOf(
                        "ΔU = Q - W",
                        "PV = nRT",
                        "Carnot Efficiency: η = 1 - (T_cold / T_hot)",
                        "Entropy Change: ΔS = Q_rev / T"
                    ),
                    part1Title = "Heat, Work & First Law",
                    part1Content = "Thermodynamics examines macroscopic energy transfers. The First Law states that heat supplied to a closed system equals the change in internal energy plus work done.",
                    part2Title = "Entropy & The Second Law",
                    part2Content = "Natural thermodynamic processes are irreversible. The Second Law asserts that the total entropy of an isolated system always increases over time.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1201,
                            questionNumber = 1,
                            question = "What does the First Law of Thermodynamics state?",
                            options = mapOf("A" to "Energy cannot be created or destroyed, only transformed (ΔU = Q - W)", "B" to "Entropy always decreases", "C" to "Absolute zero can be reached in finite steps", "D" to "All heat can be converted entirely to work without loss"),
                            correctOption = "A",
                            explanation = "The First Law formalizes the principle of conservation of energy for thermodynamic systems."
                        ),
                        QuizQuestionDto(
                            id = 1202,
                            questionNumber = 2,
                            question = "What is the theoretical maximum efficiency of a heat engine operating between hot reservoir T_h and cold reservoir T_c?",
                            options = mapOf("A" to "Carnot Efficiency: η = 1 - (T_c / T_h)", "B" to "η = T_c / T_h", "C" to "η = 100% always", "D" to "η = (T_h + T_c) / T_h"),
                            correctOption = "A",
                            explanation = "Sadi Carnot demonstrated that maximum thermal efficiency is strictly bounded by η = 1 - (T_cold / T_hot) in Kelvins."
                        ),
                        QuizQuestionDto(
                            id = 1203,
                            questionNumber = 3,
                            question = "What thermodynamic property measures the microscopic disorder or number of accessible microstates in a system?",
                            options = mapOf("A" to "Entropy (S)", "B" to "Enthalpy (H)", "C" to "Pressure (P)", "D" to "Internal Energy (U)"),
                            correctOption = "A",
                            explanation = "Entropy S = k_B * ln(Ω) quantifies statistical microstate disorder and irreversibility."
                        ),
                        QuizQuestionDto(
                            id = 1204,
                            questionNumber = 4,
                            question = "In an isothermal process involving an ideal gas, which quantity remains constant?",
                            options = mapOf("A" to "Temperature", "B" to "Pressure", "C" to "Volume", "D" to "Entropy"),
                            correctOption = "A",
                            explanation = "Isothermal processes occur at strictly constant temperature (ΔT = 0, ΔU = 0 for ideal gases)."
                        ),
                        QuizQuestionDto(
                            id = 1205,
                            questionNumber = 5,
                            question = "What is absolute zero in degrees Celsius?",
                            options = mapOf("A" to "-273.15 °C", "B" to "0 °C", "C" to "-100 °C", "D" to "-459.67 °C"),
                            correctOption = "A",
                            explanation = "Absolute zero corresponds to 0 Kelvin, which equals -273.15 °C."
                        )
                    )
                ),
                // Chapter 21: Electromagnetism
                CatalogChapter(
                    chapterId = 21,
                    chapterOrder = 4,
                    title = "Electromagnetism",
                    estimatedMinutes = 22,
                    summary = "Coulomb's law, electric fields, magnetic flux, and Faraday's law of induction.",
                    syllabus = listOf(
                        "Coulomb's Electrostatic Force Law",
                        "Electric Fields & Potential Differences",
                        "Lorentz Force & Magnetic Fields",
                        "Faraday's Law of Induction & Lenz's Law"
                    ),
                    coreFormulas = listOf(
                        "Coulomb's Law: F = k * (|q₁*q₂|) / r²",
                        "Electric Field: E = F / q",
                        "Magnetic Force: F = q * (v × B)",
                        "Faraday's Law: ε = -dΦ_B / dt"
                    ),
                    part1Title = "Electrostatics & Electric Fields",
                    part1Content = "Charges exert mutual electrostatic forces described by Coulomb's inverse-square law. Electric fields represent force per unit positive test charge at any point in space.",
                    part2Title = "Magnetic Induction & Maxwell's Synthesis",
                    part2Content = "Moving charges produce magnetic fields. Faraday's law of induction establishes that time-varying magnetic flux induces electromotive force (voltage) in closed conductive loops.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1211,
                            questionNumber = 1,
                            question = "If the distance between two point charges is doubled, by what factor does the electrostatic force between them change?",
                            options = mapOf("A" to "Decreases to 1/4 of its original value", "B" to "Halves (1/2)", "C" to "Doubles (2x)", "D" to "Remains unchanged"),
                            correctOption = "A",
                            explanation = "Coulomb's law has an inverse-square dependence on distance: F ∝ 1/r². Doubling r reduces F to 1/2² = 1/4."
                        ),
                        QuizQuestionDto(
                            id = 1212,
                            questionNumber = 2,
                            question = "What is the direction of the magnetic force on a positive charge moving through a magnetic field B?",
                            options = mapOf("A" to "Perpendicular to both velocity v and magnetic field B (Right-Hand Rule)", "B" to "Parallel to velocity v", "C" to "Parallel to magnetic field B", "D" to "Opposite to the velocity vector"),
                            correctOption = "A",
                            explanation = "The Lorentz force F = q(v × B) is a vector cross product, oriented strictly perpendicular to both v and B via the right-hand rule."
                        ),
                        QuizQuestionDto(
                            id = 1213,
                            questionNumber = 3,
                            question = "What law states that an induced electromotive force always opposes the change in magnetic flux that produced it?",
                            options = mapOf("A" to "Lenz's Law", "B" to "Ampere's Law", "C" to "Gauss's Law", "D" to "Ohm's Law"),
                            correctOption = "A",
                            explanation = "Lenz's Law gives the negative sign in Faraday's law: induced currents generate magnetic fields opposing the original flux change."
                        ),
                        QuizQuestionDto(
                            id = 1214,
                            questionNumber = 4,
                            question = "What is the SI unit of magnetic flux?",
                            options = mapOf("A" to "Weber (Wb)", "B" to "Tesla (T)", "C" to "Henry (H)", "D" to "Farad (F)"),
                            correctOption = "A",
                            explanation = "Magnetic flux Φ = B * A is measured in Webers (1 Wb = 1 T·m²)."
                        ),
                        QuizQuestionDto(
                            id = 1215,
                            questionNumber = 5,
                            question = "What does Ohm's Law state for an ideal ohmic conductor?",
                            options = mapOf("A" to "V = I * R", "B" to "V = I / R", "C" to "P = V * I²", "D" to "R = V * I"),
                            correctOption = "A",
                            explanation = "Ohm's Law establishes that potential difference V is directly proportional to current I, with resistance R as constant."
                        )
                    )
                )
            )
        )
    }

    // =========================================================================
    // 6. CHEMISTRY (id: "CHEM", numericId: 6)
    // =========================================================================
    private fun createChemistrySubject(): CatalogSubject {
        return CatalogSubject(
            id = "CHEM",
            subjectId = 6,
            name = "Chemistry",
            slug = "chemistry",
            iconSymbol = "🧪",
            colorHex = "#EF4444",
            category = "Physical Sciences",
            description = "Study of matter, atomic electron configurations, chemical bonding, reactions, stoichiometry, and kinetics.",
            isAdded = true,
            chapters = listOf(
                // Chapter 28: Chemical Bonding
                CatalogChapter(
                    chapterId = 28,
                    chapterOrder = 1,
                    title = "Chemical Bonding",
                    estimatedMinutes = 20,
                    summary = "Ionic, covalent, metallic bonds, electronegativity, and polarity.",
                    syllabus = listOf(
                        "Octet Rule & Lewis Dot Representations",
                        "Ionic Bonds: Electron Transfer & Lattice Energy",
                        "Covalent Bonds: Shared Pairs & Dipole Moments",
                        "Metallic Bonding: Delocalized Electron Sea"
                    ),
                    coreFormulas = listOf(
                        "ΔElectronegativity > 1.7: Predominantly Ionic",
                        "0.4 ≤ ΔElectronegativity ≤ 1.7: Polar Covalent",
                        "ΔElectronegativity < 0.4: Nonpolar Covalent",
                        "Dipole Moment: μ = q * r"
                    ),
                    part1Title = "Ionic vs Covalent Interactions",
                    part1Content = "Chemical bonds stabilize atoms by achieving noble gas valence electron configurations. Ionic bonds form via complete electron transfer between metals and non-metals, creating electrostatic lattices.",
                    part2Title = "Electronegativity & Molecular Geometry",
                    part2Content = "Covalent bonds involve shared electron pairs. Unequal sharing occurs when bonded atoms have different electronegativities, giving rise to polar bonds and molecular dipole moments.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1281,
                            questionNumber = 1,
                            question = "What type of chemical bond forms when valence electrons are transferred from a metal to a non-metal atom?",
                            options = mapOf("A" to "Ionic bond", "B" to "Nonpolar covalent bond", "C" to "Coordinate covalent bond", "D" to "Hydrogen bond"),
                            correctOption = "A",
                            explanation = "Ionic bonds form through complete electron transfer from low-electronegativity metals (forming cations) to high-electronegativity non-metals (forming anions)."
                        ),
                        QuizQuestionDto(
                            id = 1282,
                            questionNumber = 2,
                            question = "Which element on the periodic table possesses the highest Pauling electronegativity?",
                            options = mapOf("A" to "Fluorine (F)", "B" to "Oxygen (O)", "C" to "Chlorine (Cl)", "D" to "Francium (Fr)"),
                            correctOption = "A",
                            explanation = "Fluorine has the highest Pauling electronegativity value (~3.98), having the strongest tendency to attract shared electrons."
                        ),
                        QuizQuestionDto(
                            id = 1283,
                            questionNumber = 3,
                            question = "Which model describes metallic bonding as positive metal cations immersed in a delocalized 'sea' of valence electrons?",
                            options = mapOf("A" to "Electron Sea Model", "B" to "Lock and Key Model", "C" to "Lewis Octet Lattice", "D" to "Hybridization Hybrid Model"),
                            correctOption = "A",
                            explanation = "The Electron Sea Model explains metal conductivity, malleability, and luster via freely flowing delocalized electrons."
                        ),
                        QuizQuestionDto(
                            id = 1284,
                            questionNumber = 4,
                            question = "What is the molecular geometry of a water molecule (H₂O) according to VSEPR theory?",
                            options = mapOf("A" to "Bent (angular, ~104.5°)", "B" to "Linear (180°)", "C" to "Trigonal planar (120°)", "D" to "Tetrahedral (109.5°)"),
                            correctOption = "A",
                            explanation = "Oxygen in H₂O has two bonded hydrogen pairs and two lone pairs, resulting in a bent molecular geometry (~104.5°)."
                        ),
                        QuizQuestionDto(
                            id = 1285,
                            questionNumber = 5,
                            question = "How many shared electrons are present in a triple covalent bond (such as N₂)?",
                            options = mapOf("A" to "6 electrons (3 pairs)", "B" to "3 electrons", "C" to "4 electrons (2 pairs)", "D" to "8 electrons"),
                            correctOption = "A",
                            explanation = "A triple bond shares 3 pairs of valence electrons, totaling 6 shared bonding electrons."
                        )
                    )
                ),
                // Chapter 24: Acids and Bases
                CatalogChapter(
                    chapterId = 24,
                    chapterOrder = 2,
                    title = "Acids and Bases",
                    estimatedMinutes = 20,
                    summary = "pH scale, neutralization, buffers, and Arrhenius/Brønsted definitions.",
                    syllabus = listOf(
                        "Arrhenius vs Brønsted-Lowry Definitions",
                        "pH and pOH Calculations",
                        "Strong vs Weak Acids and Dissociation Constants (Ka)",
                        "Buffer Solutions and Neutralization Titrations"
                    ),
                    coreFormulas = listOf(
                        "pH = -log₁₀[H⁺]",
                        "pOH = -log₁₀[OH⁻]",
                        "pH + pOH = 14 (at 25 °C)",
                        "Henderson-Hasselbalch: pH = pKa + log([A⁻]/[HA])"
                    ),
                    part1Title = "Definitions & The pH Scale",
                    part1Content = "According to Brønsted-Lowry theory, an acid is a proton (H⁺) donor and a base is a proton acceptor. The pH scale quantifies hydrogen ion concentration on a logarithmic scale from 0 to 14.",
                    part2Title = "Neutralization & Buffer Solutions",
                    part2Content = "Neutralization occurs when an acid reacts stoichiometrically with a base to produce salt and water. Buffer solutions resist pH fluctuations upon addition of small amounts of strong acid or base.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1241,
                            questionNumber = 1,
                            question = "What is the pH of a solution with a hydronium ion concentration [H⁺] = 1 × 10⁻⁴ M?",
                            options = mapOf("A" to "4", "B" to "10", "C" to "-4", "D" to "7"),
                            correctOption = "A",
                            explanation = "pH = -log₁₀[10⁻⁴] = -(-4) = 4, indicating an acidic solution."
                        ),
                        QuizQuestionDto(
                            id = 1242,
                            questionNumber = 2,
                            question = "According to the Brønsted-Lowry definition, what is a base?",
                            options = mapOf("A" to "A proton (H⁺) acceptor", "B" to "A proton donor", "C" to "An electron pair donor", "D" to "A substance that produces H⁺ in water"),
                            correctOption = "A",
                            explanation = "Brønsted-Lowry defines acids as proton (H⁺) donors and bases as proton (H⁺) acceptors."
                        ),
                        QuizQuestionDto(
                            id = 1243,
                            questionNumber = 3,
                            question = "What are the typical components of an acidic buffer solution?",
                            options = mapOf("A" to "A weak acid and its conjugate base salt", "B" to "A strong acid and a strong base", "C" to "A strong acid and water", "D" to "Pure sodium chloride solution"),
                            correctOption = "A",
                            explanation = "Buffers require a conjugate weak acid-base pair (e.g. CH₃COOH and CH₃COONa) to absorb added H⁺ and OH⁻ ions."
                        ),
                        QuizQuestionDto(
                            id = 1244,
                            questionNumber = 4,
                            question = "What are the universal products of a neutralization reaction between an Arrhenius acid and base?",
                            options = mapOf("A" to "A salt and water", "B" to "Hydrogen gas and oxygen gas", "C" to "A precipitation complex only", "D" to "An ester and water"),
                            correctOption = "A",
                            explanation = "Neutralization of an acid (e.g. HCl) and base (e.g. NaOH) produces an ionic salt (NaCl) and neutral water (H₂O)."
                        ),
                        QuizQuestionDto(
                            id = 1245,
                            questionNumber = 5,
                            question = "At 25 °C, what is the ion-product constant of water Kw?",
                            options = mapOf("A" to "1.0 × 10⁻¹⁴", "B" to "1.0 × 10⁻⁷", "C" to "1.0 × 10⁻¹", "D" to "14.0"),
                            correctOption = "A",
                            explanation = "In pure water at 25 °C, Kw = [H⁺][OH⁻] = 1.0 × 10⁻¹⁴."
                        )
                    )
                ),
                // Chapter 25: Periodic Table Trends
                CatalogChapter(
                    chapterId = 25,
                    chapterOrder = 3,
                    title = "Periodic Table Trends",
                    estimatedMinutes = 20,
                    summary = "Atomic radii, ionization energy, electron affinity, and shielding effects.",
                    syllabus = listOf(
                        "Atomic & Ionic Radii Periodic Trends",
                        "First Ionization Energy across Periods and Groups",
                        "Effective Nuclear Charge (Z_eff) & Shielding",
                        "Noble Gas Valence Stability & Group Reactivity"
                    ),
                    coreFormulas = listOf(
                        "Z_eff = Z - S (Nuclear charge minus shielding electrons)",
                        "Ionization Energy: X(g) + Energy → X⁺(g) + e⁻",
                        "Electron Affinity: X(g) + e⁻ → X⁻(g) + Energy",
                        "Atomic Radius: Decreases across period, increases down group"
                    ),
                    part1Title = "Atomic Radii & Nuclear Charge",
                    part1Content = "Across a period from left to right, effective nuclear charge increases while valence electrons occupy the same principal quantum shell, pulling electron clouds closer and decreasing atomic radius.",
                    part2Title = "Ionization Energy & Reactivity",
                    part2Content = "Ionization energy measures the energy required to remove an electron from a gaseous atom. It increases across periods and decreases down groups due to distance and electron shielding.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1251,
                            questionNumber = 1,
                            question = "What is the general trend for atomic radius moving from left to right across a periodic table period?",
                            options = mapOf("A" to "Decreases", "B" to "Increases", "C" to "Remains strictly constant", "D" to "Increases then drops to zero"),
                            correctOption = "A",
                            explanation = "Greater nuclear charge (more protons) pulls electrons closer into the nucleus across a period, reducing atomic radius."
                        ),
                        QuizQuestionDto(
                            id = 1252,
                            questionNumber = 2,
                            question = "Why does first ionization energy decrease moving down a group?",
                            options = mapOf("A" to "Valence electrons are further from the nucleus and shielded by inner core electrons", "B" to "Nuclear charge decreases down a group", "C" to "Atoms become non-metals", "D" to "Electronegativity increases down a group"),
                            correctOption = "A",
                            explanation = "Additional principal energy levels increase atomic radius and inner electron shielding, weakening nuclear attraction on valence electrons."
                        ),
                        QuizQuestionDto(
                            id = 1253,
                            questionNumber = 3,
                            question = "Which group of elements possesses complete valence electron octets and exceptional chemical inertness?",
                            options = mapOf("A" to "Noble Gases (Group 18)", "B" to "Alkali Metals (Group 1)", "C" to "Halogens (Group 17)", "D" to "Alkaline Earth Metals (Group 2)"),
                            correctOption = "A",
                            explanation = "Group 18 Noble Gases possess full s²p⁶ valence shells, conferring high thermodynamic stability and low reactivity."
                        ),
                        QuizQuestionDto(
                            id = 1254,
                            questionNumber = 4,
                            question = "How does the radius of a sodium cation (Na⁺) compare to a neutral sodium atom (Na)?",
                            options = mapOf("A" to "Na⁺ is smaller than neutral Na", "B" to "Na⁺ is larger than neutral Na", "C" to "Both have identical radius", "D" to "Cations have zero radius"),
                            correctOption = "A",
                            explanation = "Losing a valence electron removes an outer shell and increases proton-to-electron attraction, making cations smaller than their parent atoms."
                        ),
                        QuizQuestionDto(
                            id = 1255,
                            questionNumber = 5,
                            question = "What term denotes the energy change when an electron is added to a neutral gaseous atom?",
                            options = mapOf("A" to "Electron Affinity", "B" to "Ionization Energy", "C" to "Electronegativity", "D" to "Lattice Energy"),
                            correctOption = "A",
                            explanation = "Electron affinity is defined as the energy released or absorbed when an atom in the gas phase gains an electron to form an anion."
                        )
                    )
                ),
                // Chapter 26: Stoichiometry & Reactions
                CatalogChapter(
                    chapterId = 26,
                    chapterOrder = 4,
                    title = "Stoichiometry & Reactions",
                    estimatedMinutes = 25,
                    summary = "Mole conversions, balancing chemical equations, and limiting reactants.",
                    syllabus = listOf(
                        "The Mole Concept & Avogadro's Number",
                        "Balancing Chemical Equations & Mass Conservation",
                        "Limiting Reactant Identification",
                        "Theoretical vs Actual Percent Yield"
                    ),
                    coreFormulas = listOf(
                        "Moles: n = mass (g) / Molar Mass (g/mol)",
                        "Avogadro's Number: N_A = 6.022 × 10²³ particles/mol",
                        "Molarity: M = moles of solute / liters of solution",
                        "Percent Yield = (Actual Yield / Theoretical Yield) * 100%"
                    ),
                    part1Title = "The Mole & Equation Balancing",
                    part1Content = "Stoichiometry links microscopic chemical reactions with macroscopic masses. Balanced chemical equations respect mass conservation by ensuring atom counts balance across reaction arrows.",
                    part2Title = "Limiting Reactants & Yields",
                    part2Content = "In chemical reactions with mixed proportions, the limiting reactant is completely consumed first, capping maximum theoretical product yield.",
                    mcqs = listOf(
                        QuizQuestionDto(
                            id = 1261,
                            questionNumber = 1,
                            question = "How many representative particles are contained in one mole of any pure substance?",
                            options = mapOf("A" to "6.022 × 10²³ (Avogadro's Number)", "B" to "3.00 × 10⁸", "C" to "1.602 × 10⁻¹⁹", "D" to "9.81 × 10²"),
                            correctOption = "A",
                            explanation = "Avogadro's constant N_A = 6.022 × 10²³ defines the number of constituent units in one mole."
                        ),
                        QuizQuestionDto(
                            id = 1262,
                            questionNumber = 2,
                            question = "What is the molar mass of water (H₂O)? (Atomic masses: H ≈ 1 g/mol, O ≈ 16 g/mol)",
                            options = mapOf("A" to "18 g/mol", "B" to "17 g/mol", "C" to "34 g/mol", "D" to "16 g/mol"),
                            correctOption = "A",
                            explanation = "Molar mass = 2(1.0 g/mol) + 16.0 g/mol = 18.0 g/mol."
                        ),
                        QuizQuestionDto(
                            id = 1263,
                            questionNumber = 3,
                            question = "What is the limiting reactant in a chemical reaction?",
                            options = mapOf("A" to "The reactant that is completely consumed first, limiting product formation", "B" to "The reactant with the highest molar mass", "C" to "The reactant present in the largest gram quantity", "D" to "A catalyst that speeds up reaction rates"),
                            correctOption = "A",
                            explanation = "The limiting reactant runs out first based on stoichiometric molar ratios, stopping the reaction."
                        ),
                        QuizQuestionDto(
                            id = 1264,
                            questionNumber = 4,
                            question = "If an experiment has a theoretical yield of 50 grams and an actual yield of 40 grams, what is the percent yield?",
                            options = mapOf("A" to "80%", "B" to "125%", "C" to "50%", "D" to "90%"),
                            correctOption = "A",
                            explanation = "Percent Yield = (Actual / Theoretical) * 100% = (40 g / 50 g) * 100% = 80%."
                        ),
                        QuizQuestionDto(
                            id = 1265,
                            questionNumber = 5,
                            question = "What is the molarity (M) of a solution containing 2 moles of NaCl dissolved in 4 liters of water?",
                            options = mapOf("A" to "0.5 M", "B" to "2.0 M", "C" to "8.0 M", "D" to "0.25 M"),
                            correctOption = "A",
                            explanation = "Molarity M = moles / volume (L) = 2 moles / 4 L = 0.5 mol/L (0.5 M)."
                        )
                    )
                )
            )
        )
    }
}
