package com.group12.starchat.model.models

/**
 * This is the user data model. It is used to store the data of a User.
 */
data class User(
    val userId: String = "",
    val userName: String = "",
    val imageUrl: String = "",
    val bio: String = "Default Bio",
    val email: String = ""
)
