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

/**
 *  This class is responsible for handling the logic for the Room screen. It includes
 *  methods such as:
 *  - getting a users information
 *  - creating a room with or without an image
 *  - removing users from the chat room
 *  - adding users to the chat room
 *  - getting the details of the chat room
 *  - getting the current users friends
 *  - updating the chat room.
 *
 * @param repository DatabaseRepo: This parameter is used to access
 * methods that interact with the Firebase's Firestore database.
 * @author Daniel Mendes
 */
class RoomViewModel(
    private val repository: DatabaseRepo = DatabaseRepo()
): ViewModel() {

    // an instance of the RoomUiState class. Use this to access the values
    // within the ui state.
    var roomUiState by mutableStateOf(RoomUiState())

    // This variable checks if the user is logged in.
    val hasUser: Boolean
        get() = repository.hasUser()

    // This variable is used to get the current user's id.
    private val userId: String
        get() = repository.getUserId()

    // This variable is used to get the current user's id, by triggering the above
    // variable.
    val currentUserId: String
        get() = userId

    /**
     * This method is used to change the room name variable. It does this by
     * replacing the current room name variable with the new one in the ui state.
     *
     * @param roomName String: This parameter is used to set the roomName
     * value for the current user.
     */
    fun onRoomNameChange(roomName: String) = viewModelScope.launch {
        roomUiState = roomUiState.copy(roomName = roomName)
    }

    /**
     * This method is used to change the room id variable. It does this by
     * replacing the current room id variable with the new one in the ui state.
     *
     * @param userId String: This parameter is used to set the userId
     * value
     */
    fun onUsersChange(userId: String) = viewModelScope.launch {
        roomUiState = roomUiState.copy(usersId = roomUiState.usersId.apply { add(userId) })
    }

    /**
     * This method is used to change the amount of users in a chat room.
     * It does this by replacing the current amount of users with the users plus
     * the new user.
     *
     * @param user User: This parameter is used to set the users in the current
     * chat room.
     */
    fun onUserChange(user: User) = viewModelScope.launch {
        roomUiState = roomUiState.copy(users = roomUiState.users.plus(user))
    }

    /**
     * This method is used to change the room image variable. It does this by
     * replacing the current room image variable with the new one in the ui state.
     *
     * @param imageUri Uri: This parameter is used to set the imageUri
     * value for the current user. Note, the image for the room could come from
     * the users device, or from a url online (such as firebase)
     */
    fun onImageChange(imageUri: Uri?) = viewModelScope.launch {
        roomUiState = roomUiState.copy(imageUri = imageUri)
    }

    /**
     * This method is used to change the room image url variable. It does this by
     * replacing the current room image url variable with the new one in the ui state.
     *
     * @param imageUrl String: This parameter is used to set the imageUrl
     * value for the current user. Note, the image for the room could come from
     * the users device, or from a url online (such as firebase)
     */
    fun onImageChangeUrl(imageUrl: String) = viewModelScope.launch {
        roomUiState = roomUiState.copy(imageUrl = imageUrl)
    }

    /**
     * This method is used to find a user from their user id. It does this by passing
     * the user id to the repository, and then updating the ui state with the user.
     *
     * @param userId String: This parameter is used to select a user that you
     * want to find.
     */
    fun getUser(userId: String) {
        repository.getUser(
            userId = userId,
            onError = {},
        ) {
            roomUiState = roomUiState.copy(selectedUser = it)
        }
    }

    /**
     * This method is used to create a new chat room. This chat room has users
     * in it, a name, and an image.
     */
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

    /**
     * This method is used to create a new chat room aswell. Unlike the above
     * method, it gets its image from a url. This allows us to have a default image,
     * or get images that the user has uploaded.
     */
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

    /**
     * This method is used to remove a user from a chat room.
     *
     * @param userId String: This parameter is used to select the user that you
     * want to remove from the chat room.
     */
    fun removeFromToAdd(userId: String) {
        roomUiState = roomUiState.copy(
            usersId = roomUiState.usersId.filter { it != userId }.toCollection(ArrayList()),
            users = roomUiState.users.filter { it.userId != userId }.toCollection(ArrayList())
        )
    }

    /**
     * This method is used to set the initial ui state with the current room's details.
     * You can call this function when you want to get a room's details.
     *
     * @param room Rooms: This parameter is used to set the room details for the
     * currently viewed room.
     */
    fun setRoomFields(room: Rooms) = viewModelScope.launch {

        roomUiState = roomUiState.copy(
            roomName = room.roomName,
            usersId = room.users,
            imageUrl = room.imageUrl,
        )
    }

    /**
     * This method is used to get a room from the repository. It does this by
     * passing the room id to the repository, and then updating the ui state with the room.
     *
     * @param roomId String: This parameter is used to select a room that you
     * want to find.
     */
    fun getRoom(roomId: String) = viewModelScope.launch {
        repository.getRoom(
            roomId = roomId,
            onError = {},
        ) {
            roomUiState = roomUiState.copy(selectedRoom = it)
            roomUiState.selectedRoom?.let { it1 -> setRoomFields(it1) }
        }

    }

    /**
     * This method is used to get the current user's friends.
     */
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

    /**
     * This method is used to display the friends that are not in current chat room.
     * If all of your friends are in the chat room, then this method will return an
     * empty list.
     */
    fun friendsNotInRoom(): List<User> {
        val friends = roomUiState.friends.data
        val users = roomUiState.users
        return friends?.filter { friend ->
            users.none { user ->
                user.userId == friend.userId

            }
        } ?: emptyList()
    }

    /**
     * This method is used to display the friends that are in current chat room.
     * If none of your friends are in the chat room, then this method will return an
     * empty list.
     */
    fun friendsInRoom(): List<User> {
        val friends = roomUiState.friends.data
        return friends?.filter { friend ->
            roomUiState.usersId.any { user ->
                user == friend.userId
            }
        } ?: emptyList()
    }

    /**
     * This method is used to update a chat room. This chat room has users
     * in it, a name, and an image.
     *
     * @param roomId String: This parameter is used to select the room that you
     * want to update.
     */
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

    /**
     * This method is used to reset the room added status. This is used to confirm
     * that the room has been added, or the room has been updated.
     *
     */
    fun resetRoomAddedStatus() = viewModelScope.launch {
        roomUiState = roomUiState.copy(
            roomAddedStatus = false,
            updateRoomStatus = false,
        )
    }

    /**
     * This method is used to reset the entire state of the ui. Use this method
     * when you want to display a new room, and the user is not looking at the
     * rooms details.
     */
    fun resetState() = viewModelScope.launch {
        roomUiState = RoomUiState()
    }
}

/**
 * This class is used to hold the state of the ui.
 */
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
