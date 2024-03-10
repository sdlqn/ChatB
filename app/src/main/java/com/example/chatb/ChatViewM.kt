package com.example.chatb

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatb.data.Chat
import com.example.chatb.data.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewM: ViewModel() {

    private  val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()


    fun onEvent(event: ChatUIE) {
        when(event) {
            is ChatUIE.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt, event.bitmap)

                    if (event.bitmap != null) {
                        getResponseImg(event.prompt, event.bitmap)

                    }
                    else {
                        getResponse(event.prompt)
                    }
                }
            }

            is ChatUIE.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatlist = it.chatlist.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, false))
                },
                prompt = "", // empty prompt after each click
                bitmap = null

            )
        }
    }


    private fun getResponseImg(prompt: String, bitmap: Bitmap) { // with image
        viewModelScope.launch {
            val chat = ChatData.ResponseWithI(prompt, bitmap)
            _chatState.update {
                it.copy(
                    chatlist = it.chatlist.toMutableList().apply {
                        add(0, chat)
                    },
                )

            }
        }
    }


    private fun getResponse(prompt: String) { // no image
        viewModelScope.launch {
            val chat = ChatData.Response(prompt)
            _chatState.update {
                it.copy(
                    chatlist = it.chatlist.toMutableList().apply {
                        add(0, chat)
                    },
                )
            }
        }
    }

}