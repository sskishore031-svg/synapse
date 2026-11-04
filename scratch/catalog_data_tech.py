# catalog_data_tech.py
# Production-grade subjects 1-10 for SYNAPSE AI Catalog

def get_tech_subjects():
    subjects = []

    # 1. Microelectronics & VLSI Design
    subjects.append({
        "id": "SUB_VLSI",
        "subject_id": 111,
        "name": "Microelectronics & VLSI Design",
        "slug": "vlsi-design",
        "icon_symbol": "⚡",
        "color_hex": "#4F46E5",
        "category": "Engineering",
        "description": "Silicon semiconductor physics, CMOS logic gates, static timing analysis, and ASIC physical layout.",
        "is_added": False,
        "chapters": [
            {
                "chapter_id": 1111,
                "chapter_order": 1,
                "title": "MOSFET Physics & CMOS Inverter Operation",
                "estimated_minutes": 20,
                "summary": "Carrier drift-diffusion, threshold voltage, subthreshold leakage, and voltage transfer characteristics of the complementary MOS inverter.",
                "syllabus": [
                    "Energy band diagrams and semiconductor doping",
                    "MOS capacitor electrostatics and threshold voltage Vt",
                    "Square-law drain current model: linear vs saturation regimes",
                    "CMOS inverter VTC, noise margins, and propagation delay"
                ],
                "core_formulas": [
                    "Id_sat = 0.5 * mu * Cox * (W/L) * (Vgs - Vt)^2 * (1 + lambda * Vds)",
                    "Cox = epsilon_ox / tox",
                    "tp = 0.69 * Cload * Req",
                    "Noise Margin High (NMH) = Voh - Vih, Noise Margin Low (NML) = Vil - Vol"
                ],
                "part1_title": "MOSFET Operation & Inversion Layer Physics",
                "part1_content": "A Metal-Oxide-Semiconductor Field-Effect Transistor (MOSFET) modulates drain-to-source conductivity by applying an electric field across a thin silicon dioxide dielectric gate. When the gate-to-source potential Vgs exceeds the threshold voltage Vt, minority carriers accumulate at the substrate surface, forming a conductive inversion channel. In the linear region (Vds < Vgs - Vt), current scales linearly with drain bias. When Vds exceeds the pinch-off point (Vds >= Vgs - Vt), carrier velocity saturates, resulting in constant saturation current modulated only by channel-length modulation.",
                "part2_title": "CMOS Inverter Characteristics & Switching Dynamics",
                "part2_content": "The CMOS inverter couples an n-channel pull-down network with a p-channel pull-up network. In steady-state logical '0' or '1', one transistor is strictly cut off, driving static power dissipation near zero. The dynamic switching power satisfies P_dyn = alpha * C_load * Vdd^2 * f. Propagation delay tp is governed by the charging and discharging of the lump sum load capacitance through the equivalent channel on-resistance Req.",
                "tip": "In VLSI exams, remember that doubling channel width W halves on-resistance Req but doubles gate input capacitance Cg.",
                "mcqs": [
                    {
                        "id": 11111,
                        "question_number": 1,
                        "question": "In an n-channel MOSFET operating in the saturation region, what causes the drain current to pinch off?",
                        "options": {
                            "A": "Vgs drops below the threshold voltage Vt",
                            "B": "The gate oxide breaks down under excessive vertical electric field",
                            "C": "Vds reaches Vgs - Vt, reducing channel inversion depth to zero at the drain edge",
                            "D": "Thermal run-away occurs due to high junction temperature"
                        },
                        "correct_option": "C",
                        "explanation": "Pinch-off occurs when the local gate-to-channel potential difference at the drain end falls below the threshold voltage (Vgd < Vt or Vds >= Vgs - Vt), depleting free inversion carriers near the drain."
                    },
                    {
                        "id": 11112,
                        "question_number": 2,
                        "question": "What is the primary formula for dynamic power dissipation in a CMOS logic gate?",
                        "options": {
                            "A": "P = Vdd * I_leak",
                            "B": "P = alpha * Cload * Vdd^2 * f",
                            "C": "P = 0.5 * Req * I_max^2",
                            "D": "P = Cload * Vdd * f^2"
                        },
                        "correct_option": "B",
                        "explanation": "Dynamic power equals the energy required to charge the load capacitance (Cload * Vdd^2) multiplied by switching activity factor alpha and operating frequency f."
                    },
                    {
                        "id": 11113,
                        "question_number": 3,
                        "question": "Why is the pMOS transistor typically sized 2 to 3 times wider than the nMOS transistor in a symmetric CMOS inverter?",
                        "options": {
                            "A": "pMOS has higher oxide capacitance per unit area",
                            "B": "Hole mobility in silicon is approximately 2 to 3 times lower than electron mobility",
                            "C": "nMOS suffers from greater subthreshold leakage",
                            "D": "pMOS requires higher supply voltage to conduct"
                        },
                        "correct_option": "B",
                        "explanation": "Because electron mobility (mu_n) is roughly 2.5x higher than hole mobility (mu_p), the pMOS channel width (W_p) must be widened to match the pull-up current to the nMOS pull-down current."
                    },
                    {
                        "id": 11114,
                        "question_number": 4,
                        "question": "Which parameter directly defines the noise margin high (NMH) of a digital inverter?",
                        "options": {
                            "A": "Voh - Vih",
                            "B": "Vil - Vol",
                            "C": "Vdd - Vss",
                            "D": "Voh - Vol"
                        },
                        "correct_option": "A",
                        "explanation": "NMH represents the tolerance of high logic levels against negative noise spikes and is mathematically defined as Voh (minimum output high) minus Vih (minimum recognized input high)."
                    },
                    {
                        "id": 11115,
                        "question_number": 5,
                        "question": "What happens to MOSFET threshold voltage Vt as the reverse substrate-to-source bias Vsb increases?",
                        "options": {
                            "A": "Vt decreases linearly",
                            "B": "Vt increases due to the body effect",
                            "C": "Vt remains perfectly invariant",
                            "D": "Vt drops to zero"
                        },
                        "correct_option": "B",
                        "explanation": "The body effect increases the depletion layer charge under the gate when the body is reverse-biased relative to the source, requiring a larger gate potential Vt to invert the channel."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_vlsi_01",
                        "subject": "Microelectronics & VLSI Design",
                        "chapter": "MOSFET Physics & CMOS Inverter Operation",
                        "topic": "CMOS Inverter Characteristics",
                        "keywords": ["cmos", "mosfet", "inverter", "vtc", "threshold voltage", "noise margin", "propagation delay"],
                        "definition": "The complementary metal-oxide-semiconductor (CMOS) inverter is the fundamental building block of modern digital integrated circuits, utilizing complementary nMOS and pMOS pairs.",
                        "formula_or_core_rule": "Dynamic Power P_dyn = alpha * C_load * Vdd^2 * f; Propagation delay tp = 0.69 * C_load * R_eq.",
                        "worked_example": "For a 1.0V CMOS gate with C_load = 50 fF, switching frequency f = 2 GHz, and activity alpha = 0.1: P_dyn = 0.1 * 50e-15 * (1.0)^2 * 2e9 = 10 uW.",
                        "content": "In digital VLSI, complementary MOS technology achieves negligible static power dissipation because nMOS and pMOS never conduct simultaneously in static logic states. Dynamic power dissipation dominates active clock cycles, proportional to capacitance and the square of supply voltage."
                    }
                ]
            },
            {
                "chapter_id": 1112,
                "chapter_order": 2,
                "title": "Digital Logic Synthesis & Static Timing Analysis (STA)",
                "estimated_minutes": 20,
                "summary": "RTL behavioral synthesis, standard cell mapping, clock skew, setup and hold time constraints, and critical path optimization.",
                "syllabus": [
                    "RTL to gate-level synthesis transformations",
                    "Flip-flop timing parameters: Tsu, Thold, Tcq",
                    "Setup slack calculation and maximum operating frequency",
                    "Hold slack calculation and race condition mitigation"
                ],
                "core_formulas": [
                    "T_period >= T_cq + T_comb_max + T_setup - T_skew",
                    "Setup Slack = (T_period + T_skew) - (T_cq + T_comb_max + T_setup)",
                    "Hold Slack = (T_cq + T_comb_min) - (T_hold + T_skew)",
                    "Maximum Clock Frequency F_max = 1 / T_period_min"
                ],
                "part1_title": "Static Timing Analysis Foundations",
                "part1_content": "Static Timing Analysis (STA) exhaustively verifies the timing performance of a synchronous digital design across all possible input transitions without dynamic testbench vectors. The core sequential model relies on edge-triggered D flip-flops driven by a distributed clock tree. Setup time (Tsu) dictates the minimum duration data must remain stable before the clock trigger edge. Hold time (Thold) dictates the minimum duration data must remain stable after the clock edge to avoid metastability.",
                "part2_title": "Setup & Hold Slack Optimization",
                "part2_content": "A setup violation occurs when combinational delay between launch and capture registers is too long, causing data to arrive after the capture setup window. Setup violations can be resolved by reducing clock frequency, retiming, or cell sizing. Conversely, hold violations occur when data propagation is too fast (Tcq + Tcomb_min < Thold + Tskew), allowing the new data to overwrite the old data during the same clock edge. Hold violations cannot be solved by changing clock frequency and require buffer insertion.",
                "tip": "Hold time violations are fatal post-silicon bugs because lowering clock frequency does NOT fix hold timing.",
                "mcqs": [
                    {
                        "id": 11121,
                        "question_number": 1,
                        "question": "What is the consequence of a hold time violation in a synchronous digital circuit?",
                        "options": {
                            "A": "The circuit runs at a lower maximum clock frequency",
                            "B": "The capturing register samples the current data value rather than the previous cycle's data, causing catastrophic logical failure regardless of clock speed",
                            "C": "Dynamic power dissipation doubles",
                            "D": "Electromigration occurs in metal interconnects"
                        },
                        "correct_option": "B",
                        "explanation": "Hold violations represent race conditions where new data arrives too quickly and corrupts the hold window of the previous data. Since hold constraints are independent of clock period T_period, lowering the clock frequency does not fix the failure."
                    },
                    {
                        "id": 11122,
                        "question_number": 2,
                        "question": "If T_cq = 200 ps, T_comb_max = 800 ps, T_setup = 150 ps, and clock skew T_skew = 50 ps, what is the minimum required clock period T_period?",
                        "options": {
                            "A": "1100 ps",
                            "B": "1200 ps",
                            "C": "1150 ps",
                            "D": "1000 ps"
                        },
                        "correct_option": "A",
                        "explanation": "T_period >= T_cq + T_comb_max + T_setup - T_skew = 200 + 800 + 150 - 50 = 1100 ps."
                    },
                    {
                        "id": 11123,
                        "question_number": 3,
                        "question": "Which standard technique is commonly employed during physical design to fix hold time violations?",
                        "options": {
                            "A": "Inserting delay delay buffers along the fast data path",
                            "B": "Increasing supply voltage Vdd",
                            "C": "Increasing the global clock frequency",
                            "D": "Removing flip-flop reset logic"
                        },
                        "correct_option": "A",
                        "explanation": "Inserting delay buffers into the data path increases T_comb_min, ensuring that T_cq + T_comb_min exceeds Thold + T_skew without affecting setup paths elsewhere."
                    },
                    {
                        "id": 11124,
                        "question_number": 4,
                        "question": "What does negative setup slack indicate in a timing report?",
                        "options": {
                            "A": "The circuit operates faster than desired",
                            "B": "A timing violation where data arrives too late at the capture register",
                            "C": "Excessive static leakage current",
                            "D": "All signals are perfectly synchronized"
                        },
                        "correct_option": "B",
                        "explanation": "Slack = Required Time - Arrival Time. A negative setup slack signifies that the signal arrives after the required setup window, meaning the design cannot run reliably at the targeted clock frequency."
                    },
                    {
                        "id": 11125,
                        "question_number": 5,
                        "question": "What is clock jitter in static timing analysis?",
                        "options": {
                            "A": "Spatial variation in clock arrival time across different registers",
                            "B": "Temporal cycle-to-cycle variation of the clock edge position from an ideal oscillator",
                            "C": "Voltage drop across the ground rail",
                            "D": "Interconnect capacitive coupling between adjacent signal wires"
                        },
                        "correct_option": "B",
                        "explanation": "Clock jitter is the temporal variation of clock edges over time caused by phase noise in PLLs and supply ripple, distinct from clock skew, which is spatial difference across physical locations."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_vlsi_02",
                        "subject": "Microelectronics & VLSI Design",
                        "chapter": "Digital Logic Synthesis & Static Timing Analysis (STA)",
                        "topic": "Setup and Hold Time Analysis",
                        "keywords": ["sta", "static timing analysis", "setup time", "hold time", "slack", "clock skew", "jitter"],
                        "definition": "Static Timing Analysis (STA) verifies synchronous performance by evaluating setup and hold time constraints across every register-to-register path without dynamic simulation.",
                        "formula_or_core_rule": "Setup: T_period >= T_cq + T_comb_max + T_setup - T_skew. Hold: T_cq + T_comb_min >= T_hold + T_skew.",
                        "worked_example": "If required time is 2.5 ns and arrival time is 2.8 ns, setup slack is 2.5 - 2.8 = -0.3 ns (300 ps violation).",
                        "content": "STA checks all timing paths: register-to-register, input-to-register, register-to-output, and clock gating paths. Setup constraints establish maximum frequency, while hold constraints establish minimum data path latency."
                    }
                ]
            },
            {
                "chapter_id": 1113,
                "chapter_order": 3,
                "title": "Physical Design: Floorplanning, Placement & Routing",
                "estimated_minutes": 20,
                "summary": "Hierarchical chip floorplanning, power grid distribution, clock tree synthesis (CTS), detailed global routing, and DRC/LVS closure.",
                "syllabus": [
                    "Die area estimation, core aspect ratios, and macro placement",
                    "Power distribution network (PDN) and IR drop minimization",
                    "Clock Tree Synthesis (CTS): H-tree, mesh, and clock skew balancing",
                    "Design Rule Checking (DRC) and Layout Versus Schematic (LVS)"
                ],
                "core_formulas": [
                    "Core Utilization = Total Standard Cell Area / Total Core Area",
                    "IR Drop = Integral of (J_current * rho_sheet * dl)",
                    "Elmore Delay = Sum(R_i * C_downstream_i)",
                    "C_total = C_area + 2 * C_sidewall + 2 * C_cross_coupling"
                ],
                "part1_title": "Floorplanning & Power Grid Architecture",
                "part1_content": "Physical design transforms a gate-level netlist into a geometric GDSII mask layout. Floorplanning dictates the physical envelope of the chip core, placing input/output pads, analog mixed-signal blocks, and SRAM hard macros. The Power Distribution Network (PDN) utilizes thick top-layer metals (e.g., M8/M9) to distribute Vdd and Vss across an orthogonal mesh, ensuring that dynamic current surges do not cause localized supply droop (IR drop) or ground bounce exceeding design margins.",
                "part2_title": "Clock Tree Synthesis & Routing Closure",
                "part2_content": "Clock Tree Synthesis (CTS) inserts symmetrical inverter buffers to distribute the clock signal to millions of sequential registers with minimal insertion delay and balanced skew. Classical CTS topologies include H-tree fractal networks and clock meshes. Routing is partitioned into Global Routing (which assigns routing congestion grids) and Detailed Routing (which realizes physical metal tracks obeying nanoscale foundry Design Rules (DRC)).",
                "tip": "Always balance high-layer power routing with signal routing tracks to avoid routing congestion hotspots.",
                "mcqs": [
                    {
                        "id": 11131,
                        "question_number": 1,
                        "question": "What is the primary purpose of Clock Tree Synthesis (CTS) in physical design?",
                        "options": {
                            "A": "To convert sequential logic into combinational logic",
                            "B": "To distribute the clock to all sequential elements while minimizing clock skew and latency",
                            "C": "To route power and ground rings around the core area",
                            "D": "To eliminate the need for standard cell placement"
                        },
                        "correct_option": "B",
                        "explanation": "CTS synthesizes an optimized buffer tree to deliver synchronous clock edges to all storage cells simultaneously, minimizing skew and clock tree power."
                    },
                    {
                        "id": 11132,
                        "question_number": 2,
                        "question": "What does an LVS (Layout Versus Schematic) check verify in the VLSI physical design flow?",
                        "options": {
                            "A": "That geometric mask features satisfy foundry minimum width and spacing rules",
                            "B": "That the electrical netlist extracted from geometric polygons matches the gate-level schematic netlist identically",
                            "C": "That the chip does not exceed 100 degrees Celsius",
                            "D": "That total wire length is minimized"
                        },
                        "correct_option": "B",
                        "explanation": "LVS extracts electrical connectivity from the physical layout geometries and performs graph isomorphism against the schematic netlist to prove functional equivalence."
                    },
                    {
                        "id": 11133,
                        "question_number": 3,
                        "question": "Why are top metal layers (e.g., M7-M9) preferred for power grid and global clock routing?",
                        "options": {
                            "A": "They are thinner and have higher sheet resistance",
                            "B": "They are significantly thicker, offering lower resistance and higher current-carrying capacity against electromigration",
                            "C": "They can only connect directly to transistors without vias",
                            "D": "They prevent parasitic capacitance entirely"
                        },
                        "correct_option": "B",
                        "explanation": "Upper metal layers in advanced sub-micron processes are thick and wide, providing low sheet resistance essential for low IR drop and reliable high-current power distribution."
                    },
                    {
                        "id": 11134,
                        "question_number": 4,
                        "question": "What is the consequence of severe IR drop on a chip's power network?",
                        "options": {
                            "A": "Logic gate propagation delays increase, potentially triggering setup time violations",
                            "B": "Clock frequency increases uncontrollably",
                            "C": "Chip package inductance drops to zero",
                            "D": "Threshold voltages of all transistors invert"
                        },
                        "correct_option": "A",
                        "explanation": "Localized IR drop reduces the effective supply voltage across standard cells, which diminishes drain current and increases gate propagation delay, causing late signal arrival and setup failures."
                    },
                    {
                        "id": 11135,
                        "question_number": 5,
                        "question": "What causes crosstalk noise between adjacent metal tracks in deep submicron routing?",
                        "options": {
                            "A": "Cross-coupling mutual capacitance and mutual inductance between parallel running interconnects",
                            "B": "Substrate leakage through reverse-biased diodes",
                            "C": "Thermal noise in poly resistors",
                            "D": "Laser radiation from optical lithography"
                        },
                        "correct_option": "A",
                        "explanation": "As aspect ratios of metal wires become tall and narrow, sidewall coupling capacitance between adjacent wires dominates, inducing spurious noise glitches and delay uncertainty on neighbor nets."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_vlsi_03",
                        "subject": "Microelectronics & VLSI Design",
                        "chapter": "Physical Design: Floorplanning, Placement & Routing",
                        "topic": "Physical Design Flow & DRC/LVS",
                        "keywords": ["physical design", "floorplanning", "placement", "routing", "cts", "clock tree", "drc", "lvs", "ir drop"],
                        "definition": "Physical design translates the logical netlist into foundry-ready geometric mask layouts through floorplanning, placement, clock tree synthesis, and detailed routing.",
                        "formula_or_core_rule": "Elmore delay T_elmore = Sum(R_i * C_downstream_i); Core utilization = Cell_Area / Core_Area.",
                        "worked_example": "A core of 2 mm x 2 mm (4 mm^2) with standard cells occupying 2.8 mm^2 exhibits a 70% core utilization factor.",
                        "content": "Physical design closure requires satisfying both geometric Design Rule Checks (DRC) mandated by foundry lithography limits and Layout Versus Schematic (LVS) electrical equivalency."
                    }
                ]
            }
        ]
    })

    # 2. Robotics & Autonomous Systems
    subjects.append({
        "id": "SUB_ROBOTICS",
        "subject_id": 112,
        "name": "Robotics & Autonomous Systems",
        "slug": "robotics-autonomous-systems",
        "icon_symbol": "🤖",
        "color_hex": "#0284C7",
        "category": "Engineering",
        "description": "Spatial kinematics, dynamics, PID trajectory control, SLAM, and autonomous robot navigation.",
        "is_added": False,
        "chapters": [
            {
                "chapter_id": 1121,
                "chapter_order": 1,
                "title": "Spatial Transformations & Kinematics",
                "estimated_minutes": 20,
                "summary": "Rotation matrices, Euler angles, quaternions, Denavit-Hartenberg (D-H) parameters, and forward/inverse kinematics.",
                "syllabus": [
                    "SO(3) rotation matrices and unit quaternions",
                    "SE(3) homogeneous transformation matrices",
                    "Denavit-Hartenberg (D-H) convention for open kinematic chains",
                    "Analytical vs numerical inverse kinematics (Jacobian pseudo-inverse)"
                ],
                "core_formulas": [
                    "T_homogeneous = [R, p; 0, 1]",
                    "q = [cos(theta/2), u * sin(theta/2)] (Unit Quaternion)",
                    "v = J(theta) * dot_theta (Manipulator Jacobian)",
                    "dot_theta = J_pinv * v + (I - J_pinv * J) * dot_theta_null"
                ],
                "part1_title": "Spatial Representation & Homogeneous Coordinates",
                "part1_content": "Rigid body posture in three-dimensional space requires defining both orientation in the Special Orthogonal Group SO(3) and position in R^3. While 3x3 rotation matrices preserve orthogonality (R * R^T = I, det(R) = +1), they exhibit nine parameters for three degrees of freedom. Unit quaternions represent spatial rotation without gimbal lock singularities using four parameters on the unit 3-sphere. Homogeneous transformation matrices combine rotation and translation into a unified 4x4 matrix enabling coordinate chaining.",
                "part2_title": "Denavit-Hartenberg Convention & Differential Kinematics",
                "part2_content": "The Denavit-Hartenberg (D-H) convention establishes standardized coordinate frames along serial robotic linkages using four geometric parameters: link length a, link twist alpha, link offset d, and joint angle theta. Forward kinematics computes end-effector pose from joint coordinates. The Manipulator Jacobian J(theta) relates joint velocities to Cartesian linear and angular velocities: v = J * dot_theta. Singularities occur when det(J) = 0, causing loss of motion capability in one or more Cartesian directions.",
                "tip": "Singularities occur when the Manipulator Jacobian drops rank, leading to infinite joint velocities for finite Cartesian end-effector motions.",
                "mcqs": [
                    {
                        "id": 11211,
                        "question_number": 1,
                        "question": "What is the primary advantage of unit quaternions over Euler angles in spatial rotation representation?",
                        "options": {
                            "A": "Quaternions require fewer than three numerical values",
                            "B": "Quaternions completely avoid gimbal lock representation singularities",
                            "C": "Quaternions only represent 2D planar motions",
                            "D": "Quaternions eliminate the need for translational coordinates"
                        },
                        "correct_option": "B",
                        "explanation": "Euler angle representations suffer from mathematical singularities (gimbal lock) when two rotation axes align. Unit quaternions provide a singularity-free, globally smooth parameterization of SO(3)."
                    },
                    {
                        "id": 11212,
                        "question_number": 2,
                        "question": "In the standard Denavit-Hartenberg (D-H) convention, which axis is defined along the axis of revolution or translation of joint i+1?",
                        "options": {
                            "A": "X_i",
                            "B": "Y_i",
                            "C": "Z_i",
                            "D": "W_i"
                        },
                        "correct_option": "C",
                        "explanation": "By D-H convention, the Z_i axis is aligned collinear with the actuation axis (revolute rotation or prismatic sliding) of joint i+1."
                    },
                    {
                        "id": 11213,
                        "question_number": 3,
                        "question": "What mathematical condition defines a kinematic singularity in an industrial robot arm?",
                        "options": {
                            "A": "The Manipulator Jacobian matrix loses full rank (det(J) = 0 for square Jacobians)",
                            "B": "The robot speed drops to zero",
                            "C": "The control loop frequency drops below 1 kHz",
                            "D": "The end-effector payload is reduced to zero"
                        },
                        "correct_option": "A",
                        "explanation": "Kinematic singularities occur when the Jacobian matrix J(q) drops rank, preventing the robot from moving its end-effector in certain Cartesian directions and causing inverse kinematics to diverge."
                    },
                    {
                        "id": 11214,
                        "question_number": 4,
                        "question": "What mathematical relationship links joint velocity vector dot_q to Cartesian twist v?",
                        "options": {
                            "A": "v = J(q) * dot_q",
                            "B": "dot_q = J(q) * v",
                            "C": "v = J(q)^2 * dot_q",
                            "D": "v = dJ/dt * q"
                        },
                        "correct_option": "A",
                        "explanation": "The Manipulator Jacobian J(q) acts as the linear mapping operator between joint velocity space and Cartesian task space velocities: v = J(q) * dot_q."
                    },
                    {
                        "id": 11215,
                        "question_number": 5,
                        "question": "What is the Jacobian pseudo-inverse formula used for redundant manipulator inverse kinematics?",
                        "options": {
                            "A": "J^T * (J * J^T)^(-1)",
                            "B": "(J^T * J)^(-1)",
                            "C": "J * J^T",
                            "D": "det(J) * I"
                        },
                        "correct_option": "A",
                        "explanation": "For redundant manipulators where task dimensions m < joint degrees of freedom n, the right Moore-Penrose pseudo-inverse is J_pinv = J^T * (J * J^T)^(-1), minimizing the norm of joint velocities."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_rob_01",
                        "subject": "Robotics & Autonomous Systems",
                        "chapter": "Spatial Transformations & Kinematics",
                        "topic": "Manipulator Kinematics & Jacobians",
                        "keywords": ["robotics", "kinematics", "denavit-hartenberg", "quaternion", "jacobian", "singularity", "so3"],
                        "definition": "Robot kinematics studies the geometry of motion with respect to a fixed reference coordinate system without regard to the forces causing motion.",
                        "formula_or_core_rule": "v = J(q) * dot_q; T_homogeneous = [R, p; 0, 1].",
                        "worked_example": "For a 2-link planar arm with lengths L1=1m, L2=1m at angles theta1=0, theta2=90: x = L1*cos(0) + L2*cos(90) = 1m; y = 1m.",
                        "content": "Spatial transformations govern robot kinematics through SO(3) rotations and SE(3) homogeneous coordinates. Denavit-Hartenberg frames systematicize serial linkage modeling, while the Jacobian matrix maps joint velocities to operational space."
                    }
                ]
            },
            {
                "chapter_id": 1122,
                "chapter_order": 2,
                "title": "Robot Dynamics, Control & PID Trajectory Tracking",
                "estimated_minutes": 20,
                "summary": "Euler-Lagrange equations of motion, inertia matrix, Coriolis torque, gravity compensation, and computed torque control.",
                "syllabus": [
                    "Euler-Lagrange formulation of rigid body dynamics",
                    "Mass-inertia matrix M(q), Coriolis C(q, dot_q), and gravity vector G(q)",
                    "Independent joint PID control and feedforward compensation",
                    "Computed Torque Control (Feedback Linearization)"
                ],
                "core_formulas": [
                    "tau = M(q) * ddot_q + C(q, dot_q) * dot_q + G(q)",
                    "L = K_kinetic - P_potential",
                    "d/dt(dL/d(dot_q)) - dL/dq = tau",
                    "tau_cmd = M(q) * (ddot_q_des + Kp * e + Kd * dot_e) + C * dot_q + G"
                ],
                "part1_title": "Euler-Lagrange Robot Dynamic Formulation",
                "part1_content": "Robot dynamics analyzes the relationship between joint actuation torques and the resulting accelerations. The Euler-Lagrange equations derive the equations of motion from kinetic energy K and potential energy P via Lagrangian L = K - P. The resulting equations of motion take the canonical matrix form: M(q) * ddot_q + C(q, dot_q) * dot_q + G(q) = tau, where M(q) is symmetric positive-definite, C(q, dot_q) contains Coriolis and centrifugal terms, and G(q) represents gravitational torque.",
                "part2_title": "Computed Torque & Trajectory Control",
                "part2_content": "Independent joint PID controllers treat inter-joint dynamic couplings as unmodeled disturbances, suffering tracking degradation during high-speed multi-axis maneuvers. Computed Torque Control utilizes feedback linearization by evaluating the non-linear dynamic model in real-time, canceling non-linear Coriolis, centrifugal, and gravitational forces, and converting the multi-input multi-output (MIMO) robotic system into decoupled linear double-integrator subsystems governed by PD error feedback.",
                "tip": "M(q) is always symmetric and positive-definite; dot_M - 2*C is always a skew-symmetric matrix, a property crucial for Lyapunov stability proofs.",
                "mcqs": [
                    {
                        "id": 11221,
                        "question_number": 1,
                        "question": "In the standard robotic equation of motion tau = M(q)*ddot_q + C(q, dot_q)*dot_q + G(q), what fundamental physical property is exhibited by the matrix (dot_M - 2*C)?",
                        "options": {
                            "A": "It is an identity matrix",
                            "B": "It is skew-symmetric, meaning x^T * (dot_M - 2*C) * x = 0 for any vector x",
                            "C": "It is diagonal with positive eigenvalues",
                            "D": "It equals the inertia matrix squared"
                        },
                        "correct_option": "B",
                        "explanation": "The skew-symmetry of (dot_M - 2*C) reflects conservation of energy in rigid body mechanics and is widely used to prove passivity and asymptotic stability in Lyapunov controller design."
                    },
                    {
                        "id": 11222,
                        "question_number": 2,
                        "question": "What is the primary mechanism of Computed Torque Control in robot manipulators?",
                        "options": {
                            "A": "Linearizing the plant using real-time dynamic inverse model compensation to decouple joint axes",
                            "B": "Limiting actuator motor currents to safe thresholds",
                            "C": "Running simple on-off bang-bang controllers",
                            "D": "Eliminating positional feedback sensors"
                        },
                        "correct_option": "A",
                        "explanation": "Computed torque control applies feedback linearization: it calculates non-linear inertia, Coriolis, and gravity torques online, canceling non-linearities and reducing each joint to an equivalent linear unit-mass system."
                    },
                    {
                        "id": 11223,
                        "question_number": 3,
                        "question": "What term in the robotic equations of motion scales quadratically with joint velocities (dot_q^2)?",
                        "options": {
                            "A": "Gravitational torque G(q)",
                            "B": "Centrifugal torque within C(q, dot_q)*dot_q",
                            "C": "Inertial torque M(q)*ddot_q",
                            "D": "Static friction torque"
                        },
                        "correct_option": "B",
                        "explanation": "The velocity-dependent vector C(q, dot_q)*dot_q consists of Coriolis terms (products of different joint velocities dot_q_i * dot_q_j) and centrifugal terms (squared velocities dot_q_i^2)."
                    },
                    {
                        "id": 11224,
                        "question_number": 4,
                        "question": "Why is pure gravity compensation often added to standard joint PID feedback?",
                        "options": {
                            "A": "To remove steady-state droop error without requiring excessive integral gain windup",
                            "B": "To increase robot link mass",
                            "C": "To prevent high-frequency vibration",
                            "D": "To avoid friction identification"
                        },
                        "correct_option": "A",
                        "explanation": "Gravity compensation computes G(q) feedforward torque directly, offsetting the static gravitational pull and eliminating steady-state positional droop without causing integral windup instability."
                    },
                    {
                        "id": 11225,
                        "question_number": 5,
                        "question": "What is the definition of the mechanical Lagrangian L?",
                        "options": {
                            "A": "L = Kinetic Energy (K) + Potential Energy (P)",
                            "B": "L = Kinetic Energy (K) - Potential Energy (P)",
                            "C": "L = Work done * Time",
                            "D": "L = Power / Velocity"
                        },
                        "correct_option": "B",
                        "explanation": "In analytical classical mechanics, the Lagrangian function L is defined as the difference between total kinetic energy K and total potential energy P: L = K - P."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_rob_02",
                        "subject": "Robotics & Autonomous Systems",
                        "chapter": "Robot Dynamics, Control & PID Trajectory Tracking",
                        "topic": "Euler-Lagrange Dynamics and Computed Torque",
                        "keywords": ["robot dynamics", "euler-lagrange", "coriolis", "computed torque", "feedback linearization", "pid control"],
                        "definition": "Robot dynamics formulates the non-linear relationship between joint actuation forces and structural multi-body accelerations.",
                        "formula_or_core_rule": "tau = M(q)*ddot_q + C(q, dot_q)*dot_q + G(q); L = K - P.",
                        "worked_example": "In a 1-DOF link with mass m and length L under gravity: tau = m*L^2*ddot_theta + m*g*L*cos(theta).",
                        "content": "Dynamic control strategies utilize computed torque control (feedback linearization) to decouple non-linear joint interactions into independent linear channels governed by PD error feedback."
                    }
                ]
            },
            {
                "chapter_id": 1123,
                "chapter_order": 3,
                "title": "Autonomous Navigation, SLAM & Motion Planning",
                "estimated_minutes": 20,
                "summary": "LiDAR/Visual SLAM, Extended Kalman Filter (EKF), graph-based SLAM, A* / RRT* motion planning, and obstacle avoidance.",
                "syllabus": [
                    "Probabilistic state estimation and Extended Kalman Filters (EKF)",
                    "Simultaneous Localization and Mapping (SLAM): Pose graph optimization",
                    "Occupancy grid mapping and scan matching (Iterative Closest Point - ICP)",
                    "Global path planning (A*, Dijkstra) and sampling-based algorithms (RRT, RRT*)"
                ],
                "core_formulas": [
                    "x_k|k = x_k|k-1 + K_k * (z_k - h(x_k|k-1)) (Kalman Update)",
                    "K_k = P_k|k-1 * H_k^T * (H_k * P_k|k-1 * H_k^T + R_k)^(-1)",
                    "Cost Function J_graph = Sum(e_ij^T * Omega_ij * e_ij)",
                    "f(n) = g(n) + h(n) (A* Heuristic Evaluation)"
                ],
                "part1_title": "Simultaneous Localization & Mapping (SLAM)",
                "part1_content": "Autonomous mobile navigation requires answering two simultaneous questions without external infrastructure: 'Where am I?' (Localization) and 'What does the environment look like?' (Mapping). The chicken-and-egg dilemma of SLAM is solved using probabilistic state estimation. Modern approaches utilize Graph-based SLAM, wherein robot poses form graph nodes and inter-pose sensor measurements (from LiDAR odometry or visual loop closures) form spatial constraint edges optimized using non-linear least squares.",
                "part2_title": "Path Planning & Obstacle Avoidance",
                "part2_content": "Motion planning generates collision-free trajectories from start to goal poses within the configuration space (C-space). Global graph search algorithms such as A* compute resolution-optimal paths on discretized occupancy grids using admissible heuristics. In high-dimensional spaces, sampling-based algorithms such as Rapidly-exploring Random Trees (RRT and asymptotically optimal RRT*) probabilistically explore the space by growing tree branches toward random samples.",
                "tip": "RRT is only probabilistically complete, whereas RRT* introduces edge rewiring to achieve asymptotic optimality.",
                "mcqs": [
                    {
                        "id": 11231,
                        "question_number": 1,
                        "question": "What is the primary computational advantage of RRT* over standard RRT in autonomous motion planning?",
                        "options": {
                            "A": "RRT* works without collision checking",
                            "B": "RRT* rewires local tree branches as new nodes are sampled, guaranteeing asymptotic optimality (path cost converges to the theoretical minimum)",
                            "C": "RRT* requires discrete grid maps only",
                            "D": "RRT* eliminates sensor noise"
                        },
                        "correct_option": "B",
                        "explanation": "Standard RRT finds a feasible path quickly but rarely the optimal one. RRT* introduces a local neighbor search and edge rewiring step that provably converges to the optimal trajectory as samples approach infinity."
                    },
                    {
                        "id": 11232,
                        "question_number": 2,
                        "question": "What is the role of loop closure detection in modern graph-based SLAM systems?",
                        "options": {
                            "A": "It shuts down the robot's drive motors upon path completion",
                            "B": "It recognizes previously visited locations, adding a global constraint edge that eliminates accumulated dead-reckoning odometry drift",
                            "C": "It loops the LiDAR mirror 360 degrees",
                            "D": "It resets robot battery monitoring"
                        },
                        "correct_option": "B",
                        "explanation": "Dead-reckoning odometry drifts unbounded over time. Recognizing a previously mapped physical location establishes an accurate constraint between distant poses, allowing non-linear graph optimization to correct accumulated drift."
                    },
                    {
                        "id": 11233,
                        "question_number": 3,
                        "question": "In the A* path planning algorithm, what condition must the heuristic function h(n) satisfy to guarantee that the returned path is optimal?",
                        "options": {
                            "A": "h(n) must be strictly greater than the true cost to goal",
                            "B": "h(n) must be admissible (never overestimate the true cost to reach the goal)",
                            "C": "h(n) must equal zero everywhere",
                            "D": "h(n) must be non-linear"
                        },
                        "correct_option": "B",
                        "explanation": "An admissible heuristic never overestimates the true remaining distance to the goal. Admissibility guarantees that A* will find the shortest path without terminating prematurely."
                    },
                    {
                        "id": 11234,
                        "question_number": 4,
                        "question": "What algorithm is universally used in 2D and 3D LiDAR point cloud processing to register consecutive laser scans?",
                        "options": {
                            "A": "Iterative Closest Point (ICP)",
                            "B": "Fast Fourier Transform (FFT)",
                            "C": "Bubble Sort",
                            "D": "Backpropagation"
                        },
                        "correct_option": "A",
                        "explanation": "The Iterative Closest Point (ICP) algorithm iteratively minimizes the geometric squared Euclidean distance between correspondences in two overlapping point clouds to estimate rigid body transform (R, t)."
                    },
                    {
                        "id": 11235,
                        "question_number": 5,
                        "question": "In the Extended Kalman Filter (EKF), what mathematical tool linearizes non-linear motion and observation models?",
                        "options": {
                            "A": "First-order Taylor series expansion via Jacobian matrices",
                            "B": "Fourier series decomposition",
                            "C": "Monte Carlo random sampling",
                            "D": "Lagrange multipliers"
                        },
                        "correct_option": "A",
                        "explanation": "The EKF approximates non-linear process and measurement functions f(x) and h(x) by computing first-order partial derivative Jacobian matrices F and H at the current state estimate."
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

    # Return subjects 1-2 for now
    # We will append 3..10 next
    return subjects

