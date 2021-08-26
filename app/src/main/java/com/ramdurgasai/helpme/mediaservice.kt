package com.ramdurgasai.helpme

import android.app.*
import android.content.Intent
import android.widget.Toast
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import androidx.annotation.RequiresApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("DEPRECATION")
class MediaService:Service() {
    var intent:Intent? = null
    var mediaPlayer:MediaPlayer? = null
    var vibrator:Vibrator? = null
    lateinit var audioManager: AudioManager
    var minVolume : Int? = null
    var maxVolume :Int? = null
    var volume: Int? = null
    var step:Int? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        step = (maxVolume?.minus(minVolume!!))?.div(10)
        super.onCreate()
    }
    override fun onBind(intent: Intent?): IBinder? {
    TODO()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val isOnlyVibrate = intent?.getBooleanExtra("OnlyVibrate",false)

        vibrate() // Let's Vibrate
        if (!isOnlyVibrate!!){ // Start Media Player and Audio Service If not DND Mode
            start_alert()
            var vol:Int = step!!
            do {
                setVolume(vol) // Set volume
                SystemClock.sleep(2000) // Wait some time
                if (getPresentVolume() == vol) {
                    //No interruption
                    // Can be continue
                    vol += step!!
                } else {
                    //Volume changed by user
                    //Break the Loop -- and set low
                    mediaPlayer!!.stop()
                    stop()
                    break
                }
            }while (vol <= this.maxVolume!!)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }
    private fun start_alert(){
        val mediaPlayer: MediaPlayer? = MediaPlayer.create(applicationContext, R.raw.alert)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            stop() // Stoping Service ifself after alterting
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate(){
        vibrator = applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val mVibratePattern = longArrayOf(0, 400, 100, 600, 100, 400, 100, 400)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(mVibratePattern, 0))
            }
        else{
            vibrator?.vibrate(400)
            Toast.makeText(applicationContext,"Unable to Vibrate the device",Toast.LENGTH_LONG).show()
        }
    }
    fun stop() {
        mediaPlayer?.release() // Releasing Mediaplayer
        vibrator?.cancel() // To Stop Vibration After Playing Sound
    }
    private fun getPresentVolume():Int{
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }
    private fun setVolume(volume:Int){
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, volume, 0)
    }
}

