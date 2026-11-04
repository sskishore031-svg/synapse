# scratch/gen_batch1.py
# Generates subjects 1 to 10 (Tech & Computing)

def get_subjects():
    subs = []
    
    # 1. Microelectronics & VLSI Design (111)
    subs.append({
        "id": "SUB_VLSI",
        "subject_id": 111,
        "name": "Microelectronics & VLSI Design",
        "slug": "vlsi-design",
        "icon_symbol": "⚡",
        "color_hex": "#4F46E5",
        "category": "Engineering",
        "description": "Semiconductor device physics, CMOS digital logic, static timing analysis, and ASIC physical design flow.",
        "is_added": False,
        "chapters": [
            {
                "chapter_id": 1111,
                "chapter_order": 1,
                "title": "MOSFET Physics & CMOS Inverter Operation",
                "estimated_minutes": 20,
                "summary": "Carrier transport, inversion threshold, and switching dynamics of CMOS digital inverters.",
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
                        "question": "In an n-channel MOSFET operating in saturation, what causes the channel inversion depth to pinch off at the drain end?",
                        "options": {"A": "Vgs drops below Vt", "B": "Gate oxide ruptures under high vertical field", "C": "Vds reaches Vgs - Vt, reducing local gate-to-channel potential below threshold", "D": "Thermal runaway occurs"},
                        "correct_option": "C",
                        "explanation": "Pinch-off occurs when the local gate-to-channel potential difference at the drain end falls below the threshold voltage (Vgd < Vt or Vds >= Vgs - Vt), depleting free inversion carriers near the drain."
                    },
                    {
                        "id": 11112,
                        "question_number": 2,
                        "question": "What is the primary governing equation for CMOS gate dynamic power dissipation?",
                        "options": {"A": "P = Vdd * I_leak", "B": "P = alpha * Cload * Vdd^2 * f", "C": "P = 0.5 * Req * I_max^2", "D": "P = Cload * Vdd * f^2"},
                        "correct_option": "B",
                        "explanation": "Dynamic power equals the energy required to charge load capacitance (Cload * Vdd^2) multiplied by switching activity alpha and frequency f."
                    },
                    {
                        "id": 11113,
                        "question_number": 3,
                        "question": "Why is pMOS channel width typically sized 2 to 3 times larger than nMOS channel width in a CMOS inverter?",
                        "options": {"A": "pMOS has higher gate capacitance", "B": "Hole mobility in silicon is 2 to 3 times lower than electron mobility", "C": "nMOS suffers greater subthreshold leakage", "D": "pMOS requires higher supply voltage"},
                        "correct_option": "B",
                        "explanation": "Because electron mobility (mu_n) is roughly 2.5x higher than hole mobility (mu_p), pMOS width must be enlarged to equalize pull-up and pull-down drive currents."
                    },
                    {
                        "id": 11114,
                        "question_number": 4,
                        "question": "Which voltage difference defines Noise Margin High (NMH) in digital inverters?",
                        "options": {"A": "Voh - Vih", "B": "Vil - Vol", "C": "Vdd - Vss", "D": "Voh - Vol"},
                        "correct_option": "A",
                        "explanation": "NMH measures immunity against negative noise spikes on a logic '1' and equals minimum output high (Voh) minus minimum recognized input high (Vih)."
                    },
                    {
                        "id": 11115,
                        "question_number": 5,
                        "question": "What happens to MOSFET threshold voltage Vt when reverse substrate-to-source bias Vsb increases?",
                        "options": {"A": "Vt decreases linearly", "B": "Vt increases due to the body effect", "C": "Vt remains invariant", "D": "Vt drops to zero"},
                        "correct_option": "B",
                        "explanation": "The body effect increases the depletion charge under the gate when the body is reverse-biased, requiring a higher gate potential to invert the channel."
                    }
                ],
                "rag_chunks": [
                    {
                        "chunk_id": "chunk_vlsi_01",
                        "subject": "Microelectronics & VLSI Design",
                        "chapter": "MOSFET Physics & CMOS Inverter Operation",
                        "topic": "CMOS Inverter Characteristics",
                        "keywords": ["cmos", "mosfet", "inverter", "vtc", "threshold voltage", "noise margin", "propagation delay"],
                        "definition": "The complementary metal-oxide-semiconductor (CMOS) inverter is the fundamental digital logic cell, combining complementary nMOS and pMOS transistors.",
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
                "part1_content": "Static Timing Analysis (STA) exhaustively verifies synchronous performance across all timing paths without dynamic simulation vectors. Setup time (Tsu) dictates how long data must be stable before the clock edge. Hold time (Thold) dictates how long data must remain stable after the clock edge.",
                "part2_title": "Setup & Hold Slack Optimization",
                "part2_content": "Setup violations occur when combinational delay is too long; they can be resolved by lowering clock frequency, sizing gates up, or pipelining. Hold violations occur when data travels too fast; hold violations are frequency-independent and must be fixed by inserting delay buffers on fast paths.",
                "tip": "Hold time violations are fatal post-silicon bugs because lowering clock frequency does NOT fix hold timing.",
                "mcqs": [
                    {
                        "id": 11121,
                        "question_number": 1,
                        "question": "What is the consequence of a hold time violation in a synchronous digital circuit?",
                        "options": {"A": "Circuit runs at lower clock frequency", "B": "Capturing register samples newly launched data in the same clock cycle, corrupting data flow regardless of clock speed", "C": "Dynamic power doubles", "D": "Electromigration occurs in metal interconnects"},
                        "correct_option": "B",
                        "explanation": "Hold violations represent race conditions where new data arrives too quickly and overwrites the hold window of previous data. Lowering clock speed does not fix hold violations."
                    },
                    {
                        "id": 11122,
                        "question_number": 2,
                        "question": "If T_cq = 200 ps, T_comb_max = 800 ps, T_setup = 150 ps, and clock skew T_skew = 50 ps, what is minimum clock period T_period?",
                        "options": {"A": "1100 ps", "B": "1200 ps", "C": "1150 ps", "D": "1000 ps"},
                        "correct_option": "A",
                        "explanation": "T_period >= T_cq + T_comb_max + T_setup - T_skew = 200 + 800 + 150 - 50 = 1100 ps."
                    },
                    {
                        "id": 11123,
                        "question_number": 3,
                        "question": "Which standard technique fixes hold time violations during physical layout?",
                        "options": {"A": "Inserting delay buffers along the fast data path", "B": "Increasing supply voltage Vdd", "C": "Increasing clock frequency", "D": "Removing flip-flop reset logic"},
                        "correct_option": "A",
                        "explanation": "Inserting delay buffers into the fast data path increases T_comb_min, ensuring T_cq + T_comb_min exceeds Thold + T_skew."
                    },
                    {
                        "id": 11124,
                        "question_number": 4,
                        "question": "What does negative setup slack indicate in an STA timing report?",
                        "options": {"A": "Circuit operates faster than desired", "B": "A timing violation where data arrives after the capture setup window", "C": "Excessive static leakage current", "D": "All signals are perfectly synchronized"},
                        "correct_option": "B",
                        "explanation": "Slack = Required Time - Arrival Time. Negative setup slack signifies that signals arrive too late, meaning the design cannot run reliably at the target frequency."
                    },
                    {
                        "id": 11125,
                        "question_number": 5,
                        "question": "What is clock jitter in static timing analysis?",
                        "options": {"A": "Spatial variation in clock arrival across registers", "B": "Temporal cycle-to-cycle variation of clock edge from ideal oscillator", "C": "Voltage drop across ground rail", "D": "Crosstalk capacitance between signal wires"},
                        "correct_option": "B",
                        "explanation": "Clock jitter is the temporal variation of clock edges over time caused by phase noise in PLLs and power supply ripple."
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
                "summary": "Hierarchical chip floorplanning, power distribution networks (PDN), clock tree synthesis (CTS), detailed routing, and DRC/LVS closure.",
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
                        "options": {"A": "Convert sequential logic into combinational logic", "B": "Distribute clock to all sequential elements while minimizing clock skew and latency", "C": "Route power and ground rings around the core area", "D": "Eliminate the need for standard cell placement"},
                        "correct_option": "B",
                        "explanation": "CTS synthesizes an optimized buffer tree to deliver synchronous clock edges to all storage cells simultaneously, minimizing skew and clock tree power."
                    },
                    {
                        "id": 11132,
                        "question_number": 2,
                        "question": "What does an LVS (Layout Versus Schematic) check verify in the VLSI physical design flow?",
                        "options": {"A": "That geometric mask features satisfy foundry minimum width and spacing rules", "B": "That the electrical netlist extracted from geometric polygons matches the gate-level schematic netlist identically", "C": "That the chip does not exceed 100 degrees Celsius", "D": "That total wire length is minimized"},
                        "correct_option": "B",
                        "explanation": "LVS extracts electrical connectivity from layout polygons and performs graph isomorphism against the schematic netlist to prove functional equivalence."
                    },
                    {
                        "id": 11133,
                        "question_number": 3,
                        "question": "Why are top metal layers (e.g., M7-M9) preferred for power grid and global clock routing?",
                        "options": {"A": "They are thinner and have higher sheet resistance", "B": "They are significantly thicker, offering lower resistance and higher current-carrying capacity against electromigration", "C": "They can only connect directly to transistors without vias", "D": "They prevent parasitic capacitance entirely"},
                        "correct_option": "B",
                        "explanation": "Upper metal layers in advanced sub-micron processes are thick and wide, providing low sheet resistance essential for low IR drop and reliable high-current power distribution."
                    },
                    {
                        "id": 11134,
                        "question_number": 4,
                        "question": "What is the consequence of severe IR drop on a chip's power network?",
                        "options": {"A": "Logic gate propagation delays increase, potentially triggering setup time violations", "B": "Clock frequency increases uncontrollably", "C": "Chip package inductance drops to zero", "D": "Threshold voltages of all transistors invert"},
                        "correct_option": "A",
                        "explanation": "Localized IR drop reduces effective supply voltage across standard cells, diminishing drain current and increasing propagation delay, causing late signal arrival."
                    },
                    {
                        "id": 11135,
                        "question_number": 5,
                        "question": "What causes crosstalk noise between adjacent metal tracks in deep submicron routing?",
                        "options": {"A": "Cross-coupling mutual capacitance and mutual inductance between parallel running interconnects", "B": "Substrate leakage through reverse-biased diodes", "C": "Thermal noise in poly resistors", "D": "Laser radiation from optical lithography"},
                        "correct_option": "A",
                        "explanation": "As aspect ratios of metal wires become tall and narrow, sidewall coupling capacitance between adjacent wires dominates, inducing noise glitches and delay uncertainty on neighbor nets."
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

    return subs

