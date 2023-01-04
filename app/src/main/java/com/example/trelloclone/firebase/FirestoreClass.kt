package com.example.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trelloclone.activity.MainActivity
import com.example.trelloclone.activity.MyProfileActivity
import com.example.trelloclone.activity.SignInActivity
import com.example.trelloclone.activity.SignUpActivity
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore =  FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "error waiting for document", e)
            }
    }
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile Data updated successfully!", Toast.LENGTH_SHORT)
                    .show()
                activity.profileUpdatesSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.",e)
                Toast.makeText(activity, "Error when updating the profile!", Toast.LENGTH_SHORT)
                    .show()
            }

    }
    fun loadUserDate(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUi(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("sigInUser", "Error while getting loggedIn user details", e)
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}