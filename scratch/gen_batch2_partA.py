# scratch/gen_batch2_partA.py
# Subjects 11 to 15: Mechanical, Civil, Aerospace, Biomedical, Chemical Process Engineering

from gen_subjects_1_to_10 import make_ch

def get_subjects_11_to_15():
    subs = []

    # 121. Mechanical Thermodynamics & Fluid Mechanics
    subs.append({
        "id": "SUB_THERMO",
        "subject_id": 121,
        "name": "Mechanical Thermodynamics & Fluid Mechanics",
        "slug": "thermodynamics-fluid-mechanics",
        "icon_symbol": "⚙️",
        "color_hex": "#E11D48",
        "category": "Engineering",
        "description": "First and Second Laws of Thermodynamics, heat engines (Carnot, Rankine, Otto), fluid statics, and Navier-Stokes equations.",
        "is_added": False,
        "chapters": [
            make_ch(
                1211, 1, "Laws of Thermodynamics & Power Cycles", 20,
                "State postulates, closed and open control volumes, entropy generation, Carnot efficiency, and Rankine steam cycle.",
                ["First Law of Thermodynamics: Energy conservation (dU = dQ - dW)", "Second Law of Thermodynamics: Clausius and Kelvin-Planck statements", "Carnot cycle efficiency: eta_carnot = 1 - (T_cold / T_hot)", "Vapor power cycles: Rankine cycle with superheating and reheat"],
                ["First Law: delta_U = Q - W", "Carnot Efficiency: eta_c = 1 - T_L / T_H (Kelvin)", "Entropy Generation: S_gen = delta_S_sys - Q / T >= 0", "Enthalpy: H = U + P * V"],
                "First & Second Laws of Thermodynamics",
                "Classical thermodynamics establishes governing laws for energy transformations. The First Law states energy cannot be created or destroyed: for a closed system, the change in internal energy dU equals heat transferred dQ minus work done dW. The Second Law dictates directional irreversibility: total entropy of an isolated system always increases (dS >= 0). No heat engine operating between two thermal reservoirs can exceed the theoretical Carnot efficiency.",
                "Gas & Vapor Power Cycles (Rankine & Otto)",
                "Practical heat engines approximate ideal thermodynamic cycles. The Rankine cycle underpins thermal and nuclear power plants, vaporizing water at high pressure, expanding steam through turbines, condensing vapor into water, and pumping liquid back to the boiler. The Otto cycle models four-stroke internal combustion engines through isentropic compression, constant-volume heat addition, isentropic expansion, and constant-volume blowdown.",
                "In thermodynamic calculations, temperatures T_H and T_L MUST always be expressed in absolute Kelvin (K = Celsius + 273.15).",
                [
                    {"id": 12111, "question_number": 1, "question": "What is the theoretical maximum thermal efficiency of a heat engine operating between a hot reservoir at 600 K and a cold reservoir at 300 K?",
                     "options": {"A": "25%", "B": "50%", "C": "75%", "D": "100%"},
                     "correct_option": "B", "explanation": "Carnot efficiency = 1 - (T_cold / T_hot) = 1 - (300 / 600) = 1 - 0.5 = 50%."},
                    {"id": 12112, "question_number": 2, "question": "Which thermodynamic property remains constant during an ideal isentropic process?",
                     "options": {"A": "Pressure", "B": "Temperature", "C": "Entropy", "D": "Volume"},
                     "correct_option": "C", "explanation": "An isentropic process is both reversible and adiabatic (dQ = 0), meaning entropy remains constant (dS = 0)."},
                    {"id": 12113, "question_number": 3, "question": "What power cycle forms the foundational thermodynamic basis for modern steam turbine electrical power plants?",
                     "options": {"A": "Otto cycle", "B": "Diesel cycle", "C": "Rankine cycle", "D": "Brayton cycle"},
                     "correct_option": "C", "explanation": "The Rankine cycle utilizes a phase-change working fluid (water-to-steam) to drive electric generator turbines in thermal, coal, and nuclear facilities."},
                    {"id": 12114, "question_number": 4, "question": "According to the Clausius statement of the Second Law of Thermodynamics, what heat transfer process is physically impossible without work input?",
                     "options": {"A": "Heat flowing spontaneously from a cooler body to a hotter body", "B": "Heat flowing from hot to cold", "C": "Converting work into heat", "D": "Expanding an ideal gas"},
                     "correct_option": "A", "explanation": "The Clausius statement dictates that heat cannot spontaneously transfer from a cooler reservoir to a hotter reservoir without net external mechanical work."},
                    {"id": 12115, "question_number": 5, "question": "How is specific enthalpy h defined in terms of internal energy u, pressure P, and specific volume v?",
                     "options": {"A": "h = u - P * v", "B": "h = u + P * v", "C": "h = u / (P * v)", "D": "h = P * v - u"},
                     "correct_option": "B", "explanation": "Enthalpy is mathematically defined as internal energy plus flow work: h = u + P * v."}
                ],
                [
                    {"chunk_id": "chunk_thermo_01", "subject": "Mechanical Thermodynamics & Fluid Mechanics",
                     "chapter": "Laws of Thermodynamics & Power Cycles", "topic": "Thermodynamic Laws and Power Cycles",
                     "keywords": ["thermodynamics", "entropy", "carnot efficiency", "rankine cycle", "enthalpy", "first law", "second law"],
                     "definition": "Thermodynamics is the branch of physics and engineering concerning heat, work, temperature, and their relations to energy and entropy.",
                     "formula_or_core_rule": "delta_U = Q - W; eta_Carnot = 1 - T_L / T_H; H = U + P*V.",
                     "worked_example": "A boiler operating at 800 K dumping heat to a river at 300 K has maximum efficiency eta = 1 - 300/800 = 62.5%.",
                     "content": "Thermodynamics governs thermal power generation and refrigeration. The First Law establishes energy conservation, while the Second Law limits thermal conversion efficiency via entropy generation."}
                ]
            ),
            make_ch(
                1212, 2, "Fluid Statics, Continuity & Bernoulli's Principle", 20,
                "Hydrostatic pressure, Pascal's principle, conservation of mass, and Bernoulli energy equation in inviscid flows.",
                ["Hydrostatic pressure gradient: dP/dz = -rho * g", "Manometry, buoyant force, and Archimedes' principle", "Conservation of mass and the Continuity Equation: A1*V1 = A2*V2", "Bernoulli's equation along a streamline and Venturi meter dynamics"],
                ["P = P_0 + rho * g * h", "Buoyant Force F_b = rho_fluid * g * V_displaced", "A1 * V1 = A2 * V2 (Incompressible Continuity)", "P + 0.5 * rho * V^2 + rho * g * z = Constant (Bernoulli)"],
                "Hydrostatics & Buoyant Forces",
                "Fluid statics studies fluids at rest. The hydrostatic pressure at depth h increases linearly with fluid density rho and gravitational acceleration g: P = P_atm + rho * g * h. Pascal's principle states pressure applied to an enclosed fluid transmits undiminished in all directions, powering hydraulic machinery. Archimedes' principle dictates that any submerged body experiences an upward buoyant force equal to the weight of fluid displaced.",
                "Fluid Dynamics & Bernoulli Conservation",
                "In steady, incompressible flow, conservation of mass dictates the Continuity Equation: A1 * V1 = A2 * V2, showing velocity increases when cross-sectional area contracts. For steady, incompressible, frictionless (inviscid) flow along a streamline, Bernoulli's equation expresses conservation of mechanical energy: P + 0.5*rho*V^2 + rho*g*z = Constant, demonstrating that static pressure drops where flow velocity accelerates (the Venturi effect).",
                "Bernoulli's equation applies ONLY to incompressible, inviscid (zero viscosity), steady flows along a continuous streamline.",
                [
                    {"id": 12121, "question_number": 1, "question": "According to Bernoulli's equation, what happens to the static pressure of a fluid flowing through a horizontal pipe when it enters a constriction with narrower cross-sectional area?",
                     "options": {"A": "Pressure increases", "B": "Pressure decreases because flow velocity increases", "C": "Pressure remains identical", "D": "Fluid freezes"},
                     "correct_option": "B", "explanation": "By continuity (A1*V1 = A2*V2), narrowing area increases velocity. By Bernoulli's equation (P + 0.5*rho*V^2 = const), higher dynamic pressure requires static pressure P to drop (the Venturi effect)."},
                    {"id": 12122, "question_number": 2, "question": "What is the buoyant force acting on a completely submerged object with volume 0.05 m^3 in water (density rho = 1000 kg/m^3, g = 9.81 m/s^2)?",
                     "options": {"A": "49.05 N", "B": "490.5 N", "C": "981 N", "D": "50 N"},
                     "correct_option": "B", "explanation": "F_buoyant = rho * g * V = 1000 kg/m^3 * 9.81 m/s^2 * 0.05 m^3 = 490.5 N."},
                    {"id": 12123, "question_number": 3, "question": "Which principle explains how hydraulic presses amplify force by applying pressure across unequal piston surface areas?",
                     "options": {"A": "Pascal's Principle", "B": "Bernoulli's Principle", "C": "Archimedes' Principle", "D": "Hooke's Law"},
                     "correct_option": "A", "explanation": "Pascal's principle states applied pressure is transmitted equally throughout an enclosed fluid: P1 = P2 => F1 / A1 = F2 / A2, amplifying output force F2 = F1 * (A2 / A1)."},
                    {"id": 12124, "question_number": 4, "question": "What instrument measures fluid flow velocity in pipes by creating a differential pressure drop across a throat constriction?",
                     "options": {"A": "Venturi tube", "B": "Hydrometer", "C": "Barometer", "D": "Galvanometer"},
                     "correct_option": "A", "explanation": "A Venturi tube utilizes a constricted throat to create a measurable pressure difference between inlet and throat, which Bernoulli's equation converts into volumetric flow rate."},
                    {"id": 12125, "question_number": 5, "question": "What condition is NOT an assumption of the standard classical Bernoulli equation?",
                     "options": {"A": "Steady flow", "B": "Incompressible fluid", "C": "Highly turbulent and viscous flow with strong internal friction", "D": "Flow along a streamline"},
                     "correct_option": "C", "explanation": "Bernoulli's equation strictly assumes inviscid (frictionless) flow; viscous shear forces dissipate mechanical energy into heat and violate standard Bernoulli conservation."}
                ],
                [
                    {"chunk_id": "chunk_thermo_02", "subject": "Mechanical Thermodynamics & Fluid Mechanics",
                     "chapter": "Fluid Statics, Continuity & Bernoulli's Principle", "topic": "Fluid Statics and Bernoulli Flow",
                     "keywords": ["fluid mechanics", "bernoulli", "hydrostatics", "continuity equation", "venturi effect", "buoyancy", "pascal principle"],
                     "definition": "Fluid mechanics studies the behavior of fluids (liquids and gases) at rest (statics) and in motion (dynamics) under applied forces.",
                     "formula_or_core_rule": "P + 0.5*rho*V^2 + rho*g*z = Const; A1*V1 = A2*V2; P = P_0 + rho*g*h.",
                     "worked_example": "Water at 2 m/s through a 0.1 m^2 pipe entering a 0.05 m^2 nozzle accelerates to V2 = (0.1 / 0.05) * 2 = 4 m/s.",
                     "content": "Fluid mechanics governs piping networks, hydraulic actuators, and aerodynamic lift. Hydrostatics determines pressure distributions, while Bernoulli's equation models energy conservation along streamlines in inviscid flows."}
                ]
            ),
            make_ch(
                1213, 3, "Navier-Stokes Equations, Boundary Layers & Viscous Flow", 20,
                "Viscous shear stress, Reynolds number, laminar vs turbulent regimes, boundary layer separation, and drag.",
                ["Newton's law of viscosity and shear stress: tau = mu * (du/dy)", "Navier-Stokes momentum conservation equations", "Reynolds number (Re) and laminar-to-turbulent transition", "Prandtl boundary layer theory, skin friction, and form drag"],
                ["Reynolds Number Re = (rho * V * L) / mu", "Shear Stress tau = mu * (du / dy)", "Navier-Stokes: rho * (Dv/Dt) = -grad(P) + mu * nabla^2(v) + rho * g", "Boundary Layer Thickness delta ≈ 5.0 * x / sqrt(Re_x) (Laminar)"],
                "Viscous Fluid Mechanics & Navier-Stokes Equations",
                "Real fluids exhibit internal friction (viscosity mu). Newton's law of viscosity states shear stress is proportional to velocity gradient: tau = mu * (du/dy). The Navier-Stokes equations describe momentum conservation for viscous Newtonian fluids, equating fluid parcel acceleration rho*(Dv/Dt) to pressure gradient forces, viscous diffusion stresses mu*nabla^2(v), and gravitational body forces.",
                "Reynolds Number & Prandtl Boundary Layer Theory",
                "The non-dimensional Reynolds number Re = (rho * V * L) / mu characterizes the ratio of inertial forces to viscous forces. At low Re (Re < 2300 in pipes), flow is smooth and laminar. At high Re, instabilities trigger turbulent mixing. Ludwig Prandtl showed that viscous effects are confined to a thin 'boundary layer' adjacent to solid walls, outside of which fluid behaves as inviscid. Adverse pressure gradients trigger boundary layer separation, generating massive wake eddies and form drag.",
                "In pipe flow, the transition from laminar to turbulent flow typically occurs at a critical Reynolds number of Re ≈ 2,300.",
                [
                    {"id": 12131, "question_number": 1, "question": "What physical ratio is represented by the dimensionless Reynolds number Re = (rho * V * L) / mu?",
                     "options": {"A": "Ratio of buoyant force to gravity force", "B": "Ratio of inertial forces to viscous forces in the fluid", "C": "Ratio of thermal conductivity to heat capacity", "D": "Speed of sound to fluid velocity"},
                     "correct_option": "B", "explanation": "The Reynolds number quantifies the relative importance of inertial forces tending to disrupt flow versus viscous forces damping out disturbances."},
                    {"id": 12132, "question_number": 2, "question": "What phenomenon occurs when a fluid boundary layer encounters a steep adverse pressure gradient (pressure increasing in the direction of flow)?",
                     "options": {"A": "Boundary layer separation, where flow detaches from the surface and forms a turbulent recirculating wake", "B": "Viscosity drops to zero", "C": "Velocity accelerates to infinity", "D": "Laminar flow is stabilized"},
                     "correct_option": "A", "explanation": "An adverse pressure gradient decelerates near-wall fluid until velocity gradient du/dy at the wall reaches zero and reverses, causing flow separation and high pressure drag."},
                    {"id": 12133, "question_number": 3, "question": "According to Newton's law of viscosity, what is shear stress tau in a fluid directly proportional to?",
                     "options": {"A": "Total fluid volume", "B": "Velocity gradient perpendicular to the direction of shear (du/dy)", "C": "Atmospheric pressure squared", "D": "Fluid color"},
                     "correct_option": "B", "explanation": "Newtonian fluids exhibit a linear relationship between applied shear stress tau and the rate of shear strain (velocity gradient du/dy): tau = mu * (du/dy)."},
                    {"id": 12134, "question_number": 4, "question": "In Prandtl's boundary layer theory for a flat plate, how does laminar boundary layer thickness delta scale with distance x from the leading edge?",
                     "options": {"A": "delta scales with x^2", "B": "delta scales with sqrt(x)", "C": "delta is constant", "D": "delta decreases with distance"},
                     "correct_option": "B", "explanation": "Since Re_x = rho * V * x / mu, boundary layer thickness delta = 5.0 * x / sqrt(Re_x) = 5.0 * sqrt(mu * x / (rho * V)), meaning thickness grows proportionally to the square root of x."},
                    {"id": 12135, "question_number": 5, "question": "What are the two primary components of total aerodynamic or hydrodynamic drag on a submerged body?",
                     "options": {"A": "Skin friction drag (viscous shear) and Form drag (pressure disparity from wake separation)", "B": "Magnetic drag and thermal drag", "C": "Static drag and kinetic drag", "D": "Nuclear drag and gravity drag"},
                     "correct_option": "A", "explanation": "Total drag comprises skin friction drag resulting from viscous shear on the surface plus pressure (form) drag resulting from pressure differences caused by flow separation wakes."}
                ],
                [
                    {"chunk_id": "chunk_thermo_03", "subject": "Mechanical Thermodynamics & Fluid Mechanics",
                     "chapter": "Navier-Stokes Equations, Boundary Layers & Viscous Flow", "topic": "Navier-Stokes and Boundary Layer Flow",
                     "keywords": ["navier-stokes", "viscosity", "reynolds number", "boundary layer", "laminar flow", "turbulent flow", "flow separation"],
                     "definition": "The Navier-Stokes equations are partial differential equations governing the motion of viscous fluid substances, expressing Newton's second law for continuous media.",
                     "formula_or_core_rule": "Re = (rho * V * L) / mu; tau = mu * (du/dy); Navier-Stokes momentum balance.",
                     "worked_example": "Water (mu=0.001 Pa.s, rho=1000 kg/m^3) at 1 m/s in a pipe of D=0.05 m has Re = (1000 * 1 * 0.05) / 0.001 = 50,000 (turbulent).",
                     "content": "Real-world fluid dynamics balances inertial momentum and viscous shear stresses via the Navier-Stokes equations. Prandtl boundary layer theory isolates viscous drag near walls and models flow separation critical for vehicle aerodynamics."}
                ]
            )
        ]
    })

    # 122. Civil Engineering & Structural Analysis
    subs.append({
        "id": "SUB_CIVIL",
        "subject_id": 122,
        "name": "Civil Engineering & Structural Analysis",
        "slug": "civil-structural-engineering",
        "icon_symbol": "🏗️",
        "color_hex": "#B45309",
        "category": "Engineering",
        "description": "Statics of determinate and indeterminate structures, shear-moment diagrams, Euler-Bernoulli beam theory, and reinforced concrete design.",
        "is_added": False,
        "chapters": [
            make_ch(
                1221, 1, "Statics of Trusses, Beams & Shear-Moment Diagrams", 20,
                "Equilibrium of rigid bodies, method of joints/sections, internal shear force V(x), and bending moment M(x) diagrams.",
                ["Static equilibrium: Sum(Fx) = 0, Sum(Fy) = 0, Sum(M) = 0", "Plane truss analysis: Method of Joints and Method of Sections", "Relationship between distributed load w(x), shear V(x), and moment M(x)", "Singularity functions and point load discontinuity"],
                ["dV/dx = -w(x)", "dM/dx = V(x)", "M(x) = Integral of V(x) dx", "Truss Determinacy: b + r = 2 * j"],
                "Static Equilibrium & Truss Analysis",
                "Structural engineering ensures structures safely support loads without collapse. Static determinacy requires that internal member forces and support reactions be solvable using equations of equilibrium: Sum(Fx)=0, Sum(Fy)=0, Sum(M)=0. For planar pin-jointed trusses with b members, r reactions, and j joints, the determinacy condition is b + r = 2j. The Method of Joints evaluates equilibrium at individual nodes, while the Method of Sections cuts through members to expose internal tension or compression.",
                "Internal Shear & Bending Moment Diagrams",
                "External transverse loads induce internal shear forces V(x) and bending moments M(x) throughout structural beams. Differential equilibrium yields the fundamental calculus relations: the negative distributed load equals the slope of the shear diagram (dV/dx = -w(x)), and the shear force equals the slope of the bending moment diagram (dM/dx = V(x)). Consequently, maximum bending moments occur precisely where shear force crosses zero.",
                "Maximum bending moment occurs at points along the beam where the shear force V(x) equals zero or changes sign.",
                [
                    {"id": 12211, "question_number": 1, "question": "Where does the maximum bending moment occur along a horizontally loaded structural beam?",
                     "options": {"A": "At the beam supports exclusively", "B": "At points where the internal shear force V(x) equals zero or passes through zero", "C": "At the exact centroid of the beam regardless of loads", "D": "Where deflection is zero"},
                     "correct_option": "B", "explanation": "Because dM/dx = V(x), finding the extrema of moment M(x) requires setting its derivative dM/dx = 0, which corresponds to points where shear V(x) = 0."},
                    {"id": 12212, "question_number": 2, "question": "In a simply supported beam of length L carrying a uniform distributed load w (N/m), what is the maximum bending moment at mid-span?",
                     "options": {"A": "w * L^2 / 8", "B": "w * L^2 / 2", "C": "w * L / 4", "D": "w * L^3 / 12"},
                     "correct_option": "A", "explanation": "Integrating the parabolic moment equation M(x) = (w*L/2)*x - (w*x^2)/2 at mid-span x = L/2 yields M_max = w * L^2 / 8."},
                    {"id": 12213, "question_number": 3, "question": "What is the condition for static determinacy of a planar truss with b members, r support reactions, and j joints?",
                     "options": {"A": "b + r = 2 * j", "B": "b + j = 2 * r", "C": "b = 3 * j", "D": "r = 2 * b"},
                     "correct_option": "A", "explanation": "Since each planar joint provides 2 equilibrium equations (Sum(Fx)=0, Sum(Fy)=0), total equations = 2j; determinacy requires unknowns (b + r) to equal 2j."},
                    {"id": 12214, "question_number": 4, "question": "What mathematical relationship links distributed load w(x) to shear force V(x)?",
                     "options": {"A": "dV/dx = -w(x)", "B": "d^2V/dx^2 = w(x)", "C": "V(x) = w(x) * x", "D": "V(x) = dw/dx"},
                     "correct_option": "A", "explanation": "Vertical force equilibrium on an infinitesimal beam element dx yields dV/dx = -w(x), meaning the slope of the shear diagram equals the negative distributed load."},
                    {"id": 12215, "question_number": 5, "question": "What type of internal force is carried by members of an ideal pin-jointed truss under nodal loading?",
                     "options": {"A": "Axial tension or axial compression only (zero internal bending moment)", "B": "Torsional twisting only", "C": "Transverse shear exclusively", "D": "Pure bending"},
                     "correct_option": "A", "explanation": "Because truss joints are modeled as frictionless pins with loads applied strictly at nodes, all truss members act as two-force members carrying pure axial tension or compression."}
                ],
                [
                    {"chunk_id": "chunk_civil_01", "subject": "Civil Engineering & Structural Analysis",
                     "chapter": "Statics of Trusses, Beams & Shear-Moment Diagrams", "topic": "Truss Analysis and Shear-Moment Diagrams",
                     "keywords": ["structural analysis", "statics", "shear force", "bending moment", "truss", "method of joints", "equilibrium"],
                     "definition": "Structural statics determines the internal reactions, axial forces, shear stresses, and bending moments within structures under equilibrium loads.",
                     "formula_or_core_rule": "dM/dx = V(x); dV/dx = -w(x); Truss Determinacy: b + r = 2j.",
                     "worked_example": "A 10m simply supported beam under uniform load w = 4 kN/m experiences peak mid-span moment M_max = 4 * 10^2 / 8 = 50 kNm.",
                     "content": "Structural analysis models load paths through frameworks. Truss analysis evaluates axial load distribution, while shear and bending moment diagrams locate critical stress concentrations for cross-sectional sizing."}
                ]
            ),
            make_ch(
                1222, 2, "Stress, Strain & Euler-Bernoulli Beam Theory", 20,
                "Normal stress, Hooke's Law, second moment of area, flexure formula sigma = -M*y/I, and beam deflection.",
                ["Stress-strain relationships: Young's Modulus and Poisson's ratio", "Second Moment of Area (Moment of Inertia I) and parallel axis theorem", "Euler-Bernoulli flexure formula: sigma = - (M * y) / I", "Transverse shear stress in beams: tau = (V * Q) / (I * b)"],
                ["Normal Stress sigma = E * epsilon", "Flexure Formula: sigma = - (M * y) / I", "Beam Deflection: E * I * (d^2 v / dx^2) = M(x)", "Shear Stress: tau = (V * Q) / (I * b)"],
                "Stress-Strain Relationships & Section Properties",
                "Materials deform under mechanical stress. Normal stress sigma = P/A produces longitudinal strain epsilon = delta_L / L related via Young's Modulus E (sigma = E * epsilon). The Second Moment of Area I = Integral(y^2 dA) characterizes the geometric resistance of a cross-section to bending. The parallel axis theorem I = I_centroid + A * d^2 enables calculating moments of inertia for composite structural sections like I-beams.",
                "Euler-Bernoulli Flexure & Deflection Theory",
                "The Euler-Bernoulli beam theory assumes plane sections remain plane and perpendicular to the neutral axis during bending. Normal flexural stress varies linearly across beam depth: sigma = - (M * y) / I, where y is the distance from the neutral axis (maximum at outer fibers). Integrating the governing differential equation E * I * (d^2 v / dx^2) = M(x) yields slope and vertical deflection v(x).",
                "I-beams are optimal for bending because placing cross-sectional area far from the neutral axis (flanges) maximizes the second moment of area I.",
                [
                    {"id": 12221, "question_number": 1, "question": "In the Euler-Bernoulli flexure formula sigma = - (M * y) / I, where does the maximum tensile or compressive stress occur in a symmetrical cross-section?",
                     "options": {"A": "At the neutral axis (y = 0)", "B": "At the extreme outermost fibers farthest from the neutral axis (y = c)", "C": "At the quarter-span", "D": "At the centroid"},
                     "correct_option": "B", "explanation": "Flexural bending stress scales linearly with distance y from the neutral axis, reaching peak magnitude at the outermost fibers (y = y_max = c)."},
                    {"id": 12222, "question_number": 2, "question": "What is the Second Moment of Area (I) of a solid rectangular cross-section of width b and height h about its neutral centroidal axis?",
                     "options": {"A": "b * h^3 / 12", "B": "b * h^2 / 6", "C": "b^3 * h / 12", "D": "b * h / 2"},
                     "correct_option": "A", "explanation": "Integrating y^2 dy from -h/2 to +h/2 across width b yields I = b * h^3 / 12."},
                    {"id": 12223, "question_number": 3, "question": "What does 'E * I' represent in structural beam deflection equations?",
                     "options": {"A": "Flexural rigidity (bending stiffness of the beam)", "B": "Kinetic energy", "C": "Thermal expansion", "D": "Torsional shear"},
                     "correct_option": "A", "explanation": "The product of Young's Modulus E (material stiffness) and second moment of area I (geometric stiffness) defines flexural rigidity EI."},
                    {"id": 12224, "question_number": 4, "question": "In the transverse shear stress formula tau = (V * Q) / (I * b), what does term 'Q' represent?",
                     "options": {"A": "First moment of area of the section above the cut plane about the neutral axis", "B": "Heat flow", "C": "Electric charge", "D": "Dynamic pressure"},
                     "correct_option": "A", "explanation": "Q = y_bar_prime * A_prime is the first moment of area of the portion of cross-section located beyond the level where shear stress tau is evaluated."},
                    {"id": 12225, "question_number": 5, "question": "What is Poisson's ratio nu defined as for an axially loaded elastic bar?",
                     "options": {"A": "Ratio of lateral strain to axial strain (nu = - epsilon_lateral / epsilon_axial)", "B": "Yield stress divided by ultimate stress", "C": "Bending moment divided by shear force", "D": "Mass divided by volume"},
                     "correct_option": "A", "explanation": "Poisson's ratio is the negative ratio of transverse (lateral) contraction strain to longitudinal extension strain under tensile loading."}
                ],
                [
                    {"chunk_id": "chunk_civil_02", "subject": "Civil Engineering & Structural Analysis",
                     "chapter": "Stress, Strain & Euler-Bernoulli Beam Theory", "topic": "Beam Flexure and Deflection",
                     "keywords": ["euler-bernoulli", "flexure formula", "bending stress", "moment of inertia", "deflection", "youngs modulus", "stress strain"],
                     "definition": "Euler-Bernoulli beam theory is a simplification of linear elastic mechanics providing a means of calculating load-carrying and deflection characteristics of beams.",
                     "formula_or_core_rule": "sigma = - (M * y) / I; I_rect = b*h^3 / 12; EI * d^2v/dx^2 = M(x).",
                     "worked_example": "For a beam with M = 20 kNm, I = 8e-5 m^4, at outer fiber y = 0.1 m: sigma = (20,000 * 0.1) / 8e-5 = 25 MPa.",
                     "content": "Euler-Bernoulli beam theory links internal bending moments to cross-sectional fiber stresses. Flexural rigidity EI dictates resistance against structural sagging, governing civil design limit states."}
                ]
            ),
            make_ch(
                1223, 3, "Reinforced Concrete Design, Foundation Mechanics & Seismic Analysis", 20,
                "Composite behavior of concrete and rebar, Whitney stress block, shear stirrup design, bearing capacity, and earthquake response spectra.",
                ["Mechanics of reinforced concrete: brittle concrete in compression + ductile rebar in tension", "Whitney stress block approximation and ultimate moment capacity Mu", "Shear reinforcement design via vertical stirrups (truss model)", "Soil bearing capacity (Terzaghi equation) and seismic response spectra"],
                ["Whitney Stress Block Depth a = (As * fy) / (0.85 * f'c * b)", "Nominal Moment Mn = As * fy * (d - a / 2)", "Terzaghi: q_ult = c*Nc + q*Nq + 0.5*gamma*B*Ngamma", "Seismic Base Shear V_base = C_s * W_seismic"],
                "Reinforced Concrete Principles & Whitney Stress Block",
                "Plain concrete possesses high compressive strength but negligible tensile capacity (approx 10% of compressive strength). Reinforced concrete embeds high-yield deformed steel reinforcing bars (rebar) in tensile zones. Under ultimate limit state design (ACI 318 / Eurocode 2), the non-linear concrete compression zone is modeled using Charles Whitney's equivalent rectangular stress block (uniform stress 0.85*f'c over depth a = beta1 * c), balancing tensile steel force T = As * fy.",
                "Foundation Mechanics & Seismic Response Spectra",
                "Structures transfer gravity and lateral loads into the earth via shallow footings or deep piles. Karl Terzaghi's bearing capacity equation computes soil ultimate shear failure q_ult. Under earthquake ground shaking, lateral inertial forces are modeled using dynamic response spectra: Base shear V = Cs * W represents total horizontal shear force at the foundation, dissipated through ductile frame yielding.",
                "Under-reinforced beams (where tensile steel yields BEFORE concrete crushes) are mandatory in seismic building codes to prevent catastrophic brittle failure.",
                [
                    {"id": 12231, "question_number": 1, "question": "Why do structural building codes strictly mandate 'under-reinforced' beam designs where tensile steel yields before concrete crushes?",
                     "options": {"A": "To ensure ductile failure with visible cracking and deflection warnings before collapse, rather than sudden catastrophic brittle concrete crushing", "B": "To save money on steel", "C": "To make concrete cure faster", "D": "To reduce foundation depth"},
                     "correct_option": "A", "explanation": "Under-reinforced beams guarantee ductile yielding: steel stretches substantially, providing visible warning deflections before ultimate concrete compressive crushing occurs."},
                    {"id": 12232, "question_number": 2, "question": "In Whitney's equivalent rectangular stress block for reinforced concrete, what is the assumed uniform compressive stress magnitude?",
                     "options": {"A": "0.85 * f'c", "B": "1.0 * f'c", "C": "0.5 * f'c", "D": "f_y"},
                     "correct_option": "A", "explanation": "Whitney's stress block simplifies non-linear parabolic concrete compression into an equivalent uniform rectangular stress of 0.85 * f'c."},
                    {"id": 12233, "question_number": 3, "question": "What is the primary function of vertical steel stirrups in reinforced concrete beams?",
                     "options": {"A": "To resist diagonal tension stresses caused by transverse shear forces and prevent shear cracking", "B": "To increase thermal insulation", "C": "To prevent paint peeling", "D": "To support ceiling fans"},
                     "correct_option": "A", "explanation": "Shear forces combined with bending induce 45-degree diagonal tension; closed vertical stirrups cross these potential shear cracks and tie the beam in tension."},
                    {"id": 12234, "question_number": 4, "question": "What formula calculates total lateral seismic base shear V_b on a building under equivalent lateral force procedures?",
                     "options": {"A": "V_b = C_s * W (Seismic response coefficient multiplied by effective building weight)", "B": "V_b = Mass * Velocity", "C": "V_b = Building Height / Wind Speed", "D": "V_b = Foundation Area * Depth"},
                     "correct_option": "A", "explanation": "Seismic codes compute base shear as V = Cs * W, where Cs depends on spectral acceleration, soil class, and structural ductility, and W is total seismic weight."},
                    {"id": 12235, "question_number": 5, "question": "What three soil strength mechanisms contribute to ultimate bearing capacity in Terzaghi's equation?",
                     "options": {"A": "Cohesion (c), surcharge overburden (q), and soil unit weight/footing width (gamma*B)", "B": "Moisture, sunlight, and air", "C": "Concrete thickness, rebar size, and aggregate", "D": "Steel yield, concrete strength, and bar spacing"},
                     "correct_option": "A", "explanation": "Terzaghi's bearing capacity equation q_ult = c*Nc + q*Nq + 0.5*gamma*B*Ngamma sums contributions from soil cohesion, overburden depth, and self-weight friction."}
                ],
                [
                    {"chunk_id": "chunk_civil_03", "subject": "Civil Engineering & Structural Analysis",
                     "chapter": "Reinforced Concrete Design, Foundation Mechanics & Seismic Analysis", "topic": "Reinforced Concrete and Seismic Engineering",
                     "keywords": ["reinforced concrete", "rebar", "whitney stress block", "seismic analysis", "base shear", "foundations", "terzaghi"],
                     "definition": "Reinforced concrete design integrates compressive concrete with tensile steel to fabricate resilient structural frames and foundation footings capable of withstanding gravity and seismic loads.",
                     "formula_or_core_rule": "Mn = As * fy * (d - a/2); Whitney depth a = (As * fy) / (0.85 * f'c * b); V_base = Cs * W.",
                     "worked_example": "A beam with As=1000 mm^2, fy=400 MPa, b=250 mm, f'c=25 MPa has a = (1000 * 400) / (0.85 * 25 * 250) = 75.3 mm.",
                     "content": "Civil infrastructure pairs reinforced concrete beam-column frames with deep geotechnical foundations designed to resist gravity loads, soil shear failures, and lateral earthquake base shears."}
                ]
            )
        ]
    })

    # 123. Aerospace & Aerodynamics
    subs.append({
        "id": "SUB_AERO",
        "subject_id": 123,
        "name": "Aerospace & Aerodynamics",
        "slug": "aerospace-aerodynamics",
        "icon_symbol": "🚀",
        "color_hex": "#1D4ED8",
        "category": "Engineering",
        "description": "Airfoil aerodynamics, lift and drag, compressible flow, shock waves, and orbital mechanics (Hohmann transfers, rocket equation).",
        "is_added": False,
        "chapters": [
            make_ch(
                1231, 1, "Airfoil Geometry, Lift Generation & Drag Components", 20,
                "NACA 4-digit airfoils, circulation, Kutta-Joukowski theorem, induced drag, and wing aspect ratio.",
                ["Airfoil nomenclature: chord line, camber, leading edge radius, angle of attack", "Kutta-Joukowski theorem: Lift L' = rho_inf * V_inf * Gamma", "Kutta condition and boundary layer circulation generation", "Drag decomposition: parasitic (skin friction, form) and induced drag (vortex)"],
                ["Lift L = 0.5 * rho * V^2 * S * C_L", "Drag D = 0.5 * rho * V^2 * S * C_D", "Induced Drag Coefficient C_Di = C_L^2 / (pi * AR * e)", "Aspect Ratio AR = b^2 / S"],
                "Airfoil Nomenclature & Aerodynamic Circulation",
                "Aerodynamic lift is generated when an airfoil moves through air, turning the fluid downward and creating a pressure disparity between upper (suction) and lower (pressure) surfaces. The Kutta-Joukowski theorem states lift per unit span equals fluid density multiplied by free-stream velocity and circulation Gamma: L' = rho * V * Gamma. Real viscous airfoils generate circulation through boundary layer separation at the sharp trailing edge, satisfying the Kutta condition.",
                "Finite Wings & Induced Drag",
                "Unlike 2D airfoils, 3D finite wings experience high pressure air curling around wingtips into the upper low-pressure zone, shedding powerful wingtip vortices. These vortices induce downward downwash, tilting the effective lift vector backward and creating Induced Drag. Induced drag coefficient C_Di = C_L^2 / (pi * AR * e) is inversely proportional to Aspect Ratio (AR = span^2 / area), explaining why gliders feature long, slender wings.",
                "Induced drag is inversely proportional to aspect ratio; doubling wing aspect ratio halves induced drag for the same lift coefficient.",
                [
                    {"id": 12311, "question_number": 1, "question": "What aerodynamic phenomenon generates 'induced drag' on a finite 3D aircraft wing?",
                     "options": {"A": "Skin friction against air molecules", "B": "Wingtip vortices caused by high pressure air curling around the tips into low pressure zones, tilting the net lift vector rearward", "C": "Jet engine noise", "D": "Payload weight"},
                     "correct_option": "B", "explanation": "Wingtip vortices induce a downward velocity component (downwash) over the wing, tilting the local lift vector backward and creating induced drag."},
                    {"id": 12312, "question_number": 2, "question": "According to the Kutta-Joukowski theorem, what physical quantity must exist around an airfoil to produce aerodynamic lift?",
                     "options": {"A": "Fluid circulation Gamma", "B": "Supersonic shock waves", "C": "Thermal combustion", "D": "Zero viscosity"},
                     "correct_option": "A", "explanation": "The Kutta-Joukowski theorem proves that lift per unit span L' = rho * V * Gamma is directly proportional to circulation Gamma around the closed airfoil contour."},
                    {"id": 12313, "question_number": 3, "question": "Why do high-performance gliders and U-2 reconnaissance aircraft feature very high aspect ratio (slender) wings?",
                     "options": {"A": "To minimize induced drag (C_Di = C_L^2 / (pi * AR * e)) during high-lift cruise", "B": "To fit inside narrow hangars", "C": "To fly at supersonic Mach numbers", "D": "To increase parasite drag"},
                     "correct_option": "A", "explanation": "High aspect ratio (AR) minimizes wingtip vortex strength and induced drag, enabling exceptional lift-to-drag ratios for long-duration gliding and cruising."},
                    {"id": 12314, "question_number": 4, "question": "What is the aerodynamic stall of an aircraft wing caused by?",
                     "options": {"A": "Jet engine flameout", "B": "Flow separation over the upper surface when exceeding the critical angle of attack, causing a sudden loss of lift and surge in drag", "C": "Running out of fuel", "D": "Flying above clouds"},
                     "correct_option": "B", "explanation": "Exceeding the critical angle of attack causes the upper surface boundary layer to separate completely, destroying low pressure suction and precipitating stall."},
                    {"id": 12315, "question_number": 5, "question": "In the standard NACA 4-digit airfoil 'NACA 2412', what does the first digit '2' represent?",
                     "options": {"A": "Maximum camber of 2% of the chord length", "B": "Wingspan in meters", "C": "Thickness of 2 inches", "D": "Two jet engines"},
                     "correct_option": "A", "explanation": "In NACA 4-digit airfoils, the first digit specifies maximum camber as a percentage of chord (2%), the second specifies camber location (40% chord), and the last two specify thickness (12% chord)."}
                ],
                [
                    {"chunk_id": "chunk_aero_01", "subject": "Aerospace & Aerodynamics",
                     "chapter": "Airfoil Geometry, Lift Generation & Drag Components", "topic": "Airfoils, Lift, and Induced Drag",
                     "keywords": ["aerodynamics", "airfoil", "lift", "drag", "kutta-joukowski", "induced drag", "aspect ratio", "stall"],
                     "definition": "Aerodynamics is the study of the behavior of air when interacting with solid moving bodies such as airplane wings and rockets.",
                     "formula_or_core_rule": "L = 0.5*rho*V^2*S*C_L; C_Di = C_L^2 / (pi*AR*e); Kutta-Joukowski: L' = rho*V*Gamma.",
                     "worked_example": "A wing with AR=10, Oswald efficiency e=0.8, operating at C_L=1.0 has C_Di = 1.0^2 / (pi * 10 * 0.8) = 0.0398.",
                     "content": "Aerodynamic lift results from pressure differentials governed by circulation and downwash. 3D finite wings incur induced drag from wingtip vortices, minimized by high aspect ratio wing planforms and winglets."}
                ]
            ),
            make_ch(
                1232, 2, "Compressible Flow, Shock Waves & Supersonic Flight", 20,
                "Mach number regimes, stagnation properties, normal and oblique shock waves, and Prandtl-Meyer expansion fans.",
                ["Mach number regimes: subsonic, transonic, supersonic, hypersonic", "Isentropic compressible flow relations for ideal gases (gamma = 1.4)", "Normal shock wave jump conditions (Rankine-Hugoniot relations)", "Oblique shocks, Mach cones, and Prandtl-Meyer expansion waves"],
                ["Mach Number M = V / a (a = sqrt(gamma * R * T))", "Stagnation Temp: T_0 / T = 1 + ((gamma - 1) / 2) * M^2", "Stagnation Pressure: P_0 / P = (1 + ((gamma - 1) / 2) * M^2)^(gamma / (gamma - 1))", "Mach Angle: mu = arcsin(1 / M)"],
                "Compressibility & Stagnation State Relations",
                "When flow velocities approach the local speed of sound (M = V/a > 0.3), density variations cannot be neglected. Speed of sound in an ideal gas satisfies a = sqrt(gamma * R * T). As flow decelerates isentropically to rest, static temperature and pressure rise to Stagnation (Total) conditions governed by isentropic relations: T0/T = 1 + ((gamma-1)/2)*M^2.",
                "Shock Waves & Supersonic Wave Drag",
                "When a supersonic flow (M > 1) decelerates or encounters a concave corner, it cannot adjust smoothly. Instead, abrupt discontinuous compression waves form: Shock Waves. Across a Normal Shock, flow instantaneously drops from supersonic to subsonic (M2 < 1), static pressure, temperature, and density surge, while total stagnation pressure drops due to irreversible entropy increase. Oblique shocks and Prandtl-Meyer expansion fans dictate supersonic wing profiles.",
                "Flow across a normal shock wave ALWAYS drops from supersonic (M1 > 1) to subsonic (M2 < 1), with an irreversible loss in total stagnation pressure.",
                [
                    {"id": 12321, "question_number": 1, "question": "What happens to Mach number, static pressure, and total stagnation pressure as gas flows across a Normal Shock wave?",
                     "options": {"A": "Mach number drops from supersonic to subsonic, static pressure increases, and total stagnation pressure decreases", "B": "Mach number increases to hypersonic", "C": "Static pressure drops to zero", "D": "Total pressure increases"},
                     "correct_option": "A", "explanation": "Normal shocks compress gas irreversibly: flow drops from M1 > 1 to M2 < 1, static pressure jumps dramatically, and total pressure P0 decreases due to entropy generation."},
                    {"id": 12322, "question_number": 2, "question": "At what threshold Mach number does fluid flow typically begin to exhibit significant compressibility effects requiring non-constant density modeling?",
                     "options": {"A": "Mach 0.3", "B": "Mach 0.01", "C": "Mach 1.0 only", "D": "Mach 5.0"},
                     "correct_option": "A", "explanation": "For Mach numbers below 0.3, density variations are under 5%, allowing incompressible modeling. Above M = 0.3, compressibility must be accounted for."},
                    {"id": 12323, "question_number": 3, "question": "What is the speed of sound in air (gamma = 1.4, R = 287 J/kg.K) at a temperature of T = 300 K?",
                     "options": {"A": "Approx 347.2 m/s", "B": "100 m/s", "C": "1200 m/s", "D": "767 m/s"},
                     "correct_option": "A", "explanation": "Speed of sound a = sqrt(gamma * R * T) = sqrt(1.4 * 287 * 300) = sqrt(120540) ≈ 347.2 m/s."},
                    {"id": 12324, "question_number": 4, "question": "What type of aerodynamic wave expands flow around a convex corner in supersonic flight without generating entropy?",
                     "options": {"A": "Prandtl-Meyer Expansion Fan", "B": "Normal Shock Wave", "C": "Bow Shock", "D": "Detonation Wave"},
                     "correct_option": "A", "explanation": "Prandtl-Meyer expansion fans turn supersonic flow around convex corners smoothly and isentropically, accelerating flow and lowering static pressure."},
                    {"id": 12325, "question_number": 5, "question": "What is the semi-vertex angle mu of the Mach cone generated by a supersonic aircraft flying at Mach 2.0?",
                     "options": {"A": "30 degrees", "B": "45 degrees", "C": "60 degrees", "D": "90 degrees"},
                     "correct_option": "A", "explanation": "Mach angle mu = arcsin(1 / M) = arcsin(1 / 2) = arcsin(0.5) = 30 degrees."}
                ],
                [
                    {"chunk_id": "chunk_aero_02", "subject": "Aerospace & Aerodynamics",
                     "chapter": "Compressible Flow, Shock Waves & Supersonic Flight", "topic": "Compressible Flow and Shock Waves",
                     "keywords": ["compressible flow", "mach number", "shock wave", "supersonic", "rankine-hugoniot", "prandtl-meyer", "stagnation"],
                     "definition": "Compressible flow is the branch of fluid mechanics dealing with flows where fluid density varies significantly under pressure gradients, dominant at high Mach numbers.",
                     "formula_or_core_rule": "M = V / a; a = sqrt(gamma * R * T); T0/T = 1 + ((gamma-1)/2)*M^2; Mach angle mu = arcsin(1/M).",
                     "worked_example": "An aircraft at 600 m/s in air at 250K (a = sqrt(1.4*287*250) = 316.9 m/s) flies at Mach M = 600 / 316.9 = 1.89.",
                     "content": "Supersonic aerodynamics analyzes wave phenomena. Normal and oblique shock waves decelerate flow and generate wave drag, while expansion fans accelerate gas around geometric contours."}
                ]
            ),
            make_ch(
                1233, 3, "Orbital Mechanics & Rocket Propulsion", 20,
                "Kepler's laws, Tsiolkovsky rocket equation, specific impulse (Isp), Hohmann transfer orbits, and escape velocity.",
                ["Kepler's three laws of planetary motion and two-body orbital dynamics", "Specific orbital energy, vis-viva equation, and orbital velocity", "Tsiolkovsky Rocket Equation and multi-stage mass ratios", "Hohmann transfer orbits, delta-v budgeting, and escape velocity"],
                ["Tsiolkovsky: delta_V = I_sp * g_0 * ln(m_initial / m_final)", "Vis-Viva: V^2 = G * M * (2 / r - 1 / a)", "Escape Velocity: V_esc = sqrt(2 * G * M / r)", "Specific Impulse: I_sp = F_thrust / (dot_m * g_0)"],
                "Rocket Propulsion & Tsiolkovsky Rocket Equation",
                "Space propulsion operates via Newton's third law, expelling mass at high exhaust velocity V_e to generate thrust. Konstantin Tsiolkovsky derived the ideal rocket equation: delta_V = I_sp * g_0 * ln(m_0 / m_f), showing that achieving high velocity increments requires either high propellant mass fractions or elevated Specific Impulse (I_sp, measured in seconds). Staging discards empty propellant tanks, multiplying effective delta-v.",
                "Orbital Mechanics & Hohmann Transfers",
                "Orbital trajectories obey Kepler's laws and the Vis-Viva equation: V^2 = mu*(2/r - 1/a). The minimum-energy orbital maneuver to transfer between two coplanar circular orbits is the Hohmann Transfer Orbit, an elliptical trajectory with periapsis at the inner orbit and apoapsis at the outer orbit. Two tangential impulsive burns (delta_V1 and delta_V2) accomplish the transfer.",
                "Specific impulse Isp measures rocket fuel efficiency in seconds: Isp = Ve / g0.",
                [
                    {"id": 12331, "question_number": 1, "question": "According to the Tsiolkovsky Rocket Equation, how does the available velocity change delta_V scale with propellant mass?",
                     "options": {"A": "Logarithmically with the mass ratio (ln(m_initial / m_final))", "B": "Linearly with mass ratio", "C": "Inversely with payload", "D": "Quadratic with fuel volume"},
                     "correct_option": "A", "explanation": "delta_V = I_sp * g_0 * ln(m_initial / m_final), showing diminishing returns as initial fuel mass increases due to the logarithmic relationship."},
                    {"id": 12332, "question_number": 2, "question": "What is the theoretical escape velocity from the surface of Earth (radius R = 6,371 km, GM = 3.986e14 m^3/s^2)?",
                     "options": {"A": "7.9 km/s", "B": "11.2 km/s", "C": "16.7 km/s", "D": "25.0 km/s"},
                     "correct_option": "B", "explanation": "V_esc = sqrt(2 * GM / R) = sqrt(2 * 3.986e14 / 6.371e6) ≈ 11,186 m/s ≈ 11.2 km/s (compared to circular LEO speed of 7.9 km/s)."},
                    {"id": 12333, "question_number": 3, "question": "What two-impulse orbital maneuver provides the most fuel-efficient transfer between two coplanar circular orbits?",
                     "options": {"A": "Hohmann Transfer Orbit", "B": "Bi-elliptic transfer exclusively", "C": "Constant thrust burn", "D": "Radial direct burn"},
                     "correct_option": "A", "explanation": "The Hohmann transfer orbit employs an elliptical trajectory tangent to both orbits, minimizing total delta-V expenditure for orbit radius ratios < 11.94."},
                    {"id": 12334, "question_number": 4, "question": "What physical parameter does Specific Impulse (I_sp) quantify in rocket propulsion?",
                     "options": {"A": "Total rocket weight", "B": "Thrust generated per unit weight rate of propellant consumed, measuring fuel efficiency in seconds", "C": "Rocket length", "D": "Payload volume"},
                     "correct_option": "B", "explanation": "Specific impulse I_sp = F / (dot_m * g_0) = V_e / g_0 measures how efficiently a rocket engine converts propellant mass into forward momentum."},
                    {"id": 12335, "question_number": 5, "question": "Why do orbital launch vehicles utilize multiple stages rather than single-stage-to-orbit (SSTO)?",
                     "options": {"A": "Discarding structural dry mass of empty tanks resets the mass ratio m0/mf for subsequent stages, achieving higher terminal delta-V", "B": "Rocket engines can only burn for 2 minutes", "C": "To pollute the ocean", "D": "To change payload colors"},
                     "correct_option": "A", "explanation": "Staging drops empty tanks and heavy structural hardware, resetting the logarithmic mass ratio in Tsiolkovsky's equation and permitting achievable payload orbital insertion."}
                ],
                [
                    {"chunk_id": "chunk_aero_03", "subject": "Aerospace & Aerodynamics",
                     "chapter": "Orbital Mechanics & Rocket Propulsion", "topic": "Orbital Mechanics and Rocket Equation",
                     "keywords": ["orbital mechanics", "rocketry", "tsiolkovsky", "delta-v", "specific impulse", "hohmann transfer", "escape velocity"],
                     "definition": "Orbital mechanics and astronautics study the trajectories of spacecraft under gravitational and propulsive reaction forces.",
                     "formula_or_core_rule": "delta_V = I_sp * g_0 * ln(m0 / mf); Vis-viva: V^2 = mu * (2/r - 1/a); V_esc = sqrt(2*mu / r).",
                     "worked_example": "A rocket with Isp = 300s, mass ratio m0/mf = 2.718 (ln=1) achieves delta_V = 300 * 9.81 * 1 = 2943 m/s.",
                     "content": "Astrodynamics models orbital motion via Kepler's laws and the vis-viva equation. Interplanetary trajectories leverage Hohmann transfers and multi-stage rocket configurations governed by the Tsiolkovsky equation."}
                ]
            )
        ]
    })

    # 124. Biomedical Engineering
    subs.append({
        "id": "SUB_BIOMED",
        "subject_id": 124,
        "name": "Biomedical Engineering",
        "slug": "biomedical-engineering",
        "icon_symbol": "🩺",
        "color_hex": "#059669",
        "category": "Engineering",
        "description": "Biomaterials biocompatibility, bioinstrumentation (ECG/EEG amplifiers), medical imaging physics (MRI, CT, Ultrasound), and biomechanics.",
        "is_added": False,
        "chapters": [
            make_ch(
                1241, 1, "Biomaterials, Tissue Compatibility & Scaffolds", 20,
                "Host immune response, protein adsorption, metallic implants (titanium), polymers (PLA/PGA), and tissue engineering scaffolds.",
                ["Biocompatibility definition and ISO 10993 testing standards", "Foreign body response: protein adsorption, macrophages, and fibrous encapsulation", "Metallic biomaterials (Ti-6Al-4V, Co-Cr), ceramics (hydroxyapatite), and polymers", "Biodegradable tissue scaffolds: porosity, cell adhesion, and mechanical matching"],
                ["Hydrolysis degradation rate: dM/dt = -k * M", "Porosity: P = 1 - (rho_scaffold / rho_solid)", "Young's Modulus matching to prevent 'Stress Shielding'"],
                "Biocompatibility & The Foreign Body Response",
                "Biomaterials are non-living materials designed to interact with biological systems for medical evaluation or therapeutics. Implantation immediately triggers protein adsorption (albumin, fibrinogen) onto the material surface. Within hours, neutrophils and monocytes arrive, differentiating into macrophages that attempt phagocytosis. Frustrated phagocytosis leads to macrophage fusion into Foreign Body Giant Cells (FBGCs) and deposition of an avascular collagenous fibrous capsule isolating the implant.",
                "Material Selection & Tissue Scaffolds",
                "Titanium alloys (Ti-6Al-4V) are golden standards for orthopedics due to spontaneous passivating TiO2 oxide films and excellent osteointegration. If implant stiffness greatly exceeds cortical bone (E_titanium ≈ 110 GPa vs E_bone ≈ 18 GPa), 'Stress Shielding' occurs, causing bone resorption due to Wolff's Law. Biodegradable polymer scaffolds (PLA, PGA, PLGA) provide temporary 3D extracellular matrices that degrade via hydrolysis as native cells regenerate tissue.",
                "Stress shielding occurs when an orthopedic implant is too stiff, shielding surrounding bone from mechanical stress and causing bone loss.",
                [
                    {"id": 12411, "question_number": 1, "question": "What is 'Stress Shielding' in orthopedic joint replacement implants?",
                     "options": {"A": "A protective lead coating against radiation", "B": "Bone loss (osteopenia) occurring around a stiff metal implant because the implant bears the mechanical loads, depriving bone of the stress required to stimulate remodeling", "C": "Implant fracturing under shock loads", "D": "Allergic reaction to cobalt"},
                     "correct_option": "B", "explanation": "Wolff's Law states bone remodels in response to applied stress. Stiff metal implants carry the load, shielding bone from stress and causing localized bone resorption."},
                    {"id": 12412, "question_number": 2, "question": "What ceramic material has a chemical composition (Ca10(PO4)6(OH)2) closely resembling the mineral phase of natural human bone?",
                     "options": {"A": "Hydroxyapatite", "B": "Silicon carbide", "C": "Zirconia", "D": "Alumina"},
                     "correct_option": "A", "explanation": "Hydroxyapatite (HA) is a bioactive calcium phosphate ceramic whose crystalline structure mirrors bone mineral, promoting direct biochemical bonding with host bone."},
                    {"id": 12413, "question_number": 3, "question": "What is the final stage of the classical foreign body response to a non-degradable biocompatible subcutaneous implant?",
                     "options": {"A": "Complete tissue regeneration without scarring", "B": "Fibrous encapsulation, where fibroblasts deposit an avascular collagen sheath isolating the implant", "C": "Bone calcification", "D": "Complete dissolution"},
                     "correct_option": "B", "explanation": "When an implant cannot be degraded or phagocytosed, fibroblasts deposit a dense, avascular collagenous capsule that physically walls off the foreign body from tissue."},
                    {"id": 12414, "question_number": 4, "question": "By what mechanism do biodegradable polymers such as Poly(lactic acid) (PLA) and Poly(glycolic acid) (PGA) break down in the human body?",
                     "options": {"A": "Hydrolytic ester cleavage into natural metabolic acids (lactic and glycolic acids)", "B": "High-temperature combustion", "C": "Electrochemical corrosion", "D": "Enzymatic DNA digestion"},
                     "correct_option": "A", "explanation": "PLA and PGA degrade by non-enzymatic bulk hydrolysis of their ester backbone bonds, yielding lactic and glycolic acids metabolized via the Krebs cycle into CO2 and H2O."},
                    {"id": 12415, "question_number": 5, "question": "Why is pure titanium exceptionally biocompatible in orthopedic and dental applications?",
                     "options": {"A": "It dissolves in blood", "B": "It forms a spontaneous, stable, passivating titanium dioxide (TiO2) oxide film that resists corrosion and enables direct osteointegration", "C": "It is magnetic", "D": "It conducts radio frequencies"},
                     "correct_option": "B", "explanation": "Titanium spontaneously forms an inert, nanometer-thin TiO2 oxide layer that blocks ion leaching, resists corrosion, and allows bone osteoblasts to anchor directly to its surface."}
                ],
                [
                    {"chunk_id": "chunk_biomed_01", "subject": "Biomedical Engineering",
                     "chapter": "Biomaterials, Tissue Compatibility & Scaffolds", "topic": "Biomaterials and Tissue Engineering",
                     "keywords": ["biomaterials", "biocompatibility", "foreign body response", "titanium", "hydroxyapatite", "tissue engineering", "stress shielding"],
                     "definition": "Biomaterials science explores the interaction between engineered materials (metallic, ceramic, polymeric) and living biological tissues.",
                     "formula_or_core_rule": "Hydrolysis: dM/dt = -k*M; Porosity P = 1 - (rho_scaffold / rho_solid).",
                     "worked_example": "A titanium stem with E=110 GPa in cortical bone (E=18 GPa) creates a 6:1 stiffness mismatch prone to stress shielding.",
                     "content": "Biocompatible engineering balances mechanical match with surface chemical passivity. Orthopedic implants utilize titanium and bioactive hydroxyapatite, while regenerative medicine employs biodegradable polymer scaffolds."}
                ]
            ),
            make_ch(
                1242, 2, "Physiological Bioinstrumentation & Biosensors", 20,
                "Biopotential origins (action potentials), silver/silver chloride electrodes, instrumentation amplifiers, and ECG/EMG/EEG signal processing.",
                ["Origins of biopotentials: Nernst equation, Goldman-Hodgkin-Katz, and membrane depolarization", "Ag/AgCl non-polarizable biopotential electrodes and half-cell potentials", "Instrumentation Amplifier (In-Amp): CMRR, high input impedance, and noise rejection", "ECG signal features (P-QRS-T complex) and analog front-end filtering"],
                ["Nernst: E_ion = (R * T / (z * F)) * ln([ion]_out / [ion]_in)", "CMRR = 20 * log10(A_diff / A_common) (dB)", "Instrumentation Amp Gain: G = 1 + (2 * R1 / R_gain)"],
                "Electrophysiology & Biopotential Electrodes",
                "Physiological signals originate from ionic transmembrane fluxes across excitable cells. The equilibrium potential for an ion species is governed by the Nernst equation. Biopotential recording requires transducting ionic currents in tissue into electronic currents in copper wires using Ag/AgCl non-polarizable electrodes, which maintain stable half-cell potentials with minimal motion artifact noise.",
                "Instrumentation Amplifiers & Biosignal Conditioning",
                "Biopotentials exhibit microvolt-to-millivolt amplitudes submerged in massive 50/60 Hz power-line interference. An Instrumentation Amplifier (three-op-amp topology) is essential, providing giga-ohm input impedance and ultra-high Common-Mode Rejection Ratio (CMRR > 100 dB) to amplify differential cardiac signals while rejecting identical common-mode noise. Right-Leg Drive (RLD) circuits actively invert and cancel common-mode body displacement currents.",
                "Right Leg Drive (RLD) circuits actively cancel 50/60 Hz common-mode noise on the patient's body by feeding back inverted common-mode signals.",
                [
                    {"id": 12421, "question_number": 1, "question": "Why is an Instrumentation Amplifier with high Common-Mode Rejection Ratio (CMRR > 100 dB) critical for electrocardiogram (ECG) recording?",
                     "options": {"A": "To amplify small differential heart voltages (1 mV) while rejecting massive 50/60 Hz powerline interference (up to 1 V) induced on the human body", "B": "To supply battery power to the heart", "C": "To convert analog sound into MP3", "D": "To defibrillate the patient"},
                     "correct_option": "A", "explanation": "Human bodies act as antennae picking up 50/60 Hz common-mode noise orders of magnitude larger than millivolt cardiac signals; a high CMRR rejects common-mode noise while amplifying differential biopotentials."},
                    {"id": 12422, "question_number": 2, "question": "What physiological event corresponds to the QRS complex on a standard surface ECG tracing?",
                     "options": {"A": "Atrial depolarization", "B": "Ventricular depolarization (initiating ventricular contraction)", "C": "Ventricular repolarization", "D": "Atrial repolarization"},
                     "correct_option": "B", "explanation": "The QRS complex represents rapid electrical depolarization of the right and left ventricles, triggering ventricular contraction."},
                    {"id": 12423, "question_number": 3, "question": "Why are Silver/Silver Chloride (Ag/AgCl) electrodes the universal gold standard for clinical ECG and EEG recording?",
                     "options": {"A": "They are non-polarizable electrodes with stable half-cell potentials and low motion artifact noise", "B": "They contain gold leafing", "C": "They are completely waterproof", "D": "They generate their own electric power"},
                     "correct_option": "A", "explanation": "Ag/AgCl is a reversible, non-polarizable electrode system where charge crosses the interface freely via Ag + Cl- <-> AgCl + e-, minimizing half-cell potential fluctuations."},
                    {"id": 12424, "question_number": 4, "question": "What is the function of a 'Right Leg Drive' (RLD) circuit in medical ECG instrumentation?",
                     "options": {"A": "To stimulate leg muscles", "B": "To measure patient temperature", "C": "To sense common-mode body noise, invert it, and drive it back onto the patient to dynamically cancel power-line interference", "D": "To ground the patient to earth mains"},
                     "correct_option": "C", "explanation": "The Right Leg Drive circuit detects common-mode noise via summing resistors, inverts the signal with high gain, and injects it back to the right leg, reducing patient displacement currents."},
                    {"id": 12425, "question_number": 5, "question": "What does the Nernst equation calculate in cellular electrophysiology?",
                     "options": {"A": "The equilibrium electrical potential across a membrane for a specific ion at concentration equilibrium", "B": "Blood flow rate", "C": "Cardiac output in L/min", "D": "Respiratory volume"},
                     "correct_option": "A", "explanation": "The Nernst equation E = (RT / zF) * ln([ion]_out / [ion]_in) computes the electrical membrane potential that exactly balances an ion's chemical concentration gradient."}
                ],
                [
                    {"chunk_id": "chunk_biomed_02", "subject": "Biomedical Engineering",
                     "chapter": "Physiological Bioinstrumentation & Biosensors", "topic": "Bioinstrumentation and ECG Conditioning",
                     "keywords": ["bioinstrumentation", "ecg", "eeg", "instrumentation amplifier", "cmrr", "nernst equation", "ag/agcl", "right leg drive"],
                     "definition": "Bioinstrumentation applies electrical and electronic engineering principles to acquire, amplify, filter, and process physiological signals from living organisms.",
                     "formula_or_core_rule": "Nernst: E = (RT/zF)*ln(C_out/C_in); InAmp Gain: G = 1 + (2*R1 / Rg); CMRR = 20*log10(Ad / Ac).",
                     "worked_example": "For K+ with [K+]in = 140 mM and [K+]out = 4 mM at 37C: E_K = 61.5 * log10(4 / 140) ≈ -95 mV.",
                     "content": "Bioinstrumentation converts ionic biological events into electronic digital signals. Instrumentation amplifiers provide high input impedance and CMRR, while Ag/AgCl electrodes and RLD circuits cancel 50/60Hz common-mode interference."}
                ]
            ),
            make_ch(
                1243, 3, "Medical Imaging Modalities: X-ray, CT, MRI & Ultrasound", 20,
                "Beer-Lambert X-ray attenuation, CT Radon transform/filtered backprojection, MRI nuclear precession (Larmor), and Ultrasound pulse-echo.",
                ["X-ray physics: Bremsstrahlung, characteristic radiation, and photoelectric attenuation", "Computed Tomography (CT): Radon transform and Filtered Backprojection", "Magnetic Resonance Imaging (MRI): Nuclear spin, Larmor frequency, T1/T2 relaxation", "Medical Ultrasound: Piezoelectric transducers, acoustic impedance, and Doppler effect"],
                ["Beer-Lambert: I = I_0 * exp(-mu * x)", "Larmor Equation: omega_0 = gamma * B_0", "Acoustic Impedance: Z = rho * c", "Doppler Shift: delta_f = (2 * f_0 * v * cos(theta)) / c"],
                "Ionizing Imaging: X-Ray & Computed Tomography (CT)",
                "X-ray imaging exploits differential tissue attenuation governed by the Beer-Lambert law: I = I0 * exp(-mu * x), where linear attenuation coefficient mu scales with tissue atomic number (bone absorbs X-rays via photoelectric effect, appearing white). Computed Tomography (CT) rotates X-ray sources around the body, acquiring 1D attenuation projections at multiple angles. Johann Radon's transform proved that cross-sectional slices can be mathematically reconstructed using Filtered Backprojection (FBP).",
                "Non-Ionizing Imaging: MRI & Ultrasound",
                "Magnetic Resonance Imaging (MRI) aligns hydrogen protons inside a strong static magnetic field B0. Protons precess at the Larmor frequency omega0 = gamma * B0. Radiofrequency (RF) pulses tip magnetization into the transverse plane; relaxation returns protons to equilibrium: T1 (spin-lattice / longitudinal recovery) and T2 (spin-spin / transverse dephasing), producing exquisite soft-tissue contrast without ionizing radiation. Ultrasound transmits high-frequency acoustic pulses via piezoelectric transducers, recording echo reflections caused by acoustic impedance mismatches Z = rho * c.",
                "MRI uses non-ionizing radiofrequency pulses and magnetic fields; CT uses ionizing X-rays.",
                [
                    {"id": 12431, "question_number": 1, "question": "What physical equation governs the resonant precession frequency of hydrogen nuclear spins in Magnetic Resonance Imaging (MRI)?",
                     "options": {"A": "Larmor Equation (omega_0 = gamma * B_0)", "B": "Beer-Lambert Law", "C": "Navier-Stokes equation", "D": "Ohm's Law"},
                     "correct_option": "A", "explanation": "The Larmor equation dictates that nuclear magnetic moments precess at frequency omega_0 directly proportional to the gyromagnetic ratio gamma and static magnetic field strength B_0."},
                    {"id": 12432, "question_number": 2, "question": "What mathematical algorithm reconstructs 2D cross-sectional slices from 1D X-ray projection angles in standard Computed Tomography (CT)?",
                     "options": {"A": "Filtered Backprojection (FBP) based on the inverse Radon transform", "B": "Fast Fourier Transform alone", "C": "Bubble sort", "D": "Finite Element Analysis"},
                     "correct_option": "A", "explanation": "CT detectors acquire line-integral projections formalized by the Radon transform; Filtered Backprojection (FBP) filters projections with a ramp filter to reconstruct unblurred spatial cross-sections."},
                    {"id": 12433, "question_number": 3, "question": "In medical diagnostic ultrasound, what causes acoustic echoes to reflect back to the transducer?",
                     "options": {"A": "Mismatches in acoustic impedance (Z = rho * c) across tissue interfaces", "B": "Electromagnetic radio waves", "C": "Nuclear fission", "D": "X-ray photon absorption"},
                     "correct_option": "A", "explanation": "Ultrasound reflections occur at boundaries between tissues with different acoustic impedances Z = rho * c; the intensity reflection coefficient depends on (Z2 - Z1)^2 / (Z2 + Z1)^2."},
                    {"id": 12434, "question_number": 4, "question": "In MRI tissue contrast, what does the T1 relaxation time describe?",
                     "options": {"A": "Spin-lattice relaxation (the time required for longitudinal magnetization Mz to recover 63% toward equilibrium along B0)", "B": "Total scan duration in minutes", "C": "Transverse spin dephasing", "D": "Patient breathing rate"},
                     "correct_option": "A", "explanation": "T1 (spin-lattice) relaxation measures the rate at which excited protons transfer energy back to the surrounding molecular lattice, recovering longitudinal magnetization."},
                    {"id": 12435, "question_number": 5, "question": "Why do bones appear bright white on conventional diagnostic X-ray radiographs while soft tissue appears dark grey?",
                     "options": {"A": "Bone has higher density and effective atomic number Z, absorbing significantly more X-ray photons via photoelectric effect", "B": "Bone emits visible light", "C": "Soft tissue reflects all X-rays", "D": "X-rays bounce off skin"},
                     "correct_option": "A", "explanation": "Bone has a higher atomic number (calcium Z=20, phosphorus Z=15) than water/soft tissue (carbon, oxygen, hydrogen), causing higher photoelectric attenuation and fewer photons reaching the detector."},
                ],
                [
                    {"chunk_id": "chunk_biomed_03", "subject": "Biomedical Engineering",
                     "chapter": "Medical Imaging Modalities: X-ray, CT, MRI & Ultrasound", "topic": "Medical Imaging Physics",
                     "keywords": ["medical imaging", "mri", "ct scan", "x-ray", "ultrasound", "larmor frequency", "radon transform", "t1 t2 relaxation"],
                     "definition": "Medical imaging encompasses diagnostic visualization techniques (ionizing X-ray/CT and non-ionizing MRI/Ultrasound) to reveal internal human anatomy and physiology.",
                     "formula_or_core_rule": "Larmor: omega = gamma * B0; Beer-Lambert: I = I0 * exp(-mu * x); Acoustic Impedance: Z = rho * c.",
                     "worked_example": "For hydrogen (gamma = 42.58 MHz/T) in a 1.5 Tesla clinical MRI scanner: resonant frequency omega_0 = 42.58 * 1.5 = 63.87 MHz.",
                     "content": "Diagnostic imaging bridges physics and medicine: CT reconstructs anatomical density matrices via X-ray Radon transforms, MRI captures soft-tissue proton relaxation, and Ultrasound tracks hemodynamic blood flow via Doppler shifts."}
                ]
            )
        ]
    })

    # 125. Chemical Process Engineering
    subs.append({
        "id": "SUB_CHEMENG",
        "subject_id": 125,
        "name": "Chemical Process Engineering",
        "slug": "chemical-process-engineering",
        "icon_symbol": "⚗️",
        "color_hex": "#D97706",
        "category": "Engineering",
        "description": "Mass and energy balances, heat exchanger design (LMTD), fractional distillation (McCabe-Thiele), and chemical reactor engineering.",
        "is_added": False,
        "chapters": [
            make_ch(
                1251, 1, "Mass and Energy Balances in Steady-State Chemical Systems", 20,
                "Conservation of mass, degrees of freedom analysis, recycle/purge streams, and first-law enthalpy energy balances.",
                ["General balance equation: Input + Generation - Output - Consumption = Accumulation", "Steady-state non-reactive and reactive species mass balances", "Recycle, bypass, and purge stream stoichiometry", "Energy balances with reaction: Heat of reaction and enthalpy of formation"],
                ["Accumulation = Input - Output + Generation - Consumption = 0 (Steady-State)", "Degrees of Freedom: N_df = N_unknowns - N_independent_eqs", "Delta_H_rxn = Sum(nu_p * Delta_H_f_products) - Sum(nu_r * Delta_H_f_reactants)"],
                "Mass Balances & Degrees of Freedom Analysis",
                "Chemical process engineering designs large-scale transformations of raw materials into valuable chemical products. The core design principle is conservation: Input + Generation - Output - Consumption = Accumulation. In steady-state continuous processes, Accumulation = 0. A systematic Degrees of Freedom analysis (N_df = N_unknowns - N_independent_equations) proves whether a process flowsheet is mathematically solvable (N_df = 0).",
                "Recycle, Bypass & Purge Systems",
                "Industrial flowsheets incorporate recycle streams to return unreacted reagents to the reactor, elevating overall conversion towards 100%. However, non-reactive inert impurities would accumulate indefinitely in a closed recycle loop, requiring a dedicated Purge stream to bleed off inerts at steady-state. Energy balances apply the First Law of Thermodynamics, tracking sensible heat, latent heat, and reaction enthalpies.",
                "A purge stream is mathematically required in any recycle loop where an inert species enters with the feed to prevent unbounded inert buildup.",
                [
                    {"id": 12511, "question_number": 1, "question": "Why is a continuous purge stream mandatory in an industrial chemical process flowsheet containing a recycle loop with an inert non-reactive impurity?",
                     "options": {"A": "To prevent inert impurities from accumulating indefinitely within the recycle loop and diluting reactants", "B": "To increase operating pressure", "C": "To cool the reactor", "D": "To change the catalyst color"},
                     "correct_option": "A", "explanation": "If inerts enter with the feed and cannot react or escape, they accumulate in the recycle loop until the system chokes; a purge stream balances inert input with output at steady-state."},
                    {"id": 12512, "question_number": 2, "question": "What does a Degrees of Freedom analysis result of N_df = 0 signify for a chemical process unit?",
                     "options": {"A": "The system is exactly specified and mathematically solvable with a unique solution", "B": "The system is underspecified with infinite solutions", "C": "The system is overspecified and contradictory", "D": "The reaction cannot occur"},
                     "correct_option": "A", "explanation": "N_df = Unknowns - Independent Equations = 0 means the number of independent physical relationships matches the number of variables, ensuring a unique solution."},
                    {"id": 12513, "question_number": 3, "question": "For a steady-state chemical system without chemical reactions, what does the general mass balance reduce to?",
                     "options": {"A": "Input = Output", "B": "Input + Generation = Output", "C": "Accumulation = Input", "D": "Output = 0"},
                     "correct_option": "A", "explanation": "At steady state (Accumulation = 0) and without reactions (Generation = Consumption = 0), the mass balance simplifies to Input = Output."},
                    {"id": 12514, "question_number": 4, "question": "How is the standard heat of reaction Delta_H_rxn calculated from standard heats of formation Delta_H_f?",
                     "options": {"A": "Sum of Delta_H_f(Products) minus Sum of Delta_H_f(Reactants)", "B": "Sum of Reactants minus Products", "C": "Reactant enthalpy divided by product enthalpy", "D": "Zero for all reactions"},
                     "correct_option": "A", "explanation": "By Hess's law, standard reaction enthalpy equals the stoichiometric sum of product heats of formation minus the stoichiometric sum of reactant heats of formation."},
                    {"id": 12515, "question_number": 5, "question": "What is the difference between single-pass conversion and overall conversion in a reactor with a recycle loop?",
                     "options": {"A": "Overall conversion evaluates reactant consumed across the entire plant boundary, whereas single-pass conversion evaluates reactant consumed across one pass through the reactor alone", "B": "They are identical numbers", "C": "Single-pass is always higher than overall", "D": "Overall conversion ignores unreacted materials"},
                     "correct_option": "A", "explanation": "Single-pass conversion = (Reactant_in_reactor - Reactant_out_reactor) / Reactant_in_reactor; overall conversion incorporates recycling, achieving much higher total plant conversion."}
                ],
                [
                    {"chunk_id": "chunk_chemeng_01", "subject": "Chemical Process Engineering",
                     "chapter": "Mass and Energy Balances in Steady-State Chemical Systems", "topic": "Mass and Energy Balances",
                     "keywords": ["chemical engineering", "mass balance", "energy balance", "recycle stream", "purge", "degrees of freedom", "stoichiometry"],
                     "definition": "Mass and energy balances are the fundamental accounting principles of chemical engineering that track material flows, reactions, and heat across plant unit operations.",
                     "formula_or_core_rule": "Accumulation = Input - Output + Generation - Consumption; N_df = Unknowns - Equations.",
                     "worked_example": "Feed of 100 mol/s with 10% inerts and 90% A: single pass conversion is 50%, but recycling with a 10 mol/s purge yields >95% overall conversion.",
                     "content": "Process engineering balances matter and energy. Flowsheet analysis establishes steady-state operating points, sizing recycle and purge streams to maximize yield while preventing inert buildup."}
                ]
            ),
            make_ch(
                1252, 2, "Heat Transfer Mechanisms & Heat Exchanger Design (LMTD)", 20,
                "Conduction (Fourier), convection (Newton), radiation (Stefan-Boltzmann), overall heat transfer U, and shell-and-tube LMTD design.",
                ["Three modes of heat transfer: Conduction, Convection, and Thermal Radiation", "Fourier's law, Newton's law of cooling, and Stefan-Boltzmann law", "Overall heat transfer coefficient U and fouling resistances", "Log-Mean Temperature Difference (LMTD) and NTU-effectiveness method"],
                ["Conduction: q = -k * A * (dT / dx)", "Convection: q = h * A * (T_s - T_inf)", "Heat Exchanger: Q = U * A * F * Delta_T_LMTD", "LMTD: Delta_T_lm = (Delta_T1 - Delta_T2) / ln(Delta_T1 / Delta_T2)"],
                "Heat Transfer Modes & The Overall Coefficient U",
                "Heat transfer drives energy exchange down thermal gradients. Conduction governs molecular transfer via Fourier's Law (q = -k*A*dT/dx). Convection governs fluid transport via Newton's law of cooling (q = h*A*(Ts - T_inf)). In industrial heat exchangers, heat transfers through liquid-solid-liquid composite thermal resistances, combined into the Overall Heat Transfer Coefficient U: 1/(U*A) = 1/(h_inner*A_inner) + R_wall + 1/(h_outer*A_outer) + R_fouling.",
                "Log-Mean Temperature Difference (LMTD) & Sizing",
                "In continuous shell-and-tube or plate heat exchangers, fluid temperatures vary continuously along path length, invalidating simple arithmetic temperature differences. Heat duty is accurately calculated using the Log-Mean Temperature Difference (LMTD): Q = U * A * F * Delta_T_lm, where F is the multi-pass correction factor. When terminal temperatures are unknown, the Number of Transfer Units (NTU-effectiveness) method calculates performance.",
                "Counter-current flow heat exchangers are always thermodynamically superior to co-current flow because they provide a larger effective LMTD and can heat the cold stream above the hot stream's outlet temperature.",
                [
                    {"id": 12521, "question_number": 1, "question": "Why is counter-current flow thermodynamically superior to co-current (parallel) flow in industrial heat exchangers?",
                     "options": {"A": "Counter-current maintains a more uniform temperature difference throughout the unit, achieving a higher LMTD and allowing the cold outlet to exceed the hot outlet temperature", "B": "Counter-current uses smaller pipes", "C": "Co-current violates the First Law of Thermodynamics", "D": "Counter-current eliminates fluid friction"},
                     "correct_option": "A", "explanation": "In counter-current flow, fluids travel in opposite directions; the driving temperature difference is maintained throughout the length, maximizing LMTD and heat recovery."},
                    {"id": 12522, "question_number": 2, "question": "What is the Log-Mean Temperature Difference (LMTD) formula for terminal temperature differences Delta_T1 and Delta_T2?",
                     "options": {"A": "(Delta_T1 - Delta_T2) / ln(Delta_T1 / Delta_T2)", "B": "(Delta_T1 + Delta_T2) / 2", "C": "sqrt(Delta_T1 * Delta_T2)", "D": "Delta_T1 - Delta_T2"},
                     "correct_option": "A", "explanation": "The LMTD is mathematically derived from integrating temperature differentials along heat exchanger area: Delta_T_lm = (Delta_T1 - Delta_T2) / ln(Delta_T1 / Delta_T2)."},
                    {"id": 12523, "question_number": 3, "question": "What does Fourier's law of thermal conduction state?",
                     "options": {"A": "Conductive heat flux is directly proportional to the negative temperature gradient: q = -k * (dT / dx)", "B": "Heat flows only in vacuum", "C": "Thermal conductivity k is always zero", "D": "Conduction scales with velocity cubed"},
                     "correct_option": "A", "explanation": "Fourier's law states that heat flux by conduction is proportional to the negative temperature gradient, with thermal conductivity k acting as the transport property."},
                    {"id": 12524, "question_number": 4, "question": "What is the effect of 'fouling resistance' (scale/sludge deposition) on heat exchanger performance over time?",
                     "options": {"A": "It adds conductive thermal resistance, reducing the overall heat transfer coefficient U and requiring increased surface area or chemical cleaning", "B": "It increases heat transfer", "C": "It doubles fluid velocity", "D": "It cools the pipes"},
                     "correct_option": "A", "explanation": "Fouling layers (scaling, rust, biological growth) have low thermal conductivity, degrading the overall heat transfer coefficient U and reducing heat exchange capacity."},
                    {"id": 12525, "question_number": 5, "question": "How does radiant thermal heat transfer between two surfaces scale with absolute temperature T according to the Stefan-Boltzmann law?",
                     "options": {"A": "T^4 (fourth power of absolute temperature)", "B": "T^2", "C": "Linearly with T", "D": "ln(T)"},
                     "correct_option": "A", "explanation": "The Stefan-Boltzmann law states total radiant emissive power of a blackbody is proportional to the fourth power of its absolute thermodynamic temperature: E = sigma * T^4."}
                ],
                [
                    {"chunk_id": "chunk_chemeng_02", "subject": "Chemical Process Engineering",
                     "chapter": "Heat Transfer Mechanisms & Heat Exchanger Design (LMTD)", "topic": "Heat Exchangers and LMTD Method",
                     "keywords": ["heat transfer", "heat exchanger", "lmtd", "fourier law", "convection", "conduction", "fouling", "shell and tube"],
                     "definition": "Heat transfer in chemical engineering sizes process equipment (boilers, condensers, shell-and-tube exchangers) to transfer thermal energy between process streams.",
                     "formula_or_core_rule": "Q = U * A * F * Delta_T_LMTD; LMTD = (dT1 - dT2) / ln(dT1/dT2); q = -k*A*(dT/dx).",
                     "worked_example": "For Delta_T1 = 40K and Delta_T2 = 20K: LMTD = (40 - 20) / ln(40 / 20) = 20 / 0.693 = 28.85 K.",
                     "content": "Thermal process design sizes heat exchangers via overall heat transfer coefficient U and LMTD. Counter-current layouts maximize energy recovery across chemical refining operations."}
                ]
            ),
            make_ch(
                1253, 3, "Mass Transfer Operations: Distillation & Chemical Reactors", 20,
                "Vapor-liquid equilibrium (Raoult's law), McCabe-Thiele distillation columns, and chemical reactor design (CSTR vs PFR).",
                ["Vapor-Liquid Equilibrium (VLE): Raoult's law and relative volatility alpha", "Fractional distillation: McCabe-Thiele graphical method (q-line, operating lines)", "Ideal reactor models: Batch, Continuous Stirred-Tank (CSTR), and Plug Flow (PFR)", "Arrhenius reaction kinetics and reactor volume comparison"],
                ["Raoult's Law: y_i * P = x_i * P_i_sat", "Relative Volatility: alpha = (y_A / x_A) / (y_B / x_B)", "CSTR Volume: V = F_A0 * X / (-r_A)", "PFR Volume: V = F_A0 * Integral_0^X (dX / (-r_A))"],
                "Fractional Distillation & McCabe-Thiele Analysis",
                "Distillation separates fluid mixtures exploiting differences in boiling points and relative volatility alpha. Raoult's law describes ideal vapor-liquid equilibrium: y_i * P = x_i * P_sat. The McCabe-Thiele graphical method determines the theoretical number of equilibrium stages by plotting the equilibrium curve alongside the Rectifying Operating Line, Stripping Operating Line, and feed thermal condition line (q-line). Reflux ratio R = L/D governs column separation efficiency versus reboiler energy cost.",
                "Chemical Reactor Design: CSTR vs Plug Flow (PFR)",
                "Chemical reactors convert chemical species. The Continuous Stirred-Tank Reactor (CSTR) assumes instantaneous, perfect mixing, operating entirely at exit outlet concentration and reaction rate. The Plug Flow Reactor (PFR) assumes fluid travels as orderly plugs without axial mixing, with concentration and reaction rate decaying along length. For positive-order reaction kinetics, a PFR requires significantly smaller reactor volume than a CSTR for identical conversion.",
                "For positive-order reaction kinetics, a Plug Flow Reactor (PFR) always requires less reactor volume than a CSTR for the same conversion.",
                [
                    {"id": 12531, "question_number": 1, "question": "Why does a Plug Flow Reactor (PFR) require smaller volume than a Continuous Stirred-Tank Reactor (CSTR) for positive-order kinetics (e.g. -rA = k*CA) at the same conversion?",
                     "options": {"A": "PFR operates across higher initial reactant concentrations along its length, whereas CSTR operates entirely at the lowest (exit) concentration and slowest rate", "B": "CSTR has no motor", "C": "PFR requires no catalyst", "D": "PFR runs at absolute zero"},
                     "correct_option": "A", "explanation": "In a CSTR, reaction rate is evaluated at the lowest exit concentration. In a PFR, concentration starts high and decays along length, yielding a higher average reaction rate and requiring smaller volume."},
                    {"id": 12532, "question_number": 2, "question": "In the McCabe-Thiele distillation method, what does the 'q-line' represent?",
                     "options": {"A": "The thermal condition of the feed stream (subcooled liquid, saturated liquid, vapor-liquid mix, saturated vapor, superheated vapor)", "B": "Cooling water temperature", "C": "Column height in meters", "D": "Condenser pressure"},
                     "correct_option": "A", "explanation": "The q-line represents the feed quality and enthalpy: q=1 for saturated liquid, q=0 for saturated vapor, q>1 for subcooled liquid, and 0<q<1 for flashing mix."},
                    {"id": 12533, "question_number": 3, "question": "What is an 'azeotrope' in vapor-liquid equilibrium and distillation?",
                     "options": {"A": "A liquid mixture whose vapor has the exact same composition as the liquid at boiling, preventing further separation by standard fractional distillation", "B": "A catalyst poison", "C": "A pure component", "D": "A solid precipitate"},
                     "correct_option": "A", "explanation": "At an azeotrope, relative volatility alpha = 1.0 (vapor composition y equals liquid composition x), making separation by ordinary binary distillation impossible."},
                    {"id": 12534, "question_number": 4, "question": "According to Raoult's Law for ideal solutions, what is partial pressure p_i of component i in the vapor phase?",
                     "options": {"A": "p_i = x_i * P_i_sat (liquid mole fraction multiplied by pure component saturation vapor pressure)", "B": "p_i = Total Pressure / 2", "C": "p_i = Temperature * Density", "D": "p_i = x_i / P_i_sat"},
                     "correct_option": "A", "explanation": "Raoult's law states partial pressure equals liquid mole fraction times pure component vapor pressure: p_i = x_i * P_i_sat."},
                    {"id": 12535, "question_number": 5, "question": "What is the consequence of operating a distillation column at 'Total Reflux' (reflux ratio R = infinity, returning all overhead condensate back into the column)?",
                     "options": {"A": "It requires the absolute minimum number of theoretical equilibrium stages (N_min), but yields zero distillate product rate", "B": "It requires infinite column stages", "C": "The column runs dry", "D": "Energy consumption drops to zero"},
                     "correct_option": "A", "explanation": "At total reflux, operating lines coincide with the 45-degree diagonal line y = x, yielding the minimum possible number of stages (Fenske equation) but zero net product withdrawal."}
                ],
                [
                    {"chunk_id": "chunk_chemeng_03", "subject": "Chemical Process Engineering",
                     "chapter": "Mass Transfer Operations: Distillation & Chemical Reactors", "topic": "Distillation and Reactor Design",
                     "keywords": ["distillation", "mccabe-thiele", "cstr", "pfr", "raoults law", "azeotrope", "reaction kinetics", "vle"],
                     "definition": "Mass transfer operations (distillation, absorption) separate chemical mixtures based on vapor-liquid equilibrium, while reactor engineering designs vessels to maximize chemical reaction yield.",
                     "formula_or_core_rule": "Raoult: y_i*P = x_i*P_sat; CSTR: V = F_A0 * X / (-r_A); PFR: V = F_A0 * int(dX / -rA).",
                     "worked_example": "For a 1st-order reaction, achieving 90% conversion: CSTR requires V = F_A0 * 0.9 / (k * 0.1) = 9 * F_A0 / k; PFR requires V = (F_A0 / k) * ln(1 / 0.1) = 2.3 * F_A0 / k.",
                     "content": "Chemical process engineering utilizes McCabe-Thiele distillation stages to purify chemical products and models CSTR and PFR reactors via Arrhenius kinetics to maximize chemical conversion."}
                ]
            )
        ]
    })

    return subs

