package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.*
import com.example.data.dao.MathDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MathRepository(private val mathDao: MathDao) {
    
    val streakState: Flow<StreakState?> = mathDao.getStreakState()
    val doubtHistory: Flow<List<AiDoubtHistory>> = mathDao.getAllDoubtHistory()
    val testAttempts: Flow<List<MockTestAttempt>> = mathDao.getAllTestAttempts()
    val bookmarks: Flow<List<CourseBookmark>> = mathDao.getAllBookmarks()

    suspend fun saveStreakState(streakState: StreakState) = withContext(Dispatchers.IO) {
        mathDao.insertStreakState(streakState)
    }

    suspend fun saveDoubt(doubt: AiDoubtHistory) = withContext(Dispatchers.IO) {
        mathDao.insertDoubt(doubt)
    }

    suspend fun deleteDoubt(id: Int) = withContext(Dispatchers.IO) {
        mathDao.deleteDoubt(id)
    }

    suspend fun saveAttempt(attempt: MockTestAttempt) = withContext(Dispatchers.IO) {
        mathDao.insertAttempt(attempt)
    }

    suspend fun saveBookmark(bookmark: CourseBookmark) = withContext(Dispatchers.IO) {
        mathDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmark(courseId: String, lessonTitle: String) = withContext(Dispatchers.IO) {
        mathDao.deleteBookmark(courseId, lessonTitle)
    }

    // Call Gemini to solve mathematics doubts or translate terms
    suspend fun queryGeminiMath(prompt: String, systemInstruction: String, imageBase64: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Gemini API Key is placeholder. Please configure your actual key in the AI Studio Secrets Panel to activate the real-time AI Solver & Translator."
        }

        val systemContent = GeminiContent(
            parts = listOf(GeminiPart(text = systemInstruction))
        )

        val parts = mutableListOf<GeminiPart>()
        parts.add(GeminiPart(text = prompt))
        if (!imageBase64.isNullOrEmpty()) {
            parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = imageBase64)))
        }

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = parts)),
            systemInstruction = systemContent,
            generationConfig = GeminiGenerationConfig(temperature = 0.2f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "The mathematical AI engine returned an empty response. Please check your query notation."
        } catch (e: Exception) {
            Log.e("MathRepository", "Error polling Gemini APIs", e)
            "Error: ${e.localizedMessage}. Please verify you have internet access and a valid Gemini API Key entered in the AI Studio Secrets panel."
        }
    }
}
