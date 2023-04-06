package com.group12.starchat.view.screens.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.group12.starchat.view.theme.StarChatTheme
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory

@Composable
fun ChatScreen(
    channelType: String,
    channelId: String,
) {

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

}

@Preview
@Composable
fun ChatScreenPreview() {
    StarChatTheme() {

        ChatScreen(channelType = "messaging", channelId = "TestChannel_266b8a12-8fba-4f18-9d6d-40ba0bb9ce8e")
    }
}