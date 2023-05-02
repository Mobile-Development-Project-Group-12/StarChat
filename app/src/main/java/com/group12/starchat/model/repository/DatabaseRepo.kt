package com.group12.starchat.model.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.group12.starchat.model.models.Chat
import com.group12.starchat.model.models.Rooms
import com.group12.starchat.model.models.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Top Level Firebase References:
 */
const val USERS_COLLECTION_REF = "Users"
const val MESSAGEROOMS_COLLECTION_REF = "MessageRooms"

/**
 * Subcollection Firebase References:
 */
const val FRIENDS_COLLECTION_REF = "Friends"
const val BLOCKED_COLLECTION_REF = "Blocked"
const val MESSAGES_COLLECTION_REF = "Messages"

/**
 * This class is a type of Repository that is used to handle functionality from the
 * DatabaseRepo, which includes creating a user, logging in a user, and
 * checking if a user exists. This is done by using Firebase's Firestore.
 */
class DatabaseRepo {

    // gets an instance of Firebase's storage
    var storage = FirebaseStorage.getInstance()

    /**
     * Gets an instance of the current user
     */
    fun user() = Firebase.auth.currentUser

    /**
     * This function checks if a user is logged in.
     *
     * @return Boolean: true if the user is logged in, false otherwise
     */
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    /**
     * This function gets the user id of the current user
     *
     * @return String: the user id of the current user
     */
    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    /**
     * This function returns the path of a users information
     *
     * @return String: the path of a users information
     */
    fun getUsersRef(): CollectionReference {

        val friendsRef = Firebase.firestore.collection(USERS_COLLECTION_REF)

        return friendsRef
    }

    /**
     * This function returns the path of a users friends
     *
     * @param userId the user id of the current user
     * @return String: the path of a users friends
     */
    fun getFriendsRef(userId: String): CollectionReference {

        val friendsRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(userId).collection(FRIENDS_COLLECTION_REF)

        return friendsRef
    }

    /**
     * This function returns the path of a users blocked list
     *
     * @param userId the user id of the current user
     * @return String: the path of a users blocked list
     */
    fun getBlockedRef(userId: String): CollectionReference {

        val blockedRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(userId).collection(BLOCKED_COLLECTION_REF)

        return blockedRef
    }

    /**
     * This function returns the path of a users messages
     *
     * @param roomId the room id of the current user
     * @return String: the path of a users messages
     */
    fun getMessagesRef(roomId: String): CollectionReference {

        val messagesRef = Firebase.firestore.collection(MESSAGEROOMS_COLLECTION_REF).document(roomId).collection(MESSAGES_COLLECTION_REF)

        return messagesRef
    }

    /**
     * This function returns the path of a users rooms
     *
     * @return String: the path of a users rooms
     */
    fun getRoomsRef(): CollectionReference {

        val roomsRef = Firebase.firestore.collection(MESSAGEROOMS_COLLECTION_REF)

        return roomsRef
    }

