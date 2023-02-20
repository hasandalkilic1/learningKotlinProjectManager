package eu.tutorials.projectmanager_v2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.models.UserModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }

        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()

    }

    private fun registerUser(){
        val name:String = et_name_sign_up.text.toString().trim{it <= ' '}
        val email:String = et_email_sign_up.text.toString().trim{it <= ' '}
        val password:String = et_password_sign_up.text.toString().trim{it <= ' '}

        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = UserModel(firebaseUser.uid,name,registeredEmail)
                    //If progress dialog is not hiding here, it maybe due to Cloud firestore rules.
                    FirestoreClass().registerUser(this@SignUpActivity,user)
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }

    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter a Name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please Enter a E-Mail")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please Enter a Password")
                false
            }
            else ->{
                return true
            }
        }
    }
}