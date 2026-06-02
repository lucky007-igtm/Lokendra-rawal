package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.DoubtUiState
import com.example.viewmodel.MathViewModel
import com.example.viewmodel.TranslationUiState

@Composable
fun DoubtSolverScreen(viewModel: MathViewModel) {
    var activeSubTab by remember { mutableStateOf("DOUBT_SOLVER") } // DOUBT_SOLVER, TRANSLATOR

    val doubtHistory by viewModel.doubtHistory.collectAsState()
    val doubtUiState by viewModel.doubtUiState.collectAsState()

    val translationUiState by viewModel.translationUiState.collectAsState()

    // Form inputs
    var typedProblem by remember { mutableStateOf("") }
    var translationText by remember { mutableStateOf("") }
    var targetLang by remember { mutableStateOf("HINDI") } // HINDI, ENGLISH

    // Prepopulated quick scanned question buttons to speed up prototype usability
    val sampleQuestions = listOf(
        "Find the eigenvalues of matrix A = [[2, 1], [0, 3]]",
        "Prove that any group of prime order is cyclic.",
        "Is the function f(x) = |x| differentiable at x = 0?",
        "State and derive Euler's formula for complex exponentials."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .immersiveBackground()
            .mathDottedPattern(0.04f)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Mode Selector Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepSpaceCard, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (activeSubTab == "DOUBT_SOLVER") CosmicPurple else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeSubTab = "DOUBT_SOLVER" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Solver",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "AI Doubt Solver",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (activeSubTab == "TRANSLATOR") CosmicPurple else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeSubTab = "TRANSLATOR" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Translator",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Math Translator",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (activeSubTab == "DOUBT_SOLVER") {
            // AI DOUBT SOLVER UI
            item {
                Text(
                    text = "AI HOLOGRAPHIC CHAT TUTOR",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Solve handwritten formula snaps, equations, and derivations instantly",
                    color = SoftGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Chat input card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = typedProblem,
                            onValueChange = { typedProblem = it },
                            placeholder = { Text("Type mathematical equations or questions...", color = SoftGray.copy(alpha = 0.6f), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("typed_doubt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = SoftGray.copy(alpha = 0.3f),
                                focusedLabelColor = NeonCyan
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Camera scan simulator button
                            Button(
                                onClick = {
                                    // Generate a mock bitmap with handwritten math equations
                                    val bitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(bitmap)
                                    canvas.drawColor(android.graphics.Color.DKGRAY)
                                    val paint = Paint().apply {
                                        color = android.graphics.Color.CYAN
                                        textSize = 24f
                                    }
                                    canvas.drawText("∫ cos(x)/x dx", 50f, 55f, paint)

                                    typedProblem = "Analyze this snapped handwritten integration proof: ∫ cos(x)/x dx"
                                    viewModel.askDoubt(typedProblem, bitmap)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepSpaceDb),
                                border = BorderStroke(1.dp, HotPink.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Cam snap", tint = HotPink, modifier = Modifier.size(14.dp))
                                    Text("Snap OCR Formula", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Solve Button
                            Button(
                                onClick = {
                                    if (typedProblem.isNotBlank()) {
                                        viewModel.askDoubt(typedProblem, null)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("solve_doubt_button"),
                                enabled = typedProblem.isNotBlank()
                            ) {
                                Text("Ask Tutor", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick template buttons
            item {
                Text(
                    text = "POPULAR DEBATES & STRUCTURED QUESTIONS",
                    color = SoftGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleQuestions.forEach { query ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0C112E), RoundedCornerShape(8.dp))
                                .border(1.dp, DeepSpaceCardStroke, RoundedCornerShape(8.dp))
                                .clickable {
                                    typedProblem = query
                                    viewModel.askDoubt(query, null)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(query, color = NeonCyan, fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Active Solve Output Results
            item {
                when (doubtUiState) {
                    is DoubtUiState.Loading -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "AI Engine Analyzing Equation...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Decoding notation strings & generating step-by-step mathematical proofs",
                                    color = SoftGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    is DoubtUiState.Success -> {
                        val solution = (doubtUiState as DoubtUiState.Success).solution
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("solved_doubt_success_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF060B24)),
                            border = BorderStroke(1.dp, NeonCyan)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(SafeGreen, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "AI STEP-BY-STEP DERIVATION",
                                            color = SafeGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    IconButton(onClick = { typedProblem = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear solver", tint = SoftGray, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = solution,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    is DoubtUiState.Error -> {
                        val error = (doubtUiState as DoubtUiState.Error).message
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, AlertRed)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("SOLVER CRITICAL WARNING", color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(error, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    DoubtUiState.Idle -> {}
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Saved doubts list
            item {
                Text(
                    "OFFLINE REVIEW DIARY (${doubtHistory.size})",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (doubtHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepSpaceCard)
                    ) {
                        Text(
                            "No doubts saved in offline memory yet. Click 'Ask Tutor' above to start.",
                            color = SoftGray,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(doubtHistory) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                        border = BorderStroke(1.dp, DeepSpaceCardStroke)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Math Doubt history log",
                                    color = CosmicPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = { viewModel.deleteDoubtHistory(item.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = SoftGray, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Q: ${item.question}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                item.solution,
                                color = SoftGray,
                                fontSize = 11.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

        } else {
            // TRANSLATOR TAB
            item {
                Text(
                    text = "BILINGUAL CONTEXT TRANSLATOR",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Translate theorems, proofs, definitions between Hindi & English while preserving equations intact.",
                    color = SoftGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Translator card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = translationText,
                            onValueChange = { translationText = it },
                            placeholder = { Text("Enter theorem or definition to translate (e.g. Find the eigenvalues of matrix A...)", color = SoftGray.copy(alpha = 0.6f), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("translation_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = SoftGray.copy(alpha = 0.3f),
                                focusedLabelColor = NeonCyan
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Target Language Selector Toggle
                            Row(
                                modifier = Modifier
                                    .background(DeepSpaceDb, RoundedCornerShape(8.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("HINDI", "ENGLISH").forEach { lang ->
                                    val isSelected = targetLang == lang
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) CosmicPurple else Color.Transparent,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { targetLang = lang }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = lang,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Solve Button
                            Button(
                                onClick = {
                                    if (translationText.isNotBlank()) {
                                        viewModel.translateMathSentence(translationText, targetLang)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("translate_button"),
                                enabled = translationText.isNotBlank()
                            ) {
                                Text("Translate", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Translation Output Box
            item {
                when (translationUiState) {
                    is TranslationUiState.Loading -> {
                        CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(24.dp))
                    }

                    is TranslationUiState.Success -> {
                        val state = translationUiState as TranslationUiState.Success
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("translated_success_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B143A)),
                            border = BorderStroke(1.dp, SafeGreen)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("BILINGUAL TRANSLATION COMPLETED", color = SafeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { viewModel.clearTranslationState() },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SoftGray, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Original: \"${state.originalText}\"",
                                    color = SoftGray,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Bilingual Out: \"${state.translatedText}\"",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    is TranslationUiState.Error -> {
                        Text(
                            text = (translationUiState as TranslationUiState.Error).message,
                            color = AlertRed,
                            fontSize = 12.sp
                        )
                    }

                    TranslationUiState.Idle -> {}
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Bilingual Dictionary Lookups
            item {
                Text(
                    "STATIC MATHEMATICAL GLOSSARY",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val glossary = viewModel.mathDictionary.toList()
            items(glossary) { (term, pair) ->
                val (hindiTerm, description) = pair
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                term,
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .background(CosmicPurple.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(hindiTerm, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(description, color = SoftGray, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
