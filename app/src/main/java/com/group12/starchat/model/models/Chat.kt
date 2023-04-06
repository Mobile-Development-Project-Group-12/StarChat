package com.group12.starchat.model.models

import com.google.firebase.Timestamp

data class Chat(
    val message: String? = null,
    val userId: String? = null,
    val imageUrl: String? = null,
    val timeSent: Timestamp? = null
)
