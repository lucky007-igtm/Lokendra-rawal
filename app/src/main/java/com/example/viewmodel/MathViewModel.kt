package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.*
import com.example.data.repository.MathRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

// UI States
sealed interface DoubtUiState {
    object Idle : DoubtUiState
    object Loading : DoubtUiState
    data class Success(val solution: String) : DoubtUiState
    data class Error(val message: String) : DoubtUiState
}

sealed interface TranslationUiState {
    object Idle : TranslationUiState
    object Loading : TranslationUiState
    data class Success(val originalText: String, val translatedText: String) : TranslationUiState
    data class Error(val message: String) : TranslationUiState
}

// Data models for Mock Test
data class MathQuestion(
    val id: Int,
    val questionEn: String,
    val questionHi: String,
    val optionsEn: List<String>,
    val optionsHi: List<String>,
    val correctOptionIndex: Int,
    val explanationEn: String,
    val explanationHi: String
)

data class MockTest(
    val id: String,
    val title: String,
    val category: String, // IIT JAM, GATE, CSIR NET
    val durationMinutes: Int,
    val questions: List<MathQuestion>
)

// Recorded Course models
data class CourseLesson(
    val title: String,
    val duration: String,
    val summaryEn: String,
    val summaryLaTeX: String,
    val isLive: Boolean = false
)

data class RecordedCourse(
    val id: String,
    val title: String,
    val category: String, // IIT JAM, GATE, CSIR NET, University
    val subject: String, // Abstract Algebra, Real Analysis, etc.
    val author: String,
    val level: String, // Beginner, Intermediate, Advanced
    val imagePrompt: String,
    val lessons: List<CourseLesson>
)

class MathViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MathRepository
    val streakState: StateFlow<StreakState>
    val doubtHistory: StateFlow<List<AiDoubtHistory>>
    val testAttempts: StateFlow<List<MockTestAttempt>>
    val bookmarks: StateFlow<List<CourseBookmark>>

    // Active AI Solver UI States
    private val _doubtUiState = MutableStateFlow<DoubtUiState>(DoubtUiState.Idle)
    val doubtUiState: StateFlow<DoubtUiState> = _doubtUiState.asStateFlow()

    // Active Translator UI States
    private val _translationUiState = MutableStateFlow<TranslationUiState>(TranslationUiState.Idle)
    val translationUiState: StateFlow<TranslationUiState> = _translationUiState.asStateFlow()

    // Test execution state
    private val _activeTest = MutableStateFlow<MockTest?>(null)
    val activeTest: StateFlow<MockTest?> = _activeTest.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // Question Index -> Chosen Option Index
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()

    private val _testSecondsRemaining = MutableStateFlow(0)
    val testSecondsRemaining: StateFlow<Int> = _testSecondsRemaining.asStateFlow()

    private val _isLanguageHindi = MutableStateFlow(false)
    val isLanguageHindi: StateFlow<Boolean> = _isLanguageHindi.asStateFlow()

    // Fractal explorer visual variables
    private val _fractalMaxIterations = MutableStateFlow(40)
    val fractalMaxIterations: StateFlow<Int> = _fractalMaxIterations.asStateFlow()

    private val _fractalZoom = MutableStateFlow(1.0f)
    val fractalZoom: StateFlow<Float> = _fractalZoom.asStateFlow()

    private val _fractalCenterX = MutableStateFlow(-0.7f)
    val fractalCenterX: StateFlow<Float> = _fractalCenterX.asStateFlow()

    private val _fractalCenterY = MutableStateFlow(0.0f)
    val fractalCenterY: StateFlow<Float> = _fractalCenterY.asStateFlow()

    // Simulated whiteboard & video state
    private val _whiteboardDrawings = MutableStateFlow<List<Pair<Float, Float>>>(emptyList())
    val whiteboardDrawings: StateFlow<List<Pair<Float, Float>>> = _whiteboardDrawings.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MathRepository(database.mathDao())

        // Collect streaks (initialize default stream, seed first entry if null)
        streakState = repository.streakState
            .map { it ?: StreakState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StreakState())

        // Initialize a default streak in database if Room is empty
        viewModelScope.launch {
            repository.streakState.firstOrNull()?.let {
                // Already seeded
            } ?: run {
                repository.saveStreakState(StreakState())
            }
        }

        doubtHistory = repository.doubtHistory.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        testAttempts = repository.testAttempts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        bookmarks = repository.bookmarks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }

    // Modern Courser DB (representing global elite curriculum)
    val courses = listOf(
        RecordedCourse(
            id = "abstract_alg_1",
            title = "Master Sylow Theorems & Galois Theory",
            category = "CSIR NET",
            subject = "Abstract Algebra",
            author = "Prof. Euler Banerjee",
            level = "Advanced",
            imagePrompt = "Abstract mathematical groups and geometric symmetries",
             lessons = listOf(
                CourseLesson(
                    title = "Sylow p-subgroups & Congruence Properties",
                    duration = "45 mins",
                    summaryEn = "Understand the core statement of the first Sylow theorem: if p^k divides |G|, then G contains a subgroup of order p^k.",
                    summaryLaTeX = "Theorem: Let \$G\$ be a finite group of order \$p^n m\$, where \$p\$ is a prime and \$p \\nmid m\$. Then for every \$k \\in \\{1, \\ldots, n\\}\$, \$G\$ features a subgroup of order \$p^k\$. \\newline Singular conjugacy: \$n_p = [G : N_G(P)] \\equiv 1 \\pmod{p}\$"
                ),
                CourseLesson(
                    title = "Galois Field Extensions & Splitting Fields",
                    duration = "52 mins",
                    summaryEn = "Analyzing field algebraic extensions, normal structures, and resolving roots of polynomials through permutation groups.",
                    summaryLaTeX = "Let \$K/F\$ be a finite field extension. It is Galois if and only if it is normal and separable. In this scenario, \$|Gal(K/F)| = [K : F]\$."
                ),
                CourseLesson(
                    title = "Solvability of Quintics by Radicals",
                    duration = "38 mins",
                    summaryEn = "Rigorous proof of the Abel-Ruffini theorem using commutative algebra. S_5 is not solvable.",
                    summaryLaTeX = "Because \$A_5\$ is simple and non-abelian, the symmetric group \$S_5\$ is not a solvable group. Hence, the general quintic equation cannot be solved by radicals."
                )
            )
        ),
        RecordedCourse(
            id = "real_analysis_1",
            title = "Lebesgue Measure & Integration Paradoxes",
            category = "IIT JAM",
            subject = "Real Analysis",
            author = "Dr. S. Ramanujan Shastri",
            level = "Intermediate",
            imagePrompt = "Infinity points merging, topology manifolds",
            lessons = listOf(
                CourseLesson(
                    title = "Outer Measure & Caratheodory Criterion",
                    duration = "50 mins",
                    summaryEn = "Developing outer measure on power set of R and isolating the set of measurable subsets via outer partitions.",
                    summaryLaTeX = "A set \$E \\subset \\mathbb{R}\$ is Lebesgue measurable if for all \$A \\subset \\mathbb{R}\$: \\newline \$m^*(A) = m^*(A \\cap E) + m^*(A \\cap E^c)\$"
                ),
                CourseLesson(
                    title = "Vitali Construction of Non-Measurable Set",
                    duration = "40 mins",
                    summaryEn = "Demonstration of a non-measurable subset of real numbers in the unit interval using the Axiom of Choice.",
                    summaryLaTeX = "Let \$V\$ represent a set containing exactly one element from each equivalence class of \$\\mathbb{R}/\\mathbb{Q}\$ in [0,1]. Then \$V\$ is non-measurable with respect to Lebesgue measure."
                )
            )
        ),
        RecordedCourse(
            id = "complex_analysis_1",
            title = "Residue Theorem & Infinite Summations",
            category = "GATE",
            subject = "Complex Analysis",
            author = "Prof. Gauss Deshmukh",
            level = "Advanced",
            imagePrompt = "Polar grids, complex coordinate curves",
            lessons = listOf(
                CourseLesson(
                    title = "Cauchy Residue Theorem",
                    duration = "48 mins",
                    summaryEn = "Unveiling the power of integrating meromorphic functions along closed loops using singularities.",
                    summaryLaTeX = "\$\\oint_{\\gamma} f(z)\\,dz = 2\\pi i \\sum_{k=1}^n Res(f, z_k)\$ which solves complex definite integration over real axis limits."
                ),
                CourseLesson(
                    title = "Argument Principle & Rouche's Theorem",
                    duration = "35 mins",
                    summaryEn = "Counting the number of zeros and poles inside a closed boundary by measuring complex phase winding numbers.",
                    summaryLaTeX = "If \$f\$ and \$g\$ are analytic within/on \$\\gamma\$, with \$|g(z)| < |f(z)|\$ on \$\\gamma\$, then \$f\$ and \$f+g\$ contain identical counts of zeros inside \$\\gamma\$."
                )
            )
        )
    )

    // Complete bilingual mock tests database
    val mockTests = listOf(
        MockTest(
            id = "test_iit_jam_1",
            title = "IIT JAM Real Analysis & Linear Algebra Challenge",
            category = "IIT JAM",
            durationMinutes = 15,
            questions = listOf(
                MathQuestion(
                    id = 101,
                    questionEn = "Let A be a 2x2 matrix with trace(A) = 5 and det(A) = 6. What are the eigenvalues of A?",
                    questionHi = "मान लीजिए A एक 2x2 आव्यूह है जिसका trace(A) = 5 और det(A) = 6 है। A के आइगेन मान क्या हैं?",
                    optionsEn = listOf("λ = 2, 3", "λ = 1, 4", "λ = 5, 1", "λ = 0, 6"),
                    optionsHi = listOf("λ = 2, 3", "λ = 1, 4", "λ = 5, 1", "λ = 0, 6"),
                    correctOptionIndex = 0,
                    explanationEn = "The characteristic equation of a 2x2 matrix is given by: \$\\lambda^2 - \\text{trace}(A)\\lambda + \\text{det}(A) = 0\$. In this case, \$\\lambda^2 - 5\\lambda + 6 = 0\$. Solving yields \$\\lambda = 2, 3\$.",
                    explanationHi = "एक 2x2 आव्यूह का अभिलाक्षणिक समीकरण इस प्रकार दिया जाता है: \$\\lambda^2 - \\text{trace}(A)\\lambda + \\text{det}(A) = 0\$। इस मामले में, \$\\lambda^2 - 5\\lambda + 6 = 0\$। हल करने पर \$\\lambda = 2, 3\$ प्राप्त होता है।"
                ),
                MathQuestion(
                    id = 102,
                    questionEn = "Let V be a vector space of dimension 5. If S is a subset of V containing 6 vectors, then S is:",
                    questionHi = "मान लीजिए V विमा 5 का एक सदिश समष्टि है। यदि S, V का एक उपसमुच्चय है जिसमें 6 सदिश हैं, तो S है:",
                    optionsEn = listOf("Always Linearly Dependent", "Always Linearly Independent", "Spans V", "A Basis of V"),
                    optionsHi = listOf("हमेशा रैखिक रूप से सम्बद्ध (Dependent)", "हमेशा रैखिक रूप से स्वतंत्र (Independent)", "V को विस्तृत (Spans) करता है", "V का एक आधार (Basis)"),
                    correctOptionIndex = 0,
                    explanationEn = "In any vector space V of dimension n, any set containing more than n vectors must be Linearly Dependent. Here, dim(V)=5 and |S|=6 > 5. Thus, S is linearly dependent.",
                    explanationHi = "n विमा वाले किसी भी सदिश समष्टि V में, n से अधिक सदिशों वाला कोई भी समुच्चय रैखिक रूप से सम्बद्ध होना चाहिए। यहाँ, dim(V)=5 और |S|=6 > 5 है। इसलिए, S रैखिक रूप से सम्बद्ध है।"
                ),
                MathQuestion(
                    id = 103,
                    questionEn = "Find the limit of the sequence \$x_n = \\frac{\\sin(n)}{n}\$ as n approaches infinity.",
                    questionHi = "n के अनंत की ओर अग्रसर होने पर अनुक्रम \$x_n = \\frac{\\sin(n)}{n}\$ की सीमा (limit) ज्ञात कीजिए।",
                    optionsEn = listOf("0", "1", "Does not exist", "Infinity"),
                    optionsHi = listOf("0", "1", "अस्तित्व में नहीं है", "अनंत"),
                    correctOptionIndex = 0,
                    explanationEn = "Since \$-1 \\leq \\sin(n) \\leq 1\$, we can sandwich the sequence: \$-\\frac{1}{n} \\leq \\frac{\\sin(n)}{n} \\leq \\frac{1}{n}\$. Since both limits of boundary terms equal 0 as \$n \\to \\infty\$, the sequence limit is 0.",
                    explanationHi = "चूंकि \$-1 \\leq \\sin(n) \\leq 1\$ है, हम Sandwich Theorem द्वारा अनुक्रम को घेर सकते हैं: \$-\\frac{1}{n} \\leq \\frac{\\sin(n)}{n} \\leq \\frac{1}{n}\$। चूंकि दोनों सीमाओं का मान \$n \\to \\infty\$ होने पर 0 होता है, अनुक्रम की सीमा 0 है।"
                )
            )
        ),
        MockTest(
            id = "test_gate_1",
            title = "GATE Mathematics Abstract Algebra & Real Topology Mock",
            category = "GATE",
            durationMinutes = 20,
            questions = listOf(
                MathQuestion(
                    id = 201,
                    questionEn = "Find the number of generators of the cyclic group \$Z_{10}\$.",
                    questionHi = "चक्रीय समूह \$Z_{10}\$ के जनकों (generators) की संख्या ज्ञात कीजिए।",
                    optionsEn = listOf("4", "2", "5", "10"),
                    optionsHi = listOf("4", "2", "5", "10"),
                    correctOptionIndex = 0,
                    explanationEn = "The number of generators of a finite cyclic group \$Z_n\$ is given by Euler's phi function \$\\phi(n)\$. Here, \$\\phi(10) = 10 \\times (1 - \\frac{1}{2}) \\times (1 - \\frac{1}{5}) = 4\$. The generators are {1, 3, 7, 9}.",
                    explanationHi = "एक सीमित चक्रीय समूह \$Z_n\$ के जनकों की संख्या यूलर के फाई फलन \$\\phi(n)\$ द्वारा दी जाती है। यहाँ, \$\\phi(10) = 10 \\times (1 - \\frac{1}{2}) \\times (1 - \\frac{1}{5}) = 4\$। जनक {1, 3, 7, 9} हैं।"
                ),
                MathQuestion(
                    id = 202,
                    questionEn = "Let T: V -> W be a linear transformation. If rank(T) = 4 and nullity(T) = 3, what is the dimension of the domain workspace V?",
                    questionHi = "मान लीजिए T: V -> W एक रैखिक रूपांतरण है। यदि rank(T) = 4 और nullity(T) = 3 है, तो प्रांत (domain) सदिश समष्टि V की विमा क्या है?",
                    optionsEn = listOf("7", "4", "3", "1"),
                    optionsHi = listOf("7", "4", "3", "1"),
                    correctOptionIndex = 0,
                    explanationEn = "By Rank-Nullity Theorem: \$\\text{dim}(V) = \\text{rank}(T) + \\text{nullity}(T)\$. Therefore, \$\\text{dim}(V) = 4 + 3 = 7\$.",
                    explanationHi = "रैंक-शून्यता (Rank-Nullity) प्रमेय के अनुसार: \$\\text{dim}(V) = \\text{rank}(T) + \\text{nullity}(T)\$ है। इसलिए, \$\\text{dim}(V) = 4 + 3 = 7\$।"
                )
            )
        )
    )

    // Mathematical dictionary translations for quick AI lookup
    val mathDictionary = mapOf(
        "Eigenvalue" to Pair("आइगेन मान", "Characteristic value of a matrix satisfying Av = λv."),
        "Linear Independence" to Pair("रैखिक स्वतंत्रता", "Vectors satisfying c1v1 + c2v2 + ... = 0 only when all coefficients are 0."),
        "Trace of Matrix" to Pair("आव्यूह के अनुरेख", "The sum of the diagonal elements of a square matrix."),
        "Normal Subgroup" to Pair("प्रसामान्य उपसमूह", "A subgroup invariant under conjugation: gHg⁻¹ = H for all g in G."),
        "Isomorphism" to Pair("तुल्याकारिता", "A bijective homomorphism preserving algebraic structures between two domains."),
        "Compactness" to Pair("संहतता", "A topological space where every open cover admits a finite subcover."),
        "Invertible Matrix" to Pair("व्युत्क्रमणीय आव्यूह", "A square matrix with determinant not equal to zero.")
    )

    // XP and Gamification modifiers
    fun addXp(amount: Int) {
        viewModelScope.launch {
            val currentState = streakState.value
            val nextXp = currentState.totalXp + amount
            val nextRank = when {
                nextXp < 100 -> "Euler Novice"
                nextXp < 250 -> "Banach Explorer"
                nextXp < 500 -> "Hilbert Pioneer"
                else -> "Fermat Mastermind"
            }
            repository.saveStreakState(
                currentState.copy(
                    totalXp = nextXp,
                    rankTitle = nextRank,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun incrementStreak() {
        viewModelScope.launch {
            val currentState = streakState.value
            repository.saveStreakState(
                currentState.copy(
                    streakCount = currentState.streakCount + 1,
                    lastUpdated = System.currentTimeMillis()
                )
            )
            addXp(50) // Bonus for streak boost
        }
    }

    // Toggle Language for Test engine
    fun toggleLanguage() {
        _isLanguageHindi.value = !_isLanguageHindi.value
    }

    // Interactive fractal controller
    fun updateFractalParams(zoomMultiplier: Float, iterChange: Int) {
        _fractalZoom.value = (_fractalZoom.value * zoomMultiplier).coerceIn(0.1f, 50.0f)
        _fractalMaxIterations.value = (_fractalMaxIterations.value + iterChange).coerceIn(10, 150)
    }

    fun panFractal(dx: Float, dy: Float) {
        _fractalCenterX.value += dx / (_fractalZoom.value * 2.0f)
        _fractalCenterY.value += dy / (_fractalZoom.value * 2.0f)
    }

    fun setFractalOrigin() {
        _fractalZoom.value = 1.0f
        _fractalMaxIterations.value = 40
        _fractalCenterX.value = -0.7f
        _fractalCenterY.value = 0.0f
    }

    // Double Whiteboard Drawings Simulator
    fun addWhiteboardPoint(x: Float, y: Float) {
        val current = _whiteboardDrawings.value.toMutableList()
        current.add(Pair(x, y))
        _whiteboardDrawings.value = current
    }

    fun clearWhiteboard() {
        _whiteboardDrawings.value = emptyList()
    }

    // Call server-side Gemini OCR / Doubt solver
    fun askDoubt(questionText: String, bitmap: Bitmap? = null) {
        viewModelScope.launch {
            _doubtUiState.value = DoubtUiState.Loading
            var imageBase64: String? = null
            if (bitmap != null) {
                try {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
                    val byteArray = stream.toByteArray()
                    imageBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                } catch (e: Exception) {
                    Log.e("MathViewModel", "Error encoding bitmap to base64", e)
                }
            }

            val systemInstruction = """
                You are 'Fractol Frontier Doubt Solver', a premier AI math professor.
                Analyze the question carefully.
                Provide a structured step-by-step solution.
                Cite the core mathematical theorems or definitions applied (e.g. Cauchy Residue, rank-nullity, first Sylow theorem).
                Provide both intuitive context and mathematical rigour.
                Use LaTex notation for formulas, enclosed in single ($) or double ($$) dollar signs.
                End with a 'Key Takeaway' summary box.
            """.trimIndent()

            val answer = repository.queryGeminiMath(questionText, systemInstruction, imageBase64)
            if (answer.startsWith("Error")) {
                _doubtUiState.value = DoubtUiState.Error(answer)
            } else {
                _doubtUiState.value = DoubtUiState.Success(answer)
                // Save to local Room history so students can browse later offline!
                repository.saveDoubt(
                    AiDoubtHistory(
                        question = questionText,
                        imageBase64 = imageBase64,
                        solution = answer
                    )
                )
                // Award XP for solving high-level mathematical doubt!
                addXp(30)
            }
        }
    }

    fun deleteDoubtHistory(id: Int) {
        viewModelScope.launch {
            repository.deleteDoubt(id)
        }
    }

    // Call specialized bilingual translation module
    fun translateMathSentence(inputText: String, targetLanguage: String) {
        viewModelScope.launch {
            _translationUiState.value = TranslationUiState.Loading

            val systemInstruction = """
                You are 'Fractol Bilingual Math Translator'.
                Your task is to translate mathematical theorems, definitions, and questions between English and Hindi, or Hindi and English.
                YOU MUST ABSOLUTELY PRESERVE all mathematical notations, integers, equations, symbols and LaTeX blocks intact! Do NOT translate or mutilate equations.
                Correctly utilize precise mathematical terminology. For example:
                - 'Eigenvalue' translates to 'आइगेन मान'
                - 'Matrix' translates to 'आव्यूह'
                - 'Trace' translates to 'अनुरेख'
                - 'Connected set' translates to 'सम्बद्ध समुच्चय'
                - 'Vector Space' translates to 'सदिश समष्टि'
                Format with gorgeous clear mathematical spacing.
            """.trimIndent()

            val prompt = "Please translate this phrase to $targetLanguage. Output ONLY the pure translated sentence: \"$inputText\""
            val translated = repository.queryGeminiMath(prompt, systemInstruction)

            if (translated.startsWith("Error")) {
                _translationUiState.value = TranslationUiState.Error(translated)
            } else {
                _translationUiState.value = TranslationUiState.Success(inputText, translated)
                addXp(15)
            }
        }
    }

    fun clearTranslationState() {
        _translationUiState.value = TranslationUiState.Idle
    }

    // Test series interactions
    fun startMockTest(test: MockTest) {
        _activeTest.value = test
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _testSecondsRemaining.value = test.durationMinutes * 60
    }

    fun selectTestAnswer(questionIndex: Int, optionIndex: Int) {
        _selectedAnswers.value = _selectedAnswers.value + (questionIndex to optionIndex)
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun nextQuestion() {
        val maxIndex = _activeTest.value?.questions?.size?.minus(1) ?: 0
        if (_currentQuestionIndex.value < maxIndex) {
            _currentQuestionIndex.value += 1
        }
    }

    fun submitMockTest() {
        val test = _activeTest.value ?: return
        val answers = _selectedAnswers.value
        var correct = 0
        test.questions.forEachIndexed { index, question ->
            if (answers[index] == question.correctOptionIndex) {
                correct++
            }
        }

        val percentage = if (test.questions.isNotEmpty()) (correct * 100) / test.questions.size else 0
        val earnedXp = correct * 40 + 20 // 40 XP per correct question + 20 participation

        val attempt = MockTestAttempt(
            examCategory = test.category,
            testTitle = test.title,
            score = percentage,
            totalQuestions = test.questions.size,
            correctAnswers = correct,
            timeTakenSeconds = (test.durationMinutes * 60) - _testSecondsRemaining.value
        )

        viewModelScope.launch {
            repository.saveAttempt(attempt)
            addXp(earnedXp)
        }

        // Close test
        _activeTest.value = null
    }

    fun cancelActiveTest() {
        _activeTest.value = null
    }

    // Video Course Bookmarks logic
    fun toggleBookmark(courseId: String, lessonTitle: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                repository.deleteBookmark(courseId, lessonTitle)
            } else {
                repository.saveBookmark(CourseBookmark(courseId = courseId, lessonTitle = lessonTitle))
            }
        }
    }

    // Decrement test timer (tick called from Composable loop)
    fun tickTimer() {
        if (_testSecondsRemaining.value > 0) {
            _testSecondsRemaining.value -= 1
        } else if (_activeTest.value != null) {
            submitMockTest()
        }
    }
}
