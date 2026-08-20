package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.R
import com.example.data.model.GeneratedPattern
import com.example.data.model.PatternStepInstruction
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class AssistantMessage(
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Content(
    val role: String? = null,
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

data class Candidate(
    val content: Content
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitGeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

class GeminiAssistantViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                sender = "assistant",
                text = "Hello! I'm Stitch Mind AI, your crochet companion. Ask me questions, generate custom crochet patterns with step-by-step row instructions, or troubleshoot stitches!"
            )
        )
    )
    val messages: StateFlow<List<AssistantMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- AI Pattern Generation State ---
    private val _isGeneratingPattern = MutableStateFlow(false)
    val isGeneratingPattern: StateFlow<Boolean> = _isGeneratingPattern.asStateFlow()

    private val _generatedPattern = MutableStateFlow<GeneratedPattern?>(
        // Sample starter pattern ready with visual sample photo
        GeneratedPattern(
            title = "Spring Floral Coaster & Motif",
            description = "A delicate 5-round blossom coaster featuring textured petal clusters, double crochet shells, and clean scalloped edging.",
            category = "Home Decor",
            skillLevel = "Beginner to Intermediate",
            hookSize = "4.0 mm (US G/6)",
            yarnWeight = "DK or Worsted Cotton Yarn",
            estimatedHours = 2,
            stitchesUsed = listOf("ch (chain)", "sl st (slip stitch)", "sc (single crochet)", "dc (double crochet)", "cluster (3-dc cluster)"),
            patternNotes = "Work in continuous or joined rounds. Fasten off with invisible join for cleanest look.",
            instructions = listOf(
                PatternStepInstruction(1, "Make a Magic Ring. Ch 2 (counts as first dc), work 11 dc into the ring. Sl st to top of initial ch 2 to join.", 12),
                PatternStepInstruction(2, "Ch 1, 2 sc in each dc around. Sl st to first sc.", 24),
                PatternStepInstruction(3, "[Ch 3, skip 1 st, sl st in next st] repeat around to form 12 petal arches.", 12),
                PatternStepInstruction(4, "In each ch-3 space work: (1 sc, 1 hdc, 3 dc, 1 hdc, 1 sc) to form petals.", 36),
                PatternStepInstruction(5, "Ch 1, sl st across each stitch with light tension for crisp scalloped edge. Fasten off and weave in ends.", 36)
            ),
            sampleImageRes = R.drawable.img_pattern_sample_1787184272896
        )
    )
    val generatedPattern: StateFlow<GeneratedPattern?> = _generatedPattern.asStateFlow()

    private val _patternError = MutableStateFlow<String?>(null)
    val patternError: StateFlow<String?> = _patternError.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = AssistantMessage(sender = "user", text = userText)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            val responseText = queryGemini(userText)
            _messages.value = _messages.value + AssistantMessage(sender = "assistant", text = responseText)
            _isLoading.value = false
        }
    }

    fun generateCrochetPattern(prompt: String, category: String = "Crochet Project") {
        if (prompt.isBlank()) return

        _isGeneratingPattern.value = true
        _patternError.value = null

        viewModelScope.launch {
            try {
                val apiKey = try {
                    BuildConfig.GEMINI_API_KEY
                } catch (e: Exception) {
                    ""
                }

                val promptClean = prompt.trim()

                // Pick an appropriate sample picture based on user prompt keywords
                val sampleImageRes = when {
                    promptClean.contains("beanie", ignoreCase = true) ||
                    promptClean.contains("hat", ignoreCase = true) ||
                    promptClean.contains("cap", ignoreCase = true) ||
                    promptClean.contains("scarf", ignoreCase = true) -> R.drawable.img_pattern_beanie_1787184284231
                    else -> R.drawable.img_pattern_sample_1787184272896
                }

                if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                    // Fallback generator when API key is not populated
                    val defaultPattern = createFallbackPattern(promptClean, category, sampleImageRes)
                    _generatedPattern.value = defaultPattern
                } else {
                    val systemInstructionText = """
                        You are Stitch Mind's expert crochet designer and master pattern writer.
                        Generate a complete, clear, and structured crochet pattern for the user's prompt.
                        Format your answer with clear sections:
                        - TITLE: [Pattern Name]
                        - DESCRIPTION: [Brief aesthetic overview]
                        - DIFFICULTY: [Beginner, Easy, Intermediate, or Advanced]
                        - HOOK: [Hook size e.g. 4.5 mm (US 7)]
                        - YARN: [Recommended yarn weight and fiber]
                        - STITCHES: [Comma-separated list of stitches]
                        - NOTES: [Key tips, gauge, or sizing notes]
                        - STEPS:
                        Round 1: [Instruction] (Stitch count: X)
                        Round 2: [Instruction] (Stitch count: Y)
                        ...
                    """.trimIndent()

                    val request = GenerateContentRequest(
                        systemInstruction = Content(
                            parts = listOf(Part(text = systemInstructionText))
                        ),
                        contents = listOf(
                            Content(parts = listOf(Part(text = "Design a crochet pattern for: $promptClean. Category: $category")))
                        )
                    )

                    val response = RetrofitGeminiClient.service.generateContent(apiKey, request)
                    val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                    if (rawText != null) {
                        val parsed = parsePatternFromText(rawText, promptClean, category, sampleImageRes)
                        _generatedPattern.value = parsed
                    } else {
                        _generatedPattern.value = createFallbackPattern(promptClean, category, sampleImageRes)
                    }
                }
            } catch (e: Exception) {
                val sampleImageRes = if (prompt.contains("beanie", ignoreCase = true) || prompt.contains("hat", ignoreCase = true)) {
                    R.drawable.img_pattern_beanie_1787184284231
                } else {
                    R.drawable.img_pattern_sample_1787184272896
                }
                _generatedPattern.value = createFallbackPattern(prompt, category, sampleImageRes)
            } finally {
                _isGeneratingPattern.value = false
            }
        }
    }

    private fun parsePatternFromText(
        rawText: String,
        fallbackTitle: String,
        category: String,
        sampleImageRes: Int
    ): GeneratedPattern {
        var title = fallbackTitle
        var description = "Custom crochet pattern created by Stitch Mind AI."
        var skillLevel = "Intermediate"
        var hookSize = "4.5 mm (US 7)"
        var yarnWeight = "Worsted (Weight 4)"
        val stitchesList = mutableListOf<String>()
        val instructions = mutableListOf<PatternStepInstruction>()
        var notes = ""

        val lines = rawText.lines()
        var currentStepNum = 1
        var parsingSteps = false

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("TITLE:", ignoreCase = true) -> {
                    title = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("DESCRIPTION:", ignoreCase = true) -> {
                    description = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("DIFFICULTY:", ignoreCase = true) -> {
                    skillLevel = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("HOOK:", ignoreCase = true) -> {
                    hookSize = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("YARN:", ignoreCase = true) -> {
                    yarnWeight = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("STITCHES:", ignoreCase = true) -> {
                    val rawStitches = trimmed.substringAfter(":")
                    stitchesList.addAll(rawStitches.split(",").map { it.trim() })
                }
                trimmed.startsWith("NOTES:", ignoreCase = true) -> {
                    notes = trimmed.substringAfter(":").trim()
                }
                trimmed.startsWith("STEPS:", ignoreCase = true) -> {
                    parsingSteps = true
                }
                parsingSteps && (trimmed.startsWith("Round", ignoreCase = true) || trimmed.startsWith("Row", ignoreCase = true) || trimmed.matches(Regex("""^\d+[\.:\)].*"""))) -> {
                    val stepText = trimmed
                    val stitchCountMatch = Regex("""\(.*?(\d+)\s*(?:sts?|stitches|st)?\)""", RegexOption.IGNORE_CASE).find(stepText)
                    val count = stitchCountMatch?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
                    instructions.add(
                        PatternStepInstruction(
                            roundOrRow = currentStepNum++,
                            instruction = stepText,
                            stitchCount = count
                        )
                    )
                }
            }
        }

        if (instructions.isEmpty()) {
            // Fallback steps if formatting was freeform
            val nonBlankLines = lines.filter { it.isNotBlank() && (it.contains("ch", ignoreCase = true) || it.contains("dc", ignoreCase = true) || it.contains("sc", ignoreCase = true)) }
            if (nonBlankLines.isNotEmpty()) {
                nonBlankLines.forEachIndexed { index, lineStr ->
                    instructions.add(PatternStepInstruction(index + 1, lineStr.trim()))
                }
            } else {
                instructions.addAll(listOf(
                    PatternStepInstruction(1, "Create foundation chain or Magic Ring to begin project.", 12),
                    PatternStepInstruction(2, "Work primary stitch repeat as detailed in pattern guidance.", 24),
                    PatternStepInstruction(3, "Complete body rows maintaining consistent tension.", 24),
                    PatternStepInstruction(4, "Fasten off and neatly weave in all yarn tails.", 24)
                ))
            }
        }

        return GeneratedPattern(
            title = if (title.isNotBlank()) title else fallbackTitle,
            description = if (description.isNotBlank()) description else "Custom AI designed crochet project.",
            category = category,
            skillLevel = skillLevel,
            hookSize = hookSize,
            yarnWeight = yarnWeight,
            estimatedHours = if (instructions.size > 8) 6 else 3,
            stitchesUsed = if (stitchesList.isNotEmpty()) stitchesList else listOf("ch", "sc", "dc", "sl st"),
            patternNotes = if (notes.isNotBlank()) notes else "Ensure proper gauge before commencing work.",
            instructions = instructions,
            sampleImageRes = sampleImageRes
        )
    }

    private fun createFallbackPattern(prompt: String, category: String, sampleImageRes: Int): GeneratedPattern {
        val isHat = prompt.contains("beanie", ignoreCase = true) || prompt.contains("hat", ignoreCase = true) || prompt.contains("cap", ignoreCase = true)
        val isFlower = prompt.contains("flower", ignoreCase = true) || prompt.contains("floral", ignoreCase = true) || prompt.contains("coaster", ignoreCase = true)
        
        return if (isHat) {
            GeneratedPattern(
                title = if (prompt.length < 35) prompt.replaceFirstChar { it.uppercase() } else "Classic Ribbed Cozy Beanie",
                description = "A warm, textured ribbed beanie crocheted flat in back loops only, seamed seamlessly and cinched with a fluffy top crown.",
                category = "Apparel",
                skillLevel = "Easy / Beginner",
                hookSize = "5.5 mm (US I/9)",
                yarnWeight = "Chunky / Bulky (Weight 5)",
                estimatedHours = 3,
                stitchesUsed = listOf("ch (chain)", "hdc (half double crochet)", "blo (back loop only)", "sl st (slip stitch)"),
                patternNotes = "Crocheted flat as a rectangle. The elasticity comes from working exclusively in back loops.",
                instructions = listOf(
                    PatternStepInstruction(1, "Ch 38 loosely for standard adult height (approx 10 inches).", 38),
                    PatternStepInstruction(2, "Hdc in 3rd ch from hook and in each ch across. Ch 2, turn.", 36),
                    PatternStepInstruction(3, "Work 1 hdc in BLO (back loop only) of each st across, except work regular hdc into the very last st for neat edges. Ch 2, turn.", 36),
                    PatternStepInstruction(4, "Repeat Row 3 until piece measures approx 19-20 inches un-stretched (approx 36-40 rows).", 36),
                    PatternStepInstruction(5, "Fold rectangle in half and sl st both ends together into a tube.", 36),
                    PatternStepInstruction(6, "Thread yarn needle through the top edge, cinch tight, knot securely, and attach pompom.", 1)
                ),
                sampleImageRes = sampleImageRes
            )
        } else if (isFlower) {
            GeneratedPattern(
                title = if (prompt.length < 35) prompt.replaceFirstChar { it.uppercase() } else "Petal Blossom Coaster & Motif",
                description = "A delightful blooming circular flower motif ideal for coasters, blanket squares, or wall hangings.",
                category = "Home Decor",
                skillLevel = "Beginner",
                hookSize = "4.0 mm (US G/6)",
                yarnWeight = "Worsted Cotton (Weight 4)",
                estimatedHours = 2,
                stitchesUsed = listOf("MR (magic ring)", "sc (single crochet)", "dc (double crochet)", "cluster", "sl st"),
                patternNotes = "Use cotton yarn for best heat resistance and structure if utilizing as a mug rug.",
                instructions = listOf(
                    PatternStepInstruction(1, "Make a Magic Ring. Ch 2, work 11 dc into the ring. Sl st to join.", 12),
                    PatternStepInstruction(2, "Ch 1, 2 sc in each dc around. Sl st to first sc.", 24),
                    PatternStepInstruction(3, "[Ch 3, skip 1 st, sl st in next st] repeat 12 times to form petal base arches.", 12),
                    PatternStepInstruction(4, "In each ch-3 space work: (1 sc, 1 hdc, 3 dc, 1 hdc, 1 sc).", 36),
                    PatternStepInstruction(5, "Sl st around each petal edge for clean finish. Fasten off.", 36)
                ),
                sampleImageRes = sampleImageRes
            )
        } else {
            GeneratedPattern(
                title = if (prompt.length < 35) prompt.replaceFirstChar { it.uppercase() } else "Custom Artisan Crochet Design",
                description = "AI crafted bespoke pattern featuring modern texture, balanced stitch counts, and clean finished borders for: $prompt",
                category = category,
                skillLevel = "Intermediate",
                hookSize = "4.5 mm (US 7)",
                yarnWeight = "Worsted Yarn (Weight 4)",
                estimatedHours = 4,
                stitchesUsed = listOf("ch (chain)", "sc (single crochet)", "hdc (half double crochet)", "dc (double crochet)"),
                patternNotes = "Maintain even tension throughout. Check stitch counts at end of each repeat.",
                instructions = listOf(
                    PatternStepInstruction(1, "Ch 25 or desired base width. Turn.", 25),
                    PatternStepInstruction(2, "Sc in 2nd ch from hook and each ch across. Ch 1, turn.", 24),
                    PatternStepInstruction(3, "Work alternating (1 sc, 1 dc) across row to create rich pebble texture. Ch 1, turn.", 24),
                    PatternStepInstruction(4, "Repeat row 3 for 20 rows or until desired dimension is achieved.", 24),
                    PatternStepInstruction(5, "Work 1 round of sc evenly around all 4 outer edges with (sc, ch 1, sc) in each corner. Fasten off.", 96)
                ),
                sampleImageRes = sampleImageRes
            )
        }
    }

    private suspend fun queryGemini(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "I can help you with stitches, pattern conversions, and row counters! You can also use the 'AI Pattern Generator' tab above to design custom crochet patterns with sample photos."
        }

        val request = GenerateContentRequest(
            systemInstruction = Content(
                parts = listOf(
                    Part(
                        text = "You are Stitch Mind AI, a friendly, warm, and highly knowledgeable crochet and knitting assistant. " +
                                "Provide clear, concise, step-by-step crochet guidance, explain stitch abbreviations (sc, hdc, dc, tr, MR, inc, dec), " +
                                "suggest yarn weights and hook sizes, and offer helpful troubleshooting advice for makers."
                    )
                )
            ),
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )

        try {
            val response = RetrofitGeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I couldn't generate a response. Please try rephrasing your question!"
        } catch (e: Exception) {
            "Note: Unable to connect to AI server (${e.localizedMessage ?: "Network error"}). Check your connection or try again!"
        }
    }
}
