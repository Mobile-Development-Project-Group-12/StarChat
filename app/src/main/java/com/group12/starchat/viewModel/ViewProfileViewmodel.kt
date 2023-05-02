package com.group12.starchat.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

/**
 *  This class is responsible for handling the logic for the ViewProfile screen. It
 *  includes methods such as loading a users profile, loading the current users
 *  friends, adding and removing friends, and blocking and unblocking users.
 *
 *  @param repository DatabaseRepo: This parameter is used to access methods
 *  that interact with the Firebase database.
 *  @author Daniel Mendes
 */
class ViewProfileViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    // an instance of the ViewProfileUiState class. Use this to access the values
    // within the ui state.
    var viewProfileUiState by mutableStateOf(ViewProfileUiState())
        private set

    /**
     * This method is used to change if the current user is friends with the user
     * in question. It does this by changing the friends value in the ui state.
     *
     * @param friends Boolean: This parameter is used to set the friends value for
     * the current user. True means they are friends, false means they are not.
     */
    fun onFriendsChange(friends: Boolean) {
        viewProfileUiState = viewProfileUiState.copy(
            friends = friends
        )
    }

    /**
     * This method is used to change if the current user has blocked the user in
     * question. It does this by changing the blocked value in the ui state.
     *
     * @param blocked Boolean: This parameter is used to set the blocked value for
     * the current user. True means they are blocked, false means they are not.
     */
    fun onBlockedChange(blocked: Boolean) {
        viewProfileUiState = viewProfileUiState.copy(
            blocked = blocked
        )
    }

    /**
     * This method is used to load the profile of the user in question. It does
     * this by calling the getUser method from the DatabaseRepo class.
     *
     * @param userId String: This parameter is used to get the user from the
     * Firestore Database with the matching userId.
     */
    fun loadUserProfile(userId: String) = viewModelScope.launch {
        repository.getUser(
            userId = userId,
            onSuccess = {
                viewProfileUiState = viewProfileUiState.copy(
                    userId = it!!.userId,
                    userName = it.userName,
                    imageUrl = it.imageUrl,
                    bio = it.bio,
                    email = it.email
                    )
            },
            onError = {
                Log.e("///// Current User Not Found /////", "Error loading profile: $it")
            }
        )
    }

    /**
     * This method is used to load the friends of the current user. It does this
     * by calling the getFriends method from the DatabaseRepo class. It essentially
     * gets all the documents from the friends subcollection in the current users
     * collection.
     */
    fun loadCurrentUserFriends() = viewModelScope.launch {
        repository.getFriends().collect {
            viewProfileUiState = viewProfileUiState.copy(
                currentUserFriends = it
            )
        }
    }

    /**
     * This method is used to add a friend to the current users friends list. It
     * does this by calling the addFriend method from the DatabaseRepo class.
     */
    fun addFriend() = viewModelScope.launch {
        repository.addFriend(
            viewProfileUiState.userId,
            viewProfileUiState.userName,
            viewProfileUiState.imageUrl,
            viewProfileUiState.bio,
            viewProfileUiState.email
        )

    }

    /**
     * This method is used to remove a friend from the current users friends list.
     * It does this by calling the removeFriend method from the DatabaseRepo class.
     * The userId parameter is used to get the user from the Firestore Database.
     */
    fun removeFriend() = viewModelScope.launch {
        repository.removeFriend(
            viewProfileUiState.userId,
        )
    }

    /**
     * This method is used to block a user. It does this by calling the blockUser
     * method from the DatabaseRepo class. This method is similar to the friends
     * methods above, except it adds the user to the blocked subcollection in the
     * Firestore Database.
     */
    fun blockUser() = viewModelScope.launch {
        repository.blockUser(
            viewProfileUiState.userId,
            viewProfileUiState.userName,
            viewProfileUiState.imageUrl,
            viewProfileUiState.bio,
            viewProfileUiState.email
        )
    }

    /**
     * This method is used to unblock a user. It does this by calling the unblockUser
     * method from the DatabaseRepo class. This method is similar to the friends
     * methods above, except it removes the user from the blocked subcollection in the
     * Firestore Database.
     */
    fun unblockUser() = viewModelScope.launch {
        repository.unBlockUser(
            viewProfileUiState.userId,
        )
    }

}

/**
 * This ui state class is used to hold the values that will be displayed on the
 * ViewProfile screen.
 */
data class ViewProfileUiState(
    val userId: String = "",
    val userName: String = "",
    var imageUrl: String = "",
    val bio: String = "",
    val email: String = "",
    val currentUserFriends: Resources<List<User>> = Resources.Loading(),
    val friends: Boolean = false,
    val blocked: Boolean = false,
)