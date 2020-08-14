package com.jocoos.flipflop.sample.chatting

sealed class ChatItem {
    data class Info(
        val id: String? = null,
        val message: String,
        val ts: Long? = null
    ) : ChatItem()
    data class Message(
        val id: String? = null,
        val mainMessage: String = "",
        val subMessage: String = "",
        val imageUrl: String? = null,
        val ts: Long? = null
    ) : ChatItem()
}