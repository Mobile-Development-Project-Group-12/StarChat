package com.group12.starchat.view.screens.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.Resources
import com.group12.starchat.view.components.bottomBars.BottomNavigationFriends
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.view.components.topBars.FriendsTopBar
import com.group12.starchat.viewModel.BlockedUiState
import com.group12.starchat.viewModel.FriendsUiState
import com.group12.starchat.viewModel.FriendsViewModel
import com.group12.starchat.viewModel.listState

@Composable
fun FriendsScreen(
    friendsViewModel: FriendsViewModel?,
    onNavToSettingsPage: () -> Unit,
    onNavToHomePage: () -> Unit,
    onNavToProfilePage: () -> Unit,
    onNavToSearchPage: () -> Unit,
    onNavToUserProfile: (String) -> Unit,
) {
    val friendsUiState = friendsViewModel?.friendsUiState ?: FriendsUiState()
    val blockedUiState = friendsViewModel?.blockedUiState ?: BlockedUiState()

    val title = if (friendsViewModel?.editListState == listState.FRIENDS) {
        "Friends"
    } else if (friendsViewModel?.editListState == listState.BLOCKED) {
        "Blocked"
    } else {
        "Select an option!"
    }

    LaunchedEffect(key1 = Unit) {
        friendsViewModel?.loadfriends()
        friendsViewModel?.loadBlocked()
    }

    when (friendsUiState.friendsList) {
        is Resources.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
        is Resources.Success -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                bottomBar = {
                    BottomNavigationFriends(
                        navToSettingsScreen = { onNavToSettingsPage() },
                        navToProfileScreen = { onNavToProfilePage() },
                        navToHomeScreen = { onNavToHomePage() },
                        navToSearchScreen = { onNavToSearchPage() },
                    )
                },
                topBar = {
                    FriendsTopBar(
                        currentScreen = title,
                        friendsViewModel = friendsViewModel,
                    )
                }
            ) {
                if (friendsViewModel?.editListState == listState.NONE) {
                    Text(
                        text = "Press the Top left dropdown to choose a list!",
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )

                } else if(friendsViewModel?.editListState == listState.FRIENDS && friendsUiState.friendsList.data!!.isEmpty()) {
                    Text(
                        text = "You have no friends!",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                } else if (friendsViewModel?.editListState == listState.BLOCKED && blockedUiState.blockedList.data!!.isEmpty()) {
                    Text(
                        text = "You have not blocked any users!",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {

                            Log.d("///// FriendsScreen /////", "1st if statement will trigger?: ${friendsViewModel?.editListState == listState.FRIENDS && friendsUiState.friendsList.data!!.size == 0} \n" +
                                    "2nd if statement will trigger?: ${friendsViewModel?.editListState == listState.BLOCKED && blockedUiState.blockedList.data!!.size == 0}")

                            items(
                                when (friendsViewModel?.editListState) {
                                    listState.FRIENDS -> friendsUiState.friendsList.data ?: emptyList()
                                    listState.BLOCKED -> blockedUiState.blockedList.data ?: emptyList()
                                    else -> emptyList()
                                }
                            ) { user ->
                                UserItem(
                                    user = user,
                                    onNavToUserProfile = { userId ->
                                        onNavToUserProfile(userId)
                                    },
                                )
                                Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
        is Resources.Failure -> {
            Text(
                text = "Error",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onNavToUserProfile: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                onClick = {
                    onNavToUserProfile(user.userId)
                }
            )
    ) {

        coilImage(
            url = user.imageUrl,
            modifier = Modifier.size(width = 50.dp, height = 50.dp),
            shape = RoundedCornerShape(100)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp)
        ) {
            Text(
                text = user.userName,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                fontStyle = MaterialTheme.typography.body1.fontStyle,
                color = MaterialTheme.colors.primary,
                fontSize = 16.sp,
            )
            Text(
                text = user.bio,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                fontStyle = MaterialTheme.typography.body1.fontStyle,
                color = MaterialTheme.colors.primary,
                fontSize = 16.sp,
            )
        }
    }
}