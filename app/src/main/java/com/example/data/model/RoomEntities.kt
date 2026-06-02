package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_state")
data class StreakState(
    @PrimaryKey val id: Int = 1,
    val streakCount: Int = 3,
    val totalXp: Int = 150,
    val lastUpdated: Long = System.currentTimeMillis(),
    val rankTitle: String = "Hilbert Pioneer" // e.g., Euler Novice, Banach Explorer, Hilbert Pioneer
)

@Entity(tableName = "ai_doubt_history")
data class AiDoubtHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val imageBase64: String? = null,
    val solution: String, // Step-by-step solution formatted with LaTeX
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mock_test_attempt")
data class MockTestAttempt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examCategory: String, // IIT JAM, CSIR NET, GATE
    val testTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeTakenSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_bookmark")
data class CourseBookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: String,
    val lessonTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)
