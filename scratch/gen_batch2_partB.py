# scratch/gen_batch2_partB.py
# Subjects 16 to 20: Materials Science, Genetics, Anatomy, Pharmacology, Neuroscience

import sys
sys.path.append('scratch')
from gen_subjects_1_to_10 import make_ch

def get_subjects_16_to_20():
    subs = []

    # 126. Materials Science & Nanotechnology
    subs.append({
        "id": "SUB_NANOTECH",
        "subject_id": 126,
        "name": "Materials Science & Nanotechnology",
        "slug": "materials-science-nanotechnology",
        "icon_symbol": "🔬",
        "color_hex": "#0D9488",
        "category": "Engineering",
        "description": "Crystalline structures, Bragg's Law, quantum confinement, carbon nanotubes, graphene, and nanomaterial synthesis.",
        "is_added": False,
        "chapters": [
            make_ch(
                1261, 1, "Crystallography & Phase Diagrams", 20,
                "Unit cells, Bravais lattices, Miller indices, X-ray diffraction, and binary isomorphous phase equilibria.",
                ["Unit cells: FCC, BCC, and HCP packing factors", "Miller indices (hkl) for planes and directions", "Bragg's Law for X-ray diffraction: n * lambda = 2 * d * sin(theta)", "Binary phase diagrams: Lever rule and eutectic transitions"],
                ["Bragg's Law: n * lambda = 2 * d * sin(theta)", "Interplanar Spacing (Cubic): d_hkl = a / sqrt(h^2 + k^2 + l^2)", "Atomic Packing Factor (FCC): APF = 0.74", "Lever Rule: W_alpha = (C_0 - C_L) / (C_alpha - C_L)"],
                "Crystal Lattices & X-Ray Diffraction",
                "Materials derive mechanical and electronic properties from atomic arrangements. The 14 Bravais lattices categorize 3D periodic structures. In FCC (Face-Centered Cubic) crystals (e.g., Cu, Al, Au), atoms touch along face diagonals, yielding an atomic packing factor of 0.74 and coordination number 12. Crystallographic planes are indexed by Miller notation (hkl). When monochromatic X-rays strike a crystal, coherent scattering yields constructive interference according to Bragg's law: n * lambda = 2 * d * sin(theta). Measuring diffraction angles theta reveals interplanar spacings d_hkl and precise lattice constants a.",
                "Binary Phase Diagrams & Solidification",
                "Phase diagrams map thermodynamic equilibrium states as functions of temperature and composition. In binary isomorphous systems (e.g., Cu-Ni), complete liquid and solid solubility exists. In eutectic systems (e.g., Pb-Sn), a liquid of eutectic composition transforms cooperatively into two distinct solid phases (alpha + beta) at an invariant eutectic temperature. The lever rule computes phase fractions across tie-lines, governing precipitation hardening and microstructure development in structural alloys.",
                "When calculating Bragg diffraction, remember that theta is the glancing angle relative to the crystal plane, not the normal angle.",
                [
                    {
                        "id": 12611, "question_number": 1,
                        "question": "What is the atomic packing factor (APF) of an ideal Face-Centered Cubic (FCC) crystal structure?",
                        "options": {"A": "0.52", "B": "0.68", "C": "0.74", "D": "0.91"},
                        "correct_option": "C",
                        "explanation": "FCC crystals have an APF of 0.74, representing close-packed spherical atoms with coordination number 12."
                    },
                    {
                        "id": 12612, "question_number": 2,
                        "question": "In Bragg's Law (n * lambda = 2 * d * sin(theta)), what does the term 'd' represent?",
                        "options": {"A": "Diameter of the incident X-ray beam", "B": "Interplanar spacing between adjacent crystal lattice planes", "C": "Lattice parameter of the unit cell", "D": "Dislocation density inside the grain boundary"},
                        "correct_option": "B",
                        "explanation": "The term 'd' represents the interplanar spacing between parallel atomic planes (hkl) responsible for constructive interference."
                    },
                    {
                        "id": 12613, "question_number": 3,
                        "question": "Which rule is utilized on a tie-line within a two-phase region of a phase diagram to calculate phase fractions?",
                        "options": {"A": "Hume-Rothery Rule", "B": "Lever Rule", "C": "Gibbs Phase Rule", "D": "Fick's Second Law"},
                        "correct_option": "B",
                        "explanation": "The Lever Rule applies mass balance across a tie-line to calculate the relative weight fractions of two coexisting phases."
                    },
                    {
                        "id": 12614, "question_number": 4,
                        "question": "What invariant reaction characterizes a eutectic transformation?",
                        "options": {"A": "Liquid -> Solid alpha + Solid beta", "B": "Solid alpha -> Solid beta + Solid gamma", "C": "Liquid 1 + Solid alpha -> Solid beta", "D": "Solid alpha + Liquid -> Liquid 2"},
                        "correct_option": "A",
                        "explanation": "A eutectic reaction is an invariant transformation where a single liquid phase cools to form two distinct solid phases simultaneously."
                    },
                    {
                        "id": 12615, "question_number": 5,
                        "question": "For a cubic crystal with lattice parameter a = 0.4 nm, what is the interplanar spacing d for the (110) plane?",
                        "options": {"A": "0.400 nm", "B": "0.283 nm", "C": "0.200 nm", "D": "0.141 nm"},
                        "correct_option": "B",
                        "explanation": "d_110 = a / sqrt(1^2 + 1^2 + 0^2) = 0.4 / sqrt(2) = 0.4 / 1.414 = 0.283 nm."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1261",
                        "subject": "Materials Science & Nanotechnology",
                        "chapter": "Crystallography & Phase Diagrams",
                        "topic": "Bragg Diffraction and Phase Equilibria",
                        "keywords": ["crystallography", "Bravais lattice", "Miller indices", "Bragg law", "lever rule", "phase diagram"],
                        "definition": "Crystallography investigates the atomic arrangement in periodic crystalline solids and determines interplanar spacing via coherent X-ray diffraction.",
                        "formula_or_core_rule": "Bragg's Law: n * lambda = 2 * d * sin(theta); Lever Rule: W_alpha = (C_0 - C_L) / (C_alpha - C_L)",
                        "worked_example": "If Cu X-rays (lambda = 0.154 nm) diffract at theta = 30 degrees (n=1), d = lambda / (2 * sin(30)) = 0.154 / 1.0 = 0.154 nm.",
                        "content": "Crystallography establishes the geometric order of solids. Bravais lattices classify 14 standard structures, with FCC providing an APF of 0.74. Bragg's Law n*lambda = 2*d*sin(theta) determines interatomic lattice spacings from diffraction angles. Binary phase diagrams describe thermodynamic phase stability, where the lever rule quantifies equilibrium fractions of solid-liquid phases."
                    }
                ]
            ),
            make_ch(
                1262, 2, "Nanoscale Physics & Quantum Confinement", 25,
                "Surface-to-volume scaling, quantum dots, density of states, exciton Bohr radius, and carbon allotropes.",
                ["Surface area to volume ratio scaling at the nanoscale", "Quantum confinement effect: 1D (quantum well), 2D (wire), 3D (dot)", "Exciton Bohr radius and bandgap tunability", "Carbon allotropes: Graphene, SWCNTs, and Fullerenes"],
                ["Surface Area to Volume (Sphere): A / V = 3 / r", "Quantum Dot Bandgap: E_g(r) = E_g,bulk + (hbar^2 * pi^2) / (2 * m_eff * r^2)", "Graphene Dirac Dispersion: E(k) = hbar * v_F * |k|", "Carbon Nanotube Chiral Vector: C_h = n * a_1 + m * a_2"],
                "Quantum Confinement & Density of States",
                "When the physical dimensions of a semiconductor crystallite approach the exciton Bohr radius (typically 1 to 10 nm), charge carriers experience quantum spatial confinement. The continuous electronic band structure collapses into discrete, atom-like energy levels. The effective bandgap widens inversely with the square of the nanoparticle radius r: E_g(r) = E_g,bulk + hbar^2*pi^2 / (2*m_eff*r^2). Consequently, colloidal quantum dots (e.g., CdSe, PbS) absorb and emit light at wavelengths precisely tuned across the visible spectrum by varying particle diameter during chemical synthesis.",
                "Carbon Nanostructures: Graphene & Carbon Nanotubes",
                "Carbon exhibits remarkable electronic versatility via sp2 hybridization. Graphene consists of a single planar hexagonal honeycomb monolayer of carbon atoms. Its low-energy electrons behave as massless Dirac fermions with linear relativistic dispersion E(k) = hbar*v_F*k, reaching Fermi velocities v_F = 10^6 m/s and room-temperature carrier mobilities exceeding 200,000 cm^2/(V*s). Rolling graphene along a chiral vector C_h = n*a_1 + m*a_2 creates single-walled carbon nanotubes (SWCNTs), which are metallic if (n - m) mod 3 = 0, or semiconducting otherwise.",
                "In quantum dots, decreasing nanoparticle size increases the energy bandgap, causing a hypsochromic (blue) shift in emission wavelength.",
                [
                    {
                        "id": 12621, "question_number": 1,
                        "question": "What happens to the optical emission wavelength of a semiconductor quantum dot as its particle radius decreases?",
                        "options": {"A": "It shifts toward longer wavelengths (red shift)", "B": "It shifts toward shorter wavelengths (blue shift)", "C": "It remains unchanged because bandgap is an intrinsic bulk property", "D": "It stops emitting photons completely"},
                        "correct_option": "B",
                        "explanation": "Decreasing nanoparticle radius strengthens quantum confinement, increasing the effective bandgap and causing a blue shift to shorter wavelengths."
                    },
                    {
                        "id": 12622, "question_number": 2,
                        "question": "Under what condition is a single-walled carbon nanotube (SWCNT) with chiral indices (n, m) metallic?",
                        "options": {"A": "Only when n = m = 0", "B": "When (n - m) is an integer multiple of 3", "C": "When n + m is an odd number", "D": "All carbon nanotubes are strictly semiconducting"},
                        "correct_option": "B",
                        "explanation": "SWCNTs are metallic whenever (n - m) is divisible by 3 (including armchair tubes where n = m), due to band structure crossing at the Fermi level."
                    },
                    {
                        "id": 12623, "question_number": 3,
                        "question": "How does the surface area-to-volume ratio of a spherical nanoparticle scale with its radius r?",
                        "options": {"A": "Directly proportional to r", "B": "Directly proportional to r^2", "C": "Inversely proportional to r (proportional to 1/r)", "D": "Inversely proportional to r^3"},
                        "correct_option": "C",
                        "explanation": "For a sphere, Surface Area = 4*pi*r^2 and Volume = (4/3)*pi*r^3, so Area/Volume = 3/r, scaling inversely with radius."
                    },
                    {
                        "id": 12624, "question_number": 4,
                        "question": "What unique electronic behavior do charge carriers exhibit in monolayer graphene?",
                        "options": {"A": "Massless Dirac fermions with linear energy-momentum dispersion", "B": "Infinite effective mass and zero electrical conductivity", "C": "Cooper pair formation at room temperature", "D": "Strict one-dimensional quantum wire hopping"},
                        "correct_option": "A",
                        "explanation": "Electrons in graphene obey the 2D relativistic Dirac equation, demonstrating a linear dispersion E(k) = hbar * v_F * k with Fermi velocity ~10^6 m/s."
                    },
                    {
                        "id": 12625, "question_number": 5,
                        "question": "What dimension of quantum confinement corresponds to a quantum dot?",
                        "options": {"A": "1D confinement (quantum well)", "B": "2D confinement (quantum wire)", "C": "3D confinement (quantum dot)", "D": "0D confinement (bulk material)"},
                        "correct_option": "C",
                        "explanation": "A quantum dot confines charge carriers in all three spatial dimensions (3D quantum confinement), producing zero-dimensional discrete states."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1262",
                        "subject": "Materials Science & Nanotechnology",
                        "chapter": "Nanoscale Physics & Quantum Confinement",
                        "topic": "Quantum Confinement and Graphene",
                        "keywords": ["quantum dot", "quantum confinement", "exciton Bohr radius", "graphene", "carbon nanotube", "Dirac fermions"],
                        "definition": "Quantum confinement occurs when physical particle size restricts electronic wavefunctions, discretizing energy bands and expanding effective bandgap inversely with r^2.",
                        "formula_or_core_rule": "Quantum Dot Bandgap: E_g(r) = E_g,bulk + (hbar^2 * pi^2) / (2 * m_eff * r^2); Dirac dispersion: E(k) = hbar * v_F * k",
                        "worked_example": "Reducing CdSe crystal radius from 5 nm to 2 nm increases quantum kinetic confinement energy, blue-shifting photoluminescence from red to bright green.",
                        "content": "Nanoscale physics exhibits quantum confinement when particle dimensions fall below the exciton Bohr radius. Continuous bands quantize into discrete states, shifting emission spectra toward blue with decreasing radius. Graphene features sp2-bonded carbon in a honeycomb lattice with massless Dirac fermions, while rolled SWCNTs exhibit metallic properties when (n - m) mod 3 = 0."
                    }
                ]
            ),
            make_ch(
                1263, 3, "Synthesis, Characterization & Nanodevices", 25,
                "Top-down vs bottom-up fabrication, CVD, ALD, SEM, TEM, AFM, and nanoscale FETs.",
                ["Bottom-up chemical synthesis vs top-down lithography", "Chemical Vapor Deposition (CVD) and Atomic Layer Deposition (ALD)", "Electron microscopy: SEM (Scanning) and TEM (Transmission)", "Atomic Force Microscopy (AFM) and scanning tunneling microscopy (STM)"],
                ["De Broglie Wavelength: lambda = h / p = h / sqrt(2 * m * e * V)", "Rayleigh Criterion: theta = 1.22 * lambda / D", "AFM Hooke's Law Force: F = -k * delta_z", "ALD Growth Per Cycle: Thickness = N_cycles * GPC"],
                "Nanomaterial Fabrication: ALD & CVD",
                "Bottom-up nanotechnology relies on controlled molecular self-assembly and vapor-phase reactions. Atomic Layer Deposition (ALD) employs sequential, self-limiting chemical reactions between gaseous precursor pulses and surface functional groups. Because each half-reaction saturates before purge gases clear the chamber, ALD guarantees atomic-scale thickness control, pinhole-free layers, and 100% conformality over high-aspect-ratio 3D finFET and Gate-All-Around (GAA) architectures. Chemical Vapor Deposition (CVD) utilizes thermal catalysis to decompose hydrocarbon gases over copper catalysts to grow large-area graphene and carbon nanotubes.",
                "High-Resolution Characterization: TEM & AFM",
                "Because optical resolution is diffraction-limited by visible wavelengths (~500 nm), electron microscopy accelerates electrons through 20-300 kV potentials to achieve de Broglie wavelengths below 0.003 nm. Transmission Electron Microscopy (TEM) transmits electrons through ultrathin (<100 nm) specimens, producing sub-angstrom atomic lattice imaging. Atomic Force Microscopy (AFM) scans an atomically sharp tip on a flexible cantilever across the sample surface, measuring cantilever deflection via laser beam reflections to map 3D surface topography with sub-nanometer vertical resolution.",
                "Atomic Layer Deposition achieves conformal step coverage due to self-limiting surface saturation reactions, whereas physical vapor deposition (sputtering) suffers from line-of-sight shadowing.",
                [
                    {
                        "id": 12631, "question_number": 1,
                        "question": "What is the primary mechanism ensuring precise thickness control in Atomic Layer Deposition (ALD)?",
                        "options": {"A": "High-velocity kinetic particle bombardment", "B": "Self-limiting sequential surface chemical reactions", "C": "Thermal evaporation in high vacuum", "D": "Direct laser ablation of solid targets"},
                        "correct_option": "B",
                        "explanation": "ALD relies on alternating, self-limiting precursor exposures that react until all surface sites are consumed, producing monolayer precision."
                    },
                    {
                        "id": 12632, "question_number": 2,
                        "question": "Why can Transmission Electron Microscopy (TEM) resolve individual atomic columns while optical microscopes cannot?",
                        "options": {"A": "Electrons have much shorter de Broglie wavelengths than visible photons", "B": "Electrons carry a magnetic moment that reflects off nuclei", "C": "TEM uses higher numerical aperture glass lenses", "D": "Visible light cannot penetrate any solid matter"},
                        "correct_option": "A",
                        "explanation": "High-energy electrons have picometer de Broglie wavelengths (lambda = h/p), bypassing the optical Abbe diffraction limit."
                    },
                    {
                        "id": 12633, "question_number": 3,
                        "question": "Which characterization technique maps atomic-scale topography by measuring cantilever deflection using an optical lever?",
                        "options": {"A": "X-ray Photoelectron Spectroscopy (XPS)", "B": "Atomic Force Microscopy (AFM)", "C": "Fourier-Transform Infrared Spectroscopy (FTIR)", "D": "Energy-Dispersive X-ray Spectroscopy (EDS)"},
                        "correct_option": "B",
                        "explanation": "AFM uses an optical lever (laser reflecting off the back of a flexible cantilever onto a photodiode) to measure surface forces and topography."
                    },
                    {
                        "id": 12634, "question_number": 4,
                        "question": "In Scanning Electron Microscopy (SEM), which signal provides the highest lateral spatial resolution for surface topography?",
                        "options": {"A": "Backscattered electrons (BSE)", "B": "Secondary electrons (SE)", "C": "Characteristic X-rays", "D": "Auger electrons"},
                        "correct_option": "B",
                        "explanation": "Secondary electrons originate from inelastic collisions within the top few nanometers of the specimen surface, offering superior topographic detail."
                    },
                    {
                        "id": 12635, "question_number": 5,
                        "question": "What is the de Broglie wavelength of an electron accelerated through a 100 kV potential (approximate non-relativistic formula)?",
                        "options": {"A": "0.0037 nm (3.7 pm)", "B": "0.154 nm", "C": "1.24 nm", "D": "500 nm"},
                        "correct_option": "A",
                        "explanation": "At 100 kV accelerating voltage, the relativistic electron wavelength is approximately 0.0037 nm (0.037 Angstroms), allowing atomic resolution."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1263",
                        "subject": "Materials Science & Nanotechnology",
                        "chapter": "Synthesis, Characterization & Nanodevices",
                        "topic": "ALD Synthesis and Electron Microscopy",
                        "keywords": ["ALD", "atomic layer deposition", "CVD", "TEM", "SEM", "AFM", "de Broglie wavelength"],
                        "definition": "Nanoscale fabrication uses self-limiting chemical reactions (ALD) and high-resolution electron/probe microscopes (TEM, AFM) to synthesize and measure materials at the sub-nanometer scale.",
                        "formula_or_core_rule": "De Broglie Wavelength: lambda = h / sqrt(2 * m * e * V); Hooke's Law: F = -k * delta_z",
                        "worked_example": "Depositing 100 cycles of Al2O3 via TMA/water ALD at 1.1 Angstroms/cycle produces an oxide dielectric exactly 11.0 nm thick.",
                        "content": "Nanoscale synthesis relies on self-terminating chemical gas exposures (Atomic Layer Deposition) to achieve conformal monolayer coatings on advanced transistors. Structural characterization leverages picometer de Broglie wavelengths of high-energy electrons in TEM and tip-sample atomic forces in AFM to visualize single atomic columns."
                    }
                ]
            )
        ]
    })

    # 127. Genetics & Molecular Biology
    subs.append({
        "id": "SUB_GENETICS",
        "subject_id": 127,
        "name": "Genetics & Molecular Biology",
        "slug": "genetics-molecular-biology",
        "icon_symbol": "🧬",
        "color_hex": "#EC4899",
        "category": "Medical Sciences",
        "description": "DNA replication, transcription, CRISPR-Cas9 genome editing, Mendelian genetics, epigenetics, and recombinant DNA.",
        "is_added": False,
        "chapters": [
            make_ch(
                1271, 1, "Molecular Genetics: Central Dogma & Replication", 25,
                "Double helix structure, semi-conservative replication, leading/lagging strand synthesis, and DNA repair.",
                ["Watson-Crick DNA double helix and Chargaff's rules", "Semi-conservative replication: Meselson-Stahl experiment", "Replication fork enzymes: Helicase, Primase, DNA Pol III, Ligase", "Okazaki fragments and telomere shortening"],
                ["Chargaff's Rule: [A] = [T] and [G] = [C]", "DNA Directionality: 5' -> 3' polymerization", "Okazaki Ligation: Phosphodiester bond formation", "End-Replication Loss: Telomerase reverse transcriptase (TERT)"],
                "DNA Architecture & Replication Enzymology",
                "Deoxyribonucleic acid (DNA) stores genetic information in antiparallel double-stranded helices with complementary purine-pyrimidine base pairs (A=T via 2 hydrogen bonds, G#C via 3 hydrogen bonds). DNA polymerase III synthesizes new strands strictly in the 5' to 3' direction by catalyzing nucleophilic attack of the 3'-OH on incoming deoxynucleotide triphosphates (dNTPs). Because the two parent strands run antiparallel, the leading strand synthesizes continuously toward the replication fork, whereas the lagging strand is synthesized discontinuously into short Okazaki fragments primed by RNA primers.",
                "Fidelity, Repair Mechanisms & Telomeres",
                "DNA replication fidelity reaches 1 error in 10^9 to 10^10 base pairs through 3'->5' exonuclease proofreading and mismatch repair (MMR). DNA ligase seals single-stranded nicks by adenylating 5'-phosphate termini and catalyzing phosphodiester bond creation. Linear eukaryotic chromosomes encounter the end-replication problem upon removing the terminal RNA primer; telomerase—a ribonucleoprotein reverse transcriptase carrying an RNA template—extends 3' telomeric repeats (TTAGGG in humans) to preserve genomic integrity during cellular divisions.",
                "DNA polymerases can only append nucleotides to an existing 3'-OH group; they cannot initiate de novo strand synthesis without an RNA primer.",
                [
                    {
                        "id": 12711, "question_number": 1,
                        "question": "In what direction does DNA polymerase synthesize new daughter strands during replication?",
                        "options": {"A": "3' to 5'", "B": "5' to 3'", "C": "Both directions simultaneously on both strands", "D": "N-terminus to C-terminus"},
                        "correct_option": "B",
                        "explanation": "DNA polymerases strictly synthesize DNA in the 5' to 3' direction, adding nucleotides to the free 3'-OH group."
                    },
                    {
                        "id": 12712, "question_number": 2,
                        "question": "Which enzyme unwinds the double-stranded DNA helix at the replication fork?",
                        "options": {"A": "DNA Topoisomerase", "B": "DNA Helicase", "C": "DNA Ligase", "D": "RNA Primase"},
                        "correct_option": "B",
                        "explanation": "DNA Helicase hydrolyzes ATP to break hydrogen bonds between complementary base pairs, unwinding the double helix."
                    },
                    {
                        "id": 12713, "question_number": 3,
                        "question": "According to Chargaff's rules, if a double-stranded DNA sample contains 30% Adenine, what percentage of Cytosine is present?",
                        "options": {"A": "30%", "B": "20%", "C": "40%", "D": "70%"},
                        "correct_option": "B",
                        "explanation": "Adenine (30%) equals Thymine (30%), totaling 60%. The remaining 40% is equally split between Guanine (20%) and Cytosine (20%)."
                    },
                    {
                        "id": 12714, "question_number": 4,
                        "question": "Why are Okazaki fragments formed during DNA replication?",
                        "options": {"A": "The lagging template strand runs 3' to 5', forcing discontinuous 5' to 3' synthesis away from the fork", "B": "RNA primers degrade before synthesis begins", "C": "Helicase operates only on one strand at a time", "D": "Telomerase halts polymerase progress"},
                        "correct_option": "A",
                        "explanation": "Because DNA polymerase can only synthesize 5'->3', the lagging strand must be synthesized discontinuously in segments as the fork opens."
                    },
                    {
                        "id": 12715, "question_number": 5,
                        "question": "Which experiment proved that DNA replication is semi-conservative?",
                        "options": {"A": "Hershey-Chase bacteriophage experiment", "B": "Meselson-Stahl isotope labeling experiment using 15N and 14N", "C": "Griffith's transformation experiment with Streptococcus", "D": "Avery-MacLeod-McCarty enzyme degradation experiment"},
                        "correct_option": "B",
                        "explanation": "Meselson and Stahl used heavy 15N and light 14N density gradient centrifugation to demonstrate semi-conservative DNA replication."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1271",
                        "subject": "Genetics & Molecular Biology",
                        "chapter": "Molecular Genetics: Central Dogma & Replication",
                        "topic": "DNA Replication and Enzymology",
                        "keywords": ["DNA replication", "helicase", "DNA polymerase", "Okazaki fragments", "telomerase", "Chargaff rules"],
                        "definition": "DNA replication is the semi-conservative synthesis of identical genomic copies catalyzed by DNA polymerases, helicases, and ligases.",
                        "formula_or_core_rule": "Chargaff's Rule: [A] = [T], [G] = [C]; Polymerization direction: 5' -> 3'",
                        "worked_example": "A duplex with 28% Guanine contains 28% Cytosine, 22% Adenine, and 22% Thymine.",
                        "content": "DNA replication proceeds semi-conservatively. DNA Helicase unzips parent strands at replication origins. DNA Polymerase III extends the leading strand continuously 5'->3' and lagging strands discontinuously via Okazaki fragments, which DNA ligase joins after RNA primers are excised. Telomerase maintains chromosomal caps to prevent end-replication decay."
                    }
                ]
            ),
            make_ch(
                1272, 2, "Gene Expression: Transcription, Translation & Epigenetics", 25,
                "RNA polymerase, promoter recognition, splicing, codon-anticodon pairing, and histone modification.",
                ["Transcription: Promoters, TATA box, and RNA Polymerase II", "Post-transcriptional processing: 5' cap, poly-A tail, and alternative splicing", "Translation: Ribosomal A, P, and E sites, tRNA charging, and peptidyl transferase", "Epigenetic regulation: DNA methylation and histone acetylation"],
                ["Transcription initiation: TATA-binding protein (TBP)", "Genetic Code: 64 codons, 20 amino acids, AUG start codon", "Translation: Polypeptide elongation rate ~20 aa/sec", "Epigenetics: CpG island methylation silences gene expression"],
                "Transcription & RNA Processing",
                "Gene expression translates genetic instructions into functional proteins. During transcription in eukaryotes, RNA Polymerase II recognizes core promoter elements (such as the TATA box) assisted by general transcription factors (TFIIA-TFIIH). The resulting precursor messenger RNA (pre-mRNA) undergoes three essential modifications before nuclear export: addition of a 7-methylguanosine 5' cap, 3' polyadenylation (poly-A tail), and spliceosome-mediated splicing of non-coding introns. Alternative splicing enables a single pre-mRNA to yield distinct protein isoforms, vastly expanding proteomic diversity.",
                "Translation & Epigenetic Regulation",
                "Mature mRNA is translated by ribosomes consisting of small (40S) and large (60S) subunits. Aminoacyl-tRNA synthetases charge cognate transfer RNAs with amino acids. Translation proceeds through initiation at the AUG methionine codon, elongation across ribosomal A (aminoacyl), P (peptidyl), and E (exit) sites, and termination when stop codons (UAA, UAG, UGA) recruit release factors. Beyond genetic sequence, epigenetic mechanisms—such as cytosine methylation at CpG islands and histone acetylation by HATs/HDACs—control chromatin compaction (euchromatin vs heterochromatin) to dynamically regulate transcription factor accessibility.",
                "DNA methylation typically represses gene transcription by compacting chromatin, whereas histone acetylation relaxes chromatin to promote active transcription.",
                [
                    {
                        "id": 12721, "question_number": 1,
                        "question": "Which enzyme catalyzes the peptide bond formation during protein translation on the ribosome?",
                        "options": {"A": "Peptidyl transferase (ribozyme activity of rRNA)", "B": "Aminoacyl-tRNA synthetase", "C": "RNA Polymerase II", "D": "Signal peptidase"},
                        "correct_option": "A",
                        "explanation": "Peptidyl transferase activity resides within the 28S/23S ribosomal RNA (a ribozyme) of the large ribosomal subunit."
                    },
                    {
                        "id": 12722, "question_number": 2,
                        "question": "What is the primary function of the 5' 7-methylguanosine cap on eukaryotic mRNA?",
                        "options": {"A": "It terminates transcription at the polyadenylation signal", "B": "It prevents exonuclease degradation and assists ribosomal binding", "C": "It encodes the catalytic domain of histone acetyltransferases", "D": "It serves as the binding site for DNA ligase"},
                        "correct_option": "B",
                        "explanation": "The 5' cap protects mRNA from 5' exonuclease digestion and facilitates translation initiation complex recruitment."
                    },
                    {
                        "id": 12723, "question_number": 3,
                        "question": "What effect does histone acetylation by Histone Acetyltransferases (HATs) generally have on chromatin structure?",
                        "options": {"A": "Condenses chromatin into transcriptionally silent heterochromatin", "B": "Neutralizes positive charges on lysine residues, opening chromatin for transcription", "C": "Directly hydrolyzes the core histone octamers", "D": "Methylates cytosine residues at promoter CpG islands"},
                        "correct_option": "B",
                        "explanation": "Acetylation neutralizes positive charges on histone tails, weakening interactions with negatively charged DNA and forming accessible euchromatin."
                    },
                    {
                        "id": 12724, "question_number": 4,
                        "question": "How many codons in the standard genetic code designate stop signals during translation?",
                        "options": {"A": "1", "B": "2", "C": "3", "D": "4"},
                        "correct_option": "C",
                        "explanation": "There are three stop codons: UAA (ochre), UAG (amber), and UGA (opal)."
                    },
                    {
                        "id": 12725, "question_number": 5,
                        "question": "What molecular complex excises introns and ligates exons during pre-mRNA processing?",
                        "options": {"A": "Spliceosome (snRNPs)", "B": "Proteasome", "C": "Ribosome", "D": "Nucleosome core"},
                        "correct_option": "A",
                        "explanation": "Spliceosomes, composed of small nuclear ribonucleoproteins (snRNPs U1, U2, U4, U5, U6), carry out pre-mRNA intron excision."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1272",
                        "subject": "Genetics & Molecular Biology",
                        "chapter": "Gene Expression: Transcription, Translation & Epigenetics",
                        "topic": "Transcription, Splicing and Epigenetics",
                        "keywords": ["transcription", "RNA polymerase II", "splicing", "spliceosome", "translation", "histone acetylation", "DNA methylation"],
                        "definition": "Gene expression is the cellular process whereby genetic information in DNA is transcribed into mRNA and translated into polypeptides, modulated by epigenetic chromatin remodeling.",
                        "formula_or_core_rule": "Central Dogma: DNA -> pre-mRNA -> mature mRNA -> Polypeptide; Histone acetylation = active chromatin; DNA methylation = silenced chromatin",
                        "worked_example": "Alternative splicing of the tropomyosin gene generates muscle-specific vs brain-specific contractile protein isoforms from one genomic sequence.",
                        "content": "Eukaryotic transcription mediated by RNA Polymerase II yields pre-mRNA modified by 5' capping, 3' polyadenylation, and spliceosomal intron removal. Translation translates codons into amino acids at ribosomal A/P/E sites. Epigenetic modifications (CpG methylation and histone acetylation) control chromatin accessibility without altering DNA sequence."
                    }
                ]
            ),
            make_ch(
                1273, 3, "Recombinant DNA, CRISPR-Cas9 & Genetic Engineering", 25,
                "Restriction enzymes, PCR amplification, CRISPR guide RNA, Cas9 endonuclease, and gene therapy.",
                ["Recombinant DNA technology: Plasmids, restriction endonucleases, and cloning vectors", "Polymerase Chain Reaction (PCR): Denaturation, annealing, extension (2^n amplification)", "CRISPR-Cas9 mechanism: crRNA, tracrRNA, sgRNA, and PAM sequence (NGG)", "Double-strand break repair: Non-Homologous End Joining (NHEJ) vs HDR"],
                ["PCR DNA Amplification: N_copies = N_0 * 2^cycle", "SpCas9 Protospacer Adjacent Motif: 5'-NGG-3'", "Restriction Cleavage: Sticky (cohesive) vs Blunt ends", "HDR repair template efficiency: ssODN recombination rate"],
                "Recombinant DNA & Polymerase Chain Reaction (PCR)",
                "Genetic engineering allows precise manipulation and amplification of specific DNA sequences. Recombinant DNA relies on type II restriction endonucleases that recognize palindromic DNA sequences (e.g., EcoRI cutting GAATTC) producing overhang sticky ends that base-pair with vector plasmids. The Polymerase Chain Reaction (PCR) amplifies target DNA exponentially: thermal cycles denature duplexes at 95 deg C, anneal sequence-specific oligonucleotide primers at 55-65 deg C, and extend new strands using thermostable Taq or Pfu polymerase at 72 deg C. After n cycles, template DNA multiplies by approximately 2^n copies.",
                "CRISPR-Cas9 Precision Genome Editing",
                "Discovered as a bacterial adaptive immune system against bacteriophages, CRISPR-Cas9 has revolutionized genomic editing. The Streptococcus pyogenes Cas9 endonuclease forms a ribonucleoprotein complex with a single guide RNA (sgRNA). The sgRNA guides Cas9 to a 20-nucleotide target protospacer immediately adjacent to a 5'-NGG-3' Protospacer Adjacent Motif (PAM). Cas9 induces a double-strand break (DSB) 3 base pairs upstream of the PAM. Cells repair this DSB via error-prone Non-Homologous End Joining (NHEJ)—causing insertions/deletions (indels) that knock out genes—or via Homology-Directed Repair (HDR) using an exogenous donor template for precise gene insertion or correction.",
                "Cas9 strictly requires the presence of a target-adjacent Protospacer Adjacent Motif (PAM); Cas9 will not cleave DNA lacking the PAM motif even if sgRNA complementary pairing is 100%.",
                [
                    {
                        "id": 12731, "question_number": 1,
                        "question": "What is the canonical Protospacer Adjacent Motif (PAM) sequence recognized by Streptococcus pyogenes Cas9 (SpCas9)?",
                        "options": {"A": "5'-AAT-3'", "B": "5'-NGG-3'", "C": "5'-CCC-3'", "D": "5'-TATA-3'"},
                        "correct_option": "B",
                        "explanation": "SpCas9 requires a 5'-NGG-3' PAM sequence (where N is any nucleotide) directly adjacent to the 3' end of the target protospacer."
                    },
                    {
                        "id": 12732, "question_number": 2,
                        "question": "If a PCR reaction begins with 10 double-stranded template molecules, how many copies exist after 10 complete cycles (assuming 100% efficiency)?",
                        "options": {"A": "100", "B": "1,024", "C": "10,240", "D": "1,048,576"},
                        "correct_option": "C",
                        "explanation": "Total copies = N_0 * 2^n = 10 * 2^10 = 10 * 1024 = 10,240 copies."
                    },
                    {
                        "id": 12733, "question_number": 3,
                        "question": "Which DNA repair pathway typically introduces indels (insertions/deletions) resulting in gene knockouts following CRISPR cleavage?",
                        "options": {"A": "Non-Homologous End Joining (NHEJ)", "B": "Homology-Directed Repair (HDR)", "C": "Nucleotide Excision Repair (NER)", "D": "Base Excision Repair (BER)"},
                        "correct_option": "A",
                        "explanation": "NHEJ ligates broken DNA ends without a homologous template, frequently introducing frameshift indels that knock out target genes."
                    },
                    {
                        "id": 12734, "question_number": 4,
                        "question": "Why is Taq polymerase from Thermus aquaticus uniquely suited for automated PCR?",
                        "options": {"A": "It can synthesize DNA at 0 deg C", "B": "It is thermostable and withstands 95 deg C denaturation temperatures without denaturing", "C": "It does not require oligonucleotide primers", "D": "It can proofread RNA sequences directly"},
                        "correct_option": "B",
                        "explanation": "Taq polymerase withstands high-temperature DNA denaturation (95 deg C) without losing catalytic activity across thermal cycles."
                    },
                    {
                        "id": 12735, "question_number": 5,
                        "question": "Which enzyme covalently bonds the 5'-phosphate of one DNA fragment to the 3'-hydroxyl of another during plasmid cloning?",
                        "options": {"A": "DNA Helicase", "B": "DNA Ligase", "C": "Reverse Transcriptase", "D": "Alkaline Phosphatase"},
                        "correct_option": "B",
                        "explanation": "DNA Ligase catalyzes phosphodiester bond formation between adjacent 3'-OH and 5'-PO4 termini in recombinant DNA vectors."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1273",
                        "subject": "Genetics & Molecular Biology",
                        "chapter": "Recombinant DNA, CRISPR-Cas9 & Genetic Engineering",
                        "topic": "CRISPR-Cas9 and PCR",
                        "keywords": ["CRISPR", "Cas9", "sgRNA", "PAM sequence", "PCR", "NHEJ", "HDR", "recombinant DNA"],
                        "definition": "Genetic engineering uses molecular tools such as restriction enzymes, PCR amplification, and CRISPR-Cas9 targeted endonucleases to modify genomes with precision.",
                        "formula_or_core_rule": "PCR amplification: N = N_0 * 2^n; SpCas9 PAM: 5'-NGG-3'; NHEJ causes knockouts, HDR enables knock-ins",
                        "worked_example": "Designing an sgRNA complementary to a mutated beta-globin allele in sickle cell disease triggers Cas9 cleavage next to a 5'-TGG-3' PAM for therapeutic gene correction.",
                        "content": "Recombinant DNA utilizes restriction endonucleases and ligases for molecular cloning. PCR exponentially amplifies target DNA segments through cyclic denaturation, annealing, and polymer extension. CRISPR-Cas9 utilizes a single guide RNA and an NGG PAM motif to induce site-specific double-strand breaks, driving gene disruption through NHEJ or gene editing through template-directed HDR."
                    }
                ]
            )
        ]
    })

    # 128. Human Anatomy & Physiology
    subs.append({
        "id": "SUB_ANATOMY",
        "subject_id": 128,
        "name": "Human Anatomy & Physiology",
        "slug": "human-anatomy-physiology",
        "icon_symbol": "🫀",
        "color_hex": "#DC2626",
        "category": "Medical Sciences",
        "description": "Cardiovascular hemodynamics, respiratory gas exchange, nephron filtration, endocrine feedback, and musculoskeletal mechanics.",
        "is_added": False,
        "chapters": [
            make_ch(
                1281, 1, "Cardiovascular System & Hemodynamics", 25,
                "Cardiac cycle, electrophysiology (ECG), blood pressure regulation, cardiac output, and vascular resistance.",
                ["Cardiac conduction system: SA node, AV node, Bundle of His, Purkinje fibers", "Cardiac cycle phases: Isovolumetric contraction, ejection, isovolumetric relaxation, filling", "Wiggers diagram and ECG components (P wave, QRS complex, T wave)", "Hemodynamics: Cardiac output (CO = HR * SV) and Mean Arterial Pressure (MAP)"],
                ["Cardiac Output: CO = HR * SV", "Mean Arterial Pressure: MAP = DBP + 1/3 * (SBP - DBP) = CO * SVR", "Poiseuille's Vascular Resistance: R = (8 * eta * L) / (pi * r^4)", "Ejection Fraction: EF = (SV / EDV) * 100%"],
                "Cardiac Electrophysiology & Mechanics",
                "The cardiovascular system provides tissue perfusion and metabolic transport. The cardiac conduction hierarchy initiates action potentials spontaneously at the Sinoatrial (SA) node (intrinsic rate 60-100 bpm). The impulse propagates across atria (generating the P wave on surface ECG), delays at the Atrioventricular (AV) node (PR interval) to allow complete atrial emptying, and rapidly descends the Bundle of His and Purkinje arborization to trigger coordinated ventricular depolarization (QRS complex). Ventricular repolarization generates the T wave. Electromechanical coupling triggers calcium-induced calcium release (CICR) from the sarcoplasmic reticulum, generating myocardial tension.",
                "Hemodynamics & Pressure Regulation",
                "Hemodynamics obeys physical fluid laws adapted to pulsatile, compliant vascular trees. Cardiac output (CO = Heart Rate * Stroke Volume) averages 5.0 L/min in resting adults. Stroke volume (SV = End-Diastolic Volume - End-Systolic Volume) is governed by preload (Frank-Starling law), afterload (systemic vascular resistance), and myocardial contractility. In accordance with Poiseuille's equation, vascular resistance varies inversely with the fourth power of vessel radius (R proportional to 1/r^4). Consequently, small arterioles serve as the primary site of systemic vascular resistance (SVR) and blood pressure regulation via baroreceptor autonomic reflexes.",
                "Arteriolar radius changes have an exponential effect on vascular resistance due to the r^4 factor in Poiseuille's equation: halving arteriolar radius increases vascular resistance 16-fold.",
                [
                    {
                        "id": 12811, "question_number": 1,
                        "question": "What electrical event in the heart is represented by the QRS complex on an electrocardiogram (ECG)?",
                        "options": {"A": "Atrial depolarization", "B": "Ventricular depolarization", "C": "Ventricular repolarization", "D": "Sinoatrial node pacemaker firing"},
                        "correct_option": "B",
                        "explanation": "The QRS complex represents rapid ventricular depolarization preceding ventricular mechanical systole."
                    },
                    {
                        "id": 12812, "question_number": 2,
                        "question": "If a patient has a heart rate of 75 bpm, end-diastolic volume (EDV) of 120 mL, and end-systolic volume (ESV) of 50 mL, what is their Cardiac Output (CO)?",
                        "options": {"A": "3.75 L/min", "B": "5.25 L/min", "C": "6.50 L/min", "D": "9.00 L/min"},
                        "correct_option": "B",
                        "explanation": "Stroke Volume (SV) = EDV - ESV = 120 - 50 = 70 mL. Cardiac Output = HR * SV = 75 bpm * 70 mL = 5250 mL/min = 5.25 L/min."
                    },
                    {
                        "id": 12813, "question_number": 3,
                        "question": "According to Poiseuille's Law, how does vascular resistance change if an arteriole constricts to half its initial radius (r / 2)?",
                        "options": {"A": "Increases 2-fold", "B": "Increases 4-fold", "C": "Increases 8-fold", "D": "Increases 16-fold"},
                        "correct_option": "D",
                        "explanation": "Resistance R is proportional to 1/r^4. Halving the radius increases resistance by (1 / 0.5^4) = 2^4 = 16 times."
                    },
                    {
                        "id": 12814, "question_number": 4,
                        "question": "What is the primary physiological pacemaker of the healthy human heart?",
                        "options": {"A": "Atrioventricular (AV) node", "B": "Sinoatrial (SA) node", "C": "Bundle of His", "D": "Purkinje fiber network"},
                        "correct_option": "B",
                        "explanation": "The Sinoatrial (SA) node possesses the fastest intrinsic spontaneous diastolic depolarization rate, pacing the heart."
                    },
                    {
                        "id": 12815, "question_number": 5,
                        "question": "What phase of the cardiac cycle occurs between mitral valve closure and aortic valve opening?",
                        "options": {"A": "Isovolumetric contraction", "B": "Rapid ventricular ejection", "C": "Isovolumetric relaxation", "D": "Atrial systole"},
                        "correct_option": "A",
                        "explanation": "During isovolumetric contraction, all four cardiac valves are closed while ventricular pressure rises steeply without changing blood volume."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1281",
                        "subject": "Human Anatomy & Physiology",
                        "chapter": "Cardiovascular System & Hemodynamics",
                        "topic": "Cardiac Electrophysiology and Hemodynamics",
                        "keywords": ["cardiac cycle", "SA node", "ECG", "QRS complex", "cardiac output", "stroke volume", "Poiseuille law", "vascular resistance"],
                        "definition": "Cardiovascular hemodynamics governs blood flow through the heart and systemic vasculature driven by pressure gradients and vascular resistance.",
                        "formula_or_core_rule": "CO = HR * SV; MAP = DBP + 1/3*(SBP - DBP); Poiseuille Resistance: R is proportional to 1/r^4",
                        "worked_example": "A blood pressure of 120/80 mmHg yields a Mean Arterial Pressure MAP = 80 + (120 - 80)/3 = 80 + 13.3 = 93.3 mmHg.",
                        "content": "The cardiovascular system coordinates mechanical pumping through electrical pathways from the SA node across the AV node and His-Purkinje system, generating P-QRS-T electrocardiographic waveforms. Hemodynamic output depends on heart rate and stroke volume (CO = HR * SV). Arteriolar constriction regulates systemic vascular resistance following Poiseuille's r^4 inverse dependence."
                    }
                ]
            ),
            make_ch(
                1282, 2, "Respiratory Gas Exchange & Renal Nephron Physiology", 25,
                "Alveolar-capillary diffusion, oxyhemoglobin curve, glomerulus filtration, and countercurrent multiplication.",
                ["Pulmonary ventilation mechanics: Diaphragm, Boyle's law, and compliance", "Alveolar gas diffusion: Fick's law and ventilation-perfusion (V/Q) ratio", "Oxyhemoglobin dissociation curve: Bohr effect and 2,3-BPG", "Nephron filtration: Glomerular Filtration Rate (GFR), Starling forces, and Loop of Henle"],
                ["Alveolar Gas Equation: P_A_O2 = F_i_O2 * (P_atm - P_H2O) - (P_a_CO2 / R)", "Net Filtration Pressure: NFP = (P_GC - P_BS) - (pi_GC - pi_BS)", "Glomerular Filtration Rate: GFR = K_f * NFP (~125 mL/min)", "Renal Clearance: C_x = (U_x * V) / P_x"],
                "Pulmonary Mechanics & Alveolar Gas Exchange",
                "Ventilation operates by negative-pressure breathing governed by Boyle's law (P1*V1 = P2*V2). Diaphragmatic contraction expands thoracic volume, dropping intrapleural pressure from -5 to -8 cm H2O and drawing ambient air into alveoli. At the alveolar-capillary membrane (thickness ~0.5 micrometers), O2 and CO2 diffuse down partial pressure gradients according to Fick's law. In blood, hemoglobin transports 98.5% of oxygen. The sigmoidal oxyhemoglobin dissociation curve exhibits cooperative binding; right shifts (Bohr effect), caused by elevated H+ (acidosis), high PCO2, increased temperature, and 2,3-BPG, facilitate oxygen unloading into metabolically active tissues.",
                "Renal Nephron Function & Countercurrent Multiplication",
                "Each kidney contains ~1 million nephrons responsible for fluid, electrolyte, and acid-base homeostasis. In the renal corpuscle, glomerular capillaries filter plasma at a Glomerular Filtration Rate (GFR) of ~125 mL/min (180 L/day). Starling forces balance glomerular hydrostatic pressure against opposing Bowman's space hydrostatic and capillary oncotic pressures. The proximal convoluted tubule reabsorbs ~65% of filtered Na+, water, and 100% of glucose and amino acids. In the medulla, the hairpin Loop of Henle acts as a countercurrent multiplier: the descending limb is water-permeable, while the thick ascending limb actively pumps Na+-K+-2Cl- without water, generating a hyperosmotic medullary gradient that allows antidiuretic hormone (ADH) to concentrate urine in collecting ducts.",
                "The thick ascending limb of Henle is impermeable to water but actively reabsorbs solutes (Na-K-2Cl), creating the corticomedullary osmotic gradient required for urine concentration.",
                [
                    {
                        "id": 12821, "question_number": 1,
                        "question": "Which physiological factor shifts the oxygen-hemoglobin dissociation curve to the RIGHT (Bohr effect), promoting O2 unloading?",
                        "options": {"A": "Decreased body temperature", "B": "Increased arterial pH (alkalosis)", "C": "Increased PCO2 and hydrogen ion concentration (acidosis)", "D": "Decreased 2,3-bisphosphoglycerate (2,3-BPG)"},
                        "correct_option": "C",
                        "explanation": "High PCO2, increased H+ (low pH), high temperature, and elevated 2,3-BPG all right-shift the curve to enhance oxygen delivery to active tissues."
                    },
                    {
                        "id": 12822, "question_number": 2,
                        "question": "What is the normal Glomerular Filtration Rate (GFR) in a healthy young adult?",
                        "options": {"A": "15 mL/min", "B": "60 mL/min", "C": "125 mL/min (approx. 180 L/day)", "D": "500 mL/min"},
                        "correct_option": "C",
                        "explanation": "Normal GFR averages 125 mL/min, filtering approximately 180 liters of blood plasma daily."
                    },
                    {
                        "id": 12823, "question_number": 3,
                        "question": "Where does the complete reabsorption of filtered glucose and amino acids normally take place in the nephron?",
                        "options": {"A": "Proximal convoluted tubule", "B": "Descending limb of the Loop of Henle", "C": "Distal convoluted tubule", "D": "Cortical collecting duct"},
                        "correct_option": "A",
                        "explanation": "The proximal convoluted tubule reabsorbs 100% of filtered glucose and amino acids via sodium-dependent secondary active cotransporters (SGLT)."
                    },
                    {
                        "id": 12824, "question_number": 4,
                        "question": "What is the primary action of Antidiuretic Hormone (ADH / Vasopressin) on the kidney?",
                        "options": {"A": "Inhibits sodium reabsorption in the thick ascending limb", "B": "Inserts Aquaporin-2 water channels into the apical membrane of collecting duct principal cells", "C": "Increases aldosterone secretion from the adrenal medulla", "D": "Constricts the afferent arteriole to halt filtration"},
                        "correct_option": "B",
                        "explanation": "ADH stimulates V2 receptors to insert Aquaporin-2 channels into collecting duct luminal membranes, promoting water reabsorption."
                    },
                    {
                        "id": 12825, "question_number": 5,
                        "question": "Which clinical clearance marker is considered the gold standard for measuring GFR because it is freely filtered and neither reabsorbed nor secreted?",
                        "options": {"A": "Glucose", "B": "Inulin", "C": "Sodium", "D": "Albumin"},
                        "correct_option": "B",
                        "explanation": "Inulin is the exogenous physiological gold standard for GFR measurement because it is freely filtered without tubular reabsorption or secretion."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1282",
                        "subject": "Human Anatomy & Physiology",
                        "chapter": "Respiratory Gas Exchange & Renal Nephron Physiology",
                        "topic": "Respiration and Renal Countercurrent Mechanism",
                        "keywords": ["respiration", "alveoli", "hemoglobin", "Bohr effect", "nephron", "GFR", "Loop of Henle", "ADH"],
                        "definition": "Respiratory gas exchange transfers oxygen and carbon dioxide across alveolar membranes, while renal nephrons filter plasma and concentrate urine via countercurrent multiplication.",
                        "formula_or_core_rule": "Bohr Effect: high CO2/H+ -> right shift -> O2 release; GFR = K_f * ((P_GC - P_BS) - pi_GC); Clearance C = (U * V) / P",
                        "worked_example": "If urine inulin is 50 mg/mL, plasma inulin is 1 mg/mL, and urine flow is 2 mL/min, GFR = (50 * 2) / 1 = 100 mL/min.",
                        "content": "Respiratory mechanics rely on diaphragmatic expansion to create subatmospheric alveolar pressures. Hemoglobin delivers O2 cooperatively, with the Bohr effect releasing oxygen in acidic, hypercapnic tissues. The renal nephron processes 180 L of filtrate daily, utilizing the countercurrent medullary multiplier in the Loop of Henle and ADH-mediated aquaporins to maintain osmolarity."
                    }
                ]
            ),
            make_ch(
                1283, 3, "Endocrine Regulation & Musculoskeletal Biomechanics", 20,
                "Hypothalamic-pituitary axes, hormone signaling, sliding filament theory, and joint kinetics.",
                ["Hypothalamic-pituitary-adrenal/thyroid axes and negative feedback loops", "Steroid vs peptide hormone signaling mechanisms (GPCRs, cAMP, nuclear receptors)", "Skeletal muscle excitation-contraction coupling: T-tubules, troponin, and tropomyosin", "Sliding filament cross-bridge cycle and biomechanical lever systems"],
                ["Sliding Filament: Ca2+ binds Troponin C -> Tropomyosin shifts -> Myosin binds Actin", "ATP Hydrolysis: Myosin cocking (ADP + Pi) -> Power stroke (Pi release)", "Endocrine Feedback: TSH inhibition by elevated free T3/T4", "Biomechanical Muscle Moment: Torque = Force * perpendicular lever arm"],
                "Endocrine Regulation & Feedback Control",
                "The endocrine system maintains metabolic homeostasis via hormonal chemical messengers. Hydrophilic peptide hormones (e.g., insulin, glucagon, ACTH) bind cell-surface G-protein coupled receptors (GPCRs), stimulating secondary messenger cascades (cAMP, IP3/DAG, protein kinase A/C). Lipophilic steroid hormones (e.g., cortisol, testosterone, estradiol) diffuse across plasma membranes to bind cytosolic or nuclear receptors, translocating to DNA Hormone Response Elements (HREs) to modulate gene transcription. Negative feedback loops govern homeostasis: in the HPT axis, elevated circulating thyroid hormones (T3/T4) suppress pituitary TSH and hypothalamic TRH secretion.",
                "Musculoskeletal Mechanics & Sliding Filament Theory",
                "Skeletal muscle contraction couples electrical excitation to mechanical force generation. Action potentials travel down transverse tubules (T-tubules), activating dihydropyridine receptors (DHPR) coupled to ryanodine receptors (RyR1) on the sarcoplasmic reticulum. Calcium efflux raises cytosolic [Ca2+] from 0.1 to 10 micromolar. Calcium binds Troponin C, causing a conformational shift that pulls tropomyosin away from actin active sites. Myosin heads bind actin, release inorganic phosphate, and execute a 45-degree power stroke sliding actin filaments toward the sarcomere M-line. ATP binding releases myosin from actin, allowing the cross-bridge cycle to repeat.",
                "Rigor mortis occurs after death because ATP depletion prevents ATP from binding to myosin heads, leaving actin-myosin cross-bridges permanently locked in contraction.",
                [
                    {
                        "id": 12831, "question_number": 1,
                        "question": "What molecule must bind to the myosin head to cause its detachment from the actin filament during the cross-bridge cycle?",
                        "options": {"A": "Calcium ion (Ca2+)", "B": "Adenosine Triphosphate (ATP)", "C": "Inorganic phosphate (Pi)", "D": "Acetylcholine"},
                        "correct_option": "B",
                        "explanation": "ATP binding to the myosin head causes a conformational change that dissociates the myosin head from the actin filament."
                    },
                    {
                        "id": 12832, "question_number": 2,
                        "question": "Which protein masks the myosin-binding sites on actin filaments in a resting skeletal muscle fiber?",
                        "options": {"A": "Troponin T", "B": "Tropomyosin", "C": "Titin", "D": "Dystrophin"},
                        "correct_option": "B",
                        "explanation": "In resting muscle, tropomyosin lies over the active sites on actin, preventing myosin cross-bridge attachment until Ca2+ binds Troponin C."
                    },
                    {
                        "id": 12833, "question_number": 3,
                        "question": "Where are steroid hormone receptors predominantly located prior to hormone binding?",
                        "options": {"A": "On the external outer leaflet of the plasma membrane", "B": "Within the cytoplasm or nucleus of target cells", "C": "In the lumen of the rough endoplasmic reticulum", "D": "Attached to mitochondrial outer membranes"},
                        "correct_option": "B",
                        "explanation": "Lipophilic steroid hormones cross cell membranes and bind intracellular receptors located in the cytoplasm or nucleus."
                    },
                    {
                        "id": 12834, "question_number": 4,
                        "question": "In the hypothalamic-pituitary-thyroid (HPT) axis, what effect do elevated levels of free T3 and T4 have on the anterior pituitary?",
                        "options": {"A": "Stimulate increased secretion of Thyroid-Stimulating Hormone (TSH)", "B": "Inhibit secretion of Thyroid-Stimulating Hormone (TSH) via negative feedback", "C": "Cause pituitary hyperplasia and thyrotoxic crisis", "D": "Promote iodine uptake into pituitary cells"},
                        "correct_option": "B",
                        "explanation": "Elevated thyroid hormones exert negative feedback on anterior pituitary thyrotrophs, suppressing TSH secretion."
                    },
                    {
                        "id": 12835, "question_number": 5,
                        "question": "What type of anatomical lever system is represented by the human biceps brachii flexing the elbow joint against resistance?",
                        "options": {"A": "First-class lever", "B": "Second-class lever", "C": "Third-class lever", "D": "Fourth-class lever"},
                        "correct_option": "C",
                        "explanation": "The elbow flexor is a third-class lever (effort applied by biceps tendon between the elbow fulcrum and hand resistance)."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1283",
                        "subject": "Human Anatomy & Physiology",
                        "chapter": "Endocrine Regulation & Musculoskeletal Biomechanics",
                        "topic": "Hormonal Feedback and Muscle Cross-Bridge Cycle",
                        "keywords": ["endocrine", "steroid hormones", "GPCR", "sliding filament", "myosin", "actin", "troponin", "tropomyosin"],
                        "definition": "Endocrine feedback loops maintain systemic homeostasis through receptor signaling, while musculoskeletal systems generate movement through calcium-regulated actin-myosin cross-bridge cycling.",
                        "formula_or_core_rule": "Cross-Bridge Cycle: Ca2+ + Troponin C -> Tropomyosin shift -> Myosin-actin binding -> Power stroke -> ATP binding causes release",
                        "worked_example": "During a bicep curl, a 150 N hand load at 35 cm from the elbow fulcrum produces a 52.5 N*m load torque that requires 1050 N of biceps tension at a 5 cm insertion.",
                        "content": "Endocrine hormones coordinate cellular activity via membrane GPCR second-messenger cascades or nuclear gene transcription receptors under negative feedback restraint. Skeletal muscle contraction operates via excitation-contraction coupling: action potentials trigger sarcoplasmic Ca2+ release, binding Troponin C to expose actin sites for ATP-powered myosin cross-bridge pulling."
                    }
                ]
            )
        ]
    })

    # 129. Pharmacology & Drug Delivery
    subs.append({
        "id": "SUB_PHARMA",
        "subject_id": 129,
        "name": "Pharmacology & Drug Delivery",
        "slug": "pharmacology-drug-delivery",
        "icon_symbol": "💊",
        "color_hex": "#8B5CF6",
        "category": "Medical Sciences",
        "description": "Pharmacokinetics (ADME), pharmacodynamics (receptor binding, EC50), targeted nanocarriers, and sustained release.",
        "is_added": False,
        "chapters": [
            make_ch(
                1291, 1, "Pharmacokinetics: ADME & Mathematical Modeling", 25,
                "Absorption, distribution, metabolism, excretion, clearance, volume of distribution, and half-life.",
                ["ADME parameters: Bioavailability (F), C_max, T_max, and AUC", "Volume of distribution (V_d) and plasma protein binding", "Hepatic metabolism: Cytochrome P450 phase I vs phase II conjugation", "Zero-order vs First-order elimination kinetics and half-life (t_1/2)"],
                ["First-Order Elimination Rate: C(t) = C_0 * e^(-k_e * t)", "Elimination Half-Life: t_1/2 = 0.693 / k_e = (0.693 * V_d) / Cl", "Clearance: Cl = (Dose * F) / AUC = k_e * V_d", "Steady-State Concentration: C_ss = (F * Dose) / (Cl * tau)"],
                "Absorption, Distribution & Volume of Distribution",
                "Pharmacokinetics (PK) models what the body does to a drug over time. Bioavailability (F) represents the fraction of administered dose reaching systemic circulation unchanged (100% for IV injections). Following oral administration, drugs undergo first-pass hepatic metabolism via the portal vein. The apparent Volume of Distribution (V_d = Dose / C_0) relates total drug in the body to plasma concentration; lipophilic drugs that partition into adipose tissue or bind tissue proteins exhibit large V_d values (>100 L), whereas hydrophilic or heavily albumin-bound drugs remain restricted to vascular compartments (small V_d ~3-5 L).",
                "Hepatic Clearance & Elimination Kinetics",
                "Drug elimination occurs primarily through renal excretion and hepatic biotransformation. Cytochrome P450 enzymes (e.g., CYP3A4, CYP2D6) catalyze Phase I functionalization reactions (oxidation, reduction, hydrolysis), while Phase II enzymes (e.g., UGTs) conjugate polar moieties (glucuronic acid, sulfate) to enhance water solubility. In first-order kinetics—exhibited by most clinical drugs at therapeutic doses—a constant fraction of drug is eliminated per unit time, resulting in an exponential concentration decay and constant half-life t_1/2 = 0.693 * V_d / Cl. In zero-order kinetics (e.g., ethanol, high-dose phenytoin), metabolic enzymes saturate, eliminating a constant amount per unit time.",
                "Steady-state drug concentration (Css) is achieved after approximately 4 to 5 elimination half-lives during constant maintenance dosing.",
                [
                    {
                        "id": 12911, "question_number": 1,
                        "question": "How many elimination half-lives does it typically take for a drug following first-order kinetics to reach steady-state concentration (Css)?",
                        "options": {"A": "1 half-life", "B": "2 to 3 half-lives", "C": "4 to 5 half-lives", "D": "10 to 12 half-lives"},
                        "correct_option": "C",
                        "explanation": "In first-order kinetics, a drug reaches ~93.75% of steady-state after 4 half-lives and ~96.88% after 5 half-lives."
                    },
                    {
                        "id": 12912, "question_number": 2,
                        "question": "If a drug has a Volume of Distribution (V_d) of 70 L and a systemic clearance (Cl) of 3.5 L/hr, what is its elimination half-life (t_1/2)?",
                        "options": {"A": "6.93 hours", "B": "13.86 hours", "C": "20.00 hours", "D": "24.50 hours"},
                        "correct_option": "B",
                        "explanation": "t_1/2 = (0.693 * V_d) / Cl = (0.693 * 70) / 3.5 = 48.51 / 3.5 = 13.86 hours."
                    },
                    {
                        "id": 12913, "question_number": 3,
                        "question": "Which enzyme family in the liver is responsible for the majority of Phase I oxidative drug metabolism?",
                        "options": {"A": "Cytochrome P450 (CYP) monooxygenases", "B": "UDP-glucuronosyltransferases (UGT)", "C": "Glutathione S-transferases", "D": "Monoamine oxidases"},
                        "correct_option": "A",
                        "explanation": "Hepatic Cytochrome P450 (CYP) enzymes carry out oxidative and hydrolytic Phase I biotransformations."
                    },
                    {
                        "id": 12914, "question_number": 4,
                        "question": "What distinguishes zero-order elimination kinetics from first-order elimination kinetics?",
                        "options": {"A": "A constant fraction of drug is eliminated per hour in zero-order", "B": "A constant mass or amount of drug is eliminated per hour in zero-order", "C": "Zero-order clearance is independent of enzyme saturation", "D": "Zero-order drugs never achieve therapeutic efficacy"},
                        "correct_option": "B",
                        "explanation": "Zero-order elimination clears a fixed amount of drug per unit time because metabolic or excretory mechanisms are saturated."
                    },
                    {
                        "id": 12915, "question_number": 5,
                        "question": "What is the bioavailability (F) of an intravenous (IV) bolus dose?",
                        "options": {"A": "0%", "B": "50%", "C": "100% (F = 1.0)", "D": "Depends on hepatic blood flow"},
                        "correct_option": "C",
                        "explanation": "Intravenous administration injects 100% of the active drug directly into the systemic circulation, meaning F = 1.0."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1291",
                        "subject": "Pharmacology & Drug Delivery",
                        "chapter": "Pharmacokinetics: ADME & Mathematical Modeling",
                        "topic": "ADME Kinetics and Half-Life Calculations",
                        "keywords": ["pharmacokinetics", "ADME", "bioavailability", "clearance", "half-life", "CYP450", "volume of distribution"],
                        "definition": "Pharmacokinetics quantitatively characterizes drug absorption, tissue distribution, enzymatic metabolism, and systemic elimination over time.",
                        "formula_or_core_rule": "t_1/2 = (0.693 * V_d) / Cl; C_ss = (F * Dose) / (Cl * tau); Clearance Cl = k_e * V_d",
                        "worked_example": "Administering 500 mg IV of a drug with V_d = 50 L yields an initial peak plasma concentration C_0 = 500 mg / 50 L = 10 mg/L.",
                        "content": "Pharmacokinetic profiling evaluates the physiological disposition of therapeutics. Bioavailability F indicates systemic delivery fractions. Volume of distribution characterizes tissue partition. First-order clearance processes eliminate constant fractions per hour, yielding exponential decay where steady-state equilibrium is reached after 4-5 elimination half-lives."
                    }
                ]
            ),
            make_ch(
                1292, 2, "Pharmacodynamics: Receptors, Agonism & Signal Transduction", 20,
                "Receptor classification, dose-response curves, EC50, Kd, competitive vs allosteric antagonism.",
                ["Receptor families: Ligand-gated ion channels, GPCRs, RTKs, and nuclear receptors", "Dose-response curves: Efficacy (E_max) vs Potency (EC50)", "Receptor binding kinetics: Dissociation constant (K_d) and occupancy", "Antagonist types: Competitive, non-competitive, uncompetitive, and inverse agonists"],
                ["Hill-Langmuir Binding: Fraction_bound = [L] / ([L] + K_d)", "Sigmoidal Dose-Response: Effect = E_max * [A]^n / ([A]^n + EC50^n)", "Therapeutic Index: TI = TD50 / ED50 (or LD50 / ED50)", "Competitive Antagonist Schild Equation: Dose Ratio = 1 + [B] / K_b"],
                "Receptor Signaling & Dose-Response Relationships",
                "Pharmacodynamics (PD) describes what a drug does to the body. Therapeutics trigger biochemical responses by interacting with specialized macromolecular receptors. Agonists bind receptors and stabilize active conformations to elicit intracellular signaling cascades. Potency is quantified by the half-maximal effective concentration (EC50)—lower EC50 indicates greater potency. Efficacy (E_max) reflects the maximal achievable therapeutic response irrespective of dose. Full agonists elicit 100% of the tissue's maximal response, while partial agonists elicit submaximal responses even at complete receptor saturation.",
                "Antagonist Pharmacology & Therapeutic Index",
                "Antagonists attenuate or prevent agonist-mediated receptor activation. Competitive antagonists reversibly bind the orthosteric active site; increasing agonist concentration outcompetes the inhibitor, shifting the log dose-response curve parallel to the right without reducing E_max. Non-competitive and allosteric antagonists bind distinct allosteric sites or form irreversible covalent bonds, suppressing E_max without right-shifting EC50. The safety margin of a drug is measured by the Therapeutic Index (TI = TD50 / ED50); high-TI drugs (e.g., penicillin) are safe, whereas low-TI drugs (e.g., warfarin, digoxin, lithium) require rigorous therapeutic drug monitoring.",
                "Competitive antagonists cause a rightward parallel shift of the agonist dose-response curve (increasing EC50) without diminishing the maximum response (Emax).",
                [
                    {
                        "id": 12921, "question_number": 1,
                        "question": "What effect does a competitive antagonist have on an agonist's log dose-response curve?",
                        "options": {"A": "Reduces E_max without altering EC50", "B": "Shifts the curve parallel to the right, increasing EC50 without decreasing E_max", "C": "Shifts the curve to the left, increasing agonist potency", "D": "Converts full agonists into inverse agonists"},
                        "correct_option": "B",
                        "explanation": "Competitive antagonists compete for the same receptor site; higher agonist doses overcome the block, increasing EC50 with unchanged E_max."
                    },
                    {
                        "id": 12922, "question_number": 2,
                        "question": "What is the pharmacological term for the drug concentration that produces 50% of the maximal biological effect?",
                        "options": {"A": "K_d", "B": "EC50", "C": "LD50", "D": "C_max"},
                        "correct_option": "B",
                        "explanation": "EC50 (half-maximal effective concentration) quantifies drug potency on functional dose-response curves."
                    },
                    {
                        "id": 12923, "question_number": 3,
                        "question": "How is the Therapeutic Index (TI) of a drug defined?",
                        "options": {"A": "EC50 / K_d", "B": "TD50 / ED50 (toxic dose to 50% / effective dose to 50%)", "C": "Bioavailability / Clearance", "D": "V_d / t_1/2"},
                        "correct_option": "B",
                        "explanation": "Therapeutic Index is the ratio of toxic dose (TD50) to effective therapeutic dose (ED50); larger values indicate safer drugs."
                    },
                    {
                        "id": 12924, "question_number": 4,
                        "question": "Which type of agent binds to a constitutively active receptor and reduces its basal activity below resting baseline?",
                        "options": {"A": "Full agonist", "B": "Partial agonist", "C": "Neutral antagonist", "D": "Inverse agonist"},
                        "correct_option": "D",
                        "explanation": "Inverse agonists bind to receptors that possess baseline constitutive activity, stabilizing the inactive receptor state."
                    },
                    {
                        "id": 12925, "question_number": 5,
                        "question": "Which receptor superfamily produces the fastest cellular response (millisecond timescale) upon agonist binding?",
                        "options": {"A": "Ligand-gated ion channels (ionotropic receptors)", "B": "G-protein coupled receptors (metabotropic)", "C": "Receptor tyrosine kinases", "D": "Nuclear steroid receptors"},
                        "correct_option": "A",
                        "explanation": "Ligand-gated ion channels (e.g., nicotinic acetylcholine receptors) directly open pores across membranes within milliseconds."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1292",
                        "subject": "Pharmacology & Drug Delivery",
                        "chapter": "Pharmacodynamics: Receptors, Agonism & Signal Transduction",
                        "topic": "Receptor Theory and Antagonism",
                        "keywords": ["pharmacodynamics", "agonist", "competitive antagonist", "EC50", "efficacy", "potency", "therapeutic index"],
                        "definition": "Pharmacodynamics analyzes molecular interactions between therapeutics and biological receptors, correlating drug concentration with physiological effect.",
                        "formula_or_core_rule": "Therapeutic Index: TI = TD50 / ED50; Hill equation: Effect = E_max * [A]^n / ([A]^n + EC50^n)",
                        "worked_example": "If Drug A has an EC50 of 2 nM and Drug B has an EC50 of 200 nM to produce the same contraction, Drug A is 100 times more potent.",
                        "content": "Pharmacodynamics establishes dose-response relationships. Potency is reflected by EC50, whereas efficacy represents maximal effect E_max. Full agonists elicit maximal tissue response, partial agonists elicit fractional response, and competitive antagonists shift curves rightward without reducing E_max. Therapeutic Index measures clinical safety margin."
                    }
                ]
            ),
            make_ch(
                1293, 3, "Advanced Drug Delivery: Nanocarriers & Controlled Release", 20,
                "Liposomes, polymeric nanoparticles, PEGylation, EPR effect in solid tumors, and sustained release implants.",
                ["Limitations of conventional systemic administration", "Liposomes and lipid nanoparticles (LNPs) for mRNA delivery", "Enhanced Permeability and Retention (EPR) effect in tumor targeting", "Polymeric microparticles (PLGA) and sustained diffusion-erosion kinetics"],
                ["Higuchi Release Equation: Q = K_H * sqrt(t)", "Korsmeyer-Peppas Power Law: M_t / M_infinity = K * t^n", "EPR Cutoff Size: Nanoparticles 50 - 200 nm extravasate through leaky tumor vasculature", "PEGylation: Polyethylene glycol steric hindrance avoids reticuloendothelial clearance"],
                "Nanocarrier Formulations: Liposomes & Lipid Nanoparticles",
                "Traditional systemic therapeutics suffer from rapid renal clearance, non-specific organ toxicity, and poor aqueous solubility. Nanotechnology overcomes these barriers through engineered colloidal carriers. Liposomes consist of self-assembled phospholipid bilayers encapsulating hydrophilic drugs in an aqueous core and hydrophobic drugs within lipid bilayers. Lipid Nanoparticles (LNPs) formulated with ionizable cationic lipids, cholesterol, and PEGylated lipids encapsulate negatively charged mRNA (as used in SARS-CoV-2 vaccines), protecting nucleic acids from ribonuclease degradation and promoting endosomal release into target cell cytoplasm.",
                "Targeted Delivery & Controlled Release Kinetics",
                "Passive tumor targeting exploits the Enhanced Permeability and Retention (EPR) effect: rapidly proliferating solid tumors recruit defective, hyperpermeable blood vessels with discontinuous endothelial fenestrations (100-800 nm) and lack functional lymphatic drainage, allowing 50-200 nm nanocarriers to extravasate and accumulate preferentially in tumor tissue. Conjugating surface polyethylene glycol (PEGylation) creates a hydration shield that prevents opsonization by plasma proteins and macrophage uptake in the liver/spleen. Biodegradable polymers such as poly(lactic-co-glycolic acid) (PLGA) provide sustained drug release through combined matrix erosion and diffusion following Higuchi kinetics.",
                "Surface PEGylation prolongs nanoparticle systemic circulation half-life by preventing opsonization and clearance by the reticuloendothelial system.",
                [
                    {
                        "id": 12931, "question_number": 1,
                        "question": "What physiological phenomenon allows 50-200 nm nanoparticles to preferentially accumulate in solid tumors?",
                        "options": {"A": "Active transcytosis mediated by insulin receptors", "B": "Enhanced Permeability and Retention (EPR) effect", "C": "Glomerular ultrafiltration", "D": "Retrograde axonal transport"},
                        "correct_option": "B",
                        "explanation": "The EPR effect arises from leaky tumor microvasculature and deficient lymphatic drainage, trapping nanocarriers in neoplastic tissue."
                    },
                    {
                        "id": 12932, "question_number": 2,
                        "question": "What is the primary role of ionizable cationic lipids in mRNA lipid nanoparticles (LNPs)?",
                        "options": {"A": "Provide covalent crosslinking to cell surface integrins", "B": "Electrostatistically condense mRNA at acidic pH and facilitate endosomal escape at physiological pH", "C": "Act as an anti-inflammatory preservative", "D": "Enzymatically transcribe mRNA into proteins"},
                        "correct_option": "B",
                        "explanation": "Ionizable lipids complex negatively charged mRNA at low formulation pH and disrupt endosomal membranes upon cellular uptake."
                    },
                    {
                        "id": 12933, "question_number": 3,
                        "question": "Why is Polyethylene Glycol (PEG) grafted onto the surface of liposomes (PEGylation)?",
                        "options": {"A": "To accelerate renal elimination of the carrier", "B": "To create a steric hydration barrier that prevents opsonization and reticuloendothelial clearance", "C": "To impart a high negative surface charge", "D": "To induce immediate systemic histamine release"},
                        "correct_option": "B",
                        "explanation": "PEGylation creates a steric hydrophilic barrier that blocks serum protein opsonization, significantly extending circulation half-life."
                    },
                    {
                        "id": 12934, "question_number": 4,
                        "question": "Which FDA-approved biodegradable copolymer hydrolyzes into lactic acid and glycolic acid to deliver sustained drug release?",
                        "options": {"A": "Poly(lactic-co-glycolic acid) (PLGA)", "B": "Polyvinyl chloride (PVC)", "C": "Polystyrene sulfonate", "D": "Polyacrylamide"},
                        "correct_option": "A",
                        "explanation": "PLGA is a biocompatible and biodegradable copolymer that safely breaks down into natural metabolic intermediates (lactic and glycolic acids)."
                    },
                    {
                        "id": 12935, "question_number": 5,
                        "question": "According to the Higuchi model of drug release from planar matrices, cumulative drug release Q is proportional to:",
                        "options": {"A": "Time t", "B": "Square root of time (sqrt(t))", "C": "Time squared (t^2)", "D": "Natural log of time (ln(t))"},
                        "correct_option": "B",
                        "explanation": "The Higuchi model describes diffusion-controlled drug release where cumulative release Q is proportional to the square root of time (sqrt(t))."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1293",
                        "subject": "Pharmacology & Drug Delivery",
                        "chapter": "Advanced Drug Delivery: Nanocarriers & Controlled Release",
                        "topic": "Lipid Nanoparticles and EPR Tumor Targeting",
                        "keywords": ["nanocarriers", "liposomes", "lipid nanoparticles", "mRNA vaccines", "EPR effect", "PEGylation", "PLGA"],
                        "definition": "Advanced drug delivery utilizes engineered nanoscale vehicles (liposomes, polymeric nanoparticles) to enhance therapeutic stability, biodistribution, and localized release.",
                        "formula_or_core_rule": "Higuchi Release: Q = K_H * sqrt(t); EPR accumulation size: 50 - 200 nm; PEGylation reduces macrophage opsonization",
                        "worked_example": "Encapsulating doxorubicin in PEGylated liposomes (Doxil) reduces peak cardiotoxic exposure while accumulating selectively in solid tumors via the EPR effect.",
                        "content": "Nanoscale drug delivery systems enhance pharmacokinetics by solubilizing fragile molecules. Lipid nanoparticles encapsulate mRNA therapeutics to prevent enzymatic degradation. Solid tumor targeting leverages the EPR effect through fenestrated capillaries. PEGylation prolongs intravascular circulation, while PLGA matrices provide sustained diffusion-erosion kinetics."
                    }
                ]
            )
        ]
    })

    # 130. Neuroscience & Brain Mapping
    subs.append({
        "id": "SUB_NEURO",
        "subject_id": 130,
        "name": "Neuroscience & Brain Mapping",
        "slug": "neuroscience-brain-mapping",
        "icon_symbol": "🧠",
        "color_hex": "#6366F1",
        "category": "Medical Sciences",
        "description": "Neuronal electrophysiology, Hodgkin-Huxley model, synaptic transmission, fMRI, connectomics, and neural plasticity.",
        "is_added": False,
        "chapters": [
            make_ch(
                1301, 1, "Neuronal Electrophysiology & Hodgkin-Huxley Dynamics", 25,
                "Resting membrane potential, Goldman-Hodgkin-Katz equation, action potential mechanics, and patch clamp.",
                ["Resting membrane potential and Nernst equilibrium potentials (K+, Na+, Cl-)", "Goldman-Hodgkin-Katz (GHK) voltage equation", "Hodgkin-Huxley model: Voltage-gated Na+ and K+ channel conductance gates", "Patch clamp electrophysiology: Whole-cell, inside-out, and outside-out configurations"],
                ["Nernst Equation: E_ion = (R * T / (z * F)) * ln([ion]_out / [ion]_in)", "GHK Voltage Equation: V_m = 61.5 * log10((P_K*[K]_o + P_Na*[Na]_o + P_Cl*[Cl]_i) / (P_K*[K]_i + P_Na*[Na]_i + P_Cl*[Cl]_o))", "Hodgkin-Huxley Current: I_m = C_m * (dV/dt) + g_Na*m^3*h*(V - E_Na) + g_K*n^4*(V - E_K) + g_L*(V - E_L)", "Axonal Space Constant: lambda = sqrt(r_m / (r_i + r_o))"],
                "Resting Membrane Potential & The GHK Equation",
                "Neurons compute and communicate through electrical potentials established across semipermeable lipid bilayers. The Na+/K+-ATPase pump actively expels 3 Na+ ions for every 2 K+ imported, maintaining steep ionic gradients: high intracellular K+ (~140 mM) and high extracellular Na+ (~145 mM). Because resting neuronal membranes are predominantly permeable to potassium through inward-rectifying and two-pore leak K+ channels, the resting membrane potential V_m (-70 mV) resides close to the potassium Nernst equilibrium potential E_K (-90 mV). The Goldman-Hodgkin-Katz (GHK) equation quantifies V_m by weighting ion concentrations by their relative membrane permeabilities (P_K : P_Na : P_Cl = 1.0 : 0.04 : 0.45 at rest).",
                "Action Potential Propagation & Hodgkin-Huxley Formulation",
                "Depolarization beyond the threshold (~ -55 mV) triggers an all-or-none action potential. Hodgkin and Huxley mathematically modeled this phenomenon using squid giant axons. Depolarization rapidly opens voltage-gated Na+ channels (activation gate m^3 opens), causing regenerative sodium influx toward E_Na (+60 mV). Within 1 ms, slower inactivation gates (h) close, halting Na+ influx, while delayed-rectifier voltage-gated K+ channels (n^4) activate, repolarizing the axon toward E_K. In myelinated axons, action potentials propagate saltatorily between Nodes of Ranvier, increasing conduction velocities up to 120 m/s while dramatically reducing metabolic ATP consumption.",
                "Saltatory conduction in myelinated axons increases conduction velocity by concentrating voltage-gated Na+ channels at Nodes of Ranvier rather than along the entire axon membrane.",
                [
                    {
                        "id": 13011, "question_number": 1,
                        "question": "What is the primary ion responsible for setting the negative resting membrane potential of a healthy neuron (~ -70 mV)?",
                        "options": {"A": "Sodium (Na+)", "B": "Potassium (K+)", "C": "Calcium (Ca2+)", "D": "Magnesium (Mg2+)"},
                        "correct_option": "B",
                        "explanation": "High resting membrane permeability to K+ allows potassium leak outward down its concentration gradient, establishing a resting potential near E_K."
                    },
                    {
                        "id": 13012, "question_number": 2,
                        "question": "In the Hodgkin-Huxley model, which gating variable describes the time-dependent inactivation of voltage-gated sodium channels?",
                        "options": {"A": "m", "B": "h", "C": "n", "D": "z"},
                        "correct_option": "B",
                        "explanation": "In the Hodgkin-Huxley model, m represents Na+ activation, h represents Na+ inactivation, and n represents K+ activation."
                    },
                    {
                        "id": 13013, "question_number": 3,
                        "question": "What is the mode of action potential propagation along a myelinated axon called?",
                        "options": {"A": "Continuous decrementing conduction", "B": "Saltatory conduction", "C": "Passive electrotonic decay", "D": "Orthodromic diffusion"},
                        "correct_option": "B",
                        "explanation": "Myelin sheaths insulate internodes, forcing action potentials to jump from node to node via saltatory conduction."
                    },
                    {
                        "id": 13014, "question_number": 4,
                        "question": "What is the theoretical equilibrium potential for K+ (E_K) at body temperature (37 deg C) if [K+]_in = 140 mM and [K+]_out = 4 mM?",
                        "options": {"A": "+60 mV", "B": "0 mV", "C": "-95 mV", "D": "-30 mV"},
                        "correct_option": "C",
                        "explanation": "E_K = 61.5 * log10(4 / 140) = 61.5 * log10(0.02857) = 61.5 * (-1.544) = -95 mV."
                    },
                    {
                        "id": 13015, "question_number": 5,
                        "question": "Which electrophysiological technique isolates a single ion channel on a microscopic patch of membrane to record pA currents?",
                        "options": {"A": "Patch clamp recording", "B": "Electroencephalography (EEG)", "C": "Positron Emission Tomography", "D": "Functional Magnetic Resonance Imaging"},
                        "correct_option": "A",
                        "explanation": "Developed by Neher and Sakmann, patch clamp recording forms a gigaseal with a glass pipette to measure single ion channel currents."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1301",
                        "subject": "Neuroscience & Brain Mapping",
                        "chapter": "Neuronal Electrophysiology & Hodgkin-Huxley Dynamics",
                        "topic": "Membrane Potentials and the Hodgkin-Huxley Model",
                        "keywords": ["electrophysiology", "action potential", "Hodgkin-Huxley", "resting potential", "GHK equation", "saltatory conduction", "patch clamp"],
                        "definition": "Neuronal electrophysiology explores electrical signaling across excitable neuronal membranes driven by ion concentration gradients and voltage-gated ion channels.",
                        "formula_or_core_rule": "Nernst equation: E = (61.5/z)*log10([out]/[in]); Hodgkin-Huxley: I_Na = g_Na*m^3*h*(V - E_Na)",
                        "worked_example": "Depolarizing an axon beyond -55 mV activates sodium channel m gates, triggering a 1 ms inward Na+ surge driving membrane potential to +40 mV.",
                        "content": "Neurons maintain resting polarization through Na+/K+ pumps and potassium leak channels. Threshold depolarization initiates action potentials described by the Hodgkin-Huxley equations: rapid activation of Na+ conductances followed by inactivation and delayed-rectifier K+ channel activation. Myelin sheaths facilitate rapid saltatory conduction across Nodes of Ranvier."
                    }
                ]
            ),
            make_ch(
                1302, 2, "Synaptic Neurotransmission & Long-Term Potentiation (LTP)", 25,
                "SNARE complex vesicle release, NMDA/AMPA receptors, dendritic spines, Hebbian plasticity, and CaMKII.",
                ["Quantal neurotransmitter release and SNARE complex (Synaptobrevin, Syntaxin, SNAP-25)", "Excitatory (Glutamate) vs Inhibitory (GABA/Glycine) neurotransmission", "AMPA and NMDA receptor activation: The magnesium block mechanism", "Long-Term Potentiation (LTP), dendritic spine remodeling, and CaMKII phosphorylation"],
                ["Quantal Content: m = n * p (number of release sites * release probability)", "Postsynaptic Potential: EPSP = g_AMPA * (V - E_AMPA)", "NMDA Magnesium Block: Relieved by membrane depolarization to >= -30 mV", "Hebbian Rule: Neurons that fire together, wire together"],
                "Presynaptic Vesicle Fusion & SNARE Dynamics",
                "Synaptic transmission translates electrical action potentials into chemical signals. Depolarization reaching presynaptic axon terminals opens voltage-gated P/Q- and N-type Ca2+ channels, elevating local calcium concentration. Intracellular Ca2+ binds synaptotagmin-1, which catalyzes zipper-like assembly of the core SNARE complex consisting of v-SNARE synaptobrevin on the vesicle and t-SNAREs (syntaxin-1 and SNAP-25) on the presynaptic plasma membrane. This nanomechanical pull overcomes electrostatic repulsion, opening a fusion pore that discharges neurotransmitters (quantal release) into the 20 nm synaptic cleft within 0.2 milliseconds.",
                "Postsynaptic Receptors & Synaptic Plasticity (LTP)",
                "At excitatory glutamatergic synapses, glutamate binds both AMPA and NMDA ionotropic receptors. AMPA receptors conduct rapid Na+ influx, generating excitatory postsynaptic potentials (EPSPs). At resting potentials, NMDA receptor pores are physically occluded by extracellular magnesium ions (Mg2+ block). Sustained high-frequency stimulation produces repetitive AMPA-mediated depolarization that expels the Mg2+ ion. Unblocked NMDA channels conduct extensive Ca2+ influx. Calcium activates Calcium/Calmodulin-Dependent Protein Kinase II (CaMKII), which phosphorylates existing AMPA receptors (enhancing single-channel conductance) and drives trafficking of reserve AMPA receptors into the postsynaptic density, establishing Long-Term Potentiation (LTP)—the molecular substrate of memory formation.",
                "NMDA receptors act as coincidence detectors because they require both glutamate binding and postsynaptic depolarization (to expel the Mg2+ block) to permit calcium influx.",
                [
                    {
                        "id": 13021, "question_number": 1,
                        "question": "What ion physically blocks the pore of the NMDA receptor channel at normal resting membrane potentials (~ -70 mV)?",
                        "options": {"A": "Sodium (Na+)", "B": "Calcium (Ca2+)", "C": "Magnesium (Mg2+)", "D": "Chloride (Cl-)"},
                        "correct_option": "C",
                        "explanation": "Extracellular Mg2+ lodges inside the NMDA pore at hyperpolarized or resting potentials and is repelled outward only upon membrane depolarization."
                    },
                    {
                        "id": 13022, "question_number": 2,
                        "question": "Which presynaptic calcium sensor protein triggers vesicle fusion upon binding Ca2+?",
                        "options": {"A": "Synaptotagmin-1", "B": "Syntaxin-1", "C": "Synaptobrevin", "D": "SNAP-25"},
                        "correct_option": "A",
                        "explanation": "Synaptotagmin-1 acts as the calcium sensor that triggers instantaneous SNARE pin-zippering and vesicle exocytosis."
                    },
                    {
                        "id": 13023, "question_number": 3,
                        "question": "What is the major inhibitory neurotransmitter in the adult mammalian cerebral cortex?",
                        "options": {"A": "Glutamate", "B": "Gamma-Aminobutyric Acid (GABA)", "C": "Dopamine", "D": "Acetylcholine"},
                        "correct_option": "B",
                        "explanation": "GABA is the primary inhibitory neurotransmitter in the brain, activating ionotropic GABA_A receptors that conduct hyperpolarizing Cl- currents."
                    },
                    {
                        "id": 13024, "question_number": 4,
                        "question": "Which enzyme becomes autophosphorylated and serves as a molecular memory switch during Long-Term Potentiation (LTP)?",
                        "options": {"A": "Protein Kinase A (PKA)", "B": "CaMKII (Calcium/Calmodulin-Dependent Protein Kinase II)", "C": "Glycogen Synthase Kinase 3", "D": "DNA Polymerase III"},
                        "correct_option": "B",
                        "explanation": "CaMKII autophosphorylates at Thr286 following NMDA-mediated Ca2+ influx, maintaining autonomous catalytic activity essential for LTP maintenance."
                    },
                    {
                        "id": 13025, "question_number": 5,
                        "question": "What cellular event directly causes the expression of early Long-Term Potentiation (LTP) at the postsynaptic membrane?",
                        "options": {"A": "Endocytosis and removal of AMPA receptors", "B": "Insertion of additional AMPA receptors into the postsynaptic density", "C": "Permanent block of presynaptic calcium channels", "D": "Degradation of dendritic spines"},
                        "correct_option": "B",
                        "explanation": "Early LTP expression involves rapid exocytosis and recruitment of AMPA receptors into the postsynaptic density, increasing synaptic responsiveness."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1302",
                        "subject": "Neuroscience & Brain Mapping",
                        "chapter": "Synaptic Neurotransmission & Long-Term Potentiation (LTP)",
                        "topic": "SNARE Vesicle Fusion and Long-Term Potentiation",
                        "keywords": ["synapse", "neurotransmitter", "SNARE complex", "glutamate", "GABA", "NMDA receptor", "LTP", "CaMKII"],
                        "definition": "Synaptic neurotransmission is the chemical signaling across synaptic clefts mediated by vesicular exocytosis, with synaptic strength dynamically modulated by LTP.",
                        "formula_or_core_rule": "NMDA Coincidence Detection: Glutamate binding + Depolarization expels Mg2+ -> Ca2+ influx -> CaMKII activation -> AMPA insertion",
                        "worked_example": "High-frequency 100 Hz tetanic stimulation depolarizes the dendritic spine, clearing the Mg2+ block from NMDA receptors to induce LTP.",
                        "content": "Action potentials trigger presynaptic Ca2+ entry, activating synaptotagmin and the SNARE complex (synaptobrevin, syntaxin, SNAP-25) to release neurotransmitters. Glutamate activates AMPA receptors for fast excitation and NMDA receptors upon Mg2+ expulsion. Intracellular Ca2+ entry triggers CaMKII phosphorylation and AMPA receptor insertion, establishing Long-Term Potentiation."
                    }
                ]
            ),
            make_ch(
                1303, 3, "Brain Mapping Technologies: fMRI, EEG & Connectomics", 25,
                "BOLD contrast mechanism, spatial vs temporal resolution, diffusion MRI tractography, and neural decoding.",
                ["Blood-Oxygen-Level-Dependent (BOLD) contrast in functional MRI", "Electroencephalography (EEG): Evoked potentials and spectral frequency bands (alpha, beta, gamma)", "Diffusion Tensor Imaging (DTI): Fractional anisotropy and white matter tractography", "Connectomics: Graph theory metrics (clustering coefficient, path length, hubs)"],
                ["fMRI BOLD Contrast: Deoxyhemoglobin is paramagnetic, Oxyhemoglobin is diamagnetic", "Diffusion Tensor: Fractional Anisotropy (FA) ranges from 0 (isotropic) to 1 (linear)", "EEG Power Spectral Density: S(f) = lim (1/T) * |X(f)|^2", "Network Small-Worldness: S = (C / C_rand) / (L / L_rand) > 1"],
                "Functional Magnetic Resonance Imaging (fMRI) & BOLD Mechanism",
                "Non-invasive brain mapping measures macroscopic neural network activity. Functional Magnetic Resonance Imaging (fMRI) leverages the Blood-Oxygen-Level-Dependent (BOLD) contrast effect. Oxyhemoglobin is diamagnetic and has little effect on surrounding magnetic fields, whereas deoxyhemoglobin is paramagnetic, causing microscopic magnetic field distortions and rapid dephasing of proton spins (lowering T2* signal). When neurons fire, local neurovascular coupling overcompensates with an influx of oxygenated arterial blood (hyperemia), flushing out deoxyhemoglobin and producing a positive local T2*-weighted MRI signal increase.",
                "Structural Connectomics & Graph Theoretical Analysis",
                "While fMRI maps functional dynamics with millimeter spatial resolution, Diffusion Tensor Imaging (DTI) maps anatomical white matter connectivity by measuring anisotropic water diffusion along aligned axonal bundles. Fractional anisotropy (FA) quantifies directional water diffusion, enabling algorithmic tractography of major brain pathways like the corpus callosum and arcuate fasciculus. The human connectome is analyzed through graph theory, representing brain regions as nodes and white matter tracts or functional correlations as edges. Human brain networks demonstrate 'small-world' topology: high local clustering combined with short average path lengths, supported by densely interconnected 'rich-club' hubs that optimize information integration.",
                "fMRI measures hemodynamic changes associated with neural activity (BOLD effect) with high spatial resolution (mm) but low temporal resolution (seconds), whereas EEG directly records electrical currents with millisecond temporal precision but coarse spatial localization.",
                [
                    {
                        "id": 13031, "question_number": 1,
                        "question": "What is the physical basis of the Blood-Oxygen-Level-Dependent (BOLD) signal in fMRI?",
                        "options": {"A": "Radioactive decay of fluorine-18 tracers", "B": "The magnetic difference between diamagnetic oxyhemoglobin and paramagnetic deoxyhemoglobin", "C": "Direct voltage measurement from cortical pyramidal cells", "D": "Infrared absorption of cytochrome c oxidase"},
                        "correct_option": "B",
                        "explanation": "Paramagnetic deoxyhemoglobin distorts local magnetic fields; neural activity triggers arteriolar dilation that washes out deoxyhemoglobin, increasing T2* signal."
                    },
                    {
                        "id": 13032, "question_number": 2,
                        "question": "Which neuroimaging modality provides the highest temporal resolution (millisecond level)?",
                        "options": {"A": "Functional Magnetic Resonance Imaging (fMRI)", "B": "Electroencephalography (EEG)", "C": "Positron Emission Tomography (PET)", "D": "Computed Tomography (CT)"},
                        "correct_option": "B",
                        "explanation": "EEG directly detects postsynaptic electrical dipoles at the scalp with sub-millisecond temporal resolution."
                    },
                    {
                        "id": 13033, "question_number": 3,
                        "question": "What does Fractional Anisotropy (FA) measured in Diffusion Tensor Imaging (DTI) reflect?",
                        "options": {"A": "The degree of directional restriction of water molecule diffusion along axonal bundles", "B": "The concentration of glucose metabolism in astrocytes", "C": "The electrical impedance of the scalp skull interface", "D": "The rate of cerebral spinal fluid turnover"},
                        "correct_option": "A",
                        "explanation": "FA quantifies how strongly directional water diffusion is; highly organized, myelinated white matter tracts exhibit high FA values near 1."
                    },
                    {
                        "id": 13034, "question_number": 4,
                        "question": "In brain network connectomics, what defines a 'small-world' network architecture?",
                        "options": {"A": "Zero clustering and infinite path length", "B": "High local clustering coefficient and short characteristic path length", "C": "Completely random connectivity with no organizational hubs", "D": "Strictly nearest-neighbor 2D grid connectivity"},
                        "correct_option": "B",
                        "explanation": "Small-world networks balance efficient local specialized processing (high clustering) with rapid distributed global integration (short path length)."
                    },
                    {
                        "id": 13035, "question_number": 5,
                        "question": "Which EEG frequency rhythm (8 to 12 Hz) typically dominates the posterior occipital cortex when an awake subject closes their eyes?",
                        "options": {"A": "Delta rhythm (0.5 - 4 Hz)", "B": "Theta rhythm (4 - 8 Hz)", "C": "Alpha rhythm (8 - 12 Hz)", "D": "Gamma rhythm (30 - 80 Hz)"},
                        "correct_option": "C",
                        "explanation": "Alpha waves (8-12 Hz) originate over the occipital cortex during relaxed wakefulness with eyes closed and attenuate upon opening eyes."
                    }
                ],
                [
                    {
                        "chunk_id": "RC_1303",
                        "subject": "Neuroscience & Brain Mapping",
                        "chapter": "Brain Mapping Technologies: fMRI, EEG & Connectomics",
                        "topic": "Neuroimaging and Connectomics",
                        "keywords": ["fMRI", "BOLD", "EEG", "DTI", "diffusion tensor imaging", "connectomics", "small-world network"],
                        "definition": "Brain mapping combines functional hemodynamic imaging (fMRI), high-speed electrophysiology (EEG), and tractography (DTI) to decode complex anatomical and functional neural networks.",
                        "formula_or_core_rule": "BOLD contrast: oxyhemoglobin = diamagnetic, deoxyhemoglobin = paramagnetic; Small-worldness: S = (C/C_rand)/(L/L_rand) > 1",
                        "worked_example": "Visual stimulation triggers a 2% local BOLD signal increase in primary visual cortex V1 within 4 to 6 seconds following stimulus onset.",
                        "content": "Functional MRI monitors hemodynamics via the BOLD effect, leveraging magnetic differences between oxygenated and deoxygenated hemoglobin. EEG captures millisecond electrical dipoles across frequency bands. DTI reconstructs white matter tractography using fractional water diffusion anisotropy, enabling graph-theoretical analysis of the human connectome."
                    }
                ]
            )
        ]
    })

    return subs

if __name__ == "__main__":
    subs = get_subjects_16_to_20()
    print(f"Generated {len(subs)} subjects:")
    for s in subs:
        print(f" - {s['subject_id']}: {s['name']} ({s['id']}) - {len(s['chapters'])} chapters, {sum(len(c['mcqs']) for c in s['chapters'])} MCQs")

