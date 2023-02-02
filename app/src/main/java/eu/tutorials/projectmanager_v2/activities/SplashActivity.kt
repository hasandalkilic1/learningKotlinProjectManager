package eu.tutorials.projectmanager_v2.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.models.UserModel
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    var checkedUserAutoLogin:Int=1

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

        Handler().postDelayed({
            var currentUserID=FirestoreClass().getCurrentUserID()
            if(currentUserID.isNotEmpty()){
                FirestoreClass().loadUserData(this)
                if(checkedUserAutoLogin==1){
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
        checkedUserAutoLogin=user.autoLogin
    }


}