package com.example.chatb

import android.graphics.Bitmap
import com.example.chatb.data.Chat

data class ChatState(
    val chatlist: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)
