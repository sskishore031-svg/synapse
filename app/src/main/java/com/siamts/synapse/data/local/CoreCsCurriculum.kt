package com.siamts.synapse.data.local

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Embedded university-grade curriculum for Computer Science.
 * 8 progressive college chapters with complete lessons, MCQs, and RAG knowledge chunks.
 */
object CoreCsCurriculum {

    fun getCsSubject(): CatalogSubject {
        return CatalogSubject(
            id = "CS",
            subjectId = 2,
            name = "Computer Science",
            slug = "computer-science",
            iconSymbol = "</>",
            colorHex = "#10B981",
            category = "Technology",
            description = "University computer science covering complexity, algorithms, data structures, operating systems, databases, networks, and compilers.",
            isAdded = true,
            chapters = listOf(
                createChapter1(),
                createChapter2(),
                createChapter3(),
                createChapter4(),
                createChapter5(),
                createChapter6(),
                createChapter7(),
                createChapter8()
            )
        )
    }

    private fun createChapter1(): CatalogChapter {
        return CatalogChapter(
            chapterId = 201,
            chapterOrder = 1,
            title = "Programming Fundamentals, Algorithms & Complexity (O(n))",
            estimatedMinutes = 25,
            summary = "Asymptotic notations (Big-O, Omega, Theta), time-space complexity trade-offs, control flow, memory allocation, and amortized cost.",
            syllabus = listOf("Asymptotic Notation: Big-O, Big-Omega (Ω), Big-Theta (Θ)", "Time & Space Complexity Analysis of Iterative & Recursive Loops", "Memory Architecture: Stack vs Heap Allocation & Pointer Arithmetic", "Amortized Complexity: Aggregate & Potential Method (Dynamic Array Doubling)", "Algorithm Invariants & Correctness Induction Proofs"),
            coreFormulas = listOf("Big-O Upper Bound: f(n) ≤ c * g(n) for all n ≥ n₀", "Big-Omega Lower Bound: f(n) ≥ c * g(n) for all n ≥ n₀", "Big-Theta Tight Bound: c₁ * g(n) ≤ f(n) ≤ c₂ * g(n) for all n ≥ n₀", "Geometric Series Sum: ∑_{i=0}^k r^i = (1 - r^(k+1)) / (1 - r)", "Dynamic Array Amortized Insertion: O(1) amortized per append"),
            part1Title = "Asymptotic Complexity, Memory Hierarchy & Invariants",
            part1Content = "Computational complexity theory provides the analytical framework for measuring algorithm resource efficiency independent of hardware architecture. Asymptotic notations formally bound function growth rates as input size n approaches infinity. Big-O notation characterizes the asymptotic upper bound, guaranteeing that an algorithm will not exceed a specified growth curve c · g(n). Big-Omega (Ω) defines the corresponding lower bound, while Big-Theta (Θ) establishes a tight two-sided sandwich bound where growth rate is asymptotically exact.\n\nTime complexity analysis inspects dominant operation frequency in loop structures and recursive calls. Nested loops with dependent bounds (such as inner loops iterating up to the outer index i) require arithmetic series summation ∑_{i=1}^n i = n(n+1)/2 = O(n²). Space complexity quantifies auxiliary memory overhead, distinguishing between stack frames allocated during recursive call stacks and dynamic heap allocations managed by manual pointers or runtime garbage collectors.\n\nAmortized analysis evaluates algorithms where occasional expensive operations are balanced by frequent inexpensive ones. In dynamic arrays (such as C++ std::vector or Python list), inserting n consecutive elements triggers array doubling when capacity is reached. By the aggregate method, total resizing work across n insertions is ∑_{i=0}^{⌊log₂ n⌋} 2^i < 2n, yielding an amortized time of O(1) per insertion despite individual worst-case O(n) reallocations.\n\nAlgorithm correctness relies on loop invariants: assertions true before loop initialization, preserved across each iteration (maintenance), and establishing the postcondition upon loop termination. Proving loop invariants by mathematical induction guarantees deterministic correctness across all valid input spaces.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Master Theorem: T(n) = a T(n/b) + O(n^d)\n• Logarithm Base Change: log_b(n) = log_a(n) / log_a(b) = O(log n)\n• Stack Frame Size: S(n) = (Stack Frame Bytes) × (Recursion Depth)\n\nWorked Numerical/Code Example:\nProblem: Analyze the time complexity of the following code snippet and find its exact operation count:\n```cpp\nint count = 0;\nfor (int i = 1; i <= n; i *= 2) {\n    for (int j = 1; j <= i; j++) {\n        count++;\n    }\n}\n```\nStep 1: The outer loop runs for values i = 1, 2, 4, 8, ..., 2^k where 2^k ≤ n. Total outer iterations k + 1 = ⌊log₂ n⌋ + 1.\nStep 2: For each outer iteration, the inner loop executes exactly i times.\nStep 3: Total operations = ∑_{m=0}^{⌊log₂ n⌋} 2^m.\nStep 4: Using the geometric series sum formula ∑_{m=0}^k 2^m = 2^(k+1) - 1.\nStep 5: Since 2^k ≤ n < 2^(k+1), the sum satisfies 2^(k+1) - 1 ≤ 2n - 1 = O(n).\nConclusion: Despite the nested loop structure, the total time complexity is strictly linear O(n), NOT O(n log n).\n\nReal-World Applications:\nComplexity analysis dictates architecture decisions in high-performance computing. Database index lookups require O(log n) B-tree traversals to meet sub-millisecond query service level agreements (SLAs). Real-time trading engines mandate O(1) algorithmic bounds to prevent latency spikes, while memory footprint constraints govern embedded firmware development.",
            examTip = "University Exam Tip: When analyzing nested loops where the loop variable multiplies or divides (e.g. i *= 2), the number of iterations is logarithmic O(log n). Never assume nested loops are automatically O(n²) without writing out the exact summation bounds.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2001,
                    questionNumber = 1,
                    question = "What is the tight asymptotic time complexity of a loop where the outer variable doubles (i = 1; i <= n; i *= 2) and the inner loop runs from 1 to i?",
                    options = mapOf("A" to "O(n)", "B" to "O(n log n)", "C" to "O(n²)", "D" to "O(log n)"),
                    correctOption = "A",
                    explanation = "The inner loop executes 1 + 2 + 4 + ... + 2^k times. This geometric series sums to 2^(k+1) - 1 < 2n, which is strictly O(n)."
                ),
                QuizQuestionDto(
                    id = 2002,
                    questionNumber = 2,
                    question = "What does Big-Omega (Ω) notation formally describe in algorithm analysis?",
                    options = mapOf("A" to "The asymptotic lower bound of algorithm growth rate", "B" to "The absolute worst-case scenario only", "C" to "The memory consumption exclusively", "D" to "The compiler optimization level"),
                    correctOption = "A",
                    explanation = "Big-Omega Ω(g(n)) defines the asymptotic lower bound: f(n) ≥ c · g(n) for all n ≥ n₀, meaning the algorithm cannot run faster than this rate asymptotically."
                ),
                QuizQuestionDto(
                    id = 2003,
                    questionNumber = 3,
                    question = "What is the amortized time complexity of appending an element to a dynamically resizing array that doubles capacity when full?",
                    options = mapOf("A" to "O(1)", "B" to "O(n)", "C" to "O(log n)", "D" to "O(n²)"),
                    correctOption = "A",
                    explanation = "While occasional doubling costs O(n), it occurs exponentially infrequently. The total cost of n insertions is less than 2n, giving an amortized cost of O(1) per append."
                ),
                QuizQuestionDto(
                    id = 2004,
                    questionNumber = 4,
                    question = "What is the auxiliary space complexity of a naive recursive Fibonacci function fib(n) that branches twice at each step without memoization?",
                    options = mapOf("A" to "O(n) auxiliary call stack space", "B" to "O(2^n) auxiliary space", "C" to "O(1) auxiliary space", "D" to "O(log n) auxiliary space"),
                    correctOption = "A",
                    explanation = "Although the total number of function calls is exponential O(2^n), the maximum depth of the active recursion call stack at any instant is n, requiring O(n) stack memory."
                ),
                QuizQuestionDto(
                    id = 2005,
                    questionNumber = 5,
                    question = "Which memory segment is automatically managed in Last-In First-Out (LIFO) order for local function variables and call return addresses?",
                    options = mapOf("A" to "Call Stack", "B" to "Heap Segment", "C" to "Data / BSS Segment", "D" to "Text / Code Segment"),
                    correctOption = "A",
                    explanation = "The call stack automatically allocates stack frames for function scopes and parameters in LIFO order upon function invocation, popping them upon return."
                ),
                QuizQuestionDto(
                    id = 2006,
                    questionNumber = 6,
                    question = "If an algorithm requires 3n³ + 100n² + 5000 operations, what is its asymptotic Big-O complexity?",
                    options = mapOf("A" to "O(n³)", "B" to "O(n²)", "C" to "O(100n²)", "D" to "O(3n³)"),
                    correctOption = "A",
                    explanation = "In asymptotic analysis, constant coefficients and lower-order polynomial terms are dominated by the highest-degree term as n → ∞. Thus 3n³ + 100n² + 5000 = O(n³)."
                ),
                QuizQuestionDto(
                    id = 2007,
                    questionNumber = 7,
                    question = "What is a loop invariant?",
                    options = mapOf("A" to "A condition that is true before and after each iteration of a loop", "B" to "A variable whose value cannot be modified", "C" to "An infinite loop that never terminates", "D" to "A hardware interrupt mechanism"),
                    correctOption = "A",
                    explanation = "A loop invariant is a formal predicate that holds true prior to initialization, is maintained during each iteration, and proves algorithm correctness upon loop exit."
                ),
                QuizQuestionDto(
                    id = 2008,
                    questionNumber = 8,
                    question = "What happens if a recursive function lacks a well-defined base case?",
                    options = mapOf("A" to "Stack Overflow Exception due to infinite call stack exhaustion", "B" to "Memory fragmentation in the heap", "C" to "Compiler optimization syntax error", "D" to "Automatic memoization conversion"),
                    correctOption = "A",
                    explanation = "Without a base case, the function recursively invokes itself indefinitely until the allocated thread call stack memory is exhausted, throwing a Stack Overflow error."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH1_001",
                    subject = "Computer Science",
                    chapter = "Programming Fundamentals & Complexity (O(n))",
                    topic = "Asymptotic Complexity and Big-O Notation",
                    keywords = listOf("computer science", "big o", "complexity", "time complexity", "space complexity", "amortized", "stack vs heap"),
                    definition = "Big-O notation quantifies algorithm scalability by bounding upper resource growth: f(n) <= c * g(n) for all n >= n0.",
                    formulaOrCoreRule = "Big-O: f(n) <= c * g(n); Big-Omega: f(n) >= c * g(n); Amortized Array Doubling: O(1) per append.",
                    workedExample = "A loop doubling i from 1 to n with an inner loop from 1 to i executes sum 2^m < 2n operations, yielding linear O(n) complexity.",
                    content = "Computational complexity analysis guides algorithm selection in database indexes, real-time operating systems, and high-frequency trading."
                )
            )
        )
    }

    private fun createChapter2(): CatalogChapter {
        return CatalogChapter(
            chapterId = 202,
            chapterOrder = 2,
            title = "Arrays, Linked Lists & Recursion",
            estimatedMinutes = 25,
            summary = "Contiguous vs node-based memory, singly/doubly linked lists, pointer manipulation, recursion call stacks, and divide-and-conquer.",
            syllabus = listOf("Contiguous Array Memory: Index Arithmetic, Cache Locality & Row-Major Order", "Singly & Doubly Linked Lists: Insertion, Deletion & Sentinel Nodes", "Fast & Slow Pointer Technique (Floyd's Cycle Finding Algorithm)", "Recursion Trees, Tail Call Optimization (TCO) & Stack Frames", "Divide-and-Conquer Sorting: Merge Sort & Quick Sort Partitioning"),
            coreFormulas = listOf("Array Element Address: Address(A[i]) = Base_Address + i * sizeof(Element)", "2D Row-Major Address: Address(A[i][j]) = Base + (i * num_cols + j) * sizeof(Type)", "Merge Sort Recurrence: T(n) = 2T(n/2) + O(n) ⇒ O(n log n)", "Quick Sort Average: T(n) = 2T(n/2) + O(n) ⇒ O(n log n); Worst Case: O(n²)", "Floyd's Cycle Detection: Fast pointer moves 2 steps, Slow pointer moves 1 step"),
            part1Title = "Memory Topologies, Pointer Dynamics & Recursive Mechanics",
            part1Content = "Linear data structures organize sequences of elements under contrasting memory allocation paradigms. Arrays allocate contiguous blocks of physical memory, enabling O(1) random access via arithmetic index calculation Address(A[i]) = Base + i · sizeof(T). Contiguous memory layout maximizes CPU hardware cache line prefetching (spatial and temporal locality), dramatically accelerating linear traversals relative to pointer-chasing structures.\n\nLinked lists allocate nodes dynamically across disjoint heap locations, with each node encapsulating a data payload and one or more pointer references (singly vs doubly linked). Insertion or deletion adjacent to a known node executes in O(1) time without element shifting, but accessing the k-th node requires O(k) sequential pointer dereferencing. Circular linked lists eliminate null terminators, while sentinel dummy nodes simplify boundary edge cases during head and tail mutations.\n\nPointer manipulation algorithms solve complex topological problems in linear time with minimal auxiliary memory. Floyd's cycle-finding algorithm (the tortoise and hare) detects loops in linked lists by advancing two pointers at speeds of 1 and 2 nodes per step. If a cycle exists, the pointers must intersect inside the loop; repositioning one pointer to the head and advancing both at unit speed identifies the exact entry node of the cycle.\n\nRecursion solves problems by decomposing instances into smaller sub-problems of the identical type until reaching base cases. Each recursive invocation allocates a new activation frame on the system call stack storing local variables and return program counters. Tail Call Optimization (TCO) allows compilers to replace the final recursive call with an iterative jump, eliminating stack frame growth and preventing stack overflow in languages supporting functional paradigms.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Merge Sort: T(n) = 2T(n/2) + O(n) ⇒ O(n log n) time, O(n) auxiliary space\n• Quick Sort: Average O(n log n), Worst-case O(n²) when pivot partitions trivially\n• Cycle Detection: Loop length L = steps taken for fast and slow pointer to meet again\n\nWorked Numerical/Code Example:\nProblem: Trace Floyd's cycle detection on a linked list with 5 nodes before a cycle of 3 nodes: [1]->[2]->[3]->[4]->[5]->[6]->[7]->[8]->[6].\nStep 1: Node indices: Path before cycle: 1, 2, 3, 4, 5 (length μ = 5). Cycle nodes: 6, 7, 8 (length λ = 3).\nStep 2: Initialize Slow = 1, Fast = 1.\n• Step 1: Slow = 2, Fast = 3\n• Step 2: Slow = 3, Fast = 5\n• Step 3: Slow = 4, Fast = 7\n• Step 4: Slow = 5, Fast = 6\n• Step 5: Slow = 6, Fast = 8\n• Step 6: Slow = 7, Fast = 7 (Meeting Point found at node 7!).\nStep 3: Finding cycle start: Keep Fast at node 7, reset Slow to head (node 1). Advance both 1 step at a time:\n• Step 1: Slow = 2, Fast = 8\n• Step 2: Slow = 3, Fast = 6\n• Step 3: Slow = 4, Fast = 7\n• Step 4: Slow = 5, Fast = 8\n• Step 5: Slow = 6, Fast = 6 (Intersection at node 6! Exactly the cycle entry).\n\nReal-World Applications:\nLinked list pointers form the basis of operating system memory free-lists (malloc headers) and LRU cache implementations (combining hash maps with doubly linked lists). Contiguous arrays power numerical linear algebra libraries (BLAS/LAPACK, NumPy) utilizing SIMD vector registers.",
            examTip = "University Exam Tip: In coding and viva exams, always handle linked list edge cases: empty list (head == null), single-node list, and deleting the head node. Using a dummy sentinel node eliminates null pointer exceptions in 90% of list interview problems.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2009,
                    questionNumber = 1,
                    question = "What is the primary memory advantage of a contiguous array over a singly linked list?",
                    options = mapOf("A" to "O(1) random index access and superior CPU cache spatial locality", "B" to "O(1) insertion at the front without shifting elements", "C" to "Zero memory overhead for storing elements", "D" to "Automatic dynamic resizing without memory reallocation"),
                    correctOption = "A",
                    explanation = "Arrays store elements contiguously in physical RAM, enabling instantaneous direct index addressing Address(A[i]) and optimal cache line prefetching."
                ),
                QuizQuestionDto(
                    id = 2010,
                    questionNumber = 2,
                    question = "In Floyd's Cycle Detection Algorithm, if the slow pointer moves 1 node per step, how many nodes does the fast pointer move per step?",
                    options = mapOf("A" to "2 nodes per step", "B" to "3 nodes per step", "C" to "1 node per step", "D" to "Depends on the list length"),
                    correctOption = "A",
                    explanation = "Floyd's algorithm advances the fast pointer at twice the rate of the slow pointer (2 steps per iteration), guaranteeing intersection within the loop if a cycle exists."
                ),
                QuizQuestionDto(
                    id = 2011,
                    questionNumber = 3,
                    question = "What is the worst-case time complexity of Quick Sort, and under what condition does it occur?",
                    options = mapOf("A" to "O(n²), when the selected pivot is always the extreme minimum or maximum element", "B" to "O(n log n), when the array is already sorted", "C" to "O(n), when all elements are distinct", "D" to "O(log n), when using Lomuto partitioning"),
                    correctOption = "A",
                    explanation = "When the pivot divides the array into subproblems of size 0 and n - 1 (such as picking the first element of an already sorted array), Quick Sort degrades to O(n²)."
                ),
                QuizQuestionDto(
                    id = 2012,
                    questionNumber = 4,
                    question = "What is the time complexity of deleting a node from a singly linked list when given only a pointer directly to that node (not the head)?",
                    options = mapOf("A" to "O(1) by copying data from the next node and deleting the next node", "B" to "O(n) because the predecessor cannot be found without traversing", "C" to "Impossible in a singly linked list", "D" to "O(log n)"),
                    correctOption = "A",
                    explanation = "By copying next->data into current->data and updating current->next = next->next, the target node is effectively removed in O(1) time (provided it is not the tail)."
                ),
                QuizQuestionDto(
                    id = 2013,
                    questionNumber = 5,
                    question = "What optimization technique allows a compiler to execute tail-recursive functions with O(1) stack space?",
                    options = mapOf("A" to "Tail Call Optimization (TCO)", "B" to "Loop unrolling", "C" to "Static memory allocation", "D" to "Garbage collection"),
                    correctOption = "A",
                    explanation = "Tail Call Optimization detects when a recursive invocation is the final operation in a function, reusing the existing stack frame rather than pushing a new one."
                ),
                QuizQuestionDto(
                    id = 2014,
                    questionNumber = 6,
                    question = "What is the space complexity of standard Merge Sort on an array of size n?",
                    options = mapOf("A" to "O(n) auxiliary memory for merging subarrays", "B" to "O(1) in-place memory", "C" to "O(log n) total space", "D" to "O(n²)"),
                    correctOption = "A",
                    explanation = "Standard array Merge Sort requires an auxiliary buffer of size n to merge two sorted halves, giving O(n) space complexity."
                ),
                QuizQuestionDto(
                    id = 2015,
                    questionNumber = 7,
                    question = "In a 2D array of integers (4 bytes each) declared as A[10][20] in row-major order with base address 1000, what is the address of A[3][5]?",
                    options = mapOf("A" to "1000 + (3 * 20 + 5) * 4 = 1260", "B" to "1000 + (3 * 10 + 5) * 4 = 1140", "C" to "1000 + (5 * 20 + 3) * 4 = 1412", "D" to "1200"),
                    correctOption = "A",
                    explanation = "Row-major address = Base + (row * num_cols + col) * sizeof(element) = 1000 + (3 * 20 + 5) * 4 = 1000 + 65 * 4 = 1260."
                ),
                QuizQuestionDto(
                    id = 2016,
                    questionNumber = 8,
                    question = "What is the utility of a dummy sentinel node at the head of a linked list?",
                    options = mapOf("A" to "Eliminates special-case code when inserting or deleting the first node", "B" to "Reduces time complexity from O(n) to O(1)", "C" to "Doubles the available capacity of the list", "D" to "Enables binary search on linked lists"),
                    correctOption = "A",
                    explanation = "A dummy head sentinel ensures that every actual data node has a valid preceding pointer, removing the need to check 'if (head == null)' during insertions and deletions."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH2_001",
                    subject = "Computer Science",
                    chapter = "Arrays, Linked Lists & Recursion",
                    topic = "Linked Lists, Pointers and Floyd's Cycle Algorithm",
                    keywords = listOf("linked list", "array", "recursion", "floyd cycle", "tortoise and hare", "pointers", "tail call optimization"),
                    definition = "Arrays provide O(1) contiguous indexed memory access. Linked lists link disjoint heap nodes via pointers, enabling O(1) local insertions.",
                    formulaOrCoreRule = "Row-Major Address: Base + (i * cols + j) * size; Floyd Cycle: Slow advances 1, Fast advances 2 steps.",
                    workedExample = "In a list of 5 initial nodes followed by a 3-node cycle, Slow and Fast meet at node 7; advancing Slow from head identifies cycle entry at node 6.",
                    content = "Pointers and linear structures form the foundation of operating system free lists, kernel buffers, and LRU memory caches."
                )
            )
        )
    }

    private fun createChapter3(): CatalogChapter {
        return CatalogChapter(
            chapterId = 203,
            chapterOrder = 3,
            title = "Stacks, Queues, Trees & BSTs",
            estimatedMinutes = 25,
            summary = "LIFO stacks, FIFO queues, circular buffers, binary trees, binary search trees (BST), and self-balancing AVL tree rotations.",
            syllabus = listOf("Stack Mechanics (LIFO): Call Frames, Postfix Evaluation & Monotonic Stacks", "Queue Architectures (FIFO): Circular Ring Buffers & Deques", "Binary Tree Traversals: In-order, Pre-order, Post-order & Level-Order (BFS)", "Binary Search Trees (BST): Insertion, Deletion & Search Properties", "Self-Balancing Trees: AVL Balance Factor & Tree Rotations (LL, RR, LR, RL)"),
            coreFormulas = listOf("AVL Balance Factor: BF(node) = Height(Left_Subtree) - Height(Right_Subtree) ∈ {-1, 0, 1}", "BST Search Complexity: Best/Average = O(log n), Worst (degenerate line) = O(n)", "Full Binary Tree Node Relation: Number of leaf nodes L = Internal nodes I + 1", "Maximum Nodes at Depth d: 2^d nodes (root at depth 0)", "Circular Queue Indexing: Front = (Front + 1) % Capacity, Rear = (Rear + 1) % Capacity"),
            part1Title = "Hierarchical Data Structures, Balancing Rotations & Traversals",
            part1Content = "Stacks and queues represent restricted linear structures governing access order. A stack enforces Last-In First-Out (LIFO) discipline, supporting push and pop in O(1) time. Stacks underpin expression evaluation (converting infix to postfix notation via Dijkstra's Shunting-Yard algorithm), syntax parenthesis parsing, and depth-first search (DFS). A queue enforces First-In First-Out (FIFO) discipline, essential for task scheduling and breadth-first search (BFS). Circular ring buffers implement bounded queues efficiently by wrapping head and tail indices via modular arithmetic.\n\nTrees generalize linear structures into hierarchical branching graphs. A binary tree restricts every node to at most two children. Tree traversals visit nodes deterministically: depth-first traversals include pre-order (root-left-right), in-order (left-root-right), and post-order (left-right-root), while breadth-first traversal (level-order) visits nodes layer by layer using an auxiliary FIFO queue.\n\nA Binary Search Tree (BST) enforces the ordering invariant: for every node x, all keys in its left subtree are strictly less than x.key, and all keys in its right subtree are strictly greater than x.key. An in-order traversal of a valid BST produces keys in strictly monotonically increasing sorted order. While average search, insertion, and deletion operate in O(log n) time, unbalanced insertions can degrade a BST into a degenerate linked list of height O(n).\n\nSelf-balancing binary search trees prevent worst-case degeneration. An AVL tree maintains the invariant that the heights of the two child subtrees of any node differ by at most one (Balance Factor BF ∈ {-1, 0, 1}). When an insertion or deletion violates this invariant (|BF| > 1), balance is restored via local O(1) tree rotations: single rotations (Left-Left, Right-Right) or double rotations (Left-Right, Right-Left), strictly bounding overall tree height to O(log n) under all mutation sequences.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• AVL Balance Factor: BF = Height(Left) - Height(Right)\n• Left Rotation on node x: x's right child y becomes new root; y's left subtree becomes x's right subtree; x becomes y's left child\n• Tree Height Bound: Height h ≤ 1.44 log₂(n + 2) - 0.328\n\nWorked Numerical Example:\nProblem: Insert keys [30, 20, 10] into an initially empty AVL tree and restore balance via rotation.\nStep 1: Insert 30: Root node 30, BF = 0, Height = 1.\nStep 2: Insert 20: 20 < 30 ⇒ Left child of 30. BF(30) = Height(Left) - Height(Right) = 1 - 0 = +1. Height = 2.\nStep 3: Insert 10: 10 < 20 ⇒ Left child of 20. Tree structure: 30 -> Left: 20 -> Left: 10.\nStep 4: Check balance factors:\n• BF(10) = 0 - 0 = 0\n• BF(20) = 1 - 0 = +1\n• BF(30) = Height(20) - 0 = 2 - 0 = +2 (Violation at root 30!).\nStep 5: Identify rotation case: Node 20 is left child of 30, node 10 is left child of 20 ⇒ Left-Left (LL) Case.\nStep 6: Execute single Right Rotation on node 30:\n• Node 20 becomes the new root.\n• Node 30 becomes the right child of 20.\n• Node 10 remains the left child of 20.\nStep 7: New balance factors: BF(10) = 0, BF(30) = 0, BF(20) = 1 - 1 = 0. Tree is perfectly balanced with height 2!\n\nReal-World Applications:\nTrees and stacks are foundational across software engineering. The call stack manages thread function execution. Abstract Syntax Trees (ASTs) represent parsed source code in compilers. Database engines utilize B-trees and self-balancing trees to index billions of records on disk. Operating system process schedulers use priority queues (heaps) to select the next runnable thread.",
            examTip = "University Exam Tip: An in-order traversal of a Binary Search Tree ALWAYS yields elements in ascending sorted order. If an exam question asks you to reconstruct a BST from traversals, in-order gives you the sorted sequence for free!",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2017,
                    questionNumber = 1,
                    question = "What is the result of performing an in-order traversal on a valid Binary Search Tree (BST)?",
                    options = mapOf("A" to "Keys printed in strictly ascending sorted order", "B" to "Keys printed in descending sorted order", "C" to "Keys printed in reverse level order", "D" to "Root printed first followed by leaves"),
                    correctOption = "A",
                    explanation = "Because in-order visits Left Subtree (< root), Root, and Right Subtree (> root), the traversal naturally outputs elements in sorted ascending order."
                ),
                QuizQuestionDto(
                    id = 2018,
                    questionNumber = 2,
                    question = "What is the allowable range of the Balance Factor BF = Height(Left) - Height(Right) in a valid AVL tree?",
                    options = mapOf("A" to "{-1, 0, 1}", "B" to "{0, 1, 2}", "C" to "{-2, 0, 2}", "D" to "Any positive integer"),
                    correctOption = "A",
                    explanation = "By definition, an AVL tree requires the height difference between child subtrees of any node to be at most 1, meaning BF must be -1, 0, or 1."
                ),
                QuizQuestionDto(
                    id = 2019,
                    questionNumber = 3,
                    question = "Which rotation sequence is required when a node is inserted into the right subtree of the left child, causing a balance factor violation (+2)?",
                    options = mapOf("A" to "Left-Right (LR) Double Rotation", "B" to "Single Left (LL) Rotation", "C" to "Single Right (RR) Rotation", "D" to "Right-Left (RL) Double Rotation"),
                    correctOption = "A",
                    explanation = "The LR imbalance occurs when a key is added to the right of the left child. It is corrected by a left rotation on the child followed by a right rotation on the parent."
                ),
                QuizQuestionDto(
                    id = 2020,
                    questionNumber = 4,
                    question = "Which data structure is fundamentally utilized to implement Breadth-First Search (BFS) level-order traversal of a tree?",
                    options = mapOf("A" to "FIFO Queue", "B" to "LIFO Stack", "C" to "Hash Map", "D" to "Disjoint Set Union"),
                    correctOption = "A",
                    explanation = "Level-order BFS traversal requires visiting nodes in the exact order they are discovered layer by layer, which is strictly governed by a FIFO Queue."
                ),
                QuizQuestionDto(
                    id = 2021,
                    questionNumber = 5,
                    question = "What is the worst-case search time complexity in an unbalanced Binary Search Tree with n elements?",
                    options = mapOf("A" to "O(n)", "B" to "O(log n)", "C" to "O(1)", "D" to "O(n log n)"),
                    correctOption = "A",
                    explanation = "If elements are inserted in strictly sorted order, an unbalanced BST degenerates into a linear linked list of height n, resulting in O(n) worst-case search time."
                ),
                QuizQuestionDto(
                    id = 2022,
                    questionNumber = 6,
                    question = "In a circular queue implemented in an array of size C with front and rear pointers, how is rear updated upon enqueue?",
                    options = mapOf("A" to "rear = (rear + 1) % C", "B" to "rear = rear + 1", "C" to "rear = (rear - 1) % C", "D" to "rear = front + C"),
                    correctOption = "A",
                    explanation = "To wrap around to index 0 when reaching the end of the array buffer, circular queues use modular arithmetic: rear = (rear + 1) % Capacity."
                ),
                QuizQuestionDto(
                    id = 2023,
                    questionNumber = 7,
                    question = "What does a postfix (Reverse Polish) expression evaluation algorithm use to store operands during evaluation?",
                    options = mapOf("A" to "A LIFO Stack", "B" to "A FIFO Queue", "C" to "A Binary Heap", "D" to "An Adjacency Matrix"),
                    correctOption = "A",
                    explanation = "Postfix evaluation reads tokens sequentially: operands are pushed onto a stack, and when an operator is encountered, the top two operands are popped, evaluated, and the result pushed back."
                ),
                QuizQuestionDto(
                    id = 2024,
                    questionNumber = 8,
                    question = "How many leaf nodes does a full binary tree with 7 internal nodes possess?",
                    options = mapOf("A" to "8", "B" to "7", "C" to "14", "D" to "15"),
                    correctOption = "A",
                    explanation = "In any full binary tree (where every internal node has exactly 2 children), the number of leaf nodes L is always equal to internal nodes I + 1. Here L = 7 + 1 = 8."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH3_001",
                    subject = "Computer Science",
                    chapter = "Stacks, Queues, Trees & BSTs",
                    topic = "Binary Search Trees and AVL Rotations",
                    keywords = listOf("stack", "queue", "binary search tree", "bst", "avl tree", "tree rotations", "in-order traversal"),
                    definition = "A BST maintains the invariant that left subtree keys are smaller and right subtree keys are greater than the node. AVL trees self-balance via rotations.",
                    formulaOrCoreRule = "AVL Balance: BF = Height(Left) - Height(Right) in {-1, 0, 1}; Full Tree Leaves: L = I + 1; Circular Index: (i + 1) % C.",
                    workedExample = "Inserting [30, 20, 10] causes an LL imbalance at 30 (BF = +2). A single right rotation makes 20 the root with children 10 and 30 (height 2).",
                    content = "Trees and stacks govern compiler expression evaluation, database B-tree indexing, and memory hierarchy call stacks."
                )
            )
        )
    }

    private fun createChapter4(): CatalogChapter {
        return CatalogChapter(
            chapterId = 204,
            chapterOrder = 4,
            title = "Graphs & Dynamic Programming",
            estimatedMinutes = 25,
            summary = "Graph representations, BFS/DFS, topological sorting, DAGs, optimal substructure, overlapping subproblems, memoization, and DP tabulation.",
            syllabus = listOf("Graph Storage: Adjacency Matrix vs Adjacency List Complexity", "Graph Traversals: BFS (Queue) & DFS (Recursion/Stack) Path Finding", "Directed Acyclic Graphs (DAGs) & Kahn's Topological Sorting Algorithm", "Dynamic Programming Foundations: Optimal Substructure & Overlapping Subproblems", "Canonical DP Paradigms: 0/1 Knapsack, Longest Common Subsequence (LCS) & Coin Change"),
            coreFormulas = listOf("0/1 Knapsack Recurrence: DP[i, w] = max(DP[i-1, w], DP[i-1, w - w_i] + v_i)", "LCS Recurrence: If X[i] == Y[j] ⇒ 1 + DP[i-1, j-1]; Else max(DP[i-1, j], DP[i, j-1])", "Coin Change Recurrence: DP[a] = min_{c ∈ Coins} (DP[a - c] + 1) with DP[0] = 0", "BFS / DFS Time Complexity: O(|V| + |E|) with adjacency list", "Topological Sort Validity: Exists if and only if the graph has no directed cycles (DAG)"),
            part1Title = "Graph Representations, Topological Ordering & Dynamic Programming",
            part1Content = "Graphs model networks of interconnected relationships. Storage efficiency depends on graph density: dense graphs (|E| ≈ |V|²) are represented via an adjacency matrix consuming O(|V|²) memory with O(1) edge lookups, whereas sparse graphs (|E| ≪ |V|²) utilize adjacency lists consuming O(|V| + |E|) memory, optimal for graph traversals. Breadth-First Search (BFS) explores vertices in concentric distance shells using a FIFO queue, discovering shortest paths in unweighted graphs. Depth-First Search (DFS) dives deeply along vertex branches using recursion, classifying edges into tree, back, forward, and cross edges.\n\nA Directed Acyclic Graph (DAG) possesses directional edges with no closed cycles. Topological sorting establishes a linear ordering of vertices such that for every directed edge u → v, vertex u precedes v in the sequence. Kahn's algorithm implements topological sorting by maintaining in-degrees of all vertices, iteratively enqueuing zero in-degree vertices into a worklist. If the number of processed vertices equals |V|, the graph is a valid DAG; otherwise, a directed cycle exists, making topological sorting impossible.\n\nDynamic Programming (DP) solves complex optimization problems by decomposing them into overlapping subproblems exhibiting optimal substructure—where the optimal solution to the global problem is composed of optimal solutions to its subproblems. Unlike greedy algorithms which make locally irrevocable choices, DP exhaustively evaluates decision spaces without redundant computation by caching subproblem results.\n\nDP implementations follow two primary methodologies: top-down memoization (recursive decomposition with lookup caches) and bottom-up tabulation (iterative filling of multi-dimensional tables in dependency order). In canonical problems such as the 0/1 Knapsack problem and Longest Common Subsequence (LCS), bottom-up state tables eliminate call-stack overhead, transforming intractable exponential O(2^n) brute-force searches into highly efficient polynomial-time O(n · W) solutions.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• 0/1 Knapsack Table: DP[i][w] = max(DP[i-1][w], DP[i-1][w - weight[i]] + value[i])\n• Space-Optimized Knapsack: Update 1D array backwards from W down to weight[i]\n• Kahn's Algorithm: In-degree array in_deg[u]; Enqueue when in_deg[u] == 0\n\nWorked Numerical Example:\nProblem: Solve the 0/1 Knapsack problem with capacity W = 4 and items:\n• Item 1: Weight = 1, Value = \$1\n• Item 2: Weight = 2, Value = \$4\n• Item 3: Weight = 3, Value = \$6\nStep 1: Construct DP table of size (4 items) × (capacity 0 to 4). Base row 0 has all zeros.\nStep 2: Process Item 1 (w=1, v=1):\n• w=0: 0; w=1: max(0, 0+1)=1; w=2: 1; w=3: 1; w=4: 1.\nStep 3: Process Item 2 (w=2, v=4):\n• w=0: 0; w=1: 1\n• w=2: max(DP[1][2], DP[1][0] + 4) = max(1, 0+4) = 4\n• w=3: max(DP[1][3], DP[1][1] + 4) = max(1, 1+4) = 5\n• w=4: max(DP[1][4], DP[1][2] + 4) = max(1, 1+4) = 5.\nStep 4: Process Item 3 (w=3, v=6):\n• w=0: 0; w=1: 1; w=2: 4\n• w=3: max(DP[2][3], DP[2][0] + 6) = max(5, 0+6) = 6\n• w=4: max(DP[2][4], DP[2][1] + 6) = max(5, 1+6) = 7.\nFinal Optimal Value: DP[3][4] = \$7 (Achieved by taking Item 1 [w=1, v=1] and Item 3 [w=3, v=6] for total weight 4).\n\nReal-World Applications:\nGraph theory and DP govern mission-critical enterprise systems. Package managers (npm, pip, Maven) use topological sorting to resolve software dependency build orders. Version control systems (git) represent commit histories as DAGs. The Needleman-Wunsch DP algorithm aligns DNA sequences in bioinformatics, while speech recognition uses the Viterbi DP algorithm to decode hidden Markov models.",
            examTip = "University Exam Tip: In the 0/1 Knapsack problem, if optimizing space to a 1D array, you MUST iterate the capacity loop backwards (from W down to weight[i]). Iterating forwards accidentally allows multiple copies of the same item to be selected (which solves the Unbounded Knapsack problem, not 0/1!).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2025,
                    questionNumber = 1,
                    question = "What two fundamental characteristics must a problem possess to be solvable via Dynamic Programming?",
                    options = mapOf("A" to "Overlapping subproblems and optimal substructure", "B" to "Linear time complexity and greedy choice property", "C" to "Divide-and-conquer and disjoint subproblems", "D" to "Binary branching and acyclic vertices"),
                    correctOption = "A",
                    explanation = "Dynamic Programming applies strictly when a global optimal solution can be constructed from optimal subproblem solutions (optimal substructure) and subproblems recur repeatedly (overlapping subproblems)."
                ),
                QuizQuestionDto(
                    id = 2026,
                    questionNumber = 2,
                    question = "For items with weights [1, 2, 3] and values [1, 4, 6], what is the maximum value achievable in a 0/1 Knapsack of capacity 4?",
                    options = mapOf("A" to "7", "B" to "6", "C" to "5", "D" to "8"),
                    correctOption = "A",
                    explanation = "Selecting Item 1 (weight 1, value 1) and Item 3 (weight 3, value 6) gives total weight 1 + 3 = 4 and total value 1 + 6 = 7."
                ),
                QuizQuestionDto(
                    id = 2027,
                    questionNumber = 3,
                    question = "Which algorithm finds topological ordering in a Directed Acyclic Graph (DAG) by iteratively tracking in-degrees?",
                    options = mapOf("A" to "Kahn's Algorithm", "B" to "Prim's Algorithm", "C" to "Bellman-Ford Algorithm", "D" to "Floyd-Warshall Algorithm"),
                    correctOption = "A",
                    explanation = "Kahn's algorithm maintains vertex in-degrees, continuously enqueuing vertices with in-degree 0 and decrementing neighbors to produce topological ordering."
                ),
                QuizQuestionDto(
                    id = 2028,
                    questionNumber = 4,
                    question = "What is the time complexity of Breadth-First Search (BFS) on a graph with V vertices and E edges stored as an adjacency list?",
                    options = mapOf("A" to "O(V + E)", "B" to "O(V²)", "C" to "O(E log V)", "D" to "O(V * E)"),
                    correctOption = "A",
                    explanation = "BFS visits every vertex once and inspects every adjacent edge once via the adjacency list, yielding linear O(V + E) complexity."
                ),
                QuizQuestionDto(
                    id = 2029,
                    questionNumber = 5,
                    question = "Why does a 1D space-optimized 0/1 Knapsack table require iterating the weight loop in reverse order (from W down to w_i)?",
                    options = mapOf("A" to "To prevent using the current item multiple times in the same step", "B" to "To sort the items by weight", "C" to "To avoid division by zero", "D" to "To speed up loop execution"),
                    correctOption = "A",
                    explanation = "Iterating backwards ensures that DP[w - w_i] represents values from the previous iteration, preventing an item from being added multiple times."
                ),
                QuizQuestionDto(
                    id = 2030,
                    questionNumber = 6,
                    question = "If a directed graph contains a cycle, what happens when Kahn's topological sort is attempted?",
                    options = mapOf("A" to "The queue becomes empty before all V vertices are processed", "B" to "The algorithm enters an infinite loop", "C" to "The algorithm returns an arbitrary ordering", "D" to "The graph is automatically partitioned into two components"),
                    correctOption = "A",
                    explanation = "Cycle vertices never reach an in-degree of 0, so the queue terminates early with processed count < V, detecting the cycle."
                ),
                QuizQuestionDto(
                    id = 2031,
                    questionNumber = 7,
                    question = "What is the length of the Longest Common Subsequence (LCS) between strings 'ABCDE' and 'ACE'?",
                    options = mapOf("A" to "3 ('ACE')", "B" to "2 ('AC')", "C" to "5", "D" to "1"),
                    correctOption = "A",
                    explanation = "The subsequence 'ACE' appears in order in both 'ABCDE' and 'ACE', giving an LCS length of 3."
                ),
                QuizQuestionDto(
                    id = 2032,
                    questionNumber = 8,
                    question = "What is the primary difference between top-down memoization and bottom-up tabulation in Dynamic Programming?",
                    options = mapOf("A" to "Memoization is recursive with cached lookups; Tabulation is iterative filling of a state table", "B" to "Memoization requires O(1) space while Tabulation requires O(n)", "C" to "Tabulation cannot solve the Knapsack problem", "D" to "Memoization is strictly faster in all circumstances"),
                    correctOption = "A",
                    explanation = "Top-down memoization decomposes problems recursively and stores solutions in a hash table or array, while bottom-up tabulation iteratively computes base cases first without recursion overhead."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH4_001",
                    subject = "Computer Science",
                    chapter = "Graphs & Dynamic Programming",
                    topic = "Graph Algorithms and Dynamic Programming",
                    keywords = listOf("graphs", "dynamic programming", "0/1 knapsack", "bfs", "dfs", "topological sort", "kahn algorithm", "memoization"),
                    definition = "Graphs represent networked entities via adjacency structures; Dynamic Programming solves optimization problems via overlapping subproblems and optimal substructure.",
                    formulaOrCoreRule = "Knapsack: DP[i, w] = max(DP[i-1, w], DP[i-1, w-w_i] + v_i); BFS/DFS: O(V + E); Kahn: Enqueue in_deg == 0.",
                    workedExample = "For knapsack capacity 4 with items (w=1, v=1), (w=2, v=4), (w=3, v=6), the optimal selection is items 1 and 3 yielding total value \$7.",
                    content = "Graph algorithms and DP power build dependency resolution (npm/pip), network packet routing, and DNA sequence alignment in genomics."
                )
            )
        )
    }

    private fun createChapter5(): CatalogChapter {
        return CatalogChapter(
            chapterId = 205,
            chapterOrder = 5,
            title = "Operating Systems & Concurrency",
            estimatedMinutes = 25,
            summary = "Process life cycle, thread concurrency, mutual exclusion, semaphores, deadlocks (Coffman conditions), virtual memory paging, and TLBs.",
            syllabus = listOf("Process Control Blocks (PCB), Context Switching & CPU Scheduling (Round Robin, CFS)", "Thread Concurrency, Race Conditions & Critical Section Problem", "Synchronization Primitives: Mutex Locks, Counting Semaphores & Condition Variables", "Deadlocks: Four Coffman Conditions & Banker's Algorithm", "Virtual Memory: Paging, Page Tables, Translation Lookaside Buffer (TLB) & Page Faults"),
            coreFormulas = listOf("Turnaround Time: Turnaround = Completion_Time - Arrival_Time", "Waiting Time: Waiting_Time = Turnaround_Time - Burst_Time", "Effective Memory Access Time: EAT = Hit_Ratio * (TLB_Time + Mem_Time) + (1 - Hit_Ratio) * (TLB_Time + 2 * Mem_Time)", "Coffman Deadlock Conditions: Mutual Exclusion, Hold & Wait, No Preemption, Circular Wait", "Banker's Algorithm Safety Condition: Need ≤ Available (where Need = Max - Allocation)"),
            part1Title = "Process Lifecycles, Thread Synchronization & Memory Paging",
            part1Content = "An Operating System (OS) serves as the core intermediary abstraction managing computer hardware resources. A process is an executing program instance encapsulated by a Process Control Block (PCB) containing program counter registers, memory maps, and open file descriptors. Context switching saves the state of an active process and restores another, incurring CPU hardware register flush overhead. CPU schedulers optimize throughput and fairness using algorithms such as Round Robin (time quantum slicing) and Completely Fair Scheduler (CFS red-black virtual runtime trees).\n\nThreads share code, data, and heap segments within a single parent process while maintaining independent program counters, registers, and execution stacks. When concurrent threads execute unsynchronized read-modify-write operations on shared variables, race conditions emerge where program output depends unpredictably on thread interleaving. Solving the Critical Section Problem requires three mandatory properties: Mutual Exclusion (only one thread in critical section), Progress (threads outside cannot block candidates), and Bounded Waiting (finite waiting time).\n\nSynchronization primitives enforce deterministic execution order. A Mutex provides binary locking, while a counting semaphore maintains an integer counter accessed via atomic wait() (P) and signal() (V) operations. When concurrent threads improperly nest locks, deadlocks occur where threads freeze indefinitely. Deadlock requires four simultaneous Coffman conditions: Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait. The Banker's Algorithm prevents deadlocks by simulating resource allocations and verifying that the system remains in a safe state.\n\nVirtual memory provides each process with the illusion of a contiguous, private address space decoupled from physical RAM. The Memory Management Unit (MMU) translates virtual addresses into physical frames using multi-level page tables. To mitigate memory access latency, a Translation Lookaside Buffer (TLB) caches recent translations. A TLB miss incurs page table traversals; if a page is not present in RAM, a hardware Page Fault interrupt triggers OS disk swap retrieval, evicting pages via algorithms such as Least Recently Used (LRU).",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Effective Access Time: EAT = h * (t_TLB + t_RAM) + (1 - h) * (t_TLB + 2 * t_RAM)\n• Turnaround Time: TAT = T_completion - T_arrival\n• Banker's Algorithm: Need[i][j] = Max[i][j] - Allocation[i][j]\n\nWorked Numerical Example:\nProblem: In a virtual memory paging system, TLB access time is 10 ns, physical memory access time is 100 ns, and the TLB hit ratio is 90% (h = 0.90). Calculate the Effective Memory Access Time (EAT).\nStep 1: On a TLB hit (probability h = 0.90): The translation is found in TLB (10 ns) and the actual memory byte is fetched (100 ns). Time = 10 + 100 = 110 ns.\nStep 2: On a TLB miss (probability 1 - h = 0.10): The TLB is checked (10 ns), the page table in memory is accessed to get the frame address (100 ns), and the actual memory byte is fetched (100 ns). Time = 10 + 100 + 100 = 210 ns.\nStep 3: Calculate EAT:\nEAT = 0.90 × 110 ns + 0.10 × 210 ns = 99 ns + 21 ns = 120 ns.\nConclusion: The effective access time is 120 ns, illustrating that a 90% TLB hit ratio reduces average access time close to single memory cycle latency.\n\nReal-World Applications:\nOperating system concepts govern cloud servers and distributed systems. Docker and Kubernetes utilize Linux cgroups and namespaces to isolate containerized process environments. Database engines manage custom buffer pools to bypass OS page cache overhead, while multi-core high-performance computing relies on lock-free data structures (compare-and-swap primitives) to avoid mutex lock contention.",
            examTip = "University Exam Tip: In viva and finals, memorize the 4 Coffman conditions word-for-word: Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait. Breaking ANY ONE of these four conditions mathematically guarantees that deadlocks cannot occur!",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2033,
                    questionNumber = 1,
                    question = "Which of the following is NOT one of the four mandatory Coffman conditions required for a deadlock to occur?",
                    options = mapOf("A" to "Preemption of allocated resources by the OS", "B" to "Mutual Exclusion", "C" to "Hold and Wait", "D" to "Circular Wait"),
                    correctOption = "A",
                    explanation = "The Coffman condition is 'No Preemption' (resources cannot be forcibly confiscated). Allowing preemption actively prevents deadlock."
                ),
                QuizQuestionDto(
                    id = 2034,
                    questionNumber = 2,
                    question = "If TLB access time is 10 ns, memory access time is 100 ns, and TLB hit ratio is 90%, what is the Effective Memory Access Time (EAT)?",
                    options = mapOf("A" to "120 ns", "B" to "110 ns", "C" to "100 ns", "D" to "150 ns"),
                    correctOption = "A",
                    explanation = "EAT = 0.90 * (10 + 100) + 0.10 * (10 + 100 + 100) = 0.90(110) + 0.10(210) = 99 + 21 = 120 ns."
                ),
                QuizQuestionDto(
                    id = 2035,
                    questionNumber = 3,
                    question = "What is the primary role of the Translation Lookaside Buffer (TLB) in virtual memory management?",
                    options = mapOf("A" to "A high-speed associative hardware cache storing recent virtual-to-physical address translations", "B" to "A disk swap space for evicting pages", "C" to "A scheduler tracking process priorities", "D" to "A compiler optimization for dead code elimination"),
                    correctOption = "A",
                    explanation = "The TLB is an on-chip associative cache inside the MMU that stores recent page-to-frame translations, bypassing page table lookups in RAM."
                ),
                QuizQuestionDto(
                    id = 2036,
                    questionNumber = 4,
                    question = "What happens when a CPU generates a virtual memory address whose page table entry has the valid/invalid bit set to 'invalid' (not in RAM)?",
                    options = mapOf("A" to "A hardware Page Fault trap is generated, invoking the OS page fault handler", "B" to "The program immediately crashes with a syntax error", "C" to "The CPU enters an infinite busy-wait loop", "D" to "The memory controller restarts the computer"),
                    correctOption = "A",
                    explanation = "An invalid bit indicates the page is not in physical memory. The MMU raises a Page Fault interrupt, prompting the OS to fetch the page from swap storage on disk."
                ),
                QuizQuestionDto(
                    id = 2037,
                    questionNumber = 5,
                    question = "Which synchronization primitive allows up to N threads to access a shared resource pool simultaneously?",
                    options = mapOf("A" to "Counting Semaphore initialized to N", "B" to "Binary Mutex lock", "C" to "Spinlock", "D" to "Condition Variable"),
                    correctOption = "A",
                    explanation = "A counting semaphore initialized to N permits exactly N threads to acquire the semaphore before subsequent wait() calls block."
                ),
                QuizQuestionDto(
                    id = 2038,
                    questionNumber = 6,
                    question = "What is a race condition in multi-threaded programming?",
                    options = mapOf("A" to "A flaw where program outcome depends uncontrollably on the relative timing or interleaving of threads", "B" to "A thread that finishes execution faster than other threads", "C" to "A deadlock condition between two processes", "D" to "CPU thermal throttling under high load"),
                    correctOption = "A",
                    explanation = "A race condition occurs when concurrent threads read and write shared data without synchronization, causing the final state to depend on non-deterministic scheduling."
                ),
                QuizQuestionDto(
                    id = 2039,
                    questionNumber = 7,
                    question = "What is the Banker's Algorithm used for in operating systems?",
                    options = mapOf("A" to "Deadlock avoidance by verifying that resource allocation leaves the system in a safe state", "B" to "Allocating thread stack frames dynamically", "C" to "Scheduling network packets in routers", "D" to "Managing file system access permissions"),
                    correctOption = "A",
                    explanation = "Dijkstra's Banker's Algorithm simulates resource allocation requests to ensure that at least one sequence of process executions can finish safely without deadlock."
                ),
                QuizQuestionDto(
                    id = 2040,
                    questionNumber = 8,
                    question = "What metric represents the total elapsed time from when a process is submitted to when it completes execution?",
                    options = mapOf("A" to "Turnaround Time", "B" to "Waiting Time", "C" to "Response Time", "D" to "Throughput"),
                    correctOption = "A",
                    explanation = "Turnaround Time = Completion Time - Arrival Time, measuring the total lifespan of the process in the system."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH5_001",
                    subject = "Computer Science",
                    chapter = "Operating Systems & Concurrency",
                    topic = "Process Synchronization, Deadlocks and Virtual Memory Paging",
                    keywords = listOf("operating systems", "concurrency", "deadlock", "coffman", "mutex", "semaphore", "virtual memory", "paging", "tlb"),
                    definition = "Operating systems manage hardware abstractions; concurrency coordinates shared resources via mutexes/semaphores, while virtual memory decouples logical addresses via paging and TLBs.",
                    formulaOrCoreRule = "Coffman Conditions: Mutual Exclusion, Hold & Wait, No Preemption, Circular Wait; EAT = h*(t_TLB + t_mem) + (1-h)*(t_TLB + 2*t_mem).",
                    workedExample = "For 90% TLB hit ratio with 10ns TLB and 100ns RAM, EAT = 0.90*(110) + 0.10*(210) = 120 ns.",
                    content = "Operating systems principles govern cloud containerization (Docker/Kubernetes), high-concurrency database engines, and microprocessor memory management units."
                )
            )
        )
    }

    private fun createChapter6(): CatalogChapter {
        return CatalogChapter(
            chapterId = 206,
            chapterOrder = 6,
            title = "Relational Databases & SQL Normalization",
            estimatedMinutes = 25,
            summary = "Relational algebra, SQL DDL/DML, ACID transaction properties, serializability, B-tree indexes, and database normalization (1NF, 2NF, 3NF, BCNF).",
            syllabus = listOf("Relational Model: Schemas, Keys (Primary, Candidate, Foreign) & Relational Algebra", "SQL Operations: Complex Joins (Inner, Left, Outer), Grouping & Aggregations", "Functional Dependencies & Normal Forms: 1NF, 2NF, 3NF & Boyce-Codd (BCNF)", "Transaction Management & ACID Guarantees: Atomicity, Consistency, Isolation, Durability", "Concurrency Control: Two-Phase Locking (2PL), Isolation Levels & B+ Tree Indexing"),
            coreFormulas = listOf("1NF: All attribute values must be atomic (no repeating groups or arrays)", "2NF: In 1NF and no non-prime attribute is partially dependent on any candidate key", "3NF: In 2NF and no transitive dependencies (for X → Y, X is superkey or Y is prime)", "BCNF: For every non-trivial functional dependency X → Y, X must strictly be a Superkey", "B+ Tree Search Cost: O(log_B N) disk block I/O operations (B = fanout)"),
            part1Title = "Relational Theory, Normalization Forms & ACID Transactions",
            part1Content = "The Relational Database Management System (RDBMS) model formulated by Edgar F. Codd organizes data into formal mathematical relations (tables) composed of tuples (rows) and attributes (columns). Relational algebra defines declarative set operations including selection (σ), projection (π), Cartesian product (×), set difference (-), and joins (⋈). Integrity constraints enforce domain boundaries: candidate keys uniquely identify tuples, primary keys enforce non-null entity uniqueness, and foreign keys guarantee referential integrity across relational boundaries.\n\nDatabase normalization eliminates data redundancy and update anomalies (insertion, deletion, update). Functional dependencies X → Y dictate that attribute set X uniquely determines Y. First Normal Form (1NF) requires all attribute values to be atomic. Second Normal Form (2NF) eliminates partial dependencies where non-prime attributes depend on a proper subset of a composite candidate key. Third Normal Form (3NF) eliminates transitive dependencies where non-prime attributes determine other non-prime attributes. Boyce-Codd Normal Form (BCNF) strengthens 3NF by mandating that for every non-trivial functional dependency X → Y, determinant X must strictly be a superkey.\n\nTransactions are sequences of database operations treated as single logical units of work governed by the ACID paradigm: Atomicity (all-or-nothing execution via write-ahead logging), Consistency (preservation of relational schema invariants), Isolation (concurrent transactions execute without interference), and Durability (committed modifications persist across power failures). Concurrency phenomena include dirty reads, non-repeatable reads, and phantom reads, mitigated by SQL isolation levels (Read Uncommitted, Read Committed, Repeatable Read, Serializable).\n\nPhysical storage engines accelerate query performance via indexing data structures. B+ trees store all record pointers in leaf nodes linked as a doubly linked list, with internal nodes storing routing keys. With high branching fanouts (B ≥ 100), B+ trees restrict tree depth to 3 or 4 levels across millions of rows, minimizing disk block I/O reads. Two-Phase Locking (2PL) guarantees conflict serializability by dividing transaction lock lifecycles into a growing phase (acquiring locks) and a shrinking phase (releasing locks).",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Functional Dependency: X → Y means if t₁[X] = t₂[X], then t₁[Y] = t₂[Y]\n• Armstrong's Axioms: Reflexivity (Y ⊆ X ⇒ X → Y), Augmentation (X → Y ⇒ XZ → YZ), Transitivity (X → Y, Y → Z ⇒ X → Z)\n• BCNF Condition: In every X → Y, X is a Superkey\n\nWorked Numerical Example:\nProblem: Relation R(A, B, C, D) has functional dependencies: AB → C, C → D, D → A. Find all candidate keys and determine the highest normal form of R.\nStep 1: Compute attribute closures:\n• (AB)⁺: AB → C gives ABC; C → D gives ABCD. Thus (AB)⁺ = {A, B, C, D} ⇒ AB is a candidate key.\n• (BC)⁺: C → D gives BCD; D → A gives BCDA. Thus (BC)⁺ = {A, B, C, D} ⇒ BC is a candidate key.\n• (BD)⁺: D → A gives BDA; AB → C gives BDAC. Thus (BD)⁺ = {A, B, C, D} ⇒ BD is a candidate key.\nCandidate keys are AB, BC, BD. Prime attributes: {A, B, C, D} (all attributes are prime!).\nStep 2: Test for 2NF: Candidate keys are AB, BC, BD. Are there partial dependencies on proper subsets {A}, {B}, {C}, {D}? None of the FDs have single-attribute LHS determining non-prime attributes. 2NF holds.\nStep 3: Test for 3NF: For every X → Y, either X is a superkey OR Y is a prime attribute. Since all attributes {A, B, C, D} are prime, the second condition is trivially satisfied for all FDs! Thus R is in 3NF.\nStep 4: Test for BCNF: Requires X to be a superkey for every X → Y. For C → D, C is NOT a superkey ((C)⁺ = {C, D, A} ≠ ABCD). Thus R violates BCNF.\nConclusion: The relation R is in 3NF, but NOT in BCNF.",
            examTip = "University Exam Tip: If all attributes in a relation are prime attributes (members of at least one candidate key), the relation is AUTOMATICALLY in 3NF! However, it may still violate BCNF if the determinant is not a superkey.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2041,
                    questionNumber = 1,
                    question = "What is the defining requirement of Boyce-Codd Normal Form (BCNF)?",
                    options = mapOf("A" to "For every non-trivial functional dependency X → Y, X must be a Superkey", "B" to "All attributes must be prime attributes", "C" to "No attribute can be a foreign key", "D" to "The table cannot have composite candidate keys"),
                    correctOption = "A",
                    explanation = "BCNF strictly requires that the left-hand side determinant X of every non-trivial functional dependency X → Y must be a superkey."
                ),
                QuizQuestionDto(
                    id = 2042,
                    questionNumber = 2,
                    question = "Which normal form specifically eliminates partial functional dependencies on composite candidate keys?",
                    options = mapOf("A" to "Second Normal Form (2NF)", "B" to "First Normal Form (1NF)", "C" to "Third Normal Form (3NF)", "D" to "Fourth Normal Form (4NF)"),
                    correctOption = "A",
                    explanation = "2NF requires the relation to be in 1NF and guarantees that no non-prime attribute is functionally dependent on a proper subset of any candidate key."
                ),
                QuizQuestionDto(
                    id = 2043,
                    questionNumber = 3,
                    question = "What does the 'A' in the ACID database transaction model represent?",
                    options = mapOf("A" to "Atomicity (all operations execute completely or none do)", "B" to "Availability in distributed clusters", "C" to "Asynchronous query execution", "D" to "Authorization and user permissions"),
                    correctOption = "A",
                    explanation = "Atomicity guarantees that a transaction is treated as an indivisible unit: either all updates are committed to disk, or the entire transaction is rolled back on failure."
                ),
                QuizQuestionDto(
                    id = 2044,
                    questionNumber = 4,
                    question = "Which SQL isolation level provides complete immunity against Dirty Reads, Non-Repeatable Reads, and Phantom Reads?",
                    options = mapOf("A" to "Serializable", "B" to "Repeatable Read", "C" to "Read Committed", "D" to "Read Uncommitted"),
                    correctOption = "A",
                    explanation = "Serializable is the highest ANSI SQL isolation level, executing transactions with serializability guarantees that prevent all concurrency anomalies."
                ),
                QuizQuestionDto(
                    id = 2045,
                    questionNumber = 5,
                    question = "Why are B+ trees preferred over standard binary search trees for disk-based relational database indexes?",
                    options = mapOf("A" to "High branching fanout minimizes disk I/O reads, and leaf nodes form a linked list for fast range scans", "B" to "B+ trees require zero memory overhead", "C" to "B+ trees eliminate the need for primary keys", "D" to "B+ trees can only store integer data types"),
                    correctOption = "A",
                    explanation = "With fanouts of 100+, B+ trees keep tree depth to 3 or 4 levels, minimizing disk block seek latencies while linked leaves enable rapid contiguous range queries."
                ),
                QuizQuestionDto(
                    id = 2046,
                    questionNumber = 6,
                    question = "What is guaranteed by the Two-Phase Locking (2PL) protocol in database concurrency control?",
                    options = mapOf("A" to "Conflict serializability of concurrent transaction execution", "B" to "Prevention of all deadlocks", "C" to "Zero lock waiting time", "D" to "Instantaneous database crash recovery"),
                    correctOption = "A",
                    explanation = "2PL guarantees that transactions are conflict serializable by separating execution into a growing phase (locks acquired) and a shrinking phase (locks released)."
                ),
                QuizQuestionDto(
                    id = 2047,
                    questionNumber = 7,
                    question = "In a relation R(A, B, C) where A → B and B → C with candidate key A, what normal form is violated by B → C?",
                    options = mapOf("A" to "Third Normal Form (3NF) due to transitive dependency", "B" to "First Normal Form (1NF)", "C" to "Second Normal Form (2NF)", "D" to "Fifth Normal Form (5NF)"),
                    correctOption = "A",
                    explanation = "Because non-prime attribute B determines non-prime attribute C, a transitive dependency A → B → C exists, violating 3NF."
                ),
                QuizQuestionDto(
                    id = 2048,
                    questionNumber = 8,
                    question = "Which relational algebra operation corresponds to filtering rows matching a specific predicate condition?",
                    options = mapOf("A" to "Selection (σ)", "B" to "Projection (π)", "C" to "Cartesian Product (×)", "D" to "Union (∪)"),
                    correctOption = "A",
                    explanation = "Selection (denoted by sigma σ) filters tuples (rows) satisfying a specified boolean predicate condition (equivalent to SQL WHERE)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH6_001",
                    subject = "Computer Science",
                    chapter = "Relational Databases & SQL Normalization",
                    topic = "Database Normalization (1NF, 2NF, 3NF, BCNF) and ACID Transactions",
                    keywords = listOf("database", "sql", "normalization", "bcnf", "3nf", "acid", "transactions", "b+ tree", "relational algebra"),
                    definition = "Database normalization decomposes relations to eliminate redundant storage and anomalies; ACID properties guarantee reliable transactional execution.",
                    formulaOrCoreRule = "BCNF: For all X -> Y, X is Superkey; 3NF: X is Superkey or Y is Prime; 2NF: No partial dependencies; B+ Tree Search: O(log_B N).",
                    workedExample = "For R(A,B,C,D) with AB->C, C->D, D->A, all attributes are prime so 3NF holds; C->D violates BCNF because C is not a superkey.",
                    content = "Relational database principles underpin financial ledger consistency (PostgreSQL, Oracle), enterprise ERP systems, and cloud databases."
                )
            )
        )
    }

    private fun createChapter7(): CatalogChapter {
        return CatalogChapter(
            chapterId = 207,
            chapterOrder = 7,
            title = "Computer Networks & TCP/IP",
            estimatedMinutes = 25,
            summary = "OSI 7-layer & TCP/IP models, IP addressing & CIDR subnetting, TCP flow/congestion control, DNS, HTTP/3, and TLS handshake cryptography.",
            syllabus = listOf("Layered Architectures: OSI 7-Layer Reference Model vs TCP/IP Protocol Suite", "Network Layer: IPv4 / IPv6 Addressing, Subnet Masks & CIDR Notation", "Routing Protocols: Distance Vector (RIP) vs Link State (OSPF) & BGP", "Transport Layer: TCP 3-Way Handshake, Sliding Window Flow Control & Congestion Control", "Application Layer: DNS Resolution Hierarchy, HTTP/1.1 vs HTTP/2 vs HTTP/3 (QUIC)"),
            coreFormulas = listOf("Subnet Usable Hosts: Usable_Hosts = 2^(32 - Prefix) - 2 (Subtract network and broadcast)", "TCP Bandwidth-Delay Product: BDP = Bandwidth (bps) * RTT (sec)", "TCP Throughput Bound (Mathis Formula): Rate ≤ (MSS / RTT) * (1 / √(Loss_Rate))", "Little's Law for Networks: L = λ * W (Packets = Arrival_Rate * Latency)", "TCP AIMD: Additive Increase (+1 MSS per RTT), Multiplicative Decrease (Halve cwnd on loss)"),
            part1Title = "Network Topologies, Routing Protocols & Reliable Transport",
            part1Content = "Computer networks interconnect autonomous computing devices via packet-switched communication channels. The OSI 7-layer model conceptually stratifies network protocols: Physical, Data Link, Network, Transport, Session, Presentation, and Application. The pragmatic TCP/IP suite consolidates these into four functional tiers: Network Access (Ethernet MAC frames), Internet (IP packet routing), Transport (end-to-end host transport), and Application (HTTP, DNS, SSH).\n\nThe Network Layer achieves host-to-host datagram delivery across heterogeneous physical topologies. IPv4 addresses comprise 32 bits categorized by Classless Inter-Domain Routing (CIDR) notation (e.g., /24), where the prefix defines the network identifier and the remaining bits address individual host interfaces. Routing protocols determine optimal forwarding paths across autonomous systems: interior gateway protocols use link-state Dijkstra flooding (OSPF), while exterior gateway protocols exchange routing reachability vectors (BGP).\n\nThe Transport Layer establishes process-to-process communication using 16-bit port multiplexing. Transmission Control Protocol (TCP) guarantees reliable, ordered byte-stream delivery via a 3-way handshake (SYN, SYN-ACK, ACK) and connection teardown (FIN, ACK). TCP employs sliding-window flow control to prevent receiver buffer overflow and Additive-Increase Multiplicative-Decrease (AIMD) congestion control (Slow Start, Congestion Avoidance, Fast Retransmit, Fast Recovery) to dynamically throttle transmission rates to network bottleneck capacity.\n\nThe Application Layer enables end-user networked workflows. The Domain Name System (DNS) implements a hierarchical distributed database translating human-readable hostnames into IP addresses via recursive and authoritative resolvers. Hypertext Transfer Protocol (HTTP) has evolved from head-of-line blocking in HTTP/1.1 to binary multiplexing in HTTP/2, culminating in HTTP/3 running over QUIC (UDP), integrating TLS 1.3 cryptographic handshakes to eliminate round-trip latency.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• CIDR Usable Hosts: 2^(32 - n) - 2\n• Bandwidth-Delay Product: BDP = B * RTT (in bits)\n• TCP Window Scale: Max Window = Window_Size × 2^(Scale_Factor)\n• Round-Trip Time EWMA: RTT_est = (1 - α) RTT_est + α RTT_sample (typically α = 0.125)\n\nWorked Numerical Example:\nProblem: A network is assigned the CIDR block 192.168.10.0/26. Find the subnet mask, total addresses, usable host count, network address, and broadcast address.\nStep 1: Prefix length = /26. Host bits = 32 - 26 = 6 bits.\nStep 2: Subnet mask has 26 ones followed by 6 zeros:\n• Binary: 11111111.11111111.11111111.11000000\n• Decimal: 255.255.255.192.\nStep 3: Total IP addresses = 2⁶ = 64 addresses.\nStep 4: Usable host count = 2⁶ - 2 = 64 - 2 = 62 usable host interfaces.\nStep 5: Address range: From 192.168.10.0 to 192.168.10.63.\n• Network Address (all host bits 0): 192.168.10.0\n• First Usable Host: 192.168.10.1\n• Last Usable Host: 192.168.10.62\n• Broadcast Address (all host bits 1): 192.168.10.63.\n\nReal-World Applications:\nNetwork protocols underpin the global internet. Content Delivery Networks (Cloudflare, Akamai) use BGP Anycast to route user requests to geographically proximate edge servers. WebRTC enables real-time peer-to-peer audio/video streaming via UDP hole punching, while modern microservices utilize gRPC with HTTP/2 multiplexing for low-latency inter-service communication.",
            examTip = "University Exam Tip: When asked for usable hosts in a /n subnet, ALWAYS subtract 2 (the first is the network address, and the last is the broadcast address). For example, a /26 subnet has 64 total addresses, but exactly 62 USABLE hosts.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2049,
                    questionNumber = 1,
                    question = "How many usable host IP addresses are available in a subnetwork with CIDR notation /26?",
                    options = mapOf("A" to "62", "B" to "64", "C" to "30", "D" to "126"),
                    correctOption = "A",
                    explanation = "32 - 26 = 6 host bits. Total addresses = 2⁶ = 64. Subtracting network address and broadcast address gives 64 - 2 = 62 usable host addresses."
                ),
                QuizQuestionDto(
                    id = 2050,
                    questionNumber = 2,
                    question = "Which packet flags establish a connection in the standard TCP 3-Way Handshake?",
                    options = mapOf("A" to "SYN -> SYN-ACK -> ACK", "B" to "ACK -> SYN -> ACK", "C" to "FIN -> ACK -> FIN-ACK", "D" to "RST -> SYN -> ACK"),
                    correctOption = "A",
                    explanation = "Client sends SYN, Server responds with SYN-ACK, and Client completes connection establishment by replying with ACK."
                ),
                QuizQuestionDto(
                    id = 2051,
                    questionNumber = 3,
                    question = "What transport-layer protocol does HTTP/3 operate over instead of traditional TCP?",
                    options = mapOf("A" to "QUIC (built on UDP)", "B" to "SCTP", "C" to "Raw IP packets", "D" to "ICMP"),
                    correctOption = "A",
                    explanation = "HTTP/3 abandons TCP in favor of QUIC running over UDP, eliminating head-of-line blocking and accelerating TLS handshake connection establishment."
                ),
                QuizQuestionDto(
                    id = 2052,
                    questionNumber = 4,
                    question = "What is the Bandwidth-Delay Product (BDP) of a 1 Gbps link with a 20 ms Round-Trip Time (RTT)?",
                    options = mapOf("A" to "20 Megabits (2.5 Megabytes)", "B" to "2 Megabits", "C" to "200 Megabits", "D" to "50 Kilobits"),
                    correctOption = "A",
                    explanation = "BDP = Bandwidth * RTT = (10⁹ bits/sec) * (0.020 sec) = 20,000,000 bits = 20 Megabits (or 2.5 MB), representing the volume of data in flight needed to saturate the link."
                ),
                QuizQuestionDto(
                    id = 2053,
                    questionNumber = 5,
                    question = "Which routing protocol uses Dijkstra's algorithm to compute shortest paths within an autonomous system?",
                    options = mapOf("A" to "OSPF (Open Shortest Path First)", "B" to "RIP (Routing Information Protocol)", "C" to "BGP (Border Gateway Protocol)", "D" to "ARP"),
                    correctOption = "A",
                    explanation = "OSPF is a link-state routing protocol where routers flood link states and run Dijkstra's shortest path algorithm on their synchronized topological databases."
                ),
                QuizQuestionDto(
                    id = 2054,
                    questionNumber = 6,
                    question = "What mechanism does TCP AIMD congestion control use upon detecting packet loss via triple duplicate ACKs?",
                    options = mapOf("A" to "Halves the congestion window (Multiplicative Decrease)", "B" to "Resets the congestion window to 1 MSS immediately", "C" to "Increases window by 1 MSS", "D" to "Closes the connection"),
                    correctOption = "A",
                    explanation = "Upon detecting packet loss without timeout, TCP Fast Recovery halves the congestion window (Multiplicative Decrease) rather than resetting to 1, maintaining throughput."
                ),
                QuizQuestionDto(
                    id = 2055,
                    questionNumber = 7,
                    question = "What is the function of the Address Resolution Protocol (ARP)?",
                    options = mapOf("A" to "Resolves a known IP address to a physical hardware MAC address", "B" to "Translates domain names to IP addresses", "C" to "Encrypts application data payload", "D" to "Assigns dynamic IP addresses to new clients"),
                    correctOption = "A",
                    explanation = "ARP maps a 32-bit logical IPv4 address to a 48-bit physical Ethernet MAC address within the local broadcast link layer."
                ),
                QuizQuestionDto(
                    id = 2056,
                    questionNumber = 8,
                    question = "In the Domain Name System (DNS), which server holds the definitive mapping records for a specific domain name (e.g., synapse.ai)?",
                    options = mapOf("A" to "Authoritative Name Server", "B" to "Root Name Server", "C" to "TLD Name Server", "D" to "Recursive Resolver Cache"),
                    correctOption = "A",
                    explanation = "The Authoritative Name Server holds the definitive DNS zone files and resource records (A, AAAA, CNAME, MX) for that specific registered domain."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH7_001",
                    subject = "Computer Science",
                    chapter = "Computer Networks & TCP/IP",
                    topic = "TCP/IP Networking, Subnetting and Protocol Stacks",
                    keywords = listOf("networking", "tcp/ip", "cidr", "subnetting", "routing", "ospf", "bgp", "dns", "http/3", "quic"),
                    definition = "Computer networks route packets across layered protocols; CIDR defines subnets, while TCP ensures reliable ordered transport via flow and congestion control.",
                    formulaOrCoreRule = "Usable Hosts: 2^(32-Prefix) - 2; BDP = Bandwidth * RTT; AIMD: +1 MSS per RTT, halve cwnd on loss.",
                    workedExample = "For 192.168.10.0/26, 6 host bits yield 64 total addresses, exactly 62 usable hosts (192.168.10.1 to .62), and broadcast address .63.",
                    content = "Computer networks underpin internet infrastructure, Cloudflare BGP anycast CDNs, WebRTC streaming, and microservice gRPC communications."
                )
            )
        )
    }

    private fun createChapter8(): CatalogChapter {
        return CatalogChapter(
            chapterId = 208,
            chapterOrder = 8,
            title = "Compiler Design & System Architecture",
            estimatedMinutes = 25,
            summary = "Lexical analysis (lex/flex), LL/LR parsing, Abstract Syntax Trees (AST), intermediate representation (IR), register allocation, and RISC/CISC CPU pipelining.",
            syllabus = listOf("Compiler Phases: Lexical Analysis, Regular Expressions & Finite Automata (DFA/NFA)", "Syntax Analysis: Context-Free Grammars, LL(1) vs LR(1) Bottom-Up Parsing", "Semantic Analysis: Symbol Tables, Type Systems & Abstract Syntax Trees (AST)", "Intermediate Representation (IR), 3-Address Code & Control Flow Graphs (CFG)", "System Architecture: Instruction Pipelining, Hazards (Data, Structural, Control) & Branch Prediction"),
            coreFormulas = listOf("Processor CPI: CPI = Total_Clock_Cycles / Instruction_Count", "CPU Execution Time: CPU_Time = Instructions * CPI * Clock_Cycle_Time", "Pipelining Speedup: Speedup = (k * CPI_non_pipelined) / (1 + Pipeline_Stalls) ≈ k (as n → ∞)", "Context-Free Grammar Production: A → α (where A is non-terminal, α is string of terminals/non-terminals)", "Register Allocation: Chaitin's Graph Coloring Algorithm (K-colorable interference graph)"),
            part1Title = "Compilation Pipeline, Syntax Parsing & Pipelined Execution",
            part1Content = "A compiler translates high-level human-readable source code into machine executable binary instructions while optimizing runtime performance. The compiler architecture consists of a front-end and a back-end decoupled via an Intermediate Representation (IR). The front-end begins with Lexical Analysis, where a scanner converts source text into a stream of atomic tokens using Deterministic Finite Automata (DFAs) synthesized from regular expressions via Thompson's construction.\n\nSyntax Analysis verifies token grammar against Context-Free Grammars (CFGs). Top-down parsers (LL(k)) construct parse trees from the root downward using recursive descent or predictive parsing tables, requiring grammars free of left recursion. Bottom-up parsers (LR(k), LALR) utilize shift-reduce engines over deterministic pushdown automata, recognizing a strictly broader grammar class. Semantic Analysis validates type consistency and scope rules, decorating an Abstract Syntax Tree (AST) while maintaining symbol table bindings.\n\nThe compiler back-end generates three-address intermediate code (such as LLVM IR), structuring basic blocks into a directed Control Flow Graph (CFG). Machine-independent optimizations include common subexpression elimination, dead code elimination, loop invariant code motion, and constant folding. Register allocation maps an infinite set of virtual IR variables to a finite set of hardware registers, modeled as an NP-complete Graph Coloring problem on the variable interference graph using Chaitin's heuristic.\n\nModern CPU architectures execute machine instructions through instruction pipelining, overlapping execution stages (Instruction Fetch, Decode, Execute, Memory Access, Writeback). Pipelining hazards include Structural Hazards (hardware resource conflicts), Data Hazards (Read-After-Write dependencies resolved by data forwarding or pipeline bubbles), and Control Hazards (branch instruction redirection). Modern superscalar CPUs utilize dynamic branch prediction tables and speculative out-of-order execution engines to sustain instruction throughput approaching an ideal Cycles Per Instruction (CPI) of 1.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Iron Law of Processor Performance: CPU Time = Instructions × CPI × Clock Cycle Time\n• Pipeline Speedup: S_k = (k × n) / (k + n - 1) ≈ k for large n (k = pipeline stages)\n• Graph Coloring for Registers: If degree(node) < K, node can be safely colored with K registers\n\nWorked Numerical Example:\nProblem: A 5-stage CPU pipeline operates with a 2 GHz clock (Clock Cycle Time = 0.5 ns). In an unpipelined architecture, each instruction takes 5 clock cycles (CPI = 5.0). In the pipelined processor, 20% of instructions cause a 2-cycle branch hazard stall. Calculate the pipelined CPI, execution time for 1,000,000 instructions, and pipeline speedup.\nStep 1: Pipelined CPI calculation:\n• Base CPI = 1.0 cycle\n• Branch penalty = 20% × 2 cycles = 0.20 × 2 = 0.40 cycles\n• Average Pipelined CPI = 1.0 + 0.40 = 1.40 cycles per instruction.\nStep 2: CPU Execution Time for 10⁶ instructions:\n• CPU Time = Instruction Count × CPI × Cycle Time\n• CPU Time = 10⁶ × 1.40 × 0.5 × 10⁻⁹ s = 0.70 × 10⁻³ s = 0.70 ms.\nStep 3: Unpipelined CPU Execution Time:\n• Unpipelined Time = 10⁶ × 5.0 × 0.5 × 10⁻⁹ s = 2.50 ms.\nStep 4: Speedup S = Unpipelined Time / Pipelined Time = 2.50 ms / 0.70 ms ≈ 3.57x.\nConclusion: Pipelining delivers a 3.57x throughput increase despite branch hazard stalls.\n\nReal-World Applications:\nCompiler design and CPU architecture principles power modern computing infrastructure. The LLVM compiler framework optimizes languages from C++ and Rust to Swift using modular optimization passes. Just-In-Time (JIT) compilers in the Java Virtual Machine (JVM) and V8 JavaScript engine dynamically profile and compile hot bytecode into native x86/ARM machine code at runtime.",
            examTip = "University Exam Tip: In processor performance questions, remember the Iron Law: CPU Time = Instructions × CPI × (1 / Clock Rate). If asked about pipelining hazards, clearly distinguish between Data Hazards (RAW dependencies solved by forwarding) and Control Hazards (branching solved by prediction).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 2057,
                    questionNumber = 1,
                    question = "What classic graph theory problem is register allocation in compiler back-ends mathematically modeled as?",
                    options = mapOf("A" to "Graph Coloring on the variable interference graph", "B" to "Shortest Path problem", "C" to "Maximum Bipartite Matching", "D" to "Minimum Spanning Tree"),
                    correctOption = "A",
                    explanation = "Register allocation maps variables with overlapping lifespans (interference graph) to a fixed number of physical registers K, equivalent to the K-vertex graph coloring problem."
                ),
                QuizQuestionDto(
                    id = 2058,
                    questionNumber = 2,
                    question = "A 5-stage CPU pipeline with 1 GHz clock (1 ns cycle) has a base CPI of 1.0. If 20% of instructions stall for 2 cycles, what is the average CPI?",
                    options = mapOf("A" to "1.40", "B" to "1.20", "C" to "2.00", "D" to "1.00"),
                    correctOption = "A",
                    explanation = "CPI = Base CPI + Stall penalty = 1.0 + (0.20 * 2) = 1.0 + 0.40 = 1.40."
                ),
                QuizQuestionDto(
                    id = 2059,
                    questionNumber = 3,
                    question = "Which compiler phase converts a stream of characters into a stream of categorized tokens?",
                    options = mapOf("A" to "Lexical Analysis (Scanner)", "B" to "Syntax Analysis (Parser)", "C" to "Semantic Analysis", "D" to "Code Optimization"),
                    correctOption = "A",
                    explanation = "Lexical analysis reads raw source code characters and matches them via regular expressions and DFAs to produce atomic language tokens."
                ),
                QuizQuestionDto(
                    id = 2060,
                    questionNumber = 4,
                    question = "What technique mitigates Read-After-Write (RAW) data hazards in CPU pipelines without stalling the pipeline?",
                    options = mapOf("A" to "Data Forwarding / Bypassing", "B" to "Increasing clock frequency", "C" to "Instruction caching", "D" to "Dynamic array doubling"),
                    correctOption = "A",
                    explanation = "Data forwarding routes computed results directly from ALU/memory output buffers back to the ALU input stages of subsequent instructions, bypassing register writeback stalls."
                ),
                QuizQuestionDto(
                    id = 2061,
                    questionNumber = 5,
                    question = "What is the primary limitation of top-down LL(1) parsing grammars?",
                    options = mapOf("A" to "Grammars cannot contain left-recursion", "B" to "Grammars cannot parse arithmetic expressions", "C" to "They require exponential stack space", "D" to "They can only parse binary tokens"),
                    correctOption = "A",
                    explanation = "Top-down predictive parsers enter infinite recursion if a production contains direct or indirect left-recursion (e.g. A → A α), requiring grammar refactoring."
                ),
                QuizQuestionDto(
                    id = 2062,
                    questionNumber = 6,
                    question = "What intermediate data structure does a compiler build to represent the hierarchical syntactic structure of a program?",
                    options = mapOf("A" to "Abstract Syntax Tree (AST)", "B" to "Adjacency Matrix", "C" to "Hash Table", "D" to "FIFO Ring Buffer"),
                    correctOption = "A",
                    explanation = "The parser generates an Abstract Syntax Tree (AST) capturing the nested hierarchical semantic structure of programming language statements."
                ),
                QuizQuestionDto(
                    id = 2063,
                    questionNumber = 7,
                    question = "What does an SSA (Static Single Assignment) intermediate representation guarantee for every variable?",
                    options = mapOf("A" to "Every variable is assigned a value exactly once", "B" to "Variables cannot be used in loops", "C" to "All variables are stored in registers", "D" to "Variables must be 64-bit integers"),
                    correctOption = "A",
                    explanation = "In Static Single Assignment (SSA) form (used by LLVM and GCC), every variable is assigned exactly once, simplifying optimization passes like dead code elimination."
                ),
                QuizQuestionDto(
                    id = 2064,
                    questionNumber = 8,
                    question = "What type of hazard occurs when a branch instruction redirects execution flow, invalidating instructions already fetched into the pipeline?",
                    options = mapOf("A" to "Control Hazard (Branch Hazard)", "B" to "Structural Hazard", "C" to "Data Hazard", "D" to "Page Fault Hazard"),
                    correctOption = "A",
                    explanation = "Control hazards arise from conditional and unconditional branch jumps where the target instruction address is not known until decode or execute, requiring branch prediction."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CS_CH8_001",
                    subject = "Computer Science",
                    chapter = "Compiler Design & System Architecture",
                    topic = "Compilers, Parsing and Processor Pipelining",
                    keywords = listOf("compiler", "lexer", "parser", "ast", "register allocation", "pipelining", "cpi", "data hazard", "llvm"),
                    definition = "Compilers translate high-level code to machine instructions via lexing, parsing (AST), and register allocation. Pipelined CPUs overlap instruction execution stages.",
                    formulaOrCoreRule = "Iron Law: CPU Time = Instructions * CPI * Cycle_Time; Register Allocation = Graph Coloring; Speedup = k / (1 + stalls).",
                    workedExample = "A 5-stage pipeline with 20% branches stalling 2 cycles achieves CPI = 1.0 + 0.4 = 1.40, yielding a 3.57x speedup over unpipelined CPI 5.0.",
                    content = "Compiler optimizations (LLVM, GCC) and CPU pipelining enable low-latency execution in modern operating systems and games."
                )
            )
        )
    }
}