    /**
     * This function finds a list of users in the database. The users are returned
     * if the userName matches the query.
     *
     * @param userName the user name of the current user
     * @param query the query to search for
     * @return Flow<Resources<List<User>>>: a list of users that match the query
     */
    fun getUsersByName(
        userName: String,
        query: String
    ): Flow<Resources<List<User>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getUsersRef()
                .whereNotEqualTo("userName", userName)
                .whereGreaterThanOrEqualTo("userName", query)
                .whereLessThanOrEqualTo("userName", query + "\uf8ff")
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val users = snapshot.toObjects(User::class.java)
                        Resources.Success(data = users)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    /**
     * This function gets a user by their user id
     *
     * @param userId the user id of the current user
     * @param onError the error that is thrown if the user is not found
     * @param onSuccess the user that is returned if the user is found
     */
    fun getUser(
        userId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (User?) -> Unit
    ) {
        getUsersRef()
            .document(userId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(User::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }
    }

    /**
     * This function gets the users friends, and returns them in a list.
     *
     * @return Flow<Resources<List<User>>>: a list of the users friends
     */
    fun getFriends(): Flow<Resources<List<User>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getFriendsRef(getUserId())
                .orderBy("userId")
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val friends = snapshot.toObjects(User::class.java)
                        Resources.Success(data = friends)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    /**
     * This function gets the users blocked list, and returns them in a list.
     *
     * @return Flow<Resources<List<User>>>: a list of the users blocked list
     */
    fun getBlocked(): Flow<Resources<List<User>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getBlockedRef(getUserId())
                .orderBy("userId")
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val blocked = snapshot.toObjects(User::class.java)
                        Resources.Success(data = blocked)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    /**
     * This function adds a friend to the users friends list
     *
     * @param friendId the user id of the friend to add
     * @param userName the user name of the friend to add
     * @param imageUrl the image url of the friend to add
     * @param bio the bio of the friend to add
     * @param email the email of the friend to add
     */
    fun addFriend(
        friendId: String,
        userName: String,
        imageUrl: String,
        bio: String,
        email: String
    ) {

        // remove from blocked list first if they are blocked
        getBlockedRef(getUserId())
            .whereEqualTo("userId", friendId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete()
                }
            }

        val friend = User(
            userId = friendId,
            userName = userName,
            imageUrl = imageUrl,
            bio = bio,
            email = email
        )

        getFriendsRef(getUserId())
            .document(friendId)
            .set(friend)
    }

    /**
     * This function removes a friend from the users friends list
     *
     * @param friendId the user id of the friend to remove
     */
    fun removeFriend(
        friendId: String
    ) {
        getFriendsRef(getUserId())
            .document(friendId)
            .delete()
            .addOnSuccessListener {
                Log.d("Friend Removed", "Friend removed")
            }
    }

