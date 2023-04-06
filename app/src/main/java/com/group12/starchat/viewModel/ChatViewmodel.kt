package com.group12.starchat.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.Chat
import com.group12.starchat.model.models.Friends
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

class ChatViewModel (
    private val repository: DatabaseRepo = DatabaseRepo()
): ViewModel() {
    var chatUiState by mutableStateOf(ChatUiState())
    var messageHolder by mutableStateOf(MessageHolder())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun loadChats(friendId: String) {
        if (friendId != null) {
            getChats(Id = friendId)
            Log.e("ChatViewModel: Line 30, folder: viewModel/ChatViewModel", "friendId = $friendId")
        } else {
            chatUiState = chatUiState.copy(messagesList = Resources.Failure(
                throwable = Throwable(message = "The User is not Logged In")
            ))
        }
    }

    private fun getChats(Id: String) = viewModelScope.launch {
        repository.getMessages(Id).collect {
            chatUiState = chatUiState.copy(messagesList = it)
        }
    }

    /**
     * adds a message to the database
     */
    fun postMessage(FriendId: String, Id: String, message: String) {
        repository.sendMessage(
            FriendId = FriendId,
            userId = Id,
            message = message,
        ) {}
    }

}

data class ChatUiState(
    val messagesList: Resources<List<Chat>> = Resources.Loading(),
    // messageDeletedStatus: Boolean = false,
)

data class MessageHolder(
    val userId: String = "",
    val imageUrl: String = "",
    val timeSent: String = "",
    val message: String = ""
)