package com.group12.starchat.view.screens.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.group12.starchat.model.models.Chat
import com.group12.starchat.model.repository.Resources
import com.group12.starchat.view.components.bottomBars.ChatBottomBar
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.view.components.topBars.ChatTopBar
import com.group12.starchat.viewModel.ChatUiState
import com.group12.starchat.viewModel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    roomId: String,
    navController: NavController,
    navToRoomEdit: (String) -> Unit
) {
    var chatUiState = chatViewModel.chatUiState
    var currentRoomUiState = chatViewModel.currentRoomUiState

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedMessage: Chat? by remember {
        mutableStateOf(null)
    }

    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        chatViewModel.loadChats(roomId)
        chatViewModel.getCurrentUser()
        chatViewModel.getBlockedUsers()
        chatViewModel.getRoomInfo(roomId)
    }

    LaunchedEffect(key1 = currentRoomUiState.currentRoom.lastMessageSeen) {
        if (!currentRoomUiState.currentRoom.lastMessageSeen && currentRoomUiState.currentRoom.lastMessageBy != chatUiState.currentUser.userId) {
            chatViewModel.onlastMessageSeenChange(true)
            chatViewModel.seenMessage(roomId)
        }
    }

    when (chatUiState.messagesList) {
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
                    ChatBottomBar(chatViewModel, roomId)
                },
                topBar = {
                    ChatTopBar(
                        currentScreen = chatViewModel.currentRoomUiState.currentRoom.roomName,
                        navController = navController,
                        navToRoomEdit = {
                            navToRoomEdit.invoke(roomId)
                        }
                    )
                }
            ) {
                // Text(text = "Chat")
                LazyColumn(modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                    state = lazyListState
                ) {

                    items(
                        chatUiState.messagesList.data ?: emptyList()
                    ) { message ->
                        MessageItem(
                            messageInfo = message,
                            chatUiState = chatUiState,
                            onLongClick = { select ->
                                selectedMessage = select
                                openDialog = true
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = openDialog) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog = false
                    },
                    title = { Text(
                        text = "Delete this Message?",
                        color = MaterialTheme.colors.onSurface,
                    ) },
                    backgroundColor = MaterialTheme.colors.primary,
                    confirmButton = {
                        Button(
                            onClick = {
                                selectedMessage?.messageId?.let {

                                    Log.d("///// ChatScreen /////", "MessageId: $it")
                                    chatViewModel?.deleteMessage(messageId = it, roomId = roomId)
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
            Log.e("///// HomeScreen /////", "Error: ${chatUiState.messagesList.throwable}")
        }
    }

    LaunchedEffect(key1 = chatUiState.messagesList.data?.size) {
        scope.launch {
            delay(100)
            lazyListState.animateScrollToItem(chatUiState.messagesList.data?.size ?: 0)
        }
    }

}

// https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/440px-Image_created_with_a_mobile_phone.png

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    messageInfo: Chat,
    chatUiState: ChatUiState,
    onLongClick: (Chat) -> Unit
) {

    if (messageInfo.userId == chatUiState.blockedList.data?.find { it.userId == messageInfo.userId }?.userId) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            coilImage(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/Solid_red.svg/512px-Solid_red.svg.png?20150316143248",
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(100)
            )
            Log.e("///// MessageItem: Image Url /////", "Message: ${messageInfo.imageUrl}")
            Card(
                shape = RoundedCornerShape(30),
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize(),
                backgroundColor = Color.Red
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Blocked user",
                        fontSize = 8.sp,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "This user has been blocked",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else if (messageInfo.userId == chatUiState.currentUserId) {

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    shape = RoundedCornerShape(30),
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize()
                        .combinedClickable(
                            onLongClick = {
                                onLongClick.invoke(messageInfo)
                            },
                            onClick = {

                            }
                        ),
                    backgroundColor = MaterialTheme.colors.onSurface
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = messageInfo.userName ?: "ERROR: No User",
                            fontSize = 8.sp,
                            color = MaterialTheme.colors.onBackground
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = messageInfo.message ?: "ERROR: No Message",
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
                coilImage(
                    url = messageInfo.imageUrl ?: "",
                    modifier = Modifier
                        .size(40.dp),
                    shape = RoundedCornerShape(100)
                )
            }
            Row {
                Card(
                    shape = RoundedCornerShape(30),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .wrapContentSize(),
                    backgroundColor = Color.LightGray
                ) {
                    Text(
                        text = "${messageInfo.timeSent?.toDate()?.day}/${messageInfo.timeSent?.toDate()?.month}/${messageInfo.timeSent?.toDate()?.year?.plus(1900)}",
                        fontSize = 10.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                coilImage(
                    url = messageInfo.imageUrl ?: "",
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(100)
                )
                Log.e("///// MessageItem: Image Url /////", "Message: ${messageInfo.imageUrl}")
                Card(
                    shape = RoundedCornerShape(30),
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize(),
                    backgroundColor = Color.LightGray
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = messageInfo.userName ?: "ERROR: No Message",
                            fontSize = 8.sp,
                            color = MaterialTheme.colors.onBackground
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = messageInfo.message ?: "ERROR: No User",
                                fontSize = 12.sp,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }
            Row {
                Card(
                    shape = RoundedCornerShape(30),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .wrapContentSize(),
                    backgroundColor = Color.LightGray
                ) {
                    Text(
                        text = "${messageInfo.timeSent?.toDate()?.day}/${messageInfo.timeSent?.toDate()?.month}/${messageInfo.timeSent?.toDate()?.year?.plus(1900)}",
                        fontSize = 10.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
