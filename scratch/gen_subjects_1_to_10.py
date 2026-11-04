# scratch/gen_subjects_1_to_10.py
# Production subjects 1 to 10 for SYNAPSE AI Extended Catalog

def make_ch(cid, order, title, mins, summary, syllabus, formulas, p1_t, p1_c, p2_t, p2_c, tip, mcqs, rag):
    return {
        "chapter_id": cid,
        "chapter_order": order,
        "title": title,
        "estimated_minutes": mins,
        "summary": summary,
        "syllabus": syllabus,
        "core_formulas": formulas,
        "part1_title": p1_t,
        "part1_content": p1_c,
        "part2_title": p2_t,
        "part2_content": p2_c,
        "tip": tip,
        "mcqs": mcqs,
        "rag_chunks": rag
    }

def get_subjects():
    from make_data_tech import get_tech_catalog
    subs = get_tech_catalog() # Already has 111 (VLSI), 112 (Robotics), 113 (DevOps)

    # 114. Mobile App Development (Android & iOS)
    subs.append({
        "id": "SUB_MOBILE",
        "subject_id": 114,
        "name": "Mobile App Development (Android & iOS)",
        "slug": "mobile-app-development",
        "icon_symbol": "📱",
        "color_hex": "#10B981",
        "category": "Engineering",
        "description": "Native declarative UI frameworks, Android Jetpack Compose, Apple SwiftUI, state management, and offline persistence.",
        "is_added": False,
        "chapters": [
            make_ch(
                1141, 1, "Declarative UI Frameworks: Jetpack Compose & SwiftUI", 20,
                "State-driven declarative rendering, composition trees, recomposition lifecycles, and view reconciliation.",
                ["Imperative vs declarative UI rendering paradigms", "Composable functions, StateFlow, and State hoisting", "SwiftUI View protocol, @State, and @Binding property wrappers", "Recomposition scoping, memoization, and derived state"],
                ["Recomposition: UI = f(State)", "remember { mutableStateOf(value) }", "StateFlow.collectAsStateWithLifecycle()", "@Published / ObservableObject in Swift"],
                "Declarative UI Architecture & Composition Lifecycles",
                "Declarative mobile frameworks shift UI programming from mutable widget hierarchies to functional state projections: UI = f(State). In Jetpack Compose, UI elements are lightweight composable functions that emit tree nodes into a Composer. When observed state mutates, the runtime performs intelligent recomposition, re-evaluating only composables that read changed state parameters while skipping unchanged branches.",
                "SwiftUI & State Hoisting Paradigms",
                "Apple SwiftUI implements a similar reactive layout engine utilizing the View protocol and struct value types. State hoisting moves state upward to a common ancestor, transforming child components into stateless, previewable, and testable widgets. Side effects are isolated using LaunchedEffect or DisposableEffect in Compose, and .onAppear or .task in SwiftUI.",
                "In Compose, never execute heavy database or network queries directly inside composable functions without LaunchedEffect or CoroutineScope.",
                [
                    {"id": 11411, "question_number": 1, "question": "What is 'recomposition' in Android Jetpack Compose?",
                     "options": {"A": "Recompiling Kotlin bytecode at runtime", "B": "Re-executing composable functions to update the UI hierarchy when underlying state changes", "C": "Restarting the Android Activity", "D": "Rebuilding the Gradle APK"},
                     "correct_option": "B", "explanation": "Recomposition is the process where Compose re-invokes composables whose input state has mutated to produce an updated visual node tree."},
                    {"id": 11412, "question_number": 2, "question": "What is the purpose of 'State Hoisting' in declarative UI design?",
                     "options": {"A": "Moving state to a caller to make components stateless, reusable, and single-source-of-truth", "B": "Caching network requests in disk memory", "C": "Elevating database permissions", "D": "Compressing PNG drawables"},
                     "correct_option": "A", "explanation": "State hoisting pattern replaces internal state with a value parameter and an event callback, making composables stateless and testable."},
                    {"id": 11413, "question_number": 3, "question": "In Apple SwiftUI, which property wrapper establishes a two-way data binding to a parent value?",
                     "options": {"A": "@Binding", "B": "@State", "C": "@ObservedObject", "D": "@Environment"},
                     "correct_option": "A", "explanation": "@Binding creates a two-way mutable reference allowing a child view to mutate state owned by a parent view without duplicating storage."},
                    {"id": 11414, "question_number": 4, "question": "Why is 'remember { mutableStateOf(...) }' used in Jetpack Compose?",
                     "options": {"A": "To preserve state across Activity recreation only", "B": "To retain an immutable calculation across recompositions within the composition tree", "C": "To store encrypted passwords", "D": "To trigger background GPS logging"},
                     "correct_option": "B", "explanation": "remember preserves state values across recomposition passes so variables are not reset on every frame."},
                    {"id": 11415, "question_number": 5, "question": "Which coroutine effect handler in Compose executes suspend tasks keyed to a changing state parameter?",
                     "options": {"A": "LaunchedEffect", "B": "rememberCoroutineScope", "C": "SideEffect", "D": "DisposableEffect"},
                     "correct_option": "A", "explanation": "LaunchedEffect launches a coroutine scoped to the composable lifecycle and restarts when its key parameters change."}
                ],
                [
                    {"chunk_id": "chunk_mobile_01", "subject": "Mobile App Development (Android & iOS)",
                     "chapter": "Declarative UI Frameworks: Jetpack Compose & SwiftUI", "topic": "Declarative UI and Recomposition",
                     "keywords": ["jetpack compose", "swiftui", "declarative ui", "state hoisting", "recomposition", "stateflow", "android"],
                     "definition": "Declarative mobile UI frameworks render user interfaces as direct, reactive projections of application state (UI = f(State)).",
                     "formula_or_core_rule": "UI = f(State); Recomposition skips functions whose input state has not mutated.",
                     "worked_example": "When a counter integer changes from 0 to 1, Compose re-executes only the Text(counter) composable, leaving sibling headers untouched.",
                     "content": "Jetpack Compose on Android and SwiftUI on iOS replace legacy XML and Storyboard hierarchies with reactive, declarative Kotlin and Swift functions. State hoisting isolates state ownership, maximizing testability and preventing rendering inconsistencies."}
                ]
            ),
            make_ch(
                1142, 2, "Mobile Architecture: MVVM, Clean Architecture & Data Flow", 20,
                "Model-View-ViewModel (MVVM) separation, Unidirectional Data Flow (UDF), and Repository design pattern.",
                ["Unidirectional Data Flow (UDF) patterns", "ViewModel lifecycle persistence across configuration changes", "Domain use-cases and clean architectural boundaries", "Kotlin Coroutines & Flow versus Swift Concurrency & Async/Await"],
                ["Events Up, State Down (UDF)", "ViewModel: survives configuration changes via ViewModelStore", "Flow.stateIn(scope, SharingStarted.WhileSubscribed(5000), initial)"],
                "Unidirectional Data Flow & MVVM Implementation",
                "Mobile Clean Architecture segregates software into Presentation, Domain, and Data tiers. The Model-View-ViewModel (MVVM) pattern enforces Unidirectional Data Flow (UDF): Views dispatch user events upward to ViewModels, and ViewModels emit immutable UI state flows downward to Views. ViewModels survive screen orientation configuration changes through ViewModelStoreOwner retention.",
                "Repository Pattern & Concurrency Dispatching",
                "The Repository pattern provides a clean API for data operations, mediating between local Room/CoreData databases and remote REST/GraphQL backends. Coroutine dispatchers ensure strict thread isolation: Dispatchers.Main drives UI rendering, Dispatchers.IO executes disk/network I/O, and Dispatchers.Default handles compute-heavy parsing.",
                "In ViewModels, always expose state via read-only StateFlow rather than mutable MutableStateFlow to preserve encapsulation.",
                [
                    {"id": 11421, "question_number": 1, "question": "What core principle governs Unidirectional Data Flow (UDF) in modern mobile architecture?",
                     "options": {"A": "Views mutate repository models directly", "B": "Events flow upward from View to ViewModel, and State flows downward from ViewModel to View", "C": "Data travels simultaneously in both directions without restriction", "D": "Network APIs update UI widgets without intermediate state"},
                     "correct_option": "B", "explanation": "UDF dictates that events flow up (View -> ViewModel) and state flows down (ViewModel -> View), ensuring single-source-of-truth consistency."},
                    {"id": 11422, "question_number": 2, "question": "Why does an Android ViewModel survive device screen rotation while an Activity is destroyed and recreated?",
                     "options": {"A": "ViewModels are saved directly in Linux kernel shared memory", "B": "ViewModels are retained in a ViewModelStore owned by the Activity's NonConfigurationInstances", "C": "Screen rotation does not destroy Activities", "D": "ViewModels run as background services"},
                     "correct_option": "B", "explanation": "The Android framework retains the ViewModelStore across configuration changes, re-attaching existing ViewModels to new Activity instances."},
                    {"id": 11423, "question_number": 3, "question": "Which Kotlin Coroutine Dispatcher should be selected for database read/write queries and HTTP network calls?",
                     "options": {"A": "Dispatchers.Main", "B": "Dispatchers.IO", "C": "Dispatchers.Default", "D": "Dispatchers.Unconfined"},
                     "correct_option": "B", "explanation": "Dispatchers.IO is backed by an elastic thread pool optimized for blocking disk I/O and network operations without starving the UI thread."},
                    {"id": 11424, "question_number": 4, "question": "What is the primary role of the Repository layer in Clean Architecture?",
                     "options": {"A": "To render UI widgets", "B": "To abstract data sources (local cache vs remote API) and provide a clean data access interface to domain use-cases", "C": "To manage Android Fragment transactions", "D": "To compile native C++ libraries"},
                     "correct_option": "B", "explanation": "Repositories abstract data sourcing logic, coordinating between local SQLite databases and remote network APIs transparently."},
                    {"id": 11425, "question_number": 5, "question": "Why is 'SharingStarted.WhileSubscribed(5000)' used when converting a cold Flow to StateFlow in Android ViewModels?",
                     "options": {"A": "It deletes the database after 5 seconds", "B": "It keeps the upstream flow active for 5 seconds after the last collector detaches, handling quick configuration changes smoothly", "C": "It sets maximum network timeout to 5 seconds", "D": "It throttles UI frame rates to 5 fps"},
                     "correct_option": "B", "explanation": "The 5-second buffer prevents restarting expensive upstream flows when an Activity is temporarily destroyed during rapid screen rotation."}
                ],
                [
                    {"chunk_id": "chunk_mobile_02", "subject": "Mobile App Development (Android & iOS)",
                     "chapter": "Mobile Architecture: MVVM, Clean Architecture & Data Flow", "topic": "MVVM and Clean Architecture",
                     "keywords": ["mvvm", "clean architecture", "udf", "viewmodel", "coroutines", "stateflow", "repository pattern"],
                     "definition": "MVVM is a software architectural pattern that separates the graphical user interface from business logic through data binding and reactive state flows.",
                     "formula_or_core_rule": "Events Up, State Down; ViewModel survives configuration changes via ViewModelStore.",
                     "worked_example": "When a user taps 'Refresh', the View emits onRefreshRequested(); the ViewModel launches a coroutine on Dispatchers.IO to update StateFlow.",
                     "content": "Clean mobile architecture maintains decoupled presentation, domain, and data layers. ViewModels mediate between domain use-cases and declarative UI composables using unidirectional data flows."}
                ]
            ),
            make_ch(
                1143, 3, "Offline Persistence, Concurrency & Lifecycle Optimization", 20,
                "Room SQLite abstraction, CoreData/SwiftData, WorkManager background tasks, and battery efficiency.",
                ["Room persistence library: Entities, DAOs, and TypeConverters", "SQLite transaction semantics and WAL (Write-Ahead Logging)", "WorkManager and BGTaskScheduler background scheduling", "Memory management: WeakReference, leak detection, and battery optimization"],
                ["SQLite Write-Ahead Logging (WAL): Concurrent readers + single writer", "WorkManager Constraints: setRequiredNetworkType, setRequiresBatteryNotLow"],
                "Offline-First Architecture & SQLite Abstraction",
                "Offline-first mobile applications treat the on-device database as the single source of truth for UI display, while background workers synchronize mutations with remote backends. The Room persistence library on Android provides compile-time SQLite query verification, mapping entity data classes to relational tables and returning reactive Flow streams.",
                "Background Processing & Power Management",
                "Mobile operating systems aggressively throttle background execution to conserve battery power (Android Doze mode and iOS Background Execution limits). WorkManager guarantees deferred task execution obeying battery and network constraints, utilizing JobScheduler under the hood.",
                "Always enable WAL (Write-Ahead Logging) mode in mobile SQLite databases to allow concurrent readers alongside an active writer.",
                [
                    {"id": 11431, "question_number": 1, "question": "What is the primary advantage of SQLite Write-Ahead Logging (WAL) mode in mobile applications?",
                     "options": {"A": "Disables database indexing", "B": "Enables concurrent read operations while a write transaction is in progress", "C": "Encrypts all tables automatically", "D": "Compresses images on disk"},
                     "correct_option": "B", "explanation": "In WAL mode, readers do not block writers and writers do not block readers, drastically reducing UI thread latency during database operations."},
                    {"id": 11432, "question_number": 2, "question": "Which Android Jetpack component guarantees reliable background task execution even across app restarts and device reboots?",
                     "options": {"A": "WorkManager", "B": "AsyncTask", "C": "BroadcastReceiver", "D": "HandlerThread"},
                     "correct_option": "A", "explanation": "WorkManager is the recommended solution for persistent, deferrable background work that must execute reliably under specified constraints."},
                    {"id": 11433, "question_number": 3, "question": "In Room Database, what does a Data Access Object (DAO) interface define?",
                     "options": {"A": "Visual theme styles", "B": "Methods for querying, inserting, updating, and deleting database entity records", "C": "Network SSL certificate pinning", "D": "Bluetooth pairing protocols"},
                     "correct_option": "B", "explanation": "DAOs are interfaces annotated with @Dao that declare SQL operations (@Query, @Insert, @Update, @Delete) verified at compile time by Room."},
                    {"id": 11434, "question_number": 4, "question": "What is an Android Memory Leak?",
                     "options": {"A": "A physical RAM chip failure", "B": "An unused object retained in heap memory because an active reference prevents garbage collection", "C": "Excessive disk usage", "D": "High CPU fan speed"},
                     "correct_option": "B", "explanation": "A memory leak occurs when an object that is no longer needed (such as a destroyed Activity) is held by a long-lived static or singleton reference, preventing GC."},
                    {"id": 11435, "question_number": 5, "question": "What state does Android Doze Mode enter when a device is unplugged, stationary, and screen-off for an extended duration?",
                     "options": {"A": "Deep sleep where network access, background syncs, and standard alarms are suspended into periodic maintenance windows", "B": "Factory reset", "C": "Airplane mode", "D": "CPU overclocking"},
                     "correct_option": "A", "explanation": "Doze mode conserves battery by batching app background activities, delaying network access and non-critical jobs to maintenance windows."}
                ],
                [
                    {"chunk_id": "chunk_mobile_03", "subject": "Mobile App Development (Android & iOS)",
                     "chapter": "Offline Persistence, Concurrency & Lifecycle Optimization", "topic": "Offline Persistence and Background Sync",
                     "keywords": ["room database", "sqlite", "wal mode", "workmanager", "offline persistence", "android lifecycle", "memory leak"],
                     "definition": "Offline-first mobile architecture treats local persistent storage as the authoritative data source, asynchronously syncing mutations with remote cloud APIs.",
                     "formula_or_core_rule": "WAL Mode allows concurrent readers + single writer; WorkManager guarantees execution across reboots.",
                     "worked_example": "A user drafts a study note offline; Room saves it instantly (0ms latency), and WorkManager syncs it when WiFi reconnects.",
                     "content": "Offline-first mobile applications leverage Room and CoreData for persistent caching, coupled with constraint-aware background scheduling via WorkManager to maintain battery efficiency."}
                ]
            )
        ]
    })

    # 115. Blockchain & Distributed Ledger Tech
    subs.append({
        "id": "SUB_BLOCKCHAIN",
        "subject_id": 115,
        "name": "Blockchain & Distributed Ledger Tech",
        "slug": "blockchain-distributed-ledger",
        "icon_symbol": "⛓️",
        "color_hex": "#F59E0B",
        "category": "Engineering",
        "description": "Cryptographic hashing, consensus protocols (PoW, PoS), smart contract execution on EVM, and decentralized applications.",
        "is_added": False,
        "chapters": [
            make_ch(
                1151, 1, "Cryptographic Primitives, Hashing & Merkle Trees", 20,
                "SHA-256 cryptographic hashes, asymmetric public-key cryptography, ECDSA signatures, and Merkle tree verification.",
                ["Cryptographic hash functions: pre-image resistance and collision resistance", "Elliptic Curve Digital Signature Algorithm (ECDSA: secp256k1)", "Merkle Tree construction, root hashes, and SPV proof paths", "Block header anatomy: previous hash, timestamp, nonce, Merkle root"],
                ["SHA-256(SHA-256(Block_Header)) < Target", "Merkle Root = H(H(A) + H(B))", "Public Key Q = d * G (Elliptic Curve Scalar Multiplication)"],
                "Cryptographic Hashing & Elliptic Curve Signatures",
                "Blockchain distributed ledgers rely on cryptographic primitives to establish tamper-evident immutability. Cryptographic hash functions such as SHA-256 guarantee pre-image resistance (infeasible to find x from H(x)), second pre-image resistance, and collision resistance. Asymmetric cryptography utilizing elliptic curves (such as secp256k1 in Bitcoin and Ethereum) generates public keys through scalar point multiplication Q = d * G, where d is the private key.",
                "Merkle Trees & Lightweight Verification",
                "A Merkle Tree organizes all transactions within a block into a binary hash hierarchy. Leaves contain hashes of individual transaction data, while parent nodes store the hash of concatenated child hashes, culminating in the Merkle Root. Simplified Payment Verification (SPV) enables thin mobile clients to verify transaction inclusion with O(log N) proofs without downloading entire blocks.",
                "In Merkle trees, verifying membership of a transaction among N items requires only log2(N) sibling hashes.",
                [
                    {"id": 11511, "question_number": 1, "question": "What property of a cryptographic hash function guarantees that finding two distinct inputs x and y such that H(x) = H(y) is computationally infeasible?",
                     "options": {"A": "Collision resistance", "B": "Reversibility", "C": "Linearity", "D": "Commutativity"},
                     "correct_option": "A", "explanation": "Collision resistance ensures that finding any two different input messages yielding the exact same hash digest is computationally impossible."},
                    {"id": 11512, "question_number": 2, "question": "What is the time complexity to verify that a transaction is included in a block using a Merkle proof path for a block with N transactions?",
                     "options": {"A": "O(1)", "B": "O(log N)", "C": "O(N)", "D": "O(N^2)"},
                     "correct_option": "B", "explanation": "Because a Merkle tree is a balanced binary tree of height log2(N), an inclusion proof requires only log2(N) sibling hashes."},
                    {"id": 11513, "question_number": 3, "question": "In Bitcoin and Ethereum, what elliptic curve standard is utilized for ECDSA key generation?",
                     "options": {"A": "secp256k1", "B": "Curve25519", "C": "RSA-4096", "D": "DES-56"},
                     "correct_option": "A", "explanation": "secp256k1 is the specific Koblitz elliptic curve parameters y^2 = x^3 + 7 over prime field F_p used for Bitcoin and Ethereum public-key cryptography."},
                    {"id": 11514, "question_number": 4, "question": "What occurs if an attacker alters a single byte of transaction data inside an existing historical block?",
                     "options": {"A": "The Merkle root changes completely, invalidating the block hash and breaking the cryptographic chain for all subsequent blocks", "B": "The miner receives a fine", "C": "Only that transaction is deleted", "D": "The network increases block rewards"},
                     "correct_option": "A", "explanation": "Altering any transaction changes its hash, which propagates up the Merkle tree to change the Merkle root, invalidating the block header hash and breaking all child block references."},
                    {"id": 11515, "question_number": 5, "question": "What is the role of the 'nonce' field in a Proof-of-Work block header?",
                     "options": {"A": "An arbitrary 32-bit integer that miners increment repeatedly until the block header hash falls below the network target difficulty", "B": "The wallet address receiving transaction fees", "C": "The software version number", "D": "The block size in megabytes"},
                     "correct_option": "A", "explanation": "Miners vary the nonce value to generate distinct candidate block header hashes until they discover a hash strictly below the target difficulty threshold."}
                ],
                [
                    {"chunk_id": "chunk_bc_01", "subject": "Blockchain & Distributed Ledger Tech",
                     "chapter": "Cryptographic Primitives, Hashing & Merkle Trees", "topic": "Blockchain Cryptography & Merkle Trees",
                     "keywords": ["blockchain", "cryptography", "sha-256", "merkle tree", "ecdsa", "secp256k1", "spv proof"],
                     "definition": "A blockchain is a decentralized, distributed, and cryptographically linked sequence of data blocks ensuring tamper-evident transaction immutability.",
                     "formula_or_core_rule": "Merkle Root = H(H(A) + H(B)); Inclusion proof complexity = O(log N).",
                     "worked_example": "For a block with 1,024 transactions, an SPV light client requires only 10 hash hashes (log2(1024)) to verify transaction inclusion.",
                     "content": "Cryptographic primitives establish blockchain integrity: SHA-256 hashing secures tamper-evident linkage, ECDSA elliptic curves verify transaction authorship, and Merkle trees enable efficient logarithmic inclusion proofs."}
                ]
            ),
            make_ch(
                1152, 2, "Consensus Mechanisms: PoW, PoS & Byzantine Fault Tolerance", 20,
                "Nakamoto consensus, Sybil attack defense, Proof of Stake slashing, and BFT consensus algorithms.",
                ["Byzantine Generals Problem and asynchronous consensus limits", "Proof of Work (PoW): Difficulty adjustment, hash rate, and 51% attacks", "Proof of Stake (PoS): Validators, Casper FFG, and slashing penalties", "Practical Byzantine Fault Tolerance (PBFT) and Raft comparison"],
                ["Difficulty_new = Difficulty_old * (Actual_Time / Target_Time)", "BFT Maximum Faulty Nodes f < (N - 1) / 3", "PoS Finality = 2/3 Supermajority Validator Signatures"],
                "Byzantine Fault Tolerance & Nakamoto Consensus",
                "Distributed ledgers must reach consensus across untrusted, asynchronous networks where malicious nodes may broadcast contradictory messages (the Byzantine Generals Problem). Nakamoto consensus in Proof of Work solves Sybil attacks by coupling voting weight to physical computational power (hash rate). Miners compete to solve cryptographic proof-of-work puzzles, with the network accepting the longest valid chain.",
                "Proof of Stake & Economic Finality",
                "Proof of Stake (PoS) replaces energy-intensive computation with economic bonded capital. Validators lock cryptocurrency as collateral to propose and attest to blocks. Byzantine attacks and double-signing are deterred by programmatic slashing penalties that burn the validator's stake. Classical BFT consensus (e.g., PBFT) provides deterministic instant finality but requires quadratic message complexity O(N^2), limiting validator set size.",
                "Classical BFT consensus can tolerate up to f faulty or malicious nodes if and only if the total node count N >= 3f + 1.",
                [
                    {"id": 11521, "question_number": 1, "question": "What maximum fraction of malicious Byzantine nodes can a classical BFT consensus network tolerate?",
                     "options": {"A": "Strictly less than 1/3 of total nodes (f < N/3)", "B": "Up to 1/2 of total nodes", "C": "Up to 2/3 of total nodes", "D": "100% of nodes"},
                     "correct_option": "A", "explanation": "Classical Byzantine Fault Tolerance requires N >= 3f + 1 nodes to achieve consensus, meaning the system tolerates strictly fewer than 33.3% malicious nodes."},
                    {"id": 11522, "question_number": 2, "question": "What is 'slashing' in Proof of Stake (PoS) protocols?",
                     "options": {"A": "Halving transaction fees", "B": "Destroying or confiscating a portion of a validator's bonded cryptocurrency stake for malicious behavior such as double-signing", "C": "Deleting user wallets", "D": "Reducing block sizes"},
                     "correct_option": "B", "explanation": "Slashing imposes financial penalties by burning bonded stake when a validator signs conflicting blocks or violates protocol rules."},
                    {"id": 11523, "question_number": 3, "question": "What is the primary vulnerability known as a '51% Attack' in Proof of Work blockchains?",
                     "options": {"A": "Stealing private keys from offline wallets", "B": "An entity controlling majority hash rate rewriting recent transaction history to execute double-spends", "C": "Shutting down the internet", "D": "Forcing Bitcoin supply cap to exceed 21 million"},
                     "correct_option": "B", "explanation": "Controlling over 50% of mining power allows an attacker to outpace the honest chain, reorganizing recent blocks to reverse their own transactions (double-spending)."},
                    {"id": 11524, "question_number": 4, "question": "How does Bitcoin's difficulty adjustment algorithm maintain an average 10-minute block interval?",
                     "options": {"A": "Every 2,016 blocks, it compares elapsed time against 2 weeks and scales the hash difficulty target proportionally", "B": "A central server updates the difficulty", "C": "Miners vote on difficulty every hour", "D": "Difficulty increases automatically on every single block"},
                     "correct_option": "A", "explanation": "Bitcoin recalibrates target difficulty every 2,016 blocks (~2 weeks) by multiplying current difficulty by (Actual Time / 20,160 minutes)."},
                    {"id": 11525, "question_number": 5, "question": "What is the 'Nothing at Stake' problem in naive Proof of Stake designs?",
                     "options": {"A": "Validators have zero financial incentive to validate transactions", "B": "Without slashing penalties, validators can vote on every competing fork simultaneously at zero computational cost", "C": "Block rewards are too high", "D": "Mining rigs consume excessive electricity"},
                     "correct_option": "B", "explanation": "Unlike PoW where mining multiple chains splits hash power, PoS without slashing allows validators to sign every fork cost-free, preventing consensus convergence."}
                ],
                [
                    {"chunk_id": "chunk_bc_02", "subject": "Blockchain & Distributed Ledger Tech",
                     "chapter": "Consensus Mechanisms: PoW, PoS & Byzantine Fault Tolerance", "topic": "Blockchain Consensus Mechanisms",
                     "keywords": ["consensus", "proof of work", "proof of stake", "byzantine fault tolerance", "slashing", "51% attack", "pow", "pos"],
                     "definition": "Blockchain consensus algorithms coordinate distributed nodes to agree on a single authoritative ledger history across untrusted networks.",
                     "formula_or_core_rule": "BFT Threshold: N >= 3f + 1; PoW: Hash(Header) < Target.",
                     "worked_example": "In a 10-node BFT system: 10 >= 3f + 1 => 3f <= 9 => f <= 3; the network tolerates at most 3 malicious nodes.",
                     "content": "Consensus mechanisms establish trust in decentralized networks. Proof of Work ties Sybil resistance to computational energy, while Proof of Stake enforces economic security through validator capital bonding and slashing."}
                ]
            ),
            make_ch(
                1153, 3, "Smart Contracts, Ethereum Virtual Machine & DApps", 20,
                "EVM state transition machine, Solidity smart contracts, Gas economics, and decentralized finance (DeFi).",
                ["Ethereum Virtual Machine (EVM) stack architecture and bytecode execution", "Gas mechanics: base fee, priority fee, and block gas limit", "Solidity security vulnerabilities: reentrancy and integer overflows", "ERC token standards (ERC-20, ERC-721) and DeFi Automated Market Makers (AMM)"],
                ["Total Gas Fee = Gas_Used * (Base_Fee + Priority_Fee)", "Constant Product Market Maker: x * y = k", "EVM Stack Depth Limit = 1024 items"],
                "Ethereum Virtual Machine (EVM) & Gas Accounting",
                "The Ethereum Virtual Machine (EVM) is a Turing-complete, quasi-Turing machine that executes smart contract bytecode. The EVM is a stack-based machine with a 256-bit word size, ephemeral memory, and persistent disk storage. To prevent infinite loops (the Halting Problem), every bytecode opcode consumes a predetermined amount of 'Gas'. Transactions set a gas limit and pay fees in Gwei.",
                "Smart Contract Security & Automated Market Makers",
                "Smart contracts written in Solidity encode self-executing business logic. Flawed contracts are susceptible to exploits such as Reentrancy attacks (exemplified by the 2016 DAO exploit), where external calls hijack execution flow before balance states update. Decentralized finance protocols utilize Constant Product Automated Market Makers (AMMs) governed by the formula x * y = k to provide decentralized liquidity without order books.",
                "To prevent reentrancy attacks, always apply the Checks-Effects-Interactions pattern: update internal balances BEFORE calling external addresses.",
                [
                    {"id": 11531, "question_number": 1, "question": "What is the primary economic purpose of the 'Gas' mechanism in the Ethereum Virtual Machine (EVM)?",
                     "options": {"A": "To charge sales tax to users", "B": "To solve the Halting Problem and prevent denial-of-service infinite loops by pricing computational steps", "C": "To cool server hardware", "D": "To pay internet service providers"},
                     "correct_option": "B", "explanation": "Gas assigns a deterministic computational cost to every EVM opcode, ensuring execution terminates when prepaid gas is exhausted and preventing infinite execution attacks."},
                    {"id": 11532, "question_number": 2, "question": "What famous smart contract vulnerability allowed the 2016 DAO exploit?",
                     "options": {"A": "Reentrancy attack, where an external contract recursively recalled withdraw before its internal balance was updated", "B": "Weak Wi-Fi password", "C": "Database SQL injection", "D": "Buffer overflow in C++ runtime"},
                     "correct_option": "A", "explanation": "In a reentrancy exploit, a fallback function in a malicious contract recursively invokes the vulnerable withdraw function before the caller updates internal balances, draining funds."},
                    {"id": 11533, "question_number": 3, "question": "What mathematical invariant governs Uniswap v2 constant product automated market makers?",
                     "options": {"A": "x + y = k", "B": "x * y = k", "C": "x / y = k", "D": "x^2 + y^2 = k"},
                     "correct_option": "B", "explanation": "Constant product AMMs enforce x * y = k, where x and y represent asset reserve quantities; trades must preserve the invariant product k (ignoring trading fees)."},
                    {"id": 11534, "question_number": 4, "question": "What Ethereum token standard defines interchangeable, fungible tokens (like USDT or DAI)?",
                     "options": {"A": "ERC-20", "B": "ERC-721", "C": "ERC-1155", "D": "ERC-404"},
                     "correct_option": "A", "explanation": "ERC-20 is the universal standard for fungible tokens, defining functions such as transfer, approve, and balanceOf."},
                    {"id": 11535, "question_number": 5, "question": "What best-practice coding pattern prevents reentrancy vulnerabilities in Solidity contracts?",
                     "options": {"A": "Checks-Effects-Interactions pattern", "B": "Using assembly code only", "C": "Removing all require statements", "D": "Hiding contract source code"},
                     "correct_option": "A", "explanation": "The Checks-Effects-Interactions pattern mandates verifying conditions first, updating internal state variables second, and interacting with external contracts last."}
                ],
                [
                    {"chunk_id": "chunk_bc_03", "subject": "Blockchain & Distributed Ledger Tech",
                     "chapter": "Smart Contracts, Ethereum Virtual Machine & DApps", "topic": "Smart Contracts and EVM",
                     "keywords": ["smart contracts", "ethereum", "evm", "solidity", "gas", "reentrancy", "defi", "amm", "erc-20"],
                     "definition": "Smart contracts are autonomous, self-executing programs deployed onto distributed blockchain networks that enforce agreed contractual terms via code.",
                     "formula_or_core_rule": "x * y = k (AMM Invariant); Total Gas Fee = Gas_Units * (Base_Fee + Priority_Fee).",
                     "worked_example": "In an AMM pool with 10 ETH and 20,000 USDC (k = 200,000): buying ETH reduces ETH and increases USDC, raising the relative price.",
                     "content": "Smart contracts execute deterministically within the EVM sandbox. Gas economics prevent infinite resource consumption, while standards like ERC-20 and constant-product AMMs form the backbone of decentralized finance."}
                ]
            )
        ]
    })

    return subs

