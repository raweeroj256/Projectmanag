package je.raweeroj.projectmanag.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.activities.MainActivity
import je.raweeroj.projectmanag.activities.SignInActivity
import je.raweeroj.projectmanag.firebase.FirestoreClass
import je.raweeroj.projectmanag.utils.Constants

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("MyFirebaseMessaging","From: ${message.from}")
        println("********** MyFirebaseMessaging onMessageReceived ${message.data}")

        message.data.isNotEmpty().let {
            Log.e("MyFirebaseMessaging", "Data: ${message.data}")

            val title = message.data[Constants.FCM_KEY_TITLE]
            val messageBody = message.data[Constants.FCM_KEY_MESSAGE]
            sendNotification(title!!, messageBody!!)

        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG,"Refresh token : ${token}")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token:String?){
        val sharedPreferences =
            this.getSharedPreferences(Constants.PROJECTMANAJ_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }

    private fun sendNotification(title:String,messageBody:String){

        val intent = if(FirestoreClass().getCurrentUserId().isNotEmpty()){
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT, // crashes on API level 31+
            PendingIntent.FLAG_IMMUTABLE, // replace with this!
        )

        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"Channel Projecmang title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())

    }

    companion object{
        private const val TAG = "MyFirebaseMsgService"
    }
}