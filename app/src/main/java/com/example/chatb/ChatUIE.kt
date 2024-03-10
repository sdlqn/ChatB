package com.example.chatb

import android.graphics.Bitmap

sealed class ChatUIE {
    data class UpdatePrompt(val newPrompt: String) : ChatUIE()
    data class SendPrompt(val prompt: String, val bitmap: Bitmap?) : ChatUIE()
}