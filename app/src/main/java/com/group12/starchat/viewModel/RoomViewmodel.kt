package com.group12.starchat.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.Rooms
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

class RoomViewModel(
    private val repository: DatabaseRepo = DatabaseRepo()
): ViewModel() {

    var roomUiState by mutableStateOf(RoomUiState())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    val currentUserId: String
        get() = userId

    fun onRoomNameChange(roomName: String) = viewModelScope.launch {
        roomUiState = roomUiState.copy(roomName = roomName)
    }

    fun onUsersChange(userId: String) = viewModelScope.launch {
        roomUiState = roomUiState.copy(usersId = roomUiState.usersId.apply { add(userId) })
    }

    fun onUserChange(user: User) = viewModelScope.launch {
        roomUiState = roomUiState.copy(users = roomUiState.users.plus(user))
    }

    fun onImageChange(imageUri: Uri?) = viewModelScope.launch {
        roomUiState = roomUiState.copy(imageUri = imageUri)
    }

    fun onImageChangeUrl(imageUrl: String) = viewModelScope.launch {
        roomUiState = roomUiState.copy(imageUrl = imageUrl)
    }

    fun getUser(userId: String) {
        repository.getUser(
            userId = userId,
            onError = {},
        ) {
            roomUiState = roomUiState.copy(selectedUser = it)
        }
    }

    fun createRoom() {
        repository.createRoom(
            roomName = roomUiState.roomName,
            imageUri = roomUiState.imageUri,
            users = roomUiState.usersId.apply {
                add(currentUserId)
            },
        ) {
            roomUiState = roomUiState.copy(
                roomAddedStatus = it
            )
        }
    }

    fun createRoomUrl() {
        repository.createRoomUrl(
            roomName = roomUiState.roomName,
            imageUrl = roomUiState.imageUrl,
            users = roomUiState.usersId.apply {
                add(currentUserId)
            },
        ) {
            roomUiState = roomUiState.copy(
                roomAddedStatus = it
            )
        }
    }

    fun removeFromToAdd(userId: String) {
        roomUiState = roomUiState.copy(
            usersId = roomUiState.usersId.filter { it != userId }.toCollection(ArrayList()),
            users = roomUiState.users.filter { it.userId != userId }.toCollection(ArrayList())
        )
    }

    fun setRoomFields(room: Rooms) = viewModelScope.launch {

        roomUiState = roomUiState.copy(
            roomName = room.roomName,
            usersId = room.users,
            imageUrl = room.imageUrl,
        )
    }

    fun getRoom(roomId: String) = viewModelScope.launch {
        repository.getRoom(
            roomId = roomId,
            onError = {},
        ) {
            roomUiState = roomUiState.copy(selectedRoom = it)
            roomUiState.selectedRoom?.let { it1 -> setRoomFields(it1) }
        }

    }

    fun getFriends() = viewModelScope.launch {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getFriends().collect {
                    roomUiState = roomUiState.copy(
                        friends = it
                    )
                }
            }
        } else {
            roomUiState = roomUiState.copy( friends = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

    fun friendsNotInRoom(): List<User> {
        val friends = roomUiState.friends.data
        val users = roomUiState.users
        return friends?.filter { friend ->
            users.none { user ->
                user.userId == friend.userId

            }
        } ?: emptyList()
    }

    fun friendsInRoom(): List<User> {
        val friends = roomUiState.friends.data
        return friends?.filter { friend ->
            roomUiState.usersId.any { user ->
                user == friend.userId
            }
        } ?: emptyList()
    }

    fun updateRoom(roomId: String) = viewModelScope.launch {
        repository.updateRoom(
            roomId = roomId,
            roomName = roomUiState.roomName,
            imageUri = roomUiState.imageUri,
            imageUrl = roomUiState.imageUrl,
            users = roomUiState.usersId,
        ) {
            roomUiState = roomUiState.copy(updateRoomStatus = it)
        }
    }

    fun deleteRoom(roomId: String) = viewModelScope.launch {
        repository.deleteRoom(
            roomId = roomId,
        ) {
            roomUiState = roomUiState.copy(updateRoomStatus = it)
        }
    }

    fun clearUsers() = viewModelScope.launch {
        roomUiState = roomUiState.copy(
            users = arrayListOf()
        )
    }

    fun addToGroup(user: User) = viewModelScope.launch {
        roomUiState = roomUiState.copy(
            users = roomUiState.users.plus(user)
        )
    }

    fun resetRoomAddedStatus() = viewModelScope.launch {
        roomUiState = roomUiState.copy(
            roomAddedStatus = false,
            updateRoomStatus = false,
        )
    }

    fun resetState() = viewModelScope.launch {
        roomUiState = RoomUiState()
    }
}

data class RoomUiState(
    private val repository: DatabaseRepo = DatabaseRepo(),
    val roomId: String = "",
    val roomName: String = "",
    val imageUri: Uri? = null,
    val imageUrl: String = "",
    val friends: Resources<List<User>> = Resources.Loading(),
    var usersId: ArrayList<String> = arrayListOf(),
    var users: List<User> = listOf(),
    val roomAddedStatus: Boolean = false,
    val updateRoomStatus: Boolean = false,
    val selectedRoom: Rooms? = null,
    val selectedUser: User? = null,
)
