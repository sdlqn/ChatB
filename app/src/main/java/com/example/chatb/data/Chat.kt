package com.example.chatb.data

import android.graphics.Bitmap

data class Chat(
    val prompt: String,
    val bitmap: Bitmap?, // img
    val isFromBot: Boolean, // from bot or not
    //val isLoading: Boolean = false

)
