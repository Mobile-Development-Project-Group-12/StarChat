package com.group12.starchat.model.models

data class Rooms(
    val roomId: String = "",
    val roomName: String = "",
    val imageUrl: String = "",
    val users: ArrayList<String> = ArrayList(),
    val lastMessageSent: String = "",
    val lastMessageSeen: Boolean = true,
    val lastMessageBy: String = "",
)
