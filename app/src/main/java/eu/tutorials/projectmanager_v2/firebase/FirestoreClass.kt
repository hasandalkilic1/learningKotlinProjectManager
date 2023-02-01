package eu.tutorials.projectmanager_v2.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import eu.tutorials.projectmanager_v2.activities.*
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