package com.group12.starchat.view.screens.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group12.starchat.R
import com.group12.starchat.model.models.User
import com.group12.starchat.view.components.coilImages.coilImage
import com.group12.starchat.view.theme.StarChatTheme
import com.group12.starchat.viewModel.HomeUiState
import com.group12.starchat.viewModel.HomeViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel?,
    onSignOutClick: () -> Unit,
    onChannelClick: (String) -> Unit,
) {
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

    val applicationContext = LocalContext.current

    // 1 - Set up the OfflinePlugin for offline storage
    val offlinePluginFactory = StreamOfflinePluginFactory(
        config = Config(
            backgroundSyncEnabled = true,
            userPresence = true,
            persistenceEnabled = true,
            uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
        ),
        appContext = applicationContext,
    )

    Log.e("/// PHASE 1", "offline storage")

    // 2 - Set up the client for API calls and with the plugin for offline storage
    val client = ChatClient.Builder(/*b67pax5b2wdq"*/"sm8um5rv2gtx", applicationContext)
        .withPlugin(offlinePluginFactory)
        .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
        .build()

    Log.e("/// PHASE 2", "client setup")

    // 3 - Authenticate and connect the user
    val user = io.getstream.chat.android.client.models.User(
        id = "FriendTestUser_4e88e519-ff4d-4f35-be10-f1274f6a7015",
        name = "FriendTestUser",
        image = "https://bit.ly/2TIt8NR",
    )

    val instance = ChatClient.instance()

    Log.e("/// PHASE 3", "log in user")

    client.connectUser(
        user = user,
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiRnJpZW5kVGVzdFVzZXJfNGU4OGU1MTktZmY0ZC00ZjM1LWJlMTAtZjEyNzRmNmE3MDE1In0.QapG7H4trLYLevAgA5VA21Bm6x7ABInur-rQEbiKVeo"
    ).enqueue()

    //val channelClient = client.channel(channelType = "messaging", channelId = "general")

    Log.i("/// Info about the current user", "Current User is :${client.getCurrentUser()} ")
    Log.i("/// Info about the current users token", "Current Token is: ${client.getCurrentToken()} ")

    Log.e("/// PHASE 4", "connect user")

    MessagesScreen(channelId = "messaging:TestChannel_266b8a12-8fba-4f18-9d6d-40ba0bb9ce8e")

    Log.e("/// PHASE 5", "display message Screen")

    StarChatTheme() {
        ChannelsScreen(
            title = stringResource(id = R.string.app_name),
            isShowingSearch = true,
            onItemClick = { channel ->
                onChannelClick(channel.id)
            },
            onBackPressed = {  },
            onHeaderAvatarClick = {
                homeViewModel?.signOut()
                onSignOutClick() },
            onHeaderActionClick = {

                Log.e("CREATING CHANNEL ///////////", "creating channel")

                client.createChannel(
                    channelType = "messaging",
                    channelId = "123",
                    memberIds = listOf("thierry", "tutorial-droid"),
                    extraData = emptyMap()
                ).enqueue { result ->
                    if (result.isSuccess) {
                        val channel = result.data()
                        Log.d("Line 95, Created Channel ///////////", "Created Channel with id of ${channel.id}")
                    } else {
                        Log.e("Line 97, ERROR CREATING CHANNEL ///////////", "Error creating channel: Error = ${result.error().message}", result.error().cause)
                    }
                }

            },
        )
    }
}

@Composable
fun FriendItem(userInfo: User) {
    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            coilImage(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/440px-Image_created_with_a_mobile_phone.png",
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(100)
            )
            Card(
                shape = RoundedCornerShape(15),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = userInfo.userName ?: "ERROR: No Message",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = userInfo.Status ?: "ERROR: No User",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
    }
}