package com.example.service

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String? = null
)

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
    }

    fun isApiKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun sendMultiTurnChat(
        messages: List<ChatMessage>,
        model: String = "gemini-3.8-flash",
        systemInstruction: String = "You are the TraceIQ Nexus Senior Forensic AI Investigator, an expert digital forensics analyst. You help examiners correlate evidence records, explore the timeline of Case TQN-2026-001 (The Missing Research Files), evaluate Hypothesis A vs Hypothesis B, and cross-examine flagged contradictions. Always adhere to: 'A suspicious pattern is not proof of wrongdoing. Every finding must have a traceable basis.'"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!isApiKeyConfigured()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your GEMINI_API_KEY in the Secrets panel in AI Studio.")
            )
        }

        try {
            val root = JSONObject()

            // System instruction
            if (systemInstruction.isNotBlank()) {
                val sysInstObj = JSONObject()
                val sysParts = JSONArray().put(JSONObject().put("text", systemInstruction))
                sysInstObj.put("parts", sysParts)
                root.put("system_instruction", sysInstObj)
            }

            // Message history
            val contentsArray = JSONArray()
            for (msg in messages) {
                val msgObj = JSONObject()
                msgObj.put("role", if (msg.role == "model") "model" else "user")
                val parts = JSONArray().put(JSONObject().put("text", msg.content))
                msgObj.put("parts", parts)
                contentsArray.put(msgObj)
            }
            root.put("contents", contentsArray)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val requestBody = root.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errorMsg = try {
                        JSONObject(bodyStr).getJSONObject("error").getString("message")
                    } catch (_: Exception) {
                        "API Request failed with HTTP ${response.code}: $bodyStr"
                    }
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val jsonResponse = JSONObject(bodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val replyText = parts.getJSONObject(0).optString("text", "")
                        return@withContext Result.success(replyText)
                    }
                }
                Result.failure(Exception("No textual response returned from Gemini."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
