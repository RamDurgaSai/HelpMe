package com.ramdurgasai.helpme

import android.app.Service
import android.content.Intent
import android.widget.Toast
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.os.postDelayed

@Suppress("DEPRECATION")
class mediaservice:Service() {

    override fun onBind(intent: Intent?): IBinder? {
    TODO()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setmaxvolume() // Set Max Volume
        var vibrator = vibrate() // Let's Vibrate the Device

        var mediaPlayer: MediaPlayer? = MediaPlayer.create(applicationContext, R.raw.alert)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
           stop(intent,vibrator,mediaPlayer) // Stoping Service ifself after alterting
        }

        // Add a hundler
        val handler : Handler? = Handler()
        val runnable : Runnable = Runnable { fun run(){
            stop(intent,vibrator,mediaPlayer) // Stoping Service ifself after waiting a minute ..
            // This will help when If oncompletion listener didn't work
            Toast.makeText(applicationContext,"Handler theread take aways media player",Toast.LENGTH_LONG).show()
        } }
        handler?.postDelayed(runnable,50*1000)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setmaxvolume(){
        val audioManager = applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate():Vibrator{
        val vibrator = applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val mVibratePattern = longArrayOf(0, 400, 100, 600, 100, 400, 100, 400)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(mVibratePattern, 0))
            }

        else{
            vibrator.vibrate(400)
            Toast.makeText(applicationContext,"Unable to Vibrate the device",Toast.LENGTH_LONG).show()
        }
        return vibrator

    }
    fun stop(intent: Intent?,vibrator : Vibrator?, mediaPlayer: MediaPlayer? ) {
        mediaPlayer?.release() // Releasing Mediaplayer
        vibrator?.cancel() // To Stop Vibration After Playing Sound
        applicationContext.stopService(intent)

    }
}

