package com.group12.starchat.view.screens.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.group12.starchat.model.models.Rooms
import com.group12.starchat.model.models.User
import com.group12.starchat.model.repository.Resources
import com.group12.starchat.view.components.bottomBars.BottomNavigationHome
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.view.components.topBars.HomeTopBar
import com.group12.starchat.viewModel.HomeUiState
import com.group12.starchat.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel?,
    onNavToLoginPage: () -> Unit,
    onRoomClick: (roomId: String) -> Unit,
    navToRoom: () -> Unit,
    navToRoomEdit: (roomId: String) -> Unit,
    onNavToSettingsPage: () -> Unit,
    onNavToProfilePage: () -> Unit,
    onNavToFriendsPage: () -> Unit,
    onNavToSearchPage: () -> Unit,
) {
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedRoom: Rooms? by remember {
        mutableStateOf(null)
    }

    var createRoomVisibility: Boolean by remember { mutableStateOf(false) }

    var roomName: String by remember { mutableStateOf("") }
    //var

    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        homeViewModel?.loadRooms()
    }

    when (homeUiState.chatsList) {
        is Resources.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
        is Resources.Success -> {
            Scaffold(
                scaffoldState = scaffoldState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navToRoom()
                        },
                        backgroundColor = MaterialTheme.colors.onSurface
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Room",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                isFloatingActionButtonDocked = false,
                bottomBar = { BottomNavigationHome(navToSettingsScreen = onNavToSettingsPage, navToProfileScreen = onNavToProfilePage, navToFriendsScreen = onNavToFriendsPage, navToSearchScreen = onNavToSearchPage) },
                topBar = {
                    HomeTopBar(
                        appTitle = "StarChat",
                        onSignOutClick = {
                            homeViewModel?.signOut()
                            onNavToLoginPage.invoke()
                        }
                    )
                }
            ) {
                LazyColumn(modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                ) {

                    items(homeUiState.chatsList.data ?: emptyList()) { roomInfo ->

                        RoomItem(
                            roomInfo,
                            onRoomClick = {
                                onRoomClick(roomInfo.roomId)
                            },
                            onLongClick = {
                                openDialog = true
                                selectedRoom = roomInfo
                            }
                        )

                        // Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)

                    }
                }
            }

            AnimatedVisibility(visible = openDialog) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog = false
                    },
                    title = { Text(
                        text = "Delete this Chat Room?",
                        color = MaterialTheme.colors.onSurface,
                    ) },
                    backgroundColor = MaterialTheme.colors.primary,
                    confirmButton = {
                        Button(
                            onClick = {
                                selectedRoom?.roomId?.let {
                                    homeViewModel?.deleteRoom(it)
                                }
                                openDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.onSurface
                            ),
                        ) {
                            Text(
                                text = "Delete",
                                color = MaterialTheme.colors.surface
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { openDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.surface
                            ),
                        ) {
                            Text(
                                text = "Cancel",
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                )
            }

        }
        is Resources.Failure -> {
            // ErrorScreen(homeUiState)
            Log.e("///// HomeScreen /////", "Error: ${homeUiState.chatsList.throwable}")
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomItem(
    roomInfo: Rooms,
    onRoomClick: (String) -> Unit,
    onLongClick: () -> Unit
) {

    Card(
        modifier = Modifier.padding(12.dp),
        shape = RoundedCornerShape(30),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colors.primary)
                    .padding(12.dp)
                    .combinedClickable(
                        onClick = {
                            onRoomClick(roomInfo.roomId)
                        },
                        onLongClick = {
                            onLongClick.invoke()
                        }
                    ),
                horizontalArrangement = Arrangement.Start
            ) {
                coilImage(
                    url = roomInfo.imageUrl,
                    modifier = Modifier.size(width = 50.dp, height = 50.dp),
                    shape = RoundedCornerShape(100)
                )
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = roomInfo.roomName,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Log.d("///// HomeScreen /////", "Seen Message?: ${roomInfo.lastMessageSeen} \nMessage by You?: ${roomInfo.lastMessageBy != FirebaseAuth.getInstance().currentUser?.uid}\n if statement will trigger?: ${!roomInfo.lastMessageSeen && roomInfo.lastMessageBy != FirebaseAuth.getInstance().currentUser?.uid}")
                        if (!roomInfo.lastMessageSeen && roomInfo.lastMessageBy != FirebaseAuth.getInstance().currentUser?.uid){
                            Box(
                                modifier = Modifier
                                    .drawBehind {
                                        drawCircle(
                                            color = Color.Blue,
                                            radius = 5.dp.toPx(),
                                        )
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                            Log.d("///// HomeScreen /////", "Seen Notification Triggered")
                        }
                        if (roomInfo.lastMessageSent != "") {
                            Text(
                                text = roomInfo.lastMessageSent,
                                fontSize = 16.sp,
                                color = MaterialTheme.colors.background,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        } else {
                            Text(
                                text = "No Messages yet, Say Hi!",
                                fontSize = 16.sp,
                                color = MaterialTheme.colors.secondary,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = {
                    onLongClick.invoke()
                },
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Room",
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
fun DirectRoomItem(
    user: User,
    roomInfo: Rooms,
    onRoomClick: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .clickable(
                onClick = {
                    onRoomClick(roomInfo.roomId)
                }
            ),
        horizontalArrangement = Arrangement.Start
    ) {
        coilImage(
            url = user.imageUrl,
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(100)
        )
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = user.userName,
                fontSize = 16.sp,
                color = Color.Black,
            )
            Text(
                text = roomInfo.roomId,
                fontSize = 16.sp,
                color = Color.Gray,
            )
        }
    }
}