package com.group12.starchat.model.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * This class is a type of Repository that is used to handle functionality from the
 * AuthenticationRepo, which includes creating a user, logging in a user, and
 * checking if a user exists. This is done by using Firebase Authentication.
 */
class AuthenticationRepo {

    /**
     * This function checks if a user is logged in.
     */
    fun hasUser():Boolean = Firebase.auth.currentUser != null

    /**
     * This function creates a user using Firebase's Firestore, to create a user
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param onComplete a callback function that returns a boolean value
     **/
    suspend fun createUser(
        email:String,
        password:String,
        onComplete:(Boolean) ->Unit
    ) = withContext(Dispatchers.IO){
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    onComplete.invoke(true)
                }else{
                    onComplete.invoke(false)
                }
            }.await()
    }

    /**
     * This function logs in a user using Firebase's Authentication. This is done by
     * using the email and password of the user.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param onComplete a callback function that returns a boolean value
     **/
    suspend fun login(
        email:String,
        password:String,
        onComplete:(Boolean) ->Unit
    ) = withContext(Dispatchers.IO){
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    onComplete.invoke(true)
                }else{
                    onComplete.invoke(false)
                }
            }.await()
    }
}