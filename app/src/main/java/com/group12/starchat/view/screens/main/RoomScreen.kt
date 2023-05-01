package com.group12.starchat.view.screens.main

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.group12.starchat.model.models.User
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.view.components.topBars.GeneralTopBar
import com.group12.starchat.viewModel.RoomViewModel
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RoomScreen(
    roomViewmodel: RoomViewModel,
    roomId: String,
    navController: NavHostController
) {
    val roomUiState = roomViewmodel.roomUiState

    val isRoomIdNotBlank = roomId.isNotBlank()

    LaunchedEffect(key1 = Unit) {
        if (isRoomIdNotBlank) {
            roomViewmodel.getRoom(roomId = roomId)
            roomViewmodel.getFriends()
        } else {
            roomViewmodel.resetState()
            roomViewmodel.getFriends()
        }
    }

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    val previousScreen = "Home"

    val currentScreen = if (isRoomIdNotBlank) "Edit: ${roomUiState.roomName}" else "Create a Room"

    val outlinedFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = MaterialTheme.colors.primary,
        unfocusedBorderColor = MaterialTheme.colors.primary,
        focusedLabelColor = MaterialTheme.colors.primary,
        unfocusedLabelColor = MaterialTheme.colors.primary,
        cursorColor = MaterialTheme.colors.primary,
        errorCursorColor = Color.Red,
        errorLabelColor = Color.Red,
        errorTrailingIconColor = Color.Red,
        errorLeadingIconColor = Color.Red,
        trailingIconColor = MaterialTheme.colors.primary,
        leadingIconColor = MaterialTheme.colors.primary,
    )

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }

    if (pickedPhoto != null) {
        roomViewmodel.onImageChange(pickedPhoto)
    } else {
        roomViewmodel.onImageChange(null)
    }

    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> pickedPhoto = uri }
    )

    val saveOrUpdate = if (isRoomIdNotBlank) "Update This Room" else "Create New Room"

    Scaffold(scaffoldState = scaffoldState,
        topBar = { GeneralTopBar(currentScreen = currentScreen, navController = navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .padding(padding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = CenterHorizontally,
            ) {

                if (roomUiState.roomAddedStatus) {
                    roomViewmodel.resetRoomAddedStatus()
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Added Room Successfully")
                    }
                }

                if (roomUiState.updateRoomStatus) {
                    roomViewmodel.resetRoomAddedStatus()
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Updated Room Successfully")
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = CenterHorizontally
                    ) {

                        if (pickedPhoto != null) {
                            coilImage(
                                uri = pickedPhoto,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .align(CenterHorizontally)
                                    .clickable {
                                        singlePhotoLauncher.launch(PickVisualMediaRequest())
                                    },
                                shape = RoundedCornerShape(100)
                            )
                        } else if (roomUiState.imageUrl != "") {
                            coilImage(
                                url = roomUiState.imageUrl,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .align(CenterHorizontally)
                                    .clickable {
                                        singlePhotoLauncher.launch(PickVisualMediaRequest())
                                    },
                                shape = RoundedCornerShape(100)
                            )
                        } else {
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .align(CenterHorizontally)
                                    .clickable(
                                        onClick = {
                                            singlePhotoLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                    ),
                                shape = RoundedCornerShape(100)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = CenterHorizontally
                                ) {
                                    Text(
                                        text = "No Image",
                                        color = MaterialTheme.colors.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Room Name",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = roomUiState.roomName,
                            onValueChange = {
                                roomViewmodel.onRoomNameChange(it)
                            },
                            textStyle = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 20.sp,
                            ),
                            colors = outlinedFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                        Divider(thickness = 1.dp, color = MaterialTheme.colors.onSurface, modifier = Modifier.fillMaxWidth())

                        Row() {
                            roomViewmodel.getUser(roomViewmodel.currentUserId)
                            userToAddItem(
                                user = roomUiState.selectedUser ?: User(),
                                removeFromGroup = { }
                            )

                            Text(text = "+", modifier = Modifier.padding(horizontal = 8.dp), style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                            ))

                            LazyRow() {

                                items(roomViewmodel.friendsInRoom()) { user ->
                                    userToAddItem(
                                        user = user,
                                        removeFromGroup = {
                                            roomViewmodel.removeFromToAdd(it.userId)
                                            scope.launch {
                                                scaffoldState.snackbarHostState
                                                    .showSnackbar("Removed ${it.userName} from groupChat")
                                            }
                                        }
                                    )
                                }

                            }
                        }

                        Divider(thickness = 1.dp, color = MaterialTheme.colors.onSurface, modifier = Modifier.fillMaxWidth())

                        LazyRow() {

                            roomViewmodel.getFriends()

                            items(roomViewmodel.friendsNotInRoom()) { user ->
                                if (!roomViewmodel.friendsInRoom().contains(user)) {
                                    FriendsToAddItem(
                                        friend = user,
                                        addToGroup = {
                                            roomViewmodel.onUserChange(it)
                                            roomViewmodel.onUsersChange(it.userId)
                                            scope.launch {
                                                scaffoldState.snackbarHostState
                                                    .showSnackbar("Added ${it.userName} to groupChat")
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Divider(thickness = 1.dp, color = MaterialTheme.colors.onSurface, modifier = Modifier.fillMaxWidth())

                    }

                }

            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {

                            singlePhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )

                        },
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Pick an Image",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 16.sp,
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (pickedPhoto == null && (roomUiState.imageUrl == "")) {
                                scope.launch {
                                    roomViewmodel.onImageChangeUrl("https://cdn11.bigcommerce.com/s-3uewkq06zr/images/stencil/1280x1280/products/258/543/fluorescent_pink__88610.1492541080.png?c=2")
                                    roomViewmodel.createRoomUrl()
                                    roomViewmodel.resetRoomAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("No Image Selected, Creating Chat room with a default image")
                                }
                            } else if (pickedPhoto == null && (roomUiState.imageUrl != "") ) {
                                scope.launch {
                                    roomViewmodel.onImageChangeUrl(roomUiState.imageUrl)
                                    roomViewmodel.updateRoom(roomId = roomId)
                                    roomViewmodel.resetRoomAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Chat Room Updated")
                                }
                            } else if (pickedPhoto != null && (roomUiState.imageUrl == "") ) {
                                scope.launch {
                                    roomViewmodel.createRoom()
                                    roomViewmodel.resetRoomAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Chat Room Created")
                                }
                            } else if (pickedPhoto != null && (roomUiState.imageUrl != "") ) {
                                scope.launch {
                                    roomViewmodel.updateRoom(roomId = roomId)
                                    roomViewmodel.resetRoomAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Chat Room Updated")
                                }
                            }
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = saveOrUpdate,
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 16.sp,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun userToAddItem(
    user: User,
    removeFromGroup: (User) -> Unit
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
            .clickable(
                onClick = {
                    removeFromGroup(user)
                }
            )
    ) {
        coilImage(
            url = user.imageUrl,
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(100)
        )
        Text(
            text = user.userName
        )
    }
}

@Composable
fun FriendsToAddItem(
    friend: User,
    addToGroup: (User) -> Unit
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
            .clickable(
                onClick = {
                    addToGroup(friend)
                }
            )
    ) {
        coilImage(
            url = friend.imageUrl,
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(100)
        )
        Text(
            text = friend.userName
        )
    }
}