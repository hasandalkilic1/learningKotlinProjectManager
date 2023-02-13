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

    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList:ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board=i.toObject(Board::class.java)!!
                    board.documentID=i.id
                    boardList.add(board)
                }
                activity.populateBoardListToUI(boardList)
            }
            .addOnFailureListener {
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

    fun addUpdateTaskList(activity: TaskListActivity,board:Board){
        val taskListHashMap=HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]=board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"TaskList updated successfully")
                activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentID:String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentID)
            .get()
            .addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board=document.toObject(Board::class.java)!!
                board.documentID=document.id
                activity.boardDetails(board)
            }
            .addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
            }

    }

     fun loadUserData(activity: Activity,readBoardsList:Boolean=false){
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
                         activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
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

    fun getAssignedMembersListDetails(activity: MembersActivity,assignedTo:ArrayList<String>){
        mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())

                val usersList:ArrayList<UserModel> = ArrayList()

                for (i in document.documents){
                    val user =i.toObject(UserModel::class.java)!!
                    usersList.add(user)
                }

                activity.setUpMembersList(usersList)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
            }
    }

    fun getMemberDetails(activity: MembersActivity, email:String,noMember:String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                if (document.documents.size>0){
                    val user=document.documents[0].toObject(UserModel::class.java)!!
                    activity.memberDetails(user)
                }
                else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(noMember)
                }
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting user details",e)
            }
    }

    fun assignMemberToBoard(activity: MembersActivity,board: Board,user:UserModel){
        val assignedToHashMap=HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO]=board.assignedTo

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
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