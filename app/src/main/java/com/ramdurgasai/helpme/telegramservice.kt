package com.ramdurgasai.helpme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.ramdurgasai.helpme.otphandler.Companion.botToken

@RequiresApi(Build.VERSION_CODES.O)
class telegramservice: Service() {

    val chatId : String = "@otp_helpme"
    val CHANNEL_ID = 10
    private var bot: TelegramBot? = null



    override fun onCreate() {
        super.onCreate()
        val sharedPreference = applicationContext.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val botToken = sharedPreference?.getString("botToken" ,null)

        if(botToken == null){ return} // Skip If no botToken

        println("Telegram Service is Started ....")
        bot = TelegramBot(botToken.toString())
        bot?.setUpdatesListener { updates ->
            for(update in updates){
                updateHandler(update)
            }
            UpdatesListener.CONFIRMED_UPDATES_ALL
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(CHANNEL_ID,
                    "Forground Notification",
                    NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Helpme")
                    .setContentText("Connected to Server").build()
            startForeground(1, notification)

        }
    }
    private fun updateHandler(update : Update) {
        val text:String? = update?.message()?.text()
        if (update?.message() == null) { return}
        if (text != null || text != "null"){
            makeToast("Otp : " + text + " from server !" )
            otphandler(applicationContext).toclipboard(text.toString())
            buildNotification(text.toString())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("On Start command is called In Telegram Service")

        if (intent?.getStringExtra("otp") != ""){
            val sendMessage = SendMessage(chatId, intent?.getStringExtra("otp"))

            bot?.execute(sendMessage, object : Callback<SendMessage?, SendResponse?> {
                override fun onResponse(request: SendMessage?, response: SendResponse?) {}
                override fun onFailure(request: SendMessage?, e: IOException?) {}
            })
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



