package com.example.chatb.data

import android.graphics.Bitmap
import com.example.chatb.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

object ChatData {

    val KEY = "YOUR_API_KEY"

    suspend fun Response(prompt: String): Chat {
        val generativeModel = GenerativeModel("gemini-pro", apiKey = KEY)

        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null, // no image
                isFromBot = true
            )
        } catch (e: Exception) {
            return Chat(
                prompt = "error",
                bitmap = null, // no image
                isFromBot = true
            )
        }
    }

    suspend fun ResponseWithI(prompt: String, bitmap: Bitmap): Chat {
        val modelName = "gemini-pro-vision"
        val generativeModel = GenerativeModel(modelName, apiKey = KEY)

        try {
            val inp = content {
                image(bitmap) // max 1 image
                text(prompt)
            }
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inp)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null, // no image
                isFromBot = true
            )
        } catch (e: ResponseStoppedException) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null, // no image
                isFromBot = true
            )
        }
    }
}