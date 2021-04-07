package com.ramdurgasai.helpme

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.ramdurgasai.helpme.otphandler.Companion.botToken
import com.ramdurgasai.helpme.Imei
import java.io.*
import java.lang.Exception
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreference =  getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        setContentView(R.layout.activity_main)

        // Get BotToken By using Device Imei and details
        var deviceIMEI: String? = null
        try {
            deviceIMEI = Imei(applicationContext).imei
        }catch (e: Exception){
            e.printStackTrace()

        }

        editor.putString("botToken", botToken(deviceIMEI))
        editor.commit()
        println("Device IMEI is : " + deviceIMEI.toString())


        val button = findViewById<Switch>(R.id.switch1)
        button.setOnClickListener { view: View? -> when(view?.id){
            R.id.switch1 -> {

                val switch: Switch = findViewById(R.id.switch1)
                editor.putBoolean("DND", switch.isChecked)
                editor.commit()

            }
        } }




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 111)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                111
            )
        }

        // Starting A  Telegram Service
        val intent = Intent(applicationContext, telegramservice::class.java)
        intent.putExtra("otp", "")

        if(botToken(deviceIMEI) != null && !isServiceRunning(this,telegramservice::class.java) ){
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

            Toast.makeText(this, "Permission Granted ... I can Work Now", Toast.LENGTH_LONG).show()
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
