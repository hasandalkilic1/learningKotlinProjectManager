package eu.tutorials.projectmanager_v2.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.installations.FirebaseInstallations
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.adapters.BoardItemsAdapter
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.models.Board
import eu.tutorials.projectmanager_v2.models.UserModel
import eu.tutorials.projectmanager_v2.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int=11
        const val CREATE_BOARD_REQUEST_CODE:Int=12
    }

    private lateinit var mUserName:String
    private lateinit var mSharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        nav_view.setNavigationItemSelectedListener (this)

        mSharedPreferences=this.getSharedPreferences(Constants.PROJECTMANAGER_PREFERENCES,Context.MODE_PRIVATE)

        val tokenUpdated=mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)

        if (tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this,true)
        }
        else{
            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(this@MainActivity){instanceIdResult->
                updateFCMToken(instanceIdResult.token)
            }
        }

        FirestoreClass().loadUserData(this,true)

        fab_create_board.setOnClickListener {
            val intent=Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor:SharedPreferences.Editor=mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this,true)
    }

    private fun updateFCMToken(token:String){
        val userHashMap=HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN]=token

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    fun populateBoardListToUI(boardList:ArrayList<Board>){
        hideProgressDialog()

        if(boardList.size >0){
            rv_boards_list.visibility=View.VISIBLE
            tv_no_boards_available.visibility=View.GONE

            rv_boards_list.layoutManager=LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)

            val adapter=BoardItemsAdapter(this,boardList)
            rv_boards_list.adapter=adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun OnClick(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentID)
                    startActivity(intent)
                }
            })
        }
        else{
            rv_boards_list.visibility=View.GONE
            tv_no_boards_available.visibility=View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user:UserModel,readBoardsList:Boolean){
        hideProgressDialog()

        mUserName=user.name

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image)

        tv_username.text=user.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode== MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }
        else if(resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().loadUserData(this,true)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                val intent =Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}