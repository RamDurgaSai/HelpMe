package com.ramdurgasai.helpme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import java.time.LocalTime

class MyReceiver : BroadcastReceiver() {
    val logout_message = "You have been logged out as you were out of network. Please check your network and turn on gps/location services to login again.\n" +
            "-Swiggy"
    override fun onReceive(context: Context?, intent: Intent?) {

        val currentHour :Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime.now().hour
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val smsText: String = getTextFromSms(intent?.extras)

        if (smsText == logout_message) {
            if (currentHour > 10 || currentHour == 10) {
                Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_LONG).show()
                alert(context)

            } else {
                Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "It's not Duty Time ... So I am not making any noise", Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun alert(context: Context?) {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)


        // It's time to alert user by Playing alert.mp3
        val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.alert)
        mediaPlayer?.start()

        // Vibrate the User
        val pattern = longArrayOf(1000, 2000, 2000, 2000, 3000, 3000, 2000, 2000, 1000, 1000)
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        //vibrator.vibrate(20000)
        //vibrator.vibrate(pattern,-1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    }
    private fun getTextFromSms(extras: Bundle?): String {
        val pdus = extras?.get("pdus") as Array<*>
        val format = extras.getString("format")
        var txt = ""
        for (pdu in pdus) {
            val smsmsg = getSmsMsg(pdu as ByteArray?, format)
            val submsg = smsmsg?.displayMessageBody
            submsg?.let { txt = "$txt$it" }
        }
        return txt
    }

    private fun getSmsMsg(pdu: ByteArray?, format: String?): SmsMessage? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> SmsMessage.createFromPdu(pdu, format)
            else -> SmsMessage.createFromPdu(pdu)
        }
    }

    companion object {
        private val TAG = MyReceiver::class.java.simpleName
    }
}
