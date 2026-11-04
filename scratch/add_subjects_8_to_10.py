# scratch/add_subjects_8_to_10.py
# Appends subjects 118, 119, 120 to complete Batch 1 (Tech & Computing)

def append_8_to_10(subs):
    from gen_subjects_1_to_10 import make_ch

    # 118. Computer Graphics & Game Engine Dev
    subs.append({
        "id": "SUB_GRAPHICS",
        "subject_id": 118,
        "name": "Computer Graphics & Game Engine Dev",
        "slug": "computer-graphics-game-engine",
        "icon_symbol": "🎮",
        "color_hex": "#EC4899",
        "category": "Engineering",
        "description": "3D rasterization pipeline, shaders (GLSL/HLSL), physically based rendering (PBR), ray tracing, and real-time game physics.",
        "is_added": False,
        "chapters": [
            make_ch(
                1181, 1, "3D Rendering Pipeline & Rasterization Mathematics", 20,
                "Model-View-Projection matrices, homogeneous clipping, barycentric rasterization, and Z-buffering depth testing.",
                ["Coordinate spaces: Object, World, View/Camera, and Clip Space", "Model-View-Projection (MVP) 4x4 matrix transformations", "Perspective division and Normalized Device Coordinates (NDC)", "Triangle rasterization and Barycentric interpolation"],
                ["P_clip = M_proj * M_view * M_model * P_local", "Barycentric: P = u*A + v*B + w*C (u + v + w = 1)", "Perspective Division: (x_ndc, y_ndc, z_ndc) = (x_clip/w, y_clip/w, z_clip/w)"],
                "Coordinate Transformations & Perspective Projection",
                "The 3D graphics rendering pipeline transforms geometric primitives (triangles) from local 3D coordinate spaces into 2D screen pixels. The Model matrix positions meshes in World space, the View matrix establishes camera orientation, and the Perspective Projection matrix applies foreshortening, scaling z-depth into the homogeneous w-coordinate. After perspective division, vertices lie in Normalized Device Coordinates (NDC) bounded by [-1, 1].",
                "Rasterization & Z-Buffer Depth Testing",
                "Rasterization determines which discrete pixel fragments are covered by each projected triangle using edge functions or barycentric coordinates. Barycentric weights (u, v, w) smoothly interpolate vertex attributes (normals, UV coordinates, colors) across triangle surfaces. The Z-buffer algorithm maintains a depth buffer, testing fragment depth to discard occluded geometry.",
                "Homogeneous perspective division by w MUST be performed before rasterization, and vertex attributes must be perspective-corrected interpolated via (attribute / w).",
                [
                    {"id": 11811, "question_number": 1, "question": "What is the purpose of the Z-buffer (depth buffer) in the 3D graphics rasterization pipeline?",
                     "options": {"A": "To compress textures", "B": "To resolve hidden surface removal by comparing fragment depths and discarding occluded pixels", "C": "To store vertex colors", "D": "To play audio effects"},
                     "correct_option": "B", "explanation": "The Z-buffer stores the closest depth value for each pixel; incoming fragments with greater depth values are discarded, ensuring proper occlusion."},
                    {"id": 11812, "question_number": 2, "question": "In what coordinate space are geometric coordinates bounded strictly within the range [-1.0, +1.0] across all three axes?",
                     "options": {"A": "World space", "B": "Normalized Device Coordinates (NDC)", "C": "Model space", "D": "Screen pixel space"},
                     "correct_option": "B", "explanation": "After perspective division by homogeneous w, coordinates reside in Normalized Device Coordinates (NDC), forming a standardized unit cube [-1, 1]^3."},
                    {"id": 11813, "question_number": 3, "question": "What mathematical tool interpolates vertex attributes (UVs, normals) across the interior of a rasterized triangle?",
                     "options": {"A": "Barycentric coordinates (u, v, w)", "B": "Fast Fourier Transform", "C": "Euler angles", "D": "Gaussian elimination"},
                     "correct_option": "A", "explanation": "Barycentric coordinates express any point inside a triangle as a weighted convex combination of its three vertices (u + v + w = 1), enabling smooth attribute interpolation."},
                    {"id": 11814, "question_number": 4, "question": "Why is perspective-correct interpolation required when mapping textures to 3D polygons?",
                     "options": {"A": "Linear interpolation in 2D screen space does not account for the non-linear foreshortening caused by perspective projection", "B": "Textures have no color", "C": "To avoid GPU overheating", "D": "To rotate normals"},
                     "correct_option": "A", "explanation": "Perspective projection causes non-linear compression in screen space; dividing attributes by depth w before interpolating ensures textures do not distort when viewed at an angle."},
                    {"id": 11815, "question_number": 5, "question": "Which transformation matrix converts coordinates from World space into Camera/View space?",
                     "options": {"A": "Model matrix", "B": "View matrix", "C": "Projection matrix", "D": "Viewport matrix"},
                     "correct_option": "B", "explanation": "The View matrix (often constructed via lookAt) transforms coordinates from global world space into the camera's local coordinate frame."}
                ],
                [
                    {"chunk_id": "chunk_graph_01", "subject": "Computer Graphics & Game Engine Dev",
                     "chapter": "3D Rendering Pipeline & Rasterization Mathematics", "topic": "3D Graphics Pipeline and Rasterization",
                     "keywords": ["computer graphics", "rasterization", "mvp matrix", "z-buffer", "barycentric coordinates", "ndc", "projection"],
                     "definition": "The graphics pipeline is the sequential hardware sequence that translates 3D polygonal models into a 2D raster image on a display.",
                     "formula_or_core_rule": "P_clip = Projection * View * Model * P_local; P = u*A + v*B + w*C.",
                     "worked_example": "A vertex at (0, 0, -5, 5) in clip space undergoes perspective division by w=5, yielding NDC coordinates (0, 0, -1).",
                     "content": "The 3D rendering pipeline processes vertex streams through Model-View-Projection matrices, clips geometry outside the view frustum, rasterizes triangles using barycentric weights, and resolves occlusions via Z-buffering."}
                ]
            ),
            make_ch(
                1182, 2, "Shaders, Lighting Models & Physically Based Rendering (PBR)", 20,
                "Programmable shaders, Phong reflection, Cook-Torrance microfacet BRDF, and real-time Ray Tracing.",
                ["Programmable pipeline: Vertex, Fragment (Pixel), and Compute shaders", "Blinn-Phong empirical lighting model: Ambient, Diffuse, Specular", "Physically Based Rendering (PBR): Microfacet theory and Cook-Torrance BRDF", "Fresnel-Schlick approximation and roughness-metallic workflow"],
                ["I_phong = k_a*I_a + k_d*(N . L)*I_d + k_s*(N . H)^shininess * I_s", "f_cook_torrance = (D * F * G) / (4 * (N . V) * (N . L))", "F_schlick = F0 + (1 - F0) * (1 - (V . H))^5"],
                "Empirical Lighting to Physically Based Rendering (PBR)",
                "Early computer graphics utilized the empirical Blinn-Phong lighting model, which approximates surface reflection as the sum of constant ambient, Lambertian diffuse, and halfway-vector specular highlights. Modern engines implement Physically Based Rendering (PBR), enforcing conservation of energy: light reflected (diffuse + specular) can never exceed incident irradiance.",
                "Cook-Torrance Microfacet BRDF Architecture",
                "PBR relies on the Cook-Torrance Bidirectional Reflectance Distribution Function (BRDF). The specular component combines three physical terms: the Normal Distribution Function D (GGX/Trowbridge-Reitz microfacet alignment), the Geometric Attenuation G (self-shadowing and masking of micro-facets), and the Fresnel equation F (reflection intensity increasing at grazing angles, approximated via Schlick's formula).",
                "In PBR, metallic surfaces have zero diffuse reflection (albedo is absorbed) and reflect tinted specular color based on their base reflectance F0.",
                [
                    {"id": 11821, "question_number": 1, "question": "What fundamental physical law must be strictly obeyed by Physically Based Rendering (PBR) lighting models?",
                     "options": {"A": "Conservation of Energy (total reflected light cannot exceed total incident light energy)", "B": "Second law of thermodynamics", "C": "Newton's third law", "D": "Kepler's second law"},
                     "correct_option": "A", "explanation": "PBR models enforce energy conservation: the sum of diffuse reflection and specular reflection cannot exceed 100% of the incident radiant energy."},
                    {"id": 11822, "question_number": 2, "question": "In the Cook-Torrance specular BRDF formula (D * F * G) / (4 * (N.V) * (N.L)), what does term 'F' represent?",
                     "options": {"A": "Focal length", "B": "Fresnel reflectance (describing how reflection increases at grazing angles)", "C": "Frame rate", "D": "Friction coefficient"},
                     "correct_option": "B", "explanation": "The Fresnel term F describes the fraction of incident light reflected versus refracted as a function of the angle of incidence, typically approximated by Schlick's formula."},
                    {"id": 11823, "question_number": 3, "question": "In the Blinn-Phong lighting model, how is the specular highlight computed?",
                     "options": {"A": "Dot product of the surface normal N and the halfway vector H (normalized sum of light vector L and view vector V)", "B": "Cross product of view and light vectors", "C": "Random noise generator", "D": "Ambient color multiplied by vertex height"},
                     "correct_option": "A", "explanation": "Blinn-Phong replaces the computationally expensive reflected light vector with the halfway vector H = normalize(L + V), evaluating (N . H)^shininess."},
                    {"id": 11824, "question_number": 4, "question": "What is the primary visual characteristic of metallic (conductive) surfaces in a standard PBR metallic-roughness workflow?",
                     "options": {"A": "High diffuse albedo and white highlights", "B": "Zero diffuse reflection (black diffuse) with specular reflections tinted by base albedo color", "C": "Total transparency", "D": "Inability to reflect light"},
                     "correct_option": "B", "explanation": "Metals absorb refracted light into free electron oscillations, leaving zero diffuse reflectance and producing tinted specular reflections determined by their base color."},
                    {"id": 11825, "question_number": 5, "question": "What hardware acceleration enables modern GPUs to execute real-time ray tracing (e.g., DXR, Vulkan Ray Tracing)?",
                     "options": {"A": "Dedicated RT Cores accelerating Bounding Volume Hierarchy (BVH) traversal and ray-triangle intersection tests", "B": "Larger power supplies", "C": "Faster display cables", "D": "Dual CPUs"},
                     "correct_option": "A", "explanation": "Modern GPUs integrate dedicated ray-tracing hardware cores that accelerate tree traversals through Bounding Volume Hierarchies (BVH) and ray-primitive intersections in silicon."}
                ],
                [
                    {"chunk_id": "chunk_graph_02", "subject": "Computer Graphics & Game Engine Dev",
                     "chapter": "Shaders, Lighting Models & Physically Based Rendering (PBR)", "topic": "PBR and Lighting Models",
                     "keywords": ["pbr", "shading", "brdf", "cook-torrance", "fresnel", "blinn-phong", "ray tracing", "shaders"],
                     "definition": "Physically Based Rendering (PBR) is a computer graphics approach that simulates light transport obeying physical laws such as energy conservation and microfacet theory.",
                     "formula_or_core_rule": "f_spec = (D * F * G) / (4 * (N . V) * (N . L)); F_schlick = F0 + (1 - F0)*(1 - (V . H))^5.",
                     "worked_example": "At grazing angle (V . H = 0), Fresnel reflectance F_schlick converges to 1.0 (100% specular reflection) for all materials.",
                     "content": "Modern game engines utilize PBR microfacet BRDFs combining GGX normal distributions, Schlick Fresnel approximations, and geometric shadowing. Real-time ray tracing augments rasterization by shooting rays for accurate reflections, global illumination, and ambient occlusion."}
                ]
            ),
            make_ch(
                1183, 3, "Game Engine Architecture, Scene Graphs & Physics", 20,
                "Entity-Component-System (ECS), scene graph spatial hierarchies, octrees/BVH, and rigid-body collision dynamics.",
                ["Game loop architecture: Fixed timestep physics vs variable render updates", "Entity Component System (ECS) architecture for cache-friendly memory layout", "Spatial partitioning: Octrees, k-d trees, and Bounding Volume Hierarchies (BVH)", "Rigid body collision detection (GJK, SAT) and impulse-based resolution"],
                ["Velocity Verlet: x(t+dt) = x(t) + v(t)*dt + 0.5*a(t)*dt^2", "Separating Axis Theorem (SAT): Overlap on all axes => Collision", "Impulse J = -(1 + e) * v_rel / (1/m1 + 1/m2)"],
                "Entity Component System & Memory Locality",
                "Traditional object-oriented game architectures suffer from deep inheritance hierarchies and cache-unfriendly pointer chasing. Modern high-performance game engines adopt the Entity Component System (ECS) pattern. Entities are simple integer IDs, Components are pure data structs packed contiguously in memory arrays, and Systems execute pure logic over matched component streams, maximizing CPU L1/L2 cache hits.",
                "Spatial Partitioning & Collision Detection",
                "Real-time game engines organize dynamic objects using scene graphs and spatial partitioning acceleration structures (such as Octrees and Bounding Volume Hierarchies - BVH) to accelerate frustum culling from O(N) to O(log N). Collision detection uses a two-phase pipeline: Broad Phase eliminates distant pairs using Axis-Aligned Bounding Boxes (AABB), and Narrow Phase computes exact contact manifolds using algorithms like Gilbert-Johnson-Keerthi (GJK) and the Separating Axis Theorem (SAT).",
                "Physics simulations must always execute with a fixed timestep dt (e.g. 1/60s) to maintain numerical stability and deterministic reproducibility.",
                [
                    {"id": 11831, "question_number": 1, "question": "Why is the Entity-Component-System (ECS) pattern preferred over traditional object-oriented inheritance in modern game engines?",
                     "options": {"A": "It packs homogeneous component data contiguously in memory arrays, maximizing CPU hardware cache line efficiency", "B": "It eliminates the need for 3D models", "C": "It removes audio files", "D": "It avoids using graphics drivers"},
                     "correct_option": "A", "explanation": "ECS groups identical component structs contiguously in memory (Structure of Arrays), maximizing CPU L1 cache utilization during bulk system updates."},
                    {"id": 11832, "question_number": 2, "question": "What is the primary role of the 'Broad Phase' in game engine collision detection?",
                     "options": {"A": "Quickly eliminating non-colliding object pairs using coarse bounding volumes (e.g., AABB) before expensive narrow-phase polygon testing", "B": "Playing impact sound effects", "C": "Calculating friction impulses", "D": "Simulating cloth tearing"},
                     "correct_option": "A", "explanation": "Testing every polygon pair scales as O(N^2); the broad phase filters out obvious non-intersecting pairs using fast bounding volume hierarchies."},
                    {"id": 11833, "question_number": 3, "question": "According to the Separating Axis Theorem (SAT), when are two convex polyhedra in collision?",
                     "options": {"A": "When their projected intervals overlap on every possible separating axis", "B": "When their centers of mass have the same velocity", "C": "When they share at least one vertex", "D": "When their bounding spheres touch"},
                     "correct_option": "A", "explanation": "SAT states that two convex shapes collide if and only if there exists no axis onto which their projections are completely disjoint."},
                    {"id": 11834, "question_number": 4, "question": "Why do game physics engines enforce a 'fixed timestep' (e.g., dt = 16.6ms) for physics integration rather than using variable render delta-time?",
                     "options": {"A": "To prevent numerical instability and ensure deterministic, reproducible physics simulations", "B": "To lock monitor refresh rates", "C": "To avoid using floating point math", "D": "To match keyboard typing speed"},
                     "correct_option": "A", "explanation": "Variable delta-times introduce accumulator jitter and numerical integration divergence in Verlet/Euler solvers; fixed timesteps guarantee stability and deterministic simulation."},
                    {"id": 11835, "question_number": 5, "question": "What spatial acceleration data structure subdivides 3D space recursively into 8 octants?",
                     "options": {"A": "Octree", "B": "Quadtree", "C": "Binary Search Tree", "D": "Red-Black Tree"},
                     "correct_option": "A", "explanation": "An Octree is a hierarchical tree data structure where each internal node has exactly eight child subdivisions corresponding to spatial octants in 3D."}
                ],
                [
                    {"chunk_id": "chunk_graph_03", "subject": "Computer Graphics & Game Engine Dev",
                     "chapter": "Game Engine Architecture, Scene Graphs & Physics", "topic": "Game Engine Architecture and Physics",
                     "keywords": ["game engine", "ecs", "scene graph", "octree", "bvh", "collision detection", "sat", "gjk", "verlet"],
                     "definition": "A game engine is a software development framework providing integrated suites for real-time rendering, physics simulation, spatial indexing, and asset management.",
                     "formula_or_core_rule": "Velocity Verlet: x(t+dt) = x(t) + v(t)*dt + 0.5*a(t)*dt^2; SAT: Overlap on all axes = Collision.",
                     "worked_example": "In broad-phase AABB collision check: Box A [0, 2] and Box B [3, 5] on X-axis do not overlap; pair is discarded without narrow-phase mesh testing.",
                     "content": "Game engine architecture separates rendering from physics. ECS architectures maximize CPU cache locality, while spatial partitioning trees accelerate culling and narrow-phase collision solvers."}
                ]
            )
        ]
    })

    # 119. Human-Computer Interaction (HCI)
    subs.append({
        "id": "SUB_HCI",
        "subject_id": 119,
        "name": "Human-Computer Interaction (HCI)",
        "slug": "human-computer-interaction",
        "icon_symbol": "🖱️",
        "color_hex": "#F97316",
        "category": "Engineering",
        "description": "User cognition, mental models, Norman's Action Cycle, heuristic evaluation, Fitts's Law, and accessibility engineering.",
        "is_added": False,
        "chapters": [
            make_ch(
                1191, 1, "Cognitive Models, Mental Models & Norman's Action Cycle", 20,
                "Human sensory processing, short-term working memory, mental models, affordances, and signifiers.",
                ["Human information processing: Model Human Processor (Card, Moran, Newell)", "Miller's Law (7 ± 2 chunks) and cognitive load theory", "Norman's Seven Stages of Action: Gulf of Execution and Gulf of Evaluation", "Affordances, signifiers, mappings, feedback, and conceptual models"],
                ["Miller's Law: Working Memory Capacity = 7 ± 2 items", "Gulf of Execution = Goal - System Input Interface", "Gulf of Evaluation = System Display State - User Mental Model Interpretation"],
                "Cognitive Psychology & The Model Human Processor",
                "Human-Computer Interaction bridges human cognitive psychology and digital interfaces. The Model Human Processor models users through three interacting subsystems: perceptual (sensory vision/audio buffers), motor (muscle movement initiation), and cognitive (short-term working memory and associative long-term memory). Cognitive load theory dictates that interface clutter exhausts working memory capacity, which Miller's Law identifies as roughly 7 ± 2 items.",
                "Norman's Seven Stages of Action & Interaction Design",
                "Don Norman's action model partitions user behavior into a cycle: Goal formation, Execution (Plan, Specify, Perform), and Evaluation (Perceive, Interpret, Compare). Interaction breakdowns manifest across two chasms: the Gulf of Execution (the difficulty of figuring out how to interact with an interface) and the Gulf of Evaluation (the difficulty of assessing whether the intended goal was achieved). Effective designs employ explicit signifiers and rapid sensory feedback.",
                "An affordance is what a physical or digital object CAN do; a signifier is a visible perceivable indicator that tells the user WHERE to act.",
                [
                    {"id": 11911, "question_number": 1, "question": "In interaction design according to Don Norman, what is the precise distinction between an affordance and a signifier?",
                     "options": {"A": "An affordance is the actual possible action relationship between agent and object; a signifier is any perceivable cue that communicates where and how the action should be performed", "B": "Affordances only exist in software; signifiers only exist in hardware", "C": "They are identical synonyms", "D": "Signifiers are hardware buttons only"},
                     "correct_option": "A", "explanation": "Affordances define possible interactions (e.g., a touch screen can be tapped); signifiers communicate where to touch (e.g., a button border with drop shadow)."},
                    {"id": 11912, "question_number": 2, "question": "What is the 'Gulf of Execution' in Norman's action cycle?",
                     "options": {"A": "The difference between user intentions and the allowable actions permitted by the physical/software interface", "B": "The delay when downloading a file", "C": "A hardware CPU failure", "D": "Screen flicker"},
                     "correct_option": "A", "explanation": "The Gulf of Execution measures how easily a user can translate their internal mental goals into explicit interface actions."},
                    {"id": 11913, "question_number": 3, "question": "According to Miller's Law, what is the typical capacity limit of human short-term working memory?",
                     "options": {"A": "7 ± 2 chunks of information", "B": "100 items", "C": "1 item only", "D": "Unlimited storage"},
                     "correct_option": "A", "explanation": "George Miller established that human working memory can hold approximately 7 ± 2 discrete information chunks simultaneously."},
                    {"id": 11914, "question_number": 4, "question": "What interaction principle is violated when a user clicks a button and the application provides no visual, haptic, or auditory response for 5 seconds?",
                     "options": {"A": "Feedback", "B": "Fitts's Law", "C": "Color contrast", "D": "Screen resolution"},
                     "correct_option": "A", "explanation": "Feedback communicates the immediate results of an action; lacking feedback leaves users stranded in the Gulf of Evaluation."},
                    {"id": 11915, "question_number": 5, "question": "What is a 'mental model' in cognitive engineering?",
                     "options": {"A": "An internal psychological explanation of someone's thought process about how something works in the real world", "B": "A 3D CAD model", "C": "A database schema", "D": "An AI neural network"},
                     "correct_option": "A", "explanation": "A mental model is the internal conceptual framework a user builds based on past experience to predict and interpret system behavior."}
                ],
                [
                    {"chunk_id": "chunk_hci_01", "subject": "Human-Computer Interaction (HCI)",
                     "chapter": "Cognitive Models, Mental Models & Norman's Action Cycle", "topic": "Cognitive Models and Norman's Action Cycle",
                     "keywords": ["hci", "interaction design", "mental model", "affordance", "signifier", "gulf of execution", "norman action cycle"],
                     "definition": "HCI cognitive modeling studies how human memory, perception, and conceptual models interact with computational interface affordances.",
                     "formula_or_core_rule": "Miller's Law: 7 ± 2 chunks; Gulf of Execution = User Goals vs System Action Controls.",
                     "worked_example": "A door with a flat brass plate provides a physical affordance for pushing; the plate itself acts as a clear signifier.",
                     "content": "Interaction design minimizes the Gulfs of Execution and Evaluation by aligning system conceptual models with user mental models through discoverable signifiers, immediate feedback, and cognitive mapping."}
                ]
            ),
            make_ch(
                1192, 2, "Heuristic Evaluation, Usability Testing & Accessibility (WCAG)", 20,
                "Nielsen's 10 Usability Heuristics, qualitative user testing, Fitts's Law, and WCAG accessibility standards.",
                ["Jakob Nielsen's 10 Usability Heuristics for user interface evaluation", "Fitts's Law: movement time as a function of target distance and width", "Hick-Hyman Law: decision time as a function of choice alternatives", "Web Content Accessibility Guidelines (WCAG): Perceivable, Operable, Understandable, Robust"],
                ["Fitts's Law: MT = a + b * log2(2 * D / W)", "Hick's Law: RT = b * log2(n + 1)", "WCAG AA Contrast Ratio = 4.5:1 (Normal Text), 3:1 (Large Text)"],
                "Nielsen's Usability Heuristics & Interface Auditing",
                "Usability engineering evaluates user interfaces through empirical inspection methods. Jakob Nielsen's 10 Usability Heuristics provide standardized benchmarks, including: Visibility of system status, Match between system and real world, User control and freedom (undo/redo), Consistency and standards, Error prevention, and Recognition rather than recall. Expert evaluators identify usability defects before user deployment.",
                "Motor Performance Laws & Accessibility Engineering",
                "Fitts's Law models motor targeting time as MT = a + b * log2(2D / W), proving that buttons are easiest to acquire when they are large (large width W) and close to the pointer (small distance D), making screen edges and corners infinitely fast 'pinned' targets. Web Content Accessibility Guidelines (WCAG 2.1) enforce inclusion across four pillars (POUR): Perceivable, Operable, Understandable, and Robust, mandating minimum contrast ratios and screen-reader accessibility.",
                "Fitts's Law proves that screen corners have infinite effective width because the cursor cannot overshoot the physical monitor edge.",
                [
                    {"id": 11921, "question_number": 1, "question": "According to Fitts's Law MT = a + b * log2(2D / W), how can interface designers minimize the movement time required to click an interactive button?",
                     "options": {"A": "Increase button width (W) and decrease distance to pointer (D)", "B": "Make buttons as tiny as possible", "C": "Move buttons to random screen locations", "D": "Change button color to red"},
                     "correct_option": "A", "explanation": "Movement time decreases when the target is wider (larger W) and located closer to the starting pointer position (smaller D)."},
                    {"id": 11922, "question_number": 2, "question": "What is the minimum WCAG 2.1 Level AA color contrast ratio required between normal body text and its background?",
                     "options": {"A": "1.5:1", "B": "3:1", "C": "4.5:1", "D": "7:1"},
                     "correct_option": "C", "explanation": "WCAG 2.1 AA mandates a minimum contrast ratio of 4.5:1 for standard text (and 3:1 for large text >= 18pt or bold 14pt)."},
                    {"id": 11923, "question_number": 3, "question": "Which Nielsen Usability Heuristic is upheld when an application provides a clear 'Undo' action after accidentally deleting a file?",
                     "options": {"A": "User control and freedom", "B": "Aesthetic and minimalist design", "C": "Help and documentation", "D": "Flexibility and efficiency of use"},
                     "correct_option": "A", "explanation": "'User control and freedom' ensures users have clear emergency exits to reverse unintended actions without entering extended dialogue flows."},
                    {"id": 11924, "question_number": 4, "question": "What does the Hick-Hyman Law dictate regarding human decision latency?",
                     "options": {"A": "Decision time increases logarithmically with the number of available choice alternatives (RT = b * log2(n + 1))", "B": "Decision time is independent of menu size", "C": "Users always choose the first item", "D": "Dropdowns take 10 seconds"},
                     "correct_option": "A", "explanation": "Hick's Law shows that decision time scales logarithmically with the number of choices; reducing options drastically speeds user decision-making."},
                    {"id": 11925, "question_number": 5, "question": "What is the primary objective of a 'Think-Aloud Protocol' during usability testing?",
                     "options": {"A": "To test the room microphone volume", "B": "To capture users' real-time mental thoughts, expectations, and cognitive confusions while performing tasks", "C": "To force users to read terms of service aloud", "D": "To increase typing speed"},
                     "correct_option": "B", "explanation": "In think-aloud protocols, participants vocalize their thought stream continuously, illuminating internal mental models and pinpointing interface confusions."}
                ],
                [
                    {"chunk_id": "chunk_hci_02", "subject": "Human-Computer Interaction (HCI)",
                     "chapter": "Heuristic Evaluation, Usability Testing & Accessibility (WCAG)", "topic": "Usability Heuristics and Quantitative Laws",
                     "keywords": ["usability", "nielsen heuristics", "fitts law", "hick law", "wcag", "accessibility", "usability testing"],
                     "definition": "Usability engineering encompasses inspection methods, empirical testing, and mathematical laws that evaluate how easily and effectively users can interact with software.",
                     "formula_or_core_rule": "Fitts's Law: MT = a + b * log2(2D / W); Hick's Law: RT = b * log2(n + 1).",
                     "worked_example": "A mobile primary action floating button placed near the thumb (low D) with a large 56dp diameter (high W) optimizes Fitts's acquisition index.",
                     "content": "Usability evaluation combines Nielsen's 10 heuristic benchmarks with quantitative interaction laws (Fitts's and Hick's laws) and WCAG accessibility standards to build equitable, low-friction digital interfaces."}
                ]
            ),
            make_ch(
                1193, 3, "Interaction Design Paradigms, Prototyping & Gesture Interfaces", 20,
                "Low-fidelity vs high-fidelity prototypes, touch gestures, multi-modal interaction, AR/VR spatial UI, and voice interaction.",
                ["Fidelity spectrum: Paper prototypes, wireframes, and interactive components", "Direct manipulation interfaces and tangible interaction", "Touchscreen gesture mechanics: tap, swipe, pinch-to-zoom kinematics", "Spatial computing: AR/VR 6-DOF tracking, gaze-pinch interaction, and haptics"],
                ["Velocity Threshold = Delta_Position / Delta_Time", "Kinematic Fling: Deceleration = a_friction * dt"],
                "Direct Manipulation & Interaction Paradigms",
                "Ben Shneiderman established the principles of Direct Manipulation: continuous visual representation of objects of interest, physical actions instead of complex syntax, and rapid incremental reversible operations with immediate impact. From command-line interfaces (CLI) to graphical user interfaces (GUI) to natural user interfaces (NUI), design paradigms increasingly align digital controls with physical human sensorimotor affordances.",
                "Spatial Computing & Multi-Modal Interfaces",
                "Modern interfaces extend beyond 2D rectangular displays into 3D Spatial Computing (Augmented and Virtual Reality). Six Degrees of Freedom (6-DOF) tracking tracks rotational (pitch, yaw, roll) and translational (x, y, z) posture. Contemporary spatial operating systems combine eye-gaze tracking (for targeting) with micro-finger pinch gestures (for confirmation), minimizing physical arm fatigue.",
                "Gorilla Arm Syndrome occurs when users must hold their arms outstretched horizontally for extended periods; spatial UIs must place primary interactions near resting hand positions.",
                [
                    {"id": 11931, "question_number": 1, "question": "What is 'Gorilla Arm Syndrome' in gesture and spatial interaction design?",
                     "options": {"A": "A computer virus", "B": "Physical muscle fatigue and strain caused by holding one's arm outstretched horizontally for prolonged interaction with vertical touchscreens or mid-air gestures", "C": "Wearing oversized gloves", "D": "Heavy VR headset weight"},
                     "correct_option": "B", "explanation": "Gorilla arm syndrome describes the rapid fatigue and shoulder ache resulting from keeping arms elevated without support; spatial UIs combat this by supporting micro-gestures resting in the lap."},
                    {"id": 11932, "question_number": 2, "question": "What is the primary advantage of paper prototyping in early design phases?",
                     "options": {"A": "High-fidelity rendering of 3D animations", "B": "Rapid, low-cost exploration of concepts allowing designers to discard flaws before writing code", "C": "Testing battery consumption", "D": "Automated code generation"},
                     "correct_option": "B", "explanation": "Low-fidelity paper prototypes cost almost nothing, allowing rapid iteration, concept validation, and unbiased user feedback before investing in software implementation."},
                    {"id": 11933, "question_number": 3, "question": "What does '6-DOF' represent in spatial VR/AR tracking systems?",
                     "options": {"A": "Six Degrees of Freedom: 3 translational axes (surge, heave, sway) and 3 rotational axes (pitch, yaw, roll)", "B": "Six color channels", "C": "Six CPU cores", "D": "Six display monitors"},
                     "correct_option": "A", "explanation": "6-DOF tracks full spatial movement in 3D: three translational positions (x, y, z) and three rotational orientations (pitch, yaw, roll)."},
                    {"id": 11934, "question_number": 4, "question": "What interaction technique combines eye gaze tracking with micro-pinch finger gestures in spatial headsets?",
                     "options": {"A": "Gaze-and-Pinch interaction (eyes select target, subtle thumb-index tap confirms)", "B": "Typing on a mechanical keyboard", "C": "Voice shouting", "D": "Mouse scrolling"},
                     "correct_option": "A", "explanation": "Gaze-and-pinch leverages the human visual system for lightning-fast target acquisition, coupled with effortless finger pinch for selection without arm elevation."},
                    {"id": 11935, "question_number": 5, "question": "Which principle of Direct Manipulation allows users to explore interface options fearlessly?",
                     "options": {"A": "Reversible operations with immediate feedback", "B": "Permanent destructive updates", "C": "Cryptographic passwords", "D": "Command-line syntax"},
                     "correct_option": "A", "explanation": "Reversible actions (such as Undo) let users experiment without anxiety, knowing any misstep can be undone immediately."}
                ],
                [
                    {"chunk_id": "chunk_hci_03", "subject": "Human-Computer Interaction (HCI)",
                     "chapter": "Interaction Design Paradigms, Prototyping & Gesture Interfaces", "topic": "Direct Manipulation and Spatial Computing",
                     "keywords": ["interaction design", "direct manipulation", "prototyping", "gesture interface", "spatial computing", "6-dof", "gaze pinch"],
                     "definition": "Interaction design paradigms define the conceptual models and physical mechanics through which humans communicate with and control digital computing environments.",
                     "formula_or_core_rule": "6-DOF = 3 Translations (x, y, z) + 3 Rotations (pitch, yaw, roll).",
                     "worked_example": "Apple Vision Pro uses eye tracking to highlight buttons and index-thumb pinch gestures with hands resting on the lap to trigger selection.",
                     "content": "Interaction design spans low-fidelity wireframing to immersive spatial computing. Direct manipulation interfaces provide immediate visual feedback, while multi-modal spatial systems combine eye-tracking and micro-gestures to bypass physical fatigue."}
                ]
            )
        ]
    })

    # 120. Embedded Systems & IoT
    subs.append({
        "id": "SUB_IOT",
        "subject_id": 120,
        "name": "Embedded Systems & IoT",
        "slug": "embedded-systems-iot",
        "icon_symbol": "📡",
        "color_hex": "#14B8A6",
        "category": "Engineering",
        "description": "Microcontroller architecture, bare-metal C, serial protocols (UART, SPI, I2C), FreeRTOS scheduling, and low-power IoT protocols.",
        "is_added": False,
        "chapters": [
            make_ch(
                1201, 1, "Microcontroller Architecture, Interrupts & Memory-Mapped I/O", 20,
                "ARM Cortex-M core, register memory mapping, Interrupt Service Routines (ISRs), and Direct Memory Access (DMA).",
                ["Microprocessor vs Microcontroller (MCU) architecture", "ARM Cortex-M register banks, NVIC, and vector table", "Memory-mapped I/O (MMIO) and volatile register access in C", "Interrupt latency, nested interrupts, and Direct Memory Access (DMA)"],
                ["UART Baud Rate divisor: BRD = F_clk / (16 * Baud)", "DMA Throughput: Bandwidth = Clock * Bus_Width / Cycles_per_burst"],
                "Microcontroller Architecture & Memory-Mapped I/O",
                "Unlike general-purpose microprocessors, microcontrollers (MCUs) integrate CPU cores, flash memory, SRAM, and peripheral interfaces onto a single monolithic die. In the ARM Cortex-M architecture, peripherals (GPIO, Timers, ADCs) are accessed via Memory-Mapped I/O (MMIO), where hardware control registers occupy designated physical addresses in the 32-bit address space. In C firmware, peripheral registers must be qualified with the 'volatile' keyword to prevent compiler optimizations from caching reads.",
                "Nested Vector Interrupt Controller & DMA",
                "The Nested Vectored Interrupt Controller (NVIC) prioritizes asynchronous hardware events. When an interrupt line triggers, the hardware automatically stacks processor registers (R0-R3, R12, LR, PC, xPSR) onto the stack pointer and fetches the Interrupt Service Routine (ISR) address from the flash vector table. Direct Memory Access (DMA) offloads bulk data transfers between peripherals and memory without CPU intervention, preserving processing cycles.",
                "Always qualify hardware register pointers with 'volatile' in embedded C to prevent the compiler from caching reads in CPU registers.",
                [
                    {"id": 12011, "question_number": 1, "question": "Why MUST hardware peripheral register pointers be declared with the 'volatile' keyword in embedded C?",
                     "options": {"A": "To allocate memory in flash instead of RAM", "B": "To instruct the compiler that the register value can change outside program control (by hardware), preventing the optimizer from caching reads in CPU registers", "C": "To encrypt the register", "D": "To increase SPI speed"},
                     "correct_option": "B", "explanation": "Hardware registers update asynchronously via external signals; 'volatile' prevents the compiler from optimizing away reads or caching values in internal registers."},
                    {"id": 12012, "question_number": 2, "question": "What is the primary role of Direct Memory Access (DMA) in a microcontroller?",
                     "options": {"A": "To transfer data blocks directly between memory and peripherals without continuous CPU intervention", "B": "To execute assembly instructions faster", "C": "To regulate battery charging", "D": "To generate analog PWM signals"},
                     "correct_option": "A", "explanation": "DMA offloads high-speed data movement (e.g. ADC samples to RAM or SPI buffers) from the CPU, allowing the processor to execute code or enter low-power sleep modes concurrently."},
                    {"id": 12013, "question_number": 3, "question": "In ARM Cortex-M microcontrollers, what is automatically saved onto the stack when an interrupt triggers?",
                     "options": {"A": "Entire flash memory", "B": "Hardware register state frame (R0-R3, R12, LR, PC, xPSR)", "C": "Nothing is saved", "D": "Global heap variables"},
                     "correct_option": "B", "explanation": "The Cortex-M hardware automatically pushes a standard 8-word stack frame (R0-R3, R12, LR, PC, xPSR) onto the stack within 12 clock cycles, enabling rapid context switching."},
                    {"id": 12014, "question_number": 4, "question": "What is 'interrupt latency' in an embedded real-time system?",
                     "options": {"A": "The elapsed time between the assertion of a hardware interrupt request and the execution of the first instruction of its ISR", "B": "The duration of the ISR", "C": "The serial baud rate", "D": "The battery discharge rate"},
                     "correct_option": "A", "explanation": "Interrupt latency measures the delay from the physical hardware interrupt trigger until the CPU begins executing the corresponding handler code."},
                    {"id": 12015, "question_number": 5, "question": "Why should Interrupt Service Routines (ISRs) be designed to be as short and fast as possible?",
                     "options": {"A": "Long ISRs block other lower or equal priority interrupts and degrade real-time system responsiveness", "B": "Flash memory will run out", "C": "Clock frequency drops during ISRs", "D": "ISRs cannot execute loops"},
                     "correct_option": "A", "explanation": "Keeping ISRs concise prevents starving other time-critical interrupts; heavy processing should be deferred to background tasks or RTOS threads."}
                ],
                [
                    {"chunk_id": "chunk_iot_01", "subject": "Embedded Systems & IoT",
                     "chapter": "Microcontroller Architecture, Interrupts & Memory-Mapped I/O", "topic": "Microcontroller Architecture and Interrupts",
                     "keywords": ["embedded systems", "microcontroller", "arm cortex-m", "interrupt", "isr", "nvic", "dma", "volatile"],
                     "definition": "A microcontroller is a compact integrated circuit containing a processor core, memory, and programmable input/output peripherals designed to govern dedicated embedded applications.",
                     "formula_or_core_rule": "UART Baud Divisor = F_clk / (16 * BaudRate); Volatile keyword forces reload from memory.",
                     "worked_example": "For a 16 MHz clock and target baud rate of 9600: Divisor = 16,000,000 / (16 * 9600) = 104.166.",
                     "content": "Microcontrollers interact with external hardware via memory-mapped registers. Hardware interrupts managed by the NVIC provide deterministic real-time response, while DMA offloads bulk data streaming without CPU intervention."}
                ]
            ),
            make_ch(
                1202, 2, "Hardware Communication Protocols: UART, SPI & I2C", 20,
                "Asynchronous serial (UART), synchronous Master-Slave SPI, multi-drop I2C bus arbitration, and clock stretching.",
                ["UART: framing, start/stop bits, parity, and baud rate timing", "SPI: Master-Out-Slave-In (MOSI), MISO, SCK, Chip Select, CPOL, CPHA", "I2C: Two-wire SDA/SCL, open-drain pull-up topology, 7-bit addressing", "I2C clock stretching, ACK/NACK signaling, and bus contention arbitration"],
                ["I2C Pull-Up Resistor R_min = (Vdd - Vol) / Iol", "SPI Clock Frequency = F_bus / Prescaler", "UART Efficiency = Data_bits / Total_frame_bits (e.g. 8/10 = 80%)"],
                "UART Asynchronous & SPI Synchronous Protocols",
                "Embedded systems interconnect sensors, memory chips, and wireless radios using standardized serial communication buses. Universal Asynchronous Receiver-Transmitter (UART) requires no shared clock line, relying on pre-configured baud rates, start bits, and stop bits. Serial Peripheral Interface (SPI) is a synchronous, full-duplex four-wire bus (MOSI, MISO, SCK, CS) supporting multi-megahertz data throughput configured via Clock Polarity (CPOL) and Clock Phase (CPHA).",
                "I2C Two-Wire Multi-Master Architecture",
                "Inter-Integrated Circuit (I2C) is a synchronous, half-duplex two-wire bus utilizing Serial Data (SDA) and Serial Clock (SCL). Lines operate in an open-drain (open-collector) configuration with external pull-up resistors, enabling multiple master and slave nodes on a shared bus. Address frames (7 or 10 bits) precede transfers, and ACK/NACK bits confirm bytes. Wired-AND arbitration resolves bus collisions without data corruption.",
                "In I2C, open-drain lines mean any device pulling low forces the line to 0V, enabling harmless multi-master collision arbitration.",
                [
                    {"id": 12021, "question_number": 1, "question": "Why do I2C bus lines (SDA and SCL) require external pull-up resistors?",
                     "options": {"A": "To convert AC into DC", "B": "Because devices use open-drain outputs that can pull lines LOW to ground but rely on pull-ups to pull lines HIGH", "C": "To prevent radio emissions", "D": "To step down voltage to 1.8V"},
                     "correct_option": "B", "explanation": "I2C uses open-drain outputs; devices can only sink current to ground (0V). External pull-up resistors pull lines high to Vdd when all drivers are high-impedance."},
                    {"id": 12022, "question_number": 2, "question": "How many signal wires are required for a standard full-duplex SPI bus communicating with a single slave device?",
                     "options": {"A": "2 wires", "B": "3 wires", "C": "4 wires (MOSI, MISO, SCK, CS/SS)", "D": "8 wires"},
                     "correct_option": "C", "explanation": "Full-duplex SPI requires 4 lines: Master Out Slave In (MOSI), Master In Slave Out (MISO), Serial Clock (SCK), and Chip Select (CS)."},
                    {"id": 12023, "question_number": 3, "question": "What is 'clock stretching' in the I2C protocol?",
                     "options": {"A": "Overclocking the bus to 10 MHz", "B": "A slave device holding the SCL line LOW to pause the master until it has processed current data", "C": "A crystal oscillator failure", "D": "Increasing baud rate"},
                     "correct_option": "B", "explanation": "In I2C, a slower slave can hold SCL low after receiving a byte, signaling to the master that it is busy and pausing transmission until SCL is released."},
                    {"id": 12024, "question_number": 4, "question": "In UART transmission with configuration '8-N-1' (8 data bits, no parity, 1 stop bit), what percentage of transmitted bits carries actual data payload?",
                     "options": {"A": "80% (8 data bits out of 10 total frame bits)", "B": "100%", "C": "50%", "D": "64%"},
                     "correct_option": "A", "explanation": "A complete frame contains 1 start bit + 8 data bits + 1 stop bit = 10 bits total. Payload efficiency = 8 / 10 = 80%."},
                    {"id": 12025, "question_number": 5, "question": "What do the CPOL and CPHA configuration settings control in SPI communication?",
                     "options": {"A": "Clock Polarity (idle clock state) and Clock Phase (sampling clock edge)", "B": "Cable length and thickness", "C": "Memory address offsets", "D": "Parity checking"},
                     "correct_option": "A", "explanation": "CPOL defines whether SCK idles low (0) or high (1); CPHA defines whether data is captured on the leading edge (0) or trailing edge (1)."}
                ],
                [
                    {"chunk_id": "chunk_iot_02", "subject": "Embedded Systems & IoT",
                     "chapter": "Hardware Communication Protocols: UART, SPI & I2C", "topic": "Serial Protocols: UART, SPI, and I2C",
                     "keywords": ["uart", "spi", "i2c", "serial protocols", "baud rate", "open-drain", "clock stretching", "mosi miso"],
                     "definition": "Serial hardware protocols define the electrical levels and timing rules used to transmit sequential data streams between integrated circuits.",
                     "formula_or_core_rule": "I2C uses 2 open-drain lines (SDA/SCL); SPI uses 4 push-pull lines (MOSI/MISO/SCK/CS).",
                     "worked_example": "At 115,200 baud with 10-bit frames, maximum transmission speed = 115,200 / 10 = 11,520 bytes per second.",
                     "content": "UART provides simple point-to-point asynchronous connectivity. SPI delivers fast synchronous full-duplex throughput for displays and flash memory, while I2C enables multi-device sensor networking on two shared wires."}
                ]
            ),
            make_ch(
                1203, 3, "RTOS Task Scheduling, IoT Networking & Power Management", 20,
                "FreeRTOS preemption, priority inversion, priority inheritance, MQTT, CoAP, and deep-sleep energy budgeting.",
                ["Real-Time Operating Systems (RTOS): Preemptive priority-based scheduling", "Task synchronization: Semaphores, mutexes, message queues, and context switching", "Priority Inversion and Priority Inheritance protocol", "IoT wireless networking: MQTT (TCP pub/sub), CoAP (UDP), BLE, and sleep modes"],
                ["Energy Consumed E = Vdd * (I_active * t_active + I_sleep * t_sleep)", "Battery Life Hours = Battery_Capacity_mAh / Average_Current_mA"],
                "Real-Time Task Scheduling & Priority Inversion",
                "Real-Time Operating Systems (RTOS), such as FreeRTOS, govern microcontrollers requiring hard timing guarantees. Preemptive schedulers allocate CPU execution to the highest-priority ready task via periodic SysTick timer ticks. When a low-priority task holds a shared mutex needed by a high-priority task while a medium task preempts the low-priority task, unbounded Priority Inversion occurs. RTOS mutexes implement Priority Inheritance to boost the low-priority task's priority dynamically until the resource is freed.",
                "IoT Protocols & Low-Power Sleep Modes",
                "Battery-powered IoT edge nodes spend >99% of time in microamp deep-sleep modes, waking up periodically via hardware Real-Time Clocks (RTC) to read sensors and transmit packets. Lightweight communication protocols include MQTT (publish/subscribe over TCP with QoS 0, 1, 2) and CoAP (RESTful request/response over UDP).",
                "Always use Mutexes with Priority Inheritance (not binary semaphores) when protecting shared resources between different priority tasks in an RTOS.",
                [
                    {"id": 12031, "question_number": 1, "question": "What is 'Priority Inversion' in a preemptive Real-Time Operating System (RTOS)?",
                     "options": {"A": "A medium-priority task runs while a high-priority task is blocked waiting for a mutex held by a low-priority task", "B": "Tasks run in alphabetical order", "C": "CPU voltage inverts", "D": "High-priority tasks are deleted"},
                     "correct_option": "A", "explanation": "Priority inversion occurs when a low-priority task holds a lock needed by a high-priority task, and intermediate-priority tasks preempt the low task, indefinitely stalling the high task."},
                    {"id": 12032, "question_number": 2, "question": "How does the Priority Inheritance protocol resolve priority inversion?",
                     "options": {"A": "It temporarily elevates the priority of the task holding the mutex to match the priority of the highest-priority task waiting for it", "B": "It reboots the operating system", "C": "It deletes the low-priority task", "D": "It disables all interrupts"},
                     "correct_option": "A", "explanation": "Priority inheritance boosts the lock holder's priority to that of the highest blocked task, preventing intermediate tasks from preempting it until the lock is released."},
                    {"id": 12033, "question_number": 3, "question": "What messaging pattern is utilized by MQTT (Message Queuing Telemetry Transport) in IoT systems?",
                     "options": {"A": "Publish/Subscribe mediated by a central Broker", "B": "Peer-to-peer torrent sharing", "C": "Point-to-point analog wiring", "D": "Direct SQL queries"},
                     "correct_option": "A", "explanation": "MQTT decouples clients through a central broker; publishers publish data to topics, and subscribers receive updates matching subscribed topics."},
                    {"id": 12034, "question_number": 4, "question": "If an IoT sensor node consumes 20 mA for 1 second during transmission and 10 uA (0.01 mA) for 59 seconds in deep sleep, what is its average current consumption?",
                     "options": {"A": "Approx 0.343 mA", "B": "10 mA", "C": "20 mA", "D": "0.01 mA"},
                     "correct_option": "A", "explanation": "Average current = (20 mA * 1s + 0.01 mA * 59s) / 60s = (20 + 0.59) / 60 ≈ 0.343 mA."},
                    {"id": 12035, "question_number": 5, "question": "What transport layer protocol is utilized by CoAP (Constrained Application Protocol)?",
                     "options": {"A": "UDP", "B": "TCP", "C": "HTTP/2", "D": "FTP"},
                     "correct_option": "A", "explanation": "CoAP is designed for constrained IoT nodes and operates over lightweight UDP to minimize connection handshake overhead and packet size."}
                ],
                [
                    {"chunk_id": "chunk_iot_03", "subject": "Embedded Systems & IoT",
                     "chapter": "RTOS Task Scheduling, IoT Networking & Power Management", "topic": "RTOS Scheduling and IoT Protocols",
                     "keywords": ["rtos", "freertos", "priority inversion", "priority inheritance", "mqtt", "coap", "deep sleep", "iot"],
                     "definition": "An RTOS provides deterministic, preemptive scheduling for embedded systems, while IoT protocols optimize data transmission over constrained networks.",
                     "formula_or_core_rule": "Energy = Vdd * (I_act*t_act + I_sleep*t_sleep); Priority Inheritance boosts lock holder.",
                     "worked_example": "A 1000 mAh battery running at an average current of 0.2 mA provides 1000 / 0.2 = 5000 operating hours (~208 days).",
                     "content": "Real-time embedded systems combine RTOS multitasking with energy budgeting. Preemptive priority inheritance prevents thread starvation, while lightweight MQTT and CoAP protocols transmit telemetry under strict battery limits."}
                ]
            )
        ]
    })

    return subs

