package com.group12.starchat.model.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.group12.starchat.model.models.Friends
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val FRIENDS_COLLECTION_REF = "Friends"
const val USERS_COLLECTION_REF = "Users"

class FirebaseRepository() {

    var storage = FirebaseStorage.getInstance()

    fun user() = Firebase.auth.currentUser

    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    fun getFriendsRef(): CollectionReference {

        val friendsRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(getUserId()).collection(FRIENDS_COLLECTION_REF)

        return friendsRef
    }

    fun getFriendsList(
        userId: String
    ): Flow<Resources<List<Friends>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getFriendsRef()
                .orderBy("friendId")
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val entries = snapshot.toObjects(Friends::class.java)
                        Resources.Success(data = entries)
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
        name: String,
        imageUri: Uri?,
        status: String,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getFriendsRef().document().id
        var addFile = imageUri
        val addDiaryImageRef = storage.reference.child("Users/${getUserId()}/Friends/$documentId")

        addDiaryImageRef.putFile(addFile!!).addOnSuccessListener {
            addDiaryImageRef.downloadUrl.addOnSuccessListener { Url ->

                val diary = Friends(
                    friendId = documentId,
                    friendName = name,
                    friendStatus = status,
                    imageUrl = Url.toString()
                )

                getFriendsRef()
                    .document(documentId)
                    .set(diary)
                    .addOnCompleteListener { result ->
                        onComplete.invoke(result.isSuccessful)
                    }

            }
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