# scratch/add_subjects_6_to_10.py
# Appends subjects 116 to 120 to gen_subjects_1_to_10

def append_6_to_10(subs):
    from gen_subjects_1_to_10 import make_ch

    # 116. Quantum Computing Fundamentals
    subs.append({
        "id": "SUB_QUANTUM",
        "subject_id": 116,
        "name": "Quantum Computing Fundamentals",
        "slug": "quantum-computing-fundamentals",
        "icon_symbol": "⚛️",
        "color_hex": "#7C3AED",
        "category": "Modern Tech",
        "description": "Qubit state vectors, superposition, entanglement, quantum logic gates, and Grover/Shor quantum algorithms.",
        "is_added": False,
        "chapters": [
            make_ch(
                1161, 1, "Qubits, Superposition & Bloch Sphere Representation", 20,
                "State vectors in Hilbert space, Dirac bra-ket notation, Born rule, and Bloch sphere visualization.",
                ["Qubits vs classical bits: state vector |psi> = alpha|0> + beta|1>", "Born rule and measurement collapse probabilities", "Bloch sphere coordinates (theta, phi) representation", "Multi-qubit tensor product Hilbert spaces"],
                ["|alpha|^2 + |beta|^2 = 1", "|psi> = cos(theta/2)|0> + e^(i*phi)*sin(theta/2)|1>", "P(0) = |<0|psi>|^2, P(1) = |<1|psi>|^2"],
                "Qubit State Vectors & Quantum Superposition",
                "While a classical bit exists strictly in state 0 or 1, a quantum bit (qubit) exists in a linear superposition |psi> = alpha|0> + beta|1>, where alpha and beta are complex probability amplitudes in a 2D complex Hilbert space C^2. Normalization requires |alpha|^2 + |beta|^2 = 1. Upon measurement in the computational basis {|0>, |1>}, the state collapses probabilistically, yielding 0 with probability |alpha|^2 and 1 with probability |beta|^2.",
                "Bloch Sphere Geometry & Quantum Operations",
                "Ignoring global unobservable phase, any single-qubit pure state maps to a point on the surface of the unit Bloch Sphere via polar angle theta and azimuthal angle phi. Unitary operations U (where U^dagger * U = I) correspond to rigid geometric rotations on the Bloch sphere, preserving vector lengths and probabilities.",
                "A system of n qubits resides in a 2^n-dimensional Hilbert space formed by tensor products.",
                [
                    {"id": 11611, "question_number": 1, "question": "What is the normalization constraint for the complex amplitudes alpha and beta of a single qubit state |psi> = alpha|0> + beta|1>?",
                     "options": {"A": "alpha + beta = 1", "B": "|alpha|^2 + |beta|^2 = 1", "C": "alpha * beta = 0", "D": "|alpha| = |beta|"},
                     "correct_option": "B", "explanation": "By the Born probability rule, the sum of measurement outcome probabilities must equal 1: |alpha|^2 + |beta|^2 = 1."},
                    {"id": 11612, "question_number": 2, "question": "How many complex basis amplitudes are required to represent an entangled system of n qubits?",
                     "options": {"A": "n", "B": "2 * n", "C": "2^n", "D": "n^2"},
                     "correct_option": "C", "explanation": "The state space of n qubits is the tensor product of n single-qubit 2D Hilbert spaces, yielding a composite Hilbert space of dimension 2^n."},
                    {"id": 11613, "question_number": 3, "question": "On the Bloch sphere, what quantum state corresponds to the north pole (theta = 0)?",
                     "options": {"A": "|0>", "B": "|1>", "C": "|+> = (|0> + |1>)/sqrt(2)", "D": "|-> = (|0> - |1>)/sqrt(2)"},
                     "correct_option": "A", "explanation": "When theta = 0, cos(0/2) = 1 and sin(0/2) = 0, representing the computational basis state |0> at the north pole."},
                    {"id": 11614, "question_number": 4, "question": "What mathematical property must every quantum logic gate operator U satisfy?",
                     "options": {"A": "Hermitian (U = U^T)", "B": "Unitary (U^dagger * U = I)", "C": "Singular (det(U) = 0)", "D": "Zero trace"},
                     "correct_option": "B", "explanation": "Quantum evolution is reversible and preserves probability amplitudes, which requires operators to be unitary: U^dagger * U = I."},
                    {"id": 11615, "question_number": 5, "question": "What happens to a qubit in superposition (|0> + |1>)/sqrt(2) immediately following a computational basis measurement?",
                     "options": {"A": "It stays in superposition forever", "B": "Wavefunction collapses irreversibly to either |0> or |1> with equal 50% probability", "C": "It is destroyed into photons", "D": "It inverts its spin to -1"},
                     "correct_option": "B", "explanation": "Under the projection postulate of quantum mechanics, measurement collapses the superposition into an eigenstate (|0> or |1>) with probability proportional to amplitude squared."}
                ],
                [
                    {"chunk_id": "chunk_qc_01", "subject": "Quantum Computing Fundamentals",
                     "chapter": "Qubits, Superposition & Bloch Sphere Representation", "topic": "Qubits, Superposition and Bloch Sphere",
                     "keywords": ["quantum computing", "qubit", "superposition", "bloch sphere", "hilbert space", "born rule", "dirac notation"],
                     "definition": "A qubit is the basic unit of quantum information, capable of existing simultaneously in superpositions of basis states |0> and |1>.",
                     "formula_or_core_rule": "|psi> = alpha|0> + beta|1>; |alpha|^2 + |beta|^2 = 1; State dimension = 2^n.",
                     "worked_example": "For state |psi> = (sqrt(3)/2)|0> + (1/2)|1>: P(|0>) = (sqrt(3)/2)^2 = 3/4 (75%), P(|1>) = (1/2)^2 = 1/4 (25%).",
                     "content": "Quantum computation harnesses superposition and quantum interference. While classical computers evaluate single bit configurations sequentially, quantum registers operate across 2^n states concurrently in Hilbert space."}
                ]
            ),
            make_ch(
                1162, 2, "Quantum Logic Gates & Entanglement (Bell States)", 20,
                "Pauli gates (X, Y, Z), Hadamard gate, CNOT entangling gate, and Einstein-Podolsky-Rosen (EPR) Bell states.",
                ["Single-qubit unitary gates: Pauli X, Y, Z, and Hadamard (H)", "Phase shift gates: S gate and T gate (pi/8)", "Two-qubit Controlled-NOT (CNOT) and controlled unitary gates", "Quantum entanglement, Bell states, and the No-Cloning Theorem"],
                ["H = (1/sqrt(2)) * [1, 1; 1, -1]", "CNOT|c, t> = |c, c XOR t>", "|Phi+> = (|00> + |11>) / sqrt(2) (Bell State)", "No-Cloning: U(|psi>|0>) != |psi>|psi>"],
                "Single-Qubit Unitary Gates & Hadamard Transform",
                "Quantum logic gates are represented by unitary matrices acting on state vectors. The Pauli-X gate acts as a quantum bit-flip (|0> <-> |1>). The Pauli-Z gate applies a relative phase flip (|1> -> -|1>). The Hadamard gate (H) is foundational: starting from pure basis state |0>, H creates an equal superposition state |+> = (|0> + |1>)/sqrt(2), transforming between computational and Fourier/Hadamard bases.",
                "Quantum Entanglement & Non-Local Correlations",
                "Two-qubit gates enable quantum entanglement, where quantum states cannot be factored into independent product states of individual subsystems: |psi_AB> != |psi_A> (x) |psi_B>. Applying a Hadamard gate followed by a Controlled-NOT (CNOT) gate to |00> synthesizes the maximally entangled Bell state |Phi+> = (|00> + |11>)/sqrt(2). The No-Cloning Theorem mathematically proves that an arbitrary unknown quantum state cannot be copied perfectly.",
                "The No-Cloning Theorem guarantees quantum cryptographic security, making eavesdropping detectable.",
                [
                    {"id": 11621, "question_number": 1, "question": "What is the output state when a Hadamard gate (H) is applied to the input state |0>?",
                     "options": {"A": "|1>", "B": "(|0> + |1>) / sqrt(2)", "C": "(|0> - |1>) / sqrt(2)", "D": "-|0>"},
                     "correct_option": "B", "explanation": "The Hadamard gate transforms basis state |0> into the symmetric superposition |+> = (|0> + |1>)/sqrt(2)."},
                    {"id": 11622, "question_number": 2, "question": "What output state is generated by passing input |00> through a Hadamard gate on qubit 1 followed by a CNOT gate with qubit 1 as control and qubit 2 as target?",
                     "options": {"A": "|01>", "B": "|10>", "C": "(|00> + |11>) / sqrt(2) (Bell State |Phi+>)", "D": "(|01> + |10>) / sqrt(2)"},
                     "correct_option": "C", "explanation": "H on qubit 1 creates (|0> + |1>)/sqrt(2) (x) |0> = (|00> + |10>)/sqrt(2). The CNOT flips qubit 2 when qubit 1 is |1>, producing the entangled Bell state (|00> + |11>)/sqrt(2)."},
                    {"id": 11623, "question_number": 3, "question": "What does the No-Cloning Theorem in quantum mechanics prove?",
                     "options": {"A": "Quantum computers cannot execute Shor's algorithm", "B": "It is impossible to create an identical copy of an arbitrary unknown quantum state", "C": "Entanglement only occurs at absolute zero", "D": "Qubits cannot be measured"},
                     "correct_option": "B", "explanation": "The linearity of unitary quantum mechanics makes it mathematically impossible to design an operator that copies an arbitrary unknown quantum state |psi> onto a blank qubit."},
                    {"id": 11624, "question_number": 4, "question": "Which Pauli gate performs a pure phase-flip operation (|0> -> |0>, |1> -> -|1>)?",
                     "options": {"A": "Pauli-X", "B": "Pauli-Y", "C": "Pauli-Z", "D": "Hadamard"},
                     "correct_option": "C", "explanation": "The Pauli-Z matrix [1, 0; 0, -1] leaves |0> unchanged while multiplying |1> by a phase of -1."},
                    {"id": 11625, "question_number": 5, "question": "Why is quantum entanglement termed 'non-local'?",
                     "options": {"A": "Measuring one entangled qubit instantaneously determines the measurement outcome of the other, regardless of physical separation", "B": "It requires radio towers", "C": "It transmits classical data faster than light", "D": "It works only over short millimeter distances"},
                     "correct_option": "A", "explanation": "Entangled pairs exhibit quantum correlations that persist across arbitrary spatial separations, though no faster-than-light classical communication can occur without a classical channel."},
                ],
                [
                    {"chunk_id": "chunk_qc_02", "subject": "Quantum Computing Fundamentals",
                     "chapter": "Quantum Logic Gates & Entanglement (Bell States)", "topic": "Quantum Logic Gates and Entanglement",
                     "keywords": ["quantum gates", "hadamard", "cnot", "entanglement", "bell state", "no-cloning theorem", "pauli"],
                     "definition": "Quantum logic gates are elementary unitary transformations acting on qubit state vectors; multi-qubit gates generate non-separable entangled states.",
                     "formula_or_core_rule": "CNOT|c, t> = |c, c XOR t>; Bell State |Phi+> = (|00> + |11>)/sqrt(2).",
                     "worked_example": "Input |10> into CNOT (control=qubit 1): since control is 1, target flips from 0 to 1, producing output |11>.",
                     "content": "Single-qubit operations perform rotations on the Bloch sphere, while entangling gates like CNOT link multiple qubits into non-separable composite states that form the basis for quantum teleportation and algorithms."}
                ]
            ),
            make_ch(
                1163, 3, "Quantum Algorithms: Deutsch-Jozsa, Grover & Shor", 20,
                "Quantum parallelism, Deutsch-Jozsa oracle, Grover's quadratic search, and Shor's polynomial-time factoring algorithm.",
                ["Quantum parallelism and constructive/destructive interference", "Deutsch-Jozsa algorithm: constant vs balanced oracle determination", "Grover's Search Algorithm: amplitude amplification and O(sqrt(N)) speedup", "Shor's Algorithm: Quantum Fourier Transform (QFT) and period finding"],
                ["Grover Iterations: R ≈ (pi / 4) * sqrt(N)", "Classical Search: O(N) vs Grover: O(sqrt(N))", "Shor Complexity: O((log N)^3) (Polynomial vs Exponential)"],
                "Quantum Interference & Grover's Amplitude Amplification",
                "Quantum algorithms do not merely evaluate configurations simultaneously; they orchestrate constructive interference on target solutions and destructive interference on incorrect candidates. Grover's search algorithm finds a marked item in an unstructured database of size N in O(sqrt(N)) operations, providing a quadratic speedup over classical O(N) exhaustive search via iterative phase inversion and inversion-about-the-mean reflection operators.",
                "Shor's Factoring Algorithm & RSA Cryptanalysis",
                "Shor's algorithm solves prime factorization and discrete logarithms in polynomial time O((log N)^3), threatening public-key cryptosystems like RSA and ECC. Shor converts factoring into period finding of a modular exponential function f(x) = a^x mod N, utilizing the Quantum Fourier Transform (QFT) to extract periodicity with exponential speedup.",
                "Grover's algorithm provides quadratic speedup for unstructured search; Shor's algorithm provides exponential speedup for period-finding.",
                [
                    {"id": 11631, "question_number": 1, "question": "What is the time complexity of Grover's search algorithm to locate an item in an unsorted database of N items?",
                     "options": {"A": "O(N)", "B": "O(N^2)", "C": "O(sqrt(N))", "D": "O(log N)"},
                     "correct_option": "C", "explanation": "Grover's algorithm achieves a quadratic speedup over classical O(N) search by using amplitude amplification in O(sqrt(N)) oracle evaluations."},
                    {"id": 11632, "question_number": 2, "question": "Why does Shor's algorithm pose a catastrophic threat to classical RSA encryption?",
                     "options": {"A": "It guesses passwords via brute force", "B": "It factors large composite integers in polynomial time O((log N)^3), breaking the mathematical premise of RSA", "C": "It disables network routers", "D": "It generates infinite prime numbers"},
                     "correct_option": "B", "explanation": "RSA security rests on the computational hardness of prime factorization. Shor's algorithm uses the Quantum Fourier Transform to find periods in polynomial time, factoring RSA moduli efficiently."},
                    {"id": 11633, "question_number": 3, "question": "What key quantum sub-routine enables Shor's algorithm to achieve exponential speedup in period finding?",
                     "options": {"A": "Quantum Fourier Transform (QFT)", "B": "Simulated annealing", "C": "Bubble sort", "D": "Gradient descent"},
                     "correct_option": "A", "explanation": "The Quantum Fourier Transform (QFT) transforms a quantum state into the frequency domain in O((log N)^2) gate operations, revealing the periodicity of modular exponentiation."},
                    {"id": 11634, "question_number": 4, "question": "What problem does the Deutsch-Jozsa algorithm solve in a single quantum query that requires 2^(n-1) + 1 classical queries?",
                     "options": {"A": "Determining whether a boolean function f is constant (same output for all inputs) or balanced (output 0 for half, 1 for half)", "B": "Inverting a matrix", "C": "Finding shortest path in a graph", "D": "Sorting numbers"},
                     "correct_option": "A", "explanation": "The Deutsch-Jozsa algorithm evaluates whether an oracle function is constant or balanced in a single quantum evaluation via constructive interference."},
                    {"id": 11635, "question_number": 5, "question": "In Grover's algorithm, what geometric operation does the diffusion operator perform?",
                     "options": {"A": "Inversion about the average (mean) amplitude", "B": "Projection onto the Z-axis", "C": "Random rotation", "D": "Measurement collapse"},
                     "correct_option": "A", "explanation": "The Grover diffusion operator reflects state amplitudes about their mean, boosting the amplitude of marked states while suppressing unmarked states."}
                ],
                [
                    {"chunk_id": "chunk_qc_03", "subject": "Quantum Computing Fundamentals",
                     "chapter": "Quantum Algorithms: Deutsch-Jozsa, Grover & Shor", "topic": "Quantum Algorithms: Grover and Shor",
                     "keywords": ["quantum algorithms", "grover", "shor", "deutsch-jozsa", "quantum fourier transform", "qft", "amplitude amplification"],
                     "definition": "Quantum algorithms leverage quantum superposition, entanglement, and interference to solve mathematical problems exponentially or quadratically faster than classical algorithms.",
                     "formula_or_core_rule": "Grover Complexity: O(sqrt(N)); Shor Factoring: O((log N)^3).",
                     "worked_example": "For a database of N = 1,000,000 items: classical search requires ~500,000 queries; Grover's algorithm requires only ~785 iterations.",
                     "content": "Grover's algorithm provides quadratic speedup for search via amplitude amplification, while Shor's algorithm provides exponential speedup for integer factorization using the Quantum Fourier Transform."}
                ]
            )
        ]
    })

    # 117. Natural Language Processing (NLP)
    subs.append({
        "id": "SUB_NLP",
        "subject_id": 117,
        "name": "Natural Language Processing (NLP)",
        "slug": "natural-language-processing",
        "icon_symbol": "💬",
        "color_hex": "#06B6D4",
        "category": "Modern Tech",
        "description": "Text tokenization, distributed word embeddings, sequence modeling (RNNs/LSTMs), transformers, and self-attention.",
        "is_added": False,
        "chapters": [
            make_ch(
                1171, 1, "Text Preprocessing, Tokenization & Word Embeddings", 20,
                "Byte-Pair Encoding (BPE), WordPiece, distribution hypothesis, Word2Vec (Skip-gram, CBOW), and GloVe.",
                ["Subword tokenization: Byte-Pair Encoding (BPE) and WordPiece", "Vector space models: TF-IDF and cosine similarity", "Word2Vec architectures: Continuous Bag of Words (CBOW) and Skip-Gram", "GloVe matrix factorization and semantic vector arithmetic"],
                ["TF-IDF = TF(t, d) * log(N / DF(t))", "Cosine Similarity = (u . v) / (||u|| * ||v||)", "Skip-gram Objective: Maximize Sum(log P(w_t+j | w_t))"],
                "Tokenization & Subword Representation",
                "Natural Language Processing translates unstructured text into numeric tensor representations. Early word-level tokenization suffered from massive out-of-vocabulary (OOV) rates and morphological fragmentation. Modern language models utilize subword algorithms such as Byte-Pair Encoding (BPE) and WordPiece, which iteratively merge frequent character pairs, compressing vocabulary while preserving compositional morphology.",
                "Distributed Semantic Representations (Word2Vec & GloVe)",
                "The distributional hypothesis states: 'A word is characterized by the company it keeps.' Word2Vec formalizes this via two shallow neural architectures: CBOW predicts a target word from context words, while Skip-Gram predicts context words from a central word using negative sampling. GloVe combines matrix factorization with local window statistics, enabling geometric semantic analogies like vector('King') - vector('Man') + vector('Woman') ≈ vector('Queen').",
                "Subword tokenization eliminates out-of-vocabulary (OOV) tokens by decomposing unknown words into known byte/character fragments.",
                [
                    {"id": 11711, "question_number": 1, "question": "What primary problem of word-level tokenization is solved by Byte-Pair Encoding (BPE)?",
                     "options": {"A": "Out-of-Vocabulary (OOV) words are eliminated by segmenting rare words into known subword units", "B": "BPE makes neural networks run on CPU", "C": "BPE translates text into audio", "D": "BPE removes all punctuation"},
                     "correct_option": "A", "explanation": "BPE subword tokenization splits rare or unseen words into smaller constituent subword chunks, avoiding out-of-vocabulary unknown tokens."},
                    {"id": 11712, "question_number": 2, "question": "In Word2Vec, what is the architectural difference between Continuous Bag of Words (CBOW) and Skip-Gram?",
                     "options": {"A": "CBOW predicts the center target word from context words; Skip-Gram predicts surrounding context words from a center word", "B": "CBOW uses recurrent connections; Skip-Gram uses convolutions", "C": "Skip-Gram only runs on English text", "D": "CBOW requires labelled sentiment data"},
                     "correct_option": "A", "explanation": "CBOW aggregates surrounding context words to predict the target word, whereas Skip-gram uses the target word to predict context words."},
                    {"id": 11713, "question_number": 3, "question": "How is TF-IDF (Term Frequency-Inverse Document Frequency) calculated?",
                     "options": {"A": "TF multiplied by log(Total Documents / Documents containing term)", "B": "Word count divided by character count", "C": "Vocabulary size squared", "D": "Sentence length times page count"},
                     "correct_option": "A", "explanation": "TF-IDF multiplies local frequency in a document by the logarithm of inverse document frequency, penalizing universally common stop words."},
                    {"id": 11714, "question_number": 4, "question": "What semantic property is demonstrated by vector('King') - vector('Man') + vector('Woman') ≈ vector('Queen') in GloVe/Word2Vec?",
                     "options": {"A": "Linear semantic compositionality in embedding vector space", "B": "Overfitting", "C": "Loss of grammatical structure", "D": "Syntax tree inversion"},
                     "correct_option": "A", "explanation": "Dense vector embeddings capture relational semantic meanings as geometric vector displacements, allowing meaningful vector arithmetic."},
                    {"id": 11715, "question_number": 5, "question": "What is the range of Cosine Similarity between two non-zero vectors?",
                     "options": {"A": "[-1.0, 1.0]", "B": "[0.0, 1.0]", "C": "[-infinity, +infinity]", "D": "[0, 100]"},
                     "correct_option": "A", "explanation": "Cosine similarity equals the cosine of the angle between two vectors: +1 for identical directions, 0 for orthogonal, and -1 for opposite directions."}
                ],
                [
                    {"chunk_id": "chunk_nlp_01", "subject": "Natural Language Processing (NLP)",
                     "chapter": "Text Preprocessing, Tokenization & Word Embeddings", "topic": "Tokenization and Word Embeddings",
                     "keywords": ["nlp", "tokenization", "bpe", "word2vec", "glove", "tf-idf", "embeddings", "cosine similarity"],
                     "definition": "Word embeddings are dense, low-dimensional vector representations of words where semantically similar words map to nearby geometric coordinates.",
                     "formula_or_core_rule": "TF-IDF = TF * log(N / DF); Cosine Similarity = (u . v) / (||u|| * ||v||).",
                     "worked_example": "If word 'quantum' appears 5 times in a document of 100 words (TF=0.05), and in 10 out of 10,000 documents (IDF=log(1000)=3): TF-IDF = 0.15.",
                     "content": "NLP pipelines convert raw text into structured tokens via Byte-Pair Encoding and map tokens to continuous vector spaces using Skip-gram and GloVe distributed representations."}
                ]
            ),
            make_ch(
                1172, 2, "Sequence Models: RNNs, LSTMs & Seq2Seq Architectures", 20,
                "Recurrent neural networks, vanishing gradients, LSTM gating mechanisms, and encoder-decoder Seq2Seq.",
                ["Recurrent neural networks (RNN) and Backpropagation Through Time (BPTT)", "Vanishing and exploding gradient problems in recurrent loops", "Long Short-Term Memory (LSTM) gates: Forget, Input, Output", "Gated Recurrent Units (GRU) and Seq2Seq with Bahdanau attention"],
                ["f_t = sigma(W_f * [h_t-1, x_t] + b_f) (Forget Gate)", "i_t = sigma(W_i * [h_t-1, x_t] + b_i) (Input Gate)", "C_t = f_t * C_t-1 + i_t * tanh(W_c * [h_t-1, x_t] + b_c)", "h_t = o_t * tanh(C_t) (Hidden State)"],
                "Recurrent Architecture & Vanishing Gradients",
                "Standard feedforward neural networks cannot process variable-length sequential dependencies. Recurrent Neural Networks (RNNs) introduce cyclic feedback connections, maintaining an internal hidden state h_t = tanh(W_hh * h_t-1 + W_xh * x_t). However, backpropagation through time (BPTT) involves repeated matrix multiplications, causing gradients to explode or vanish exponentially over long sequences.",
                "Gated Architectures: LSTM & GRU",
                "Long Short-Term Memory (LSTM) networks resolve vanishing gradients by establishing a dedicated additive cell state C_t running through time. Information flow is regulated by three sigmoid-activated gates: Forget gate f_t (discards obsolete history), Input gate i_t (writes candidate memories), and Output gate o_t (filters cell state into hidden output h_t). GRUs streamline this into Update and Reset gates.",
                "In LSTMs, the additive cell state C_t acts as an information superhighway, allowing gradients to propagate backward without vanishing.",
                [
                    {"id": 11721, "question_number": 1, "question": "What is the primary mathematical cause of the vanishing gradient problem in standard recurrent neural networks (RNNs)?",
                     "options": {"A": "Repeated multiplication of hidden weight matrix W_hh across many time steps during BPTT causes gradients to decay exponentially when eigenvalues are < 1", "B": "High learning rates", "C": "Excessive GPU memory", "D": "ReLu activation functions"},
                     "correct_option": "A", "explanation": "During Backpropagation Through Time, gradients are multiplied by the recurrent weight matrix W_hh at every time step; if spectral radius is less than 1, gradients decay to zero over long sequences."},
                    {"id": 11722, "question_number": 2, "question": "What is the role of the 'Forget Gate' in an LSTM cell?",
                     "options": {"A": "Deletes the neural network weights", "B": "Decides what fraction of information from previous cell state C_t-1 should be retained or discarded", "C": "Resets the optimizer", "D": "Shuts down the GPU"},
                     "correct_option": "B", "explanation": "The forget gate outputs values between 0 (completely discard) and 1 (completely retain) via sigmoid activation applied to [h_t-1, x_t]."},
                    {"id": 11723, "question_number": 3, "question": "How does the Gated Recurrent Unit (GRU) differ architecturally from the standard LSTM?",
                     "options": {"A": "GRU has no gates", "B": "GRU combines cell state and hidden state, using only two gates (Reset and Update)", "C": "GRU cannot process sequential data", "D": "GRU uses four gates instead of three"},
                     "correct_option": "B", "explanation": "GRUs merge the cell state and hidden state, replacing the 3 LSTM gates with 2 gates (Reset and Update), reducing parameters and training latency."},
                    {"id": 11724, "question_number": 4, "question": "In a classical Seq2Seq encoder-decoder architecture without attention, what is the primary bottleneck?",
                     "options": {"A": "The entire variable-length input sequence must be compressed into a single fixed-size context vector", "B": "It requires dual GPUs", "C": "It cannot translate text", "D": "It only accepts numbers"},
                     "correct_option": "A", "explanation": "Compressing an arbitrarily long sentence into a single static context vector leads to severe performance degradation on long sequences."},
                    {"id": 11725, "question_number": 5, "question": "What technique prevents exploding gradients during RNN training?",
                     "options": {"A": "Gradient clipping (capping norm ||g|| at a maximum threshold)", "B": "Doubling learning rate", "C": "Removing activation functions", "D": "Zeroing all biases"},
                     "correct_option": "A", "explanation": "Gradient clipping rescales gradients if their L2 norm exceeds a threshold, preventing numerical overflow and catastrophic parameter updates."}
                ],
                [
                    {"chunk_id": "chunk_nlp_02", "subject": "Natural Language Processing (NLP)",
                     "chapter": "Sequence Models: RNNs, LSTMs & Seq2Seq Architectures", "topic": "Sequence Models and LSTMs",
                     "keywords": ["rnn", "lstm", "gru", "seq2seq", "bptt", "vanishing gradient", "forget gate", "hidden state"],
                     "definition": "Recurrent neural networks are sequential deep learning models that process sequential data by passing hidden state vectors through temporal recurrence loops.",
                     "formula_or_core_rule": "C_t = f_t * C_t-1 + i_t * C_candidate; h_t = o_t * tanh(C_t).",
                     "worked_example": "If forget gate f_t = 0.9 and input gate i_t = 0.1, 90% of old memory is preserved and 10% new information is written.",
                     "content": "Sequential deep learning transitioned from vanilla RNNs to gated LSTMs and GRUs to overcome vanishing gradients. LSTMs maintain memory across hundreds of time steps using additive cell state highways regulated by gating mechanisms."}
                ]
            ),
            make_ch(
                1173, 3, "Transformers, Self-Attention & Large Language Models", 20,
                "Scaled dot-product attention, multi-head attention, Positional Encoding, encoder-decoder, and autoregressive LLMs.",
                ["Scaled Dot-Product Attention: Query, Key, Value vectors", "Multi-Head Attention (MHA) and parallel representation subspaces", "Positional Encodings (Sinusoidal, RoPE) for sequence order", "Autoregressive generation (GPT) vs masked bidirectional representations (BERT)"],
                ["Attention(Q, K, V) = softmax((Q * K^T) / sqrt(d_k)) * V", "MultiHead(Q, K, V) = Concat(head_1, ..., head_h) * W_O", "head_i = Attention(Q * W_i^Q, K * W_i^K, V * W_i^V)"],
                "Scaled Dot-Product Attention & Multi-Head Projections",
                "The Transformer architecture ('Attention Is All You Need') replaces recurrence entirely with attention mechanisms. For every token, linear projections produce Query (Q), Key (K), and Value (V) vectors. Scaled dot-product attention computes compatibility between queries and keys, normalized by sqrt(d_k) to prevent softmax saturation in high dimensions. Multi-Head Attention projects Q, K, and V across multiple subspaces concurrently, enabling the model to attend to syntactic and semantic dependencies simultaneously.",
                "Positional Embeddings & Autoregressive Decoder LLMs",
                "Because attention operates permutation-invariantly over sets, positional encodings (sinusoidal or Rotary Position Embedding - RoPE) are injected to encode token sequence order. Modern Large Language Models (LLMs) like GPT utilize stacked decoder layers with causal masking (preventing tokens from attending to future positions), trained via next-token prediction cross-entropy loss over massive internet-scale text corpora.",
                "Scaling attention dot products by 1/sqrt(d_k) prevents logits from becoming extremely large, which would cause softmax gradients to vanish.",
                [
                    {"id": 11731, "question_number": 1, "question": "Why is the dot product of Query (Q) and Key (K) scaled by 1/sqrt(d_k) in the Transformer attention formula?",
                     "options": {"A": "To invert the matrix", "B": "To prevent large dot products from pushing the softmax function into regions with near-zero gradients", "C": "To convert words into characters", "D": "To reduce GPU temperature"},
                     "correct_option": "B", "explanation": "In high dimensions d_k, dot products grow large in magnitude, pushing softmax into flat saturated regions with vanishingly small gradients; scaling by sqrt(d_k) stabilizes variance to 1.0."},
                    {"id": 11732, "question_number": 2, "question": "Why do Transformers require Positional Encodings (e.g., Sinusoidal or RoPE)?",
                     "options": {"A": "To compress image tokens", "B": "Because self-attention is permutation-invariant and has no inherent awareness of token sequence order", "C": "To decrypt user queries", "D": "To speed up gradient descent"},
                     "correct_option": "B", "explanation": "Self-attention treats input tokens as an unordered set; positional encodings explicitly inject relative or absolute positional information into token representations."},
                    {"id": 11733, "question_number": 3, "question": "What is the primary architectural difference between BERT and GPT models?",
                     "options": {"A": "BERT is a bidirectional encoder trained on masked language modeling; GPT is a causal autoregressive decoder trained on next-token prediction", "B": "BERT uses CNNs; GPT uses RNNs", "C": "GPT only runs on audio data", "D": "BERT requires no training data"},
                     "correct_option": "A", "explanation": "BERT utilizes bidirectional attention across all tokens to build representations for classification and extraction, whereas GPT uses causal masking to generate text autoregressively."},
                    {"id": 11734, "question_number": 4, "question": "What is the computational complexity of standard self-attention with respect to sequence length N?",
                     "options": {"A": "O(N)", "B": "O(N * log N)", "C": "O(N^2)", "D": "O(1)"},
                     "correct_option": "C", "explanation": "Computing the N x N attention matrix (Q * K^T) scales quadratically O(N^2) with sequence length N, making long-context modeling computationally demanding."},
                    {"id": 11735, "question_number": 5, "question": "What is 'causal masking' in decoder-only language models?",
                     "options": {"A": "An upper-triangular mask of negative infinity that prevents a token from attending to future tokens during training", "B": "Deleting punctuation", "C": "Masking user IP addresses", "D": "Random dropout"},
                     "correct_option": "A", "explanation": "Causal masking sets attention scores to future positions to -infinity (becoming 0 after softmax), ensuring predictions depend only on past and present tokens."}
                ],
                [
                    {"chunk_id": "chunk_nlp_03", "subject": "Natural Language Processing (NLP)",
                     "chapter": "Transformers, Self-Attention & Large Language Models", "topic": "Transformer Architecture and Self-Attention",
                     "keywords": ["transformer", "self-attention", "query key value", "llm", "multi-head attention", "gpt", "bert", "causal mask"],
                     "definition": "The Transformer is a deep learning architecture based entirely on multi-head self-attention mechanisms, eliminating recurrence and enabling massive parallel training.",
                     "formula_or_core_rule": "Attention(Q, K, V) = softmax((Q * K^T) / sqrt(d_k)) * V; MultiHead = Concat(heads) * W_O.",
                     "worked_example": "For query vector q and key vector k with dimension d_k = 64: scaling factor = 1 / sqrt(64) = 0.125.",
                     "content": "Transformers revolutionized AI by replacing sequential recurrence with parallel multi-head self-attention. Scaled dot products map token relationships globally across sequences, powering modern foundational Large Language Models."}
                ]
            )
        ]
    })

    return subs

