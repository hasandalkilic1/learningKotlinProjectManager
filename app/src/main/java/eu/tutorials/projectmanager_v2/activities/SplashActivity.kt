package eu.tutorials.projectmanager_v2.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.models.UserModel
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "Windpower.otf")
        tv_app_name.typeface = typeface

        var currentUserID=FirestoreClass().getCurrentUserID()

        if(currentUserID.isNotEmpty()){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this)
        }

        Handler().postDelayed({
            if(currentUserID.isNotEmpty()){

                if(checkedUserAutoLogin=="1"){
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
                else{
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }

            }else{
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 2500)
    }

    fun getCurrentUserAutoLogin(user: UserModel){
        hideProgressDialog()
        checkedUserAutoLogin=user.autoLogin
    }


}