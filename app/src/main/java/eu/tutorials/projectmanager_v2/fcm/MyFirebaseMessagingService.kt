package eu.tutorials.projectmanager_v2.fcm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.tutorials.projectmanager_v2.R
import eu.tutorials.projectmanager_v2.activities.MainActivity
import eu.tutorials.projectmanager_v2.activities.SignInActivity
import eu.tutorials.projectmanager_v2.firebase.FirestoreClass
import eu.tutorials.projectmanager_v2.utils.Constants

class MyFirebaseMessagingService:FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG,"FROM: ${remoteMessage.from}")
        remoteMessage.data.isEmpty().let {
            Log.d(TAG,"Message data Payload: ${remoteMessage.data}")
            val title=remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message=remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            sendNotification(title,message,)
        }

        remoteMessage.notification?.let {
            Log.d(TAG,"Message notification body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG,"Refreshed Token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token:String?){
        Log.e(TAG, "Token : $token")
    }

    private fun sendNotification(title:String,message:String){
        val intent= if(FirestoreClass().getCurrentUserID().isNotEmpty()){
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelID=this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder=NotificationCompat.Builder(this, channelID,)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelID,"Channel ProjectManager title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())

    }

    companion object{
        private const val TAG="myFirebaseMsgService"
    }
}