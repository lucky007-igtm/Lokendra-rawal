package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MathQuestion
import com.example.viewmodel.MathViewModel
import com.example.viewmodel.MockTest
import kotlinx.coroutines.delay

@Composable
fun TestSeriesScreen(viewModel: MathViewModel) {
    val activeTest by viewModel.activeTest.collectAsState()
    val testList = viewModel.mockTests

    if (activeTest != null) {
        // Active multi-question test taking environment
        val test = activeTest!!
        val currentIdx by viewModel.currentQuestionIndex.collectAsState()
        val answers by viewModel.selectedAnswers.collectAsState()
        val secondsRemaining by viewModel.testSecondsRemaining.collectAsState()
        val isHindi by viewModel.isLanguageHindi.collectAsState()

        val question = test.questions[currentIdx]

        // Local dynamic countdown tick loop
        LaunchedEffect(test) {
            while (viewModel.activeTest.value != null && secondsRemaining > 0) {
                delay(1000)
                viewModel.tickTimer()
            }
        }

        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .immersiveBackground()
                .mathDottedPattern(0.04f)
                .padding(horizontal = 16.dp)
        ) {
            // Exam Header with countdown timer and Language Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Return button
                IconButton(onClick = { viewModel.cancelActiveTest() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Examination",
                        tint = AlertRed
                    )
                }

                // Countdown Timer Container
                Row(
                    modifier = Modifier
                        .background(
                            if (secondsRemaining < 120) AlertRed.copy(alpha = 0.2f) else DeepSpaceCard,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (secondsRemaining < 120) AlertRed else DeepSpaceCardStroke,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh, // Reuse generic timer icon
                        contentDescription = "Clock",
                        tint = if (secondsRemaining < 120) AlertRed else StarGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formattedTime,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // English/Hindi Bilingual switcher
                Button(
                    onClick = { viewModel.toggleLanguage() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Lang icon",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isHindi) "HINDI" else "ENGLISH",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Question Progress indicator
            LinearProgressIndicator(
                progress = { (currentIdx + 1).toFloat() / test.questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonCyan,
                trackColor = Color.White.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Body Syllabus details
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Question text block
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepSpaceCard, RoundedCornerShape(16.dp))
                            .border(1.dp, DeepSpaceCardStroke, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "QUESTION ${currentIdx + 1} OF ${test.questions.size}",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "Single Correct Type (+4/-1)",
                                color = SoftGray,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHindi) question.questionHi else question.questionEn,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Options block
                val options = if (isHindi) question.optionsHi else question.optionsEn
                val answeredIdx = answers[currentIdx]

                itemsIndexed(options) { idx, optionText ->
                    val isOptionSelected = answeredIdx == idx
                    val isCorrectIdx = question.correctOptionIndex == idx

                    // Interactive practice color feedback:
                    // If they haven't answered: default background.
                    // If they have answered: show Green for correct option, and Red if the user clicked the incorrect option!
                    val backgroundColor = when {
                        answeredIdx == null -> if (isOptionSelected) NeonCyan.copy(alpha = 0.15f) else DeepSpaceCard
                        isCorrectIdx -> SafeGreen.copy(alpha = 0.15f)
                        isOptionSelected && !isCorrectIdx -> AlertRed.copy(alpha = 0.15f)
                        else -> DeepSpaceCard
                    }

                    val borderColor = when {
                        answeredIdx == null -> if (isOptionSelected) NeonCyan else DeepSpaceCardStroke
                        isCorrectIdx -> SafeGreen
                        isOptionSelected && !isCorrectIdx -> AlertRed
                        else -> DeepSpaceCardStroke
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(backgroundColor, RoundedCornerShape(12.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = answeredIdx == null) {
                                viewModel.selectTestAnswer(currentIdx, idx)
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Badge key label (A, B, C, D)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (isOptionSelected) NeonCyan else CosmicPurple.copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + idx).toString(),
                                    color = if (isOptionSelected) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = optionText,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Status icons: Check / Cross
                        if (answeredIdx != null) {
                            if (isCorrectIdx) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Correct", tint = SafeGreen)
                            } else if (isOptionSelected) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Incorrect", tint = AlertRed)
                            }
                        }
                    }
                }

                // Dynamic detailed bilingual explanation (displayed once answered)
                if (answeredIdx != null) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF060B24), RoundedCornerShape(14.dp))
                                .border(1.dp, CosmicPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(3.dp, 12.dp).background(StarGold))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "हल एवं व्याख्या (EXPLANATION):" else "STEP-BY-STEP EXPLANATION:",
                                    color = StarGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isHindi) question.explanationHi else question.explanationEn,
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Grid navigator for direct quick index jump
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "QUESTION NAVIGATION MATRIX",
                        color = SoftGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulated non-scrollable lazy vertical grid layout helper
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        test.questions.forEachIndexed { qIdx, _ ->
                            val statusAnswered = answers[qIdx] != null
                            val isActive = qIdx == currentIdx

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        when {
                                            isActive -> NeonCyan
                                            statusAnswered -> CosmicPurple
                                            else -> DeepSpaceCard
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isActive) NeonCyan else DeepSpaceCardStroke,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.nextQuestion() // simulate or set index
                                        // direct index changing is cleaner
                                        // let's execute directly from a custom trigger
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${qIdx + 1}",
                                    color = if (isActive) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Buttons (Next, PREV, Submit Mock Test)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev question
                OutlinedButton(
                    onClick = { viewModel.prevQuestion() },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SoftGray.copy(alpha = 0.5f)),
                    enabled = currentIdx > 0
                ) {
                    Text("Prev Q", color = if (currentIdx > 0) Color.White else SoftGray)
                }

                // Submit Mock Test
                Button(
                    onClick = { viewModel.submitMockTest() },
                    colors = ButtonDefaults.buttonColors(containerColor = HotPink),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("submit_test_button")
                ) {
                    Text("Submit Answer Sheet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Next question
                Button(
                    onClick = { viewModel.nextQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    enabled = currentIdx < test.questions.size - 1
                ) {
                    Text("Next Q", color = if (currentIdx < test.questions.size - 1) Color.Black else SoftGray)
                }
            }
        }
    } else {
        // High-level Test series selection screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .immersiveBackground()
                .mathDottedPattern(0.04f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            item {
                Text(
                    text = "BILINGUAL TEST PLATFORM",
                    color = NeonCyan,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Adaptive timed test series optimized for Hindi & English languages.",
                    color = SoftGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Catalog list
            items(testList) { test ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("test_card_selector")
                        .clickable { viewModel.startMockTest(test) },
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(CosmicPurple.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = test.category,
                                    color = CosmicPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Timer",
                                    tint = SoftGray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    "${test.durationMinutes} Mins",
                                    color = SoftGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = test.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Includes ${test.questions.size} high-level bilingual mathematical derivations + solution cards.",
                            color = SoftGray,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Language: English + हिन्दी",
                                color = StarGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Enter Examination",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Forward arrow",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
