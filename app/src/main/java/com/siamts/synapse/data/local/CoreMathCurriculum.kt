package com.siamts.synapse.data.local

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Embedded university-grade curriculum for Mathematics.
 * 8 progressive college chapters with complete lessons, MCQs, and RAG knowledge chunks.
 */
object CoreMathCurriculum {

    fun getMathSubject(): CatalogSubject {
        return CatalogSubject(
            id = "MATH",
            subjectId = 1,
            name = "Mathematics",
            slug = "mathematics",
            iconSymbol = "∑",
            colorHex = "#3B82F6",
            category = "Formal Sciences",
            description = "University mathematics covering foundations of algebra, calculus, linear algebra, differential equations, and probability.",
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
            title = "Foundations of Algebra, Functions & Set Theory",
            estimatedMinutes = 25,
            summary = "Sets, relations, domain/range, polynomial algebra, division algorithm, quadratic & cubic roots, and equivalence classes.",
            syllabus = listOf("Set Operations, Cartesian Products & Power Sets", "Relations: Reflexive, Symmetric, Transitive & Equivalence Classes", "Functions: Injective, Surjective, Bijective & Inverses", "Polynomial Rings, Remainder Theorem & Synthetic Division", "Quadratic Equations, Discriminant & Cardano's Cubic Formula"),
            coreFormulas = listOf("Quadratic Formula: x = (-b ± √(b² - 4ac)) / (2a)", "Discriminant: D = b² - 4ac (D > 0: two real, D = 0: one real, D < 0: complex conjugates)", "Vieta's Formulas: For ax² + bx + c = 0, r₁ + r₂ = -b/a, r₁r₂ = c/a", "De Morgan's Laws: (A ∪ B)' = A' ∩ B', (A ∩ B)' = A' ∪ B'", "Cardano's Depressed Cubic: For x³ + px + q = 0, Δ = (q/2)² + (p/3)³"),
            part1Title = "Set Theory, Relations & Algebraic Structures",
            part1Content = "A set is a well-defined collection of distinct mathematical elements governed by the Zermelo-Fraenkel axioms. Set algebra establishes foundational Boolean operations including unions, intersections, complements, and Cartesian products. An equivalence relation over a set is simultaneously reflexive, symmetric, and transitive, partitioning the set into disjoint equivalence classes that form the basis for quotient spaces and modular arithmetic in abstract algebra.\n\nA function f: A → B assigns to each element of domain A exactly one element in codomain B. A mapping is injective (one-to-one) if f(x₁) = f(x₂) implies x₁ = x₂, surjective (onto) if every element in B has a pre-image in A, and bijective if it satisfies both conditions, establishing an invertible isomorphism between sets. The domain of a real-valued algebraic function requires non-negative radicands for even roots and non-zero denominators for rational expressions.\n\nPolynomials over a field form a commutative ring denoted F[x]. The Division Algorithm guarantees that for any polynomials f(x) and g(x) with g(x) ≠ 0, there exist unique quotient q(x) and remainder r(x) such that f(x) = g(x)q(x) + r(x) where deg(r) < deg(g). The Fundamental Theorem of Algebra asserts that every non-zero single-variable polynomial of degree n with complex coefficients has exactly n complex roots, counted with algebraic multiplicity.\n\nQuadratic and cubic polynomials model parabolic and inflection geometries across engineering. Quadratic roots are determined by completing the square or applying the quadratic formula, where the discriminant D = b² - 4ac dictates the algebraic split. Cubic equations can be reduced to depressed form x³ + px + q = 0 through Tschirnhaus transformation, with roots resolvable analytically via Cardano's formula or trigonometric substitution.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Quadratic Formula: x = (-b ± √(b² - 4ac)) / (2a)\n• Rational Root Theorem: Any rational root p/q of a_n x^n + ... + a_0 = 0 satisfies p | a_0 and q | a_n\n• Vieta's Theorem for cubics ax³ + bx² + cx + d = 0: r₁ + r₂ + r₃ = -b/a, r₁r₂ + r₂r₃ + r₃r₁ = c/a, r₁r₂r₃ = -d/a\n\nWorked Numerical Example:\nProblem: Find the domain of f(x) = √((x - 2) / (x + 3)) and solve the polynomial 2x³ - 3x² - 11x + 6 = 0.\nStep 1: For the domain, the radicand must be non-negative: (x - 2)/(x + 3) ≥ 0 with x ≠ -3. Critical points are x = -3 and x = 2. Testing intervals: (-∞, -3) yields positive; (-3, 2) yields negative; [2, ∞) yields positive. Domain: (-∞, -3) ∪ [2, ∞).\nStep 2: For 2x³ - 3x² - 11x + 6 = 0, possible rational roots are ±1, ±2, ±3, ±6, ±1/2, ±3/2. Testing x = 3: 2(27) - 3(9) - 11(3) + 6 = 54 - 27 - 33 + 6 = 0. Thus (x - 3) is a factor.\nStep 3: Synthetic division by (x - 3) yields quotient 2x² + 3x - 2.\nStep 4: Factoring 2x² + 3x - 2 = (2x - 1)(x + 2) = 0 yields roots x = 1/2 and x = -2.\nFinal Roots: x ∈ {3, 1/2, -2}.\n\nReal-World Applications:\nIn computational science, set theory and equivalence relations underpin relational database query optimization and type checking in compiler theory. Polynomial root finding is ubiquitous in digital signal processing for filter pole-zero placement, control systems stability analysis (Routh-Hurwitz criterion), and cryptography in RSA modular key generation.",
            examTip = "University Exam Tip: Always verify inequality sign reversals when multiplying or dividing by variables of unknown sign. In viva examinations, be prepared to prove that an equivalence relation induces a unique partition of the underlying set.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1001,
                    questionNumber = 1,
                    question = "Which condition is required for a relation R on set A to be an equivalence relation?",
                    options = mapOf("A" to "Reflexive, Symmetric, and Transitive", "B" to "Reflexive, Antisymmetric, and Transitive", "C" to "Irreflexive, Symmetric, and Non-transitive", "D" to "Injective, Surjective, and Bijective"),
                    correctOption = "A",
                    explanation = "By mathematical definition, an equivalence relation must simultaneously satisfy reflexivity (aRa), symmetry (aRb implies bRa), and transitivity (aRb and bRc implies aRc)."
                ),
                QuizQuestionDto(
                    id = 1002,
                    questionNumber = 2,
                    question = "What is the natural domain of the real-valued function f(x) = √((x - 4) / (x + 1))?",
                    options = mapOf("A" to "(-∞, -1) ∪ [4, ∞)", "B" to "[-1, 4]", "C" to "(-1, 4]", "D" to "(-∞, -1] ∪ [4, ∞)"),
                    correctOption = "A",
                    explanation = "The radicand (x - 4)/(x + 1) ≥ 0 when numerator and denominator have the same sign. The denominator cannot be zero, so x ≠ -1. Testing sign intervals gives (-∞, -1) ∪ [4, ∞)."
                ),
                QuizQuestionDto(
                    id = 1003,
                    questionNumber = 3,
                    question = "If a quadratic equation ax² + bx + c = 0 has a discriminant D = b² - 4ac < 0, what are its roots in the complex field?",
                    options = mapOf("A" to "Two distinct real roots", "B" to "One repeated rational root", "C" to "Two complex conjugate roots", "D" to "No roots exist in any number system"),
                    correctOption = "C",
                    explanation = "When D < 0, the square root of D introduces the imaginary unit i = √(-1), producing complex conjugate roots x = -b/(2a) ± i√(4ac - b²)/(2a)."
                ),
                QuizQuestionDto(
                    id = 1004,
                    questionNumber = 4,
                    question = "For the cubic equation 2x³ - 6x² + 4x - 8 = 0, what is the sum of all three roots according to Vieta's formulas?",
                    options = mapOf("A" to "3", "B" to "-3", "C" to "2", "D" to "4"),
                    correctOption = "A",
                    explanation = "By Vieta's formulas, the sum of roots for ax³ + bx² + cx + d = 0 is -b/a = -(-6)/2 = 3."
                ),
                QuizQuestionDto(
                    id = 1005,
                    questionNumber = 5,
                    question = "What does the Fundamental Theorem of Algebra state regarding a polynomial P(z) of degree n ≥ 1 with complex coefficients?",
                    options = mapOf("A" to "It has at least one real root", "B" to "It has exactly n complex roots, counted with multiplicity", "C" to "All of its roots must be distinct rational numbers", "D" to "It can only be factored over the field of real numbers"),
                    correctOption = "B",
                    explanation = "The Fundamental Theorem of Algebra guarantees that every non-constant single-variable polynomial with complex coefficients splits completely into linear factors over C, having exactly n roots counted with algebraic multiplicity."
                ),
                QuizQuestionDto(
                    id = 1006,
                    questionNumber = 6,
                    question = "If f: A → B is a bijection, which property is strictly guaranteed?",
                    options = mapOf("A" to "f has a unique inverse function f⁻¹: B → A", "B" to "A must be a subset of B", "C" to "f(x) = x for all x in A", "D" to "f cannot be represented by algebraic formulas"),
                    correctOption = "A",
                    explanation = "A bijection is both injective (one-to-one) and surjective (onto), which is the necessary and sufficient condition for the existence of a unique two-sided inverse function f⁻¹."
                ),
                QuizQuestionDto(
                    id = 1007,
                    questionNumber = 7,
                    question = "What are the roots of the polynomial equation x³ - 7x + 6 = 0?",
                    options = mapOf("A" to "x = 1, 2, -3", "B" to "x = -1, -2, 3", "C" to "x = 1, -2, -3", "D" to "x = 2, 3, -1"),
                    correctOption = "A",
                    explanation = "Testing x = 1: 1 - 7 + 6 = 0. Factoring out (x - 1) gives (x - 1)(x² + x - 6) = (x - 1)(x - 2)(x + 3) = 0. Roots are x = 1, 2, -3."
                ),
                QuizQuestionDto(
                    id = 1008,
                    questionNumber = 8,
                    question = "Under what condition does the Division Algorithm for polynomials f(x) = g(x)q(x) + r(x) terminate?",
                    options = mapOf("A" to "r(x) = 0 or deg(r) < deg(g)", "B" to "deg(r) > deg(g)", "C" to "deg(q) = deg(g)", "D" to "r(x) must be a constant integer"),
                    correctOption = "A",
                    explanation = "The polynomial division algorithm uniquely determines quotient q(x) and remainder r(x) such that either r(x) = 0 or the degree of r(x) is strictly less than the degree of divisor g(x)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH1_001",
                    subject = "Mathematics",
                    chapter = "Foundations of Algebra, Functions & Set Theory",
                    topic = "Sets, Relations and Equivalence Classes",
                    keywords = listOf("set theory", "equivalence relation", "injective", "surjective", "bijective", "partition", "domain", "algebra"),
                    definition = "An equivalence relation is a binary relation that is reflexive, symmetric, and transitive, partitioning a set into mutually exclusive equivalence classes.",
                    formulaOrCoreRule = "Equivalence: aRa, aRb <=> bRa, (aRb and bRc) => aRc. Partition: Union of classes = A and classes are disjoint.",
                    workedExample = "For integers modulo n, a ~ b iff n divides (a - b). This induces n distinct equivalence classes [0], [1], ..., [n-1].",
                    content = "Set foundations and equivalence relations form the mathematical basis for modular arithmetic, abstract quotient groups, and relational database schema normalization."
                )
            )
        )
    }

    private fun createChapter2(): CatalogChapter {
        return CatalogChapter(
            chapterId = 2,
            chapterOrder = 2,
            title = "Trigonometry & Complex Numbers",
            estimatedMinutes = 25,
            summary = "Trigonometric identities, De Moivre's theorem, Argand plane geometry, Euler's formula, roots of unity, and AC phasor analysis.",
            syllabus = listOf("Fundamental & Double-Angle Trigonometric Identities", "Complex Numbers in Rectangular, Polar & Exponential Form", "Argand Plane, Modulus-Argument & Complex Conjugates", "Euler's Formula: e^(iθ) = cos θ + i sin θ & Proof via Taylor Series", "De Moivre's Theorem & nth Roots of Unity Geometry"),
            coreFormulas = listOf("Euler's Formula: e^(iθ) = cos θ + i sin θ", "Euler's Identity: e^(iπ) + 1 = 0", "De Moivre's Theorem: (cos θ + i sin θ)^n = cos(nθ) + i sin(nθ)", "Modulus & Argument: |z| = √(a² + b²), θ = atan2(b, a)", "Roots of Unity: z_k = r^(1/n) * exp(i(θ + 2kπ)/n) for k = 0, 1, ..., n-1"),
            part1Title = "Argand Geometry, Euler's Formula & Roots of Unity",
            part1Content = "Complex numbers extend the real number continuum by adjoining the imaginary unit i satisfying i² = -1. Any complex number z can be expressed in rectangular form as z = a + bi where a = Re(z) and b = Im(z). In the Argand plane, z corresponds to the Cartesian point (a, b) or a vector directed from the origin with magnitude |z| = √(a² + b²) and direction argument θ = arg(z), establishing polar form z = r(cos θ + i sin θ).\n\nEuler's formula e^(iθ) = cos θ + i sin θ bridges exponential and trigonometric functions through power series expansions. Substituting x = iθ into the Taylor series for e^x separates into the alternating series for cos θ and sin θ. Setting θ = π produces Euler's identity e^(iπ) + 1 = 0, uniting the five fundamental mathematical constants (0, 1, e, i, π) in a single equation of profound elegance.\n\nDe Moivre's theorem asserts that for any real θ and integer n, [r(cos θ + i sin θ)]^n = r^n[cos(nθ) + i sin(nθ)]. This result simplifies powers of complex numbers from tedious binomial expansions into elementary multiplication of arguments. Furthermore, de Moivre's theorem enables exact derivation of multiple-angle trigonometric formulas such as cos(3θ) = 4 cos³ θ - 3 cos θ.\n\nThe n-th roots of unity are the solutions to z^n = 1, given by ω_k = exp(i 2kπ / n) for k = 0, 1, ..., n-1. Geometrically, these n roots are vertices of a regular n-sided polygon inscribed in the unit circle on the Argand plane. The sum of all n-th roots of unity is identically zero for n ≥ 2, a foundational symmetry exploited in Discrete Fourier Transforms (DFT).",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Rectangular to Polar: r = √(a² + b²), θ = atan2(b, a)\n• Multiplication in Polar Form: z₁z₂ = r₁r₂ e^(i(θ₁ + θ₂))\n• Division in Polar Form: z₁/z₂ = (r₁/r₂) e^(i(θ₁ - θ₂))\n• De Moivre: z^n = r^n e^(i n θ)\n\nWorked Numerical Example:\nProblem: Compute (1 + i√3)⁶ using De Moivre's Theorem, and find all four roots of z⁴ = -16.\nStep 1: For z = 1 + i√3: a = 1, b = √3. Modulus r = √(1² + (√3)²) = √(1 + 3) = 2.\nStep 2: Argument θ = arctan(√3 / 1) = π/3. Thus z = 2 e^(i π/3).\nStep 3: Apply De Moivre's theorem: z⁶ = 2⁶ e^(i 6(π/3)) = 64 e^(i 2π) = 64(cos 2π + i sin 2π) = 64(1 + 0) = 64.\nStep 4: For z⁴ = -16: Modulus r = 16, argument θ = π (since -16 lies on negative real axis). Thus -16 = 16 e^(i π).\nStep 5: Fourth roots: z_k = 16^(1/4) e^(i (π + 2kπ)/4) = 2 e^(i (2k+1)π / 4) for k ∈ {0, 1, 2, 3}.\n• k = 0: z₀ = 2 e^(i π/4) = 2(√2/2 + i√2/2) = √2 + i√2\n• k = 1: z₁ = 2 e^(i 3π/4) = -√2 + i√2\n• k = 2: z₂ = 2 e^(i 5π/4) = -√2 - i√2\n• k = 3: z₃ = 2 e^(i 7π/4) = √2 - i√2.\n\nReal-World Applications:\nIn electrical engineering, complex numbers represent AC circuit phasors V = V₀ e^(i ω t), converting differential circuit equations into algebraic impedance equations Z = R + i(ωL - 1/(ωC)). In quantum mechanics, state vectors evolve according to the complex Schrödinger equation. In computer graphics, quaternions extend complex numbers to avoid gimbal lock in 3D rotations.",
            examTip = "University Exam Tip: When computing arguments θ = arctan(b/a), remember that if a < 0 and b ≥ 0, add π to arctan(b/a). If a < 0 and b < 0, subtract π. Always sketch the Argand point first to verify the quadrant.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1009,
                    questionNumber = 1,
                    question = "What is the rectangular form of Euler's formula e^(iθ)?",
                    options = mapOf("A" to "cos θ + i sin θ", "B" to "sin θ + i cos θ", "C" to "cos θ - i sin θ", "D" to "cosh θ + i sinh θ"),
                    correctOption = "A",
                    explanation = "Euler's formula explicitly states that e^(iθ) = cos θ + i sin θ for any real number θ."
                ),
                QuizQuestionDto(
                    id = 1010,
                    questionNumber = 2,
                    question = "What is the exact value of (1 + i√3)⁶ calculated via De Moivre's theorem?",
                    options = mapOf("A" to "64", "B" to "-64", "C" to "64i", "D" to "0"),
                    correctOption = "A",
                    explanation = "In polar form, 1 + i√3 = 2 e^(i π/3). Raising to the 6th power gives 2⁶ e^(i 6π/3) = 64 e^(i 2π) = 64(1) = 64."
                ),
                QuizQuestionDto(
                    id = 1011,
                    questionNumber = 3,
                    question = "What is the sum of all n distinct n-th roots of unity for any integer n ≥ 2?",
                    options = mapOf("A" to "0", "B" to "1", "C" to "-1", "D" to "n"),
                    correctOption = "A",
                    explanation = "The roots of unity form a geometric series 1 + ω + ω² + ... + ω^(n-1) = (1 - ω^n) / (1 - ω). Since ω^n = 1 and ω ≠ 1, the sum evaluates identically to 0."
                ),
                QuizQuestionDto(
                    id = 1012,
                    questionNumber = 4,
                    question = "What is the modulus |z| of the complex number z = 3 - 4i?",
                    options = mapOf("A" to "5", "B" to "7", "C" to "25", "D" to "1"),
                    correctOption = "A",
                    explanation = "|z| = √(a² + b²) = √(3² + (-4)²) = √(9 + 16) = √25 = 5."
                ),
                QuizQuestionDto(
                    id = 1013,
                    questionNumber = 5,
                    question = "Where do the roots of the equation zⁿ = 1 lie on the complex Argand plane?",
                    options = mapOf("A" to "Equally spaced on the unit circle |z| = 1", "B" to "Along the real axis between -1 and 1", "C" to "Along the imaginary axis", "D" to "At the origin (0, 0)"),
                    correctOption = "A",
                    explanation = "All n-th roots of unity have modulus |z| = 1 and arguments 2kπ/n, forming the vertices of a regular n-sided polygon inscribed in the unit circle."
                ),
                QuizQuestionDto(
                    id = 1014,
                    questionNumber = 6,
                    question = "Which identity represents Euler's famous formula linking five fundamental constants?",
                    options = mapOf("A" to "e^(iπ) + 1 = 0", "B" to "e^(iπ) - 1 = 0", "C" to "e^(2iπ) = -1", "D" to "cos(π) + sin(π) = 0"),
                    correctOption = "A",
                    explanation = "Setting θ = π in e^(iθ) = cos θ + i sin θ yields e^(iπ) = -1 + 0, which rearranges to e^(iπ) + 1 = 0."
                ),
                QuizQuestionDto(
                    id = 1015,
                    questionNumber = 7,
                    question = "What is the principal argument of the complex number z = -1 - i?",
                    options = mapOf("A" to "-3π/4", "B" to "3π/4", "C" to "5π/4", "D" to "-π/4"),
                    correctOption = "A",
                    explanation = "Point (-1, -1) lies in the third quadrant. The principal argument must lie in (-π, π]. Thus θ = arctan(1) - π = π/4 - π = -3π/4."
                ),
                QuizQuestionDto(
                    id = 1016,
                    questionNumber = 8,
                    question = "In electrical engineering AC analysis, what physical quantity does the imaginary component of complex impedance represent?",
                    options = mapOf("A" to "Reactance (inductive or capacitive energy storage)", "B" to "Ohmic thermal dissipation (resistance)", "C" to "Direct current voltage", "D" to "Magnetic core hysteresis loss"),
                    correctOption = "A",
                    explanation = "Impedance is Z = R + iX, where the real part R is resistance (power dissipation) and the imaginary part X is reactance (storage in inductors and capacitors)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH2_001",
                    subject = "Mathematics",
                    chapter = "Trigonometry & Complex Numbers",
                    topic = "Euler's Formula and De Moivre's Theorem",
                    keywords = listOf("complex numbers", "euler formula", "de moivre theorem", "argand plane", "polar form", "roots of unity", "phasors"),
                    definition = "Euler's formula states that e^(iθ) = cos θ + i sin θ. De Moivre's theorem extends this to integer powers: (cos θ + i sin θ)^n = cos(nθ) + i sin(nθ).",
                    formulaOrCoreRule = "e^(iθ) = cos θ + i sin θ; [r e^(iθ)]^n = r^n e^(i nθ); Roots: z_k = r^(1/n) e^(i(θ + 2kπ)/n).",
                    workedExample = "For z = 1 + i√3, r = 2, θ = π/3. z^6 = 2^6 e^(i 2π) = 64.",
                    content = "Euler's formula and De Moivre's theorem govern AC circuit phasor calculations, signal frequency decomposition, and quantum mechanics wavefunctions."
                )
            )
        )
    }

    private fun createChapter3(): CatalogChapter {
        return CatalogChapter(
            chapterId = 3,
            chapterOrder = 3,
            title = "Differential Calculus & Limits",
            estimatedMinutes = 25,
            summary = "Epsilon-delta limits, continuity, chain rule, implicit differentiation, partial derivatives, and Taylor & Maclaurin series expansions.",
            syllabus = listOf("Epsilon-Delta Limit Foundations & Continuity Theorems", "Differentiation Rules: Chain, Product, Quotient & Implicit", "Higher-Order Derivatives & Taylor / Maclaurin Series", "Multivariable Partial Derivatives & Gradient Vectors (∇f)", "Extrema Optimization: Critical Points & Second Derivative Test"),
            coreFormulas = listOf("Epsilon-Delta Limit: ∀ε > 0, ∃δ > 0 such that 0 < |x - a| < δ ⇒ |f(x) - L| < ε", "Chain Rule: d/dx[f(g(x))] = f'(g(x)) * g'(x)", "Taylor Series: f(x) = ∑ [f^(n)(a) / n!] * (x - a)^n from n=0 to ∞", "Gradient Vector: ∇f = [∂f/∂x₁, ∂f/∂x₂, ..., ∂f/∂x_n]^T", "Hessian Discriminant: D = f_xx * f_yy - (f_xy)² (D > 0, f_xx > 0 ⇒ local min)"),
            part1Title = "Limits, Continuity, Multivariable Gradients & Series",
            part1Content = "The calculus of limits formalized by Cauchy and Weierstrass establishes the rigorous bedrock for all continuous analysis. A function f(x) converges to limit L as x approaches a if for every infinitesimal tolerance ε > 0, there exists a neighborhood δ > 0 such that 0 < |x - a| < δ implies |f(x) - L| < ε. Continuity requires that the limit exists and equals the functional value f(a), guaranteeing the Intermediate Value and Extreme Value Theorems across compact intervals.\n\nThe derivative f'(x) quantifies the instantaneous rate of change as the limit of the difference quotient. Differentiability guarantees local linear approximation f(x + Δx) ≈ f(x) + f'(x)Δx. Advanced differentiation rules, particularly the Chain Rule d/dx[f(g(x))] = f'(g(x))g'(x), govern the propagation of derivatives through composite functions, serving as the foundational mathematical engine behind gradient backpropagation in deep neural networks.\n\nTaylor's Theorem extends linear approximations to arbitrary polynomial degrees. If f has n+1 continuous derivatives on an open interval, then f(x) can be expressed as a Taylor polynomial centered at a plus a Lagrange remainder term R_n(x) = [f^(n+1)(c) / (n+1)!](x - a)^(n+1). When centered at a = 0, the expansion is termed a Maclaurin series, transforming transcendental functions such as e^x, sin x, and ln(1+x) into tractable power series.\n\nIn multivariable domains, scalar fields z = f(x, y) possess directional derivatives along arbitrary unit vectors. The gradient vector ∇f = [∂f/∂x, ∂f/∂y]^T points in the direction of greatest instantaneous functional increase, with magnitude representing the steepest ascent rate. Critical points occur where ∇f = 0, classified via the eigenvalues of the symmetric Hessian matrix or the two-dimensional discriminant D = f_xx f_yy - (f_xy)² into local minima, local maxima, or saddle points.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• L'Hôpital's Rule: If lim f(x)/g(x) produces 0/0 or ±∞/±∞, then lim f(x)/g(x) = lim f'(x)/g'(x)\n• Maclaurin of e^x = 1 + x + x²/2! + x³/3! + ...\n• Maclaurin of sin x = x - x³/3! + x⁵/5! - ...\n• Maclaurin of cos x = 1 - x²/2! + x⁴/4! - ...\n• Directional Derivative: D_u f = ∇f · u (where |u| = 1)\n\nWorked Numerical Example:\nProblem: Find the local extrema of f(x, y) = x³ + y³ - 3xy and calculate the 3rd-order Maclaurin polynomial of g(x) = ln(1 + x).\nStep 1: Compute partial derivatives: f_x = 3x² - 3y = 0 ⇒ y = x². f_y = 3y² - 3x = 0 ⇒ x = y².\nStep 2: Substitute y = x² into second equation: x = (x²)² = x⁴ ⇒ x⁴ - x = 0 ⇒ x(x³ - 1) = 0. Real solutions: x = 0 or x = 1. Critical points are (0, 0) and (1, 1).\nStep 3: Second partial derivatives: f_xx = 6x, f_yy = 6y, f_xy = -3. Discriminant D(x, y) = f_xx f_yy - (f_xy)² = 36xy - 9.\n• At (0, 0): D = 0 - 9 = -9 < 0 ⇒ Saddle point at (0, 0, 0).\n• At (1, 1): D = 36(1)(1) - 9 = 27 > 0, and f_xx = 6 > 0 ⇒ Local minimum at (1, 1, -1).\nStep 4: For g(x) = ln(1 + x): g(0) = 0; g'(x) = (1+x)⁻¹ ⇒ g'(0) = 1; g''(x) = -(1+x)⁻² ⇒ g''(0) = -1; g'''(x) = 2(1+x)⁻³ ⇒ g'''(0) = 2.\nStep 5: Maclaurin P₃(x) = 0 + 1(x) + (-1/2!)x² + (2/3!)x³ = x - x²/2 + x³/3.\n\nReal-World Applications:\nDifferential calculus is the universal engine of physics and engineering. In machine learning, Stochastic Gradient Descent (SGD) minimizes high-dimensional loss surfaces by updating weights W ← W - η ∇L(W). In aerospace, Euler-Lagrange equations optimize spacecraft orbital maneuvers, while in robotics, Jacobian differential matrices map actuator joint velocities to end-effector trajectories.",
            examTip = "University Exam Tip: Always confirm the indeterminate form condition before applying L'Hôpital's rule. If D = 0 in the multivariable second derivative test, the test is inconclusive, requiring higher-order path analysis or algebraic inspection.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1017,
                    questionNumber = 1,
                    question = "What is the third-order term in the Maclaurin series expansion of ln(1 + x)?",
                    options = mapOf("A" to "+x³/3", "B" to "-x³/3", "C" to "+x³/6", "D" to "-x³/6"),
                    correctOption = "A",
                    explanation = "The Maclaurin series for ln(1 + x) is x - x²/2 + x³/3 - x⁴/4 + ... The third-order term is +x³/3."
                ),
                QuizQuestionDto(
                    id = 1018,
                    questionNumber = 2,
                    question = "If the Hessian discriminant D = f_xx * f_yy - (f_xy)² is negative at a critical point, what is the nature of the point?",
                    options = mapOf("A" to "Saddle point", "B" to "Local minimum", "C" to "Local maximum", "D" to "The test is inconclusive"),
                    correctOption = "A",
                    explanation = "When D < 0, the eigenvalues of the Hessian matrix have opposite signs, indicating that the surface curves upwards in one direction and downwards in another (a saddle point)."
                ),
                QuizQuestionDto(
                    id = 1019,
                    questionNumber = 3,
                    question = "What does the gradient vector ∇f of a scalar function f(x, y, z) physically represent?",
                    options = mapOf("A" to "The direction of maximum rate of increase of f", "B" to "The tangent plane to the surface", "C" to "The direction where f remains constant", "D" to "The total volume enclosed by f"),
                    correctOption = "A",
                    explanation = "By vector calculus, the gradient vector points in the direction of steepest ascent (maximum directional derivative), and its magnitude equals this maximal rate of change."
                ),
                QuizQuestionDto(
                    id = 1020,
                    questionNumber = 4,
                    question = "Evaluating the limit lim_{x→0} (sin 3x) / x gives:",
                    options = mapOf("A" to "3", "B" to "1", "C" to "0", "D" to "1/3"),
                    correctOption = "A",
                    explanation = "Applying L'Hôpital's rule to the 0/0 form yields lim_{x→0} (3 cos 3x) / 1 = 3(1) = 3."
                ),
                QuizQuestionDto(
                    id = 1021,
                    questionNumber = 5,
                    question = "What is the derivative of f(x) = e^(x²) with respect to x using the Chain Rule?",
                    options = mapOf("A" to "2x e^(x²)", "B" to "e^(x²)", "C" to "x² e^(x² - 1)", "D" to "2 e^(x²)"),
                    correctOption = "A",
                    explanation = "Let u = x². Then d/dx[e^u] = e^u * du/dx = e^(x²) * 2x = 2x e^(x²)."
                ),
                QuizQuestionDto(
                    id = 1022,
                    questionNumber = 6,
                    question = "What is the critical point classification for f(x, y) = x² + y² at (0, 0)?",
                    options = mapOf("A" to "Strict local minimum", "B" to "Strict local maximum", "C" to "Saddle point", "D" to "Inconclusive boundary"),
                    correctOption = "A",
                    explanation = "f_xx = 2, f_yy = 2, f_xy = 0. D = 2(2) - 0 = 4 > 0, and f_xx = 2 > 0. Therefore, (0, 0) is a strict local minimum."
                ),
                QuizQuestionDto(
                    id = 1023,
                    questionNumber = 7,
                    question = "Under what exact condition is L'Hôpital's rule mathematically valid?",
                    options = mapOf("A" to "The limit produces an indeterminate form 0/0 or ±∞/±∞", "B" to "The denominator evaluates to a non-zero constant", "C" to "The numerator must be an algebraic polynomial", "D" to "The limit must approach infinity only"),
                    correctOption = "A",
                    explanation = "L'Hôpital's rule requires that both numerator and denominator simultaneously tend to 0 or simultaneously tend to ±∞."
                ),
                QuizQuestionDto(
                    id = 1024,
                    questionNumber = 8,
                    question = "What is the directional derivative of f(x, y) = 2x + 3y in the direction of the unit vector u = [1, 0]^T?",
                    options = mapOf("A" to "2", "B" to "3", "C" to "5", "D" to "√(13)"),
                    correctOption = "A",
                    explanation = "∇f = [∂f/∂x, ∂f/∂y]^T = [2, 3]^T. Directional derivative D_u f = ∇f · u = 2(1) + 3(0) = 2."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH3_001",
                    subject = "Mathematics",
                    chapter = "Differential Calculus & Limits",
                    topic = "Limits, Derivatives and Taylor Series",
                    keywords = listOf("calculus", "derivative", "limits", "taylor series", "maclaurin", "gradient", "hessian", "chain rule", "optimization"),
                    definition = "Differential calculus studies instantaneous rates of change via limits, tangent slopes, and multivariable gradient vectors.",
                    formulaOrCoreRule = "Chain Rule: d/dx[f(g(x))] = f'(g(x)) * g'(x); Taylor: f(x) = sum [f^(n)(a)/n!] * (x - a)^n; Gradient: grad f = [df/dx, df/dy]^T.",
                    workedExample = "For f(x, y) = x^3 + y^3 - 3xy, critical points are (0,0) [saddle] and (1,1) [local minimum with D = 27 > 0, f_xx = 6 > 0].",
                    content = "Differential calculus is the engine of gradient descent in AI neural networks, fluid velocity gradient analysis, and mechanical dynamics."
                )
            )
        )
    }

    private fun createChapter4(): CatalogChapter {
        return CatalogChapter(
            chapterId = 4,
            chapterOrder = 4,
            title = "Integral Calculus & Applications",
            estimatedMinutes = 25,
            summary = "Riemann integration, Fundamental Theorem of Calculus, integration by parts, partial fractions, area under curves, and multiple integrals.",
            syllabus = listOf("Riemann Sums, Darboux Partitions & Definite Integrals", "Fundamental Theorem of Calculus (Part I & II)", "Integration Techniques: Substitution, Parts (LIATE) & Partial Fractions", "Geometric Applications: Area, Arc Length & Volumes of Revolution", "Double & Triple Integrals: Fubini's Theorem & Polar Jacobians"),
            coreFormulas = listOf("Fundamental Theorem: d/dx [∫_a^x f(t)dt] = f(x) and ∫_a^b f(x)dx = F(b) - F(a)", "Integration by Parts: ∫ u dv = uv - ∫ v du", "Volume of Revolution (Disk): V = π ∫_a^b [f(x)]² dx", "Volume of Revolution (Shell): V = 2π ∫_a^b x * f(x) dx", "Polar Double Integral: ∬_R f(x, y) dx dy = ∬_S f(r cos θ, r sin θ) * r dr dθ"),
            part1Title = "Riemann Integration, FTC & Multiple Coordinate Integrals",
            part1Content = "Integral calculus formalizes continuous summation and accumulation of infinitesimal quantities. A bounded function f on [a, b] is Riemann integrable if the upper and lower Darboux sums converge to the same value as the mesh size of the partition approaches zero. The definite integral geometrically represents the signed net area between the curve f(x) and the horizontal axis.\n\nThe Fundamental Theorem of Calculus (FTC) unifies differential and integral calculus as inverse operational procedures. Part I demonstrates that the accumulation function F(x) = ∫_a^x f(t)dt is continuous and differentiable with F'(x) = f(x). Part II provides the universal computational shortcut ∫_a^b f(x)dx = F(b) - F(a), where F is any antiderivative of f, circumventing laborious limit sums of partitions.\n\nTechniques of integration extend analytical solvability across broad functional classes. Integration by substitution reverses the Chain Rule via differential change of variables. Integration by parts reverses the Product Rule, stated as ∫ u dv = uv - ∫ v du. The LIATE rule (Logarithmic, Inverse trigonometric, Algebraic, Trigonometric, Exponential) provides a systematic priority hierarchy for selecting u to ensure that ∫ v du is strictly simpler than the original integrand.\n\nMultiple integrals generalize integration to higher dimensions. By Fubini's Theorem, double integrals over rectangular or elementary regions can be evaluated as iterated single integrals. When transitioning to curvilinear coordinates, the differential area element scales by the determinant of the transformation Jacobian matrix. In polar coordinates, dx dy transforms into r dr dθ, an essential geometric factor representing the wedge area element.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Integration by Parts: ∫ u dv = uv - ∫ v du\n• Disk Method: V = π ∫_a^b [R(x)]² dx\n• Arc Length: L = ∫_a^b √(1 + [f'(x)]²) dx\n• Polar Jacobian: J = |∂(x, y)/∂(r, θ)| = r\n\nWorked Numerical Example:\nProblem: Evaluate ∫_0^1 x e^(2x) dx using integration by parts, and find the area enclosed by the circle x² + y² = R² using polar integration.\nStep 1: For ∫ x e^(2x) dx: Using LIATE, choose u = x (Algebraic) and dv = e^(2x) dx (Exponential).\nStep 2: Differentiate u: du = dx. Integrate dv: v = (1/2) e^(2x).\nStep 3: Apply integration by parts: ∫ x e^(2x) dx = u v - ∫ v du = (1/2) x e^(2x) - (1/2) ∫ e^(2x) dx = (1/2) x e^(2x) - (1/4) e^(2x) + C = (1/4) e^(2x) [2x - 1] + C.\nStep 4: Evaluate limits from 0 to 1:\n• At x = 1: (1/4) e² (2(1) - 1) = (1/4) e²\n• At x = 0: (1/4) e⁰ (2(0) - 1) = -1/4\n• Value = (1/4) e² - (-1/4) = (e² + 1) / 4 ≈ (7.389 + 1)/4 = 2.097.\nStep 5: For circular area: Region in polar coordinates is 0 ≤ r ≤ R, 0 ≤ θ ≤ 2π.\nArea = ∫_0^(2π) ∫_0^R r dr dθ = [∫_0^(2π) dθ] * [∫_0^R r dr] = (2π) * [r²/2]_0^R = 2π * (R²/2) = π R².\n\nReal-World Applications:\nIntegral calculus is central to physics and engineering mechanics. It determines centers of mass, moments of inertia, and total mechanical work done by variable force fields W = ∫ F · dr. In probability theory, continuous random variables require integration to compute cumulative distributions P(a ≤ X ≤ b) = ∫_a^b f(x) dx, while in electrical power engineering, Root Mean Square (RMS) voltage is computed via V_rms = √( (1/T) ∫_0^T [v(t)]² dt ).",
            examTip = "University Exam Tip: Never forget the Jacobian factor r when converting double integrals to polar coordinates (dx dy = r dr dθ). In integration by parts, beware of negative sign distribution when applying limits.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1025,
                    questionNumber = 1,
                    question = "What is the result of evaluating ∫_0^1 x e^(2x) dx?",
                    options = mapOf("A" to "(e² + 1) / 4", "B" to "(e² - 1) / 2", "C" to "e² / 4", "D" to "(e² - 1) / 4"),
                    correctOption = "A",
                    explanation = "Integrating by parts gives [(1/2)x e^(2x) - (1/4)e^(2x)]_0^1 = [(1/4)e²] - [-1/4] = (e² + 1) / 4."
                ),
                QuizQuestionDto(
                    id = 1026,
                    questionNumber = 2,
                    question = "What is the Jacobian determinant factor when transforming Cartesian area element dx dy into polar coordinates?",
                    options = mapOf("A" to "r", "B" to "r²", "C" to "1/r", "D" to "sin θ"),
                    correctOption = "A",
                    explanation = "The transformation x = r cos θ, y = r sin θ has Jacobian matrix determinant J = (cos θ)(r cos θ) - (-r sin θ)(sin θ) = r(cos² θ + sin² θ) = r. Hence dx dy = r dr dθ."
                ),
                QuizQuestionDto(
                    id = 1027,
                    questionNumber = 3,
                    question = "According to the Fundamental Theorem of Calculus Part I, what is d/dx [∫_1^x √(1 + t³) dt]?",
                    options = mapOf("A" to "√(1 + x³)", "B" to "√(1 + t³)", "C" to "3x² / (2√(1 + x³))", "D" to "(1/3)(1 + x³)^(3/2)"),
                    correctOption = "A",
                    explanation = "By FTC Part I, d/dx [∫_a^x f(t)dt] = f(x). Here f(x) = √(1 + x³)."
                ),
                QuizQuestionDto(
                    id = 1028,
                    questionNumber = 4,
                    question = "Which formula gives the volume of a solid formed by rotating y = f(x) from x = a to x = b around the x-axis using the disk method?",
                    options = mapOf("A" to "π ∫_a^b [f(x)]² dx", "B" to "2π ∫_a^b x f(x) dx", "C" to "∫_a^b f(x) dx", "D" to "π² ∫_a^b [f(x)]³ dx"),
                    correctOption = "A",
                    explanation = "Each circular disk slice has cross-sectional area A(x) = π r² = π [f(x)]². Integrating across the interval yields V = π ∫_a^b [f(x)]² dx."
                ),
                QuizQuestionDto(
                    id = 1029,
                    questionNumber = 5,
                    question = "In the LIATE rule for integration by parts, which functional class has the highest priority to be chosen as u?",
                    options = mapOf("A" to "Logarithmic functions", "B" to "Exponential functions", "C" to "Trigonometric functions", "D" to "Algebraic functions"),
                    correctOption = "A",
                    explanation = "LIATE stands for Logarithmic, Inverse trigonometric, Algebraic, Trigonometric, Exponential. Logarithmic functions have top priority for u."
                ),
                QuizQuestionDto(
                    id = 1030,
                    questionNumber = 6,
                    question = "What is the value of the improper Gaussian integral ∫_(-∞)^∞ e^(-x²) dx?",
                    options = mapOf("A" to "√π", "B" to "π", "C" to "1", "D" to "2√π"),
                    correctOption = "A",
                    explanation = "Squaring the integral, converting to polar coordinates (I² = ∫_0^(2π) ∫_0^∞ e^(-r²) r dr dθ = π), and taking the square root gives I = √π."
                ),
                QuizQuestionDto(
                    id = 1031,
                    questionNumber = 7,
                    question = "What is the arc length of a smooth curve y = f(x) from x = a to x = b?",
                    options = mapOf("A" to "∫_a^b √(1 + [f'(x)]²) dx", "B" to "∫_a^b (1 + f'(x)) dx", "C" to "∫_a^b √(f(x)) dx", "D" to "∫_a^b [f'(x)]² dx"),
                    correctOption = "A",
                    explanation = "The infinitesimal hypotenuse ds = √(dx² + dy²) = √(1 + (dy/dx)²) dx. Integrating gives L = ∫_a^b √(1 + [f'(x)]²) dx."
                ),
                QuizQuestionDto(
                    id = 1032,
                    questionNumber = 8,
                    question = "Under what condition does Fubini's Theorem guarantee that double integrals can be computed as iterated integrals in either order?",
                    options = mapOf("A" to "The integrand f(x, y) is continuous or absolutely integrable over the region", "B" to "The region must strictly be a circle", "C" to "The function must have zero partial derivatives", "D" to "The limits of integration must be infinite"),
                    correctOption = "A",
                    explanation = "Fubini's Theorem guarantees that ∬ f dA = ∫ [∫ f dy] dx = ∫ [∫ f dx] dy when f(x, y) is continuous or absolutely integrable over the integration domain."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH4_001",
                    subject = "Mathematics",
                    chapter = "Integral Calculus & Applications",
                    topic = "Fundamental Theorem of Calculus and Integration Techniques",
                    keywords = listOf("integral calculus", "fundamental theorem of calculus", "integration by parts", "liate", "polar coordinates", "jacobian", "riemann sum"),
                    definition = "The Fundamental Theorem of Calculus links differentiation and integration as inverse operations: d/dx[int_a^x f(t)dt] = f(x) and int_a^b f(x)dx = F(b) - F(a).",
                    formulaOrCoreRule = "int u dv = uv - int v du; Disk Volume: V = pi int [f(x)]^2 dx; Polar: dx dy = r dr dtheta.",
                    workedExample = "int_0^1 x e^(2x) dx = [(1/2)x e^(2x) - (1/4)e^(2x)]_0^1 = (e^2 + 1)/4.",
                    content = "Integral calculus is fundamental for probability distribution normalization, work energy theorems, and electromagnetic field flux calculations."
                )
            )
        )
    }

    private fun createChapter5(): CatalogChapter {
        return CatalogChapter(
            chapterId = 5,
            chapterOrder = 5,
            title = "Linear Algebra, Matrices & Vector Spaces",
            estimatedMinutes = 25,
            summary = "Vector spaces, linear independence, Gaussian elimination, determinants, eigenvalues & eigenvectors, Gram-Schmidt, and SVD.",
            syllabus = listOf("Vector Spaces, Subspaces, Span & Basis / Dimension", "Systems of Linear Equations, Row Echelon Form & Rank-Nullity", "Determinants, Invertibility & Cramer's Rule", "Eigenvalues, Eigenvectors & Diagonalization (A = P D P⁻¹)", "Orthogonality, Gram-Schmidt & Singular Value Decomposition (SVD)"),
            coreFormulas = listOf("Characteristic Equation: det(A - λI) = 0", "Eigenvector Definition: Av = λv (v ≠ 0)", "Rank-Nullity Theorem: rank(A) + nullity(A) = n (number of columns)", "Gram-Schmidt Orthogonalization: u_k = v_k - ∑ [(v_k · u_j) / (u_j · u_j)] * u_j", "Singular Value Decomposition: A = U Σ V^T"),
            part1Title = "Vector Spaces, Linear Transformations, Eigenvalues & SVD",
            part1Content = "Linear algebra is the foundational mathematical language of multidimensional computation. A vector space V over a field F is an algebraic structure closed under vector addition and scalar multiplication satisfying eight core axioms. A set of vectors {v₁, ..., v_k} is linearly independent if the only linear combination yielding the zero vector is the trivial combination where all coefficients are zero. The span of a linearly independent set forms a basis, with its cardinality defining the intrinsic dimension of the vector space.\n\nA linear transformation T: V → W preserves vector addition and scalar scaling, T(c₁v₁ + c₂v₂) = c₁T(v₁) + c₂T(v₂), admitting an exact representation as a matrix A relative to chosen bases. The Rank-Nullity Theorem establishes that the dimension of the domain n equals the dimension of the range (rank) plus the dimension of the kernel (nullity). Solving systems of linear equations Ax = b via Gaussian elimination reduces matrices to row echelon form, exposing pivot columns and free variables.\n\nFor an n × n square matrix A, an eigenvector v ≠ 0 satisfies Av = λv, meaning the transformation acts on v purely by scaling with eigenvalue λ. Eigenvalues are the roots of the characteristic polynomial p(λ) = det(A - λI) = 0. A matrix is diagonalizable as A = P D P⁻¹ if and only if it possesses n linearly independent eigenvectors, where D is a diagonal matrix containing the eigenvalues and P contains the corresponding eigenvectors as columns. The Spectral Theorem guarantees that any real symmetric matrix is orthogonally diagonalizable with strictly real eigenvalues.\n\nSingular Value Decomposition (SVD) generalizes diagonalization to arbitrary m × n rectangular matrices as A = U Σ V^T, where U and V are orthogonal matrices containing left- and right-singular vectors, and Σ contains non-negative singular values σ_i = √(λ_i(A^T A)) sorted in descending order. SVD provides the theoretical optimal low-rank approximation (Eckart-Young-Mirsky theorem), making it the gold standard for noise reduction and dimensionality reduction across high-dimensional datasets.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Characteristic Polynomial: det(A - λI) = 0\n• Trace and Determinant Properties: Tr(A) = ∑ λ_i and det(A) = ∏ λ_i\n• Matrix Inverse: A⁻¹ = (1 / det(A)) * adj(A)\n• Gram-Schmidt: u_k = v_k - ∑_{j=1}^{k-1} proj_{u_j}(v_k)\n• SVD: A = U Σ V^T\n\nWorked Numerical Example:\nProblem: Find the eigenvalues and eigenvectors of matrix A = [[4, 1], [2, 3]], and verify Tr(A) and det(A).\nStep 1: Form the characteristic matrix: A - λI = [[4 - λ, 1], [2, 3 - λ]].\nStep 2: Compute determinant: det(A - λI) = (4 - λ)(3 - λ) - (1)(2) = λ² - 7λ + 12 - 2 = λ² - 7λ + 10 = 0.\nStep 3: Factor: (λ - 5)(λ - 2) = 0 ⇒ Eigenvalues λ₁ = 5, λ₂ = 2.\nStep 4: Verify trace and determinant:\n• Tr(A) = 4 + 3 = 7 = 5 + 2 = 7 (Verified).\n• det(A) = (4)(3) - (1)(2) = 10 = (5)(2) = 10 (Verified).\nStep 5: Find eigenvector for λ₁ = 5: (A - 5I)v = [[-1, 1], [2, -2]] [x, y]^T = [0, 0]^T ⇒ -x + y = 0 ⇒ y = x. Eigenvector v₁ = [1, 1]^T.\nStep 6: Find eigenvector for λ₂ = 2: (A - 2I)v = [[2, 1], [2, 1]] [x, y]^T = [0, 0]^T ⇒ 2x + y = 0 ⇒ y = -2x. Eigenvector v₂ = [1, -2]^T.\n\nReal-World Applications:\nLinear algebra powers modern artificial intelligence and data science. Principal Component Analysis (PCA) uses eigenvector decomposition of data covariance matrices to reduce high-dimensional feature spaces. Google's PageRank algorithm calculates the dominant eigenvector of a billion-node web link transition matrix via power iteration. In computer graphics, 4×4 affine transformation matrices execute 3D rotation, scaling, and perspective projection in real-time GPUs.",
            examTip = "University Exam Tip: Always check your calculated eigenvalues instantly using Tr(A) = ∑ λ_i and det(A) = ∏ λ_i. If their sum does not equal the matrix diagonal sum, re-check your characteristic polynomial immediately.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1033,
                    questionNumber = 1,
                    question = "What are the eigenvalues of the matrix A = [[4, 1], [2, 3]]?",
                    options = mapOf("A" to "λ = 5 and λ = 2", "B" to "λ = 4 and λ = 3", "C" to "λ = 6 and λ = 1", "D" to "λ = -5 and λ = -2"),
                    correctOption = "A",
                    explanation = "det(A - λI) = (4 - λ)(3 - λ) - 2 = λ² - 7λ + 10 = 0. Factoring gives (λ - 5)(λ - 2) = 0, so λ = 5, 2."
                ),
                QuizQuestionDto(
                    id = 1034,
                    questionNumber = 2,
                    question = "According to the Rank-Nullity Theorem, if a 4×6 matrix has rank 3, what is the dimension of its null space (nullity)?",
                    options = mapOf("A" to "3", "B" to "1", "C" to "4", "D" to "6"),
                    correctOption = "A",
                    explanation = "Rank-Nullity states: rank(A) + nullity(A) = n (number of columns). Here n = 6 and rank = 3, so nullity = 6 - 3 = 3."
                ),
                QuizQuestionDto(
                    id = 1035,
                    questionNumber = 2,
                    question = "What is the relationship between the determinant of a square matrix and its eigenvalues?",
                    options = mapOf("A" to "det(A) equals the product of all eigenvalues", "B" to "det(A) equals the sum of all eigenvalues", "C" to "det(A) is always zero if eigenvalues are real", "D" to "det(A) equals the maximum eigenvalue"),
                    correctOption = "A",
                    explanation = "By matrix algebra, the determinant of any square matrix equals the product of its eigenvalues counted with algebraic multiplicity: det(A) = ∏ λ_i."
                ),
                QuizQuestionDto(
                    id = 1036,
                    questionNumber = 4,
                    question = "What is the primary guarantee of the Spectral Theorem for real symmetric matrices?",
                    options = mapOf("A" to "All eigenvalues are real, and the matrix is orthogonally diagonalizable", "B" to "The matrix has no inverse", "C" to "The determinant must be negative", "D" to "Eigenvalues must all be positive integers"),
                    correctOption = "A",
                    explanation = "The Spectral Theorem guarantees that every real symmetric matrix (A = A^T) has purely real eigenvalues and an orthonormal basis of eigenvectors, allowing orthogonal diagonalization A = Q D Q^T."
                ),
                QuizQuestionDto(
                    id = 1037,
                    questionNumber = 5,
                    question = "In Singular Value Decomposition A = U Σ V^T, what do the singular values in Σ represent?",
                    options = mapOf("A" to "The square roots of the eigenvalues of A^T A", "B" to "The trace of matrix A", "C" to "The diagonal elements of matrix U", "D" to "The determinants of sub-matrices"),
                    correctOption = "A",
                    explanation = "Singular values σ_i are defined as the non-negative square roots of the eigenvalues of the positive semi-definite matrix A^T A (or AA^T)."
                ),
                QuizQuestionDto(
                    id = 1038,
                    questionNumber = 6,
                    question = "What is the result of applying Gram-Schmidt orthogonalization to a set of linearly independent vectors?",
                    options = mapOf("A" to "An orthogonal (or orthonormal) basis spanning the same subspace", "B" to "A set of linearly dependent zero vectors", "C" to "The inverse of the matrix", "D" to "The characteristic polynomial"),
                    correctOption = "A",
                    explanation = "The Gram-Schmidt process iteratively subtracts vector projections to generate an orthogonal set of vectors spanning the identical vector subspace."
                ),
                QuizQuestionDto(
                    id = 1039,
                    questionNumber = 7,
                    question = "If det(A) = 0 for a square matrix A, which statement is true?",
                    options = mapOf("A" to "A is singular and has at least one eigenvalue equal to 0", "B" to "A has full rank", "C" to "A is guaranteed to be symmetric", "D" to "A possesses a unique inverse matrix A⁻¹"),
                    correctOption = "A",
                    explanation = "A determinant of zero means the product of eigenvalues is zero (so at least one λ = 0), the matrix is singular (non-invertible), and columns are linearly dependent."
                ),
                QuizQuestionDto(
                    id = 1040,
                    questionNumber = 8,
                    question = "For a 3×3 matrix with eigenvalues λ = 1, 2, 4, what is the trace of the matrix?",
                    options = mapOf("A" to "7", "B" to "8", "C" to "14", "D" to "0"),
                    correctOption = "A",
                    explanation = "The trace of a square matrix equals the sum of its eigenvalues: Tr(A) = 1 + 2 + 4 = 7."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH5_001",
                    subject = "Mathematics",
                    chapter = "Linear Algebra, Matrices & Vector Spaces",
                    topic = "Eigenvalues, Eigenvectors and Singular Value Decomposition",
                    keywords = listOf("linear algebra", "eigenvalue", "eigenvector", "svd", "rank nullity", "gram schmidt", "diagonalization", "determinant", "matrix"),
                    definition = "An eigenvector v of square matrix A satisfies Av = lambda*v where lambda is the eigenvalue. SVD factors any matrix into A = U Sigma V^T.",
                    formulaOrCoreRule = "Characteristic equation: det(A - lambda*I) = 0; Tr(A) = sum lambda_i; det(A) = prod lambda_i; SVD: A = U Sigma V^T.",
                    workedExample = "For A = [[4, 1], [2, 3]], det(A - lambda*I) = lambda^2 - 7lambda + 10 = 0 gives eigenvalues lambda = 5, 2 with eigenvectors [1, 1]^T and [1, -2]^T.",
                    content = "Eigenvalues and SVD are the mathematical backbone of Principal Component Analysis (PCA), Google PageRank, 3D graphics rendering, and quantum state matrices."
                )
            )
        )
    }

    private fun createChapter6(): CatalogChapter {
        return CatalogChapter(
            chapterId = 6,
            chapterOrder = 6,
            title = "Ordinary & Partial Differential Equations (ODE & PDE)",
            estimatedMinutes = 25,
            summary = "First/second order ODEs, integrating factors, Laplace transforms, Fourier series, and separation of variables for PDEs (heat, wave, Laplace).",
            syllabus = listOf("First-Order ODEs: Separable, Exact & Integrating Factor Methods", "Second-Order Linear Homogeneous & Non-Homogeneous ODEs", "Method of Undetermined Coefficients & Variation of Parameters", "Laplace Transforms & Operational Calculus for IVPs", "Fourier Series & Separation of Variables for 1D Wave and Heat PDEs"),
            coreFormulas = listOf("Integrating Factor (1st-Order Linear y' + P(x)y = Q(x)): μ(x) = exp(∫ P(x)dx)", "Characteristic Equation (ay'' + by' + cy = 0): ar² + br + c = 0", "Laplace Transform: L{f(t)} = ∫_0^∞ e^(-st) f(t) dt", "Laplace Derivative: L{y'} = s Y(s) - y(0), L{y''} = s² Y(s) - s y(0) - y'(0)", "1D Wave Equation: ∂²u/∂t² = c² ∂²u/∂x²; 1D Heat Equation: ∂u/∂t = α ∂²u/∂x²"),
            part1Title = "ODEs, Laplace Transforms & Boundary-Value PDE Solutions",
            part1Content = "Differential equations mathematically formulate physical laws relating rates of change to systemic states. First-order ordinary differential equations (ODEs) of the standard linear form y' + P(x)y = Q(x) are solved via integrating factor μ(x) = exp(∫ P(x)dx), converting the left side into an exact total derivative d/dx[μ(x)y] = μ(x)Q(x). Exact differential equations M(x, y)dx + N(x, y)dy = 0 satisfy ∂M/∂y = ∂N/∂x, stemming from the gradient of a scalar potential field.\n\nSecond-order linear ODEs with constant coefficients ay'' + by' + cy = g(t) model harmonic oscillators and resonant systems. The homogeneous solution y_h(t) is dictated by the roots of the algebraic characteristic equation ar² + br + c = 0: two distinct real roots yield exponential decay/growth, repeated roots introduce a secular factor t e^(rt), and complex conjugate roots r = α ± iβ produce damped oscillations e^(αt)[c₁ cos(βt) + c₂ sin(βt)]. The particular solution y_p(t) is constructed using undetermined coefficients or variation of parameters.\n\nThe Laplace transform L{f(t)} = ∫_0^∞ e^(-st) f(t) dt maps differential operations in the time domain into algebraic operations in the complex s-frequency domain. By transforming derivatives via L{y'} = s Y(s) - y(0) and L{y''} = s² Y(s) - s y(0) - y'(0), initial value problems (IVPs) are converted into straightforward polynomial fractions resolved by partial fractions and inverse transforms.\n\nPartial Differential Equations (PDEs) govern continuous multidimensional fields such as heat diffusion, acoustic wave propagation, and electrostatic potential. The canonical method of Separation of Variables decomposes a multivariable PDE u(x, t) into independent single-variable ODEs u(x, t) = X(x)T(t). Imposing spatial boundary conditions generates an infinite discrete spectrum of Fourier orthogonal modes, whose superposition matches arbitrary initial profiles via Fourier series coefficient integration.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Integrating Factor: μ(x) = exp(∫ P(x)dx) ⇒ y(x) = (1/μ(x)) ∫ μ(x)Q(x)dx + C/μ(x)\n• Laplace of Standard Functions: L{1} = 1/s, L{t^n} = n!/s^(n+1), L{e^(at)} = 1/(s-a), L{sin ωt} = ω/(s² + ω²), L{cos ωt} = s/(s² + ω²)\n• Fourier Series: f(x) = a₀/2 + ∑ [a_n cos(nx) + b_n sin(nx)]\n\nWorked Numerical Example:\nProblem: Solve the initial value problem y'' + 4y = 8, with initial conditions y(0) = 3 and y'(0) = 0 using Laplace Transforms.\nStep 1: Take the Laplace transform of both sides: L{y''} + 4 L{y} = L{8}.\nStep 2: Substitute operational derivative formulas: [s² Y(s) - s y(0) - y'(0)] + 4 Y(s) = 8/s.\nStep 3: Plug in y(0) = 3, y'(0) = 0: s² Y(s) - 3s + 4 Y(s) = 8/s ⇒ (s² + 4) Y(s) = 3s + 8/s = (3s² + 8) / s.\nStep 4: Solve algebraically for Y(s): Y(s) = (3s² + 8) / [s(s² + 4)].\nStep 5: Partial fraction decomposition: Y(s) = A/s + (Bs + C)/(s² + 4).\n• 3s² + 8 = A(s² + 4) + (Bs + C)s = (A + B)s² + Cs + 4A.\n• 4A = 8 ⇒ A = 2. A + B = 3 ⇒ 2 + B = 3 ⇒ B = 1. C = 0.\nStep 6: Y(s) = 2/s + s/(s² + 4).\nStep 7: Take the inverse Laplace transform: y(t) = 2 L⁻¹{1/s} + L⁻¹{s/(s² + 2²)} = 2(1) + cos(2t).\nFinal Solution: y(t) = 2 + cos(2t). Verifying: y(0) = 2 + 1 = 3; y'(t) = -2 sin(2t) ⇒ y'(0) = 0; y'' = -4 cos(2t) ⇒ y'' + 4y = -4 cos(2t) + 4(2 + cos(2t)) = 8 (Verified).\n\nReal-World Applications:\nODEs and PDEs are foundational across engineering. Second-order ODEs govern RLC electric circuit oscillations and mechanical vehicle suspension dynamics. The heat conduction PDE governs CPU thermal dissipation and climate temperature modeling, while Maxwell's electromagnetic wave PDEs govern optical fiber signal transmission and 5G wireless telecommunications.",
            examTip = "University Exam Tip: When applying Laplace transforms, double-check that you subtracted initial condition terms s y(0) and y'(0) with correct negative signs. For PDEs, remember that separation of variables requires linear and homogeneous boundary conditions to establish orthogonal eigenmodes.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1041,
                    questionNumber = 1,
                    question = "What is the integrating factor for the linear first-order ODE dy/dx + (2/x) y = 4x?",
                    options = mapOf("A" to "x²", "B" to "2 ln x", "C" to "e^(2x)", "D" to "x"),
                    correctOption = "A",
                    explanation = "Integrating factor μ(x) = exp(∫ (2/x) dx) = exp(2 ln x) = exp(ln x²) = x²."
                ),
                QuizQuestionDto(
                    id = 1042,
                    questionNumber = 2,
                    question = "What is the Laplace transform L{cos(3t)} in the complex s-domain?",
                    options = mapOf("A" to "s / (s² + 9)", "B" to "3 / (s² + 9)", "C" to "1 / (s - 3)", "D" to "s / (s² - 9)"),
                    correctOption = "A",
                    explanation = "By Laplace transform tables, L{cos(ωt)} = s / (s² + ω²). For ω = 3, this gives s / (s² + 9)."
                ),
                QuizQuestionDto(
                    id = 1043,
                    questionNumber = 3,
                    question = "What is the solution to the initial value problem y'' + 4y = 8, y(0) = 3, y'(0) = 0?",
                    options = mapOf("A" to "y(t) = 2 + cos(2t)", "B" to "y(t) = 3 cos(2t)", "C" to "y(t) = 2 + sin(2t)", "D" to "y(t) = e^(2t) + 2"),
                    correctOption = "A",
                    explanation = "Particular solution y_p = 8/4 = 2. Homogeneous solution y_h = c₁ cos(2t) + c₂ sin(2t). y(0) = 2 + c₁ = 3 ⇒ c₁ = 1. y'(0) = 2c₂ = 0 ⇒ c₂ = 0. Thus y(t) = 2 + cos(2t)."
                ),
                QuizQuestionDto(
                    id = 1044,
                    questionNumber = 4,
                    question = "Which differential equation models physical diffusion and thermal dissipation?",
                    options = mapOf("A" to "∂u/∂t = α ∂²u/∂x² (Heat Equation)", "B" to "∂²u/∂t² = c² ∂²u/∂x² (Wave Equation)", "C" to "∂²u/∂x² + ∂²u/∂y² = 0 (Laplace Equation)", "D" to "dy/dx + y = 0"),
                    correctOption = "A",
                    explanation = "The 1D Heat Equation ∂u/∂t = α ∂²u/∂x² models parabolic diffusion of temperature through a conducting medium."
                ),
                QuizQuestionDto(
                    id = 1045,
                    questionNumber = 5,
                    question = "If the characteristic equation of a 2nd-order ODE has repeated roots r₁ = r₂ = -3, what is the general homogeneous solution?",
                    options = mapOf("A" to "y(t) = (c₁ + c₂ t) e^(-3t)", "B" to "y(t) = c₁ e^(-3t) + c₂ e^(3t)", "C" to "y(t) = c₁ cos(3t) + c₂ sin(3t)", "D" to "y(t) = c₁ e^(-3t)"),
                    correctOption = "A",
                    explanation = "For repeated real roots r, the second linearly independent solution requires multiplying by the independent variable t, yielding y(t) = (c₁ + c₂ t) e^(rt)."
                ),
                QuizQuestionDto(
                    id = 1046,
                    questionNumber = 6,
                    question = "What is the Laplace transform of the derivative y'(t) in terms of Y(s) = L{y(t)}?",
                    options = mapOf("A" to "s Y(s) - y(0)", "B" to "s Y(s)", "C" to "Y(s) / s", "D" to "s² Y(s) - y(0)"),
                    correctOption = "A",
                    explanation = "Using integration by parts on ∫_0^∞ e^(-st) y'(t) dt gives L{y'(t)} = s Y(s) - y(0)."
                ),
                QuizQuestionDto(
                    id = 1047,
                    questionNumber = 7,
                    question = "What is the fundamental requirement for Separation of Variables u(x, t) = X(x)T(t) to succeed on a PDE boundary value problem?",
                    options = mapOf("A" to "The PDE and boundary conditions must be linear and homogeneous", "B" to "The domain must have infinite length", "C" to "The solution must be a static constant", "D" to "The PDE must only contain first-order derivatives"),
                    correctOption = "A",
                    explanation = "Linearity and homogeneous boundary conditions allow establishing independent Sturm-Liouville ODE eigenvalue problems that yield orthogonal basis functions."
                ),
                QuizQuestionDto(
                    id = 1048,
                    questionNumber = 8,
                    question = "In an RLC circuit modeled by L q'' + R q' + (1/C) q = 0, what does the condition R² - 4L/C < 0 represent?",
                    options = mapOf("A" to "Underdamped oscillatory response", "B" to "Critically damped non-oscillatory response", "C" to "Overdamped sluggish response", "D" to "Zero electrical resistance"),
                    correctOption = "A",
                    explanation = "When the discriminant of the characteristic equation is negative, the roots are complex conjugates, producing damped sinusoidal oscillations (underdamping)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH6_001",
                    subject = "Mathematics",
                    chapter = "Ordinary & Partial Differential Equations (ODE & PDE)",
                    topic = "Differential Equations and Laplace Transforms",
                    keywords = listOf("differential equations", "ode", "pde", "laplace transform", "integrating factor", "heat equation", "wave equation", "rlc circuit"),
                    definition = "ODEs involve functions of a single variable and their derivatives; Laplace transforms convert differential equations into algebraic equations in the s-domain.",
                    formulaOrCoreRule = "Integrating Factor: mu(x) = exp(int P(x)dx); Laplace: L{y'} = s Y(s) - y(0); Heat PDE: du/dt = alpha * d^2u/dx^2; Wave PDE: d^2u/dt^2 = c^2 * d^2u/dx^2.",
                    workedExample = "For y'' + 4y = 8 with y(0)=3, y'(0)=0, Laplace transformation gives Y(s) = 2/s + s/(s^2+4), which inverts to y(t) = 2 + cos(2t).",
                    content = "Differential equations govern harmonic oscillators, RLC electrical circuits, heat transfer, electromagnetic wave propagation, and fluid mechanics."
                )
            )
        )
    }

    private fun createChapter7(): CatalogChapter {
        return CatalogChapter(
            chapterId = 7,
            chapterOrder = 7,
            title = "Probability Theory & Random Variables",
            estimatedMinutes = 25,
            summary = "Probability axioms, Bayes' theorem, discrete/continuous distributions (Binomial, Poisson, Normal), expectation, variance, and Central Limit Theorem.",
            syllabus = listOf("Sample Spaces, Axioms of Probability & Conditional Probability", "Law of Total Probability & Bayes' Theorem Inversion", "Discrete Random Variables: PMF, Binomial & Poisson Distributions", "Continuous Random Variables: PDF, Normal & Exponential Distributions", "Expectation, Variance, Covariance & The Central Limit Theorem (CLT)"),
            coreFormulas = listOf("Bayes' Theorem: P(A|B) = [P(B|A) * P(A)] / P(B)", "Law of Total Probability: P(B) = ∑ P(B|A_i) * P(A_i)", "Binomial PMF: P(X = k) = (n choose k) * p^k * (1 - p)^(n - k)", "Standard Normal PDF: f(x) = (1 / (σ √(2π))) * exp(- (x - μ)² / (2σ²))", "Central Limit Theorem: Z = (X̄ - μ) / (σ / √n) → N(0, 1) as n → ∞"),
            part1Title = "Probability Axioms, Bayesian Inference & Asymptotic Distributions",
            part1Content = "Probability theory provides the rigorous mathematical apparatus for reasoning under uncertainty. Andrei Kolmogorov formalized modern probability through measure theory over sample space Ω, sigma-algebra of events F, and probability measure P satisfying three non-negotiable axioms: non-negativity P(E) ≥ 0, unit measure of certainty P(Ω) = 1, and countable additivity for mutually exclusive events. Conditional probability P(A|B) = P(A ∩ B) / P(B) quantifies the updated likelihood of event A given empirical observation of event B.\n\nBayes' Theorem formalizes rational belief updating by inverting conditional dependencies: P(A|B) = [P(B|A) P(A)] / P(B). Combined with the Law of Total Probability P(B) = ∑ P(B|A_i)P(A_i) over an exhaustive partition of hypotheses, Bayes' theorem transforms a prior probability P(A) into an evidence-adjusted posterior probability P(A|B) through the likelihood ratio P(B|A)/P(B). This theorem is the foundation of statistical machine learning, Bayesian optimization, and decision theory.\n\nRandom variables map probabilistic outcomes to real numbers. Discrete random variables are characterized by a Probability Mass Function (PMF) where ∑ p(x_i) = 1, exemplified by the Binomial distribution (number of successes in n Bernoulli trials) and the Poisson distribution (rare event arrival counts). Continuous random variables are characterized by a Probability Density Function (PDF) f(x) ≥ 0 where probabilities correspond to integrals over intervals P(a ≤ X ≤ b) = ∫_a^b f(x) dx, with the total area ∫_(-∞)^∞ f(x) dx = 1.\n\nThe Normal (Gaussian) distribution N(μ, σ²) is the cornerstone of statistical inference. Its bell-shaped curve arises universally from the Central Limit Theorem (CLT), which states that the standardized sum or average of n independent and identically distributed (i.i.d.) random variables with finite mean μ and variance σ² converges in distribution to the standard normal distribution N(0, 1) as n → ∞, regardless of the underlying distribution of the original population.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Bayes' Rule: P(H|E) = [P(E|H) * P(H)] / P(E)\n• Expected Value: E[X] = ∑ x p(x) for discrete, or ∫ x f(x)dx for continuous\n• Variance: Var(X) = E[X²] - (E[X])²\n• Z-score Transformation: Z = (X - μ) / σ\n• Central Limit Theorem Standard Error: SE = σ / √n\n\nWorked Numerical Example:\nProblem: A disease has prevalence P(D) = 0.01 (1%). A diagnostic test has sensitivity P(+|D) = 0.95 and false-positive rate P(+|D') = 0.05. Find P(D|+), the probability a patient has the disease given a positive test.\nStep 1: Identify given probabilities:\n• Prior: P(D) = 0.01. Complement: P(D') = 0.99.\n• Likelihood: P(+|D) = 0.95.\n• False positive: P(+|D') = 0.05.\nStep 2: Calculate total probability of a positive test P(+):\nP(+) = P(+|D) P(D) + P(+|D') P(D') = (0.95)(0.01) + (0.05)(0.99) = 0.0095 + 0.0495 = 0.0590.\nStep 3: Apply Bayes' Theorem:\nP(D|+) = [P(+|D) * P(D)] / P(+) = 0.0095 / 0.0590 ≈ 0.161 (16.1%).\nConclusion: Despite 95% sensitivity, because the disease is rare, a positive test result only yields a 16.1% posterior probability of disease (a classic Bayesian base-rate fallacy insight).\n\nReal-World Applications:\nProbability theory is ubiquitous in engineering. Machine learning relies on Naive Bayes classifiers, Maximum Likelihood Estimation (MLE), and Markov Chain Monte Carlo (MCMC) sampling. In financial quantitative engineering, the Black-Scholes options pricing model assumes geometric Brownian motion with log-normal distributions. In telecommunications, Poisson queues model internet server request traffic.",
            examTip = "University Exam Tip: Avoid the base-rate fallacy in Bayes' theorem questions: always compute the total probability in the denominator carefully. In Central Limit Theorem problems, divide the population standard deviation by √n to obtain the sample mean's standard error.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1049,
                    questionNumber = 1,
                    question = "A diagnostic test has 95% sensitivity and a 5% false positive rate for a disease with 1% prevalence. What is the probability that a patient testing positive actually has the disease?",
                    options = mapOf("A" to "Approximately 16.1%", "B" to "95.0%", "C" to "50.0%", "D" to "90.0%"),
                    correctOption = "A",
                    explanation = "P(D|+) = (0.95 * 0.01) / [(0.95 * 0.01) + (0.05 * 0.99)] = 0.0095 / 0.0590 ≈ 0.161 (16.1%)."
                ),
                QuizQuestionDto(
                    id = 1050,
                    questionNumber = 2,
                    question = "What is the variance of a discrete random variable X defined as?",
                    options = mapOf("A" to "E[X²] - (E[X])²", "B" to "E[X] - E[X²]", "C" to "(E[X])²", "D" to "E[X] / n"),
                    correctOption = "A",
                    explanation = "By definition, Var(X) = E[(X - E[X])²] = E[X² - 2X E[X] + (E[X])²] = E[X²] - (E[X])²."
                ),
                QuizQuestionDto(
                    id = 1051,
                    questionNumber = 3,
                    question = "What is the primary conclusion of the Central Limit Theorem?",
                    options = mapOf("A" to "The distribution of sample means approaches a normal distribution as sample size n increases", "B" to "All random variables must be normally distributed", "C" to "The sample mean is always equal to the population median", "D" to "Variance approaches infinity for large samples"),
                    correctOption = "A",
                    explanation = "The Central Limit Theorem guarantees that the distribution of standardized sample means converges to N(0, 1) as n → ∞, regardless of the shape of the original population distribution."
                ),
                QuizQuestionDto(
                    id = 1052,
                    questionNumber = 4,
                    question = "For a Binomial distribution with parameters n = 20 and p = 0.4, what are its mean and variance?",
                    options = mapOf("A" to "Mean = 8, Variance = 4.8", "B" to "Mean = 8, Variance = 8", "C" to "Mean = 10, Variance = 5", "D" to "Mean = 4.8, Variance = 8"),
                    correctOption = "A",
                    explanation = "Mean μ = np = 20(0.4) = 8. Variance σ² = np(1 - p) = 20(0.4)(0.6) = 4.8."
                ),
                QuizQuestionDto(
                    id = 1053,
                    questionNumber = 5,
                    question = "If two events A and B are independent with P(A) = 0.3 and P(B) = 0.4, what is P(A ∩ B)?",
                    options = mapOf("A" to "0.12", "B" to "0.70", "C" to "0.10", "D" to "0.00"),
                    correctOption = "A",
                    explanation = "By definition of independent events, P(A ∩ B) = P(A) * P(B) = 0.3 * 0.4 = 0.12."
                ),
                QuizQuestionDto(
                    id = 1054,
                    questionNumber = 6,
                    question = "What is the value of the total area under any valid continuous probability density function f(x)?",
                    options = mapOf("A" to "Exactly 1.0", "B" to "Infinity", "C" to "0.5", "D" to "Equal to the variance"),
                    correctOption = "A",
                    explanation = "The second Kolmogorov axiom requires the total probability across the entire support of a continuous random variable to satisfy ∫_(-∞)^∞ f(x) dx = 1."
                ),
                QuizQuestionDto(
                    id = 1055,
                    questionNumber = 7,
                    question = "Which distribution models the number of rare events occurring in a fixed interval of time with constant average rate λ?",
                    options = mapOf("A" to "Poisson distribution", "B" to "Uniform distribution", "C" to "Binomial distribution with p = 0.5", "D" to "Bernoulli distribution"),
                    correctOption = "A",
                    explanation = "The Poisson distribution P(X = k) = (λ^k e^(-λ)) / k! models arrival counts of rare, independent events over continuous intervals."
                ),
                QuizQuestionDto(
                    id = 1056,
                    questionNumber = 8,
                    question = "What is the standard error of the mean for a sample of size n = 64 drawn from a population with standard deviation σ = 16?",
                    options = mapOf("A" to "2", "B" to "0.25", "C" to "4", "D" to "8"),
                    correctOption = "A",
                    explanation = "Standard error SE = σ / √n = 16 / √64 = 16 / 8 = 2."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH7_001",
                    subject = "Mathematics",
                    chapter = "Probability Theory & Random Variables",
                    topic = "Bayes' Theorem, Random Variables and Central Limit Theorem",
                    keywords = listOf("probability", "bayes theorem", "conditional probability", "random variables", "normal distribution", "poisson", "central limit theorem"),
                    definition = "Bayes' Theorem updates the probability of a hypothesis given evidence: P(A|B) = [P(B|A) P(A)] / P(B). The CLT guarantees that sample averages converge to normality.",
                    formulaOrCoreRule = "P(A|B) = [P(B|A) * P(A)] / P(B); Binomial: P(k) = (n choose k) p^k (1-p)^(n-k); CLT: Z = (X_bar - mu)/(sigma / sqrt(n)).",
                    workedExample = "For 1% disease prevalence with 95% sensitivity and 5% false positive rate, P(D|+) = (0.95*0.01)/[(0.95*0.01)+(0.05*0.99)] = 16.1%.",
                    content = "Probability theory underpins machine learning classifiers, risk quantification in algorithmic trading, and network traffic modeling."
                )
            )
        )
    }

    private fun createChapter8(): CatalogChapter {
        return CatalogChapter(
            chapterId = 8,
            chapterOrder = 8,
            title = "Discrete Mathematics & Graph Theory",
            estimatedMinutes = 25,
            summary = "Propositional logic, mathematical induction, combinatorics, recurrence relations, graph connectivity, trees, Euler/Hamilton paths, and Dijkstra's algorithm.",
            syllabus = listOf("Propositional & Predicate Logic, Inference Rules & Proof by Induction", "Combinatorics: Permutations, Combinations & Pigeonhole Principle", "Recurrence Relations: Master Theorem & Characteristic Roots", "Graph Theory Fundamentals: Adjacency, Degrees & Handshaking Lemma", "Trees, Spanning Trees & Dijkstra's Shortest Path Algorithm"),
            coreFormulas = listOf("Handshaking Lemma: ∑_{v ∈ V} deg(v) = 2|E|", "Euler's Planar Formula: V - E + F = 2", "Combinations: C(n, k) = n! / (k! * (n - k)!)", "Master Theorem: T(n) = a T(n/b) + O(n^d) (Compares log_b(a) with d)", "Dijkstra Relaxation: If d[u] + w(u, v) < d[v], then d[v] = d[u] + w(u, v)"),
            part1Title = "Logic, Combinatorics, Recurrences & Graph Algorithms",
            part1Content = "Discrete mathematics provides the structural foundation for algorithmic computer science and computational complexity. Propositional logic formalizes declarative statements via connectives (conjunction, disjunction, implication, negation), verified by truth tables and inference rules such as Modus Ponens [(p → q) ∧ p ⊢ q]. Mathematical induction establishes universal truth across all natural numbers through two non-negotiable steps: establishing a base case P(0) and proving that P(k) implies P(k+1).\n\nCombinatorial analysis quantifies the cardinality of finite configurations. The Pigeonhole Principle dictates that if n items are distributed into k containers with n > k, at least one container must hold at least ⌈n/k⌉ items. Recurrence relations describe sequences defined recursively by previous terms, ubiquitous in divide-and-conquer algorithm analysis. Linear homogeneous recurrences are resolved via roots of characteristic algebraic polynomials, while divide-and-conquer recurrences T(n) = aT(n/b) + f(n) are categorized by the Master Theorem.\n\nGraph theory models pairwise relational networks between entities. An undirected graph G = (V, E) consists of vertices V and edges E. The degree deg(v) counts incident edges; the fundamental Handshaking Lemma establishes that the sum of all vertex degrees equals twice the total number of edges (∑ deg(v) = 2|E|), proving that any graph must have an even number of odd-degree vertices. Planar graphs, which can be embedded in the plane without crossing edges, strictly obey Euler's formula V - E + F = 2.\n\nTrees are connected acyclic graphs where any two vertices are connected by exactly one simple path, possessing exactly |V| - 1 edges. Spanning trees connect all vertices with minimal edges. Shortest path problems on weighted graphs are solved by Dijkstra's algorithm, which greedily relaxes vertex distances using a priority queue, guaranteeing optimal shortest path distances in O((|V| + |E|) log |V|) time when all edge weights are non-negative.",
            part2Title = "Analytical Formulations, Worked Examples & Applications",
            part2Content = "Exact Equations & Governing Rules:\n• Handshaking Lemma: ∑ deg(v) = 2|E|\n• Master Theorem: If a > b^d, T(n) = O(n^(log_b a)); If a = b^d, T(n) = O(n^d log n); If a < b^d, T(n) = O(n^d)\n• Dijkstra Relaxation Step: d[v] = min(d[v], d[u] + w(u, v))\n• Permutations with Repetition: n! / (n₁! n₂! ... n_k!)\n\nWorked Numerical Example:\nProblem: Solve the recurrence relation a_n = 5a_(n-1) - 6a_(n-2) with a_0 = 1, a_1 = 4, and find the number of edges in a graph with 6 vertices of degree 3.\nStep 1: For the recurrence a_n - 5a_(n-1) + 6a_(n-2) = 0, form the characteristic equation: r² - 5r + 6 = 0.\nStep 2: Factor: (r - 2)(r - 3) = 0 ⇒ Roots r₁ = 2, r₂ = 3.\nStep 3: General solution: a_n = c₁(2^n) + c₂(3^n).\nStep 4: Use initial conditions:\n• a_0 = 1 ⇒ c₁ + c₂ = 1\n• a_1 = 4 ⇒ 2c₁ + 3c₂ = 4\n• Multiply first equation by 2: 2c₁ + 2c₂ = 2. Subtract: c₂ = 2. Then c₁ = 1 - 2 = -1.\nStep 5: Closed form: a_n = -(2^n) + 2(3^n).\nStep 6: For the graph: 6 vertices, each of degree 3. Sum of degrees = 6 × 3 = 18.\nStep 7: By Handshaking Lemma: ∑ deg(v) = 2|E| ⇒ 18 = 2|E| ⇒ |E| = 9 edges.\n\nReal-World Applications:\nDiscrete mathematics and graph theory form the foundation of computer science. Internet routing protocols (OSPF, IS-IS) run Dijkstra's algorithm to compute shortest paths across network switches. Social networks use graph centrality to identify key influencers. Compiler optimization relies on Abstract Syntax Trees (ASTs) and graph coloring for CPU register allocation.",
            examTip = "University Exam Tip: In any undirected graph, the number of odd-degree vertices is always even—this is a classic proof question based on the Handshaking Lemma. In Dijkstra's algorithm, remember that edge weights must be strictly non-negative; negative weights require the Bellman-Ford algorithm.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 1057,
                    questionNumber = 1,
                    question = "In an undirected graph with 8 vertices where each vertex has degree 4, how many edges does the graph have?",
                    options = mapOf("A" to "16", "B" to "32", "C" to "8", "D" to "12"),
                    correctOption = "A",
                    explanation = "By the Handshaking Lemma, ∑ deg(v) = 2|E|. Here, 8 × 4 = 32 = 2|E|, so |E| = 16."
                ),
                QuizQuestionDto(
                    id = 1058,
                    questionNumber = 2,
                    question = "What is the closed form solution to the recurrence a_n = 5a_(n-1) - 6a_(n-2) with a_0 = 1, a_1 = 4?",
                    options = mapOf("A" to "a_n = 2(3^n) - 2^n", "B" to "a_n = 3^n + 2^n", "C" to "a_n = 4(2^n) - 3(3^n)", "D" to "a_n = 2^n - 3^n"),
                    correctOption = "A",
                    explanation = "The characteristic roots are r = 2, 3. Solving c₁ + c₂ = 1 and 2c₁ + 3c₂ = 4 gives c₁ = -1, c₂ = 2. Thus a_n = 2(3^n) - 2^n."
                ),
                QuizQuestionDto(
                    id = 1059,
                    questionNumber = 3,
                    question = "What is Euler's formula relating vertices V, edges E, and faces F in any connected planar graph?",
                    options = mapOf("A" to "V - E + F = 2", "B" to "V + E - F = 2", "C" to "V - E - F = 0", "D" to "V + E + F = 1"),
                    correctOption = "A",
                    explanation = "Euler's planar graph formula states that V - E + F = 2 for any connected planar graph."
                ),
                QuizQuestionDto(
                    id = 1060,
                    questionNumber = 4,
                    question = "How many edges does a tree with n vertices have?",
                    options = mapOf("A" to "n - 1", "B" to "n", "C" to "n + 1", "D" to "2n"),
                    correctOption = "A",
                    explanation = "By the structural definition of a tree (a connected acyclic graph), a tree with n vertices always has exactly n - 1 edges."
                ),
                QuizQuestionDto(
                    id = 1061,
                    questionNumber = 5,
                    question = "What limitation exists for Dijkstra's shortest path algorithm?",
                    options = mapOf("A" to "Edge weights must be non-negative", "B" to "The graph must be a tree", "C" to "The graph cannot have more than 10 vertices", "D" to "It can only be used on directed graphs"),
                    correctOption = "A",
                    explanation = "Dijkstra's greedy choice assumes that adding an edge never decreases the path distance. Negative weights violate this assumption, requiring the Bellman-Ford algorithm."
                ),
                QuizQuestionDto(
                    id = 1062,
                    questionNumber = 6,
                    question = "According to the Pigeonhole Principle, if 13 pigeons occupy 12 pigeonholes, what is guaranteed?",
                    options = mapOf("A" to "At least one pigeonhole contains 2 or more pigeons", "B" to "Every pigeonhole contains at least 1 pigeon", "C" to "Exactly one pigeonhole is empty", "D" to "All pigeons are in the same pigeonhole"),
                    correctOption = "A",
                    explanation = "If n items are placed in k containers and n > k, at least one container must contain at least ⌈n/k⌉ = ⌈13/12⌉ = 2 items."
                ),
                QuizQuestionDto(
                    id = 1063,
                    questionNumber = 7,
                    question = "What is the time complexity of the Master Theorem case T(n) = 2T(n/2) + O(n)?",
                    options = mapOf("A" to "O(n log n)", "B" to "O(n)", "C" to "O(n²)", "D" to "O(log n)"),
                    correctOption = "A",
                    explanation = "Here a = 2, b = 2, and d = 1. Since log_b(a) = log₂(2) = 1 = d, the Master Theorem gives T(n) = O(n^d log n) = O(n log n) (Merge Sort recurrence)."
                ),
                QuizQuestionDto(
                    id = 1064,
                    questionNumber = 8,
                    question = "Which logical statement is the contrapositive of the conditional implication p → q?",
                    options = mapOf("A" to "¬q → ¬p", "B" to "q → p", "C" to "¬p → ¬q", "D" to "p ∧ ¬q"),
                    correctOption = "A",
                    explanation = "The contrapositive of p → q is ¬q → ¬p. A conditional statement and its contrapositive are logically equivalent (they have identical truth values)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "MATH_CH8_001",
                    subject = "Mathematics",
                    chapter = "Discrete Mathematics & Graph Theory",
                    topic = "Graph Theory, Trees and Dijkstra's Algorithm",
                    keywords = listOf("discrete mathematics", "graph theory", "trees", "dijkstra", "handshaking lemma", "euler formula", "recurrence", "combinatorics"),
                    definition = "Graph theory studies mathematical structures modeling pairwise relations between objects; Dijkstra's algorithm finds single-source shortest paths in weighted graphs.",
                    formulaOrCoreRule = "Handshaking: sum deg(v) = 2|E|; Planar Euler: V - E + F = 2; Tree edges: |E| = |V| - 1; Dijkstra: d[v] = min(d[v], d[u] + w(u, v)).",
                    workedExample = "For a graph with 6 vertices of degree 3, sum deg(v) = 18 = 2|E| => |E| = 9 edges.",
                    content = "Graph theory and discrete math underpin internet routing protocols (OSPF, BGP), social network graph analysis, and compiler syntax trees."
                )
            )
        )
    }
}
