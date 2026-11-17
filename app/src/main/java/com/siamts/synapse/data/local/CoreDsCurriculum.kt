package com.siamts.synapse.data.local

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Embedded university-grade curriculum for Data Science.
 * 8 progressive college chapters with complete lessons, MCQs, and RAG knowledge chunks.
 */
object CoreDsCurriculum {

    fun getDsSubject(): CatalogSubject {
        return CatalogSubject(
            id = "DS",
            subjectId = 3,
            name = "Data Science",
            slug = "data-science",
            iconSymbol = "📈",
            colorHex = "#8B5CF6",
            category = "Data & Analytics",
            description = "University data science covering wrangling, inferential statistics, visualization, regression, classification, PCA, time series, and MLOps.",
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
            title = "Data Wrangling & Exploratory Analysis",
            estimatedMinutes = 25,
            summary = "Tidy data principles, missing data imputation, outlier detection, data normalization, pandas transformations, and exploratory statistical summaries.",
            syllabus = listOf("Tidy Data Principles & Wide-to-Long Melting/Pivoting", "Missing Data Mechanisms: MCAR, MAR, MNAR & Imputation Techniques", "Outlier Detection: Tukey IQR Fences & Z-Score Robust Thresholds", "Feature Scaling: Min-Max, Z-Score Standardisation & Robust Scaler", "Exploratory Analysis: Skewness, Kurtosis & Bivariate Correlation Analysis"),
            coreFormulas = listOf("Z-Score: z = (x - mu) / sigma", "Min-Max Normalization: x_norm = (x - x_min) / (x_max - x_min)", "Tukey IQR Rule: Q1 - 1.5*IQR <= x <= Q3 + 1.5*IQR, where IQR = Q3 - Q1", "Sample Pearson Correlation: r = Sum((x_i - mean(x))*(y_i - mean(y))) / (sqrt(Sum(x_i - mean(x))^2) * sqrt(Sum(y_i - mean(y))^2))", "Sample Variance: s^2 = (1 / (n - 1)) * Sum((x_i - mean(x))^2)"),
            part1Title = "Data Wrangling, Imputation & Exploratory Data Analysis",
            part1Content = "Data wrangling is the systematic process of transforming messy, unstructured, or raw datasets into structured formats suitable for algorithmic consumption. Governed by Wickham's Tidy Data paradigm, a clean dataset mandates that each variable forms a distinct column, each observation forms a unique row, and each observational unit forms a table. Handling dirty real-world datasets requires addressing structural issues, duplicate records, non-standard encoding, and irregular temporal timestamps.\n\nMissing data mechanisms dictate the mathematical validity of downstream statistical models. Missing Completely at Random (MCAR) implies missingness is independent of both observed and unobserved data, justifying listwise deletion or mean imputation. Missing at Random (MAR) denotes that missingness depends systematically on other observed variables, requiring advanced techniques like Multiple Imputation by Chained Equations (MICE) or k-Nearest Neighbors (KNN) imputation. Missing Not at Random (MNAR) indicates the probability of missingness depends directly on the unobserved value itself, demanding specialized selection or pattern-mixture models.\n\nOutlier detection protects statistical estimators from catastrophic distortion. The Interquartile Range (IQR) fence approach, formulated by John Tukey, establishes inner fences at Q1 - 1.5*IQR and Q3 + 1.5*IQR; data points beyond these boundaries represent potential anomalies. Alternatively, standard score (Z-score) filtering flags records exceeding 3 standard deviations under normal distributional assumptions. When normality is violated, robust metrics such as the Median Absolute Deviation (MAD) prevent masking and swamping effects.\n\nExploratory Data Analysis (EDA) leverages univariate and bivariate descriptive statistics to diagnose data topology. Pearson product-moment correlation quantifies linear relationships between continuous variables, while Spearman rank correlation measures monotonic associations resilient to extreme values. Evaluating skewness and kurtosis determines whether log, square root, or Box-Cox power transformations are necessary to stabilize variance prior to parametric modeling.",
            part2Title = "Wrangling Formulations, Worked Python Pipeline & Real-World Use",
            part2Content = "Exact Equations & Algorithmic Rules:\n• Min-Max Scaling: x_scaled = (x - min(X)) / (max(X) - min(X)) [scales to interval [0, 1]]\n• Z-Score Standardisation: z = (x - mu) / sigma [yields zero mean and unit variance]\n• Median Absolute Deviation: MAD = median(|x_i - median(X)|), Robust Z = 0.6745 * (x - median(X)) / MAD\n\nWorked Data Pipeline Example:\nProblem: Given a dataset vector X = [12, 14, 15, 18, 19, 21, 85], identify outliers using Tukey IQR and compute the Min-Max normalized value of x = 21.\nStep 1: Sort data: [12, 14, 15, 18, 19, 21, 85]. Median = 18.\nStep 2: Lower half = [12, 14, 15] => Q1 = 14. Upper half = [19, 21, 85] => Q3 = 21.\nStep 3: Calculate IQR: IQR = Q3 - Q1 = 21 - 14 = 7.\nStep 4: Tukey Fences: Lower Fence = 14 - 1.5*(7) = 14 - 10.5 = 3.5. Upper Fence = 21 + 1.5*(7) = 21 + 10.5 = 31.5.\nStep 5: Since 85 > 31.5, x = 85 is flagged as a severe outlier.\nStep 6: Min-Max scaling for x = 21 (excluding outlier, min=12, max=21): x_norm = (21 - 12) / (21 - 12) = 9/9 = 1.0. Including outlier (min=12, max=85): x_norm = (21 - 12) / (85 - 12) = 9 / 73 = 0.123.\n\nReal-World Applications:\nIn quantitative finance, automated ETL wrangling pipelines ingest millions of tick-by-tick order book updates, imputing dropped packets via forward fill and pruning flash-crash anomalies. In healthcare informatics, EHR wrangling maps ICD-10 medical codes, sanitizes lab measurement units across hospital wards, and applies KNN imputation to patient vitals prior to training clinical risk prognosis models.",
            examTip = "University Exam Tip: Always state whether missing data is MCAR, MAR, or MNAR before selecting an imputation strategy. In viva exams, remember that Min-Max scaling is highly sensitive to outliers, whereas RobustScaler uses median and IQR to preserve distributional integrity.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4001,
                    questionNumber = 1,
                    question = "In data wrangling, what characterizes data that is Missing Completely at Random (MCAR)?",
                    options = mapOf("A" to "The probability of missingness is completely independent of both observed and unobserved variables", "B" to "Missingness depends strictly on observed predictor variables in the dataset", "C" to "The probability of missingness depends directly on the unobserved missing value itself", "D" to "Missing values follow an alternating deterministic periodic cycle"),
                    correctOption = "A",
                    explanation = "MCAR implies that the missingness mechanism is unrelated to any variable under study, whether observed or unobserved, meaning the observed data is a random sample of the complete dataset."
                ),
                QuizQuestionDto(
                    id = 4002,
                    questionNumber = 2,
                    question = "Given Q1 = 20 and Q3 = 50 for a continuous attribute, what is the upper Tukey outlier fence threshold?",
                    options = mapOf("A" to "95", "B" to "80", "C" to "105", "D" to "70"),
                    correctOption = "A",
                    explanation = "IQR = Q3 - Q1 = 50 - 20 = 30. The upper fence is Q3 + 1.5 * IQR = 50 + 1.5*(30) = 50 + 45 = 95."
                ),
                QuizQuestionDto(
                    id = 4003,
                    questionNumber = 3,
                    question = "Which scaling technique scales data to have a median of 0 and an interquartile range of 1, providing resilience against extreme outliers?",
                    options = mapOf("A" to "RobustScaler", "B" to "StandardScaler (Z-Score)", "C" to "MinMaxScaler", "D" to "Unit Vector Normalization (L2)"),
                    correctOption = "A",
                    explanation = "RobustScaler subtracts the median and divides by the interquartile range (IQR), making the transformation insensitive to extreme values unlike mean- and variance-based scalers."
                ),
                QuizQuestionDto(
                    id = 4004,
                    questionNumber = 4,
                    question = "What does a positive skewness value (skewness > 0) indicate about a frequency distribution?",
                    options = mapOf("A" to "The right tail is elongated and the mean is generally greater than the median", "B" to "The left tail is elongated and the mean is lower than the median", "C" to "The distribution is perfectly symmetric and Gaussian", "D" to "The distribution has negative excess kurtosis with heavy flat shoulders"),
                    correctOption = "A",
                    explanation = "Positive (right) skewness indicates that the long tail extends towards higher positive values, pulling the parametric mean to the right of the median."
                ),
                QuizQuestionDto(
                    id = 4005,
                    questionNumber = 5,
                    question = "Under tidy data principles formulated by Hadley Wickham, which rule must be satisfied?",
                    options = mapOf("A" to "Each variable forms a column, each observation forms a row, and each type of observational unit forms a table", "B" to "Multiple variables must be compressed into composite single column headers for space efficiency", "C" to "Missing values must be replaced by zeros immediately upon file ingestion", "D" to "All numerical features must be binned into discrete categorical intervals"),
                    correctOption = "A",
                    explanation = "Tidy data rules specify: 1. Each variable must have its own column. 2. Each observation must have its own row. 3. Each value must have its own cell."
                ),
                QuizQuestionDto(
                    id = 4006,
                    questionNumber = 6,
                    question = "If a feature X has min = 10 and max = 50, what is the Min-Max scaled value of x = 30?",
                    options = mapOf("A" to "0.50", "B" to "0.25", "C" to "0.75", "D" to "0.40"),
                    correctOption = "A",
                    explanation = "x_norm = (30 - 10) / (50 - 10) = 20 / 40 = 0.50."
                ),
                QuizQuestionDto(
                    id = 4007,
                    questionNumber = 7,
                    question = "When bivariate data exhibits a non-linear monotonic relationship with extreme outliers, which correlation coefficient is most appropriate?",
                    options = mapOf("A" to "Spearman rank correlation", "B" to "Pearson product-moment correlation", "C" to "Phi coefficient", "D" to "Point-biserial correlation"),
                    correctOption = "A",
                    explanation = "Spearman rank correlation calculates Pearson correlation on the ranks of values, effectively evaluating monotonic relationships without assuming linear proportionality or normal residuals."
                ),
                QuizQuestionDto(
                    id = 4008,
                    questionNumber = 8,
                    question = "Why is mean imputation problematic when applied to data missing not at random (MNAR)?",
                    options = mapOf("A" to "It severely underestimates variance and induces systematic estimation bias into the parameters", "B" to "It expands the sample variance artificially and causes collinearity", "C" to "It converts numerical variables into non-computable strings", "D" to "It violates the central limit theorem across large sample sizes"),
                    correctOption = "A",
                    explanation = "Mean imputation shrinks sample variance (since imputed values have zero deviation from the mean) and induces systematic bias when missingness is correlated with the outcome or unobserved values."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH1_001",
                    subject = "Data Science",
                    chapter = "Data Wrangling & Exploratory Analysis",
                    topic = "Missing Data Imputation and Tidy Data Principles",
                    keywords = listOf("data wrangling", "missing data", "MCAR", "MAR", "MNAR", "imputation", "tidy data", "outliers", "IQR", "scaling"),
                    definition = "Data wrangling encompasses data auditing, restructuring into tidy formats, handling missing mechanisms (MCAR/MAR/MNAR), outlier remediation, and scaling features for predictive modeling.",
                    formulaOrCoreRule = "Z = (X - mu)/sigma. Min-Max = (X - min)/(max - min). IQR = Q3 - Q1. Tukey Lower = Q1 - 1.5*IQR, Upper = Q3 + 1.5*IQR.",
                    workedExample = "For Q1=10, Q3=30: IQR=20. Upper fence = 30 + 1.5(20) = 60. Value 75 exceeds fence and is flagged as an outlier.",
                    content = "Imputation strategies depend on missingness topology: simple mean/median for MCAR, KNN or iterative MICE for MAR, and selection modeling for MNAR to prevent parametric estimation bias."
                )
            )
        )
    }

    private fun createChapter2(): CatalogChapter {
        return CatalogChapter(
            chapterId = 2,
            chapterOrder = 2,
            title = "Statistical Inference & Hypothesis Testing",
            estimatedMinutes = 30,
            summary = "Sampling distributions, Central Limit Theorem, confidence intervals, null hypothesis significance testing, Student t-test, ANOVA, and Chi-Square tests of independence.",
            syllabus = listOf("Central Limit Theorem & Sampling Error Distributions", "Point Estimation, Bias & Maximum Likelihood Estimation (MLE)", "Confidence Intervals: Z-Interval & Student's t-Interval", "Null Hypothesis Testing: Type I and Type II Errors & Statistical Power", "Parametric & Non-Parametric Tests: t-Test, One-Way ANOVA & Chi-Square"),
            coreFormulas = listOf("Central Limit Theorem Standard Error: SE = sigma / sqrt(n)", "Z-Statistic: z = (mean(x) - mu_0) / (sigma / sqrt(n))", "Student's t-Statistic: t = (mean(x) - mu_0) / (s / sqrt(n)) with df = n - 1", "Confidence Interval: CI = mean(x) +/- t_crit * (s / sqrt(n))", "Chi-Square Test Statistic: Chi^2 = Sum((O_i - E_i)^2 / E_i)"),
            part1Title = "Inferential Theory, Estimators & Hypothesis Testing Frameworks",
            part1Content = "Statistical inference provides the mathematical machinery to draw generalizable conclusions about population parameters from finite, stochastic sample data. Central to frequentist inference is the Central Limit Theorem (CLT), which states that the distribution of sample means approaches a normal distribution as sample size n grows large (n >= 30), regardless of the underlying population distribution, with mean equal to population mean mu and standard error SE = sigma / sqrt(n).\n\nPoint estimators evaluate population parameters through sample statistics. An estimator is unbiased if its expected value equals the true parameter value (E[theta_hat] = theta), and consistent if it converges in probability to the true parameter as sample size approaches infinity. Maximum Likelihood Estimation (MLE) seeks parameter values theta that maximize the likelihood function L(theta; X) = Prod(f(x_i; theta)), commonly executed by maximizing the log-likelihood to convert multiplicative joint densities into tractable additive summations.\n\nNull Hypothesis Significance Testing (NHST) formalizes scientific refutation. The null hypothesis H0 posits no effect or equality, pitted against the alternative hypothesis H1. Significance level alpha specifies the probability of a Type I error (rejecting H0 when true), while beta represents the Type II error rate (failing to reject H0 when false). The statistical power (1 - beta) denotes the probability of correctly rejecting a false null hypothesis, driven by effect size, sample size, and variance.\n\nP-values quantify the probability of observing a test statistic as extreme as, or more extreme than, the sample outcome under the assumption that H0 holds. When testing differences across more than two groups, Student's t-test inflation is mitigated by One-Way Analysis of Variance (ANOVA), which partitions total sum of squares into between-group and within-group variance components evaluated via Fisher's F-distribution.",
            part2Title = "Exact Test Formulas, Step-by-Step Worked Hypothesis & Applications",
            part2Content = "Exact Equations & Decision Rules:\n• One-sample t-statistic: t = (x_bar - mu_0) / (s / sqrt(n)), df = n - 1\n• Two-sample pooled t-statistic: t = (x_bar1 - x_bar2) / (s_p * sqrt(1/n1 + 1/n2))\n• ANOVA F-statistic: F = MS_between / MS_within = (SS_between / (k - 1)) / (SS_within / (N - k))\n• Chi-Square Independence: Chi^2 = Sum((O - E)^2 / E) with df = (rows - 1)*(cols - 1)\n\nWorked Hypothesis Testing Example:\nProblem: An automated ML query cache claims an average latency of mu_0 = 100 ms. A sample of n = 25 queries yields sample mean x_bar = 108 ms and sample standard deviation s = 15 ms. Test at alpha = 0.05 whether the latency is significantly greater than 100 ms.\nStep 1: Hypotheses: H0: mu <= 100 ms vs H1: mu > 100 ms (One-tailed upper test).\nStep 2: Degrees of freedom: df = 25 - 1 = 24. For alpha = 0.05 and df = 24, t_critical = 1.711.\nStep 3: Standard error: SE = s / sqrt(n) = 15 / sqrt(25) = 15 / 5 = 3.0 ms.\nStep 4: Compute test statistic: t_calc = (108 - 100) / 3.0 = 8 / 3.0 = 2.67.\nStep 5: Decision: Since t_calc = 2.67 > t_critical = 1.711, we reject H0 at alpha = 0.05.\nStep 6: Conclusion: Latency is statistically significantly greater than the 100 ms baseline (p < 0.01).\n\nReal-World Applications:\nIn web engineering and digital commerce, A/B testing platforms deploy hypothesis testing to evaluate conversion rate lift between UI layouts, controlling false discovery rates via Bonferroni or Benjamini-Hochberg adjustments. In clinical pharmacology, randomized controlled trials (RCTs) enforce two-tailed t-tests and ANOVA to validate pharmaceutical efficacy against placebo controls.",
            examTip = "University Exam Tip: Clarify whether the population variance sigma is known (use Z-test) or unknown (use Student t-test). In viva exams, clearly articulate that failing to reject H0 does not prove H0 is true; it merely indicates insufficient empirical evidence to falsify it.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4009,
                    questionNumber = 1,
                    question = "What is the standard error of the mean for a sample of size n = 64 drawn from a population with standard deviation sigma = 16?",
                    options = mapOf("A" to "2.0", "B" to "4.0", "C" to "0.25", "D" to "8.0"),
                    correctOption = "A",
                    explanation = "SE = sigma / sqrt(n) = 16 / sqrt(64) = 16 / 8 = 2.0."
                ),
                QuizQuestionDto(
                    id = 4010,
                    questionNumber = 2,
                    question = "In statistical hypothesis testing, committing a Type I error corresponds to which scenario?",
                    options = mapOf("A" to "Rejecting a true null hypothesis (false positive)", "B" to "Failing to reject a false null hypothesis (false negative)", "C" to "Accepting the alternative hypothesis when the sample size is underpowered", "D" to "Miscalculating the degrees of freedom in an F-distribution"),
                    correctOption = "A",
                    explanation = "A Type I error occurs when the null hypothesis H0 is actually true, but our test statistic leads us to incorrectly reject it (probability alpha)."
                ),
                QuizQuestionDto(
                    id = 4011,
                    questionNumber = 3,
                    question = "Which parametric test is used to compare the means of three or more independent groups simultaneously without inflating the Type I error rate?",
                    options = mapOf("A" to "One-Way ANOVA (Analysis of Variance)", "B" to "Repeated Student t-tests", "C" to "Mann-Whitney U Test", "D" to "Pearson Chi-Square test"),
                    correctOption = "A",
                    explanation = "ANOVA compares the ratio of between-group variance to within-group variance (F-statistic) across k groups, avoiding the alpha-inflation caused by conducting multiple pairwise t-tests."
                ),
                QuizQuestionDto(
                    id = 4012,
                    questionNumber = 4,
                    question = "What happens to the width of a 95% confidence interval for a population mean as the sample size n increases?",
                    options = mapOf("A" to "The interval narrows because standard error decreases proportionally to 1/sqrt(n)", "B" to "The interval widens due to increased degrees of freedom", "C" to "The interval remains constant because the 95% confidence level is fixed", "D" to "The interval expands asymptotically towards infinity"),
                    correctOption = "A",
                    explanation = "Margin of error is t_crit * (s / sqrt(n)). As n increases, the denominator sqrt(n) increases, decreasing standard error and narrowing the confidence interval."
                ),
                QuizQuestionDto(
                    id = 4013,
                    questionNumber = 5,
                    question = "In a Chi-Square test of independence with a 3 x 4 contingency table, what are the degrees of freedom?",
                    options = mapOf("A" to "6", "B" to "12", "C" to "7", "D" to "11"),
                    correctOption = "A",
                    explanation = "Degrees of freedom for a contingency table: df = (r - 1) * (c - 1) = (3 - 1) * (4 - 1) = 2 * 3 = 6."
                ),
                QuizQuestionDto(
                    id = 4014,
                    questionNumber = 6,
                    question = "What is the definition of statistical power in the context of hypothesis testing?",
                    options = mapOf("A" to "1 - beta (the probability of correctly rejecting a false null hypothesis)", "B" to "1 - alpha (the confidence coefficient)", "C" to "The p-value multiplied by the sample size", "D" to "The ratio of between-group variance to total variance"),
                    correctOption = "A",
                    explanation = "Statistical power is 1 - beta, representing the probability that a statistical test will detect an effect when there truly is an effect to be detected."
                ),
                QuizQuestionDto(
                    id = 4015,
                    questionNumber = 7,
                    question = "Which principle guarantees that Maximum Likelihood Estimators (MLE) are asymptotically efficient and normally distributed under regularity conditions?",
                    options = mapOf("A" to "Asymptotic Normality of MLE (Cramer-Rao Lower Bound attainment)", "B" to "Markov Inequality theorem", "C" to "Bayes' Optimal Classifier theorem", "D" to "Gauss-Markov Theorem"),
                    correctOption = "A",
                    explanation = "Under mild regularity conditions, as n -> infinity, MLE estimators are consistent, asymptotically normal, and achieve the minimum variance given by the Cramer-Rao Lower Bound."
                ),
                QuizQuestionDto(
                    id = 4016,
                    questionNumber = 8,
                    question = "When a p-value is 0.015 and the pre-specified significance threshold is alpha = 0.05, what is the proper statistical decision?",
                    options = mapOf("A" to "Reject H0 because p < alpha", "B" to "Fail to reject H0 because p > 0.01", "C" to "Accept H0 and discard the alternative hypothesis", "D" to "Increase the sample size until p equals alpha"),
                    correctOption = "A",
                    explanation = "When p-value <= alpha, the observed outcome is sufficiently unlikely under H0 to justify rejecting the null hypothesis in favor of H1."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH2_001",
                    subject = "Data Science",
                    chapter = "Statistical Inference & Hypothesis Testing",
                    topic = "Central Limit Theorem and Hypothesis Testing",
                    keywords = listOf("statistical inference", "CLT", "p-value", "t-test", "ANOVA", "chi-square", "type I error", "type II error", "confidence interval"),
                    definition = "Statistical inference uses sampling distributions, Central Limit Theorem bounds, and hypothesis tests (t-test, ANOVA, Chi-Square) to make probabilistic statements about population parameters.",
                    formulaOrCoreRule = "SE = s / sqrt(n). t = (x_bar - mu_0) / SE. F = MS_between / MS_within. Chi^2 = Sum((O - E)^2 / E).",
                    workedExample = "For n=25, x_bar=108, mu_0=100, s=15: SE = 15/5 = 3. t = (108 - 100)/3 = 2.67. For df=24, t_crit=1.711. Since 2.67 > 1.711, H0 is rejected at alpha=0.05.",
                    content = "Hypothesis testing balances Type I false positive risk (alpha) against Type II false negative risk (beta), with statistical power (1 - beta) maximizing the probability of true discovery."
                )
            )
        )
    }

    private fun createChapter3(): CatalogChapter {
        return CatalogChapter(
            chapterId = 3,
            chapterOrder = 3,
            title = "Data Visualization Principles",
            estimatedMinutes = 25,
            summary = "Visual encoding principles, Gestalt theory, grammar of graphics, Matplotlib/Seaborn architectural layers, and interactive dashboard analytics.",
            syllabus = listOf("Bertin's Semiology of Graphics & Visual Encoding Channels", "Gestalt Psychology in Information Architecture & Chart Design", "Wilkinson's Grammar of Graphics (ggplot2 & Vega-Lite Concepts)", "Statistical Plot Types: Box Plots, Violin Plots, Heatmaps & Pairplots", "Cognitive Load Optimization & Color Accessibility (Colorblind-Safe Palettes)"),
            coreFormulas = listOf("Lie Factor = (Size of effect shown in graphic) / (Size of effect in data)", "Data-Ink Ratio = (Data-Ink) / (Total Ink used in graphic)", "Weber-Fechner Law of Perception: delta_S = k * (delta_I / I)", "Stevens' Power Law for Perceptual Magnitude: S = k * I^a", "Color Contrast Ratio: (L1 + 0.05) / (L2 + 0.05) >= 4.5:1 for WCAG AA"),
            part1Title = "Visual Encoding, Semiology & The Grammar of Graphics",
            part1Content = "Data visualization translates numerical and categorical abstractions into perceptual geometric representations. Rooted in Jacques Bertin's Semiology of Graphics, data features are mapped onto visual encoding channels: position, length, direction, angle, area, volume, color hue, and color saturation. William Cleveland and Robert McGill empirically established the perceptual hierarchy of these channels, proving that position along a common scale affords the lowest perceptual error, followed by length, angle, and area, with color shading ranking lowest in quantitative accuracy.\n\nGestalt principles of visual perception—proximity, similarity, continuity, closure, and figure-ground organization—govern how human cognitive systems group disparate graphic marks into holistic patterns. Incorporating Gestalt theory eliminates visual ambiguity, allowing viewers to detect clustering, trends, and anomalies effortlessly without conscious mental computation.\n\nLeland Wilkinson's Grammar of Graphics provides the formal algebraic foundation for modern statistical graphics libraries (such as ggplot2 and Plotly). The grammar deconstructs any visualization into independent composable layers: Data (tidy data frame), Aesthetic Mappings (variables bound to visual aesthetics like x, y, color), Geometric Objects (geoms: points, bars, lines), Statistical Transformations (stats: binning, smoothing), Scales (mapping data space to pixel/color space), Coordinate Systems (Cartesian, polar), and Faceting (small multiples).\n\nEdward Tufte's design philosophy champions the maximization of the Data-Ink Ratio, arguing that non-data ink (gratuitous gridlines, 3D chart junk, decorative borders) degrades communicative efficacy. Tufte's Lie Factor warns against visual distortions where the graphical representation exaggerates or minimizes the actual numeric rate of change in the underlying data.",
            part2Title = "Visualization Formulations, Design Case & Enterprise Dashboards",
            part2Content = "Exact Design Metrics & Perception Laws:\n• Data-Ink Ratio = Data-Ink / Total-Ink (target >= 0.85)\n• Lie Factor = (Graphical Effect %) / (Data Effect %) (target: 0.95 to 1.05)\n• Stevens' Law exponents: Position a = 1.0 (linear, accurate), Area a = 0.7 (underestimated), Volume a = 0.5 (severely underestimated)\n\nWorked Graphical Optimization Example:\nProblem: A corporate infographic shows sales growing from \$10M to \$20M (a 100% data increase) using a 2D dollar bill icon whose width AND height are doubled.\nStep 1: Compute true data effect: Delta_Data = (20 - 10) / 10 = 100%.\nStep 2: Compute graphical representation: Doubling width and height scales the area by 2 * 2 = 4 (a 300% graphical area increase).\nStep 3: Calculate Tufte Lie Factor: Lie Factor = Graphical Effect / Data Effect = 300% / 100% = 3.0.\nStep 4: Analysis: Because Lie Factor = 3.0 >> 1.05, the graphic severely misleads viewers by tripling visual magnitude for a doubling of revenue.\nStep 5: Remedy: Constrain scaling to a single dimension (bar length along a uniform Cartesian scale) to enforce Lie Factor = 1.0.\n\nReal-World Applications:\nIn clinical trials, Kaplan-Meier survival curves encode patient retention over time with small multiples. In financial market surveillance, real-time heatmaps display correlations across thousands of equities, utilizing diverging perceptually uniform colormaps (e.g., Viridis, Plasma) to prevent colorblind misinterpretation.",
            examTip = "University Exam Tip: Memorize Cleveland and McGill's visual channel hierarchy: Position along a common scale > Position along non-aligned scales > Length > Direction/Angle > Area > Volume > Color Saturation/Hue. Never recommend pie charts for fine quantitative comparisons.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4017,
                    questionNumber = 1,
                    question = "According to Cleveland and McGill's perceptual hierarchy of visual encodings, which channel yields the highest quantitative decoding accuracy?",
                    options = mapOf("A" to "Position along a common aligned scale", "B" to "Area of geometric circles or polygons", "C" to "Color saturation and hue brightness", "D" to "Angle and slope orientation"),
                    correctOption = "A",
                    explanation = "Human visual perception decodes position along a common aligned scale with the lowest perceptual error, vastly outperforming area, angle, or color."
                ),
                QuizQuestionDto(
                    id = 4018,
                    questionNumber = 2,
                    question = "If a chart depicts a 25% change in underlying data as a 75% change in physical graphic dimension, what is the Tufte Lie Factor?",
                    options = mapOf("A" to "3.0", "B" to "0.33", "C" to "1.5", "D" to "2.0"),
                    correctOption = "A",
                    explanation = "Lie Factor = (Size of effect in graphic) / (Size of effect in data) = 75% / 25% = 3.0."
                ),
                QuizQuestionDto(
                    id = 4019,
                    questionNumber = 3,
                    question = "What is the primary objective of maximizing Edward Tufte's Data-Ink Ratio?",
                    options = mapOf("A" to "Eliminate non-data ink such as heavy grid lines and 3D chart junk to minimize cognitive clutter", "B" to "Use as much decorative ink as possible to make the graphic visually stimulating", "C" to "Ensure all chart backgrounds are shaded in dark gray", "D" to "Replace all Cartesian plots with 3D spherical projections"),
                    correctOption = "A",
                    explanation = "Maximizing the Data-Ink Ratio directs viewer focus entirely to the data points, eliminating redundant ornamentation, faux 3D effects, and unnecessary borders."
                ),
                QuizQuestionDto(
                    id = 4020,
                    questionNumber = 4,
                    question = "Which layer in Wilkinson's Grammar of Graphics is responsible for transforming raw data values into physical pixel positions or color codes?",
                    options = mapOf("A" to "Scales", "B" to "Geometries (Geoms)", "C" to "Facets", "D" to "Theme"),
                    correctOption = "A",
                    explanation = "The Scales layer maps abstract data domains to concrete perceptual aesthetic ranges (e.g., mapping continuous numbers to screen pixel coordinates or color spectrums)."
                ),
                QuizQuestionDto(
                    id = 4021,
                    questionNumber = 5,
                    question = "Why are perceptually uniform colormaps like Viridis and Cividis preferred over traditional rainbow (jet) colormaps?",
                    options = mapOf("A" to "They maintain monotonically increasing luminance, preventing false perceptual boundaries and supporting color-blind viewers", "B" to "They display fewer overall colors to reduce rendering GPU memory", "C" to "They automatically convert continuous data into categorical integers", "D" to "They eliminate the need for axis labeling"),
                    correctOption = "A",
                    explanation = "Rainbow maps exhibit non-linear luminance spikes (especially in yellow) that fool human eyes into seeing false boundaries. Viridis has linear perceptual luminance and is accessible to red-green color-blind users."
                ),
                QuizQuestionDto(
                    id = 4022,
                    questionNumber = 6,
                    question = "Which visualization type is best suited for simultaneously inspecting the median, interquartile range, and kernel density distribution of a continuous feature across categories?",
                    options = mapOf("A" to "Violin Plot", "B" to "Pie Chart", "C" to "Stacked Bar Chart", "D" to "Radar Chart"),
                    correctOption = "A",
                    explanation = "A violin plot combines a standard box plot (showing median and IQR) with a mirrored kernel density estimate (KDE) to visualize multi-modal distributions across categories."
                ),
                QuizQuestionDto(
                    id = 4023,
                    questionNumber = 7,
                    question = "Which Gestalt principle states that visual elements enclosed within a common bordered boundary are perceived as belonging to a unified group?",
                    options = mapOf("A" to "Common Region", "B" to "Continuity", "C" to "Symmetry", "D" to "Proximity"),
                    correctOption = "A",
                    explanation = "The Gestalt Law of Common Region states that elements located within the same bounded enclosure are automatically perceived as related."
                ),
                QuizQuestionDto(
                    id = 4024,
                    questionNumber = 8,
                    question = "Under Stevens' Power Law S = k * I^a, why do circles of varying area lead to perceptual underestimation compared to bar lengths?",
                    options = mapOf("A" to "Perceived area has an exponent a approximately 0.7, causing humans to mentally compress large circles", "B" to "Perceived area has an exponent a > 1.5, causing visual overexpansion", "C" to "Human eyes cannot perceive curved boundaries in print media", "D" to "Circle radius scales inversely with perimeter"),
                    correctOption = "A",
                    explanation = "Perceived area has an exponent around 0.7 to 0.8, meaning increases in true geometric area are systematically underestimated by human observers (Flannery's compensation)."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH3_001",
                    subject = "Data Science",
                    chapter = "Data Visualization Principles",
                    topic = "Grammar of Graphics and Cognitive Visualization Principles",
                    keywords = listOf("visualization", "grammar of graphics", "data-ink ratio", "lie factor", "Cleveland McGill", "Gestalt", "Viridis", "violin plot"),
                    definition = "Data visualization translates numerical distributions into visual encodings governed by Cleveland's perceptual hierarchy, Wilkinson's Grammar of Graphics, and Tufte's data-ink principles.",
                    formulaOrCoreRule = "Data-Ink Ratio = Data Ink / Total Ink. Lie Factor = Graphical Effect % / Data Effect %. Stevens Law: S = k * I^a.",
                    workedExample = "If data increases 100% but 2D area scales by 4x (300% increase), Lie Factor = 300 / 100 = 3.0, representing severe graphical distortion.",
                    content = "Modern statistical graphics prioritize high-fidelity encodings (aligned Cartesian position over area/angles) and perceptually uniform palettes (Viridis) to prevent misinterpretation."
                )
            )
        )
    }

    private fun createChapter4(): CatalogChapter {
        return CatalogChapter(
            chapterId = 4,
            chapterOrder = 4,
            title = "Regression Modeling",
            estimatedMinutes = 30,
            summary = "Ordinary Least Squares (OLS), Gauss-Markov theorem, multicollinearity (VIF), regularization (Ridge, Lasso, ElasticNet), and generalized linear models.",
            syllabus = listOf("Ordinary Least Squares (OLS) Derivation & Matrix Normal Equations", "Gauss-Markov Assumptions: Homoscedasticity & BLUE Property", "Model Diagnostics: Residual Analysis, R-squared & Adjusted R-squared", "Multicollinearity Diagnostics: Variance Inflation Factor (VIF)", "Regularization: Ridge (L2), Lasso (L1) & ElasticNet Penalty Optimization"),
            coreFormulas = listOf("Normal Equation: beta = (X^T * X)^(-1) * X^T * y", "Coefficient of Determination: R^2 = 1 - (SS_res / SS_tot)", "Adjusted R^2: R^2_adj = 1 - [(1 - R^2)*(n - 1) / (n - p - 1)]", "Variance Inflation Factor: VIF_j = 1 / (1 - R^2_j)", "Ridge Loss: L_ridge = ||y - X*beta||^2 + lambda * ||beta||_2^2", "Lasso Loss: L_lasso = ||y - X*beta||^2 + lambda * ||beta||_1"),
            part1Title = "Linear Regression, Gauss-Markov Theorem & Regularization",
            part1Content = "Regression modeling analyzes the conditional expectation of a continuous target variable given a set of explanatory predictors. Ordinary Least Squares (OLS) minimizes the sum of squared vertical residuals between observed target values y and model predictions X*beta. The closed-form analytical solution is derived via vector calculus, yielding the Normal Equations beta_hat = (X^T * X)^(-1) * X^T * y, where X is the design matrix including a column of ones for intercept.\n\nThe Gauss-Markov Theorem establishes that if the linear regression model satisfies five core assumptions—linearity in parameters, strict exogeneity (E[epsilon|X] = 0), no perfect multicollinearity (rank(X) = p + 1), spherical errors (homoscedasticity Var(epsilon|X) = sigma^2 * I), and no autocorrelation—the OLS estimator beta_hat is the Best Linear Unbiased Estimator (BLUE), possessing minimum variance among all linear unbiased estimators.\n\nViolations of classical assumptions require diagnostic intervention. Heteroscedasticity (non-constant error variance) is detected using Breusch-Pagan or White tests, corrected via White-Huber heteroscedasticity-consistent robust standard errors or Generalized Least Squares (GLS). Severe multicollinearity among predictors inflates parameter standard errors, quantified by the Variance Inflation Factor (VIF); VIF values exceeding 5 to 10 indicate problematic linear dependencies.\n\nWhen predictors are correlated or exceed the number of observations (p > n), OLS suffers from high variance and overfitting. Regularization penalizes model complexity: Ridge Regression adds an L2-norm penalty lambda*||beta||_2^2, shrinking coefficients smoothly and stabilizing matrix inversion; Lasso Regression applies an L1-norm penalty lambda*||beta||_1, driving non-essential coefficients exactly to zero to perform intrinsic feature selection; ElasticNet balances both penalties.",
            part2Title = "Mathematical Derivations, Worked Numerical Regression & Use Cases",
            part2Content = "Exact Equations & Optimization Targets:\n• OLS Solution: beta_hat = (X^T * X)^(-1) * X^T * y\n• Ridge Closed Form: beta_ridge = (X^T * X + lambda * I)^(-1) * X^T * y\n• Lasso Objective: min_beta [ (1/2n)*||y - X*beta||^2 + lambda*Sum(|beta_j|) ]\n• VIF: VIF_j = 1 / (1 - R_j^2)\n\nWorked Numerical Regression Example:\nProblem: Fit a simple linear regression line y = beta_0 + beta_1*x to points (1, 2), (2, 3), (3, 5), (4, 7), (5, 8). Compute beta_1, beta_0, and R^2.\nStep 1: Compute summary statistics:\nn = 5, Sum(x) = 15 => x_bar = 3. Sum(y) = 25 => y_bar = 5.\nSum(x^2) = 1 + 4 + 9 + 16 + 25 = 55.\nSum(x*y) = (1*2) + (2*3) + (3*5) + (4*7) + (5*8) = 2 + 6 + 15 + 28 + 40 = 91.\nStep 2: Slope beta_1: beta_1 = (Sum(xy) - n*x_bar*y_bar) / (Sum(x^2) - n*x_bar^2) = (91 - 5*3*5) / (55 - 5*9) = (91 - 75) / (55 - 45) = 16 / 10 = 1.6.\nStep 3: Intercept beta_0: beta_0 = y_bar - beta_1*x_bar = 5 - (1.6 * 3) = 5 - 4.8 = 0.2.\nFitted Model: y_hat = 0.2 + 1.6*x.\nStep 4: Compute SS_tot and SS_res:\nObserved y: [2, 3, 5, 7, 8]. Predicted y_hat: [1.8, 3.4, 5.0, 6.6, 8.2].\nResiduals (y - y_hat): [0.2, -0.4, 0.0, 0.4, -0.2].\nSS_res = 0.04 + 0.16 + 0.0 + 0.16 + 0.04 = 0.40.\nSS_tot = (2-5)^2 + (3-5)^2 + (5-5)^2 + (7-5)^2 + (8-5)^2 = 9 + 4 + 0 + 4 + 9 = 26.0.\nStep 5: R^2 = 1 - (SS_res / SS_tot) = 1 - (0.40 / 26.0) = 1 - 0.0154 = 0.9846 (98.46% variance explained).\n\nReal-World Applications:\nIn macroeconomics, vector autoregression and linear models estimate interest rate impacts on GDP growth. In real estate pricing engines (e.g., Zillow Zestimate), regularized regression establishes baseline property valuations incorporating thousands of geographic and architectural features without collinearity breakdown.",
            examTip = "University Exam Tip: Explain why Lasso sets coefficients to zero while Ridge does not: the L1 diamond constraint has sharp vertices on the coordinate axes where the elliptical OLS contours intersect first. In viva exams, remember that R^2 never decreases when adding variables, which is why Adjusted R^2 is required.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4025,
                    questionNumber = 1,
                    question = "What does the Gauss-Markov theorem state regarding the Ordinary Least Squares (OLS) estimator?",
                    options = mapOf("A" to "Under classical assumptions, OLS is the Best Linear Unbiased Estimator (BLUE)", "B" to "OLS is always non-linear and guarantees zero training error", "C" to "OLS is robust against high multicollinearity and outliers", "D" to "OLS automatically performs L1 feature selection"),
                    correctOption = "A",
                    explanation = "The Gauss-Markov theorem proves that among all linear unbiased estimators, the OLS estimator possesses the minimum variance (BLUE: Best Linear Unbiased Estimator)."
                ),
                QuizQuestionDto(
                    id = 4026,
                    questionNumber = 2,
                    question = "What is the analytical closed-form solution for the OLS coefficient vector beta in matrix notation?",
                    options = mapOf("A" to "(X^T * X)^(-1) * X^T * y", "B" to "X * (X^T * X)^(-1) * y", "C" to "(X * X^T)^(-1) * X * y", "D" to "X^T * (X * X^T)^(-1) * y"),
                    correctOption = "A",
                    explanation = "Setting the gradient of the sum of squared errors to zero yields the normal equations X^T * X * beta = X^T * y, which solves to beta = (X^T * X)^(-1) * X^T * y."
                ),
                QuizQuestionDto(
                    id = 4027,
                    questionNumber = 3,
                    question = "Why does Lasso regression (L1 regularization) produce sparse models with exact zero coefficients, unlike Ridge regression (L2)?",
                    options = mapOf("A" to "The L1 penalty constraint region has sharp corners aligned with the coordinate axes", "B" to "Lasso adds an exponential penalty to large parameters", "C" to "Lasso eliminates the intercept term from the loss function", "D" to "Lasso computes analytical matrix inverses directly"),
                    correctOption = "A",
                    explanation = "The diamond-shaped geometry of the L1 ball contains sharp vertices on the feature axes. When the elliptical OLS loss contours expand, they frequently make first contact at these corner axes where weights equal zero."
                ),
                QuizQuestionDto(
                    id = 4028,
                    questionNumber = 4,
                    question = "If a predictor feature has an R^2 of 0.90 when regressed on all other predictors, what is its Variance Inflation Factor (VIF)?",
                    options = mapOf("A" to "10.0", "B" to "1.11", "C" to "5.0", "D" to "0.10"),
                    correctOption = "A",
                    explanation = "VIF = 1 / (1 - R^2) = 1 / (1 - 0.90) = 1 / 0.10 = 10.0, indicating severe multicollinearity."
                ),
                QuizQuestionDto(
                    id = 4029,
                    questionNumber = 5,
                    question = "Why is Adjusted R-squared preferred over standard R-squared when evaluating multiple linear regression models?",
                    options = mapOf("A" to "Adjusted R-squared penalizes the addition of non-informative predictor variables", "B" to "Standard R-squared is always negative for linear models", "C" to "Adjusted R-squared eliminates heteroscedasticity in residuals", "D" to "Adjusted R-squared does not depend on sample size"),
                    correctOption = "A",
                    explanation = "Standard R^2 monotonically increases (or stays constant) whenever a new predictor is added. Adjusted R^2 penalizes model complexity via (n - 1)/(n - p - 1)."
                ),
                QuizQuestionDto(
                    id = 4030,
                    questionNumber = 6,
                    question = "In residual diagnostics, what does a funnel-shaped pattern (residuals spreading out as fitted values increase) signify?",
                    options = mapOf("A" to "Heteroscedasticity (non-constant error variance)", "B" to "Perfect multicollinearity", "C" to "Zero autocorrelation", "D" to "Normally distributed residuals"),
                    correctOption = "A",
                    explanation = "A funnel or fan shape in a residual-versus-fitted plot signifies heteroscedasticity, violating the constant variance assumption of OLS."
                ),
                QuizQuestionDto(
                    id = 4031,
                    questionNumber = 7,
                    question = "What is the effect of increasing the regularization parameter lambda in Ridge regression?",
                    options = mapOf("A" to "Bias increases while variance decreases (coefficients shrink towards zero)", "B" to "Variance increases while bias decreases (model overfits)", "C" to "All coefficients are set to exactly zero instantly", "D" to "Training mean squared error decreases monotonically"),
                    correctOption = "A",
                    explanation = "As lambda increases, penalty on coefficient magnitude increases, shrinking weights towards zero. This increases model bias but reduces parameter variance, curbing overfitting."
                ),
                QuizQuestionDto(
                    id = 4032,
                    questionNumber = 8,
                    question = "Which statistical test is commonly used to test for first-order autocorrelation in regression residuals?",
                    options = mapOf("A" to "Durbin-Watson Test", "B" to "Breusch-Pagan Test", "C" to "Kolmogorov-Smirnov Test", "D" to "Shapiro-Wilk Test"),
                    correctOption = "A",
                    explanation = "The Durbin-Watson statistic tests for first-order autocorrelation in residuals. Values near 2 indicate no autocorrelation, values < 2 indicate positive autocorrelation."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH4_001",
                    subject = "Data Science",
                    chapter = "Regression Modeling",
                    topic = "Ordinary Least Squares and Regularization (Ridge and Lasso)",
                    keywords = listOf("regression", "OLS", "Gauss Markov", "BLUE", "VIF", "multicollinearity", "Ridge", "Lasso", "ElasticNet", "homoscedasticity"),
                    definition = "Regression models describe continuous responses via linear combinations of predictors. OLS is BLUE under Gauss-Markov assumptions, while Ridge (L2) and Lasso (L1) handle multicollinearity and high-dimensional sparsity.",
                    formulaOrCoreRule = "beta = (X^T X)^(-1) X^T y. Ridge: min ||y - X*beta||^2 + lambda*||beta||_2^2. Lasso: min ||y - X*beta||^2 + lambda*||beta||_1. VIF = 1/(1 - R^2).",
                    workedExample = "For y=[2,3,5,7,8] on x=[1,2,3,4,5], slope beta_1=1.6, intercept beta_0=0.2. R^2 = 1 - (0.40/26.0) = 0.9846.",
                    content = "Diagnostics evaluate residuals for normality, exogeneity, and homoscedasticity. Lasso performs embedded feature selection by driving non-influential coefficients to exact zero."
                )
            )
        )
    }

    private fun createChapter5(): CatalogChapter {
        return CatalogChapter(
            chapterId = 5,
            chapterOrder = 5,
            title = "Classification & Ensemble Methods",
            estimatedMinutes = 35,
            summary = "Logistic regression, ROC-AUC, support vector machines, decision trees, bagging (Random Forest), boosting (XGBoost, LightGBM), and cost-sensitive classification.",
            syllabus = listOf("Binary & Multinomial Logistic Regression (Log-Odds & Cross-Entropy)", "Performance Metrics: Precision, Recall, F1-Score, PR-Curves & ROC-AUC", "Support Vector Machines: Maximum Margin Hyperplane & Kernel Trick", "Ensemble Bagging: Bootstrap Aggregating & Random Forests", "Ensemble Boosting: Gradient Boosting Machines, XGBoost & LightGBM"),
            coreFormulas = listOf("Sigmoid Function: sigma(z) = 1 / (1 + e^(-z))", "Binary Cross-Entropy Loss: L = - (1/n) * Sum(y_i*log(p_i) + (1 - y_i)*log(1 - p_i))", "F1-Score: F1 = 2 * (Precision * Recall) / (Precision + Recall)", "Gini Impurity: Gini = 1 - Sum(p_i^2)", "Entropy: H(S) = - Sum(p_i * log2(p_i))", "Gradient Boosting Step: F_m(x) = F_{m-1}(x) + gamma_m * h_m(x)"),
            part1Title = "Classification Theory, Margins & Advanced Ensembles",
            part1Content = "Classification algorithms map feature vectors into discrete categorical class labels. Logistic Regression models the posterior probability of a binary label via the sigmoid activation function sigma(z) = 1 / (1 + e^(-z)), transforming a linear log-odds combination w^T*x + b into a bounded probability interval [0, 1]. Model optimization maximizes the Bernoulli log-likelihood, solved numerically via gradient descent on binary cross-entropy loss.\n\nEvaluating classifiers on imbalanced datasets requires moving beyond raw classification accuracy. The confusion matrix tabulates True Positives (TP), False Positives (FP), True Negatives (TN), and False Negatives (FN). Precision (TP / (TP + FP)) quantifies positive predictive value, while Recall (TP / (TP + FN)) measures sensitivity. The F1-score provides the harmonic mean of precision and recall. The Receiver Operating Characteristic (ROC) curve plots True Positive Rate against False Positive Rate across all decision thresholds; the Area Under the Curve (ROC-AUC) quantifies threshold-independent discriminative capacity.\n\nSupport Vector Machines (SVM) identify the unique hyperplane that maximizes the geometric margin between separable classes, governed by support vectors on the margin boundaries. For non-linearly separable topologies, Mercer's Theorem permits mapping input space into infinite-dimensional Hilbert spaces via kernel functions (such as Radial Basis Function RBF K(x, z) = exp(-gamma*||x - z||^2)) without explicitly computing high-dimensional coordinates.\n\nEnsemble learning combines diverse base estimators to achieve superior predictive accuracy. Bagging (Bootstrap Aggregating), exemplified by Random Forests, trains independent decision trees on bootstrapped data subsets and randomly subsampled feature splits, drastically reducing model variance through decorrelated averaging. Boosting, formalized by AdaBoost and refined by Gradient Boosting (XGBoost, LightGBM, CatBoost), trains trees sequentially, where each successive learner fits the negative gradient (pseudo-residuals) of the loss function, systematically eliminating bias.",
            part2Title = "Classification Metrics, Worked Decision Tree Split & Systems",
            part2Content = "Exact Formulations & Evaluation Metrics:\n• Precision = TP / (TP + FP), Recall = TP / (TP + FN)\n• Specificity = TN / (TN + FP), Fall-out (FPR) = FP / (TN + FP)\n• Gini Impurity: I_G = 1 - Sum(p_i^2)\n• Information Gain: IG(S, A) = H(S) - Sum((|S_v|/|S|) * H(S_v))\n\nWorked Gini Impurity & Split Example:\nProblem: A dataset of 20 samples contains 12 Positives and 8 Negatives. A binary split on Feature A yields:\nLeft child: 10 samples (8 Positives, 2 Negatives).\nRight child: 10 samples (4 Positives, 6 Negatives).\nCompute the initial Gini impurity and the Weighted Gini of the split.\nStep 1: Parent Gini: p(+) = 12/20 = 0.6, p(-) = 8/20 = 0.4.\nGini_parent = 1 - (0.6^2 + 0.4^2) = 1 - (0.36 + 0.16) = 1 - 0.52 = 0.48.\nStep 2: Left child Gini: p(+) = 8/10 = 0.8, p(-) = 2/10 = 0.2.\nGini_left = 1 - (0.8^2 + 0.2^2) = 1 - (0.64 + 0.04) = 1 - 0.68 = 0.32.\nStep 3: Right child Gini: p(+) = 4/10 = 0.4, p(-) = 6/10 = 0.6.\nGini_right = 1 - (0.4^2 + 0.6^2) = 1 - (0.16 + 0.36) = 1 - 0.52 = 0.48.\nStep 4: Weighted Gini of split:\nGini_split = (10/20)*Gini_left + (10/20)*Gini_right = 0.5*(0.32) + 0.5*(0.48) = 0.16 + 0.24 = 0.40.\nStep 5: Gini Gain = Gini_parent - Gini_split = 0.48 - 0.40 = 0.08.\n\nReal-World Applications:\nIn banking fraud prevention, gradient boosted decision trees process credit card transactions in under 20 milliseconds, balancing extreme class imbalance (0.1% fraud rate) via focal loss and SMOTE oversampling. In autonomous vehicles, SVM and ensemble classifiers categorize sensor point clouds into pedestrians, cyclists, and static obstacles.",
            examTip = "University Exam Tip: Differentiate Bagging and Boosting: Bagging reduces variance by averaging independent low-bias trees (parallel); Boosting reduces bias by iteratively training on residual errors (sequential). In viva exams, know why harmonic mean is used in F1-score instead of arithmetic mean.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4033,
                    questionNumber = 1,
                    question = "A medical diagnostic classifier yields 80 True Positives, 20 False Positives, 10 False Negatives, and 890 True Negatives. What is its Precision?",
                    options = mapOf("A" to "0.80 (80%)", "B" to "0.89 (89%)", "C" to "0.75 (75%)", "D" to "0.90 (90%)"),
                    correctOption = "A",
                    explanation = "Precision = TP / (TP + FP) = 80 / (80 + 20) = 80 / 100 = 0.80 (80%)."
                ),
                QuizQuestionDto(
                    id = 4034,
                    questionNumber = 2,
                    question = "What is the primary mechanism by which Random Forests reduce variance compared to individual decision trees?",
                    options = mapOf("A" to "Bootstrap aggregating (bagging) combined with random feature subspace splitting to decorrelate individual trees", "B" to "Iterative residual fitting via second-order Taylor approximations", "C" to "Constraining tree depth to exactly one decision stump", "D" to "Applying an L1 penalty directly to split thresholds"),
                    correctOption = "A",
                    explanation = "Random Forests reduce ensemble variance by averaging many deep, unpruned trees trained on bootstrap samples and decorrelated by choosing random subsets of features at each split."
                ),
                QuizQuestionDto(
                    id = 4035,
                    questionNumber = 3,
                    question = "In Gradient Boosting Machines (GBM), what does each successive base learner fit during training?",
                    options = mapOf("A" to "The negative gradient (pseudo-residuals) of the loss function with respect to current predictions", "B" to "A randomly shuffled permutation of the original target labels", "C" to "The inverse covariance matrix of the input design space", "D" to "The raw majority class labels using L2 distance"),
                    correctOption = "A",
                    explanation = "Gradient boosting sequentially optimizes an arbitrary differentiable loss function by training each new base tree on the negative gradient of the loss evaluated at the current ensemble prediction."
                ),
                QuizQuestionDto(
                    id = 4036,
                    questionNumber = 4,
                    question = "Which mathematical property allows Support Vector Machines to operate in high-dimensional implicit spaces without explicitly computing coordinates?",
                    options = mapOf("A" to "Mercer's Theorem and the Kernel Trick", "B" to "Central Limit Theorem", "C" to "Gauss-Markov Theorem", "D" to "Bayes' Theorem of Posterior Odds"),
                    correctOption = "A",
                    explanation = "Mercer's condition guarantees that any positive semi-definite kernel function K(x, z) computes the inner product of vectors in a transformed Hilbert space without explicit evaluation of the feature mapping phi(x)."
                ),
                QuizQuestionDto(
                    id = 4037,
                    questionNumber = 5,
                    question = "What does an ROC-AUC score of 0.50 indicate about a binary classification model?",
                    options = mapOf("A" to "The classifier has zero discriminative ability and performs no better than random guessing", "B" to "The classifier achieves perfect classification with 0% error", "C" to "The classifier is completely inverted and needs its threshold reversed", "D" to "The model has achieved optimal Bayes error rate"),
                    correctOption = "A",
                    explanation = "An ROC-AUC of 0.5 corresponds to the diagonal chance line, meaning the model cannot distinguish between positive and negative classes any better than a fair coin flip."
                ),
                QuizQuestionDto(
                    id = 4038,
                    questionNumber = 6,
                    question = "For a node containing 50 positive samples and 50 negative samples, what is its Gini Impurity?",
                    options = mapOf("A" to "0.50", "B" to "0.25", "C" to "1.00", "D" to "0.00"),
                    correctOption = "A",
                    explanation = "p(+) = 0.5, p(-) = 0.5. Gini = 1 - (0.5^2 + 0.5^2) = 1 - (0.25 + 0.25) = 1 - 0.50 = 0.50 (maximum impurity for binary classification)."
                ),
                QuizQuestionDto(
                    id = 4039,
                    questionNumber = 7,
                    question = "Why is the harmonic mean used in the F1-score rather than the arithmetic mean?",
                    options = mapOf("A" to "The harmonic mean heavily penalizes extreme imbalances between precision and recall", "B" to "The arithmetic mean cannot handle decimal values between 0 and 1", "C" to "The harmonic mean guarantees that F1 is always equal to accuracy", "D" to "The harmonic mean eliminates the need for calculating False Positives"),
                    correctOption = "A",
                    explanation = "The harmonic mean is dominated by the smaller value. If either Precision or Recall is close to zero, F1 will be close to zero, preventing trivial classifiers from obtaining deceptively high scores."
                ),
                QuizQuestionDto(
                    id = 4040,
                    questionNumber = 8,
                    question = "Which loss function is minimized by standard binary logistic regression?",
                    options = mapOf("A" to "Binary Cross-Entropy (Log Loss)", "B" to "Mean Squared Error (L2 loss)", "C" to "Hinge Loss", "D" to "Huber Loss"),
                    correctOption = "A",
                    explanation = "Logistic regression minimizes binary cross-entropy (negative log-likelihood of Bernoulli distribution), ensuring convex optimization with smooth probabilistic gradients."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH5_001",
                    subject = "Data Science",
                    chapter = "Classification & Ensemble Methods",
                    topic = "Logistic Regression, SVM and Ensemble Methods (Bagging and Boosting)",
                    keywords = listOf("classification", "logistic regression", "precision", "recall", "F1", "ROC AUC", "SVM", "Random Forest", "XGBoost", "Gini"),
                    definition = "Classification maps inputs to discrete categories. Evaluated by ROC-AUC and F1-score, modern production systems utilize Random Forests (bagging to reduce variance) and XGBoost (boosting to reduce bias).",
                    formulaOrCoreRule = "Sigmoid: sigma(z) = 1/(1 + e^(-z)). F1 = 2*P*R/(P + R). Gini = 1 - Sum(p_i^2). Cross-Entropy: -[y*log(p) + (1-y)*log(1-p)].",
                    workedExample = "For TP=80, FP=20, FN=10: Precision = 80/100 = 0.80. Recall = 80/90 = 0.889. F1 = 2*(0.8*0.889)/(0.8 + 0.889) = 0.842.",
                    content = "SVM optimizes maximum margin hyperplanes via Mercer kernel mappings. Random Forests build decorrelated decision trees, whereas Gradient Boosters fit negative loss gradients sequentially."
                )
            )
        )
    }

    private fun createChapter6(): CatalogChapter {
        return CatalogChapter(
            chapterId = 6,
            chapterOrder = 6,
            title = "Feature Engineering & Dimensionality Reduction (PCA)",
            estimatedMinutes = 30,
            summary = "Curse of dimensionality, Principal Component Analysis (PCA), Singular Value Decomposition (SVD), t-SNE, UMAP, target encoding, and automated feature synthesis.",
            syllabus = listOf("The Curse of Dimensionality & Geometric Sparsity in High Dimensions", "Principal Component Analysis: Covariance Matrix Eigendecomposition", "Singular Value Decomposition (SVD) & Truncated SVD for Sparse Matrices", "Non-linear Manifold Learning: t-SNE & UMAP Projections", "Feature Engineering: Categorical Target Encoding & Polynomial Interactions"),
            coreFormulas = listOf("Sample Covariance Matrix: Sigma = (1 / (n - 1)) * X^T * X (for centered X)", "PCA Eigenvalue Equation: Sigma * v_i = lambda_i * v_i", "Explained Variance Ratio: EVR_k = lambda_k / Sum(lambda_i)", "Singular Value Decomposition: X = U * Sigma * V^T", "Smoothed Target Encoding: S_i = (n_i * mean(y_i) + m * global_mean) / (n_i + m)"),
            part1Title = "Dimensionality Theory, Manifolds & Feature Transformations",
            part1Content = "As feature dimensionality d increases, the volume of high-dimensional space grows exponentially, leading to the Curse of Dimensionality. In high dimensions, sample data points become exponentially sparse, pairwise Euclidean distances converge to a uniform constant ratio (the distance concentration phenomenon), and parametric density estimators break down. Dimensionality reduction compresses the feature space onto lower-dimensional sub-manifolds while preserving maximal geometric and variance information.\n\nPrincipal Component Analysis (PCA) is an unsupervised linear transformation that projects data onto orthogonal axes of maximum variance. Mathematically, for a zero-mean standardized data matrix X, the first principal component aligns with the eigenvector v_1 corresponding to the largest eigenvalue lambda_1 of the sample covariance matrix Sigma = (1/(n-1))*X^T*X. Successive components v_2, ..., v_d are orthogonal eigenvectors ordered by decreasing eigenvalues, guaranteeing zero mutual covariance among transformed features.\n\nSingular Value Decomposition (SVD) provides the computational foundation for PCA without explicitly constructing the massive covariance matrix. Any real m x n matrix X factors uniquely into X = U * Sigma * V^T, where U and V are orthogonal matrices of left- and right-singular vectors, and Sigma is a diagonal matrix of non-negative singular values sigma_i. The right-singular vectors V correspond exactly to the principal component loadings, and singular values relate to eigenvalues via lambda_i = sigma_i^2 / (n - 1).\n\nWhen data lies on non-linear low-dimensional manifolds (e.g., Swiss Roll geometry), linear PCA fails to capture topological structure. Non-linear dimensionality reduction algorithms like t-Distributed Stochastic Neighbor Embedding (t-SNE) convert Euclidean distances into conditional probabilities and minimize Kullback-Leibler (KL) divergence, excelling at cluster visualization. Uniform Manifold Approximation and Projection (UMAP) models manifold topology via fuzzy simplicial sets, achieving superior preservation of global data structure with dramatically lower computational complexity.",
            part2Title = "Eigen-Derivations, Worked 2D PCA Numerical & Applications",
            part2Content = "Exact Formulations & Transformation Steps:\n• Center matrix: X_c = X - mu\n• Covariance matrix: Sigma = (1 / (n - 1)) * X_c^T * X_c\n• Eigendecomposition: det(Sigma - lambda * I) = 0\n• Projection to k dimensions: Z = X_c * W_k (where W_k contains top k eigenvectors)\n\nWorked 2D PCA Numerical Example:\nProblem: Given centered bivariate dataset X = [[-2, -1], [-1, -1], [0, 0], [1, 1], [2, 1]] with n = 5 samples. Compute the sample covariance matrix, eigenvalues, and the first principal component.\nStep 1: Compute Covariance Matrix Sigma:\nn - 1 = 4.\nVar(x1) = ((-2)^2 + (-1)^2 + 0^2 + 1^2 + 2^2) / 4 = (4 + 1 + 0 + 1 + 4) / 4 = 10 / 4 = 2.5.\nVar(x2) = ((-1)^2 + (-1)^2 + 0^2 + 1^2 + 1^2) / 4 = (1 + 1 + 0 + 1 + 1) / 4 = 4 / 4 = 1.0.\nCov(x1, x2) = ((-2*-1) + (-1*-1) + (0*0) + (1*1) + (2*1)) / 4 = (2 + 1 + 0 + 1 + 2) / 4 = 6 / 4 = 1.5.\nCovariance Matrix: Sigma = [[2.5, 1.5], [1.5, 1.0]].\nStep 2: Solve Characteristic Equation: det(Sigma - lambda*I) = 0:\n(2.5 - lambda)(1.0 - lambda) - (1.5)^2 = 0\nlambda^2 - 3.5*lambda + 2.5 - 2.25 = 0 => lambda^2 - 3.5*lambda + 0.25 = 0.\nUsing quadratic formula: lambda = (3.5 +/- sqrt(12.25 - 4*0.25)) / 2 = (3.5 +/- sqrt(11.25)) / 2 = (3.5 +/- 3.354) / 2.\nlambda_1 = (3.5 + 3.354) / 2 = 6.854 / 2 = 3.427.\nlambda_2 = (3.5 - 3.354) / 2 = 0.146 / 2 = 0.073.\nStep 3: Explained Variance Ratio of PC1: EVR_1 = 3.427 / (3.427 + 0.073) = 3.427 / 3.50 = 0.979 (97.9% of variance captured by the 1st component).\n\nReal-World Applications:\nIn genomics, PCA reduces 500,000 single-nucleotide polymorphisms (SNPs) down to 2 principal components to visualize geographic human migration ancestry. In computer vision, Eigenfaces and Truncated SVD compress high-resolution image matrices for facial recognition and latent semantic text document analysis.",
            examTip = "University Exam Tip: Always standardize features to zero mean and unit variance before PCA if features are measured in different units; otherwise, the feature with the largest absolute numeric scale will dominate the first principal component. In viva exams, know that t-SNE is non-deterministic and does not preserve global distances.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4041,
                    questionNumber = 1,
                    question = "What mathematical criterion does the first principal component in PCA optimize?",
                    options = mapOf("A" to "It maximizes the variance of the projected data points along an orthogonal direction", "B" to "It minimizes the correlation between the labels and predictors", "C" to "It maximizes the mutual information between categorical columns", "D" to "It minimizes the number of clusters in latent space"),
                    correctOption = "A",
                    explanation = "The first principal component finds the unit vector v1 that maximizes sample variance Var(X*v1) = v1^T * Sigma * v1, subject to ||v1|| = 1."
                ),
                QuizQuestionDto(
                    id = 4042,
                    questionNumber = 2,
                    question = "If the eigenvalues of a 3-feature covariance matrix are lambda1 = 6.0, lambda2 = 3.0, and lambda3 = 1.0, what proportion of total variance is explained by the first two principal components?",
                    options = mapOf("A" to "90%", "B" to "60%", "C" to "80%", "D" to "70%"),
                    correctOption = "A",
                    explanation = "Total variance = 6.0 + 3.0 + 1.0 = 10.0. Explained variance by top 2 = (6.0 + 3.0) / 10.0 = 9.0 / 10.0 = 0.90 (90%)."
                ),
                QuizQuestionDto(
                    id = 4043,
                    questionNumber = 3,
                    question = "Why is feature standardization (Z-score scaling) critical prior to executing PCA?",
                    options = mapOf("A" to "Features with larger raw numeric scales would arbitrarily dominate the principal components regardless of intrinsic information content", "B" to "Standardization ensures all eigenvalues are equal to 1.0", "C" to "PCA cannot compute matrix inverses for negative numbers", "D" to "Standardization converts categorical variables to continuous floats"),
                    correctOption = "A",
                    explanation = "Because PCA seeks axes of maximum variance, a feature with values in thousands (e.g., income) would have far larger variance than a feature scaled 0-1 (e.g., age), completely distorting the eigenvectors."
                ),
                QuizQuestionDto(
                    id = 4044,
                    questionNumber = 4,
                    question = "What is the primary operational advantage of Singular Value Decomposition (SVD) over direct covariance eigendecomposition in high-dimensional PCA?",
                    options = mapOf("A" to "SVD computes principal components directly on data matrix X without constructing the expensive n x n or p x p covariance matrix", "B" to "SVD guarantees 100% explained variance in 1 dimension", "C" to "SVD automatically performs non-linear manifold unrolling", "D" to "SVD eliminates the need for centered data"),
                    correctOption = "A",
                    explanation = "When p is very large (e.g., text term-document matrices), computing X^T * X is computationally prohibitive. SVD factors X directly with higher numerical stability."
                ),
                QuizQuestionDto(
                    id = 4045,
                    questionNumber = 5,
                    question = "Which dimensionality reduction algorithm minimizes Kullback-Leibler (KL) divergence between high-dimensional and low-dimensional pairwise student-t distributions?",
                    options = mapOf("A" to "t-Distributed Stochastic Neighbor Embedding (t-SNE)", "B" to "Linear Discriminant Analysis (LDA)", "C" to "Independent Component Analysis (ICA)", "D" to "Factor Analysis"),
                    correctOption = "A",
                    explanation = "t-SNE models local neighborhood similarities using Gaussian distributions in high-dimensional space and Student-t distributions in low-dimensional space, minimizing KL divergence."
                ),
                QuizQuestionDto(
                    id = 4046,
                    questionNumber = 6,
                    question = "What phenomenon occurs in high-dimensional spaces where the ratio between the distance to the nearest point and the distance to the farthest point approaches 1?",
                    options = mapOf("A" to "Distance Concentration Phenomenon (Curse of Dimensionality)", "B" to "Central Limit Convergence", "C" to "Gauss-Markov Uniformity", "D" to "Gibbs Phenomenon"),
                    correctOption = "A",
                    explanation = "Under the distance concentration phenomenon, as dimensionality approaches infinity, all pairwise Euclidean distances become almost identical, rendering distance-based algorithms (like KNN) ineffective."
                ),
                QuizQuestionDto(
                    id = 4047,
                    questionNumber = 7,
                    question = "What is the purpose of smoothing (e.g., m-estimate smoothing) in Target Encoding for high-cardinality categorical variables?",
                    options = mapOf("A" to "Prevent target leakage and overfitting for rare categories with few occurrences", "B" to "Ensure all category labels sum to exactly 1.0", "C" to "Convert the target variable into a binary indicator", "D" to "Replace all missing values with global median"),
                    correctOption = "A",
                    explanation = "Smoothing blends the category's empirical mean with the global target mean weighted by category frequency, preventing rare categories (e.g., n=1) from causing extreme overfitting."
                ),
                QuizQuestionDto(
                    id = 4048,
                    questionNumber = 8,
                    question = "What are the geometric relationships among the principal component loading vectors in PCA?",
                    options = mapOf("A" to "They are mutually orthogonal unit vectors", "B" to "They are linearly dependent and parallel", "C" to "They intersect at 45-degree acute angles", "D" to "They form a convex simplex spanning the unit cube"),
                    correctOption = "A",
                    explanation = "The eigenvectors of a symmetric real covariance matrix are mutually orthogonal, meaning their dot products equal zero (v_i^T * v_j = 0 for i != j) and ||v_i|| = 1."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH6_001",
                    subject = "Data Science",
                    chapter = "Feature Engineering & Dimensionality Reduction (PCA)",
                    topic = "Principal Component Analysis and Manifold Learning",
                    keywords = listOf("PCA", "SVD", "dimensionality reduction", "eigenvalues", "eigenvectors", "covariance", "t-SNE", "UMAP", "target encoding"),
                    definition = "PCA projects centered, standardized data onto orthogonal eigenvectors of maximum variance. SVD provides stable matrix factorization, while t-SNE and UMAP preserve non-linear manifold topologies.",
                    formulaOrCoreRule = "Sigma = (1/(n-1))*X^T*X. Sigma*v = lambda*v. EVR_k = lambda_k / Sum(lambda). X = U*Sigma*V^T.",
                    workedExample = "For covariance matrix with lambda1=6.0, lambda2=3.0, lambda3=1.0: EVR of top 2 components = (6+3)/10 = 90% total variance preserved.",
                    content = "Feature engineering mitigates the curse of dimensionality. Standardization is mandatory prior to PCA to ensure scale invariance across heterogeneous predictor dimensions."
                )
            )
        )
    }

    private fun createChapter7(): CatalogChapter {
        return CatalogChapter(
            chapterId = 7,
            chapterOrder = 7,
            title = "Time-Series Forecasting",
            estimatedMinutes = 30,
            summary = "Stationarity, autocorrelation (ACF/PACF), ARIMA, Seasonal ARIMA (SARIMA), exponential smoothing, Prophet, and LSTM recurrent networks.",
            syllabus = listOf("Time-Series Components: Trend, Seasonality, Cyclical & Residual Noise", "Stationarity: Strict vs Weak Stationarity & Augmented Dickey-Fuller (ADF) Test", "Autocorrelation (ACF) & Partial Autocorrelation (PACF) Diagnostics", "ARIMA Modeling: Autoregressive (AR), Differencing (I) & Moving Average (MA)", "Deep Learning & Additive Decomposition: SARIMA, Prophet & LSTM Networks"),
            coreFormulas = listOf("Additive Decomposition: Y_t = Trend_t + Seasonal_t + Residual_t", "Multiplicative Decomposition: Y_t = Trend_t * Seasonal_t * Residual_t", "First Difference: Delta Y_t = Y_t - Y_{t-1}", "AR(p) Model: Y_t = c + Sum(phi_i * Y_{t-i}) + epsilon_t", "MA(q) Model: Y_t = mu + epsilon_t + Sum(theta_j * epsilon_{t-j})", "Mean Absolute Percentage Error: MAPE = (1/n) * Sum(|(Y_t - Y_hat_t) / Y_t|) * 100%"),
            part1Title = "Temporal Stochastic Processes, Stationarity & ARIMA Formulations",
            part1Content = "A time series is a sequential collection of observations indexed chronologically. Classical time-series analysis decomposes a signal into systematic components: long-term Trend (deterministic trajectory), Seasonality (fixed periodic oscillations), Cyclical movements (irregular macroeconomic fluctuations), and Residual Noise (white noise stochastic shocks). Additive models apply when seasonal variance remains constant across trend levels, whereas multiplicative models suit scenarios where seasonal amplitudes scale proportionally with trend.\n\nStatistical modeling of time series fundamentally assumes weak stationarity (covariance stationarity). A stochastic process is weakly stationary if its mean E[Y_t] = mu is constant over time, its variance Var(Y_t) = sigma^2 is finite and time-invariant, and its autocovariance Cov(Y_t, Y_{t-k}) depends solely on the time lag k, not on absolute time t. Non-stationary series exhibiting trends or unit roots are transformed to stationarity via differencing (Delta Y_t = Y_t - Y_{t-1}). The Augmented Dickey-Fuller (ADF) test formally evaluates the null hypothesis of a unit root (non-stationarity).\n\nModel identification relies on Autocorrelation Function (ACF) and Partial Autocorrelation Function (PACF) plots. The ACF measures correlation between Y_t and Y_{t-k} including intermediate lag effects, while the PACF controls for intermediate lags, isolating direct correlation. For an Autoregressive AR(p) process, PACF cuts off sharply after lag p while ACF decays exponentially; conversely, for a Moving Average MA(q) process, ACF cuts off after lag q while PACF decays exponentially.\n\nThe Box-Jenkins methodology integrates these elements into the ARIMA(p, d, q) framework: p represents autoregressive terms, d denotes order of differencing, and q indicates moving average residual terms. Seasonal ARIMA (SARIMA(p, d, q)x(P, D, Q)_s) extends this to model seasonal lag intervals s. In modern production environments, decomposable Bayesian additive models (Prophet) and Recurrent Neural Networks (LSTM, GRU) capture non-linear calendar events and high-frequency multi-variate dependencies.",
            part2Title = "Forecasting Equations, Worked AR(1) Numerical & Applications",
            part2Content = "Exact Equations & Forecasting Formulations:\n• AR(1) Equation: Y_t = c + phi * Y_{t-1} + epsilon_t (Stationarity condition: |phi| < 1)\n• One-step-ahead forecast: Y_hat_{t+1|t} = c + phi * Y_t\n• k-step-ahead forecast: Y_hat_{t+k|t} = c * Sum_{i=0}^{k-1} phi^i + phi^k * Y_t\n• Long-term unconditional mean: mu = c / (1 - phi)\n\nWorked AR(1) Forecast Example:\nProblem: An AR(1) model for server memory consumption has constant c = 10, autoregressive coefficient phi = 0.6, and current memory Y_t = 80 GB. Compute forecasts for t+1 and t+2, and the long-term equilibrium mean.\nStep 1: Compute 1-step forecast (t+1):\nY_hat_{t+1} = c + phi * Y_t = 10 + (0.6 * 80) = 10 + 48 = 58 GB.\nStep 2: Compute 2-step forecast (t+2):\nY_hat_{t+2} = c + phi * Y_hat_{t+1} = 10 + (0.6 * 58) = 10 + 34.8 = 44.8 GB.\nStep 3: Long-term equilibrium mean:\nmu = c / (1 - phi) = 10 / (1 - 0.6) = 10 / 0.4 = 25 GB.\nAnalysis: Because |phi| = 0.6 < 1, the process is stationary and mean-reverting, decaying asymptotically towards 25 GB.\n\nReal-World Applications:\nIn electric grid management, SARIMA and LSTM models forecast hourly regional kilowatt-hour demand, scheduling peaking plant dispatch to prevent blackouts. In supply chain logistics, Prophet models forecast inventory replenishment incorporating Black Friday promotional spikes and Chinese New Year manufacturing shutdowns.",
            examTip = "University Exam Tip: Memorize the ACF/PACF identification table: AR(p) has tailing off ACF and sharp cut-off PACF at lag p; MA(q) has sharp cut-off ACF at lag q and tailing off PACF. In viva exams, explain that the Augmented Dickey-Fuller (ADF) null hypothesis is that a unit root is present (non-stationary).",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4049,
                    questionNumber = 1,
                    question = "Which set of conditions defines weak (covariance) stationarity in a time-series process?",
                    options = mapOf("A" to "Constant mean, constant variance, and autocovariance depending solely on time lag k", "B" to "Strict zero mean, infinite variance, and deterministic seasonality", "C" to "Monotonically increasing trend with Gaussian white noise", "D" to "Identical joint distribution across all possible time translations"),
                    correctOption = "A",
                    explanation = "Weak stationarity requires: 1. Constant mean over time E[Y_t] = mu; 2. Constant finite variance Var(Y_t) = sigma^2; 3. Autocovariance Cov(Y_t, Y_{t-k}) depends only on lag k."
                ),
                QuizQuestionDto(
                    id = 4050,
                    questionNumber = 2,
                    question = "What is the null hypothesis H0 of the Augmented Dickey-Fuller (ADF) test?",
                    options = mapOf("A" to "The series possesses a unit root (is non-stationary)", "B" to "The series is strictly stationary with zero autocorrelation", "C" to "The residual variance is homoscedastic", "D" to "The seasonal period equals 12"),
                    correctOption = "A",
                    explanation = "The ADF test posits H0: gamma = 0 (unit root exists, implying non-stationarity). A small p-value (p < 0.05) rejects H0 in favor of stationarity."
                ),
                QuizQuestionDto(
                    id = 4051,
                    questionNumber = 3,
                    question = "In the Box-Jenkins methodology, what does an ACF that cuts off sharply after lag 2 combined with an exponentially decaying PACF indicate?",
                    options = mapOf("A" to "An MA(2) Moving Average process", "B" to "An AR(2) Autoregressive process", "C" to "A deterministic linear trend", "D" to "A random walk with drift"),
                    correctOption = "A",
                    explanation = "For an MA(q) process, the ACF cuts off sharply after lag q while the PACF tails off (decays exponentially or sinusoidally)."
                ),
                QuizQuestionDto(
                    id = 4052,
                    questionNumber = 4,
                    question = "For a stationary AR(1) process Y_t = 12 + 0.4*Y_{t-1} + e_t, what is the long-term unconditional mean mu?",
                    options = mapOf("A" to "20.0", "B" to "12.0", "C" to "30.0", "D" to "4.8"),
                    correctOption = "A",
                    explanation = "Unconditional mean mu = c / (1 - phi) = 12 / (1 - 0.4) = 12 / 0.6 = 20.0."
                ),
                QuizQuestionDto(
                    id = 4053,
                    questionNumber = 5,
                    question = "What mathematical operation does the parameter 'd' represent in an ARIMA(p, d, q) model?",
                    options = mapOf("A" to "The degree of non-seasonal differencing applied to make the series stationary", "B" to "The number of autoregressive lag terms", "C" to "The discount factor used in exponential smoothing", "D" to "The polynomial degree of seasonal trend"),
                    correctOption = "A",
                    explanation = "In ARIMA(p, d, q), 'd' represents the order of integrated differencing required to eliminate non-stationarity (e.g., d=1 means Delta Y_t = Y_t - Y_{t-1})."
                ),
                QuizQuestionDto(
                    id = 4054,
                    questionNumber = 6,
                    question = "When seasonal fluctuations grow in absolute magnitude proportionally as the overall trend increases, which decomposition model is appropriate?",
                    options = mapOf("A" to "Multiplicative decomposition (Y_t = Trend * Seasonal * Residual)", "B" to "Additive decomposition (Y_t = Trend + Seasonal + Residual)", "C" to "First-order autoregressive difference", "D" to "Polynomial splines"),
                    correctOption = "A",
                    explanation = "Multiplicative decomposition models seasonal variation as a percentage multiplier of the trend, handling cases where seasonal peaks widen as the baseline grows."
                ),
                QuizQuestionDto(
                    id = 4055,
                    questionNumber = 7,
                    question = "What is the condition on parameter phi for an AR(1) process to be weakly stationary?",
                    options = mapOf("A" to "|phi| < 1", "B" to "phi > 1", "C" to "phi = 0", "D" to "phi^2 = 2"),
                    correctOption = "A",
                    explanation = "For an AR(1) process to be stationary, the root of the characteristic polynomial must lie outside the unit circle, which translates to |phi| < 1."
                ),
                QuizQuestionDto(
                    id = 4056,
                    questionNumber = 8,
                    question = "Which evaluation metric measures forecast accuracy as a scale-independent percentage of actual values?",
                    options = mapOf("A" to "Mean Absolute Percentage Error (MAPE)", "B" to "Root Mean Squared Error (RMSE)", "C" to "Mean Absolute Error (MAE)", "D" to "Sum of Squared Errors (SSE)"),
                    correctOption = "A",
                    explanation = "MAPE = (1/n) * Sum(|(Y - Y_hat) / Y|) * 100% measures relative percentage error, making it scale-independent across series with different baseline magnitudes."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH7_001",
                    subject = "Data Science",
                    chapter = "Time-Series Forecasting",
                    topic = "Stationarity, Autocorrelation and ARIMA Modeling",
                    keywords = listOf("time-series", "forecasting", "ARIMA", "stationarity", "ADF test", "ACF", "PACF", "SARIMA", "Prophet", "MAPE"),
                    definition = "Time-series forecasting models temporal stochastic sequences. Stationarity (verified via ADF test) is required for ARIMA(p, d, q) models identified via ACF and PACF diagnostic patterns.",
                    formulaOrCoreRule = "AR(1): Y_t = c + phi*Y_{t-1} + e_t. Differencing: Delta Y_t = Y_t - Y_{t-1}. MAPE = (1/n)*Sum(|(Y - Y_hat)/Y|)*100%. Equilibrium: mu = c/(1 - phi).",
                    workedExample = "For AR(1) with c=10, phi=0.6, Y_t=80: 1-step forecast Y_{t+1} = 10 + 0.6(80) = 58. Equilibrium mean mu = 10/(1 - 0.6) = 25.",
                    content = "Box-Jenkins modeling requires differencing non-stationary trends (d > 0). Seasonality is modeled by SARIMA, while complex multi-seasonality is handled by Prophet and LSTM networks."
                )
            )
        )
    }

    private fun createChapter8(): CatalogChapter {
        return CatalogChapter(
            chapterId = 8,
            chapterOrder = 8,
            title = "Big Data Pipelines & Model Deployment",
            estimatedMinutes = 35,
            summary = "Distributed computing (Spark, MapReduce), batch vs streaming (Kafka, Flink), MLOps pipelines (MLflow, Docker), model drift monitoring, and REST/gRPC serving.",
            syllabus = listOf("Distributed Computing Paradigms: MapReduce & Apache Spark RDDs/DataFrames", "Real-Time Event Streaming: Apache Kafka Architectures & Stream Processing", "MLOps Lifecycle: Containerization (Docker), Orchestration (Kubernetes) & MLflow", "Model Serving Patterns: Synchronous REST/gRPC vs Asynchronous Batch Inference", "Model Drift Monitoring: Covariate Shift, Concept Drift & Population Stability Index (PSI)"),
            coreFormulas = listOf("Amdahl's Law of Speedup: S(n) = 1 / ((1 - p) + (p / n))", "Population Stability Index: PSI = Sum((Actual% - Expected%) * ln(Actual% / Expected%))", "Wasserstein Distance (Earth Mover's Distance): W_1(u, v) = Integral(|U(x) - V(x)| dx)", "Kafka Consumer Lag: Lag = Latest_Offset - Committed_Offset", "Model Latency P99: Latency threshold bounding 99% of requests"),
            part1Title = "Distributed Architectures, Streaming & Production MLOps",
            part1Content = "Scaling data science systems to petabyte volumes mandates distributed computing architectures. The foundational MapReduce paradigm splits computations into parallel map tasks (filtering/sorting) and reduce tasks (aggregation) across commodity clusters. Apache Spark revolutionized distributed analytics by introducing Resilient Distributed Datasets (RDDs) and Catalyst-optimized DataFrames, executing iterative machine learning algorithms in-memory up to 100 times faster than disk-bound MapReduce by eliminating intermediate disk I/O.\n\nEnterprise streaming architectures decouple producers from consumers via distributed message logs such as Apache Kafka. Kafka partitions topics across broker clusters to provide horizontal write scaling, high-throughput fault tolerance, and replayable event logs. Stream processing engines like Apache Flink and Spark Structured Streaming process continuous event streams with exactly-once semantic guarantees, maintaining stateful temporal sliding and tumbling aggregations over real-time clickstreams.\n\nMachine Learning Operations (MLOps) establishes disciplined engineering practices for continuous integration, continuous delivery (CI/CD), and automated deployment of machine learning assets. Core MLOps components include experiment tracking and model registries (MLflow, Weights & Biases), environment containerization (Docker), and cluster orchestration (Kubernetes). Containerizing model inference services guarantees deterministic execution environments across staging and cloud production.\n\nPost-deployment monitoring is essential because machine learning models degrade over time. Data drift (covariate shift) occurs when the input feature distribution P(X) changes while conditional label probability P(Y|X) remains fixed. Concept drift occurs when the underlying relationship between inputs and targets P(Y|X) shifts. Statistical metrics like the Population Stability Index (PSI) and two-sample Kolmogorov-Smirnov tests quantify distributional divergence, automatically triggering retraining pipelines when thresholds are breached.",
            part2Title = "Drift Formulations, Worked PSI Drift Calculation & Architecture",
            part2Content = "Exact Equations & Production Metrics:\n• Amdahl's Law: Speedup S = 1 / ((1 - p) + p/N) (where p is parallel fraction, N is worker nodes)\n• Population Stability Index: PSI = Sum((Actual_i - Expected_i) * ln(Actual_i / Expected_i))\n• PSI Interpretation: PSI < 0.10: No significant drift; 0.10 <= PSI < 0.25: Moderate shift; PSI >= 0.25: Significant drift requiring retraining\n\nWorked PSI Drift Calculation Example:\nProblem: Monitor a credit score feature bin where training baseline had Expected = 20% (0.20) and production traffic has Actual = 30% (0.30). Calculate the bin's contribution to PSI.\nStep 1: Difference: Actual - Expected = 0.30 - 0.20 = +0.10.\nStep 2: Ratio: Actual / Expected = 0.30 / 0.20 = 1.50.\nStep 3: Natural log: ln(1.50) = 0.4055.\nStep 4: Contribution: PSI_bin = (Actual - Expected) * ln(Actual / Expected) = 0.10 * 0.4055 = 0.04055.\nStep 5: If the sum across all bins equals 0.28 (> 0.25), the MLOps monitor fires an automated alerting webhook to initiate model retraining on fresh data.\n\nReal-World Applications:\nIn global ride-sharing systems (e.g., Uber Michelangelo), Kafka streams driver GPS locations into Spark streaming pipelines, computing dynamic surge pricing via low-latency gRPC microservices deployed on Kubernetes pods with horizontal pod autoscaling.",
            examTip = "University Exam Tip: Differentiate Covariate Shift (P(X) changes while P(Y|X) is constant) from Concept Drift (P(Y|X) changes). In viva exams, know that a PSI >= 0.25 indicates significant distribution drift requiring immediate model retraining.",
            mcqs = listOf(
                QuizQuestionDto(
                    id = 4057,
                    questionNumber = 1,
                    question = "What is the primary architectural factor that makes Apache Spark significantly faster than Hadoop MapReduce for iterative machine learning algorithms?",
                    options = mapOf("A" to "Spark performs computations in-memory using Resilient Distributed Datasets (RDDs) without intermediate disk writes", "B" to "Spark eliminates all network serialization between cluster nodes", "C" to "Spark executes code directly on bare-metal firmware without Java Virtual Machines", "D" to "Spark restricts all datasets to single-threaded CPU pipelines"),
                    correctOption = "A",
                    explanation = "Spark caches intermediate data partitions in RAM across iterations, eliminating the massive disk I/O and serialization overhead incurred by Hadoop MapReduce between successive steps."
                ),
                QuizQuestionDto(
                    id = 4058,
                    questionNumber = 2,
                    question = "If an algorithm is 80% parallelizable (p = 0.8), what is the theoretical maximum speedup possible according to Amdahl's Law as the number of parallel processors approaches infinity?",
                    options = mapOf("A" to "5.0x", "B" to "4.0x", "C" to "10.0x", "D" to "8.0x"),
                    correctOption = "A",
                    explanation = "As N -> infinity, Amdahl's Law yields S = 1 / (1 - p) = 1 / (1 - 0.8) = 1 / 0.2 = 5.0x maximum theoretical speedup."
                ),
                QuizQuestionDto(
                    id = 4059,
                    questionNumber = 3,
                    question = "What type of drift occurs when the distribution of input features P(X) changes over time while the conditional probability P(Y|X) remains unchanged?",
                    options = mapOf("A" to "Covariate Shift (Data Drift)", "B" to "Concept Drift", "C" to "Prior Probability Shift", "D" to "Sensor Drift"),
                    correctOption = "A",
                    explanation = "Covariate shift refers specifically to changes in the marginal distribution of the predictors P(X), while the conditional relationship between inputs and targets P(Y|X) remains static."
                ),
                QuizQuestionDto(
                    id = 4060,
                    questionNumber = 4,
                    question = "In production model monitoring, what does a Population Stability Index (PSI) score greater than 0.25 indicate?",
                    options = mapOf("A" to "Significant distributional shift indicating that the model has degraded and requires retraining", "B" to "Perfect stability between training and production distributions", "C" to "The model has achieved 99% accuracy on production inferences", "D" to "The Kafka message queue has reached zero consumer lag"),
                    correctOption = "A",
                    explanation = "Industry standard benchmarks categorize PSI: < 0.10: No shift; 0.10 - 0.25: Moderate shift; >= 0.25: Significant shift requiring model investigation and retraining."
                ),
                QuizQuestionDto(
                    id = 4061,
                    questionNumber = 5,
                    question = "Which protocol is preferred over traditional HTTP/REST for low-latency, high-throughput inter-microservice model inference communication?",
                    options = mapOf("A" to "gRPC over HTTP/2 using Protocol Buffers", "B" to "SOAP XML over HTTP/1.0", "C" to "FTP batch file transfers", "D" to "Telnet command strings"),
                    correctOption = "A",
                    explanation = "gRPC utilizes HTTP/2 multiplexed streaming and compact binary serialization via Protocol Buffers (protobuf), dramatically reducing latency and network overhead compared to JSON REST."
                ),
                QuizQuestionDto(
                    id = 4062,
                    questionNumber = 6,
                    question = "What is the primary function of an MLOps Model Registry (such as MLflow Model Registry)?",
                    options = mapOf("A" to "Manage model lifecycle stages (Staging, Production, Archived), versioning, and governance metadata", "B" to "Automatically compile Python scripts into FPGA circuit boards", "C" to "Encrypt database passwords using SHA-256 hashes", "D" to "Eliminate the need for unit testing and code review"),
                    correctOption = "A",
                    explanation = "A model registry provides centralized version control, lineage tracking, lifecycle transition approvals (Dev -> Staging -> Prod), and cryptographic auditability for trained models."
                ),
                QuizQuestionDto(
                    id = 4063,
                    questionNumber = 7,
                    question = "In Apache Kafka, what is 'Consumer Lag'?",
                    options = mapOf("A" to "The delta between the latest offset produced to a partition and the current offset read by a consumer group", "B" to "The round-trip ping time between client and ZooKeeper", "C" to "The CPU idle percentage of the broker cluster", "D" to "The rate of disk encryption on consumer nodes"),
                    correctOption = "A",
                    explanation = "Consumer lag measures how many unconsumed messages remain behind the producer head, serving as a critical metric for monitoring stream processing delays."
                ),
                QuizQuestionDto(
                    id = 4064,
                    questionNumber = 8,
                    question = "Why is Docker containerization essential for deploying production machine learning models?",
                    options = mapOf("A" to "It packages model binaries, dependencies, C-libraries, and Python runtimes into an immutable, portable artifact", "B" to "It automatically converts CPU training routines into GPU CUDA kernels", "C" to "It removes all memory limitations from host operating systems", "D" to "It guarantees zero network latency across cloud providers"),
                    correctOption = "A",
                    explanation = "Docker encapsulates the complete dependency graph (OS packages, Python packages, C++ dependencies) ensuring that models run identically across development, testing, and production servers."
                )
            ),
            ragChunks = listOf(
                KnowledgeChunk(
                    chunkId = "DS_CH8_001",
                    subject = "Data Science",
                    chapter = "Big Data Pipelines & Model Deployment",
                    topic = "Distributed Pipelines, Kafka Streaming and MLOps Drift Monitoring",
                    keywords = listOf("Big Data", "Spark", "Kafka", "MLOps", "model deployment", "Docker", "Kubernetes", "gRPC", "drift", "PSI"),
                    definition = "Big Data pipelines leverage distributed in-memory computing (Spark) and event streaming (Kafka) for large-scale ETL. MLOps governs automated serving (Docker, gRPC) and drift monitoring (PSI, covariate shift).",
                    formulaOrCoreRule = "Amdahl: S = 1/((1 - p) + p/N). PSI = Sum((Actual - Expected) * ln(Actual/Expected)). Consumer Lag = Latest - Committed.",
                    workedExample = "For feature bin with Expected=20% and Actual=30%: PSI_bin = (0.30 - 0.20)*ln(0.30/0.20) = 0.10*0.4055 = 0.0405. PSI >= 0.25 triggers retraining.",
                    content = "Production ML systems require continuous monitoring for covariate shift and concept drift. Containerization guarantees deterministic inference environments across distributed Kubernetes clusters."
                )
            )
        )
    }
}
