package eu.tutorials.projectmanager_v2.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import eu.tutorials.projectmanager_v2.activities.SignInActivity
import eu.tutorials.projectmanager_v2.activities.SignUpActivity
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
     fun signInUser(activity: SignInActivity){
         mFirestore.collection(Constants.USERS)
             .document(getCurrentUserID())
             .get()
             .addOnSuccessListener { document ->
                 val loggedInUser=document.toObject(UserModel::class.java)
                 if(loggedInUser!=null){
                     activity.signInSuccess(loggedInUser)
                 }
             }.addOnFailureListener {
                 e->
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