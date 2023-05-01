package com.group12.starchat.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.Chat
import com.group12.starchat.model.models.Rooms
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

class ChatViewModel (
    private val repository: DatabaseRepo = DatabaseRepo()
): ViewModel() {
    var chatUiState by mutableStateOf(ChatUiState())
    var currentRoomUiState by mutableStateOf(CurrentRoomUiState())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun onlastMessageSeenChange(lastMessageSeen: Boolean) {
        currentRoomUiState = currentRoomUiState.copy(
            lastMessageSeen = lastMessageSeen
        )
    }

    fun loadChats(roomId: String) {
        getChats(roomId)
    }

    fun getCurrentUser() {
        repository.getUser(
            userId = userId,
            onSuccess = {
                chatUiState = chatUiState.copy(
                    currentUser = it ?: User()
                )
            },
            onError = {
                Log.e("///// Current User Not Found /////", "Error loading profile: $it")
            }
        )
    }

    fun getRoomUsers(roomId: String) = viewModelScope.launch {
        repository.getRoomUsers(roomId).collect {
            chatUiState = chatUiState.copy(
                usersList = it
            )
        }
    }

    private fun getChats(roomId: String) = viewModelScope.launch {
        repository.getMessages(roomId).collect {
            chatUiState = chatUiState.copy(
                messagesList = it,
                currentUserId = userId
            )
        }
    }

    fun getRoomInfo(roomId: String) = viewModelScope.launch {
        repository.getRoom(
            roomId = roomId,
            onError = {
                Log.e("///// Room Not Found /////", "Error loading room: $it")
            }
        ) {
            currentRoomUiState = currentRoomUiState.copy(
                currentRoom = it ?: Rooms()
            )
        }
    }

    /**
     * adds a message to the database
     */
    fun postMessage(
        userName: String,
        roomId: String,
        message: String,
        imageUrl: String
    ) = viewModelScope.launch {
        repository.sendMessage(
            roomId = roomId,
            userName = userName,
            userId = userId,
            message = message,
            imageUrl = imageUrl
        ) {
            if (it) {
                Log.d("///// Message Sent /////", "Message sent successfully")
            } else {
                Log.e("///// Message Not Sent /////", "Error sending message")
            }
        }
    }

    fun deleteMessage(roomId: String, messageId: String) = viewModelScope.launch {
        repository.deleteMessage(
            roomId = roomId,
            messageId = messageId
        ) {

        }
    }

    fun updateMessageStatus(roomId: String, message: String) = viewModelScope.launch {
        repository.messageToRoom(
            roomId = roomId,
            message = message
        )
    }

    fun seenMessage(roomId: String) = viewModelScope.launch {
        repository.seenMessage(
            roomId = roomId,
            currentRoomUiState.lastMessageSeen
        )
    }

    fun getBlockedUsers() = viewModelScope.launch {
        repository.getBlocked().collect {
            chatUiState = chatUiState.copy(
                blockedList = it
            )
        }
    }


}

data class ChatUiState(
    var messagesList: Resources<List<Chat>> = Resources.Loading(),
    var usersList: Resources<List<User>> = Resources.Loading(),
    var blockedList: Resources<List<User>> = Resources.Loading(),
    var currentUser: User = User(),
    var currentUserId: String = ""
)

data class CurrentRoomUiState(
    var currentRoom: Rooms = Rooms(),
    var lastMessageSeen: Boolean = false
)
