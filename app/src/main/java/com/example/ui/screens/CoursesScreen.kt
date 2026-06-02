package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CourseLesson
import com.example.viewmodel.MathViewModel
import com.example.viewmodel.RecordedCourse
import kotlinx.coroutines.delay

@Composable
fun CoursesScreen(viewModel: MathViewModel) {
    val allCourses = viewModel.courses
    val bookmarks by viewModel.bookmarks.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "IIT JAM", "GATE", "CSIR NET")

    var selectedCourse by remember { mutableStateOf<RecordedCourse?>(null) }
    var selectedLesson by remember { mutableStateOf<CourseLesson?>(null) }

    var playSpeed by remember { mutableFloatStateOf(1.0f) }
    val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f)

    var simulatedProgress by remember { mutableFloatStateOf(0.0f) }
    var isPlaying by remember { mutableStateOf(false) }

    // Simulated progress incrementer
    LaunchedEffect(isPlaying, playSpeed) {
        if (isPlaying) {
            while (simulatedProgress < 1.0f) {
                delay((1000 / playSpeed).toLong())
                simulatedProgress += 0.02f
            }
            isPlaying = false
        }
    }

    val filteredCourses = if (selectedCategory == "ALL") {
        allCourses
    } else {
        allCourses.filter { it.category == selectedCategory }
    }

    if (selectedCourse != null) {
        // Detailed course detail view with simulated modern player, notes review and speed toggles
        val course = selectedCourse!!
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .immersiveBackground()
                .mathDottedPattern(0.04f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // Header to navigate back to list
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            selectedCourse = null
                            selectedLesson = null
                            isPlaying = false
                            simulatedProgress = 0f
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back back",
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recorded Syllabus",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Hero details banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = course.subject.uppercase(),
                                color = CosmicPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Exam: ${course.category}",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = course.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Author: ${course.author}", color = SoftGray, fontSize = 11.sp)
                            Box(
                                modifier = Modifier
                                    .background(StarGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    course.level,
                                    color = StarGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Cinematic video workspace
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val currentLesson = selectedLesson ?: course.lessons.first().also { selectedLesson = it }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("classroom_video_card"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        // Simulated abstract video visuals drawn in real time
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            // Draw glowing circular spectrum coordinates
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        CosmicPurple.copy(alpha = 0.3f),
                                        NeonCyan.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                radius = 180f,
                                center = center
                            )
                        }

                        // Play state overlays
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = currentLesson.title,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(180.dp)
                                    )
                                }

                                // Interactive local Bookmark icon
                                val isBookmarked = bookmarks.any { it.courseId == course.id && it.lessonTitle == currentLesson.title }
                                IconButton(
                                    onClick = {
                                        viewModel.toggleBookmark(course.id, currentLesson.title, isBookmarked)
                                    },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isBookmarked) HotPink else SoftGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Interactive playback controllers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Jump back
                                IconButton(onClick = { simulatedProgress = (simulatedProgress - 0.1f).coerceAtLeast(0.0f) }) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Rewind", tint = Color.White)
                                }

                                // Play / Pause Button
                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(NeonCyan, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                        contentDescription = "Play state",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Fast-forward
                                IconButton(onClick = { simulatedProgress = (simulatedProgress + 0.1f).coerceAtMost(1.0f) }) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Fast Forward", tint = Color.White)
                                }
                            }

                            // Horizontal scrubbing progress bar
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("00:00", color = SoftGray, fontSize = 9.sp)
                                    Text(currentLesson.duration, color = SoftGray, fontSize = 9.sp)
                                }
                                LinearProgressIndicator(
                                    progress = { simulatedProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = NeonCyan,
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }

            // Speeds selector and download summary docs
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speeds
                    Row(
                        modifier = Modifier
                            .background(DeepSpaceCard, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        speeds.forEach { speed ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (playSpeed == speed) NeonCyan else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { playSpeed = speed }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${speed}x",
                                    color = if (playSpeed == speed) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Download Notes Button
                    Row(
                        modifier = Modifier
                            .background(CosmicPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, CosmicPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.addXp(10) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Downloads",
                            tint = CosmicPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Get LaTeX Notes",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Selected Lesson Summary Notes (gorgeous formatted LaTeX)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                val currentLesson = selectedLesson ?: course.lessons.first()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepSpaceCard, RoundedCornerShape(14.dp))
                        .border(1.dp, DeepSpaceCardStroke, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        "AI CONCEPT EXTRACTION & STUDY REVISION",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        currentLesson.summaryEn,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF070B1F), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(3.dp, 12.dp)
                                        .background(HotPink)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "FORMULA DERIVATION (LATEX):",
                                    color = HotPink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                currentLesson.summaryLaTeX,
                                color = SoftGray,
                                fontSize = 11.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Playlist chapters
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "PLAYLIST CHAPTERS (${course.lessons.size})",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(course.lessons) { lesson ->
                val isSelected = lesson == selectedLesson
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            if (isSelected) ComicGlowBg() else DeepSpaceCard,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan.copy(alpha = 0.5f) else DeepSpaceCardStroke,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedLesson = lesson
                            simulatedProgress = 0f
                            isPlaying = true
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    if (isSelected) NeonCyan else CosmicPurple.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                                contentDescription = "Lesson indicator",
                                tint = if (isSelected) Color.Black else CosmicPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = lesson.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Duration: ${lesson.duration}",
                                color = SoftGray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Download index check
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed mark",
                        tint = if (isSelected) NeonCyan else SoftGray.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    } else {
        // Netflix-style horizontal course visual explorer list
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
                    text = "ACADEMIC CATALOG",
                    color = NeonCyan,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Browse recorded higher mathematical courses by topic",
                    color = SoftGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Categories list slider
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NeonCyan else DeepSpaceCard,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else DeepSpaceCardStroke,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Catalog list items
            items(filteredCourses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("course_browse_card")
                        .clickable { selectedCourse = course },
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DeepSpaceCardStroke)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = course.subject.uppercase(),
                                color = CosmicPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(NeonCyan, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    course.category,
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = course.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Created by: ${course.author}",
                            color = SoftGray,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Syllabus Details: ${course.lessons.size} Lessons",
                                color = SoftGray,
                                fontSize = 10.sp
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Launch Simulator",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Forward icon",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Background utility to style active playlist row
private val ComicGlowColor = Color(0xFF15183A)
@Composable
fun ComicGlowBg(): Color {
    return ComicGlowColor
}
