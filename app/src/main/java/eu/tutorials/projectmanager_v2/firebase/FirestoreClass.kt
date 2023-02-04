package eu.tutorials.projectmanager_v2.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import eu.tutorials.projectmanager_v2.activities.*
import eu.tutorials.projectmanager_v2.models.Board
import eu.tutorials.projectmanager_v2.models.UserModel
import eu.tutorials.projectmanager_v2.utils.Constants


class FirestoreClass {

    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity,userInfo: UserModel){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName,"Error writing document",e)
            }
    }

    fun createBoard(activity:CreateBoardActivity,boardInfo:Board){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board created successfully")
                Toast.makeText(activity,"Board created successfully.",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
            }
    }


    fun updateUserProfileData(activity: Activity,userHashMap:HashMap<String,Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                when(activity){
                    is MyProfileActivity->{
                        Log.i(activity.javaClass.simpleName,"profile data updated successfully")
                        activity.profileUpdateSuccess()
                    }
                    is SignInActivity->{
                        activity.hideProgressDialog()
                        Log.i(activity.javaClass.simpleName,"User auto login updated successfully")
                    }
                }

            }
            .addOnFailureListener {
                e->
                when(activity){
                    is MyProfileActivity->{
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)
                    }
                    is SignInActivity->{
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)
                    }
                }
            }

    }

     fun loadUserData(activity: Activity){
         mFirestore.collection(Constants.USERS)
             .document(getCurrentUserID())
             .get()
             .addOnSuccessListener { document ->
                 val loggedInUser=document.toObject(UserModel::class.java)!!

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
                     is SplashActivity->{
                         activity.getCurrentUserAutoLogin(loggedInUser)
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
                     is MyProfileActivity -> {
                         activity.hideProgressDialog()
                     }
                     is SplashActivity->{
                         activity.hideProgressDialog()
                     }
                 }
                 Log.e("signInUser","Error writing document",e)
             }
     }

    fun getCurrentUserID():String{
        var currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }
}