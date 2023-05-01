package com.group12.starchat.model.models

data class User(
    val userId: String = "",
    val userName: String = "",
    val imageUrl: String = "",
    val bio: String = "Default Bio",
    val email: String = ""
)
