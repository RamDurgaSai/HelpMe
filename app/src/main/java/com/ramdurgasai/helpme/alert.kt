package com.ramdurgasai.helpme

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalTime

class alert(val context :Context?){
    val loggedOutMessages: MutableList<String> = mutableListOf("Off Duty" , "Duty Stopped","You have been logged out as you were out of network. Please check your network and turn on gps/location services to login again.\n" +
            "-Swiggy") // A list contains all logged out messages...
    val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.alert)
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val pattern = longArrayOf(1000, 2000, 2000, 2000, 3000, 3000, 2000, 2000, 1000, 1000)


    @RequiresApi(Build.VERSION_CODES.O)
    fun alert(text : String?){
        if(isDutyTime() && isLoggedOutMessage(text)){
            // If Logged Out In Duty Time
            startAlert()
            Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()
        }else if (isLoggedOutMessage(text)){
            // If logged out when not in Duty
            Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "It's not Duty Time ... So I am not making any noise", Toast.LENGTH_LONG).show()

        }

    }

    private fun isLoggedOutMessage(text: String?):Boolean{
        //returns true if given text is a Logged Out Message
        return text in loggedOutMessages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isDutyTime():Boolean {
        //Gets The Current Time
        // If Current Time is  Duty Time then returns true else false
        val currentHour :Int? = LocalTime.now().hour
        val currentMinute :Int? = LocalTime.now().minute

        if (currentHour != null && currentMinute != null) {
            if (currentHour == 10 ) return currentMinute in 30..59
            if (currentHour == 23) return currentMinute in 0..30
            return currentHour in 10..23
        }else return false

        return false
    }

    private fun startAlert(){
        //Starts All the alerts ...
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        mediaPlayer?.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    }



}