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

class DatabaseRepo() {

    var storage = FirebaseStorage.getInstance()

    fun user() = Firebase.auth.currentUser

    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    val scope = GlobalScope

    // fun getEmail(): String = Firebase.auth.currentUser?.updateProfile()

    fun getUsersRef(): CollectionReference {

        val friendsRef = Firebase.firestore.collection(USERS_COLLECTION_REF)

        return friendsRef
    }

    fun getFriendsRef(userId: String): CollectionReference {

        val friendsRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(userId).collection(FRIENDS_COLLECTION_REF)

        return friendsRef
    }

    fun getBlockedRef(userId: String): CollectionReference {

        val blockedRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(userId).collection(BLOCKED_COLLECTION_REF)

        return blockedRef
    }

    fun getMessagesRef(roomId: String): CollectionReference {

        val messagesRef = Firebase.firestore.collection(MESSAGEROOMS_COLLECTION_REF).document(roomId).collection(MESSAGES_COLLECTION_REF)

        return messagesRef
    }

    fun getRoomsRef(): CollectionReference {

        val roomsRef = Firebase.firestore.collection(MESSAGEROOMS_COLLECTION_REF)

        return roomsRef
    }

    fun getAllUsers(): Flow<Resources<List<User>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getUsersRef()
                .orderBy("userId")
                .whereNotEqualTo("userId", getUserId())
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

    fun getRoomUsers(
        roomId: String
    ): Flow<Resources<List<User>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getUsersRef()
                .orderBy("userId")
                .whereEqualTo("roomId", roomId)
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

    fun addUser(
        userName: String,
        imageUri: Uri,
        email: String,
        onComplete: (Boolean) -> Unit
    ) {
        var imageFile = imageUri
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

    fun signOut() = Firebase.auth.signOut()

}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Failure<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}