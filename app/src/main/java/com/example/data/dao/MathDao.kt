package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MathDao {
    // Streak Queries
    @Query("SELECT * FROM streak_state WHERE id = 1 LIMIT 1")
    fun getStreakState(): Flow<StreakState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreakState(streakState: StreakState)

    // AI Doubt Queries
    @Query("SELECT * FROM ai_doubt_history ORDER BY timestamp DESC")
    fun getAllDoubtHistory(): Flow<List<AiDoubtHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: AiDoubtHistory)

    @Query("DELETE FROM ai_doubt_history WHERE id = :id")
    suspend fun deleteDoubt(id: Int)

    // Test Attempt Queries
    @Query("SELECT * FROM mock_test_attempt ORDER BY timestamp DESC")
    fun getAllTestAttempts(): Flow<List<MockTestAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: MockTestAttempt)

    // Bookmarks Queries
    @Query("SELECT * FROM course_bookmark ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<CourseBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: CourseBookmark)

    @Query("DELETE FROM course_bookmark WHERE courseId = :courseId AND lessonTitle = :lessonTitle")
    suspend fun deleteBookmark(courseId: String, lessonTitle: String)
}
