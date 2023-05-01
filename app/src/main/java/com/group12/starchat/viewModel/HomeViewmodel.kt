package com.group12.starchat.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group12.starchat.model.models.Rooms
import com.group12.starchat.model.repository.DatabaseRepo
import com.group12.starchat.model.repository.Resources
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: DatabaseRepo = DatabaseRepo(),
): ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())

    val hasUser: Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun loadRooms() {
        getRooms()
    }

    private fun getRooms() = viewModelScope.launch {
        if (hasUser) {
            if (userId.isNotBlank()) {
                repository.getRooms().collect {
                    homeUiState = homeUiState.copy(
                        chatsList = it
                    )
                }
            }
        } else {
            homeUiState = homeUiState.copy( chatsList = Resources.Failure(
                throwable = Throwable("User is not logged in!")
            ))
        }
    }

    fun deleteRoom(roomId: String) {
        repository.deleteRoom(roomId = roomId) {
            homeUiState = homeUiState.copy(roomDeletedStatus = it)
        }
    }

    /**
     * This method signs the currently logged in user out of the app.
     **/
    fun signOut() = repository.signOut()

}

data class HomeUiState(
    val chatsList: Resources<List<Rooms>> = Resources.Loading(),
    val roomCreated: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    var imageUrl: String = "",
    val bio: String = "",
    val online: Boolean = false,
    val roomDeletedStatus: Boolean = false,
)
