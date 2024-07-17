package com.example.projectflow.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectflow.activities.CreateBoardActivity
import com.example.projectflow.activities.MainActivity
import com.example.projectflow.activities.MyProfileActivity
import com.example.projectflow.activities.SignInActivity
import com.example.projectflow.activities.SignUpActivity
import com.example.projectflow.models.Board
import com.example.projectflow.models.User
import com.example.projectflow.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }
// How this createBoard function is working-
   /*1. Firestore Reference: mFireStore.collection(Constants.BOARDS) obtains a reference to the Firestore collection
 where boards are stored
    2. New Document: .document() generates a new unique document ID for the board.
    3.Data Upload: .set(board, SetOptions.merge()) sets the data from the board object to the newly created
    document. SetOptions.merge() ensures that existing fields in the document are not overwritten if they are not present
    in the board object.
    4. Success Listener: .addOnSuccessListener handles the successful creation of the board. It logs a
    success message, displays a Toast notification, and calls the boardCreatedSuccessfully()
    method on the CreateBoardActivity to notify it of the successful operation.
    5. Failure Listener: .addOnFailureListener handles any errors that occur during the board creation
    process. It hides any progress dialog being displayed, logs the error, and potentially provides
    feedback to the user (not explicitly shown in this code snippet).
 */
    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
            .document()// Generates a new unique document ID
            .set(board, SetOptions.merge())// Sets the board data to the new document
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board Created Successfully.")

                Toast.makeText(activity,
                    "Board created successfully.", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()// Notify the activity of success
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()// Hide any progress dialog
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    exception
                )// Log the error
            }

    }


        fun getBoardsList(activity: MainActivity){
            activity.hideProgressDialog()
            mFireStore.collection(Constants.BOARDS)
                .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
                .get()
                .addOnSuccessListener {
                    document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val boardList: ArrayList<Board> = ArrayList()
                    for(i in document.documents){
                        val board = i.toObject(Board::class.java)!!
                        board.documentId = i.id
                        boardList.add(board)

                    }

                    activity.populateBoardsListToUI(boardList)
                }.addOnFailureListener {
                    e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                }
        }

        fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {

            mFireStore.collection(Constants.USERS)
                .document(getCurrentUserId())
                .update(userHashMap)
                .addOnSuccessListener {
                    Log.i(activity.javaClass.simpleName, "Profile Data updated successfully.")
                    Toast.makeText(activity, "Profile Updated Successfully!", Toast.LENGTH_SHORT)
                        .show()
                    activity.profileUpdateSuccess()
                }.addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while updating profile",
                        e
                    )
                    Toast.makeText(activity, "Error when updating the profile!", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {

            mFireStore.collection(Constants.USERS)
                .document(getCurrentUserId())
                .get()
                .addOnSuccessListener { document ->
                    val loggedInUser = document.toObject(User::class.java)!!

                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccess(loggedInUser)

                        }

                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }

                        is MyProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }

                }.addOnFailureListener { e ->
                    when (activity) {
                        is SignInActivity -> {
                            activity.hideProgressDialog()
                        }

                        is MainActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e("SignInUser", "Error writing document")
                }
        }

        fun getCurrentUserId(): String {

            var currentUser = FirebaseAuth.getInstance().currentUser
            var currentUserID = ""
            if (currentUser != null) {
                currentUserID = currentUser.uid
            }
            return currentUserID
        }
    }
