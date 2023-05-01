package com.group12.starchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group12.starchat.view.theme.StarChatTheme
import com.group12.starchat.viewModel.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val signinViewModel = viewModel(modelClass = SigninViewModel::class.java)
            val signupViewModel = viewModel(modelClass = SignupViewModel::class.java)
            val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
            val chatViewModel = viewModel(modelClass = ChatViewModel::class.java)
            val roomViewModel = viewModel(modelClass = RoomViewModel::class.java)
            val settingsViewModel = viewModel(modelClass = SettingsViewModel::class.java)
            val profileViewModel = viewModel(modelClass = ProfileViewModel::class.java)
            val friendsViewModel = viewModel(modelClass = FriendsViewModel::class.java)
            val searchViewModel = viewModel(modelClass = SearchViewModel::class.java)
            val viewProfileViewModel = viewModel(modelClass = ViewProfileViewModel::class.java)
            StarChatTheme(
                darkTheme = settingsViewModel.settingsUiState.darkMode
            ) {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(
                        signinViewModel = signinViewModel,
                        homeViewModel = homeViewModel,
                        signupViewModel = signupViewModel,
                        chatViewModel = chatViewModel,
                        roomViewModel = roomViewModel,
                        settingsViewModel = settingsViewModel,
                        profileViewModel = profileViewModel,
                        friendsViewModel = friendsViewModel,
                        searchViewModel = searchViewModel,
                        viewProfileViewModel = viewProfileViewModel
                    )
                }
            }
        }
    }

}
