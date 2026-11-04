# scratch/gen_tech_1_10.py
# Tech and Computing Subjects 1 to 10 (111 to 120)

def build_tech_subjects():
    from gen_batch1 import get_subjects
    subs = get_subjects() # Has 111 (VLSI)

    # 112. Robotics & Autonomous Systems
    subs.append({
        "id": "SUB_ROBOTICS",
        "subject_id": 112,
        "name": "Robotics & Autonomous Systems",
        "slug": "robotics-autonomous-systems",
        "icon_symbol": "🤖",
        "color_hex": "#0284C7",
        "category": "Engineering",
        "description": "Spatial kinematics, dynamics, PID trajectory control, SLAM, and autonomous mobile navigation.",
        "is_added": False,
        "chapters": [
            {
                "chapter_id": 1121,
                "chapter_order": 1,
                "title": "Spatial Transformations & Kinematics",
                "estimated_minutes": 20,
                "summary": "Rotation matrices, unit quaternions, Denavit-Hartenberg parameters, and manipulator Jacobians.",
                "syllabus": [
                    "SO(3) rotation matrices and unit quaternions",
                    "SE(3) homogeneous transformation matrices",
                    "Denavit-Hartenberg (D-H) forward kinematics convention",
                    "Manipulator Jacobian and velocity kinematics"
                ],
                "core_formulas": [
                    "T_homogeneous = [R, p; 0, 1]",
                    "q = [cos(theta/2), u * sin(theta/2)]",
                    "v = J(theta) * dot_theta",
                    "dot_theta = J_pinv * v + (I - J_pinv * J) * dot_theta_null"
                ],
                "part1_title": "Spatial Representation & Homogeneous Coordinates",
                "part1_content": "Rigid body posture in 3D space combines orientation in SO(3) with position in R^3. While rotation matrices exhibit 9 parameters with orthogonality constraints, unit quaternions represent spatial rotation without gimbal lock singularities. Homogeneous transformation matrices combine rotation and translation into a unified 4x4 matrix enabling coordinate chaining.",
                "part2_title": "Denavit-Hartenberg Convention & Differential Kinematics",
                "part2_content": "The D-H convention establishes coordinate frames along serial linkages using four parameters: link length a, twist alpha, offset d, and joint angle theta. Forward kinematics computes end-effector pose from joint angles. The Manipulator Jacobian relates joint velocities to Cartesian linear and angular velocities: v = J * dot_theta.",
                "tip": "Singularities occur when the Manipulator Jacobian drops rank, leading to infinite joint velocities for finite Cartesian end-effector motions.",
                "mcqs": [
                    {
                        "id": 11211, "question_number": 1,
                        "question": "What is the primary advantage of unit quaternions over Euler angles in robotics?",
                        "options": {"A": "Fewer numerical values", "B": "Complete avoidance of gimbal lock singularities", "C": "Only represent 2D motions", "D": "Eliminate translation vectors"},
                        "correct_option": "B",
                        "explanation": "Euler angles suffer from gimbal lock singularities when two axes align. Quaternions provide a smooth, singularity-free parameterization of SO(3)."
                    },
                    {
                        "id": 11212, "question_number": 2,
                        "question": "In the standard D-H convention, which axis is aligned collinear with the joint actuation axis?",
                        "options": {"A": "X-axis", "B": "Y-axis", "C": "Z-axis", "D": "W-axis"},
                        "correct_option": "C",
                        "explanation": "By D-H convention, the Z-axis is assigned collinear with the motion axis (revolute or prismatic) of the joint."
                    },
                    {
                        "id": 11213, "question_number": 3,
                        "question": "What mathematical condition defines a kinematic singularity in a robot arm?",
                        "options": {"A": "The Manipulator Jacobian matrix drops rank (det(J) = 0)", "B": "Motor current reaches zero", "C": "Control loop rate drops below 100 Hz", "D": "Robot payload is zero"},
                        "correct_option": "A",
                        "explanation": "Kinematic singularities occur when Jacobian J(q) drops rank, preventing motion in certain directions and causing inverse kinematics to diverge."
                    },
                    {
                        "id": 11214, "question_number": 4,
                        "question": "Which equation relates joint velocities dot_q to operational Cartesian twist v?",
                        "options": {"A": "v = J(q) * dot_q", "B": "dot_q = J(q) * v", "C": "v = J(q)^2 * dot_q", "D": "v = dJ/dt * q"},
                        "correct_option": "A",
                        "explanation": "The Manipulator Jacobian J(q) acts as the linear mapping operator between joint velocity space and Cartesian task space velocities: v = J(q) * dot_q."
                    },
                    {
                        "id": 11215, "question_number": 5,
                        "question": "For redundant manipulators (DOF > 6), how is the Jacobian pseudo-inverse formulated?",
                        "options": {"A": "J^T * (J * J^T)^(-1)", "B": "(J^T * J)^(-1)", "C": "J * J^T", "D": "det(J) * I"},
                        "correct_option": "A",
                        "explanation": "The right Moore-Penrose pseudo-inverse is J_pinv = J^T * (J * J^T)^(-1), minimizing the Euclidean norm of joint velocities."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_rob_01",
                        "subject": "Robotics & Autonomous Systems",
                        "chapter": "Spatial Transformations & Kinematics",
                        "topic": "Manipulator Kinematics & Jacobians",
                        "keywords": ["robotics", "kinematics", "denavit-hartenberg", "quaternion", "jacobian", "singularity", "so3"],
                        "definition": "Robot kinematics studies the geometry of motion with respect to a fixed reference frame without regard to the forces causing motion.",
                        "formula_or_core_rule": "v = J(q) * dot_q; T_homogeneous = [R, p; 0, 1].",
                        "worked_example": "For a 2-link planar arm with lengths L1=1m, L2=1m at angles theta1=0, theta2=90: x = L1*cos(0) + L2*cos(90) = 1m; y = 1m.",
                        "content": "Spatial transformations govern robot kinematics through SO(3) rotations and SE(3) homogeneous coordinates. Denavit-Hartenberg frames systematicize serial linkage modeling, while the Jacobian matrix maps joint velocities to operational space."
                    }
                ]
            },
            {
                "chapter_id": 1122,
                "chapter_order": 2,
                "title": "Robot Dynamics & Computed Torque Control",
                "estimated_minutes": 20,
                "summary": "Euler-Lagrange equations, mass-inertia matrix, Coriolis torques, and feedback linearization.",
                "syllabus": [
                    "Lagrangian mechanics: kinetic and potential energy",
                    "Mass-inertia matrix M(q), Coriolis C(q, dot_q), and gravity G(q)",
                    "Independent joint PID control and gravity compensation",
                    "Computed Torque Control (Feedback Linearization)"
                ],
                "core_formulas": [
                    "tau = M(q) * ddot_q + C(q, dot_q) * dot_q + G(q)",
                    "L = K_kinetic - P_potential",
                    "tau_cmd = M(q) * (ddot_q_des + Kp * e + Kd * dot_e) + C * dot_q + G",
                    "x^T * (dot_M - 2*C) * x = 0 (Skew-Symmetry)"
                ],
                "part1_title": "Euler-Lagrange Equations of Motion",
                "part1_content": "Robot dynamics derives equations of motion relating joint torques to accelerations via Lagrangian L = K - P. The resulting system takes the canonical form M(q)*ddot_q + C(q, dot_q)*dot_q + G(q) = tau, where M(q) is positive-definite, C contains velocity-squared terms, and G represents gravitational torque.",
                "part2_title": "Computed Torque Control Architecture",
                "part2_content": "Computed Torque Control utilizes feedback linearization by evaluating the non-linear dynamic model in real-time, canceling non-linear Coriolis and gravitational forces, and converting the multi-input multi-output robotic system into decoupled linear double-integrator subsystems governed by PD error feedback.",
                "tip": "M(q) is always symmetric and positive-definite; dot_M - 2*C is always skew-symmetric, essential for Lyapunov stability proofs.",
                "mcqs": [
                    {
                        "id": 11221, "question_number": 1,
                        "question": "What physical principle is captured by the skew-symmetry of the matrix (dot_M - 2*C)?",
                        "options": {"A": "Conservation of energy in rigid body mechanics", "B": "Friction dissipation", "C": "Motor armature inductance", "D": "Link elasticity"},
                        "correct_option": "A",
                        "explanation": "The skew-symmetry of (dot_M - 2*C) guarantees that internal Coriolis and centrifugal forces do no net virtual work, reflecting conservation of energy."
                    },
                    {
                        "id": 11222, "question_number": 2,
                        "question": "What is the primary role of Computed Torque Control in multi-axis robot control?",
                        "options": {"A": "Linearizing non-linear dynamics via model-based feedback cancellation", "B": "Reducing battery voltage", "C": "Eliminating position sensors", "D": "Operating in open-loop mode"},
                        "correct_option": "A",
                        "explanation": "Computed torque control applies feedback linearization to cancel non-linearities and decouple inter-joint interactions."
                    },
                    {
                        "id": 11223, "question_number": 3,
                        "question": "Which term in the robot dynamic model scales quadratically with joint velocity?",
                        "options": {"A": "Gravity torque G(q)", "B": "Centrifugal forces in C(q, dot_q)*dot_q", "C": "Inertia M(q)*ddot_q", "D": "Static friction"},
                        "correct_option": "B",
                        "explanation": "C(q, dot_q)*dot_q contains centrifugal terms proportional to dot_q_i^2 and Coriolis terms proportional to dot_q_i * dot_q_j."
                    },
                    {
                        "id": 11224, "question_number": 4,
                        "question": "Why is feedforward gravity compensation added to joint PID controllers?",
                        "options": {"A": "Eliminates steady-state droop without requiring excessive integral windup", "B": "Reduces robot payload", "C": "Prevents mechanical wear", "D": "Eliminates need for encoders"},
                        "correct_option": "A",
                        "explanation": "Gravity compensation calculates G(q) feedforward torque directly, offsetting gravitational pull and preventing steady-state positional droop."
                    },
                    {
                        "id": 11225, "question_number": 5,
                        "question": "How is the mechanical Lagrangian L defined?",
                        "options": {"A": "L = Kinetic Energy + Potential Energy", "B": "L = Kinetic Energy - Potential Energy", "C": "L = Work * Time", "D": "L = Power / Speed"},
                        "correct_option": "B",
                        "explanation": "In analytical mechanics, the Lagrangian L is defined as kinetic energy minus potential energy: L = K - P."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_rob_02",
                        "subject": "Robotics & Autonomous Systems",
                        "chapter": "Robot Dynamics & Computed Torque Control",
                        "topic": "Robot Dynamics and Control",
                        "keywords": ["robot dynamics", "euler-lagrange", "coriolis", "computed torque", "feedback linearization", "pid control"],
                        "definition": "Robot dynamics formulates the relationship between joint actuation torques and the resulting multi-body accelerations.",
                        "formula_or_core_rule": "tau = M(q)*ddot_q + C(q, dot_q)*dot_q + G(q); L = K - P.",
                        "worked_example": "For a single link with mass m and length L under gravity: tau = m*L^2*ddot_theta + m*g*L*cos(theta).",
                        "content": "Dynamic control strategies utilize computed torque control (feedback linearization) to decouple non-linear joint interactions into independent linear channels governed by PD error feedback."
                    }
                ]
            },
            {
                "chapter_id": 1123,
                "chapter_order": 3,
                "title": "Autonomous Navigation, SLAM & Motion Planning",
                "estimated_minutes": 20,
                "summary": "LiDAR/Visual SLAM, EKF state estimation, pose graph optimization, and A* / RRT* path planning.",
                "syllabus": [
                    "Probabilistic state estimation and Extended Kalman Filter (EKF)",
                    "Graph-based SLAM and loop closure detection",
                    "Occupancy grid mapping and ICP scan matching",
                    "Global path planning (A*) and sampling-based planning (RRT, RRT*)"
                ],
                "core_formulas": [
                    "x_k|k = x_k|k-1 + K_k * (z_k - h(x_k|k-1))",
                    "f(n) = g(n) + h(n) (A* Heuristic)",
                    "J_graph = Sum(e_ij^T * Omega_ij * e_ij)"
                ],
                "part1_title": "Simultaneous Localization & Mapping (SLAM)",
                "part1_content": "SLAM addresses the challenge of building a map of an unknown environment while tracking the robot's pose within it. Graph-based SLAM represents poses as graph nodes and spatial sensor constraints as edges, applying non-linear least squares to achieve global consistency upon loop closure.",
                "part2_title": "Path Planning & Obstacle Avoidance",
                "part2_content": "A* search provides resolution-optimal paths on discretized grids using admissible heuristics. In high-dimensional configuration spaces, RRT and asymptotically optimal RRT* build collision-free trees by randomly sampling points and rewiring local edges.",
                "tip": "RRT is only probabilistically complete, whereas RRT* introduces edge rewiring to achieve asymptotic optimality.",
                "mcqs": [
                    {
                        "id": 11231, "question_number": 1,
                        "question": "What is the key advantage of RRT* over standard RRT in autonomous motion planning?",
                        "options": {"A": "RRT* avoids collision checks", "B": "RRT* rewires local tree branches to guarantee asymptotic optimality", "C": "RRT* only runs on 2D grids", "D": "RRT* removes sensor noise"},
                        "correct_option": "B",
                        "explanation": "Standard RRT finds a feasible path quickly, while RRT* introduces local neighbor rewiring that provably converges to the shortest path."
                    },
                    {
                        "id": 11232, "question_number": 2,
                        "question": "What is the function of loop closure in graph-based SLAM?",
                        "options": {"A": "Shuts down drive motors", "B": "Recognizes previously visited locations to eliminate accumulated odometry drift", "C": "Spins LiDAR 360 degrees", "D": "Resets battery monitoring"},
                        "correct_option": "B",
                        "explanation": "Recognizing a previously visited location creates a loop closure constraint edge, allowing graph optimization to eliminate accumulated dead-reckoning drift."
                    },
                    {
                        "id": 11233, "question_number": 3,
                        "question": "What property must the heuristic h(n) satisfy for A* search to guarantee an optimal path?",
                        "options": {"A": "Must overestimate distance", "B": "Must be admissible (never overestimate true cost to goal)", "C": "Must equal zero everywhere", "D": "Must be non-linear"},
                        "correct_option": "B",
                        "explanation": "An admissible heuristic never overestimates the true remaining distance, ensuring A* never overlooks shorter paths."
                    },
                    {
                        "id": 11234, "question_number": 4,
                        "question": "Which algorithm registers overlapping LiDAR point clouds into rigid body transforms (R, t)?",
                        "options": {"A": "Iterative Closest Point (ICP)", "B": "Fast Fourier Transform", "C": "QuickSort", "D": "Backpropagation"},
                        "correct_option": "A",
                        "explanation": "Iterative Closest Point (ICP) iteratively pairs nearest points between consecutive scans to estimate relative rotation and translation."
                    },
                    {
                        "id": 11235, "question_number": 5,
                        "question": "In the Extended Kalman Filter (EKF), how are non-linear models linearized?",
                        "options": {"A": "First-order Taylor series expansion via Jacobian matrices", "B": "Fourier series", "C": "Monte Carlo sampling", "D": "Lagrangian multipliers"},
                        "correct_option": "A",
                        "explanation": "The EKF linearizes non-linear dynamics and measurement functions by computing first-order Jacobian partial derivative matrices around the current estimate."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_rob_03",
                        "subject": "Robotics & Autonomous Systems",
                        "chapter": "Autonomous Navigation, SLAM & Motion Planning",
                        "topic": "SLAM and Motion Planning",
                        "keywords": ["slam", "navigation", "kalman filter", "ekf", "rrt", "a*", "occupancy grid", "loop closure", "lidar"],
                        "definition": "Simultaneous Localization and Mapping (SLAM) is the computational problem of constructing or updating a map of an unknown environment while tracking an agent's location within it.",
                        "formula_or_core_rule": "Kalman Update: x = x_pred + K*(z - h(x_pred)); A*: f(n) = g(n) + h(n).",
                        "worked_example": "In A* search with current cost g=10 and Euclidean distance heuristic h=5: f = 10 + 5 = 15.",
                        "content": "Autonomous mobile navigation combines SLAM probabilistic state estimation with sampling-based motion planning (RRT/RRT*) and heuristic grid search (A*) to achieve collision-free trajectory execution."
                    }
                ]
            }
        ]
    })

    return subs

