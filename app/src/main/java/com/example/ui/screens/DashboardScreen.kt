package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MathViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    viewModel: MathViewModel,
    onNavigateToCourses: () -> Unit,
    onNavigateToTests: () -> Unit
) {
    val streak by viewModel.streakState.collectAsState()
    val attempts by viewModel.testAttempts.collectAsState()

    // Animation for neon ambient glow in hero banner
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val bannerGlowOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow"
    )

    // Wave audio indicator for Simulated Live Session
    val waveAnimation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            waveAnimation.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            waveAnimation.animateTo(0f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .immersiveBackground()
            .mathDottedPattern(0.04f)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // App header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FRONTIER PHASE I",
                        color = NeonCyan,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("FRACTOL")
                            withStyle(style = SpanStyle(color = CosmicPurple)) {
                                append(".")
                            }
                        },
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Entering Dimension of Advanced Mathematics",
                        color = SoftGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Streak Banner Button
                Row(
                    modifier = Modifier
                        .background(DeepSpaceCard.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .border(1.dp, DeepSpaceCardStroke, RoundedCornerShape(12.dp))
                        .clickable { viewModel.incrementStreak() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Streak Fire",
                        tint = StarGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${streak.streakCount} Days",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Gamified Dashboard Stats Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // XP Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("xp_stat_card"),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CosmicPurple.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "XP Icon",
                                tint = CosmicPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(text = "Total XP", color = SoftGray, fontSize = 11.sp)
                            Text(
                                text = "${streak.totalXp} XP",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Rank Title Card
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("rank_stat_card"),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(NeonCyan.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Rank Icon",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(text = "Space Rank", color = SoftGray, fontSize = 11.sp)
                            Text(
                                text = streak.rankTitle,
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Hero Cinematic Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .drawBehind {
                        // Ambient sliding gradient ray
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonCyan.copy(alpha = 0.15f), Color.Transparent),
                                center = Offset(bannerGlowOffset, 150f),
                                radius = 250f
                            )
                        )
                        // Diagonal purple overlay
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(CosmicPurple.copy(alpha = 0.1f), Color.Transparent),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, size.height)
                            )
                        )
                    }
                    .background(Color(0xFF070B1F))
                    .padding(20.dp)
            ) {
                // Floating complex math symbols
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "∂f/∂x = ∫ e^(-x²) dx",
                        color = NeonCyan.copy(alpha = 0.08f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = (-5).dp)
                    )
                    Text(
                        text = "∇²Ψ = EΨ",
                        color = CosmicPurple.copy(alpha = 0.08f),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 40.dp, y = 10.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter the Frontier of Maths",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rigorous interactive preparation for IIT JAM, GATE & CSIR NET.",
                        color = SoftGray,
                        fontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCourses,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("hero_explore_courses")
                        ) {
                            Text("Explore Courses", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNavigateToTests,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Mock Tests", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live interactive classroom layout demo
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(HotPink, CircleShape)
                    )
                    Text(
                        text = "LIVE COLLABORATIVE CLASSROOM",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // WebRTC status badge
                Row(
                    modifier = Modifier
                        .background(HotPink.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Live",
                        tint = HotPink,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("HQ WebRTC", color = HotPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Simulated premium futuristic live card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_class_spotlight"),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, HotPink.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Teacher spotlight and description
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, CosmicPurple, CircleShape)
                                .background(DeepSpaceDb),
                            contentAlignment = Alignment.Center
                        ) {
                            // Avatar simulator with mathematical symbol
                            Text("Σ", color = CosmicPurple, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "MSc / CSIR NET Entrance Preparation",
                                color = HotPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Topic: Bilinear Forms & Spectral Theorems",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "By Prof. Cauchy Deshmukh (IIT Bombay)",
                                color = SoftGray,
                                fontSize = 11.sp
                            )
                        }

                        // Simulated audio wave height based on animated offset
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(20.dp)
                        ) {
                            listOf(0.4f, 0.8f, 1.0f, 0.5f, 0.7f).forEach { scale ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .fillMaxHeight(waveAnimation.value * scale + 0.1f)
                                        .background(HotPink, RoundedCornerShape(1.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Collaborative Live Whiteboard
                    Text(
                        "Interactive Whiteboard Workspace (Try drawing formulas below)",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val drawings by viewModel.whiteboardDrawings.collectAsState()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(DeepSpaceDb, RoundedCornerShape(12.dp))
                            .border(1.dp, DeepSpaceCardStroke, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.addWhiteboardPoint(
                                        change.position.x,
                                        change.position.y
                                    )
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw preloaded matrix template on whiteboard background
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.argb(30, 0, 240, 255)
                                    textSize = 34f
                                    isFakeBoldText = true
                                }
                                drawText("H |v⟩ = E |v⟩", 80f, 120f, paint)
                                drawText("[ A ] x = b", size.width - 240f, 180f, paint)
                            }

                            // Render drawings
                            if (drawings.isNotEmpty()) {
                                val path = Path().apply {
                                    moveTo(drawings.first().first, drawings.first().second)
                                    for (i in 1 until drawings.size) {
                                        lineTo(drawings[i].first, drawings[i].second)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = NeonCyan,
                                    style = Stroke(width = 6f)
                                )
                            }
                        }

                        // Clear Board button layered on top
                        IconButton(
                            onClick = { viewModel.clearWhiteboard() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear board",
                                tint = SoftGray.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (drawings.isEmpty()) {
                            Text(
                                "✍️ Use your fingers to derive equations",
                                color = SoftGray.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Collaborative active poll
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF13112E), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Quick Live Quiz: Is the infinite matrix G compact?", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.addXp(15) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CosmicPurple),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Yes (82%)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.addXp(15) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepSpaceDb),
                                    border = BorderStroke(1.dp, SoftGray.copy(alpha = 0.3f)),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("No (18%)", fontSize = 9.sp, color = SoftGray)
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information",
                            tint = SoftGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Student Testimonials / Achievement logs
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "PREPARATION ACHIEVEMENTS & HISTORY",
                color = SoftGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (attempts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Testing badge",
                            tint = NeonCyan.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "No mock tests attempted yet.",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Take a CSIR NET or IIT JAM bilingual mock test to generate statistical learning curves.",
                            color = SoftGray,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    attempts.forEach { attempt ->
                        Card(
                            modifier = Modifier
                                .width(200.dp)
                                .testTag("achievement_attempt_card"),
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    attempt.examCategory,
                                    color = NeonCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    attempt.testTitle,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Score: ${attempt.score}%",
                                        color = if (attempt.score >= 50) SafeGreen else AlertRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${attempt.correctAnswers}/${attempt.totalQuestions} Solved",
                                        color = SoftGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
