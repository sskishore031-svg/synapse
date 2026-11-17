package com.siamts.synapse.data.local

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Embedded university-grade curriculum for Chemistry.
 * 8 progressive college chapters with complete lessons, MCQs, and RAG knowledge chunks.
 */
object CoreChemistryCurriculum {

    fun getChemistrySubject(): CatalogSubject {
        return CatalogSubject(
            id = "CHEM",
            subjectId = 6,
            name = "Chemistry",
            slug = "chemistry",
            iconSymbol = "🧪",
            colorHex = "#EC4899",
            category = "Physical Sciences",
            description = "University chemistry covering quantum atomic theory, bonding, periodic coordination, thermodynamics, kinetics, organic mechanisms, and spectroscopy.",
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
            chapterId = 1,
            chapterOrder = 1,
            title = "Atomic Structure & Quantum Numbers",
            estimatedMinutes = 25,
            summary = "Bohr model, de Broglie matter waves, Heisenberg uncertainty, quantum numbers (n, l, m_l, m_s), Aufbau/Hund rules, and radial probability distribution functions.",
            syllabus = listOf("Bohr Hydrogen Atom, Rydberg Equation & Emission Spectra", "Heisenberg Uncertainty Principle & Wave-Particle Duality", "Quantum Numbers: Principal (n), Azimuthal (l), Magnetic (m_l) & Spin (m_s)", "Electronic Configuration Rules: Aufbau Principle, Pauli Exclusion & Hund's Rule", "Radial & Angular Wavefunctions, Nodes & Orbital Contours (s, p, d, f)"),
            coreFormulas = listOf("Rydberg Formula: 1 / lambda = R_H * (1/n_1^2 - 1/n_2^2) (where R_H = 1.097 * 10^7 m^(-1))", "Bohr Orbit Energy: E_n = - (13.6 eV * Z^2) / n^2", "Heisenberg Uncertainty: Delta_x * Delta_p >= hbar / 2 = h / (4*pi)", "Total Nodes in Orbital: Total Nodes = n - 1 (Radial = n - l - 1, Angular = l)", "Orbital Angular Momentum: L = sqrt(l * (l + 1)) * hbar"),
            part1Title = "Quantum Atomic Architecture, Orbitals & Wave Mechanics",
            part1Content = "The modern understanding of atomic structure originated from the failure of classical electrodynamics to explain atomic stability and discrete line spectra. Niels Bohr introduced quantized angular momentum L = n*hbar for planetary orbits, yielding discrete energy levels E_n = -13.6 eV * Z^2 / n^2 for hydrogenic systems. The transition between orbits n_2 and n_1 releases or absorbs a photon whose wavenumber follows Johannes Rydberg's empirical equation.\n\nWerner Heisenberg formulated the Uncertainty Principle Delta_x * Delta_p >= hbar / 2, proving that deterministic electronic orbits are physical impossibilities. Instead, Erwin Schrödinger's wave equation describes electrons as 3D standing probability waves characterized by spatial wavefunctions psi(r, theta, phi). The square of the wavefunction |psi|^2 represents probability density, and the radial probability distribution 4*pi*r^2*R(r)^2 d_r dictates the likelihood of locating an electron within a spherical shell at distance r from the nucleus.\n\nElectrons in multi-electron atoms are fully specified by four quantum numbers: Principal (n = 1, 2, ...), governing shell energy and radial extent; Azimuthal/Orbital Angular Momentum (l = 0, ..., n-1), specifying subshell shape (s, p, d, f); Magnetic (m_l = -l, ..., +l), determining spatial orbital orientation; and Electron Spin (m_s = +1/2, -1/2), representing intrinsic angular momentum.\n\nGround-state electronic configurations adhere to three fundamental quantum principles: The Aufbau Principle directs electrons into lowest-energy subshells first; Wolfgang Pauli's Exclusion Principle forbids any two fermions in an atom from sharing identical sets of four quantum numbers; and Friedrich Hund's Rule of Maximum Multiplicity dictates that degenerate orbitals are occupied singly with parallel spins before pairing, minimizing inter-electronic Coulomb repulsion.",
            part2Title = "Quantum Calculations, Worked Rydberg Spectral Line & Applications",
            part2Content = "Exact Equations & Spectroscopic Series:\n• Rydberg equation: 1 / lambda = R_H * Z^2 * (1 / n_1^2 - 1 / n_2^2)\n• Balmer series (Visible): n_1 = 2, n_2 = 3, 4, 5...\n• Lyman series (UV): n_1 = 1, n_2 = 2, 3, 4...\n• Paschen series (IR): n_1 = 3, n_2 = 4, 5, 6...\n• Total nodes = n - 1; Radial nodes = n - l - 1; Angular nodes = l\n\nWorked Rydberg Spectral Transition Example:\nProblem: Calculate the wavelength lambda and photon energy Delta_E of the first spectral line of the Balmer series (transition from n = 3 to n = 1 in hydrogen, Z = 1). (R_H = 1.097 * 10^7 m^(-1)).\nStep 1: Identify quantum numbers for H_alpha line: n_1 = 2, n_2 = 3.\nStep 2: Apply Rydberg equation:\n1 / lambda = R_H * (1 / 2^2 - 1 / 3^2) = 1.097 * 10^7 * (1/4 - 1/9) = 1.097 * 10^7 * (5 / 36).\n1 / lambda = 1.097 * 10^7 * 0.13889 = 1.5236 * 10^6 m^(-1).\nStep 3: Solve for wavelength lambda:\nlambda = 1 / (1.5236 * 10^6) = 6.563 * 10^(-7) meters = 656.3 nm (Crimson red H_alpha line).\nStep 4: Compute photon transition energy Delta_E:\nDelta_E = h * c / lambda = (6.626 * 10^(-34) * 3.0 * 10^8) / (6.563 * 10^(-7)) = 3.029 * 10^(-19) J = 1.89 eV.\nAlternatively: Delta_E = -13.6 * (1/3^2 - 1/2^2) = -13.6 * (-5/36) = +1.889 eV.\n\nReal-World Applications:\nIn astrophysics, spectral line absorption patterns (Fraunhofer lines) reveal the elemental composition and surface temperatures of distant stars. In materials science, X-ray photoelectron spectroscopy (XPS) leverages core electron binding energies to characterize surface oxidation states.",
            examTip = "University Exam Tip: For a 3p orbital: n=3, l=1. Total nodes = n - 1 = 2; Radial nodes = n - l - 1 = 3 - 1 - 1 = 1; Angular nodes = l = 1. In viva exams, explain why chromium ([Ar] 3d^5 4s^1) and copper ([Ar] 3d^10 4s^1) violate standard Aufbau filling (half-filled and fully-filled d-subshells possess exceptional exchange energy stability).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6001,
                    questionNumber = 1,
                    question = "How many radial nodes and angular nodes does a 4d atomic orbital possess?",
                    options = mapOf("A" to "1 radial node, 2 angular nodes", "B" to "2 radial nodes, 1 angular node", "C" to "0 radial nodes, 3 angular nodes", "D" to "3 radial nodes, 0 angular nodes"),
                    correctOption = "A",
                    explanation = "For 4d: n = 4, l = 2. Angular nodes = l = 2. Radial nodes = n - l - 1 = 4 - 2 - 1 = 1. Total nodes = n - 1 = 3."
                ),
                QuizQuestionDto(
                    id = 6002,
                    questionNumber = 2,
                    question = "What is the ground-state electronic configuration of Chromium (Cr, Z = 24)?",
                    options = mapOf("A" to "[Ar] 3d^5 4s^1", "B" to "[Ar] 3d^4 4s^2", "C" to "[Ar] 3d^6 4s^0", "D" to "[Ar] 3d^3 4s^2 4p^1"),
                    correctOption = "A",
                    explanation = "An electron promotes from 4s to 3d to achieve a half-filled 3d^5 subshell, which confers exceptional thermodynamic stability due to enhanced quantum mechanical exchange energy."
                ),
                QuizQuestionDto(
                    id = 6003,
                    questionNumber = 3,
                    question = "Which spectral series of the hydrogen atom emission spectrum falls in the visible region of the electromagnetic spectrum?",
                    options = mapOf("A" to "Balmer Series (transitions ending at n = 2)", "B" to "Lyman Series (transitions ending at n = 1)", "C" to "Paschen Series (transitions ending at n = 3)", "D" to "Brackett Series (transitions ending at n = 4)"),
                    correctOption = "A",
                    explanation = "The Balmer series corresponds to electronic de-excitations down to the n = 2 level, generating photons with wavelengths between 400 nm and 700 nm in the human visible range."
                ),
                QuizQuestionDto(
                    id = 6004,
                    questionNumber = 4,
                    question = "According to Pauli's Exclusion Principle, what is the maximum number of electrons that can occupy a single atomic orbital?",
                    options = mapOf("A" to "2 electrons (with opposing spins +1/2 and -1/2)", "B" to "6 electrons", "C" to "10 electrons", "D" to "1 electron"),
                    correctOption = "A",
                    explanation = "Because no two electrons in an atom can have the same four quantum numbers, a single spatial orbital defined by (n, l, m_l) can hold at most two electrons differing in spin m_s (+1/2 and -1/2)."
                ),
                QuizQuestionDto(
                    id = 6005,
                    questionNumber = 5,
                    question = "If the uncertainty in position Delta_x of an electron is reduced to zero, what does Heisenberg's uncertainty principle dictate regarding its momentum uncertainty Delta_p?",
                    options = mapOf("A" to "Delta_p approaches infinity", "B" to "Delta_p becomes zero", "C" to "Delta_p equals hbar", "D" to "Delta_p is bounded by the speed of light"),
                    correctOption = "A",
                    explanation = "Delta_x * Delta_p >= hbar / 2. If Delta_x -> 0, then Delta_p >= hbar / (2 * Delta_x) -> infinity, meaning momentum becomes entirely indeterminate."
                ),
                QuizQuestionDto(
                    id = 6006,
                    questionNumber = 6,
                    question = "What is the orbital angular momentum L of an electron in an s orbital (l = 0)?",
                    options = mapOf("A" to "0", "B" to "hbar", "C" to "sqrt(2) * hbar", "D" to "0.5 * hbar"),
                    correctOption = "A",
                    explanation = "L = sqrt(l * (l + 1)) * hbar. For an s orbital, azimuthal quantum number l = 0, so L = sqrt(0 * 1) * hbar = 0."
                ),
                QuizQuestionDto(
                    id = 6007,
                    questionNumber = 7,
                    question = "Which quantum number specifies the spatial orientation of an orbital in three-dimensional space?",
                    options = mapOf("A" to "Magnetic Quantum Number (m_l)", "B" to "Principal Quantum Number (n)", "C" to "Azimuthal Quantum Number (l)", "D" to "Spin Quantum Number (m_s)"),
                    correctOption = "A",
                    explanation = "The magnetic quantum number m_l (ranging from -l to +l) dictates the orientation of the orbital in space relative to an external magnetic field."
                ),
                QuizQuestionDto(
                    id = 6008,
                    questionNumber = 8,
                    question = "What is the ionization energy of a ground-state hydrogen atom (n = 1)?",
                    options = mapOf("A" to "+13.6 eV", "B" to "-13.6 eV", "C" to "+3.4 eV", "D" to "+1.51 eV"),
                    correctOption = "A",
                    explanation = "Ionization energy is the energy required to remove an electron from the ground state (n = 1) to infinity (n = infinity): E_ion = E_inf - E_1 = 0 - (-13.6 eV) = +13.6 eV."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH1_001",
                    subject = "Chemistry",
                    chapter = "Atomic Structure & Quantum Numbers",
                    topic = "Quantum Numbers, Orbitals and Hydrogen Emission Spectra",
                    keywords = listOf("chemistry", "atomic structure", "quantum numbers", "Bohr model", "Rydberg", "Heisenberg", "Aufbau", "Pauli", "Hund rule", "nodes"),
                    definition = "Atomic structure describes electronic configurations via four quantum numbers (n, l, m_l, m_s) satisfying the Aufbau principle, Pauli exclusion, and Hund's rule, governing spatial orbital nodes and line spectra.",
                    formulaOrCoreRule = "1/lambda = R_H*(1/n1^2 - 1/n2^2). E_n = -13.6*Z^2/n^2. Delta_x*Delta_p >= hbar/2. Total nodes = n - 1. L = sqrt(l(l+1))*hbar.",
                    workedExample = "Balmer H_alpha line (n=3 to n=2): 1/lambda = 1.097e7*(1/4 - 1/9) = 1.524e6 m^-1 => lambda = 656.3 nm (visible red).",
                    content = "Radial and angular wavefunctions delineate electron probability density. Half-filled and fully-filled d-subshells exhibit heightened stability due to quantum mechanical exchange energy."
                )
            )
        )
    }

    private fun createChapter2(): CatalogChapter {
        return CatalogChapter(
            chapterId = 2,
            chapterOrder = 2,
            title = "Chemical Bonding & Molecular Orbital Theory",
            estimatedMinutes = 30,
            summary = "Lewis structures, VSEPR geometry, hybridization (sp, sp2, sp3, sp3d2), Valence Bond Theory, and Molecular Orbital Theory (LCAO, bond order, diamagnetism/paramagnetism).",
            syllabus = listOf("Lewis Electron-Dot Structures, Formal Charge & Resonance Hybrids", "VSEPR Theory: Molecular Geometries & Steric Numbers", "Valence Bond Theory: Orbital Overlap & Hybridization (sp, sp2, sp3, sp3d, sp3d2)", "Molecular Orbital Theory: LCAO Method, Bonding vs Antibonding Orbitals", "Diatomic Homonuclear/Heteronuclear MO Diagrams & Bond Order Calculations"),
            coreFormulas = listOf("Formal Charge: FC = Valence_Electrons - Non_Bonding_Electrons - 0.5 * Bonding_Electrons", "Bond Order (MOT): Bond_Order = 0.5 * (N_bonding - N_antibonding)", "Steric Number: SN = (Number of bonded atoms) + (Number of lone pairs on central atom)", "Dipole Moment: mu = q * d (in Debye, 1 D = 3.336 * 10^(-30) C.m)", "Bond Energy / Dissociation Enthalpy: Delta H_rxn = Sum(BE_broken) - Sum(BE_formed)"),
            part1Title = "Chemical Bonding, VSEPR Geometry & Molecular Orbitals",
            part1Content = "Chemical bonding represents the thermodynamic stabilization achieved when isolated atoms assemble into molecules by sharing or transferring valence electrons. Gilbert N. Lewis formalized electron pairing via the octet rule. Formal charge FC = V - N - B/2 identifies optimal resonance contributors, favoring configurations that minimize formal charges and place negative charges on the most electronegative atoms.\n\nValence Shell Electron Pair Repulsion (VSEPR) theory predicts 3D molecular geometry by minimizing electrostatic repulsion among valence electron pairs surrounding a central atom. The Steric Number (SN = bonded atoms + lone pairs) establishes electron domain geometries: SN = 2 (linear, 180°), SN = 3 (trigonal planar, 120°), SN = 4 (tetrahedral, 109.5°), SN = 5 (trigonal bipyramidal, 90°/120°), and SN = 6 (octahedral, 90°). Lone pairs exert greater repulsion than bonding pairs, systematically compressing adjacent bond angles (e.g., H2O angle is 104.5° due to two lone pairs).\n\nValence Bond Theory (VBT) models covalent bonds through the constructive spatial overlap of atomic orbitals: end-to-end overlap produces cylindrically symmetric sigma bonds, while lateral sideways overlap produces pi bonds. To explain observed geometries, Linus Pauling introduced Orbital Hybridization, mathematically mixing atomic orbitals into degenerate hybrid sets: sp (linear), sp2 (trigonal planar), sp3 (tetrahedral), and sp3d2 (octahedral).\n\nMolecular Orbital Theory (MOT) surpasses VBT by treating electrons as delocalized over the entire molecule. Linear Combination of Atomic Orbitals (LCAO) combines atomic wavefunctions constructively to yield low-energy bonding molecular orbitals (sigma, pi) and destructively to yield high-energy antibonding molecular orbitals (sigma*, pi*). Bond Order = 0.5*(N_b - N_a) dictates bond stability and length. MOT famously explains why dioxygen (O2) is paramagnetic with two unpaired electrons in degenerate pi* antibonding orbitals, a fact completely inexplicable by Lewis or VBT models.",
            part2Title = "MOT Diagrams, Worked Bond Order & Magnetism Applications",
            part2Content = "Exact Equations & Orbital Filling Rules:\n• Bond order: BO = 0.5 * (N_bonding - N_antibonding)\n• Formal charge: FC = V - L - 0.5 * B\n• Diatomic O2 MO configuration: sigma_1s^2 sigma*_1s^2 sigma_2s^2 sigma*_2s^2 sigma_2p_z^2 (pi_2p_x^2 = pi_2p_y^2) (pi*_2p_x^1 = pi*_2p_y^1)\n• Diatomic N2 MO configuration (s-p mixing alters ordering): sigma_1s^2 sigma*_1s^2 sigma_2s^2 sigma*_2s^2 (pi_2p_x^2 = pi_2p_y^2) sigma_2p_z^2\n\nWorked Molecular Orbital Example:\nProblem: Using Molecular Orbital Theory, determine the bond order, magnetic character, and relative bond strength for O2, O2^+, and O2^2- (peroxide ion).\nStep 1: O2 has 16 electrons (12 valence electrons in n=2 shell):\nCore electrons cancel: sigma_1s^2 and sigma*_1s^2 (BO contribution = 0).\nValence MO filling: sigma_2s^2, sigma*_2s^2, sigma_2p_z^2, (pi_2p_x^2, pi_2p_y^2), (pi*_2p_x^1, pi*_2p_y^1).\nBonding valence electrons N_b = 2 (sigma_2s) + 2 (sigma_2p) + 4 (pi_2p) = 8.\nAntibonding valence electrons N_a = 2 (sigma*_2s) + 2 (pi*_2p) = 4.\nBond Order O2 = 0.5 * (8 - 4) = 2.0 (Double bond).\nMagnetic property: 2 unpaired electrons in pi* => Paramagnetic.\nStep 2: O2^+ (15 electrons, removes 1 antibonding electron from pi*):\nN_b = 8, N_a = 3 => Bond Order = 0.5 * (8 - 3) = 2.5 (Stronger and shorter bond than O2).\nStep 3: O2^2- (18 electrons, adds 2 antibonding electrons to pi*):\nN_b = 8, N_a = 6 => Bond Order = 0.5 * (8 - 6) = 1.0 (Single bond, longest and weakest).\nMagnetic property: All electrons paired => Diamagnetic.\nBond Strength Ranking: O2^+ (BO=2.5) > O2 (BO=2.0) > O2^2- (BO=1.0).\n\nReal-World Applications:\nIn biochemistry, the reversible binding of dioxygen (O2) to iron in hemoglobin relies on molecular orbital overlap between Fe(II) d-orbitals and oxygen pi* orbitals. In organic electronics, conjugated polymers (e.g., polyacetylene) conduct electricity through extended delocalized pi-molecular orbitals.",
            examTip = "University Exam Tip: For B2, C2, N2 (Z <= 7), s-p mixing pushes sigma_2p_z higher in energy than degenerate pi_2p_x and pi_2p_y orbitals. For O2 and F2 (Z >= 8), sigma_2p_z lies below pi_2p. In viva exams, explain why O2 is paramagnetic while N2 is diamagnetic.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6009,
                    questionNumber = 1,
                    question = "According to Molecular Orbital Theory, why is the oxygen molecule (O2) paramagnetic?",
                    options = mapOf("A" to "It contains two unpaired electrons in degenerate pi* antibonding orbitals", "B" to "It has an odd total number of valence electrons", "C" to "Its bond order equals zero", "D" to "All electrons are paired in sigma bonding orbitals"),
                    correctOption = "A",
                    explanation = "Filling 12 valence electrons in O2 leaves 2 electrons in the degenerate pi*_2p_x and pi*_2p_y antibonding orbitals. By Hund's rule, they occupy separate orbitals with parallel spins, rendering O2 paramagnetic."
                ),
                QuizQuestionDto(
                    id = 6010,
                    questionNumber = 2,
                    question = "What is the molecular geometry of sulfur hexafluoride (SF6) according to VSEPR theory?",
                    options = mapOf("A" to "Octahedral (Steric Number = 6, 0 lone pairs)", "B" to "Trigonal bipyramidal", "C" to "Tetrahedral", "D" to "Square planar"),
                    correctOption = "A",
                    explanation = "Sulfur in SF6 forms 6 single bonds with fluorine and has zero lone pairs (Steric Number = 6 + 0 = 6). This yields an octahedral geometry with 90-degree bond angles."
                ),
                QuizQuestionDto(
                    id = 6011,
                    questionNumber = 3,
                    question = "What is the formal charge of the central nitrogen atom in the ammonium cation (NH4^+)?",
                    options = mapOf("A" to "+1", "B" to "0", "C" to "-1", "D" to "+2"),
                    correctOption = "A",
                    explanation = "Valence electrons of N = 5. Non-bonding electrons = 0. Bonding electrons = 8. Formal Charge = 5 - 0 - 0.5*(8) = 5 - 4 = +1."
                ),
                QuizQuestionDto(
                    id = 6012,
                    questionNumber = 4,
                    question = "What is the hybridization of the carbon atoms in ethene (H2C=CH2)?",
                    options = mapOf("A" to "sp2", "B" to "sp3", "C" to "sp", "D" to "sp3d"),
                    correctOption = "A",
                    explanation = "Each carbon atom in ethene is bonded to three atoms (two hydrogens and one carbon) with zero lone pairs (Steric Number = 3), requiring sp2 hybridization with 120-degree bond angles and one unhybridized p-orbital forming the pi-bond."
                ),
                QuizQuestionDto(
                    id = 6013,
                    questionNumber = 5,
                    question = "What is the bond order of the dinitrogen molecule (N2) calculated via Molecular Orbital Theory?",
                    options = mapOf("A" to "3.0 (Triple Bond)", "B" to "2.0", "C" to "2.5", "D" to "1.0"),
                    correctOption = "A",
                    explanation = "N2 has 10 valence electrons: sigma_2s^2, sigma*_2s^2, pi_2p_x^2, pi_2p_y^2, sigma_2p_z^2. N_b = 8, N_a = 2. Bond Order = 0.5 * (8 - 2) = 3.0."
                ),
                QuizQuestionDto(
                    id = 6014,
                    questionNumber = 6,
                    question = "Why is the bond angle in a water molecule (H2O, 104.5°) smaller than the ideal tetrahedral angle (109.5°)?",
                    options = mapOf("A" to "Lone pair - lone pair electrostatic repulsions exceed bonding pair repulsions, compressing the H-O-H bond angle", "B" to "Oxygen is less electronegative than hydrogen", "C" to "The O-H bonds are ionic rather than covalent", "D" to "Hydrogen atoms have large nuclear radii"),
                    correctOption = "A",
                    explanation = "Oxygen in H2O has steric number 4 with two bonding pairs and two lone pairs. Because lone-pair electron clouds are held closer to the oxygen nucleus, their mutual repulsion compresses the bonding pairs from 109.5° down to 104.5°."
                ),
                QuizQuestionDto(
                    id = 6015,
                    questionNumber = 7,
                    question = "Which of the following species has the highest bond dissociation energy?",
                    options = mapOf("A" to "N2 (Bond Order = 3)", "B" to "O2 (Bond Order = 2)", "C" to "F2 (Bond Order = 1)", "D" to "O2^2- (Bond Order = 1)"),
                    correctOption = "A",
                    explanation = "Bond dissociation energy scales directly with bond order. Dinitrogen (N2) has a bond order of 3 with a massive bond dissociation energy of 945 kJ/mol, rendering it exceptionally inert."
                ),
                QuizQuestionDto(
                    id = 6016,
                    questionNumber = 8,
                    question = "What type of orbital overlap constitutes a carbon-carbon pi (pi) bond?",
                    options = mapOf("A" to "Lateral (side-by-side) overlap of unhybridized parallel p-orbitals", "B" to "End-to-end direct coaxial overlap of sp3 hybrid orbitals", "C" to "Spherical overlap of two s-orbitals", "D" to "Overlap between a d-orbital and an s-orbital"),
                    correctOption = "A",
                    explanation = "A pi bond is formed by the lateral (sideways) overlap of parallel atomic p-orbitals, producing electron density above and below the internuclear axis."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH2_001",
                    subject = "Chemistry",
                    chapter = "Chemical Bonding & Molecular Orbital Theory",
                    topic = "VSEPR Geometries, Hybridization and Molecular Orbital Theory",
                    keywords = listOf("chemical bonding", "VSEPR", "hybridization", "MOT", "bond order", "paramagnetism", "formal charge", "LCAO", "pi bond"),
                    definition = "Chemical bonding stabilizes atoms via valence electron redistribution. VSEPR dictates 3D geometry, valence bond theory details orbital hybridization, and MOT explains bond orders and paramagnetism.",
                    formulaOrCoreRule = "FC = V - L - 0.5*B. BO = 0.5*(N_b - N_a). Steric Number = Bonds + Lone Pairs. Dipole: mu = q*d.",
                    workedExample = "For O2: N_b=8, N_a=4 => BO = 0.5*(8 - 4) = 2.0. Two unpaired electrons in pi* orbitals confer paramagnetism.",
                    content = "MOT resolves paramagnetic anomalies of O2. Molecular geometries range from linear (sp) to octahedral (sp3d2), with lone pairs exerting greater repulsion than bonding pairs."
                )
            )
        )
    }

    private fun createChapter3(): CatalogChapter {
        return CatalogChapter(
            chapterId = 3,
            chapterOrder = 3,
            title = "Periodic Properties & Coordination Compounds",
            estimatedMinutes = 30,
            summary = "Periodic trends (effective nuclear charge, ionization energy, electron affinity, electronegativity), Crystal Field Theory (CFT, Delta_oct, Delta_tet), spectrochemical series, and coordination isomerism.",
            syllabus = listOf("Periodic Trends: Effective Nuclear Charge (Z_eff & Slater's Rules) & Atomic Radii", "Ionization Energy, Electron Affinity & Pauling Electronegativity Scales", "Coordination Chemistry: Werner's Theory, Ligands & IUPAC Nomenclature", "Crystal Field Theory (CFT): Octahedral & Tetrahedral d-Orbital Splitting", "Spectrochemical Series, High-Spin vs Low-Spin Complexes & Magnetic Moments"),
            coreFormulas = listOf("Slater's Effective Nuclear Charge: Z_eff = Z - S (where S is screening constant)", "Octahedral Crystal Field Splitting: Delta_oct = E(e_g) - E(t_2g)", "Crystal Field Stabilization Energy (CFSE_oct): CFSE = (-0.4 * n_t2g + 0.6 * n_eg) * Delta_oct + m * P", "Tetrahedral Splitting Relation: Delta_tet = (4/9) * Delta_oct", "Spin-Only Magnetic Moment: mu_eff = sqrt(n * (n + 2)) * mu_B (where mu_B is Bohr Magneton)"),
            part1Title = "Periodic Laws, Effective Nuclear Charge & Crystal Field Theory",
            part1Content = "The Periodic Law arranges chemical elements by increasing atomic number Z, revealing recurring patterns in physical and chemical behavior. Core electrons shield valence electrons from the full nuclear charge. Under Slater's Rules, the Effective Nuclear Charge experienced by valence electrons is Z_eff = Z - S. Across a period from left to right, Z_eff increases monotonically because nuclear charge increases while valence shielding remains incomplete, contracting atomic radii and driving sharp increases in First Ionization Energy and Pauling Electronegativity.\n\nCoordination compounds consist of a central transition metal cation bonded to surrounding electron-pair donating ligands via coordinate covalent bonds. Alfred Werner pioneered coordination theory, distinguishing primary valence (oxidation state, satisfied by counter-ions) from secondary valence (coordination number, satisfied by directional ligands). Ligands are categorized by denticity as monodentate (H2O, NH3, Cl-) or polydentate chelating agents (ethylenediamine, EDTA^4-), where chelation enhances thermodynamic stability (the chelate effect).\n\nCrystal Field Theory (CFT) models metal-ligand interactions as pure electrostatic point-charge repulsions. In an isolated gaseous transition metal ion, the five d-orbitals (d_xy, d_yz, d_xz, d_x2-y2, d_z2) are degenerate. In an octahedral complex, ligand electron clouds approach along the Cartesian coordinate axes. The axial orbitals (d_x2-y2, d_z2) experience intense electrostatic repulsion and split upward into a doubly degenerate e_g level (+0.6*Delta_oct), while inter-axial orbitals (d_xy, d_yz, d_xz) drop into a triply degenerate t_2g level (-0.4*Delta_oct).\n\nThe magnitude of crystal field splitting Delta_oct is governed by the Spectrochemical Series: I- < Br- < Cl- < F- < H2O < NH3 < en < CN- < CO. Strong-field ligands (e.g., CN-, CO) induce large Delta_oct exceeding electron pairing energy P, driving electrons into the lower t_2g level to form Low-Spin complexes. Weak-field ligands (e.g., halides) yield small Delta_oct < P, producing High-Spin complexes governed by Hund's rule.",
            part2Title = "CFT Formulations, Worked CFSE & Magnetic Moment Problem",
            part2Content = "Exact Equations & Spectrochemical Ordering:\n• Spectrochemical series: I- < Br- < S2- < SCN- < Cl- < NO3- < F- < OH- < C2O4^2- < H2O < NCS- < edta^4- < NH3 < en < NO2- < CN- < CO\n• Spin-only magnetic moment: mu_eff = sqrt(n * (n + 2)) mu_B (mu_B = 9.274 * 10^(-24) J/T)\n• CFSE (Octahedral): CFSE = [-0.4 * n(t_2g) + 0.6 * n(e_g)] * Delta_oct + pairing energy\n\nWorked Crystal Field Example:\nProblem: For the octahedral complex ion [Fe(CN)6]^3- and [Fe(H2O)6]^3+: (a) Determine oxidation state and d-electron count of Iron, (b) State whether each is high-spin or low-spin, (c) Calculate CFSE, and (d) Determine spin-only magnetic moment mu_eff for both.\nStep 1: Iron oxidation state in both: Fe^3+ (Z = 26).\nNeutral Fe: [Ar] 3d^6 4s^2 => Fe^3+: [Ar] 3d^5 (five d-electrons).\nStep 2: Complex 1: [Fe(CN)6]^3-:\nCN- is a strong-field ligand (Delta_oct > Pairing energy P) => Low-spin complex.\nAll 5 electrons pair into lower t_2g orbitals: configuration = (t_2g)^5 (e_g)^0.\nNumber of unpaired electrons n = 1.\nCFSE = [-0.4 * 5 + 0.6 * 0] * Delta_oct + 2*P = -2.0 * Delta_oct + 2*P.\nSpin-only magnetic moment: mu_eff = sqrt(1 * (1 + 2)) = sqrt(3) = 1.73 mu_B.\nStep 3: Complex 2: [Fe(H2O)6]^3-:\nH2O is a weak-field ligand (Delta_oct < P) => High-spin complex.\nHund's rule occupies all 5 orbitals singly: configuration = (t_2g)^3 (e_g)^2.\nNumber of unpaired electrons n = 5.\nCFSE = [-0.4 * 3 + 0.6 * 2] * Delta_oct = [-1.2 + 1.2] * Delta_oct = 0.0 Delta_oct.\nSpin-only magnetic moment: mu_eff = sqrt(5 * (5 + 2)) = sqrt(35) = 5.92 mu_B.\n\nReal-World Applications:\nIn oncology, Cisplatin [cis-Pt(NH3)2Cl2] cross-links DNA guanine bases in cancer cells to prevent mitosis. In biology, chlorophyll (Mg-porphyrin) and heme (Fe-porphyrin) exploit coordination geometry to drive photosynthesis and cellular respiration.",
            examTip = "University Exam Tip: For tetrahedral complexes, ligand splitting is inverted (e level is lower, t_2 level is higher) and Delta_tet = (4/9)*Delta_oct; because Delta_tet is inherently small, virtually all tetrahedral complexes are high-spin. In viva exams, explain why [Fe(CN)6]^3- is weakly paramagnetic (1.73 mu_B) while [Fe(H2O)6]^3- is strongly paramagnetic (5.92 mu_B).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6017,
                    questionNumber = 1,
                    question = "What is the spin-only magnetic moment mu_eff of a high-spin octahedral d^5 transition metal complex with 5 unpaired electrons?",
                    options = mapOf("A" to "5.92 Bohr Magnetons (mu_B)", "B" to "1.73 Bohr Magnetons (mu_B)", "C" to "3.87 Bohr Magnetons (mu_B)", "D" to "0.0 Bohr Magnetons (mu_B)"),
                    correctOption = "A",
                    explanation = "mu_eff = sqrt(n * (n + 2)) mu_B. For n = 5: mu_eff = sqrt(5 * 7) = sqrt(35) approximately 5.916 mu_B."
                ),
                QuizQuestionDto(
                    id = 6018,
                    questionNumber = 2,
                    question = "In an octahedral coordination complex, which d-orbitals point directly toward the incoming ligands and are destabilized to the higher energy e_g set?",
                    options = mapOf("A" to "d_x2-y2 and d_z2", "B" to "d_xy, d_yz, and d_xz", "C" to "d_xy and d_z2", "D" to "d_x2-y2 and d_xy"),
                    correctOption = "A",
                    explanation = "The axial orbitals d_x2-y2 and d_z2 have lobes oriented directly along the Cartesian x, y, and z axes where ligands approach, experiencing maximum electrostatic repulsion."
                ),
                QuizQuestionDto(
                    id = 6019,
                    questionNumber = 3,
                    question = "What is the mathematical relationship between tetrahedral crystal field splitting Delta_tet and octahedral splitting Delta_oct for identical metal-ligand pairs?",
                    options = mapOf("A" to "Delta_tet = (4/9) * Delta_oct", "B" to "Delta_tet = (9/4) * Delta_oct", "C" to "Delta_tet = Delta_oct", "D" to "Delta_tet = 0.5 * Delta_oct"),
                    correctOption = "A",
                    explanation = "Because a tetrahedron has only 4 ligands instead of 6 (giving a 4/6 geometric factor) and ligands do not point directly at any d-orbitals (yielding another 2/3 directional factor), Delta_tet = (4/6)*(2/3)*Delta_oct = (4/9)*Delta_oct."
                ),
                QuizQuestionDto(
                    id = 6020,
                    questionNumber = 4,
                    question = "Which of the following ligands ranks highest as a strong-field ligand in the Spectrochemical Series, inducing maximum crystal field splitting?",
                    options = mapOf("A" to "Carbon monoxide (CO)", "B" to "Water (H2O)", "C" to "Chloride ion (Cl-)", "D" to "Ammonia (NH3)"),
                    correctOption = "A",
                    explanation = "Carbon monoxide (CO) is a powerful pi-acid ligand capable of back-bonding, placing it at the extreme high-field end of the spectrochemical series: Cl- < H2O < NH3 < CN- < CO."
                ),
                QuizQuestionDto(
                    id = 6021,
                    questionNumber = 5,
                    question = "Why does First Ionization Energy generally increase across a period from left to right in the periodic table?",
                    options = mapOf("A" to "Effective nuclear charge (Z_eff) increases, pulling valence electrons closer to the nucleus", "B" to "Atomic radius expands across the period", "C" to "Valence electrons fill into higher principal quantum shells", "D" to "Electron-electron repulsion vanishes completely"),
                    correctOption = "A",
                    explanation = "Across a period, nuclear charge Z increases while electrons enter the same principal shell, providing poor shielding. Consequently, Z_eff increases, holding valence electrons tighter."
                ),
                QuizQuestionDto(
                    id = 6022,
                    questionNumber = 6,
                    question = "What type of isomerism is demonstrated by the pair [Co(NH3)5SO4]Br and [Co(NH3)5Br]SO4?",
                    options = mapOf("A" to "Ionization Isomerism", "B" to "Linkage Isomerism", "C" to "Coordination Isomerism", "D" to "Geometrical (cis-trans) Isomerism"),
                    correctOption = "A",
                    explanation = "Ionization isomerism occurs when a coordinated ligand and an external counter-ion exchange positions, generating different ions upon dissolution in aqueous solution."
                ),
                QuizQuestionDto(
                    id = 6023,
                    questionNumber = 7,
                    question = "What is the Crystal Field Stabilization Energy (CFSE) of a high-spin d^5 octahedral complex?",
                    options = mapOf("A" to "0.0 Delta_oct", "B" to "-2.0 Delta_oct", "C" to "-0.4 Delta_oct", "D" to "+1.2 Delta_oct"),
                    correctOption = "A",
                    explanation = "Electronic configuration is (t_2g)^3 (e_g)^2. CFSE = [-0.4 * 3 + 0.6 * 2] * Delta_oct = [-1.2 + 1.2] * Delta_oct = 0.0."
                ),
                QuizQuestionDto(
                    id = 6024,
                    questionNumber = 8,
                    question = "What is the primary thermodynamic factor that makes polydentate chelating ligands (such as EDTA^4-) bind significantly tighter to metals than monodentate ligands?",
                    options = mapOf("A" to "The Chelate Effect (large favorable increase in entropy Delta S upon displacing multiple solvent molecules)", "B" to "Zero enthalpy of formation", "C" to "Reduction of metal oxidation state to zero", "D" to "Nuclear magnetic resonance resonance"),
                    correctOption = "A",
                    explanation = "When a hexadentate ligand like EDTA binds to a metal ion, it displaces six coordinated water molecules. The net release of molecules increases system disorder (Delta S > 0), driving Delta G heavily negative."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH3_001",
                    subject = "Chemistry",
                    chapter = "Periodic Properties & Coordination Compounds",
                    topic = "Effective Nuclear Charge, Coordination Compounds and Crystal Field Theory",
                    keywords = listOf("periodic properties", "Z_eff", "ionization energy", "coordination compounds", "CFT", "crystal field splitting", "spectrochemical series", "CFSE", "magnetic moment"),
                    definition = "Periodic trends reflect effective nuclear charge (Z_eff) evolution. Coordination compounds exhibit d-orbital splitting (t_2g and e_g) under Crystal Field Theory, dictating high/low spin states and magnetic moments.",
                    formulaOrCoreRule = "Z_eff = Z - S. Delta_tet = (4/9)*Delta_oct. mu_eff = sqrt(n(n+2)) mu_B. CFSE_oct = (-0.4*n_t2g + 0.6*n_eg)*Delta_oct.",
                    workedExample = "For [Fe(CN)6]^3- (low-spin d^5): n=1 unpaired electron, CFSE = -2.0*Delta_oct + 2P, mu_eff = sqrt(3) = 1.73 mu_B. For [Fe(H2O)6]^3- (high-spin d^5): n=5, CFSE = 0, mu_eff = sqrt(35) = 5.92 mu_B.",
                    content = "Spectrochemical series orders ligand field strength. Polydentate chelators achieve extreme thermodynamic stability via entropic solvent displacement (the chelate effect)."
                )
            )
        )
    }

    private fun createChapter4(): CatalogChapter {
        return CatalogChapter(
            chapterId = 4,
            chapterOrder = 4,
            title = "Chemical Thermodynamics & Equilibrium",
            estimatedMinutes = 30,
            summary = "Enthalpy, Hess's Law, Gibbs Free Energy (Delta G = Delta H - T*Delta S), chemical equilibrium constant K_eq, reaction quotient Q, and Le Chatelier's Principle.",
            syllabus = listOf("Enthalpy of Reaction, Standard Enthalpies of Formation & Hess's Law", "Second Law in Chemistry: Entropy of Reaction & Third Law Absolute Entropies", "Gibbs Free Energy (Delta G), Spontaneity Criteria & Helmholtz Energy", "Chemical Equilibrium Constant K_p/K_c, Reaction Quotient Q & van 't Hoff Equation", "Le Chatelier's Principle: Perturbations via Pressure, Temperature & Concentration"),
            coreFormulas = listOf("Hess's Law: Delta H_rxn^0 = Sum(n * Delta H_f^0(products)) - Sum(m * Delta H_f^0(reactants))", "Gibbs Free Energy Fundamental Equation: Delta G = Delta H - T * Delta S", "Thermodynamic Equilibrium Relation: Delta G^0 = - R * T * ln(K_eq)", "Reaction Quotient Relation: Delta G = Delta G^0 + R * T * ln(Q)", "Van 't Hoff Equation: ln(K2 / K1) = - (Delta H^0 / R) * (1/T2 - 1/T1)", "Gas Equilibrium Relation: K_p = K_c * (R * T)^(Delta n_gas)"),
            part1Title = "Thermodynamics, Spontaneity & Dynamic Chemical Equilibrium",
            part1Content = "Chemical thermodynamics quantifies the energetic feasibility, thermal exchanges, and equilibrium compositions of chemical transformations. Enthalpy H = U + PV measures heat exchange at constant pressure. Hess's Law of Constant Heat Summation establishes that because enthalpy is a state function, the net enthalpy change of a reaction is identical whether executed in a single step or through a series of intermediates: Delta H_rxn^0 = Sum(Delta H_f^0(prod)) - Sum(Delta H_f^0(react)).\n\nSpontaneity cannot be judged by enthalpy alone; nature favors states that maximize total universe entropy (Delta S_univ = Delta S_sys + Delta S_surr >= 0). Willard Gibbs unified system enthalpy and entropy into the Gibbs Free Energy state function G = H - TS. At constant temperature and pressure, the criterion for chemical spontaneity is Delta G < 0 (exergonic). When Delta G > 0, the reaction is non-spontaneous (endergonic); when Delta G = 0, the system resides in dynamic chemical equilibrium.\n\nDynamic chemical equilibrium occurs when the forward and reverse reaction rates balance exactly, causing macroscopic concentrations of reactants and products to remain constant. The equilibrium position is quantified by equilibrium constant K, linked to standard Gibbs free energy via Delta G^0 = -R*T*ln(K_eq). For gas-phase reactions, partial pressure constant K_p relates to molar concentration constant K_c via K_p = K_c*(RT)^(Delta n_g), where Delta n_g is the moles of gaseous products minus gaseous reactants.\n\nHenri Le Chatelier's Principle states that when a dynamic equilibrium is subjected to an external perturbation (change in concentration, pressure, or temperature), the system adjusts its position to counteract the applied stress. The temperature dependence of equilibrium constants is quantified by Jacobus van 't Hoff's equation d(ln K)/dT = Delta H^0 / (R*T^2): increasing temperature shifts endothermic reactions (Delta H > 0) toward products while shifting exothermic reactions (Delta H < 0) toward reactants.",
            part2Title = "Thermodynamic Formulations, Worked Haber-Bosch Equilibrium & Use",
            part2Content = "Exact Equations & Equilibrium Constants:\n• Delta G^0 = Delta H^0 - T * Delta S^0\n• Delta G^0 = - R * T * ln(K) (R = 8.314 J/(mol.K))\n• Van 't Hoff: ln(K2 / K1) = (Delta H^0 / R) * (1/T1 - 1/T2)\n• K_p = K_c * (R * T)^(Delta n_g)\n\nWorked Chemical Equilibrium Example:\nProblem: For the synthesis of ammonia (Haber-Bosch process: N2(g) + 3 H2(g) <=> 2 NH3(g)), Delta H^0 = -92.2 kJ/mol and Delta S^0 = -198.7 J/(mol.K) at 298 K. Calculate: (a) Delta G^0 at 298 K, (b) Equilibrium constant K_p at 298 K, and (c) Temperature at which the reaction switches from spontaneous to non-spontaneous.\nStep 1: Compute Delta G^0 at T = 298 K:\nDelta H^0 = -92,200 J/mol.\nT * Delta S^0 = 298 * (-198.7) = -59,212.6 J/mol.\nDelta G^0 = Delta H^0 - T*Delta S^0 = -92,200 - (-59,212.6) = -92,200 + 59,212.6 = -32,987.4 J/mol = -32.99 kJ/mol.\nBecause Delta G^0 < 0, the reaction is spontaneous under standard conditions at 298 K.\nStep 2: Calculate equilibrium constant K_p at 298 K:\nDelta G^0 = - R * T * ln(K_p) => ln(K_p) = - Delta G^0 / (R * T).\nln(K_p) = - (-32,987.4) / (8.314 * 298) = 32,987.4 / 2477.57 = 13.314.\nK_p = e^(13.314) = 6.05 * 10^5.\nStep 3: Equilibrium temperature threshold T_eq (where Delta G^0 = 0):\n0 = Delta H^0 - T_eq * Delta S^0 => T_eq = Delta H^0 / Delta S^0.\nT_eq = (-92,200 J/mol) / (-198.7 J/(mol.K)) = 464 K (191°C).\nAbove 464 K, T*Delta S dominates and Delta G^0 becomes positive, shifting equilibrium away from ammonia.\n\nReal-World Applications:\nIn industrial chemical engineering, Haber-Bosch ammonia synthesis operates at 450°C and 200 atm. High pressure shifts equilibrium toward fewer gas moles (2 vs 4), while iron-potassium oxide catalysts accelerate reaction kinetics despite unfavorable high-temperature thermodynamics.",
            examTip = "University Exam Tip: Differentiate Delta G from Delta G^0: Delta G = Delta G^0 + R*T*ln(Q). At equilibrium, Delta G = 0, yielding Delta G^0 = -R*T*ln(K). In viva exams, explain why catalysts do not alter equilibrium constants (they accelerate forward and reverse rates by identical factors without changing Delta G^0).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6025,
                    questionNumber = 1,
                    question = "For a reaction with Delta H^0 < 0 (exothermic) and Delta S^0 < 0, under what temperature conditions is the reaction spontaneous?",
                    options = mapOf("A" to "Spontaneous only at low temperatures (where |Delta H| > |T * Delta S|)", "B" to "Spontaneous at all temperatures", "C" to "Spontaneous only at high temperatures", "D" to "Non-spontaneous at all temperatures"),
                    correctOption = "A",
                    explanation = "Delta G = Delta H - T*Delta S. When Delta H is negative and Delta S is negative, the -T*Delta S term is positive. Delta G is negative only when temperature T is sufficiently low that enthalpy dominates entropy."
                ),
                QuizQuestionDto(
                    id = 6026,
                    questionNumber = 2,
                    question = "What is the relationship between standard Gibbs Free Energy change Delta G^0 and the equilibrium constant K_eq at temperature T?",
                    options = mapOf("A" to "Delta G^0 = - R * T * ln(K_eq)", "B" to "Delta G^0 = R * T * ln(K_eq)", "C" to "Delta G^0 = - n * F * K_eq", "D" to "Delta G^0 = Delta H^0 / K_eq"),
                    correctOption = "A",
                    explanation = "Derived from the reaction quotient expression Delta G = Delta G^0 + R*T*ln(Q) at equilibrium where Delta G = 0 and Q = K, Delta G^0 = -R*T*ln(K_eq)."
                ),
                QuizQuestionDto(
                    id = 6027,
                    questionNumber = 3,
                    question = "For the gas-phase reaction N2(g) + 3 H2(g) <=> 2 NH3(g), what happens to the equilibrium mixture if total pressure is doubled at constant temperature?",
                    options = mapOf("A" to "Equilibrium shifts to the right (toward products) to reduce total gas moles", "B" to "Equilibrium shifts to the left (toward reactants)", "C" to "Equilibrium constant K_p doubles", "D" to "The mixture composition remains completely unchanged"),
                    correctOption = "A",
                    explanation = "By Le Chatelier's Principle, increasing pressure causes the system to relieve stress by shifting toward the side with fewer gas molecules (2 moles of NH3 vs 4 moles of reactants)."
                ),
                QuizQuestionDto(
                    id = 6028,
                    questionNumber = 4,
                    question = "According to the van 't Hoff equation, how does increasing temperature affect the equilibrium constant K of an endothermic reaction (Delta H^0 > 0)?",
                    options = mapOf("A" to "K increases", "B" to "K decreases", "C" to "K remains strictly constant", "D" to "K drops to zero"),
                    correctOption = "A",
                    explanation = "d(ln K)/dT = Delta H^0 / (R*T^2). For endothermic reactions where Delta H^0 > 0, the derivative is positive, meaning K increases as temperature rises."
                ),
                QuizQuestionDto(
                    id = 6029,
                    questionNumber = 5,
                    question = "What is the value of Gibbs Free Energy change Delta G for a chemical system that has achieved dynamic equilibrium?",
                    options = mapOf("A" to "Delta G = 0", "B" to "Delta G = -1", "C" to "Delta G = Delta G^0", "D" to "Delta G approaches negative infinity"),
                    correctOption = "A",
                    explanation = "At dynamic equilibrium, chemical potentials of reactants and products are balanced, meaning no net driving force exists and Delta G = 0."
                ),
                QuizQuestionDto(
                    id = 6030,
                    questionNumber = 6,
                    question = "What is the relation between K_p and K_c for the reaction 2 SO2(g) + O2(g) <=> 2 SO3(g)?",
                    options = mapOf("A" to "K_p = K_c / (R * T)", "B" to "K_p = K_c * (R * T)", "C" to "K_p = K_c", "D" to "K_p = K_c * (R * T)^2"),
                    correctOption = "A",
                    explanation = "Delta n_g = moles of gaseous products - moles of gaseous reactants = 2 - (2 + 1) = 2 - 3 = -1. Therefore, K_p = K_c * (R*T)^(-1) = K_c / (R*T)."
                ),
                QuizQuestionDto(
                    id = 6031,
                    questionNumber = 7,
                    question = "What effect does adding a chemical catalyst have on the equilibrium constant K_eq of a reversible reaction?",
                    options = mapOf("A" to "No effect whatsoever (catalysts lower activation energy equally for both forward and reverse paths)", "B" to "K_eq increases proportionally to catalyst surface area", "C" to "K_eq decreases to zero", "D" to "K_eq shifts the reaction toward products"),
                    correctOption = "A",
                    explanation = "A catalyst accelerates both forward and backward rates by identical proportions without modifying the state functions (Delta G^0) of reactants or products, leaving K_eq unchanged."
                ),
                QuizQuestionDto(
                    id = 6032,
                    questionNumber = 8,
                    question = "If a reaction quotient Q is greater than the equilibrium constant K (Q > K), in which direction will the reaction proceed spontaneously?",
                    options = mapOf("A" to "In the reverse direction (toward reactants) until Q = K", "B" to "In the forward direction (toward products)", "C" to "The system is already at equilibrium", "D" to "The reaction halts permanently"),
                    correctOption = "A",
                    explanation = "When Q > K, products are in excess relative to equilibrium proportions. The system responds by shifting leftward to consume products and form reactants until Q reaches K."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH4_001",
                    subject = "Chemistry",
                    chapter = "Chemical Thermodynamics & Equilibrium",
                    topic = "Hess's Law, Gibbs Free Energy and Chemical Equilibrium",
                    keywords = listOf("thermodynamics", "enthalpy", "Hess law", "entropy", "Gibbs free energy", "equilibrium", "K_eq", "Le Chatelier", "van 't Hoff"),
                    definition = "Chemical thermodynamics evaluates reaction spontaneity via Gibbs free energy Delta G = Delta H - T*Delta S. Dynamic equilibrium balances forward/reverse rates, obeying Delta G^0 = -RT ln(K_eq) and Le Chatelier's principle.",
                    formulaOrCoreRule = "Delta G^0 = Delta H^0 - T*Delta S^0. Delta G^0 = -R*T*ln(K). K_p = K_c*(RT)^(Delta n). ln(K2/K1) = -(Delta H/R)*(1/T2 - 1/T1).",
                    workedExample = "For Haber-Bosch with Delta H^0 = -92.2 kJ, Delta S^0 = -198.7 J/K: Delta G^0(298K) = -32.99 kJ/mol, K_p = e^(32987/(8.314*298)) = 6.05e5. Equilibrium flips non-spontaneous above T = 464 K.",
                    content = "Le Chatelier's principle governs industrial yield optimization. Catalysts reduce activation energy without altering Delta G^0 or the thermodynamic equilibrium constant K_eq."
                )
            )
        )
    }

    private fun createChapter5(): CatalogChapter {
        return CatalogChapter(
            chapterId = 5,
            chapterOrder = 5,
            title = "Electrochemistry & Chemical Kinetics",
            estimatedMinutes = 35,
            summary = "Galvanic/electrolytic cells, Nernst equation, standard reduction potentials, Faraday's laws of electrolysis, reaction orders (0th, 1st, 2nd), and Arrhenius activation energy.",
            syllabus = listOf("Galvanic Cells, Standard Hydrogen Electrode (SHE) & Standard Potentials E^0", "Nernst Equation: Cell Potentials under Non-Standard Conditions & Concentration Cells", "Electrolytic Cells, Overpotential & Faraday's Laws of Electrolysis", "Reaction Rates, Differential/Integrated Rate Laws (0th, 1st, 2nd Order)", "Temperature Dependence: Arrhenius Equation & Transition State Theory"),
            coreFormulas = listOf("Cell EMF from Free Energy: Delta G^0 = - n * F * E_cell^0 (where F = 96,485 C/mol)", "Nernst Equation: E_cell = E_cell^0 - (R * T / (n * F)) * ln(Q) = E_cell^0 - (0.0592 / n) * log10(Q) at 298 K", "Faraday's First Law: Mass = (M * I * t) / (n * F)", "First-Order Integrated Rate Law: ln([A]_t / [A]_0) = - k * t, t_half = ln(2) / k = 0.693 / k", "Second-Order Integrated Rate Law: 1 / [A]_t = 1 / [A]_0 + k * t, t_half = 1 / (k * [A]_0)", "Arrhenius Equation: k = A * e^(- E_a / (R * T)), ln(k2 / k1) = - (E_a / R) * (1/T2 - 1/T1)"),
            part1Title = "Electrochemical Potentials, Nernst Physics & Reaction Kinetics",
            part1Content = "Electrochemistry bridges chemical transformations with electrical work. In Galvanic (voltaic) cells, spontaneous redox reactions drive electron flow from anode (oxidation site) to cathode (reduction site) through an external circuit. Standard reduction potentials E^0 are referenced against the Standard Hydrogen Electrode (SHE, 0.00 V). Cell electromotive force relates directly to Gibbs free energy via Delta G^0 = -n*F*E_cell^0, where F is Faraday's constant (96,485 C/mol e-). A positive standard cell potential E^0 > 0 confirms thermodynamic spontaneity.\n\nWalther Nernst derived the cell potential under non-standard concentration regimes: E_cell = E_cell^0 - (RT/nF)*ln(Q). At 298.15 K, this simplifies to E_cell = E_cell^0 - (0.0592/n)*log10(Q). In concentration cells, two identical half-cells with differing electrolyte concentrations produce an electrical current driven purely by entropy of dilution, operating until concentrations equilibrate (E_cell = 0).\n\nChemical kinetics investigates the rates and microscopic mechanisms of chemical reactions. The differential rate law Rate = k*[A]^m*[B]^n reflects reaction orders m and n determined empirically via the method of initial rates. Integrated rate laws yield distinctive linear diagnostic plots: zero-order ([A] vs t), first-order (ln[A] vs t with constant half-life t_half = 0.693/k independent of initial concentration), and second-order (1/[A] vs t).\n\nSvante Arrhenius formulated the temperature dependence of reaction rate constants: k = A*exp(-E_a / RT), where A is the pre-exponential frequency factor and E_a is the Activation Energy representing the minimum kinetic energy required to surmount the transition state barrier. Eyring-Polanyi Transition State Theory models the activated complex [X]^‡ in quasi-equilibrium with reactants, proving that chemical catalysts accelerate reaction rates exponentially by providing alternative reaction pathways with reduced activation energy.",
            part2Title = "Kinetics & Nernst Calculations, Worked Battery Cell & Applications",
            part2Content = "Exact Equations & Half-Life Formulations:\n• Nernst: E = E^0 - (0.0592 / n) * log10(Q) at 25°C\n• Faraday's Law: m = (I * t * M) / (n * F)\n• 1st order: ln[A]_t = ln[A]_0 - k*t, t_{1/2} = 0.693 / k\n• Arrhenius: ln(k2 / k1) = (E_a / R) * (1/T1 - 1/T2)\n\nWorked Electrochemical Daniell Cell Example:\nProblem: A Daniell cell consists of a Zn anode in Zn^2+ (0.010 M) and a Cu cathode in Cu^2+ (1.0 M) at 298 K. Given E^0(Zn^2+/Zn) = -0.76 V and E^0(Cu^2+/Cu) = +0.34 V. Calculate: (a) Standard cell potential E^0_cell, (b) Non-standard cell potential E_cell, and (c) Standard free energy Delta G^0.\nStep 1: Compute standard cell potential E^0_cell:\nE^0_cell = E^0_cathode - E^0_anode = +0.34 V - (-0.76 V) = +1.10 Volts.\nStep 2: Write overall cell reaction and identify n:\nZn(s) + Cu^2+(aq) <=> Zn^2+(aq) + Cu(s) (n = 2 electrons transferred).\nStep 3: Compute reaction quotient Q:\nQ = [Zn^2+] / [Cu^2+] = 0.010 M / 1.0 M = 0.010 = 10^(-2).\nStep 4: Apply Nernst equation at 298 K:\nE_cell = E^0_cell - (0.0592 / n) * log10(Q)\nE_cell = 1.10 - (0.0592 / 2) * log10(10^(-2)) = 1.10 - (0.0296) * (-2) = 1.10 + 0.0592 = 1.159 Volts.\nAnalysis: Because reactant Cu^2+ concentration exceeds product Zn^2+, the chemical driving force increases cell voltage above the 1.10 V standard potential.\nStep 5: Compute standard free energy Delta G^0:\nDelta G^0 = - n * F * E^0_cell = - 2 * (96,485 C/mol) * (1.10 V) = - 212,267 J/mol = - 212.3 kJ/mol.\n\nReal-World Applications:\nIn electric vehicle battery packs, Lithium-ion cells (LiCoO2 cathode, graphite anode) deliver high gravimetric energy density governed by intercalation Nernst potentials. In metallurgical refining, the Hall-Héroult electrolytic process reduces bauxite ore into pure aluminum metal using massive 150,000-ampere currents.",
            examTip = "University Exam Tip: For a first-order reaction, the half-life t_half = 0.693 / k is completely independent of initial concentration [A]_0; for a second-order reaction, t_half = 1 / (k*[A]_0), doubling as concentration halves. In viva exams, explain why salt bridges are required in galvanic cells (maintain electrical neutrality by ion migration).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6033,
                    questionNumber = 1,
                    question = "What is the cell potential of a Daniell cell (E^0 = 1.10 V, n = 2) when [Zn^2+] = 0.01 M and [Cu^2+] = 1.0 M at 298 K?",
                    options = mapOf("A" to "1.159 V", "B" to "1.041 V", "C" to "1.100 V", "D" to "0.804 V"),
                    correctOption = "A",
                    explanation = "E = E^0 - (0.0592 / 2) * log(0.01 / 1.0) = 1.10 - 0.0296 * (-2) = 1.10 + 0.0592 = 1.1592 V."
                ),
                QuizQuestionDto(
                    id = 6034,
                    questionNumber = 2,
                    question = "For a first-order chemical reaction with rate constant k = 0.0693 min^(-1), what is the reaction half-life?",
                    options = mapOf("A" to "10.0 minutes", "B" to "5.0 minutes", "C" to "1.0 minute", "D" to "14.4 minutes"),
                    correctOption = "A",
                    explanation = "t_half = ln(2) / k = 0.69315 / 0.0693 min^(-1) = 10.0 minutes."
                ),
                QuizQuestionDto(
                    id = 6035,
                    questionNumber = 3,
                    question = "What is the standard Gibbs Free Energy change Delta G^0 for an electrochemical cell with standard cell potential E^0_cell = +2.0 V transferring n = 1 mole of electrons (F = 96,485 C/mol)?",
                    options = mapOf("A" to "-192.97 kJ/mol", "B" to "+192.97 kJ/mol", "C" to "-96.48 kJ/mol", "D" to "0 kJ/mol"),
                    correctOption = "A",
                    explanation = "Delta G^0 = - n * F * E^0 = - 1 * 96,485 C/mol * 2.0 V = - 192,970 J/mol = -192.97 kJ/mol."
                ),
                QuizQuestionDto(
                    id = 6036,
                    questionNumber = 4,
                    question = "Plotting ln(k) versus 1/T for a chemical reaction yields a straight line with slope equal to which value?",
                    options = mapOf("A" to "- E_a / R", "B" to "+ E_a / R", "C" to "- E_a * R", "D" to "ln(A)"),
                    correctOption = "A",
                    explanation = "From the Arrhenius equation ln(k) = ln(A) - (E_a / R)*(1/T), the slope of ln(k) versus 1/T is directly - E_a / R."
                ),
                QuizQuestionDto(
                    id = 6037,
                    questionNumber = 5,
                    question = "According to Faraday's Laws of Electrolysis, how many coulombs of electric charge are required to deposit 1 mole of copper metal from a Cu^2+ aqueous solution?",
                    options = mapOf("A" to "2 Faradays (2 * 96,485 = 192,970 Coulombs)", "B" to "1 Faraday (96,485 Coulombs)", "C" to "0.5 Faradays", "D" to "4 Faradays"),
                    correctOption = "A",
                    explanation = "Reduction of copper: Cu^2+ + 2 e- -> Cu(s). Reducing 1 mole of Cu^2+ requires 2 moles of electrons, which equals 2 * F = 2 * 96,485 C = 192,970 C."
                ),
                QuizQuestionDto(
                    id = 6038,
                    questionNumber = 6,
                    question = "A reaction is found to have rate units of M^(-1) * s^(-1). What is the overall reaction order?",
                    options = mapOf("A" to "Second-order", "B" to "First-order", "C" to "Zero-order", "D" to "Third-order"),
                    correctOption = "A",
                    explanation = "Rate = k * [A]^2 => M/s = k * M^2 => k = M^(-1) * s^(-1). This dimensional unit corresponds exclusively to a second-order reaction."
                ),
                QuizQuestionDto(
                    id = 6039,
                    questionNumber = 7,
                    question = "In any electrochemical cell, at which electrode does oxidation strictly take place?",
                    options = mapOf("A" to "Anode", "B" to "Cathode", "C" to "Salt bridge", "D" to "Voltmeter terminal"),
                    correctOption = "A",
                    explanation = "By definition across both galvanic and electrolytic cells, Oxidation occurs at the Anode (An Ox), and Reduction occurs at the Cathode (Red Cat)."
                ),
                QuizQuestionDto(
                    id = 6040,
                    questionNumber = 8,
                    question = "How does a positive catalyst increase the rate of a chemical reaction?",
                    options = mapOf("A" to "By providing an alternative mechanism with a lower activation energy barrier", "B" to "By increasing the total kinetic energy of reactant molecules", "C" to "By increasing Delta G^0 of the reaction", "D" to "By shifting the chemical equilibrium toward products"),
                    correctOption = "A",
                    explanation = "Catalysts lower the activation energy E_a. Because rate constant k scales exponentially as exp(-E_a / RT), reducing E_a dramatically increases the fraction of collisions with sufficient energy to react."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH5_001",
                    subject = "Chemistry",
                    chapter = "Electrochemistry & Chemical Kinetics",
                    topic = "Galvanic Cells, Nernst Equation and Reaction Kinetics",
                    keywords = listOf("electrochemistry", "galvanic cell", "Nernst equation", "Faraday law", "electrolysis", "rate law", "half life", "Arrhenius", "activation energy"),
                    definition = "Electrochemistry analyzes redox energetics via Delta G^0 = -nFE^0 and Nernst potential adjustments. Chemical kinetics models reaction rates, integrated orders, and Arrhenius temperature dependencies.",
                    formulaOrCoreRule = "E = E^0 - (0.0592/n)*log(Q). Delta G^0 = -nFE^0. Mass = (M*I*t)/(n*F). t_1/2 = 0.693/k (1st order). k = A*e^(-E_a/RT).",
                    workedExample = "For Daniell cell (E^0=1.10V, n=2) with [Zn^2+]=0.01M, [Cu^2+]=1.0M: E = 1.10 - (0.0592/2)*log(0.01) = 1.159 V. Delta G^0 = -2*96485*1.10 = -212.3 kJ/mol.",
                    content = "First-order half-life is independent of initial concentration. Electrolytic refining and battery charging reverse spontaneous cell potentials via external power supply."
                )
            )
        )
    }

    private fun createChapter6(): CatalogChapter {
        return CatalogChapter(
            chapterId = 6,
            chapterOrder = 6,
            title = "Organic Reaction Mechanisms",
            estimatedMinutes = 35,
            summary = "Nucleophilic substitution (SN1 vs SN2), elimination (E1 vs E2), electrophilic aromatic substitution, Markovnikov/anti-Markovnikov addition, and carbocation rearrangements.",
            syllabus = listOf("Thermodynamic vs Kinetic Control & Reaction Energy Profiles", "Nucleophilic Substitution: SN1 (Carbocations) vs SN2 (Inversion of Configuration)", "Elimination Mechanisms: E1 vs E2 (Zaitsev vs Hofmann Regioselectivity)", "Electrophilic Addition to Alkenes: Markovnikov Rule & Wagner-Meerwein Shifts", "Electrophilic Aromatic Substitution (EAS): Arenium Ion Intermediates & Directing Groups"),
            coreFormulas = listOf("SN2 Rate Law: Rate = k * [Substrate] * [Nucleophile] (Bimolecular, concerted)", "SN1 Rate Law: Rate = k * [Substrate] (Unimolecular, carbocation RDS)", "Arrhenius Rate Ratio: k_rel = e^(- Delta_Delta_G^‡ / (R * T))", "Zaitsev Rule: More substituted alkene is thermodynamically favored", "Hammond's Postulate: Transition state structure resembles the species closest to it in free energy"),
            part1Title = "Reaction Coordinates, Polar Mechanisms & Aromatic Substitution",
            part1Content = "Organic reaction mechanisms trace the step-by-step electronic pathways and bond reorganizations connecting reactants to products. Curved arrows depict the movement of electron pairs from nucleophilic (electron-rich) sources to electrophilic (electron-deficient) sinks. Hammond's Postulate dictates that the geometry of a transition state resembles the adjacent reactant, intermediate, or product species closest to it in Gibbs free energy, explaining why stable carbocation intermediates lower activation barriers.\n\nNucleophilic aliphatic substitution occurs via two competing polar pathways: SN2 (Substitution Nucleophilic Bimolecular) proceeds via a concerted, single-step backside displacement of the leaving group, obeying second-order kinetics (Rate = k*[R-X]*[Nu-]) and resulting in complete Walden Inversion of stereochemical configuration; SN1 (Substitution Nucleophilic Unimolecular) proceeds via a two-step mechanism initiated by rate-determining leaving group departure to form a planar sp2 carbocation intermediate, obeying first-order kinetics (Rate = k*[R-X]) and yielding racemic stereochemical mixtures.\n\nElimination pathways compete directly with nucleophilic substitution. E2 elimination is a concerted bimolecular process requiring an anti-periplanar dihedral alignment between the beta-proton and leaving group. Governed by Zaitsev's Rule, small bases abstract protons to yield the most substituted, thermodynamically stable alkene, whereas bulky sterically hindered bases (such as potassium tert-butoxide) abstract accessible peripheral protons to yield the less substituted Hofmann alkene.\n\nElectrophilic additions to alkenes proceed through carbocation intermediates, governed by Vladimir Markovnikov's rule: the electrophilic proton attaches to the carbon with more hydrogens, generating the more substituted, stable carbocation. Carbocations readily undergo Wagner-Meerwein rearrangements (1,2-hydride or 1,2-alkyl shifts) to convert secondary carbocations into stable tertiary cations. In aromatic chemistry, Electrophilic Aromatic Substitution (EAS: nitration, halogenation, Friedel-Crafts) proceeds through resonance-stabilized arenium ion (Wheland) intermediates, where substituents act as activating ortho/para-directors or deactivating meta-directors.",
            part2Title = "Mechanism Schematics, Worked SN1/SN2 Comparison & Applications",
            part2Content = "Exact Mechanism Comparison Rules:\n• Substrate: Methyl > 1° > 2° (SN2 favored); 3° > 2° (SN1 favored)\n• Nucleophile: Strong, charged (OH-, CN-, I-) favors SN2; Weak, neutral (H2O, MeOH) favors SN1\n• Solvent: Polar Aprotic (DMSO, DMF, Acetone) favors SN2; Polar Protic (H2O, EtOH) stabilizes carbocation, favoring SN1\n• Stereochemistry: SN2 yields 100% inversion; SN1 yields partial/full racemization\n\nWorked Nucleophilic Substitution Problem:\nProblem: Predict the mechanism (SN1 or SN2), major organic product, and stereochemical outcome for the reaction of (R)-2-bromobutane with: (a) Sodium cyanide (NaCN) in dimethyl sulfoxide (DMSO), and (b) Methanol (CH3OH) heated at 50°C.\nStep 1: Substrate analysis: 2-bromobutane is a secondary alkyl halide (capable of both SN1 and SN2).\nStep 2: Case (a): NaCN in DMSO:\nReagents: Cyanide ion (CN-) is a powerful charged nucleophile. DMSO is a polar aprotic solvent that solvates Na+ cations while leaving CN- anions unencumbered ('naked') and highly reactive.\nMechanism: Pure concerted SN2.\nProduct: Backside nucleophilic attack inverts the chiral stereocenter, converting (R)-2-bromobutane into (S)-2-methylbutanenitrile.\nStereochemistry: 100% Walden inversion of configuration.\nStep 3: Case (b): CH3OH heated:\nReagents: Methanol is a weak, neutral nucleophile and a polar protic solvent that stabilizes ionic transition states via hydrogen bonding.\nMechanism: Solvolysis via SN1.\nReaction Steps: Rate-determining loss of Br- yields planar secondary carbocation CH3-CH(+)-CH2-CH3. Methanol attacks equally from the top and bottom faces, followed by deprotonation to yield 2-methoxybutane.\nStereochemistry: Racemic mixture of (R)- and (S)-2-methoxybutane.\n\nReal-World Applications:\nIn pharmaceutical synthesis, the production of non-steroidal anti-inflammatory drugs (NSAIDs, e.g., Ibuprofen) utilizes Friedel-Crafts acylation of isobutylbenzene. In petrochemical refining, acid-catalyzed carbocation cracking converts long-chain petroleum alkanes into high-octane branched gasoline components.",
            examTip = "University Exam Tip: Polar aprotic solvents (DMSO, DMF, acetone) accelerate SN2 reactions by stripping solvent shells from nucleophilic anions, whereas polar protic solvents (water, alcohols) stabilize carbocations and favor SN1/E1. In viva exams, explain why tert-butyl bromide exclusively undergoes elimination (E2) when treated with strong base (steric hindrance prevents backside nucleophilic attack).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6041,
                    questionNumber = 1,
                    question = "What is the stereochemical outcome of an SN2 reaction occurring at a single chiral stereocenter?",
                    options = mapOf("A" to "Complete inversion of configuration (Walden Inversion)", "B" to "Complete retention of configuration", "C" to "Racemization (50% retention, 50% inversion)", "D" to "Formation of equal amounts of diastereomers"),
                    correctOption = "A",
                    explanation = "Because the nucleophile must attack from the backside opposite to the leaving group (180-degree trajectory), the three non-reacting bonds invert like an umbrella in a gale, producing 100% stereochemical inversion."
                ),
                QuizQuestionDto(
                    id = 6042,
                    questionNumber = 2,
                    question = "Which alkyl halide substrate is most reactive toward an SN1 nucleophilic substitution reaction?",
                    options = mapOf("A" to "2-Bromo-2-methylpropane (tert-butyl bromide, tertiary)", "B" to "1-Bromobutane (primary)", "C" to "Bromomethane (methyl)", "D" to "2-Bromobutane (secondary)"),
                    correctOption = "A",
                    explanation = "SN1 rates are determined by carbocation intermediate stability. Tertiary alkyl halides form tertiary carbocations stabilized by hyperconjugation and inductive electron donation from three alkyl groups."
                ),
                QuizQuestionDto(
                    id = 6043,
                    questionNumber = 3,
                    question = "According to Zaitsev's Rule, what is the major organic product when 2-bromobutane is treated with a small base like sodium ethoxide?",
                    options = mapOf("A" to "2-Butene (the more substituted, more stable alkene)", "B" to "1-Butene (Hofmann product)", "C" to "1-Butyne", "D" to "Butane"),
                    correctOption = "A",
                    explanation = "Zaitsev's rule states that elimination reactions favor the more substituted alkene (2-butene, disubstituted) over the less substituted terminal alkene (1-butene, monosubstituted) due to thermodynamic hyperconjugation stability."
                ),
                QuizQuestionDto(
                    id = 6044,
                    questionNumber = 4,
                    question = "Why do polar aprotic solvents (such as DMSO and DMF) dramatically accelerate the rate of SN2 reactions?",
                    options = mapOf("A" to "They solvate metal cations strongly while leaving nucleophilic anions relatively un-solvated and highly reactive", "B" to "They form hydrogen bonds with the leaving group", "C" to "They freeze the substrate into a planar configuration", "D" to "They eliminate activation energy entirely"),
                    correctOption = "A",
                    explanation = "Polar aprotic solvents lack acidic protons and cannot hydrogen-bond with anions. This leaves the nucleophile 'naked' with high chemical potential, maximizing nucleophilic attack velocity."
                ),
                QuizQuestionDto(
                    id = 6045,
                    questionNumber = 5,
                    question = "In electrophilic aromatic substitution, what effect does a nitro group (-NO2) on a benzene ring have on reactivity and orientation?",
                    options = mapOf("A" to "Strongly deactivating and meta-directing", "B" to "Strongly activating and ortho/para-directing", "C" to "Weakly deactivating and ortho/para-directing", "D" to "Weakly activating and meta-directing"),
                    correctOption = "A",
                    explanation = "The nitro group withdraws electron density strongly through both inductive and resonance effects, deactivating the benzene ring and destabilizing ortho/para intermediates, directing electrophiles to the meta position."
                ),
                QuizQuestionDto(
                    id = 6046,
                    questionNumber = 6,
                    question = "What is the driving force behind 1,2-hydride and 1,2-methyl shifts in carbocation intermediates?",
                    options = mapOf("A" to "Rearrangement to form a more thermodynamically stable carbocation (e.g., secondary to tertiary)", "B" to "Conversion of an alkene to an alkyne", "C" to "Loss of aromaticity", "D" to "Elimination of formal charge"),
                    correctOption = "A",
                    explanation = "Carbocations spontaneously rearrange via Wagner-Meerwein shifts whenever migration of a hydride or alkyl group transforms a less stable cation (primary or secondary) into a more stable tertiary or allylic/benzylic cation."
                ),
                QuizQuestionDto(
                    id = 6047,
                    questionNumber = 7,
                    question = "What geometric conformation is strictly required between the beta-hydrogen and the leaving group in an E2 elimination mechanism?",
                    options = mapOf("A" to "Anti-periplanar (180-degree dihedral angle)", "B" to "Syn-coplanar (0-degree dihedral angle)", "C" to "Perpendicular (90-degree dihedral angle)", "D" to "Gauche conformation"),
                    correctOption = "A",
                    explanation = "E2 elimination requires anti-periplanar geometry so that the C-H sigma orbital and C-X sigma* antibonding orbital achieve optimal parallel orbital overlap to establish the new pi bond."
                ),
                QuizQuestionDto(
                    id = 6048,
                    questionNumber = 8,
                    question = "According to Markovnikov's Rule, where does the proton (H+) add when HBr reacts with propene (CH3-CH=CH2)?",
                    options = mapOf("A" to "To C1 (the terminal carbon with more hydrogens), forming the more stable secondary carbocation at C2", "B" to "To C2 (the middle carbon with fewer hydrogens)", "C" to "Equally to both carbons", "D" to "Directly to the methyl group"),
                    correctOption = "A",
                    explanation = "Electrophilic addition of H+ occurs at the less substituted carbon C1 (which has 2 hydrogens), generating a stable secondary carbocation at C2, which is then captured by bromide to yield 2-bromopropane."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH6_001",
                    subject = "Chemistry",
                    chapter = "Organic Reaction Mechanisms",
                    topic = "Nucleophilic Substitution, Elimination and Electrophilic Addition",
                    keywords = listOf("organic chemistry", "reaction mechanisms", "SN1", "SN2", "E1", "E2", "Markovnikov", "carbocation", "Walden inversion", "Zaitsev rule"),
                    definition = "Organic reaction mechanisms describe electron pair movement in polar reactions. SN2 proceeds via concerted backside inversion, SN1 involves planar carbocation intermediates, and E2 requires anti-periplanar geometry.",
                    formulaOrCoreRule = "Rate_SN2 = k[Substrate][Nu]. Rate_SN1 = k[Substrate]. Zaitsev: More substituted alkene favored. Markovnikov: H adds to carbon with more H's.",
                    workedExample = "(R)-2-bromobutane with NaCN in DMSO undergoes pure SN2 backside attack, yielding (S)-2-methylbutanenitrile with 100% Walden inversion.",
                    content = "Polar aprotic solvents accelerate SN2 mechanisms by leaving anions unsolvated. Carbocations undergo Wagner-Meerwein shifts to achieve tertiary thermodynamic stability."
                )
            )
        )
    }

    private fun createChapter7(): CatalogChapter {
        return CatalogChapter(
            chapterId = 7,
            chapterOrder = 7,
            title = "Stereochemistry & Biomolecules",
            estimatedMinutes = 30,
            summary = "Chirality, Cahn-Ingold-Prelog (R/S) rules, enantiomers/diastereomers, meso compounds, Fischer/Haworth projections, carbohydrates, amino acids, and DNA structure.",
            syllabus = listOf("Molecular Chirality, Asymmetric Centers & Optical Activity", "Cahn-Ingold-Prelog (CIP) Priority Rules & (R)/(S) Nomenclature", "Stereoisomeric Relationships: Enantiomers, Diastereomers & Meso Compounds", "Carbohydrates: Monosaccharides, Mutarotation & Glycosidic Linkages", "Proteins & Nucleic Acids: Peptide Bonds, Protein Folding & DNA Double Helix"),
            coreFormulas = listOf("Specific Optical Rotation: [alpha]_T^lambda = alpha / (c * l) (where c in g/mL, l in dm)", "Enantiomeric Excess: ee = (|R - S| / (R + S)) * 100% = ([alpha]_observed / [alpha]_pure) * 100%", "Number of Stereoisomers: Max Stereoisomers = 2^n (for n chiral centers with no internal symmetry)", "Isoelectric Point of Amino Acid: pI = 0.5 * (pK_a1 + pK_a2)", "Beer-Lambert Absorption Law for Biomolecules: A = epsilon * b * c"),
            part1Title = "Chirality Principles, CIP Rules & Macromolecular Biochemistry",
            part1Content = "Stereochemistry investigates the spatial three-dimensional arrangement of atoms in molecules. A molecule is chiral if it is non-superimposable on its mirror image, possessing no internal plane of symmetry (sigma) or inversion center (i). Carbon atoms bonded to four distinct substituents constitute stereogenic (chiral) centers. A pair of non-superimposable mirror images are Enantiomers, possessing identical scalar physical properties (melting points, boiling points, NMR spectra) but rotating plane-polarized light in equal and opposite directions (+/- [alpha]). Stereoisomers that are not mirror images are Diastereomers, exhibiting distinct physical and chemical properties.\n\nThe Cahn-Ingold-Prelog (CIP) rules unambiguously designate absolute stereochemistry. Substituents attached to a stereocenter are prioritized by decreasing atomic number (Z). When two atoms are identical, priority is determined by comparing successive atoms along substituent chains until a point of difference is reached. Viewing the molecule with the lowest priority group (priority 4, typically H) directed away from the observer, a clockwise progression from priority 1 -> 2 -> 3 defines the (R) configuration (Rectus), while counterclockwise defines (S) (Sinister).\n\nBiomolecules exhibit strict homochirality in living organisms. Carbohydrates are polyhydroxy aldehydes or ketones classified by carbon count. Naturally occurring sugars exist almost exclusively as D-enantiomers. In aqueous solution, open-chain monosaccharides undergo intramolecular hemiacetal cyclization, producing alpha and beta anomers that interconvert dynamically via mutarotation. Monosaccharides link via condensation reactions into disaccharides (sucrose, lactose) and structural/storage polysaccharides (cellulose, glycogen) through O-glycosidic bonds.\n\nProteins are linear polymers constructed from 20 standard L-alpha-amino acids joined via planar amide (peptide) bonds. Depending on pH relative to their isoelectric point (pI), amino acids exist as neutral zwitterions, cations, or anions. Protein architecture stratifies into Primary (covalent sequence), Secondary (alpha-helices and beta-pleated sheets stabilized by backbone hydrogen bonds), Tertiary (3D folding driven by the hydrophobic effect, disulfide bridges, and salt bridges), and Quaternary structures. Nucleic acids (DNA and RNA) encode genetic information through antiparallel phosphodiester backbones and Watson-Crick base pairs (A=T with 2 hydrogen bonds, G≡C with 3 hydrogen bonds).",
            part2Title = "CIP Prioritization, Worked (R/S) Assignment & Biochemical Systems",
            part2Content = "Exact Equations & CIP Prioritization Rules:\n• CIP Priority: Higher atomic number Z gets higher priority (-Br > -Cl > -OH > -NH2 > -CH3 > -H)\n• Optical Purity: % ee = ([alpha]_obs / [alpha]_pure) * 100%\n• Amino Acid Isoelectric Point: pI = 0.5 * (pK_a1 + pK_a2)\n• Max Stereoisomers = 2^n (diminished by meso forms when internal symmetry exists)\n\nWorked (R/S) Stereochemical Assignment Example:\nProblem: Assign the absolute configuration ((R) or (S)) for glyceraldehyde (HO-CH2-CH(OH)-CHO) and calculate the enantiomeric excess if a synthetic sample exhibits specific rotation [alpha] = +8.7° (pure (R)-glyceraldehyde [alpha] = +13.5°).\nStep 1: Identify chiral center: C2 is bonded to four groups: -OH, -CHO, -CH2OH, -H.\nStep 2: Assign CIP priorities by atomic number:\nPriority 1: -OH (Oxygen Z = 8 beats Carbon Z = 6).\nPriority 2: -CHO (Carbon bonded to (O, O, H) via formal double bond).\nPriority 3: -CH2OH (Carbon bonded to (O, H, H)).\nPriority 4: -H (Hydrogen Z = 1).\nStep 3: Determine configuration:\nWith lowest-priority -H pointing to the rear (dashed wedge):\nTracing priority 1 (-OH) -> 2 (-CHO) -> 3 (-CH2OH) proceeds in a clockwise direction.\nTherefore, the absolute stereochemical configuration is (R).\nStep 4: Compute enantiomeric excess (ee):\nee = ([alpha]_obs / [alpha]_pure) * 100% = (+8.7° / +13.5°) * 100% = 64.44% ee.\nStep 5: Compute composition of mixture:\nMajor enantiomer (R) = 64.44% + (100 - 64.44)/2 = 64.44% + 17.78% = 82.22% (R).\nMinor enantiomer (S) = 17.78% (S).\n\nReal-World Applications:\nIn pharmacology, enantiomeric purity is critical: while (S)-naproxen is a potent anti-inflammatory pain reliever, (R)-naproxen is a severe liver toxin. The thalidomide tragedy underscored the necessity of testing enantiomers independently, as the (R)-enantiomer cures morning sickness while the (S)-enantiomer is teratogenic.",
            examTip = "University Exam Tip: A meso compound contains two or more chiral centers yet is completely optically inactive (achiral) due to an internal plane of symmetry (e.g., meso-tartaric acid). In viva exams, explain why Watson-Crick G-C base pairs are more thermally stable than A-T pairs (3 hydrogen bonds versus 2).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6049,
                    questionNumber = 1,
                    question = "What is a Meso compound in organic stereochemistry?",
                    options = mapOf("A" to "A molecule containing multiple chiral centers that is achiral and optically inactive due to an internal plane of symmetry", "B" to "A 50:50 racemic mixture of two enantiomers", "C" to "A stereoisomer with no chiral centers", "D" to "A carbohydrate that cannot undergo mutarotation"),
                    correctOption = "A",
                    explanation = "A meso compound contains stereogenic centers but possesses an internal plane or inversion center of symmetry that superimposes the molecule on its mirror image, canceling optical rotation."
                ),
                QuizQuestionDto(
                    id = 6050,
                    questionNumber = 2,
                    question = "What is the enantiomeric excess (ee) of a sample containing 80% (R)-enantiomer and 20% (S)-enantiomer?",
                    options = mapOf("A" to "60%", "B" to "80%", "C" to "40%", "D" to "100%"),
                    correctOption = "A",
                    explanation = "ee = % Major - % Minor = 80% - 20% = 60%."
                ),
                QuizQuestionDto(
                    id = 6051,
                    questionNumber = 3,
                    question = "Under Cahn-Ingold-Prelog (CIP) rules, which substituent has the highest priority?",
                    options = mapOf("A" to "-Br (Bromine, Z = 35)", "B" to "-OH (Oxygen, Z = 8)", "C" to "-Cl (Chlorine, Z = 17)", "D" to "-COOH (Carbon, Z = 6)"),
                    correctOption = "A",
                    explanation = "CIP priority is governed directly by atomic number of the immediately attached atom. Bromine (Z = 35) has the highest atomic number and receives highest priority."
                ),
                QuizQuestionDto(
                    id = 6052,
                    questionNumber = 4,
                    question = "How many hydrogen bonds form between a Guanine-Cytosine (G-C) complementary base pair in double-stranded DNA?",
                    options = mapOf("A" to "3 hydrogen bonds", "B" to "2 hydrogen bonds", "C" to "1 hydrogen bond", "D" to "4 hydrogen bonds"),
                    correctOption = "A",
                    explanation = "Watson-Crick Guanine-Cytosine (G-C) pairs are held together by three specific hydrogen bonds, while Adenine-Thymine (A-T) pairs have only two."
                ),
                QuizQuestionDto(
                    id = 6053,
                    questionNumber = 5,
                    question = "What is the maximum number of stereoisomers possible for a non-cyclic molecule with 3 asymmetric chiral centers and no internal symmetry?",
                    options = mapOf("A" to "8 (2^3)", "B" to "6", "C" to "4", "D" to "16"),
                    correctOption = "A",
                    explanation = "For n chiral centers with no internal symmetry, the maximum number of stereoisomers is 2^n = 2^3 = 8."
                ),
                QuizQuestionDto(
                    id = 6054,
                    questionNumber = 6,
                    question = "What chemical phenomenon causes the gradual change in optical rotation of freshly dissolved alpha-D-glucose until reaching an equilibrium value (+52.7°)?",
                    options = mapOf("A" to "Mutarotation (interconversion between alpha and beta anomers via open-chain intermediate)", "B" to "Racemization into L-glucose", "C" to "Esterification of hydroxyl groups", "D" to "Thermal denaturation"),
                    correctOption = "A",
                    explanation = "Mutarotation is the equilibrium ring-opening and re-closing of monosaccharides, causing dynamic interconversion between alpha and beta anomers until thermodynamic equilibrium is established."
                ),
                QuizQuestionDto(
                    id = 6055,
                    questionNumber = 7,
                    question = "What is the isoelectric point (pI) of alanine given pK_a1 (alpha-COOH) = 2.34 and pK_a2 (alpha-NH3+) = 9.69?",
                    options = mapOf("A" to "6.02", "B" to "7.00", "C" to "5.50", "D" to "12.03"),
                    correctOption = "A",
                    explanation = "pI = 0.5 * (pK_a1 + pK_a2) = 0.5 * (2.34 + 9.69) = 0.5 * (12.03) = 6.015 approximately 6.02."
                ),
                QuizQuestionDto(
                    id = 6056,
                    questionNumber = 8,
                    question = "What type of chemical bond links consecutive amino acid residues in the primary sequence of a polypeptide chain?",
                    options = mapOf("A" to "Planar Amide (Peptide) Bond", "B" to "Phosphodiester linkage", "C" to "Disulfide cross-link", "D" to "Glycosidic bond"),
                    correctOption = "A",
                    explanation = "Amino acids condense via peptide bonds (-CO-NH-), which possess partial double-bond character due to resonance, enforcing rigid planarity on the backbone."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH7_001",
                    subject = "Chemistry",
                    chapter = "Stereochemistry & Biomolecules",
                    topic = "Chirality, Cahn-Ingold-Prelog Rules and Biomolecular Architecture",
                    keywords = listOf("stereochemistry", "chirality", "CIP rules", "enantiomers", "diastereomers", "meso compound", "carbohydrates", "amino acids", "DNA", "pI"),
                    definition = "Stereochemistry studies 3D molecular conformations and chirality. CIP rules define absolute (R/S) configurations. Biomolecules display strict homochirality in carbohydrates (D-sugars), proteins (L-amino acids), and DNA double helices.",
                    formulaOrCoreRule = "[alpha] = alpha/(c*l). % ee = (|R-S|/(R+S))*100%. Max isomers = 2^n. pI = 0.5*(pK_a1 + pK_a2).",
                    workedExample = "Glyceraldehyde with C2 groups -OH(1), -CHO(2), -CH2OH(3), -H(4): clockwise tracking with H back gives (R)-glyceraldehyde. Sample with [alpha]=+8.7 vs +13.5 pure has ee = 64.4%.",
                    content = "Enantiomers have identical physical constants but rotate polarized light in opposite directions. Watson-Crick DNA architecture is stabilized by A-T (2 H-bonds) and G-C (3 H-bonds) base pairings."
                )
            )
        )
    }

    private fun createChapter8(): CatalogChapter {
        return CatalogChapter(
            chapterId = 8,
            chapterOrder = 8,
            title = "Analytical & Industrial Chemistry",
            estimatedMinutes = 35,
            summary = "Spectroscopy (UV-Vis, FTIR, 1H-NMR, Mass Spec), chromatography (HPLC, GC), buffer solutions, Haber/Contact processes, and green chemistry principles.",
            syllabus = listOf("Absorption Spectroscopy: Beer-Lambert Law & UV-Vis Chromophores", "Infrared Spectroscopy (FTIR): Characteristic Vibrational Bond Frequencies", "Proton NMR (1H-NMR): Chemical Shift (delta), Integration & Spin-Spin Splitting (n+1 rule)", "Mass Spectrometry (MS): Molecular Ion M+, Base Peak & Isotope Patterns", "Industrial Catalytic Processes: Haber-Bosch, Contact Process & 12 Green Chemistry Principles"),
            coreFormulas = listOf("Beer-Lambert Law: A = - log10(T) = epsilon * b * c", "NMR Chemical Shift: delta = ((nu_sample - nu_TMS) / nu_spectrometer) * 10^6 (in ppm)", "Spin-Spin Splitting Multiplicity: Multiplicity = n + 1 (for n neighboring equivalent protons)", "Henderson-Hasselbalch Equation: pH = pK_a + log10([A-] / [HA])", "Atom Economy: Atom_Economy = (Molecular_Weight_of_Desired_Product / Sum_MW_All_Reactants) * 100%"),
            part1Title = "Analytical Spectrometry, Separation Science & Chemical Manufacturing",
            part1Content = "Analytical chemistry provides the qualitative and quantitative methodologies required to determine the elemental composition, molecular structure, and purity of chemical matter. The Beer-Lambert Law A = epsilon*b*c governs UV-Visible spectroscopy, where molar absorptivity epsilon characterizes electronic transitions (pi -> pi* and n -> pi*) in conjugated chromophores. Infrared (FTIR) spectroscopy probes quantized vibrational modes (stretching and bending), where absorption frequencies correlate with bond spring constants: carbonyl C=O stretches appear prominently at 1700 cm^(-1), while broad O-H stretches span 3200-3600 cm^(-1).\n\nNuclear Magnetic Resonance (1H-NMR) is the preeminent technique for organic structural elucidation. Placing nuclei with non-zero spin (such as 1H, I = 1/2) in a magnetic field splits nuclear spin states via the Zeeman effect. Resonance chemical shifts delta = (nu - nu_TMS) / nu_0 * 10^6 (ppm) report the local electronic shielding environment. Signal integration quantifies the relative number of protons, while scalar J-coupling produces multiplet splitting following the n+1 multiplicity rule for n magnetically equivalent neighboring protons.\n\nMass Spectrometry (MS) ionizes analyte molecules (via Electron Ionization EI or Electrospray ESI) and separates the resulting ionic fragments by their mass-to-charge ratio (m/z). The radical cation parent peak M+ reveals nominal molecular mass, while characteristic fragmentation pathways and isotopic signatures (e.g., a 3:1 M:(M+2) ratio for chlorine, or 1:1 for bromine) identify halogenation and elemental composition.\n\nIndustrial chemistry translates laboratory reactions to multi-ton continuous manufacturing. Fritz Haber and Carl Bosch commercialized the catalytic synthesis of ammonia from atmospheric N2 and H2 under 200 bar and 450°C. The Contact Process manufactures sulfuric acid (H2SO4) via vanadium pentoxide (V2O5) oxidation of SO2. Modern chemical manufacturing enforces Anastas and Warner's Twelve Principles of Green Chemistry, prioritizing atom economy, renewable feedstocks, and catalytic efficiency to minimize hazardous waste.",
            part2Title = "Spectral Formulations, Worked Structure Elucidation & Green Chemistry",
            part2Content = "Exact Equations & Spectroscopic Signatures:\n• Beer-Lambert: A = epsilon * b * c (b in cm, c in M)\n• Henderson-Hasselbalch: pH = pK_a + log([Conjugate Base] / [Weak Acid])\n• Atom Economy % = (Molar Mass of Desired Product / Sum of Reactant Molar Masses) * 100%\n• 1H-NMR splitting: Singlet (0 neighbors), Doublet (1 neighbor), Triplet (2 neighbors), Quartet (3 neighbors)\n\nWorked Spectroscopic Structure Elucidation Example:\nProblem: An unknown organic compound with molecular formula C3H6O yields the following spectroscopic data: (a) MS shows molecular ion M+ at m/z = 58; (b) FTIR exhibits a sharp intense peak at 1715 cm^(-1) and no absorption between 3200-3600 cm^(-1); (c) 1H-NMR displays a single sharp singlet at delta = 2.15 ppm integrating to 6 protons. Identify the molecule.\nStep 1: Calculate Degrees of Unsaturation (Index of Hydrogen Deficiency, IHD):\nIHD = C - (H/2) + (N/2) + 1 = 3 - (6/2) + 0 + 1 = 3 - 3 + 1 = 1.\nOne degree of unsaturation implies one double bond or one ring.\nStep 2: Analyze FTIR spectrum:\nSharp peak at 1715 cm^(-1) confirms a carbonyl group (C=O), accounting for the 1 degree of unsaturation and the single oxygen atom. Absence of absorption at 3200-3600 cm^(-1) confirms no hydroxyl (-OH) group (not an alcohol or carboxylic acid).\nStep 3: Analyze 1H-NMR spectrum:\nA single singlet at delta = 2.15 ppm integrating to 6 protons means all six hydrogens are chemically and magnetically equivalent, with zero adjacent protons (n = 0 => multiplicity = 0 + 1 = singlet).\nDelta = 2.15 ppm is characteristic of methyl protons adjacent to a carbonyl group (-CO-CH3).\nStep 4: Molecular identification:\nTwo equivalent methyl groups attached to a carbonyl: CH3-C(=O)-CH3 (Propan-2-one or Acetone).\nStep 5: Verify MS:\nMolecular weight of acetone = (3*12) + (6*1) + 16 = 36 + 6 + 16 = 58 g/mol, matching M+ at m/z = 58 perfectly.\nFinal Structure: Acetone (Propanone).\n\nReal-World Applications:\nIn forensic toxicology, Gas Chromatography-Mass Spectrometry (GC-MS) detects trace narcotics and performance-enhancing steroids in athlete biofluids at parts-per-billion sensitivity. In environmental engineering, high-performance liquid chromatography (HPLC) quantifies carcinogenic pesticide residues in municipal drinking water.",
            examTip = "University Exam Tip: Differentiate Atom Economy from Percentage Yield: Percentage yield measures reaction efficiency in practice ((actual/theoretical)*100%), whereas Atom Economy measures theoretical waste built into the stoichiometric equation ((MW desired/MW reactants)*100%). In viva exams, explain why TMS (tetramethylsilane) is chosen as the universal 0.0 ppm NMR reference (chemically inert, volatile, and gives a sharp 12-proton singlet at higher field than almost all organic protons).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 6057,
                    questionNumber = 1,
                    question = "An unknown compound with formula C3H6O shows a strong FTIR absorption at 1715 cm^(-1) and a single 1H-NMR singlet at delta = 2.15 ppm (6H). What is the compound?",
                    options = mapOf("A" to "Acetone (Propan-2-one)", "B" to "Propanal", "C" to "Allyl alcohol", "D" to "Propylene oxide"),
                    correctOption = "A",
                    explanation = "A sharp 1715 cm^(-1) peak indicates a carbonyl C=O. A single 6H singlet at 2.15 ppm indicates two symmetric methyl groups attached to the carbonyl with no neighboring protons: CH3-CO-CH3 (acetone)."
                ),
                QuizQuestionDto(
                    id = 6058,
                    questionNumber = 2,
                    question = "According to the Beer-Lambert Law A = epsilon * b * c, what is the absorbance A of a 0.002 M dye solution in a 1.0 cm path-length cuvette with molar absorptivity epsilon = 500 M^(-1)*cm^(-1)?",
                    options = mapOf("A" to "1.0", "B" to "0.1", "C" to "0.5", "D" to "2.0"),
                    correctOption = "A",
                    explanation = "A = epsilon * b * c = 500 M^(-1)*cm^(-1) * 1.0 cm * 0.002 M = 1.0."
                ),
                QuizQuestionDto(
                    id = 6059,
                    questionNumber = 3,
                    question = "What is the 1H-NMR spin-spin splitting multiplicity of the methylene (-CH2-) protons in bromoethane (CH3-CH2-Br)?",
                    options = mapOf("A" to "Quartet (multiplicity = 3 + 1 = 4)", "B" to "Triplet (multiplicity = 3)", "C" to "Doublet", "D" to "Singlet"),
                    correctOption = "A",
                    explanation = "By the n + 1 rule, the -CH2- protons are coupled to n = 3 equivalent protons on the adjacent methyl group (-CH3). Multiplicity = 3 + 1 = 4 (a quartet)."
                ),
                QuizQuestionDto(
                    id = 6060,
                    questionNumber = 4,
                    question = "In Mass Spectrometry, the presence of two molecular ion peaks of approximately equal intensity separated by 2 mass units (M+ and M+2 in a 1:1 ratio) is characteristic of which halogen?",
                    options = mapOf("A" to "Bromine (79Br and 81Br in ~50.5% : 49.5% abundance)", "B" to "Chlorine (35Cl and 37Cl in ~3:1 ratio)", "C" to "Iodine (monoisotopic 127I)", "D" to "Fluorine (monoisotopic 19F)"),
                    correctOption = "A",
                    explanation = "Bromine naturally exists as two stable isotopes, 79Br (~50.7%) and 81Br (~49.3%), yielding twin M and M+2 peaks of virtually identical heights in mass spectra."
                ),
                QuizQuestionDto(
                    id = 6061,
                    questionNumber = 5,
                    question = "What catalyst is employed in the industrial Contact Process for the oxidation of sulfur dioxide to sulfur trioxide (2 SO2 + O2 <=> 2 SO3)?",
                    options = mapOf("A" to "Vanadium pentoxide (V2O5)", "B" to "Iron-potassium oxide (Fe/K2O)", "C" to "Finely divided Platinum (Pt)", "D" to "Ziegler-Natta titanium chloride"),
                    correctOption = "A",
                    explanation = "Vanadium pentoxide (V2O5) on silica support is the primary heterogeneous catalyst used industrially in the Contact Process at 450°C to oxidize SO2 to SO3."
                ),
                QuizQuestionDto(
                    id = 6062,
                    questionNumber = 6,
                    question = "Why is Tetramethylsilane (TMS, (CH3)4Si) universally chosen as the internal reference standard for 1H and 13C NMR spectroscopy?",
                    options = mapOf("A" to "It gives a single sharp 12-proton singlet that is chemically inert, volatile, and shielded farther upfield than almost all organic proton signals", "B" to "It reacts with organic solvents to create colored complexes", "C" to "It eliminates the need for deuterated NMR solvents", "D" to "It has an atomic mass of exactly 100"),
                    correctOption = "A",
                    explanation = "Because silicon is less electronegative than carbon, the twelve equivalent methyl protons in TMS are heavily shielded, appearing at higher magnetic field (defined as 0.0 ppm) than almost all organic proton resonances."
                ),
                QuizQuestionDto(
                    id = 6063,
                    questionNumber = 7,
                    question = "Under the 12 Principles of Green Chemistry, what does 'Atom Economy' evaluate?",
                    options = mapOf("A" to "The proportion of reactant atoms that are incorporated into the final desired product versus wasted as byproducts", "B" to "The monetary cost of purchasing pure chemical reagents", "C" to "The electrical efficiency of electrolysis power supplies", "D" to "The number of electrons lost during oxidation"),
                    correctOption = "A",
                    explanation = "Atom Economy = (Molar mass of desired product / Total molar mass of all reactants) * 100%. It evaluates the synthetic efficiency of incorporating all input atoms into the target molecule."
                ),
                QuizQuestionDto(
                    id = 6064,
                    questionNumber = 8,
                    question = "What is the pH of a buffer solution containing 0.10 M acetic acid (pK_a = 4.76) and 0.10 M sodium acetate?",
                    options = mapOf("A" to "4.76", "B" to "7.00", "C" to "3.76", "D" to "5.76"),
                    correctOption = "A",
                    explanation = "By Henderson-Hasselbalch: pH = pK_a + log([A-] / [HA]) = 4.76 + log(0.10 / 0.10) = 4.76 + log(1) = 4.76 + 0 = 4.76."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "CHEM_CH8_001",
                    subject = "Chemistry",
                    chapter = "Analytical & Industrial Chemistry",
                    topic = "Spectroscopic Structural Elucidation and Green Industrial Chemistry",
                    keywords = listOf("analytical chemistry", "spectroscopy", "NMR", "FTIR", "mass spec", "Beer Lambert", "Haber process", "Contact process", "green chemistry"),
                    definition = "Analytical chemistry elucidates structures using spectroscopy (UV-Vis, FTIR, NMR, MS) and chromatography. Industrial chemistry scales catalytic syntheses (Haber, Contact process) guided by 12 green chemistry principles.",
                    formulaOrCoreRule = "A = eps*b*c. delta = ((nu - nu_TMS)/nu_0)*10^6. pH = pK_a + log([A-]/[HA]). Atom Economy = (MW_desired / Sum MW_reactants)*100%.",
                    workedExample = "C3H6O with FTIR 1715 cm^-1 (C=O) and 1H-NMR delta 2.15 ppm singlet (6H) corresponds to acetone (CH3-CO-CH3), MW=58 g/mol matching M+ peak.",
                    content = "1H-NMR multiplicity obeys the n+1 coupling rule. Green chemistry maximizes atom economy and catalytic efficiency to minimize hazardous byproduct generation."
                )
            )
        )
    }
}
