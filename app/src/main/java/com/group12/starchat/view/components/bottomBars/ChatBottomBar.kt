package com.group12.starchat.view.components.bottomBars

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.group12.starchat.viewModel.ChatViewModel

@Preview()
@Composable
fun ChatBottomBar(
    chatViewModel: ChatViewModel = ChatViewModel(),
    roomId: String = "",
) {
    /**
     * Message Input
     */

    val chatUiState = chatViewModel.chatUiState
    val currentRoomUiState = chatViewModel.currentRoomUiState

    var messageInput by remember { mutableStateOf("") }

    val fr = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        // chatViewModel.getRoomInfo(roomId)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = messageInput,
            onValueChange = {messageInput = it},
            singleLine = false,
            modifier = Modifier
                .width(300.dp)
                .focusRequester(fr),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    chatViewModel.postMessage(
                        userName = chatUiState.currentUser.userName,
                        roomId = roomId,
                        message = messageInput,
                        imageUrl = chatUiState.currentUser.imageUrl
                    )

                    messageInput = ""

                    // close keyboard
                    fr.freeFocus()

                }
            ),
            shape = RoundedCornerShape(30),
            label = {
                Text(text = "Type Here!")
            },
            maxLines = 3
        )
        Button(
            onClick = {
                chatViewModel.postMessage(
                    userName = chatUiState.currentUser.userName,
                    roomId = roomId,
                    message = messageInput,
                    imageUrl = chatUiState.currentUser.imageUrl
                )

                chatViewModel.updateMessageStatus(
                    roomId = currentRoomUiState.currentRoom.roomId,
                    message = messageInput,
                )

                messageInput = ""

                // close keyboard
                fr.freeFocus() },
            shape = RoundedCornerShape(100),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            ),
            modifier = Modifier.wrapContentSize()
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send Message",
                tint = MaterialTheme.colors.background,
                modifier = Modifier
                    .size(width = 30.dp, height = 30.dp)
            )
        }
    }
}