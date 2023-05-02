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

/**
 * This class is responsible for handling the logic for the Chat screen. It
 * includes methods such as sending messages, loading chats, deleting messages,
 * and loading users.
 *
 * @param repository DatabaseRepo: This parameter is used to access
 * methods that interact with the Firebase's Firestore database.
 * @author Daniel Mendes
 */
class ChatViewModel (
    private val repository: DatabaseRepo = DatabaseRepo()
): ViewModel() {

    // an instance of the ChatUiState class. Use this to access the values
    // within the ui state.
    var chatUiState by mutableStateOf(ChatUiState())

    // an instance of the CurrentRoomUiState class. Use this to access the values
    // within the ui state.
    var currentRoomUiState by mutableStateOf(CurrentRoomUiState())

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // This variable gets the current users id.
    private val userId: String
        get() = repository.getUserId()

    /**
     * This method is used to change if the user saw the last message or not. It
     * does this by changing the value of the lastMessageSeen variable.
     *
     * @param lastMessageSeen Boolean: This parameter is used to determine if the
     * user saw the last message or not.
     */
    fun onlastMessageSeenChange(lastMessageSeen: Boolean) {
        currentRoomUiState = currentRoomUiState.copy(
            lastMessageSeen = lastMessageSeen
        )
    }

    /**
     * This method is used to load the current users chat rooms.
     **/
    fun loadChats(roomId: String) {
        getChats(roomId)
    }

    /**
     * This method is used to load the details of the current user.
     **/
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

    /**
     * This method is used to load the messages of the current room. It does this by
     * finding the current rooms id, and returning all of the messages within the
     * rooms subcollection.
     *
     * @param roomId String: This parameter is used to get the current rooms id.
     **/
    private fun getChats(roomId: String) = viewModelScope.launch {
        repository.getMessages(roomId).collect {
            chatUiState = chatUiState.copy(
                messagesList = it,
                currentUserId = userId
            )
        }
    }

    /**
     * This method is used to load the information of the current room. It does this
     * by finding the current rooms id, and returning all of the information within
     * the rooms subcollection.
     *
     * @param roomId String: This parameter is used to get the current rooms id.
     **/
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
     * adds a message to the firebase firestore database. It does this by adding
     * the message to the rooms messages subcollection.
     *
     * @param userName String: This parameter is used to get the current users
     * name.
     * @param roomId String: This parameter is used to get the current rooms id.
     * @param message String: This parameter is used to get the message that the
     * user sent.
     * @param imageUrl String: This parameter is used to get the image url of the
     * image that the user sent.
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

    /**
     * This method is used to delete a message from the firebase firestore database.
     * It does this by deleting the message from the rooms messages subcollection.
     *
     * @param roomId String: This parameter is used to get the current rooms id.
     * @param messageId String: This parameter is used to get the id of the message
     **/
    fun deleteMessage(roomId: String, messageId: String) = viewModelScope.launch {
        repository.deleteMessage(
            roomId = roomId,
            messageId = messageId
        ) {

        }
    }

    /**
     * This method is used to load the update the latest message seen by the user.
     * This method is used for the seen message feature.
     *
     * @param roomId String: This parameter is used to get the current rooms id.
     * @param message String: This parameter is used to get the message that the
     * user sent.
     **/
    fun updateMessageStatus(roomId: String, message: String) = viewModelScope.launch {
        repository.messageToRoom(
            roomId = roomId,
            message = message
        )
    }

    /**
     * This method is used to update the last message seen by the user. It does this
     * by updating the lastMessageSeen variable.
     *
     * @param roomId String: This parameter is used to get the current rooms id.
     **/
    fun seenMessage(roomId: String) = viewModelScope.launch {
        repository.seenMessage(
            roomId = roomId,
            currentRoomUiState.lastMessageSeen
        )
    }

    /**
     * This method is used to get the list of users that the current user has blocked.
     **/
    fun getBlockedUsers() = viewModelScope.launch {
        repository.getBlocked().collect {
            chatUiState = chatUiState.copy(
                blockedList = it
            )
        }
    }
}

/**
 * This class is used to hold the variables used within the chat screen.
 */
data class ChatUiState(
    var messagesList: Resources<List<Chat>> = Resources.Loading(),
    var usersList: Resources<List<User>> = Resources.Loading(),
    var blockedList: Resources<List<User>> = Resources.Loading(),
    var currentUser: User = User(),
    var currentUserId: String = ""
)

/**
 * This class is used to hold the variables used within the current room.
 */
data class CurrentRoomUiState(
    var currentRoom: Rooms = Rooms(),
    var lastMessageSeen: Boolean = false
)
