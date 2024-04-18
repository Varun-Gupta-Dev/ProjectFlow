package com.example.projectflow.firebase

import android.app.Activity
import android.util.Log
import com.example.projectflow.activities.MainActivity
import com.example.projectflow.activities.MyProfileActivity
import com.example.projectflow.activities.SignInActivity
import com.example.projectflow.activities.SignUpActivity
import com.example.projectflow.models.User
import com.example.projectflow.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess( )
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun loadUserData(activity : Activity){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
               val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity->{
                        activity.signInSuccess(loggedInUser)

                    }
                    is MainActivity->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener {
                    e->
                when(activity){
                    is SignInActivity->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser", "Error writing document")
            }
    }

    fun getCurrentUserId(): String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}