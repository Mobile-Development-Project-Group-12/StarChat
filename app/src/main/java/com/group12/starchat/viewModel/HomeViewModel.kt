package com.group12.starchat.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.group12.starchat.model.models.Friends
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources

class HomeViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun loadFriends() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getFriendsList()
            }
        } else {
            homeUiState = homeUiState.copy( friendsList = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

    fun signOut() = repository.signOut()

}

data class HomeUiState(
    val friendsList: Resources<List<User>> = Resources.Loading(),
    val diaryDeletedStatus: Boolean = false,
)
