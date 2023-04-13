package com.group12.starchat.model.repository

import androidx.compose.runtime.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.group12.starchat.model.models.Chat
import com.group12.starchat.model.models.Friends
import com.group12.starchat.model.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


/**
 * Top Level Firebase References:
 */
const val USERS_COLLECTION_REF = "Users"
const val MESSAGEROOMS_COLLECTION_REF = "MessageRooms"

/**
 * Subcollection Firebase References:
 */
const val FRIENDS_COLLECTION_REF = "Friends"
const val MESSAGES_COLLECTION_REF = "Messages"

class DatabaseRepo() {

    var storage = FirebaseStorage.getInstance()
    var functions = FirebaseFunctions.getInstance()

    fun user() = Firebase.auth.currentUser

    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    fun getUsersRef(): CollectionReference {

        val friendsRef = Firebase.firestore.collection(USERS_COLLECTION_REF)

        return friendsRef
    }

    fun getMessagesRef(RoomId: String): CollectionReference {

        val messagesRef = Firebase.firestore.collection(MESSAGEROOMS_COLLECTION_REF).document(RoomId).collection(MESSAGES_COLLECTION_REF)

        return messagesRef
    }

    fun getRoomsRef(): CollectionReference {

        val roomsRef = Firebase.firestore.collection(MESSAGEROOMS_COLLECTION_REF)

        return roomsRef
    }

    fun getFriendsList(): Flow<Resources<List<Friends>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getUsersRef()
                .orderBy("userId")
                .whereNotEqualTo("userId", getUserId())
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val friends = snapshot.toObjects(Friends::class.java)
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

    fun getRooms(): Flow<Resources<List<Friends>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getRoomsRef()
                .orderBy("RoomId")
                .whereArrayContains("Users", getUserId())
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val friends = snapshot.toObjects(Friends::class.java)
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

    fun addUser(
        id: String,
        onComplete: (Boolean) -> Unit
    ) {
        //val documentId = getUsersRef().document().id

        val user = User(
            userId = id,
            userName = "",
            Status = "",
            imageUrl = ""
        )

        getUsersRef()
            .document(id)
            .set(user)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun getMessages(
        RoomId: String
    ): Flow<Resources<List<Chat>>> = callbackFlow {

    }

    fun sendMessage(
        FriendId: String,
        userId: String,
        message: String,
        onComplete: (Boolean) -> Unit
    ) {

    }

    fun MyFunction(): String {
        var result = ""

        functions.getHttpsCallable("http://localhost:5000/starchat-62e58/us-central1/function7").call().addOnCompleteListener {
            result = it.result?.data.toString()
        }

        return result
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