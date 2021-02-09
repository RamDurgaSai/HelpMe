package com.ramdurgasai.helpme

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import java.time.LocalTime
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.widget.Toast
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationListener1 : NotificationListenerService() {

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val currentHour :Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime.now().hour.toInt()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val logout_message :String = "Off Duty"
        val extraBundle: Bundle? = sbn?.notification?.extras ?: null
        val tittle = extraBundle?.get(Notification.EXTRA_TITLE)

        if (tittle == logout_message){
            if (currentHour>=10){
                Toast.makeText(getApplicationContext(),"Duty is Stopped Check The App Once",Toast.LENGTH_LONG).show()
                alert()
            }else{
                Toast.makeText(getApplicationContext(),"Duty is Stopped Check The App Once",Toast.LENGTH_SHORT).show()
                Toast.makeText(getApplicationContext(),"It's not Duty Time ... So I am not making any noise",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {

    }
    private fun alert(){
        val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC),0)


        // It's time to alert user by Playing alert.mp3
        val mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.alert)
        mediaPlayer?.start()

        // Vibrate the User
        val pattern = longArrayOf(1000,2000,3000,1000,2000,3000,1000,2000,3000,1000)
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        //vibrator.vibrate(pattern,-1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }

    }
}