package com.ramdurgasai.helpme

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pengrad.telegrambot.Callback
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse
import com.ramdurgasai.helpme.OtpHandler.Companion.textFromOtp
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime


@ExperimentalTime
@RequiresApi(Build.VERSION_CODES.O)
class TelegramService: Service() {

    var channelId : String? = null
    val CHANNEL_ID = 10
    private var bot: TelegramBot? = null

    val NOTIFICATION_CHANNEL_ID = "com.ramdurgasai.helpme"
    val channelName = "Helpme Telegram Service"
    val NotificationTitle = "Connected to Server"
    val NotificationDNDTitle = "DND is Activated - Tap to Disable"



    override fun onCreate() {
        super.onCreate()
        val sharedPreference = applicationContext.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        channelId =  "@" + sharedPreference.getString("channelId",null)
        val botToken = sharedPreference?.getString("botToken" ,null)

        if(botToken == null){ return} // Skip If no botToken


        bot = TelegramBot(botToken.toString())
        bot?.setUpdatesListener { updates ->
            for(update in updates){
                updateHandler(update)
            }
            UpdatesListener.CONFIRMED_UPDATES_ALL
        }


        if(getSharedPreferences("PRIVATE", MODE_PRIVATE)
                .getBoolean("DND",false))
                    startForeGroundService(NotificationDNDTitle)
        else startForeGroundService()



    }
    private fun startForeGroundService(title:String = NotificationTitle){
        if (Build.VERSION.SDK_INT >= 26) {

            startForeground(CHANNEL_ID, getNotification(title))
            //App is running in background
        }

    }

    private fun getNotification(title: String): Notification? {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)!!
            manager!!.createNotificationChannel(notificationChannel)

            val contentIntent = PendingIntent.getActivity(
                this,
                0, Intent(this, MainActivity::class.java), 0
            )

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title).setContentIntent(contentIntent)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            return notification


        }
        return null
    }

    private fun updateHandler(update : Update) {
        val post = update.channelPost()
        var chat_id: String? = null
        if(post != null){
                chat_id = post.chat().username().toString()
        }else{ return }

        if(post != null && chat_id != channelId){
            val otp = post.text()
            if (otp != null || otp != "null"){
                val text = textFromOtp(otp)
                val otphandler = OtpHandler(applicationContext,text)
                otphandler.toclipboard()
                otphandler.makeToast("Otp : " + otp + " from server !")
                otphandler.sendBroadcast()
                // Deprecated
                buildNotification(otp)
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val otp = intent?.getStringExtra("otp")
        if (otp != null && channelId != null ) {
            val sendMessage = SendMessage(channelId, otp)
            try {
                bot?.execute(sendMessage, object : Callback<SendMessage?, SendResponse?> {
                    override fun onResponse(request: SendMessage?, response: SendResponse?) {}
                    override fun onFailure(request: SendMessage?, e: IOException?) {}
                })
            } catch (e: Exception) {
            }
        }
        val dndStatus = intent?.getStringExtra("dnd")


        if(dndStatus != null){
            val dndChecked = dndStatus.toBoolean()
            when(dndChecked){
                true ->{
                    startForeGroundService(title = NotificationDNDTitle)
                }
                false ->{
                    startForeGroundService()
                }
            }
        }
        return START_NOT_STICKY
    }

    fun makeToast(text: String){
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            run {
                Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onDestroy() {
        makeToast("Service Telegram is stopped")
        println("Telegram Service is Stopped ....")
        bot?.removeGetUpdatesListener()
        bot = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    fun buildNotification(text: String){
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID.toString()).setSmallIcon(R.drawable.ic_launcher_foreground
        ).setContentTitle("Otp")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setChannelId(10.toString())
        with(NotificationManagerCompat.from(applicationContext)){
            notify(SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt(), builder.build())
        }

    }

}



