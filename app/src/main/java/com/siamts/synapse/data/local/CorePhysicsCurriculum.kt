package com.siamts.synapse.data.local

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Embedded university-grade curriculum for Physics.
 * 8 progressive college chapters with complete lessons, MCQs, and RAG knowledge chunks.
 */
object CorePhysicsCurriculum {

    fun getPhysicsSubject(): CatalogSubject {
        return CatalogSubject(
            id = "PHY",
            subjectId = 5,
            name = "Physics",
            slug = "physics",
            iconSymbol = "⚛",
            colorHex = "#F59E0B",
            category = "Physical Sciences",
            description = "University physics covering kinematics, rotational dynamics, gravitation, thermodynamics, electrodynamics, Maxwell equations, and quantum optics.",
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
            title = "Units, Vectors & Kinematics",
            estimatedMinutes = 25,
            summary = "SI dimensional analysis, vector dot and cross products, 1D/2D kinematics, projectile motion with air resistance, and relative frames of reference.",
            syllabus = listOf("SI Base Units, Dimensional Analysis & Buckingham Pi Theorem", "Vector Algebra: Dot Products, Cross Products & Triple Products", "1D Kinematics: Calculus of Velocity, Acceleration & Jerk", "2D Projectile Motion: Parabolic Trajectories & Drag Equations", "Galilean Transformations & Relative Velocity Vectors"),
            coreFormulas = listOf("Kinematic Velocity: v(t) = v_0 + a*t = dx/dt", "Kinematic Displacement: x(t) = x_0 + v_0*t + 0.5*a*t^2", "Kinematic Energy Equation: v^2 = v_0^2 + 2*a*(x - x_0)", "Vector Dot Product: A . B = |A||B|*cos(theta) = A_x*B_x + A_y*B_y + A_z*B_z", "Vector Cross Product: |A x B| = |A||B|*sin(theta)*n_hat", "Projectile Range (Vacuum): R = (v_0^2 * sin(2*theta)) / g"),
            part1Title = "Dimensional Analysis, Vector Calculus & Kinematic Equations",
            part1Content = "Classical mechanics begins with the precise quantitative description of physical dimensions and coordinate frames. Under the Système International (SI), seven fundamental base units (length [L], mass [M], time [T], electric current [I], thermodynamic temperature [Theta], amount of substance [N], and luminous intensity [J]) span all physical observables. Dimensional analysis, formalized by the Buckingham Pi Theorem, ensures physical equations maintain dimensional homogeneity and enables scale modeling in fluid and aeronautical engineering.\n\nPhysical quantities are classified as scalars or vectors. In Euclidean three-space R^3, vectors obey vector space axioms. The scalar dot product A . B = |A||B|cos(theta) computes projections and work, vanishing for orthogonal vectors. The vector cross product A x B = |A||B|sin(theta) n_hat generates an axial vector perpendicular to the plane of operands, fundamental to torque, angular momentum, and Lorentz magnetic forces. Vector components transform covariantly under spatial coordinate rotations.\n\nKinematics characterizes the geometry of motion without reference to the forces generating it. Position r(t) is a continuous vector function of time. Its first time derivative yields instantaneous velocity v(t) = dr/dt (tangent to the trajectory), and its second derivative yields instantaneous acceleration a(t) = dv/dt = d^2r/dt^2. For constant acceleration, integration yields the classical Galileo-Newton kinematic equations.\n\nTwo-dimensional projectile motion decomposes along orthogonal Cartesian coordinates: uniform horizontal motion with zero acceleration (in vacuum) and uniformly accelerated vertical motion governed by gravitational acceleration g. When aerodynamic quadratic drag F_d = 0.5*C_d*rho*A*v^2 is introduced, the trajectory loses parabolic symmetry, asymptotically approaching a terminal velocity v_t = sqrt(2*m*g / (C_d*rho*A)).",
            part2Title = "Kinematic Formulations, Worked Trajectory Problem & Applications",
            part2Content = "Exact Equations & Trajectory Metrics:\n• Range in vacuum: R = (v_0^2 * sin(2*theta)) / g\n• Maximum height: H = (v_0^2 * sin^2(theta)) / (2*g)\n• Time of flight: T = (2*v_0*sin(theta)) / g\n• Dot product: A . B = A_x*B_x + A_y*B_y + A_z*B_z\n• Cross product: A x B = (A_y*B_z - A_z*B_y)i - (A_x*B_z - A_z*B_x)j + (A_x*B_y - A_y*B_x)k\n\nWorked Kinematics Example:\nProblem: A projectile is launched from ground level with initial speed v_0 = 40 m/s at an angle theta = 30 degrees above horizontal (g = 9.8 m/s^2). Calculate: (a) Time of flight T, (b) Maximum altitude H, and (c) Horizontal range R.\nStep 1: Compute initial velocity components:\nv_{0x} = v_0 * cos(30°) = 40 * (sqrt(3)/2) = 34.64 m/s.\nv_{0y} = v_0 * sin(30°) = 40 * 0.5 = 20.0 m/s.\nStep 2: Time of flight T:\nT = (2 * v_{0y}) / g = (2 * 20.0) / 9.8 = 40.0 / 9.8 = 4.08 seconds.\nStep 3: Maximum height H:\nH = v_{0y}^2 / (2 * g) = (20.0)^2 / (2 * 9.8) = 400.0 / 19.6 = 20.41 meters.\nStep 4: Horizontal range R:\nR = v_{0x} * T = 34.64 * 4.08 = 141.33 meters.\nAlternatively: R = (v_0^2 * sin(60°)) / g = (1600 * 0.8660) / 9.8 = 1385.64 / 9.8 = 141.39 meters.\n\nReal-World Applications:\nIn aerospace telemetry, satellite launch trajectories optimize orbital insertion vectors via multi-stage thrust integration. In autonomous robotics, Kalman filters fuse accelerometer and gyroscope measurements using kinematic state-transition matrices to maintain high-accuracy dead reckoning.",
            examTip = "University Exam Tip: For maximum horizontal range on flat ground in vacuum, theta = 45 degrees. On inclined planes, theta_opt = 45° + (beta/2) where beta is slope inclination. In viva exams, prove that the dot product is invariant under coordinate rotation.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5001,
                    questionNumber = 1,
                    question = "Which of the following pairs of vectors A and B are orthogonal (perpendicular)?",
                    options = mapOf("A" to "A = 2i + 3j - 4k, B = 4i - 4j - k", "B" to "A = i + j + k, B = 2i + 2j + 2k", "C" to "A = 3i - 2j, B = 2i + 3j + 4k", "D" to "A = 5i, B = 5i"),
                    correctOption = "A",
                    explanation = "A . B = (2*4) + (3*-4) + (-4*-1) = 8 - 12 + 4 = 0. A dot product of zero indicates mutual orthogonality."
                ),
                QuizQuestionDto(
                    id = 5002,
                    questionNumber = 2,
                    question = "At what launch angle above the horizontal is the range of an ideal projectile maximized on flat ground?",
                    options = mapOf("A" to "45 degrees", "B" to "30 degrees", "C" to "60 degrees", "D" to "90 degrees"),
                    correctOption = "A",
                    explanation = "Range R = (v_0^2 * sin(2*theta)) / g. The term sin(2*theta) achieves its maximum value of 1.0 when 2*theta = 90 degrees, hence theta = 45 degrees."
                ),
                QuizQuestionDto(
                    id = 5003,
                    questionNumber = 3,
                    question = "What is the physical meaning of the third time derivative of position, d^3r/dt^3?",
                    options = mapOf("A" to "Jerk (time rate of change of acceleration)", "B" to "Momentum", "C" to "Work", "D" to "Impulse"),
                    correctOption = "A",
                    explanation = "The first derivative of position is velocity, the second is acceleration, and the third derivative is jerk (da/dt), which dictates mechanical stress and ride comfort in transit systems."
                ),
                QuizQuestionDto(
                    id = 5004,
                    questionNumber = 4,
                    question = "A vehicle decelerates uniformly from 25 m/s to rest over a distance of 50 m. What is its acceleration?",
                    options = mapOf("A" to "-6.25 m/s^2", "B" to "-12.5 m/s^2", "C" to "-3.125 m/s^2", "D" to "-5.0 m/s^2"),
                    correctOption = "A",
                    explanation = "Using v^2 = v_0^2 + 2*a*s => 0 = 25^2 + 2*a*50 => 0 = 625 + 100*a => a = -625 / 100 = -6.25 m/s^2."
                ),
                QuizQuestionDto(
                    id = 5005,
                    questionNumber = 5,
                    question = "Under the Buckingham Pi Theorem, if a physical system has n physical variables and m fundamental dimensions, how many dimensionless Pi groups describe the system?",
                    options = mapOf("A" to "n - m", "B" to "n + m", "C" to "n * m", "D" to "n / m"),
                    correctOption = "A",
                    explanation = "The Buckingham Pi theorem states that a dimensionally homogeneous equation involving n variables with m fundamental dimensions can be reduced to a relationship among p = n - m independent dimensionless parameters."
                ),
                QuizQuestionDto(
                    id = 5006,
                    questionNumber = 6,
                    question = "What is the cross product of unit vectors i and j in a standard right-handed Cartesian coordinate system?",
                    options = mapOf("A" to "k", "B" to "-k", "C" to "0", "D" to "1"),
                    correctOption = "A",
                    explanation = "By definition of the right-handed Cartesian basis: i x j = k, j x k = i, and k x i = j."
                ),
                QuizQuestionDto(
                    id = 5007,
                    questionNumber = 7,
                    question = "In the presence of quadratic air drag F_d = -b*v^2, how does terminal velocity v_t scale with object mass m?",
                    options = mapOf("A" to "v_t is proportional to sqrt(m)", "B" to "v_t is proportional to m^2", "C" to "v_t is inversely proportional to m", "D" to "v_t is independent of mass"),
                    correctOption = "A",
                    explanation = "At terminal velocity, drag balances weight: b*v_t^2 = m*g => v_t = sqrt(m*g / b), showing that terminal speed is proportional to the square root of mass."
                ),
                QuizQuestionDto(
                    id = 5008,
                    questionNumber = 8,
                    question = "Under Galilean relativity, if train A moves east at 80 km/h and train B moves east at 110 km/h on parallel tracks, what is the velocity of train B relative to train A?",
                    options = mapOf("A" to "+30 km/h east", "B" to "-30 km/h west", "C" to "+190 km/h east", "D" to "+95 km/h east"),
                    correctOption = "A",
                    explanation = "Relative velocity v_{B/A} = v_B - v_A = 110 km/h - 80 km/h = +30 km/h directed east."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH1_001",
                    subject = "Physics",
                    chapter = "Units, Vectors & Kinematics",
                    topic = "Vector Algebra, Dimensional Analysis and Kinematic Equations",
                    keywords = listOf("physics", "kinematics", "vectors", "cross product", "dot product", "projectile motion", "terminal velocity", "dimensional analysis"),
                    definition = "Kinematics describes motion through position, velocity, and acceleration vectors. Vector dot products model work, while cross products model rotational torque and Lorentz forces.",
                    formulaOrCoreRule = "v(t) = v_0 + at. x(t) = x_0 + v_0*t + 0.5*a*t^2. R = (v_0^2 * sin(2*theta))/g. A . B = |A||B|cos(theta).",
                    workedExample = "For v_0=40 m/s at theta=30 deg: v_{0y}=20 m/s, time of flight T = 2(20)/9.8 = 4.08 s, max altitude H = 20^2/(2*9.8) = 20.41 m.",
                    content = "Buckingham Pi theorem reduces physical equations to dimensionless ratios. Projectile range in vacuum is maximized at 45 degrees, whereas air drag induces asymmetry and terminal velocity limits."
                )
            )
        )
    }

    private fun createChapter2(): CatalogChapter {
        return CatalogChapter(
            chapterId = 2,
            chapterOrder = 2,
            title = "Newton's Laws & Rotational Mechanics",
            estimatedMinutes = 30,
            summary = "Newtonian dynamics, friction models, rotational inertia tensors, torque, conservation of angular momentum, and gyroscope precession.",
            syllabus = listOf("Newton's Three Laws of Motion & Galilean Invariance", "Friction Mechanics: Static, Kinetic & Viscous Damping", "Rotational Dynamics: Moment of Inertia & Parallel Axis Theorem", "Torque & Angular Momentum: tau = I*alpha & L = I*omega", "Gyroscopic Precession & Rotating Reference Frames (Coriolis Effect)"),
            coreFormulas = listOf("Newton's Second Law: F_net = dp/dt = m*a (for constant mass)", "Coulomb Friction: F_s <= mu_s * N, F_k = mu_k * N", "Moment of Inertia: I = Sum(m_i * r_i^2) = Integral(r^2 dm)", "Parallel Axis Theorem: I = I_cm + M * d^2", "Rotational Kinetic Energy: K_rot = 0.5 * I * omega^2", "Gyroscopic Precession Angular Velocity: Omega_p = tau / L = (M*g*r) / (I*omega)"),
            part1Title = "Newtonian Dynamics, Rotational Inertia & Conservation Laws",
            part1Content = "Newtonian dynamics establishes the foundational relationship between applied forces and the resulting change in momentum of physical bodies. Newton's First Law defines inertial reference frames where free particles maintain constant velocity. The Second Law asserts that the net external force equals the time rate of change of linear momentum, F_net = dp/dt = m*a. The Third Law enforces momentum conservation: forces occur in equal and opposite interaction pairs acting on distinct bodies.\n\nFrictional forces arise from microscopic asperities and molecular bonding across contact interfaces. Under Coulomb's empirical friction laws, static friction F_s matches applied tangential loads up to a threshold F_{s,max} = mu_s * N, where mu_s is the static coefficient and N is the normal force. Once relative sliding initiates, kinetic friction F_k = mu_k * N opposes motion, with mu_k typically less than mu_s.\n\nRotational mechanics maps linear translational variables to rotational analogs: linear displacement maps to angle theta, velocity to angular velocity omega = dtheta/dt, and mass to moment of inertia I. The moment of inertia quantifies an object's resistance to angular acceleration about an axis, defined as I = Integral(r^2 dm). The Parallel Axis (Steiner's) Theorem enables rapid computation of rotational inertia about any parallel axis displaced by distance d: I = I_cm + M*d^2.\n\nRotational dynamics is governed by torque tau = r x F = I*alpha, where alpha = domega/dt is angular acceleration. In closed isolated systems with zero net external torque, total angular momentum L = I*omega is strictly conserved. In rotating non-inertial reference frames, fictitious inertial forces emerge: the outward centrifugal force F_cf = -m*(omega x (omega x r)) and the velocity-dependent Coriolis force F_cor = -2m*(omega x v), which drives cyclonic weather patterns across the Earth.",
            part2Title = "Rotational Formulations, Worked Rolling Cylinder & Gyroscopes",
            part2Content = "Exact Equations & Dynamic Relations:\n• Torque: tau = r x F, |tau| = r * F * sin(phi)\n• Angular momentum: L = r x p = I * omega\n• Parallel Axis Theorem: I = I_cm + M*d^2\n• Rolling without slipping: v_cm = R * omega, a_cm = R * alpha\n• Total rolling kinetic energy: K_total = 0.5*M*v_cm^2 + 0.5*I_cm*omega^2\n\nWorked Rolling Cylinder Example:\nProblem: A solid uniform cylinder of mass M and radius R (I_cm = 0.5*M*R^2) rolls down an incline of angle theta without slipping. Determine the linear acceleration a_cm of the cylinder.\nStep 1: Write force equations along incline:\nM*g*sin(theta) - F_friction = M * a_cm.\nStep 2: Write torque equation about center of mass:\ntau = F_friction * R = I_cm * alpha.\nStep 3: Apply no-slip constraint: alpha = a_cm / R:\nF_friction * R = (0.5 * M * R^2) * (a_cm / R) => F_friction = 0.5 * M * a_cm.\nStep 4: Substitute F_friction into force equation:\nM*g*sin(theta) - 0.5*M*a_cm = M*a_cm => M*g*sin(theta) = 1.5*M*a_cm.\nStep 5: Solve for a_cm:\na_cm = (g * sin(theta)) / 1.5 = (2/3) * g * sin(theta).\nAnalysis: A solid cylinder accelerates at 0.67*g*sin(theta), faster than a hollow hoop (0.5*g*sin(theta)) because a smaller fraction of potential energy is partitioned into rotational inertia.\n\nReal-World Applications:\nIn mechanical powertrains, flywheels store kinetic energy and smooth cyclical engine torque spikes. In spacecraft attitude control, Control Moment Gyroscopes (CMGs) reorient satellites without expending thruster propellant by transferring internal angular momentum.",
            examTip = "University Exam Tip: For any rolling body on an incline without slipping, a_cm = (g * sin(theta)) / (1 + I_cm / (M*R^2)). In viva exams, explain why Coriolis acceleration is zero for particles moving parallel to the axis of rotation.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5009,
                    questionNumber = 1,
                    question = "A solid cylinder (I = 0.5*M*R^2) and a thin hollow ring (I = M*R^2) of identical mass and radius roll down an incline from rest without slipping. Which reaches the bottom first?",
                    options = mapOf("A" to "The solid cylinder", "B" to "The thin hollow ring", "C" to "Both reach the bottom simultaneously", "D" to "The ring, because its center of mass is hollow"),
                    correctOption = "A",
                    explanation = "a_cm = g*sin(theta)/(1 + I/(M*R^2)). For the cylinder, denominator is 1 + 0.5 = 1.5 (a = 2/3 g sin theta). For the ring, denominator is 1 + 1 = 2 (a = 1/2 g sin theta). The cylinder accelerates faster."
                ),
                QuizQuestionDto(
                    id = 5010,
                    questionNumber = 2,
                    question = "What is the moment of inertia of a uniform thin rod of mass M and length L about an axis through one of its endpoints, perpendicular to its length?",
                    options = mapOf("A" to "(1/3) * M * L^2", "B" to "(1/12) * M * L^2", "C" to "(1/2) * M * L^2", "D" to "(2/5) * M * L^2"),
                    correctOption = "A",
                    explanation = "By the Parallel Axis Theorem, I = I_cm + M*(L/2)^2 = (1/12)*M*L^2 + (1/4)*M*L^2 = (4/12)*M*L^2 = (1/3)*M*L^2."
                ),
                QuizQuestionDto(
                    id = 5011,
                    questionNumber = 3,
                    question = "If a spinning ice skater pulls her outstretched arms inward, reducing her rotational inertia by half, what happens to her rotational kinetic energy?",
                    options = mapOf("A" to "It doubles", "B" to "It halves", "C" to "It remains constant", "D" to "It quadruples"),
                    correctOption = "A",
                    explanation = "Angular momentum L is conserved (L = I*omega = constant). Rotational kinetic energy K = L^2 / (2*I). If I is halved, K doubles (the extra energy comes from mechanical work done by the skater's muscles)."
                ),
                QuizQuestionDto(
                    id = 5012,
                    questionNumber = 4,
                    question = "What is the direction of the Coriolis force acting on a projectile launched horizontally northward in the Northern Hemisphere?",
                    options = mapOf("A" to "Eastward (deflected to the right of motion)", "B" to "Westward (deflected to the left of motion)", "C" to "Vertically downward", "D" to "Directly south"),
                    correctOption = "A",
                    explanation = "F_cor = -2m(omega x v). For Earth rotating eastward (counterclockwise viewed from North Pole), omega points out of the North Pole. A northward velocity vector cross product produces an eastward deflection (to the right)."
                ),
                QuizQuestionDto(
                    id = 5013,
                    questionNumber = 5,
                    question = "Under what condition is total angular momentum of a mechanical system strictly conserved?",
                    options = mapOf("A" to "When the net external torque acting on the system is zero", "B" to "When the system has zero linear velocity", "C" to "When friction between components is strictly non-zero", "D" to "When all forces are non-conservative"),
                    correctOption = "A",
                    explanation = "dL/dt = tau_net,ext. When net external torque tau_net,ext = 0, dL/dt = 0, meaning total angular momentum is constant over time."
                ),
                QuizQuestionDto(
                    id = 5014,
                    questionNumber = 6,
                    question = "What is the precession frequency Omega_p of a gyroscope with rotor angular momentum L subject to gravitational torque tau?",
                    options = mapOf("A" to "Omega_p = tau / L", "B" to "Omega_p = tau * L", "C" to "Omega_p = L / tau", "D" to "Omega_p = sqrt(tau / L)"),
                    correctOption = "A",
                    explanation = "Gyroscopic torque tau = dL/dt = Omega_p x L. In magnitude, tau = Omega_p * L, which yields precession angular velocity Omega_p = tau / L."
                ),
                QuizQuestionDto(
                    id = 5015,
                    questionNumber = 7,
                    question = "Why is the coefficient of static friction mu_s generally greater than the coefficient of kinetic friction mu_k?",
                    options = mapOf("A" to "Stationary contact permits micro-weld bonding and deeper interlocking of surface asperities that must be sheared to initiate motion", "B" to "Kinetic friction includes electromagnetic attraction between atoms", "C" to "Normal force decreases automatically once an object starts moving", "D" to "Static friction violates the second law of thermodynamics"),
                    correctOption = "A",
                    explanation = "When stationary, contact junctions have time to establish adhesive chemical bonds and settle into microscopic valleys. Once sliding begins, asperities skip over crests, reducing the average shear force required."
                ),
                QuizQuestionDto(
                    id = 5016,
                    questionNumber = 8,
                    question = "A force F = (4i + 3j) N acts at position r = (2i - 5j) m. What is the torque about the origin?",
                    options = mapOf("A" to "26 k N.m", "B" to "-26 k N.m", "C" to "14 k N.m", "D" to "-7 k N.m"),
                    correctOption = "A",
                    explanation = "tau = r x F = (r_x * F_y - r_y * F_x) k = (2 * 3 - (-5) * 4) k = (6 - (-20)) k = (6 + 20) k = 26 k N.m."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH2_001",
                    subject = "Physics",
                    chapter = "Newton's Laws & Rotational Mechanics",
                    topic = "Rotational Dynamics and Angular Momentum Conservation",
                    keywords = listOf("Newton laws", "torque", "moment of inertia", "parallel axis theorem", "angular momentum", "rolling without slipping", "gyroscope", "coriolis"),
                    definition = "Rotational mechanics governs angular dynamics via torque tau = I*alpha and angular momentum L = I*omega. In the absence of external torque, angular momentum is conserved.",
                    formulaOrCoreRule = "F = dp/dt. tau = r x F = I*alpha. I = I_cm + M*d^2. K_rot = 0.5*I*omega^2. Precession: Omega_p = tau/L.",
                    workedExample = "Solid cylinder rolling without slipping down incline: a_cm = g*sin(theta)/(1 + 0.5) = (2/3)*g*sin(theta).",
                    content = "Parallel axis theorem computes inertia about displaced axes. Non-inertial frames produce Coriolis and centrifugal fictitious forces fundamental to atmospheric and oceanographic dynamics."
                )
            )
        )
    }

    private fun createChapter3(): CatalogChapter {
        return CatalogChapter(
            chapterId = 3,
            chapterOrder = 3,
            title = "Gravitation & Fluid Dynamics",
            estimatedMinutes = 30,
            summary = "Kepler's laws, Newton's universal gravitation, gravitational potential, fluid statics (Pascal, Archimedes), and fluid dynamics (Bernoulli, Navier-Stokes).",
            syllabus = listOf("Kepler's Three Laws of Planetary Motion & Orbital Mechanics", "Newton's Universal Gravitation & Gravitational Potential Wells", "Fluid Statics: Pascal's Principle, Hydrostatic Pressure & Archimedes' Buoyancy", "Fluid Dynamics: Continuity Equation & Bernoulli's Energy Conservation", "Viscous Flow: Poiseuille's Law, Reynolds Number & Navier-Stokes Overview"),
            coreFormulas = listOf("Newton's Law of Gravitation: F = G * (m1 * m2) / r^2", "Gravitational Potential Energy: U(r) = - G * (M * m) / r", "Escape Velocity: v_esc = sqrt(2 * G * M / R)", "Hydrostatic Pressure: P = P_0 + rho * g * h", "Continuity Equation: A1 * v1 = A2 * v2", "Bernoulli's Equation: P + 0.5 * rho * v^2 + rho * g * h = constant", "Reynolds Number: Re = (rho * v * L) / mu"),
            part1Title = "Orbital Gravitation, Fluid Statics & Conservation Laws",
            part1Content = "Newton's Law of Universal Gravitation states that every point mass attracts every other point mass with a force proportional to the product of their masses and inversely proportional to the square of the separation distance: F = G*(m1*m2)/r^2. Kepler's empirical laws of planetary motion derive analytically from this central inverse-square force: planets follow elliptical orbits with the Sun at one focus, equal areas are swept in equal time intervals (angular momentum conservation), and the orbital period squared is proportional to the semi-major axis cubed (T^2 = (4*pi^2 / (G*M))*a^3).\n\nGravitational potential energy U(r) = -G*M*m / r defines an attractive potential well where infinity serves as zero-energy reference. The minimum speed required for a projectile to escape a celestial body's gravitational well is escape velocity v_esc = sqrt(2*G*M / R). Circular orbital speed at radius r equals v_orb = sqrt(G*M / r), demonstrating that orbital velocity decreases with increasing altitude.\n\nFluid statics analyzes fluids in mechanical equilibrium. Hydrostatic pressure increases linearly with depth according to dP/dz = -rho*g. Pascal's Principle dictates that pressure applied to an enclosed incompressible fluid transmits undiminished throughout all fluid boundaries. Archimedes' Principle states that a body submerged in a fluid experiences an upward buoyant force equal to the weight of the displaced fluid, F_b = rho_fluid * V_submerged * g.\n\nFluid dynamics models the transport of mass, momentum, and energy. For incompressible, irrotational, inviscid flow (ideal fluid), mass conservation yields the Continuity Equation A1*v1 = A2*v2. Integrating Euler's momentum equation along a streamline yields Bernoulli's Principle P + 0.5*rho*v^2 + rho*g*h = constant. Viscous fluid friction is parameterized by dynamic viscosity mu and the dimensionless Reynolds Number Re = rho*v*L / mu, which demarcates smooth laminar flow (Re < 2000) from chaotic turbulent regimes (Re > 4000).",
            part2Title = "Bernoulli Formulations, Worked Venturi Tube & Aero Applications",
            part2Content = "Exact Equations & Flow Dynamics:\n• Bernoulli conservation: P1 + 0.5*rho*v1^2 + rho*g*h1 = P2 + 0.5*rho*v2^2 + rho*g*h2\n• Torricelli's Law: v_efflux = sqrt(2 * g * h)\n• Poiseuille Flow: Q = (pi * Delta_P * r^4) / (8 * mu * L)\n• Escape speed: v_esc = sqrt(2*G*M / R)\n\nWorked Venturi Flowmeter Example:\nProblem: Water (rho = 1000 kg/m^3) flows horizontally through a pipe that constricts from diameter D1 = 0.20 m to D2 = 0.10 m. The static pressure difference between wide and constricted sections is Delta_P = P1 - P2 = 4500 Pa. Determine the volumetric flow rate Q.\nStep 1: Compute pipe cross-sectional areas:\nA1 = pi * (D1/2)^2 = pi * (0.10)^2 = 0.03142 m^2.\nA2 = pi * (D2/2)^2 = pi * (0.05)^2 = 0.007854 m^2.\nArea ratio: A1 / A2 = (0.20 / 0.10)^2 = 4.0 => v2 = 4 * v1.\nStep 2: Apply horizontal Bernoulli equation (h1 = h2):\nP1 - P2 = 0.5 * rho * (v2^2 - v1^2).\nStep 3: Substitute v2 = 4*v1:\n4500 = 0.5 * 1000 * ((4*v1)^2 - v1^2) = 500 * (16*v1^2 - v1^2) = 500 * 15 * v1^2 = 7500 * v1^2.\nStep 4: Solve for v1:\nv1^2 = 4500 / 7500 = 0.60 => v1 = sqrt(0.60) = 0.7746 m/s.\nStep 5: Compute volumetric flow rate Q:\nQ = A1 * v1 = 0.03142 * 0.7746 = 0.02434 m^3/s = 24.34 liters/second.\n\nReal-World Applications:\nIn aerospace, Bernoulli pressure gradients and circulation (Kutta-Joukowski theorem) generate aerodynamic lift across aircraft wings. In medicine, Poiseuille's law governs cardiovascular hemodynamics, explaining why a 50% arterial radius reduction increases flow resistance by sixteen-fold (r^4 dependency).",
            examTip = "University Exam Tip: Remember Poiseuille's law r^4 dependency: halving the pipe radius increases fluid resistance by a factor of 16. In viva exams, clearly state the assumptions behind Bernoulli's equation: steady, incompressible, inviscid (non-viscous), and irrotational flow along a streamline.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5017,
                    questionNumber = 1,
                    question = "What happens to the orbital period T of a satellite if its orbital radius r around Earth is increased by a factor of 4?",
                    options = mapOf("A" to "The period increases by a factor of 8", "B" to "The period doubles (factor of 2)", "C" to "The period increases by a factor of 16", "D" to "The period remains unchanged"),
                    correctOption = "A",
                    explanation = "By Kepler's Third Law, T^2 is proportional to r^3 => T is proportional to r^(3/2). If r becomes 4*r, T scales by 4^(3/2) = (sqrt(4))^3 = 2^3 = 8."
                ),
                QuizQuestionDto(
                    id = 5018,
                    questionNumber = 2,
                    question = "What is the escape velocity from Earth's surface relative to circular orbital velocity at the same radius?",
                    options = mapOf("A" to "v_esc = sqrt(2) * v_orb", "B" to "v_esc = 2 * v_orb", "C" to "v_esc = 0.5 * v_orb", "D" to "v_esc = 4 * v_orb"),
                    correctOption = "A",
                    explanation = "v_esc = sqrt(2*G*M/R) and v_orb = sqrt(G*M/R). Therefore, v_esc = sqrt(2) * v_orb approximately 1.414 * v_orb."
                ),
                QuizQuestionDto(
                    id = 5019,
                    questionNumber = 3,
                    question = "According to Poiseuille's Law, how does the volumetric flow rate Q of a viscous fluid change if the vessel radius r is halved, holding pressure drop constant?",
                    options = mapOf("A" to "Q decreases to 1/16 of its original value", "B" to "Q decreases to 1/2 of its original value", "C" to "Q decreases to 1/4 of its original value", "D" to "Q decreases to 1/8 of its original value"),
                    correctOption = "A",
                    explanation = "Poiseuille's Law states Q is proportional to r^4. Halving the radius yields (1/2)^4 = 1/16 of the original volumetric flow rate."
                ),
                QuizQuestionDto(
                    id = 5020,
                    questionNumber = 4,
                    question = "Which dimensionless number determines whether a fluid flow regime is laminar or turbulent?",
                    options = mapOf("A" to "Reynolds Number (Re)", "B" to "Mach Number (Ma)", "C" to "Prandtl Number (Pr)", "D" to "Froude Number (Fr)"),
                    correctOption = "A",
                    explanation = "The Reynolds number Re = (rho * v * L) / mu represents the ratio of inertial forces to viscous forces, dictating transition to turbulence (typically Re > 2300 in pipes)."
                ),
                QuizQuestionDto(
                    id = 5021,
                    questionNumber = 5,
                    question = "Water flows through a constriction in a horizontal pipe. In the constricted section, what happens to velocity and static pressure?",
                    options = mapOf("A" to "Velocity increases and static pressure decreases", "B" to "Velocity decreases and static pressure increases", "C" to "Both velocity and static pressure increase", "D" to "Both velocity and static pressure decrease"),
                    correctOption = "A",
                    explanation = "By the continuity equation (A1*v1 = A2*v2), velocity increases in the smaller area. By Bernoulli's equation (P + 0.5*rho*v^2 = const), an increase in dynamic pressure produces a decrease in static pressure."
                ),
                QuizQuestionDto(
                    id = 5022,
                    questionNumber = 6,
                    question = "An object floats in water with 60% of its volume submerged. What is the density of the object (density of water = 1000 kg/m^3)?",
                    options = mapOf("A" to "600 kg/m^3", "B" to "400 kg/m^3", "C" to "1000 kg/m^3", "D" to "1667 kg/m^3"),
                    correctOption = "A",
                    explanation = "For a floating body, Weight = Buoyant Force: rho_obj * V * g = rho_fluid * (0.60 * V) * g => rho_obj = 0.60 * 1000 = 600 kg/m^3."
                ),
                QuizQuestionDto(
                    id = 5023,
                    questionNumber = 7,
                    question = "What is the speed of efflux of liquid from a small orifice at depth h below the open surface of an open tank (Torricelli's Law)?",
                    options = mapOf("A" to "v = sqrt(2 * g * h)", "B" to "v = 2 * g * h", "C" to "v = sqrt(g * h)", "D" to "v = g * h^2"),
                    correctOption = "A",
                    explanation = "Torricelli's Law, derived from Bernoulli's equation, states that efflux velocity v = sqrt(2*g*h), identical to the speed acquired by a body falling freely from height h."
                ),
                QuizQuestionDto(
                    id = 5024,
                    questionNumber = 8,
                    question = "Why is the gravitational potential energy of two masses separated by distance r defined with a negative sign (U = -G*M*m/r)?",
                    options = mapOf("A" to "Gravity is strictly attractive, requiring positive work to separate the masses to infinity", "B" to "Negative numbers prevent potential energy from violating the second law of thermodynamics", "C" to "Gravitational mass is defined as negative in general relativity", "D" to "Mass particles lose weight as they approach each other"),
                    correctOption = "A",
                    explanation = "With the zero reference chosen at infinite separation (U(infinity) = 0), work must be performed against the attractive gravitational field to pull masses apart, making bound states possess negative potential energy."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH3_001",
                    subject = "Physics",
                    chapter = "Gravitation & Fluid Dynamics",
                    topic = "Universal Gravitation, Bernoulli Principle and Fluid Dynamics",
                    keywords = listOf("gravitation", "Kepler laws", "orbital mechanics", "escape velocity", "fluid dynamics", "Bernoulli", "continuity equation", "Poiseuille", "Reynolds"),
                    definition = "Gravitation governs orbital mechanics via Kepler's laws and inverse-square attraction. Fluid dynamics models mass and energy conservation through the Continuity equation and Bernoulli's principle.",
                    formulaOrCoreRule = "F = G*M*m/r^2. v_esc = sqrt(2GM/R). A1*v1 = A2*v2. P + 0.5*rho*v^2 + rho*g*h = const. Re = rho*v*L/mu.",
                    workedExample = "Venturi tube with D1=0.2m, D2=0.1m, Delta_P=4500 Pa: v2=4*v1, Delta_P = 0.5*1000*(15*v1^2) = 7500*v1^2 => v1=0.775 m/s, Q=0.0243 m^3/s.",
                    content = "Poiseuille's law highlights extreme fourth-power radius sensitivity in viscous flow. Reynolds number demarcates laminar and turbulent flow regimes fundamental to aeronautical wing design."
                )
            )
        )
    }

    private fun createChapter4(): CatalogChapter {
        return CatalogChapter(
            chapterId = 4,
            chapterOrder = 4,
            title = "Waves, Oscillations & Wave Mechanics",
            estimatedMinutes = 30,
            summary = "Simple harmonic motion (SHM), damped/driven oscillators, resonance, wave equation, Doppler effect, and acoustic impedance.",
            syllabus = listOf("Simple Harmonic Motion (SHM): Restoring Forces & Energy Exchange", "Damped Harmonic Oscillations: Underdamped, Overdamped & Critical Damping", "Driven Oscillations, Quality Factor (Q) & Amplitude Resonance", "Classical Wave Equation: Phase Velocity, Dispersion & Superposition", "Doppler Effect for Sound & Acoustic Impedance Across Media"),
            coreFormulas = listOf("SHM Equation of Motion: d^2x/dt^2 + omega_0^2 * x = 0", "Natural Frequency: omega_0 = sqrt(k / m) (spring) or sqrt(g / L) (pendulum)", "Damped Oscillation Equation: d^2x/dt^2 + 2*gamma*(dx/dt) + omega_0^2*x = 0", "Quality Factor: Q = omega_0 / (2 * gamma)", "Classical Wave Equation: d^2y/dx^2 = (1 / v^2) * d^2y/dt^2", "Doppler Shift: f_obs = f_source * ((v +/- v_obs) / (v -/+ v_source))"),
            part1Title = "Harmonic Oscillation, Damping Regimes & Wave Mechanics",
            part1Content = "Oscillatory motion occurs when a physical system displaced from stable equilibrium experiences a restoring force proportional to displacement (Hooke's Law F = -k*x). The resulting Simple Harmonic Motion (SHM) is governed by the second-order linear differential equation d^2x/dt^2 + omega_0^2*x = 0, where omega_0 = sqrt(k/m) is the natural angular frequency. Total mechanical energy E = 0.5*k*A^2 continuously exchanges between kinetic energy and elastic potential energy, remaining constant in lossless systems.\n\nReal physical oscillators experience dissipative drag forces, typically modeled as viscous resistance F_d = -b*(dx/dt). The damped oscillator equation d^2x/dt^2 + 2*gamma*(dx/dt) + omega_0^2*x = 0 (where gamma = b / 2m) yields three distinct physical regimes depending on discriminant Delta = gamma^2 - omega_0^2: Underdamped (gamma < omega_0), exhibiting decaying sinusoidal oscillations with shifted frequency omega_d = sqrt(omega_0^2 - gamma^2); Overdamped (gamma > omega_0), decaying exponentially without oscillation; and Critically Damped (gamma = omega_0), returning to equilibrium in minimal time without overshoot.\n\nWhen subjected to an external periodic driving force F_0*cos(omega*t), the oscillator undergoes driven vibrations. As the driving frequency omega approaches the natural frequency omega_0, the oscillation amplitude spikes sharply, producing mechanical Resonance. The sharpness of the resonance peak is quantified by the dimensionless Quality Factor Q = omega_0 / (2*gamma), representing the ratio of total energy stored to energy dissipated per cycle.\n\nMechanical waves propagate disturbances through elastic media via coupled local oscillations. The 1D wave equation d^2y/dx^2 = (1/v^2)*(d^2y/dt^2) is satisfied by arbitrary traveling waveforms y(x, t) = f(x - v*t). When relative motion exists between an acoustic source and an observer, the observed wave frequency shifts according to the Doppler Effect, compressing wavelengths ahead of approaching sources and elongating wavelengths behind receding sources.",
            part2Title = "Oscillatory Formulations, Worked Damped Q-Factor & Applications",
            part2Content = "Exact Equations & Wave Relations:\n• Wave speed on string: v = sqrt(Tension / mu_mass_density)\n• Doppler formula: f_obs = f_s * (v_sound + v_obs) / (v_sound - v_source) [approaching]\n• Quality Factor: Q = omega_0 / (2*gamma) = omega_0 * m / b\n• Resonance amplitude: A(omega) = (F_0 / m) / sqrt((omega_0^2 - omega^2)^2 + (2*gamma*omega)^2)\n\nWorked Resonance & Damping Example:\nProblem: A mechanical mass-spring suspension has mass m = 2 kg, spring stiffness k = 800 N/m, and damping constant b = 8 N.s/m. Determine: (a) Undamped natural frequency omega_0, (b) Damping factor gamma, (c) Damped frequency omega_d, and (d) Quality factor Q.\nStep 1: Compute natural frequency omega_0:\nomega_0 = sqrt(k / m) = sqrt(800 / 2) = sqrt(400) = 20.0 rad/s.\nStep 2: Compute damping coefficient gamma:\ngamma = b / (2 * m) = 8 / (2 * 2) = 8 / 4 = 2.0 s^(-1).\nStep 3: Determine damping regime:\nSince gamma = 2.0 < omega_0 = 20.0, the system is underdamped.\nStep 4: Damped oscillation frequency omega_d:\nomega_d = sqrt(omega_0^2 - gamma^2) = sqrt(20^2 - 2^2) = sqrt(400 - 4) = sqrt(396) = 19.90 rad/s.\nStep 5: Quality factor Q:\nQ = omega_0 / (2 * gamma) = 20.0 / (2 * 2.0) = 20.0 / 4.0 = 5.0.\n\nReal-World Applications:\nIn civil engineering, tuned mass dampers (such as the 660-ton pendulum suspended in Taipei 101) vibrate in anti-phase to dissipate wind and seismic oscillations. In medical ultrasound imaging, acoustic impedance matching layers eliminate acoustic reflections across the transducer-tissue interface.",
            examTip = "University Exam Tip: Critical damping (gamma = omega_0) returns the system to equilibrium faster than any other regime without oscillatory overshoot, making it ideal for automotive shock absorbers and galvanometer meters. In viva exams, explain why sound waves cannot travel in vacuum (mechanical longitudinal waves require a material medium).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5025,
                    questionNumber = 1,
                    question = "What happens to the natural frequency omega_0 of a simple pendulum if its length L is quadrupled?",
                    options = mapOf("A" to "It is halved (divided by 2)", "B" to "It doubles", "C" to "It is quartered (divided by 4)", "D" to "It remains unchanged"),
                    correctOption = "A",
                    explanation = "omega_0 = sqrt(g / L). If L becomes 4*L, omega_0 = sqrt(g / (4*L)) = 0.5 * sqrt(g / L), halving the angular frequency."
                ),
                QuizQuestionDto(
                    id = 5026,
                    questionNumber = 2,
                    question = "Which damping condition allows an oscillator to return to mechanical equilibrium in the shortest possible time without any oscillatory overshoot?",
                    options = mapOf("A" to "Critical Damping (gamma = omega_0)", "B" to "Underdamping (gamma < omega_0)", "C" to "Overdamping (gamma > omega_0)", "D" to "Zero Damping (gamma = 0)"),
                    correctOption = "A",
                    explanation = "Critical damping represents the boundary between oscillatory behavior and slow exponential decay, returning the displaced system to equilibrium in minimum time without ringing."
                ),
                QuizQuestionDto(
                    id = 5027,
                    questionNumber = 3,
                    question = "An acoustic ambulance siren emits a frequency of 600 Hz while approaching a stationary observer at 34 m/s. If the speed of sound is 340 m/s, what frequency is heard?",
                    options = mapOf("A" to "667 Hz", "B" to "545 Hz", "C" to "600 Hz", "D" to "720 Hz"),
                    correctOption = "A",
                    explanation = "f_obs = f_s * (v / (v - v_s)) = 600 * (340 / (340 - 34)) = 600 * (340 / 306) = 600 * 1.111 = 666.7 Hz (approximately 667 Hz)."
                ),
                QuizQuestionDto(
                    id = 5028,
                    questionNumber = 4,
                    question = "What is the physical interpretation of a high Quality Factor (Q >> 100) in an oscillating system?",
                    options = mapOf("A" to "Extremely low energy dissipation per cycle, resulting in a very sharp, narrow resonance peak", "B" to "Heavy damping that halts all oscillations immediately", "C" to "High non-linear distortion across all harmonics", "D" to "Zero restoring force at the equilibrium point"),
                    correctOption = "A",
                    explanation = "Quality factor Q = 2*pi*(Energy Stored / Energy Lost per cycle). A high Q implies minimal dissipation and high frequency selectivity with a narrow resonant bandwidth."
                ),
                QuizQuestionDto(
                    id = 5029,
                    questionNumber = 5,
                    question = "If the tension in a stretched guitar string is quadrupled while keeping mass per unit length constant, what happens to the wave propagation speed?",
                    options = mapOf("A" to "Wave speed doubles", "B" to "Wave speed quadruples", "C" to "Wave speed is halved", "D" to "Wave speed remains constant"),
                    correctOption = "A",
                    explanation = "v = sqrt(Tension / mu). Quadrupling tension increases wave velocity by sqrt(4) = 2."
                ),
                QuizQuestionDto(
                    id = 5030,
                    questionNumber = 6,
                    question = "In a lossless Simple Harmonic Oscillator with mass m and spring constant k, what is the total mechanical energy in terms of amplitude A?",
                    options = mapOf("A" to "E = 0.5 * k * A^2", "B" to "E = k * A", "C" to "E = 0.5 * m * A^2", "D" to "E = 2 * pi * k * A"),
                    correctOption = "A",
                    explanation = "At maximum displacement x = A, velocity v = 0. Total energy equals potential energy: E = 0.5 * k * A^2."
                ),
                QuizQuestionDto(
                    id = 5031,
                    questionNumber = 7,
                    question = "What mathematical relationship characterizes two sinusoidal waves that exhibit constructive interference?",
                    options = mapOf("A" to "Their phase difference is an even integer multiple of pi (Delta phi = 2*n*pi)", "B" to "Their phase difference is an odd integer multiple of pi (Delta phi = (2n + 1)*pi)", "C" to "Their frequencies must be mutually irrational", "D" to "Their amplitudes must be strictly orthogonal"),
                    correctOption = "A",
                    explanation = "Constructive interference occurs when wave crests align perfectly, requiring phase difference Delta phi = 2*n*pi (path difference Delta x = n * lambda)."
                ),
                QuizQuestionDto(
                    id = 5032,
                    questionNumber = 8,
                    question = "Why does a driven oscillator's steady-state amplitude not grow infinitely at resonance in real physical systems?",
                    options = mapOf("A" to "Inherent damping and viscous dissipation limit maximum peak amplitude", "B" to "Mass becomes infinite at the natural frequency", "C" to "Driving force phase automatically locks to zero", "D" to "Wave speed drops to zero at resonance"),
                    correctOption = "A",
                    explanation = "In real systems, damping parameter gamma > 0 ensures the denominator sqrt((omega_0^2 - omega^2)^2 + (2*gamma*omega)^2) remains non-zero, bounding the peak amplitude to F_0 / (2*m*gamma*omega_0)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH4_001",
                    subject = "Physics",
                    chapter = "Waves, Oscillations & Wave Mechanics",
                    topic = "Waves, Oscillations & Wave Mechanics",
                    keywords = listOf("wave", "waves", "wavelength", "frequency", "amplitude", "transverse", "longitudinal", "sound", "oscillation", "oscillations", "v = f lambda", "harmonic motion", "resonance", "doppler effect", "shm"),
                    definition = "A wave is a disturbance or oscillation that transfers energy through space or a medium without permanent displacement of matter. Wave mechanics formalizes propagation through elastic media, energy transport, and Doppler frequency shifts.",
                    formulaOrCoreRule = "v = f * lambda. T = 1 / f. omega = 2 * pi * f. omega_0 = sqrt(k / m). Wave equation: d^2y/dx^2 = (1 / v^2) * d^2y/dt^2. Doppler shift: f_obs = f_s * ((v +/- v_o) / (v -/+ v_s)).",
                    workedExample = "For concert pitch A4 (f = 440 Hz) in air at 20°C (v = 343 m/s): lambda = 343 / 440 = 0.78 m, period T = 1 / 440 = 2.27 ms. Transverse waves oscillate perpendicularly; longitudinal waves oscillate parallel.",
                    content = "Transverse waves include light and electromagnetic waves. Longitudinal waves include sound waves traveling in air at 343 m/s. Wave velocity depends on medium elasticity and density: v = sqrt(T / mu) on strings or v = sqrt(B / rho) in bulk fluids."
                )
            )
        )
    }

    private fun createChapter5(): CatalogChapter {
        return CatalogChapter(
            chapterId = 5,
            chapterOrder = 5,
            title = "Laws of Thermodynamics & Entropy",
            estimatedMinutes = 30,
            summary = "Zeroth/First/Second/Third Laws, PV work, Carnot heat engine, Otto/Diesel cycles, Clausius inequality, and microscopic Boltzmann entropy.",
            syllabus = listOf("Zeroth & First Laws: Thermal Equilibrium, Internal Energy & PV Work", "Thermodynamic Processes: Isothermal, Adiabatic, Isobaric & Isochoric", "Second Law of Thermodynamics: Kelvin-Planck & Clausius Formulations", "Carnot Engine Cycle, Thermal Efficiency & Reversible Limits", "Entropy Formulations: Clausius Integral & Boltzmann Microscopic Entropy"),
            coreFormulas = listOf("First Law of Thermodynamics: Delta U = Q - W", "PV Work: W = Integral(P dV)", "Ideal Gas Adiabatic Relation: P * V^gamma = constant (where gamma = C_p / C_v)", "Carnot Engine Efficiency: eta_carnot = 1 - (T_cold / T_hot)", "Clausius Inequality: Cyclic_Integral(dQ / T) <= 0", "Boltzmann Entropy Formula: S = k_B * ln(Omega)"),
            part1Title = "Thermodynamic Laws, Cycles & Reversibility Limits",
            part1Content = "Thermodynamics governs energy transformations, heat transfer, and mechanical work across macroscopic physical systems. The Zeroth Law establishes thermal equilibrium as a transitive relation, enabling the definition of temperature. The First Law formalizes conservation of energy: the change in internal energy of a closed system equals net heat added minus work performed by the system, Delta U = Q - W. For an ideal gas, internal energy depends exclusively on temperature (U = C_v * T).\n\nThermodynamic processes alter state variables (Pressure P, Volume V, Temperature T) along defined paths: Isobaric (constant pressure, W = P*Delta_V), Isochoric (constant volume, W = 0, Q = Delta_U), Isothermal (constant temperature, Delta_U = 0, W = n*R*T*ln(V_f/V_i)), and Reversible Adiabatic (zero heat transfer Q = 0, obeying P*V^gamma = constant, where gamma = C_p / C_v is the heat capacity ratio).\n\nThe Second Law of Thermodynamics dictates the directionality of spontaneous processes. The Kelvin-Planck statement asserts that no cyclic heat engine can convert heat entirely into work without rejecting waste heat to a colder reservoir. The Clausius statement affirms that heat cannot spontaneously flow from a colder body to a hotter body without external work input. Nicolas Léonard Sadi Carnot established the upper theoretical efficiency limit for any heat engine operating between temperatures T_hot and T_cold: eta_carnot = 1 - (T_cold / T_hot).\n\nEntropy S is the fundamental state function quantifying thermodynamic irreversibility and microscopic disorder. Under Rudolf Clausius's macroscopic formulation, differential entropy is dS = dQ_rev / T, with total entropy of an isolated system always non-decreasing (Delta S_universe >= 0). Ludwig Boltzmann bridged macroscopic entropy to statistical mechanics via S = k_B * ln(Omega), where k_B is Boltzmann's constant and Omega denotes the number of accessible microscopic configurations (microstates) corresponding to the macroscopic state.",
            part2Title = "Thermodynamic Formulations, Worked Carnot Efficiency & Applications",
            part2Content = "Exact Equations & Cycle Efficiencies:\n• Carnot efficiency: eta = 1 - T_L / T_H (temperatures in Kelvin)\n• Otto cycle efficiency: eta_otto = 1 - 1 / (r^(gamma - 1)) (r = compression ratio)\n• Isothermal work: W = n * R * T * ln(V_2 / V_1)\n• Adiabatic work: W = (P_1*V_1 - P_2*V_2) / (gamma - 1)\n• Entropy change: Delta_S = Integral(dQ_rev / T)\n\nWorked Heat Engine Example:\nProblem: A high-temperature geothermal power plant operates as an ideal Carnot cycle between a steam source at T_H = 327°C and a cooling river at T_C = 27°C. If the plant absorbs Q_H = 500 MJ of heat per cycle, calculate: (a) Carnot thermal efficiency eta, (b) Work output W, and (c) Heat rejected Q_C.\nStep 1: Convert temperatures to absolute Kelvin:\nT_H = 327 + 273.15 = 600.15 K approximately 600 K.\nT_C = 27 + 273.15 = 300.15 K approximately 300 K.\nStep 2: Calculate Carnot efficiency eta:\neta = 1 - (T_C / T_H) = 1 - (300 / 600) = 1 - 0.50 = 0.50 (50%).\nStep 3: Calculate net mechanical work produced:\nW = eta * Q_H = 0.50 * 500 MJ = 250 MJ.\nStep 4: Calculate waste heat rejected to river:\nQ_C = Q_H - W = 500 MJ - 250 MJ = 250 MJ.\nStep 5: Verify Clausius equality for reversible cycle:\nDelta S_cycle = -Q_H / T_H + Q_C / T_C = -500 MJ / 600 K + 250 MJ / 300 K = -0.833 MJ/K + 0.833 MJ/K = 0.\n\nReal-World Applications:\nIn nuclear power generation, secondary steam loops implement Rankine cycles with superheating to approach Carnot efficiency limits. In cryogenic liquefaction (e.g., liquid nitrogen and hydrogen storage), multi-stage Stirling refrigeration cycles extract heat near absolute zero.",
            examTip = "University Exam Tip: Always convert temperatures to Kelvin (K) when evaluating thermodynamic efficiencies; using Celsius yields catastrophic errors. In viva exams, state the Third Law: as temperature approaches absolute zero (T -> 0 K), the entropy of a pure crystalline substance approaches exactly zero.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5033,
                    questionNumber = 1,
                    question = "What is the maximum theoretical efficiency of a heat engine operating between 500 K and 300 K?",
                    options = mapOf("A" to "40%", "B" to "60%", "C" to "20%", "D" to "50%"),
                    correctOption = "A",
                    explanation = "eta = 1 - (T_cold / T_hot) = 1 - (300 / 500) = 1 - 0.60 = 0.40 (40%)."
                ),
                QuizQuestionDto(
                    id = 5034,
                    questionNumber = 2,
                    question = "In an ideal gas undergoing a reversible adiabatic expansion, which quantity is strictly zero?",
                    options = mapOf("A" to "Heat transfer (Q = 0)", "B" to "Work performed (W = 0)", "C" to "Change in temperature (Delta T = 0)", "D" to "Change in internal energy (Delta U = 0)"),
                    correctOption = "A",
                    explanation = "An adiabatic process by definition involves zero heat exchange across system boundaries (Q = 0). By the First Law, Delta U = -W."
                ),
                QuizQuestionDto(
                    id = 5035,
                    questionNumber = 3,
                    question = "What does the Clausius Inequality state for any real (irreversible) thermodynamic cycle?",
                    options = mapOf("A" to "Cyclic_Integral(dQ / T) < 0", "B" to "Cyclic_Integral(dQ / T) = 0", "C" to "Cyclic_Integral(dQ / T) > 0", "D" to "Cyclic_Integral(dQ / T) = infinity"),
                    correctOption = "A",
                    explanation = "Clausius's theorem states that for any cycle, Cyclic_Integral(dQ / T) <= 0, with equality holding strictly for reversible cycles and strict inequality (< 0) for irreversible cycles."
                ),
                QuizQuestionDto(
                    id = 5036,
                    questionNumber = 4,
                    question = "According to Boltzmann's statistical entropy equation S = k_B * ln(Omega), what happens to entropy if the number of accessible microstates Omega equals 1?",
                    options = mapOf("A" to "S = 0 (perfect microscopic order)", "B" to "S = 1 J/K", "C" to "S = k_B", "D" to "S approaches infinity"),
                    correctOption = "A",
                    explanation = "When Omega = 1, ln(1) = 0, so S = k_B * 0 = 0. This aligns with the Third Law of Thermodynamics for a perfect crystal at absolute zero."
                ),
                QuizQuestionDto(
                    id = 5037,
                    questionNumber = 5,
                    question = "For a monoatomic ideal gas, what is the value of the adiabatic heat capacity ratio gamma = C_p / C_v?",
                    options = mapOf("A" to "5/3 (approximately 1.67)", "B" to "7/5 (approximately 1.40)", "C" to "4/3 (approximately 1.33)", "D" to "2/1 (2.00)"),
                    correctOption = "A",
                    explanation = "For a monoatomic gas with 3 translational degrees of freedom, C_v = 1.5*R and C_p = 2.5*R. Therefore gamma = C_p / C_v = (2.5) / (1.5) = 5/3."
                ),
                QuizQuestionDto(
                    id = 5038,
                    questionNumber = 6,
                    question = "An ideal gas expands isothermally at temperature T from volume V to 2*V. What is the work performed by 1 mole of the gas?",
                    options = mapOf("A" to "W = R * T * ln(2)", "B" to "W = 2 * R * T", "C" to "W = 0", "D" to "W = R * T / 2"),
                    correctOption = "A",
                    explanation = "For an isothermal expansion: W = Integral(P dV) = n*R*T * ln(V_f / V_i) = 1 * R * T * ln(2*V / V) = R * T * ln(2)."
                ),
                QuizQuestionDto(
                    id = 5039,
                    questionNumber = 7,
                    question = "Which statement represents the Kelvin-Planck formulation of the Second Law of Thermodynamics?",
                    options = mapOf("A" to "It is impossible to construct a heat engine operating in a cycle that produces no effect other than extracting heat from a reservoir and doing an equivalent amount of work", "B" to "Heat can flow spontaneously from cold to hot without external work", "C" to "Absolute zero temperature can be achieved in a finite number of thermodynamic operations", "D" to "Energy can be created from thermodynamic vacuum"),
                    correctOption = "A",
                    explanation = "The Kelvin-Planck statement prohibits a 100% efficient heat engine: some thermal energy must always be rejected to a low-temperature sink."
                ),
                QuizQuestionDto(
                    id = 5040,
                    questionNumber = 8,
                    question = "What is the change in internal energy Delta U of an ideal gas during a complete closed thermodynamic cycle?",
                    options = mapOf("A" to "Delta U = 0", "B" to "Delta U = Q_in", "C" to "Delta U = W_net", "D" to "Delta U = C_v * T_max"),
                    correctOption = "A",
                    explanation = "Internal energy U is a state function. Returning to the initial thermodynamic state implies Delta U = U_final - U_initial = 0."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH5_001",
                    subject = "Physics",
                    chapter = "Laws of Thermodynamics & Entropy",
                    topic = "Thermodynamic Laws, Heat Engines and Entropy",
                    keywords = listOf("thermodynamics", "entropy", "Carnot engine", "first law", "second law", "Clausius", "Boltzmann", "efficiency", "adiabatic"),
                    definition = "Thermodynamics governs heat, work, and state transformations. First Law dictates energy conservation (Delta U = Q - W), while Second Law imposes entropy growth (Delta S >= 0) and Carnot efficiency limits.",
                    formulaOrCoreRule = "Delta U = Q - W. eta_carnot = 1 - T_C/T_H. S = k_B * ln(Omega). dS = dQ_rev/T. P*V^gamma = const.",
                    workedExample = "For heat engine operating between 600K and 300K with Q_H=500MJ: eta = 1 - 300/600 = 0.50 (50%), work output W = 250 MJ, waste heat Q_C = 250 MJ.",
                    content = "Carnot's theorem bounds heat engine efficiency. Macroscopic Clausius entropy connects with statistical mechanics via Boltzmann's microstate formulation."
                )
            )
        )
    }

    private fun createChapter6(): CatalogChapter {
        return CatalogChapter(
            chapterId = 6,
            chapterOrder = 6,
            title = "Electrostatics & Current Electricity",
            estimatedMinutes = 30,
            summary = "Coulomb's Law, Gauss's Law, electric potential, capacitance, Ohm's/Kirchhoff's Laws, RC circuits, and drift velocity.",
            syllabus = listOf("Coulomb's Law, Superposition Principle & Electric Field Vectors", "Gauss's Law in Integral & Differential Forms (Flux Calculations)", "Electric Potential Energy, Potential V & Conservative Fields (E = -grad V)", "Capacitors: Dielectrics, Energy Density & Series/Parallel Combinations", "Current Electricity: Microscopic Drift Velocity, Ohm's Law & Kirchhoff's Rules"),
            coreFormulas = listOf("Coulomb's Law: F = (1 / (4*pi*epsilon_0)) * (q1 * q2) / r^2", "Gauss's Law: Closed_Integral(E . dA) = Q_enclosed / epsilon_0", "Potential from Field: Delta V = - Integral(E . dl), E = - grad(V)", "Parallel Plate Capacitance: C = (kappa * epsilon_0 * A) / d", "Capacitor Energy: U = 0.5 * C * V^2 = 0.5 * Q^2 / C", "Microscopic Current: I = n * q * A * v_d", "RC Discharging Voltage: V(t) = V_0 * e^(-t / (R*C))"),
            part1Title = "Electrostatic Fields, Gauss's Law & Conduction Physics",
            part1Content = "Electrostatics investigates the forces, fields, and potentials produced by static electric charges. Coulomb's Law quantifies the fundamental electrostatic force between two point charges, exhibiting an inverse-square distance dependence in accordance with Newton's third law. The electric field E = F / q_0 represents force per unit positive test charge. By the principle of linear superposition, the total field of an arbitrary charge distribution is the vector summation or volume integral of individual differential contributions.\n\nGauss's Law, the first of Maxwell's equations, states that the total electric flux penetrating any closed Gaussian surface equals the net enclosed charge divided by the permittivity of free space epsilon_0: Closed_Integral(E . dA) = Q_enc / epsilon_0. Utilizing spatial symmetries (spherical, cylindrical, planar), Gauss's law resolves electric fields effortlessly without computing cumbersome Coulomb vector integrals.\n\nBecause the electrostatic field is conservative (curl(E) = 0), it can be expressed as the negative gradient of an electric scalar potential: E = -grad(V). Potential difference Delta V = -Integral(E . dl) quantifies the work required to transport a unit charge between points. Capacitors store electrostatic potential energy in the electric field established between conductors: C = Q / V, with energy density u_e = 0.5*epsilon_0*E^2.\n\nCurrent electricity characterizes the transport of charge carriers through conductive media. Electric current I = dq/dt reflects microscopic drift motion: I = n*q*A*v_d, where n is charge carrier density, A is conductor cross-section, and v_d is drift velocity. Ohm's Law J = sigma*E translates macroscopically to V = I*R. In complex multi-loop circuits, Kirchhoff's Current Law (KCL, charge conservation) and Kirchhoff's Voltage Law (KVL, energy conservation) establish linear nodal and mesh equations.",
            part2Title = "Circuit Formulations, Worked RC Charging Problem & Applications",
            part2Content = "Exact Equations & Circuit Theorems:\n• Gauss's Law: Phi_E = Closed_Integral(E . dA) = Q_enc / epsilon_0\n• Capacitance: C = epsilon * A / d, U = 0.5 * C * V^2\n• RC Charging: V_c(t) = V_0 * (1 - e^(-t / tau)), where tau = R * C\n• Drift velocity: v_d = I / (n * q * A)\n• Kirchhoff's Rules: Sum(I_in) = Sum(I_out), Sum(Delta V_loop) = 0\n\nWorked RC Circuit Example:\nProblem: A 100 kOhm resistor is connected in series with an uncharged 10 microFarad capacitor across a 12 V DC power source. Calculate: (a) Circuit time constant tau, (b) Initial charging current I_0, (c) Capacitor voltage at t = 1.0 s, and (d) Energy stored at full charge.\nStep 1: Compute time constant tau:\ntau = R * C = (100 * 10^3 Ohm) * (10 * 10^(-6) F) = 1.0 second.\nStep 2: Initial current at t = 0 (capacitor acts as short circuit):\nI_0 = V_0 / R = 12 V / 100,000 Ohm = 1.2 * 10^(-4) A = 120 microAmperes.\nStep 3: Capacitor voltage at t = 1.0 s (t = 1 tau):\nV_c(1.0) = V_0 * (1 - e^(-1.0 / 1.0)) = 12 * (1 - e^(-1)) = 12 * (1 - 0.3679) = 12 * 0.6321 = 7.585 Volts.\nStep 4: Energy stored at full charge (t -> infinity, V_c = 12 V):\nU = 0.5 * C * V_0^2 = 0.5 * (10 * 10^(-6)) * (12)^2 = 5 * 10^(-6) * 144 = 7.2 * 10^(-4) Joules = 0.72 milliJoules.\n\nReal-World Applications:\nIn microelectronics, dynamic random-access memory (DRAM) cells store binary bits as electrostatic charge across sub-micron trench capacitors, periodically refreshed via RC sensing circuits. In medical cardiac defibrillators, high-voltage capacitors discharge hundreds of joules within milliseconds through chest paddles to reset cardiac arrhythmia.",
            examTip = "University Exam Tip: At t = 0 (immediately upon switch closure), an uncharged capacitor acts as a zero-resistance short circuit; at t -> infinity (steady state), a charged capacitor acts as an open circuit (infinite resistance). In viva exams, explain why electrostatic fields vanish inside a solid conductor (free charges redistribute to cancel internal fields).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5041,
                    questionNumber = 1,
                    question = "What is the electric field inside a hollow conducting sphere carrying net charge Q on its outer surface?",
                    options = mapOf("A" to "E = 0 everywhere inside the sphere", "B" to "E = Q / (4*pi*epsilon_0*r^2)", "C" to "E is uniform and directed radially outward", "D" to "E approaches infinity at the center"),
                    correctOption = "A",
                    explanation = "By Gauss's Law, any Gaussian surface drawn inside the hollow conductor encloses zero charge (Q_enc = 0). Therefore, the electric field is identically zero everywhere within the interior cavity."
                ),
                QuizQuestionDto(
                    id = 5042,
                    questionNumber = 2,
                    question = "What happens to the capacitance of a parallel plate capacitor if a dielectric material of dielectric constant kappa = 4 is inserted between the plates?",
                    options = mapOf("A" to "Capacitance quadruples (increases by 4x)", "B" to "Capacitance decreases to 1/4 of original", "C" to "Capacitance doubles (increases by 2x)", "D" to "Capacitance remains unchanged"),
                    correctOption = "A",
                    explanation = "C = kappa * C_0. Inserting a dielectric of relative permittivity kappa = 4 scales capacitance by a factor of 4."
                ),
                QuizQuestionDto(
                    id = 5043,
                    questionNumber = 3,
                    question = "In a series RC circuit connected to DC voltage V_0, how much time is required for the capacitor to charge to approximately 63.2% of its maximum voltage?",
                    options = mapOf("A" to "1 time constant (t = R * C)", "B" to "2 time constants (t = 2 * R * C)", "C" to "0.5 time constants", "D" to "5 time constants"),
                    correctOption = "A",
                    explanation = "At t = tau = R*C: V(tau) = V_0 * (1 - e^(-1)) = V_0 * (1 - 0.368) = 0.632 * V_0 (63.2%)."
                ),
                QuizQuestionDto(
                    id = 5044,
                    questionNumber = 4,
                    question = "Kirchhoff's Junction Rule (Current Law, KCL) is a direct consequence of which fundamental conservation law?",
                    options = mapOf("A" to "Conservation of Electric Charge", "B" to "Conservation of Energy", "C" to "Conservation of Linear Momentum", "D" to "Conservation of Magnetic Flux"),
                    correctOption = "A",
                    explanation = "Kirchhoff's Current Law states that total current entering a junction equals total current exiting it, directly enforcing conservation of electric charge."
                ),
                QuizQuestionDto(
                    id = 5045,
                    questionNumber = 5,
                    question = "If the distance d between the plates of an isolated charged capacitor is doubled, what happens to the stored electrostatic energy U?",
                    options = mapOf("A" to "Energy doubles (U = Q^2 / (2C), and C is halved)", "B" to "Energy is halved", "C" to "Energy remains constant", "D" to "Energy quadruples"),
                    correctOption = "A",
                    explanation = "For an isolated capacitor, charge Q is fixed. C = epsilon*A/d. Doubling d halves C. Stored energy U = Q^2 / (2*C). Halving the denominator doubles the stored energy (the increase comes from mechanical work pulling the plates apart)."
                ),
                QuizQuestionDto(
                    id = 5046,
                    questionNumber = 6,
                    question = "What is the typical magnitude of electron drift velocity v_d in a household copper wire carrying normal electric current?",
                    options = mapOf("A" to "Fractions of a millimeter per second (~10^(-4) m/s)", "B" to "The speed of light (3 * 10^8 m/s)", "C" to "The speed of sound in air (340 m/s)", "D" to "1000 kilometers per hour"),
                    correctOption = "A",
                    explanation = "Due to frequent collisions with the lattice ions, the net drift velocity of electrons is extremely slow (on the order of 10^(-4) m/s), even though electromagnetic signals propagate near the speed of light."
                ),
                QuizQuestionDto(
                    id = 5047,
                    questionNumber = 7,
                    question = "What is the electric potential V at distance r from a point charge q in vacuum?",
                    options = mapOf("A" to "V = q / (4 * pi * epsilon_0 * r)", "B" to "V = q / (4 * pi * epsilon_0 * r^2)", "C" to "V = q^2 / (4 * pi * epsilon_0 * r)", "D" to "V = 0"),
                    correctOption = "A",
                    explanation = "Electric potential V = -Integral(E . dr) from infinity to r = q / (4*pi*epsilon_0*r). Unlike the 1/r^2 electric field, potential scales inversely with the first power of r."
                ),
                QuizQuestionDto(
                    id = 5048,
                    questionNumber = 8,
                    question = "Three identical resistors of resistance R = 6 Ohms are connected in parallel. What is their equivalent resistance?",
                    options = mapOf("A" to "2 Ohms", "B" to "18 Ohms", "C" to "3 Ohms", "D" to "0.5 Ohms"),
                    correctOption = "A",
                    explanation = "1/R_eq = 1/R + 1/R + 1/R = 3/R = 3/6 = 1/2 => R_eq = 2 Ohms."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH6_001",
                    subject = "Physics",
                    chapter = "Electrostatics & Current Electricity",
                    topic = "Gauss's Law, Capacitance, and DC Circuit Analysis",
                    keywords = listOf("electrostatics", "Coulomb law", "Gauss law", "capacitance", "electric field", "electric potential", "Ohm law", "Kirchhoff", "RC circuit"),
                    definition = "Electrostatics investigates static charge interactions via Coulomb's and Gauss's laws. Current electricity governs charge transport (drift velocity), Ohm's law, Kirchhoff's rules, and RC transient responses.",
                    formulaOrCoreRule = "F = q1*q2/(4*pi*eps_0*r^2). Closed_Integral(E.dA) = Q/eps_0. V = -grad(E). C = eps*A/d. tau = R*C. V(t) = V_0*(1 - e^(-t/tau)).",
                    workedExample = "For R=100kOhm, C=10uF, V_0=12V: tau = 100k * 10u = 1.0 s. At t=1s, V_c = 12*(1 - e^(-1)) = 7.585 V.",
                    content = "Gauss's law calculates symmetric fields. Capacitors store electrostatic field energy (U = 0.5*C*V^2). Kirchhoff's laws enforce charge and energy conservation across complex multi-branch circuits."
                )
            )
        )
    }

    private fun createChapter7(): CatalogChapter {
        return CatalogChapter(
            chapterId = 7,
            chapterOrder = 7,
            title = "Electromagnetism & Maxwell's Equations",
            estimatedMinutes = 35,
            summary = "Biot-Savart Law, Ampere's Law, Faraday's Law, Lenz's Law, displacement current, Maxwell's four equations, and EM wave propagation.",
            syllabus = listOf("Magnetic Forces & Fields: Lorentz Force & Biot-Savart Law", "Ampere's Circuital Law & Magnetic Fields of Solenoids/Toroids", "Faraday's Law of Induction, Lenz's Law & Eddy Currents", "Maxwell's Correction: Displacement Current & The Complete Field Equations", "Electromagnetic Waves: Speed of Light c, Poynting Vector & Energy Flux"),
            coreFormulas = listOf("Lorentz Force: F = q * (E + v x B)", "Biot-Savart Law: dB = (mu_0 / (4*pi)) * (I * dl x r_hat) / r^2", "Ampere's Law: Closed_Integral(B . dl) = mu_0 * I_enclosed", "Faraday's Law: emf = - d(Phi_B) / dt = - d/dt [Closed_Integral(B . dA)]", "Maxwell-Ampere Law: Closed_Integral(B . dl) = mu_0 * I_enc + mu_0 * epsilon_0 * (dPhi_E / dt)", "Speed of Light: c = 1 / sqrt(mu_0 * epsilon_0) approximately 3 * 10^8 m/s", "Poynting Vector: S = (1 / mu_0) * (E x B)"),
            part1Title = "Magnetostatics, Induction & The Maxwell Field Synthesis",
            part1Content = "Magnetostatics describes the magnetic fields generated by steady electric currents. Moving electric charges experience the Lorentz Force F = q*(E + v x B), where the magnetic component F_B = q*(v x B) acts perpendicularly to both velocity and magnetic field B, doing zero mechanical work on free charges. The Biot-Savart Law calculates the differential magnetic field dB = (mu_0 / 4pi)*(I dl x r_hat)/r^2 produced by a current element. Ampere's Circuital Law Closed_Integral(B . dl) = mu_0 * I_enc provides an elegant path integral formulation for symmetric current distributions.\n\nElectromagnetic induction, discovered by Michael Faraday, established that dynamic magnetic fields create electric fields. Faraday's Law states that the induced electromotive force (emf) around a closed loop is proportional to the negative time rate of change of magnetic flux: emf = -dPhi_B / dt. Lenz's Law enforces energy conservation: the polarity of the induced emf generates a current whose secondary magnetic field opposes the original change in flux.\n\nJames Clerk Maxwell discovered that Ampere's Law was mathematically incomplete for time-varying fields, violating charge continuity in capacitors. Maxwell introduced the Displacement Current term I_d = epsilon_0 * (dPhi_E / dt), revealing that time-varying electric fields generate magnetic fields, just as time-varying magnetic fields generate electric fields.\n\nThis synthesis yielded Maxwell's Four Equations: Gauss's Law for Electricity (div E = rho/epsilon_0), Gauss's Law for Magnetism (div B = 0, no magnetic monopoles), Faraday's Law of Induction (curl E = -dB/dt), and the Maxwell-Ampere Law (curl B = mu_0*J + mu_0*epsilon_0*dE/dt). Combining these curl equations in free space yields the Electromagnetic Wave Equation, demonstrating that coupled E and B fields self-propagate at speed c = 1 / sqrt(mu_0*epsilon_0), proving that light is an electromagnetic wave.",
            part2Title = "Maxwell Formulations, Worked Solenoid Induction & Applications",
            part2Content = "Exact Equations & Wave Energy Flux:\n• Solenoid magnetic field: B = mu_0 * n * I (n = turns per meter)\n• Mutual inductance: emf = -M * (dI / dt)\n• Poynting Vector: S = (1 / mu_0) * (E x B) [Watts/m^2]\n• Wave impedance of free space: Z_0 = sqrt(mu_0 / epsilon_0) approximately 377 Ohms\n• EM field ratio: E = c * B\n\nWorked Faraday Induction Example:\nProblem: A circular coil of N = 200 turns and radius r = 0.05 m is oriented perpendicular to a uniform magnetic field. The magnetic field ramps down from B_1 = 0.8 T to B_2 = 0.2 T in Delta_t = 0.04 seconds. Calculate: (a) Initial magnetic flux per turn, (b) Rate of change of magnetic flux dPhi/dt, and (c) Magnitude of the induced electromotive force emf in the coil.\nStep 1: Compute coil area A:\nA = pi * r^2 = pi * (0.05)^2 = 0.007854 m^2.\nStep 2: Initial magnetic flux Phi_1:\nPhi_1 = B_1 * A = 0.8 * 0.007854 = 6.283 * 10^(-3) Wb (Weber).\nStep 3: Flux change Delta_Phi:\nDelta_B = B_2 - B_1 = 0.2 - 0.8 = -0.6 T.\nDelta_Phi = Delta_B * A = -0.6 * 0.007854 = -4.712 * 10^(-3) Wb.\nStep 4: Rate of flux change:\ndPhi / dt = Delta_Phi / Delta_t = -4.712 * 10^(-3) / 0.04 = -0.1178 Wb/s.\nStep 5: Compute total induced emf for N turns:\nemf = - N * (dPhi / dt) = - 200 * (-0.1178) = +23.56 Volts.\n\nReal-World Applications:\nIn power distribution, electromagnetic induction drives electrical transformers, stepping voltages up to 500 kV to minimize I^2*R transmission line losses. In wireless communication, RF antenna arrays emit oscillating E and B vectors, transporting terabytes of cellular and satellite data via the Poynting flux.",
            examTip = "University Exam Tip: Maxwell's displacement current term J_d = epsilon_0*(dE/dt) is what allows electromagnetic waves to propagate across the vacuum of interstellar space. In viva exams, explain why Gauss's law for magnetism div B = 0 implies that magnetic field lines always form closed continuous loops (no magnetic monopoles).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5049,
                    questionNumber = 1,
                    question = "Which of Maxwell's equations mathematically states that isolated magnetic monopoles do not exist in classical electromagnetism?",
                    options = mapOf("A" to "Gauss's Law for Magnetism: Closed_Integral(B . dA) = 0", "B" to "Gauss's Law for Electricity: Closed_Integral(E . dA) = Q / epsilon_0", "C" to "Faraday's Law: curl E = -dB/dt", "D" to "Ampere-Maxwell Law: curl B = mu_0*J + mu_0*epsilon_0*dE/dt"),
                    correctOption = "A",
                    explanation = "Because magnetic field lines form continuous closed loops without start or end points, the net magnetic flux through any closed surface is identically zero (div B = 0)."
                ),
                QuizQuestionDto(
                    id = 5050,
                    questionNumber = 2,
                    question = "What is the speed of electromagnetic waves in vacuum expressed in terms of fundamental vacuum constants?",
                    options = mapOf("A" to "c = 1 / sqrt(mu_0 * epsilon_0)", "B" to "c = sqrt(mu_0 / epsilon_0)", "C" to "c = mu_0 * epsilon_0", "D" to "c = sqrt(mu_0 * epsilon_0)"),
                    correctOption = "A",
                    explanation = "Derived from the wave equation for coupled electromagnetic fields in vacuum, wave speed c = 1 / sqrt(mu_0 * epsilon_0) approximately 2.998 * 10^8 m/s."
                ),
                QuizQuestionDto(
                    id = 5051,
                    questionNumber = 3,
                    question = "What physical quantity does the Poynting Vector S = (1 / mu_0) * (E x B) represent?",
                    options = mapOf("A" to "The rate of electromagnetic energy transfer per unit area (power flux density in W/m^2)", "B" to "The total mechanical momentum of charges", "C" to "The electrostatic charge density in space", "D" to "The capacitance of dielectric media"),
                    correctOption = "A",
                    explanation = "The Poynting vector S specifies both the direction of energy propagation and the magnitude of power transferred per unit area by electromagnetic waves."
                ),
                QuizQuestionDto(
                    id = 5052,
                    questionNumber = 4,
                    question = "Why did Maxwell introduce the concept of 'Displacement Current' into Ampere's Law?",
                    options = mapOf("A" to "To satisfy the continuity equation of charge conservation for time-varying electric fields (such as inside a charging capacitor)", "B" to "To account for gravitational deflection of light", "C" to "To correct for relativistic length contraction in wires", "D" to "To eliminate eddy currents in transformer cores"),
                    correctOption = "A",
                    explanation = "Without displacement current epsilon_0*(dPhi_E/dt), Ampere's circuital law yielded contradictory results when applied to a surface passing between the plates of a charging capacitor where no conduction current flows."
                ),
                QuizQuestionDto(
                    id = 5053,
                    questionNumber = 5,
                    question = "A proton moves with velocity v directly parallel to a uniform magnetic field B. What is the magnetic force acting on the proton?",
                    options = mapOf("A" to "F = 0", "B" to "F = q * v * B", "C" to "F = q * v^2 * B", "D" to "F = - q * v * B"),
                    correctOption = "A",
                    explanation = "Magnetic force F = q * (v x B) = q * v * B * sin(theta). When velocity is parallel to the field, theta = 0 degrees, sin(0) = 0, so the force is identically zero."
                ),
                QuizQuestionDto(
                    id = 5054,
                    questionNumber = 6,
                    question = "What is the magnetic field inside an ideal long solenoid with n turns per meter carrying steady current I?",
                    options = mapOf("A" to "B = mu_0 * n * I", "B" to "B = mu_0 * I / (2 * pi * r)", "C" to "B = mu_0 * n^2 * I", "D" to "B = 0"),
                    correctOption = "A",
                    explanation = "Applying Ampere's law around an Amperian rectangular loop through an ideal long solenoid gives uniform axial field B = mu_0 * n * I."
                ),
                QuizQuestionDto(
                    id = 5055,
                    questionNumber = 7,
                    question = "According to Lenz's Law, what governs the direction of an induced current in a conductive loop?",
                    options = mapOf("A" to "The induced current produces a magnetic field that opposes the change in magnetic flux that caused it", "B" to "The induced current always points clockwise regardless of flux direction", "C" to "The induced current aligns directly with the external magnetic field to amplify flux", "D" to "The current flows perpendicular to all electrical leads"),
                    correctOption = "A",
                    explanation = "Lenz's Law, reflected by the negative sign in Faraday's law emf = -dPhi/dt, ensures energy conservation by opposing the mechanical action driving flux changes."
                ),
                QuizQuestionDto(
                    id = 5056,
                    questionNumber = 8,
                    question = "What is the characteristic wave impedance of free space (vacuum), Z_0?",
                    options = mapOf("A" to "Approximately 377 Ohms (120 * pi)", "B" to "50 Ohms", "C" to "0 Ohms", "D" to "1 Ohm"),
                    correctOption = "A",
                    explanation = "Z_0 = E / H = sqrt(mu_0 / epsilon_0) approximately 376.73 Ohms (commonly approximated as 120*pi or 377 Ohms)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH7_001",
                    subject = "Physics",
                    chapter = "Electromagnetism & Maxwell's Equations",
                    topic = "Electromagnetic Induction, Maxwell's Equations and Wave Propagation",
                    keywords = listOf("electromagnetism", "Lorentz force", "Biot Savart", "Ampere law", "Faraday law", "Lenz law", "Maxwell equations", "displacement current", "Poynting vector"),
                    definition = "Electromagnetism unifies electric and magnetic phenomena. Maxwell's equations formalize field dynamics, displacement current, and self-propagating electromagnetic radiation at the speed of light.",
                    formulaOrCoreRule = "F = q(E + v x B). curl E = -dB/dt. curl B = mu_0*J + mu_0*eps_0*dE/dt. c = 1/sqrt(mu_0*eps_0). S = (1/mu_0)*(E x B).",
                    workedExample = "For N=200 turns, r=0.05m coil in field dropping from 0.8T to 0.2T in 0.04s: Area=0.00785m^2, Delta_Phi = -0.6*0.00785 = -0.00471Wb, emf = -200*(-0.00471/0.04) = +23.56 V.",
                    content = "Maxwell's displacement current completes Ampere's circuital law, proving that time-varying electric fields generate magnetic fields, enabling light wave propagation through vacuum."
                )
            )
        )
    }

    private fun createChapter8(): CatalogChapter {
        return CatalogChapter(
            chapterId = 8,
            chapterOrder = 8,
            title = "Optics, Quantum Mechanics & Semiconductor Physics",
            estimatedMinutes = 35,
            summary = "Wave optics (Snell, diffraction, thin films), photoelectric effect, de Broglie wavelength, Schrodinger equation, energy band theory, and p-n junction diodes.",
            syllabus = listOf("Wave Optics: Snell's Law, Total Internal Reflection & Double-Slit Interference", "Diffraction Grating & Thin-Film Destructive/Constructive Interference", "Quantum Foundations: Photoelectric Effect, Compton Scattering & Photons", "Wave-Particle Duality: De Broglie Wavelength & 1D Time-Independent Schrödinger Equation", "Solid-State Semiconductor Physics: Valence/Conduction Bands & p-n Junctions"),
            coreFormulas = listOf("Snell's Law: n1 * sin(theta1) = n2 * sin(theta2)", "Critical Angle for Total Internal Reflection: theta_c = arcsin(n2 / n1)", "Young's Double-Slit Bright Fringe: d * sin(theta) = m * lambda", "Photoelectric Equation: K_max = h * f - phi = h * (c / lambda) - phi", "De Broglie Wavelength: lambda = h / p = h / (m * v)", "1D Schrödinger Equation: - (hbar^2 / (2m)) * (d^2 psi / dx^2) + V(x) * psi = E * psi", "Diode Shockley Equation: I = I_s * (e^(q*V / (k_B*T)) - 1)"),
            part1Title = "Wave Optics, Wave-Particle Duality & Quantum Band Theory",
            part1Content = "Wave optics describes electromagnetic propagation where wavelength lambda is comparable to aperture dimensions. Snell's Law n1*sin(theta1) = n2*sin(theta2) governs refraction at dielectric interfaces. When light travels from a dense to a rare medium at angles exceeding the critical angle theta_c = arcsin(n2/n1), Total Internal Reflection occurs, forming the physical waveguide basis of fiber-optic telecommunications. Thomas Young's double-slit experiment proved the wave nature of light via spatial interference fringes d*sin(theta) = m*lambda.\n\nClassical physics failed to explain the ultraviolet catastrophe and the Photoelectric Effect. Albert Einstein resolved this by postulating that light consists of quantized energy packets called photons, with energy E = h*f. When a photon strikes a metal surface, it transfers energy to an electron; if photon energy exceeds the material's surface Work Function phi, the photoelectron is ejected with maximum kinetic energy K_max = h*f - phi, independent of light beam intensity.\n\nLouis de Broglie extended wave-particle duality to matter, proposing that any moving particle of momentum p possesses an associated matter wavelength lambda = h / p. Erwin Schrödinger formulated the quantum wave equation governing particle states psi(x, t). The 1D Time-Independent Schrödinger Equation -(hbar^2 / 2m)*(d^2psi/dx^2) + V(x)*psi = E*psi reveals that bound particles in potential wells (e.g., infinite square well) exhibit discrete, quantized energy eigenvalues E_n = (n^2*pi^2*hbar^2) / (2*m*L^2).\n\nIn condensed matter physics, atomic orbital overlap in periodic crystal lattices splits discrete atomic levels into continuous Energy Bands. The Valence Band contains bound electrons, separated from the upper Conduction Band by a forbidden Energy Bandgap E_g. In metals, bands overlap (E_g = 0); in insulators, E_g > 4 eV; in semiconductors (Silicon, Germanium), E_g approximately 1.1 eV allows thermal carrier excitation. Doping with Group V donor atoms yields n-type semiconductors, while Group III acceptors produce p-type material. Joining them creates a p-n junction with a depletion region and built-in potential barrier, establishing diode rectification governed by the Shockley equation.",
            part2Title = "Quantum Formulations, Worked Photoelectric Problem & Modern Devices",
            part2Content = "Exact Equations & Physical Constants:\n• Planck's constant: h = 6.626 * 10^(-34) J.s (hbar = h / (2*pi) = 1.055 * 10^(-34) J.s)\n• Photoelectric effect: K_max = h*c / lambda - phi (1 eV = 1.602 * 10^(-19) J)\n• De Broglie relation: lambda = h / (m * v)\n• Infinite square well energy: E_n = (n^2 * h^2) / (8 * m * L^2)\n• Diode forward current: I = I_s * (e^(V / V_t) - 1), where V_t = k_B*T / q approximately 26 mV at 300 K\n\nWorked Photoelectric Effect Example:\nProblem: Light of wavelength lambda = 300 nm illuminates a potassium photocathode with work function phi = 2.20 eV. Calculate: (a) Energy of incident photon in eV, (b) Maximum kinetic energy K_max of photoelectrons in eV, and (c) Stopping potential V_stop.\nStep 1: Convert photon wavelength to energy:\nh*c = (6.626 * 10^(-34) J.s) * (3.0 * 10^8 m/s) = 1.988 * 10^(-18) Joules.\nConvert to eV: E_photon = 1.988 * 10^(-18) / (1.602 * 10^(-19)) = 12.41 / (lambda in hundreds of nm) = 4.137 eV.\nStep 2: Calculate maximum kinetic energy K_max:\nK_max = E_photon - phi = 4.137 eV - 2.20 eV = 1.937 eV.\nIn Joules: K_max = 1.937 * (1.602 * 10^(-19) J) = 3.103 * 10^(-19) Joules.\nStep 3: Calculate stopping potential V_stop:\nSince K_max = e * V_stop => V_stop = 1.937 Volts.\n\nReal-World Applications:\nIn telecommunications, semiconductor laser diodes emit coherent single-mode photons channeled into total-internal-reflection silica fiber cables. In clean energy, photovoltaic solar cells utilize silicon p-n junctions to convert incident solar photon flux directly into DC electricity.",
            examTip = "University Exam Tip: In the photoelectric effect, increasing light intensity increases the number of emitted electrons (photocurrent), but does NOT increase their kinetic energy; kinetic energy depends strictly on light frequency. In viva exams, know that quantum tunneling allows particles to penetrate classically forbidden potential barriers (E < V_0).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 5057,
                    questionNumber = 1,
                    question = "In the photoelectric effect, what happens to the maximum kinetic energy K_max of emitted electrons if the incident light intensity is doubled while keeping frequency constant?",
                    options = mapOf("A" to "K_max remains completely unchanged (only photocurrent doubles)", "B" to "K_max doubles", "C" to "K_max quadruples", "D" to "K_max drops to zero"),
                    correctOption = "A",
                    explanation = "Photoelectron kinetic energy K_max = h*f - phi depends exclusively on photon frequency f. Increasing intensity increases the photon arrival rate (doubling photocurrent) but leaves individual photon energy unchanged."
                ),
                QuizQuestionDto(
                    id = 5058,
                    questionNumber = 2,
                    question = "What is the de Broglie wavelength of an electron (mass m = 9.11 * 10^(-31) kg) traveling at velocity v = 2.0 * 10^6 m/s (h = 6.626 * 10^(-34) J.s)?",
                    options = mapOf("A" to "0.364 nm (3.64 * 10^(-10) m)", "B" to "1.24 nm", "C" to "0.053 nm", "D" to "5.45 nm"),
                    correctOption = "A",
                    explanation = "lambda = h / (m * v) = (6.626 * 10^(-34)) / (9.11 * 10^(-31) * 2.0 * 10^6) = (6.626 * 10^(-34)) / (1.822 * 10^(-24)) = 3.637 * 10^(-10) m = 0.364 nm."
                ),
                QuizQuestionDto(
                    id = 5059,
                    questionNumber = 3,
                    question = "What optical phenomenon occurs when light propagating in glass (n1 = 1.50) strikes a water interface (n2 = 1.33) at an angle of incidence greater than the critical angle?",
                    options = mapOf("A" to "Total Internal Reflection", "B" to "Complete transmission with zero reflection", "C" to "Destructive thin-film interference", "D" to "Optical dispersion into rainbow spectrum"),
                    correctOption = "A",
                    explanation = "When light travels from a higher refractive index medium to a lower one at an angle theta > theta_c = arcsin(n2 / n1), 100% of the light energy is reflected back into the dense medium (Total Internal Reflection)."
                ),
                QuizQuestionDto(
                    id = 5060,
                    questionNumber = 4,
                    question = "For a particle of mass m confined in an infinite 1D potential well of width L, how do the quantized energy levels E_n scale with quantum number n?",
                    options = mapOf("A" to "E_n is proportional to n^2", "B" to "E_n is proportional to n", "C" to "E_n is proportional to 1/n", "D" to "E_n is proportional to sqrt(n)"),
                    correctOption = "A",
                    explanation = "The eigenvalues of the 1D infinite square well are E_n = (n^2 * h^2) / (8 * m * L^2), scaling quadratically with integer quantum number n = 1, 2, 3..."
                ),
                QuizQuestionDto(
                    id = 5061,
                    questionNumber = 5,
                    question = "What type of semiconductor is formed when pure silicon (Group IV) is doped with phosphorus (Group V donor atoms)?",
                    options = mapOf("A" to "n-type semiconductor (majority electrons)", "B" to "p-type semiconductor (majority holes)", "C" to "Intrinsic semiconductor", "D" to "Superconductor"),
                    correctOption = "A",
                    explanation = "Group V phosphorus atoms provide five valence electrons. Four participate in covalent lattice bonds while the fifth easily ionizes into the conduction band, creating an n-type semiconductor."
                ),
                QuizQuestionDto(
                    id = 5062,
                    questionNumber = 6,
                    question = "What is the typical energy bandgap E_g of silicon at room temperature (300 K)?",
                    options = mapOf("A" to "Approximately 1.1 eV", "B" to "0.0 eV (metallic conductor)", "C" to "9.0 eV (insulator)", "D" to "0.1 eV"),
                    correctOption = "A",
                    explanation = "Silicon is an indirect bandgap semiconductor with an energy bandgap E_g of approximately 1.12 eV at room temperature (300 K)."
                ),
                QuizQuestionDto(
                    id = 5063,
                    questionNumber = 7,
                    question = "What quantum phenomenon enables an electron to cross an energy barrier higher than its kinetic energy, which underpins the operation of Tunnel Diodes and Flash Memory?",
                    options = mapOf("A" to "Quantum Tunneling (Barrier Penetration)", "B" to "Compton Scattering", "C" to "Blackbody Radiation", "D" to "Spontaneous Emission"),
                    correctOption = "A",
                    explanation = "Because the wave function psi has a non-zero decaying exponential tail inside a finite potential barrier V_0 > E, there is a finite probability that the particle tunnels through to the opposite side."
                ),
                QuizQuestionDto(
                    id = 5064,
                    questionNumber = 8,
                    question = "In Young's double-slit interference experiment with slit separation d and wavelength lambda, what is the condition for constructive interference (bright fringes)?",
                    options = mapOf("A" to "d * sin(theta) = m * lambda (where m is an integer)", "B" to "d * sin(theta) = (m + 0.5) * lambda", "C" to "d * cos(theta) = m * lambda", "D" to "sin(theta) = m * d * lambda"),
                    correctOption = "A",
                    explanation = "Bright fringes occur when the optical path difference d*sin(theta) equals an integer number of full wavelengths m*lambda, causing constructive phase alignment."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "PHYS_CH8_001",
                    subject = "Physics",
                    chapter = "Optics, Quantum Mechanics & Semiconductor Physics",
                    topic = "Wave Optics, Photoelectric Effect and Semiconductor Physics",
                    keywords = listOf("optics", "quantum mechanics", "Snell law", "photoelectric effect", "de Broglie", "Schrodinger", "semiconductor", "bandgap", "pn junction"),
                    definition = "Wave optics analyzes diffraction and refraction. Quantum mechanics formalizes wave-particle duality (de Broglie, photoelectric effect, Schrodinger equation), governing solid-state bandgaps and p-n junction diodes.",
                    formulaOrCoreRule = "K_max = hf - phi. lambda = h/p. -hbar^2/(2m) d^2psi/dx^2 + V*psi = E*psi. E_n = n^2*h^2/(8mL^2). Shockley: I = I_s*(e^(qV/kT) - 1).",
                    workedExample = "For 300nm light on potassium (phi=2.20 eV): E_photon = 1241/300 = 4.137 eV, K_max = 4.137 - 2.20 = 1.937 eV, stopping potential V_stop = 1.937 V.",
                    content = "Einstein's photoelectric equation proved energy quantization. In solid-state physics, valence/conduction band separation governs semiconductor conductivity and diode rectification."
                )
            )
        )
    }
}
