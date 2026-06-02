package com.example.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MathViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MathLabScreen(viewModel: MathViewModel) {
    var activeSubTab by remember { mutableStateOf("ANALYTICS") } // ANALYTICS, MATH_LAB

    // Interactive Fourier parameters
    var fourierHarmonics by remember { mutableFloatStateOf(4f) }
    var runningTime by remember { mutableFloatStateOf(0f) }

    // Lorenz Attractor parameters
    var lorenzSigma by remember { mutableFloatStateOf(10f) }

    // Tick time for waveforms
    LaunchedEffect(activeSubTab) {
        while (true) {
            delay(16) // ~60fps
            runningTime += 0.05f
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .immersiveBackground()
            .mathDottedPattern(0.04f)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Mode Selector
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
                            if (activeSubTab == "ANALYTICS") CosmicPurple else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeSubTab = "ANALYTICS" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Stats",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Math Analytics",
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
                            if (activeSubTab == "MATH_LAB") CosmicPurple else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeSubTab = "MATH_LAB" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Interactive explorer
                        Icon(
                            imageVector = Icons.Default.ThumbUp, // Alternative physics symbol icon
                            contentDescription = "Visualization",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Theorem Explorer",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (activeSubTab == "ANALYTICS") {
            // DETAILED STATISTICAL DASHBOARD (drawn with glowing neon vectors)
            item {
                Text(
                    text = "STUDENT INTEL ANALYTICS",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Custom performance vectors based on syllabus testing performance",
                    color = SoftGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Streaks heatmap drawn with Grid Cells
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("heatmap_card"),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "STUDY TIMELINE CONSISTENCY (LAST 30 DAYS)",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Draw mock heatmap boxes (different activity ranges)
                        FlowRow(
                            maxItemsInEachRow = 10,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val mockupDays = listOf(
                                3, 4, 1, 0, 5, 2, 4, 0, 1, 5,
                                0, 0, 2, 5, 4, 1, 3, 0, 5, 2,
                                4, 2, 0, 4, 5, 1, 3, 5, 4, 5
                            )
                            mockupDays.forEachIndexed { idx, intensity ->
                                val boxColor = when (intensity) {
                                    0 -> Color(0xFF0F142D) // Uncommitted
                                    1 -> NeonCyan.copy(alpha = 0.2f)
                                    2 -> NeonCyan.copy(alpha = 0.4f)
                                    3 -> CosmicPurple.copy(alpha = 0.4f)
                                    4 -> CosmicPurple.copy(alpha = 0.8f)
                                    else -> NeonCyan // Max commitment
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(boxColor, RoundedCornerShape(4.dp))
                                        .border(0.5.dp, DeepSpaceCardStroke, RoundedCornerShape(4.dp))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Novice  ", color = SoftGray, fontSize = 9.sp)
                            listOf(0.2f, 0.5f, 1.0f).forEach { intensity ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(NeonCyan.copy(alpha = intensity), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text("  Professor", color = NeonCyan, fontSize = 9.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Radial Weak area coordinate chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "MATHEMATICAL WEAK AND STRENGTH SUBTOPICS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom drawn line graph chart representation on Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val sizeWidth = size.width
                                val sizeHeight = size.height

                                // Draw Cartesian graph grids
                                drawLine(color = SoftGray.copy(alpha = 0.2f), start = Offset(0f, sizeHeight / 2f), end = Offset(sizeWidth, sizeHeight / 2f))
                                drawLine(color = SoftGray.copy(alpha = 0.2f), start = Offset(sizeWidth / 2f, 0f), end = Offset(sizeWidth / 2f, sizeHeight))

                                // Plot a complex star vector representing Algebra, Analysis, Topology, Linear Algebra, Complex analysis
                                val center = Offset(sizeWidth / 2f, sizeHeight / 2f)
                                val sectors = listOf(
                                    Pair("Algebra (85%)", Offset(0f, -60f)),
                                    Pair("Syllabus Review (40%)", Offset(60f, -20f)),
                                    Pair("Analysis (75%)", Offset(40f, 50f)),
                                    Pair("Topology (30%)", Offset(-50f, 40f)),
                                    Pair("Linear Algebra (92%)", Offset(-70f, -20f))
                                )

                                val plotPath = Path().apply {
                                    val startPos = center + sectors.first().second
                                    moveTo(startPos.x, startPos.y)
                                    for (i in 1 until sectors.size) {
                                        val nextPos = center + sectors[i].second
                                        lineTo(nextPos.x, nextPos.y)
                                    }
                                    close()
                                }

                                // Fill area
                                drawPath(path = plotPath, color = NeonCyan.copy(alpha = 0.15f))
                                drawPath(path = plotPath, color = NeonCyan, style = Stroke(width = 3f))

                                // Render dots
                                sectors.forEach { (_, offset) ->
                                    drawCircle(color = HotPink, radius = 6f, center = center + offset)
                                }
                            }

                            // Layer labels manually in corners
                            Text("Linear Alg: 92%", color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart))
                            Text("Algebra: 85%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter))
                            Text("Analysis: 75%", color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomEnd))
                            Text("Topology: 30%", color = AlertRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomStart))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Syllabus metrics report listings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SYLLABUS PROGRESS TRACKER", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .background(SafeGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text("Ahead by 14%", color = SafeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        val mockLevels = listOf(
                            Triple("Abstract Algebra & Symmetries", "Syllabus Completed", 0.85f),
                            Triple("Real Topology & Connectedness", "Lessons Bookmarked", 0.60f),
                            Triple("Residue Summations & Complex Planes", "Review Required", 0.40f)
                        )

                        mockLevels.forEach { (title, subtitle, completion) ->
                            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(subtitle, color = SoftGray, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { completion },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (completion >= 0.7f) NeonCyan else CosmicPurple,
                                    trackColor = Color.White.copy(alpha = 0.08f)
                                )
                            }
                        }
                    }
                }
            }

        } else {
            // HIGH-PERFORMANCE MATHEMATICAL THEOREM EXPLORER GRAPH
            item {
                Text(
                    text = "VISUAL MATHEMATICAL THEOREM EXPERIMENTAL LAB",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Observe real-time synthesis of complex functional integrations (Fourier expansions) compiled natively at 60fps.",
                    color = SoftGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Harmonic slider control
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Interactive Fourier Harmonic Series",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Theorem: Any square wave boundary can be synthesized through an infinite sum of harmonics: s_n(t) = (4/π) ∑ [sin(2π(2k-1)ft) / (2k-1)]",
                            color = SoftGray,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Harmonics (n): ${fourierHarmonics.toInt()}",
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Slider(
                                value = fourierHarmonics,
                                onValueChange = { fourierHarmonics = it },
                                valueRange = 1f..32f,
                                modifier = Modifier
                                    .testTag("fourier_harmonic_slider")
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = NeonCyan,
                                    inactiveTrackColor = SoftGray.copy(alpha = 0.2f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fourier Synthesizer Canvas display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color.Black, RoundedCornerShape(12.dp))
                                .border(1.dp, DeepSpaceCardStroke, RoundedCornerShape(12.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val width = size.width
                                val height = size.height
                                val centerY = height / 2f

                                // Draw center axis
                                drawLine(
                                    color = SoftGray.copy(alpha = 0.2f),
                                    start = Offset(0f, centerY),
                                    end = Offset(width, centerY)
                                )

                                val wavePath = Path()
                                val steps = width.toInt()
                                val iterations = fourierHarmonics.toInt()

                                for (x in 0..steps) {
                                    val t = (x.toFloat() / width) * 4f * Math.PI // span 4 periods
                                    var ySum = 0f

                                    // Compute Fourier sum for square wave
                                    for (k in 1..iterations) {
                                        val harmonic = 2 * k - 1
                                        // add offset using dynamic 'runningTime' variable to slide the wave cleanly across screen!
                                        ySum += (sin(harmonic * (t + runningTime)) / harmonic).toFloat()
                                    }

                                    val finalY = centerY + (ySum * (height / 3f) * (4f / Math.PI.toFloat()))

                                    if (x == 0) {
                                        wavePath.moveTo(0f, finalY)
                                    } else {
                                        wavePath.lineTo(x.toFloat(), finalY)
                                    }
                                }

                                drawPath(
                                    path = wavePath,
                                    color = NeonCyan,
                                    style = Stroke(width = 4f)
                                )

                                // Draw harmonic orbital vectors (classic mathematical visual circles)
                                var orbitsCenter = Offset(width * 0.15f, centerY)
                                var cumulativeRadius = 0f
                                for (k in 1..iterations) {
                                    val harmonic = 2 * k - 1
                                    val radius = (height / 6f) * (4f / (Math.PI.toFloat() * harmonic))

                                    // Circle guide
                                    drawCircle(
                                        color = CosmicPurple.copy(alpha = 0.15f),
                                        radius = radius,
                                        center = orbitsCenter,
                                        style = Stroke(width = 2f)
                                    )

                                    // Computed point coordinate based on current orbital velocity
                                    val angle = harmonic * runningTime
                                    val orbitalOffset = Offset(
                                        (cos(angle) * radius).toFloat(),
                                        (sin(angle) * radius).toFloat()
                                    )

                                    drawLine(
                                        color = SoftGray,
                                        start = orbitsCenter,
                                        end = orbitsCenter + orbitalOffset,
                                        strokeWidth = 2f
                                    )

                                    orbitsCenter += orbitalOffset
                                }

                                // Dot at end of orbital vector
                                drawCircle(
                                    color = HotPink,
                                    radius = 5f,
                                    center = orbitsCenter
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lorenz attractor visual model
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Lorenz Attractor Chaos Orbit",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "System equations modeling atmospheric turbulence and mathematical chaos.",
                            color = SoftGray,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Chaos Parameter (σ): ${lorenzSigma.toInt()}",
                                color = HotPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Slider(
                                value = lorenzSigma,
                                onValueChange = { lorenzSigma = it },
                                valueRange = 5f..25f,
                                modifier = Modifier
                                    .testTag("lorenz_sigma_slider")
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = HotPink,
                                    activeTrackColor = HotPink,
                                    inactiveTrackColor = SoftGray.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Orbit drawings
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color.Black, RoundedCornerShape(12.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val width = size.width
                                val height = size.height
                                val center = Offset(width / 2f, height / 2f)

                                // Draw double dimensional loops
                                val lorenzPath = Path()
                                var x = 0.1
                                var y = 0.0
                                var z = 0.0

                                val dt = 0.05
                                val r = 28.0
                                val b = 8.0 / 3.0

                                val sigma = lorenzSigma.toDouble()

                                for (i in 0..120) {
                                    val dx = sigma * (y - x) * dt
                                    val dy = (x * (r - z) - y) * dt
                                    val dz = (x * y - b * z) * dt

                                    x += dx
                                    y += dy
                                    z += dz

                                    // Projection coordinate coordinates to match canvas
                                    val canvasX = center.x + (x * 5.0).toFloat()
                                    val canvasY = center.y + (y * 4.0).toFloat() - 40f

                                    if (i == 0) {
                                        lorenzPath.moveTo(canvasX, canvasY)
                                    } else {
                                        lorenzPath.lineTo(canvasX, canvasY)
                                    }
                                }

                                drawPath(
                                    path = lorenzPath,
                                    color = CosmicPurple,
                                    style = Stroke(width = 3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
