package com.ramdurgasai.helpme

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsMessage
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.time.ExperimentalTime
import com.ramdurgasai.helpme.otphandler.Companion.botToken

@ExperimentalTime
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS ) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS),111)

        }

        // Starting A  Telegram Service
        val intent = Intent(applicationContext,telegramservice::class.java)
        intent.putExtra("otp","")
        if(botToken() != null ){
            // If running in My Devices ..... Start Telegram Service
            ContextCompat.startForegroundService(this, intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Toast.makeText(this,"Permission Granted ... I can Work Now",Toast.LENGTH_LONG).show()
        }
    }
    fun isServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }




}
