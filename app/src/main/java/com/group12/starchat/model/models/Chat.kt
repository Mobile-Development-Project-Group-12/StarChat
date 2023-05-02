package com.group12.starchat.model.models

import com.google.firebase.Timestamp

/**
 * This is the Chat data model. It is used to store the data of a chat message.
 */
data class Chat(
    val userName: String? = null,
    val message: String? = null,
    val userId: String? = null,
    val imageUrl: String? = null,
    val timeSent: Timestamp? = null,
    val messageId: String? = null,
)