    /**
     * This function blocks a user and adds them to the users blocked list
     *
     * @param userId the user id of the user to block
     * @param userName the user name of the user to block
     * @param imageUrl the image url of the user to block
     * @param bio the bio of the user to block
     * @param email the email of the user to block
     */
    fun blockUser(
        userId: String,
        userName: String,
        imageUrl: String,
        bio: String,
        email: String
    ) {
        // remove from friends list first if they are friends
        getFriendsRef(getUserId())
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete()
                }
            }

        val blocked = User(
            userId = userId,
            userName = userName,
            imageUrl = imageUrl,
            bio = bio,
            email = email
        )

        getBlockedRef(getUserId())
            .document(userId)
            .set(blocked)
    }

    /**
     * This function unblocks a user and removes them from the users blocked list
     *
     * @param userId the user id of the user to unblock
     */
    fun unBlockUser(
        userId: String
    ) {
        getBlockedRef(getUserId())
            .document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("User Unblocked", "User unblocked")
            }
    }

    /**
     * This function adds a user to the database
     *
     * @param userName the user name of the user to add
     * @param imageUri the image uri of the user to add
     * @param email the email of the user to add
     * @param onComplete a function to call when the user has been added
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun addUser(
        userName: String,
        imageUri: Uri,
        email: String,
        onComplete: (Boolean) -> Unit
    ) {
        val imageFile = imageUri
        val userProfilePictureRef = storage.reference.child("Users/${getUserId()}/Images/${getUserId()}")

        GlobalScope.launch {

            val imageDeferred = async {
                if (imageFile != null) {
                    userProfilePictureRef.putFile(imageFile).await()
                    userProfilePictureRef.downloadUrl.await().toString()
                } else {
                    ""
                }
            }

            val url = imageDeferred.await()

            val user = User(
                userId = getUserId(),
                userName = userName,
                imageUrl = url,
                email = email
            )

            getUsersRef()
                .document(getUserId())
                .set(user)
                .addOnCompleteListener { result ->
                    onComplete.invoke(result.isSuccessful)
                }

        }
    }

    /**
     * This function gets all the chat rooms that the current user is in
     *
     * @return Flow<Resources<List<Rooms>>>: a list of the chat rooms that the
     * current user is in
     */
    fun getRooms(): Flow<Resources<List<Rooms>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getRoomsRef()
                .orderBy("roomId")
                .whereArrayContains("users", getUserId())
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val rooms = snapshot.toObjects(Rooms::class.java)
                        Resources.Success(data = rooms)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    /**
     * This function gets a chat room by its id
     *
     * @param roomId the id of the chat room to get
     * @param onError a function to call if there is an error
     * @param onSuccess a function to call if the chat room is successfully retrieved
     */
    fun getRoom(
        roomId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Rooms?) -> Unit
    ) {
        getRoomsRef()
            .document(roomId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Rooms::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }
    }

    /**
     * This function creates a chat room
     *
     * @param roomName the name of the chat room to create
     * @param imageUri the image uri of the chat room to create
     * @param users the users to add to the chat room
     * @param onComplete a function to call when the chat room has been created
     */
    fun createRoom(
        roomName: String,
        imageUri: Uri?,
        users: ArrayList<String>,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getRoomsRef().document().id
        var addFile = imageUri
        val addRoomImageRef = storage.reference.child("Rooms/Images/$documentId")

        addRoomImageRef.putFile(addFile!!).addOnSuccessListener {
            addRoomImageRef.downloadUrl.addOnSuccessListener { Url ->

                val room = mapOf(
                    "roomId" to documentId,
                    "roomName" to roomName,
                    "imageUrl" to Url.toString(),
                    "users" to users
                )

                getRoomsRef()
                    .document(documentId)
                    .set(room)
                    .addOnCompleteListener { result ->
                        onComplete.invoke(result.isSuccessful)
                    }

            }
        }
    }

    /**
     * This function creates a chat room with a default image, or an external
     * firebase storage image
     *
     * @param roomId the id of the chat room to delete
     * @param onComplete a function to call when the chat room has been deleted
     */
    fun createRoomUrl(
        roomName: String,
        imageUrl: String,
        users: ArrayList<String>,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getRoomsRef().document().id

        val room = mapOf(
            "roomId" to documentId,
            "roomName" to roomName,
            "imageUrl" to imageUrl,
            "users" to users
        )

        getRoomsRef()
            .document(documentId)
            .set(room)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    /**
     * This function deletes a chat room
     *
     * @param roomId the id of the chat room to delete
     * @param onComplete a function to call when the chat room has been deleted
     */
    fun deleteRoom(
        roomId: String,
        onComplete: (Boolean) -> Unit
    ) {
        getRoomsRef()
            .document(roomId)
            .delete()
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    /**
     * This function updates a chat room
     *
     * @param roomId the id of the chat room to update
     * @param roomName the name of the chat room to update
     * @param imageUri the image uri of the chat room to update
     * @param imageUrl the image url of the chat room to update
     * @param users the users to add to the chat room
     * @param onComplete a function to call when the chat room has been updated
     */
    fun updateRoom(
        roomId: String,
        roomName: String,
        imageUri: Uri?,
        imageUrl: String,
        users: ArrayList<String>,
        onComplete: (Boolean) -> Unit
    ) {

        var updateImageFile = imageUri
        val updateRoomImageRef = storage.reference.child("Rooms/$roomId/Images/")

        val room = hashMapOf<String, Any>(
            "roomId" to roomId,
            "roomName" to roomName,
            "imageUrl" to imageUrl,
            "users" to users
        )

        GlobalScope.launch {

            val imageDeferred = async {
                if (updateImageFile != null) {
                    updateRoomImageRef.putFile(updateImageFile).await()
                    updateRoomImageRef.downloadUrl.await().toString()
                } else {
                    imageUrl
                }
            }

            val imageUrl = imageDeferred.await()

            val addImagetoRoom = room.apply {
                put("imageUrl", imageUrl)
            }

            getRoomsRef()
                .document(roomId)
                .update(addImagetoRoom)
                .addOnCompleteListener { result ->
                    onComplete.invoke(result.isSuccessful)
                }

        }
    }

    /**
     * This function updates whether a message has been seen, what the last
     * message is, and who sent the last message
     *
     * @param roomId the id of the chat room to update
     * @param message the message to update
     */
    fun messageToRoom(
        roomId: String,
        message: String,
    ) {
        var room = hashMapOf<String, Any>(
            "roomId" to roomId,
            "lastMessageSent" to message,
            "lastMessageSeen" to false,
            "lastMessageBy" to getUserId()
        )

        getRoomsRef()
            .document(roomId)
            .update(room)
    }

    /**
     * This function updates whether a message has been seen
     *
     * @param roomId the id of the chat room to update
     * @param messageSeen whether the message has been seen
     */
    fun seenMessage(
        roomId: String,
        messageSeen: Boolean,
    ) {
        var room = hashMapOf<String, Any>(
            "roomId" to roomId,
            "lastMessageSeen" to messageSeen,
        )

        getRoomsRef()
            .document(roomId)
            .update(room)
    }

    /**
     * This function gets the messages from a chat room
     *
     * @param roomId the id of the chat room to get the messages from
     * @return a flow of the messages from the chat room
     */
    fun getMessages(
        roomId: String
    ): Flow<Resources<List<Chat>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null
        try {
            snapshotStateListener = getMessagesRef(roomId)
                .orderBy("timeSent", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val messages = snapshot.toObjects(Chat::class.java)
                        Resources.Success(data = messages)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    /**
     * This function gets the updates the current users profile information. This is
     * used when the user updates their profile information.
     *
     * @param userName the name of the user
     * @param imageUri the image uri of the user
     * @param imageUrl the image url of the user
     * @param bio the bio of the user
     * @param onComplete a function to call when the profile has been updated
     */
    fun updateProfile(
        userName: String,
        imageUri: Uri?,
        imageUrl: String,
        bio: String,
        onComplete: (Boolean) -> Unit
    ) {

        var updateImageFile = imageUri
        val updateEntryImageRef = storage.reference.child("Users/${getUserId()}/Images/${getUserId()}")

        var user = hashMapOf<String, Any>(
            "userId" to getUserId(),
            "userName" to userName,
            "bio" to bio
        )

        GlobalScope.launch {

            val imageDeferred = async {
                if (updateImageFile != null) {
                    updateEntryImageRef.putFile(updateImageFile).await()
                    updateEntryImageRef.downloadUrl.await().toString()
                } else {
                    imageUrl
                }
            }

            val imageUrl = imageDeferred.await()

            val addImagetoProfile = user.apply {
                put("imageUrl", imageUrl)
            }

            getUsersRef()
                .document(getUserId())
                .update(addImagetoProfile)
                .addOnCompleteListener { result ->
                    onComplete.invoke(result.isSuccessful)
                }
        }
    }

    /**
     * This function sends a message to a chat room
     *
     * @param imageUrl the image url of the user
     * @param userName the name of the user
     * @param userId the id of the user
     * @param message the message to send
     * @param roomId the id of the chat room to send the message to
     * @param onComplete a function to call when the message has been sent
     */
    fun sendMessage(
        imageUrl: String,
        userName: String,
        userId: String,
        message: String,
        roomId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getMessagesRef(roomId).document().id

        val chat = Chat(
            userName = userName,
            userId = userId,
            message = message,
            timeSent = Timestamp.now(),
            imageUrl = imageUrl,
            messageId = documentId
        )

        getMessagesRef(roomId)
            .document(documentId)
            .set(chat)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    /**
     * This function deletes a message from a chat room
     *
     * @param roomId the id of the chat room to delete the message from
     * @param messageId the id of the message to delete
     * @param onComplete a function to call when the message has been deleted
     */
    fun deleteMessage(
        roomId: String,
        messageId: String,
        onComplete: (Boolean) -> Unit
    ) {
        getMessagesRef(roomId)
            .document(messageId)
            .delete()
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    /**
     * This function signs out the logged in user
     */
    fun signOut() = Firebase.auth.signOut()

}

/**
 * This class is used to determine the state of a resource. If the resource is
 * successful then the data is returned. If the resource is loading then the
 * data is null. If the resource is a failure then a throwable error is returned.
 */
sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Failure<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}